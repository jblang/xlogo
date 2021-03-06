package xlogo.gui;

import xlogo.Logo;
import xlogo.utils.ExtensionFilter;

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

public class StartupFileDialog extends JDialog implements ActionListener {
    private static final long serialVersionUID = 1L;
    private final BorderLayout borderLayout1 = new BorderLayout();
    private final JList jList1 = new JList(Logo.config.getStartupFiles().toArray());
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
    private final JLabel exec = new JLabel(" " + Logo.getString("startup.command") + " ");
    private final JTextField proc_demarrage = new JTextField();
    private final GridBagLayout gridBagLayout1 = new GridBagLayout();

    public StartupFileDialog(Application cadre) throws HeadlessException {
        super(cadre);
        setTitle(Logo.getString("menu.tools.startupFile"));
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void jbInit() throws Exception {
        this.getContentPane().setLayout(borderLayout1);
        jList1.setOpaque(true);
        jLabel1.setText(Logo.getString("startup.filePath") + " ");
        jPanel1.setLayout(borderLayout2);

        jButton4.setText(Logo.getString("button.ok"));
        jButton2.setText(Logo.getString("startup.addFile"));
        jButton1.setText(Logo.getString("startup.browseFiles"));
        jButton3.setText(Logo.getString("startup.removeFile"));
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
        jButton1.setActionCommand(Logo.getString("startup.browseFiles"));
        jButton2.addActionListener(this);
        jButton2.setActionCommand(Logo.getString("startup.addFile"));
        jButton3.addActionListener(this);
        jButton3.setActionCommand(Logo.getString("startup.removeFile"));
        jButton4.addActionListener(this);
        jButton4.setActionCommand(Logo.getString("button.ok"));
        jTextField1.addActionListener(this);
        if (!Logo.config.getStartupCommand().equals("")) proc_demarrage.setText(Logo.config.getStartupCommand());
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    }

    protected void processWindowEvent(WindowEvent e) {
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            this.toFront();
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals(Logo.getString("startup.browseFiles"))) {
            JFileChooser jf = new JFileChooser();
            String[] ext = {".lgo"};
            jf.addChoosableFileFilter(new ExtensionFilter(Logo.getString("application.filter.logoFiles"), ext));
            int retval = jf.showDialog(this, Logo.getString("menu.file.open"));
            if (retval == JFileChooser.APPROVE_OPTION) {
                jTextField1.setText(jf.getSelectedFile().getAbsolutePath());
            }
        } else if (e.getActionCommand().equals(Logo.getString("startup.removeFile"))) {    //Enlever
            if (!Logo.config.getStartupFiles().isEmpty()) {
                int index = Logo.config.getStartupFiles().indexOf(jList1.getSelectedValue());
                if (index != -1) {
                    Logo.config.getStartupFiles().remove(index);
                    jList1.setListData(Logo.config.getStartupFiles().toArray());
                }
            }
        }
        if (e.getActionCommand().equals(Logo.getString("button.ok"))) {
            String texte = proc_demarrage.getText().trim();
            if (texte.equals("")) Logo.config.setStartupCommand("");
            else Logo.config.setStartupCommand(texte);
            dispose();
        } else {  // Si on valide ou si on appuie sur le bouton ajouter

            String text = jTextField1.getText().trim();
            if (Logo.config.getStartupFiles().indexOf(text) == -1 && !text.equals("")) Logo.config.getStartupFiles().add(text);
            jList1.setListData(Logo.config.getStartupFiles().toArray());
            jTextField1.setText("");
        }
    }
}