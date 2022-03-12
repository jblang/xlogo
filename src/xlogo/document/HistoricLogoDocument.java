package xlogo.document;

import xlogo.gui.HistoryPanel;
import xlogo.gui.preferences.FontPanel;

import javax.swing.text.*;
import java.awt.*;

/**
 * Title :        XLogo
 * Description :  XLogo is an interpreter for the Logo
 * programming language
 *
 * @author Loïc Le Coq
 */

public class HistoricLogoDocument extends LogoDocument {
    private static final long serialVersionUID = 1L;
    private Color textColor = Color.BLUE;
    private int fontSize = 12;
    private String style = "normal";
    private final MutableAttributeSet normal;
    private final MutableAttributeSet error;
    private final MutableAttributeSet comment;
    private final MutableAttributeSet primitive;
    private boolean tape = false;

    public HistoricLogoDocument() {
        super();

        //Style normal
        normal = new SimpleAttributeSet();

        // Style pour l'écriture des erreurs
        error = new SimpleAttributeSet();

        //Style pour les commentaires (Vous venez de définir ...)
        comment = new SimpleAttributeSet();

        // Style pour la primitive écris et la primitive tape
        primitive = new SimpleAttributeSet();
    }

    public void setStyle(String sty) {
        style = sty;
    }

    public Color getTextColor() {
        return textColor;
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setTextColor(Color color) {
        textColor = color;
        StyleConstants.setForeground(primitive, textColor);
    }

    public void setFontSize(int size) {
        fontSize = size;
        StyleConstants.setFontSize(primitive, fontSize);
    }

    public void setFontNumber(int id) {
        StyleConstants.setFontFamily(primitive,
                FontPanel.fontes[HistoryPanel.fontPrint].getName());
    }

    public void setBold(boolean b) {
        StyleConstants.setBold(primitive, b);
    }

    public void setItalic(boolean b) {
        StyleConstants.setItalic(primitive, b);
    }

    public void setUnderline(boolean b) {
        StyleConstants.setUnderline(primitive, b);
    }

    public void setSuperscript(boolean b) {
        StyleConstants.setSuperscript(primitive, b);
    }

    public void setSubscript(boolean b) {
        StyleConstants.setSubscript(primitive, b);
    }

    public void setStrikeThrough(boolean b) {
        StyleConstants.setStrikeThrough(primitive, b);
    }

    public boolean isBold() {
        return StyleConstants.isBold(primitive);
    }

    public boolean isItalic() {
        return StyleConstants.isItalic(primitive);
    }

    public boolean isUnderline() {
        return StyleConstants.isUnderline(primitive);
    }

    public boolean isSuperscript() {
        return StyleConstants.isSuperscript(primitive);
    }

    public boolean isSubscript() {
        return StyleConstants.isSubscript(primitive);
    }

    public boolean isStrikethrough() {
        return StyleConstants.isStrikeThrough(primitive);
    }

    public Font getFont() {
        return FontPanel.fontes[HistoryPanel.fontPrint].deriveFont(Font.BOLD, (float) fontSize);
    }

    public void setFont(Font font, int taille) {

        String famille = font.getName();

        StyleConstants.setFontSize(normal, taille);
        StyleConstants.setFontFamily(normal, famille);

        StyleConstants.setFontSize(comment, taille);
        StyleConstants.setFontFamily(comment, famille);

        StyleConstants.setFontSize(error, taille);
        StyleConstants.setFontFamily(error, famille);
    }

    public void insertString(int offset, String str, AttributeSet a) throws BadLocationException {
        if (tape) {
            tape = false;
            super.insertStyleNormal(offset, str, a);
        } else super.insertString(offset, str, a);
        if (style.equals("erreur")) this.setCharacterAttributes(offset, str.length(), error, true);
        else if (style.equals("commentaire")) this.setCharacterAttributes(offset, str.length(), comment, true);
        else if (style.equals("perso")) this.setCharacterAttributes(offset, str.length(), primitive, true);
        if (!str.endsWith("\n")) tape = true;
    }

}