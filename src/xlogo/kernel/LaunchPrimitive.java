/**
 * Title :        XLogo
 * Description :  XLogo is an interpreter for the Logo
 * programming language
 *
 * @author Loïc Le Coq
 */
package xlogo.kernel;

import xlogo.gui.Application;
import xlogo.Logo;
import xlogo.gui.HistoryPanel;
import xlogo.gui.InputFrame;
import xlogo.gui.MessageTextArea;
import xlogo.gui.preferences.FontPanel;
import xlogo.kernel.gui.GuiButton;
import xlogo.kernel.gui.GuiMenu;
import xlogo.kernel.network.NetworkClientChat;
import xlogo.kernel.network.NetworkClientExecute;
import xlogo.kernel.network.NetworkClientSend;
import xlogo.kernel.network.NetworkServer;
import xlogo.kernel.perspective.ElementLine;
import xlogo.kernel.perspective.ElementPoint;
import xlogo.kernel.perspective.ElementPolygon;
import xlogo.utils.LogoException;
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
import java.util.*;

/*******************************************************************************
 * When a primitive or a procedure has all arguments, LauchPrimitive executes
 * the appropriate code.
 ******************************************************************************/
public class LaunchPrimitive {
    /**
     * Default Application frame
     */
    private final Application cadre;
    /**
     * Default kernel
     */
    private final Kernel kernel;
    /**
     * Default workspace
     */
    private Workspace wp;
    private Procedure procedure;
    // private MathContext mc=MathContext.DECIMAL64;
    /**
     * This is the start for the String returned by primitive or procedure.<br>
     * It is "\"" for words and "" for numbers. <br>
     * <br>
     *
     * Ceci est le début de la chaine générique renvoyé par les primitives<br>
     * Elle vaut "\"" pour les mots et "" pour les nombres<br>
     */
    private String debut_chaine = "";
    /***************************************************************************
     * When we launch the primitive "listentcp", we have to save workspaces
     **************************************************************************/
    private Stack<Workspace> savedWorkspace;

    /**
     * @param cadre
     *            Default frame Application
     * @param wp
     *            Default workspace
     */
    public LaunchPrimitive(Application cadre, Workspace wp) {
        this.wp = wp;
        this.cadre = cadre;
        this.kernel = cadre.getKernel();
    }

    /**
     * Tests if "li" is a list
     *
     * @param li
     *            The String to test
     * @return true if it is a list, else false
     */
    //
    protected static boolean isList(String li) {
        li = li.trim();
        return li.length() > 0 && li.charAt(0) == '['
                && li.substring(li.length() - 1).equals("]");
    }

