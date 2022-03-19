package xlogo.gui.preferences;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import xlogo.Logo;
import xlogo.gui.EditorFrame;

import javax.swing.*;
import java.awt.*;

/**
 * Title :        XLogo
 * Description :  XLogo is an interpreter for the Logo
 * programming language
 *
 * @author Loïc Le Coq
 */
public class TurtlesPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    EditorFrame editor;
    private final ButtonGroup buttonGroup3 = new ButtonGroup(); //Pour les images de tortue
    private final Icon[] icon = new Icon[7]; //POur les vignettes
    private final GridBagLayout gridBagLayout2 = new GridBagLayout();

    protected TurtlesPanel(EditorFrame editor) {
        this.editor = editor;
        initGui();
    }

    private void initGui() {
        for (int i = 0; i < 7; i++) {
            icon[i] = new Icon(i);
            icon[i].setText("");
            buttonGroup3.add(icon[i]);
        }
        setLayout(gridBagLayout2);

        add(icon[5], new GridBagConstraints(2, 1, 1, 1, 1.0,
                1.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(5, 5, 5, 5), 0, 0));
        add(icon[3], new GridBagConstraints(0, 1, 1, 1, 1.0,
                1.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(5, 5, 5, 5), 0, 0));
        add(icon[4], new GridBagConstraints(1, 1, 1, 1, 1.0,
                1.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(5, 5, 5, 5), 0, 0));
        add(icon[2], new GridBagConstraints(2, 0, 1, 1, 1.0,
                1.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(5, 5, 5, 5), 0, 0));
        add(icon[1], new GridBagConstraints(1, 0, 1, 1, 1.0,
                1.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(5, 5, 5, 5), 0, 0));
        add(icon[6], new GridBagConstraints(0, 2, 1, 1, 1.0,
                1.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(5, 5, 5, 5), 0, 0));
        add(icon[0], new GridBagConstraints(0, 0, 1, 1, 1.0,
                1.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(5, 5, 5, 5), 0, 0));
    }

    protected void update() {
        int turtle = -1;
        for (int i = 0; i < 7; i++) {
            if (icon[i].num() != -1) {
                turtle = i;
                Logo.config.setActiveTurtle(i);
                break;
            }
        }
        if (turtle != -1) {
            Logo.kernel.getActiveTurtle().setShape(Logo.config.getActiveTurtle());
            Logo.kernel.change_image_tortue(turtle);
        }
    }

    class Icon extends JToggleButton {
        private static final long serialVersionUID = 1L;
        private final int number;

        Icon(int number) {
            this.number = number;
            FlatSVGIcon icon = Logo.getTurtle(number);
            if (icon != null) {
                float factor = (float) 70 / (float) icon.getIconHeight();
                icon = icon.derive(factor);
                this.setIcon(icon);
            }
            if (number == Logo.config.getActiveTurtle())
                setSelected(true);
        }

        int num() {
            if (isSelected())
                return number;
            return -1;
        }
    }
}
