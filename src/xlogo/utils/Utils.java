/**
 * Title :        XLogo
 * Description :  XLogo is an interpreter for the Logo
 * programming language
 *
 * @author Loïc Le Coq
 */
package xlogo.utils;

import xlogo.Logo;
import xlogo.kernel.Animation;
import xlogo.kernel.Calculator;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

public class Utils {

    public static String escapeString(String st) {
        StringBuilder sb = new StringBuilder();
        for (int j = 0; j < st.length(); j++) {
            char c = st.charAt(j);
            if (c == '\\') sb.append("\\\\");
            else if (c == ' ') sb.append("\\e");
            else if ("()[]#".indexOf(c) != -1) sb.append("\\").append(c);
            else sb.append(c);
        }
        return (new String(sb));
    }

    public static String unescapeString(String chaine) { // Enlève les backslash
        StringBuilder sb = new StringBuilder();
        boolean backslash = false;
        boolean ignore = false;
        for (int j = 0; j < chaine.length(); j++) {
            char c = chaine.charAt(j);
            if (backslash) {
                if (c == 'e') sb.append(' ');
                else if (c == 'n') sb.append("\n");
                else if (c == 'v') {
                }
                else if (c == 'l') {
                    ignore = true;
                } else if ("[]()#\\".indexOf(c) > -1) sb.append(c);
                backslash = false;
            } else {
                if (c == '\\') backslash = true;
                else if (!ignore) sb.append(c);
                else if (c == ' ') ignore = false;
            }
        }
        return Calculator.getOutputNumber(new String(sb));
    }

    /**
     * This method is formatting the String st.<br>
     * - Unused white spaces are deleted.<br>
     * - The character \ is modified to \\ <br>
     * - The sequence "\ " is modified to "\e"<br>
     * @param st The String instruction to format
     * @return The formatted instructions
     */
    public static StringBuffer formatCode(String st) {
        StringBuffer sb = new StringBuffer();
        // If last character is a white space
        boolean space = false;
        // If last character is a backslash
        boolean backslash = false;
        // If last character is a word
        boolean word = false;

        int brackets = 0;
//		boolean variable=false;
        // If XLogo is running a program
        boolean launched = Animation.executionLaunched;
        for (int i = 0; i < st.length(); i++) {
            char c = st.charAt(i);
            if (c == ' ') {
                if (!space && sb.length() != 0) {
                    if (backslash) sb.append("\\e");
                    else {
                        sb.append(c);
                        space = true;
                        word = false;
                    }
                    backslash = false;
                }
            } else if (c == '\\' && !backslash) {
                space = false;
                backslash = true;
            } else if (c == '\"') {
                if (space && brackets <= 0) {
                    word = true;
                }
                sb.append(c);
                space = false;
                backslash = false;
            } else if (c == ':') {
                sb.append(c);
                space = false;
                backslash = false;
            } else if (c == '[' || c == ']' || c == '(' || c == ')') {
                //Modifications apportées
                if (backslash) {
                    sb.append("\\").append(c);
                    backslash = false;
                } else {
                    if (c == '[') brackets++;
                    else if (c == ']') brackets--;
                    if (space || sb.length() == 0) {
                        sb.append(c).append(" ");
                    } else {
                        sb.append(" ").append(c).append(" ");
                        word = false;
                    }
                    space = true;
                }
            } else if (c == '+' || c == '-' || c == '*' || c == '/' || c == '=' || c == '<' || c == '>' || c == '&' || c == '|') {
                // à modifier (test + fin)
                if (word || brackets > 0) {
                    sb.append(c);
                    if (space) space = false;
                } else {
                    String op = String.valueOf(c);
                    // Looking for operator <= or >=
                    if (c == '<' || c == '>') {
                        if (i + 1 < st.length()) {
                            if (st.charAt(i + 1) == '=') {
                                op += "=";
                                i++;
                            }
                        }
                    }
                    if (space) sb.append(op).append(" ");
                    else {
                        space = true;
                        if (sb.length() != 0) sb.append(" ").append(op).append(" ");
                            // If buffer is empty no white space before
                        else sb.append(op).append(" ");
                    }
                }
            } else {
                if (backslash) {
                    if (c == 'n') sb.append("\\n");
                    else if (c == '\\') sb.append("\\\\");
                    else if (c == 'v' && launched) sb.append("\"");
                    else if (c == 'e' && launched) sb.append("\\e");
                    else if (c == '#') sb.append("\\#");
                    else if (c == 'l' && launched) sb.append("\\l");
                    else {
                        sb.append(c);
                    }
                } else {
                    sb.append(c);
                }
                backslash = false;
                space = false;
            }
        }
        return (sb);
    }

    public static String readLogoFile(String path) throws IOException {
        String txt = "";
        // The old format before XLogo 0.9.23 is no longer supported from version 0.9.30
        try {
            // New format for XLogo >=0.923
            // Encoded with UTF-8
            StringBuilder sb = new StringBuilder();
            FileInputStream fr = new FileInputStream(path);
            InputStreamReader isr = new InputStreamReader(fr, StandardCharsets.UTF_8);
            BufferedReader brd = new BufferedReader(isr);
            while (brd.ready()) {
                sb.append(brd.readLine());
                sb.append("\n");
            }
            txt = new String(sb);
        } catch (FileNotFoundException e1) {
            // tentative fichier réseau
            try {
                URL url = new java.net.URL(path);
                StringBuilder sb = new StringBuilder();
                java.io.InputStream fr = url.openStream();
                InputStreamReader isr = new InputStreamReader(fr, StandardCharsets.UTF_8);
                BufferedReader brd = new BufferedReader(isr);
                while (brd.ready()) {
                    String st = brd.readLine();
                    sb.append(st);
                    sb.append("\n");
                }
                txt = new String(sb);
            } catch (java.net.MalformedURLException e) {
                System.out.println("File not found: " + path);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (txt.startsWith("# " + Logo.messages.getString("mainCommand"))) {
            int id = txt.indexOf("\n");
            if (id != -1) {
                Logo.setMainCommand(txt.substring(("# " + Logo.messages.getString("mainCommand")).length(), id).trim());
                txt = txt.substring(id + 1);
            }
        }
        return txt;
    }

    public static void writeLogoFile(String path, String txt) throws IOException {
        try {
            if (!Logo.getMainCommand().trim().equals("")) {
                String heading = "# " + Logo.messages.getString("mainCommand") + " " + Logo.getMainCommand() + "\n";
                txt = heading + txt;
            }
            FileOutputStream f = new FileOutputStream(path);
            BufferedOutputStream b = new BufferedOutputStream(f);
            OutputStreamWriter osw = new OutputStreamWriter(b, StandardCharsets.UTF_8);
            osw.write(txt);
            osw.close();
            b.close();
            f.close();

        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }
    }

    public static String primitiveName(String generic) {
        Locale locale = Logo.getLocale(Logo.config.getLanguage());
        ResourceBundle prim = ResourceBundle.getBundle(
                "primitives", locale);
        String st = prim.getString(generic);
        StringTokenizer str = new StringTokenizer(st);
        while (str.hasMoreTokens()) {
            st = str.nextToken();
        }
        return st;
    }

    public static JButton createButton(JToolBar parent, String iconName, String toolTip, ActionListener listener) {
        var button = new JButton(Logo.getIcon(iconName));
        if (toolTip != null)
            button.setToolTipText(Logo.messages.getString(toolTip));
        button.addActionListener(listener);
        parent.add(button);
        return button;
    }

    public static JButton createButton(JToolBar parent, String iconName, ActionListener listener) {
        return createButton(parent, iconName, null, listener);
    }
}