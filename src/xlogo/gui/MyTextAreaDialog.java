package xlogo.gui;

import xlogo.Config;
import xlogo.StyledDocument.DocumentLogoHistorique;

import javax.swing.*;
import java.awt.*;

/**
 * This class creates the common yellow JTextArea for all dialog box.
 *
 * @author loic
 */
public class MyTextAreaDialog extends JTextArea {

    private static final long serialVersionUID = 1L;

    public MyTextAreaDialog(String message) {
        setText(message);
        setEditable(false);
        setBackground(new Color(255, 255, 177));
        setFont(Config.police);
    }

    public MyTextAreaDialog(String message, DocumentLogoHistorique dsd) {
        setFont(dsd.getFont());
        setText(message);
        setEditable(false);
        setBackground(new Color(255, 255, 177));
        setFont(Config.police);
    }
}
