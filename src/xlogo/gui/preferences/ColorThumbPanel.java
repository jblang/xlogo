package xlogo.gui.preferences;

import java.awt.*;
import java.awt.event.ActionEvent;

public class ColorThumbPanel extends AbstractColorPanel {
    private static final long serialVersionUID = 1L;
    private final ThumbDialog tb;

    ColorThumbPanel(Color c, ThumbDialog tb) {
        super(c);
        this.tb = tb;
        combo_couleur.addActionListener(this);
        combo_couleur.setActionCommand("combo");
    }

    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        if (cmd.equals("bouton")) {
            actionButton();
            tb.updateFirstButton(getValue());
        } else if (cmd.equals("combo")) {
            tb.updateFirstButton(getValue());
        }
    }


}
