package xlogo.StyledDocument;

import xlogo.gui.HistoryPanel;
import xlogo.gui.preferences.Panel_Font;

import javax.swing.text.*;
import java.awt.*;

/**
 * Title :        XLogo
 * Description :  XLogo is an interpreter for the Logo
 * programming language
 *
 * @author Loïc Le Coq
 */

public class DocumentLogoHistorique extends DocumentLogo {
    private static final long serialVersionUID = 1L;
    private Color couleur_texte = Color.BLUE;
    private int taille_texte = 12;
    private String style = "normal";
    private final MutableAttributeSet normal;
    private final MutableAttributeSet erreur;
    private final MutableAttributeSet commentaire;
    private final MutableAttributeSet perso;
    private boolean tape = false;

    public DocumentLogoHistorique() {
        super();

        //Style normal
        normal = new SimpleAttributeSet();
        //StyleConstants.setFontSize(normal, Config.police.getSize());
        //StyleConstants.setFontFamily(normal, Config.police.getName());

        // Style pour l'écriture des erreurs
        erreur = new SimpleAttributeSet();
        //StyleConstants.setForeground(erreur, Color.RED);
        //StyleConstants.setFontSize(erreur, Config.police.getSize());
        //StyleConstants.setFontFamily(erreur, Config.police.getName());

        //Style pour les commentaires (Vous venez de définir ...)
        commentaire = new SimpleAttributeSet();
        //StyleConstants.setForeground(commentaire, Color.BLUE);
        //StyleConstants.setFontSize(commentaire, Config.police.getSize());
        //StyleConstants.setFontFamily(commentaire, Config.police.getName());

        // Style pour la primitive écris et la primitive tape
        perso = new SimpleAttributeSet();
        //StyleConstants.setForeground(perso, Color.BLACK);
        //StyleConstants.setFontFamily(perso, Config.police.getName());


    }

    public void setStyle(String sty) {
        style = sty;
    }

    public Color getCouleurtexte() {
        return couleur_texte;
    }

    public int police() {
        return taille_texte;
    }

    public void fixecouleur(Color color) {
        couleur_texte = color;
        StyleConstants.setForeground(perso, couleur_texte);
    }

    public void fixepolice(int taille) {
        taille_texte = taille;
        StyleConstants.setFontSize(perso, taille_texte);
    }

    public void fixenompolice(int id) {
        StyleConstants.setFontFamily(perso,
                Panel_Font.fontes[HistoryPanel.fontPrint].getName());
    }

    public void fixegras(boolean b) {
        StyleConstants.setBold(perso, b);
    }

    public void fixeitalique(boolean b) {
        StyleConstants.setItalic(perso, b);
    }

    public void fixesouligne(boolean b) {
        StyleConstants.setUnderline(perso, b);
    }

    public void fixeexposant(boolean b) {
        StyleConstants.setSuperscript(perso, b);
    }

    public void fixeindice(boolean b) {
        StyleConstants.setSubscript(perso, b);
    }

    public void fixebarre(boolean b) {
        StyleConstants.setStrikeThrough(perso, b);
    }

    public boolean estgras() {
        return StyleConstants.isBold(perso);
    }

    public boolean estitalique() {
        return StyleConstants.isItalic(perso);
    }

    public boolean estsouligne() {
        return StyleConstants.isUnderline(perso);
    }

    public boolean estexposant() {
        return StyleConstants.isSuperscript(perso);
    }

    public boolean estindice() {
        return StyleConstants.isSubscript(perso);
    }

    public boolean estbarre() {
        return StyleConstants.isStrikeThrough(perso);
    }

    public Font getFont() {
        return Panel_Font.fontes[HistoryPanel.fontPrint].deriveFont(Font.BOLD, (float) taille_texte);

    }

    public void change_police_interface(Font font, int taille) {

        String famille = font.getName();

        StyleConstants.setFontSize(normal, taille);
        StyleConstants.setFontFamily(normal, famille);

        StyleConstants.setFontSize(commentaire, taille);
        StyleConstants.setFontFamily(commentaire, famille);

        StyleConstants.setFontSize(erreur, taille);
        StyleConstants.setFontFamily(erreur, famille);
    }

    public void insertString(int offset, String str, AttributeSet a) throws BadLocationException {
        if (tape) {
            tape = false;
            super.insertStyleNormal(offset, str, a);
        } else super.insertString(offset, str, a);
        if (style.equals("erreur")) this.setCharacterAttributes(offset, str.length(), erreur, true);
        else if (style.equals("commentaire")) this.setCharacterAttributes(offset, str.length(), commentaire, true);
        else if (style.equals("perso")) this.setCharacterAttributes(offset, str.length(), perso, true);
        if (!str.endsWith("\n")) tape = true;
    }

}