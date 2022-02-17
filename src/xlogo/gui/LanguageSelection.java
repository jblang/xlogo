package xlogo.gui;

import xlogo.Config;
import xlogo.Logo;
import xlogo.utils.Utils;

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

/* The first time the user opens XLogo, it shows a frame
 * who allows the user to choose a language
 *
 * */

public class LanguageSelection extends JFrame implements ActionListener {
    private static final long serialVersionUID = 1L;


    private final int NUMBER_OF_LANGUAGE = Logo.englishLanguage.length;
    private boolean selection_faite = false;
    private final JPanel central = new JPanel();
    private JScrollPane js = null;
    private ImageIcon ic;
    private final JToggleButton[] boutons = new JToggleButton[NUMBER_OF_LANGUAGE];
    private final JButton bouton_ok = new JButton("OK");
    private final ButtonGroup groupe = new ButtonGroup();
    private final GridBagLayout gb = new GridBagLayout();

    public LanguageSelection() {
        super.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setIconImage(Toolkit.getDefaultToolkit().createImage(Utils.class.getResource("icone.png")));
        setTitle("XLOGO");
        central.setLayout(gb);
        for (int i = 0; i < boutons.length; i++) {
            ic = new ImageIcon(Utils.dimensionne_image("drapeau" + i + ".png", this));
            boutons[i] = new JToggleButton(ic);
            boutons[i].setToolTipText(Logo.englishLanguage[i]);
            groupe.add(boutons[i]);
            central.add(boutons[i], new GridBagConstraints(i % 3, i / 3, 1, 1, 1.0, 1.0
                    , GridBagConstraints.CENTER, GridBagConstraints.VERTICAL, new Insets(10, 10, 0, 10), 0, 0));
        }
        boutons[0].setSelected(true);

        bouton_ok.setPreferredSize(new Dimension(100, 50));
        central.add(bouton_ok, new GridBagConstraints(NUMBER_OF_LANGUAGE % 3, NUMBER_OF_LANGUAGE / 3, 1, 1, 1.0, 1.0
                , GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 10, 0, 10), 0, 0));
        js = new JScrollPane(central);
        js.setPreferredSize(new Dimension(800, 500));
        getContentPane().add(js);
        bouton_ok.addActionListener(this);
        pack();
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        for (int i = 0; i < boutons.length; i++) {
            if (boutons[i].isSelected()) Config.langage = i;
        }
        selection_faite = true;
    }

    public boolean getSelection_faite() {
        return selection_faite;
    }
}