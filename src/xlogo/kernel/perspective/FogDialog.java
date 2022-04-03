package xlogo.kernel.perspective;

import xlogo.Logo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FogDialog extends JDialog implements ActionListener {
    private static final long serialVersionUID = 1L;
    private final String[] type = {Logo.getString("3d.fog.none"), Logo.getString("3d.fog.linear"),
            Logo.getString("3d.fog.exponential")};
    private JComboBox comboType;
    private PanelValue panelDensity;
    private PanelValue panelFront;
    private PanelValue panelBack;
    private JLabel labelType;
    private JButton ok;
    private JButton refresh;
    private final Viewer3D viewer3d;
    private final Fog fog;

    FogDialog(Viewer3D viewer3d, Fog fog, String title) {
        super(viewer3d, title, true);
        this.viewer3d = viewer3d;
        this.fog = fog;
        initGui();
    }

    private void initGui() {
        getContentPane().setLayout(new GridBagLayout());
        setSize(450, 200);
        labelType = new JLabel(Logo.getString("3d.fog.type"));
        comboType = new JComboBox(type);
        comboType.setSelectedIndex(fog.getType());

        panelDensity = new PanelValue(fog.getDensity(), Logo.getString("3d.fog.density"));
        panelFront = new PanelValue((int) (fog.getFront() * 1000), Logo.getString("3d.fog.frontdistance"));
        panelBack = new PanelValue((int) (fog.getBack() * 1000), Logo.getString("3d.fog.backdistance"));

        ok = new JButton(Logo.getString("button.ok"));
        refresh = new JButton(Logo.getString("3d.light.apply"));
        labelType.setFont(Logo.config.getFont());
        comboType.setFont(Logo.config.getFont());
        ok.setFont(Logo.config.getFont());
        refresh.setFont(Logo.config.getFont());

        comboType.addActionListener(this);
        comboType.setActionCommand("combo");
        ok.addActionListener(this);
        ok.setActionCommand("ok");
        refresh.addActionListener(this);
        refresh.setActionCommand("refresh");

        getContentPane().add(labelType, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
                0, 0, 0, 0), 0, 0));
        getContentPane().add(comboType, new GridBagConstraints(1, 0, 1, 1, 1.0, 1.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(
                0, 0, 0, 0), 0, 0));
        getContentPane().add(panelFront, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(
                0, 0, 0, 0), 0, 0));
        getContentPane().add(panelBack, new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(
                0, 0, 0, 0), 0, 0));
        getContentPane().add(panelDensity, new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(
                0, 0, 0, 0), 0, 0));
        getContentPane().add(refresh, new GridBagConstraints(0, 4, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
                0, 0, 0, 0), 0, 0));
        getContentPane().add(ok, new GridBagConstraints(1, 4, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
                0, 0, 0, 0), 0, 0));
        selectComponents();
        setVisible(true);

    }

    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        // The selected item in the combo Box has changed
        if (cmd.equals("combo")) selectComponents();
            // Button Ok pressed
        else if (cmd.equals("ok")) {
            updateFog();
            dispose();
        }
        // Button Apply pressed
        else if (cmd.equals("refresh")) {
            updateFog();
        }
    }

    private void updateFog() {
        int t = comboType.getSelectedIndex();
        float d = panelDensity.getTextValue();
        float back = panelBack.getTextValue();
        float front = panelFront.getTextValue();
        fog.setType(t);
        fog.setDensity(d);
        fog.setBack(back / 1000);
        fog.setFront(front / 1000);
        fog.detach();
        fog.removeAllChildren();
        fog.createFog();
        viewer3d.addNode(fog);
    }

    private void selectComponents() {
        int id = comboType.getSelectedIndex();
        // None
        if (id == Fog.FOG_OFF) {
            panelDensity.setEnabled(false);
            panelBack.setEnabled(false);
            panelFront.setEnabled(false);
        }
        // Linear Fog
        else if (id == Fog.FOG_LINEAR) {
            panelDensity.setEnabled(false);
            panelBack.setEnabled(true);
            panelFront.setEnabled(true);
        }
        // Exponential Fog
        else if (id == Fog.FOG_EXPONENTIAL) {
            panelBack.setEnabled(false);
            panelFront.setEnabled(false);
            panelDensity.setEnabled(true);
        }
    }


    class PanelValue extends JPanel {
        private static final long serialVersionUID = 1L;
        private JLabel label;
        private JTextField text;
        private final String title;
        private final float textValue;

        PanelValue(float textValue, String title) {
            this.textValue = textValue;
            this.title = title;
            initGui();
        }

        private void initGui() {
            label = new JLabel(title);
            label.setFont(Logo.config.getFont());
            String st;
            int i = (int) textValue;
            if (i == textValue) {
                st = String.valueOf(i);

            } else st = String.valueOf(textValue);
            text = new JTextField(st, 4);
            add(label);
            add(text);
        }

        public void setEnabled(boolean b) {
            super.setEnabled(b);
            label.setEnabled(b);
            text.setEnabled(b);
        }

        float getTextValue() {
            try {
                float f = Float.parseFloat(text.getText());
                return f;
            } catch (NumberFormatException e) {
            }
            return 0;
        }
    }

}
