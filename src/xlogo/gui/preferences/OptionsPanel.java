package xlogo.gui.preferences;

import xlogo.gui.Application;
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
    private final ColorPanel b_screencolor = new ColorPanel(Logo.config.getScreenColor());
    private final JLabel label_pencolor = new JLabel(Logo.messages.getString("pencolor"));
    private final ColorPanel b_pencolor = new ColorPanel(Logo.config.getPenColor());
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
    private final JTextField largeur = new JTextField(String.valueOf(Logo.config.getImageWidth()));
    private final JLabel labelx = new JLabel("x");
    private final JTextField hauteur = new JTextField(String.valueOf(Logo.config.getImageHeight()));
    private final JLabel label_memoire = new JLabel(Logo.messages.getString("memoire"));
    private final JTextField memoire = new JTextField(String.valueOf(Logo.config.getMemoryLimit()));
    private final JLabel label_qualite = new JLabel(Logo.messages.getString("qualite_dessin"));
    private final Object[] choix_qualite = {Logo.messages.getString("normal"), Logo.messages.getString("haut"), Logo.messages.getString("bas")};
    private final JComboBox jc_qualite = new JComboBox(choix_qualite);
    private final GridPanel gridPanel = new GridPanel();
    private final AxisPanel axisPanel = new AxisPanel();
    private final JLabel labelTcp = new JLabel(Logo.messages.getString("pref.options.tcp"));
    private final JTextField textTcp = new JTextField(String.valueOf(Logo.config.getTcpPort()));

    protected OptionsPanel(Application cadre) {
        this.cadre = cadre;

        jc.setSelectedIndex(Logo.config.getPenShape());
        if (Logo.config.isEraseImage())
            effacer_editeur.setSelected(true);
        if (Logo.config.isClearVariables())
            clearVariables.setSelected(true);
        epaisseur.setText(String.valueOf(Logo.config.getMaxPenWidth()));
        tortues.setText(String.valueOf(Logo.config.getMaxTurtles()));
        jc_qualite.setSelectedIndex(Logo.config.getDrawQuality());
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
            Logo.config.setTcpPort(p);
        } catch (NumberFormatException e) {
            Logo.config.setTcpPort(1948);
        }
        //pen color
        Logo.config.setPenColor(b_pencolor.getValue());
        // screencolor
        Logo.config.setScreenColor(b_screencolor.getValue());
        // Clear screen when we leave editor?
        Logo.config.setEraseImage(effacer_editeur.isSelected());
        Logo.config.setClearVariables(clearVariables.isSelected());
        // Number of turtles
        try {
            int i = Integer.parseInt(tortues.getText());
            cadre.getKernel().setNumberOfTurtles(i);
        } catch (NumberFormatException e2) {
        }

        // maximum pen width
        try {
            int i = Integer.parseInt(epaisseur.getText());
            Logo.config.setMaxPenWidth(i);
            if (cadre.getKernel().getActiveTurtle().getPenWidth() * 2 > i) {
                cadre.getKernel().getActiveTurtle().fixe_taille_crayon(i);
            }
        } catch (NumberFormatException e1) {
        }
        // pen shape
        Logo.config.setPenShape(jc.getSelectedIndex());
        // Quality of drawing
        Logo.config.setDrawQuality(jc_qualite.getSelectedIndex());
        cadre.getKernel().setDrawingQuality(Logo.config.getDrawQuality());
        // Si on redimensionne la zone de dessin
        // Resize drawing zone
        try {
            boolean changement = false;
            int dim = Integer.parseInt(hauteur.getText());
            if (dim != Logo.config.getImageHeight()) changement = true;
            int tmp_hauteur = Logo.config.getImageHeight();
            Logo.config.setImageHeight(dim);
            dim = Integer.parseInt(largeur.getText());
            if (dim != Logo.config.getImageWidth()) changement = true;
            int tmp_largeur = Logo.config.getImageWidth();
            Logo.config.setImageWidth(dim);
            if (Logo.config.getImageWidth() < 100 || Logo.config.getImageHeight() < 100) {
                Logo.config.setImageWidth(1000);
                Logo.config.setImageHeight(1000);
            }
            if (changement) {
                int memoire_necessaire = Logo.config.getImageWidth() * Logo.config.getImageHeight() * 4 / 1024 / 1024;
                int memoire_image = tmp_hauteur * tmp_largeur * 4 / 1024 / 1024;
                long free = Runtime.getRuntime().freeMemory() / 1024 / 1024;
                long total = Runtime.getRuntime().totalMemory() / 1024 / 1024;
//				System.out.println("memoire envisagee "+(total-free+memoire_necessaire-memoire_image));
                if (total - free + memoire_necessaire - memoire_image < Logo.config.getMemoryLimit() * 0.8) {
                    cadre.resizeDrawingZone();
                } else {
                    Logo.config.setImageWidth(tmp_largeur);
                    Logo.config.setImageHeight(tmp_hauteur);
                    largeur.setText(String.valueOf(Logo.config.getImageWidth()));
                    hauteur.setText(String.valueOf(Logo.config.getImageHeight()));
                    long conseil = 64 * ((total - free + memoire_necessaire - memoire_image) / 64) + 64;
                    if (total - free + memoire_necessaire - memoire_image > 0.8 * conseil) conseil += 64;
                    if (conseil == Logo.config.getMemoryLimit()) conseil += 64;
                    String message = Logo.messages.getString("erreur_memoire") + " " + conseil + "\n" + Logo.messages.getString("relancer");
                    MessageTextArea jt = new MessageTextArea(message);
                    JOptionPane.showMessageDialog(this, jt, Logo.messages.getString("erreur"), JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (NumberFormatException e1) {
            Logo.config.setImageWidth(1000);
            Logo.config.setImageHeight(1000);
        }
        Logo.config.setMemoryLimit(Integer.parseInt(memoire.getText()));
        if (Logo.config.getMemoryLimit() < 256) Logo.config.setMemoryLimit(256);
        // Draw the grid and axis
        boolean refresh = false;
        boolean b = gridPanel.gridVisible();
        if (b) {
            if (Logo.config.isGridEnabled() == false) {
                refresh = true;
            } else {
                if (Logo.config.getXGridSpacing() != gridPanel.getXGrid()
                        || Logo.config.getYGridSpacing() != gridPanel.getYGrid()
                        || Logo.config.getGridColor() != gridPanel.getGridColor())
                    refresh = true;
            }
        } else if (Logo.config.isGridEnabled() == true) {
            refresh = true;
        }
        b = axisPanel.xAxisVisible();
        if (b) {
            if (Logo.config.isXAxisEnabled() == false) refresh = true;
            else {
                if (Logo.config.getXAxisSpacing() != axisPanel.getXAxis() ||
                        Logo.config.getAxisColor() != axisPanel.getAxisColor())
                    refresh = true;
            }
        } else if (Logo.config.isXAxisEnabled() == true) refresh = true;
        b = axisPanel.yAxisVisible();
        if (b) {
            if (Logo.config.isYAxisEnabled() == false) refresh = true;
            else {
                if (Logo.config.getYAxisSpacing() != axisPanel.getYAxis() ||
                        Logo.config.getAxisColor() != axisPanel.getAxisColor())
                    refresh = true;
            }
        } else if (Logo.config.isYAxisEnabled() == true) refresh = true;
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
        cadre.getKernel().vide_ecran();
    }
}
