package xlogo.gui.preferences;

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
public class PreferencesDialog extends JDialog {
    private final Application app;
    private GeneralPanel generalPanel;
    private TurtlesPanel turtlesPanel;
    private SoundPanel soundPanel;
    private OptionsPanel optionsPanel;
    private EditorPanel editorPanel;

    public PreferencesDialog(Application app) {
        super(app);
        this.app = app;
        initGui();
    }

    private void initGui() {
        setModal(false);
        setResizable(true);
        setTitle("Preferences");
        getContentPane().setLayout(new BorderLayout());

        generalPanel = new GeneralPanel(app);
        turtlesPanel = new TurtlesPanel(app);
        optionsPanel = new OptionsPanel(app);
        soundPanel = new SoundPanel(app);
        editorPanel = new EditorPanel(app);

        var turtlesPane = new JScrollPane(turtlesPanel);
        var optionsPane = new JScrollPane(optionsPanel);
        var editorPane = new JScrollPane(editorPanel);

        var tabs = new JTabbedPane();
        tabs.add(generalPanel, Logo.getString("pref.general"));
        tabs.add(turtlesPane, Logo.getString("pref.turtles"));
        tabs.add(optionsPane, Logo.getString("pref.options"));
        tabs.add(editorPane, Logo.getString("editor"));
        tabs.add(soundPanel, Logo.getString("pref.sound"));
        getContentPane().add(tabs, BorderLayout.CENTER);

        var okButton = new JButton(Logo.getString("button.ok"));
        okButton.addActionListener(e -> updateAndClose());
        var cancelButton = new JButton(Logo.getString("button.cancel"));
        cancelButton.addActionListener(e -> dispose());

        var buttonPanel = new JPanel();
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void updateAndClose() {
        editorPanel.update();
        generalPanel.update();
        turtlesPanel.update();
        optionsPanel.update();
        soundPanel.update();
        dispose();
    }
}