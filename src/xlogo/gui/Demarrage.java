package xlogo.gui;

import xlogo.Application;
import xlogo.Config;
import xlogo.Logo;
import xlogo.utils.ExtensionFichier;
import xlogo.utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

/**
 * Title :        XLogo
 * Description :  XLogo is an interpreter for the Logo
 * programming language
 *
 * @author Loïc Le Coq
 */
// Frame to define which files could be loaded when XLogo starts
// You can define a command to launch when XLogo starts.

public class Demarrage extends JDialog implements ActionListener {
    private static final long serialVersionUID = 1L;
    private final BorderLayout borderLayout1 = new BorderLayout();
    private final JList jList1 = new JList(Config.path.toArray());
    private final JScrollPane scroll = new JScrollPane(jList1);
    private final JPanel jPanel1 = new JPanel();
    private final JLabel jLabel1 = new JLabel();
    private final JTextField jTextField1 = new JTextField();
    private final BorderLayout borderLayout2 = new BorderLayout();
    private final JButton jButton2 = new JButton();
    private final JButton jButton1 = new JButton();
    private final JPanel jPanel2 = new JPanel();
    private final JButton jButton3 = new JButton();
    private final JButton jButton4 = new JButton();
    private final JPanel p_executer = new JPanel();
    private final BorderLayout borderexec = new BorderLayout();
    private final JLabel exec = new JLabel(" " + Logo.messages.getString("procedure_demarrage") + " ");
    private final JTextField proc_demarrage = new JTextField();
    private final GridBagLayout gridBagLayout1 = new GridBagLayout();

    public Demarrage(Application cadre) throws HeadlessException {
        super(cadre);
        setTitle(Logo.messages.getString("menu.tools.startup"));
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void jbInit() throws Exception {
        setFont(Config.police);
        jList1.setFont(Config.police);
        jButton1.setFont(Config.police);
        jButton2.setFont(Config.police);
        jButton3.setFont(Config.police);
        jButton4.setFont(Config.police);
        jLabel1.setFont(Config.police);
        jTextField1.setFont(Config.police);
        exec.setFont(Config.police);
        this.getContentPane().setLayout(borderLayout1);
        jList1.setOpaque(true);
        jLabel1.setText(Logo.messages.getString("chemin") + " ");
        jPanel1.setLayout(borderLayout2);

        jButton4.setText(Logo.messages.getString("pref.ok"));
        jButton2.setText(Logo.messages.getString("ajouter"));
        jButton1.setText(Logo.messages.getString("parcourir"));
        jButton3.setText(Logo.messages.getString("enlever"));
        jPanel2.setLayout(gridBagLayout1);
        this.getContentPane().add(scroll, BorderLayout.CENTER);
        this.getContentPane().add(jPanel1, BorderLayout.NORTH);
        jPanel1.add(jTextField1, BorderLayout.CENTER);
        jPanel1.add(jLabel1, BorderLayout.WEST);
        this.getContentPane().add(jPanel2, BorderLayout.EAST);
        jPanel2.add(jButton4, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
                , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        jPanel2.add(jButton3, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
                , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        jPanel2.add(jButton2, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
                , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        jPanel2.add(jButton1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
                , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        p_executer.setLayout(borderexec);
        p_executer.add(proc_demarrage, BorderLayout.CENTER);
        p_executer.add(exec, BorderLayout.WEST);
        this.getContentPane().add(p_executer, BorderLayout.SOUTH);
        jButton1.addActionListener(this);
        jButton1.setActionCommand(Logo.messages.getString("parcourir"));
        jButton2.addActionListener(this);
        jButton2.setActionCommand(Logo.messages.getString("ajouter"));
        jButton3.addActionListener(this);
        jButton3.setActionCommand(Logo.messages.getString("enlever"));
        jButton4.addActionListener(this);
        jButton4.setActionCommand(Logo.messages.getString("pref.ok"));
        jTextField1.addActionListener(this);
        if (!Config.a_executer.equals("")) proc_demarrage.setText(Config.a_executer);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    }

    protected void processWindowEvent(WindowEvent e) {
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            this.toFront();
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals(Logo.messages.getString("parcourir"))) {
            JFileChooser jf = new JFileChooser();
            String[] ext = {".lgo"};
            jf.addChoosableFileFilter(new ExtensionFichier(Logo.messages.getString("fichiers_logo"), ext));
            Utils.recursivelySetFonts(jf, Config.police);
            int retval = jf.showDialog(this, Logo.messages.getString("menu.file.open"));
            if (retval == JFileChooser.APPROVE_OPTION) {
                jTextField1.setText(jf.getSelectedFile().getAbsolutePath());
            }
        } else if (e.getActionCommand().equals(Logo.messages.getString("enlever"))) {    //Enlever
            if (!Config.path.isEmpty()) {
                int index = Config.path.indexOf(jList1.getSelectedValue());
                if (index != -1) {
                    Config.path.remove(index);
                    jList1.setListData(Config.path.toArray());
                }
            }
        }
        if (e.getActionCommand().equals(Logo.messages.getString("pref.ok"))) {
            String texte = proc_demarrage.getText().trim();
            if (texte.equals("")) Config.a_executer = "";
            else Config.a_executer = texte;
            dispose();
        } else {  // Si on valide ou si on appuie sur le bouton ajouter

            String text = jTextField1.getText().trim();
            if (Config.path.indexOf(text) == -1 && !text.equals("")) Config.path.add(text);
            jList1.setListData(Config.path.toArray());
            jTextField1.setText("");
        }
    }
}