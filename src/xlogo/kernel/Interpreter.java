/**
 * Title :        XLogo
 * Description :  XLogo is an interpreter for the Logo
 * programming language
 *
 * @author Loïc Le Coq
 */
package xlogo.kernel;

import xlogo.Config;
import xlogo.Logo;
import xlogo.gui.Application;
import xlogo.gui.HistoryPanel;
import xlogo.gui.InputFrame;
import xlogo.gui.MessageTextArea;
import xlogo.kernel.gui.GuiButton;
import xlogo.kernel.gui.GuiMenu;
import xlogo.kernel.network.NetworkClientChat;
import xlogo.kernel.network.NetworkClientExecute;
import xlogo.kernel.network.NetworkClientSend;
import xlogo.kernel.network.NetworkServer;
import xlogo.kernel.perspective.ElementLine;
import xlogo.kernel.perspective.ElementPoint;
import xlogo.kernel.perspective.ElementPolygon;
import xlogo.resources.ResourceLoader;
import xlogo.utils.Utils;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.vecmath.Point3d;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.*;

public class Interpreter {
    static final String END_PROCEDURE = "\n";
    static final String END_LOOP = "\\";
    static final String END_TCP = "\\x";
    static final String LOOP_CONDITION = "\\siwhile";
    public static StringBuffer actionInstruction = new StringBuffer();
    static Stack<String> operands = new Stack<>(); // stack containing procedure operands
    static Stack<String> procedures = new Stack<>(); // currently running procedures
    static Stack<String> consumers = new Stack<>(); // procedures waiting to consume parameters
    static Stack<Loop> loops = new Stack<>();
    static Stack<HashMap<String, String>> scopes = new Stack<>();
    static HashMap<String, String> locals = new HashMap<>(); // local variables
    static String lineNumber = "";
    static Map<String, Primitive> primitiveMap = new TreeMap<>();
    static boolean returnValue = false; // if the element is a number
    static boolean evalReturnValue = false; // if a primitive returns a series of statements to evaluate
    static boolean openParen = false; // if the element is an opening parenthesis
    static boolean closeParen = false; // is a closing parenthesis
    final Application app;
    final InstructionBuffer instructionBuffer = new InstructionBuffer();
    final Kernel kernel;
    Workspace workspace;
    Stack<Workspace> savedWorkspace;
    String wordPrefix = "";

    public Interpreter(Application app) {
        this.app = app;
        this.kernel = app.getKernel();
        this.workspace = kernel.getWorkspace();
        app.error = false;
        buildPrimitiveTreemap();
    }

    /**
     * Tests if "li" is a list
     *
     * @param li The String to test
     * @return true if it is a list, else false
     */
    //
    protected static boolean isList(String li) {
        li = li.trim();
        return li.length() > 0 && li.charAt(0) == '[' && li.endsWith("]");
    }

    /**
     * Tests if the string op is a logic operator: |,&,<,>,=,>=,<=
     *
     * @param op The operator to test
     * @return true if op is a logic operator
     */
    static boolean isLogicOperator(String op) {
        return List.of("=", "<", ">", "|", "&", "<=", ">=").contains(op);
    }

    /**
     * Tests if the string op is a + or -
     *
     * @param op The operator to test
     * @return true if op is + or -
     */
    static boolean isPlusMinus(String op) {
        return (op.equals("+") || op.equals("-"));
    }

    /**
     * Tests is the op is * or /
     *
     * @param op The operator to test
     * @return true if op is * or /
     */
    static boolean isTimesDiv(String op) {
        return (op.equals("*") || op.equals("/"));
    }

    /**
     * Tests if the string op is a special primitive (end conditions, loop conditions)
     *
     * @param op The operator to test
     * @return true if op is a special token
     */
    static boolean isSpecialPrim(String op) {
        return List.of(END_PROCEDURE, END_LOOP, END_TCP, LOOP_CONDITION, ")").contains(op);
    }

    /**
     * Tests if the string is any kind of operator
     *
     * @param op The operator to test
     * @return true if op is + or -
     */
    static boolean isOperator(String op) {
        return isTimesDiv(op) || isPlusMinus(op) || isLogicOperator(op);
    }

    static void reset() {
        Interpreter.returnValue = Interpreter.openParen = false;
        Interpreter.operands = new Stack<>();
        Interpreter.consumers = new Stack<>();
        Interpreter.locals = new HashMap<>();
        Interpreter.procedures = new Stack<>();
    }

    /**
     * This methods returns a list that contains all procedures name
     *
     * @return A list with all procedure names
     */
    StringBuffer getAllProcedures() {
        StringBuffer sb = new StringBuffer("[ ");
        for (int i = 0; i < workspace.getNumberOfProcedure(); i++) {
            Procedure proc = workspace.getProcedure(i);
            if (proc.displayed) {
                sb.append(proc.name);
                sb.append(" ");
            }
        }
        sb.append("] ");
        return sb;
    }

