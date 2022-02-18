package xlogo.gui.preferences;

import xlogo.gui.Application;
import xlogo.Logo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Title :        XLogo
 * Description :  XLogo is an interpreter for the Logo
 * programming language
 *
 * @author Loïc Le Coq
 */
public class PreferencesDialog extends JDialog implements ActionListener {
    private static final long serialVersionUID = 1L;
    private final Application app;
    private final JButton bouton_OK = new JButton(Logo.messages.getString("pref.ok"));
    private final JButton bouton_CANCEL = new JButton(Logo.messages.getString("pref.cancel"));
    private final JPanel panneau_bouton = new JPanel();

    private final JTabbedPane jt = new JTabbedPane();

    private final JScrollPane jsTurtles = new JScrollPane();
    private final JScrollPane jsOptions = new JScrollPane();
    private GeneralPanel generalPanel;
    private TurtlesPanel panel_Turtles;
    private SoundPanel soundPanel;
    private OptionsPanel panel_Options;
    private FontPanel fontPanel;
    private HighlighterPanel highlighterPanel;

    public PreferencesDialog(Application app) {
        super(app);
        this.app = app;
        try {
            initGui();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initGui() throws Exception {
        setModal(false);
        setResizable(true);
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        setLocation(100, 100);
        // Init all Panels
        generalPanel = new GeneralPanel(app);
        panel_Options = new OptionsPanel(app);
        soundPanel = new SoundPanel(app);
        fontPanel = new FontPanel(app);
        highlighterPanel = new HighlighterPanel(app);

        this.setTitle("");
        this.getContentPane().setLayout(new BorderLayout());

        jt.add(generalPanel, Logo.messages.getString("pref.general"));

        if (!app.getDrawPanel().enabled3D()) {
            panel_Turtles = new TurtlesPanel(app);
            jsTurtles.getViewport().add(panel_Turtles);
            jt.add(jsTurtles, Logo.messages.getString("pref.turtles"));
        }

        jsOptions.getViewport().add(panel_Options);
        jt.add(jsOptions, Logo.messages.getString("pref.options"));

        jt.add(soundPanel, Logo.messages.getString("pref.sound"));
        jt.add(fontPanel, Logo.messages.getString("pref.font"));

        JScrollPane js_coloration = new JScrollPane(highlighterPanel);
        jt.add(js_coloration, Logo.messages.getString("pref.highlight"));

        getContentPane().add(jt, BorderLayout.CENTER);

        panneau_bouton.add(bouton_CANCEL);
        panneau_bouton.add(bouton_OK);
        getContentPane().add(panneau_bouton, BorderLayout.SOUTH);
        bouton_OK.addActionListener(this);
        bouton_CANCEL.addActionListener(this);
        setVisible(true);
        pack();


    }

    public void actionPerformed(ActionEvent e) {
        app.closePreferences();
        if (e.getActionCommand().equals(Logo.messages.getString("pref.ok"))) {
            generalPanel.update();
            if (null != panel_Turtles) panel_Turtles.update();
            panel_Options.update();
            soundPanel.update();
            fontPanel.update();
            highlighterPanel.update();
            dispose();
        } else
            dispose();
    }
}