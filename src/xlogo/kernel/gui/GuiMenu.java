package xlogo.kernel.gui;

import xlogo.Application;
import xlogo.Config;
import xlogo.kernel.Interpreter;
import xlogo.utils.Utils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.StringTokenizer;

public class GuiMenu extends GuiComponent {
    private final Application app;
    private final String[] item;
    private final StringBuffer[] action;

    public GuiMenu(String id, String text, Application app) {
        this.app = app;
        setId(id);
        StringTokenizer st = new StringTokenizer(text);
        item = new String[st.countTokens()];
        action = new StringBuffer[st.countTokens()];
        int i = 0;
        originalWidth = 0;
        while (st.hasMoreTokens()) {
            item[i] = Utils.SortieTexte(st.nextToken());
            java.awt.FontMetrics fm = app.getGraphics()
                    .getFontMetrics(Config.police);
            originalWidth = Math.max(originalWidth, fm.stringWidth(item[i]));
            i++;
        }
        originalWidth += 50;
        guiObject = new JComboBox(item);
        originalHeight = Config.police.getSize() + 10;
        setSize(originalWidth, originalHeight);
    }

    public void actionPerformed(ActionEvent e) {
//		System.out.println("coucou");
        int select = ((JComboBox) guiObject).getSelectedIndex();
        if (!app.commande_isEditable()) {
            Interpreter.actionInstruction.append(action[select]);
        } else {
            app.affichage_Start(action[select]);
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