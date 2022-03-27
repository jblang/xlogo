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

/**
 * When a primitive or a procedure has all arguments, Primitives executes
 * the appropriate code.
 */
public class Primitives {
    static Map<String, Primitive> primitiveMap = new TreeMap<>();
    static Stack<LoopProperties> stackLoop = new Stack<>();
    private final Application cadre;
    private final Kernel kernel;
    private Workspace wp;
    private Stack<Workspace> savedWorkspace;
    private String debut_chaine = "";

    interface PrimFunc {
        void execute(Stack<String> param);
    }

    static class Primitive {
        public final String key;
        public final int arity;
        public final boolean general;
        public final PrimFunc function;

        Primitive(String key, int arity, boolean general, PrimFunc function) {
            this.key = key;
            this.arity = arity;
            this.general = general;
            this.function = function;
        }
    }

    final List<Primitive> primitives = List.of(
            new Primitive("&", 1, false, this::andOp),
            new Primitive("|", 1, false, this::orOp),
            new Primitive("*", 1, false, this::multiplyOp),
            new Primitive("+", 1, false, this::addOp),
            new Primitive("-", 1, false, this::subtractOp),
            new Primitive("/", 1, false, this::divideOp),
            new Primitive("<", 1, false, this::isLessThan),
            new Primitive("<=", 1, false, this::isLessThanOrEqual),
            new Primitive("=", 1, false, this::isEqual),
            new Primitive(">", 1, false, this::isGreaterThan),
            new Primitive(">=", 1, false, this::isGreatherThanOrEqual),
            new Primitive(")", 0, false, this::closeParen),
            new Primitive("\\", 0, false, this::endLoop),
            new Primitive("\\siwhile", 2, false, this::evalWhile),
            new Primitive("\\x", 0, false, this::endExecuteTcp),
            new Primitive("\n", 0, false, this::endProc),
            new Primitive("3d.downpitch", 1, false, this::pitchDown),
            new Primitive("3d.leftroll", 1, false, this::rollLeft),
            new Primitive("3d.lineend", 0, false, this::endLine),
            new Primitive("3d.linestart", 0, false, this::startLine),
            new Primitive("3d.orientation", 0, false, this::getOrientation),
            new Primitive("3d.perspective", 0, false, this::enable3D),
            new Primitive("3d.pitch", 0, false, this::getPitch),
            new Primitive("3d.pointend", 0, false, this::endPoint),
            new Primitive("3d.pointstart", 0, false, this::startPoint),
            new Primitive("3d.polyend", 0, false, this::endPolygon),
            new Primitive("3d.polystart", 0, false, this::startPolygon),
            new Primitive("3d.polyview", 0, false, this::view3D),
            new Primitive("3d.rightroll", 1, false, this::rollRight),
            new Primitive("3d.roll", 0, false, this::getRoll),
            new Primitive("3d.setorientation", 1, false, this::setOrientation),
            new Primitive("3d.setpitch", 1, false, this::setPitch),
            new Primitive("3d.setroll", 1, false, this::setRoll),
            new Primitive("3d.setxyz", 3, false, this::setXYZ),
            new Primitive("3d.setz", 1, false, this::setZ),
            new Primitive("3d.textend", 0, false, this::endText),
            new Primitive("3d.textstart", 0, false, this::startText),
            new Primitive("3d.uppitch", 1, false, this::pitchUp),
            new Primitive("ajoute", 3, false, this::addItem),
            new Primitive("ajouteligneflux", 2, false, this::fileAppendLine),
            new Primitive("animation", 0, false, this::startAnimation),
            new Primitive("arithmetic.absolue", 1, false, this::abs),
            new Primitive("arithmetic.acos", 1, false, this::acos),
            new Primitive("arithmetic.alea", 0, false, this::randomZeroToOne),
            new Primitive("arithmetic.arrondi", 1, false, this::round),
            new Primitive("arithmetic.asin", 1, false, this::asin),
            new Primitive("arithmetic.atan", 1, false, this::atan),
            new Primitive("arithmetic.cos", 1, false, this::cos),
            new Primitive("arithmetic.difference", 2, false, this::difference),
            new Primitive("arithmetic.digits", 0, false, this::getSignificantDigits),
            new Primitive("arithmetic.div", 2, false, this::divide),
            new Primitive("arithmetic.exp", 1, false, this::exp),
            new Primitive("arithmetic.hasard", 1, false, this::random),
            new Primitive("arithmetic.inf", 2, false, this::isLessThan),
            new Primitive("arithmetic.infequal", 2, false, this::isLessThanOrEqual),
            new Primitive("arithmetic.log", 1, false, this::log),
            new Primitive("arithmetic.log10", 1, false, this::log10),
            new Primitive("arithmetic.moins", 1, false, this::minus),
            new Primitive("arithmetic.pi", 0, false, this::pi),
            new Primitive("arithmetic.produit", 2, true, this::product),
            new Primitive("arithmetic.puissance", 2, false, this::power),
            new Primitive("arithmetic.quotient", 2, false, this::quotient),
            new Primitive("arithmetic.racine", 1, false, this::sqrt),
            new Primitive("arithmetic.reste", 2, false, this::remainder),
            new Primitive("arithmetic.setdigits", 1, false, this::setSignificantDigits),
            new Primitive("arithmetic.sin", 1, false, this::sin),
            new Primitive("arithmetic.somme", 2, true, this::sum),
            new Primitive("arithmetic.sup", 2, false, this::isGreaterThan),
            new Primitive("arithmetic.supequal", 2, false, this::isGreatherThanOrEqual),
            new Primitive("arithmetic.tan", 1, false, this::tan),
            new Primitive("arithmetic.tronque", 1, false, this::truncate),
            new Primitive("aritmetic.modulo", 2, false, this::mod),
            new Primitive("attends", 1, false, this::wait),
            new Primitive("axis", 1, false, this::drawBothAxes),
            new Primitive("axiscolor", 0, false, this::getAxisColor),
            new Primitive("bc", 0, false, this::penDown),
            new Primitive("bc?", 0, false, this::isPenDown),
            new Primitive("bye", 0, false, this::bye),
            new Primitive("cap", 0, false, this::heading),
            new Primitive("caractere", 1, false, this::getCharacter),
            new Primitive("cat", 0, false, this::getFiles),
            new Primitive("cc", 0, false, this::penColor),
            new Primitive("cf", 0, false, this::screenColor),
            new Primitive("changedossier", 1, false, this::changeDirectory),
            new Primitive("chattcp", 2, false, this::chatTcp),
            new Primitive("choix", 1, false, this::pick),
            new Primitive("ci", 1, false, this::loadImage),
            new Primitive("clos", 0, false, this::fenceTurtle),
            new Primitive("color.blanc", 0, false, this::setColorWhite),
            new Primitive("color.bleu", 0, false, this::setColorBlue),
            new Primitive("color.bleufonce", 0, false, this::setColorDarkBlue),
            new Primitive("color.cyan", 0, false, this::setColorCyan),
            new Primitive("color.gris", 0, false, this::setColorGray),
            new Primitive("color.grisclair", 0, false, this::setColorLightGray),
            new Primitive("color.jaune", 0, false, this::setColorYellow),
            new Primitive("color.magenta", 0, false, this::setColorMagenta),
            new Primitive("color.marron", 0, false, this::setColorBrown),
            new Primitive("color.noir", 0, false, this::setColorBlack),
            new Primitive("color.orange", 0, false, this::setColorOrange),
            new Primitive("color.rose", 0, false, this::setColorPink),
            new Primitive("color.rouge", 0, false, this::setColorRed),
            new Primitive("color.rougefonce", 0, false, this::setColorDarkRed),
            new Primitive("color.vert", 0, false, this::setColorGreen),
            new Primitive("color.vertfonce", 0, false, this::setColorDarkGreen),
            new Primitive("color.violet", 0, false, this::setColorPurple),
            new Primitive("compte", 1, false, this::count),
            new Primitive("controls.compteur", 0, false, this::getRepCount),
            new Primitive("controls.foreach", 3, false, this::forEach),
            new Primitive("controls.forever", 1, false, this::forever),
            new Primitive("controls.ifelse", 3, false, this::ifElse),
            new Primitive("controls.repete", 2, false, this::repeat),
            new Primitive("controls.repetepour", 2, false, this::_for),
            new Primitive("controls.stop", 0, false, this::stop),
            new Primitive("controls.stoptout", 0, false, this::stopAll),
            new Primitive("controls.tantque", 2, false, this::_while),
            new Primitive("couleurtexte", 0, false, this::getTextColor),
            new Primitive("ct", 0, false, this::hideTurtle),
            new Primitive("date", 0, false, this::getDate),
            new Primitive("de", 0, false, this::penPaint),
            new Primitive("debuttemps", 1, false, this::countdown),
            new Primitive("dernier", 1, false, this::last),
            new Primitive("distance", 1, false, this::distance),
            new Primitive("drawing.arc", 3, false, this::arc),
            new Primitive("drawing.av", 1, false, this::forward),
            new Primitive("drawing.cercle", 1, false, this::circle),
            new Primitive("drawing.etiquette", 1, false, this::label),
            new Primitive("drawing.fillpolygon", 1, false, this::fillPolygon),
            new Primitive("drawing.fixecap", 1, false, this::setHeading),
            new Primitive("drawing.fixex", 1, false, this::setX),
            new Primitive("drawing.fixexy", 2, false, this::setXY),
            new Primitive("drawing.fixey", 1, false, this::setY),
            new Primitive("drawing.fontjustify", 0, false, this::getFontJustify),
            new Primitive("drawing.fpos", 1, false, this::setPos),
            new Primitive("drawing.longueuretiquette", 1, false, this::getLabelLength),
            new Primitive("drawing.origine", 0, false, this::home),
            new Primitive("drawing.point", 1, false, this::dot),
            new Primitive("drawing.re", 1, false, this::back),
            new Primitive("drawing.saveimage", 2, false, this::saveImage),
            new Primitive("drawing.setfontjustify", 1, false, this::setFontJustify),
            new Primitive("drawing.td", 1, false, this::right),
            new Primitive("drawing.tg", 1, false, this::left),
            new Primitive("drawing.x", 0, false, this::getX),
            new Primitive("drawing.y", 0, false, this::getY),
            new Primitive("drawing.z", 0, false, this::getZ),
            new Primitive("drawingquality", 0, false, this::getDrawingQuality),
            new Primitive("ec", 1, true, this::print),
            new Primitive("ecoutetcp", 0, false, this::listenTcp),
            new Primitive("ecrisligneflux", 2, false, this::fileWriteLine),
            new Primitive("efseq", 0, false, this::deleteSequence),
            new Primitive("egal?", 2, false, this::isEqual),
            new Primitive("enleve", 2, false, this::remove),
            new Primitive("enr", 0, false, this::wrapTurtle),
            new Primitive("entier?", 1, false, this::isInteger),
            new Primitive("envoietcp", 2, false, this::sendTcp),
            new Primitive("et", 2, true, this::and),
            new Primitive("executetcp", 2, false, this::executeTcp),
            new Primitive("faux", 0, false, this::_false),
            new Primitive("fcc", 1, false, this::setPenColor),
            new Primitive("fcfg", 1, false, this::setScreenColor),
            new Primitive("fct", 1, false, this::setTextColor),
            new Primitive("fen", 0, false, this::windowTurtle),
            new Primitive("fermeflux", 1, false, this::closeFile),
            new Primitive("findseq", 1, false, this::setSequenceIndex),
            new Primitive("finflux?", 1, false, this::isEndOfFile),
            new Primitive("finstr", 1, false, this::setInstrument),
            new Primitive("fintemps?", 0, false, this::isCountdownEnded),
            new Primitive("fixenompolice", 1, false, this::setLabelFont),
            new Primitive("fixenompolicetexte", 1, false, this::setTextFont),
            new Primitive("fixeseparation", 1, false, this::setSeparation),
            new Primitive("fixestyle", 1, false, this::setTextStyle),
            new Primitive("fpolice", 1, false, this::setFontSize),
            new Primitive("fpt", 1, false, this::setTextSize),
            new Primitive("frep", 1, false, this::setDirectory),
            new Primitive("ftc", 1, false, this::setPenWidth),
            new Primitive("ftortue", 1, false, this::setTurtle),
            new Primitive("go", 0, false, this::penErase),
            new Primitive("grid?", 0, false, this::isGridEnabled),
            new Primitive("gridcolor", 0, false, this::getGridColor),
            new Primitive("grille", 2, false, this::drawGrid),
            new Primitive("guiaction", 2, false, this::guiAction),
            new Primitive("guibutton", 2, false, this::guiButton),
            new Primitive("guidraw", 1, false, this::guiDraw),
            new Primitive("guimenu", 2, false, this::guiMenu),
            new Primitive("guiposition", 2, false, this::guiPosition),
            new Primitive("guiremove", 1, false, this::guiRemove),
            new Primitive("heure", 0, false, this::getTime),
            new Primitive("ic", 0, false, this::penReverse),
            new Primitive("indseq", 0, false, this::getSequenceIndex),
            new Primitive("instr", 0, false, this::getInstrument),
            new Primitive("inverse", 1, false, this::reverse),
            new Primitive("item", 2, false, this::item),
            new Primitive("joue", 0, false, this::play),
            new Primitive("lc", 0, false, this::penUp),
            new Primitive("lis", 2, false, this::read),
            new Primitive("liscar", 0, false, this::readChar),
            new Primitive("liscarflux", 1, false, this::fileReadChar),
            new Primitive("lisligneflux", 1, false, this::fileReadLine),
            new Primitive("lissouris", 0, false, this::mouseButton),
            new Primitive("liste", 2, true, this::list),
            new Primitive("liste?", 1, false, this::isList),
            new Primitive("listeflux", 0, false, this::getOpenFiles),
            new Primitive("loop.dountil", 2, false, this::doUntil),
            new Primitive("loop.dowhile", 2, false, this::doWhile),
            new Primitive("md", 2, false, this::lput),
            new Primitive("membre", 2, false, this::isMember71),
            new Primitive("membre?", 2, false, this::isMember69),
            new Primitive("message", 1, false, this::message),
            new Primitive("mot", 2, true, this::word),
            new Primitive("mot?", 1, false, this::isWord),
            new Primitive("mp", 2, false, this::fput),
            new Primitive("mt", 0, false, this::showTurtle),
            new Primitive("nettoie", 0, false, this::wash),
            new Primitive("nombre?", 1, false, this::isNumber),
            new Primitive("nompolice", 0, false, this::getLabelFont),
            new Primitive("nompolicetexte", 0, false, this::getTextFont),
            new Primitive("non", 1, false, this::not),
            new Primitive("ou", 2, true, this::or),
            new Primitive("ouvreflux", 2, false, this::openFile),
            new Primitive("penshape", 0, false, this::getPenShape),
            new Primitive("penwidth", 0, false, this::getPenWidth),
            new Primitive("ph", 2, true, this::sentence),
            new Primitive("police", 0, false, this::getFontSize),
            new Primitive("pos", 0, false, this::position),
            new Primitive("possouris", 0, false, this::mousePosition),
            new Primitive("precede?", 2, false, this::isBeforeWrap),
            new Primitive("premier", 1, false, this::first),
            new Primitive("prim?", 1, false, this::isPrimitive),
            new Primitive("proc?", 1, false, this::isProcedure),
            new Primitive("ptexte", 0, false, this::getTextSize),
            new Primitive("rafraichis", 0, false, this::refresh),
            new Primitive("ramene", 1, false, this::load),
            new Primitive("remplace", 3, false, this::replaceItem),
            new Primitive("remplis", 0, false, this::fill),
            new Primitive("rempliszone", 0, false, this::fillZone),
            new Primitive("rep", 0, false, this::getDirectory),
            new Primitive("resetall", 0, false, this::resetAll),
            new Primitive("ret", 1, false, this::output),
            new Primitive("sauve", 2, false, this::save),
            new Primitive("sauved", 1, false, this::saveAll),
            new Primitive("sd", 1, false, this::butLast),
            new Primitive("separation", 0, false, this::getSeparation),
            new Primitive("seq", 1, false, this::sequence),
            new Primitive("setaxiscolor", 1, false, this::setAxisColor),
            new Primitive("setdrawingquality", 1, false, this::setDrawingQuality),
            new Primitive("setgridcolor", 1, false, this::setGridColor),
            new Primitive("setpenshape", 1, false, this::setPenShape),
            new Primitive("setscreensize", 1, false, this::setScreenSize),
            new Primitive("setturtlesnumber", 1, false, this::setTurtlesMax),
            new Primitive("setzoom", 1, false, this::setZoom),
            new Primitive("si", 2, false, this::_if),
            new Primitive("sound.mp3play", 1, false, this::playMP3),
            new Primitive("sound.mp3stop", 0, false, this::stopMP3),
            new Primitive("souris?", 0, false, this::isMouseEvent),
            new Primitive("sp", 1, false, this::butFirst),
            new Primitive("stopanimation", 0, false, this::stopAnimation),
            new Primitive("stopaxis", 0, false, this::disableAxes),
            new Primitive("stopgrille", 0, false, this::disableGrid),
            new Primitive("style", 0, false, this::getTextStyle),
            new Primitive("taillefenetre", 0, false, this::getZoneSize),
            new Primitive("tailleimage", 0, false, this::getImageSize),
            new Primitive("tape", 1, false, this::write),
            new Primitive("tc", 1, false, this::findColor),
            new Primitive("temps", 0, false, this::getTimePassed),
            new Primitive("tortue", 0, false, this::getActiveTurtle),
            new Primitive("tortues", 0, false, this::getTurtles),
            new Primitive("touche?", 0, false, this::isKey),
            new Primitive("tuetortue", 1, false, this::eraseTurtle),
            new Primitive("turtle.fforme", 1, false, this::setShape),
            new Primitive("turtle.forme", 0, false, this::getShape),
            new Primitive("turtlesnumber", 0, false, this::getTurtlesMax),
            new Primitive("unicode", 1, false, this::getUnicode),
            new Primitive("var?", 1, false, this::isVariable),
            new Primitive("ve", 0, false, this::clearScreenWrap),
            new Primitive("vers", 1, false, this::towards),
            new Primitive("vide?", 1, false, this::isEmpty),
            new Primitive("visible?", 0, false, this::isVisible),
            new Primitive("vrai", 0, false, this::_true),
            new Primitive("vt", 0, false, this::clearText),
            new Primitive("workspace.chose", 1, false, this::getVariableValue),
            new Primitive("workspace.content", 0, false, this::getContents),
            new Primitive("workspace.def", 2, false, this::define),
            new Primitive("workspace.donne", 2, false, this::globalMakeWrap),
            new Primitive("workspace.donnelocale", 2, false, this::localMake),
            new Primitive("workspace.ed", 1, false, this::edit),
            new Primitive("workspace.edall", 0, false, this::editAll),
            new Primitive("workspace.efn", 1, false, this::eraseProcedure),
            new Primitive("workspace.efns", 0, false, this::eraseAll),
            new Primitive("workspace.efv", 1, false, this::eraseVariable),
            new Primitive("workspace.erpl", 1, false, this::erasePropertyList),
            new Primitive("workspace.exec", 1, false, this::run),
            new Primitive("workspace.externalcommand", 1, false, this::runExternalCommand),
            new Primitive("workspace.gprop", 2, false, this::getProperty),
            new Primitive("workspace.imts", 0, false, this::procedures),
            new Primitive("workspace.listevariables", 0, false, this::listVariables),
            new Primitive("workspace.locale", 1, false, this::localWrap),
            new Primitive("workspace.plist", 1, false, this::listProperties),
            new Primitive("workspace.pprop", 3, false, this::setProperty),
            new Primitive("workspace.primitives", 0, false, this::listPrimitives),
            new Primitive("workspace.propertylists", 0, false, this::getPropertyLists),
            new Primitive("workspace.remprop", 2, false, this::removeProperty),
            new Primitive("workspace.stoptrace", 0, false, this::stopTrace),
            new Primitive("workspace.text", 1, false, this::getProcedureBody),
            new Primitive("workspace.trace", 0, false, this::trace),
            new Primitive("xaxis", 1, false, this::drawXAxis),
            new Primitive("xaxis?", 0, false, this::isXAxisEnabled),
            new Primitive("yaxis", 1, false, this::drawYAxis),
            new Primitive("yaxis?", 0, false, this::isYAxisEnabled),
            new Primitive("zoom", 0, false, this::getZoom)
    );

    /**
     * @param cadre Default frame Application
     * @param wp  Default workspace
     */
    public Primitives(Application cadre, Workspace wp) {
        this.wp = wp;
        this.cadre = cadre;
        this.kernel = cadre.getKernel();
        buildPrimitiveTreemap();
    }

