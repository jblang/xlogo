package xlogo.gui.preferences;

import xlogo.Config;
import xlogo.Language;
import xlogo.Logo;
import xlogo.gui.Application;
import xlogo.gui.LanguageListRenderer;

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
    private final Application app;

    private final JList<Language> languageList = new JList<>(Language.values());
    private final JRadioButton lightLaf = new JRadioButton();
    private final JRadioButton nativeLaf = new JRadioButton();
    private final JRadioButton darkLaf = new JRadioButton();
    private final JSlider speedSlider = new JSlider(0, 100);

    protected GeneralPanel(Application app) {
        this.app = app;
        setLayout(new GridBagLayout());
        initGui();
    }

    private void initGui() {
        languageList.setCellRenderer(new LanguageListRenderer());
        languageList.setSelectedIndex(Logo.config.getLanguage().ordinal());
        languageList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        var languageScroll = new JScrollPane(languageList);

        darkLaf.setActionCommand(Logo.getString("pref.general.darkLaf"));
        darkLaf.setHorizontalAlignment(SwingConstants.LEFT);
        darkLaf.setHorizontalTextPosition(SwingConstants.LEFT);
        darkLaf.setText(Logo.getString("pref.general.darkLaf"));
        lightLaf.setActionCommand(Logo.getString("pref.general.lightLaf"));
        lightLaf.setHorizontalAlignment(SwingConstants.LEFT);
        lightLaf.setHorizontalTextPosition(SwingConstants.LEFT);
        lightLaf.setText(Logo.getString("pref.general.lightLaf"));
        nativeLaf.setActionCommand(Logo.getString("pref.general.nativeLaf"));
        nativeLaf.setHorizontalAlignment(SwingConstants.LEFT);
        nativeLaf.setHorizontalTextPosition(SwingConstants.LEFT);
        nativeLaf.setText(Logo.getString("pref.general.nativeLaf"));
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
        var lafButtonGroup = new ButtonGroup();
        lafButtonGroup.add(darkLaf);
        lafButtonGroup.add(lightLaf);
        lafButtonGroup.add(nativeLaf);

        var languageLabel = new JLabel(Logo.getString("pref.general.lang"));
        languageLabel.setOpaque(true);
        languageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        languageLabel.setHorizontalTextPosition(SwingConstants.LEFT);

        var themeLabel = new JLabel(Logo.getString("pref.general.aspect"));
        themeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        themeLabel.setHorizontalTextPosition(SwingConstants.LEFT);

        var slowLabel = new JLabel(Logo.getString("pref.general.slow"));
        slowLabel.setHorizontalAlignment(SwingConstants.LEFT);
        slowLabel.setHorizontalTextPosition(SwingConstants.LEFT);
        slowLabel.setVerticalAlignment(SwingConstants.BOTTOM);
        slowLabel.setVerticalTextPosition(SwingConstants.BOTTOM);

        var fastLabel = new JLabel(Logo.getString("pref.general.fast"));
        fastLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        fastLabel.setHorizontalTextPosition(SwingConstants.RIGHT);
        fastLabel.setVerticalAlignment(SwingConstants.BOTTOM);
        fastLabel.setVerticalTextPosition(SwingConstants.BOTTOM);

        var turtleSpeedLabel = new JLabel(Logo.getString("pref.general.speed"));
        speedSlider.setValue(speedSlider.getMaximum() - Logo.config.getTurtleSpeed());
        speedSlider.setMajorTickSpacing(10);
        speedSlider.setMinorTickSpacing(5);
        speedSlider.setPaintTicks(true);
        speedSlider.setPaintLabels(true);
        speedSlider.setSnapToTicks(true);

        add(languageLabel, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
                10, 10, 0, 10), 0, 0));
        add(languageScroll, new GridBagConstraints(0, 1, 1, 3, 1.0,
                1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(10, 10, 10, 10), 0, 0));
        add(themeLabel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
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

        add(turtleSpeedLabel, new GridBagConstraints(0, 4, 1,
                1, 1.0, 0.5, GridBagConstraints.EAST, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));
        add(fastLabel, new GridBagConstraints(1, 5, 1, 1, 1.0, 0.5,
                GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(0,
                0, 0, 0), 0, 0));
        add(slowLabel, new GridBagConstraints(0, 5, 1, 1, 1.0, 0.5,
                GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0,
                0, 0, 0), 0, 0));
        add(speedSlider, new GridBagConstraints(0, 6, 2, 1, 1.0,
                1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 10, 0), 0, 0));


    }

    /**
     * Apply any modification, if some application parameter have been modified with this panel.
     */
    protected void update() {
        Language lang = Language.byIndex(languageList.getSelectedIndex());
        if (Logo.config.getLanguage() != lang) {
            Logo.config.setLanguage(lang);
            app.changeLanguage();
        }
        Logo.config.setTurtleSpeed(speedSlider.getMaximum() - speedSlider.getValue());
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

}
