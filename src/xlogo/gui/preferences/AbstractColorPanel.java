package xlogo.gui.preferences;

import xlogo.Logo;
import xlogo.kernel.DrawPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public abstract class AbstractColorPanel extends JPanel implements ActionListener {
    private static final long serialVersionUID = 1L;
    protected JComboBox combo_couleur;
    //	private ImageIcon[] images=new ImageIcon[17];
    private final Integer[] intArray = new Integer[17];
    private final JButton bchoisir = new JButton(Logo.messages.getString("pref.highlight.other"));
    private Color couleur_perso = Color.WHITE;

    public AbstractColorPanel(Color c) {
        for (int i = 0; i < 17; i++) {
            intArray[i] = i;
        }
        combo_couleur = new JComboBox(intArray);
        ComboBoxRenderer renderer = new ComboBoxRenderer();
        combo_couleur.setRenderer(renderer);
        setColorAndStyle(c);
        combo_couleur.setActionCommand("combo");
        combo_couleur.addActionListener(this);
        bchoisir.setActionCommand("bouton");
        bchoisir.addActionListener(this);
        add(combo_couleur);
        add(bchoisir);
    }

    public void setEnabled(boolean b) {
        super.setEnabled(b);
        combo_couleur.setEnabled(b);
        bchoisir.setEnabled(b);

    }

    void setColorAndStyle(Color rgb) {
        int index = -1;
        for (int i = 0; i < 17; i++) {
            if (DrawPanel.defaultColors[i].equals(rgb)) {
                index = i;
            }
        }
        if (index == -1) {
            couleur_perso = rgb;
            index = 7;
        }
        combo_couleur.setSelectedIndex(index);
    }

    abstract public void actionPerformed(ActionEvent e);

    public Color getValue() {
        int id = combo_couleur.getSelectedIndex();
        if (id != 7) return DrawPanel.defaultColors[id];
        return couleur_perso;
    }

    protected void actionButton() {
        Color color = JColorChooser.showDialog(this, "", DrawPanel.defaultColors[combo_couleur.getSelectedIndex()]);
        if (null != color) {
            couleur_perso = color;
            combo_couleur.setSelectedIndex(7);
            combo_couleur.repaint();
        }
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