    /**
     * This methods returns a list which contains all the primitive for the current language
     *
     * @return The primitives list
     */
    protected String getAllPrimitives() {
        List<String> list = new ArrayList<>();
        Locale locale = Logo.getLocale(Logo.config.getLanguage());
        ResourceBundle names = ResourceBundle.getBundle("primitives", Objects.requireNonNull(locale));
        for (var prim : primitives) {
            // Exclude internal primitives \n \\x and \\siwhile
            // Exclude all arithmetic symbols + - / * & |
            if (!Interpreter.isInfixedOperator(prim.key) || !Interpreter.isSpecialPrim(prim.key)) {
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

    //Exécution des primitives
    public void buildPrimitiveTreemap() {
        primitiveMap = new TreeMap<>();
        Locale locale = Logo.getLocale(Logo.config.getLanguage());
        ResourceBundle names = ResourceBundle.getBundle("primitives", Objects.requireNonNull(locale));
        for (var prim : primitives) {
            // read the standard number of arguments for the primitive
            String name = prim.key;
            if (!Interpreter.isInfixedOperator(name) && !Interpreter.isSpecialPrim(name)) {
                name = names.getString(name);
            }
            if (name.equals("\n")) {
                primitiveMap.put(name, prim);
            } else {
                StringTokenizer st = new StringTokenizer(name.toLowerCase());
                while (st.hasMoreTokens())
                    primitiveMap.put(st.nextToken(), prim);
            }
        }
    }

    /**
     * This method creates the loop "repeat"
     *
     * @param i  The number of iteration
     * @param st The instruction to execute
     */
    protected void repete(int i, String st) {
        if (i > 0) {
            st = new String(Utils.formatCode(st));
            LoopProperties bp = new LoopRepeat(BigDecimal.ONE, new BigDecimal(i), BigDecimal.ONE, st);
            stackLoop.push(bp);
            cadre.getKernel().getInstructionBuffer().insert(st + "\\ ");
        } else if (i != 0) {
            try {
                throw new LogoException(cadre, Utils.primitiveName("controls.repete") + " " + Logo.messages.getString("attend_positif"));
            } catch (LogoException e) {
            }
        }
    }

    /**
     * This method is an internal primitive for the primitive "while"
     *
     * @param b  Do we still execute the loop body?
     * @param li The loop body instructions
     */
    void whilesi(boolean b, String li) {
        if (b) {
            cadre.getKernel().getInstructionBuffer().insert(li + stackLoop.peek().getInstr());
        } else {
            try {
                eraseLevelStop();
            } catch (LogoException e) {
            }
        }
    }

    // primitive if
    void si(boolean b, String li, String li2) {
        if (b) {
            cadre.getKernel().getInstructionBuffer().insert(li);
        } else if (null != li2) {
            cadre.getKernel().getInstructionBuffer().insert(li2);
        }
    }

    // primitive stop
    void stop() throws LogoException {
        Interpreter.operande = false;
        String car = "";
        try {
            car = eraseLevelStop();
        } catch (LogoException e) {
        }

        // A procedure has been stopped
        if (car.equals("\n")) {
            String en_cours = Interpreter.en_cours.pop();
            Interpreter.locale = Interpreter.stockvariable.pop();
            // Example: 	to bug
            //				fd stop
            //				end
            // 				--------
            //				bug
            //				stop doesn't output to fd
            if (!Interpreter.nom.isEmpty() && !Interpreter.nom.peek().equals("\n")) {
                //	System.out.println(Interpreter.nom);
                throw new LogoException(cadre, Utils.primitiveName("controls.stop") + " " + Logo.messages.getString("ne_renvoie_pas") + " " + Interpreter.nom.peek());
            } else if (!Interpreter.nom.isEmpty()) {
                // Removing the character "\n"
                Interpreter.nom.pop();
                // Example: 	to bug		|	to bug2
                // 				fd bug2		|	stop
                //				end			|	end
                //				------------------------
                // 				bug
                //				bug2 doesn't output to fd
                if (!Interpreter.nom.isEmpty() && !Interpreter.nom.peek().equals("\n")) {
                    //	System.out.println(Interpreter.nom);
                    throw new LogoException(cadre, en_cours + " " + Logo.messages.getString("ne_renvoie_pas") + " " + Interpreter.nom.peek());
                }
            }
        }
    }

    // primitive output
    void retourne(String val) throws LogoException {
        Interpreter.calcul.push(val);
        Interpreter.operande = true;
        if (Kernel.mode_trace) {
            String buffer = "  ".repeat(Math.max(0, Interpreter.en_cours.size() - 1)) + Interpreter.en_cours.peek() + " " + Utils.primitiveName("ret") + " " + val;
            cadre.updateHistory("normal", Utils.unescapeString(buffer) + "\n");
        }
        Interpreter.en_cours.pop();
        Interpreter.locale = Interpreter.stockvariable.pop();
        if ((!Interpreter.nom.isEmpty()) && Interpreter.nom.peek().equals("\n")) {
            try {
                eraseLevelReturn();
            } catch (LogoException e) {
            }
            Interpreter.nom.pop();
        } else if (!Interpreter.nom.isEmpty())
            throw new LogoException(cadre, Utils.primitiveName("ret") + " " + Logo.messages.getString("ne_renvoie_pas") + " " + Interpreter.nom.peek());
        else throw new LogoException(cadre, Logo.messages.getString("erreur_retourne"));
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
        InstructionBuffer instruction = cadre.getKernel().getInstructionBuffer();
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
            throw new LogoException(cadre, Logo.messages.getString("erreur_stop"));
        }
        if (marqueur + 2 <= instruction.getLength()) instruction.delete(0, marqueur + 2);
        if (!caractere.equals("\n")) {
            stackLoop.pop();
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
        InstructionBuffer instruction = cadre.getKernel().getInstructionBuffer();
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
            throw new LogoException(cadre, Logo.messages.getString("erreur_retourne"));
        }
        if (marqueur + 2 <= instruction.getLength()) instruction.delete(0, marqueur + 2);
        for (int i = 0; i < loopLevel; i++) {
            stackLoop.pop();
        }
    }

    void exportPrimCSV() {
        StringBuilder sb = new StringBuilder();
        Locale locale = new Locale("fr", "FR");
        ResourceBundle names_fr = ResourceBundle.getBundle("primitives", locale);
        locale = new Locale("en", "US");
        ResourceBundle names_en = ResourceBundle.getBundle("primitives", locale);
        locale = new Locale("ar", "MA");
        ResourceBundle names_ar = ResourceBundle.getBundle("primitives", locale);
        locale = new Locale("es", "ES");
        ResourceBundle names_es = ResourceBundle.getBundle("primitives", locale);
        locale = new Locale("pt", "BR");
        ResourceBundle names_pt = ResourceBundle.getBundle("primitives", locale);
        locale = new Locale("eo", "EO");
        ResourceBundle names_eo = ResourceBundle.getBundle("primitives", locale);
        locale = new Locale("de", "DE");
        ResourceBundle names_de = ResourceBundle.getBundle("primitives", locale);
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
        ResourceBundle lang_fr = ResourceBundle.getBundle("langage", locale);
        locale = new Locale("en", "US");
        ResourceBundle lang_en = ResourceBundle.getBundle("langage", locale);
        locale = new Locale("ar", "MA");
        ResourceBundle lang_ar = ResourceBundle.getBundle("langage", locale);
        locale = new Locale("es", "ES");
        ResourceBundle lang_es = ResourceBundle.getBundle("langage", locale);
        locale = new Locale("pt", "BR");
        ResourceBundle lang_pt = ResourceBundle.getBundle("langage", locale);
        locale = new Locale("eo", "EO");
        ResourceBundle lang_eo = ResourceBundle.getBundle("langage", locale);
        locale = new Locale("de", "DE");
        ResourceBundle lang_de = ResourceBundle.getBundle("langage", locale);
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

    /**
     * Tests if "li" is a list
     *
     * @param li The String to test
     * @return true if it is a list, else false
     */
    //
    protected static boolean isList(String li) {
        li = li.trim();
        return li.length() > 0 && li.charAt(0) == '['
                && li.endsWith("]");
    }

    private void getFontJustify(Stack<String> param) {
        Interpreter.operande = true;
        Interpreter.calcul.push(kernel.getActiveTurtle().getFontJustify());
    }

    private void setFontJustify(Stack<String> param) {
        Interpreter.operande = false;
        try {
            String li1 = getFinalList(param.get(0));
            kernel.getActiveTurtle().setFontJustify(li1);
        } catch (LogoException e) {
            e.printStackTrace();
        }
    }

    private void mod(Stack<String> param) {
        Interpreter.operande = true;
        try {
            Interpreter.calcul.push(kernel.getCalculator().modulo(param.get(0), param.get(1)));
        } catch (LogoException e) {
        }
    }

    private void doWhile(Stack<String> param) {
        try {
            String li1 = getList(param.get(0));
            li1 = new String(Utils.formatCode(li1));
            String li2 = getList(param.get(1));
            li2 = new String(Utils.formatCode(li2));
            String instr = "\\siwhile " + li2 + "[ " + li1 + "] ";
            LoopWhile bp = new LoopWhile(BigDecimal.ONE, BigDecimal.ZERO, BigDecimal.ONE, instr);
            stackLoop.push(bp);
            cadre.getKernel().getInstructionBuffer().insert(li1 + instr + Interpreter.END_LOOP + " ");
        } catch (LogoException e) {
        }
    }

    private void doUntil(Stack<String> param) {
        try {
            String li1 = getList(param.get(0));
            li1 = new String(Utils.formatCode(li1));
            String li2 = getList(param.get(1));
            li2 = new String(Utils.formatCode(li2));
            String instr = "\\siwhile " + Utils.primitiveName("non") + " " + li2 + "[ " + li1 + "] ";
            LoopWhile bp = new LoopWhile(BigDecimal.ONE, BigDecimal.ZERO, BigDecimal.ONE, instr);
            stackLoop.push(bp);
            cadre.getKernel().getInstructionBuffer().insert(instr + Interpreter.END_LOOP + " ");
        } catch (LogoException e) {
        }
    }

    private void randomZeroToOne(Stack<String> param) {
        Interpreter.operande = true;
        Interpreter.calcul.push(Calculator.teste_fin_double(Math.random()));
    }

    private void fillPolygon(Stack<String> param) {
        Interpreter.operande = false;
        try {
            String list = getFinalList(param.get(0));
            LoopFillPolygon bp = new LoopFillPolygon();
            stackLoop.push(bp);
            cadre.getKernel().getInstructionBuffer().insert(Utils.formatCode(list) + Interpreter.END_LOOP + " ");
            cadre.getDrawPanel().startRecord2DPolygon();
        } catch (LogoException e) {
        }
    }

    private void getZ(Stack<String> param) {
        Interpreter.operande = true;
        try {
            primitive3D("drawing.z");
            Interpreter.calcul.push(Calculator.teste_fin_double(kernel.getActiveTurtle().Z));
        } catch (LogoException e) {
        }
    }

    private void getY(Stack<String> param) {
        Interpreter.operande = true;
        Interpreter.calcul.push(Calculator.teste_fin_double(kernel.getActiveTurtle().getY()));
    }

    private void getX(Stack<String> param) {
        Interpreter.operande = true;
        Interpreter.calcul.push(Calculator.teste_fin_double(kernel.getActiveTurtle().getX()));
    }

    private void getZoom(Stack<String> param) {
        Interpreter.operande = true;
        Interpreter.calcul.push(Calculator.teste_fin_double(DrawPanel.zoom));
    }

    private void stopMP3(Stack<String> param) {
        Interpreter.operande = false;
        if (null != kernel.getMp3Player()) kernel.getMp3Player().getPlayer().close();
    }

    private void playMP3(Stack<String> param) {
        String mot;
        Interpreter.operande = false;
        if (kernel.getMp3Player() != null) kernel.getMp3Player().getPlayer().close();
        mot = getWord(param.get(0));
        try {
            if (null == mot) throw new LogoException(cadre, mot + " "
                    + Logo.messages.getString("error.word"));
            MP3Player player = new MP3Player(cadre, mot);
            kernel.setMp3Player(player);
            kernel.getMp3Player().start();
        } catch (LogoException z) {
        }
    }

    private void saveImage(Stack<String> param) {
        try {
            String word = getWord(param.get(0));
            if (null == word)
                throw new LogoException(cadre, param.get(0) + " " + Logo.messages.getString("error.word"));
            if (word.equals(""))
                throw new LogoException(cadre, param.get(0) + " " + Logo.messages.getString("mot_vide"));
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
            cadre.getDrawPanel().saveImage(word, coord);
            Interpreter.operande = false;
        } catch (LogoException e) {
        }
    }

    private void runExternalCommand(Stack<String> param) {
        Interpreter.operande = false;
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

    private void getProcedureBody(Stack<String> param) {
        StringBuffer sb;
        try {
            String var = getWord(param.get(0));
            if (null == var)
                throw new LogoException(cadre, param.get(0) + " " + Logo.messages.getString("error.word"));
            int index = -1;
            for (int i = 0; i < wp.getNumberOfProcedure(); i++) {
                if (wp.getProcedure(i).name.equals(var)) {
                    index = i;
                    break;
                }
            }
            if (index != -1) {
                Procedure proc = wp.getProcedure(index);
                sb = new StringBuffer();
                sb.append("[ [ ");
                // Append variable names
                for (int j = 0; j < proc.nbparametre; j++) {
                    sb.append(proc.variable.get(j));
                    sb.append(" ");
                }
                for (int j = 0; j < proc.optVariables.size(); j++) {
                    sb.append("[ ");
                    sb.append(proc.optVariables.get(j));
                    sb.append(" ");
                    sb.append(proc.optVariablesExp.get(j).toString());
                    sb.append(" ] ");
                }
                sb.append("] ");
                // Append body procedure
                sb.append(proc.cutInList());
                sb.append("] ");
                Interpreter.operande = true;
                Interpreter.calcul.push(sb.toString());
            } else
                throw new LogoException(cadre, var + " " + Logo.messages.getString("error.procedure.must.be"));
        } catch (LogoException e) {
        }
    }

    private void getSignificantDigits(Stack<String> param) {
        Interpreter.operande = true;
        Interpreter.calcul.push(String.valueOf(kernel.getCalculator().getDigits()));
    }

    private void setSignificantDigits(Stack<String> param) {
        Interpreter.operande = false;
        try {
            kernel.initCalculator(kernel.getCalculator().getInteger(param.get(0)));
        } catch (LogoException e) {
        }
    }

    private void forever(Stack<String> param) {
        try {
            String li2 = getList(param.get(0));
            li2 = new String(Utils.formatCode(li2));
            LoopProperties bp = new LoopProperties(BigDecimal.ONE, BigDecimal.ZERO
                    , BigDecimal.ONE, li2);
            cadre.getKernel().getInstructionBuffer().insert(li2 + Interpreter.END_LOOP + " ");
            stackLoop.push(bp);
        } catch (LogoException e) {
        }
    }

    private void forEach(Stack<String> param) {
        try {
            // Variable name
            String var = getWord(param.get(0));
            // If it isn't a word
            if (null == var) throw new LogoException(cadre, param.get(0) + " " +
                    Logo.messages.getString("error.word"));
                // If it's a number
            else {
                try {
                    Double.parseDouble(var);
                    throw new LogoException(cadre, Logo.messages.getString("erreur_nom_nombre_variable"));
                } catch (NumberFormatException e1) {
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
            Vector<String> elements = new Vector<String>();
            while (!li1.equals("")) {
                String character = "";
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
                LoopForEach bp = new LoopForEach(BigDecimal.ZERO, new BigDecimal(elements.size() - 1)
                        , BigDecimal.ONE, li2, var.toLowerCase(), elements);
                bp.AffecteVar(true);
                cadre.getKernel().getInstructionBuffer().insert(li2 + Interpreter.END_LOOP + " ");
                stackLoop.push(bp);
            }
        } catch (LogoException e) {
        }
    }

    private void editAll(Stack<String> param) {
        cadre.editor.open();
    }

    private void edit(Stack<String> param) {
        String mot;
        try {
            mot = this.getWord(param.get(0));
            if (null == mot) mot = this.getFinalList(param.get(0));
            StringTokenizer st = new StringTokenizer(mot);
            // Write all procedures names in a Vector
            Vector<String> names = new Vector<String>();
            while (st.hasMoreTokens()) {
                names.add(st.nextToken());
            }
            cadre.editor.setTitle(Logo.messages
                    .getString("editeur"));

            cadre.editor.setMainCommand();
            cadre.editor.setTitle(Logo.messages.getString("editeur"));
            cadre.editor.discardAllEdits();
            cadre.editor.setVisible(true);
            cadre.editor.toFront();
            cadre.editor.requestFocus();

            for (int i = 0; i < wp.getNumberOfProcedure(); i++) {
                Procedure procedure = wp.getProcedure(i);
//							System.out.println(procedure.toString().length());
                if (names.contains(procedure.name) && procedure.affichable) {
                    cadre.editor.appendText(procedure.toString());
                }
            }
        } catch (LogoException e) {
        }
    }

    private void ifElse(Stack<String> param) {
        String liste;
        try {
            liste = getList(param.get(1));
            liste = new String(Utils.formatCode(liste));
            boolean predicat = predicat(param.get(0));
            String liste2 = getList(param.get(2));
            liste = new String(Utils.formatCode(liste));
            kernel.primitive.si(predicat, liste, liste2);
            Interpreter.renvoi_instruction = true;
        } catch (LogoException e) {
        }
    }

    private void log(Stack<String> param) {
        Interpreter.operande = true;
        try {
            Interpreter.calcul.push(kernel.getCalculator().log(param.get(0)));
        } catch (LogoException e) {
        }
    }

    private void exp(Stack<String> param) {
        Interpreter.operande = true;
        try {
            Interpreter.calcul.push(kernel.getCalculator().exp(param.get(0)));

        } catch (LogoException e) {
        }
    }

    private void erasePropertyList(Stack<String> param) {
        Interpreter.operande = false;
        this.erase(param.get(0), "propertylist");
    }

    private void getContents(Stack<String> param) {
        StringBuffer sb;
        Interpreter.operande = true;
        sb = new StringBuffer("[ ");
        sb.append(this.getAllProcedures());
        sb.append(this.getAllVariables());
        sb.append(this.getAllpropertyLists());
        sb.append("] ");
        Interpreter.calcul.push(new String(sb));
    }

    private void getPropertyLists(Stack<String> param) {
        Interpreter.operande = true;
        Interpreter.calcul.push(new String(getAllpropertyLists()));
    }

    private void listPrimitives(Stack<String> param) {
        Interpreter.operande = true;
        Interpreter.calcul.push(kernel.primitive.getAllPrimitives());
    }

    private void endText(Stack<String> param) {
        DrawPanel.record3D = DrawPanel.RECORD_3D_NONE;
    }

    private void startText(Stack<String> param) {
        DrawPanel.record3D = DrawPanel.RECORD_3D_TEXT;
        cadre.initViewer3D();
//                    	if (null==DrawPanel.listText) DrawPanel.listText=new java.util.Vector<TransformGroup>();
        DrawPanel.poly = null;
    }

    private void endPoint(Stack<String> param) {
        DrawPanel.record3D = DrawPanel.RECORD_3D_NONE;
        try {
            DrawPanel.poly.addToScene();
        } catch (LogoException e) {
        }
    }

    private void startPoint(Stack<String> param) {
        DrawPanel.record3D = DrawPanel.RECORD_3D_POINT;
        cadre.initViewer3D();
//                    	if (null==DrawPanel.listPoly) DrawPanel.listPoly=new java.util.Vector<Shape3D>();
        DrawPanel.poly = new ElementPoint(cadre);
    }

    private void endLine(Stack<String> param) {
        DrawPanel.record3D = DrawPanel.RECORD_3D_NONE;
        try {
            DrawPanel.poly.addToScene();
        } catch (LogoException e) {
        }
    }

    private void startLine(Stack<String> param) {
        DrawPanel.record3D = DrawPanel.RECORD_3D_LINE;
        cadre.initViewer3D();
//                    	if (null==DrawPanel.listPoly) DrawPanel.listPoly=new java.util.Vector<Shape3D>();
        DrawPanel.poly = new ElementLine(cadre);
        DrawPanel.poly.addVertex(new Point3d(kernel.getActiveTurtle().X / 1000,
                kernel.getActiveTurtle().Y / 1000,
                kernel.getActiveTurtle().Z / 1000), kernel.getActiveTurtle().penColor);
    }

    private void view3D(Stack<String> param) {
        try {
            primitive3D("3d.polyview");
            cadre.viewerOpen();
        } catch (LogoException e) {
        }
    }

    private void endPolygon(Stack<String> param) {
        DrawPanel.record3D = DrawPanel.RECORD_3D_NONE;
        try {
            DrawPanel.poly.addToScene();
        } catch (LogoException e) {
        }
    }

    private void startPolygon(Stack<String> param) {
        DrawPanel.record3D = DrawPanel.RECORD_3D_POLYGON;
        cadre.initViewer3D();
//                    	if (null==DrawPanel.listPoly) DrawPanel.listPoly=new java.util.Vector<Shape3D>();
        DrawPanel.poly = new ElementPolygon(cadre);
    }

    private void listProperties(Stack<String> param) {
        String mot;
        try {
            Interpreter.operande = true;
            mot = getWord(param.get(0));
            if (null == mot)
                throw new LogoException(cadre, param.get(0) + " " + Logo.messages.getString("error.word"));
            Interpreter.calcul.push(wp.displayPropList(mot));
        } catch (LogoException e) {
        }
    }

    private void removeProperty(Stack<String> param) {
        String mot;
        String mot2;
        try {
            Interpreter.operande = false;
            mot = getWord(param.get(0));
            if (null == mot)
                throw new LogoException(cadre, param.get(0) + " " + Logo.messages.getString("error.word"));
            mot2 = getWord(param.get(1));
            if (null == mot2)
                throw new LogoException(cadre, param.get(1) + " " + Logo.messages.getString("error.word"));
            wp.removePropList(mot, mot2);
        } catch (LogoException e) {
        }
    }

    private void getProperty(Stack<String> param) {
        String mot;
        String mot2;
        try {
            Interpreter.operande = true;
            mot = getWord(param.get(0));
            if (null == mot)
                throw new LogoException(cadre, param.get(0) + " " + Logo.messages.getString("error.word"));
            mot2 = getWord(param.get(1));
            if (null == mot2)
                throw new LogoException(cadre, param.get(1) + " " + Logo.messages.getString("error.word"));
            String value = wp.getPropList(mot, mot2);
            if (value.startsWith("[")) value += " ";
            Interpreter.calcul.push(value);
        } catch (LogoException e) {
        }
    }

    private void setProperty(Stack<String> param) {
        String mot;
        String mot2;
        Interpreter.operande = false;
        try {
            mot = getWord(param.get(0));
            if (null == mot)
                throw new LogoException(cadre, param.get(0) + " " + Logo.messages.getString("error.word"));
            mot2 = getWord(param.get(1));
            if (null == mot2)
                throw new LogoException(cadre, param.get(1) + " " + Logo.messages.getString("error.word"));
            wp.addPropList(mot, mot2, param.get(2));
        } catch (LogoException e) {
        }
    }

    private void setZ(Stack<String> param) {
        delay();
        try {
            primitive3D("3d.setz");
            cadre.getDrawPanel().setPosition(kernel.getActiveTurtle().X + " " + kernel.getActiveTurtle().Y
                    + " " + kernel.getCalculator().numberDouble(param.get(0)));

        } catch (LogoException e) {
        }
    }

    private void setXYZ(Stack<String> param) {
        try {
            primitive3D("3d.setxyz");
            cadre.getDrawPanel().setPosition(kernel.getCalculator().numberDouble(param.get(0)) + " "
                    + kernel.getCalculator().numberDouble(param.get(1)) + " " +
                    kernel.getCalculator().numberDouble(param.get(2)));
        } catch (LogoException e) {
        }
    }

    private void getOrientation(Stack<String> param) {
        try {
            primitive3D("3d.orientation");
            Interpreter.operande = true;
            String pitch = Calculator.teste_fin_double(kernel.getActiveTurtle().pitch);
            String roll = Calculator.teste_fin_double(kernel.getActiveTurtle().roll);
            String heading = Calculator.teste_fin_double(kernel.getActiveTurtle().heading);
            Interpreter.calcul.push("[ " + roll + " " + pitch + " " + heading + " ] ");
        } catch (LogoException e) {
        }
    }

    private void setOrientation(Stack<String> param) {
        try {
            primitive3D("3d.setorientation");
            delay();
            cadre.getDrawPanel().setOrientation(getFinalList(param.pop()));
        } catch (LogoException e) {
        }
    }

    private void setPitch(Stack<String> param) {
        try {
            primitive3D("3d.setpitch");
            delay();
            cadre.getDrawPanel().setPitch(kernel.getCalculator().numberDouble(param.pop()));
        } catch (LogoException e) {
        }
    }

    private void setRoll(Stack<String> param) {
        try {
            primitive3D("3d.setroll");
            delay();
            cadre.getDrawPanel().setRoll(kernel.getCalculator().numberDouble(param.pop()));
        } catch (LogoException e) {
        }
    }

    private void getPitch(Stack<String> param) {
        try {
            primitive3D("3d.pitch");
            Interpreter.operande = true;
            Interpreter.calcul.push(Calculator.teste_fin_double(kernel.getActiveTurtle().pitch));
        } catch (LogoException e) {
        }
    }

    private void getRoll(Stack<String> param) {
        try {
            primitive3D("3d.roll");
            Interpreter.operande = true;
            Interpreter.calcul.push(Calculator.teste_fin_double(kernel.getActiveTurtle().roll));
        } catch (LogoException e) {
        }
    }

    private void pitchDown(Stack<String> param) {
        delay();
        try {
            primitive3D("3d.downpitch");
            cadre.getDrawPanel().pitch(-kernel.getCalculator().numberDouble(param.pop()));
        } catch (LogoException e) {
        }
    }

    private void rollLeft(Stack<String> param) {
        delay();
        try {
            primitive3D("3d.leftroll");
            cadre.getDrawPanel().roll(-kernel.getCalculator().numberDouble(param.pop()));
        } catch (LogoException e) {
        }
    }

    private void pitchUp(Stack<String> param) {
        delay();
        try {
            primitive3D("3d.uppitch");
            cadre.getDrawPanel().pitch(kernel.getCalculator().numberDouble(param.pop()));
        } catch (LogoException e) {
        }
    }

    private void rollRight(Stack<String> param) {
        delay();
        try {
            primitive3D("3d.rightroll");
            cadre.getDrawPanel().roll(kernel.getCalculator().numberDouble(param.pop()));
        } catch (LogoException e) {
        }
    }

    private void enable3D(Stack<String> param) {
        cadre.getDrawPanel().perspective();
    }

    private void setAxisColor(Stack<String> param) {
        Interpreter.operande = false;
        try {
            if (isList(param.get(0))) {
                Logo.config.setAxisColor(rgb(param.get(0), Utils.primitiveName("setaxiscolor")).getRGB());
            } else {
                int coul = kernel.getCalculator().getInteger(param.get(0)) % DrawPanel.defaultColors.length;
                if (coul < 0) coul += DrawPanel.defaultColors.length;
                Logo.config.setAxisColor(DrawPanel.defaultColors[coul].getRGB());
            }
        } catch (LogoException e) {
        }
    }

    private void setGridColor(Stack<String> param) {
        Interpreter.operande = false;
        try {
            if (isList(param.get(0))) {
                Logo.config.setGridColor(rgb(param.get(0), Utils.primitiveName("setgridcolor")).getRGB());
            } else {
                int coul = kernel.getCalculator().getInteger(param.get(0)) % DrawPanel.defaultColors.length;
                if (coul < 0) coul += DrawPanel.defaultColors.length;
                Logo.config.setGridColor(DrawPanel.defaultColors[coul].getRGB());
            }
        } catch (LogoException e) {
        }
    }

    private void isYAxisEnabled(Stack<String> param) {
        Interpreter.operande = true;
        if (Logo.config.isYAxisEnabled())
            Interpreter.calcul.push(Logo.messages.getString("vrai"));
        else
            Interpreter.calcul.push(Logo.messages.getString("faux"));
    }

    private void isXAxisEnabled(Stack<String> param) {
        Interpreter.operande = true;
        if (Logo.config.isXAxisEnabled())
            Interpreter.calcul.push(Logo.messages.getString("vrai"));
        else
            Interpreter.calcul.push(Logo.messages.getString("faux"));
    }

    private void isGridEnabled(Stack<String> param) {
        Interpreter.operande = true;
        if (Logo.config.isGridEnabled())
            Interpreter.calcul.push(Logo.messages.getString("vrai"));
        else
            Interpreter.calcul.push(Logo.messages.getString("faux"));
    }

    private void getGridColor(Stack<String> param) {
        Color c;
        Interpreter.operande = true;
        c = new Color(Logo.config.getGridColor());
        Interpreter.calcul.push("[ " + c.getRed() + " " + c.getGreen()
                + " " + c.getBlue() + " ] ");
    }

    private void getAxisColor(Stack<String> param) {
        Color c;
        Interpreter.operande = true;
        c = new Color(Logo.config.getAxisColor());
        Interpreter.calcul.push("[ " + c.getRed() + " " + c.getGreen()
                + " " + c.getBlue() + " ] ");
    }

    private void isVariable(Stack<String> param) {
        String mot;
        try {
            Interpreter.operande = true;
            mot = getWord(param.get(0));
            if (null == mot)
                throw new LogoException(cadre, param.get(0) + " "
                        + Logo.messages.getString("error.word"));
            mot = mot.toLowerCase();
            if (wp.globals.containsKey(mot) || Interpreter.locale.containsKey(mot))
                Interpreter.calcul.push(Logo.messages.getString("vrai"));
            else
                Interpreter.calcul.push(Logo.messages.getString("faux"));
        } catch (LogoException e) {
        }
    }

    private void bye(Stack<String> param) {
        cadre.closeWindow();
    }

    private void disableAxes(Stack<String> param) {
        Logo.config.setXAxisEnabled(false);
        Logo.config.setYAxisEnabled(false);
        Interpreter.operande = false;
        clearScreen();
    }

    private void drawYAxis(Stack<String> param) {
        Interpreter.operande = false;
        try {
            primitive2D("yaxis");
            int nombre = kernel.getCalculator().getInteger(param.get(0));
            if (nombre < 0) {
                String name = Utils.primitiveName("yaxis");
                throw new LogoException(cadre, name + " "
                        + Logo.messages.getString("attend_positif"));
            } else if (nombre < 25) nombre = 25;
            Logo.config.setYAxisEnabled(true);
            Logo.config.setYAxisSpacing(nombre);
            clearScreen();
        } catch (LogoException e) {
        }
    }

    private void drawXAxis(Stack<String> param) {
        Interpreter.operande = false;
        try {
            primitive2D("xaxis");
            int nombre = kernel.getCalculator().getInteger(param.get(0));
            if (nombre < 0) {
                String name = Utils.primitiveName("xaxis");
                throw new LogoException(cadre, name + " "
                        + Logo.messages.getString("attend_positif"));
            } else if (nombre < 25) nombre = 25;
            Logo.config.setXAxisEnabled(true);
            Logo.config.setXAxisSpacing(nombre);
            clearScreen();
        } catch (LogoException e) {
        }
    }

    private void drawBothAxes(Stack<String> param) {
        Interpreter.operande = false;
        try {
            primitive2D("axis");
            int nombre = kernel.getCalculator().getInteger(param.get(0));
            if (nombre < 0) {
                String name = Utils.primitiveName("axis");
                throw new LogoException(cadre, name + " "
                        + Logo.messages.getString("attend_positif"));
            } else if (nombre < 25) nombre = 25;
            Logo.config.setXAxisEnabled(true);
            Logo.config.setXAxisSpacing(nombre);
            Logo.config.setYAxisEnabled(true);
            Logo.config.setYAxisSpacing(nombre);
            clearScreen();
        } catch (LogoException e) {
        }
    }

    private void guiMenu(Stack<String> param) {
        String liste;
        try {
            String ident = getWord(param.get(0));
            if (null == ident)
                throw new LogoException(cadre, param.get(0) + " "
                        + Logo.messages.getString("error.word"));
            liste = getFinalList(param.get(1));
            GuiMenu gm = new GuiMenu(ident.toLowerCase(), liste, cadre);
            cadre.getDrawPanel().addToGuiMap(gm);
        } catch (LogoException e) {
        }
    }

    private void stopTrace(Stack<String> param) {
        Kernel.mode_trace = false;
        Interpreter.operande = false;
    }

    private void stopAnimation(Stack<String> param) {
        cadre.getDrawPanel().setAnimation(false);
        Interpreter.operande = false;
    }

    private void disableGrid(Stack<String> param) {
        Interpreter.operande = false;
        Logo.config.setGridEnabled(false);
        clearScreen();
    }

    private void drawGrid(Stack<String> param) {
        Interpreter.operande = false;
        try {
            primitive2D("grille");
            int[] args = new int[2];
            for (int i = 0; i < 2; i++) {
                args[i] = kernel.getCalculator().getInteger(param.get(i));
                if (args[i] < 0) {
                    String grille = Utils.primitiveName("grille");
                    throw new LogoException(cadre, grille + " "
                            + Logo.messages.getString("attend_positif"));
                } else if (args[i] == 0) {
                    args[i] = 1;
                }
            }
            Logo.config.setGridEnabled(true);
            Logo.config.setXGridSpacing(args[0]);
            Logo.config.setYGridSpacing(args[1]);
            clearScreen();

        } catch (LogoException e) {
        }
    }

    private void setZoom(Stack<String> param) {
        double d;
        Interpreter.operande = false;
        try {
            d = kernel.getCalculator().numberDouble(param.get(0));
            if (d <= 0) {
                String name = Utils.primitiveName("zoom");
                throw new LogoException(cadre, name + " "
                        + Logo.messages.getString("attend_positif"));
            }
            cadre.getDrawPanel().zoom(d, false);
        } catch (LogoException e) {
        }
    }

    private void guiDraw(Stack<String> param) {
        try {
            String ident = getWord(param.get(0));
            if (null == ident)
                throw new LogoException(cadre, param.get(0) + " "
                        + Logo.messages.getString("error.word"));
            cadre.getDrawPanel().guiDraw(ident);
        } catch (LogoException e) {
        }
    }

    private void guiPosition(Stack<String> param) {
        String liste;
        try {
            String ident = getWord(param.get(0));
            if (null == ident)
                throw new LogoException(cadre, param.get(0) + " "
                        + Logo.messages.getString("error.word"));
            liste = getFinalList(param.get(1));
            cadre.getDrawPanel().guiposition(ident, liste, Utils.primitiveName("guiposition"));
        } catch (LogoException e) {
        }
    }

    private void guiRemove(Stack<String> param) {
        try {
            String ident = getWord(param.get(0));
            if (null == ident)
                throw new LogoException(cadre, param.get(0) + " "
                        + Logo.messages.getString("error.word"));
            cadre.getDrawPanel().guiRemove(ident);
        } catch (LogoException e) {
        }
    }

    private void guiAction(Stack<String> param) {
        String liste;
        try {
            String ident = getWord(param.get(0));
            if (null == ident)
                throw new LogoException(cadre, param.get(0) + " "
                        + Logo.messages.getString("error.word"));
            liste = getFinalList(param.get(1));
            cadre.getDrawPanel().guiAction(ident, liste);
        } catch (LogoException e) {
        }
    }

    private void guiButton(Stack<String> param) {
        String mot;
        try {
            String ident = getWord(param.get(0));
            if (null == ident)
                throw new LogoException(cadre, param.get(0) + " "
                        + Logo.messages.getString("error.word"));
            mot = getWord(param.get(1));
            if (null == mot)
                throw new LogoException(cadre, param.get(1) + " "
                        + Logo.messages.getString("error.word"));
            GuiButton gb = new GuiButton(ident.toLowerCase(), mot, cadre);
            cadre.getDrawPanel().addToGuiMap(gb);
        } catch (LogoException e) {
        }
    }

    private void setScreenSize(Stack<String> param) {
        String liste;
        Interpreter.operande = false;
        try {
            String prim = Utils.primitiveName("setscreensize");
            liste = getFinalList(param.get(0));
            int width, height;
            StringTokenizer st = new StringTokenizer(liste);
            try {
                if (!st.hasMoreTokens())
                    throw new LogoException(cadre, prim
                            + " " + Logo.messages.getString("n_aime_pas") + liste
                            + Logo.messages.getString("comme_parametre"));
                width = Integer.parseInt(st.nextToken());
                if (!st.hasMoreTokens())
                    throw new LogoException(cadre, prim
                            + " " + Logo.messages.getString("n_aime_pas") + liste
                            + Logo.messages.getString("comme_parametre"));
                height = Integer.parseInt(st.nextToken());
            } catch (NumberFormatException e) {
                throw new LogoException(cadre, prim
                        + " " + Logo.messages.getString("n_aime_pas") + liste
                        + Logo.messages.getString("comme_parametre"));
            }
            if (st.hasMoreTokens())
                throw new LogoException(cadre, prim
                        + " " + Logo.messages.getString("n_aime_pas") + liste
                        + Logo.messages.getString("comme_parametre"));
            boolean changement = height != Logo.config.getImageHeight();
            int tmp_hauteur = Logo.config.getImageHeight();
            Logo.config.setImageHeight(height);
            if (width != Logo.config.getImageWidth()) changement = true;
            int tmp_largeur = Logo.config.getImageWidth();
            Logo.config.setImageWidth(width);
            if (Logo.config.getImageWidth() < 100 || Logo.config.getImageHeight() < 100) {
                Logo.config.setImageWidth(400);
                Logo.config.setImageHeight(400);
            }
            if (changement) {
                cadre.resizeDrawingZone();
            }
        } catch (LogoException e) {
        }
    }

    private void getTurtlesMax(Stack<String> param) {
        Interpreter.operande = true;
        Interpreter.calcul.push(String.valueOf(Logo.config.getMaxTurtles()));
    }

    private void setTurtlesMax(Stack<String> param) {
        Interpreter.operande = false;
        try {
            int i = kernel.getCalculator().getInteger(param.get(0));
            if (i < 0) {
                String fmt = Utils.primitiveName("setturtlesnumber");
                throw new LogoException(cadre, fmt + " "
                        + Logo.messages.getString("attend_positif"));
            } else if (i == 0) i = 1;
            kernel.setNumberOfTurtles(i);
        } catch (LogoException e) {
        }
    }

    private void getDrawingQuality(Stack<String> param) {
        Interpreter.operande = true;
        Interpreter.calcul.push(String.valueOf(Logo.config.getDrawQuality()));
    }

    private void setDrawingQuality(Stack<String> param) {
        Interpreter.operande = false;
        try {
            int i = kernel.getCalculator().getInteger(param.get(0));
            if (i != Config.DRAW_QUALITY_NORMAL && i != Config.DRAW_QUALITY_HIGH && i != Config.DRAW_QUALITY_LOW) {
                String st = Utils.primitiveName("setdrawingquality") + " " + Logo.messages.getString("error_bad_values") + " 0 1 2";
                throw new LogoException(cadre, st);
            }
            Logo.config.setDrawQuality(i);
            kernel.setDrawingQuality(Logo.config.getDrawQuality());
        } catch (LogoException e) {
        }
    }

    private void getPenShape(Stack<String> param) {
        Interpreter.operande = true;
        Interpreter.calcul.push(String.valueOf(Logo.config.getPenShape()));
    }

    private void setPenShape(Stack<String> param) {
        Interpreter.operande = false;
        try {
            int i = kernel.getCalculator().getInteger(param.get(0));
            if (i != Config.PEN_SHAPE_OVAL && i != Config.PEN_SHAPE_SQUARE) {
                String st = Utils.primitiveName("setpenshape") + " " + Logo.messages.getString("error_bad_values");
                st += " " + Config.PEN_SHAPE_SQUARE + " " + Config.PEN_SHAPE_OVAL;
                throw new LogoException(cadre, st);
            }
            Logo.config.setPenShape(i);
            cadre.getDrawPanel().updateAllTurtleShape();
            cadre.getDrawPanel().setStroke(kernel.getActiveTurtle().crayon);
        } catch (LogoException e) {
        }
    }

    private void getPenWidth(Stack<String> param) {
        Interpreter.operande = true;
        double penwidth = 2 * kernel.getActiveTurtle().getPenWidth();
        Interpreter.calcul.push(Calculator.teste_fin_double(penwidth));
    }

    private void resetAll(Stack<String> param) {
        Interpreter.operande = false;
        // resize drawing zone if necessary
        if (Logo.config.getImageHeight() != 1000 || Logo.config.getImageWidth() != 1000) {
            Logo.config.setImageHeight(1000);
            Logo.config.setImageWidth(1000);
            cadre.resizeDrawingZone();
        }
        Logo.config.setGridEnabled(false);
        Logo.config.setXAxisEnabled(false);
        Logo.config.setYAxisEnabled(false);
        cadre.getDrawPanel().home();
        cadre.getDrawPanel().resetScreenColor();
        if (kernel.getActiveTurtle().id == 0) {
            Logo.config.setActiveTurtle(0);
        }
        DrawPanel.windowMode = DrawPanel.WINDOW_CLASSIC;
        kernel.change_image_tortue(0);
        cadre.getDrawPanel().setScreenColor(Color.WHITE);
        cadre.getDrawPanel().setPenColor(Color.BLACK);
        cadre.getDrawPanel().setAnimation(false);
        Logo.config.setFont(new Font("dialog", Font.PLAIN, 12));
        kernel.getActiveTurtle().police = 12;
        cadre.getDrawPanel().setGraphicsFont(Logo.config.getFont());
        HistoryPanel.printFontId = Application.getFontId(Logo.config.getFont());
        cadre.getHistoryPanel().setFontSize(12);
        cadre.getHistoryPanel().setFontNumber(HistoryPanel.printFontId);
        cadre.getHistoryPanel().setTextColor(Color.black);
        Logo.config.setPenShape(0);
        Logo.config.setDrawQuality(0);
        kernel.setDrawingQuality(Logo.config.getDrawQuality());
        kernel.setNumberOfTurtles(16);
        Logo.config.setTurtleSpeed(0);
        Kernel.mode_trace = false;
        DrawPanel.windowMode = DrawPanel.WINDOW_CLASSIC;
        cadre.getDrawPanel().zoom(1, false);
    }

    private void chatTcp(Stack<String> param) {
        String mot;
        String liste;
        Interpreter.operande = false;
        mot = getWord(param.get(0));
        if (null == mot) {
            try {
                throw new LogoException(cadre, param.get(0) + " "
                        + Logo.messages.getString("error.word"));
            } catch (LogoException e) {
            }
        }
        mot = mot.toLowerCase();
        liste = "";
        try {
            liste = getFinalList(param.get(1));
            new NetworkClientChat(cadre, mot, liste);
        } catch (LogoException e) {
        }
    }

    private void endExecuteTcp(Stack<String> param) {
        // \x internal operator to specify
        // the end of network instructions with
        // "executetcp"
        // have to replace workspace
        Interpreter.operande = false;
        kernel.setWorkspace(savedWorkspace.pop());
    }

    private void executeTcp(Stack<String> param) {
        String mot;
        String liste;
        mot = getWord(param.get(0));
        if (null == mot) {
            try {
                throw new LogoException(cadre, param.get(0) + " "
                        + Logo.messages.getString("error.word"));
            } catch (LogoException e) {
            }
        }
        mot = mot.toLowerCase();
        liste = "";
        try {
            liste = getFinalList(param.get(1));
            new NetworkClientExecute(cadre, mot, liste);
        } catch (LogoException e) {
        }
    }

    private void listenTcp(Stack<String> param) {
        Interpreter.operande = false;
        if (null == savedWorkspace) savedWorkspace = new Stack<Workspace>();
        savedWorkspace.push(wp);
        new NetworkServer(cadre);
    }

    private void sendTcp(Stack<String> param) {
        String liste;
        String mot;
        Interpreter.operande = true;
        mot = getWord(param.get(0));
        if (null == mot) {
            try {
                throw new LogoException(cadre, param.get(0) + " "
                        + Logo.messages.getString("error.word"));
            } catch (LogoException e) {
            }
        }
        mot = mot.toLowerCase();
        liste = "";
        try {
            liste = getFinalList(param.get(1));
            NetworkClientSend ncs = new NetworkClientSend(cadre, mot, liste);
            Interpreter.calcul.push("[ " + ncs.getAnswer() + " ] ");
        } catch (LogoException e) {
        }
        /*
         * try{
         *
         * liste = "[ "; mot2 = getFinalList(param.get(0).toString()); liste += mot2 + "
         * ]"; String rip = liste.substring(2,17); // cadre.updateHistory("perso", rip + "\n");
         * //para debug String rdat = "_" + liste.substring(18,23) + "*\n\r"; //
         * cadre.updateHistory("perso", rdat + "\n"); //para debug Socket echoSocket = null;
         * DataOutputStream tcpout = null; BufferedReader tcpin = null; String resp =
         * null; try { echoSocket = new Socket(rip, 1948); tcpout = new
         * DataOutputStream(echoSocket.getOutputStream()); tcpin= new BufferedReader(new
         * InputStreamReader(echoSocket.getInputStream())); tcpout.writeBytes(rdat);
         * resp = tcpin.readLine(); // readLine detiene el programa hasta que recibe una
         * respuesta del robot. Que hacer si no recibe nada? tcpout.close();
         * tcpin.close(); echoSocket.close(); } catch (UnknownHostException e) { throw
         * new LogoException(cadre, Logo.messages.getString("erreur_tcp")); } catch
         * (IOException e) { throw new LogoException(cadre,
         * Logo.messages.getString("erreur_tcp")); } Interpreter.calcul.push("[ " + resp + "
         * ]"); } catch(LogoException e){}
         */
    }

    private void getLabelLength(Stack<String> param) {
        String mot;
        try {
            mot = getWord(param.get(0));
            if (null != mot) mot = Utils.unescapeString(mot);
            else mot = getFinalList(param.get(0)).trim();
            Interpreter.operande = true;
            FontMetrics fm = cadre.getDrawPanel().getGraphics()
                    .getFontMetrics(cadre.getDrawPanel().getGraphicsFont());
            int longueur = fm.stringWidth(mot);
            Interpreter.calcul.push(String.valueOf(longueur));
        } catch (LogoException e) {
        }
    }

    private void getZoneSize(Stack<String> param) {
        StringBuffer sb;
        Interpreter.operande = true;
        Point p = cadre.scrollPane.getViewport().getViewPosition();
        Rectangle rec = cadre.scrollPane.getVisibleRect();
        sb = new StringBuffer();
        int x1 = p.x - Logo.config.getImageWidth() / 2;
        int y1 = Logo.config.getImageHeight() / 2 - p.y;
        int x2 = x1 + rec.width - cadre.scrollPane.getVerticalScrollBar().getWidth();
        int y2 = y1 - rec.height + cadre.scrollPane.getHorizontalScrollBar().getHeight();
        sb.append("[ ");
        sb.append(x1);
        sb.append(" ");
        sb.append(y1);
        sb.append(" ");
        sb.append(x2);
        sb.append(" ");
        sb.append(y2);
        sb.append(" ] ");
        Interpreter.calcul.push(new String(sb));
    }

    private void getTextStyle(Stack<String> param) {
        StringBuffer buffer = new StringBuffer();
        int compteur = 0;
        if (cadre.getHistoryPanel().isBold()) {
            buffer.append(Logo.messages.getString("style.bold").toLowerCase() + " ");
            compteur++;
        }
        if (cadre.getHistoryPanel().isItalic()) {
            buffer.append(Logo.messages.getString("style.italic").toLowerCase() + " ");
            compteur++;
        }
        if (cadre.getHistoryPanel().isUnderline()) {
            buffer.append(Logo.messages.getString("style.underline").toLowerCase() + " ");
            compteur++;
        }
        if (cadre.getHistoryPanel().isSuperscript()) {
            buffer.append(Logo.messages.getString("style.exposant").toLowerCase() + " ");
            compteur++;
        }
        if (cadre.getHistoryPanel().isSubscript()) {
            buffer.append(Logo.messages.getString("style.subscript").toLowerCase() + " ");
            compteur++;
        }
        if (cadre.getHistoryPanel().isStrikethrough()) {
            buffer.append(Logo.messages.getString("style.strike").toLowerCase() + " ");
            compteur++;
        }
        Interpreter.operande = true;
        if (compteur == 0)
            Interpreter.calcul.push("\"" + Logo.messages.getString("style.none").toLowerCase());
        else if (compteur == 1) Interpreter.calcul.push("\"" + new String(buffer).trim());
        else if (compteur > 1) Interpreter.calcul.push("[ " + new String(buffer) + "]");
    }

    private void setTextStyle(Stack<String> param) {
        String liste;
        String mot;
        try {
            boolean gras = false;
            boolean italique = false;
            boolean souligne = false;
            boolean exposant = false;
            boolean indice = false;
            boolean barre = false;
            mot = getWord(param.get(0));
            if (null == mot) liste = getFinalList(param.get(0));
            else liste = mot;
            if (liste.trim().equals("")) liste = Logo.messages.getString("style.none");
            StringTokenizer st = new StringTokenizer(liste);
            while (st.hasMoreTokens()) {
                String element = st.nextToken().toLowerCase();
                if (element.equals(Logo.messages.getString("style.underline").toLowerCase())) {
                    souligne = true;
                } else if (element.equals(Logo.messages.getString("style.bold").toLowerCase())) {
                    gras = true;
                } else if (element.equals(Logo.messages.getString("style.italic").toLowerCase())) {
                    italique = true;
                } else if (element.equals(Logo.messages.getString("style.exposant").toLowerCase())) {
                    exposant = true;
                } else if (element.equals(Logo.messages.getString("style.subscript").toLowerCase())) {
                    indice = true;
                } else if (element.equals(Logo.messages.getString("style.strike").toLowerCase())) {
                    barre = true;
                } else if (element.equals(Logo.messages.getString("style.none").toLowerCase())) {
                } else throw new LogoException(cadre, Logo.messages.getString("erreur_fixestyle"));
            }
            cadre.getHistoryPanel().setBold(gras);
            cadre.getHistoryPanel().setItalic(italique);
            cadre.getHistoryPanel().setUnderline(souligne);
            cadre.getHistoryPanel().setSuperscript(exposant);
            cadre.getHistoryPanel().setSubscript(indice);
            cadre.getHistoryPanel().setStrikeThrough(barre);
        } catch (LogoException e) {
        }
    }

    private void closeParen(Stack<String> param) {
        // Distinguons les deux cas : (3)*2 et (4+3)*2
        // Le 3 est ici a retourner au +
        boolean a_retourner = true;
        // On enleve le "(" correspondant a la parenthese ouvrante de la
        // pile nom
        // a condition que l'element attendant de la pile nom ne soit
        // pas une procedure
        boolean est_procedure = false;
        int pos = Interpreter.nom.lastIndexOf("(");
        if (pos == -1) {
            // Parenthese fermante sans parenthese ouvrante au prealable
            try {
                throw new LogoException(cadre, Logo.messages.getString("parenthese_ouvrante"));
            } catch (LogoException e) {
            }
        } else { // Evitons l'erreur en cas de par exemple: "ec )"
            // (parenthese fermante sans ouvrante)--> else a
            // executer qu'en cas de non erreur
            if (Interpreter.drapeau_ouvrante) {
                // parenthese vide
                try {
                    throw new LogoException(cadre, Logo.messages.getString("parenthese_vide"));
                } catch (LogoException e) {
                }

            }
            for (int j = pos; j < Interpreter.nom.size(); j++) {
                String proc = Interpreter.nom.get(j).toLowerCase();
                if (primitiveMap.containsKey(proc)) est_procedure = true;
                else {
                    for (int i = 0; i < wp.getNumberOfProcedure(); i++) {
                        if (wp.getProcedure(i).name.equals(proc)) {
                            est_procedure = true;
                            break;
                        }
                    }
                    if (est_procedure) break;
                }
            }
        }
        // Si une procedure est presente dans la pile nom, on garde les
        // parenteses
// System.out.println(primitiveMap.containsKey("puissance")+"
// "+est_procedure);
        if (est_procedure) {
            cadre.getKernel().getInstructionBuffer().insert(") ");
        }
        // Sinon on les enleve avec leurs imbrications eventuelles
        else {
            if (Interpreter.en_cours.isEmpty() || !Interpreter.en_cours.peek().equals("(")) {
                try {
                    throw new LogoException(cadre, Logo.messages.getString("parenthese_ouvrante"));
                } catch (LogoException e) {
                }
            } else Interpreter.en_cours.pop();
            if (!Interpreter.nom.isEmpty()) {
                if (Interpreter.nom.peek().equals("(")) a_retourner = false;
                pos = Interpreter.nom.lastIndexOf("(");
                if (pos == -1) {
                    // Parenthese fermante sans parenthese ouvrante
                    // au prelable
                    try {
                        throw new LogoException(cadre, Logo.messages.getString("parenthese_ouvrante"));
                    } catch (LogoException e) {
                    }
                } else {
                    Interpreter.nom.removeElementAt(pos);
                    // S'il y a imbrication de parentheses (((20)))
                    pos--;
                    InstructionBuffer instruction = cadre.getKernel().getInstructionBuffer();
                    while (instruction.getNextWord().equals(")") && (pos > -1)) {
                        if (!Interpreter.nom.isEmpty() && Interpreter.nom.get(pos).equals("(")) {
                            instruction.deleteFirstWord(")");
                            Interpreter.nom.removeElementAt(pos);
                            pos--;
                        } else break;
                    }
                }
            }
        }
        if (Interpreter.calcul.isEmpty()) {
            Interpreter.operande = false;
        } else {
            Interpreter.operande = true;
            Interpreter.drapeau_fermante = a_retourner;
        }
    }

    private void circle(Stack<String> param) {
        try {
            cadre.getDrawPanel().circle((kernel.getCalculator().numberDouble(param.pop())));
        } catch (LogoException e) {
        }
    }

    private void setColorWhite(Stack<String> param) {
        colorCode(7);
    }

    private void setColorCyan(Stack<String> param) {
        colorCode(6);
    }

    private void setColorMagenta(Stack<String> param) {
        colorCode(5);
    }

    private void setColorBlue(Stack<String> param) {
        colorCode(4);
    }

    private void setColorYellow(Stack<String> param) {
        colorCode(3);
    }

    private void setColorGreen(Stack<String> param) {
        colorCode(2);
    }

    private void setColorRed(Stack<String> param) {
        colorCode(1);
    }

    private void setColorBlack(Stack<String> param) {
        colorCode(0);
    }

    private void setColorBrown(Stack<String> param) {
        colorCode(16);
    }

    private void setColorPurple(Stack<String> param) {
        colorCode(15);
    }

    private void setColorPink(Stack<String> param) {
        colorCode(14);
    }

    private void setColorOrange(Stack<String> param) {
        colorCode(13);
    }

    private void setColorDarkBlue(Stack<String> param) {
        colorCode(12);
    }

    private void setColorDarkGreen(Stack<String> param) {
        colorCode(11);
    }

    private void setColorDarkRed(Stack<String> param) {
        colorCode(10);
    }

    private void setColorLightGray(Stack<String> param) {
        colorCode(9);
    }

    private void setColorGray(Stack<String> param) {
        colorCode(8);
    }

    private void addItem(Stack<String> param) {
        String mot;
        String liste;
        try {
            String reponse = "";
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
                throw new LogoException(cadre, Logo.messages.getString("y_a_pas")
                        + " " + entier + " "
                        + Logo.messages.getString("element_dans_liste") + liste
                        + "]");
            if (!liste.trim().equals(""))
                reponse = "[ " + liste.substring(0, compteur) + mot + " " + liste.substring(compteur) + "] ";
            else reponse = "[ " + mot + " ] ";
            Interpreter.operande = true;
            Interpreter.calcul.push(reponse);
        } catch (LogoException e) {
        }
    }

    private void replaceItem(Stack<String> param) {
        String mot;
        String liste;
        try {
            String reponse = "";
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
                throw new LogoException(cadre, Logo.messages.getString("y_a_pas")
                        + " " + entier + " "
                        + Logo.messages.getString("element_dans_liste") + liste
                        + "]");
            reponse = "[ " + liste.substring(0, compteur) + mot;
            // On extrait le mot suivant
            if (compteur + 1 < liste.length() && liste.charAt(compteur) == '[' && liste.charAt(compteur + 1) == ' ') {
                compteur = extractList(liste, compteur + 2);
                reponse += liste.substring(compteur) + "] ";

            } else {
                for (int i = compteur + 1; i < liste.length(); i++) {
                    if (liste.charAt(i) == ' ') {
                        compteur = i;
                        break;
                    }
                }
                reponse += liste.substring(compteur) + "] ";
            }
            Interpreter.operande = true;
            Interpreter.calcul.push(reponse);
        } catch (LogoException e) {
        }
    }

    private void abs(Stack<String> param) {
        try {
            Interpreter.operande = true;
            Interpreter.calcul.push(kernel.getCalculator().abs(param.get(0)));
        } catch (LogoException e) {
        }
    }

    private void _for(Stack<String> param) {
        try {
            String li2 = getList(param.get(1));
            li2 = new String(Utils.formatCode(li2));
            String li1 = getFinalList(param.get(0));
            int nb = numberOfElements(li1);
            if (nb < 3 || nb > 4)
                throw new LogoException(cadre, Logo.messages.getString("erreur_repetepour"));
            StringTokenizer st = new StringTokenizer(li1);
            String var = st.nextToken().toLowerCase();
            BigDecimal deb = kernel.getCalculator().numberDecimal(st.nextToken());
            BigDecimal fin = kernel.getCalculator().numberDecimal(st.nextToken());
            BigDecimal increment = BigDecimal.ONE;
            if (nb == 4) increment = kernel.getCalculator().numberDecimal(st.nextToken());
            if (var.equals("")) throw new LogoException(cadre, Logo.messages.getString("variable_vide"));
            try {
                Double.parseDouble(var);
                throw new LogoException(cadre, Logo.messages.getString("erreur_nom_nombre_variable"));
            } catch (NumberFormatException e) {
                LoopFor bp = new LoopFor(deb, fin, increment, li2, var);
                bp.AffecteVar(true);

                if ((increment.compareTo(BigDecimal.ZERO) == 1 && fin.compareTo(deb) >= 0)
                        || (increment.compareTo(BigDecimal.ZERO) == -1 && fin.compareTo(deb) <= 0)) {
                    cadre.getKernel().getInstructionBuffer().insert(li2 + Interpreter.END_LOOP + " ");
                    stackLoop.push(bp);
                }
            }
        } catch (LogoException e) {
        }
    }

    private void getRepCount(Stack<String> param) {
        boolean erreur = false;
        if (!stackLoop.isEmpty()) {
            LoopProperties bp = stackLoop.peek();
            if (bp.isRepeat()) {
                Interpreter.operande = true;
                Interpreter.calcul.push(bp.getCounter().toString());
            } else erreur = true;
        } else erreur = true;
        if (erreur) {
            try {
                throw new LogoException(cadre, Logo.messages.getString("erreur_compteur"));
            } catch (LogoException e) {
            }
        }
    }

    private void stopAll(Stack<String> param) {
        cadre.error = true;
    }

    private void getCharacter(Stack<String> param) {
        try {
            int i = kernel.getCalculator().getInteger(param.get(0));
            if (i < 0 || i > 65535)
                throw new LogoException(cadre, param.get(0) + " " + Logo.messages.getString("nombre_unicode"));
            else {
                String st = "";
                Interpreter.operande = true;
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
                Interpreter.calcul.push(st);
            }
        } catch (LogoException e) {
        }
    }

    private void getUnicode(Stack<String> param) {
        String mot;
        try {
            mot = getWord(param.get(0));
            if (null == mot)
                throw new LogoException(cadre, param.get(0) + " "
                        + Logo.messages.getString("error.word"));
            else if (getWordLength(mot) != 1) throw new LogoException(cadre, param.get(0) + " "
                    + Logo.messages.getString("un_caractere"));
            else {
                Interpreter.operande = true;
                String st = String.valueOf((int) Utils.unescapeString(itemWord(1, mot)).charAt(0));
                Interpreter.calcul.push(st);
            }
        } catch (LogoException e) {
        }
    }

    private void changeDirectory(Stack<String> param) {
        String mot;
        Interpreter.operande = false;
        try {
            mot = getWord(param.get(0));
            if (null == mot)
                throw new LogoException(cadre, param.get(0) + " "
                        + Logo.messages.getString("error.word"));
            String chemin = "";
            if (Logo.config.getDefaultFolder().endsWith(File.separator))
                chemin = Utils.unescapeString(Logo.config.getDefaultFolder() + mot);
            else
                chemin = Utils.unescapeString(Logo.config.getDefaultFolder() + Utils.escapeString(File.separator) + mot);
            if ((new File(chemin)).isDirectory()) {
                try {
                    Logo.config.setDefaultFolder(Utils.escapeString((new File(chemin)).getCanonicalPath()));
                } catch (NullPointerException e1) {
                } catch (IOException e2) {
                }
            } else
                throw new LogoException(cadre, Utils.escapeString(chemin)
                        + " "
                        + Logo.messages
                        .getString("erreur_pas_repertoire"));
        } catch (LogoException e) {
        }
    }

    private void trace(Stack<String> param) {
        Kernel.mode_trace = true;
        Interpreter.operande = false;
    }

    private void truncate(Stack<String> param) {
        Interpreter.operande = true;
        try {
            Interpreter.calcul.push(kernel.getCalculator().truncate(param.get(0)));
        } catch (LogoException e) {
        }
    }

    private void getSeparation(Stack<String> param) {
        Interpreter.operande = true;
        Interpreter.calcul.push(Calculator.teste_fin_double(cadre.splitPane.getResizeWeight()));
    }

    private void setSeparation(Stack<String> param) {
        try {
            double nombre = kernel.getCalculator().numberDouble(param.get(0));
            if (nombre < 0 || nombre > 1)
                throw new LogoException(cadre, nombre + " " + Logo.messages.getString("entre_zero_un"));
            cadre.splitPane.setResizeWeight(nombre);
            cadre.splitPane.setDividerLocation(nombre);
        } catch (LogoException e) {
        }
    }

    private void isInteger(Stack<String> param) {
        Interpreter.operande = true;
        try {
            double ent = kernel.getCalculator().numberDouble(param.get(0));
            if ((int) ent == ent) Interpreter.calcul.push(Logo.messages.getString("vrai"));
            else Interpreter.calcul.push(Logo.messages.getString("faux"));
        } catch (LogoException e) {
        }
    }

    private void quotient(Stack<String> param) {
        try {
            Interpreter.operande = true;
            Interpreter.calcul.push(kernel.getCalculator().quotient(param.get(0), param.get(1)));
        } catch (LogoException e) {
        }
    }

    private void getImageSize(Stack<String> param) {
        Interpreter.operande = true;
        StringBuffer sb = new StringBuffer();
        sb.append("[ ");
        sb.append(Logo.config.getImageWidth());
        sb.append(" ");
        sb.append(Logo.config.getImageHeight());
        sb.append(" ] ");
        Interpreter.calcul.push(new String(sb));
    }

    private void refresh(Stack<String> param) {
        if (DrawPanel.classicMode == DrawPanel.MODE_ANIMATION) {
            cadre.getDrawPanel().refresh();
        }
    }

    private void startAnimation(Stack<String> param) {
        cadre.getDrawPanel().setAnimation(true);
        Interpreter.operande = false;
    }

    private void fillZone(Stack<String> param) {
        cadre.getDrawPanel().fillZone();
    }

    private void arc(Stack<String> param) {
        try {
            cadre.getDrawPanel().arc(kernel.getCalculator().numberDouble(param.get(0)), kernel.getCalculator().numberDouble(param.get(1)), kernel.getCalculator().numberDouble(param.get(2)));
        } catch (LogoException e) {
        }
    }

    private void write(Stack<String> param) {
        String mot;
        String par = param.get(0).trim();
        if (isList(par))
            par = formatList(par.substring(1, par.length() - 1));
        mot = getWord(param.get(0));
        if (null == mot)
            cadre.updateHistory("perso", Utils.unescapeString(par));
        else
            cadre.updateHistory("perso", Utils.unescapeString(mot));
    }

    private void wash(Stack<String> param) {
        cadre.getDrawPanel().wash();
    }

    private void getVariableValue(Stack<String> param) {
        String mot;
        mot = getWord(param.get(0));
        try {
            if (null == mot) {
                throw new LogoException(cadre, Logo.messages
                        .getString("error.word"));
            } // si c'est une liste
            else if (debut_chaine.equals("")) {
                throw new LogoException(cadre, Logo.messages
                        .getString("erreur_variable"));
            } // si c'est un nombre
            Interpreter.operande = true;
            String value;
            mot = mot.toLowerCase();
            if (!Interpreter.locale.containsKey(mot)) {
                if (!wp.globals.containsKey(mot))
                    throw new LogoException(cadre, mot
                            + " "
                            + Logo.messages
                            .getString("erreur_variable"));
                else
                    value = wp.globals.get(mot);
            } else {
                value = Interpreter.locale.get(mot);
            }
            if (null == value)
                throw new LogoException(cadre, mot + "  "
                        + Logo.messages.getString("erreur_variable"));
            Interpreter.calcul.push(value);
        } catch (LogoException e) {
        }
    }

    private void listVariables(Stack<String> param) {
        Interpreter.operande = true;
        Interpreter.calcul.push(new String(getAllVariables()));
    }

    private void isMouseEvent(Stack<String> param) {
        Interpreter.operande = true;
        if (cadre.getDrawPanel().get_lissouris())
            Interpreter.calcul.push(Logo.messages.getString("vrai"));
        else
            Interpreter.calcul.push(Logo.messages.getString("faux"));
    }

    private void clearText(Stack<String> param) {
        cadre.getHistoryPanel().clearText();
    }

    private void fileAppendLine(Stack<String> param) {
        String liste;
        try {
            int ident = kernel.getCalculator().getInteger(param.get(0));
            int index = kernel.flows.search(ident);
            liste = getFinalList(param.get(1));
            if (index == -1)
                throw new LogoException(cadre, Logo.messages
                        .getString("flux_non_disponible")
                        + " " + ident);
            Flow flow = kernel.flows.get(index);
            FlowWriter flowWriter;
            // If the flow is a readable flow, throw an error
            if (flow.isReader()) throw new LogoException(cadre, Logo.messages.getString("flux_ecriture"));
                // Else if the flow is a writable flow , convert to MrFlowWriter
            else if (flow.isWriter()) flowWriter = (FlowWriter) flow;
                // Else the flow isn't defined yet, initialize
            else flowWriter = new FlowWriter(flow);

            // Write the line
            flowWriter.append(Utils.unescapeString(liste));
            kernel.flows.set(index, flowWriter);
        } catch (FileNotFoundException e1) {
        } catch (IOException e2) {
        } catch (LogoException e) {
        }
    }

    private void closeFile(Stack<String> param) {
        try {
            int ident = kernel.getCalculator().getInteger(param.get(0));
            int index = kernel.flows.search(ident);
            if (index == -1)
                throw new LogoException(cadre, Logo.messages
                        .getString("flux_non_disponible")
                        + " " + ident);
            Flow flow = kernel.flows.get(index);
            // If the flow is a readable flow
            if (flow.isReader()) ((FlowReader) flow).close();
                // Else if it's a writable flow
            else if (flow.isWriter()) ((FlowWriter) flow).close();
            kernel.flows.remove(index);
        } catch (IOException e2) {
        } catch (LogoException e) {
        }
    }

    private void openFile(Stack<String> param) {
        String liste;
        String mot;
        try {
            mot = getWord(param.get(1));
            if (null == mot)
                throw new LogoException(cadre, param.get(0) + " "
                        + Logo.messages.getString("error.word"));
            liste = Utils.unescapeString(Logo.config.getDefaultFolder()) + File.separator + Utils.unescapeString(mot);
            int ident = kernel.getCalculator().getInteger(param.get(0));
            if (kernel.flows.search(ident) == -1)
                kernel.flows.add(new Flow(ident, liste, false));
            else
                throw new LogoException(cadre, ident + " "
                        + Logo.messages.getString("flux_existant"));
        } catch (LogoException e) {
        }
    }

    private void isEndOfFile(Stack<String> param) {
        try {
            int ident = kernel.getCalculator().getInteger(param.get(0));
            int index = kernel.flows.search(ident);
            if (index == -1)
                throw new LogoException(cadre, Logo.messages
                        .getString("flux_non_disponible")
                        + " " + ident);
            else {
                Flow flow = kernel.flows.get(index);
                FlowReader flowReader = null;
                // If the flow isn't defined yet, initialize
                if (!flow.isWriter() && !flow.isReader()) {
                    flowReader = new FlowReader(flow);
                } else if (flow.isReader())
                    flowReader = (FlowReader) flow;
                if (null != flowReader) {
                    if (flow.isFinished()) {
                        Interpreter.operande = true;
                        Interpreter.calcul.push(Logo.messages
                                .getString("vrai"));
                    } else {
                        int read = flowReader.isReadable();
                        if (read == -1) {
                            Interpreter.operande = true;
                            Interpreter.calcul.push(Logo.messages
                                    .getString("vrai"));
                            flow.setFinished(true);
                        } else {
                            Interpreter.operande = true;
                            Interpreter.calcul.push(Logo.messages
                                    .getString("faux"));
                        }
                    }
                } else throw new LogoException(cadre, Logo.messages
                        .getString("flux_lecture"));
            }
        } catch (FileNotFoundException e1) {
        } catch (IOException e2) {
        } catch (LogoException e) {
        }
    }

    private void fileWriteLine(Stack<String> param) {
        String liste;
        try {
            int ident = kernel.getCalculator().getInteger(param.get(0));
            int index = kernel.flows.search(ident);
            liste = getFinalList(param.get(1));
            if (index == -1)
                throw new LogoException(cadre, Logo.messages
                        .getString("flux_non_disponible")
                        + " " + ident);
            Flow flow = kernel.flows.get(index);
            FlowWriter flowWriter;
            // If the flow is a readable flow, throw an error
            if (flow.isReader()) throw new LogoException(cadre, Logo.messages.getString("flux_ecriture"));
                // Else if the flow is a writable flow , convert to MrFlowWriter
            else if (flow.isWriter()) flowWriter = (FlowWriter) flow;
                // Else the flow isn't defined yet, initialize
            else flowWriter = new FlowWriter(flow);

//					System.out.println(flow.isReader()+" "+flow.isWriter());
            // Write the line
            flowWriter.write(Utils.unescapeString(liste));
            kernel.flows.set(index, flowWriter);
        } catch (FileNotFoundException e1) {
        } catch (IOException e2) {
        } catch (LogoException e) {
        }
    }

    private void fileReadChar(Stack<String> param) {
        try {
            int ident = kernel.getCalculator().getInteger(param.get(0));
            int index = kernel.flows.search(ident);
            if (index == -1)
                throw new LogoException(cadre, Logo.messages
                        .getString("flux_non_disponible")
                        + " " + ident);
            Flow flow = kernel.flows.get(index);
            FlowReader flowReader;
            // If the flow is a writable flow, throw error
            if (flow.isWriter())
                throw new LogoException(cadre, Logo.messages
                        .getString("flux_lecture"));
                // else if the flow is reader, convert to FlowReader
            else if (flow.isReader()) {
                flowReader = ((FlowReader) flow);
            }
            // else the flow isn't yet defined, initialize
            else flowReader = new FlowReader(flow);

            if (flowReader.isFinished())
                throw new LogoException(cadre, Logo.messages.getString("fin_flux") + " " + ident);

            int character = ((FlowReader) flow).readChar();
            if (character == -1) {
                flow.setFinished(true);
                throw new LogoException(cadre, Logo.messages
                        .getString("fin_flux")
                        + " " + ident);
            }
            Interpreter.operande = true;
            String car = String.valueOf(character);
            if (car.equals("\\")) car = "\\\\";
            Interpreter.calcul.push(car);
            kernel.flows.set(index, flowReader);
        } catch (FileNotFoundException e1) {
            try {
                throw new LogoException(cadre, Logo.messages
                        .getString("error.iolecture"));
            } catch (LogoException e5) {
            }
        } catch (IOException e2) {
        } catch (LogoException e) {
        }
    }

    private void fileReadLine(Stack<String> param) {
        try {
            int ident = kernel.getCalculator().getInteger(param.get(0));
            int index = kernel.flows.search(ident);
            if (index == -1)
                throw new LogoException(cadre, Logo.messages
                        .getString("flux_non_disponible")
                        + " " + ident);
            Flow flow = kernel.flows.get(index);
            FlowReader flowReader;
            // If the flow is a writable flow, throw error
            if (flow.isWriter())
                throw new LogoException(cadre, Logo.messages
                        .getString("flux_lecture"));
                // else if the flow is a readable flow, convert to FlowReader
            else if (flow.isReader()) {
                flowReader = ((FlowReader) flow);
            }
            // else the flow isn't yet defined, initialize
            else flowReader = new FlowReader(flow);

            if (flowReader.isFinished())
                throw new LogoException(cadre, Logo.messages.getString("fin_flux") + " " + ident);
            // Reading line
            String line = flowReader.readLine();
            if (null == line) {
                flow.setFinished(true);
                throw new LogoException(cadre, Logo.messages.getString("fin_flux")
                        + " " + ident);
            }
            Interpreter.operande = true;
            Interpreter.calcul.push("[ " + Utils.formatCode(line.trim()) + " ] ");
            kernel.flows.set(index, flowReader);
        } catch (FileNotFoundException e1) {
            try {
                throw new LogoException(cadre, Logo.messages
                        .getString("error.iolecture"));
            } catch (LogoException e5) {
            }
        } catch (IOException e2) {
        } catch (LogoException e) {
        }
    }

    private void getOpenFiles(Stack<String> param) {
        String liste;
        liste = "[ ";
        for (Flow flow : kernel.flows) {
            liste += "[ " + flow.getId()
                    + " " + flow.getPath() + " ] ";
        }
        liste += "] ";
        Interpreter.operande = true;
        Interpreter.calcul.push(liste);
    }

    private void getTextFont(Stack<String> param) {
        Interpreter.operande = true;
        Interpreter.calcul.push("[ "
                + HistoryPanel.printFontId
                + " [ "
                + Application.fonts[HistoryPanel.printFontId]
                .getFontName() + " ] ] ");
    }

    private void setTextFont(Stack<String> param) {
        try {
            int int_police = kernel.getCalculator().getInteger(param.get(0));
            HistoryPanel.printFontId = int_police
                    % Application.fonts.length;
            cadre.getHistoryPanel().setFontNumber(int_police);
        } catch (LogoException e) {
        }
    }

    private void getLabelFont(Stack<String> param) {
        Interpreter.operande = true;
        Interpreter.calcul.push("[ "
                + cadre.getDrawPanel().drawingFont
                + " [ "
                + Application.fonts[cadre.getDrawPanel().drawingFont]
                .getFontName() + " ] ] ");
    }

    private void setLabelFont(Stack<String> param) {
        try {
            int int_police = kernel.getCalculator().getInteger(param.get(0));
            cadre.getDrawPanel().drawingFont = int_police
                    % Application.fonts.length;
        } catch (LogoException e) {
        }
    }

    private void isCountdownEnded(Stack<String> param) {
        Interpreter.operande = true;
        if (Calendar.getInstance().getTimeInMillis() > Kernel.chrono)
            Interpreter.calcul.push(Logo.messages.getString("vrai"));
        else
            Interpreter.calcul.push(Logo.messages.getString("faux"));
    }

    private void countdown(Stack<String> param) {
        try {
            int temps = kernel.getCalculator().getInteger(param.get(0));
            Kernel.chrono = Calendar.getInstance().getTimeInMillis()
                    + 1000 * temps;
        } catch (LogoException e) {
        }
    }

    private void getTimePassed(Stack<String> param) {
        Interpreter.operande = true;
        long heure_actuelle = Calendar.getInstance().getTimeInMillis();
        Interpreter.calcul
                .push(String
                        .valueOf((heure_actuelle - Logo.getStartupHour()) / 1000));
    }

    private void getTime(Stack<String> param) {
        Calendar cal;
        Interpreter.operande = true;
        cal = Calendar.getInstance(Logo.getLocale(Logo.config.getLanguage()));
        int heure = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        int seconde = cal.get(Calendar.SECOND);
        Interpreter.calcul.push("[ " + heure + " " + minute + " "
                + seconde + " ] ");
    }

    private void getDate(Stack<String> param) {
        Interpreter.operande = true;
        Calendar cal = Calendar.getInstance(Logo.getLocale(Logo.config.getLanguage()));
        int jour = cal.get(Calendar.DAY_OF_MONTH);
        int mois = cal.get(Calendar.MONTH) + 1;
        int annee = cal.get(Calendar.YEAR);
        Interpreter.calcul.push("[ " + jour + " " + mois + " " + annee
                + " ] ");
    }

    private void message(Stack<String> param) {
        String liste;
        try {
            liste = getFinalList(param.get(0));
            StringTokenizer st = new StringTokenizer(liste); // On
            // découpe
            // le
            // message
            // en
            // tranche
            // de
            // longueurs
            // acceptables
            FontMetrics fm = cadre.getGraphics()
                    .getFontMetrics(Logo.config.getFont());
            liste = "";
            String tampon = "";
            while (st.hasMoreTokens()) {
                tampon += st.nextToken() + " ";
                if (fm.stringWidth(tampon) > 200) {
                    liste += tampon + "\n";
                    tampon = "";
                }
            }
            liste += tampon;
            liste = Utils.unescapeString(liste);

            MessageTextArea jt = new MessageTextArea(liste);
            JOptionPane.showMessageDialog(cadre, jt, "", JOptionPane.INFORMATION_MESSAGE, ResourceLoader.getAppIcon());

        } catch (LogoException e) {
        }
    }

    private void mousePosition(Stack<String> param) {
        Interpreter.calcul.push(cadre.getDrawPanel().get_possouris());
        Interpreter.operande = true;
    }

    private void mouseButton(Stack<String> param) {
        while (!cadre.getDrawPanel().get_lissouris()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
            if (LogoException.lance)
                break;
        }
        Interpreter.calcul.push(String.valueOf(cadre.getDrawPanel()
                .get_bouton_souris()));
        Interpreter.operande = true;
    }

    private void getTextColor(Stack<String> param) {
        Interpreter.operande = true;
        Color c = cadre.getHistoryPanel().getTextColor();
        Interpreter.calcul.push("[ " + c.getRed() + " " + c.getGreen()
                + " " + c.getBlue() + " ] ");
    }

    private void setTextColor(Stack<String> param) {
        try {
            if (isList(param.get(0))) {
                cadre.getHistoryPanel().setTextColor(rgb(param.get(0), Utils.primitiveName("fct")));
            } else {
                int coul = kernel.getCalculator().getInteger(param.get(0)) % DrawPanel.defaultColors.length;
                if (coul < 0) coul += DrawPanel.defaultColors.length;
                cadre.getHistoryPanel().setTextColor(DrawPanel.defaultColors[coul]);
            }
        } catch (LogoException e) {
        }
    }

    private void getTextSize(Stack<String> param) {
        Interpreter.operande = true;
        Interpreter.calcul.push(String.valueOf(cadre.getHistoryPanel()
                .getFontSize()));
    }

    private void setTextSize(Stack<String> param) {
        try {
            int i = kernel.getCalculator().getInteger(param.get(0));
            cadre.getHistoryPanel().setFontSize(i);
        } catch (LogoException e) {
        }
    }

    private void setSequenceIndex(Stack<String> param) {
        try {
            int i = kernel.getCalculator().getInteger(param.get(0));
            cadre.getSoundPlayer().setTicks(i * 64);
        } catch (LogoException e) {
        }
    }

    private void getSequenceIndex(Stack<String> param) {
        Interpreter.operande = true;
        double d = (double) cadre.getSoundPlayer().getTicks() / 64;
        Interpreter.calcul.push(Calculator.teste_fin_double(d));
    }

    private void deleteSequence(Stack<String> param) {
        cadre.getSoundPlayer().efface_sequence();
    }

    private void play(Stack<String> param) {
        cadre.getSoundPlayer().joue();
    }

    private void setInstrument(Stack<String> param) {
        try {
            int i = kernel.getCalculator().getInteger(param.get(0));
            cadre.getSoundPlayer().setInstrument(i);
        } catch (LogoException e) {
        }
    }

    private void getInstrument(Stack<String> param) {
        Interpreter.operande = true;
        Interpreter.calcul.push(String
                .valueOf(cadre.getSoundPlayer().getInstrument()));
    }

    private void sequence(Stack<String> param) {
        String liste;
        try {
            liste = getFinalList(param.get(0));
            cadre.getSoundPlayer().cree_sequence(Utils.formatCode(liste).toString());
        } catch (LogoException e) {
        }
    }

    private void eraseTurtle(Stack<String> param) {
        int id;
        try {
            id = Integer.parseInt(param.get(0));
            if (id > -1 && id < Logo.config.getMaxTurtles()) {
                // On compte le nombre de tortues à l'écran
                int compteur = 0;
                int premier_dispo = -1;
                for (int i = 0; i < Logo.config.getMaxTurtles(); i++) {
                    if (null != cadre.getDrawPanel().turtles[i]) {
                        if (i != id && premier_dispo == -1)
                            premier_dispo = i;
                        compteur++;
                    }
                }
                // On vérifie que ce n'est pas la seule tortue
                // dispopnible:
                if (null != cadre.getDrawPanel().turtles[id]) {
                    if (compteur > 1) {
                        int tortue_utilisee = kernel.getActiveTurtle().id;
                        cadre.getDrawPanel().turtle = cadre.getDrawPanel().turtles[id];
                        cadre.getDrawPanel().toggleTurtle();
                        cadre.getDrawPanel().turtle = cadre.getDrawPanel().turtles[tortue_utilisee];
                        cadre.getDrawPanel().turtles[id] = null;
                        if (cadre.getDrawPanel().visibleTurtles.search(String
                                .valueOf(id)) > 0)
                            cadre.getDrawPanel().visibleTurtles.remove(String
                                    .valueOf(id));
                        if (kernel.getActiveTurtle().id == id) {
                            cadre.getDrawPanel().turtle = cadre.getDrawPanel().turtles[premier_dispo];
                            cadre.getDrawPanel()
                                    .setStroke(kernel.getActiveTurtle().crayon); // on
                            // adapte
                            // le
                            // nouveau
                            // crayon
                            String police = cadre.getDrawPanel().getGraphicsFont()
                                    .getName();
                            cadre.getDrawPanel()
                                    .setFont(new Font(police,
                                            Font.PLAIN,
                                            kernel.getActiveTurtle().police));

                        }
                    } else {
                        try {
                            throw new LogoException(cadre, Logo.messages
                                    .getString("seule_tortue_dispo"));
                        } catch (LogoException e) {
                        }
                    }
                }
            }
        } catch (NumberFormatException e) {
            try {
                kernel.getCalculator().getInteger(param.get(0));
            } catch (LogoException e1) {
            }
        }
    }

    private void setFontSize(Stack<String> param) {
        try {
            int taille = kernel.getCalculator().getInteger(param.get(0));
            kernel.getActiveTurtle().police = taille;
            Font police = Logo.config.getFont();
            cadre.getDrawPanel().setGraphicsFont(police
                    .deriveFont((float) kernel.getActiveTurtle().police));
        } catch (LogoException e) {
        }
    }

    private void getFontSize(Stack<String> param) {
        Interpreter.operande = true;
        Interpreter.calcul.push(String.valueOf(kernel.getActiveTurtle().police));
    }

    private void setTurtle(Stack<String> param) {
        try {
            int i = Integer.parseInt(param.get(0));
            if (i > -1 && i < Logo.config.getMaxTurtles()) {
                if (null == cadre.getDrawPanel().turtles[i]) {
                    cadre.getDrawPanel().turtles[i] = new Turtle(cadre);
                    cadre.getDrawPanel().turtles[i].id = i;
                    cadre.getDrawPanel().turtles[i].setVisible(false);
                }
                cadre.getDrawPanel().turtle = cadre.getDrawPanel().turtles[i];
                cadre.getDrawPanel().setStroke(kernel.getActiveTurtle().crayon);
                String police = cadre.getDrawPanel().getGraphicsFont().getName();
                cadre.getDrawPanel()
                        .setGraphicsFont(new Font(police,
                                Font.PLAIN,
                                kernel.getActiveTurtle().police));

            } else {
                try {
                    throw new LogoException(cadre, Logo.messages
                            .getString("tortue_inaccessible"));
                } catch (LogoException e) {
                }
            }
        } catch (NumberFormatException e) {
            try {
                kernel.getCalculator().getInteger(param.get(0));
            } catch (LogoException e1) {
            }
        }
    }

    private void getTurtles(Stack<String> param) {
        Interpreter.operande = true;
        String li = "[ ";
        for (int i = 0; i < cadre.getDrawPanel().turtles.length; i++) {
            if (null != cadre.getDrawPanel().turtles[i])
                li += i + " ";
        }
        li += "]";
        Interpreter.calcul.push(li);
    }

    private void getActiveTurtle(Stack<String> param) {
        Interpreter.operande = true;
        Interpreter.calcul.push(String.valueOf(kernel.getActiveTurtle().id));
    }

    private void define(Stack<String> param) {
        String mot;
        try {
            mot = getWord(param.get(0));
            if (null == mot)
                throw new LogoException(cadre, param.get(0) + " " + Logo.messages.getString("error.word"));
            if (mot.equals("")) new LogoException(cadre, Logo.messages.getString("procedure_vide"));
            String list = getFinalList(param.get(1));
            StringBuffer sb = new StringBuffer();
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
            cadre.editor.appendText(new String(sb));
        } catch (LogoException e) {
        }
        try {
            cadre.editor.parseProcedures();
            cadre.editor.clearText();
        } catch (Exception e2) {
        }
    }

    private void setShape(Stack<String> param) {
        try {
            primitive2D("turtle.fforme");
            int i = kernel.getCalculator().getInteger(param.get(0)) % 7;
            if (kernel.getActiveTurtle().id == 0) {
                Logo.config.setActiveTurtle(i);
            }
            kernel.change_image_tortue(i);
        } catch (LogoException e) {
        }
    }

    private void getShape(Stack<String> param) {
        try {
            primitive2D("turtle.forme");
            Interpreter.operande = true;
            Interpreter.calcul.push(String.valueOf(kernel.getActiveTurtle().getShape()));
        } catch (LogoException e) {
        }
    }

    private void _false(Stack<String> param) {
        Interpreter.operande = true;
        Interpreter.calcul.push(Logo.messages.getString("faux"));
    }

    private void _true(Stack<String> param) {
        Interpreter.operande = true;
        Interpreter.calcul.push(Logo.messages.getString("vrai"));
    }

    private void atan(Stack<String> param) {
        try {
            Interpreter.calcul.push(kernel.getCalculator().atan(param.get(0)));
            Interpreter.operande = true;
        } catch (LogoException e) {
        }
    }

    private void asin(Stack<String> param) {
        try {
            Interpreter.calcul.push(kernel.getCalculator().asin(param.get(0)));
            Interpreter.operande = true;
        } catch (LogoException e) {
        }
    }

    private void acos(Stack<String> param) {
        try {
            Interpreter.calcul.push(kernel.getCalculator().acos(param.get(0)));
            Interpreter.operande = true;
        } catch (LogoException e) {
        }
    }

    private void tan(Stack<String> param) {
        Interpreter.operande = true;
        try {
            Interpreter.calcul.push(kernel.getCalculator().tan(param.get(0)));
        } catch (LogoException e) {
        }
    }

    private void pi(Stack<String> param) {
        Interpreter.operande = true;
        Interpreter.calcul.push(kernel.getCalculator().pi());
    }

    private void load(Stack<String> param) {
        String mot;
        try {
            mot = getWord(param.get(0));
            if (null == mot)
                throw new LogoException(cadre, param.get(0) + " "
                        + Logo.messages.getString("error.word"));
            String path = Utils.unescapeString(Logo.config.getDefaultFolder()) + File.separator + mot;
            try {
                String txt = Utils.readLogoFile(path);
                cadre.editor.appendText(txt);
            } catch (IOException e1) {
                throw new LogoException(cadre,
                        Logo.messages.getString("error.iolecture"));
            }
            try {
                cadre.editor.parseProcedures();
                if (!cadre.isNewEnabled())
                    cadre.setNewEnabled(true);
            } catch (Exception e3) {
                System.out.println(e3);
            }
            cadre.editor.clearText();
        } catch (LogoException e) {
        }
    }

    private void saveAll(Stack<String> param) {
        String mot;
        try {
            mot = getWord(param.get(0));
            if (null == mot)
                throw new LogoException(cadre, param.get(0) + " "
                        + Logo.messages.getString("error.word"));
            saveProcedures(mot, null);
        } catch (LogoException e) {
        }
    }

    private void save(Stack<String> param) {
        String mot;
        String liste;
        try {
            mot = getWord(param.get(0));
            if (null == mot)
                throw new LogoException(cadre, Logo.messages
                        .getString("error.word"));
            liste = getFinalList(param.get(1));
            StringTokenizer st = new StringTokenizer(liste);
            Stack<String> pile = new Stack<String>();
            while (st.hasMoreTokens())
                pile.push(st.nextToken());
            saveProcedures(mot, pile);
        } catch (LogoException e) {
        }
    }

    private void getDirectory(Stack<String> param) {
        Interpreter.operande = true;
        Interpreter.calcul.push("\"" + Logo.config.getDefaultFolder());
    }

    private void setDirectory(Stack<String> param) {
        String liste;
        try {
            liste = getWord(param.get(0));
            if (null == liste)
                throw new LogoException(cadre, param.get(0) + " "
                        + Logo.messages.getString("error.word"));
            String chemin = Utils.unescapeString(liste);
            if ((new File(chemin)).isDirectory() && !chemin.startsWith("..")) {
                Logo.config.setDefaultFolder(Utils.escapeString(chemin));
            } else throw new LogoException(cadre, liste
                    + " "
                    + Logo.messages
                    .getString("erreur_pas_repertoire"));
        } catch (LogoException e) {
        }
    }

    private void getFiles(Stack<String> param) {
        String str = Utils.unescapeString(Logo.config.getDefaultFolder());
        File f = new File(str);
        String fichier = "";
        String dossier = "";
        int nbdossier = 0;
        int nbfichier = 0;
        String[] l = f.list();
        for (int i = 0; i < l.length; i++) {
            if ((new File(str + File.separator
                    + l[i])).isDirectory()) {
                nbdossier++;
                if (nbdossier % 5 == 0)
                    dossier += l[i] + "\n";
                else
                    dossier += l[i] + " ";
            } else {
                nbfichier++;
                if (nbfichier % 5 == 0)
                    fichier += l[i] + "\n";
                else
                    fichier += l[i] + " ";
            }
        }
        String texte = "";
        if (!dossier.equals(""))
            texte += Logo.messages.getString("repertoires") + ":\n"
                    + dossier + "\n";
        if (!fichier.equals(""))
            texte += Logo.messages.getString("fichiers") + ":\n"
                    + fichier + "\n";
        cadre.updateHistory("commentaire", texte);
    }

    private void run(Stack<String> param) {
        String mot;
        try {
            mot = getWord(param.get(0));
            if (null == mot) {
                mot = getList(param.get(0).trim());
                mot = new String(Utils.formatCode(mot));
            } else mot = mot + " ";
            cadre.getKernel().getInstructionBuffer().insert(mot);
            Interpreter.renvoi_instruction = true;
        } catch (LogoException e) {
        }
    }

    private void isProcedure(Stack<String> param) {
        String mot;
        Interpreter.operande = true;
        boolean test = false;
        mot = getWord(param.get(0));
        for (int i = 0; i < wp.getNumberOfProcedure(); i++) {
            if (wp.getProcedure(i).name.equals(mot))
                test = true;
        }
        if (test)
            Interpreter.calcul.push(Logo.messages.getString("vrai"));
        else
            Interpreter.calcul.push(Logo.messages.getString("faux"));
    }

    private void isPrimitive(Stack<String> param) {
        String mot;
        try {
            Interpreter.operande = true;
            mot = getWord(param.get(0));
            if (null == mot)
                throw new LogoException(cadre, param.get(0) + " "
                        + Logo.messages.getString("error.word"));
            if (primitiveMap.containsKey(mot))
                Interpreter.calcul.push(Logo.messages.getString("vrai"));
            else
                Interpreter.calcul.push(Logo.messages.getString("faux"));
        } catch (LogoException e) {
        }
    }

    private void isVisible(Stack<String> param) {
        Interpreter.operande = true;
        if (kernel.getActiveTurtle().isVisible())
            Interpreter.calcul.push(Logo.messages.getString("vrai"));
        else
            Interpreter.calcul.push(Logo.messages.getString("faux"));
    }

    private void isPenDown(Stack<String> param) {
        Interpreter.operande = true;
        if (kernel.getActiveTurtle().isPenDown())
            Interpreter.calcul.push(Logo.messages.getString("vrai"));
        else
            Interpreter.calcul.push(Logo.messages.getString("faux"));
    }

    private void screenColor(Stack<String> param) {
        Interpreter.operande = true;
        Color color = cadre.getDrawPanel().getScreenColor();
        Interpreter.calcul.push("[ " + color.getRed() + " "
                + color.getGreen() + " "
                + color.getBlue() + " ] ");
    }

    private void penColor(Stack<String> param) {
        Interpreter.operande = true;
        Interpreter.calcul.push("[ "
                + kernel.getActiveTurtle().penColor.getRed() + " "
                + kernel.getActiveTurtle().penColor.getGreen() + " "
                + kernel.getActiveTurtle().penColor.getBlue() + " ] ");
    }

    private void distance(Stack<String> param) {
        try {
            Interpreter.operande = true;
            double distance = cadre.getDrawPanel().distance(getFinalList(param.get(0)));
            Interpreter.calcul.push(Calculator.teste_fin_double(distance));
        } catch (LogoException e) {
        }
    }

    private void towards(Stack<String> param) {
        try {
            Interpreter.operande = true;
            if (DrawPanel.windowMode != DrawPanel.WINDOW_3D) {
                double angle = cadre.getDrawPanel().to2D(getFinalList(param.get(0)));
                Interpreter.calcul.push(Calculator.teste_fin_double(angle));
            } else {
                double[] orientation = cadre.getDrawPanel().vers3D(getFinalList(param.get(0)));
                Interpreter.calcul.push("[ " + orientation[0] + " " + orientation[1] + " " + orientation[2] + " ] ");
            }
        } catch (LogoException e) {
        }
    }

    private void dot(Stack<String> param) {
        if (kernel.getActiveTurtle().isVisible())
            cadre.getDrawPanel().eraseTurtle(false);
        try {
            cadre.getDrawPanel().point(getFinalList(param.get(0)));
        } catch (LogoException e) {
        }
        if (kernel.getActiveTurtle().isVisible())
            cadre.getDrawPanel().eraseTurtle(true);
    }

    private void fill(Stack<String> param) {
        cadre.getDrawPanel().fill();
    }

    private void readChar(Stack<String> param) {
        while (cadre.getKey() == -1) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
            if (LogoException.lance)
                break;
        }
        Interpreter.calcul.push(String.valueOf(cadre.getKey()));
        Interpreter.operande = true;
        cadre.setKey(-1);
    }

    private void evalWhile(Stack<String> param) {
        try {
            String liste = getFinalList(param.get(1));
            boolean predicat = predicat(param.get(0));
            kernel.primitive.whilesi(predicat, liste);
        } catch (LogoException e) {
        }
    }

    private void isKey(Stack<String> param) {
        Interpreter.operande = true;
        if (cadre.getKey() != -1)
            Interpreter.calcul.push(Logo.messages.getString("vrai"));
        else
            Interpreter.calcul.push(Logo.messages.getString("faux"));
    }

    private void read(Stack<String> param) {
        String mot;
        try {
            String liste = getFinalList(param.get(0));
            mot = getWord(param.get(1));
            if (null == mot)
                throw new LogoException(cadre, Logo.messages
                        .getString("error.word"));
            FontMetrics fm = cadre.getGraphics()
                    .getFontMetrics(Logo.config.getFont());
            int longueur = fm.stringWidth(liste) + 100;
            InputFrame inputFrame = new InputFrame(liste, longueur);
            while (inputFrame.isVisible()) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                }
            }
            param = new Stack<String>();
            param.push("\"" + mot);
            String phrase = inputFrame.getText();
            // phrase="[ "+Logo.rajoute_backslash(phrase)+" ] ";
            StringBuffer tampon = new StringBuffer();
            for (int j = 0; j < phrase.length(); j++) {
                char c = phrase.charAt(j);
                if (c == '\\') tampon.append("\\\\");
                else tampon.append(c);
            }
            int offset = tampon.indexOf(" ");
            if (offset != -1) {
                tampon.insert(0, "[ ");
                tampon.append(" ] ");
            } else {
                try {
                    Double.parseDouble(phrase);
                } catch (NumberFormatException e) {
                    tampon.insert(0, "\"");
                }
            }
            phrase = new String(tampon);
            param.push(phrase);
            globalMake(param);
            String texte = liste + "\n" + phrase;
            cadre.updateHistory("commentaire", Utils.unescapeString(texte) + "\n");
            cadre.focusCommandLine();
            inputFrame.dispose();
            cadre.focusCommandLine();
        } catch (LogoException e) {
        }
    }

    private void _while(Stack<String> param) {
        try {
            String li1 = getList(param.get(0));
            li1 = new String(Utils.formatCode(li1));
            String li2 = getList(param.get(1));
            li2 = new String(Utils.formatCode(li2));
            String instr = "\\siwhile " + li1 + "[ " + li2 + "] ";
            LoopWhile bp = new LoopWhile(BigDecimal.ONE, BigDecimal.ZERO, BigDecimal.ONE, instr);
            stackLoop.push(bp);
            cadre.getKernel().getInstructionBuffer().insert(instr + Interpreter.END_LOOP + " ");
        } catch (LogoException e) {
        }
    }

    private void setPenWidth(Stack<String> param) {
        try {
            double nombre = kernel.getCalculator().numberDouble(param.get(0));
            if (nombre < 0)
                nombre = Math.abs(nombre);
            if (DrawPanel.record3D == DrawPanel.RECORD_3D_LINE || DrawPanel.record3D == DrawPanel.RECORD_3D_POINT) {
                if (kernel.getActiveTurtle().getPenWidth() != (float) nombre) DrawPanel.poly.addToScene();
            }
            kernel.getActiveTurtle().fixPenWidth((float) nombre);
            cadre.getDrawPanel().setStroke(kernel.getActiveTurtle().crayon);
            if (DrawPanel.record3D == DrawPanel.RECORD_3D_LINE) {
                DrawPanel.poly = new ElementLine(cadre);
                DrawPanel.poly.addVertex(new Point3d(kernel.getActiveTurtle().X / 1000,
                        kernel.getActiveTurtle().Y / 1000,
                        kernel.getActiveTurtle().Z / 1000), kernel.getActiveTurtle().penColor);
            } else if (DrawPanel.record3D == DrawPanel.RECORD_3D_POINT) {
                DrawPanel.poly = new ElementPoint(cadre);
            }
        } catch (LogoException e) {
        }
    }

    private void loadImage(Stack<String> param) {
        BufferedImage image = null;
        try {
            primitive2D("ci");
            image = getImage(param.get(0));
        } catch (LogoException e) {
        }
        if (null != image)
            cadre.getDrawPanel().drawImage(image);
    }

    private void fenceTurtle(Stack<String> param) {
        cadre.getDrawPanel().setWindowMode(DrawPanel.WINDOW_CLOSE);
    }

    private void wrapTurtle(Stack<String> param) {
        cadre.getDrawPanel().setWindowMode(DrawPanel.WINDOW_WRAP);
    }

    private void windowTurtle(Stack<String> param) {
        cadre.getDrawPanel().setWindowMode(DrawPanel.WINDOW_CLASSIC);
    }

    private void findColor(Stack<String> param) {
        if (kernel.getActiveTurtle().isVisible())
            cadre.getDrawPanel().eraseTurtle(false);
        try {
            String liste = getFinalList(param.get(0));
            Color r = cadre.getDrawPanel().guessColorPoint(liste);
            Interpreter.operande = true;
            Interpreter.calcul.push("[ " + r.getRed() + " "
                    + r.getGreen() + " " + r.getBlue() + " ] ");
        } catch (LogoException e) {
        }
        if (kernel.getActiveTurtle().isVisible())
            cadre.getDrawPanel().eraseTurtle(true);
    }

    private void label(Stack<String> param) {
        String mot;
        String par = param.get(0).trim();
        if (isList(par))
            par = formatList(par.substring(1, par.length() - 1));
        mot = getWord(param.get(0));
        if (null == mot)
            cadre.getDrawPanel().label(Utils.unescapeString(par));
        else
            cadre.getDrawPanel().label(Utils.unescapeString(mot));
    }

    private void word(Stack<String> param) {
        String mot;
        Interpreter.operande = true;
        String result = "";
        for (int i = 0; i < param.size(); i++) {
            mot = getWord(param.get(i));
            if (null == mot)
                try {
                    throw new LogoException(cadre, param.get(i) + " "
                            + Logo.messages.getString("error.word"));
                } catch (LogoException e) {
                }
            result += mot;
        }
        try {
            Double.parseDouble(result);
        } catch (NumberFormatException e) {
            result = "\"" + result;
        }
        Interpreter.calcul.push(result);
    }

    private void eraseAll(Stack<String> param) {
        wp.deleteAllProcedures();
        wp.deleteAllVariables();
        wp.deleteAllPropertyLists();
        cadre.setNewEnabled(false);
    }

    private void eraseVariable(Stack<String> param) {
        erase(param.get(0), "variable");
    }

    private void eraseProcedure(Stack<String> param) {
        erase(param.get(0), "procedure");
    }

    private void procedures(Stack<String> param) {
        Interpreter.operande = true;
        Interpreter.calcul.push(new String(getAllProcedures()));
    }

    private void wait(Stack<String> param) {
        try {
            int temps = kernel.getCalculator().getInteger(param.get(0));
            if (temps < 0) {
                String attends = Utils.primitiveName("attends");
                throw new LogoException(cadre, attends + " "
                        + Logo.messages.getString("attend_positif"));
            } else {
                int nbsecondes = temps / 60;
                int reste = temps % 60;
                for (int i = 0; i < nbsecondes; i++) {
                    Thread.sleep(1000);
                    if (cadre.error)
                        break;
                }
                if (!cadre.error)
                    Thread.sleep(reste * 50 / 3);
            }

        } catch (LogoException e1) {
        } catch (InterruptedException e2) {
        }
    }

    private void random(Stack<String> param) {
        Interpreter.operande = true;
        try {
            int i = kernel.getCalculator().getInteger(param.get(0));
            i = (int) Math.floor(Math.random() * i);
            Interpreter.calcul.push(String.valueOf(i));
        } catch (LogoException e) {
        }
    }

    private void setScreenColor(Stack<String> param) {
        try {
            Color color = null;
            if (isList(param.get(0))) {
                try {
                    color = rgb(param.get(0), Utils.primitiveName("fcfg"));
                } catch (LogoException e) {
                }
            } else {
                int coul = kernel.getCalculator().getInteger(param.get(0)) % DrawPanel.defaultColors.length;
                if (coul < 0) coul += DrawPanel.defaultColors.length;
                color = DrawPanel.defaultColors[coul];
            }
            cadre.getDrawPanel().setScreenColor(color);
        } catch (LogoException e) {
        }
    }

    private void setPenColor(Stack<String> param) {
        try {
            Color color = null;
            if (isList(param.get(0))) {
                try {
                    color = rgb(param.get(0), Utils.primitiveName("fcc"));
                } catch (LogoException e) {
                }
            } else {
                int coul = kernel.getCalculator().getInteger(param.get(0)) % DrawPanel.defaultColors.length;
                if (coul < 0) coul += DrawPanel.defaultColors.length;
                color = DrawPanel.defaultColors[coul];
            }
            cadre.getDrawPanel().setPenColor(color);
        } catch (LogoException e) {
        }
    }

    private void localMake(Stack<String> param) {
        try {
            local(param);
            globalMake(param);
            Interpreter.operande = false;
        } catch (LogoException e) {
        }
    }

    private void localWrap(Stack<String> param) {
        try {
            local(param);
            Interpreter.operande = false;
        } catch (LogoException e) {
        }
    }

    private void globalMakeWrap(Stack<String> param) {
        try {
            globalMake(param);
            Interpreter.operande = false;
        } catch (LogoException e) {
        }
    }

    private void sqrt(Stack<String> param) {
        Interpreter.operande = true;
        try {
            Interpreter.calcul.push(kernel.getCalculator().sqrt(param.get(0)));

        } catch (LogoException e) {
        }
    }

    private void isMember69(Stack<String> param) {
        try {
            isMember(param, 69);
        } catch (LogoException e) {
        }
    }

    private void isMember71(Stack<String> param) {
        try {
            isMember(param, 71);
        } catch (LogoException e) {
        }
    }

    private void isBeforeWrap(Stack<String> param) {
        try {
            isBefore(param);
        } catch (LogoException e) {
        }
    }

    private void isEmpty(Stack<String> param) {
        String mot;
        String liste = param.get(0).trim();
        mot = getWord(param.get(0));
        if (null == mot) { // si c'est une liste ou un nombre
            try {
                liste = getFinalList(liste).trim();
                if (liste.equals(""))
                    Interpreter.calcul.push(Logo.messages
                            .getString("vrai"));
                else
                    Interpreter.calcul.push(Logo.messages
                            .getString("faux"));
            } catch (LogoException e) {
            }
        } else { // Si c'est un mot
            if (mot.equals(""))
                Interpreter.calcul.push(Logo.messages.getString("vrai"));
            else
                Interpreter.calcul.push(Logo.messages.getString("faux"));
        }
        Interpreter.operande = true;
    }

    private void isList(Stack<String> param) {
        String liste = param.get(0).trim();
        if (isList(liste))
            Interpreter.calcul.push(Logo.messages.getString("vrai"));
        else
            Interpreter.calcul.push(Logo.messages.getString("faux"));
        Interpreter.operande = true;
    }

    private void isNumber(Stack<String> param) {
        try {
            Double.parseDouble(param.get(0));
            Interpreter.calcul.push(Logo.messages.getString("vrai"));
        } catch (NumberFormatException e) {
            Interpreter.calcul.push(Logo.messages.getString("faux"));
        }
        Interpreter.operande = true;
    }

    private void isWord(Stack<String> param) {
        String mot;
        mot = getWord(param.get(0));
        if (null == mot)
            Interpreter.calcul.push(Logo.messages.getString("faux"));
        else
            Interpreter.calcul.push(Logo.messages.getString("vrai"));
        Interpreter.operande = true;
    }

    private void count(Stack<String> param) {
        String mot;
        Interpreter.operande = true;
        mot = getWord(param.get(0));
        if (null == mot) {
            try {
                String liste = getFinalList(param.get(0));
                Interpreter.calcul.push(String
                        .valueOf(numberOfElements(liste)));
            } catch (LogoException e) {
            }
        } else
            Interpreter.calcul.push(String.valueOf(getWordLength(mot)));
    }

    private void first(Stack<String> param) {
        Interpreter.operande = true;
        String mot = getWord(param.get(0));
        if (null == mot) { // SI c'est une liste
            try {
                String liste = getFinalList(param.get(0));
                // System.out.println("b"+item(liste, 1)+"b");
                Interpreter.calcul.push(item(liste, 1));
            } catch (LogoException e) {
            }
        } else if (getWordLength(mot) == 1)
            Interpreter.calcul.push(debut_chaine + mot);
        else {
            String st = "";
            try {
                st = itemWord(1, mot);
                Double.parseDouble(st);
                Interpreter.calcul.push(st);
            } catch (LogoException e1) {
            } catch (NumberFormatException e2) {
                Interpreter.calcul.push("\"" + st);
            }
        }
    }

    private void last(Stack<String> param) {
        Interpreter.operande = true;
        String mot = getWord(param.get(0));
        if (null == mot) { // Si c'est une liste
            try {
                String liste = getFinalList(param.get(0));
                Interpreter.calcul
                        .push(item(liste, numberOfElements(liste)));
            } catch (LogoException e) {
            }
        } else if (getWordLength(mot) == 1)
            Interpreter.calcul.push(debut_chaine + mot);
        else {
            String st = "";
            try {
                st = itemWord(getWordLength(mot), mot);
                Double.parseDouble(st);
                Interpreter.calcul.push(st);
            } catch (NumberFormatException e1) {
                Interpreter.calcul.push("\"" + st);
            } catch (LogoException e2) {
            }
        }
    }

    private void butFirst(Stack<String> param) {
        String mot;
        Interpreter.operande = true;
        mot = getWord(param.get(0));
        if (null == mot) {
            try {
                String liste = getFinalList(param.get(0)).trim();
                String element = item(liste, 1);
                int longueur = element.length();
                if (element.startsWith("\"") || element.startsWith("["))
                    longueur--;
                Interpreter.calcul.push("["
                        + liste.substring(longueur)
                        + " ] ");
            } catch (LogoException e) {
            }
        } else if (mot.equals("")) {
            try {
                throw new LogoException(cadre, Logo.messages.getString("mot_vide"));
            } catch (LogoException e1) {
            }
        } else if (getWordLength(mot) == 1)
            Interpreter.calcul.push("\"");
        else {
            if (!mot.startsWith("\\")) mot = mot.substring(1);
            else mot = mot.substring(2);
            try {
                Double.parseDouble(mot);
                Interpreter.calcul.push(mot);
            } catch (NumberFormatException e) {
                Interpreter.calcul.push(debut_chaine + mot);
            }
        }
    }

    private void butLast(Stack<String> param) {
        String mot;
        Interpreter.operande = true;
        mot = getWord(param.get(0));
        if (null == mot) {
            try {
                String liste = getFinalList(param.get(0)).trim();
                String element = item(liste, numberOfElements(liste));
                int longueur = element.length();

                if (element.startsWith("\"") || element.startsWith("["))
                    longueur--;
                Interpreter.calcul.push("[ "
                        + liste.substring(0, liste.length() - longueur)
                        + "] ");
            } catch (LogoException e) {
            }
        } else if (mot.equals("")) {
            try {
                throw new LogoException(cadre, Logo.messages.getString("mot_vide"));
            } catch (LogoException e1) {
            }
        } else if (getWordLength(mot) == 1)
            Interpreter.calcul.push("\"");
        else {
            String tmp = mot.substring(0, mot.length() - 1);
            if (tmp.endsWith("\\")) tmp = tmp.substring(0, tmp.length() - 1);
            try {
                Double.parseDouble(tmp);
                Interpreter.calcul.push(tmp);
            } catch (NumberFormatException e) {
                Interpreter.calcul.push(debut_chaine + tmp);
            }
        }
    }

    private void item(Stack<String> param) {
        String mot;
        Interpreter.operande = true;
        try {
            mot = getWord(param.get(1));
            if (null == mot)
                Interpreter.calcul.push(item(getFinalList(param.get(1)
                ), kernel.getCalculator().getInteger(param.get(0))));
            else {
                int i = kernel.getCalculator().getInteger(param.get(0));
                if (i < 1 || i > getWordLength(mot))
                    throw new LogoException(cadre, Utils.primitiveName("item") + " " +
                            Logo.messages.getString("n_aime_pas") + i + " " +
                            Logo.messages.getString("comme_parametre") + ".");
                else {
                    String st = itemWord(i, mot);
                    try {
                        Double.parseDouble(st);
                        Interpreter.calcul.push(st);
                    } catch (NumberFormatException e1) {
                        Interpreter.calcul.push("\"" + st);
                    }
                }
            }
        } catch (LogoException e) {
        }
    }

    private void remove(Stack<String> param) {
        String mot;
        Interpreter.operande = true;
        try {
            String liste = getFinalList(param.get(1));
            StringTokenizer st = new StringTokenizer(liste);
            liste = "[ ";
            mot = getWord(param.get(0));
            String str;
            if (null != mot && mot.equals("")) mot = "\\v";
            if (null == mot)
                mot = param.get(0).trim();

            while (st.hasMoreTokens()) {
                str = st.nextToken();
                if (str.equals("["))
                    str = extractList(st);
                if (!str.equals(mot))
                    liste += str + " ";
            }
            Interpreter.calcul.push(liste.trim() + " ] ");
        } catch (LogoException e) {
        }
    }

    private void pick(Stack<String> param) {
        Interpreter.operande = true;
        String mot = getWord(param.get(0));
        if (null == mot) {
            try {
                String liste = getFinalList(param.get(0));
                int nombre = (int) Math.floor(numberOfElements(liste)
                        * Math.random()) + 1;
                String tmp = item(liste, nombre);
                if (tmp.equals("\"\\v")) tmp = "\"";
                Interpreter.calcul.push(tmp);
            } catch (LogoException e) {
            }
        } else {
            int nombre = (int) Math.floor(Math.random() * getWordLength(mot)) + 1;
            String st = "";
            try {
                st = itemWord(nombre, mot);
                Double.parseDouble(st);
                Interpreter.calcul.push(st);
            } catch (NumberFormatException e1) {
                Interpreter.calcul.push("\"" + st);
            } catch (LogoException e2) {
            }
        }
    }

    private void reverse(Stack<String> param) {
        try {
            String liste = getFinalList(param.get(0)).trim();
            Interpreter.operande = true;
            StringTokenizer st = new StringTokenizer(liste);
            liste = " ] ";
            String element = "";
            while (st.hasMoreTokens()) {
                element = st.nextToken();
                if (element.equals("["))
                    element = extractList(st);
                liste = " " + element + liste;
            }
            Interpreter.calcul.push("[" + liste);
        } catch (LogoException e) {
        }
    }

    private void lput(Stack<String> param) {
        try {
            String liste = getFinalList(param.get(1)).trim();
            Interpreter.operande = true;
            String mot = getWord(param.get(0));
            if (null != mot && mot.equals("")) mot = "\\v";
            if (null == mot) { // Si c'est une liste
                Interpreter.calcul.push(("[ " + liste).trim() + " "
                        + param.get(0).trim() + " ] ");

            } else
                Interpreter.calcul.push(("[ " + liste).trim() + " " + mot + " ] ");
        } catch (LogoException e) {
        }
    }

    private void fput(Stack<String> param) {
        try {
            String liste = getFinalList(param.get(1));
            Interpreter.operande = true;
            String mot = getWord(param.get(0));
            if (null != mot && mot.equals("")) mot = "\\v";
            if (null == mot) {
                if (!liste.equals(""))
                    Interpreter.calcul.push("[ "
                            + param.get(0).trim() + " "
                            + liste.trim() + " ] ");
                else
                    Interpreter.calcul.push("[ "
                            + param.get(0).trim() + " ] ");
            } else {
                if (!liste.equals(""))
                    Interpreter.calcul.push("[ " + mot + " " + liste.trim()
                            + " ] ");
                else Interpreter.calcul.push("[ " + mot + " ] ");
            }
        } catch (LogoException e) {
        }
    }

    private void sentence(Stack<String> param) {
        String liste = "[ ";
        Interpreter.operande = true;
        for (int i = 0; i < param.size(); i++) {
            String mot = getWord(param.get(i));
            String mot2 = param.get(i).trim();
            if (null == mot) {
                if (isList(mot2))
                    liste += mot2.substring(1, mot2.length() - 1)
                            .trim()
                            + " ";
                else
                    liste += mot2 + " ";
            } else {
                if (mot.equals("")) mot = "\\v";
                liste += mot + " ";
            }
        }
        Interpreter.calcul.push(liste + "] ");
    }

    private void list(Stack<String> param) {
        String liste = "[ ";
        Interpreter.operande = true;
        for (int i = 0; i < param.size(); i++) {
            String mot2 = param.get(i);
            String mot = getWord(param.get(i));
            if (null == mot) {
                liste += mot2;
                // System.out.println("a"+mot2+"a");
            } else {
                if (mot.equals("")) mot = "\\v";
                liste += mot + " ";
            }
        }
        Interpreter.calcul.push(liste + "] ");
    }

    private void not(Stack<String> param) {
        try {
            Interpreter.operande = true;
            boolean b1 = predicat(param.get(0));
            if (b1)
                Interpreter.calcul.push(Logo.messages.getString("faux"));
            else
                Interpreter.calcul.push(Logo.messages.getString("vrai"));
        } catch (LogoException e) {
        }
    }

    private void cos(Stack<String> param) {
        Interpreter.operande = true;
        try {
            Interpreter.calcul.push(kernel.getCalculator().cos(param.get(0)));
        } catch (LogoException e) {
        }
    }

    private void sin(Stack<String> param) {
        Interpreter.operande = true;
        try {
            Interpreter.calcul.push(kernel.getCalculator().sin(param.get(0)));
        } catch (LogoException e) {
        }
    }

    private void log10(Stack<String> param) {
        Interpreter.operande = true;
        try {
            Interpreter.calcul.push(kernel.getCalculator().log10(param.get(0)));
        } catch (LogoException e) {
        }
    }

    private void round(Stack<String> param) {
        Interpreter.operande = true;
        try {
            Interpreter.calcul.push(String.valueOf(Math
                    .round(kernel.getCalculator().numberDouble(param.get(0)))));
        } catch (LogoException e) {
        }
    }

    private void heading(Stack<String> param) {
        Interpreter.operande = true;
        Interpreter.calcul.push(Calculator.teste_fin_double(kernel.getActiveTurtle().heading));
    }

    private void position(Stack<String> param) {
        Interpreter.operande = true;
        if (DrawPanel.windowMode != DrawPanel.WINDOW_3D) {
            double a = kernel.getActiveTurtle().curX - Logo.config.getImageWidth() / 2;
            double b = Logo.config.getImageHeight() / 2 - kernel.getActiveTurtle().curY;
            Interpreter.calcul.push("[ " + Calculator.teste_fin_double(a) + " " + Calculator.teste_fin_double(b) + " ] ");
        } else {
            Interpreter.calcul.push("[ " + kernel.getActiveTurtle().X + " "
                    + kernel.getActiveTurtle().Y + " " + kernel.getActiveTurtle().Z + " ] ");

        }
    }

    private void endLoop(Stack<String> param) {
        LoopProperties loop = stackLoop.peek();
        // LOOP REPEAT
        if (loop.isRepeat()) {
            BigDecimal compteur = loop.getCounter();
            BigDecimal fin = loop.getEnd();
            if (compteur.compareTo(fin) < 0) {
                loop.incremente();
                stackLoop.pop();
                stackLoop.push(loop);
                cadre.getKernel().getInstructionBuffer().insert(loop.getInstr() + Interpreter.END_LOOP + " ");
            } else if (compteur.compareTo(fin) == 0) {
                stackLoop.pop();
            }
        }
        // LOOP FOR or LOOP FOREACH
        else if (loop.isFor() || loop.isForEach()) {
            BigDecimal inc = loop.getIncrement();
            BigDecimal compteur = loop.getCounter();
            BigDecimal fin = loop.getEnd();
            if ((inc.compareTo(BigDecimal.ZERO) == 1 && (compteur.add(inc).compareTo(fin) <= 0))
                    || (inc.compareTo(BigDecimal.ZERO) == -1 && (compteur.add(inc).compareTo(fin) >= 0))) {
                loop.incremente();
                ((LoopFor) loop).AffecteVar(false);
                stackLoop.pop();
                stackLoop.push(loop);
                cadre.getKernel().getInstructionBuffer().insert(loop.getInstr() + Interpreter.END_LOOP + " ");
            } else {
                ((LoopFor) loop).DeleteVar();
                stackLoop.pop();
            }
        }
        // LOOP FOREVER
        else if (loop.isForEver()) {
            cadre.getKernel().getInstructionBuffer().insert(loop.getInstr() + Interpreter.END_LOOP + " ");
        }
        // LOOP FILL POLYGON
        else if (loop.isFillPolygon()) {
            cadre.getDrawPanel().stopRecord2DPolygon();
            stackLoop.pop();
        }
    }

    private void endProc(Stack<String> param) {
        Interpreter.locale = Interpreter.stockvariable.pop();
        if (Interpreter.nom.peek().equals("\n")) {
            Interpreter.nom.pop();
            Interpreter.lineNumber = "";
        } else {
            /* Example
             * to bug
             * av
             * end
             */
            try {
                throw new LogoException(cadre, Logo.messages
                        .getString("pas_assez_de")
                        + " " + Interpreter.nom.peek());
            } catch (LogoException e) {
            }
        }
        /* to bug [:a]	| (bug 10)
         * av :a		|
         * end			|
         */
        if (!Interpreter.nom.isEmpty() && !Interpreter.nom.peek().equals("\n") && !Interpreter.nom.peek().equals("(")) {
            try {
                if (!cadre.error)
                    throw new LogoException(cadre, Interpreter.en_cours.peek() + " " + Logo.messages.getString("ne_renvoie_pas") + " " + Interpreter.nom.peek());
            } catch (LogoException e) {
            }
        }
        if (!Interpreter.en_cours.isEmpty()) Interpreter.en_cours.pop();
    }

    private void andOp(Stack<String> param) {
        try {
            boolean b1 = predicat(param.get(0));
            boolean b2 = predicat(param.get(1));
            b1 = b1 & b2;
            if (b1)
                Interpreter.calcul.push(Logo.messages.getString("vrai"));
            else
                Interpreter.calcul.push(Logo.messages.getString("faux"));
        } catch (LogoException e) {
        }
        Interpreter.operande = true;
    }

    private void orOp(Stack<String> param) {
        try {
            boolean b1 = predicat(param.get(0));
            boolean b2 = predicat(param.get(1));
            b1 = b1 | b2;
            if (b1)
                Interpreter.calcul.push(Logo.messages.getString("vrai"));
            else
                Interpreter.calcul.push(Logo.messages.getString("faux"));
        } catch (LogoException e) {
        }
        Interpreter.operande = true;
    }

    private void subtractOp(Stack<String> param) {
        Interpreter.operande = true;
        try {
            Interpreter.calcul.push(kernel.getCalculator().substract(param));
        } catch (LogoException e) {
        }
    }

    private void addOp(Stack<String> param) {
        Interpreter.operande = true;
        Interpreter.calcul.push(kernel.getCalculator().add(param));
    }

    private void divideOp(Stack<String> param) {
        Interpreter.operande = true;
        try {
            Interpreter.calcul.push(kernel.getCalculator().divide(param));
        } catch (LogoException e) {
        }
    }

    private void multiplyOp(Stack<String> param) {
        Interpreter.operande = true;
        Interpreter.calcul.push(kernel.getCalculator().multiply(param));
    }

    private void output(Stack<String> param) {
        try {
            kernel.primitive.retourne(param.get(0));
        } catch (LogoException e) {
        }
    }

    private void remainder(Stack<String> param) {
        Interpreter.operande = true;
        try {
            Interpreter.calcul.push(kernel.getCalculator().remainder(param.get(0), param.get(1)));
        } catch (LogoException e) {
        }
    }

    private void divide(Stack<String> param) {
        Interpreter.operande = true;
        try {
            Interpreter.calcul.push(kernel.getCalculator().divide(param));
        } catch (LogoException e) {
        }
    }

    private void product(Stack<String> param) {
        Interpreter.calcul.push(kernel.getCalculator().multiply(param));
        Interpreter.operande = true;
    }

    private void minus(Stack<String> param) {
        try {
            Interpreter.calcul.push(kernel.getCalculator().minus(param.get(0)));
            Interpreter.operande = true;
        } catch (LogoException e) {
        }
    }

    private void difference(Stack<String> param) {
        Interpreter.operande = true;
        try {
            Interpreter.calcul.push(kernel.getCalculator().substract(param));
        } catch (LogoException e) {
        }
    }

    private void sum(Stack<String> param) {
        Interpreter.operande = true;
        Interpreter.calcul.push(kernel.getCalculator().add(param));
    }

    private void penPaint(Stack<String> param) {
        kernel.getActiveTurtle().setPenReverse(false);
        kernel.getActiveTurtle().setPenDown(true);
        kernel.getActiveTurtle().penColor = kernel.getActiveTurtle().imageColorMode;
    }

    private void penReverse(Stack<String> param) {
        kernel.getActiveTurtle().setPenDown(true);
        kernel.getActiveTurtle().setPenReverse(true);
    }

    private void penErase(Stack<String> param) {
        kernel.getActiveTurtle().setPenDown(true);
        // if mode penerase isn't active yet
        if (kernel.getActiveTurtle().imageColorMode.equals(kernel.getActiveTurtle().penColor)) {
            kernel.getActiveTurtle().imageColorMode = kernel.getActiveTurtle().penColor;
            kernel.getActiveTurtle().penColor = cadre.getDrawPanel().getScreenColor();
        }
    }

    private void penDown(Stack<String> param) {
        kernel.getActiveTurtle().setPenDown(true);
    }

    private void penUp(Stack<String> param) {
        kernel.getActiveTurtle().setPenDown(false);
    }

    private void setHeading(Stack<String> param) {
        delay();
        try {
            if (DrawPanel.windowMode != DrawPanel.WINDOW_3D)
                cadre.getDrawPanel().td(360 - kernel.getActiveTurtle().heading
                        + kernel.getCalculator().numberDouble(param.pop()));
            else {
                cadre.getDrawPanel().setHeading(kernel.getCalculator().numberDouble(param.pop()));
            }
        } catch (LogoException e) {
        }
    }

    private void setXY(Stack<String> param) {
        delay();
        try {
            primitive2D("drawing.fixexy");
            cadre.getDrawPanel().setPosition(kernel.getCalculator().numberDouble(param.get(0)) + " "
                    + kernel.getCalculator().numberDouble(param.get(1)));
        } catch (LogoException e) {
        }
    }

    private void setY(Stack<String> param) {
        delay();
        try {
            if (DrawPanel.windowMode != DrawPanel.WINDOW_3D) {
                double y = kernel.getCalculator().numberDouble(param.get(0));
                double x = kernel.getActiveTurtle().curX - Logo.config.getImageWidth() / 2;
                cadre.getDrawPanel().setPosition(x + " " + y);
            } else
                cadre.getDrawPanel().setPosition(kernel.getActiveTurtle().X + " " + kernel.getCalculator().numberDouble(param.get(0))
                        + " " + kernel.getActiveTurtle().Z);

        } catch (LogoException e) {
        }
    }

    private void setX(Stack<String> param) {
        delay();
        try {
            if (DrawPanel.windowMode != DrawPanel.WINDOW_3D) {
                double x = kernel.getCalculator().numberDouble(param.get(0));
                double y = Logo.config.getImageHeight() / 2 - kernel.getActiveTurtle().curY;
                cadre.getDrawPanel().setPosition(x + " " + y);
            } else
                cadre.getDrawPanel().setPosition(kernel.getCalculator().numberDouble(param.get(0)) + " " + kernel.getActiveTurtle().Y + " "
                        + kernel.getActiveTurtle().Z);
        } catch (LogoException e) {
        }
    }

    private void setPos(Stack<String> param) {
        delay();
        try {
            String list = getFinalList(param.get(0));
            cadre.getDrawPanel().setPosition(list);
        } catch (LogoException e) {
        }
    }

    private void home(Stack<String> param) {
        delay();
        cadre.getDrawPanel().home();
    }

    private void stop(Stack<String> param) {
        try {
            kernel.primitive.stop();
        } catch (LogoException e) {
        }
    }

    private void _if(Stack<String> param) {
        try {
            String liste = getList(param.get(1));
            liste = new String(Utils.formatCode(liste));
            String liste2 = null;
            boolean predicat = predicat(param.get(0));
            InstructionBuffer instruction = cadre.getKernel().getInstructionBuffer();
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
                } catch (Exception e) {
                }
            }
            kernel.primitive.si(predicat, liste, liste2);
            Interpreter.renvoi_instruction = true;
        } catch (LogoException e) {
        }
    }

    private void print(Stack<String> param) {
        int size = param.size();
        String result = "";
        for (int i = 0; i < size; i++) {
            String par = param.get(i).trim();
            if (isList(par))
                par = formatList(par.substring(1, par.length() - 1));
            String mot = getWord(param.get(i));
            if (null == mot)
                result += Utils.unescapeString(par) + " ";
            else
                result += Utils.unescapeString(mot) + " ";
        }
        cadre.updateHistory("perso", result + "\n");
    }

    private void showTurtle(Stack<String> param) {
        if (!kernel.getActiveTurtle().isVisible()) {
            cadre.getDrawPanel().toggleTurtle();
            cadre.getDrawPanel().visibleTurtles.push(String.valueOf(kernel.getActiveTurtle().id));
        }
        kernel.getActiveTurtle().setVisible(true);
    }

    private void hideTurtle(Stack<String> param) {
        if (kernel.getActiveTurtle().isVisible()) {
            cadre.getDrawPanel().toggleTurtle();
            cadre.getDrawPanel().visibleTurtles.remove(String
                    .valueOf(kernel.getActiveTurtle().id));
        }
        kernel.getActiveTurtle().setVisible(false);
    }

    private void clearScreen() {
        cadre.getDrawPanel().clearScreen();
    }

    private void clearScreenWrap(Stack<String> param) {
        cadre.getDrawPanel().clearScreen();
    }

    private void repeat(Stack<String> param) {
        try {
            String liste = getList(param.get(1));
            kernel.primitive.repete(kernel.getCalculator().getInteger(param.get(0)), liste);
        } catch (LogoException e) {
        }
    }

    private void power(Stack<String> param) {
        try {
            Interpreter.operande = true;
            Interpreter.calcul.push(kernel.getCalculator().power(param.get(0), param.get(1)));
        } catch (LogoException e) {
        }
    }

    private void left(Stack<String> param) {
        delay();
        try {
            cadre.getDrawPanel().td(-kernel.getCalculator().numberDouble(param.pop()));

        } catch (LogoException e) {
        }
    }

    private void right(Stack<String> param) {
        delay();
        try {
            cadre.getDrawPanel().td(kernel.getCalculator().numberDouble(param.pop()));
        } catch (LogoException e) {
        }
    }

    private void back(Stack<String> param) {
        delay();
        try {
            cadre.getDrawPanel().move(-kernel.getCalculator().numberDouble(param.pop()));
        } catch (LogoException e) {
        }
    }

    private void forward(Stack<String> param) {
        delay();
        try {
            cadre.getDrawPanel().move(kernel.getCalculator().numberDouble(param.pop()));
        } catch (LogoException e) {
        }
        return;
    }

    /**
     * This method tests if the primitive name exist in 2D mode
     *
     * @param name The primitive name
     * @throws LogoException
     */
    private void primitive2D(String name) throws LogoException {
        if (DrawPanel.windowMode == DrawPanel.WINDOW_3D)
            throw new LogoException(cadre, Utils.primitiveName(name) + " "
                    + Logo.messages.getString("error.primitive2D"));
    }

    /**
     * This method tests if the primitive name exist in 2D mode
     *
     * @param name The primitive name
     * @throws LogoException
     */
    private void primitive3D(String name) throws LogoException {
        if (DrawPanel.windowMode != DrawPanel.WINDOW_3D)
            throw new LogoException(cadre, Utils.primitiveName(name) + " "
                    + Logo.messages.getString("error.primitive3D"));
    }

    /**
     * Returns the code [r g b] for the color i
     *
     * @param i Integer representing the Color
     */
    private void colorCode(int i) {
        Interpreter.operande = true;
        Color co = DrawPanel.defaultColors[i];
        Interpreter.calcul.push("[ " + co.getRed() + " " + co.getGreen() + " "
                + co.getBlue() + " ] ");
    }

    /**
     * Save all procedures whose name are contained in the Stack pile
     *
     * @param fichier The patch to the saved file
     * @param pile    Stack Stack containing all procedure names
     */
    private void saveProcedures(String fichier, Stack<String> pile) {// throws
        // LogoException
        // {
        try {
            String aecrire = "";
            boolean bool = true;
            if (!fichier.endsWith(".lgo"))
                fichier += ".lgo";
            String path = Utils.unescapeString(Logo.config.getDefaultFolder())
                    + File.separator + fichier;
            try {
                for (int i = 0; i < wp.getNumberOfProcedure(); i++) {
                    Procedure procedure = wp.getProcedure(i);
                    if (null == pile)
                        bool = true;
                    else
                        bool = (pile.search(procedure.name) != -1);
                    if (procedure.affichable && bool) {
                        aecrire += Logo.messages.getString("pour") + " "
                                + procedure.name;
                        for (int j = 0; j < procedure.nbparametre; j++) {
                            aecrire += " :" + procedure.variable.get(j);
                        }
                        aecrire += "\n" + procedure.instruction
                                + Logo.messages.getString("fin") + "\n\n";
                    }
                }
            } catch (NullPointerException ex) {
            } // Si aucune procédure n'a été définie.
            Utils.writeLogoFile(path, aecrire);
        } catch (IOException e2) {
            cadre.updateHistory("erreur", Logo.messages.getString("error.ioecriture"));
        }
    }

    /**
     * Returns the Image defined by the path "chemin"
     *
     * @param path The absolute path for the image
     * @return BufferedImage defined by the path "chemin"
     * @throws LogoException If Image format isn't valid(jpg or png)
     */
    private BufferedImage getImage(String path) throws LogoException {
        BufferedImage image = null;
        String pathWord = getWord(path);
        if (null == pathWord) throw new LogoException(cadre, path + " "
                + Logo.messages.getString("error.word"));
        if (!(pathWord.endsWith(".png") || pathWord.endsWith(".jpg")))
            throw new LogoException(cadre, Logo.messages
                    .getString("erreur_format_image"));
        else {
            try {
                pathWord = Utils.unescapeString(pathWord);
                File f = new File(Utils.unescapeString(Logo.config.getDefaultFolder())
                        + File.separator + pathWord);
                image = ImageIO.read(f);
            } catch (Exception e1) {
                throw new LogoException(cadre, Logo.messages
                        .getString("error.iolecture"));
            }
        }
        return image;
    }

    /**
     * Create a local variable called "mot" with no value.
     *
     * @param mot Variable name
     */
    private void createLocaleName(String mot) {
        mot = mot.toLowerCase();
        if (!Interpreter.locale.containsKey(mot)) {
            Interpreter.locale.put(mot, null);
        }
    }

    /**
     * Create a new local variable
     *
     * @param param The variable name or a list of variable names
     * @throws LogoException If "param" isn't a list containing all variable names, or a
     *                       word
     */

    private void local(Stack<String> param) throws LogoException {
        String li = param.get(0);
        if (Primitives.isList(li)) {
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
            } else
                throw new LogoException(cadre, param.get(0)
                        + Logo.messages.getString("error.word"));
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

    private Color rgb(String obj, String name) throws LogoException {
        String liste = getFinalList(obj);
        StringTokenizer st = new StringTokenizer(liste);
        if (st.countTokens() != 3)
            throw new LogoException(cadre, name + " "
                    + Logo.messages.getString("color_3_arguments"));
        int[] entier = new int[3];
        for (int i = 0; i < 3; i++) {
            String element = st.nextToken();
            try {
                entier[i] = (int) (Double.parseDouble(element) + 0.5);
            } catch (NumberFormatException e) {
                throw new LogoException(cadre, element + " "
                        + Logo.messages.getString("pas_nombre"));
            }
            if (entier[i] < 0)
                entier[i] = 0;
            if (entier[i] > 255)
                entier[i] = 255;

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
    private void isMember(Stack<String> param, int id) throws LogoException {
        Interpreter.operande = true;
        String mot_retourne = null;
        boolean b = false;
        String mot = getWord(param.get(1));
        String liste = "[ ";
        if (null == mot) { // on travaille sur une liste
            try {
                liste = getFinalList(param.get(1));
                StringTokenizer st = new StringTokenizer(liste);
                liste = "[ ";
                mot = getWord(param.get(0));
                String str;
                if (null != mot && mot.equals(""))
                    mot = "\\v";
                if (null == mot)
                    mot = param.get(0).trim();
                while (st.hasMoreTokens()) {
                    str = st.nextToken();
                    if (str.equals("["))
                        str = extractList(st);
                    if (!liste.equals("[ "))
                        liste += str + " ";
                    if (str.equals(mot) && liste.equals("[ ")) {
                        if (id == 69) {
                            b = true;
                            break;
                        } else
                            liste += str + " ";
                    }
                }
            } catch (LogoException ex) {
            }
        } else { // on travaille sur un mot
            String mot2 = getWord(param.get(0));
            if (null != mot2) {
                boolean backslash = false;
                for (int i = 0; i < mot.length(); i++) {
                    char c = mot.charAt(i);
                    if (!backslash && c == '\\')
                        backslash = true;
                    else {
                        String tmp = Character.toString(c);
                        if (backslash)
                            tmp = "\\" + tmp;
                        if (tmp.equals(mot2)) {
                            if (id == 69) {
                                b = true;
                                break;
                            } else {
                                if (!backslash)
                                    mot_retourne = mot.substring(i);
                                else
                                    mot_retourne = mot.substring(i - 1);
                                break;
                            }
                        }
                        backslash = false;
                    }
                }
            }
        }
        if (!liste.equals("[ "))
            Interpreter.calcul.push(liste + "] ");
        else if (null != mot_retourne) {
            try {
                Double.parseDouble(mot_retourne);
                Interpreter.calcul.push(mot_retourne);
            } catch (NumberFormatException e) {
                Interpreter.calcul.push(debut_chaine + mot_retourne);
            }
        } else if (b)
            Interpreter.calcul.push(Logo.messages.getString("vrai"));
        else
            Interpreter.calcul.push(Logo.messages.getString("faux"));
    }

    /**
     * Primitive before?
     *
     * @param param Stack that contains all arguments
     * @throws LogoException Bad argument type
     */

    private void isBefore(Stack<String> param) throws LogoException {
        Interpreter.operande = true;
        boolean b = false;
        String[] ope = {"", ""};
        String mot = "";
        for (int i = 0; i < 2; i++) {
            mot = getWord(param.get(i));
            if (null == mot)
                throw new LogoException(cadre, param.get(i) + " "
                        + Logo.messages.getString("pas_mot"));
            else
                ope[i] = mot;
        }
        if (ope[1].compareTo(ope[0]) > 0)
            b = true;
        if (b)
            mot = Logo.messages.getString("vrai");
        else
            mot = Logo.messages.getString("faux");
        Interpreter.calcul.push(mot);
    }

    private void isLessThanOrEqual(Stack<String> param) {
        Interpreter.operande = true;
        Interpreter.calcul.push(kernel.getCalculator().infequal(param));
    }

    private void isGreatherThanOrEqual(Stack<String> param) {
        Interpreter.operande = true;
        Interpreter.calcul.push(kernel.getCalculator().supequal(param));
    }

    private void isLessThan(Stack<String> param) {
        Interpreter.operande = true;
        Interpreter.calcul.push(kernel.getCalculator().inf(param));
    }

    private void isGreaterThan(Stack<String> param) {
        Interpreter.operande = true;
        Interpreter.calcul.push(kernel.getCalculator().sup(param));
    }

    /**
     * / Primitive equal?
     *
     * @param param Stack that contains all arguments
     */
    private void isEqual(Stack<String> param) {
        try {
            Double.parseDouble(param.get(0));
            Double.parseDouble(param.get(1));
            Interpreter.calcul.push(kernel.getCalculator().equal(param));
        } catch (NumberFormatException e) {
            if (param.get(0).equals(param.get(1)))
                Interpreter.calcul.push(Logo.messages.getString("vrai"));
            else
                Interpreter.calcul.push(Logo.messages.getString("faux"));
        }
        Interpreter.operande = true;
    }

    /**
     * this method returns the boolean corresponding to the string st
     *
     * @param st true or false
     * @return The boolean corresponding to the string st
     * @throws LogoException If st isn't equal to true or false
     */

    private boolean predicat(String st) throws LogoException {
        if (st.toLowerCase().equals(Logo.messages.getString("vrai")))
            return true;
        else if (st.toLowerCase().equals(Logo.messages.getString("faux")))
            return false;
        else
            throw new LogoException(cadre, st + " "
                    + Logo.messages.getString("pas_predicat"));

    }

    /**
     * Returns the word contained in st. If it isn't a word, returns null
     *
     * @param st The Object to convert
     * @return The word corresponding to st
     */
    private String getWord(Object st) { // Si c'est un mot
        String liste = st.toString();
        if (liste.equals("\"")) {
            debut_chaine = "";
            return "";
        }
        if (liste.length() > 0 && liste.charAt(0) == '"') {
            debut_chaine = "\"";
            return (liste.substring(1));
        } else
            try {
                if (liste == String.valueOf(Double.parseDouble(liste)))
                    debut_chaine = "";
                else debut_chaine = "\"";
                return Utils.unescapeString(liste);
            } catch (NumberFormatException e) {
            }
        return (null);
    }

    /**
     * Returns the list contained in the string li without any lineNumber
     *
     * @param li The String corresponding to the list
     * @return A list without any line Number tag (\0, \1, \2 ...)
     * @throws LogoException List bad format
     */

    private String getFinalList(String li) throws LogoException {
        // remove line number
        li = li.replaceAll("\\\\l([0-9])+ ", "");
        // return list
        return getList(li);
    }

    /**
     * Returns the list contained in the string li
     *
     * @param li The String corresponding to the list
     * @return A list with line Number tag (\0, \1, \2 ...)
     * @throws LogoException List bad format
     */
    private String getList(String li) throws LogoException {
        li = li.trim();
        // Retourne la liste sans crochets;
        if (li.charAt(0) == '['
                && li.endsWith("]")) {
            li = li.substring(1, li.length() - 1).trim() + " ";
            if (!li.equals(" "))
                return li;
            else
                return ("");
        } else
            throw new LogoException(cadre, li + " "
                    + Logo.messages.getString("pas_liste"));
    }

    // Format the List (only one space between two elements)
    private String formatList(String li) {
        String tampon = "";
        String precedent = "";
        StringTokenizer st = new StringTokenizer(li, " []", true);
        String element = "";
        while (st.hasMoreTokens()) {
            element = st.nextToken();
            while (st.hasMoreTokens() && element.equals(" ")) {
                element = st.nextToken();
            }
            if (element.equals("]"))
                tampon = tampon.trim() + "] ";
            else if (element.equals("[")) {
                if (precedent.equals("["))
                    tampon += "[";
                else
                    tampon = tampon.trim() + " [";
            } else
                tampon += element + " ";
            precedent = element;
        }
        return (tampon.trim());
    }

    private String extractList(StringTokenizer st) {
        int compteur = 1;
        String crochet = "[ ";
        String element = "";
        while (st.hasMoreTokens()) {
            element = st.nextToken();
            if (element.equals("[")) {
                compteur++;
                crochet += "[ ";
            } else if (!element.equals("]"))
                crochet += element + " ";
            else if (compteur != 1) {
                compteur--;
                crochet += "] ";
            } else {
                crochet += element + " ";
                break;
            }
        }
        element = crochet;
        compteur = 0;
        return element.trim();
    }

    private int extractList(String st, int deb) {
        int compteur = 1;
        char element;
        boolean espace = true;
        boolean crochet_ouvert = false;
        boolean crochet_ferme = false;
        for (int i = deb; i < st.length(); i++) {
            element = st.charAt(i);
            if (element == '[') {
                if (espace)
                    crochet_ouvert = true;
                espace = false;
                crochet_ferme = false;
            } else if (element == ']') {
                if (espace)
                    crochet_ferme = true;
                espace = false;
                crochet_ouvert = false;
            } else if (element == ' ') {
                espace = true;
                if (crochet_ouvert) {
                    compteur++;
                    crochet_ouvert = false;
                } else if (crochet_ferme) {
                    crochet_ferme = false;
                    if (compteur != 1)
                        compteur--;
                    else {
                        compteur = i;
                        break;
                    }
                }
            }
        }
        return compteur;
    }

    // returns how many elements contains the list "liste"
    private int numberOfElements(String liste) { // calcule le nombre
        // d'éléments dans une
        // liste
        StringTokenizer st = new StringTokenizer(liste);
        int i = 0;
        String element = "";
        while (st.hasMoreTokens()) {
            element = st.nextToken();
            if (element.equals("["))
                element = extractList(st);
            i++;
        }
        return i;
    }

    // returns the item "i" from the list "liste"
    private String item(String liste, int i) throws LogoException { // retourne
        // l'élément i d'une
        // liste
        StringTokenizer st = new StringTokenizer(liste);
        String element = "";
        int j = 0;
        while (st.hasMoreTokens()) {
            j++;
            element = st.nextToken();
            if (element.equals("["))
                element = extractList(st);
            if (j == i)
                break;
        }
        if (j != i)
            throw new LogoException(cadre, Logo.messages.getString("y_a_pas")
                    + " " + i + " "
                    + Logo.messages.getString("element_dans_liste") + liste
                    + "]");
        else if (i == 0 && j == 0)
            throw new LogoException(cadre, Logo.messages.getString("liste_vide"));
        try {
            Double.parseDouble(element);
            return element;
        } // Si c'est un nombre, on le renvoie.
        catch (Exception e) {
        }
        if (element.startsWith("["))
            return element + " "; // C'est une liste, on la renvoie telle
        // quelle.
        if (element.equals("\\v"))
            element = "";
        return "\"" + element; // C'est forcément un mot, on le renvoie.
    }

    // Test if the name of the variable is valid
    private void isVariableName(String st) throws LogoException {
        if (st.equals(""))
            throw new LogoException(cadre, Logo.messages
                    .getString("variable_vide"));
        if (":+-*/() []=<>&|".indexOf(st) > -1)
            throw new LogoException(cadre, st + " "
                    + Logo.messages.getString("erreur_variable"));

        try {
            Double.parseDouble(st);
            throw new LogoException(cadre, Logo.messages
                    .getString("erreur_nom_nombre_variable"));
        } catch (NumberFormatException e) {

        }

    }

    // primitve make
    private void globalMake(Stack<String> param) throws LogoException {
        String mot = getWord(param.get(0));
        if (null == mot)
            throw new LogoException(cadre, param.get(0) + " "
                    + Logo.messages.getString("error.word"));
        mot = mot.toLowerCase();
        isVariableName(mot);
        if (Interpreter.locale.containsKey(mot)) {
            Interpreter.locale.put(mot, param.get(1));
        } else {
            wp.globals.put(mot, param.get(1));
        }
    }

    private void delay() {
        if (Logo.config.getTurtleSpeed() != 0) {
            try {
                Thread.sleep(Logo.config.getTurtleSpeed() * 5);
            } catch (InterruptedException e) {
            }
        }
    }

    // How many characters in the word "mot"
    private int getWordLength(String mot) {// retourne le nombre de caractères
        // d'un mot
        int compteur = 0;
        boolean backslash = false;
        for (int i = 0; i < mot.length(); i++) {
            if (!backslash && mot.charAt(i) == '\\')
                backslash = true;
            else {
                backslash = false;
                compteur++;
            }
        }
        return compteur;
    }

    // the character number "i" in the word "mot"
    private String itemWord(int entier, String mot) throws LogoException {
        String reponse = "";
        int compteur = 1;
        boolean backslash = false;
        if (mot.equals(""))
            throw new LogoException(cadre, Logo.messages.getString("mot_vide"));
        for (int i = 0; i < mot.length(); i++) {
            char c = mot.charAt(i);
            if (!backslash && c == '\\')
                backslash = true;
            else {
                if (compteur == entier) {
                    if (backslash)
                        reponse = "\\" + c;
                    else
                        reponse = Character.toString(c);
                    break;
                } else {
                    compteur++;
                    backslash = false;
                }
            }
        }
        return reponse;
    }

    protected void setWorkspace(Workspace workspace) {
        wp = workspace;
    }

    private void or(Stack<String> param) {
        int size = param.size();
        boolean result = false;
        boolean b;
        try {
            for (int i = 0; i < size; i++) {
                b = predicat(param.get(i));
                result = result | b;
            }
            if (result)
                Interpreter.calcul.push(Logo.messages.getString("vrai"));
            else
                Interpreter.calcul.push(Logo.messages.getString("faux"));
            Interpreter.operande = true;
        } catch (LogoException e) {
        }
    }

    private void and(Stack<String> param) {
        int size = param.size();
        boolean result = true;
        boolean b;
        try {
            for (int i = 0; i < size; i++) {
                b = predicat(param.get(i));
                result = result & b;
            }
            Interpreter.operande = true;
            if (result)
                Interpreter.calcul.push(Logo.messages.getString("vrai"));
            else
                Interpreter.calcul.push(Logo.messages.getString("faux"));
        } catch (LogoException e) {
        }
    }

    /**
     * This methods returns a list that contains all procedures name
     *
     * @return A list with all procedure names
     */
    private StringBuffer getAllProcedures() {
        StringBuffer sb = new StringBuffer("[ ");
        for (int i = 0; i < wp.getNumberOfProcedure(); i++) {
            Procedure proc = wp.getProcedure(i);
            if (proc.affichable) {
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

    private StringBuffer getAllVariables() {
        StringBuffer sb = new StringBuffer("[ ");
        Iterator<String> it = Interpreter.locale.keySet().iterator();
        while (it.hasNext()) {
            String name = it.next();
            sb.append(name);
            sb.append(" ");
        }
        it = wp.globals.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            if (!Interpreter.locale.containsKey(key)) {
                sb.append(key);
                sb.append(" ");
            }
        }
        sb.append("] ");
        return sb;
    }

    /**
     * This methods returns a list that contains all Property Lists name
     *
     * @return A list with all Property Lists names
     */
    private StringBuffer getAllpropertyLists() {
        StringBuffer sb = new StringBuffer("[ ");
        Iterator<String> it = wp.getPropListKeys().iterator();
        while (it.hasNext()) {
            sb.append(it.next());
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
    private void deleteVariable(String name) {
        if (!Interpreter.locale.isEmpty()) {
            Interpreter.locale.remove(name);
        } else {
            wp.deleteVariable(name);
        }
    }

    /**
     * Delete the procedure called "name" from the workspace
     *
     * @param name The procedure name
     */
    private void deleteProcedure(String name) {
        for (int i = 0; i < wp.getNumberOfProcedure(); i++) {
            Procedure procedure = wp.getProcedure(i);
            if (procedure.name.equals(name)
                    && procedure.affichable == true) {
                wp.deleteProcedure(i);
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

    private void erase(String name, String type) {
        Interpreter.operande = false;
        try {
            if (Primitives.isList(name)) {
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
                } else
                    throw new LogoException(cadre, name
                            + Logo.messages.getString("error.word"));

            }
        } catch (LogoException e) {
        }
    }

    /**
     * According to the type of the data, erase from workspace the resource called "name"
     *
     * @param name The name of the deleted resource
     * @param type The type for the data, it could be "variable", "procedure" or "propertylist"
     */
    private void eraseItem(String name, String type) {
        if (type.equals("procedure")) {
            this.deleteProcedure(name);
        } else if (type.equals("variable")) {
            this.deleteVariable(name);
        } else if (type.equals("propertylist")) {
            wp.removePropList(name);
        }

    }
}