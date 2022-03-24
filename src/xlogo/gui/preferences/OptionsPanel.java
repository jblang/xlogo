package xlogo.gui.preferences;

import xlogo.Logo;
import xlogo.gui.Application;
import xlogo.gui.MessageTextArea;

import javax.swing.*;
import java.awt.*;

/**
 * Title :        XLogo
 * Description :  XLogo is an interpreter for the Logo
 * programming language
 *
 * @author Loïc Le Coq
 */
public class OptionsPanel extends JPanel {
    private final Application app;
    private final JCheckBox clearScreen = new JCheckBox(Logo.messages.getString("effacer_editeur"));
    private final JCheckBox clearVariables = new JCheckBox(Logo.messages.getString("pref.options.eraseVariables"));

    private final ColorPanel screenColorPanel = new ColorPanel(Logo.config.getScreenColor());
    private final ColorPanel penColorPanel = new ColorPanel(Logo.config.getPenColor());
    private final String[] penShapeOptions = {Logo.messages.getString("carre"), Logo.messages.getString("rond")};
    private final JComboBox<String> penShape = new JComboBox<>(penShapeOptions);
    private final JTextField maxTurtles = new JTextField();
    private final JTextField penSize = new JTextField();
    private final JTextField imageWidth = new JTextField(String.valueOf(Logo.config.getImageWidth()));
    private final JTextField imageHeight = new JTextField(String.valueOf(Logo.config.getImageHeight()));
    private final JTextField memoryLimit = new JTextField(String.valueOf(Logo.config.getMemoryLimit()));
    private final String[] imageQualityOptions = {Logo.messages.getString("normal"), Logo.messages.getString("haut"), Logo.messages.getString("bas")};
    private final JComboBox<String> imageQuality = new JComboBox<>(imageQualityOptions);
    private final GridPanel gridPanel = new GridPanel();
    private final AxisPanel axisPanel = new AxisPanel();
    private final JTextField tcpPort = new JTextField(String.valueOf(Logo.config.getTcpPort()));

