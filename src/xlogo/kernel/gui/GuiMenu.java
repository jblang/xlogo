package xlogo.kernel.gui;

import xlogo.Logo;
import xlogo.gui.GraphFrame;
import xlogo.kernel.Interpreter;
import xlogo.utils.Utils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.StringTokenizer;

public class GuiMenu extends GuiComponent {
    private final GraphFrame graphFrame;
    private final String[] item;
    private final StringBuffer[] action;

    public GuiMenu(String id, String text, GraphFrame graphFrame) {
        this.graphFrame = graphFrame;
        setId(id);
        StringTokenizer st = new StringTokenizer(text);
        item = new String[st.countTokens()];
        action = new StringBuffer[st.countTokens()];
        int i = 0;
        originalWidth = 0;
        while (st.hasMoreTokens()) {
            item[i] = Utils.unescapeString(st.nextToken());
            java.awt.FontMetrics fm = graphFrame.getGraphics()
                    .getFontMetrics(Logo.config.getFont());
            originalWidth = Math.max(originalWidth, fm.stringWidth(item[i]));
            i++;
        }
        originalWidth += 50;
        guiObject = new JComboBox(item);
        originalHeight = Logo.config.getFont().getSize() + 10;
        setSize(originalWidth, originalHeight);
    }

    public void actionPerformed(ActionEvent e) {
//		System.out.println("coucou");
        int select = ((JComboBox) guiObject).getSelectedIndex();
        if (!graphFrame.editor.isCommandEditable()) {
            Interpreter.actionInstruction.append(action[select]);
        } else {
            graphFrame.editor.startAnimation(action[select]);
        }
    }

    public boolean isButton() {
        return false;
    }

    public boolean isMenu() {
        return true;
    }

    public void setAction(StringBuffer action, int id) {
        this.action[id] = action;
    }
}