    /**
     * This methods returns a list that contains all variables name
     *
     * @return A list with all variables names
     */
    StringBuffer getAllVariables() {
        StringBuffer sb = new StringBuffer("[ ");
        Iterator<String> it = Interpreter.locals.keySet().iterator();
        while (it.hasNext()) {
            String name = it.next();
            sb.append(name);
            sb.append(" ");
        }
        it = workspace.globals.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            if (!Interpreter.locals.containsKey(key)) {
                sb.append(key);
                sb.append(" ");
            }
        }
        sb.append("] ");
        return sb;
    }    final List<Primitive> primitives = List.of(new Primitive("&", 1, false, this::andOp), new Primitive("|", 1, false, this::orOp), new Primitive("*", 1, false, this::multiplyOp), new Primitive("+", 1, false, this::addOp), new Primitive("-", 1, false, this::subtractOp), new Primitive("/", 1, false, this::divideOp), new Primitive("<", 1, false, this::isLessThan), new Primitive("<=", 1, false, this::isLessThanOrEqual), new Primitive("=", 1, false, this::isEqual), new Primitive(">", 1, false, this::isGreaterThan), new Primitive(">=", 1, false, this::isGreatherThanOrEqual), new Primitive(")", 0, false, this::closeParen), new Primitive("\\", 0, false, this::endLoop), new Primitive("\\siwhile", 2, false, this::testWhile), new Primitive("\\x", 0, false, this::endExecuteTcp), new Primitive("\n", 0, false, this::endProc), new Primitive("3d.downpitch", 1, false, this::pitchDown), new Primitive("3d.leftroll", 1, false, this::rollLeft), new Primitive("3d.lineend", 0, false, this::endLine), new Primitive("3d.linestart", 0, false, this::startLine), new Primitive("3d.orientation", 0, false, this::getOrientation), new Primitive("3d.perspective", 0, false, this::enable3D), new Primitive("3d.pitch", 0, false, this::getPitch), new Primitive("3d.pointend", 0, false, this::endPoint), new Primitive("3d.pointstart", 0, false, this::startPoint), new Primitive("3d.polyend", 0, false, this::endPolygon), new Primitive("3d.polystart", 0, false, this::startPolygon), new Primitive("3d.polyview", 0, false, this::view3D), new Primitive("3d.rightroll", 1, false, this::rollRight), new Primitive("3d.roll", 0, false, this::getRoll), new Primitive("3d.setorientation", 1, false, this::setOrientation), new Primitive("3d.setpitch", 1, false, this::setPitch), new Primitive("3d.setroll", 1, false, this::setRoll), new Primitive("3d.setxyz", 3, false, this::setXYZ), new Primitive("3d.setz", 1, false, this::setZ), new Primitive("3d.textend", 0, false, this::endText), new Primitive("3d.textstart", 0, false, this::startText), new Primitive("3d.uppitch", 1, false, this::pitchUp), new Primitive("color.black", 0, false, this::colorBlack), new Primitive("color.blue", 0, false, this::colorBlue), new Primitive("color.brown", 0, false, this::colorBrown), new Primitive("color.cyan", 0, false, this::colorCyan), new Primitive("color.darkblue", 0, false, this::colorDarkBlue), new Primitive("color.darkgreen", 0, false, this::colorDarkGreen), new Primitive("color.darkred", 0, false, this::colorDarkRed), new Primitive("color.gray", 0, false, this::colorGray), new Primitive("color.green", 0, false, this::colorGreen), new Primitive("color.lightgray", 0, false, this::colorLightGray), new Primitive("color.magenta", 0, false, this::colorMagenta), new Primitive("color.orange", 0, false, this::colorOrange), new Primitive("color.pink", 0, false, this::colorPink), new Primitive("color.purple", 0, false, this::colorPurple), new Primitive("color.red", 0, false, this::colorRed), new Primitive("color.white", 0, false, this::colorWhite), new Primitive("color.yellow", 0, false, this::colorYellow), new Primitive("control.and", 2, true, this::and), new Primitive("control.before?", 2, false, this::isBeforeWrap), new Primitive("control.bye", 0, false, this::bye), new Primitive("control.dountil", 2, false, this::doUntil), new Primitive("control.dowhile", 2, false, this::doWhile), new Primitive("control.empty?", 1, false, this::isEmpty), new Primitive("control.equal?", 2, false, this::isEqual), new Primitive("control.false", 0, false, this::falseValue), new Primitive("control.for", 2, false, this::forLoop), new Primitive("control.foreach", 3, false, this::forEach), new Primitive("control.forever", 1, false, this::forever), new Primitive("control.if", 2, false, this::ifThen), new Primitive("control.ifelse", 3, false, this::ifThenElse), new Primitive("control.integer?", 1, false, this::isInteger), new Primitive("control.not", 1, false, this::not), new Primitive("control.number?", 1, false, this::isNumber), new Primitive("control.or", 2, true, this::or), new Primitive("control.output", 1, false, this::output), new Primitive("control.primitive?", 1, false, this::isPrimitive), new Primitive("control.procedure?", 1, false, this::isProcedure), new Primitive("control.repcount", 0, false, this::getRepCount), new Primitive("control.repeat", 2, false, this::repeat), new Primitive("control.resetall", 0, false, this::resetAll), new Primitive("control.sentence", 2, true, this::sentence), new Primitive("control.stop", 0, false, this::stop), new Primitive("control.stopall", 0, false, this::stopAll), new Primitive("control.true", 0, false, this::trueValue), new Primitive("control.variable?", 1, false, this::isVariable), new Primitive("control.while", 2, false, this::whileLoop), new Primitive("control.word", 2, true, this::word), new Primitive("control.word?", 1, false, this::isWord), new Primitive("drawing.animation", 0, false, this::startAnimation), new Primitive("drawing.arc", 3, false, this::arc), new Primitive("drawing.axis", 1, false, this::drawBothAxes), new Primitive("drawing.axiscolor", 0, false, this::getAxisColor), new Primitive("drawing.back", 1, false, this::back), new Primitive("drawing.changedirectory", 1, false, this::changeDirectory), new Primitive("drawing.circle", 1, false, this::circle), new Primitive("drawing.clearscreen", 0, false, this::clearScreenWrap), new Primitive("drawing.distance", 1, false, this::distance), new Primitive("drawing.dot", 1, false, this::dot), new Primitive("drawing.drawingquality", 0, false, this::getDrawingQuality), new Primitive("drawing.eraseturtle", 1, false, this::eraseTurtle), new Primitive("drawing.fence", 0, false, this::fenceTurtle), new Primitive("drawing.fill", 0, false, this::fill), new Primitive("drawing.fillpolygon", 1, false, this::fillPolygon), new Primitive("drawing.fillzone", 0, false, this::fillZone), new Primitive("drawing.findcolor", 1, false, this::findColor), new Primitive("drawing.fontjustify", 0, false, this::getFontJustify), new Primitive("drawing.fontname", 0, false, this::getLabelFont), new Primitive("drawing.fontsize", 0, false, this::getFontSize), new Primitive("drawing.forward", 1, false, this::forward), new Primitive("drawing.grid", 2, false, this::drawGrid), new Primitive("drawing.grid?", 0, false, this::isGridEnabled), new Primitive("drawing.gridcolor", 0, false, this::getGridColor), new Primitive("drawing.heading", 0, false, this::heading), new Primitive("drawing.hideturtle", 0, false, this::hideTurtle), new Primitive("drawing.home", 0, false, this::home), new Primitive("drawing.label", 1, false, this::label), new Primitive("drawing.labellength", 1, false, this::getLabelLength), new Primitive("drawing.left", 1, false, this::left), new Primitive("drawing.loadimage", 1, false, this::loadImage), new Primitive("drawing.pencolor", 0, false, this::penColor), new Primitive("drawing.pendown", 0, false, this::penDown), new Primitive("drawing.pendown?", 0, false, this::isPenDown), new Primitive("drawing.penerase", 0, false, this::penErase), new Primitive("drawing.penpaint", 0, false, this::penPaint), new Primitive("drawing.penreverse", 0, false, this::penReverse), new Primitive("drawing.penshape", 0, false, this::getPenShape), new Primitive("drawing.penup", 0, false, this::penUp), new Primitive("drawing.penwidth", 0, false, this::getPenWidth), new Primitive("drawing.position", 0, false, this::position), new Primitive("drawing.repaint", 0, false, this::refresh), new Primitive("drawing.right", 1, false, this::right), new Primitive("drawing.saveimage", 2, false, this::saveImage), new Primitive("drawing.screencolor", 0, false, this::screenColor), new Primitive("drawing.screensize", 0, false, this::getImageSize), new Primitive("drawing.setaxiscolor", 1, false, this::setAxisColor), new Primitive("drawing.setdrawingquality", 1, false, this::setDrawingQuality), new Primitive("drawing.setfontjustify", 1, false, this::setFontJustify), new Primitive("drawing.setfontname", 1, false, this::setLabelFont), new Primitive("drawing.setfontsize", 1, false, this::setFontSize), new Primitive("drawing.setgridcolor", 1, false, this::setGridColor), new Primitive("drawing.setheading", 1, false, this::setHeading), new Primitive("drawing.setpencolor", 1, false, this::setPenColor), new Primitive("drawing.setpenshape", 1, false, this::setPenShape), new Primitive("drawing.setpenwidth", 1, false, this::setPenWidth), new Primitive("drawing.setposition", 1, false, this::setPos), new Primitive("drawing.setscreencolor", 1, false, this::setScreenColor), new Primitive("drawing.setscreensize", 1, false, this::setScreenSize), new Primitive("drawing.setshape", 1, false, this::setShape), new Primitive("drawing.setturtle", 1, false, this::setTurtle), new Primitive("drawing.setturtlesmax", 1, false, this::setTurtlesMax), new Primitive("drawing.setx", 1, false, this::setX), new Primitive("drawing.setxy", 2, false, this::setXY), new Primitive("drawing.sety", 1, false, this::setY), new Primitive("drawing.setzoom", 1, false, this::setZoom), new Primitive("drawing.shape", 0, false, this::getShape), new Primitive("drawing.showturtle", 0, false, this::showTurtle), new Primitive("drawing.stopanimation", 0, false, this::stopAnimation), new Primitive("drawing.stopaxis", 0, false, this::disableAxes), new Primitive("drawing.stopgrid", 0, false, this::disableGrid), new Primitive("drawing.towards", 1, false, this::towards), new Primitive("drawing.turtle", 0, false, this::getActiveTurtle), new Primitive("drawing.turtles", 0, false, this::getTurtles), new Primitive("drawing.turtlesmax", 0, false, this::getTurtlesMax), new Primitive("drawing.visible?", 0, false, this::isVisible), new Primitive("drawing.wash", 0, false, this::wash), new Primitive("drawing.window", 0, false, this::windowTurtle), new Primitive("drawing.wrap", 0, false, this::wrapTurtle), new Primitive("drawing.x", 0, false, this::getX), new Primitive("drawing.xaxis", 1, false, this::drawXAxis), new Primitive("drawing.xaxis?", 0, false, this::isXAxisEnabled), new Primitive("drawing.y", 0, false, this::getY), new Primitive("drawing.yaxis", 1, false, this::drawYAxis), new Primitive("drawing.yaxis?", 0, false, this::isYAxisEnabled), new Primitive("drawing.z", 0, false, this::getZ), new Primitive("drawing.zonesize", 0, false, this::getZoneSize), new Primitive("drawing.zoom", 0, false, this::getZoom), new Primitive("file.appendlineflow", 2, false, this::fileAppendLine), new Primitive("file.closeflow", 1, false, this::closeFile), new Primitive("file.directory", 0, false, this::getDirectory), new Primitive("file.endflow?", 1, false, this::isEndOfFile), new Primitive("file.files", 0, false, this::getFiles), new Primitive("file.listflow", 0, false, this::getOpenFiles), new Primitive("file.load", 1, false, this::load), new Primitive("file.openflow", 2, false, this::openFile), new Primitive("file.readcharflow", 1, false, this::fileReadChar), new Primitive("file.readlineflow", 1, false, this::fileReadLine), new Primitive("file.save", 2, false, this::save), new Primitive("file.saved", 1, false, this::saveAll), new Primitive("file.setdirectory", 1, false, this::setDirectory), new Primitive("file.writelineflow", 2, false, this::fileWriteLine), new Primitive("list.additem", 3, false, this::addItem), new Primitive("list.butfirst", 1, false, this::butFirst), new Primitive("list.butlast", 1, false, this::butLast), new Primitive("list.count", 1, false, this::count), new Primitive("list.first", 1, false, this::first), new Primitive("list.fput", 2, false, this::fput), new Primitive("list.item", 2, false, this::item), new Primitive("list.last", 1, false, this::last), new Primitive("list.list", 2, true, this::list), new Primitive("list.list?", 1, false, this::isList), new Primitive("list.lput", 2, false, this::lput), new Primitive("list.member", 2, false, this::isMember71), new Primitive("list.member?", 2, false, this::isMember69), new Primitive("list.pick", 1, false, this::pick), new Primitive("list.remove", 2, false, this::remove), new Primitive("list.replace", 3, false, this::replaceItem), new Primitive("list.reverse", 1, false, this::reverse), new Primitive("math.abs", 1, false, this::abs), new Primitive("math.acos", 1, false, this::acos), new Primitive("math.asin", 1, false, this::asin), new Primitive("math.atan", 1, false, this::atan), new Primitive("math.cos", 1, false, this::cos), new Primitive("math.difference", 2, false, this::difference), new Primitive("math.digits", 0, false, this::getSignificantDigits), new Primitive("math.div", 2, false, this::divide), new Primitive("math.exp", 1, false, this::exp), new Primitive("math.greater", 2, false, this::isGreaterThan), new Primitive("math.greaterequal", 2, false, this::isGreatherThanOrEqual), new Primitive("math.integer", 1, false, this::truncate), new Primitive("math.less", 2, false, this::isLessThan), new Primitive("math.lessequal", 2, false, this::isLessThanOrEqual), new Primitive("math.log", 1, false, this::log), new Primitive("math.log10", 1, false, this::log10), new Primitive("math.minus", 1, false, this::minus), new Primitive("math.mod", 2, false, this::mod), new Primitive("math.pi", 0, false, this::pi), new Primitive("math.power", 2, false, this::power), new Primitive("math.product", 2, true, this::product), new Primitive("math.quotient", 2, false, this::quotient), new Primitive("math.random", 1, false, this::random), new Primitive("math.randomfrac", 0, false, this::randomZeroToOne), new Primitive("math.remainder", 2, false, this::remainder), new Primitive("math.round", 1, false, this::round), new Primitive("math.setdigits", 1, false, this::setSignificantDigits), new Primitive("math.sin", 1, false, this::sin), new Primitive("math.sqrt", 1, false, this::sqrt), new Primitive("math.sum", 2, true, this::sum), new Primitive("math.tan", 1, false, this::tan), new Primitive("net.chattcp", 2, false, this::chatTcp), new Primitive("net.executetcp", 2, false, this::executeTcp), new Primitive("net.listentcp", 0, false, this::listenTcp), new Primitive("net.sendtcp", 2, false, this::sendTcp), new Primitive("sound.deletesequence", 0, false, this::deleteSequence), new Primitive("sound.indexsequence", 0, false, this::getSequenceIndex), new Primitive("sound.instrument", 0, false, this::getInstrument), new Primitive("sound.mp3play", 1, false, this::playMP3), new Primitive("sound.mp3stop", 0, false, this::stopMP3), new Primitive("sound.play", 0, false, this::play), new Primitive("sound.sequence", 1, false, this::sequence), new Primitive("sound.setindexsequence", 1, false, this::setSequenceIndex), new Primitive("sound.setinstrument", 1, false, this::setInstrument), new Primitive("time.countdown", 1, false, this::countdown), new Primitive("time.date", 0, false, this::getDate), new Primitive("time.endcountdown?", 0, false, this::isCountdownEnded), new Primitive("time.pasttime", 0, false, this::getTimePassed), new Primitive("time.time", 0, false, this::getTime), new Primitive("time.wait", 1, false, this::wait), new Primitive("ui.character", 1, false, this::getCharacter), new Primitive("ui.cleartext", 0, false, this::clearText), new Primitive("ui.guiaction", 2, false, this::guiAction), new Primitive("ui.guibutton", 2, false, this::guiButton), new Primitive("ui.guidraw", 1, false, this::guiDraw), new Primitive("ui.guimenu", 2, false, this::guiMenu), new Primitive("ui.guiposition", 2, false, this::guiPosition), new Primitive("ui.guiremove", 1, false, this::guiRemove), new Primitive("ui.key?", 0, false, this::isKey), new Primitive("ui.message", 1, false, this::message), new Primitive("ui.mouse?", 0, false, this::isMouseEvent), new Primitive("ui.mouseposition", 0, false, this::mousePosition), new Primitive("ui.print", 1, true, this::print), new Primitive("ui.read", 2, false, this::read), new Primitive("ui.readcharacter", 0, false, this::readChar), new Primitive("ui.readmouse", 0, false, this::mouseButton), new Primitive("ui.separation", 0, false, this::getSeparation), new Primitive("ui.setseparation", 1, false, this::setSeparation), new Primitive("ui.setstyle", 1, false, this::setTextStyle), new Primitive("ui.settextcolor", 1, false, this::setTextColor), new Primitive("ui.settextname", 1, false, this::setTextFont), new Primitive("ui.settextsize", 1, false, this::setTextSize), new Primitive("ui.style", 0, false, this::getTextStyle), new Primitive("ui.textcolor", 0, false, this::getTextColor), new Primitive("ui.textname", 0, false, this::getTextFont), new Primitive("ui.textsize", 0, false, this::getTextSize), new Primitive("ui.unicode", 1, false, this::getUnicode), new Primitive("ui.write", 1, false, this::write), new Primitive("workspace.contents", 0, false, this::getContents), new Primitive("workspace.define", 2, false, this::define), new Primitive("workspace.edit", 1, false, this::edit), new Primitive("workspace.editall", 0, false, this::editAll), new Primitive("workspace.eraseall", 0, false, this::eraseAll), new Primitive("workspace.eraseprocedure", 1, false, this::eraseProcedure), new Primitive("workspace.erasepropertylist", 1, false, this::erasePropertyList), new Primitive("workspace.erasevariable", 1, false, this::eraseVariable), new Primitive("workspace.externalcommand", 1, false, this::runExternalCommand), new Primitive("workspace.getproperty", 2, false, this::getProperty), new Primitive("workspace.globalmake", 2, false, this::globalMakeWrap), new Primitive("workspace.local", 1, false, this::localWrap), new Primitive("workspace.localmake", 2, false, this::localMake), new Primitive("workspace.primitives", 0, false, this::listPrimitives), new Primitive("workspace.procedures", 0, false, this::procedures), new Primitive("workspace.propertylist", 1, false, this::listProperties), new Primitive("workspace.propertylists", 0, false, this::getPropertyLists), new Primitive("workspace.putproperty", 3, false, this::setProperty), new Primitive("workspace.removeproperty", 2, false, this::removeProperty), new Primitive("workspace.run", 1, false, this::run), new Primitive("workspace.stoptrace", 0, false, this::stopTrace), new Primitive("workspace.text", 1, false, this::getProcedureBody), new Primitive("workspace.thing", 1, false, this::getVariableValue), new Primitive("workspace.trace", 0, false, this::trace), new Primitive("workspace.variables", 0, false, this::listVariables));

    /**
     * This methods returns a list that contains all Property Lists name
     *
     * @return A list with all Property Lists names
     */
    StringBuffer getAllpropertyLists() {
        StringBuffer sb = new StringBuffer("[ ");
        for (String s : workspace.getPropListKeys()) {
            sb.append(s);
            sb.append(" ");
        }
        sb.append("] ");
        return sb;
    }

    /**
     * Delete The variable called "name" from the workspace if it exists
     *
     * @param name The variable name
     */
    void deleteVariable(String name) {
        if (!Interpreter.locals.isEmpty()) {
            Interpreter.locals.remove(name);
        } else {
            workspace.deleteVariable(name);
        }
    }

    /**
     * Delete the procedure called "name" from the workspace
     *
     * @param name The procedure name
     */
    void deleteProcedure(String name) {
        for (int i = 0; i < workspace.getNumberOfProcedure(); i++) {
            Procedure procedure = workspace.getProcedure(i);
            if (procedure.name.equals(name) && procedure.displayed) {
                workspace.deleteProcedure(i);
                break;
            }
        }
    }

    /**
     * According to the type of the data, erase from workspace the resource called "name"
     *
     * @param name The name of the deleted resource, it couls be a list with all resource names
     * @param type The type for the data, it could be "variable", "procedure" or "propertylist"
     */
    void erase(String name, String type) {
        Interpreter.returnValue = false;
        try {
            if (isList(name)) {
                name = getFinalList(name);
                StringTokenizer st = new StringTokenizer(name);
                while (st.hasMoreTokens()) {
                    String item = st.nextToken();
                    this.eraseItem(item, type);
                }
            } else {
                name = getWord(name);
                if (null != name) {
                    this.eraseItem(name, type);
                } else throw new LogoException(app, Logo.messages.getString("error.word"));

            }
        } catch (LogoException ignored) {
        }
    }

    /**
     * According to the type of the data, erase from workspace the resource called "name"
     *
     * @param name The name of the deleted resource
     * @param type The type for the data, it could be "variable", "procedure" or "propertylist"
     */
    void eraseItem(String name, String type) {
        if (type.equals("procedure")) {
            this.deleteProcedure(name);
        } else if (type.equals("variable")) {
            this.deleteVariable(name);
        } else if (type.equals("propertylist")) {
            workspace.removePropList(name);
        }

    }

    String execute(StringBuffer instructions) throws LogoException {
        if (!instructions.toString().equals("")) {
            instructionBuffer.insertCode(instructions);
        }
        while (instructionBuffer.getLength() != 0) {
            if (app.error && LogoException.lance) throw new LogoException(app, Logo.messages.getString("stop"));
            while (app.animation.isOnPause()) { // If we touch the scrollbars
                try {
                    wait();
                } catch (Exception ignored) {
                }
            }
            // Is this line really required?
            if (instructionBuffer.getLength() == 0) break;
            String element = instructionBuffer.getNextWord();
            String lower = element.toLowerCase();
            AbstractProcedure proc;
            if (primitiveMap.containsKey(lower)) proc = primitiveMap.get(lower);
            else proc = workspace.getProcedure(lower);
            if (proc != null) {
                // if it is a primitive or a procedure
                Stack<String> param = new Stack<>();
                if (isOperator(lower)) {
                    // If it is an infix operator
                    deleteLineNumber();
                    returnValue = false;
                    if (operands.isEmpty()) {
                        // If the + or the - represents the negative or positive sign
                        if (!isPlusMinus(element))
                            throw new LogoException(app, element + " " + Logo.messages.getString("error.ne_peut_etre")); // of a name
                        if (consumers.isEmpty()) param.push("0");
                        else {
                            String st = consumers.peek();
                            if (!isOperator(st)) param.push("0");
                            else if (isTimesDiv(element)) { // If the - or + sign follows an or
                                instructionBuffer.deleteFirstWord(element);
                                if (st.equals("*")) instructionBuffer.insert("* ");
                                else instructionBuffer.insert("/ ");
                                if (element.equals("+")) return ("1"); // If that's a plus
                                else return ("-1"); // If it's a minus
                            } else param.push("0");
                        }
                    } else if (consumers.isEmpty()) {
                        param.push(operands.pop());
                    } else {
                        String st = consumers.peek();
                        if (isOperator(st)) {
                            if (relativePriority(st, element)) {
                                param.push(operands.pop());
                            } else return (operands.pop());
                        } else param.push(operands.pop());
                    }
                } else if (returnValue && !element.equals(")")) {
                    checkParenthesis();
                    returnValue = false;
                    break;
                } // If it is not the end parenthesis operator, we exit

                /*Example:
                 * To test  |	Formatted Form:
                 * fd 5 	| 	fd 5 \l1 rt \l2
                 * rt 		| --> The \l2 can't be removed before be
                 * end		|		sure the rt has noproblem
                 * */
                if (!element.equals("\n")) deleteLineNumber();
                instructionBuffer.deleteFirstWord(element);

                // Case with parenthensis
                // eg (sum 3 4 5)
                // eg (myProcedure 2 3 4 5)
                if (openParen) {
                    openParen = false;
                    int constantNumber = -1;
                    if (!proc.isGeneral()) constantNumber = proc.getArity();
                    // Looking for all arguments (Number undefined)
                    consumers.push(element);
                    int j = 0;
                    while (true) {
                        /* This line fixed the bug for primitive or procedure without arguments
                         * eg: pr (pi+2)
                         */
                        if (constantNumber == 0) break;
                        returnValue = openParen = false;
                        if (instructionBuffer.getNextWord().equals(")")) {
                            if (constantNumber != -1) {
                                // If the primitive or the procedure doesn't
                                // accept optional parameters
                                if (j > constantNumber) {
                                    throw new LogoException(app, Logo.messages.getString("too_much_arguments"));
                                } else if (j < constantNumber)
                                    throw new LogoException(app, Logo.messages.getString("pas_assez_de") + " " + consumers.peek());
                            }
                            break;
                        }
                        String a = execute(new StringBuffer());

                        param.push(a);
                        j++;
                    }
                    // If It's a procedure
                    if (proc instanceof Procedure) {
                        var userproc = (Procedure) proc;
                        if (j > userproc.arity + userproc.optVariables.size())
                            throw new LogoException(app, Logo.messages.getString("too_much_arguments"));
                        else if (j < userproc.arity)
                            throw new LogoException(app, Logo.messages.getString("pas_assez_de") + " " + consumers.peek());
                        // Searching for optional arguments that are not defined
                        if (j < userproc.optVariables.size() + userproc.arity) {
                            j = j - userproc.arity;
                            for (int c = j; c < userproc.optVariables.size(); c++) {
                                returnValue = openParen = false;
                                String a = execute(userproc.formattedOptVariables.get(c));
                                param.push(a);
                            }
                        }
                    }
                } else {
                    // classic case: predefined number of arguments
                    openParen = false;
                    // How many arguments for the procedure or the primitive
                    int nbparametre = proc.getArity();
                    // Looking for each arguments
                    int j = 0;
                    consumers.push(element);
                    while (j < nbparametre) {
                        returnValue = openParen = false;

                        String a = execute(new StringBuffer());
                        param.push(a);
                        j++;
                    }
                    // Looking for Optional arguments in case of procedure
                    if (proc instanceof Procedure) {
                        var userproc = (Procedure) proc;
                        nbparametre = userproc.optVariables.size();
                        for (j = 0; j < nbparametre; j++) {
                            returnValue = openParen = false;
                            String a = execute(userproc.formattedOptVariables.get(j));
                            param.push(a);
                        }
                    }
                }
                consumers.pop();
                if (!app.error) proc.execute(param);
                if (app.error) break;
                if (closeParen && !operands.empty()) {
                    closeParen = false;
                    returnValue = false;
                    return operands.pop();
                }
                // Test if the procedure returns something when
                // expected in the case of exec primitives or if
                if (!returnValue) {
                    if (evalReturnValue) {
                        evalReturnValue = false;
                    } else {
                        if (!consumers.isEmpty() && !app.error && !consumers.peek().equals("\n")) {
                            if (!element.equals("\n")) {
                                // If it's the end of a loop
                                // repeat 2 [fd 90 rt]
                                if (element.equals("\\")) {
                                    // The loop had been executed, we have to remove
                                    // the loop instruction
                                    int offset = instructionBuffer.indexOf(" \\ ");
                                    instructionBuffer.delete(0, offset + 1);
                                    throw new LogoException(app, Logo.messages.getString("pas_assez_de") + " " + consumers.peek());
                                }
                                // (av 100) ---> OK
                                // av av 20 ----> Bad
                                if (!consumers.peek().equals("("))
                                    throw new LogoException(app, element + " " + Logo.messages.getString("ne_renvoie_pas") + " " + consumers.peek());
                            }
                        }
                    }
                } else {
                    // The primitive returns a word or a list.
                    // There's no primitive or procedure waiting for it.
                    if (!consumers.isEmpty() && consumers.peek().equals("\n"))
                        throw new LogoException(app, Logo.messages.getString("error.whattodo") + " " + operands.peek() + " ?");
                }
            } else if (element.charAt(0) == ':' && element.length() > 1) {
                // If element is a variable
                if (returnValue) {
                    checkParenthesis();
                    returnValue = false;
                    break;
                } else deleteLineNumber();
                String value;
                String variableName = lower.substring(1);
                // If the variable isn't local
                if (!locals.containsKey(variableName)) {
                    // check it's a global variable
                    if (!workspace.globals.containsKey(variableName))
                        throw new LogoException(app, variableName + " " + Logo.messages.getString("error.novalue"));
                    else value = workspace.globals.get(variableName);
                }
                // If the variable is local
                else {
                    value = locals.get(variableName);
                }
                if (null == value)
                    throw new LogoException(app, variableName + "  " + Logo.messages.getString("error.novalue"));
                operands.push(value);
                returnValue = true;
                openParen = false;
                instructionBuffer.deleteFirstWord(element);
            } else {
                // If element is a number
                try {
                    Double.parseDouble(element);
                    boolean deleteEndZero = false;
                    if (element.endsWith(".0")) {
                        deleteEndZero = true;
                        element = element.substring(0, element.length() - 2);
                    }
                    operands.push(element);
                    if (returnValue) {
                        checkParenthesis();
                        operands.pop();
                        returnValue = false;
                        break;
                    } else deleteLineNumber();
                    returnValue = true;
                    openParen = false;
                    if (deleteEndZero) instructionBuffer.deleteFirstWord(element + ".0");
                    else instructionBuffer.deleteFirstWord(element);
                } catch (NumberFormatException e) {
                    if (element.equals("[")) {
                        if (returnValue) {
                            checkParenthesis();
                            break;
                        } else deleteLineNumber();
                        returnValue = true;
                        openParen = false;
                        instructionBuffer.deleteFirstWord(element);
                        String a = searchList();
                        operands.push(a);
                    } else if (element.equals("(")) {
                        if (returnValue) {
                            checkParenthesis();
                            break;
                        } else deleteLineNumber();
                        openParen = true;
                        Interpreter.procedures.push("(");
                        int pos = searchParentheses();
                        if (pos == -1) {
                            try {
                                throw new LogoException(app, Logo.messages.getString("parenthese_fermante"));
                            } catch (LogoException ignored) {
                            }
                        }
                        instructionBuffer.deleteFirstWord(element);
                        Interpreter.consumers.push("(");
                    } else if (element.charAt(0) == '"') {
                        // If the element is a word
                        try {
                            String el = element.substring(1);
                            Double.parseDouble(el);
                            operands.push(el);
                        } catch (NumberFormatException e1) {
                            operands.push(element);
                        }
                        if (returnValue) {
                            checkParenthesis();
                            operands.pop();
                            returnValue = false;
                            break;
                        } else deleteLineNumber();
                        returnValue = true;
                        openParen = false;
                        instructionBuffer.deleteFirstWord(element);
                    } else if (lower.equals(Logo.messages.getString("pour"))) {
                        // If that's the word for
                        instructionBuffer.deleteFirstWord(element);
                        if (instructionBuffer.getLength() != 0) {
                            element = instructionBuffer.getNextWord();
                            lower = element.toLowerCase();
                        } else
                            throw new LogoException(app, Logo.messages.getString("pas_assez_de") + " " + "\"" + Logo.messages.getString("pour") + "\"");
                        StringBuilder definition = new StringBuilder(Logo.messages.getString("pour") + " " + element + " ");
                        instructionBuffer.deleteFirstWord(element);
                        while (instructionBuffer.getLength() != 0) {
                            element = instructionBuffer.getNextWord().toLowerCase();
                            if (element.charAt(0) != ':' || element.length() == 1)
                                throw new LogoException(app, element + " " + Logo.messages.getString("pas_argument"));
                            definition.append(element).append(" ");
                            instructionBuffer.deleteFirstWord(element);
                        }
                        if (app.editor.isVisible())
                            throw new LogoException(app, Logo.messages.getString("ferme_editeur"));
                        else {
                            app.editor.setVisible(true);
                            app.editor.appendText(definition + "\n\n" + Logo.messages.getString("fin"));
                        }
                    } else if (element.startsWith("\\l")) {
                        if (returnValue) {
                            break;
                        }
                        instructionBuffer.deleteFirstWord(element);
                        lineNumber = element + " ";
                        element = instructionBuffer.getNextWord();

                    } else {
                        deleteLineNumber();
                        throw new LogoException(app, Logo.messages.getString("je_ne_sais_pas") + " " + element);
                    }
                }
            }
        }
        // END OF THE MAIN LOOP
        // If there's nothing to return.
        if (operands.isEmpty()) {
            if (!consumers.isEmpty()) {// &&!nom.peek().equals("\n")) {
                while ((!consumers.isEmpty()) && consumers.peek().equals("\n")) consumers.pop();
                if (!consumers.isEmpty()) {
                    throw new LogoException(app, Logo.messages.getString("pas_assez_de") + " " + consumers.peek());
                }
            }
        }
        // Otherwise, we return the value contained in the calculation stack.
        if (!operands.isEmpty()) {
            // If there is a procedure to launch
            // Ex: pour t -- 6 -- fin . Puis, av t.
            if ((!consumers.isEmpty()) && consumers.peek().equals("\n")) {
                String up;
                int id = 0;
                while (!consumers.isEmpty() && consumers.peek().equals("\n")) {
                    consumers.pop();
                    id++;
                }
                if (!consumers.isEmpty()) {
                    up = consumers.peek();
                    try {
                        throw new LogoException(app, procedures.get(procedures.size() - id) + " " + Logo.messages.getString("ne_renvoie_pas") + " " + up);
                    } catch (LogoException ignored) {
                    }
                } else {
                    try {
                        throw new LogoException(app, Logo.messages.getString("error.whattodo") + " " + operands.peek() + " ?");
                    } catch (LogoException ignored) {
                    }
                }
            } else {
                returnValue = false;
                return (operands.pop());
            }
        }
        return ("");
    }

    private int searchParentheses() { // position where the next parenthesis stops
        boolean cont = true;
        int open;
        int close = 0;
        int nextOpen = 1;
        int nextClose = 1;
        while (cont) {
            open = instructionBuffer.indexOf("(", nextOpen);
            close = instructionBuffer.indexOf(")", nextClose);
            if (close == -1) break;
            if (open != -1 && open < close) {
                nextOpen = open + 1;
                nextClose = close + 1;
            } else cont = false;
        }
        return close;
    }

    protected String searchList() throws LogoException {
        StringBuilder list = new StringBuilder("[ ");
        String element;
        while (instructionBuffer.getLength() != 0) {
            element = instructionBuffer.getNextWord();
            // If opening square bracket, it is pushed into the calculation stack
            if (element.equals("[")) {
                operands.push("[");
                instructionBuffer.deleteFirstWord(element);
                list.append("[ ");
            } else if (element.equals("]")) { // If we reach a closing bracket
                instructionBuffer.deleteFirstWord(element);
                list.append("] ");
                if (operands.empty()) {
                    return (list.toString());
                } // 1er cas: rien dans la pile de calcul, on renvoie la liste
                else if (!operands.peek().equals("[")) {
                    return (list.toString());
                } // 2nd case: no opening bracket at the top of the stack, same
                else operands.pop(); // 3rd case: a bracket opening at the top of the stack, it is removed
            } else {
                instructionBuffer.deleteFirstWord(element);
                list.append(element).append(" ");
            }
        }
        throw new LogoException(app, Logo.messages.getString("erreur_crochet"));
    }

    /**
     * This method compares the two operators first and second.<br>
     *
     * @param first  The first operator
     * @param second The second operator
     * @return true if first has a lower priority than second.
     */
    private boolean relativePriority(String first, String second) {
        if (isTimesDiv(second) && !isTimesDiv(first)) return (true);
        else if (isPlusMinus(second) && isLogicOperator(first)) return (true);
        else return ">=<=".contains(second) && "|&".contains(first);
    }

    protected void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
    }

    private void checkParenthesis() throws LogoException {
        if (!consumers.isEmpty()) {
            String name = consumers.peek();
            if (name.equals("(")) {
                throw new LogoException(app, Logo.messages.getString("too_much_arguments"));

            }
        }
    }

    private void deleteLineNumber() {
        lineNumber = "";
    }

    /**
     * This method returns a list which contains all the primitive for the current language
     *
     * @return The primitives list
     */
    protected String getAllPrimitives() {
        List<String> list = new ArrayList<>();
        Locale locale = Logo.getLocale(Logo.config.getLanguage());
        ResourceBundle names = ResourceLoader.getPrimitiveBundle(Objects.requireNonNull(locale));
        for (var prim : primitives) {
            // Exclude internal primitives \n \\x and \\siwhile
            // Exclude all arithmetic symbols + - / * & |
            if (!Interpreter.isOperator(prim.key) || !Interpreter.isSpecialPrim(prim.key)) {
                list.add(names.getString(prim.key).trim());
            }
        }
        Collections.sort(list);
        StringBuilder sb = new StringBuilder("[ ");
        for (String s : list) {
            sb.append("[ ");
            sb.append(s);
            sb.append("] ");
        }
        sb.append("] ");
        return (sb.toString());
    }

    // Execution of primitives
    public void buildPrimitiveTreemap() {
        primitiveMap = new TreeMap<>();
        Locale locale = Logo.getLocale(Logo.config.getLanguage());
        ResourceBundle names = ResourceLoader.getPrimitiveBundle(Objects.requireNonNull(locale));
        for (var prim : primitives) {
            // read the standard number of arguments for the primitive
            String name = prim.key;
            if (!Interpreter.isOperator(name) && !Interpreter.isSpecialPrim(name)) {
                name = names.getString(name);
            }
            if (name.equals("\n")) {
                primitiveMap.put(name, prim);
            } else {
                StringTokenizer st = new StringTokenizer(name.toLowerCase());
                while (st.hasMoreTokens()) primitiveMap.put(st.nextToken(), prim);
            }
        }
    }

    /**
     * This method creates the loop "repeat"
     *
     * @param i  The number of iteration
     * @param st The instruction to execute
     */
    protected void repeat(int i, String st) {
        if (i > 0) {
            st = new String(Utils.formatCode(st));
            Loop bp = new LoopRepeat(BigDecimal.ONE, new BigDecimal(i), BigDecimal.ONE, st);
            loops.push(bp);
            app.getKernel().getInstructionBuffer().insert(st + "\\ ");
        } else if (i != 0) {
            try {
                throw new LogoException(app, Utils.primitiveName("control.repeat") + " " + Logo.messages.getString("attend_positif"));
            } catch (LogoException ignored) {
            }
        }
    }

    // primitive if
    void ifThen(boolean b, String li, String li2) {
        if (b) {
            app.getKernel().getInstructionBuffer().insert(li);
        } else if (null != li2) {
            app.getKernel().getInstructionBuffer().insert(li2);
        }
    }

    // primitive stop
    void stop() throws LogoException {
        Interpreter.returnValue = false;
        String car = "";
        try {
            car = eraseLevelStop();
        } catch (LogoException ignored) {
        }

        // A procedure has been stopped
        if (car.equals("\n")) {
            String en_cours = Interpreter.procedures.pop();
            Interpreter.locals = Interpreter.scopes.pop();
            // Example: 	to bug
            //				fd stop
            //				end
            // 				--------
            //				bug
            //				stop doesn't output to fd
            if (!Interpreter.consumers.isEmpty() && !Interpreter.consumers.peek().equals("\n")) {
                //	System.out.println(Interpreter.nom);
                throw new LogoException(app, Utils.primitiveName("control.stop") + " " + Logo.messages.getString("ne_renvoie_pas") + " " + Interpreter.consumers.peek());
            } else if (!Interpreter.consumers.isEmpty()) {
                // Removing the character "\n"
                Interpreter.consumers.pop();
                // Example: 	to bug		|	to bug2
                // 				fd bug2		|	stop
                //				end			|	end
                //				------------------------
                // 				bug
                //				bug2 doesn't output to fd
                if (!Interpreter.consumers.isEmpty() && !Interpreter.consumers.peek().equals("\n")) {
                    //	System.out.println(Interpreter.nom);
                    throw new LogoException(app, en_cours + " " + Logo.messages.getString("ne_renvoie_pas") + " " + Interpreter.consumers.peek());
                }
            }
        }
    }

    // primitive output
    void output(String val) throws LogoException {
        Interpreter.operands.push(val);
        Interpreter.returnValue = true;
        if (Kernel.traceMode) {
            String buffer = "  ".repeat(Math.max(0, Interpreter.procedures.size() - 1)) + Interpreter.procedures.peek() + " " + Utils.primitiveName("control.output") + " " + val;
            app.updateHistory("normal", Utils.unescapeString(buffer) + "\n");
        }
        Interpreter.procedures.pop();
        Interpreter.locals = Interpreter.scopes.pop();
        if ((!Interpreter.consumers.isEmpty()) && Interpreter.consumers.peek().equals("\n")) {
            try {
                eraseLevelReturn();
            } catch (LogoException ignored) {
            }
            Interpreter.consumers.pop();
        } else if (!Interpreter.consumers.isEmpty())
            throw new LogoException(app, Utils.primitiveName("control.output") + " " + Logo.messages.getString("ne_renvoie_pas") + " " + Interpreter.consumers.peek());
        else throw new LogoException(app, Logo.messages.getString("erreur_retourne"));
    }

    /**
     * This method deletes all instruction since it encounters the end of a loop or the end of a procedure
     *
     * @return The specific character \n or \ if found
     * @throws LogoException if an error occurs
     */
    String eraseLevelStop() throws LogoException {
        boolean error = true;
        String caractere = "";
        int marqueur = 0;
        InstructionBuffer instruction = app.getKernel().getInstructionBuffer();
        for (int i = 0; i < instruction.getLength(); i++) {
            caractere = String.valueOf(instruction.charAt(i));
            if (caractere.equals(Interpreter.END_LOOP) | caractere.equals(Interpreter.END_PROCEDURE)) {
                marqueur = i;
                if (caractere.equals(Interpreter.END_LOOP) && i != instruction.getLength() - 1) {
                    // We test if the character "\" is indeed an end of
                    // loop character and not of the style "\e" or "\"
                    if (instruction.charAt(i + 1) == ' ') {
                        error = false;
                        break;
                    }
                } else {
                    error = false;
                    break;
                }
            }
        }

        if (error) {
            throw new LogoException(app, Logo.messages.getString("erreur_stop"));
        }
        if (marqueur + 2 <= instruction.getLength()) instruction.delete(0, marqueur + 2);
        if (!caractere.equals("\n")) {
            loops.pop();
        }
        return (caractere);
    }

    /**
     * This method deletes all instruction since it encounters the end of a procedure
     *
     * @throws LogoException if there is an error
     */
    void eraseLevelReturn() throws LogoException {
        boolean error = true;
        String caractere;
        int loopLevel = 0;
        int marqueur = 0;
        InstructionBuffer instruction = app.getKernel().getInstructionBuffer();
        for (int i = 0; i < instruction.getLength(); i++) {
            caractere = String.valueOf(instruction.charAt(i));
            if (caractere.equals(Interpreter.END_PROCEDURE)) {
                marqueur = i;
                error = false;
                break;
            } else if (caractere.equals(Interpreter.END_LOOP)) {
                // We test if the character "\" is indeed an end of
                // loop character and not of the style "\e" or "\"
                if (instruction.charAt(i + 1) == ' ') {
                    loopLevel++;
                }
            }
        }
        if (error) {
            throw new LogoException(app, Logo.messages.getString("erreur_retourne"));
        }
        if (marqueur + 2 <= instruction.getLength()) instruction.delete(0, marqueur + 2);
        for (int i = 0; i < loopLevel; i++) {
            loops.pop();
        }
    }

    void getFontJustify(Stack<String> param) {
        Interpreter.returnValue = true;
        Interpreter.operands.push(kernel.getActiveTurtle().getFontJustify());
    }

    void exportPrimCSV() {
        StringBuilder sb = new StringBuilder();
        Locale locale = new Locale("fr", "FR");
        ResourceBundle names_fr = ResourceLoader.getPrimitiveBundle(locale);
        locale = new Locale("en", "US");
        ResourceBundle names_en = ResourceLoader.getPrimitiveBundle(locale);
        locale = new Locale("ar", "MA");
        ResourceBundle names_ar = ResourceLoader.getPrimitiveBundle(locale);
        locale = new Locale("es", "ES");
        ResourceBundle names_es = ResourceLoader.getPrimitiveBundle(locale);
        locale = new Locale("pt", "BR");
        ResourceBundle names_pt = ResourceLoader.getPrimitiveBundle(locale);
        locale = new Locale("eo", "EO");
        ResourceBundle names_eo = ResourceLoader.getPrimitiveBundle(locale);
        locale = new Locale("de", "DE");
        ResourceBundle names_de = ResourceLoader.getPrimitiveBundle(locale);
        //locale=new Locale("nl","NL");
        ResourceBundle[] names = {names_fr, names_en, names_es, names_pt, names_ar, names_eo, names_de};
        int i = 0;
        for (var prim : primitives) {
            var cle = prim.key;
            if (i != 39 && i != 95 && i != 212 && cle.length() != 1) {
                sb.append("$");
                sb.append(cle);
                sb.append("$");
                sb.append(";");
                for (ResourceBundle name : names) {
                    String txt = name.getString(cle);
                    sb.append("$");
                    sb.append(txt);
                    sb.append("$");
                    sb.append(";");
                }
                sb.append("\n");
            }
            i++;
        }
        try {
            Utils.writeLogoFile("/home/loic/primTable.csv", new String(sb));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void exportMessageCSV() {
        StringBuilder sb = new StringBuilder();
        Locale locale = new Locale("fr", "FR");
        ResourceBundle lang_fr = ResourceLoader.getLanguageBundle(locale);
        locale = new Locale("en", "US");
        ResourceBundle lang_en = ResourceLoader.getLanguageBundle(locale);
        locale = new Locale("ar", "MA");
        ResourceBundle lang_ar = ResourceLoader.getLanguageBundle(locale);
        locale = new Locale("es", "ES");
        ResourceBundle lang_es = ResourceLoader.getLanguageBundle(locale);
        locale = new Locale("pt", "BR");
        ResourceBundle lang_pt = ResourceLoader.getLanguageBundle(locale);
        locale = new Locale("eo", "EO");
        ResourceBundle lang_eo = ResourceLoader.getLanguageBundle(locale);
        locale = new Locale("de", "DE");
        ResourceBundle lang_de = ResourceLoader.getLanguageBundle(locale);
        //locale=new Locale("nl","NL");
        ResourceBundle[] lang = {lang_fr, lang_en, lang_es, lang_pt, lang_ar, lang_eo, lang_de};
        try {
            Enumeration<String> en = lang_fr.getKeys();
            while (en.hasMoreElements()) {
                String cle = en.nextElement();
                sb.append("$");
                sb.append(cle);
                sb.append("$");
                sb.append(";");
                for (int j = 0; j < lang.length; j++) {
                    String txt = lang[j].getString(cle);
                    sb.append("$");
                    sb.append(txt);
                    sb.append("$");
                    if (j != lang.length - 1) sb.append(";");
                }

                sb.append("\n");
            }
            Utils.writeLogoFile("/home/loic/messageTable.csv", new String(sb));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void setFontJustify(Stack<String> param) {
        Interpreter.returnValue = false;
        try {
            String li1 = getFinalList(param.get(0));
            kernel.getActiveTurtle().setFontJustify(li1);
        } catch (LogoException e) {
            e.printStackTrace();
        }
    }

    void mod(Stack<String> param) {
        Interpreter.returnValue = true;
        try {
            Interpreter.operands.push(kernel.getCalculator().modulo(param.get(0), param.get(1)));
        } catch (LogoException ignored) {
        }
    }

    void doWhile(Stack<String> param) {
        try {
            String li1 = getList(param.get(0));
            li1 = new String(Utils.formatCode(li1));
            String li2 = getList(param.get(1));
            li2 = new String(Utils.formatCode(li2));
            String instr = "\\siwhile " + li2 + "[ " + li1 + "] ";
            LoopWhile bp = new LoopWhile(BigDecimal.ONE, BigDecimal.ZERO, BigDecimal.ONE, instr);
            loops.push(bp);
            app.getKernel().getInstructionBuffer().insert(li1 + instr + Interpreter.END_LOOP + " ");
        } catch (LogoException ignored) {
        }
    }

    void doUntil(Stack<String> param) {
        try {
            String li1 = getList(param.get(0));
            li1 = new String(Utils.formatCode(li1));
            String li2 = getList(param.get(1));
            li2 = new String(Utils.formatCode(li2));
            String instr = "\\siwhile " + Utils.primitiveName("control.not") + " " + li2 + "[ " + li1 + "] ";
            LoopWhile bp = new LoopWhile(BigDecimal.ONE, BigDecimal.ZERO, BigDecimal.ONE, instr);
            loops.push(bp);
            app.getKernel().getInstructionBuffer().insert(instr + Interpreter.END_LOOP + " ");
        } catch (LogoException ignored) {
        }
    }

    void randomZeroToOne(Stack<String> param) {
        Interpreter.returnValue = true;
        Interpreter.operands.push(Calculator.teste_fin_double(Math.random()));
    }

    void fillPolygon(Stack<String> param) {
        Interpreter.returnValue = false;
        try {
            String list = getFinalList(param.get(0));
            LoopFillPolygon bp = new LoopFillPolygon();
            loops.push(bp);
            app.getKernel().getInstructionBuffer().insert(Utils.formatCode(list) + Interpreter.END_LOOP + " ");
            app.getDrawPanel().startRecord2DPolygon();
        } catch (LogoException ignored) {
        }
    }

    void getZ(Stack<String> param) {
        Interpreter.returnValue = true;
        try {
            primitive3D("drawing.z");
            Interpreter.operands.push(Calculator.teste_fin_double(kernel.getActiveTurtle().Z));
        } catch (LogoException ignored) {
        }
    }

    void getY(Stack<String> param) {
        Interpreter.returnValue = true;
        Interpreter.operands.push(Calculator.teste_fin_double(kernel.getActiveTurtle().getY()));
    }

    void getX(Stack<String> param) {
        Interpreter.returnValue = true;
        Interpreter.operands.push(Calculator.teste_fin_double(kernel.getActiveTurtle().getX()));
    }

    void getZoom(Stack<String> param) {
        Interpreter.returnValue = true;
        Interpreter.operands.push(Calculator.teste_fin_double(DrawPanel.zoom));
    }

    void stopMP3(Stack<String> param) {
        Interpreter.returnValue = false;
        if (null != kernel.getMp3Player()) kernel.getMp3Player().getPlayer().close();
    }

    void playMP3(Stack<String> param) {
        String mot;
        Interpreter.returnValue = false;
        if (kernel.getMp3Player() != null) kernel.getMp3Player().getPlayer().close();
        mot = getWord(param.get(0));
        try {
            if (null == mot) throw new LogoException(app, Logo.messages.getString("error.word"));
            MP3Player player = new MP3Player(app, mot);
            kernel.setMp3Player(player);
            kernel.getMp3Player().start();
        } catch (LogoException ignored) {
        }
    }

    void saveImage(Stack<String> param) {
        try {
            String word = getWord(param.get(0));
            if (null == word) throw new LogoException(app, param.get(0) + " " + Logo.messages.getString("error.word"));
            if (word.equals("")) throw new LogoException(app, param.get(0) + " " + Logo.messages.getString("mot_vide"));
            // xmin, ymin, width, height
            int[] coord = new int[4];
            String list = getFinalList(param.get(1));
            StringTokenizer st = new StringTokenizer(list);
            if (st.countTokens() == 4) {
                try {
                    int j = 0;
                    while (st.hasMoreTokens()) {
                        coord[j] = Integer.parseInt(st.nextToken());
                        j++;
                    }
                    coord[0] += Logo.config.getImageWidth() / 2;
                    coord[2] += Logo.config.getImageWidth() / 2;
                    coord[1] = Logo.config.getImageHeight() / 2 - coord[1];
                    coord[3] = Logo.config.getImageHeight() / 2 - coord[3];
                    if (coord[2] < coord[0]) {
                        int tmp = coord[0];
                        coord[0] = coord[2];
                        coord[2] = tmp;
                    }
                    if (coord[3] < coord[1]) {
                        int tmp = coord[1];
                        coord[1] = coord[3];
                        coord[3] = tmp;
                    }
                    coord[2] = coord[2] - coord[0];
                    coord[3] = coord[3] - coord[1];
                } catch (NumberFormatException e) {
                    coord[0] = 0;
                    coord[2] = Logo.config.getImageWidth();
                    coord[1] = 0;
                    coord[3] = Logo.config.getImageHeight();
                }
            } else {
                coord[0] = 0;
                coord[2] = Logo.config.getImageWidth();
                coord[1] = 0;
                coord[3] = Logo.config.getImageHeight();
            }
            if (coord[2] == 0 || coord[3] == 0) {
                coord[0] = 0;
                coord[2] = Logo.config.getImageWidth();
                coord[1] = 0;
                coord[3] = Logo.config.getImageHeight();
            }
            app.getDrawPanel().saveImage(word, coord);
            Interpreter.returnValue = false;
        } catch (LogoException ignored) {
        }
    }

    void runExternalCommand(Stack<String> param) {
        Interpreter.returnValue = false;
        try {
            String list = getFinalList(param.get(0));
            int index = numberOfElements(list);
            String[] cmd = new String[index];
            for (int i = 0; i < index; i++) {
                String liste1 = item(list, i + 1);
                cmd[i] = Utils.unescapeString(getFinalList(liste1).trim());
            }
            try {
                Runtime.getRuntime().exec(cmd);
            } catch (IOException e2) {
                //System.out.println("a");
            }

        } catch (LogoException e) {
            //System.out.println("coucou");
        }
    }

    void getProcedureBody(Stack<String> param) {
        StringBuilder sb;
        try {
            String var = getWord(param.get(0));
            if (null == var) throw new LogoException(app, param.get(0) + " " + Logo.messages.getString("error.word"));
            int index = -1;
            for (int i = 0; i < workspace.getNumberOfProcedure(); i++) {
                if (workspace.getProcedure(i).name.equals(var)) {
                    index = i;
                    break;
                }
            }
            if (index != -1) {
                Procedure proc = workspace.getProcedure(index);
                sb = new StringBuilder();
                sb.append("[ [ ");
                // Append variable names
                for (int j = 0; j < proc.arity; j++) {
                    sb.append(proc.variables.get(j));
                    sb.append(" ");
                }
                for (int j = 0; j < proc.optVariables.size(); j++) {
                    sb.append("[ ");
                    sb.append(proc.optVariables.get(j));
                    sb.append(" ");
                    sb.append(proc.formattedOptVariables.get(j).toString());
                    sb.append(" ] ");
                }
                sb.append("] ");
                // Append body procedure
                sb.append(proc.cutInList());
                sb.append("] ");
                Interpreter.returnValue = true;
                Interpreter.operands.push(sb.toString());
            } else throw new LogoException(app, var + " " + Logo.messages.getString("error.procedure.must.be"));
        } catch (LogoException ignored) {
        }
    }

    void getSignificantDigits(Stack<String> param) {
        Interpreter.returnValue = true;
        Interpreter.operands.push(String.valueOf(kernel.getCalculator().getDigits()));
    }

    void setSignificantDigits(Stack<String> param) {
        Interpreter.returnValue = false;
        try {
            kernel.initCalculator(kernel.getCalculator().getInteger(param.get(0)));
        } catch (LogoException ignored) {
        }
    }

    void forever(Stack<String> param) {
        try {
            String li2 = getList(param.get(0));
            li2 = new String(Utils.formatCode(li2));
            Loop bp = new Loop(BigDecimal.ONE, BigDecimal.ZERO, BigDecimal.ONE, li2);
            app.getKernel().getInstructionBuffer().insert(li2 + Interpreter.END_LOOP + " ");
            loops.push(bp);
        } catch (LogoException ignored) {
        }
    }

    void forEach(Stack<String> param) {
        try {
            // Variable name
            String var = getWord(param.get(0));
            // If it isn't a word
            if (null == var) throw new LogoException(app, param.get(0) + " " + Logo.messages.getString("error.word"));
                // If it's a number
            else {
                try {
                    Double.parseDouble(var);
                    throw new LogoException(app, Logo.messages.getString("erreur_nom_nombre_variable"));
                } catch (NumberFormatException ignored) {
                }
            }
            String li2 = getList(param.get(2));
            li2 = new String(Utils.formatCode(li2));
            String li1 = getWord(param.get(1));
            boolean list = false;
            if (null == li1) {
                list = true;
                li1 = getFinalList(param.get(1));
            }
            Vector<String> elements = new Vector<>();
            while (!li1.equals("")) {
                String character;
                // If it's a list
                if (list) {
                    character = this.item(li1, 1);
                    // If it's a number
                    try {
                        // Fix Bug: foreach "i [1 2 3][pr :i]
                        // character=1 , 2 , 3 (without quote)
                        Double.parseDouble(character);
                        li1 = li1.substring(character.length() + 1);
                    } catch (NumberFormatException e) {
                        // Fix Bug: foreach "i [r s t][pr :i]
                        // character="r ,  "s  or  "t
                        li1 = li1.substring(character.length());
                    }
                }
                // If it's a word
                else {
                    character = this.itemWord(1, li1);
                    li1 = li1.substring(character.length());
                    // If it isn't a number, adding a quote
                    try {
                        Double.parseDouble(character);
                    } catch (NumberFormatException e) {
                        character = "\"" + character;
                    }
                }

                elements.add(character);
            }
            if (elements.size() > 0) {
                LoopForEach bp = new LoopForEach(BigDecimal.ZERO, new BigDecimal(elements.size() - 1), BigDecimal.ONE, li2, var.toLowerCase(), elements);
                bp.AffecteVar(true);
                app.getKernel().getInstructionBuffer().insert(li2 + Interpreter.END_LOOP + " ");
                loops.push(bp);
            }
        } catch (LogoException ignored) {
        }
    }

    void edit(Stack<String> param) {
        String mot;
        try {
            mot = this.getWord(param.get(0));
            if (null == mot) mot = this.getFinalList(param.get(0));
            StringTokenizer st = new StringTokenizer(mot);
            // Write all procedures names in a Vector
            Vector<String> names = new Vector<>();
            while (st.hasMoreTokens()) {
                names.add(st.nextToken());
            }
            app.editor.setTitle(Logo.messages.getString("editeur"));

            app.editor.setMainCommand();
            app.editor.setTitle(Logo.messages.getString("editeur"));
            app.editor.discardAllEdits();
            app.editor.setVisible(true);
            app.editor.toFront();
            app.editor.requestFocus();

            for (int i = 0; i < workspace.getNumberOfProcedure(); i++) {
                Procedure procedure = workspace.getProcedure(i);
//							System.out.println(procedure.toString().length());
                if (names.contains(procedure.name) && procedure.displayed) {
                    app.editor.appendText(procedure.toString());
                }
            }
        } catch (LogoException ignored) {
        }
    }

    void editAll(Stack<String> param) {
        app.editor.open();
    }

    void ifThenElse(Stack<String> param) {
        String liste;
        try {
            liste = getList(param.get(1));
            liste = new String(Utils.formatCode(liste));
            boolean predicat = getBoolean(param.get(0));
            String liste2 = getList(param.get(2));
            liste = new String(Utils.formatCode(liste));
            ifThen(predicat, liste, liste2);
            Interpreter.evalReturnValue = true;
        } catch (LogoException ignored) {
        }
    }

    void log(Stack<String> param) {
        Interpreter.returnValue = true;
        try {
            Interpreter.operands.push(kernel.getCalculator().log(param.get(0)));
        } catch (LogoException ignored) {
        }
    }

    void exp(Stack<String> param) {
        Interpreter.returnValue = true;
        try {
            Interpreter.operands.push(kernel.getCalculator().exp(param.get(0)));

        } catch (LogoException ignored) {
        }
    }

    void erasePropertyList(Stack<String> param) {
        Interpreter.returnValue = false;
        this.erase(param.get(0), "propertylist");
    }

    void getContents(Stack<String> param) {
        StringBuilder sb;
        Interpreter.returnValue = true;
        sb = new StringBuilder("[ ");
        sb.append(this.getAllProcedures());
        sb.append(this.getAllVariables());
        sb.append(this.getAllpropertyLists());
        sb.append("] ");
        Interpreter.operands.push(new String(sb));
    }

    void getPropertyLists(Stack<String> param) {
        Interpreter.returnValue = true;
        Interpreter.operands.push(new String(getAllpropertyLists()));
    }

    void listPrimitives(Stack<String> param) {
        Interpreter.returnValue = true;
        Interpreter.operands.push(getAllPrimitives());
    }

    void endPoint(Stack<String> param) {
        DrawPanel.record3D = DrawPanel.RECORD_3D_NONE;
        try {
            DrawPanel.poly.addToScene();
        } catch (LogoException ignored) {
        }
    }

    void endText(Stack<String> param) {
        DrawPanel.record3D = DrawPanel.RECORD_3D_NONE;
    }

    void startText(Stack<String> param) {
        DrawPanel.record3D = DrawPanel.RECORD_3D_TEXT;
        app.initViewer3D();
//                    	if (null==DrawPanel.listText) DrawPanel.listText=new java.util.Vector<TransformGroup>();
        DrawPanel.poly = null;
    }

    void endLine(Stack<String> param) {
        DrawPanel.record3D = DrawPanel.RECORD_3D_NONE;
        try {
            DrawPanel.poly.addToScene();
        } catch (LogoException ignored) {
        }
    }

    void startPoint(Stack<String> param) {
        DrawPanel.record3D = DrawPanel.RECORD_3D_POINT;
        app.initViewer3D();
//                    	if (null==DrawPanel.listPoly) DrawPanel.listPoly=new java.util.Vector<Shape3D>();
        DrawPanel.poly = new ElementPoint(app);
    }

    void startLine(Stack<String> param) {
        DrawPanel.record3D = DrawPanel.RECORD_3D_LINE;
        app.initViewer3D();
//                    	if (null==DrawPanel.listPoly) DrawPanel.listPoly=new java.util.Vector<Shape3D>();
        DrawPanel.poly = new ElementLine(app);
        DrawPanel.poly.addVertex(new Point3d(kernel.getActiveTurtle().X / 1000, kernel.getActiveTurtle().Y / 1000, kernel.getActiveTurtle().Z / 1000), kernel.getActiveTurtle().penColor);
    }

    void view3D(Stack<String> param) {
        try {
            primitive3D("3d.polyview");
            app.viewerOpen();
        } catch (LogoException ignored) {
        }
    }

    void endPolygon(Stack<String> param) {
        DrawPanel.record3D = DrawPanel.RECORD_3D_NONE;
        try {
            DrawPanel.poly.addToScene();
        } catch (LogoException ignored) {
        }
    }

    void listProperties(Stack<String> param) {
        String mot;
        try {
            Interpreter.returnValue = true;
            mot = getWord(param.get(0));
            if (null == mot) throw new LogoException(app, param.get(0) + " " + Logo.messages.getString("error.word"));
            Interpreter.operands.push(workspace.displayPropList(mot));
        } catch (LogoException ignored) {
        }
    }

    void startPolygon(Stack<String> param) {
        DrawPanel.record3D = DrawPanel.RECORD_3D_POLYGON;
        app.initViewer3D();
//                    	if (null==DrawPanel.listPoly) DrawPanel.listPoly=new java.util.Vector<Shape3D>();
        DrawPanel.poly = new ElementPolygon(app);
    }

    void removeProperty(Stack<String> param) {
        String mot;
        String mot2;
        try {
            Interpreter.returnValue = false;
            mot = getWord(param.get(0));
            if (null == mot) throw new LogoException(app, param.get(0) + " " + Logo.messages.getString("error.word"));
            mot2 = getWord(param.get(1));
            if (null == mot2) throw new LogoException(app, param.get(1) + " " + Logo.messages.getString("error.word"));
            workspace.removePropList(mot, mot2);
        } catch (LogoException ignored) {
        }
    }

    void getProperty(Stack<String> param) {
        String mot;
        String mot2;
        try {
            Interpreter.returnValue = true;
            mot = getWord(param.get(0));
            if (null == mot) throw new LogoException(app, param.get(0) + " " + Logo.messages.getString("error.word"));
            mot2 = getWord(param.get(1));
            if (null == mot2) throw new LogoException(app, param.get(1) + " " + Logo.messages.getString("error.word"));
            String value = workspace.getPropList(mot, mot2);
            if (value.startsWith("[")) value += " ";
            Interpreter.operands.push(value);
        } catch (LogoException ignored) {
        }
    }

    void setProperty(Stack<String> param) {
        String mot;
        String mot2;
        Interpreter.returnValue = false;
        try {
            mot = getWord(param.get(0));
            if (null == mot) throw new LogoException(app, param.get(0) + " " + Logo.messages.getString("error.word"));
            mot2 = getWord(param.get(1));
            if (null == mot2) throw new LogoException(app, param.get(1) + " " + Logo.messages.getString("error.word"));
            workspace.addPropList(mot, mot2, param.get(2));
        } catch (LogoException ignored) {
        }
    }

    void setZ(Stack<String> param) {
        delay();
        try {
            primitive3D("3d.setz");
            app.getDrawPanel().setPosition(kernel.getActiveTurtle().X + " " + kernel.getActiveTurtle().Y + " " + kernel.getCalculator().numberDouble(param.get(0)));

        } catch (LogoException ignored) {
        }
    }

    void setXYZ(Stack<String> param) {
        try {
            primitive3D("3d.setxyz");
            app.getDrawPanel().setPosition(kernel.getCalculator().numberDouble(param.get(0)) + " " + kernel.getCalculator().numberDouble(param.get(1)) + " " + kernel.getCalculator().numberDouble(param.get(2)));
        } catch (LogoException ignored) {
        }
    }

    void getOrientation(Stack<String> param) {
        try {
            primitive3D("3d.orientation");
            Interpreter.returnValue = true;
            String pitch = Calculator.teste_fin_double(kernel.getActiveTurtle().pitch);
            String roll = Calculator.teste_fin_double(kernel.getActiveTurtle().roll);
            String heading = Calculator.teste_fin_double(kernel.getActiveTurtle().heading);
            Interpreter.operands.push("[ " + roll + " " + pitch + " " + heading + " ] ");
        } catch (LogoException ignored) {
        }
    }

    void setOrientation(Stack<String> param) {
        try {
            primitive3D("3d.setorientation");
            delay();
            app.getDrawPanel().setOrientation(getFinalList(param.pop()));
        } catch (LogoException ignored) {
        }
    }

    void setPitch(Stack<String> param) {
        try {
            primitive3D("3d.setpitch");
            delay();
            app.getDrawPanel().setPitch(kernel.getCalculator().numberDouble(param.pop()));
        } catch (LogoException ignored) {
        }
    }

    void setRoll(Stack<String> param) {
        try {
            primitive3D("3d.setroll");
            delay();
            app.getDrawPanel().setRoll(kernel.getCalculator().numberDouble(param.pop()));
        } catch (LogoException ignored) {
        }
    }

    void getPitch(Stack<String> param) {
        try {
            primitive3D("3d.pitch");
            Interpreter.returnValue = true;
            Interpreter.operands.push(Calculator.teste_fin_double(kernel.getActiveTurtle().pitch));
        } catch (LogoException ignored) {
        }
    }

    void getRoll(Stack<String> param) {
        try {
            primitive3D("3d.roll");
            Interpreter.returnValue = true;
            Interpreter.operands.push(Calculator.teste_fin_double(kernel.getActiveTurtle().roll));
        } catch (LogoException ignored) {
        }
    }

    void pitchDown(Stack<String> param) {
        delay();
        try {
            primitive3D("3d.downpitch");
            app.getDrawPanel().pitch(-kernel.getCalculator().numberDouble(param.pop()));
        } catch (LogoException ignored) {
        }
    }

    void rollLeft(Stack<String> param) {
        delay();
        try {
            primitive3D("3d.leftroll");
            app.getDrawPanel().roll(-kernel.getCalculator().numberDouble(param.pop()));
        } catch (LogoException ignored) {
        }
    }

    void pitchUp(Stack<String> param) {
        delay();
        try {
            primitive3D("3d.uppitch");
            app.getDrawPanel().pitch(kernel.getCalculator().numberDouble(param.pop()));
        } catch (LogoException ignored) {
        }
    }

    void rollRight(Stack<String> param) {
        delay();
        try {
            primitive3D("3d.rightroll");
            app.getDrawPanel().roll(kernel.getCalculator().numberDouble(param.pop()));
        } catch (LogoException ignored) {
        }
    }

    void setAxisColor(Stack<String> param) {
        Interpreter.returnValue = false;
        try {
            if (isList(param.get(0))) {
                Logo.config.setAxisColor(rgb(param.get(0), Utils.primitiveName("drawing.setaxiscolor")).getRGB());
            } else {
                int coul = kernel.getCalculator().getInteger(param.get(0)) % DrawPanel.defaultColors.length;
                if (coul < 0) coul += DrawPanel.defaultColors.length;
                Logo.config.setAxisColor(DrawPanel.defaultColors[coul].getRGB());
            }
        } catch (LogoException ignored) {
        }
    }

    void enable3D(Stack<String> param) {
        app.getDrawPanel().perspective();
    }

    void setGridColor(Stack<String> param) {
        Interpreter.returnValue = false;
        try {
            if (isList(param.get(0))) {
                Logo.config.setGridColor(rgb(param.get(0), Utils.primitiveName("drawing.setgridcolor")).getRGB());
            } else {
                int coul = kernel.getCalculator().getInteger(param.get(0)) % DrawPanel.defaultColors.length;
                if (coul < 0) coul += DrawPanel.defaultColors.length;
                Logo.config.setGridColor(DrawPanel.defaultColors[coul].getRGB());
            }
        } catch (LogoException ignored) {
        }
    }

    void isYAxisEnabled(Stack<String> param) {
        Interpreter.returnValue = true;
        if (Logo.config.isYAxisEnabled()) Interpreter.operands.push(Logo.messages.getString("vrai"));
        else Interpreter.operands.push(Logo.messages.getString("faux"));
    }

    void isXAxisEnabled(Stack<String> param) {
        Interpreter.returnValue = true;
        if (Logo.config.isXAxisEnabled()) Interpreter.operands.push(Logo.messages.getString("vrai"));
        else Interpreter.operands.push(Logo.messages.getString("faux"));
    }

    void isGridEnabled(Stack<String> param) {
        Interpreter.returnValue = true;
        if (Logo.config.isGridEnabled()) Interpreter.operands.push(Logo.messages.getString("vrai"));
        else Interpreter.operands.push(Logo.messages.getString("faux"));
    }

    void getGridColor(Stack<String> param) {
        Color c;
        Interpreter.returnValue = true;
        c = new Color(Logo.config.getGridColor());
        Interpreter.operands.push("[ " + c.getRed() + " " + c.getGreen() + " " + c.getBlue() + " ] ");
    }

    void getAxisColor(Stack<String> param) {
        Color c;
        Interpreter.returnValue = true;
        c = new Color(Logo.config.getAxisColor());
        Interpreter.operands.push("[ " + c.getRed() + " " + c.getGreen() + " " + c.getBlue() + " ] ");
    }

    void isVariable(Stack<String> param) {
        String mot;
        try {
            Interpreter.returnValue = true;
            mot = getWord(param.get(0));
            if (null == mot) throw new LogoException(app, param.get(0) + " " + Logo.messages.getString("error.word"));
            mot = mot.toLowerCase();
            if (workspace.globals.containsKey(mot) || Interpreter.locals.containsKey(mot))
                Interpreter.operands.push(Logo.messages.getString("vrai"));
            else Interpreter.operands.push(Logo.messages.getString("faux"));
        } catch (LogoException ignored) {
        }
    }

    void disableAxes(Stack<String> param) {
        Logo.config.setXAxisEnabled(false);
        Logo.config.setYAxisEnabled(false);
        Interpreter.returnValue = false;
        clearScreen();
    }

    void bye(Stack<String> param) {
        app.closeWindow();
    }

    void drawYAxis(Stack<String> param) {
        Interpreter.returnValue = false;
        try {
            primitive2D("drawing.yaxis");
            int nombre = kernel.getCalculator().getInteger(param.get(0));
            if (nombre < 0) {
                String name = Utils.primitiveName("drawing.yaxis");
                throw new LogoException(app, name + " " + Logo.messages.getString("attend_positif"));
            } else if (nombre < 25) nombre = 25;
            Logo.config.setYAxisEnabled(true);
            Logo.config.setYAxisSpacing(nombre);
            clearScreen();
        } catch (LogoException ignored) {
        }
    }

    void drawXAxis(Stack<String> param) {
        Interpreter.returnValue = false;
        try {
            primitive2D("drawing.xaxis");
            int nombre = kernel.getCalculator().getInteger(param.get(0));
            if (nombre < 0) {
                String name = Utils.primitiveName("drawing.xaxis");
                throw new LogoException(app, name + " " + Logo.messages.getString("attend_positif"));
            } else if (nombre < 25) nombre = 25;
            Logo.config.setXAxisEnabled(true);
            Logo.config.setXAxisSpacing(nombre);
            clearScreen();
        } catch (LogoException ignored) {
        }
    }

    void drawBothAxes(Stack<String> param) {
        Interpreter.returnValue = false;
        try {
            primitive2D("drawing.axis");
            int nombre = kernel.getCalculator().getInteger(param.get(0));
            if (nombre < 0) {
                String name = Utils.primitiveName("drawing.axis");
                throw new LogoException(app, name + " " + Logo.messages.getString("attend_positif"));
            } else if (nombre < 25) nombre = 25;
            Logo.config.setXAxisEnabled(true);
            Logo.config.setXAxisSpacing(nombre);
            Logo.config.setYAxisEnabled(true);
            Logo.config.setYAxisSpacing(nombre);
            clearScreen();
        } catch (LogoException ignored) {
        }
    }

    void guiMenu(Stack<String> param) {
        String liste;
        try {
            String ident = getWord(param.get(0));
            if (null == ident) throw new LogoException(app, param.get(0) + " " + Logo.messages.getString("error.word"));
            liste = getFinalList(param.get(1));
            GuiMenu gm = new GuiMenu(ident.toLowerCase(), liste, app);
            app.getDrawPanel().addToGuiMap(gm);
        } catch (LogoException ignored) {
        }
    }

    void stopTrace(Stack<String> param) {
        Kernel.traceMode = false;
        Interpreter.returnValue = false;
    }

    void stopAnimation(Stack<String> param) {
        app.getDrawPanel().setAnimation(false);
        Interpreter.returnValue = false;
    }

    void disableGrid(Stack<String> param) {
        Interpreter.returnValue = false;
        Logo.config.setGridEnabled(false);
        clearScreen();
    }

    void drawGrid(Stack<String> param) {
        Interpreter.returnValue = false;
        try {
            primitive2D("drawing.grid");
            int[] args = new int[2];
            for (int i = 0; i < 2; i++) {
                args[i] = kernel.getCalculator().getInteger(param.get(i));
                if (args[i] < 0) {
                    String grille = Utils.primitiveName("drawing.grid");
                    throw new LogoException(app, grille + " " + Logo.messages.getString("attend_positif"));
                } else if (args[i] == 0) {
                    args[i] = 1;
                }
            }
            Logo.config.setGridEnabled(true);
            Logo.config.setXGridSpacing(args[0]);
            Logo.config.setYGridSpacing(args[1]);
            clearScreen();

        } catch (LogoException ignored) {
        }
    }

    void setZoom(Stack<String> param) {
        double d;
        Interpreter.returnValue = false;
        try {
            d = kernel.getCalculator().numberDouble(param.get(0));
            if (d <= 0) {
                String name = Utils.primitiveName("drawing.zoom");
                throw new LogoException(app, name + " " + Logo.messages.getString("attend_positif"));
            }
            app.getDrawPanel().zoom(d, false);
        } catch (LogoException ignored) {
        }
    }

    void guiDraw(Stack<String> param) {
        try {
            String ident = getWord(param.get(0));
            if (null == ident) throw new LogoException(app, param.get(0) + " " + Logo.messages.getString("error.word"));
            app.getDrawPanel().guiDraw(ident);
        } catch (LogoException ignored) {
        }
    }

    void guiPosition(Stack<String> param) {
        String liste;
        try {
            String ident = getWord(param.get(0));
            if (null == ident) throw new LogoException(app, param.get(0) + " " + Logo.messages.getString("error.word"));
            liste = getFinalList(param.get(1));
            app.getDrawPanel().guiposition(ident, liste, Utils.primitiveName("ui.guiposition"));
        } catch (LogoException ignored) {
        }
    }

    void guiRemove(Stack<String> param) {
        try {
            String ident = getWord(param.get(0));
            if (null == ident) throw new LogoException(app, param.get(0) + " " + Logo.messages.getString("error.word"));
            app.getDrawPanel().guiRemove(ident);
        } catch (LogoException ignored) {
        }
    }

    void guiAction(Stack<String> param) {
        String liste;
        try {
            String ident = getWord(param.get(0));
            if (null == ident) throw new LogoException(app, param.get(0) + " " + Logo.messages.getString("error.word"));
            liste = getFinalList(param.get(1));
            app.getDrawPanel().guiAction(ident, liste);
        } catch (LogoException ignored) {
        }
    }

    void guiButton(Stack<String> param) {
        String mot;
        try {
            String ident = getWord(param.get(0));
            if (null == ident) throw new LogoException(app, param.get(0) + " " + Logo.messages.getString("error.word"));
            mot = getWord(param.get(1));
            if (null == mot) throw new LogoException(app, param.get(1) + " " + Logo.messages.getString("error.word"));
            GuiButton gb = new GuiButton(ident.toLowerCase(), mot, app);
            app.getDrawPanel().addToGuiMap(gb);
        } catch (LogoException ignored) {
        }
    }

    void setScreenSize(Stack<String> param) {
        String liste;
        Interpreter.returnValue = false;
        try {
            String prim = Utils.primitiveName("drawing.setscreensize");
            liste = getFinalList(param.get(0));
            int width, height;
            StringTokenizer st = new StringTokenizer(liste);
            try {
                if (!st.hasMoreTokens())
                    throw new LogoException(app, prim + " " + Logo.messages.getString("n_aime_pas") + liste + Logo.messages.getString("comme_parametre"));
                width = Integer.parseInt(st.nextToken());
                if (!st.hasMoreTokens())
                    throw new LogoException(app, prim + " " + Logo.messages.getString("n_aime_pas") + liste + Logo.messages.getString("comme_parametre"));
                height = Integer.parseInt(st.nextToken());
            } catch (NumberFormatException e) {
                throw new LogoException(app, prim + " " + Logo.messages.getString("n_aime_pas") + liste + Logo.messages.getString("comme_parametre"));
            }
            if (st.hasMoreTokens())
                throw new LogoException(app, prim + " " + Logo.messages.getString("n_aime_pas") + liste + Logo.messages.getString("comme_parametre"));
            boolean changement = height != Logo.config.getImageHeight();
            Logo.config.setImageHeight(height);
            if (width != Logo.config.getImageWidth()) changement = true;
            Logo.config.setImageWidth(width);
            if (Logo.config.getImageWidth() < 100 || Logo.config.getImageHeight() < 100) {
                Logo.config.setImageWidth(400);
                Logo.config.setImageHeight(400);
            }
            if (changement) {
                app.resizeDrawingZone();
            }
        } catch (LogoException ignored) {
        }
    }

    void getTurtlesMax(Stack<String> param) {
        Interpreter.returnValue = true;
        Interpreter.operands.push(String.valueOf(Logo.config.getMaxTurtles()));
    }

    void setTurtlesMax(Stack<String> param) {
        Interpreter.returnValue = false;
        try {
            int i = kernel.getCalculator().getInteger(param.get(0));
            if (i < 0) {
                String fmt = Utils.primitiveName("drawing.setturtlesmax");
                throw new LogoException(app, fmt + " " + Logo.messages.getString("attend_positif"));
            } else if (i == 0) i = 1;
            kernel.setNumberOfTurtles(i);
        } catch (LogoException ignored) {
        }
    }

    void getDrawingQuality(Stack<String> param) {
        Interpreter.returnValue = true;
        Interpreter.operands.push(String.valueOf(Logo.config.getDrawQuality()));
    }

    void setDrawingQuality(Stack<String> param) {
        Interpreter.returnValue = false;
        try {
            int i = kernel.getCalculator().getInteger(param.get(0));
            if (i != Config.DRAW_QUALITY_NORMAL && i != Config.DRAW_QUALITY_HIGH && i != Config.DRAW_QUALITY_LOW) {
                String st = Utils.primitiveName("drawing.setdrawingquality") + " " + Logo.messages.getString("error_bad_values") + " 0 1 2";
                throw new LogoException(app, st);
            }
            Logo.config.setDrawQuality(i);
            kernel.setDrawingQuality(Logo.config.getDrawQuality());
        } catch (LogoException ignored) {
        }
    }

    void getPenShape(Stack<String> param) {
        Interpreter.returnValue = true;
        Interpreter.operands.push(String.valueOf(Logo.config.getPenShape()));
    }

    void setPenShape(Stack<String> param) {
        Interpreter.returnValue = false;
        try {
            int i = kernel.getCalculator().getInteger(param.get(0));
            if (i != Config.PEN_SHAPE_OVAL && i != Config.PEN_SHAPE_SQUARE) {
                String st = Utils.primitiveName("drawing.setpenshape") + " " + Logo.messages.getString("error_bad_values");
                st += " " + Config.PEN_SHAPE_SQUARE + " " + Config.PEN_SHAPE_OVAL;
                throw new LogoException(app, st);
            }
            Logo.config.setPenShape(i);
            app.getDrawPanel().updateAllTurtleShape();
            app.getDrawPanel().setStroke(kernel.getActiveTurtle().crayon);
        } catch (LogoException ignored) {
        }
    }

    void getPenWidth(Stack<String> param) {
        Interpreter.returnValue = true;
        double penwidth = 2 * kernel.getActiveTurtle().getPenWidth();
        Interpreter.operands.push(Calculator.teste_fin_double(penwidth));
    }

    void resetAll(Stack<String> param) {
        Interpreter.returnValue = false;
        // resize drawing zone if necessary
        if (Logo.config.getImageHeight() != 1000 || Logo.config.getImageWidth() != 1000) {
            Logo.config.setImageHeight(1000);
            Logo.config.setImageWidth(1000);
            app.resizeDrawingZone();
        }
        Logo.config.setGridEnabled(false);
        Logo.config.setXAxisEnabled(false);
        Logo.config.setYAxisEnabled(false);
        app.getDrawPanel().home();
        app.getDrawPanel().resetScreenColor();
        if (kernel.getActiveTurtle().id == 0) {
            Logo.config.setActiveTurtle(0);
        }
        DrawPanel.windowMode = DrawPanel.WINDOW_CLASSIC;
        kernel.change_image_tortue(0);
        app.getDrawPanel().setScreenColor(Color.WHITE);
        app.getDrawPanel().setPenColor(Color.BLACK);
        app.getDrawPanel().setAnimation(false);
        Logo.config.setFont(new Font("dialog", Font.PLAIN, 12));
        kernel.getActiveTurtle().police = 12;
        app.getDrawPanel().setGraphicsFont(Logo.config.getFont());
        HistoryPanel.printFontId = Application.getFontId(Logo.config.getFont());
        app.getHistoryPanel().setFontSize(12);
        app.getHistoryPanel().setFontNumber(HistoryPanel.printFontId);
        app.getHistoryPanel().setTextColor(Color.black);
        Logo.config.setPenShape(0);
        Logo.config.setDrawQuality(0);
        kernel.setDrawingQuality(Logo.config.getDrawQuality());
        kernel.setNumberOfTurtles(16);
        Logo.config.setTurtleSpeed(0);
        Kernel.traceMode = false;
        DrawPanel.windowMode = DrawPanel.WINDOW_CLASSIC;
        app.getDrawPanel().zoom(1, false);
    }

    void chatTcp(Stack<String> param) {
        String mot;
        String liste;
        Interpreter.returnValue = false;
        mot = getWord(param.get(0));
        if (null == mot) {
            try {
                throw new LogoException(app, param.get(0) + " " + Logo.messages.getString("error.word"));
            } catch (LogoException ignored) {
            }
        }
        mot = Objects.requireNonNull(mot).toLowerCase();
        try {
            liste = getFinalList(param.get(1));
            new NetworkClientChat(app, mot, liste);
        } catch (LogoException ignored) {
        }
    }

    void endExecuteTcp(Stack<String> param) {
        // \x internal operator to specify
        // the end of network instructions with
        // "executetcp"
        // have to replace workspace
        Interpreter.returnValue = false;
        kernel.setWorkspace(savedWorkspace.pop());
    }

    void executeTcp(Stack<String> param) {
        String mot;
        String liste;
        mot = getWord(param.get(0));
        if (null == mot) {
            try {
                throw new LogoException(app, param.get(0) + " " + Logo.messages.getString("error.word"));
            } catch (LogoException ignored) {
            }
        }
        mot = Objects.requireNonNull(mot).toLowerCase();
        try {
            liste = getFinalList(param.get(1));
            new NetworkClientExecute(app, mot, liste);
        } catch (LogoException ignored) {
        }
    }

    void listenTcp(Stack<String> param) {
        Interpreter.returnValue = false;
        if (null == savedWorkspace) savedWorkspace = new Stack<>();
        savedWorkspace.push(workspace);
        new NetworkServer(app);
    }

    void sendTcp(Stack<String> param) {
        String liste;
        String mot;
        Interpreter.returnValue = true;
        mot = getWord(param.get(0));
        if (null == mot) {
            try {
                throw new LogoException(app, param.get(0) + " " + Logo.messages.getString("error.word"));
            } catch (LogoException ignored) {
            }
        }
        mot = Objects.requireNonNull(mot).toLowerCase();
        try {
            liste = getFinalList(param.get(1));
            NetworkClientSend ncs = new NetworkClientSend(app, mot, liste);
            Interpreter.operands.push("[ " + ncs.getAnswer() + " ] ");
        } catch (LogoException ignored) {
        }
    }

    void getLabelLength(Stack<String> param) {
        String mot;
        try {
            mot = getWord(param.get(0));
            if (null != mot) mot = Utils.unescapeString(mot);
            else mot = getFinalList(param.get(0)).trim();
            Interpreter.returnValue = true;
            FontMetrics fm = app.getDrawPanel().getGraphics().getFontMetrics(app.getDrawPanel().getGraphicsFont());
            int longueur = fm.stringWidth(mot);
            Interpreter.operands.push(String.valueOf(longueur));
        } catch (LogoException ignored) {
        }
    }

    void getZoneSize(Stack<String> param) {
        StringBuilder sb;
        Interpreter.returnValue = true;
        Point p = app.scrollPane.getViewport().getViewPosition();
        Rectangle rec = app.scrollPane.getVisibleRect();
        sb = new StringBuilder();
        int x1 = p.x - Logo.config.getImageWidth() / 2;
        int y1 = Logo.config.getImageHeight() / 2 - p.y;
        int x2 = x1 + rec.width - app.scrollPane.getVerticalScrollBar().getWidth();
        int y2 = y1 - rec.height + app.scrollPane.getHorizontalScrollBar().getHeight();
        sb.append("[ ");
        sb.append(x1);
        sb.append(" ");
        sb.append(y1);
        sb.append(" ");
        sb.append(x2);
        sb.append(" ");
        sb.append(y2);
        sb.append(" ] ");
        Interpreter.operands.push(new String(sb));
    }

    void getTextStyle(Stack<String> param) {
        StringBuilder buffer = new StringBuilder();
        int compteur = 0;
        if (app.getHistoryPanel().isBold()) {
            buffer.append(Logo.messages.getString("style.bold").toLowerCase()).append(" ");
            compteur++;
        }
        if (app.getHistoryPanel().isItalic()) {
            buffer.append(Logo.messages.getString("style.italic").toLowerCase()).append(" ");
            compteur++;
        }
        if (app.getHistoryPanel().isUnderline()) {
            buffer.append(Logo.messages.getString("style.underline").toLowerCase()).append(" ");
            compteur++;
        }
        if (app.getHistoryPanel().isSuperscript()) {
            buffer.append(Logo.messages.getString("style.exposant").toLowerCase()).append(" ");
            compteur++;
        }
        if (app.getHistoryPanel().isSubscript()) {
            buffer.append(Logo.messages.getString("style.subscript").toLowerCase()).append(" ");
            compteur++;
        }
        if (app.getHistoryPanel().isStrikethrough()) {
            buffer.append(Logo.messages.getString("style.strike").toLowerCase()).append(" ");
            compteur++;
        }
        Interpreter.returnValue = true;
        if (compteur == 0) Interpreter.operands.push("\"" + Logo.messages.getString("style.none").toLowerCase());
        else if (compteur == 1) Interpreter.operands.push("\"" + new String(buffer).trim());
        else Interpreter.operands.push("[ " + new String(buffer) + "]");
    }

    void setTextStyle(Stack<String> param) {
        String liste;
        String mot;
        try {
            boolean bold = false;
            boolean italic = false;
            boolean underline = false;
            boolean superscript = false;
            boolean subscript = false;
            boolean strikethrough = false;
            mot = getWord(param.get(0));
            if (null == mot) liste = getFinalList(param.get(0));
            else liste = mot;
            if (liste.trim().equals("")) liste = Logo.messages.getString("style.none");
            StringTokenizer st = new StringTokenizer(liste);
            while (st.hasMoreTokens()) {
                String element = st.nextToken().toLowerCase();
                if (element.equals(Logo.messages.getString("style.underline").toLowerCase())) {
                    underline = true;
                } else if (element.equals(Logo.messages.getString("style.bold").toLowerCase())) {
                    bold = true;
                } else if (element.equals(Logo.messages.getString("style.italic").toLowerCase())) {
                    italic = true;
                } else if (element.equals(Logo.messages.getString("style.exposant").toLowerCase())) {
                    superscript = true;
                } else if (element.equals(Logo.messages.getString("style.subscript").toLowerCase())) {
                    subscript = true;
                } else if (element.equals(Logo.messages.getString("style.strike").toLowerCase())) {
                    strikethrough = true;
                } else if (element.equals(Logo.messages.getString("style.none").toLowerCase())) {
                    // ignored
                } else throw new LogoException(app, Logo.messages.getString("erreur_fixestyle"));
            }
            app.getHistoryPanel().setBold(bold);
            app.getHistoryPanel().setItalic(italic);
            app.getHistoryPanel().setUnderline(underline);
            app.getHistoryPanel().setSuperscript(superscript);
            app.getHistoryPanel().setSubscript(subscript);
            app.getHistoryPanel().setStrikeThrough(strikethrough);
        } catch (LogoException ignored) {
        }
    }

    void closeParen(Stack<String> param) {
        // Let us distinguish the two cases: (3)*2 and (4+3)*2 The 3 is here to return to the +
        boolean a_retourner = true;
        // We remove the "(" corresponding to the opening parenthesis of the stack
        // name provided that the waiting element of the stack name is not a procedure
        boolean est_procedure = false;
        int pos = Interpreter.consumers.lastIndexOf("(");
        if (pos == -1) {
            // Closing parenthesis without opening parenthesis first
            try {
                throw new LogoException(app, Logo.messages.getString("parenthese_ouvrante"));
            } catch (LogoException ignored) {
            }
        } else {
            // Let's avoid the error in case of for example: "etc )"
            // (closing parenthesis without opening) -->
            // else to be executed only in case of no error
            if (Interpreter.openParen) {
                // empty parentheses
                try {
                    throw new LogoException(app, Logo.messages.getString("parenthese_vide"));
                } catch (LogoException ignored) {
                }

            }
            for (int j = pos; j < Interpreter.consumers.size(); j++) {
                String proc = Interpreter.consumers.get(j).toLowerCase();
                if (primitiveMap.containsKey(proc)) est_procedure = true;
                else {
                    for (int i = 0; i < workspace.getNumberOfProcedure(); i++) {
                        if (workspace.getProcedure(i).name.equals(proc)) {
                            est_procedure = true;
                            break;
                        }
                    }
                    if (est_procedure) break;
                }
            }
        }
        // If a procedure is present in the name stack, we keep the parentheses
        if (est_procedure) {
            app.getKernel().getInstructionBuffer().insert(") ");
        }
        // Otherwise we remove them with their possible nestings
        else {
            if (Interpreter.procedures.isEmpty() || !Interpreter.procedures.peek().equals("(")) {
                try {
                    throw new LogoException(app, Logo.messages.getString("parenthese_ouvrante"));
                } catch (LogoException ignored) {
                }
            } else Interpreter.procedures.pop();
            if (!Interpreter.consumers.isEmpty()) {
                if (Interpreter.consumers.peek().equals("(")) a_retourner = false;
                pos = Interpreter.consumers.lastIndexOf("(");
                if (pos == -1) {
                    // Closing parenthesis without first opening parenthesis
                    try {
                        throw new LogoException(app, Logo.messages.getString("parenthese_ouvrante"));
                    } catch (LogoException ignored) {
                    }
                } else {
                    Interpreter.consumers.removeElementAt(pos);
                    // If there is nesting of parentheses (((20)))
                    pos--;
                    InstructionBuffer instruction = app.getKernel().getInstructionBuffer();
                    while (instruction.getNextWord().equals(")") && (pos > -1)) {
                        if (!Interpreter.consumers.isEmpty() && Interpreter.consumers.get(pos).equals("(")) {
                            instruction.deleteFirstWord(")");
                            Interpreter.consumers.removeElementAt(pos);
                            pos--;
                        } else break;
                    }
                }
            }
        }
        if (Interpreter.operands.isEmpty()) {
            Interpreter.returnValue = false;
        } else {
            Interpreter.returnValue = true;
            Interpreter.closeParen = a_retourner;
        }
    }

    void circle(Stack<String> param) {
        try {
            app.getDrawPanel().circle((kernel.getCalculator().numberDouble(param.pop())));
        } catch (LogoException ignored) {
        }
    }

    void colorWhite(Stack<String> param) {
        colorCode(7);
    }

    void colorCyan(Stack<String> param) {
        colorCode(6);
    }

    void colorMagenta(Stack<String> param) {
        colorCode(5);
    }

    void colorBlue(Stack<String> param) {
        colorCode(4);
    }

    void colorYellow(Stack<String> param) {
        colorCode(3);
    }

    void colorGreen(Stack<String> param) {
        colorCode(2);
    }

    void colorRed(Stack<String> param) {
        colorCode(1);
    }

    void colorBlack(Stack<String> param) {
        colorCode(0);
    }

    void colorBrown(Stack<String> param) {
        colorCode(16);
    }

    void colorPurple(Stack<String> param) {
        colorCode(15);
    }

    void colorPink(Stack<String> param) {
        colorCode(14);
    }

    void colorOrange(Stack<String> param) {
        colorCode(13);
    }

    void colorDarkBlue(Stack<String> param) {
        colorCode(12);
    }

    void colorDarkGreen(Stack<String> param) {
        colorCode(11);
    }

    void colorDarkRed(Stack<String> param) {
        colorCode(10);
    }

    void colorLightGray(Stack<String> param) {
        colorCode(9);
    }

    void colorGray(Stack<String> param) {
        colorCode(8);
    }

    void addItem(Stack<String> param) {
        String mot;
        String liste;
        try {
            String reponse;
            liste = getFinalList(param.get(0));
            int entier = kernel.getCalculator().getInteger(param.get(1));
            mot = getWord(param.get(2));
            if (null != mot && mot.equals("")) mot = "\\v";
            if (null == mot) mot = "[ " + getFinalList(param.get(2)) + "]";
            char element;
            int compteur = 1;
            boolean espace = true;
            boolean crochet = false;
            boolean error = true;
            for (int j = 0; j < liste.length(); j++) {
                if (compteur == entier) {
                    error = false;
                    compteur = j;
                    break;
                }
                element = liste.charAt(j);
                if (element == '[') {
                    if (espace) crochet = true;
                    espace = false;
                }
                if (element == ' ') {
                    espace = true;
                    if (crochet) {
                        crochet = false;
                        j = extractList(liste, j);
                    }
                    compteur++;
                    if (j == liste.length() - 1 && compteur == entier) {
                        error = false;
                        compteur = liste.length();
                    }
                }
            }
            if (error && entier != compteur)
                throw new LogoException(app, Logo.messages.getString("y_a_pas") + " " + entier + " " + Logo.messages.getString("element_dans_liste") + liste + "]");
            if (!liste.trim().equals(""))
                reponse = "[ " + liste.substring(0, compteur) + mot + " " + liste.substring(compteur) + "] ";
            else reponse = "[ " + mot + " ] ";
            Interpreter.returnValue = true;
            Interpreter.operands.push(reponse);
        } catch (LogoException ignored) {
        }
    }

    void replaceItem(Stack<String> param) {
        String mot;
        String liste;
        try {
            String reponse;
            liste = getFinalList(param.get(0));
            int entier = kernel.getCalculator().getInteger(param.get(1));
            mot = getWord(param.get(2));
            if (null != mot && mot.equals("")) mot = "\\v";
            if (null == mot) mot = "[ " + getFinalList(param.get(2)) + "]";
            char element;
            int compteur = 1;
            boolean espace = true;
            boolean crochet = false;
            boolean error = true;
            for (int j = 0; j < liste.length(); j++) {
                if (compteur == entier) {
                    error = false;
                    compteur = j;
                    break;
                }
                element = liste.charAt(j);
                if (element == '[') {
                    if (espace) crochet = true;
                    espace = false;
                }
                if (element == ' ') {
                    espace = true;
                    if (crochet) {
                        crochet = false;
                        j = extractList(liste, j);
                    }
                    compteur++;
                }
            }
            if (error)
                throw new LogoException(app, Logo.messages.getString("y_a_pas") + " " + entier + " " + Logo.messages.getString("element_dans_liste") + liste + "]");
            reponse = "[ " + liste.substring(0, compteur) + mot;
            // On extrait le mot suivant
            if (compteur + 1 < liste.length() && liste.charAt(compteur) == '[' && liste.charAt(compteur + 1) == ' ') {
                compteur = extractList(liste, compteur + 2);

            } else {
                for (int i = compteur + 1; i < liste.length(); i++) {
                    if (liste.charAt(i) == ' ') {
                        compteur = i;
                        break;
                    }
                }
            }
            reponse += liste.substring(compteur) + "] ";
            Interpreter.returnValue = true;
            Interpreter.operands.push(reponse);
        } catch (LogoException ignored) {
        }
    }

    void abs(Stack<String> param) {
        try {
            Interpreter.returnValue = true;
            Interpreter.operands.push(kernel.getCalculator().abs(param.get(0)));
        } catch (LogoException ignored) {
        }
    }

    void forLoop(Stack<String> param) {
        try {
            String li2 = getList(param.get(1));
            li2 = new String(Utils.formatCode(li2));
            String li1 = getFinalList(param.get(0));
            int nb = numberOfElements(li1);
            if (nb < 3 || nb > 4) throw new LogoException(app, Logo.messages.getString("erreur_repetepour"));
            StringTokenizer st = new StringTokenizer(li1);
            String var = st.nextToken().toLowerCase();
            BigDecimal deb = kernel.getCalculator().numberDecimal(st.nextToken());
            BigDecimal fin = kernel.getCalculator().numberDecimal(st.nextToken());
            BigDecimal increment = BigDecimal.ONE;
            if (nb == 4) increment = kernel.getCalculator().numberDecimal(st.nextToken());
            if (var.equals("")) throw new LogoException(app, Logo.messages.getString("variable_vide"));
            try {
                Double.parseDouble(var);
                throw new LogoException(app, Logo.messages.getString("erreur_nom_nombre_variable"));
            } catch (NumberFormatException e) {
                LoopFor bp = new LoopFor(deb, fin, increment, li2, var);
                bp.AffecteVar(true);

                if ((increment.compareTo(BigDecimal.ZERO) > 0 && fin.compareTo(deb) >= 0) || (increment.compareTo(BigDecimal.ZERO) < 0 && fin.compareTo(deb) <= 0)) {
                    app.getKernel().getInstructionBuffer().insert(li2 + Interpreter.END_LOOP + " ");
                    loops.push(bp);
                }
            }
        } catch (LogoException ignored) {
        }
    }

    void getRepCount(Stack<String> param) {
        boolean erreur = false;
        if (!loops.isEmpty()) {
            Loop bp = loops.peek();
            if (bp.isRepeat()) {
                Interpreter.returnValue = true;
                Interpreter.operands.push(bp.getCounter().toString());
            } else erreur = true;
        } else erreur = true;
        if (erreur) {
            try {
                throw new LogoException(app, Logo.messages.getString("erreur_compteur"));
            } catch (LogoException ignored) {
            }
        }
    }

    void getCharacter(Stack<String> param) {
        try {
            int i = kernel.getCalculator().getInteger(param.get(0));
            if (i < 0 || i > 65535)
                throw new LogoException(app, param.get(0) + " " + Logo.messages.getString("nombre_unicode"));
            else {
                String st;
                Interpreter.returnValue = true;
                if (i == 92) st = "\"\\\\";
                else if (i == 10) st = "\"\\n";
                else if (i == 32) st = "\"\\e";
                else {
                    st = String.valueOf((char) i);
                    try {
                        Double.parseDouble(st);
                    } catch (NumberFormatException e) {
                        st = "\"" + st;
                    }
                }
                Interpreter.operands.push(st);
            }
        } catch (LogoException ignored) {
        }
    }

    void stopAll(Stack<String> param) {
        app.error = true;
    }

    void getUnicode(Stack<String> param) {
        String mot;
        try {
            mot = getWord(param.get(0));
            if (null == mot) throw new LogoException(app, param.get(0) + " " + Logo.messages.getString("error.word"));
            else if (getWordLength(mot) != 1)
                throw new LogoException(app, param.get(0) + " " + Logo.messages.getString("un_caractere"));
            else {
                Interpreter.returnValue = true;
                String st = String.valueOf((int) Utils.unescapeString(itemWord(1, mot)).charAt(0));
                Interpreter.operands.push(st);
            }
        } catch (LogoException ignored) {
        }
    }

    void changeDirectory(Stack<String> param) {
        String mot;
        Interpreter.returnValue = false;
        try {
            mot = getWord(param.get(0));
            if (null == mot) throw new LogoException(app, param.get(0) + " " + Logo.messages.getString("error.word"));
            String chemin;
            if (Logo.config.getDefaultFolder().endsWith(File.separator))
                chemin = Utils.unescapeString(Logo.config.getDefaultFolder() + mot);
            else
                chemin = Utils.unescapeString(Logo.config.getDefaultFolder() + Utils.escapeString(File.separator) + mot);
            if ((new File(chemin)).isDirectory()) {
                try {
                    Logo.config.setDefaultFolder(Utils.escapeString((new File(chemin)).getCanonicalPath()));
                } catch (NullPointerException | IOException ignored) {
                }
            } else
                throw new LogoException(app, Utils.escapeString(chemin) + " " + Logo.messages.getString("erreur_pas_repertoire"));
        } catch (LogoException ignored) {
        }
    }

    void trace(Stack<String> param) {
        Kernel.traceMode = true;
        Interpreter.returnValue = false;
    }

    void truncate(Stack<String> param) {
        Interpreter.returnValue = true;
        try {
            Interpreter.operands.push(kernel.getCalculator().truncate(param.get(0)));
        } catch (LogoException ignored) {
        }
    }

    void getSeparation(Stack<String> param) {
        Interpreter.returnValue = true;
        Interpreter.operands.push(Calculator.teste_fin_double(app.splitPane.getResizeWeight()));
    }

    void setSeparation(Stack<String> param) {
        try {
            double nombre = kernel.getCalculator().numberDouble(param.get(0));
            if (nombre < 0 || nombre > 1)
                throw new LogoException(app, nombre + " " + Logo.messages.getString("entre_zero_un"));
            app.splitPane.setResizeWeight(nombre);
            app.splitPane.setDividerLocation(nombre);
        } catch (LogoException ignored) {
        }
    }

    void isInteger(Stack<String> param) {
        Interpreter.returnValue = true;
        try {
            double ent = kernel.getCalculator().numberDouble(param.get(0));
            if ((int) ent == ent) Interpreter.operands.push(Logo.messages.getString("vrai"));
            else Interpreter.operands.push(Logo.messages.getString("faux"));
        } catch (LogoException ignored) {
        }
    }

    void quotient(Stack<String> param) {
        try {
            Interpreter.returnValue = true;
            Interpreter.operands.push(kernel.getCalculator().quotient(param.get(0), param.get(1)));
        } catch (LogoException ignored) {
        }
    }

    void getImageSize(Stack<String> param) {
        Interpreter.returnValue = true;
        StringBuilder sb = new StringBuilder();
        sb.append("[ ");
        sb.append(Logo.config.getImageWidth());
        sb.append(" ");
        sb.append(Logo.config.getImageHeight());
        sb.append(" ] ");
        Interpreter.operands.push(new String(sb));
    }

    void startAnimation(Stack<String> param) {
        app.getDrawPanel().setAnimation(true);
        Interpreter.returnValue = false;
    }

    void refresh(Stack<String> param) {
        if (DrawPanel.classicMode == DrawPanel.MODE_ANIMATION) {
            app.getDrawPanel().refresh();
        }
    }

    void arc(Stack<String> param) {
        try {
            app.getDrawPanel().arc(kernel.getCalculator().numberDouble(param.get(0)), kernel.getCalculator().numberDouble(param.get(1)), kernel.getCalculator().numberDouble(param.get(2)));
        } catch (LogoException ignored) {
        }
    }

    void fillZone(Stack<String> param) {
        app.getDrawPanel().fillZone();
    }

    void write(Stack<String> param) {
        String mot;
        String par = param.get(0).trim();
        if (isList(par)) par = formatList(par.substring(1, par.length() - 1));
        mot = getWord(param.get(0));
        if (null == mot) app.updateHistory("perso", Utils.unescapeString(par));
        else app.updateHistory("perso", Utils.unescapeString(mot));
    }

    void getVariableValue(Stack<String> param) {
        String mot;
        mot = getWord(param.get(0));
        try {
            if (null == mot) {
                throw new LogoException(app, Logo.messages.getString("error.word"));
            } // si c'est une liste
            else if (wordPrefix.equals("")) {
                throw new LogoException(app, Logo.messages.getString("erreur_variable"));
            } // si c'est un nombre
            Interpreter.returnValue = true;
            String value;
            mot = mot.toLowerCase();
            if (!Interpreter.locals.containsKey(mot)) {
                if (!workspace.globals.containsKey(mot))
                    throw new LogoException(app, mot + " " + Logo.messages.getString("erreur_variable"));
                else value = workspace.globals.get(mot);
            } else {
                value = Interpreter.locals.get(mot);
            }
            if (null == value) throw new LogoException(app, mot + "  " + Logo.messages.getString("erreur_variable"));
            Interpreter.operands.push(value);
        } catch (LogoException ignored) {
        }
    }

    void wash(Stack<String> param) {
        app.getDrawPanel().wash();
    }

    void listVariables(Stack<String> param) {
        Interpreter.returnValue = true;
        Interpreter.operands.push(new String(getAllVariables()));
    }

    void isMouseEvent(Stack<String> param) {
        Interpreter.returnValue = true;
        if (app.getDrawPanel().get_lissouris()) Interpreter.operands.push(Logo.messages.getString("vrai"));
        else Interpreter.operands.push(Logo.messages.getString("faux"));
    }

    void fileAppendLine(Stack<String> param) {
        String liste;
        try {
            int ident = kernel.getCalculator().getInteger(param.get(0));
            int index = kernel.flows.search(ident);
            liste = getFinalList(param.get(1));
            if (index == -1) throw new LogoException(app, Logo.messages.getString("flux_non_disponible") + " " + ident);
            Flow flow = kernel.flows.get(index);
            FlowWriter flowWriter;
            // If the flow is a readable flow, throw an error
            if (flow.isReader()) throw new LogoException(app, Logo.messages.getString("flux_ecriture"));
                // Else if the flow is a writable flow , convert to MrFlowWriter
            else if (flow.isWriter()) flowWriter = (FlowWriter) flow;
                // Else the flow isn't defined yet, initialize
            else flowWriter = new FlowWriter(flow);

            // Write the line
            flowWriter.append(Utils.unescapeString(liste));
            kernel.flows.set(index, flowWriter);
        } catch (IOException | LogoException ignored) {
        }
    }

    void clearText(Stack<String> param) {
        app.getHistoryPanel().clearText();
    }

    void closeFile(Stack<String> param) {
        try {
            int ident = kernel.getCalculator().getInteger(param.get(0));
            int index = kernel.flows.search(ident);
            if (index == -1) throw new LogoException(app, Logo.messages.getString("flux_non_disponible") + " " + ident);
            Flow flow = kernel.flows.get(index);
            // If the flow is a readable flow
            if (flow.isReader()) ((FlowReader) flow).close();
                // Else if it's a writable flow
            else if (flow.isWriter()) ((FlowWriter) flow).close();
            kernel.flows.remove(index);
        } catch (IOException | LogoException ignored) {
        }
    }

    void openFile(Stack<String> param) {
        String liste;
        String mot;
        try {
            mot = getWord(param.get(1));
            if (null == mot) throw new LogoException(app, param.get(0) + " " + Logo.messages.getString("error.word"));
            liste = Utils.unescapeString(Logo.config.getDefaultFolder()) + File.separator + Utils.unescapeString(mot);
            int ident = kernel.getCalculator().getInteger(param.get(0));
            if (kernel.flows.search(ident) == -1) kernel.flows.add(new Flow(ident, liste, false));
            else throw new LogoException(app, ident + " " + Logo.messages.getString("flux_existant"));
        } catch (LogoException ignored) {
        }
    }

    void isEndOfFile(Stack<String> param) {
        try {
            int ident = kernel.getCalculator().getInteger(param.get(0));
            int index = kernel.flows.search(ident);
            if (index == -1) throw new LogoException(app, Logo.messages.getString("flux_non_disponible") + " " + ident);
            else {
                Flow flow = kernel.flows.get(index);
                FlowReader flowReader = null;
                // If the flow isn't defined yet, initialize
                if (!flow.isWriter() && !flow.isReader()) {
                    flowReader = new FlowReader(flow);
                } else if (flow.isReader()) flowReader = (FlowReader) flow;
                if (null != flowReader) {
                    if (flow.isFinished()) {
                        Interpreter.returnValue = true;
                        Interpreter.operands.push(Logo.messages.getString("control.true"));
                    } else {
                        int read = flowReader.isReadable();
                        Interpreter.returnValue = true;
                        if (read == -1) {
                            Interpreter.operands.push(Logo.messages.getString("control.true"));
                            flow.setFinished(true);
                        } else {
                            Interpreter.operands.push(Logo.messages.getString("control.false"));
                        }
                    }
                } else throw new LogoException(app, Logo.messages.getString("flux_lecture"));
            }
        } catch (IOException | LogoException ignored) {
        }
    }

    void fileWriteLine(Stack<String> param) {
        String liste;
        try {
            int ident = kernel.getCalculator().getInteger(param.get(0));
            int index = kernel.flows.search(ident);
            liste = getFinalList(param.get(1));
            if (index == -1) throw new LogoException(app, Logo.messages.getString("flux_non_disponible") + " " + ident);
            Flow flow = kernel.flows.get(index);
            FlowWriter flowWriter;
            // If the flow is a readable flow, throw an error
            if (flow.isReader()) throw new LogoException(app, Logo.messages.getString("flux_ecriture"));
                // Else if the flow is a writable flow , convert to MrFlowWriter
            else if (flow.isWriter()) flowWriter = (FlowWriter) flow;
                // Else the flow isn't defined yet, initialize
            else flowWriter = new FlowWriter(flow);
            // Write the line
            flowWriter.write(Utils.unescapeString(liste));
            kernel.flows.set(index, flowWriter);
        } catch (IOException | LogoException ignored) {
        }
    }

    void fileReadChar(Stack<String> param) {
        try {
            int ident = kernel.getCalculator().getInteger(param.get(0));
            int index = kernel.flows.search(ident);
            if (index == -1) throw new LogoException(app, Logo.messages.getString("flux_non_disponible") + " " + ident);
            Flow flow = kernel.flows.get(index);
            FlowReader flowReader;
            // If the flow is a writable flow, throw error
            if (flow.isWriter()) throw new LogoException(app, Logo.messages.getString("flux_lecture"));
                // else if the flow is reader, convert to FlowReader
            else if (flow.isReader()) {
                flowReader = ((FlowReader) flow);
            }
            // else the flow isn't yet defined, initialize
            else flowReader = new FlowReader(flow);

            if (flowReader.isFinished())
                throw new LogoException(app, Logo.messages.getString("fin_flux") + " " + ident);

            int character = ((FlowReader) flow).readChar();
            if (character == -1) {
                flow.setFinished(true);
                throw new LogoException(app, Logo.messages.getString("fin_flux") + " " + ident);
            }
            Interpreter.returnValue = true;
            String car = String.valueOf(character);
            if (car.equals("\\")) car = "\\\\";
            Interpreter.operands.push(car);
            kernel.flows.set(index, flowReader);
        } catch (FileNotFoundException e1) {
            try {
                throw new LogoException(app, Logo.messages.getString("error.iolecture"));
            } catch (LogoException ignored) {
            }
        } catch (IOException | LogoException ignored) {
        }
    }

    void fileReadLine(Stack<String> param) {
        try {
            int ident = kernel.getCalculator().getInteger(param.get(0));
            int index = kernel.flows.search(ident);
            if (index == -1) throw new LogoException(app, Logo.messages.getString("flux_non_disponible") + " " + ident);
            Flow flow = kernel.flows.get(index);
            FlowReader flowReader;
            // If the flow is a writable flow, throw error
            if (flow.isWriter()) throw new LogoException(app, Logo.messages.getString("flux_lecture"));
                // else if the flow is a readable flow, convert to FlowReader
            else if (flow.isReader()) {
                flowReader = ((FlowReader) flow);
            }
            // else the flow isn't yet defined, initialize
            else flowReader = new FlowReader(flow);

            if (flowReader.isFinished())
                throw new LogoException(app, Logo.messages.getString("fin_flux") + " " + ident);
            // Reading line
            String line = flowReader.readLine();
            if (null == line) {
                flow.setFinished(true);
                throw new LogoException(app, Logo.messages.getString("fin_flux") + " " + ident);
            }
            Interpreter.returnValue = true;
            Interpreter.operands.push("[ " + Utils.formatCode(line.trim()) + " ] ");
            kernel.flows.set(index, flowReader);
        } catch (FileNotFoundException e1) {
            try {
                throw new LogoException(app, Logo.messages.getString("error.iolecture"));
            } catch (LogoException ignored) {
            }
        } catch (IOException | LogoException ignored) {
        }
    }

    void getOpenFiles(Stack<String> param) {
        StringBuilder liste;
        liste = new StringBuilder("[ ");
        for (Flow flow : kernel.flows) {
            liste.append("[ ").append(flow.getId()).append(" ").append(flow.getPath()).append(" ] ");
        }
        liste.append("] ");
        Interpreter.returnValue = true;
        Interpreter.operands.push(liste.toString());
    }

    void getTextFont(Stack<String> param) {
        Interpreter.returnValue = true;
        Interpreter.operands.push("[ " + HistoryPanel.printFontId + " [ " + Application.fonts[HistoryPanel.printFontId].getFontName() + " ] ] ");
    }

    void setTextFont(Stack<String> param) {
        try {
            int int_police = kernel.getCalculator().getInteger(param.get(0));
            HistoryPanel.printFontId = int_police % Application.fonts.length;
            app.getHistoryPanel().setFontNumber(int_police);
        } catch (LogoException ignored) {
        }
    }

    void getLabelFont(Stack<String> param) {
        Interpreter.returnValue = true;
        Interpreter.operands.push("[ " + app.getDrawPanel().drawingFont + " [ " + Application.fonts[app.getDrawPanel().drawingFont].getFontName() + " ] ] ");
    }

    void setLabelFont(Stack<String> param) {
        try {
            int int_police = kernel.getCalculator().getInteger(param.get(0));
            app.getDrawPanel().drawingFont = int_police % Application.fonts.length;
        } catch (LogoException ignored) {
        }
    }

    void isCountdownEnded(Stack<String> param) {
        Interpreter.returnValue = true;
        if (Calendar.getInstance().getTimeInMillis() > Kernel.chrono)
            Interpreter.operands.push(Logo.messages.getString("vrai"));
        else Interpreter.operands.push(Logo.messages.getString("faux"));
    }

    void countdown(Stack<String> param) {
        try {
            int temps = kernel.getCalculator().getInteger(param.get(0));
            Kernel.chrono = Calendar.getInstance().getTimeInMillis() + 1000L * temps;
        } catch (LogoException ignored) {
        }
    }

    void getTimePassed(Stack<String> param) {
        Interpreter.returnValue = true;
        long heure_actuelle = Calendar.getInstance().getTimeInMillis();
        Interpreter.operands.push(String.valueOf((heure_actuelle - Logo.getStartupHour()) / 1000));
    }

    void getTime(Stack<String> param) {
        Calendar cal;
        Interpreter.returnValue = true;
        cal = Calendar.getInstance(Objects.requireNonNull(Logo.getLocale(Logo.config.getLanguage())));
        int heure = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        int seconde = cal.get(Calendar.SECOND);
        Interpreter.operands.push("[ " + heure + " " + minute + " " + seconde + " ] ");
    }

    void getDate(Stack<String> param) {
        Interpreter.returnValue = true;
        Calendar cal = Calendar.getInstance(Objects.requireNonNull(Logo.getLocale(Logo.config.getLanguage())));
        int jour = cal.get(Calendar.DAY_OF_MONTH);
        int mois = cal.get(Calendar.MONTH) + 1;
        int annee = cal.get(Calendar.YEAR);
        Interpreter.operands.push("[ " + jour + " " + mois + " " + annee + " ] ");
    }

    void message(Stack<String> param) {
        try {
            StringBuilder list = new StringBuilder(getFinalList(param.get(0)));
            StringTokenizer st = new StringTokenizer(list.toString());
            // The message is cut into slices of acceptable lengths
            FontMetrics fm = app.getGraphics().getFontMetrics(Logo.config.getFont());
            list = new StringBuilder();
            String tampon = "";
            while (st.hasMoreTokens()) {
                tampon += st.nextToken() + " ";
                if (fm.stringWidth(tampon) > 200) {
                    list.append(tampon).append("\n");
                    tampon = "";
                }
            }
            list.append(tampon);
            list = new StringBuilder(Utils.unescapeString(list.toString()));

            MessageTextArea jt = new MessageTextArea(list.toString());
            JOptionPane.showMessageDialog(app, jt, "", JOptionPane.INFORMATION_MESSAGE, ResourceLoader.getAppIcon());

        } catch (LogoException ignored) {
        }
    }

    void mousePosition(Stack<String> param) {
        Interpreter.operands.push(app.getDrawPanel().get_possouris());
        Interpreter.returnValue = true;
    }

    void mouseButton(Stack<String> param) {
        while (!app.getDrawPanel().get_lissouris()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {
            }
            if (LogoException.lance) break;
        }
        Interpreter.operands.push(String.valueOf(app.getDrawPanel().get_bouton_souris()));
        Interpreter.returnValue = true;
    }

    void getTextColor(Stack<String> param) {
        Interpreter.returnValue = true;
        Color c = app.getHistoryPanel().getTextColor();
        Interpreter.operands.push("[ " + c.getRed() + " " + c.getGreen() + " " + c.getBlue() + " ] ");
    }

    void setTextColor(Stack<String> param) {
        try {
            if (isList(param.get(0))) {
                app.getHistoryPanel().setTextColor(rgb(param.get(0), Utils.primitiveName("ui.settextcolor")));
            } else {
                int coul = kernel.getCalculator().getInteger(param.get(0)) % DrawPanel.defaultColors.length;
                if (coul < 0) coul += DrawPanel.defaultColors.length;
                app.getHistoryPanel().setTextColor(DrawPanel.defaultColors[coul]);
            }
        } catch (LogoException ignored) {
        }
    }

    void getTextSize(Stack<String> param) {
        Interpreter.returnValue = true;
        Interpreter.operands.push(String.valueOf(app.getHistoryPanel().getFontSize()));
    }

    void setTextSize(Stack<String> param) {
        try {
            int i = kernel.getCalculator().getInteger(param.get(0));
            app.getHistoryPanel().setFontSize(i);
        } catch (LogoException ignored) {
        }
    }

    void setSequenceIndex(Stack<String> param) {
        try {
            int i = kernel.getCalculator().getInteger(param.get(0));
            app.getSoundPlayer().setTicks(i * 64);
        } catch (LogoException ignored) {
        }
    }

    void getSequenceIndex(Stack<String> param) {
        Interpreter.returnValue = true;
        double d = (double) app.getSoundPlayer().getTicks() / 64;
        Interpreter.operands.push(Calculator.teste_fin_double(d));
    }

    void setInstrument(Stack<String> param) {
        try {
            int i = kernel.getCalculator().getInteger(param.get(0));
            app.getSoundPlayer().setInstrument(i);
        } catch (LogoException ignored) {
        }
    }

    void deleteSequence(Stack<String> param) {
        app.getSoundPlayer().efface_sequence();
    }

    void play(Stack<String> param) {
        app.getSoundPlayer().joue();
    }

    void getInstrument(Stack<String> param) {
        Interpreter.returnValue = true;
        Interpreter.operands.push(String.valueOf(app.getSoundPlayer().getInstrument()));
    }

    void sequence(Stack<String> param) {
        String liste;
        try {
            liste = getFinalList(param.get(0));
            app.getSoundPlayer().cree_sequence(Utils.formatCode(liste).toString());
        } catch (LogoException ignored) {
        }
    }

    void eraseTurtle(Stack<String> param) {
        int id;
        try {
            id = Integer.parseInt(param.get(0));
            if (id > -1 && id < Logo.config.getMaxTurtles()) {
                // On compte le nombre de tortues à l'écran
                int compteur = 0;
                int premier_dispo = -1;
                for (int i = 0; i < Logo.config.getMaxTurtles(); i++) {
                    if (null != app.getDrawPanel().turtles[i]) {
                        if (i != id && premier_dispo == -1) premier_dispo = i;
                        compteur++;
                    }
                }
                // On vérifie que ce n'est pas la seule tortue
                // dispopnible:
                if (null != app.getDrawPanel().turtles[id]) {
                    if (compteur > 1) {
                        int tortue_utilisee = kernel.getActiveTurtle().id;
                        app.getDrawPanel().turtle = app.getDrawPanel().turtles[id];
                        app.getDrawPanel().toggleTurtle();
                        app.getDrawPanel().turtle = app.getDrawPanel().turtles[tortue_utilisee];
                        app.getDrawPanel().turtles[id] = null;
                        if (app.getDrawPanel().visibleTurtles.search(String.valueOf(id)) > 0)
                            app.getDrawPanel().visibleTurtles.remove(String.valueOf(id));
                        if (kernel.getActiveTurtle().id == id) {
                            app.getDrawPanel().turtle = app.getDrawPanel().turtles[premier_dispo];
                            app.getDrawPanel().setStroke(kernel.getActiveTurtle().crayon); // on
                            // adapte
                            // le
                            // nouveau
                            // crayon
                            String police = app.getDrawPanel().getGraphicsFont().getName();
                            app.getDrawPanel().setFont(new Font(police, Font.PLAIN, kernel.getActiveTurtle().police));

                        }
                    } else {
                        try {
                            throw new LogoException(app, Logo.messages.getString("seule_tortue_dispo"));
                        } catch (LogoException ignored) {
                        }
                    }
                }
            }
        } catch (NumberFormatException e) {
            try {
                kernel.getCalculator().getInteger(param.get(0));
            } catch (LogoException ignored) {
            }
        }
    }

    void setFontSize(Stack<String> param) {
        try {
            kernel.getActiveTurtle().police = kernel.getCalculator().getInteger(param.get(0));
            Font police = Logo.config.getFont();
            app.getDrawPanel().setGraphicsFont(police.deriveFont((float) kernel.getActiveTurtle().police));
        } catch (LogoException ignored) {
        }
    }

    void getFontSize(Stack<String> param) {
        Interpreter.returnValue = true;
        Interpreter.operands.push(String.valueOf(kernel.getActiveTurtle().police));
    }

    void setTurtle(Stack<String> param) {
        try {
            int i = Integer.parseInt(param.get(0));
            if (i > -1 && i < Logo.config.getMaxTurtles()) {
                if (null == app.getDrawPanel().turtles[i]) {
                    app.getDrawPanel().turtles[i] = new Turtle(app);
                    app.getDrawPanel().turtles[i].id = i;
                    app.getDrawPanel().turtles[i].setVisible(false);
                }
                app.getDrawPanel().turtle = app.getDrawPanel().turtles[i];
                app.getDrawPanel().setStroke(kernel.getActiveTurtle().crayon);
                String police = app.getDrawPanel().getGraphicsFont().getName();
                app.getDrawPanel().setGraphicsFont(new Font(police, Font.PLAIN, kernel.getActiveTurtle().police));

            } else {
                try {
                    throw new LogoException(app, Logo.messages.getString("tortue_inaccessible"));
                } catch (LogoException ignored) {
                }
            }
        } catch (NumberFormatException e) {
            try {
                kernel.getCalculator().getInteger(param.get(0));
            } catch (LogoException ignored) {
            }
        }
    }

    void getTurtles(Stack<String> param) {
        Interpreter.returnValue = true;
        StringBuilder li = new StringBuilder("[ ");
        for (int i = 0; i < app.getDrawPanel().turtles.length; i++) {
            if (null != app.getDrawPanel().turtles[i]) li.append(i).append(" ");
        }
        li.append("]");
        Interpreter.operands.push(li.toString());
    }

    void getActiveTurtle(Stack<String> param) {
        Interpreter.returnValue = true;
        Interpreter.operands.push(String.valueOf(kernel.getActiveTurtle().id));
    }

    void define(Stack<String> param) {
        String mot;
        try {
            mot = getWord(param.get(0));
            if (null == mot) throw new LogoException(app, param.get(0) + " " + Logo.messages.getString("error.word"));
            if (mot.equals("")) throw new LogoException(app, Logo.messages.getString("procedure_vide"));
            String list = getFinalList(param.get(1));
            StringBuilder sb = new StringBuilder();
            for (int i = 1; i <= numberOfElements(list); i++) {
                String liste1 = item(list, i);
                liste1 = getFinalList(liste1);

                // First line
                if (i == 1) {
                    StringTokenizer st = new StringTokenizer(liste1);
                    sb.append(Logo.messages.getString("pour"));
                    sb.append(" ");
                    sb.append(mot);
                    sb.append(" ");

                    while (st.hasMoreTokens()) {
                        // Optional variables
                        String token = st.nextToken();
                        if (token.equals("[")) {
                            sb.append("[ :");
                            while (st.hasMoreTokens()) {
                                token = st.nextToken();
                                if (token.equals("]")) {
                                    sb.append("] ");
                                    break;
                                } else {
                                    sb.append(token);
                                    sb.append(" ");
                                }
                            }
                        } else {
                            sb.append(":");
                            sb.append(token);
                            sb.append(" ");
                        }
                    }
                }
                // Body of the procedure
                else if (i > 1) {
                    sb.append("\n");
                    sb.append(liste1);
                }
            }
            sb.append("\n");
            sb.append(Logo.messages.getString("fin"));
            app.editor.appendText(new String(sb));
        } catch (LogoException ignored) {
        }
        try {
            app.editor.parseProcedures();
            app.editor.clearText();
        } catch (Exception ignored) {
        }
    }

    void setShape(Stack<String> param) {
        try {
            primitive2D("drawing.setshape");
            int i = kernel.getCalculator().getInteger(param.get(0)) % 7;
            if (kernel.getActiveTurtle().id == 0) {
                Logo.config.setActiveTurtle(i);
            }
            kernel.change_image_tortue(i);
        } catch (LogoException ignored) {
        }
    }

    void getShape(Stack<String> param) {
        try {
            primitive2D("drawing.shape");
            Interpreter.returnValue = true;
            Interpreter.operands.push(String.valueOf(kernel.getActiveTurtle().getShape()));
        } catch (LogoException ignored) {
        }
    }

    void falseValue(Stack<String> param) {
        Interpreter.returnValue = true;
        Interpreter.operands.push(Logo.messages.getString("faux"));
    }

    void trueValue(Stack<String> param) {
        Interpreter.returnValue = true;
        Interpreter.operands.push(Logo.messages.getString("vrai"));
    }

    void atan(Stack<String> param) {
        try {
            Interpreter.operands.push(kernel.getCalculator().atan(param.get(0)));
            Interpreter.returnValue = true;
        } catch (LogoException ignored) {
        }
    }

    void asin(Stack<String> param) {
        try {
            Interpreter.operands.push(kernel.getCalculator().asin(param.get(0)));
            Interpreter.returnValue = true;
        } catch (LogoException ignored) {
        }
    }

    void acos(Stack<String> param) {
        try {
            Interpreter.operands.push(kernel.getCalculator().acos(param.get(0)));
            Interpreter.returnValue = true;
        } catch (LogoException ignored) {
        }
    }

    void tan(Stack<String> param) {
        Interpreter.returnValue = true;
        try {
            Interpreter.operands.push(kernel.getCalculator().tan(param.get(0)));
        } catch (LogoException ignored) {
        }
    }

    void pi(Stack<String> param) {
        Interpreter.returnValue = true;
        Interpreter.operands.push(kernel.getCalculator().pi());
    }

    void load(Stack<String> param) {
        String mot;
        try {
            mot = getWord(param.get(0));
            if (null == mot) throw new LogoException(app, param.get(0) + " " + Logo.messages.getString("error.word"));
            String path = Utils.unescapeString(Logo.config.getDefaultFolder()) + File.separator + mot;
            try {
                String txt = Utils.readLogoFile(path);
                app.editor.appendText(txt);
            } catch (IOException e1) {
                throw new LogoException(app, Logo.messages.getString("error.iolecture"));
            }
            try {
                app.editor.parseProcedures();
            } catch (Exception e) {
                e.printStackTrace();
            }
            app.editor.clearText();
        } catch (LogoException ignored) {
        }
    }

    void saveAll(Stack<String> param) {
        String mot;
        try {
            mot = getWord(param.get(0));
            if (null == mot) throw new LogoException(app, param.get(0) + " " + Logo.messages.getString("error.word"));
            saveProcedures(mot, null);
        } catch (LogoException ignored) {
        }
    }

    void save(Stack<String> param) {
        String mot;
        String liste;
        try {
            mot = getWord(param.get(0));
            if (null == mot) throw new LogoException(app, Logo.messages.getString("error.word"));
            liste = getFinalList(param.get(1));
            StringTokenizer st = new StringTokenizer(liste);
            Stack<String> pile = new Stack<>();
            while (st.hasMoreTokens()) pile.push(st.nextToken());
            saveProcedures(mot, pile);
        } catch (LogoException ignored) {
        }
    }

    void getDirectory(Stack<String> param) {
        Interpreter.returnValue = true;
        Interpreter.operands.push("\"" + Logo.config.getDefaultFolder());
    }

    void setDirectory(Stack<String> param) {
        String liste;
        try {
            liste = getWord(param.get(0));
            if (null == liste) throw new LogoException(app, param.get(0) + " " + Logo.messages.getString("error.word"));
            String chemin = Utils.unescapeString(liste);
            if ((new File(chemin)).isDirectory() && !chemin.startsWith("..")) {
                Logo.config.setDefaultFolder(Utils.escapeString(chemin));
            } else throw new LogoException(app, liste + " " + Logo.messages.getString("erreur_pas_repertoire"));
        } catch (LogoException ignored) {
        }
    }

    void getFiles(Stack<String> param) {
        String str = Utils.unescapeString(Logo.config.getDefaultFolder());
        File f = new File(str);
        StringBuilder file = new StringBuilder();
        StringBuilder directory = new StringBuilder();
        int nbdossier = 0;
        int nbfichier = 0;
        String[] l = f.list();
        for (String s : Objects.requireNonNull(l)) {
            if ((new File(str + File.separator + s)).isDirectory()) {
                nbdossier++;
                if (nbdossier % 5 == 0) directory.append(s).append("\n");
                else directory.append(s).append(" ");
            } else {
                nbfichier++;
                if (nbfichier % 5 == 0) file.append(s).append("\n");
                else file.append(s).append(" ");
            }
        }
        String texte = "";
        if (!directory.toString().equals(""))
            texte += Logo.messages.getString("repertoires") + ":\n" + directory + "\n";
        if (!file.toString().equals("")) texte += Logo.messages.getString("fichiers") + ":\n" + file + "\n";
        app.updateHistory("commentaire", texte);
    }

    void run(Stack<String> param) {
        String mot;
        try {
            mot = getWord(param.get(0));
            if (null == mot) {
                mot = getList(param.get(0).trim());
                mot = new String(Utils.formatCode(mot));
            } else mot = mot + " ";
            app.getKernel().getInstructionBuffer().insert(mot);
            Interpreter.evalReturnValue = true;
        } catch (LogoException ignored) {
        }
    }

    void isProcedure(Stack<String> param) {
        String mot;
        Interpreter.returnValue = true;
        boolean test = false;
        mot = getWord(param.get(0));
        for (int i = 0; i < workspace.getNumberOfProcedure(); i++) {
            if (workspace.getProcedure(i).name.equals(mot)) test = true;
        }
        if (test) Interpreter.operands.push(Logo.messages.getString("vrai"));
        else Interpreter.operands.push(Logo.messages.getString("faux"));
    }

    void isPrimitive(Stack<String> param) {
        String mot;
        try {
            Interpreter.returnValue = true;
            mot = getWord(param.get(0));
            if (null == mot) throw new LogoException(app, param.get(0) + " " + Logo.messages.getString("error.word"));
            if (primitiveMap.containsKey(mot)) Interpreter.operands.push(Logo.messages.getString("vrai"));
            else Interpreter.operands.push(Logo.messages.getString("faux"));
        } catch (LogoException ignored) {
        }
    }

    void isVisible(Stack<String> param) {
        Interpreter.returnValue = true;
        if (kernel.getActiveTurtle().isVisible()) Interpreter.operands.push(Logo.messages.getString("vrai"));
        else Interpreter.operands.push(Logo.messages.getString("faux"));
    }

    void isPenDown(Stack<String> param) {
        Interpreter.returnValue = true;
        if (kernel.getActiveTurtle().isPenDown()) Interpreter.operands.push(Logo.messages.getString("vrai"));
        else Interpreter.operands.push(Logo.messages.getString("faux"));
    }

    void screenColor(Stack<String> param) {
        Interpreter.returnValue = true;
        Color color = app.getDrawPanel().getScreenColor();
        Interpreter.operands.push("[ " + color.getRed() + " " + color.getGreen() + " " + color.getBlue() + " ] ");
    }

    void penColor(Stack<String> param) {
        Interpreter.returnValue = true;
        Interpreter.operands.push("[ " + kernel.getActiveTurtle().penColor.getRed() + " " + kernel.getActiveTurtle().penColor.getGreen() + " " + kernel.getActiveTurtle().penColor.getBlue() + " ] ");
    }

    void distance(Stack<String> param) {
        try {
            Interpreter.returnValue = true;
            double distance = app.getDrawPanel().distance(getFinalList(param.get(0)));
            Interpreter.operands.push(Calculator.teste_fin_double(distance));
        } catch (LogoException ignored) {
        }
    }

    void towards(Stack<String> param) {
        try {
            Interpreter.returnValue = true;
            if (DrawPanel.windowMode != DrawPanel.WINDOW_3D) {
                double angle = app.getDrawPanel().to2D(getFinalList(param.get(0)));
                Interpreter.operands.push(Calculator.teste_fin_double(angle));
            } else {
                double[] orientation = app.getDrawPanel().vers3D(getFinalList(param.get(0)));
                Interpreter.operands.push("[ " + orientation[0] + " " + orientation[1] + " " + orientation[2] + " ] ");
            }
        } catch (LogoException ignored) {
        }
    }

    void dot(Stack<String> param) {
        if (kernel.getActiveTurtle().isVisible()) app.getDrawPanel().eraseTurtle(false);
        try {
            app.getDrawPanel().point(getFinalList(param.get(0)));
        } catch (LogoException ignored) {
        }
        if (kernel.getActiveTurtle().isVisible()) app.getDrawPanel().eraseTurtle(true);
    }

    void readChar(Stack<String> param) {
        while (app.getKey() == -1) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {
            }
            if (LogoException.lance) break;
        }
        Interpreter.operands.push(String.valueOf(app.getKey()));
        Interpreter.returnValue = true;
        app.setKey(-1);
    }

    void fill(Stack<String> param) {
        app.getDrawPanel().fill();
    }

    void testWhile(Stack<String> param) {
        try {
            String liste = getFinalList(param.get(1));
            boolean predicat = getBoolean(param.get(0));
            if (predicat) {
                app.getKernel().getInstructionBuffer().insert(liste + loops.peek().getInstr());
            } else {
                try {
                    eraseLevelStop();
                } catch (LogoException ignored) {
                }
            }
        } catch (LogoException ignored) {
        }
    }

    void isKey(Stack<String> param) {
        Interpreter.returnValue = true;
        if (app.getKey() != -1) Interpreter.operands.push(Logo.messages.getString("vrai"));
        else Interpreter.operands.push(Logo.messages.getString("faux"));
    }

    void read(Stack<String> param) {
        String mot;
        try {
            String liste = getFinalList(param.get(0));
            mot = getWord(param.get(1));
            if (null == mot) throw new LogoException(app, Logo.messages.getString("error.word"));
            FontMetrics fm = app.getGraphics().getFontMetrics(Logo.config.getFont());
            int longueur = fm.stringWidth(liste) + 100;
            InputFrame inputFrame = new InputFrame(liste, longueur);
            while (inputFrame.isVisible()) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException ignored) {
                }
            }
            param = new Stack<>();
            param.push("\"" + mot);
            String phrase = inputFrame.getText();
            // phrase="[ "+Logo.rajoute_backslash(phrase)+" ] ";
            StringBuilder buffer = new StringBuilder();
            for (int j = 0; j < phrase.length(); j++) {
                char c = phrase.charAt(j);
                if (c == '\\') buffer.append("\\\\");
                else buffer.append(c);
            }
            int offset = buffer.indexOf(" ");
            if (offset != -1) {
                buffer.insert(0, "[ ");
                buffer.append(" ] ");
            } else {
                try {
                    Double.parseDouble(phrase);
                } catch (NumberFormatException e) {
                    buffer.insert(0, "\"");
                }
            }
            phrase = new String(buffer);
            param.push(phrase);
            globalMake(param);
            String texte = liste + "\n" + phrase;
            app.updateHistory("commentaire", Utils.unescapeString(texte) + "\n");
            app.focusCommandLine();
            inputFrame.dispose();
            app.focusCommandLine();
        } catch (LogoException ignored) {
        }
    }

    void whileLoop(Stack<String> param) {
        try {
            String li1 = getList(param.get(0));
            li1 = new String(Utils.formatCode(li1));
            String li2 = getList(param.get(1));
            li2 = new String(Utils.formatCode(li2));
            String instr = "\\siwhile " + li1 + "[ " + li2 + "] ";
            LoopWhile bp = new LoopWhile(BigDecimal.ONE, BigDecimal.ZERO, BigDecimal.ONE, instr);
            loops.push(bp);
            app.getKernel().getInstructionBuffer().insert(instr + Interpreter.END_LOOP + " ");
        } catch (LogoException ignored) {
        }
    }

    void setPenWidth(Stack<String> param) {
        try {
            double nombre = kernel.getCalculator().numberDouble(param.get(0));
            if (nombre < 0) nombre = Math.abs(nombre);
            if (DrawPanel.record3D == DrawPanel.RECORD_3D_LINE || DrawPanel.record3D == DrawPanel.RECORD_3D_POINT) {
                if (kernel.getActiveTurtle().getPenWidth() != (float) nombre) DrawPanel.poly.addToScene();
            }
            kernel.getActiveTurtle().fixPenWidth((float) nombre);
            app.getDrawPanel().setStroke(kernel.getActiveTurtle().crayon);
            if (DrawPanel.record3D == DrawPanel.RECORD_3D_LINE) {
                DrawPanel.poly = new ElementLine(app);
                DrawPanel.poly.addVertex(new Point3d(kernel.getActiveTurtle().X / 1000, kernel.getActiveTurtle().Y / 1000, kernel.getActiveTurtle().Z / 1000), kernel.getActiveTurtle().penColor);
            } else if (DrawPanel.record3D == DrawPanel.RECORD_3D_POINT) {
                DrawPanel.poly = new ElementPoint(app);
            }
        } catch (LogoException ignored) {
        }
    }

    void loadImage(Stack<String> param) {
        BufferedImage image = null;
        try {
            primitive2D("drawing.loadimage");
            image = getImage(param.get(0));
        } catch (LogoException ignored) {
        }
        if (null != image) app.getDrawPanel().drawImage(image);
    }

    void findColor(Stack<String> param) {
        if (kernel.getActiveTurtle().isVisible()) app.getDrawPanel().eraseTurtle(false);
        try {
            String liste = getFinalList(param.get(0));
            Color r = app.getDrawPanel().guessColorPoint(liste);
            Interpreter.returnValue = true;
            Interpreter.operands.push("[ " + r.getRed() + " " + r.getGreen() + " " + r.getBlue() + " ] ");
        } catch (LogoException ignored) {
        }
        if (kernel.getActiveTurtle().isVisible()) app.getDrawPanel().eraseTurtle(true);
    }

    void fenceTurtle(Stack<String> param) {
        app.getDrawPanel().setWindowMode(DrawPanel.WINDOW_CLOSE);
    }

    void wrapTurtle(Stack<String> param) {
        app.getDrawPanel().setWindowMode(DrawPanel.WINDOW_WRAP);
    }

    void windowTurtle(Stack<String> param) {
        app.getDrawPanel().setWindowMode(DrawPanel.WINDOW_CLASSIC);
    }

    void label(Stack<String> param) {
        String mot;
        String par = param.get(0).trim();
        if (isList(par)) par = formatList(par.substring(1, par.length() - 1));
        mot = getWord(param.get(0));
        if (null == mot) app.getDrawPanel().label(Utils.unescapeString(par));
        else app.getDrawPanel().label(Utils.unescapeString(mot));
    }

    void word(Stack<String> param) {
        String mot;
        Interpreter.returnValue = true;
        StringBuilder result = new StringBuilder();
        for (String s : param) {
            mot = getWord(s);
            if (null == mot) try {
                throw new LogoException(app, s + " " + Logo.messages.getString("error.word"));
            } catch (LogoException ignored) {
            }
            result.append(mot);
        }
        try {
            Double.parseDouble(result.toString());
        } catch (NumberFormatException e) {
            result.insert(0, "\"");
        }
        Interpreter.operands.push(result.toString());
    }

    void eraseAll(Stack<String> param) {
        workspace.deleteAllProcedures();
        workspace.deleteAllVariables();
        workspace.deleteAllPropertyLists();
    }

    void procedures(Stack<String> param) {
        Interpreter.returnValue = true;
        Interpreter.operands.push(new String(getAllProcedures()));
    }

    void eraseVariable(Stack<String> param) {
        erase(param.get(0), "variable");
    }

    void eraseProcedure(Stack<String> param) {
        erase(param.get(0), "procedure");
    }

    void wait(Stack<String> param) {
        try {
            int temps = kernel.getCalculator().getInteger(param.get(0));
            if (temps < 0) {
                String attends = Utils.primitiveName("time.wait");
                throw new LogoException(app, attends + " " + Logo.messages.getString("attend_positif"));
            } else {
                int nbsecondes = temps / 60;
                int reste = temps % 60;
                for (int i = 0; i < nbsecondes; i++) {
                    Thread.sleep(1000);
                    if (app.error) break;
                }
                if (!app.error) Thread.sleep(reste * 50 / 3);
            }

        } catch (LogoException | InterruptedException ignored) {
        }
    }

    void random(Stack<String> param) {
        Interpreter.returnValue = true;
        try {
            int i = kernel.getCalculator().getInteger(param.get(0));
            i = (int) Math.floor(Math.random() * i);
            Interpreter.operands.push(String.valueOf(i));
        } catch (LogoException ignored) {
        }
    }

    void setScreenColor(Stack<String> param) {
        try {
            Color color = null;
            if (isList(param.get(0))) {
                try {
                    color = rgb(param.get(0), Utils.primitiveName("drawing.setscreencolor"));
                } catch (LogoException ignored) {
                }
            } else {
                int coul = kernel.getCalculator().getInteger(param.get(0)) % DrawPanel.defaultColors.length;
                if (coul < 0) coul += DrawPanel.defaultColors.length;
                color = DrawPanel.defaultColors[coul];
            }
            app.getDrawPanel().setScreenColor(color);
        } catch (LogoException ignored) {
        }
    }

    void setPenColor(Stack<String> param) {
        try {
            Color color = null;
            if (isList(param.get(0))) {
                try {
                    color = rgb(param.get(0), Utils.primitiveName("drawing.setpencolor"));
                } catch (LogoException ignored) {
                }
            } else {
                int coul = kernel.getCalculator().getInteger(param.get(0)) % DrawPanel.defaultColors.length;
                if (coul < 0) coul += DrawPanel.defaultColors.length;
                color = DrawPanel.defaultColors[coul];
            }
            app.getDrawPanel().setPenColor(color);
        } catch (LogoException ignored) {
        }
    }

    void localMake(Stack<String> param) {
        try {
            local(param);
            globalMake(param);
            Interpreter.returnValue = false;
        } catch (LogoException ignored) {
        }
    }

    void localWrap(Stack<String> param) {
        try {
            local(param);
            Interpreter.returnValue = false;
        } catch (LogoException ignored) {
        }
    }

    void globalMakeWrap(Stack<String> param) {
        try {
            globalMake(param);
            Interpreter.returnValue = false;
        } catch (LogoException ignored) {
        }
    }

    void sqrt(Stack<String> param) {
        Interpreter.returnValue = true;
        try {
            Interpreter.operands.push(kernel.getCalculator().sqrt(param.get(0)));

        } catch (LogoException ignored) {
        }
    }

    void isMember69(Stack<String> param) {
        try {
            isMember(param, 69);
        } catch (LogoException ignored) {
        }
    }

    void isMember71(Stack<String> param) {
        try {
            isMember(param, 71);
        } catch (LogoException ignored) {
        }
    }

    void isBeforeWrap(Stack<String> param) {
        try {
            isBefore(param);
        } catch (LogoException ignored) {
        }
    }

    void isEmpty(Stack<String> param) {
        String mot;
        String liste = param.get(0).trim();
        mot = getWord(param.get(0));
        if (null == mot) { // si c'est une liste ou un nombre
            try {
                liste = getFinalList(liste).trim();
                if (liste.equals("")) Interpreter.operands.push(Logo.messages.getString("control.true"));
                else Interpreter.operands.push(Logo.messages.getString("control.false"));
            } catch (LogoException ignored) {
            }
        } else { // Si c'est un mot
            if (mot.equals("")) Interpreter.operands.push(Logo.messages.getString("vrai"));
            else Interpreter.operands.push(Logo.messages.getString("faux"));
        }
        Interpreter.returnValue = true;
    }

    void isList(Stack<String> param) {
        String liste = param.get(0).trim();
        if (isList(liste)) Interpreter.operands.push(Logo.messages.getString("vrai"));
        else Interpreter.operands.push(Logo.messages.getString("faux"));
        Interpreter.returnValue = true;
    }

    void isNumber(Stack<String> param) {
        try {
            Double.parseDouble(param.get(0));
            Interpreter.operands.push(Logo.messages.getString("vrai"));
        } catch (NumberFormatException e) {
            Interpreter.operands.push(Logo.messages.getString("faux"));
        }
        Interpreter.returnValue = true;
    }

    void isWord(Stack<String> param) {
        String mot;
        mot = getWord(param.get(0));
        if (null == mot) Interpreter.operands.push(Logo.messages.getString("faux"));
        else Interpreter.operands.push(Logo.messages.getString("vrai"));
        Interpreter.returnValue = true;
    }

    void count(Stack<String> param) {
        String mot;
        Interpreter.returnValue = true;
        mot = getWord(param.get(0));
        if (null == mot) {
            try {
                String liste = getFinalList(param.get(0));
                Interpreter.operands.push(String.valueOf(numberOfElements(liste)));
            } catch (LogoException ignored) {
            }
        } else Interpreter.operands.push(String.valueOf(getWordLength(mot)));
    }

    void first(Stack<String> param) {
        Interpreter.returnValue = true;
        String mot = getWord(param.get(0));
        if (null == mot) { // SI c'est une liste
            try {
                String liste = getFinalList(param.get(0));
                // System.out.println("b"+item(liste, 1)+"b");
                Interpreter.operands.push(item(liste, 1));
            } catch (LogoException ignored) {
            }
        } else if (getWordLength(mot) == 1) Interpreter.operands.push(wordPrefix + mot);
        else {
            String st = "";
            try {
                st = itemWord(1, mot);
                Double.parseDouble(st);
                Interpreter.operands.push(st);
            } catch (LogoException ignored) {
            } catch (NumberFormatException e2) {
                Interpreter.operands.push("\"" + st);
            }
        }
    }

    void last(Stack<String> param) {
        Interpreter.returnValue = true;
        String mot = getWord(param.get(0));
        if (null == mot) { // Si c'est une liste
            try {
                String liste = getFinalList(param.get(0));
                Interpreter.operands.push(item(liste, numberOfElements(liste)));
            } catch (LogoException ignored) {
            }
        } else if (getWordLength(mot) == 1) Interpreter.operands.push(wordPrefix + mot);
        else {
            String st = "";
            try {
                st = itemWord(getWordLength(mot), mot);
                Double.parseDouble(st);
                Interpreter.operands.push(st);
            } catch (NumberFormatException e1) {
                Interpreter.operands.push("\"" + st);
            } catch (LogoException ignored) {
            }
        }
    }

    void butFirst(Stack<String> param) {
        String mot;
        Interpreter.returnValue = true;
        mot = getWord(param.get(0));
        if (null == mot) {
            try {
                String liste = getFinalList(param.get(0)).trim();
                String element = item(liste, 1);
                int longueur = element.length();
                if (element.startsWith("\"") || element.startsWith("[")) longueur--;
                Interpreter.operands.push("[" + liste.substring(longueur) + " ] ");
            } catch (LogoException ignored) {
            }
        } else if (mot.equals("")) {
            try {
                throw new LogoException(app, Logo.messages.getString("mot_vide"));
            } catch (LogoException ignored) {
            }
        } else if (getWordLength(mot) == 1) Interpreter.operands.push("\"");
        else {
            if (!mot.startsWith("\\")) mot = mot.substring(1);
            else mot = mot.substring(2);
            try {
                Double.parseDouble(mot);
                Interpreter.operands.push(mot);
            } catch (NumberFormatException e) {
                Interpreter.operands.push(wordPrefix + mot);
            }
        }
    }

    void butLast(Stack<String> param) {
        String mot;
        Interpreter.returnValue = true;
        mot = getWord(param.get(0));
        if (null == mot) {
            try {
                String liste = getFinalList(param.get(0)).trim();
                String element = item(liste, numberOfElements(liste));
                int longueur = element.length();

                if (element.startsWith("\"") || element.startsWith("[")) longueur--;
                Interpreter.operands.push("[ " + liste.substring(0, liste.length() - longueur) + "] ");
            } catch (LogoException ignored) {
            }
        } else if (mot.equals("")) {
            try {
                throw new LogoException(app, Logo.messages.getString("mot_vide"));
            } catch (LogoException ignored) {
            }
        } else if (getWordLength(mot) == 1) Interpreter.operands.push("\"");
        else {
            String tmp = mot.substring(0, mot.length() - 1);
            if (tmp.endsWith("\\")) tmp = tmp.substring(0, tmp.length() - 1);
            try {
                Double.parseDouble(tmp);
                Interpreter.operands.push(tmp);
            } catch (NumberFormatException e) {
                Interpreter.operands.push(wordPrefix + tmp);
            }
        }
    }

    void item(Stack<String> param) {
        String mot;
        Interpreter.returnValue = true;
        try {
            mot = getWord(param.get(1));
            if (null == mot)
                Interpreter.operands.push(item(getFinalList(param.get(1)), kernel.getCalculator().getInteger(param.get(0))));
            else {
                int i = kernel.getCalculator().getInteger(param.get(0));
                if (i < 1 || i > getWordLength(mot))
                    throw new LogoException(app, Utils.primitiveName("list.item") + " " + Logo.messages.getString("n_aime_pas") + i + " " + Logo.messages.getString("comme_parametre") + ".");
                else {
                    String st = itemWord(i, mot);
                    try {
                        Double.parseDouble(st);
                        Interpreter.operands.push(st);
                    } catch (NumberFormatException e1) {
                        Interpreter.operands.push("\"" + st);
                    }
                }
            }
        } catch (LogoException ignored) {
        }
    }

    void remove(Stack<String> param) {
        String mot;
        Interpreter.returnValue = true;
        try {
            StringBuilder list = new StringBuilder(getFinalList(param.get(1)));
            StringTokenizer st = new StringTokenizer(list.toString());
            list = new StringBuilder("[ ");
            mot = getWord(param.get(0));
            String str;
            if (null != mot && mot.equals("")) mot = "\\v";
            if (null == mot) mot = param.get(0).trim();

            while (st.hasMoreTokens()) {
                str = st.nextToken();
                if (str.equals("[")) str = extractList(st);
                if (!str.equals(mot)) list.append(str).append(" ");
            }
            Interpreter.operands.push(list.toString().trim() + " ] ");
        } catch (LogoException ignored) {
        }
    }

    void pick(Stack<String> param) {
        Interpreter.returnValue = true;
        String mot = getWord(param.get(0));
        if (null == mot) {
            try {
                String list = getFinalList(param.get(0));
                int nombre = (int) Math.floor(numberOfElements(list) * Math.random()) + 1;
                String tmp = item(list, nombre);
                if (tmp.equals("\"\\v")) tmp = "\"";
                Interpreter.operands.push(tmp);
            } catch (LogoException ignored) {
            }
        } else {
            int nombre = (int) Math.floor(Math.random() * getWordLength(mot)) + 1;
            String st = "";
            try {
                st = itemWord(nombre, mot);
                Double.parseDouble(st);
                Interpreter.operands.push(st);
            } catch (NumberFormatException e1) {
                Interpreter.operands.push("\"" + st);
            } catch (LogoException ignored) {
            }
        }
    }

    void reverse(Stack<String> param) {
        try {
            StringBuilder liste = new StringBuilder(getFinalList(param.get(0)).trim());
            Interpreter.returnValue = true;
            StringTokenizer st = new StringTokenizer(liste.toString());
            liste = new StringBuilder(" ] ");
            String element;
            while (st.hasMoreTokens()) {
                element = st.nextToken();
                if (element.equals("[")) element = extractList(st);
                liste.insert(0, " " + element);
            }
            Interpreter.operands.push("[" + liste);
        } catch (LogoException ignored) {
        }
    }

    void lput(Stack<String> param) {
        try {
            String liste = getFinalList(param.get(1)).trim();
            Interpreter.returnValue = true;
            String mot = getWord(param.get(0));
            if (null != mot && mot.equals("")) mot = "\\v";
            // Si c'est une liste
            Interpreter.operands.push(("[ " + liste).trim() + " " + Objects.requireNonNullElseGet(mot, () -> param.get(0).trim()) + " ] ");
        } catch (LogoException ignored) {
        }
    }

    void fput(Stack<String> param) {
        try {
            String liste = getFinalList(param.get(1));
            Interpreter.returnValue = true;
            String mot = getWord(param.get(0));
            if (null != mot && mot.equals("")) mot = "\\v";
            if (null == mot) {
                if (!liste.equals(""))
                    Interpreter.operands.push("[ " + param.get(0).trim() + " " + liste.trim() + " ] ");
                else Interpreter.operands.push("[ " + param.get(0).trim() + " ] ");
            } else {
                if (!liste.equals("")) Interpreter.operands.push("[ " + mot + " " + liste.trim() + " ] ");
                else Interpreter.operands.push("[ " + mot + " ] ");
            }
        } catch (LogoException ignored) {
        }
    }

    void sentence(Stack<String> param) {
        StringBuilder liste = new StringBuilder("[ ");
        Interpreter.returnValue = true;
        for (String s : param) {
            String mot = getWord(s);
            String mot2 = s.trim();
            if (null == mot) {
                if (isList(mot2)) liste.append(mot2.substring(1, mot2.length() - 1).trim()).append(" ");
                else liste.append(mot2).append(" ");
            } else {
                if (mot.equals("")) mot = "\\v";
                liste.append(mot).append(" ");
            }
        }
        Interpreter.operands.push(liste + "] ");
    }

    void list(Stack<String> param) {
        StringBuilder liste = new StringBuilder("[ ");
        Interpreter.returnValue = true;
        for (String mot2 : param) {
            String mot = getWord(mot2);
            if (null == mot) {
                liste.append(mot2);
                // System.out.println("a"+mot2+"a");
            } else {
                if (mot.equals("")) mot = "\\v";
                liste.append(mot).append(" ");
            }
        }
        Interpreter.operands.push(liste + "] ");
    }

    void not(Stack<String> param) {
        try {
            Interpreter.returnValue = true;
            boolean b1 = getBoolean(param.get(0));
            if (b1) Interpreter.operands.push(Logo.messages.getString("faux"));
            else Interpreter.operands.push(Logo.messages.getString("vrai"));
        } catch (LogoException ignored) {
        }
    }

    void cos(Stack<String> param) {
        Interpreter.returnValue = true;
        try {
            Interpreter.operands.push(kernel.getCalculator().cos(param.get(0)));
        } catch (LogoException ignored) {
        }
    }

    void sin(Stack<String> param) {
        Interpreter.returnValue = true;
        try {
            Interpreter.operands.push(kernel.getCalculator().sin(param.get(0)));
        } catch (LogoException ignored) {
        }
    }

    void log10(Stack<String> param) {
        Interpreter.returnValue = true;
        try {
            Interpreter.operands.push(kernel.getCalculator().log10(param.get(0)));
        } catch (LogoException ignored) {
        }
    }

    void round(Stack<String> param) {
        Interpreter.returnValue = true;
        try {
            Interpreter.operands.push(String.valueOf(Math.round(kernel.getCalculator().numberDouble(param.get(0)))));
        } catch (LogoException ignored) {
        }
    }

    void heading(Stack<String> param) {
        Interpreter.returnValue = true;
        Interpreter.operands.push(Calculator.teste_fin_double(kernel.getActiveTurtle().heading));
    }

    void position(Stack<String> param) {
        Interpreter.returnValue = true;
        if (DrawPanel.windowMode != DrawPanel.WINDOW_3D) {
            double a = kernel.getActiveTurtle().curX - Logo.config.getImageWidth() / 2.0;
            double b = Logo.config.getImageHeight() / 2.0 - kernel.getActiveTurtle().curY;
            Interpreter.operands.push("[ " + Calculator.teste_fin_double(a) + " " + Calculator.teste_fin_double(b) + " ] ");
        } else {
            Interpreter.operands.push("[ " + kernel.getActiveTurtle().X + " " + kernel.getActiveTurtle().Y + " " + kernel.getActiveTurtle().Z + " ] ");

        }
    }

    void endLoop(Stack<String> param) {
        Loop loop = loops.peek();
        // LOOP REPEAT
        if (loop.isRepeat()) {
            BigDecimal compteur = loop.getCounter();
            BigDecimal fin = loop.getEnd();
            if (compteur.compareTo(fin) < 0) {
                loop.incremente();
                loops.pop();
                loops.push(loop);
                app.getKernel().getInstructionBuffer().insert(loop.getInstr() + Interpreter.END_LOOP + " ");
            } else if (compteur.compareTo(fin) == 0) {
                loops.pop();
            }
        }
        // LOOP FOR or LOOP FOREACH
        else if (loop.isFor() || loop.isForEach()) {
            BigDecimal inc = loop.getIncrement();
            BigDecimal compteur = loop.getCounter();
            BigDecimal fin = loop.getEnd();
            if ((inc.compareTo(BigDecimal.ZERO) > 0 && (compteur.add(inc).compareTo(fin) <= 0)) || (inc.compareTo(BigDecimal.ZERO) < 0 && (compteur.add(inc).compareTo(fin) >= 0))) {
                loop.incremente();
                ((LoopFor) loop).AffecteVar(false);
                loops.pop();
                loops.push(loop);
                app.getKernel().getInstructionBuffer().insert(loop.getInstr() + Interpreter.END_LOOP + " ");
            } else {
                ((LoopFor) loop).DeleteVar();
                loops.pop();
            }
        }
        // LOOP FOREVER
        else if (loop.isForEver()) {
            app.getKernel().getInstructionBuffer().insert(loop.getInstr() + Interpreter.END_LOOP + " ");
        }
        // LOOP FILL POLYGON
        else if (loop.isFillPolygon()) {
            app.getDrawPanel().stopRecord2DPolygon();
            loops.pop();
        }
    }

    void endProc(Stack<String> param) {
        Interpreter.locals = Interpreter.scopes.pop();
        if (Interpreter.consumers.peek().equals("\n")) {
            Interpreter.consumers.pop();
            Interpreter.lineNumber = "";
        } else {
            /* Example
             * to bug
             * av
             * end
             */
            try {
                throw new LogoException(app, Logo.messages.getString("pas_assez_de") + " " + Interpreter.consumers.peek());
            } catch (LogoException ignored) {
            }
        }
        /* to bug [:a]	| (bug 10)
         * av :a		|
         * end			|
         */
        if (!Interpreter.consumers.isEmpty() && !Interpreter.consumers.peek().equals("\n") && !Interpreter.consumers.peek().equals("(")) {
            try {
                if (!app.error)
                    throw new LogoException(app, Interpreter.procedures.peek() + " " + Logo.messages.getString("ne_renvoie_pas") + " " + Interpreter.consumers.peek());
            } catch (LogoException ignored) {
            }
        }
        if (!Interpreter.procedures.isEmpty()) Interpreter.procedures.pop();
    }

    void andOp(Stack<String> param) {
        try {
            boolean b1 = getBoolean(param.get(0));
            boolean b2 = getBoolean(param.get(1));
            b1 = b1 & b2;
            if (b1) Interpreter.operands.push(Logo.messages.getString("vrai"));
            else Interpreter.operands.push(Logo.messages.getString("faux"));
        } catch (LogoException ignored) {
        }
        Interpreter.returnValue = true;
    }

    void orOp(Stack<String> param) {
        try {
            boolean b1 = getBoolean(param.get(0));
            boolean b2 = getBoolean(param.get(1));
            b1 = b1 | b2;
            if (b1) Interpreter.operands.push(Logo.messages.getString("vrai"));
            else Interpreter.operands.push(Logo.messages.getString("faux"));
        } catch (LogoException ignored) {
        }
        Interpreter.returnValue = true;
    }

    void subtractOp(Stack<String> param) {
        Interpreter.returnValue = true;
        try {
            Interpreter.operands.push(kernel.getCalculator().substract(param));
        } catch (LogoException ignored) {
        }
    }

    void addOp(Stack<String> param) {
        Interpreter.returnValue = true;
        Interpreter.operands.push(kernel.getCalculator().add(param));
    }

    void divideOp(Stack<String> param) {
        Interpreter.returnValue = true;
        try {
            Interpreter.operands.push(kernel.getCalculator().divide(param));
        } catch (LogoException ignored) {
        }
    }

    void multiplyOp(Stack<String> param) {
        Interpreter.returnValue = true;
        Interpreter.operands.push(kernel.getCalculator().multiply(param));
    }

    void output(Stack<String> param) {
        try {
            output(param.get(0));
        } catch (LogoException ignored) {
        }
    }

    void remainder(Stack<String> param) {
        Interpreter.returnValue = true;
        try {
            Interpreter.operands.push(kernel.getCalculator().remainder(param.get(0), param.get(1)));
        } catch (LogoException ignored) {
        }
    }

    void divide(Stack<String> param) {
        Interpreter.returnValue = true;
        try {
            Interpreter.operands.push(kernel.getCalculator().divide(param));
        } catch (LogoException ignored) {
        }
    }

    void product(Stack<String> param) {
        Interpreter.operands.push(kernel.getCalculator().multiply(param));
        Interpreter.returnValue = true;
    }

    void minus(Stack<String> param) {
        try {
            Interpreter.operands.push(kernel.getCalculator().minus(param.get(0)));
            Interpreter.returnValue = true;
        } catch (LogoException ignored) {
        }
    }

    void difference(Stack<String> param) {
        Interpreter.returnValue = true;
        try {
            Interpreter.operands.push(kernel.getCalculator().substract(param));
        } catch (LogoException ignored) {
        }
    }

    void sum(Stack<String> param) {
        Interpreter.returnValue = true;
        Interpreter.operands.push(kernel.getCalculator().add(param));
    }

    void setHeading(Stack<String> param) {
        delay();
        try {
            if (DrawPanel.windowMode != DrawPanel.WINDOW_3D)
                app.getDrawPanel().turn(360 - kernel.getActiveTurtle().heading + kernel.getCalculator().numberDouble(param.pop()));
            else {
                app.getDrawPanel().setHeading(kernel.getCalculator().numberDouble(param.pop()));
            }
        } catch (LogoException ignored) {
        }
    }

    void penPaint(Stack<String> param) {
        kernel.getActiveTurtle().setPenReverse(false);
        kernel.getActiveTurtle().setPenDown(true);
        kernel.getActiveTurtle().penColor = kernel.getActiveTurtle().imageColorMode;
    }

    void penReverse(Stack<String> param) {
        kernel.getActiveTurtle().setPenDown(true);
        kernel.getActiveTurtle().setPenReverse(true);
    }

    void penErase(Stack<String> param) {
        kernel.getActiveTurtle().setPenDown(true);
        // if mode penerase isn't active yet
        if (kernel.getActiveTurtle().imageColorMode.equals(kernel.getActiveTurtle().penColor)) {
            kernel.getActiveTurtle().imageColorMode = kernel.getActiveTurtle().penColor;
            kernel.getActiveTurtle().penColor = app.getDrawPanel().getScreenColor();
        }
    }

    void penDown(Stack<String> param) {
        kernel.getActiveTurtle().setPenDown(true);
    }

    void penUp(Stack<String> param) {
        kernel.getActiveTurtle().setPenDown(false);
    }

    void setXY(Stack<String> param) {
        delay();
        try {
            primitive2D("drawing.setxy");
            app.getDrawPanel().setPosition(kernel.getCalculator().numberDouble(param.get(0)) + " " + kernel.getCalculator().numberDouble(param.get(1)));
        } catch (LogoException ignored) {
        }
    }

    void setY(Stack<String> param) {
        delay();
        try {
            if (DrawPanel.windowMode != DrawPanel.WINDOW_3D) {
                double y = kernel.getCalculator().numberDouble(param.get(0));
                double x = kernel.getActiveTurtle().curX - Logo.config.getImageWidth() / 2.0;
                app.getDrawPanel().setPosition(x + " " + y);
            } else
                app.getDrawPanel().setPosition(kernel.getActiveTurtle().X + " " + kernel.getCalculator().numberDouble(param.get(0)) + " " + kernel.getActiveTurtle().Z);

        } catch (LogoException ignored) {
        }
    }

    void setX(Stack<String> param) {
        delay();
        try {
            if (DrawPanel.windowMode != DrawPanel.WINDOW_3D) {
                double x = kernel.getCalculator().numberDouble(param.get(0));
                double y = Logo.config.getImageHeight() / 2.0 - kernel.getActiveTurtle().curY;
                app.getDrawPanel().setPosition(x + " " + y);
            } else
                app.getDrawPanel().setPosition(kernel.getCalculator().numberDouble(param.get(0)) + " " + kernel.getActiveTurtle().Y + " " + kernel.getActiveTurtle().Z);
        } catch (LogoException ignored) {
        }
    }

    void setPos(Stack<String> param) {
        delay();
        try {
            String list = getFinalList(param.get(0));
            app.getDrawPanel().setPosition(list);
        } catch (LogoException ignored) {
        }
    }

    void stop(Stack<String> param) {
        try {
            stop();
        } catch (LogoException ignored) {
        }
    }

    void home(Stack<String> param) {
        delay();
        app.getDrawPanel().home();
    }

    void ifThen(Stack<String> param) {
        try {
            String liste = getList(param.get(1));
            liste = new String(Utils.formatCode(liste));
            String liste2 = null;
            boolean predicat = getBoolean(param.get(0));
            InstructionBuffer instruction = app.getKernel().getInstructionBuffer();
            if (instruction.getLength() != 0) {
                try {
                    String element = instruction.getNextWord();
                    // System.out.println("a"+element+"a");
                    if (element.startsWith("\\l")) {
                        instruction.deleteFirstWord(element);
                        Interpreter.lineNumber = element + " ";
                    }
                    if (instruction.charAt(0) == '[') {
                        instruction.deleteFirstWord("[");
                        liste2 = getFinalList(kernel.listSearch());
                        liste2 = new String(Utils.formatCode(liste2));
                    }
                } catch (Exception ignored) {
                }
            }
            ifThen(predicat, liste, liste2);
            Interpreter.evalReturnValue = true;
        } catch (LogoException ignored) {
        }
    }

    void print(Stack<String> param) {
        StringBuilder result = new StringBuilder();
        for (String s : param) {
            String par = s.trim();
            if (isList(par)) par = formatList(par.substring(1, par.length() - 1));
            String mot = getWord(s);
            if (null == mot) result.append(Utils.unescapeString(par)).append(" ");
            else result.append(Utils.unescapeString(mot)).append(" ");
        }
        app.updateHistory("perso", result + "\n");
    }

    void hideTurtle(Stack<String> param) {
        if (kernel.getActiveTurtle().isVisible()) {
            app.getDrawPanel().toggleTurtle();
            app.getDrawPanel().visibleTurtles.remove(String.valueOf(kernel.getActiveTurtle().id));
        }
        kernel.getActiveTurtle().setVisible(false);
    }

    void showTurtle(Stack<String> param) {
        if (!kernel.getActiveTurtle().isVisible()) {
            app.getDrawPanel().toggleTurtle();
            app.getDrawPanel().visibleTurtles.push(String.valueOf(kernel.getActiveTurtle().id));
        }
        kernel.getActiveTurtle().setVisible(true);
    }

    void repeat(Stack<String> param) {
        try {
            String liste = getList(param.get(1));
            repeat(kernel.getCalculator().getInteger(param.get(0)), liste);
        } catch (LogoException ignored) {
        }
    }

    void clearScreen() {
        app.getDrawPanel().clearScreen();
    }

    void clearScreenWrap(Stack<String> param) {
        app.getDrawPanel().clearScreen();
    }

    void power(Stack<String> param) {
        try {
            Interpreter.returnValue = true;
            Interpreter.operands.push(kernel.getCalculator().power(param.get(0), param.get(1)));
        } catch (LogoException ignored) {
        }
    }

    void left(Stack<String> param) {
        delay();
        try {
            app.getDrawPanel().turn(-kernel.getCalculator().numberDouble(param.pop()));

        } catch (LogoException ignored) {
        }
    }

    void right(Stack<String> param) {
        delay();
        try {
            app.getDrawPanel().turn(kernel.getCalculator().numberDouble(param.pop()));
        } catch (LogoException ignored) {
        }
    }

    void back(Stack<String> param) {
        delay();
        try {
            app.getDrawPanel().move(-kernel.getCalculator().numberDouble(param.pop()));
        } catch (LogoException ignored) {
        }
    }

    void forward(Stack<String> param) {
        delay();
        try {
            app.getDrawPanel().move(kernel.getCalculator().numberDouble(param.pop()));
        } catch (LogoException ignored) {
        }
    }

    /**
     * This method tests if the primitive name exist in 2D mode
     *
     * @param name The primitive name
     * @throws LogoException if in 3D mode
     */
    void primitive2D(String name) throws LogoException {
        if (DrawPanel.windowMode == DrawPanel.WINDOW_3D)
            throw new LogoException(app, Utils.primitiveName(name) + " " + Logo.messages.getString("error.primitive2D"));
    }

    /**
     * This method tests if the primitive name exist in 2D mode
     *
     * @param name The primitive name
     * @throws LogoException if in 2D mode
     */
    void primitive3D(String name) throws LogoException {
        if (DrawPanel.windowMode != DrawPanel.WINDOW_3D)
            throw new LogoException(app, Utils.primitiveName(name) + " " + Logo.messages.getString("error.primitive3D"));
    }

    /**
     * Returns the code [r g b] for the color i
     *
     * @param i Integer representing the Color
     */
    void colorCode(int i) {
        Interpreter.returnValue = true;
        Color co = DrawPanel.defaultColors[i];
        Interpreter.operands.push("[ " + co.getRed() + " " + co.getGreen() + " " + co.getBlue() + " ] ");
    }

    /**
     * Save all procedures whose name are contained in the Stack pile
     *
     * @param fichier The patch to the saved file
     * @param pile    Stack Stack containing all procedure names
     */
    void saveProcedures(String fichier, Stack<String> pile) {
        try {
            StringBuilder aecrire = new StringBuilder();
            boolean bool;
            if (!fichier.endsWith(".lgo")) fichier += ".lgo";
            String path = Utils.unescapeString(Logo.config.getDefaultFolder()) + File.separator + fichier;
            try {
                for (int i = 0; i < workspace.getNumberOfProcedure(); i++) {
                    Procedure procedure = workspace.getProcedure(i);
                    if (null == pile) bool = true;
                    else bool = (pile.search(procedure.name) != -1);
                    if (procedure.displayed && bool) {
                        aecrire.append(Logo.messages.getString("pour")).append(" ").append(procedure.name);
                        for (int j = 0; j < procedure.arity; j++) {
                            aecrire.append(" :").append(procedure.variables.get(j));
                        }
                        aecrire.append("\n").append(procedure.body).append(Logo.messages.getString("fin")).append("\n\n");
                    }
                }
            } catch (NullPointerException ignored) {
            } // If no procedure has been defined.
            Utils.writeLogoFile(path, aecrire.toString());
        } catch (IOException e2) {
            app.updateHistory("erreur", Logo.messages.getString("error.ioecriture"));
        }
    }

    /**
     * Returns the Image defined by the path "chemin"
     *
     * @param path The absolute path for the image
     * @return BufferedImage defined by the path "chemin"
     * @throws LogoException If Image format isn't valid(jpg or png)
     */
    BufferedImage getImage(String path) throws LogoException {
        BufferedImage image;
        String pathWord = getWord(path);
        if (null == pathWord) throw new LogoException(app, path + " " + Logo.messages.getString("error.word"));
        if (!(pathWord.endsWith(".png") || pathWord.endsWith(".jpg")))
            throw new LogoException(app, Logo.messages.getString("erreur_format_image"));
        else {
            try {
                pathWord = Utils.unescapeString(pathWord);
                File f = new File(Utils.unescapeString(Logo.config.getDefaultFolder()) + File.separator + pathWord);
                image = ImageIO.read(f);
            } catch (Exception e1) {
                throw new LogoException(app, Logo.messages.getString("error.iolecture"));
            }
        }
        return image;
    }

    /**
     * Create a local variable called "mot" with no value.
     *
     * @param mot Variable name
     */
    void createLocaleName(String mot) {
        mot = mot.toLowerCase();
        if (!Interpreter.locals.containsKey(mot)) {
            Interpreter.locals.put(mot, null);
        }
    }

    /**
     * Create a new local variable
     *
     * @param param The variable name or a list of variable names
     * @throws LogoException If "param" isn't a list containing all variable names, or a
     *                       word
     */

    void local(Stack<String> param) throws LogoException {
        String li = param.get(0);
        if (isList(li)) {
            li = getFinalList(li);
            StringTokenizer st = new StringTokenizer(li);
            while (st.hasMoreTokens()) {
                String item = st.nextToken();
                isVariableName(item);
                createLocaleName(item);
            }
        } else {
            String mot = getWord(param.get(0));
            if (null != mot) {
                createLocaleName(mot);
            } else throw new LogoException(app, param.get(0) + Logo.messages.getString("error.word"));
        }
    }

    /**
     * returns the color defined by [r g b] contained in "ob"
     *
     * @param obj  the list [r g b]
     * @param name The name of the calling primitive
     * @return The Object Color
     * @throws LogoException If the list doesn't contain 3 numbers
     */

    Color rgb(String obj, String name) throws LogoException {
        String liste = getFinalList(obj);
        StringTokenizer st = new StringTokenizer(liste);
        if (st.countTokens() != 3)
            throw new LogoException(app, name + " " + Logo.messages.getString("color_3_arguments"));
        int[] entier = new int[3];
        for (int i = 0; i < 3; i++) {
            String element = st.nextToken();
            try {
                entier[i] = (int) (Double.parseDouble(element) + 0.5);
            } catch (NumberFormatException e) {
                throw new LogoException(app, element + " " + Logo.messages.getString("pas_nombre"));
            }
            if (entier[i] < 0) entier[i] = 0;
            if (entier[i] > 255) entier[i] = 255;

        }
        return (new Color(entier[0], entier[1], entier[2]));
    }

    /**
     * Primitive member or member?
     *
     * @param param Stack that contains arguments for the primitive member
     * @param id    69 --> member? or 70--> member
     * @throws LogoException Incorrect arguments
     */
    void isMember(Stack<String> param, int id) throws LogoException {
        Interpreter.returnValue = true;
        String mot_retourne = null;
        boolean b = false;
        String mot = getWord(param.get(1));
        StringBuilder liste = new StringBuilder("[ ");
        if (null == mot) { // on travaille sur une liste
            try {
                liste = new StringBuilder(getFinalList(param.get(1)));
                StringTokenizer st = new StringTokenizer(liste.toString());
                liste = new StringBuilder("[ ");
                mot = getWord(param.get(0));
                String str;
                if (null != mot && mot.equals("")) mot = "\\v";
                if (null == mot) mot = param.get(0).trim();
                while (st.hasMoreTokens()) {
                    str = st.nextToken();
                    if (str.equals("[")) str = extractList(st);
                    if (!liste.toString().equals("[ ")) liste.append(str).append(" ");
                    if (str.equals(mot) && liste.toString().equals("[ ")) {
                        if (id == 69) {
                            b = true;
                            break;
                        } else liste.append(str).append(" ");
                    }
                }
            } catch (LogoException ignored) {
            }
        } else { // we work on a word
            String mot2 = getWord(param.get(0));
            if (null != mot2) {
                boolean backslash = false;
                for (int i = 0; i < mot.length(); i++) {
                    char c = mot.charAt(i);
                    if (!backslash && c == '\\') backslash = true;
                    else {
                        String tmp = Character.toString(c);
                        if (backslash) tmp = "\\" + tmp;
                        if (tmp.equals(mot2)) {
                            if (id == 69) {
                                b = true;
                            } else {
                                if (!backslash) mot_retourne = mot.substring(i);
                                else mot_retourne = mot.substring(i - 1);
                            }
                            break;
                        }
                        backslash = false;
                    }
                }
            }
        }
        if (!liste.toString().equals("[ ")) Interpreter.operands.push(liste + "] ");
        else if (null != mot_retourne) {
            try {
                Double.parseDouble(mot_retourne);
                Interpreter.operands.push(mot_retourne);
            } catch (NumberFormatException e) {
                Interpreter.operands.push(wordPrefix + mot_retourne);
            }
        } else if (b) Interpreter.operands.push(Logo.messages.getString("vrai"));
        else Interpreter.operands.push(Logo.messages.getString("faux"));
    }

    /**
     * Primitive before?
     *
     * @param param Stack that contains all arguments
     * @throws LogoException Bad argument type
     */

    void isBefore(Stack<String> param) throws LogoException {
        Interpreter.returnValue = true;
        boolean b = false;
        String[] ope = {"", ""};
        String mot;
        for (int i = 0; i < 2; i++) {
            mot = getWord(param.get(i));
            if (null == mot) throw new LogoException(app, param.get(i) + " " + Logo.messages.getString("pas_mot"));
            else ope[i] = mot;
        }
        if (ope[1].compareTo(ope[0]) > 0) b = true;
        if (b) mot = Logo.messages.getString("vrai");
        else mot = Logo.messages.getString("faux");
        Interpreter.operands.push(mot);
    }

    void isLessThanOrEqual(Stack<String> param) {
        Interpreter.returnValue = true;
        Interpreter.operands.push(kernel.getCalculator().infequal(param));
    }

    void isGreatherThanOrEqual(Stack<String> param) {
        Interpreter.returnValue = true;
        Interpreter.operands.push(kernel.getCalculator().supequal(param));
    }

    void isLessThan(Stack<String> param) {
        Interpreter.returnValue = true;
        Interpreter.operands.push(kernel.getCalculator().inf(param));
    }

    void isGreaterThan(Stack<String> param) {
        Interpreter.returnValue = true;
        Interpreter.operands.push(kernel.getCalculator().sup(param));
    }

    /**
     * / Primitive equal?
     *
     * @param param Stack that contains all arguments
     */
    void isEqual(Stack<String> param) {
        try {
            Double.parseDouble(param.get(0));
            Double.parseDouble(param.get(1));
            Interpreter.operands.push(kernel.getCalculator().equal(param));
        } catch (NumberFormatException e) {
            if (param.get(0).equals(param.get(1))) Interpreter.operands.push(Logo.messages.getString("vrai"));
            else Interpreter.operands.push(Logo.messages.getString("faux"));
        }
        Interpreter.returnValue = true;
    }

    /**
     * this method returns the boolean corresponding to the string st
     *
     * @param st true or false
     * @return The boolean corresponding to the string st
     * @throws LogoException If st isn't equal to true or false
     */

    boolean getBoolean(String st) throws LogoException {
        if (st.toLowerCase().equals(Logo.messages.getString("vrai"))) return true;
        else if (st.toLowerCase().equals(Logo.messages.getString("faux"))) return false;
        else throw new LogoException(app, st + " " + Logo.messages.getString("pas_predicat"));

    }

    /**
     * Returns the word contained in st. If it isn't a word, returns null
     *
     * @param st The Object to convert
     * @return The word corresponding to st
     */
    String getWord(Object st) { // Si c'est un mot
        String liste = st.toString();
        if (liste.equals("\"")) {
            wordPrefix = "";
            return "";
        }
        if (liste.length() > 0 && liste.charAt(0) == '"') {
            wordPrefix = "\"";
            return (liste.substring(1));
        } else try {
            if (liste.equals(String.valueOf(Double.parseDouble(liste)))) wordPrefix = "";
            else wordPrefix = "\"";
            return Utils.unescapeString(liste);
        } catch (NumberFormatException ignored) {
        }
        return (null);
    }

    /**
     * Returns the list contained in the string li
     *
     * @param li The String corresponding to the list
     * @return A list with line Number tag (\0, \1, \2 ...)
     * @throws LogoException List bad format
     */
    String getList(String li) throws LogoException {
        li = li.trim();
        // Retourne la liste sans crochets;
        if (li.charAt(0) == '[' && li.endsWith("]")) {
            li = li.substring(1, li.length() - 1).trim() + " ";
            if (!li.equals(" ")) return li;
            else return ("");
        } else throw new LogoException(app, li + " " + Logo.messages.getString("pas_liste"));
    }

    /**
     * Returns the list contained in the string li without any lineNumber
     *
     * @param li The String corresponding to the list
     * @return A list without any line Number tag (\0, \1, \2 ...)
     * @throws LogoException List bad format
     */

    String getFinalList(String li) throws LogoException {
        // remove line number
        li = li.replaceAll("\\\\l([0-9])+ ", "");
        // return list
        return getList(li);
    }

    // Format the List (only one space between two elements)
    String formatList(String li) {
        StringBuilder tampon = new StringBuilder();
        String precedent = "";
        StringTokenizer st = new StringTokenizer(li, " []", true);
        String element;
        while (st.hasMoreTokens()) {
            element = st.nextToken();
            while (st.hasMoreTokens() && element.equals(" ")) {
                element = st.nextToken();
            }
            if (element.equals("]")) tampon = new StringBuilder(tampon.toString().trim() + "] ");
            else if (element.equals("[")) {
                if (precedent.equals("[")) tampon.append("[");
                else tampon = new StringBuilder(tampon.toString().trim() + " [");
            } else tampon.append(element).append(" ");
            precedent = element;
        }
        return (tampon.toString().trim());
    }

    String extractList(StringTokenizer st) {
        int compteur = 1;
        StringBuilder crochet = new StringBuilder("[ ");
        String element;
        while (st.hasMoreTokens()) {
            element = st.nextToken();
            if (element.equals("[")) {
                compteur++;
                crochet.append("[ ");
            } else if (!element.equals("]")) crochet.append(element).append(" ");
            else if (compteur != 1) {
                compteur--;
                crochet.append("] ");
            } else {
                crochet.append(element).append(" ");
                break;
            }
        }
        element = crochet.toString();
        return element.trim();
    }

    int extractList(String st, int deb) {
        int compteur = 1;
        char element;
        boolean espace = true;
        boolean crochet_ouvert = false;
        boolean crochet_ferme = false;
        for (int i = deb; i < st.length(); i++) {
            element = st.charAt(i);
            if (element == '[') {
                if (espace) crochet_ouvert = true;
                espace = false;
                crochet_ferme = false;
            } else if (element == ']') {
                if (espace) crochet_ferme = true;
                espace = false;
                crochet_ouvert = false;
            } else if (element == ' ') {
                espace = true;
                if (crochet_ouvert) {
                    compteur++;
                    crochet_ouvert = false;
                } else if (crochet_ferme) {
                    crochet_ferme = false;
                    if (compteur != 1) compteur--;
                    else {
                        compteur = i;
                        break;
                    }
                }
            }
        }
        return compteur;
    }

    // returns how many elements contains the list
    int numberOfElements(String list) {
        StringTokenizer st = new StringTokenizer(list);
        int i = 0;
        String element;
        while (st.hasMoreTokens()) {
            element = st.nextToken();
            if (element.equals("[")) extractList(st);
            i++;
        }
        return i;
    }

    // returns the item "i" from the list
    String item(String list, int i) throws LogoException {
        StringTokenizer st = new StringTokenizer(list);
        String element = "";
        int j = 0;
        while (st.hasMoreTokens()) {
            j++;
            element = st.nextToken();
            if (element.equals("[")) element = extractList(st);
            if (j == i) break;
        }
        if (j != i)
            throw new LogoException(app, Logo.messages.getString("y_a_pas") + " " + i + " " + Logo.messages.getString("element_dans_liste") + list + "]");
        else if (i == 0) throw new LogoException(app, Logo.messages.getString("liste_vide"));
        try {
            Double.parseDouble(element);
            return element;
        } // If it's a number, it's returned.
        catch (Exception ignored) {
        }
        if (element.startsWith("[")) return element + " "; // It's a list, we return it as
        // quelle.
        if (element.equals("\\v")) element = "";
        return "\"" + element; // It's necessarily a word, we send it back.
    }

    // Test if the name of the variable is valid
    void isVariableName(String st) throws LogoException {
        if (st.equals("")) throw new LogoException(app, Logo.messages.getString("variable_vide"));
        if (":+-*/() []=<>&|".contains(st))
            throw new LogoException(app, st + " " + Logo.messages.getString("erreur_variable"));

        try {
            Double.parseDouble(st);
            throw new LogoException(app, Logo.messages.getString("erreur_nom_nombre_variable"));
        } catch (NumberFormatException ignored) {
        }

    }

    // primitve make
    void globalMake(Stack<String> param) throws LogoException {
        String mot = getWord(param.get(0));
        if (null == mot) throw new LogoException(app, param.get(0) + " " + Logo.messages.getString("error.word"));
        mot = mot.toLowerCase();
        isVariableName(mot);
        if (Interpreter.locals.containsKey(mot)) {
            Interpreter.locals.put(mot, param.get(1));
        } else {
            workspace.globals.put(mot, param.get(1));
        }
    }

    void delay() {
        if (Logo.config.getTurtleSpeed() != 0) {
            try {
                Thread.sleep(Logo.config.getTurtleSpeed() * 5L);
            } catch (InterruptedException ignored) {
            }
        }
    }

    // How many characters in the word "mot"
    int getWordLength(String mot) {// retourne le nombre de caractères
        // d'un mot
        int compteur = 0;
        boolean backslash = false;
        for (int i = 0; i < mot.length(); i++) {
            if (!backslash && mot.charAt(i) == '\\') backslash = true;
            else {
                backslash = false;
                compteur++;
            }
        }
        return compteur;
    }

    // the character number "i" in the word "mot"
    String itemWord(int entier, String mot) throws LogoException {
        String reponse = "";
        int compteur = 1;
        boolean backslash = false;
        if (mot.equals("")) throw new LogoException(app, Logo.messages.getString("mot_vide"));
        for (int i = 0; i < mot.length(); i++) {
            char c = mot.charAt(i);
            if (!backslash && c == '\\') backslash = true;
            else {
                if (compteur == entier) {
                    if (backslash) reponse = "\\" + c;
                    else reponse = Character.toString(c);
                    break;
                } else {
                    compteur++;
                    backslash = false;
                }
            }
        }
        return reponse;
    }

    void or(Stack<String> param) {
        boolean result = false;
        boolean b;
        try {
            for (String s : param) {
                b = getBoolean(s);
                result = result | b;
            }
            if (result) Interpreter.operands.push(Logo.messages.getString("vrai"));
            else Interpreter.operands.push(Logo.messages.getString("faux"));
            Interpreter.returnValue = true;
        } catch (LogoException ignored) {
        }
    }

    void and(Stack<String> param) {
        boolean result = true;
        boolean b;
        try {
            for (String s : param) {
                b = getBoolean(s);
                result = result & b;
            }
            Interpreter.returnValue = true;
            if (result) Interpreter.operands.push(Logo.messages.getString("vrai"));
            else Interpreter.operands.push(Logo.messages.getString("faux"));
        } catch (LogoException ignored) {
        }
    }



    protected InstructionBuffer getInstructionBuffer() {
        return instructionBuffer;
    }
}