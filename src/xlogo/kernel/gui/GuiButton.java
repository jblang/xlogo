package xlogo.kernel.gui;

import xlogo.Application;
import xlogo.Config;
import xlogo.kernel.Interpreter;
import xlogo.utils.Utils;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class GuiButton extends GuiComponent {
    private StringBuffer action;
    private final Application app;

    public GuiButton(String id, String text, Application app) {
        super.setId(id);
        guiObject = new JButton(Utils.SortieTexte(text));
        this.app = app;
        java.awt.FontMetrics fm = app.getGraphics()
                .getFontMetrics(Config.police);
        originalWidth = fm.stringWidth(((JButton) (getGuiObject())).getText()) + 50;
        originalHeight = Config.police.getSize() + 10;
        setSize(originalWidth, originalHeight);
    }

    public void actionPerformed(ActionEvent e) {
        if (!app.commande_isEditable()) {
            Interpreter.actionInstruction.append(action);
        } else {
            app.affichage_Start(action);
        }
    }

    public boolean isButton() {
        return true;
    }

    public boolean isMenu() {
        return false;
    }

    public void setAction(StringBuffer action) {
        this.action = action;
    }

}
