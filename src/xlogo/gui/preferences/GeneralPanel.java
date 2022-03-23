package xlogo.gui.preferences;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import xlogo.Config;
import xlogo.Logo;
import xlogo.gui.Application;

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
    private final Application app;

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

    protected GeneralPanel(Application app) {
        this.app = app;
        initGui();
    }

    private void initGui() {
        jl_langues.setCellRenderer(new Contenu());
        buttonGroup2.add(darkLaf);
        buttonGroup2.add(lightLaf);
        buttonGroup2.add(nativeLaf);

        jl_langues.setSelectedIndex(Logo.config.getLanguage());
        jSlider1.setValue(jSlider1.getMaximum() - Logo.config.getTurtleSpeed());

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
        switch (Logo.config.getLookAndFeel()) {
            case Config.LAF_LIGHT:
                lightLaf.setSelected(true);
                break;
            case Config.LAF_DARK:
                darkLaf.setSelected(true);
                break;
            case Config.LAF_NATIVE:
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

        if (indicateur != Logo.config.getLanguage() && indicateur != -1) {
            app.changeLanguage(indicateur);
        }
        // Turtle Speed
        Logo.config.setTurtleSpeed(jSlider1.getMaximum() - jSlider1.getValue());
        if (nativeLaf.isSelected()) {
            if (Logo.config.getLookAndFeel() != Config.LAF_NATIVE) {
                Logo.config.setLookAndFeel(Config.LAF_NATIVE);
                Logo.config.loadLightEditorTheme();
            }
        } else if (lightLaf.isSelected()) {
            if (Logo.config.getLookAndFeel() != Config.LAF_LIGHT) {
                Logo.config.setLookAndFeel(Config.LAF_LIGHT);
                Logo.config.loadLightEditorTheme();
            }
        } else if (darkLaf.isSelected()) {
            if (Logo.config.getLookAndFeel() != Config.LAF_DARK) {
                Logo.config.setLookAndFeel(Config.LAF_DARK);
                Logo.config.loadDarkEditorTheme();
            }
        }
        app.changeLookAndFeel();
    }

    class Contenu extends JLabel implements ListCellRenderer {
        private static final long serialVersionUID = 1L;
        private final ImageIcon[] flag;

        Contenu() {
            flag = new ImageIcon[Logo.translationLanguage.length];
            createIcon();
        }

        void createIcon() {
            for (int i = 0; i < Logo.translationLanguage.length; i++) {
                FlatSVGIcon icon = Logo.getFlag(i);
                float factor = (float) 40 / (float) icon.getIconWidth();
                icon = icon.derive(factor);
                flag[i] = icon;
//			drapeau[i]=new ImageIcon(image);
            }

        }

        public Component getListCellRendererComponent(JList list, Object value, int
                index, boolean isSelected, boolean cellHasFocus) {
            setOpaque(true);
            String s = value.toString();
            setText(s);
            setIcon(flag[index]);
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
