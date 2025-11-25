/**
 * Title :        XLogo
 * Description :  XLogo is an interpreter for the Logo programming language
 */
package xlogo.kernel;

import xlogo.Logo;
import xlogo.gui.Application;
import xlogo.gui.HistoryPanel;
import xlogo.gui.InputFrame;
import xlogo.gui.MessageTextArea;
import xlogo.kernel.gui.GuiButton;
import xlogo.kernel.gui.GuiMenu;
import xlogo.utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Stack;
import java.util.StringTokenizer;

/**
 * UI-related primitives: text output, keyboard/mouse input, GUI components.
 */
public class UIPrimitives extends PrimitiveGroup {

    public UIPrimitives(Interpreter interpreter) {
        super(interpreter);
    }

    @Override
    public List<Primitive> getPrimitives() {
        return List.of(
            new Primitive("ui.character", 1, false, this::getCharacter),
            new Primitive("ui.cleartext", 0, false, this::clearText),
            new Primitive("ui.guiaction", 2, false, this::guiAction),
            new Primitive("ui.guibutton", 2, false, this::guiButton),
            new Primitive("ui.guidraw", 1, false, this::guiDraw),
            new Primitive("ui.guimenu", 2, false, this::guiMenu),
            new Primitive("ui.guiposition", 2, false, this::guiPosition),
            new Primitive("ui.guiremove", 1, false, this::guiRemove),
            new Primitive("ui.key?", 0, false, this::isKey),
            new Primitive("ui.message", 1, false, this::message),
            new Primitive("ui.mouse?", 0, false, this::isMouseEvent),
            new Primitive("ui.mouseposition", 0, false, this::mousePosition),
            new Primitive("ui.print", 1, true, this::print),
            new Primitive("ui.read", 2, false, this::read),
            new Primitive("ui.readcharacter", 0, false, this::readChar),
            new Primitive("ui.readmouse", 0, false, this::mouseButton),
            new Primitive("ui.separation", 0, false, this::getSeparation),
            new Primitive("ui.setseparation", 1, false, this::setSeparation),
            new Primitive("ui.setstyle", 1, false, this::setTextStyle),
            new Primitive("ui.settextcolor", 1, false, this::setTextColor),
            new Primitive("ui.settextname", 1, false, this::setTextFont),
            new Primitive("ui.settextsize", 1, false, this::setTextSize),
            new Primitive("ui.style", 0, false, this::getTextStyle),
            new Primitive("ui.textcolor", 0, false, this::getTextColor),
            new Primitive("ui.textname", 0, false, this::getTextFont),
            new Primitive("ui.textsize", 0, false, this::getTextSize),
            new Primitive("ui.unicode", 1, false, this::getUnicode),
            new Primitive("ui.write", 1, false, this::write)
        );
    }

    void guiMenu(Stack<String> param) {
        String liste;
        try {
            String ident = getWord(param.get(0));
            if (null == ident) throw new LogoException(app, param.get(0) + " " + Logo.getString("interpreter.error.wordRequired"));
            liste = getFinalList(param.get(1));
            GuiMenu gm = new GuiMenu(ident.toLowerCase(), liste, app);
            app.getDrawPanel().addToGuiMap(gm);
        } catch (LogoException ignored) {
        }
    }

    void guiDraw(Stack<String> param) {
        try {
            String ident = getWord(param.get(0));
            if (null == ident) throw new LogoException(app, param.get(0) + " " + Logo.getString("interpreter.error.wordRequired"));
            app.getDrawPanel().guiDraw(ident);
        } catch (LogoException ignored) {
        }
    }

    void guiPosition(Stack<String> param) {
        String liste;
        try {
            String ident = getWord(param.get(0));
            if (null == ident) throw new LogoException(app, param.get(0) + " " + Logo.getString("interpreter.error.wordRequired"));
            liste = getFinalList(param.get(1));
            app.getDrawPanel().guiposition(ident, liste, Utils.primitiveName("ui.guiposition"));
        } catch (LogoException ignored) {
        }
    }

    void guiRemove(Stack<String> param) {
        try {
            String ident = getWord(param.get(0));
            if (null == ident) throw new LogoException(app, param.get(0) + " " + Logo.getString("interpreter.error.wordRequired"));
            app.getDrawPanel().guiRemove(ident);
        } catch (LogoException ignored) {
        }
    }

    void guiAction(Stack<String> param) {
        String liste;
        try {
            String ident = getWord(param.get(0));
            if (null == ident) throw new LogoException(app, param.get(0) + " " + Logo.getString("interpreter.error.wordRequired"));
            liste = getFinalList(param.get(1));
            app.getDrawPanel().guiAction(ident, liste);
        } catch (LogoException ignored) {
        }
    }

