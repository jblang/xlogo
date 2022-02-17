package xlogo.document;

import xlogo.Config;
import xlogo.kernel.Primitive;

import javax.swing.text.*;
import java.awt.*;

/**
 * Title :        XLogo
 * Description :  XLogo is an interpreter for the Logo
 * programming language
 *
 * @author Loïc Le Coq
 */


/* Cette classe permet de définir la coloration syntaxique dans l'éditeur
Coloration des primitives
Coloration des nombres, variables ou mot
Coloration des commentaires
Correspondance entre parenthèses ou crochets
*/
public class LogoDocument extends DefaultStyledDocument {
    private static final long serialVersionUID = 1L;
    private final DefaultStyledDocument doc;
    private final Element rootElement;

    private MutableAttributeSet parenthese;
    private MutableAttributeSet normal;
    private MutableAttributeSet keyword;
    private MutableAttributeSet comment;
    private MutableAttributeSet quote;
    private boolean coloration_activee = Config.COLOR_ENABLED;
    private boolean colore_parenthese = false;

    public LogoDocument() {
        doc = this;
        rootElement = doc.getDefaultRootElement();
        putProperty(DefaultEditorKit.EndOfLineStringProperty, "\n");
        initStyles(Config.coloration_commentaire, Config.style_commentaire, Config.coloration_primitive, Config.style_primitive,
                Config.coloration_parenthese, Config.style_parenthese, Config.coloration_operande, Config.style_operande);
    }

    public void setColoration(boolean b) {
        coloration_activee = b;
    }

    public void initStyles(int c_comment, int sty_comment, int c_primitive, int sty_primitive,
                           int c_parenthese, int sty_parenthese, int c_operande, int sty_operande) {
        normal = new SimpleAttributeSet();
        //StyleConstants.setFontFamily(normal, Config.police.getFamily());
        //StyleConstants.setForeground(normal, Color.black);
        StyleConstants.setFontSize(normal, Config.police.getSize());

        comment = new SimpleAttributeSet();
        StyleConstants.setForeground(comment, new Color(c_comment));
        setBoldItalic(sty_comment, comment);
        StyleConstants.setFontSize(comment, Config.police.getSize());

        keyword = new SimpleAttributeSet();
        StyleConstants.setForeground(keyword, new Color(c_primitive));
        setBoldItalic(sty_primitive, keyword);
        StyleConstants.setFontSize(keyword, Config.police.getSize());

        quote = new SimpleAttributeSet();
        StyleConstants.setForeground(quote, new Color(c_operande));
        setBoldItalic(sty_operande, quote);
        StyleConstants.setFontSize(quote, Config.police.getSize());

        parenthese = new SimpleAttributeSet();
        StyleConstants.setForeground(parenthese, new Color(c_parenthese));
        setBoldItalic(sty_parenthese, parenthese);
        StyleConstants.setFontSize(parenthese, Config.police.getSize());
    }

    void setBoldItalic(int id, MutableAttributeSet sty) {
        switch (id) {
            case 0: //aucun style
                StyleConstants.setBold(sty, false);
                StyleConstants.setItalic(sty, false);
                StyleConstants.setUnderline(sty, false);
                break;
            case 1: // Gras
                StyleConstants.setItalic(sty, false);
                StyleConstants.setBold(sty, true);
                StyleConstants.setUnderline(sty, false);
                break;
            case 2: // italique
                StyleConstants.setBold(sty, false);
                StyleConstants.setItalic(sty, true);
                StyleConstants.setUnderline(sty, false);
                break;
            case 3: // Souligné
                StyleConstants.setBold(sty, false);
                StyleConstants.setItalic(sty, false);
                StyleConstants.setUnderline(sty, true);
                break;
        }

    }

    /*
     *  Override to apply syntax highlighting after the document has been updated
     */
    public void insertString(int offset, String str, AttributeSet a) throws BadLocationException {
        if (str.equals("\t")) str = "  ";
        else if (str.equals("[")) {
            if (offset > 0) {
                String element = doc.getText(offset - 1, 1);
                if (!element.equals(" ") && !element.equals("\\")) str = " [";
            }
        } else if (str.equals("(")) {
            if (offset > 0) {
                String element = doc.getText(offset - 1, 1);
                if ("\\ *-+/&|><=(".indexOf(element) == -1) str = " (";
            }
        }
        super.insertString(offset, str, a);
        if (coloration_activee) processChangedLines(offset, str.length());
    }

    /*
     *  Override to apply syntax highlighting after the document has been updated
     */
    public void remove(int offset, int length) throws BadLocationException {
/*		if (getText(offset+length-1,1).equals(" ")){
			if(getLength()>offset+length) {
				String element=getText(offset+length,1);
				if (element.equals("[")) length--;
			}
		}*/
        super.remove(offset, length);

        if (coloration_activee) processChangedLines(offset, 0);
    }

    /*
     *  Determine how many lines have been changed,
     *  then apply highlighting to each line
     */
    public void processChangedLines(int offset, int length)
            throws BadLocationException {
        String content = doc.getText(0, doc.getLength());

        //  The lines affected by the latest document update

        int startLine = rootElement.getElementIndex(offset);
        int endLine = rootElement.getElementIndex(offset + length);

        //  Do the actual highlighting

        for (int i = startLine; i <= endLine; i++) {
            applyHighlighting(content, i);
        }
    }


