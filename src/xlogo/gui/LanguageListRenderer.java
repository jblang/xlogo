package xlogo.gui;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import xlogo.Logo;

import javax.swing.*;
import java.awt.*;

public class LanguageListRenderer extends JLabel implements ListCellRenderer<String> {
    private final ImageIcon[] flag;

    public LanguageListRenderer() {
        flag = new ImageIcon[Logo.translationLanguage.length];
        for (int i = 0; i < Logo.translationLanguage.length; i++) {
            FlatSVGIcon icon = Logo.getFlag(i);
            float factor = (float) 40 / (float) icon.getIconWidth();
            flag[i] = icon.derive(factor);
        }
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends String> list, String value, int index, boolean isSelected, boolean cellHasFocus) {
        if (index == -1)
            index = list.getSelectedIndex();
        setText(value);
        setIcon(flag[index]);
        setOpaque(true);
        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        return this;
    }
}
