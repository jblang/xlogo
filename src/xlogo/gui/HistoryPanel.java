package xlogo.gui;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import xlogo.Application;
import xlogo.Config;
import xlogo.Logo;
import xlogo.document.HistoricLogoDocument;
import xlogo.gui.preferences.FontPanel;
import xlogo.kernel.DrawPanel;
import xlogo.utils.ExtensionFilter;
import xlogo.utils.LogoException;
import xlogo.utils.Utils;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import javax.swing.text.rtf.RTFEditorKit;
import java.awt.*;
import java.awt.event.*;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Title :        XLogo
 * Description :  XLogo is an interpreter for the Logo
 * programming language
 *
 * @author Loïc Le Coq
 */
public class HistoryPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    // numéro identifiant la police de
    // l'historique avec "ecris"
    public static int fontPrint = FontPanel.police_id(Config.police);
    private final ImageIcon ianimation = new FlatSVGIcon("xlogo/icons/cwmCamOn.svg");
    private final JLabel label_animation = new JLabel(ianimation);
    private MouseAdapter mouseAdapt;
    private final Color couleur_texte = Color.BLUE;
    private final int taille_texte = 12;
    private final JScrollPane jScrollPane1 = new JScrollPane();
    private final Historique historique = new Historique();
    private HistoricLogoDocument dsd;
    private final BorderLayout borderLayout1 = new BorderLayout();
    private Application cadre;

    public HistoryPanel() {
    }

    public HistoryPanel(Application cadre) {
        this.cadre = cadre;
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        dsd = new HistoricLogoDocument();
        historique.setDocument(dsd);
    }

    public Color getCouleurtexte() {
        return couleur_texte;
    }

    public int police() {
        return taille_texte;
    }

    public void vide_texte() {
        historique.setText("");
    }

    public void ecris(String sty, String texte) {
        try {
            int longueur = historique.getDocument().getLength();
            if (texte.length() > 32000) throw new LogoException(cadre, Logo.messages.getString("chaine_trop_longue"));
            if (longueur + texte.length() < 65000) {
                try {
                    dsd.setStyle(sty);
                    dsd.insertString(dsd.getLength(), texte, null);
                    historique.setCaretPosition(dsd.getLength());
                } catch (BadLocationException e) {
                }
            } else {
                vide_texte();
            }
        } catch (LogoException e2) {
        }
    }

    private void jbInit() throws Exception {
        this.setLayout(borderLayout1);
        historique.setEditable(false);
        label_animation.setToolTipText(Logo.messages.getString("animation_active"));
        this.add(jScrollPane1, BorderLayout.CENTER);
        jScrollPane1.getViewport().add(historique, null);
    }


    public void active_animation() {
        add(label_animation, BorderLayout.WEST);
        DrawPanel.classicMode = DrawPanel.MODE_ANIMATION;
        mouseAdapt = new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                stop_animation();
                cadre.getArdoise().repaint();
            }
        };
        label_animation.addMouseListener(mouseAdapt);
        validate();
    }

    public void stop_animation() {
        DrawPanel.classicMode = DrawPanel.MODE_CLASSIC;
        remove(label_animation);
        label_animation.removeMouseListener(mouseAdapt);
        validate();
    }

    // Change Syntax Highlighting for the editor
    public void initStyles(int c_comment, int sty_comment, int c_primitive, int sty_primitive,
                           int c_parenthese, int sty_parenthese, int c_operande, int sty_operande) {
        dsd.initStyles(Config.coloration_commentaire, Config.style_commentaire, Config.coloration_primitive, Config.style_primitive,
                Config.coloration_parenthese, Config.style_parenthese, Config.coloration_operande, Config.style_operande);
    }

    // Enable or disable Syntax Highlighting
    public void setColoration(boolean b) {
        dsd.setColoration(b);
    }

    public void changeFont(Font f) {
        historique.setFont(f);
    }

    public void updateText() {
        historique.setText();
    }

    public void changeLanguage() {
    }

    public HistoricLogoDocument getDsd() {
        return dsd;
    }

    public StyledDocument sd_Historique() {
        return historique.getStyledDocument();
    }


    class Historique extends JTextPane implements ActionListener {
        private static final long serialVersionUID = 1L;
        private final JPopupMenu popup = new JPopupMenu();
        private final JMenuItem jpopcopier = new JMenuItem();
        private final JMenuItem jpopselect = new JMenuItem();
        private final JMenuItem jpopsave = new JMenuItem();

        Historique() {
            popup.add(jpopcopier);
            popup.add(jpopselect);
            popup.add(jpopsave);
            jpopselect.addActionListener(this);
            jpopcopier.addActionListener(this);
            jpopsave.addActionListener(this);
            setText();
            MouseListener popupListener = new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (e.getButton() == 1) {
                        int i = getCaretPosition();
                        int borneinf = borne(i, -1);
                        int bornesup = borne(i, 1);
                        if (borneinf == 0) borneinf = borneinf - 1;
                        select(borneinf + 1, bornesup - 2);
                        cadre.setCommandText(getSelectedText());
//				    historique.setCaretPosition(historique.getDocument().getLength());
                        cadre.focus_Commande();
                    }
                }

                public void mouseReleased(MouseEvent e) {
                    maybeShowPopup(e);
                    cadre.focus_Commande();
                }

                public void mousePressed(MouseEvent e) {
                    maybeShowPopup(e);
                }

                private void maybeShowPopup(MouseEvent e) {
                    if (e.isPopupTrigger()) {
                        popup.show(e.getComponent(), e.getX(), e.getY());
                    }
                }
            };
            addMouseListener(popupListener);
        }

        int borne(int i, int increment) {
            boolean continuer = true;
            while (continuer && i != 0) {
                select(i - 1, i);
                String t = historique.getSelectedText();
                if (t.equals("\n")) {
                    continuer = false;
                }
                i = i + increment;
            }
            return (i);
        }

        void setText() {
            jpopselect.setText(Logo.messages.getString("menu.edition.selectall"));
            jpopcopier.setText(Logo.messages.getString("menu.edition.copy"));
            jpopsave.setText(Logo.messages.getString("menu.file.textzone.rtf"));
            jpopselect.setActionCommand(Logo.messages.getString("menu.edition.selectall"));
            jpopcopier.setActionCommand(Logo.messages.getString("menu.edition.copy"));
            jpopsave.setActionCommand(Logo.messages.getString("menu.file.textzone.rtf"));
        }

        public void actionPerformed(ActionEvent e) {
            String cmd = e.getActionCommand();
            if (Logo.messages.getString("menu.edition.copy").equals(cmd)) {   //Copier
                copy();
            } else if (Logo.messages.getString("menu.edition.selectall").equals(cmd)) {   //Selectionner tout
                requestFocus();
                selectAll();
                cadre.focus_Commande();
            } else if (cmd.equals(Logo.messages.getString("menu.file.textzone.rtf"))) {
                RTFEditorKit myRTFEditorKit = new RTFEditorKit();
                StyledDocument myStyledDocument = getStyledDocument();
                try {
                    JFileChooser jf = new JFileChooser(Utils.SortieTexte(Config.defaultFolder));
                    String[] ext = {".rtf"};
                    jf.addChoosableFileFilter(new ExtensionFilter(Logo.messages.getString("fichiers_rtf"), ext));
                    int retval = jf.showDialog(cadre, Logo.messages.getString("menu.file.save"));
                    if (retval == JFileChooser.APPROVE_OPTION) {
                        String path = jf.getSelectedFile().getPath();
                        String path2 = path.toLowerCase();  // on garde la casse du path pour les systèmes d'exploitation faisant la différence
                        if (!path2.endsWith(".rtf")) path += ".rtf";
                        FileOutputStream myFileOutputStream = new FileOutputStream(path);
                        myRTFEditorKit.write(myFileOutputStream, myStyledDocument, 0, myStyledDocument.getLength() - 1);
                        myFileOutputStream.close();

                    }
                } catch (FileNotFoundException e1) {
                } catch (IOException e2) {
                } catch (BadLocationException e3) {
                } catch (NullPointerException e4) {
                }
            }
        }
    }
}
