package xlogo.utils;

import javax.swing.*;
import java.awt.*;

public class GridBagPanel extends JPanel {
    public GridBagConstraints gb;

    public GridBagPanel() {
        super();
        setLayout(new GridBagLayout());
        gb = new GridBagConstraints();
    }

    public GridBagPanel(GridBagConstraints gb) {
        super();
        this.gb = gb;
    }

    public void addRow(int x, int y, JComponent... children) {
        gb.gridx = x;
        gb.gridy = y;
        for (var child : children) {
            if (child != null)
                add(child, gb);
            gb.gridx++;
        }
    }

    public void addCol(int x, int y, JComponent... children) {
        gb.gridx = x;
        gb.gridy = y;
        for (var child : children) {
            if (child != null)
                add(child, gb);
            gb.gridy++;
        }
    }

    public void add(int x, int y, JComponent child) {
        gb.gridx = x;
        gb.gridy = y;
        add(child, gb);
    }

    public void setSpan(int width, int height) {
        gb.gridwidth = width;
        gb.gridheight = height;
    }

    public void setSpan(int width, int height, int fill) {
        setSpan(width, height);
        gb.fill = fill;
    }
}