    protected OptionsPanel(Application app) {
        this.app = app;
        setLayout(new GridBagLayout());
        penShape.setSelectedIndex(Logo.config.getPenShape());
        if (Logo.config.getEraseImage())
            clearScreen.setSelected(true);
        if (Logo.config.getClearVariables())
            clearVariables.setSelected(true);
        penSize.setText(String.valueOf(Logo.config.getMaxPenWidth()));
        maxTurtles.setText(String.valueOf(Logo.config.getMaxTurtles()));
        imageQuality.setSelectedIndex(Logo.config.getDrawQuality());

        var imageSizePanel = new JPanel();
        imageSizePanel.add(imageWidth);
        JLabel xLabel = new JLabel("x");
        imageSizePanel.add(xLabel);
        imageSizePanel.add(imageHeight);

        var standard = new Insets(1, 5, 1, 5);
        add(gridPanel, new GridBagConstraints(0, 0, 4, 1,
                1.0, 1.0, GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL,
                standard, 0, 0));
        add(axisPanel, new GridBagConstraints(0, 1, 4, 1,
                1.0, 1.0, GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL,
                standard, 0, 0));

        JLabel screenColorLabel = new JLabel(Logo.messages.getString("screencolor"));
        add(screenColorLabel, new GridBagConstraints(0, 2, 1, 1, 1.0,
                1.0, GridBagConstraints.LINE_START, GridBagConstraints.NONE,
                standard, 0, 0));
        add(screenColorPanel, new GridBagConstraints(1, 2, 1, 1, 1.0,
                0.5, GridBagConstraints.LINE_START, GridBagConstraints.NONE,
                standard, 0, 0));

        JLabel penColorLabel = new JLabel(Logo.messages.getString("pencolor"));
        add(penColorLabel, new GridBagConstraints(2, 2, 1, 1, 1.0,
                1.0, GridBagConstraints.LINE_START, GridBagConstraints.NONE,
                standard, 0, 0));
        add(penColorPanel, new GridBagConstraints(3, 2, 1, 1, 1.0,
                1.0, GridBagConstraints.LINE_START, GridBagConstraints.NONE,
                standard, 0, 0));

        JLabel penSizeLabel = new JLabel(Logo.messages.getString("epaisseur_crayon"));
        add(penSizeLabel, new GridBagConstraints(0, 3, 1,
                1, 1.0, 1.0, GridBagConstraints.LINE_START,
                GridBagConstraints.NONE, standard, 0, 0));
        add(penSize, new GridBagConstraints(1, 3, 1, 1, 1.0,
                1.0, GridBagConstraints.LINE_START, GridBagConstraints.NONE,
                standard, 0, 0));

        JLabel penShapeLabel = new JLabel(Logo.messages.getString("forme_crayon"));
        add(penShapeLabel, new GridBagConstraints(2, 3, 1, 1,
                1.0, 1.0, GridBagConstraints.LINE_START, GridBagConstraints.NONE,
                standard, 0, 0));
        add(penShape, new GridBagConstraints(3, 3, 1, 1, 1.0, 1.0,
                GridBagConstraints.LINE_START, GridBagConstraints.NONE,
                standard, 0, 0));

        JLabel imageSizeLabel = new JLabel(Logo.messages.getString("taille_dessin"));
        add(imageSizeLabel, new GridBagConstraints(0, 4, 1,
                1, 1.0, 1.0, GridBagConstraints.LINE_START,
                GridBagConstraints.NONE, standard, 0, 0));
        add(imageSizePanel, new GridBagConstraints(1, 4, 1, 1,
                1.0, 1.0, GridBagConstraints.LINE_START,
                GridBagConstraints.NONE, standard, 0, 0));

        JLabel imageQualityLabel = new JLabel(Logo.messages.getString("qualite_dessin"));
        add(imageQualityLabel, new GridBagConstraints(2, 4, 1,
                1, 1.0, 1.0, GridBagConstraints.LINE_START,
                GridBagConstraints.NONE, standard, 0, 0));
        add(imageQuality, new GridBagConstraints(3, 4, 1, 1,
                1.0, 1.0, GridBagConstraints.LINE_START,
                GridBagConstraints.NONE, standard, 0, 0));

        JLabel maxTurtlesLabel = new JLabel(Logo.messages.getString("nb_tortues"));
        add(maxTurtlesLabel, new GridBagConstraints(0, 5, 1, 1, 1.0,
                1.0, GridBagConstraints.LINE_START, GridBagConstraints.NONE,
                standard, 0, 0));
        add(maxTurtles, new GridBagConstraints(1, 5, 1, 1, 1.0,
                1.0, GridBagConstraints.LINE_START, GridBagConstraints.NONE,
                standard, 0, 0));

        JLabel memoryLabel = new JLabel(Logo.messages.getString("memoire"));
        add(memoryLabel, new GridBagConstraints(0, 6, 1,
                1, 1.0, 1.0, GridBagConstraints.LINE_START,
                GridBagConstraints.NONE, standard, 0, 0));
        add(memoryLimit, new GridBagConstraints(1, 6, 1, 1,
                1.0, 1.0, GridBagConstraints.LINE_START,
                GridBagConstraints.NONE, standard, 0, 0));

        JLabel tcpPortLabel = new JLabel(Logo.messages.getString("pref.options.tcp"));
        add(tcpPortLabel, new GridBagConstraints(2, 6, 1,
                1, 1.0, 1.0, GridBagConstraints.LINE_START,
                GridBagConstraints.NONE, standard, 0, 0));
        add(tcpPort, new GridBagConstraints(3, 6, 1, 1,
                1.0, 1.0, GridBagConstraints.LINE_START,
                GridBagConstraints.NONE, standard, 0, 0));

        add(clearScreen, new GridBagConstraints(0, 7, 2, 1,
                1.0, 1.0, GridBagConstraints.LINE_START, GridBagConstraints.NONE,
                standard, 0, 0));
        add(clearVariables, new GridBagConstraints(2, 7, 2, 1,
                1.0, 1.0, GridBagConstraints.LINE_START, GridBagConstraints.NONE,
                standard, 0, 0));
    }

