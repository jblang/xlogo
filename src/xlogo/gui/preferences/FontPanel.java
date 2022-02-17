package xlogo.gui.preferences;

import xlogo.Application;
import xlogo.Config;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Title :        XLogo
 * Description :  XLogo is an interpreter for the Logo
 * programming language
 *
 * @author Loïc Le Coq
 */
public class FontPanel extends JPanel implements ActionListener {
    public static final Font[] fontes = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();//Toolkit.getDefaultToolkit().getFontList();
    private static final long serialVersionUID = 1L;
    private static final String chaine_apercu = "ABCDEFGHIJKLMNOPQRSTUVWXYZ\nabcdefghijklmnopqrstuvwxyz";
    private final Application cadre;
    private final JPanel panneau_taille_police = new JPanel();
    private final JButton gauche = new JButton("-");
    private final JButton droite = new JButton("+");
    private final JLabel taille_police = new JLabel();
    private final JTextArea apercu_police = new JTextArea(chaine_apercu);
    private JScrollPane js_police = null;
    private String[] noms_police = null;
    private JList jl_police = null;

    protected FontPanel(Application cadre) {
        this.cadre = cadre;
        initGui();
    }

    static public int police_id(Font font) {
        for (int i = 0; i < fontes.length; i++) {
            if (fontes[i].getFontName().equals(font.getFontName())) return i;
        }
        return 0;
    }

    private void initGui() {
        apercu_police.setEditable(false);
        setLayout(new BorderLayout());
        noms_police = new String[fontes.length];
        for (int i = 0; i < fontes.length; i++) {
            noms_police[i] = i + " " + fontes[i].getFontName();
        }
        jl_police = new JList(noms_police);
        jl_police.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jl_police.setSelectedIndex(Application.police);
        js_police = new JScrollPane(jl_police);
        gauche.setActionCommand("gauche");
        droite.setActionCommand("droite");
        gauche.addActionListener(this);
        droite.addActionListener(this);
        taille_police.setText(String.valueOf(Config.police.getSize()));
        panneau_taille_police.add(gauche);
        panneau_taille_police.add(taille_police);
        panneau_taille_police.add(droite);
        add(js_police, BorderLayout.CENTER);
        add(apercu_police, BorderLayout.SOUTH);
        add(panneau_taille_police, BorderLayout.EAST);
        MouseListener mouseListener = new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int index = jl_police.locationToIndex(e.getPoint());
                apercu_police.setFont(fontes[index].deriveFont((float) Integer.parseInt(taille_police.getText())));
                apercu_police.setText(chaine_apercu);
            }
        };
        KeyListener key = new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                int code = e.getKeyCode();
                if (code == KeyEvent.VK_UP || code == KeyEvent.VK_DOWN) {
                    int index = jl_police.getSelectedIndex();
                    apercu_police.setFont(fontes[index].deriveFont((float) Integer.parseInt(taille_police.getText())));
                    apercu_police.setText(chaine_apercu);
                }
            }
        };
        jl_police.addKeyListener(key);
        jl_police.addMouseListener(mouseListener);
    }

    protected void update() {
        int size = Integer.parseInt(taille_police
                .getText());

        Font font = FontPanel.fontes[jl_police
                .getSelectedIndex()].deriveFont((float) size);
        Application.police = jl_police.getSelectedIndex();

        // Si l'on change la police de l'interface
        if (!Config.police.equals(font)) {
            cadre.changeFont(font, size);
        }

    }

    public void actionPerformed(ActionEvent e) {
        int taille = Integer.parseInt(taille_police.getText());
        if (e.getActionCommand().equals("gauche") && taille > 8) {
            taille_police.setText(String.valueOf(taille - 1));
            apercu_police.setFont(apercu_police.getFont().deriveFont((float) taille - 1));
            apercu_police.setText(chaine_apercu);
        } else if (e.getActionCommand().equals("droite") && taille < 40) {
            taille_police.setText(String.valueOf(taille + 1));
            apercu_police.setFont(apercu_police.getFont().deriveFont((float) taille + 1));
            apercu_police.setText(chaine_apercu);
        }
//		apercu_police.setSize((new Dimension((int)this.getSize().getWidth(),taille+20));
    }
}
