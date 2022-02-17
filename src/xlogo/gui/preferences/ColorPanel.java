package xlogo.gui.preferences;

import java.awt.*;
import java.awt.event.ActionEvent;

public class ColorPanel extends AbstractColorPanel {

    private static final long serialVersionUID = 1L;

    public ColorPanel(Color c) {
        super(c);
    }

    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        if (cmd.equals("bouton")) {
            actionButton();
        }
    }
}
