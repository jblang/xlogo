package xlogo.gui.preferences;

import xlogo.Logo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AxisPanel extends JPanel implements ActionListener {
    private static final long serialVersionUID = 1L;
    private JCheckBox xAxisEnabled;
    private JCheckBox yAxisEnabled;
    private JLabel xAxisLabel;
    private JLabel yAxisLabel;
    private JTextField xAxisSpacing;
    private JTextField yAxisSpacing;
    private ColorPanel axisColor;

    AxisPanel() {
        initGui();
    }

    public void actionPerformed(ActionEvent e) {
        boolean b1 = xAxisEnabled.isSelected();
        boolean b2 = yAxisEnabled.isSelected();
        xAxisLabel.setEnabled(b1);
        yAxisLabel.setEnabled(b2);
        xAxisSpacing.setEnabled(b1);
        yAxisSpacing.setEnabled(b2);
        axisColor.setEnabled(b1 || b2);
    }

    protected int getAxisColor() {
        return axisColor.getValue().getRGB();
    }

    protected int getXAxis() {
        try {
            int x = Integer.parseInt(xAxisSpacing.getText());
            return x;
        } catch (NumberFormatException e) {
            return 30;
        }
    }

    protected int getYAxis() {
        try {
            int x = Integer.parseInt(yAxisSpacing.getText());
            return x;
        } catch (NumberFormatException e) {
            return 30;
        }
    }

    protected boolean xAxisVisible() {
        return xAxisEnabled.isSelected();
    }

    protected boolean yAxisVisible() {
        return yAxisEnabled.isSelected();
    }

    private void initGui() {
        setBorder(BorderFactory.createTitledBorder(Logo.getString("title_axis")));

        xAxisEnabled = new JCheckBox(Logo.getString("active_xaxis"));
        xAxisEnabled.setSelected(Logo.config.isXAxisEnabled());
        yAxisEnabled = new JCheckBox(Logo.getString("active_yaxis"));
        yAxisEnabled.setSelected(Logo.config.isYAxisEnabled());
        xAxisLabel = new JLabel(Logo.getString("pas"));
        yAxisLabel = new JLabel(Logo.getString("pas"));
        xAxisSpacing = new JTextField(String.valueOf(Logo.config.getXAxisSpacing()));
        yAxisSpacing = new JTextField(String.valueOf(Logo.config.getYAxisSpacing()));
        axisColor = new ColorPanel(new Color(Logo.config.getAxisColor()));
        setLayout(new GridBagLayout());
        var standard = new Insets(1, 1, 1, 1);
        add(xAxisEnabled, new GridBagConstraints(0, 0, 1, 1, 0.25, 1.0,
                GridBagConstraints.LINE_START, GridBagConstraints.NONE,
                standard, 0, 0));
        add(xAxisLabel, new GridBagConstraints(1, 0, 1, 1, 0.25, 1.0,
                GridBagConstraints.LINE_START, GridBagConstraints.NONE,
                standard, 0, 0));
        add(xAxisSpacing, new GridBagConstraints(2, 0, 1, 1, 0.25, 1.0,
                GridBagConstraints.LINE_START, GridBagConstraints.NONE,
                standard, 0, 0));
        add(yAxisEnabled, new GridBagConstraints(3, 0, 1, 1, 0.25, 1.0,
                GridBagConstraints.LINE_START, GridBagConstraints.NONE,
                standard, 0, 0));
        add(yAxisLabel, new GridBagConstraints(4, 0, 1, 1, 0.25, 1.0,
                GridBagConstraints.LINE_START, GridBagConstraints.NONE,
                standard, 0, 0));
        add(yAxisSpacing, new GridBagConstraints(5, 0, 1, 1, 0.25, 1.0,
                GridBagConstraints.LINE_START, GridBagConstraints.NONE,
                standard, 0, 0));
        add(axisColor, new GridBagConstraints(6, 0, 1, 1, 0.25, 1.0,
                GridBagConstraints.LINE_START, GridBagConstraints.NONE,
                standard, 0, 0));
        xAxisLabel.setEnabled(Logo.config.isXAxisEnabled());
        yAxisLabel.setEnabled(Logo.config.isYAxisEnabled());
        xAxisSpacing.setEnabled(Logo.config.isXAxisEnabled());
        yAxisSpacing.setEnabled(Logo.config.isYAxisEnabled());
        axisColor.setEnabled(Logo.config.isXAxisEnabled() || Logo.config.isYAxisEnabled());
        xAxisEnabled.addActionListener(this);
        yAxisEnabled.addActionListener(this);
    }


}
