package xlogo.gui.preferences;

import xlogo.gui.Application;
import xlogo.Config;
import xlogo.Logo;
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
    private static final long serialVersionUID = 1L;
    private final Application cadre;
    private final JCheckBox effacer_editeur = new JCheckBox(Logo.messages.getString("effacer_editeur"));
    private final JCheckBox clearVariables = new JCheckBox(Logo.messages.getString("pref.options.eraseVariables"));
    private final GridBagLayout gridBagLayout4 = new GridBagLayout();

    private final JLabel label_screencolor = new JLabel(Logo.messages.getString("screencolor"));
    private final ColorPanel b_screencolor = new ColorPanel(Config.screenColor);
    private final JLabel label_pencolor = new JLabel(Logo.messages.getString("pencolor"));
    private final ColorPanel b_pencolor = new ColorPanel(Config.penColor);
    private final JLabel nb_tortues = new JLabel(Logo.messages.getString("nb_tortues"));
    private final JLabel epaisseur_crayon = new JLabel(Logo.messages
            .getString("epaisseur_crayon"));
    private final JLabel forme_crayon = new JLabel(Logo.messages.getString("forme_crayon"));
    private final Object[] carre_rond = {Logo.messages.getString("carre"),
            Logo.messages.getString("rond")};
    private final JComboBox jc = new JComboBox(carre_rond);
    private final JTextField tortues = new JTextField();
    private final JTextField epaisseur = new JTextField();
    private final JLabel dimension_dessin = new JLabel(Logo.messages
            .getString("taille_dessin"));
    private final JPanel taille_dessin = new JPanel();
    private final JTextField largeur = new JTextField(String.valueOf(Config.imageWidth));
    private final JLabel labelx = new JLabel("x");
    private final JTextField hauteur = new JTextField(String.valueOf(Config.imageHeight));
    private final JLabel label_memoire = new JLabel(Logo.messages.getString("memoire"));
    private final JTextField memoire = new JTextField(String.valueOf(Config.newMemoryLimit));
    private final JLabel label_qualite = new JLabel(Logo.messages.getString("qualite_dessin"));
    private final Object[] choix_qualite = {Logo.messages.getString("normal"), Logo.messages.getString("haut"), Logo.messages.getString("bas")};
    private final JComboBox jc_qualite = new JComboBox(choix_qualite);
    private final GridPanel gridPanel = new GridPanel();
    private final AxisPanel axisPanel = new AxisPanel();
    private final JLabel labelTcp = new JLabel(Logo.messages.getString("pref.options.tcp"));
    private final JTextField textTcp = new JTextField(String.valueOf(Config.tcpPort));

    protected OptionsPanel(Application cadre) {
        this.cadre = cadre;

        jc.setSelectedIndex(Config.penShape);
        if (Config.eraseImage)
            effacer_editeur.setSelected(true);
        if (Config.clearVariables)
            clearVariables.setSelected(true);
        epaisseur.setText(String.valueOf(Config.maxPenWidth));
        tortues.setText(String.valueOf(Config.maxTurtles));
        jc_qualite.setSelectedIndex(Config.drawQuality);
        setLayout(gridBagLayout4);
        taille_dessin.add(largeur);
        taille_dessin.add(labelx);
        taille_dessin.add(hauteur);

        add(gridPanel, new GridBagConstraints(0, 0, 2, 1,
                1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(10, 0, 0, 0), 0, 0));
        add(axisPanel, new GridBagConstraints(0, 1, 2, 1,
                1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(10, 10, 0, 0), 0, 0));
        add(label_screencolor, new GridBagConstraints(0, 2, 1, 1, 1.0,
                1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(10, 10, 0, 10), 0, 0));
        add(b_screencolor, new GridBagConstraints(1, 2, 1, 1, 1.0,
                0.5, GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(10, 10, 0, 10), 0, 0));
        add(label_pencolor, new GridBagConstraints(0, 3, 1, 1, 1.0,
                1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(10, 10, 0, 10), 0, 0));
        add(b_pencolor, new GridBagConstraints(1, 3, 1, 1, 1.0,
                1.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(10, 10, 0, 10), 0, 0));

        add(nb_tortues, new GridBagConstraints(0, 5, 1, 1, 1.0,
                1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(10, 10, 0, 10), 0, 0));
        add(tortues, new GridBagConstraints(1, 5, 1, 1, 1.0,
                1.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(10, 10, 0, 10), 0, 0));
        add(epaisseur_crayon, new GridBagConstraints(0, 6, 1,
                1, 1.0, 1.0, GridBagConstraints.CENTER,
                GridBagConstraints.BOTH, new Insets(10, 10, 0, 10), 0, 0));
        add(epaisseur, new GridBagConstraints(1, 6, 1, 1, 1.0,
                1.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(10, 10, 0, 10), 0, 0));
        add(forme_crayon, new GridBagConstraints(0, 7, 1, 1,
                1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(10, 10, 0, 10), 0, 0));
        add(jc, new GridBagConstraints(1, 7, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(10, 10, 0, 10), 0, 0));
        add(label_qualite, new GridBagConstraints(0, 8, 1,
                1, 1.0, 1.0, GridBagConstraints.CENTER,
                GridBagConstraints.BOTH, new Insets(10, 10, 0, 10), 0, 0));
        add(jc_qualite, new GridBagConstraints(1, 8, 1, 1,
                1.0, 1.0, GridBagConstraints.CENTER,
                GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
        add(effacer_editeur, new GridBagConstraints(0, 9, 2, 1,
                1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(10, 0, 0, 0), 0, 0));
        add(clearVariables, new GridBagConstraints(0, 10, 2, 1,
                1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(10, 0, 0, 0), 0, 0));
        add(dimension_dessin, new GridBagConstraints(0, 11, 1,
                1, 1.0, 1.0, GridBagConstraints.CENTER,
                GridBagConstraints.BOTH, new Insets(10, 10, 0, 10), 0, 0));
        add(taille_dessin, new GridBagConstraints(1, 11, 1, 1,
                1.0, 1.0, GridBagConstraints.CENTER,
                GridBagConstraints.HORIZONTAL, new Insets(10, 0, 0, 0), 0, 0));
        add(label_memoire, new GridBagConstraints(0, 12, 1,
                1, 1.0, 1.0, GridBagConstraints.CENTER,
                GridBagConstraints.BOTH, new Insets(10, 10, 0, 10), 0, 0));
        add(memoire, new GridBagConstraints(1, 12, 1, 1,
                1.0, 1.0, GridBagConstraints.CENTER,
                GridBagConstraints.HORIZONTAL, new Insets(10, 0, 0, 0), 0, 0));
        add(labelTcp, new GridBagConstraints(0, 13, 1,
                1, 1.0, 1.0, GridBagConstraints.CENTER,
                GridBagConstraints.BOTH, new Insets(10, 10, 0, 10), 0, 0));
        add(textTcp, new GridBagConstraints(1, 13, 1, 1,
                1.0, 1.0, GridBagConstraints.CENTER,
                GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
    }

    protected void update() {
        // TCP Port
        try {
            int p = Integer.parseInt(textTcp.getText());
            if (p <= 0) p = 1948;
            Config.tcpPort = p;
        } catch (NumberFormatException e) {
            Config.tcpPort = 1948;
        }
        //pen color
        Config.penColor = b_pencolor.getValue();
        // screencolor
        Config.screenColor = b_screencolor.getValue();
        // Clear screen when we leave editor?
        Config.eraseImage = effacer_editeur.isSelected();
        Config.clearVariables = clearVariables.isSelected();
        // Number of turtles
        try {
            int i = Integer.parseInt(tortues.getText());
            cadre.getKernel().setNumberOfTurtles(i);
        } catch (NumberFormatException e2) {
        }

        // maximum pen width
        try {
            int i = Integer.parseInt(epaisseur.getText());
            Config.maxPenWidth = i;
            if (cadre.getKernel().getActiveTurtle().getPenWidth() * 2 > i) {
                cadre.getKernel().getActiveTurtle().fixe_taille_crayon(i);
            }
        } catch (NumberFormatException e1) {
        }
        // pen shape
        Config.penShape = jc.getSelectedIndex();
        // Quality of drawing
        Config.drawQuality = jc_qualite.getSelectedIndex();
        cadre.getKernel().setDrawingQuality(Config.drawQuality);
        // Si on redimensionne la zone de dessin
        // Resize drawing zone
        try {
            boolean changement = false;
            int dim = Integer.parseInt(hauteur.getText());
            if (dim != Config.imageHeight) changement = true;
            int tmp_hauteur = Config.imageHeight;
            Config.imageHeight = dim;
            dim = Integer.parseInt(largeur.getText());
            if (dim != Config.imageWidth) changement = true;
            int tmp_largeur = Config.imageWidth;
            Config.imageWidth = dim;
            if (Config.imageWidth < 100 || Config.imageHeight < 100) {
                Config.imageWidth = 1000;
                Config.imageHeight = 1000;
            }
            if (changement) {
                int memoire_necessaire = Config.imageWidth * Config.imageHeight * 4 / 1024 / 1024;
                int memoire_image = tmp_hauteur * tmp_largeur * 4 / 1024 / 1024;
                long free = Runtime.getRuntime().freeMemory() / 1024 / 1024;
                long total = Runtime.getRuntime().totalMemory() / 1024 / 1024;
//				System.out.println("memoire envisagee "+(total-free+memoire_necessaire-memoire_image));
                if (total - free + memoire_necessaire - memoire_image < Config.memoryLimit * 0.8) {
                    cadre.resizeDrawingZone();
                } else {
                    Config.imageWidth = tmp_largeur;
                    Config.imageHeight = tmp_hauteur;
                    largeur.setText(String.valueOf(Config.imageWidth));
                    hauteur.setText(String.valueOf(Config.imageHeight));
                    long conseil = 64 * ((total - free + memoire_necessaire - memoire_image) / 64) + 64;
                    if (total - free + memoire_necessaire - memoire_image > 0.8 * conseil) conseil += 64;
                    if (conseil == Config.memoryLimit) conseil += 64;
                    String message = Logo.messages.getString("erreur_memoire") + " " + conseil + "\n" + Logo.messages.getString("relancer");
                    MessageTextArea jt = new MessageTextArea(message);
                    JOptionPane.showMessageDialog(this, jt, Logo.messages.getString("erreur"), JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (NumberFormatException e1) {
            Config.imageWidth = 1000;
            Config.imageHeight = 1000;
        }
        Config.newMemoryLimit = Integer.parseInt(memoire.getText());
        if (Config.newMemoryLimit < 64) Config.newMemoryLimit = 64;
        // Draw the grid and axis
        boolean refresh = false;
        boolean b = gridPanel.gridVisible();
        if (b) {
            if (Config.gridEnabled == false) {
                refresh = true;
            } else {
                if (Config.xGridSpacing != gridPanel.getXGrid()
                        || Config.yGridSpacing != gridPanel.getYGrid()
                        || Config.gridColor != gridPanel.getGridColor())
                    refresh = true;
            }
        } else if (Config.gridEnabled == true) {
            refresh = true;
        }
        b = axisPanel.xAxisVisible();
        if (b) {
            if (Config.xAxisEnabled == false) refresh = true;
            else {
                if (Config.xAxisSpacing != axisPanel.getXAxis() ||
                        Config.axisColor != axisPanel.getAxisColor())
                    refresh = true;
            }
        } else if (Config.xAxisEnabled == true) refresh = true;
        b = axisPanel.yAxisVisible();
        if (b) {
            if (Config.yAxisEnabled == false) refresh = true;
            else {
                if (Config.yAxisSpacing != axisPanel.getYAxis() ||
                        Config.axisColor != axisPanel.getAxisColor())
                    refresh = true;
            }
        } else if (Config.yAxisEnabled == true) refresh = true;
        if (refresh) refreshGridAxis();
    }

    private void refreshGridAxis() {
        boolean b = gridPanel.gridVisible();
        Config.gridEnabled = b;
        Config.xGridSpacing = gridPanel.getXGrid();
        Config.yGridSpacing = gridPanel.getYGrid();
        Config.gridColor = gridPanel.getGridColor();
        b = axisPanel.xAxisVisible();
        Config.xAxisEnabled = b;
        Config.xAxisSpacing = axisPanel.getXAxis();
        b = axisPanel.yAxisVisible();
        Config.yAxisEnabled = b;
        Config.yAxisSpacing = axisPanel.getYAxis();
        Config.axisColor = axisPanel.getAxisColor();
        cadre.getKernel().vide_ecran();
    }
}
