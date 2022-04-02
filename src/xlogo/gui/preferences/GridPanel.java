package xlogo.gui.preferences;

import xlogo.Logo;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GridPanel extends JPanel implements ActionListener {
    private static final long serialVersionUID = 1L;
    private JCheckBox gridEnabled;
    private JLabel xSpacingLabel;
    private JLabel ySpacingLabel;
    private JTextField xSpacing;
    private JTextField ySpacing;
    private ColorPanel colorPanelGrid;

    GridPanel() {
        initGui();
    }

    public void actionPerformed(ActionEvent e) {
        boolean b = gridEnabled.isSelected();
        xSpacing.setEnabled(b);
        ySpacing.setEnabled(b);
        xSpacingLabel.setEnabled(b);
        ySpacingLabel.setEnabled(b);
        colorPanelGrid.setEnabled(b);
    }

    protected int getGridColor() {
        return colorPanelGrid.getValue().getRGB();
    }

    protected int getXGrid() {
        try {
            int x = Integer.parseInt(xSpacing.getText());
            return x;
        } catch (NumberFormatException e) {
            return 20;
        }
    }

    protected int getYGrid() {
        try {
            int x = Integer.parseInt(ySpacing.getText());
            return x;
        } catch (NumberFormatException e) {
            return 20;
        }
    }

    protected boolean gridVisible() {
        return gridEnabled.isSelected();
    }

    private void initGui() {
        gridEnabled = new JCheckBox(Logo.getString("active_grid"));
        gridEnabled.setSelected(Logo.config.isGridEnabled());
        xSpacingLabel = new JLabel(Logo.getString("xgrid"));
        ySpacingLabel = new JLabel(Logo.getString("ygrid"));
        xSpacing = new JTextField(String.valueOf(Logo.config.getXGridSpacing()));
        ySpacing = new JTextField(String.valueOf(Logo.config.getYGridSpacing()));
        colorPanelGrid = new ColorPanel(new Color(Logo.config.getGridColor()));
        setLayout(new GridBagLayout());
        var standard = new Insets(1, 1, 1, 1);
        add(gridEnabled, new GridBagConstraints(0, 0, 1, 1, 0.25, 1.0,
                GridBagConstraints.LINE_START, GridBagConstraints.NONE,
                standard, 0, 0));
        add(xSpacingLabel, new GridBagConstraints(1, 0, 1, 1, 0.25, 1.0,
                GridBagConstraints.LINE_START, GridBagConstraints.NONE,
                standard, 0, 0));
        add(xSpacing, new GridBagConstraints(2, 0, 1, 1, 0.25, 1.0,
                GridBagConstraints.LINE_START, GridBagConstraints.NONE,
                standard, 0, 0));
        add(ySpacingLabel, new GridBagConstraints(3, 0, 1, 1, 0.25, 1.0,
                GridBagConstraints.LINE_START, GridBagConstraints.NONE,
                standard, 0, 0));
        add(ySpacing, new GridBagConstraints(4, 0, 1, 1, 0.25, 1.0,
                GridBagConstraints.LINE_START, GridBagConstraints.NONE,
                standard, 0, 0));
        add(colorPanelGrid, new GridBagConstraints(5, 0, 1, 1, 0.25, 1.0,
                GridBagConstraints.LINE_START, GridBagConstraints.NONE,
                standard, 0, 0));
        xSpacingLabel.setEnabled(Logo.config.isGridEnabled());
        ySpacingLabel.setEnabled(Logo.config.isGridEnabled());
        xSpacing.setEnabled(Logo.config.isGridEnabled());
        ySpacing.setEnabled(Logo.config.isGridEnabled());
        colorPanelGrid.setEnabled(Logo.config.isGridEnabled());

        TitledBorder tb = BorderFactory.createTitledBorder(Logo.getString("draw_grid"));
        setBorder(tb);
        gridEnabled.addActionListener(this);
    }
}
