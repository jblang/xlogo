package xlogo.gui;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import xlogo.Logo;
import xlogo.resources.ResourceLoader;

import javax.swing.*;
import java.awt.*;

public class LanguageListRenderer implements ListCellRenderer<String> {
    private final ImageIcon[] flag;
    private final boolean large;

    public LanguageListRenderer(boolean large) {
        this.large = large;
        flag = new ImageIcon[Logo.nativeLanguages.length];
        for (int i = 0; i < Logo.nativeLanguages.length; i++) {
            FlatSVGIcon icon = ResourceLoader.getFlag(i);
            float factor;
            if (large)
                factor = (float) 100 / (float) icon.getIconHeight();
            else
                factor = (float) 40 / (float) icon.getIconWidth();
            flag[i] = icon.derive(factor);
        }
    }

    public LanguageListRenderer() {
        this(false);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends String> list, String value, int index, boolean isSelected, boolean cellHasFocus) {
        if (index == -1)
            index = list.getSelectedIndex();
        var p = new JPanel();
        p.setOpaque(true);
        if (isSelected) {
            p.setBackground(list.getSelectionBackground());
            p.setForeground(list.getSelectionForeground());
        } else {
            p.setBackground(list.getBackground());
            p.setForeground(list.getForeground());
        }
        var flagLabel = new JLabel(flag[index]);
        var textLabel = new JLabel(value);
        if (this.large) {
            p.setLayout(new BoxLayout(p, BoxLayout.PAGE_AXIS));
            p.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            textLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            textLabel.setAlignmentY(Component.BOTTOM_ALIGNMENT);
            flagLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            flagLabel.setAlignmentY(Component.BOTTOM_ALIGNMENT);
            p.add(flagLabel);
            p.add(textLabel);
        } else {
            p.setLayout(new BoxLayout(p, BoxLayout.LINE_AXIS));
            p.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
            p.add(flagLabel);
            p.add(Box.createRigidArea(new Dimension(5,0)));
            p.add(textLabel);
        }
        return p;
    }
}
