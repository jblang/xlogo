package xlogo.kernel.gui;

import xlogo.Logo;
import xlogo.gui.GraphFrame;
import xlogo.kernel.Interpreter;
import xlogo.utils.Utils;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class GuiButton extends GuiComponent {
    private StringBuffer action;
    private final GraphFrame graphFrame;

    public GuiButton(String id, String text, GraphFrame graphFrame) {
        super.setId(id);
        guiObject = new JButton(Utils.unescapeString(text));
        this.graphFrame = graphFrame;
        java.awt.FontMetrics fm = graphFrame.getGraphics()
                .getFontMetrics(Logo.config.getFont());
        originalWidth = fm.stringWidth(((JButton) (getGuiObject())).getText()) + 50;
        originalHeight = Logo.config.getFont().getSize() + 10;
        setSize(originalWidth, originalHeight);
    }

    public void actionPerformed(ActionEvent e) {
        if (!graphFrame.editor.isCommandEditable()) {
            Interpreter.actionInstruction.append(action);
        } else {
            graphFrame.editor.startAnimation(action);
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
