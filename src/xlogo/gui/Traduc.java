package xlogo.gui;

import xlogo.Config;
import xlogo.Logo;
import xlogo.utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
/**
 * Title :        XLogo
 * Description :  XLogo is an interpreter for the Logo
 * programming language
 *
 * @author Loïc Le Coq
 */

/** Frame For translating Logo Code from a language to another
 *  * */

public class Traduc extends JFrame implements ActionListener {
    private static final long serialVersionUID = 1L;
    private final JLabel traduire_de = new JLabel(Logo.messages.getString("traduire_de") + " ");
    private final JLabel vers = new JLabel(" " + Logo.messages.getString("vers") + " ");

    private final JComboBox combo_origine = new JComboBox(Logo.translationLanguage);
    private final JComboBox combo_destination = new JComboBox(Logo.translationLanguage);
    private final JScrollPane js_source = new JScrollPane();
    private final JScrollPane js_destination = new JScrollPane();
    private final JTextArea origine = new JTextArea();
    private final JTextArea destination = new JTextArea();
    private final JPanel p_nord_origine = new JPanel();
    private final JPanel p_nord_destination = new JPanel();
    private final JPanel p_ouest = new JPanel();
    private final JPanel p_est = new JPanel();
    private final JPanel p_edition_origine = new JPanel();
    private final JPanel p_edition_destination = new JPanel();
    private final JButton traduire = new JButton(Logo.messages.getString("traduire"));
    private final ImageIcon icopier = new ImageIcon(Utils.dimensionne_image("editcopy.png", this));
    private final ImageIcon icoller = new ImageIcon(Utils.dimensionne_image("editpaste.png", this));
    private final ImageIcon icouper = new ImageIcon(Utils.dimensionne_image("editcut.png", this));
    private final JButton copier_origine = new JButton(icopier);
    private final JButton coller_origine = new JButton(icoller);
    private final JButton couper_origine = new JButton(icouper);
    private final JButton copier_destination = new JButton(icopier);
    private final JButton coller_destination = new JButton(icoller);
    private final JButton couper_destination = new JButton(icouper);

    private ResourceBundle primitives_origine = null;
    private ResourceBundle primitives_destination = null;
    private final TreeMap<String, String> tre = new TreeMap<String, String>();

    public Traduc() {
        setTitle(Logo.messages.getString("menu.tools.translate"));
        setIconImage(Toolkit.getDefaultToolkit().createImage(Utils.class.getResource("icone.png")));
        setFont(Config.police);
        traduire.setFont(Config.police);
        vers.setFont(Config.police);
        traduire_de.setFont(Config.police);
        getContentPane().setLayout(new BorderLayout());
        combo_origine.setSelectedIndex(Config.langage);
        combo_destination.setSelectedIndex(Config.langage);

        p_nord_origine.add(traduire_de);
        p_nord_origine.add(combo_origine);
        p_nord_destination.add(vers);
        p_nord_destination.add(combo_destination);

        p_edition_origine.add(copier_origine);
        p_edition_origine.add(couper_origine);
        p_edition_origine.add(coller_origine);

        p_ouest.setLayout(new BorderLayout());
        p_ouest.add(js_source, BorderLayout.CENTER);
        p_ouest.add(p_edition_origine, BorderLayout.SOUTH);
        p_ouest.add(p_nord_origine, BorderLayout.NORTH);

        getContentPane().add(p_ouest, BorderLayout.WEST);

        p_edition_destination.add(copier_destination);
        p_edition_destination.add(couper_destination);
        p_edition_destination.add(coller_destination);

        p_est.setLayout(new BorderLayout());
        p_est.add(js_destination, BorderLayout.CENTER);
        p_est.add(p_edition_destination, BorderLayout.SOUTH);
        p_est.add(p_nord_destination, BorderLayout.NORTH);

        getContentPane().add(p_est, BorderLayout.EAST);
        getContentPane().add(traduire, BorderLayout.CENTER);

        js_source.getViewport().add(origine);
        js_destination.getViewport().add(destination);
        js_source.setPreferredSize(new Dimension(300, 300));
        js_destination.setPreferredSize(new Dimension(300, 300));

        traduire.addActionListener(this);
        copier_destination.addActionListener(this);
        couper_destination.addActionListener(this);
        coller_destination.addActionListener(this);
        copier_origine.addActionListener(this);
        couper_origine.addActionListener(this);
        coller_origine.addActionListener(this);
        copier_origine.setActionCommand("copier_origine");
        couper_origine.setActionCommand("couper_origine");
        coller_origine.setActionCommand("coller_origine");
        coller_destination.setActionCommand("coller_destination");
        copier_destination.setActionCommand("copier_destination");
        couper_destination.setActionCommand("couper_destination");

        pack();
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        if (Logo.messages.getString("traduire").equals(cmd)) {
            String texte = origine.getText();
            texte = texte.replaceAll("\\t", "  ");
            primitives_origine = genere_langue(combo_origine);
            primitives_destination = genere_langue(combo_destination);
            Enumeration<String> en = primitives_origine.getKeys();
            while (en.hasMoreElements()) {
                String element = en.nextElement();
                String primitives = primitives_origine.getString(element);
                String primitives2 = primitives_destination.getString(element);
                StringTokenizer st = new StringTokenizer(primitives);
                StringTokenizer st2 = new StringTokenizer(primitives2);
                int compteur = st.countTokens();
                for (int i = 0; i < compteur; i++) {
                    while (st2.hasMoreTokens()) element = st2.nextToken();
                    tre.put(st.nextToken(), element);
                }
            }
            // ajout des mots clés pour et fin
            int id = combo_origine.getSelectedIndex();
            Locale locale = Logo.getLocale(id);
            ResourceBundle res1 = ResourceBundle.getBundle("langage", locale);
            id = combo_destination.getSelectedIndex();
            locale = Logo.getLocale(id);
            ResourceBundle res2 = ResourceBundle.getBundle("langage", locale);
            tre.put(res1.getString("pour"), res2.getString("pour"));
            tre.put(res1.getString("fin"), res2.getString("fin"));
            StringTokenizer st = new StringTokenizer(texte, " */+-\n|&()[]", true);
            String traduc = "";
            while (st.hasMoreTokens()) {
                String element = st.nextToken().toLowerCase();
                if (tre.containsKey(element)) traduc += tre.get(element);
                else traduc += element;
            }
            destination.setText(traduc);
        } else if ("copier_origine".equals(cmd)) {
            origine.copy();
        } else if ("couper_origine".equals(cmd)) {
            origine.cut();
        } else if ("coller_origine".equals(cmd)) {
            origine.paste();
        } else if ("copier_destination".equals(cmd)) {
            destination.copy();
        } else if ("couper_destination".equals(cmd)) {
            destination.cut();
        } else if ("coller_destination".equals(cmd)) {
            destination.paste();
        }
    }

    private ResourceBundle genere_langue(JComboBox jc) { // fixe la langue utilisée pour les messages
        Locale locale = null;
        int id = jc.getSelectedIndex();
        locale = Logo.getLocale(id);
        return ResourceBundle.getBundle("primitives", locale);
    }
}
