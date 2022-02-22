/**
 * Title :        XLogo
 * Description :  XLogo is an interpreter for the Logo
 * programming language
 *
 * @author Loïc Le Coq
 */
package xlogo.gui;

import xlogo.Logo;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * This class represents the JPopupMenu when the user clicks with the right button on the command line.<br>
 * It displays the action cut, copy, paste and:<br>
 * In esperanto mode,  the special characters for esperanto are added.
 * @author loic
 *
 */
public class EditorPopupMenu extends JPopupMenu implements ActionListener {
    private static final long serialVersionUID = 1L;
    /**
     * This array contains all special characters for esperanto
     */
    private final String[] car = new String[12];
    /**
     * Items for esperanto special characters
     */
    private final JMenuItem[] jpopcar = new JMenuItem[12];
    /**
     * Item for copy action
     */
    private final JMenuItem jpopcopier = new JMenuItem();
    /**
     * Item for paste action
     */
    private final JMenuItem jpopcoller = new JMenuItem();
    /**
     * Item for cut action
     */
    private final JMenuItem jpopcouper = new JMenuItem();
    /**
     * Separator between items
     */
    private final JPopupMenu.Separator separ = new JPopupMenu.Separator();
    /**
     * The text Component for the Jpopup Menu
     */
    private JTextComponent jt;

    /**
     * This Constructot attached the Jpopup to Text Component jt
     * @param jt The Text Component
     */
    public EditorPopupMenu(JTextComponent jt) {
        this.jt = jt;
        car[0] = "\u0109";
        car[1] = "\u011d";
        car[2] = "\u0125";
        car[3] = "\u0135";
        car[4] = "\u015d";
        car[5] = "\u016d";
        car[6] = "\u0108";
        car[7] = "\u011c";
        car[8] = "\u0124";
        car[9] = "\u0134";
        car[10] = "\u015c";
        car[11] = "\u016c";
        for (int i = 0; i < car.length; i++) {
            jpopcar[i] = new JMenuItem(car[i]);
            jpopcar[i].addActionListener(this);
            jpopcar[i].setActionCommand(car[i]);
        }
        add(jpopcouper);
        add(jpopcopier);
        add(jpopcoller);
        jpopcouper.setActionCommand("menu.edition.cut");
        jpopcouper.addActionListener(this);
        jpopcoller.setActionCommand("menu.edition.paste");
        jpopcoller.addActionListener(this);
        jpopcopier.setActionCommand("menu.edition.copy");
        jpopcopier.addActionListener(this);
        setText();
    }

    /**
     * Called by constructor and when the language is modified.
     */
    void setText() {
        jpopcoller.setText(Logo.messages.getString("menu.edition.paste"));
        jpopcouper.setText(Logo.messages.getString("menu.edition.cut"));
        jpopcopier.setText(Logo.messages.getString("menu.edition.copy"));
        // Si le langage choisie est l'esperanto, on rajoute les caractères accentués spéciaux au menu
        if (Logo.config.getLanguage() == Logo.config.LANGUAGE_ESPERANTO) {
            add(separ);
            for (int i = 0; i < jpopcar.length; i++) {
                add(jpopcar[i]);
            }
        } else if (this.getComponentCount() > 3) {
            remove(separ);
            for (int i = 0; i < jpopcar.length; i++) remove(jpopcar[i]);
        }
    }

    /**
     * The action to dispatch when the user clicks on the popup menu
     */
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        if (cmd.equals("menu.edition.copy")) jt.copy();
        else if (cmd.equals("menu.edition.cut")) jt.cut();
        else if (cmd.equals("menu.edition.copy")) jt.paste();
        else {
            for (int i = 0; i < car.length; i++) {
                if (car[i].equals(cmd)) {
                    int pos = jt.getCaretPosition();
                    try {
                        String debut = jt.getText(0, pos);
                        int longueur = jt.getText().length();
                        String fin = "";
                        if (longueur > pos) fin = jt.getText(pos + 1, longueur - pos);
                        jt.setText(debut + car[i] + fin);
                        jt.setCaretPosition(pos + 1);
                    } catch (BadLocationException e1) {
                    }
                    break;
                }
            }
        }
    }
}