    void guiButton(Stack<String> param) {
        String mot;
        try {
            String ident = getWord(param.get(0));
            if (null == ident) throw new LogoException(app, param.get(0) + " " + Logo.getString("interpreter.error.wordRequired"));
            mot = getWord(param.get(1));
            if (null == mot) throw new LogoException(app, param.get(1) + " " + Logo.getString("interpreter.error.wordRequired"));
            GuiButton gb = new GuiButton(ident.toLowerCase(), mot, app);
            app.getDrawPanel().addToGuiMap(gb);
        } catch (LogoException ignored) {
        }
    }

    void getTextStyle(Stack<String> param) {
        StringBuilder buffer = new StringBuilder();
        int compteur = 0;
        if (app.getHistoryPanel().isBold()) {
            buffer.append(Logo.getString("style.bold").toLowerCase()).append(" ");
            compteur++;
        }
        if (app.getHistoryPanel().isItalic()) {
            buffer.append(Logo.getString("style.italic").toLowerCase()).append(" ");
            compteur++;
        }
        if (app.getHistoryPanel().isUnderline()) {
            buffer.append(Logo.getString("style.underline").toLowerCase()).append(" ");
            compteur++;
        }
        if (app.getHistoryPanel().isSuperscript()) {
            buffer.append(Logo.getString("style.superscript").toLowerCase()).append(" ");
            compteur++;
        }
        if (app.getHistoryPanel().isSubscript()) {
            buffer.append(Logo.getString("style.subscript").toLowerCase()).append(" ");
            compteur++;
        }
        if (app.getHistoryPanel().isStrikethrough()) {
            buffer.append(Logo.getString("style.strike").toLowerCase()).append(" ");
            compteur++;
        }
        setReturnValue(true);
        if (compteur == 0) pushResult("\"" + Logo.getString("style.none").toLowerCase());
        else if (compteur == 1) pushResult("\"" + new String(buffer).trim());
        else pushResult("[ " + new String(buffer) + "]");
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
            if (liste.trim().equals("")) liste = Logo.getString("style.none");
            StringTokenizer st = new StringTokenizer(liste);
            while (st.hasMoreTokens()) {
                String element = st.nextToken().toLowerCase();
                if (element.equals(Logo.getString("style.underline").toLowerCase())) {
                    underline = true;
                } else if (element.equals(Logo.getString("style.bold").toLowerCase())) {
                    bold = true;
                } else if (element.equals(Logo.getString("style.italic").toLowerCase())) {
                    italic = true;
                } else if (element.equals(Logo.getString("style.superscript").toLowerCase())) {
                    superscript = true;
                } else if (element.equals(Logo.getString("style.subscript").toLowerCase())) {
                    subscript = true;
                } else if (element.equals(Logo.getString("style.strike").toLowerCase())) {
                    strikethrough = true;
                } else if (element.equals(Logo.getString("style.none").toLowerCase())) {
                    // ignored
                } else throw new LogoException(app, Logo.getString("interpreter.error.setstyle"));
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

    void getCharacter(Stack<String> param) {
        try {
            int i = kernel.getCalculator().getInteger(param.get(0));
            if (i < 0 || i > 65535)
                throw new LogoException(app, param.get(0) + " " + Logo.getString("interpreter.error.unicode"));
            else {
                String st;
                setReturnValue(true);
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
                pushResult(st);
            }
        } catch (LogoException ignored) {
        }
    }

    void getUnicode(Stack<String> param) {
        String mot;
        try {
            mot = getWord(param.get(0));
            if (null == mot) throw new LogoException(app, param.get(0) + " " + Logo.getString("interpreter.error.wordRequired"));
            else if (getWordLength(mot) != 1)
                throw new LogoException(app, param.get(0) + " " + Logo.getString("interpreter.error.requireCharacter"));
            else {
                setReturnValue(true);
                String st = String.valueOf((int) Utils.unescapeString(itemWord(1, mot)).charAt(0));
                pushResult(st);
            }
        } catch (LogoException ignored) {
        }
    }

    void getSeparation(Stack<String> param) {
        setReturnValue(true);
        pushResult(Calculator.formatDouble(app.splitPane.getResizeWeight()));
    }

    void setSeparation(Stack<String> param) {
        try {
            double nombre = kernel.getCalculator().numberDouble(param.get(0));
            if (nombre < 0 || nombre > 1)
                throw new LogoException(app, nombre + " " + Logo.getString("interpreter.error.requireZeroToOne"));
            app.splitPane.setResizeWeight(nombre);
            app.splitPane.setDividerLocation(nombre);
        } catch (LogoException ignored) {
        }
    }

    void write(Stack<String> param) {
        String mot;
        String par = param.get(0).trim();
        if (isList(par)) par = formatList(par.substring(1, par.length() - 1));
        mot = getWord(param.get(0));
        if (null == mot) app.updateHistory("perso", Utils.unescapeString(par));
        else app.updateHistory("perso", Utils.unescapeString(mot));
    }

    void isMouseEvent(Stack<String> param) {
        setReturnValue(true);
        if (app.getDrawPanel().hasMouseEvent()) pushResult(Logo.getString("interpreter.keyword.true"));
        else pushResult(Logo.getString("interpreter.keyword.false"));
    }

    void clearText(Stack<String> param) {
        app.getHistoryPanel().clearText();
    }

    void getTextFont(Stack<String> param) {
        setReturnValue(true);
        pushResult("[ " + HistoryPanel.printFontId + " [ " + Application.fonts[HistoryPanel.printFontId].getFontName() + " ] ] ");
    }

    void setTextFont(Stack<String> param) {
        try {
            int int_police = kernel.getCalculator().getInteger(param.get(0));
            HistoryPanel.printFontId = int_police % Application.fonts.length;
            app.getHistoryPanel().setFontNumber(int_police);
        } catch (LogoException ignored) {
        }
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
            JOptionPane.showMessageDialog(app, jt, "", JOptionPane.INFORMATION_MESSAGE, Logo.getAppIcon());

        } catch (LogoException ignored) {
        }
    }

    void mousePosition(Stack<String> param) {
        pushResult(app.getDrawPanel().getMousePositionList());
        setReturnValue(true);
    }

    void mouseButton(Stack<String> param) {
        while (!app.getDrawPanel().hasMouseEvent()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {
            }
            if (LogoException.thrown) break;
        }
        pushResult(String.valueOf(app.getDrawPanel().getMouseButton()));
        setReturnValue(true);
    }

    void getTextColor(Stack<String> param) {
        setReturnValue(true);
        Color c = app.getHistoryPanel().getTextColor();
        pushResult("[ " + c.getRed() + " " + c.getGreen() + " " + c.getBlue() + " ] ");
    }

    void setTextColor(Stack<String> param) {
        try {
            if (isList(param.get(0))) {
                app.getHistoryPanel().setTextColor(interpreter.rgb(param.get(0), Utils.primitiveName("ui.settextcolor")));
            } else {
                int coul = kernel.getCalculator().getInteger(param.get(0)) % DrawPanel.defaultColors.length;
                if (coul < 0) coul += DrawPanel.defaultColors.length;
                app.getHistoryPanel().setTextColor(DrawPanel.defaultColors[coul]);
            }
        } catch (LogoException ignored) {
        }
    }

    void getTextSize(Stack<String> param) {
        setReturnValue(true);
        pushResult(String.valueOf(app.getHistoryPanel().getFontSize()));
    }

    void setTextSize(Stack<String> param) {
        try {
            int i = kernel.getCalculator().getInteger(param.get(0));
            app.getHistoryPanel().setFontSize(i);
        } catch (LogoException ignored) {
        }
    }

    void readChar(Stack<String> param) {
        while (app.getKey() == -1) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {
            }
            if (LogoException.thrown) break;
        }
        pushResult(String.valueOf(app.getKey()));
        setReturnValue(true);
        app.setKey(-1);
    }

    void isKey(Stack<String> param) {
        setReturnValue(true);
        if (app.getKey() != -1) pushResult(Logo.getString("interpreter.keyword.true"));
        else pushResult(Logo.getString("interpreter.keyword.false"));
    }

    void read(Stack<String> param) {
        String mot;
        try {
            String liste = getFinalList(param.get(0));
            mot = getWord(param.get(1));
            if (null == mot) throw new LogoException(app, Logo.getString("interpreter.error.wordRequired"));
            FontMetrics fm = app.getGraphics().getFontMetrics(Logo.config.getFont());
            int longueur = fm.stringWidth(liste) + 100;
            InputFrame inputFrame = new InputFrame(liste, longueur);
            while (inputFrame.isVisible()) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException ignored) {
                }
            }
            Stack<String> makeParam = new Stack<>();
            makeParam.push("\"" + mot);
            String phrase = inputFrame.getText();
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
            makeParam.push(phrase);
            globalMake(makeParam);
            String texte = liste + "\n" + phrase;
            app.updateHistory("commentaire", Utils.unescapeString(texte) + "\n");
            app.focusCommandLine();
            inputFrame.dispose();
            app.focusCommandLine();
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
}