    /*
     *  Parse the line to determine the appropriate highlighting
     */
    private void applyHighlighting(String content, int line)
            throws BadLocationException {
        int startOffset = rootElement.getElement(line).getStartOffset();
        int endOffset = rootElement.getElement(line).getEndOffset() - 1;
        int lineLength = endOffset - startOffset;
        int contentLength = content.length();

        if (endOffset > contentLength)
            endOffset = contentLength - 1;

        //  set normal attributes for the line

        doc.setCharacterAttributes(startOffset, lineLength, normal, true);

        //  check for single line comment
        // On enlève les éventuels commentaires
        int index = content.indexOf(getSingleLineDelimiter(), startOffset);
        while (index != -1) {
            if (index == 0) {
                break;
            } else if (content.charAt(index - 1) != '\\') {
                break;
            }
            index = content.indexOf(getSingleLineDelimiter(), index + 1);
        }

        if ((index > -1) && (index < endOffset)) {
            doc.setCharacterAttributes(index, endOffset - index + 1, comment, false);
            endOffset = index - 1;
        }

        //  check for tokens
        colore(content, startOffset, endOffset);
    }

    protected boolean isOperator(char character) {
        String operands = "+-/*<=>&|";

        return operands.indexOf(character) != -1;
    }

    /*
     *  Override for other languages
     */
    protected boolean isKeyword(String token) {
        token = token.toLowerCase();
        return Primitive.primitives.containsKey(token);
    }

    protected String getSingleLineDelimiter() {
        return "#";
    }

    public void Montre_Parenthese(int offset) {
        doc.setCharacterAttributes(offset, 1, parenthese, false);
    }

    public void setColore_Parenthese(boolean b) {
        colore_parenthese = b;
    }

    public void colore(String content, int startOffset, int endOffset) {
        int debut = startOffset;
        boolean nouveau_mot = true;
        boolean backslash = false;
        boolean mot = false;
        boolean variable = false;
        for (int i = startOffset; i < endOffset; i++) {
            try { // Sometimes, Exception happens on next line
                char c = content.charAt(i);
                if (c == ' ' || c == '(' || c == ')' || c == '[' || c == ']') {
                    if (!backslash) {
                        String element = content.substring(debut, i);
                        if (mot) {
                            doc.setCharacterAttributes(debut, i - debut, quote, false);
                        } else if (variable) {
                            doc.setCharacterAttributes(debut, i - debut, quote, false);
                        } else if (isKeyword(element)) {
                            doc.setCharacterAttributes(debut, i - debut, keyword, false);
                        } else try {
                            Double.parseDouble(element);
                            doc.setCharacterAttributes(debut, i - debut, quote, false);
                        } catch (NumberFormatException e) {
                        }
                        mot = false;
                        variable = false;
                        debut = i + 1;
                        nouveau_mot = true;
                        if (c != ' ') {
                            if (colore_parenthese) doc.setCharacterAttributes(i, 1, parenthese, false);
                            else {
                                doc.setCharacterAttributes(i, 1, normal, false);
//						System.out.println(i+" normal "+StyleConstants.getFontFamily(normal)+" "+StyleConstants.isBold(normal));
                            }
                        }
                    }
                    backslash = false;
                } else if (nouveau_mot) {
                    if (c == '\"') {
                        mot = true;
                        backslash = false;
                        nouveau_mot = false;
                    } else if (c == ':') {
                        variable = true;
                        backslash = false;
                        nouveau_mot = false;
                    } else if (isOperator(c)) {
                        backslash = false;
                        mot = false;
                        variable = false;
                        nouveau_mot = true;
                        debut = i + 1;
                        doc.setCharacterAttributes(i, 1, keyword, false);
                    } else nouveau_mot = false;
                } else if (c == '\\') {
                    backslash = !backslash;
                    nouveau_mot = false;
                } else if (isOperator(c)) {
                    if (!mot) {
                        String element = content.substring(debut, i);
                        if (variable) doc.setCharacterAttributes(debut, i - debut, quote, false);
                        else if (isKeyword(element)) doc.setCharacterAttributes(debut, i - debut, keyword, false);
                        else try {
                                Double.parseDouble(element);
                                doc.setCharacterAttributes(debut, i - debut, quote, false);
                            } catch (NumberFormatException e) {
                            }
                        backslash = false;
                        mot = false;
                        variable = false;
                        nouveau_mot = true;
                        debut = i + 1;
                        doc.setCharacterAttributes(i, 1, keyword, false);
                    }
                }
            } catch (StringIndexOutOfBoundsException e22) {
            }
        }
        if (!nouveau_mot) {
            String element = content.substring(debut, endOffset);
            if (mot) {
                doc.setCharacterAttributes(debut, endOffset - debut, quote, false);
            } else if (variable) {
                doc.setCharacterAttributes(debut, endOffset - debut, quote, false);
            } else if (isKeyword(element)) {
                doc.setCharacterAttributes(debut, endOffset - debut, keyword, false);
            } else try {
                Double.parseDouble(element);
                doc.setCharacterAttributes(debut, endOffset - debut, quote, false);
            } catch (NumberFormatException e) {
            }
        }

    }

    public void insertStyleNormal(int offset, String str, AttributeSet a) {
        try {
            super.insertString(offset, str, a);
        } catch (BadLocationException e) {
        }
    }
}
