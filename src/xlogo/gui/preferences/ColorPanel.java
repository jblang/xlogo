package xlogo.gui.preferences;

import xlogo.Logo;
import xlogo.kernel.DrawPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ColorPanel extends JPanel implements ActionListener {

    private final JButton chooseButton = new JButton(Logo.messages.getString("pref.highlight.other"));
    protected JComboBox<Integer> colorCombo;
    private Color customColor = Color.WHITE;

    public ColorPanel(Color c) {
        Integer[] colorNumbers = new Integer[17];
        for (int i = 0; i < 17; i++) {
            colorNumbers[i] = i;
        }
        colorCombo = new JComboBox<>(colorNumbers);
        colorCombo.setRenderer(new ComboBoxRenderer());
        setColorAndStyle(c);
        colorCombo.setActionCommand("combo");
        colorCombo.addActionListener(this);
        chooseButton.setActionCommand("bouton");
        chooseButton.addActionListener(this);
        add(colorCombo);
        add(chooseButton);
    }

    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        if (cmd.equals("bouton")) {
            actionButton();
        }
    }

    public void setEnabled(boolean b) {
        super.setEnabled(b);
        colorCombo.setEnabled(b);
        chooseButton.setEnabled(b);

    }

    void setColorAndStyle(Color rgb) {
        int index = -1;
        for (int i = 0; i < 17; i++) {
            if (DrawPanel.defaultColors[i].equals(rgb)) {
                index = i;
            }
        }
        if (index == -1) {
            customColor = rgb;
            index = 7;
        }
        colorCombo.setSelectedIndex(index);
    }

    public Color getValue() {
        int id = colorCombo.getSelectedIndex();
        if (id != 7) return DrawPanel.defaultColors[id];
        return customColor;
    }

    protected void actionButton() {
        Color color = JColorChooser.showDialog(this, "", DrawPanel.defaultColors[colorCombo.getSelectedIndex()]);
        if (null != color) {
            customColor = color;
            colorCombo.setSelectedIndex(7);
            colorCombo.repaint();
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
            int selectedIndex = (Integer) value;
            id = selectedIndex;
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
            else g.setColor(customColor);
            g.fillRect(5, 2, 40, 15);
        }
    }
}
