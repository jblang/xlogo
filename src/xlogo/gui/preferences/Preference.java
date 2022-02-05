package xlogo.gui.preferences;

import xlogo.Application;
import xlogo.Config;
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
public class Preference extends JDialog implements ActionListener {
    private static final long serialVersionUID = 1L;
    private final Application app;
    private final JButton bouton_OK = new JButton(Logo.messages.getString("pref.ok"));
    private final JButton bouton_CANCEL = new JButton(Logo.messages.getString("pref.cancel"));
    private final JPanel panneau_bouton = new JPanel();

    private final JTabbedPane jt = new JTabbedPane();

    private final JScrollPane jsTurtles = new JScrollPane();
    private final JScrollPane jsOptions = new JScrollPane();
    private Panel_General panel_General;
    private Panel_Turtles panel_Turtles;
    private Panel_Sound panel_Sound;
    private Panel_Options panel_Options;
    private Panel_Font panel_Font;
    private Panel_Highlighter panel_Highlighter;

    public Preference(Application app) {
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
        panel_General = new Panel_General(app);
        panel_Options = new Panel_Options(app);
        panel_Sound = new Panel_Sound(app);
        panel_Font = new Panel_Font(app);
        panel_Highlighter = new Panel_Highlighter(app);

        this.setTitle("");
        this.getContentPane().setLayout(new BorderLayout());

        jt.add(panel_General, Logo.messages.getString("pref.general"));

        if (!app.getArdoise().enabled3D()) {
            panel_Turtles = new Panel_Turtles(app);
            jsTurtles.getViewport().add(panel_Turtles);
            jt.add(jsTurtles, Logo.messages.getString("pref.turtles"));
        }

        jsOptions.getViewport().add(panel_Options);
        jt.add(jsOptions, Logo.messages.getString("pref.options"));

        jt.add(panel_Sound, Logo.messages.getString("pref.sound"));
        jt.add(panel_Font, Logo.messages.getString("pref.font"));

        JScrollPane js_coloration = new JScrollPane(panel_Highlighter);
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
        app.close_Preference();
        if (e.getActionCommand().equals(Logo.messages.getString("pref.ok"))) {
            panel_General.update();
            if (null != panel_Turtles) panel_Turtles.update();
            panel_Options.update();
            panel_Sound.update();
            panel_Font.update();
            panel_Highlighter.update();
            dispose();
        } else
            dispose();
    }
}