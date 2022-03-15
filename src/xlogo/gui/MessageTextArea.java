package xlogo.gui;

import javax.swing.*;

/**
 * This class creates the common yellow JTextArea for all dialog box.
 *
 * @author loic
 */
public class MessageTextArea extends JTextArea {

    private static final long serialVersionUID = 1L;

    public MessageTextArea(String message) {
        setText(message);
        setEditable(false);
    }
}
