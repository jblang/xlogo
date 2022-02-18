package xlogo.gui.preferences;

import xlogo.Config;
import xlogo.Logo;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AxisPanel extends JPanel implements ActionListener {
    private static final long serialVersionUID = 1L;
    private JCheckBox checkXAxis;
    private JCheckBox checkYAxis;
    private JLabel labelXAxis;
    private JLabel labelYAxis;
    private JTextField jtXAxis;
    private JTextField jtYAxis;
    private ColorPanel panelAxisColor;

    AxisPanel() {
        initGui();
    }

    public void actionPerformed(ActionEvent e) {
        boolean b1 = checkXAxis.isSelected();
        boolean b2 = checkYAxis.isSelected();
        labelXAxis.setEnabled(b1);
        labelYAxis.setEnabled(b2);
        jtXAxis.setEnabled(b1);
        jtYAxis.setEnabled(b2);
        panelAxisColor.setEnabled(b1 || b2);
    }

    protected int getAxisColor() {
        return panelAxisColor.getValue().getRGB();
    }

    protected int getXAxis() {
        try {
            int x = Integer.parseInt(jtXAxis.getText());
            return x;
        } catch (NumberFormatException e) {
            return 30;
        }
    }

    protected int getYAxis() {
        try {
            int x = Integer.parseInt(jtYAxis.getText());
            return x;
        } catch (NumberFormatException e) {
            return 30;
        }
    }

    protected boolean xAxisVisible() {
        return checkXAxis.isSelected();
    }

    protected boolean yAxisVisible() {
        return checkYAxis.isSelected();
    }

    private void initGui() {
        checkXAxis = new JCheckBox(Logo.messages.getString("active_xaxis"));
        checkXAxis.setSelected(Config.xAxisEnabled);
        checkYAxis = new JCheckBox(Logo.messages.getString("active_yaxis"));
        checkYAxis.setSelected(Config.yAxisEnabled);
        labelXAxis = new JLabel(Logo.messages.getString("pas"));
        labelYAxis = new JLabel(Logo.messages.getString("pas"));
        jtXAxis = new JTextField(String.valueOf(Config.xAxisSpacing));
        jtYAxis = new JTextField(String.valueOf(Config.yAxisSpacing));
        panelAxisColor = new ColorPanel(new Color(Config.axisColor));
        setLayout(new GridBagLayout());
        add(checkXAxis, new GridBagConstraints(0, 0, 1, 1, 0.3, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(10, 10, 10, 10), 0, 0));
        add(labelXAxis, new GridBagConstraints(1, 0, 1, 1, 0.25, 1.0,
                GridBagConstraints.EAST, GridBagConstraints.BOTH,
                new Insets(10, 10, 10, 10), 0, 0));
        add(jtXAxis, new GridBagConstraints(2, 0, 1, 1, 0.25, 1.0,
                GridBagConstraints.WEST, GridBagConstraints.BOTH,
                new Insets(10, 10, 10, 10), 0, 0));
        add(checkYAxis, new GridBagConstraints(0, 1, 1, 1, 0.3, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(10, 10, 10, 10), 0, 0));
        add(labelYAxis, new GridBagConstraints(1, 1, 1, 1, 0.25, 1.0,
                GridBagConstraints.EAST, GridBagConstraints.BOTH,
                new Insets(10, 10, 10, 10), 0, 0));
        add(jtYAxis, new GridBagConstraints(2, 1, 1, 1, 0.25, 1.0,
                GridBagConstraints.WEST, GridBagConstraints.BOTH,
                new Insets(10, 10, 10, 10), 0, 0));
        add(panelAxisColor, new GridBagConstraints(0, 2, 1, 1, 0.3, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(10, 10, 10, 10), 0, 0));
        labelXAxis.setEnabled(Config.xAxisEnabled);
        labelYAxis.setEnabled(Config.yAxisEnabled);
        jtXAxis.setEnabled(Config.xAxisEnabled);
        jtYAxis.setEnabled(Config.yAxisEnabled);
        panelAxisColor.setEnabled(Config.xAxisEnabled || Config.yAxisEnabled);

        TitledBorder tb = BorderFactory.createTitledBorder(Logo.messages.getString("title_axis"));
        setBorder(tb);
        checkXAxis.addActionListener(this);
        checkYAxis.addActionListener(this);
    }


}
