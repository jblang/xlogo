package xlogo.gui.preferences;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import xlogo.Application;
import xlogo.Config;
import xlogo.Logo;
import xlogo.utils.Utils;

import javax.swing.*;
import java.awt.*;

/**
 * Title :        XLogo
 * Description :  XLogo is an interpreter for the Logo
 * programming language
 *
 * @author Loïc Le Coq
 */
public class GeneralPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private final Application cadre;

    private final JList jl_langues = new JList(Logo.translationLanguage); //Pour les différentes langues
    private final JScrollPane js_langues = new JScrollPane(jl_langues);
    private final ButtonGroup buttonGroup2 = new ButtonGroup();
    private final JRadioButton lightLaf = new JRadioButton();
    private final JRadioButton nativeLaf = new JRadioButton();
    private final JRadioButton darkLaf = new JRadioButton();
    private final JLabel Langue = new JLabel(Logo.messages.getString("pref.general.lang"));
    private final JLabel Aspect = new JLabel(Logo.messages.getString("pref.general.aspect"));
    private final GridBagLayout gridBagLayout3 = new GridBagLayout();
    private final JLabel vitesse_defilement = new JLabel(Logo.messages.getString("pref.general.speed"));
    private final JLabel lent = new JLabel(Logo.messages.getString("pref.general.slow"));
    private final JLabel rapide = new JLabel(Logo.messages.getString("pref.general.fast"));
    private final JSlider jSlider1 = new JSlider(0, 100);

    protected GeneralPanel(Application cadre) {
        this.cadre = cadre;
        initGui();
    }

    private void initGui() {
        jl_langues.setCellRenderer(new Contenu());
        buttonGroup2.add(darkLaf);
        buttonGroup2.add(lightLaf);
        buttonGroup2.add(nativeLaf);

        jl_langues.setSelectedIndex(Config.langage);
        jSlider1.setValue(jSlider1.getMaximum() - Config.turtleSpeed);

        setLayout(gridBagLayout3);
        jl_langues.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);


        darkLaf.setActionCommand(Logo.messages.getString("pref.general.darkLaf"));
        darkLaf.setHorizontalAlignment(SwingConstants.LEFT);
        darkLaf.setHorizontalTextPosition(SwingConstants.LEFT);
        darkLaf.setText(Logo.messages.getString("pref.general.darkLaf"));
        lightLaf.setActionCommand(Logo.messages.getString("pref.general.lightLaf"));
        lightLaf.setHorizontalAlignment(SwingConstants.LEFT);
        lightLaf.setHorizontalTextPosition(SwingConstants.LEFT);
        lightLaf.setText(Logo.messages.getString("pref.general.lightLaf"));
        nativeLaf.setActionCommand(Logo.messages.getString("pref.general.nativeLaf"));
        nativeLaf.setHorizontalAlignment(SwingConstants.LEFT);
        nativeLaf.setHorizontalTextPosition(SwingConstants.LEFT);
        nativeLaf.setText(Logo.messages.getString("pref.general.nativeLaf"));
        nativeLaf.setVerticalAlignment(SwingConstants.CENTER);
        switch (Config.looknfeel) {
            case Config.LOOKNFEEL_LIGHT:
                lightLaf.setSelected(true);
                break;
            case Config.LOOKNFEEL_DARK:
                darkLaf.setSelected(true);
                break;
            case Config.LOOKNFEEL_NATIVE:
                nativeLaf.setSelected(true);
                break;
        }
        Langue.setOpaque(true);
        Langue.setHorizontalAlignment(SwingConstants.CENTER);
        Langue.setHorizontalTextPosition(SwingConstants.LEFT);
        Langue.setText(Logo.messages.getString("pref.general.lang"));
        Aspect.setOpaque(true);
        Aspect.setHorizontalAlignment(SwingConstants.CENTER);
        Aspect.setHorizontalTextPosition(SwingConstants.LEFT);
        Aspect.setText(Logo.messages.getString("pref.general.aspect"));

        lent.setOpaque(true);
        lent.setToolTipText("");
        lent.setHorizontalAlignment(SwingConstants.LEFT);
        lent.setHorizontalTextPosition(SwingConstants.LEFT);
        lent.setText(Logo.messages.getString("pref.general.slow"));
        lent.setVerticalAlignment(SwingConstants.BOTTOM);
        lent.setVerticalTextPosition(SwingConstants.BOTTOM);
        rapide.setDebugGraphicsOptions(0);
        rapide.setOpaque(true);
        rapide.setHorizontalAlignment(SwingConstants.RIGHT);
        rapide.setHorizontalTextPosition(SwingConstants.RIGHT);
        rapide.setText(Logo.messages.getString("pref.general.fast"));
        rapide.setVerticalAlignment(SwingConstants.BOTTOM);
        rapide.setVerticalTextPosition(SwingConstants.BOTTOM);
        jSlider1.setMajorTickSpacing(10);
        jSlider1.setMinorTickSpacing(5);
        jSlider1.setPaintTicks(true);
        jSlider1.setPaintLabels(true);
        jSlider1.setSnapToTicks(true);

        add(Langue, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
                10, 10, 0, 10), 0, 0));
        add(js_langues, new GridBagConstraints(0, 1, 1, 3, 1.0,
                1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(10, 10, 10, 10), 0, 0));
        add(Aspect, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
                10, 10, 0, 10), 0, 0));
        add(darkLaf, new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
                0, 0, 0, 0), 0, 0));
        add(lightLaf, new GridBagConstraints(1, 2, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
                0, 0, 0, 0), 0, 0));
        add(nativeLaf, new GridBagConstraints(1, 3, 1, 1, 1.0,
                1.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(0, 0, 0, 0), 0, 0));

        add(vitesse_defilement, new GridBagConstraints(0, 4, 1,
                1, 1.0, 0.5, GridBagConstraints.EAST, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));
        add(rapide, new GridBagConstraints(1, 5, 1, 1, 1.0, 0.5,
                GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(0,
                0, 0, 0), 0, 0));
        add(lent, new GridBagConstraints(0, 5, 1, 1, 1.0, 0.5,
                GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0,
                0, 0, 0), 0, 0));
        add(jSlider1, new GridBagConstraints(0, 6, 2, 1, 1.0,
                1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 10, 0), 0, 0));


    }

    /**
     * Apply any modification, if some application parameter have been modified with this panel.
     */
    protected void update() {
        // Language has changed?
        int indicateur = jl_langues.getSelectedIndex();

        if (indicateur != Config.langage && indicateur != -1) {
            cadre.changeLanguage(indicateur);
        }
        // Turtle Speed
        Config.turtleSpeed = jSlider1.getMaximum() - jSlider1.getValue();
        try { //Look and Feel has changed?
            if (nativeLaf.isSelected()) {
                Config.looknfeel = Config.LOOKNFEEL_NATIVE;
                UIManager.setLookAndFeel(UIManager
                        .getSystemLookAndFeelClassName());
            } else if (lightLaf.isSelected()) {
                Config.looknfeel = Config.LOOKNFEEL_LIGHT;
                UIManager
                        .setLookAndFeel(new FlatLightLaf());
            } else if (darkLaf.isSelected()) {
                Config.looknfeel=Config.LOOKNFEEL_DARK;
                UIManager.setLookAndFeel(new FlatDarkLaf());
            }
            cadre.changeLookNFeel();
        } catch (Exception exc) {
            System.out.println(exc);
        }


    }

    class Contenu extends JLabel implements ListCellRenderer {
        private static final long serialVersionUID = 1L;
        private final ImageIcon[] drapeau;

        Contenu() {
            drapeau = new ImageIcon[Logo.translationLanguage.length];
            cree_icone();
        }

        void cree_icone() {
            for (int i = 0; i < Logo.translationLanguage.length; i++) {
                Image image = null;
                image = Toolkit.getDefaultToolkit().getImage(Utils.class.getResource("drapeau" + i + ".png"));
                MediaTracker tracker = new MediaTracker(this);
                tracker.addImage(image, 0);
                try {
                    tracker.waitForID(0);
                } catch (InterruptedException e1) {
                }
                int largeur = image.getWidth(this);
                int hauteur = image.getHeight(this);
                double facteur = (double) Config.police.getSize() / (double) hauteur;
                image = image.getScaledInstance((int) (facteur * largeur), (int) (facteur * hauteur), Image.SCALE_SMOOTH);
                tracker = new MediaTracker(this);
                tracker.addImage(image, 0);
                try {
                    tracker.waitForID(0);
                } catch (InterruptedException e1) {
                }
                drapeau[i] = new ImageIcon();
                drapeau[i].setImage(image);
//			drapeau[i]=new ImageIcon(image);
            }

        }

        public Component getListCellRendererComponent(JList list, Object value, int
                index, boolean isSelected, boolean cellHasFocus) {
            setOpaque(true);
            String s = value.toString();
            setText(s);
            setIcon(drapeau[index]);
            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }
            setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 5));
            return (this);
        }
    }

}
