package xlogo.gui;

import xlogo.Logo;
import xlogo.document.LogoDocument;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;

/**
 * Title :        XLogo
 * Description :  XLogo is an interpreter for the Logo
 * programming language
 *
 * @author Loïc Le Coq
 */
class HighlightedTextPane extends JTextPane implements CaretListener {
    private static final long serialVersionUID = 1L;
    private LogoDocument dsd = null;
    // Si la correspondance entre parenthese ou crochets est activée
    private boolean active = false;
    // Dernière position allumée
    private final int[] position = new int[2];


    HighlightedTextPane(EditorTextPane etp) {
        dsd = etp.getDsd();
        addCaretListener(this);
    }

    //	public LogoDocument getDsd(){
//		return dsd;
//	}
    public void setActive(boolean b) {
        active = b;
    }

    // Teste si le caractère précédent est un backslash
    private boolean TesteBackslash(String content, int pos) {
        String caractere = "";
        if (pos > 0) caractere = content.substring(pos - 1, pos);
        //System.out.println(caractere);
        return caractere.equals("\\");
    }

    public void caretUpdate(CaretEvent e) {
        int pos = e.getDot();
        if (Logo.config.isSyntaxHighlightingEnabled()) SwingUtilities.invokeLater(new verif_parenthese(pos));
    }

    void chercheApres(String content, int pos, String ouv, String fer) {
        boolean continuer = true;
        int of_ouvrant;
        int of_fermant = 0;
        int from_index_ouvrant = 1;
        int from_index_fermant = 1;
        while (continuer) {
            of_ouvrant = content.indexOf(ouv, from_index_ouvrant);
            while (of_ouvrant != -1 && TesteBackslash(content, of_ouvrant))
                of_ouvrant = content.indexOf(ouv, of_ouvrant + 1);
            of_fermant = content.indexOf(fer, from_index_fermant);
            while (of_fermant != -1 && TesteBackslash(content, of_fermant))
                of_fermant = content.indexOf(fer, of_fermant + 1);
            if (of_fermant == -1) break;
            if (of_ouvrant != -1 && of_ouvrant < of_fermant) {
                from_index_ouvrant = of_ouvrant + 1;
                from_index_fermant = of_fermant + 1;
            } else continuer = false;
        }
        if (of_fermant != -1) {
            dsd.Montre_Parenthese(of_fermant + pos);
            position[1] = of_fermant + pos;
        } else position[1] = -1;
        dsd.Montre_Parenthese(pos);
        position[0] = pos;
    }

    void chercheAvant(String content, int pos, String ouv, String fer) {
        boolean continuer = true;
        int of_fermant = 0;
        int of_ouvrant = 0;
        int from_index_ouvrant = pos;
        int from_index_fermant = pos;
        while (continuer) {
            of_ouvrant = content.lastIndexOf(ouv, from_index_ouvrant);
            while (of_ouvrant != -1 && TesteBackslash(content, of_ouvrant))
                of_ouvrant = content.lastIndexOf(ouv, of_ouvrant - 1);
            of_fermant = content.lastIndexOf(fer, from_index_fermant);
            while (of_fermant != -1 && TesteBackslash(content, of_fermant))
                of_fermant = content.lastIndexOf(fer, of_fermant - 1);
            if (of_ouvrant == -1) break;
            if (of_ouvrant < of_fermant) {
                from_index_ouvrant = of_ouvrant - 1;
                from_index_fermant = of_fermant - 1;
            } else continuer = false;
        }
        if (of_ouvrant != -1) {
            dsd.Montre_Parenthese(of_ouvrant);
            position[0] = of_ouvrant;
        } else position[0] = -1;
        dsd.Montre_Parenthese(pos);
        position[1] = pos;


    }

    class verif_parenthese implements Runnable {
        int pos;

        verif_parenthese(int pos) {
            this.pos = pos;
        }

        public void run() {
            if (active) {
                active = false;
                int debut = position[0];
                int fin = position[0];
                if (debut != -1) {
                    if (debut > 0) debut--;
                    if (fin < dsd.getLength()) fin++;
                    try {
                        String content = dsd.getText(0, dsd.getLength());
                        dsd.colore(content, debut, fin);
                    } catch (BadLocationException e) {
                    }
                }
                debut = position[1];
                fin = position[1];
                if (debut != -1) {
                    if (debut > 0) debut--;
                    if (fin < dsd.getLength()) debut++;
                    if (fin < dsd.getLength()) fin++;
                    try {
                        String content = dsd.getText(0, dsd.getLength());
                        dsd.colore(content, debut, fin);
                    } catch (BadLocationException e) {
                    }
                }
            }
            int length = dsd.getLength();
            try {
                String content = dsd.getText(pos, length - pos);
                int id = -1;
                if (length > pos) {
                    id = "[]()".indexOf(content.substring(0, 1));
                }
                if (id > -1 && !TesteBackslash(dsd.getText(0, pos), pos)) {
                    active = true;
                    switch (id) {
                        case 0:
                            chercheApres(content, pos, "[", "]");
                            break;
                        case 1:
                            content = getText(0, pos);
                            chercheAvant(content, pos, "[", "]");
                            break;
                        case 2:
                            chercheApres(content, pos, "(", ")");
                            break;
                        case 3:
                            content = getText(0, pos);
                            chercheAvant(content, pos, "(", ")");
                            break;
                    }
                }
            } catch (BadLocationException e1) {
            }

        }
    }

}