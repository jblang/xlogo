package xlogo.gui.preferences;

import xlogo.Config;
import xlogo.Logo;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GridPanel extends JPanel implements ActionListener {
    private static final long serialVersionUID = 1L;
    private JCheckBox checkGrid;
    private JLabel labelXGrid;
    private JLabel labelYGrid;
    private JTextField jtXGrid;
    private JTextField jtYGrid;
    private ColorPanel colorPanelGrid;

    GridPanel() {
        initGui();
    }

    public void actionPerformed(ActionEvent e) {
        boolean b = checkGrid.isSelected();
        jtXGrid.setEnabled(b);
        jtYGrid.setEnabled(b);
        labelXGrid.setEnabled(b);
        labelYGrid.setEnabled(b);
        colorPanelGrid.setEnabled(b);
    }

    protected int getGridColor() {
        return colorPanelGrid.getValue().getRGB();
    }

    protected int getXGrid() {
        try {
            int x = Integer.parseInt(jtXGrid.getText());
            return x;
        } catch (NumberFormatException e) {
            return 20;
        }
    }

    protected int getYGrid() {
        try {
            int x = Integer.parseInt(jtYGrid.getText());
            return x;
        } catch (NumberFormatException e) {
            return 20;
        }
    }

    protected boolean gridVisible() {
        return checkGrid.isSelected();
    }

    private void initGui() {
        checkGrid = new JCheckBox(Logo.messages.getString("active_grid"));
        checkGrid.setSelected(Config.gridEnabled);
        labelXGrid = new JLabel(Logo.messages.getString("xgrid"));
        labelYGrid = new JLabel(Logo.messages.getString("ygrid"));
        jtXGrid = new JTextField(String.valueOf(Config.xGridSpacing));
        jtYGrid = new JTextField(String.valueOf(Config.yGridSpacing));
        colorPanelGrid = new ColorPanel(new Color(Config.gridColor));
        setLayout(new GridBagLayout());
        add(checkGrid, new GridBagConstraints(0, 0, 1, 1, 0.3, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(10, 10, 10, 10), 0, 0));
        add(colorPanelGrid, new GridBagConstraints(0, 1, 1, 1, 0.3, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(10, 10, 10, 10), 0, 0));
        add(labelXGrid, new GridBagConstraints(1, 0, 1, 1, 0.25, 1.0,
                GridBagConstraints.EAST, GridBagConstraints.BOTH,
                new Insets(10, 10, 10, 10), 0, 0));
        add(jtXGrid, new GridBagConstraints(2, 0, 1, 1, 0.25, 1.0,
                GridBagConstraints.WEST, GridBagConstraints.BOTH,
                new Insets(10, 10, 10, 10), 0, 0));
        add(labelYGrid, new GridBagConstraints(1, 1, 1, 1, 0.25, 1.0,
                GridBagConstraints.WEST, GridBagConstraints.BOTH,
                new Insets(10, 10, 10, 10), 0, 0));
        add(jtYGrid, new GridBagConstraints(2, 1, 1, 1, 0.25, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(10, 10, 0, 10), 0, 0));
        labelXGrid.setEnabled(Config.gridEnabled);
        labelYGrid.setEnabled(Config.gridEnabled);
        jtXGrid.setEnabled(Config.gridEnabled);
        jtYGrid.setEnabled(Config.gridEnabled);
        colorPanelGrid.setEnabled(Config.gridEnabled);

        TitledBorder tb = BorderFactory.createTitledBorder(Logo.messages.getString("draw_grid"));
        setBorder(tb);
        checkGrid.addActionListener(this);
    }
}
