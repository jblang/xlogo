package xlogo.kernel.perspective;

import xlogo.Logo;
import xlogo.gui.preferences.ColorPanel;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3f;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LightDialog extends JDialog implements ActionListener {
    private static final long serialVersionUID = 1L;
    ColorPanel colorPanel;
    private final String[] type = {Logo.getString("3d.light.none"), Logo.getString("3d.light.ambient"),
            Logo.getString("3d.light.directional"), Logo.getString("3d.light.point"),
            Logo.getString("3d.light.spot")};
    private JComboBox comboType;
    private PanelPosition panelPosition;
    private PanelPosition panelDirection;
    private PanelAngle panelAngle;
    private JLabel labelType;
    private JButton ok;
    private JButton refresh;
    private final Viewer3D viewer3d;
    private final Light light;

    LightDialog(Viewer3D viewer3d, Light light, String title) {
        super(viewer3d, title, true);
        this.viewer3d = viewer3d;
        this.light = light;
        initGui();
    }

    private void initGui() {
        getContentPane().setLayout(new GridBagLayout());
        setSize(500, 200);
        labelType = new JLabel(Logo.getString("3d.light.type"));
        comboType = new JComboBox(type);
        comboType.setSelectedIndex(light.getType());

        Color3f col = light.getColor();
        if (null != col) colorPanel = new ColorPanel(col.get());
        else colorPanel = new ColorPanel(Color.white);
        colorPanel.setBackground(comboType.getBackground());

        panelPosition = new PanelPosition(Logo.getString("3d.light.position"), light.getPosition());
        panelDirection = new PanelPosition(Logo.getString("3d.light.direction"), light.getDirection());
        panelAngle = new PanelAngle(light.getAngle());
        ok = new JButton(Logo.getString("pref.ok"));
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
        getContentPane().add(colorPanel, new GridBagConstraints(0, 1, 2, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(
                0, 0, 0, 0), 0, 0));
        getContentPane().add(panelPosition, new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(
                0, 0, 0, 0), 0, 0));
        getContentPane().add(panelDirection, new GridBagConstraints(1, 2, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(
                0, 0, 0, 0), 0, 0));
        getContentPane().add(panelAngle, new GridBagConstraints(0, 3, 1, 1, 1.0, 1.0,
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
            updateLight();
            dispose();
        }
        // Button Apply pressed
        else if (cmd.equals("refresh")) {
            updateLight();
        }
    }

    private void updateLight() {
        int t = comboType.getSelectedIndex();
        Color3f c = new Color3f(colorPanel.getValue());
        Point3f p = panelPosition.getPosition();
        Vector3f d = panelDirection.getDirection();
        float a = panelAngle.getAngleValue();
        light.setType(t);
        light.setColor(c);
        light.setPosition(p);
        light.setDirection(d);
        light.setAngle(a);
        light.detach();
        light.removeAllChildren();
        light.createLight();
//		System.out.println(c+" "+" "+p+" "+d);
        viewer3d.addNode(light);

    }

    private void selectComponents() {
        int id = comboType.getSelectedIndex();
        // None
        if (id == Light.LIGHT_OFF) {
            colorPanel.setEnabled(false);
            panelPosition.setEnabled(false);
            panelDirection.setEnabled(false);
            panelAngle.setEnabled(false);
        }
        // Ambient
        else if (id == Light.LIGHT_AMBIENT) {
            colorPanel.setEnabled(true);
            panelPosition.setEnabled(false);
            panelDirection.setEnabled(false);
            panelAngle.setEnabled(false);
        }
        // Directional
        else if (id == Light.LIGHT_DIRECTIONAL) {
            colorPanel.setEnabled(true);
            panelPosition.setEnabled(false);
            panelDirection.setEnabled(true);
            panelAngle.setEnabled(false);
        }
        // PointLight
        else if (id == Light.LIGHT_POINT) {
            colorPanel.setEnabled(true);
            panelPosition.setEnabled(true);
            panelDirection.setEnabled(false);
            panelAngle.setEnabled(false);
        }
        // Spot
        else if (id == Light.LIGHT_SPOT) {
            colorPanel.setEnabled(true);
            panelPosition.setEnabled(true);
            panelDirection.setEnabled(true);
            panelAngle.setEnabled(true);
        }
    }

    class PanelAngle extends JPanel {
        private static final long serialVersionUID = 1L;
        private JLabel label;
        private JTextField angle;
        private final float angleValue;

        PanelAngle(float angleValue) {
            this.angleValue = angleValue;

            initGui();
        }

        private void initGui() {
            label = new JLabel(Logo.getString("3d.light.angle"));
            label.setFont(Logo.config.getFont());
            angle = new JTextField(String.valueOf(angleValue));
            angle.setSize(30, Logo.config.getFont().getSize() + 10);
            add(label);
            add(angle);
        }

        public void setEnabled(boolean b) {
            super.setEnabled(b);
            label.setEnabled(b);
            angle.setEnabled(b);
        }

        float getAngleValue() {
            try {
                float f = Float.parseFloat(angle.getText());
                return f;
            } catch (NumberFormatException e) {
            }
            return Light.DEFAULT_ANGLE;
        }
    }

    class PanelPosition extends JPanel {
        private static final long serialVersionUID = 1L;
        Tuple3f tuple;
        private final String title;
        private JTextField Xpos;
        private JTextField Ypos;
        private JTextField Zpos;
        private JLabel sep1;
        private JLabel sep2;

        PanelPosition(String title, Tuple3f tuple) {
            this.title = title;
            this.tuple = tuple;
            initGui();
        }

        private void initGui() {
            TitledBorder tb = BorderFactory.createTitledBorder(title);
            tb.setTitleFont(Logo.config.getFont());
            setBorder(tb);
            sep1 = new JLabel("x");
            sep2 = new JLabel("x");
            Xpos = new JTextField(4);
            Ypos = new JTextField(4);
            Zpos = new JTextField(4);
            if (null != tuple) {
                Xpos.setText(String.valueOf((int) (tuple.x * 1000)));
                Ypos.setText(String.valueOf((int) (tuple.y * 1000)));
                Zpos.setText(String.valueOf((int) (tuple.z * 1000)));
            }
            add(Xpos);
            add(sep1);
            add(Ypos);
            add(sep2);
            add(Zpos);
        }


        Point3f getPosition() {
            try {
                float x = Float.parseFloat(Xpos.getText());
                float y = Float.parseFloat(Ypos.getText());
                float z = Float.parseFloat(Zpos.getText());
                return new Point3f(x / 1000, y / 1000, z / 1000);
            } catch (NumberFormatException e) {
            }
            return (new Point3f(0, 0, 0));
        }

        Vector3f getDirection() {
            try {
                float x = Float.parseFloat(Xpos.getText());
                float y = Float.parseFloat(Ypos.getText());
                float z = Float.parseFloat(Zpos.getText());
                return new Vector3f(x / 1000, y / 1000, z / 1000);
            } catch (NumberFormatException e) {
            }
            return (new Vector3f(0, 0, 0));

        }

        public void setEnabled(boolean b) {
            super.setEnabled(b);
            sep1.setEnabled(b);
            sep2.setEnabled(b);
            Xpos.setEnabled(b);
            Ypos.setEnabled(b);
            Zpos.setEnabled(b);
        }

    }

}