    protected void update() {
        try {
            int p = Integer.parseInt(tcpPort.getText());
            if (p <= 0) p = 1948;
            Logo.config.setTcpPort(p);
        } catch (NumberFormatException e) {
            Logo.config.setTcpPort(1948);
        }
        Logo.config.setPenColor(penColorPanel.getValue());
        Logo.config.setScreenColor(screenColorPanel.getValue());
        Logo.config.setEraseImage(clearScreen.isSelected());
        Logo.config.setClearVariables(clearVariables.isSelected());
        try {
            int i = Integer.parseInt(maxTurtles.getText());
            app.getKernel().setNumberOfTurtles(i);
        } catch (NumberFormatException ignored) {
        }
        try {
            int i = Integer.parseInt(penSize.getText());
            Logo.config.setMaxPenWidth(i);
            if (app.getKernel().getActiveTurtle().getPenWidth() * 2 > i) {
                app.getKernel().getActiveTurtle().fixPenWidth(i);
            }
        } catch (NumberFormatException ignored) {
        }
        Logo.config.setPenShape(penShape.getSelectedIndex());
        Logo.config.setDrawQuality(imageQuality.getSelectedIndex());
        app.getKernel().setDrawingQuality(Logo.config.getDrawQuality());
        try {
            boolean changement = false;
            int dim = Integer.parseInt(imageHeight.getText());
            if (dim != Logo.config.getImageHeight()) changement = true;
            Logo.config.setImageHeight(dim);
            dim = Integer.parseInt(imageWidth.getText());
            if (dim != Logo.config.getImageWidth()) changement = true;
            Logo.config.setImageWidth(dim);
            if (Logo.config.getImageWidth() < 100 || Logo.config.getImageHeight() < 100) {
                Logo.config.setImageWidth(400);
                Logo.config.setImageHeight(400);
            }
            if (changement) {
                    app.resizeDrawingZone();
            }
        } catch (NumberFormatException e1) {
            Logo.config.setImageWidth(400);
            Logo.config.setImageHeight(400);
        }
        Logo.config.setMemoryLimit(Integer.parseInt(memoryLimit.getText()));
        if (Logo.config.getMemoryLimit() < 256) Logo.config.setMemoryLimit(256);
        // Draw the grid and axis
        boolean refresh = false;
        boolean b = gridPanel.gridVisible();
        if (b) {
            if (!Logo.config.isGridEnabled()) {
                refresh = true;
            } else {
                if (Logo.config.getXGridSpacing() != gridPanel.getXGrid()
                        || Logo.config.getYGridSpacing() != gridPanel.getYGrid()
                        || Logo.config.getGridColor() != gridPanel.getGridColor())
                    refresh = true;
            }
        } else if (Logo.config.isGridEnabled()) {
            refresh = true;
        }
        b = axisPanel.xAxisVisible();
        if (b) {
            if (!Logo.config.isXAxisEnabled()) refresh = true;
            else {
                if (Logo.config.getXAxisSpacing() != axisPanel.getXAxis() ||
                        Logo.config.getAxisColor() != axisPanel.getAxisColor())
                    refresh = true;
            }
        } else if (Logo.config.isXAxisEnabled()) refresh = true;
        b = axisPanel.yAxisVisible();
        if (b) {
            if (!Logo.config.isYAxisEnabled()) refresh = true;
            else {
                if (Logo.config.getYAxisSpacing() != axisPanel.getYAxis() ||
                        Logo.config.getAxisColor() != axisPanel.getAxisColor())
                    refresh = true;
            }
        } else if (Logo.config.isYAxisEnabled()) refresh = true;
        if (refresh) refreshGridAxis();
    }

    private void refreshGridAxis() {
        boolean b = gridPanel.gridVisible();
        Logo.config.setGridEnabled(b);
        Logo.config.setXGridSpacing(gridPanel.getXGrid());
        Logo.config.setYGridSpacing(gridPanel.getYGrid());
        Logo.config.setGridColor(gridPanel.getGridColor());
        b = axisPanel.xAxisVisible();
        Logo.config.setXAxisEnabled(b);
        Logo.config.setXAxisSpacing(axisPanel.getXAxis());
        b = axisPanel.yAxisVisible();
        Logo.config.setYAxisEnabled(b);
        Logo.config.setYAxisSpacing(axisPanel.getYAxis());
        Logo.config.setAxisColor(axisPanel.getAxisColor());
        app.getKernel().vide_ecran();
    }
}
