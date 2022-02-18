package xlogo.gui.preferences;

import xlogo.Config;
import xlogo.Logo;
import xlogo.kernel.DrawPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Title :        XLogo
 * Description :  XLogo is an interpreter for the Logo
 * programming language
 *
 * @author Loïc Le Coq
 */
public class SyntaxStylePanel extends JPanel implements ActionListener {
    private static final long serialVersionUID = 1L;
    private final Integer[] intArray = new Integer[17];
    private final JButton bchoisir = new JButton(Logo.messages.getString("pref.highlight.other"));
    private final JComboBox combo_couleur;
    private final String[] msg = {Logo.messages.getString("style.none"), Logo.messages.getString("style.bold"), Logo.messages.getString("style.italic"), Logo.messages.getString("style.underline")};
    private final JComboBox style = new JComboBox(msg);
    private final JLabel titre = new JLabel();
    private Color couleur_perso = Color.WHITE;
    private final GridBagLayout gb = new GridBagLayout();
    private final HighlighterPanel pc;

    public SyntaxStylePanel(int rgb, int sty, String ti, HighlighterPanel pc) {
        this.pc = pc;
        setLayout(gb);

        for (int i = 0; i < 17; i++) {
            intArray[i] = i;
        }
        //Create the combo box.
        titre.setText(ti + ":");

        combo_couleur = new JComboBox(intArray);
        ComboBoxRenderer renderer = new ComboBoxRenderer();
        combo_couleur.setRenderer(renderer);
        setColorAndStyle(rgb, sty);
        combo_couleur.setActionCommand("combo");
        combo_couleur.addActionListener(this);
        bchoisir.setActionCommand("bouton");
        bchoisir.addActionListener(this);

        style.setActionCommand("style");
        style.addActionListener(this);
        int hauteur = Config.font.getSize() + 5;
//	        jt.setPreferredSize(new Dimension(240,hauteur));

        //Lay out the demo.
        add(combo_couleur, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(
                5, 5, 5, 5), 0, 0));
        add(bchoisir, new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
                5, 5, 5, 5), 0, 0));
        add(style, new GridBagConstraints(2, 1, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(
                5, 5, 5, 5), 0, 0));
        add(titre, new GridBagConstraints(0, 0, 3, 1, 1.0, 1.0,
                GridBagConstraints.WEST, GridBagConstraints.VERTICAL, new Insets(
                5, 5, 0, 5), 0, 0));

        setBorder(BorderFactory.createTitledBorder(""));
        setPreferredSize(new Dimension(300, hauteur * 2 + 20));
    }

    void setColorAndStyle(int rgb, int sty) {
        style.setSelectedIndex(sty);
        int index = -1;
        for (int i = 0; i < 17; i++) {
            if (DrawPanel.defaultColors[i].getRGB() == rgb) {
                index = i;
            }
        }
        if (index == -1) {
            couleur_perso = new Color(rgb);
            index = 7;
        }
        combo_couleur.setSelectedIndex(index);
    }

    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        if (cmd.equals("bouton")) {
            Color color = JColorChooser.showDialog(this, "", DrawPanel.defaultColors[combo_couleur.getSelectedIndex()]);
            if (null != color) {
                couleur_perso = color;
                combo_couleur.setSelectedIndex(7);
                combo_couleur.repaint();
                pc.rafraichis_texte();
            }
        } else if (cmd.equals("combo")) {
            pc.rafraichis_texte();
        } else if (cmd.equals("style")) {
            pc.rafraichis_texte();
        }
    }

    public int color() {
        int id = combo_couleur.getSelectedIndex();
        if (id != 7) return DrawPanel.defaultColors[id].getRGB();
        return couleur_perso.getRGB();
    }

    public int style() {
        int id = style.getSelectedIndex();
        return id;
    }

    public void setEnabled(boolean b) {
        super.setEnabled(b);
        combo_couleur.setEnabled(b);
        style.setEnabled(b);
        bchoisir.setEnabled(b);
    }

    private class ComboBoxRenderer extends JPanel
            implements ListCellRenderer {
        private static final long serialVersionUID = 1L;
        int id = 0;

        public ComboBoxRenderer() {
            setOpaque(true);
            setPreferredSize(new Dimension(50, 20));
        }

        public Component getListCellRendererComponent(
                JList list,
                Object value,
                int index,
                boolean isSelected,
                boolean cellHasFocus) {
//Get the selected index. (The index param isn't
//always valid, so just use the value.)
            int selectedIndex = ((Integer) value).intValue();
            this.id = selectedIndex;
            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }
            //Set the icon and text.  If icon was null, say so.

            setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
            return this;
        }

        public void paint(Graphics g) {
            super.paint(g);
            if (id != 7) g.setColor(DrawPanel.defaultColors[id]);
            else g.setColor(couleur_perso);
            g.fillRect(5, 2, 40, 15);
        }
    }

}
	