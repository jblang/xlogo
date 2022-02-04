/**
 * Title :        XLogo
 * Description :  XLogo is an interpreter for the Logo
 * programming language
 *
 * @author Loïc Le Coq
 */

package xlogo.kernel.grammar;


import xlogo.Config;
import xlogo.Logo;
import xlogo.kernel.DrawPanel;
import xlogo.kernel.Primitive;

public class LogoParser {
    private char c;
    private int cursor;
    private final String text;

    /**
     *
     * @param text The String input reader
     */
    public LogoParser(String text) {
        this.text = text;
        cursor = 0;
        while (cursor < text.length()) {
            LogoType lt = getToken();
            if (Config.DEBUG) System.out.println("[DEBUG] Token " + lt.toDebug() + " cursor " + cursor);
        }
    }

    private LogoType getToken() {
        boolean isQuotedWord = false;
        boolean isVariable = false;

        StringBuffer sb = new StringBuffer();
        boolean start = false;
        boolean backslash = false;
        do {
            c = text.charAt(cursor);
            // Skip White Spaces
            if (c == ' ' || c == '\t') {
                if (start) break;
                else cursor++;
            } else {
                if (backslash) {
                    if (c == ' ') sb.append(" ");
                    else if (c == '#') sb.append("#");
                    else if (c == '\\') sb.append("\\");
                    else if (c == '(') sb.append("(");
                    else if (c == ')') sb.append(")");
                    else if (c == '[') sb.append("[");
                    else if (c == ']') sb.append("]");
                    else if (c == 'n') sb.append("\n");
                    else sb.append(c);
                    cursor++;
                } else {
                    // If it's the first character, check for type
                    if (!start) {
                        if (c == ':') isVariable = true;
                        else if (c == '\"') isQuotedWord = true;
                    }
                    if (c == '\\') backslash = true;

                    else if (c == '[') {
                        if (start) break;
                        else {
                            cursor++;
                            return extractList();
                        }
                    } else if (c == '(') {
                        if (start) break;
                    } else if (c == '*' || c == '/' || c == '+' || c == '-' || c == '|' || c == '&' || c == '=') {
                        if (!isQuotedWord) {
                            if (!start) {
                                sb.append(c);
                                cursor++;
                            }
                            break;

                        } else cursor++;
                    } else if (c == ')') return new LogoException(Logo.messages
                            .getString("parenthese_ouvrante"));
                    else if (c == ']') return new LogoException(Logo.messages
                            .getString("error.whattodo") + " ]");
                    else {
                        sb.append(c);
                        cursor++;
                    }
                }
                start = true;
            }
        } while (cursor < text.length());
        if (sb.length() == 0) return DrawPanel.nullType;
        else if (isQuotedWord) return new LogoWord(sb.substring(1));
        else if (isVariable) return new LogoVariable(sb.substring(1));
        try {
            double d = Double.parseDouble(sb.toString());
            return new LogoNumber(d);
        } catch (NumberFormatException e) {
            int id = Primitive.isPrimitive(sb.toString());
            if (id != -1) {
                return new LogoPrimitive(id, sb.toString());
            }
            return new LogoException(Logo.messages.getString("je_ne_sais_pas") + " " + sb);
        }

    }

    /**
     * This method extracts a list.
     * @return a LogoList if operation succeed,
     * 			a LogoException otherwise
     */

    private LogoType extractList() {
        LogoList list = new LogoList();
        while (cursor < text.length()) {
            LogoType lt = getListToken();
            if (lt.isNull()) {
                return new LogoException(Logo.messages.getString("erreur_crochet"));
            } else if (lt.isRightDelimiter()) return list;
            else {
                list.add(lt);
            }
        }
        return new LogoException(Logo.messages.getString("erreur_crochet"));
    }

    private LogoType getListToken() {
        StringBuffer sb = new StringBuffer();
        boolean start = false;
        boolean backslash = false;
        for (int i = cursor; i < text.length(); i++) {
            cursor = i;
            c = text.charAt(i);
            // Skip White Spaces
            if (c == ' ' || c == '\t') {
                if (start) break;
            } else {
                if (backslash) {
                    if (c == ' ') sb.append(" ");
                    else if (c == '#') sb.append("#");
                    else if (c == '\\') sb.append("\\");
                    else if (c == '(') sb.append("(");
                    else if (c == ')') sb.append(")");
                    else if (c == '[') sb.append("[");
                    else if (c == ']') sb.append("]");
                    else if (c == 'n') sb.append("\n");
                    else sb.append(c);
                } else {
                    if (c == '\\') {
                        backslash = true;
                    } else if (c == '[') {
                        if (start) break;
                        else {
                            cursor++;
                            return
                                    extractList();
                        }
                    } else if (c == ']') {
                        if (start) break;
                        else {
                            System.out.println("coucou");
                            cursor++;
                            return new LogoRightDelimiter();
                        }
                    } else sb.append(c);
                }
                start = true;
            }
        }
        if (sb.length() == 0) return DrawPanel.nullType;
        return new LogoWord(sb.toString());
    }


}