    /**
     * Execute the primitive number "id" with the arguments contained in "param"<br>
     * <ul>
     * <li> if id<0: it is a procedure. <br>
     * For example, if id=-3, it is procedure number -i-2=-(-3)-2=1 </li>
     * <li> if d>=0: it is primitive number "id"</li>
     * </ul>
     *
     * @param id
     *            The number representing the procedure or the primitive
     * @param param
     *            The Stack that contains all arguments
     */
    protected void execute(int id, Stack<String> param) {
        // identifiant procédure ou primitive, valeur des paramètres
        if (id < 0) {
            procedure = wp.getProcedure(-id - 2);
            Interpreter.stockvariable.push(Interpreter.locale);
            Interpreter.locale = new HashMap<String, String>();
            // Read local Variable
            int optSize = procedure.optVariables.size();
            int normSize = procedure.variable.size();
            for (int j = 0; j < optSize + normSize; j++) {
                // Add local Variable
                if (j < normSize) {
                    Interpreter.locale.put(procedure.variable.get(j), param.get(j));
                }    // add optional variables
                else {
                    String value = "";
                    if (j < param.size()) value = param.get(j);
                    else value = procedure.optVariablesExp.get(j - param.size()).toString();
                    Interpreter.locale.put(procedure.optVariables.get(j - normSize), value);

                }
            }
            // Add Optionnal variable
            if (Kernel.mode_trace) {
                StringBuffer buffer = new StringBuffer();
                for (int i = 0; i < Interpreter.en_cours.size(); i++) buffer.append("  ");
                buffer.append(procedure.name);
                for (int i = 0; i < param.size(); i++) buffer.append(" " + Utils.unescapeString(param.get(i)));
                String msg = buffer + "\n";
                cadre.updateHistory("normal", msg);
            }
            Interpreter.en_cours.push(procedure.name);

            // Add Procedure code in Interpreter.instruction
            kernel.getInstructionBuffer().insert("\n ");
            kernel.getInstructionBuffer().insertCode(procedure.instr);
//			System.out.println("instr " +Interpreter.instruction);
            //		System.out.println("stock "+Interpreter.stockInstruction);
// System.out.println("a"+Interpreter.instruction+"a");
            Interpreter.nom.push("\n");
        } else {
            switch (id) {
                case 0: // av
                    delay();
                    try {
                        cadre.getDrawPanel().move(kernel.getCalculator().numberDouble(param.pop()));
                    } catch (LogoException e) {
                    }
                    break;
                case 1: // re
                    delay();
                    try {
                        cadre.getDrawPanel().move(-kernel.getCalculator().numberDouble(param.pop()));
                    } catch (LogoException e) {
                    }
                    break;
                case 2: // td
                    delay();
                    try {
                        cadre.getDrawPanel().td(kernel.getCalculator().numberDouble(param.pop()));
                    } catch (LogoException e) {
                    }
                    break;
                case 3: // tg
                    delay();
                    try {
                        cadre.getDrawPanel().td(-kernel.getCalculator().numberDouble(param.pop()));

                    } catch (LogoException e) {
                    }
                    break;
                case 4: // arithmetic.power puissance
                    try {
                        Interpreter.operande = true;
                        Interpreter.calcul.push(kernel.getCalculator().power(param.get(0), param.get(1)));
                    } catch (LogoException e) {
                    }
                    break;
                case 5: // repete controls.repeat
                    try {
                        String liste = getList(param.get(1));
                        kernel.primitive.repete(kernel.getCalculator().getInteger(param.get(0)), liste);
                    } catch (LogoException e) {
                    }
                    break;
                case 6: // ve
                    cadre.getDrawPanel().clearScreen();
                    break;
                case 7: // ct
                    if (kernel.getActiveTurtle().isVisible()) {
                        cadre.getDrawPanel().toggleTurtle();
                        cadre.getDrawPanel().visibleTurtles.remove(String
                                .valueOf(kernel.getActiveTurtle().id));
                    }
                    kernel.getActiveTurtle().setVisible(false);
                    break;
                case 8: // mt
                    if (!kernel.getActiveTurtle().isVisible()) {
                        cadre.getDrawPanel().toggleTurtle();
                        cadre.getDrawPanel().visibleTurtles.push(String.valueOf(kernel.getActiveTurtle().id));
                    }
                    kernel.getActiveTurtle().setVisible(true);
                    break;
                case 9: // ecris, ec
                    int size = param.size();
                    String result = "";
                    String mot;
                    for (int i = 0; i < size; i++) {
                        String par = param.get(i).trim();
                        if (isList(par))
                            par = formatList(par.substring(1, par.length() - 1));
                        mot = getWord(param.get(i));
                        if (null == mot)
                            result += Utils.unescapeString(par) + " ";
                        else
                            result += Utils.unescapeString(mot) + " ";
                    }
                    cadre.updateHistory("perso", result + "\n");
                    break;
                case 10: // si // if
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
                    break;
                case 11: // STOP
                    try {
                        kernel.primitive.stop();
                    } catch (LogoException e) {
                    }
                    break;
                case 12: // origine
                    delay();
                    cadre.getDrawPanel().home();
                    break;
                case 13: // fpos
                    delay();
                    try {
                        String list = getFinalList(param.get(0));
                        cadre.getDrawPanel().setPosition(list);
                    } catch (LogoException e) {
                    }
                    break;
                case 14: // fixex
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
                    break;
                case 15: // fixey
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
                    break;
                case 16: // fixexy
                    delay();
                    try {
                        primitive2D("drawing.fixexy");
                        cadre.getDrawPanel().setPosition(kernel.getCalculator().numberDouble(param.get(0)) + " "
                                + kernel.getCalculator().numberDouble(param.get(1)));
                    } catch (LogoException e) {
                    }
                    break;
                case 17: // fixecap
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
                    break;
                case 18: // lc
                    kernel.getActiveTurtle().setPenDown(false);
                    break;
                case 19: // bc
                    kernel.getActiveTurtle().setPenDown(true);
                    break;
                case 20: // gomme
                    kernel.getActiveTurtle().setPenDown(true);
                    // if mode penerase isn't active yet
                    if (kernel.getActiveTurtle().imageColorMode.equals(kernel.getActiveTurtle().penColor)) {
                        kernel.getActiveTurtle().imageColorMode = kernel.getActiveTurtle().penColor;
                        kernel.getActiveTurtle().penColor = cadre.getDrawPanel().getScreenColor();
                    }
                    break;
                case 21: // inversecrayon
                    kernel.getActiveTurtle().setPenDown(true);
                    kernel.getActiveTurtle().setPenReverse(true);
                    break;
                case 22: // dessine
                    kernel.getActiveTurtle().setPenReverse(false);
                    kernel.getActiveTurtle().setPenDown(true);
                    kernel.getActiveTurtle().penColor = kernel.getActiveTurtle().imageColorMode;
                    break;
                case 23: // somme
                    Interpreter.operande = true;
                    Interpreter.calcul.push(kernel.getCalculator().add(param));
                    break;

                case 24: // difference
                    Interpreter.operande = true;
                    try {
                        Interpreter.calcul.push(kernel.getCalculator().substract(param));
                    } catch (LogoException e) {
                    }
                    break;
                case 25: // arithmetic.minus moins (opposé)
                    try {
                        Interpreter.calcul.push(kernel.getCalculator().minus(param.get(0)));
                        Interpreter.operande = true;
                    } catch (LogoException e) {
                    }

                    break;
                case 26: // produit
                    Interpreter.calcul.push(kernel.getCalculator().multiply(param));
                    Interpreter.operande = true;
                    break;
                case 27: // div
                    Interpreter.operande = true;
                    try {
                        Interpreter.calcul.push(kernel.getCalculator().divide(param));
                    } catch (LogoException e) {
                    }
                    break;
                case 28: // reste
                    Interpreter.operande = true;
                    try {
                        Interpreter.calcul.push(kernel.getCalculator().remainder(param.get(0), param.get(1)));
                    } catch (LogoException e) {
                    }
                    break;
                case 29: // retourne
                    try {
                        kernel.primitive.retourne(param.get(0));
                    } catch (LogoException e) {
                    }
                    break;
                case 30: // *
                    Interpreter.operande = true;
                    Interpreter.calcul.push(kernel.getCalculator().multiply(param));
                    break;
                case 31: // diviser /
                    Interpreter.operande = true;
                    try {
                        Interpreter.calcul.push(kernel.getCalculator().divide(param));
                    } catch (LogoException e) {
                    }
                    break;
                case 32: // +
                    Interpreter.operande = true;
                    Interpreter.calcul.push(kernel.getCalculator().add(param));
                    break;
                case 33: // -
                    Interpreter.operande = true;
                    try {
                        Interpreter.calcul.push(kernel.getCalculator().substract(param));
                    } catch (LogoException e) {
                    }
                    break;
                case 34: // =
                    equal(param);
                    break;
                case 35: // <
                    inf(param);
                    break;
                case 36: // >
                    sup(param);
                    break;
                case 37: // |
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
                    break;
                case 38: // &
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
                    break;
                case 39: // opérateur interne \n signalant une fin de procédure
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
                    break;
                case 40: // opérateur interne \ signalant une fin de boucle

                    LoopProperties loop = Primitive.stackLoop.peek();
                    // LOOP REPEAT
                    if (loop.isRepeat()) {
                        BigDecimal compteur = loop.getCounter();
                        BigDecimal fin = loop.getEnd();
                        if (compteur.compareTo(fin) < 0) {
                            loop.incremente();
                            Primitive.stackLoop.pop();
                            Primitive.stackLoop.push(loop);
                            cadre.getKernel().getInstructionBuffer().insert(loop.getInstr() + Primitive.END_LOOP + " ");
                        } else if (compteur.compareTo(fin) == 0) {
                            Primitive.stackLoop.pop();
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
                            Primitive.stackLoop.pop();
                            Primitive.stackLoop.push(loop);
                            cadre.getKernel().getInstructionBuffer().insert(loop.getInstr() + Primitive.END_LOOP + " ");
                        } else {
                            ((LoopFor) loop).DeleteVar();
                            Primitive.stackLoop.pop();
                        }
                    }
                    // LOOP FOREVER
                    else if (loop.isForEver()) {
                        cadre.getKernel().getInstructionBuffer().insert(loop.getInstr() + Primitive.END_LOOP + " ");
                    }
                    // LOOP FILL POLYGON
                    else if (loop.isFillPolygon()) {
                        cadre.getDrawPanel().stopRecord2DPolygon();
                        Primitive.stackLoop.pop();
                    }
                    break;
                case 41: // pos
                    Interpreter.operande = true;
                    if (DrawPanel.windowMode != DrawPanel.WINDOW_3D) {
                        double a = kernel.getActiveTurtle().curX - Logo.config.getImageWidth() / 2;
                        double b = Logo.config.getImageHeight() / 2 - kernel.getActiveTurtle().curY;
                        Interpreter.calcul.push("[ " + Calculator.teste_fin_double(a) + " " + Calculator.teste_fin_double(b) + " ] ");
                    } else {
                        Interpreter.calcul.push("[ " + kernel.getActiveTurtle().X + " "
                                + kernel.getActiveTurtle().Y + " " + kernel.getActiveTurtle().Z + " ] ");

                    }
                    break;
                case 42: // cap
                    Interpreter.operande = true;
                    Interpreter.calcul.push(Calculator.teste_fin_double(kernel.getActiveTurtle().heading));
                    break;
                case 43: // arrondi
                    Interpreter.operande = true;
                    try {
                        Interpreter.calcul.push(String.valueOf(Math
                                .round(kernel.getCalculator().numberDouble(param.get(0)))));
                    } catch (LogoException e) {
                    }
                    break;
                case 44: // log10
                    Interpreter.operande = true;
                    try {
                        Interpreter.calcul.push(kernel.getCalculator().log10(param.get(0)));
                    } catch (LogoException e) {
                    }
                    break;
                case 45: // arithmetic.sin
                    Interpreter.operande = true;
                    try {
                        Interpreter.calcul.push(kernel.getCalculator().sin(param.get(0)));
                    } catch (LogoException e) {
                    }

                    break;
                case 46: // arithmetic.cos
                    Interpreter.operande = true;
                    try {
                        Interpreter.calcul.push(kernel.getCalculator().cos(param.get(0)));
                    } catch (LogoException e) {
                    }

                    break;
                case 47: // ou
                    ou(param);
                    break;
                case 48: // et
                    et(param);
                    break;
                case 49: // non

                    try {
                        Interpreter.operande = true;
                        boolean b1 = predicat(param.get(0));
                        if (b1)
                            Interpreter.calcul.push(Logo.messages.getString("faux"));
                        else
                            Interpreter.calcul.push(Logo.messages.getString("vrai"));
                    } catch (LogoException e) {
                    }
                    break;
                case 50: // liste
                    String liste = "[ ";
                    Interpreter.operande = true;
                    String mot2;
                    for (int i = 0; i < param.size(); i++) {
                        mot2 = param.get(i);
                        mot = getWord(param.get(i));
                        if (null == mot) {
                            liste += mot2;
                            // System.out.println("a"+mot2+"a");
                        } else {
                            if (mot.equals("")) mot = "\\v";
                            liste += mot + " ";
                        }
                    }
                    Interpreter.calcul.push(liste + "] ");
                    break;
                case 51: // phrase
                    liste = "[ ";
                    Interpreter.operande = true;
                    for (int i = 0; i < param.size(); i++) {
                        mot = getWord(param.get(i));
                        mot2 = param.get(i).trim();
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
                    break;
                case 52: // metspremier
                    try {
                        liste = getFinalList(param.get(1));
                        Interpreter.operande = true;
                        mot = getWord(param.get(0));
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
                    break;
                case 53: // metsdernier
                    try {
                        liste = getFinalList(param.get(1)).trim();
                        Interpreter.operande = true;
                        mot = getWord(param.get(0));
                        if (null != mot && mot.equals("")) mot = "\\v";
                        if (null == mot) { // Si c'est une liste
                            Interpreter.calcul.push(("[ " + liste).trim() + " "
                                    + param.get(0).trim() + " ] ");

                        } else
                            Interpreter.calcul.push(("[ " + liste).trim() + " " + mot + " ] ");
                    } catch (LogoException e) {
                    }
                    break;
                case 54: // inverse liste
                    try {
                        liste = getFinalList(param.get(0)).trim();
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
                    break;
                case 55: // choix
                    Interpreter.operande = true;
                    mot = getWord(param.get(0));
                    if (null == mot) {
                        try {
                            liste = getFinalList(param.get(0));
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
                    break;
                case 56: // enleve
                    Interpreter.operande = true;
                    try {
                        liste = getFinalList(param.get(1));
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
                    break;
                case 57: // item
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
                    break;
                case 58: // saufdernier
                    Interpreter.operande = true;
                    mot = getWord(param.get(0));
                    if (null == mot) {
                        try {
                            liste = getFinalList(param.get(0)).trim();
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
                    break;
                case 59: // saufpremier
                    Interpreter.operande = true;
                    mot = getWord(param.get(0));
                    if (null == mot) {
                        try {
                            liste = getFinalList(param.get(0)).trim();
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


                    break;
                case 60: // dernier
                    Interpreter.operande = true;
                    mot = getWord(param.get(0));
                    if (null == mot) { // Si c'est une liste
                        try {
                            liste = getFinalList(param.get(0));
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
                    break;
                case 61: // premier first
                    Interpreter.operande = true;
                    mot = getWord(param.get(0));
                    if (null == mot) { // SI c'est une liste
                        try {
                            liste = getFinalList(param.get(0));
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
                    break;
                case 62: // compte
                    Interpreter.operande = true;
                    mot = getWord(param.get(0));
                    if (null == mot) {
                        try {
                            liste = getFinalList(param.get(0));
                            Interpreter.calcul.push(String
                                    .valueOf(numberOfElements(liste)));
                        } catch (LogoException e) {
                        }
                    } else
                        Interpreter.calcul.push(String.valueOf(getWordLength(mot)));
                    break;
                case 63: // mot?
                    mot = getWord(param.get(0));
                    if (null == mot)
                        Interpreter.calcul.push(Logo.messages.getString("faux"));
                    else
                        Interpreter.calcul.push(Logo.messages.getString("vrai"));
                    Interpreter.operande = true;
                    break;
                case 64: // nombre?
                    try {
                        Double.parseDouble(param.get(0));
                        Interpreter.calcul.push(Logo.messages.getString("vrai"));
                    } catch (NumberFormatException e) {
                        Interpreter.calcul.push(Logo.messages.getString("faux"));
                    }
                    Interpreter.operande = true;
                    break;
                case 65: // liste?
                    liste = param.get(0).trim();
                    if (isList(liste))
                        Interpreter.calcul.push(Logo.messages.getString("vrai"));
                    else
                        Interpreter.calcul.push(Logo.messages.getString("faux"));
                    Interpreter.operande = true;
                    break;
                case 66: // vide?
                    liste = param.get(0).trim();
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
                    break;
                case 67: // egal?
                    equal(param);
                    break;
                case 68: // precede?
                    try {
                        precede(param);
                    } catch (LogoException e) {
                    }
                    break;
                case 69: // membre ?
                    try {
                        membre(param, id);
                    } catch (LogoException e) {
                    }
                    break;
                case 70: // racine arithmetic.sqrt
                    Interpreter.operande = true;
                    try {
                        Interpreter.calcul.push(kernel.getCalculator().sqrt(param.get(0)));

                    } catch (LogoException e) {
                    }
                    break;
                case 71: // membre
                    try {
                        membre(param, id);
                    } catch (LogoException e) {
                    }
                    break;
                case 72: // donne
                    try {
                        donne(param);
                        Interpreter.operande = false;
                    } catch (LogoException e) {
                    }

                    break;
                case 73: // locale
                    try {
                        locale(param);
                        Interpreter.operande = false;
                    } catch (LogoException e) {
                    }
                    break;
                case 74: // donnelocale
                    try {
                        locale(param);
                        donne(param);
                        Interpreter.operande = false;
                    } catch (LogoException e) {
                    }
                    break;
                case 75: // fcc
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
                    break;
                case 76: // fcfg setscreencolor
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
                    break;
                case 77: // hasard
                    Interpreter.operande = true;
                    try {
                        int i = kernel.getCalculator().getInteger(param.get(0));
                        i = (int) Math.floor(Math.random() * i);
                        Interpreter.calcul.push(String.valueOf(i));
                    } catch (LogoException e) {
                    }
                    break;
                case 78: // attends
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
                    break;
                case 79: // procedures
                    Interpreter.operande = true;
                    Interpreter.calcul.push(new String(getAllProcedures()));
                    break;
                case 80: // effaceprocedure efp
                    erase(param.get(0), "procedure");
                    break;

                case 81: // effacevariable
                    erase(param.get(0), "variable");
                    break;
                case 82: // effacetout erall
                    wp.deleteAllProcedures();
                    wp.deleteAllVariables();
                    wp.deleteAllPropertyLists();
                    cadre.setNewEnabled(false);
                    break;
                case 83: // mot
                    Interpreter.operande = true;
                    result = "";
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
                    break;
                case 84: // etiquette
                    String par = param.get(0).trim();
                    if (isList(par))
                        par = formatList(par.substring(1, par.length() - 1));
                    mot = getWord(param.get(0));
                    if (null == mot)
                        cadre.getDrawPanel().label(Utils.unescapeString(par));
                    else
                        cadre.getDrawPanel().label(Utils.unescapeString(mot));
                    break;
                case 85: // /trouvecouleur
                    if (kernel.getActiveTurtle().isVisible())
                        cadre.getDrawPanel().eraseTurtle(false);
                    try {
                        liste = getFinalList(param.get(0));
                        Color r = cadre.getDrawPanel().guessColorPoint(liste);
                        Interpreter.operande = true;
                        Interpreter.calcul.push("[ " + r.getRed() + " "
                                + r.getGreen() + " " + r.getBlue() + " ] ");
                    } catch (LogoException e) {
                    }
                    if (kernel.getActiveTurtle().isVisible())
                        cadre.getDrawPanel().eraseTurtle(true);
                    break;
                case 86: // fenetre
                    cadre.getDrawPanel().setWindowMode(DrawPanel.WINDOW_CLASSIC);
                    break;
                case 87: // enroule
                    cadre.getDrawPanel().setWindowMode(DrawPanel.WINDOW_WRAP);
                    break;
                case 88: // clos
                    cadre.getDrawPanel().setWindowMode(DrawPanel.WINDOW_CLOSE);
                    break;
                case 89: // videtexte
                    cadre.getHistoryPanel().vide_texte();
                    break;
                case 90: // chargeimage
                    BufferedImage image = null;
                    try {
                        primitive2D("ci");
                        image = getImage(param.get(0));
                    } catch (LogoException e) {
                    }
                    if (null != image)
                        cadre.getDrawPanel().drawImage(image);
                    break;
                case 91: // ftc, fixetaillecrayon
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
                    break;
                case 92: // tantque
                    try {
                        String li1 = getList(param.get(0));
                        li1 = new String(Utils.formatCode(li1));
                        String li2 = getList(param.get(1));
                        li2 = new String(Utils.formatCode(li2));
                        String instr = "\\siwhile " + li1 + "[ " + li2 + "] ";
                        LoopWhile bp = new LoopWhile(BigDecimal.ONE, BigDecimal.ZERO, BigDecimal.ONE, instr);
                        Primitive.stackLoop.push(bp);
                        cadre.getKernel().getInstructionBuffer().insert(instr + Primitive.END_LOOP + " ");
                    } catch (LogoException e) {
                    }

                    break;
                case 93: // lis
                    try {
                        liste = getFinalList(param.get(0));
                        mot = getWord(param.get(1));
                        if (null == mot)
                            throw new LogoException(cadre, Logo.messages
                                    .getString("error.word"));
                        java.awt.FontMetrics fm = cadre.getGraphics()
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
                        donne(param);
                        String texte = liste + "\n" + phrase;
                        cadre.updateHistory("commentaire", Utils.unescapeString(texte) + "\n");
                        cadre.focusCommandLine();
                        inputFrame.dispose();
                        cadre.focusCommandLine();
                    } catch (LogoException e) {
                    }
                    break;
                case 94: // touche?
                    Interpreter.operande = true;
                    if (cadre.getKey() != -1)
                        Interpreter.calcul.push(Logo.messages.getString("vrai"));
                    else
                        Interpreter.calcul.push(Logo.messages.getString("faux"));
                    break;
                case 95: // siwhile --> Evalue l'expression test du while
                    try {
                        liste = getFinalList(param.get(1));
                        boolean predicat = predicat(param.get(0));
                        kernel.primitive.whilesi(predicat, liste);
                    } catch (LogoException e) {
                    }
                    break;
                case 96: // liscar
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
                    break;
                case 97: // remplis
                    cadre.getDrawPanel().fill();
                    break;
                case 98: // point
                    if (kernel.getActiveTurtle().isVisible())
                        cadre.getDrawPanel().eraseTurtle(false);
                    try {
                        cadre.getDrawPanel().point(getFinalList(param.get(0)));
                    } catch (LogoException e) {
                    }
                    if (kernel.getActiveTurtle().isVisible())
                        cadre.getDrawPanel().eraseTurtle(true);
                    break;
                case 99: // vers=towards vers
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
                    break;
                case 100: // distance
                    try {
                        Interpreter.operande = true;
                        double distance = cadre.getDrawPanel().distance(getFinalList(param.get(0)));
                        Interpreter.calcul.push(Calculator.teste_fin_double(distance));
                    } catch (LogoException e) {
                    }
                    break;
                case 101: // couleurcrayon
                    Interpreter.operande = true;
                    Interpreter.calcul.push("[ "
                            + kernel.getActiveTurtle().penColor.getRed() + " "
                            + kernel.getActiveTurtle().penColor.getGreen() + " "
                            + kernel.getActiveTurtle().penColor.getBlue() + " ] ");
                    break;
                case 102: // couleurfond
                    Interpreter.operande = true;
                    Color color = cadre.getDrawPanel().getScreenColor();
                    Interpreter.calcul.push("[ " + color.getRed() + " "
                            + color.getGreen() + " "
                            + color.getBlue() + " ] ");
                    break;
                case 103: // bc?
                    Interpreter.operande = true;
                    if (kernel.getActiveTurtle().isPenDown())
                        Interpreter.calcul.push(Logo.messages.getString("vrai"));
                    else
                        Interpreter.calcul.push(Logo.messages.getString("faux"));
                    break;
                case 104: // visible?
                    Interpreter.operande = true;
                    if (kernel.getActiveTurtle().isVisible())
                        Interpreter.calcul.push(Logo.messages.getString("vrai"));
                    else
                        Interpreter.calcul.push(Logo.messages.getString("faux"));
                    break;
                case 105: // prim?
                    try {
                        Interpreter.operande = true;
                        mot = getWord(param.get(0));
                        if (null == mot)
                            throw new LogoException(cadre, param.get(0) + " "
                                    + Logo.messages.getString("error.word"));
                        if (Primitive.primitives.containsKey(mot))
                            Interpreter.calcul.push(Logo.messages.getString("vrai"));
                        else
                            Interpreter.calcul.push(Logo.messages.getString("faux"));
                    } catch (LogoException e) {
                    }
                    break;
                case 106: // proc?
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
                    break;
                case 107: // exec
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
                    break;
                case 108: // catalogue
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
                    break;
                case 109: // frepertoire
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
                    break;
                case 110: // repertoire
                    Interpreter.operande = true;
                    Interpreter.calcul.push("\"" + Logo.config.getDefaultFolder());
                    break;
                case 111: // sauve
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
                    break;
                case 112: // sauved
                    try {
                        mot = getWord(param.get(0));
                        if (null == mot)
                            throw new LogoException(cadre, param.get(0) + " "
                                    + Logo.messages.getString("error.word"));
                        saveProcedures(mot, null);
                    } catch (LogoException e) {
                    }
                    break;
                case 113: // ramene load
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

                    break;
                case 114: // pi
                    Interpreter.operande = true;
                    Interpreter.calcul.push(kernel.getCalculator().pi());
                    break;
                case 115: // tangente arithmetic.tan
                    Interpreter.operande = true;
                    try {
                        Interpreter.calcul.push(kernel.getCalculator().tan(param.get(0)));
                    } catch (LogoException e) {
                    }

                    break;
                case 116: // acos
                    try {
                        Interpreter.calcul.push(kernel.getCalculator().acos(param.get(0)));
                        Interpreter.operande = true;
                    } catch (LogoException e) {
                    }
                    break;
                case 117: // asin
                    try {
                        Interpreter.calcul.push(kernel.getCalculator().asin(param.get(0)));
                        Interpreter.operande = true;
                    } catch (LogoException e) {
                    }


                    break;
                case 118: // atan
                    try {
                        Interpreter.calcul.push(kernel.getCalculator().atan(param.get(0)));
                        Interpreter.operande = true;
                    } catch (LogoException e) {
                    }
                    break;
                case 119: // vrai
                    Interpreter.operande = true;
                    Interpreter.calcul.push(Logo.messages.getString("vrai"));
                    break;
                case 120: // faux
                    Interpreter.operande = true;
                    Interpreter.calcul.push(Logo.messages.getString("faux"));
                    break;
                case 121: // forme
                    try {
                        primitive2D("turtle.forme");
                        Interpreter.operande = true;
                        Interpreter.calcul.push(String.valueOf(kernel.getActiveTurtle().getShape()));
                    } catch (LogoException e) {
                    }
                    break;
                case 122: // fixeforme setshape
                    try {
                        primitive2D("turtle.fforme");
                        int i = kernel.getCalculator().getInteger(param.get(0)) % 7;
                        if (kernel.getActiveTurtle().id == 0) {
                            Logo.config.setActiveTurtle(i);
                        }
                        kernel.change_image_tortue(i);
                    } catch (LogoException e) {
                    }
                    break;
                case 123: // definis workspace.define
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

                    break;

                case 124: // tortue
                    Interpreter.operande = true;
                    Interpreter.calcul.push(String.valueOf(kernel.getActiveTurtle().id));
                    break;
                case 125: // tortues
                    Interpreter.operande = true;
                    String li = "[ ";
                    for (int i = 0; i < cadre.getDrawPanel().turtles.length; i++) {
                        if (null != cadre.getDrawPanel().turtles[i])
                            li += i + " ";
                    }
                    li += "]";
                    Interpreter.calcul.push(li);
                    break;
                case 126: // fixetortue
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
                                    .setGraphicsFont(new java.awt.Font(police,
                                            java.awt.Font.PLAIN,
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
                    break;
                case 127: // police
                    Interpreter.operande = true;
                    Interpreter.calcul.push(String.valueOf(kernel.getActiveTurtle().police));
                    break;
                case 128: // fixetaillepolice
                    try {
                        int taille = kernel.getCalculator().getInteger(param.get(0));
                        kernel.getActiveTurtle().police = taille;
                        Font police = Logo.config.getFont();
                        cadre.getDrawPanel().setGraphicsFont(police
                                .deriveFont((float) kernel.getActiveTurtle().police));
                    } catch (LogoException e) {
                    }

                    break;
                case 129: // tuetortue
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
                                                .setFont(new java.awt.Font(police,
                                                        java.awt.Font.PLAIN,
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
                    break;
                case 130: // sequence
                    try {
                        liste = getFinalList(param.get(0));
                        cadre.getSoundPlayer().cree_sequence(Utils.formatCode(liste).toString());
                    } catch (LogoException e) {
                    }

                    break;
                case 131: // instrument
                    Interpreter.operande = true;
                    Interpreter.calcul.push(String
                            .valueOf(cadre.getSoundPlayer().getInstrument()));
                    break;
                case 132: // fixeinstrument
                    try {
                        int i = kernel.getCalculator().getInteger(param.get(0));
                        cadre.getSoundPlayer().setInstrument(i);
                    } catch (LogoException e) {
                    }

                    break;
                case 133: // joue
                    cadre.getSoundPlayer().joue();
                    break;
                case 134: // effacesequence
                    cadre.getSoundPlayer().efface_sequence();
                    break;
                case 135: // indexsequence
                    Interpreter.operande = true;
                    double d = (double) cadre.getSoundPlayer().getTicks() / 64;
                    Interpreter.calcul.push(Calculator.teste_fin_double(d));

                    break;
                case 136: // fixeindexsequence
                    try {
                        int i = kernel.getCalculator().getInteger(param.get(0));
                        cadre.getSoundPlayer().setTicks(i * 64);
                    } catch (LogoException e) {
                    }
                    break;
                case 137:// fpt
                    try {
                        int i = kernel.getCalculator().getInteger(param.get(0));
                        cadre.getHistoryPanel().getDsd().fixepolice(i);
                    } catch (LogoException e) {
                    }
                    break;
                case 138: // ptexte
                    Interpreter.operande = true;
                    Interpreter.calcul.push(String.valueOf(cadre.getHistoryPanel()
                            .police()));
                    break;
                case 139: // fct,fixecouleurtexte
                    try {
                        if (isList(param.get(0))) {
                            cadre.getHistoryPanel().getDsd().fixecouleur(rgb(param.get(0), Utils.primitiveName("fct")));
                        } else {
                            int coul = kernel.getCalculator().getInteger(param.get(0)) % DrawPanel.defaultColors.length;
                            if (coul < 0) coul += DrawPanel.defaultColors.length;
                            cadre.getHistoryPanel().getDsd().fixecouleur(DrawPanel.defaultColors[coul]);
                        }
                    } catch (LogoException e) {
                    }
                    break;
                case 140: // couleurtexte
                    Interpreter.operande = true;
                    Color c = cadre.getHistoryPanel().getCouleurtexte();
                    Interpreter.calcul.push("[ " + c.getRed() + " " + c.getGreen()
                            + " " + c.getBlue() + " ] ");
                    break;
                case 141: // lissouris readmouse
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
                    break;
                case 142: // possouris
                    Interpreter.calcul.push(cadre.getDrawPanel().get_possouris());
                    Interpreter.operande = true;
                    break;
                case 143: // msg message
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
                        java.awt.FontMetrics fm = cadre.getGraphics()
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

                        MessageTextArea jt = new MessageTextArea(liste, cadre.getHistoryPanel().getDsd());
                        JOptionPane.showMessageDialog(cadre, jt, "", JOptionPane.INFORMATION_MESSAGE, Logo.getAppIcon());

                    } catch (LogoException e) {
                    }
                    break;
                case 144: // date
                    Interpreter.operande = true;
                    Calendar cal = Calendar.getInstance(Logo.getLocale(Logo.config.getLanguage()));
                    int jour = cal.get(Calendar.DAY_OF_MONTH);
                    int mois = cal.get(Calendar.MONTH) + 1;
                    int annee = cal.get(Calendar.YEAR);
                    Interpreter.calcul.push("[ " + jour + " " + mois + " " + annee
                            + " ] ");
                    break;
                case 145: // heure
                    Interpreter.operande = true;
                    cal = Calendar.getInstance(Logo.getLocale(Logo.config.getLanguage()));
                    int heure = cal.get(Calendar.HOUR_OF_DAY);
                    int minute = cal.get(Calendar.MINUTE);
                    int seconde = cal.get(Calendar.SECOND);
                    Interpreter.calcul.push("[ " + heure + " " + minute + " "
                            + seconde + " ] ");
                    break;
                case 146: // temps
                    Interpreter.operande = true;
                    long heure_actuelle = Calendar.getInstance().getTimeInMillis();
                    Interpreter.calcul
                            .push(String
                                    .valueOf((heure_actuelle - Logo.getStartupHour()) / 1000));
                    break;
                case 147: // debuttemps
                    try {
                        int temps = kernel.getCalculator().getInteger(param.get(0));
                        Kernel.chrono = Calendar.getInstance().getTimeInMillis()
                                + 1000 * temps;
                    } catch (LogoException e) {
                    }
                    break;
                case 148: // fintemps?
                    Interpreter.operande = true;
                    if (Calendar.getInstance().getTimeInMillis() > Kernel.chrono)
                        Interpreter.calcul.push(Logo.messages.getString("vrai"));
                    else
                        Interpreter.calcul.push(Logo.messages.getString("faux"));
                    break;
                case 149: // fnp fixenompolice
                    try {
                        int int_police = kernel.getCalculator().getInteger(param.get(0));
                        cadre.getDrawPanel().drawingFont = int_police
                                % FontPanel.fontes.length;
                    } catch (LogoException e) {
                    }
                    break;
                case 150: // np nompolice
                    Interpreter.operande = true;
                    Interpreter.calcul.push("[ "
                            + cadre.getDrawPanel().drawingFont
                            + " [ "
                            + FontPanel.fontes[cadre.getDrawPanel().drawingFont]
                            .getFontName() + " ] ] ");
                    break;
                case 151: // fnpt fixenompolicetexte
                    try {
                        int int_police = kernel.getCalculator().getInteger(param.get(0));
                        HistoryPanel.fontPrint = int_police
                                % FontPanel.fontes.length;
                        cadre.getHistoryPanel().getDsd().fixenompolice(int_police);
                    } catch (LogoException e) {
                    }

                    break;
                case 152: // npt nompolicetexte
                    Interpreter.operande = true;
                    Interpreter.calcul.push("[ "
                            + HistoryPanel.fontPrint
                            + " [ "
                            + FontPanel.fontes[HistoryPanel.fontPrint]
                            .getFontName() + " ] ] ");
                    break;
                case 153: // listeflux
                    liste = "[ ";
                    for (Flow flow : kernel.flows) {
                        liste += "[ " + flow.getId()
                                + " " + flow.getPath() + " ] ";
                    }
                    liste += "] ";
                    Interpreter.operande = true;
                    Interpreter.calcul.push(liste);
                    break;
                case 154: // lisligneflux
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
                    break;
                case 155: // liscaractereflux
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
                    break;
                case 156: // ecrisligneflux
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
                    break;
                case 157: // finficher?
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
                    break;
                case 158: // ouvreflux
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
                    break;
                case 159: // fermeflux
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
                    break;
                case 160: // ajouteligneflux
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
                    break;
                case 161: // souris?
                    Interpreter.operande = true;
                    if (cadre.getDrawPanel().get_lissouris())
                        Interpreter.calcul.push(Logo.messages.getString("vrai"));
                    else
                        Interpreter.calcul.push(Logo.messages.getString("faux"));
                    break;
                case 162: // variables
                    Interpreter.operande = true;
                    Interpreter.calcul.push(new String(getAllVariables()));
                    break;
                case 163: // chose
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
                    break;
                case 164: // nettoie
                    cadre.getDrawPanel().wash();
                    break;
                case 165: // tape
                    par = param.get(0).trim();
                    if (isList(par))
                        par = formatList(par.substring(1, par.length() - 1));
                    mot = getWord(param.get(0));
                    if (null == mot)
                        cadre.updateHistory("perso", Utils.unescapeString(par));
                    else
                        cadre.updateHistory("perso", Utils.unescapeString(mot));
                    break;
                case 166: // cercle
                    try {
                        cadre.getDrawPanel().circle((kernel.getCalculator().numberDouble(param.pop())));
                    } catch (LogoException e) {
                    }
                    break;
                case 167: // arc
                    try {
                        cadre.getDrawPanel().arc(kernel.getCalculator().numberDouble(param.get(0)), kernel.getCalculator().numberDouble(param.get(1)), kernel.getCalculator().numberDouble(param.get(2)));
                    } catch (LogoException e) {
                    }
                    break;
                case 168: // rempliszone
                    cadre.getDrawPanel().fillZone();
                    break;
                case 169: // animation
                    cadre.getDrawPanel().setAnimation(true);
                    Interpreter.operande = false;
                    break;
                case 170: // rafraichis
                    if (DrawPanel.classicMode == DrawPanel.MODE_ANIMATION) {
                        cadre.getDrawPanel().refresh();
                    }
                    break;

                case 171: // tailledessin
                    Interpreter.operande = true;
                    StringBuffer sb = new StringBuffer();
                    sb.append("[ ");
                    sb.append(Logo.config.getImageWidth());
                    sb.append(" ");
                    sb.append(Logo.config.getImageHeight());
                    sb.append(" ] ");
                    Interpreter.calcul.push(new String(sb));
                    break;
                case 172: // quotient
                    try {
                        Interpreter.operande = true;
                        Interpreter.calcul.push(kernel.getCalculator().quotient(param.get(0), param.get(1)));
                    } catch (LogoException e) {
                    }


                    break;
                case 173: // entier?
                    Interpreter.operande = true;
                    try {
                        double ent = kernel.getCalculator().numberDouble(param.get(0));
                        if ((int) ent == ent) Interpreter.calcul.push(Logo.messages.getString("vrai"));
                        else Interpreter.calcul.push(Logo.messages.getString("faux"));
                    } catch (LogoException e) {
                    }
                    break;
                case 174: // fixeseparation
                    try {
                        double nombre = kernel.getCalculator().numberDouble(param.get(0));
                        if (nombre < 0 || nombre > 1)
                            throw new LogoException(cadre, nombre + " " + Logo.messages.getString("entre_zero_un"));
                        cadre.splitPane.setResizeWeight(nombre);
                        cadre.splitPane.setDividerLocation(nombre);
                    } catch (LogoException e) {
                    }
                    break;
                case 175: // separation
                    Interpreter.operande = true;
                    Interpreter.calcul.push(Calculator.teste_fin_double(cadre.splitPane.getResizeWeight()));
                    break;
                case 176: // tronque
                    Interpreter.operande = true;
                    try {
                        Interpreter.calcul.push(kernel.getCalculator().truncate(param.get(0)));
                    } catch (LogoException e) {
                    }
                    break;
                case 177: // trace
                    Kernel.mode_trace = true;
                    Interpreter.operande = false;
                    break;
                case 178:// changedossier
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

                    break;
                case 179:// unicode
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
                    break;
                case 180:// caractere
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
                    break;
                case 181: // stoptout
                    cadre.error = true;
                    break;
                case 182: // compteur
                    boolean erreur = false;
                    if (!Primitive.stackLoop.isEmpty()) {
                        LoopProperties bp = Primitive.stackLoop.peek();
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
                    break;
                case 183: // controls.for repetepour
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
                                cadre.getKernel().getInstructionBuffer().insert(li2 + Primitive.END_LOOP + " ");
                                Primitive.stackLoop.push(bp);
                            }
                        }
                    } catch (LogoException e) {
                    }
                    break;
                case 184: // absolue
                    try {
                        Interpreter.operande = true;
                        Interpreter.calcul.push(kernel.getCalculator().abs(param.get(0)));
                    } catch (LogoException e) {
                    }
                    break;
                case 185: // remplace
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
                    break;
                case 186: // ajoute
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
                    break;
                case 187: // gris
                    colorCode(8);
                    break;
                case 188: // grisclair
                    colorCode(9);
                    break;
                case 189: // rougefonce
                    colorCode(10);
                    break;
                case 190: // vertfonce
                    colorCode(11);
                    break;
                case 191: // bleufonce
                    colorCode(12);
                    break;
                case 192: // orange
                    colorCode(13);
                    break;
                case 193: // rose
                    colorCode(14);
                    break;
                case 194: // violet
                    colorCode(15);
                    break;
                case 195: // marron
                    colorCode(16);
                    break;
                case 196: // noir
                    colorCode(0);
                    break;
                case 197: // rouge
                    colorCode(1);
                    break;
                case 198: // vert
                    colorCode(2);
                    break;
                case 199: // jaune
                    colorCode(3);
                    break;
                case 200: // bleu
                    colorCode(4);
                    break;
                case 201: // magenta
                    colorCode(5);
                    break;
                case 202: // cyan
                    colorCode(6);
                    break;
                case 203: // blanc
                    colorCode(7);
                    break;
                case 204: // Parenthese fermante
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
                            if (Primitive.primitives.containsKey(proc)) est_procedure = true;
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
// System.out.println(Primitive.primitives.containsKey("puissance")+"
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
                    break;
                case 205: // fixestyle
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
                        cadre.getHistoryPanel().getDsd().fixegras(gras);
                        cadre.getHistoryPanel().getDsd().fixeitalique(italique);
                        cadre.getHistoryPanel().getDsd().fixesouligne(souligne);
                        cadre.getHistoryPanel().getDsd().fixeexposant(exposant);
                        cadre.getHistoryPanel().getDsd().fixeindice(indice);
                        cadre.getHistoryPanel().getDsd().fixebarre(barre);
                    } catch (LogoException e) {
                    }
                    break;
                case 206: // style
                    StringBuffer buffer = new StringBuffer();
                    int compteur = 0;
                    if (cadre.getHistoryPanel().getDsd().estgras()) {
                        buffer.append(Logo.messages.getString("style.bold").toLowerCase() + " ");
                        compteur++;
                    }
                    if (cadre.getHistoryPanel().getDsd().estitalique()) {
                        buffer.append(Logo.messages.getString("style.italic").toLowerCase() + " ");
                        compteur++;
                    }
                    if (cadre.getHistoryPanel().getDsd().estsouligne()) {
                        buffer.append(Logo.messages.getString("style.underline").toLowerCase() + " ");
                        compteur++;
                    }
                    if (cadre.getHistoryPanel().getDsd().estexposant()) {
                        buffer.append(Logo.messages.getString("style.exposant").toLowerCase() + " ");
                        compteur++;
                    }
                    if (cadre.getHistoryPanel().getDsd().estindice()) {
                        buffer.append(Logo.messages.getString("style.subscript").toLowerCase() + " ");
                        compteur++;
                    }
                    if (cadre.getHistoryPanel().getDsd().estbarre()) {
                        buffer.append(Logo.messages.getString("style.strike").toLowerCase() + " ");
                        compteur++;
                    }
                    Interpreter.operande = true;
                    if (compteur == 0)
                        Interpreter.calcul.push("\"" + Logo.messages.getString("style.none").toLowerCase());
                    else if (compteur == 1) Interpreter.calcul.push("\"" + new String(buffer).trim());
                    else if (compteur > 1) Interpreter.calcul.push("[ " + new String(buffer) + "]");
                    break;
                case 207: // listaillefenetre
                    Interpreter.operande = true;
                    java.awt.Point p = cadre.scrollPane.getViewport().getViewPosition();
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
                    break;
                case 208: // LongueurEtiquette
                    try {
                        mot = getWord(param.get(0));
                        if (null != mot) mot = Utils.unescapeString(mot);
                        else mot = getFinalList(param.get(0)).trim();
                        Interpreter.operande = true;
                        java.awt.FontMetrics fm = cadre.getDrawPanel().getGraphics()
                                .getFontMetrics(cadre.getDrawPanel().getGraphicsFont());
                        int longueur = fm.stringWidth(mot);
                        Interpreter.calcul.push(String.valueOf(longueur));
                    } catch (LogoException e) {
                    }
                    break;
                case 209: // envoietcp // sendtcp // enviatcp etcp
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
                    break;
                case 210: // ecoutetcp
                    Interpreter.operande = false;
                    if (null == savedWorkspace) savedWorkspace = new Stack<Workspace>();
                    savedWorkspace.push(wp);
                    new NetworkServer(cadre);


                    break;
                case 211: // executetcp
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
                    break;
                case 212: // \x internal operator to specify
                    // the end of network instructions with
                    // "executetcp"
                    // have to replace workspace
                    Interpreter.operande = false;
                    kernel.setWorkspace(savedWorkspace.pop());
                    break;
                case 213: // chattcp
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
                    break;
                case 214: // init resetall
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
                    HistoryPanel.fontPrint = FontPanel.police_id(Logo.config.getFont());
                    cadre.getHistoryPanel().getDsd().fixepolice(12);
                    cadre.getHistoryPanel().getDsd().fixenompolice(HistoryPanel.fontPrint);
                    cadre.getHistoryPanel().getDsd().fixecouleur(Color.black);
                    Logo.config.setPenShape(0);
                    Logo.config.setDrawQuality(0);
                    kernel.setDrawingQuality(Logo.config.getDrawQuality());
                    kernel.setNumberOfTurtles(16);
                    Logo.config.setTurtleSpeed(0);
                    Kernel.mode_trace = false;
                    DrawPanel.windowMode = DrawPanel.WINDOW_CLASSIC;
                    cadre.getDrawPanel().zoom(1, false);
                    break;
                case 215: // tc taillecrayon
                    Interpreter.operande = true;
                    double penwidth = 2 * kernel.getActiveTurtle().getPenWidth();
                    Interpreter.calcul.push(Calculator.teste_fin_double(penwidth));
                    break;
                case 216: // setpenshape=ffc fixeformecrayon
                    Interpreter.operande = false;
                    try {
                        int i = kernel.getCalculator().getInteger(param.get(0));
                        if (i != Logo.config.PEN_SHAPE_OVAL && i != Logo.config.PEN_SHAPE_SQUARE) {
                            String st = Utils.primitiveName("setpenshape") + " " + Logo.messages.getString("error_bad_values");
                            st += " " + Logo.config.PEN_SHAPE_SQUARE + " " + Logo.config.PEN_SHAPE_OVAL;
                            throw new LogoException(cadre, st);
                        }
                        Logo.config.setPenShape(i);
                        cadre.getDrawPanel().updateAllTurtleShape();
                        cadre.getDrawPanel().setStroke(kernel.getActiveTurtle().crayon);
                    } catch (LogoException e) {
                    }
                    break;
                case 217: // penshape=fc formecrayon
                    Interpreter.operande = true;
                    Interpreter.calcul.push(String.valueOf(Logo.config.getPenShape()));
                    break;
                case 218: // setdrawingquality=fqd fixequalitedessin
                    Interpreter.operande = false;
                    try {
                        int i = kernel.getCalculator().getInteger(param.get(0));
                        if (i != Logo.config.DRAW_QUALITY_NORMAL && i != Logo.config.DRAW_QUALITY_HIGH && i != Logo.config.DRAW_QUALITY_LOW) {
                            String st = Utils.primitiveName("setdrawingquality") + " " + Logo.messages.getString("error_bad_values") + " 0 1 2";
                            throw new LogoException(cadre, st);
                        }
                        Logo.config.setDrawQuality(i);
                        kernel.setDrawingQuality(Logo.config.getDrawQuality());
                    } catch (LogoException e) {
                    }
                    break;
                case 219: // drawingquality=qd qualitedessin
                    Interpreter.operande = true;
                    Interpreter.calcul.push(String.valueOf(Logo.config.getDrawQuality()));
                    break;
                case 220: // setturtlesnumber=fmt fixemaxtortues
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
                    break;
                case 221: // turtlesnumber=maxtortues
                    Interpreter.operande = true;
                    Interpreter.calcul.push(String.valueOf(Logo.config.getMaxTurtles()));

                    break;
                case 222: // setscreensize=ftd fixetailledessin
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
                            Logo.config.setImageWidth(1000);
                            Logo.config.setImageHeight(1000);
                        }
                        if (changement) {
                            int memoire_necessaire = Logo.config.getImageWidth() * Logo.config.getImageHeight() * 4 / 1024 / 1024;
                            int memoire_image = tmp_hauteur * tmp_largeur * 4 / 1024 / 1024;
                            long free = Runtime.getRuntime().freeMemory() / 1024 / 1024;
                            long total = Runtime.getRuntime().totalMemory() / 1024 / 1024;
                            /*
                             * System.out.println("memoire nécessaire
                             * "+memoire_necessaire); System.out.println("memoire
                             * image "+memoire_image); System.out.println("memoire
                             * libre "+free); System.out.println("memoire totale
                             * "+total); System.out.println("memoire envisagee
                             * "+(total-free+memoire_necessaire-memoire_image));
                             * System.out.println();
                             */
                            if (total - free + memoire_necessaire - memoire_image < Logo.getMemoryLimit() * 0.8) {
                                cadre.resizeDrawingZone();
                            } else {
                                Logo.config.setImageWidth(tmp_largeur);
                                Logo.config.setImageHeight(tmp_hauteur);
                                long conseil = 64 * ((total - free + memoire_necessaire - memoire_image) / 64) + 64;
                                if (total - free + memoire_necessaire - memoire_image > 0.8 * conseil) conseil += 64;
                                if (conseil == Logo.getMemoryLimit()) conseil += 64;
                                String message = Logo.messages.getString("erreur_memoire") + " " + conseil + "\n" + Logo.messages.getString("relancer");
                                MessageTextArea jt = new MessageTextArea(message);
                                JOptionPane.showMessageDialog(cadre, jt, Logo.messages.getString("erreur"), JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    } catch (LogoException e) {
                    }
                    break;
                case 223: // guibutton guibouton
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
                    break;
                case 224: // guiaction
                    try {
                        String ident = getWord(param.get(0));
                        if (null == ident)
                            throw new LogoException(cadre, param.get(0) + " "
                                    + Logo.messages.getString("error.word"));
                        liste = getFinalList(param.get(1));
                        cadre.getDrawPanel().guiAction(ident, liste);
                    } catch (LogoException e) {
                    }
                    break;
                case 225: // guiremove
                    try {
                        String ident = getWord(param.get(0));
                        if (null == ident)
                            throw new LogoException(cadre, param.get(0) + " "
                                    + Logo.messages.getString("error.word"));
                        cadre.getDrawPanel().guiRemove(ident);
                    } catch (LogoException e) {
                    }

                    break;
                case 226: // guiposition
                    try {
                        String ident = getWord(param.get(0));
                        if (null == ident)
                            throw new LogoException(cadre, param.get(0) + " "
                                    + Logo.messages.getString("error.word"));
                        liste = getFinalList(param.get(1));
                        cadre.getDrawPanel().guiposition(ident, liste, Utils.primitiveName("guiposition"));
                    } catch (LogoException e) {
                    }
                    break;
                case 227: // guidraw
                    try {
                        String ident = getWord(param.get(0));
                        if (null == ident)
                            throw new LogoException(cadre, param.get(0) + " "
                                    + Logo.messages.getString("error.word"));
                        cadre.getDrawPanel().guiDraw(ident);
                    } catch (LogoException e) {
                    }
                    break;
                case 228: // zoom
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
                    break;
                case 229: // grille
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
                        cadre.getDrawPanel().clearScreen();

                    } catch (LogoException e) {
                    }
                    break;
                case 230: // stopgrille
                    Interpreter.operande = false;
                    Logo.config.setGridEnabled(false);
                    cadre.getDrawPanel().clearScreen();
                    break;
                case 231: // stopanimation
                    cadre.getDrawPanel().setAnimation(false);
                    Interpreter.operande = false;
                    break;
                case 232: // stoptrace
                    Kernel.mode_trace = false;
                    Interpreter.operande = false;
                    break;
                case 233: // guimenu
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
                    break;
                case 234: // axis

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
                        cadre.getDrawPanel().clearScreen();
                    } catch (LogoException e) {
                    }
                    break;
                case 235: // xaxis
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
                        cadre.getDrawPanel().clearScreen();
                    } catch (LogoException e) {
                    }
                    break;
                case 236: // yaxis
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
                        cadre.getDrawPanel().clearScreen();
                    } catch (LogoException e) {
                    }
                    break;
                case 237: // stopaxis
                    Logo.config.setXAxisEnabled(false);
                    Logo.config.setYAxisEnabled(false);
                    Interpreter.operande = false;
                    cadre.getDrawPanel().clearScreen();
                    break;
                case 238: // bye
                    cadre.closeWindow();
                    break;
                case 239: // var? variable?
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
                    break;
                case 240: // axiscolor= couleuraxes
                    Interpreter.operande = true;
                    c = new Color(Logo.config.getAxisColor());
                    Interpreter.calcul.push("[ " + c.getRed() + " " + c.getGreen()
                            + " " + c.getBlue() + " ] ");

                    break;
                case 241: // gridcolor=couleurgrille
                    Interpreter.operande = true;
                    c = new Color(Logo.config.getGridColor());
                    Interpreter.calcul.push("[ " + c.getRed() + " " + c.getGreen()
                            + " " + c.getBlue() + " ] ");
                    break;
                case 242: // grid?=grille?
                    Interpreter.operande = true;
                    if (Logo.config.isGridEnabled())
                        Interpreter.calcul.push(Logo.messages.getString("vrai"));
                    else
                        Interpreter.calcul.push(Logo.messages.getString("faux"));
                    break;
                case 243: // xaxis?=axex?
                    Interpreter.operande = true;
                    if (Logo.config.isXAxisEnabled())
                        Interpreter.calcul.push(Logo.messages.getString("vrai"));
                    else
                        Interpreter.calcul.push(Logo.messages.getString("faux"));
                    break;
                case 244: // yaxis?=axey?
                    Interpreter.operande = true;
                    if (Logo.config.isYAxisEnabled())
                        Interpreter.calcul.push(Logo.messages.getString("vrai"));
                    else
                        Interpreter.calcul.push(Logo.messages.getString("faux"));
                    break;
                case 245: // setgridcolor=fcg fixecouleurgrille
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
                    break;
                case 246: // setaxiscolor=fca fixecouleuraxes
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
                    break;
                case 247: // perspective

                    cadre.getDrawPanel().perspective();

                    break;
                case 248:// rightroll=rd roulisdroite
                    delay();
                    try {
                        primitive3D("3d.rightroll");
                        cadre.getDrawPanel().roll(kernel.getCalculator().numberDouble(param.pop()));
                    } catch (LogoException e) {
                    }
                    break;
                case 249:// uppitch=cabre
                    delay();
                    try {
                        primitive3D("3d.uppitch");
                        cadre.getDrawPanel().pitch(kernel.getCalculator().numberDouble(param.pop()));
                    } catch (LogoException e) {
                    }
                    break;
                case 250:// leftroll=rg roulisgauche
                    delay();
                    try {
                        primitive3D("3d.leftroll");
                        cadre.getDrawPanel().roll(-kernel.getCalculator().numberDouble(param.pop()));
                    } catch (LogoException e) {
                    }
                    break;
                case 251:// downpitch=pique
                    delay();
                    try {
                        primitive3D("3d.downpitch");
                        cadre.getDrawPanel().pitch(-kernel.getCalculator().numberDouble(param.pop()));
                    } catch (LogoException e) {
                    }
                    break;
                case 252:// roll=roulis
                    try {
                        primitive3D("3d.roll");
                        Interpreter.operande = true;
                        Interpreter.calcul.push(Calculator.teste_fin_double(kernel.getActiveTurtle().roll));
                    } catch (LogoException e) {
                    }
                    break;
                case 253:// pitch=cabrement tangage
                    try {
                        primitive3D("3d.pitch");
                        Interpreter.operande = true;
                        Interpreter.calcul.push(Calculator.teste_fin_double(kernel.getActiveTurtle().pitch));
                    } catch (LogoException e) {
                    }
                    break;
                case 254:// setroll=fixeroulis
                    try {
                        primitive3D("3d.setroll");
                        delay();
                        cadre.getDrawPanel().setRoll(kernel.getCalculator().numberDouble(param.pop()));
                    } catch (LogoException e) {
                    }
                    break;
                case 255:// setpitch=fixetangage
                    try {
                        primitive3D("3d.setpitch");
                        delay();
                        cadre.getDrawPanel().setPitch(kernel.getCalculator().numberDouble(param.pop()));
                    } catch (LogoException e) {
                    }
                    break;
                case 256:// setorientation=fixeorientation
                    try {
                        primitive3D("3d.setorientation");
                        delay();
                        cadre.getDrawPanel().setOrientation(getFinalList(param.pop()));
                    } catch (LogoException e) {
                    }
                    break;
                case 257: // orientation=orientation
                    try {
                        primitive3D("3d.orientation");
                        Interpreter.operande = true;
                        String pitch = Calculator.teste_fin_double(kernel.getActiveTurtle().pitch);
                        String roll = Calculator.teste_fin_double(kernel.getActiveTurtle().roll);
                        String heading = Calculator.teste_fin_double(kernel.getActiveTurtle().heading);
                        Interpreter.calcul.push("[ " + roll + " " + pitch + " " + heading + " ] ");
                    } catch (LogoException e) {
                    }
                    break;
                case 258: // setxyz=fposxyz
                    try {
                        primitive3D("3d.setxyz");
                        cadre.getDrawPanel().setPosition(kernel.getCalculator().numberDouble(param.get(0)) + " "
                                + kernel.getCalculator().numberDouble(param.get(1)) + " " +
                                kernel.getCalculator().numberDouble(param.get(2)));
                    } catch (LogoException e) {
                    }
                    break;
                case 259: // setz=fixez
                    delay();
                    try {
                        primitive3D("3d.setz");
                        cadre.getDrawPanel().setPosition(kernel.getActiveTurtle().X + " " + kernel.getActiveTurtle().Y
                                + " " + kernel.getCalculator().numberDouble(param.get(0)));

                    } catch (LogoException e) {
                    }
                    break;
                case 260: // pprop=dprop
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
                    break;
                case 261: // gprop=rprop
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
                    break;
                case 262: // remprop=efprop
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
                    break;
                case 263: // plist=lprop
                    try {
                        Interpreter.operande = true;
                        mot = getWord(param.get(0));
                        if (null == mot)
                            throw new LogoException(cadre, param.get(0) + " " + Logo.messages.getString("error.word"));
                        Interpreter.calcul.push(wp.displayPropList(mot));
                    } catch (LogoException e) {
                    }

                    break;
                case 264: // polystart=polydef
                    DrawPanel.record3D = DrawPanel.RECORD_3D_POLYGON;
                    cadre.initViewer3D();
//                    	if (null==DrawPanel.listPoly) DrawPanel.listPoly=new java.util.Vector<Shape3D>();
                    DrawPanel.poly = new ElementPolygon(cadre);
                    break;
                case 265: // polyend=polyfin
                    DrawPanel.record3D = DrawPanel.RECORD_3D_NONE;
                    try {
                        DrawPanel.poly.addToScene();
                    } catch (LogoException e) {
                    }
                    break;
                case 266: // polyview=polyaf vue3d
                    try {
                        primitive3D("3d.polyview");
                        cadre.viewerOpen();
                    } catch (LogoException e) {
                    }
                    break;
                case 267: // linestart=lignedef
                    DrawPanel.record3D = DrawPanel.RECORD_3D_LINE;
                    cadre.initViewer3D();
//                    	if (null==DrawPanel.listPoly) DrawPanel.listPoly=new java.util.Vector<Shape3D>();
                    DrawPanel.poly = new ElementLine(cadre);
                    DrawPanel.poly.addVertex(new Point3d(kernel.getActiveTurtle().X / 1000,
                            kernel.getActiveTurtle().Y / 1000,
                            kernel.getActiveTurtle().Z / 1000), kernel.getActiveTurtle().penColor);
                    break;
                case 268: // lineend=lignefin
                    DrawPanel.record3D = DrawPanel.RECORD_3D_NONE;
                    try {
                        DrawPanel.poly.addToScene();
                    } catch (LogoException e) {
                    }
                    break;
                case 269: // pointstart=pointdef
                    DrawPanel.record3D = DrawPanel.RECORD_3D_POINT;
                    cadre.initViewer3D();
//                    	if (null==DrawPanel.listPoly) DrawPanel.listPoly=new java.util.Vector<Shape3D>();
                    DrawPanel.poly = new ElementPoint(cadre);
                    break;
                case 270: // pointend=pointfin
                    DrawPanel.record3D = DrawPanel.RECORD_3D_NONE;
                    try {
                        DrawPanel.poly.addToScene();
                    } catch (LogoException e) {
                    }
                    break;
                case 271: // textstart=textedef
                    DrawPanel.record3D = DrawPanel.RECORD_3D_TEXT;
                    cadre.initViewer3D();
//                    	if (null==DrawPanel.listText) DrawPanel.listText=new java.util.Vector<TransformGroup>();
                    DrawPanel.poly = null;
                    break;
                case 272: // textend=textefin
                    DrawPanel.record3D = DrawPanel.RECORD_3D_NONE;
                    break;
                case 273: // operator <=
                    infequal(param);
                    break;
                case 274: // operator >=
                    supequal(param);
                    break;
                case 275: // primitives
                    Interpreter.operande = true;
                    Interpreter.calcul.push(kernel.primitive.getAllPrimitives());
                    break;
                case 276: //listesproprietes propertylists
                    Interpreter.operande = true;
                    Interpreter.calcul.push(new String(getAllpropertyLists()));
                    break;
                case 277: // contenu
                    Interpreter.operande = true;
                    sb = new StringBuffer("[ ");
                    sb.append(this.getAllProcedures());
                    sb.append(this.getAllVariables());
                    sb.append(this.getAllpropertyLists());
                    sb.append("] ");
                    Interpreter.calcul.push(new String(sb));
                    break;
                case 278: // erpl=eflp effacelistepropriete
                    Interpreter.operande = false;
                    this.erase(param.get(0), "propertylist");
                    break;
                case 279: //arithmetic.exp
                    Interpreter.operande = true;
                    try {
                        Interpreter.calcul.push(kernel.getCalculator().exp(param.get(0)));

                    } catch (LogoException e) {
                    }
                    break;
                case 280: //arithmetic.log
                    Interpreter.operande = true;
                    try {
                        Interpreter.calcul.push(kernel.getCalculator().log(param.get(0)));
                    } catch (LogoException e) {
                    }
                    break;
                case 281: // controls.ifelse
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
                    break;
                case 282: // workspace.ed
                    try {
                        mot = this.getWord(param.get(0));
                        if (null == mot) mot = this.getFinalList(param.get(0));
                        StringTokenizer st = new StringTokenizer(mot);
                        // Write all procedures names in a Vector
                        java.util.Vector<String> names = new java.util.Vector<String>();
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
                    break;
                case 283: // workspace.edall
                    cadre.editor.open();
                    break;
                case 284: // controls.foreach pourchaque
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
                            cadre.getKernel().getInstructionBuffer().insert(li2 + Primitive.END_LOOP + " ");
                            Primitive.stackLoop.push(bp);
                        }
                    } catch (LogoException e) {
                    }
                    break;
                case 285: // controls.forever repetetoujours
                    try {
                        String li2 = getList(param.get(0));
                        li2 = new String(Utils.formatCode(li2));
                        LoopProperties bp = new LoopProperties(BigDecimal.ONE, BigDecimal.ZERO
                                , BigDecimal.ONE, li2);
                        cadre.getKernel().getInstructionBuffer().insert(li2 + Primitive.END_LOOP + " ");
                        Primitive.stackLoop.push(bp);
                    } catch (LogoException e) {
                    }
                    break;
                case 286: // arithmetic.setdigits
                    Interpreter.operande = false;
                    try {
                        kernel.initCalculator(kernel.getCalculator().getInteger(param.get(0)));
                    } catch (LogoException e) {
                    }
                    break;
                case 287: // arithmetic.digits
                    Interpreter.operande = true;
                    Interpreter.calcul.push(String.valueOf(kernel.getCalculator().getDigits()));
                    break;
                case 288: //workspace.text
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
                    break;
                case 289: //workspace.externalcommand
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
/*                       			String com="";
                       			for(int i=0;i<cmd.length;i++){
                       				com+=cmd[i]+" ";
                       			}
                       			System.out.println(com);*/
                            Runtime.getRuntime().exec(cmd);
                        } catch (IOException e2) {
                            //System.out.println("a");
                        }

                    } catch (LogoException e) {
                        //System.out.println("coucou");
                    }
                    break;
                case 290: // drawing.saveimage
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
                    break;
                case 291: //sound.mp3play
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
                    break;
                case 292: //sound.mp3stop
                    Interpreter.operande = false;
                    if (null != kernel.getMp3Player()) kernel.getMp3Player().getPlayer().close();
                    break;
                case 293: // zoom
                    Interpreter.operande = true;
                    Interpreter.calcul.push(Calculator.teste_fin_double(DrawPanel.zoom));
                    break;
                case 294: // drawing.x
                    Interpreter.operande = true;
                    Interpreter.calcul.push(Calculator.teste_fin_double(kernel.getActiveTurtle().getX()));
                    break;
                case 295:// drawing.y
                    Interpreter.operande = true;
                    Interpreter.calcul.push(Calculator.teste_fin_double(kernel.getActiveTurtle().getY()));
                    break;
                case 296: // drawing.z
                    Interpreter.operande = true;
                    try {
                        primitive3D("drawing.z");
                        Interpreter.calcul.push(Calculator.teste_fin_double(kernel.getActiveTurtle().Z));
                    } catch (LogoException e) {
                    }
                    break;
                case 297: // drawing.fillpolygon
                    Interpreter.operande = false;
                    try {
                        String list = getFinalList(param.get(0));
                        LoopFillPolygon bp = new LoopFillPolygon();
                        Primitive.stackLoop.push(bp);
                        cadre.getKernel().getInstructionBuffer().insert(Utils.formatCode(list) + Primitive.END_LOOP + " ");
                        cadre.getDrawPanel().startRecord2DPolygon();
                    } catch (LogoException e) {
                    }
                    break;
                case 298: // arithmetic.alea
                    Interpreter.operande = true;
                    Interpreter.calcul.push(Calculator.teste_fin_double(Math.random()));
                    break;
                case 299: // loop.dountil
                    try {
                        String li1 = getList(param.get(0));
                        li1 = new String(Utils.formatCode(li1));
                        String li2 = getList(param.get(1));
                        li2 = new String(Utils.formatCode(li2));
                        String instr = "\\siwhile " + Utils.primitiveName("non") + " " + li2 + "[ " + li1 + "] ";
                        LoopWhile bp = new LoopWhile(BigDecimal.ONE, BigDecimal.ZERO, BigDecimal.ONE, instr);
                        Primitive.stackLoop.push(bp);
                        cadre.getKernel().getInstructionBuffer().insert(instr + Primitive.END_LOOP + " ");
                    } catch (LogoException e) {
                    }
                    break;
                case 300: // loop.dowhile
                    try {
                        String li1 = getList(param.get(0));
                        li1 = new String(Utils.formatCode(li1));
                        String li2 = getList(param.get(1));
                        li2 = new String(Utils.formatCode(li2));
                        String instr = "\\siwhile " + li2 + "[ " + li1 + "] ";
                        LoopWhile bp = new LoopWhile(BigDecimal.ONE, BigDecimal.ZERO, BigDecimal.ONE, instr);
                        Primitive.stackLoop.push(bp);
                        cadre.getKernel().getInstructionBuffer().insert(li1 + instr + Primitive.END_LOOP + " ");
                    } catch (LogoException e) {
                    }
                    break;
                case 301: // arithmetic.modulo
                    Interpreter.operande = true;
                    try {
                        Interpreter.calcul.push(kernel.getCalculator().modulo(param.get(0), param.get(1)));
                    } catch (LogoException e) {
                    }
                    break;
                case 302: // drawing.setfontjustify
                    Interpreter.operande = false;
                    try {
                        String li1 = getFinalList(param.get(0));
                        kernel.getActiveTurtle().setFontJustify(li1);
                    } catch (LogoException e) {
                        e.printStackTrace();
                    }
                    break;
                case 303: // drawing.fontjustify
                    Interpreter.operande = true;
                    Interpreter.calcul.push(kernel.getActiveTurtle().getFontJustify());
                    break;
                case 304: // arithmetic.inf
                    inf(param);
                    break;
                case 305: // arithmetic.sup
                    sup(param);
                    break;
                case 306: // arithmetic.infequal
                    infequal(param);
                    break;
                case 307: // arithmetic.supequal
                    supequal(param);
                    break;

            }
        }
    }

    /**
     * This method tests if the primitive name exist in 2D mode
     *
     * @param name
     *            The primitive name
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
     * @param name
     *            The primitive name
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
     * @param i
     *            Integer representing the Color
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
     * @param fichier
     *            The patch to the saved file
     * @param pile
     *            Stack Stack containing all procedure names
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
     * @param path
     *            The absolute path for the image
     * @return BufferedImage defined by the path "chemin"
     * @throws LogoException
     *             If Image format isn't valid(jpg or png)
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
     * @param mot
     *            Variable name
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
     * @param param
     *            The variable name or a list of variable names
     * @throws LogoException
     *             If "param" isn't a list containing all variable names, or a
     *             word
     */

    private void locale(Stack<String> param) throws LogoException {
        String li = param.get(0);
        if (LaunchPrimitive.isList(li)) {
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
     * @param obj
     *            the list [r g b]
     * @param name
     *            The name of the calling primitive
     * @return The Object Color
     * @throws LogoException
     *             If the list doesn't contain 3 numbers
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
     * @param param
     *            Stack that contains arguments for the primitive member
     * @param id
     *            69 --> member? or 70--> member
     * @throws LogoException
     *             Incorrect arguments
     */
    private void membre(Stack<String> param, int id) throws LogoException {
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
     * @param param
     *            Stack that contains all arguments
     * @throws LogoException
     *             Bad argument type
     */

    private void precede(Stack<String> param) throws LogoException {
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

    private void infequal(Stack<String> param) {
        Interpreter.operande = true;
        Interpreter.calcul.push(kernel.getCalculator().infequal(param));
    }

    private void supequal(Stack<String> param) {
        Interpreter.operande = true;
        Interpreter.calcul.push(kernel.getCalculator().supequal(param));
    }

    private void inf(Stack<String> param) {
        Interpreter.operande = true;
        Interpreter.calcul.push(kernel.getCalculator().inf(param));
    }

    private void sup(Stack<String> param) {
        Interpreter.operande = true;
        Interpreter.calcul.push(kernel.getCalculator().sup(param));
    }

    /**
     * / Primitive equal?
     *
     * @param param
     *            Stack that contains all arguments
     */
    private void equal(Stack<String> param) {
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
     * @param st
     *            true or false
     * @return The boolean corresponding to the string st
     * @throws LogoException
     *             If st isn't equal to true or false
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
     * @param st
     *            The Object to convert
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
     * @param li
     *            The String corresponding to the list
     * @return A list without any line Number tag (\0, \1, \2 ...)
     * @throws LogoException
     *             List bad format
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
     * @param li
     *            The String corresponding to the list
     * @return A list with line Number tag (\0, \1, \2 ...)
     * @throws LogoException
     *             List bad format
     */
    private String getList(String li) throws LogoException {
        li = li.trim();
        // Retourne la liste sans crochets;
        if (li.charAt(0) == '['
                && li.substring(li.length() - 1).equals("]")) {
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
    private void donne(Stack<String> param) throws LogoException {
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


    private void ou(Stack<String> param) {
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

    private void et(Stack<String> param) {
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
     * @param name The name of the deleted resource, it couls be a list with all resource names
     * @param type The type for the data, it could be "variable", "procedure" or "propertylist"
     */

    private void erase(String name, String type) {
        Interpreter.operande = false;
        try {
            if (LaunchPrimitive.isList(name)) {
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