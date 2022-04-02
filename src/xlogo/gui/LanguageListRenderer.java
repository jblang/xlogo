package xlogo.gui;

import xlogo.Language;

import javax.swing.*;
import java.awt.*;

public class LanguageListRenderer implements ListCellRenderer<Language> {
    private final JPanel[] panels;

    public LanguageListRenderer(boolean large) {
        var langs = Language.values();
        panels = new JPanel[langs.length];
        int i = 0;
        for (var lang : langs) {
            var p = panels[i++] = new JPanel();
            p.setOpaque(true);
            var flag = lang.getFlag();
            var nameLabel = new JLabel(lang.name);
            if (large) {
                var factor = (float) 100 / (float) flag.getIconHeight();
                var flagLabel = new JLabel(flag.derive(factor));
                p.setLayout(new BoxLayout(p, BoxLayout.PAGE_AXIS));
                p.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                nameLabel.setAlignmentY(Component.BOTTOM_ALIGNMENT);
                flagLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                flagLabel.setAlignmentY(Component.BOTTOM_ALIGNMENT);
                p.add(flagLabel);
                p.add(nameLabel);
            } else {
                var factor = (float) 40 / (float) flag.getIconWidth();
                var flagLabel = new JLabel(flag.derive(factor));
                p.setLayout(new BoxLayout(p, BoxLayout.LINE_AXIS));
                p.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
                p.add(flagLabel);
                p.add(Box.createRigidArea(new Dimension(5,0)));
                p.add(nameLabel);
            }
        }
    }

    public LanguageListRenderer() {
        this(false);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends Language> list, Language value, int index, boolean isSelected, boolean cellHasFocus) {
        if (index == -1)
            index = list.getSelectedIndex();
        var p = panels[index];
        if (isSelected) {
            p.setBackground(list.getSelectionBackground());
            p.setForeground(list.getSelectionForeground());
        } else {
            p.setBackground(list.getBackground());
            p.setForeground(list.getForeground());
        }
        return p;
    }
}