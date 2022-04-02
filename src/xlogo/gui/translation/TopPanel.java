package xlogo.gui.translation;

import xlogo.Logo;

import javax.swing.*;
import java.awt.*;

public class TopPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private final UiTranslator tx;
    private JTextArea area;
    private JButton sendButton;

    protected TopPanel(UiTranslator tx) {
        this.tx = tx;
        initGui();
    }

    private void initGui() {
        setLayout(new FlowLayout());
        area = new JTextArea(Logo.getString("translatemessage"));
        area.setWrapStyleWord(true);
        area.setLineWrap(true);
        sendButton = new JButton(Logo.getString("pref.ok"));


        area.setEditable(false);
        sendButton.addActionListener(tx);
        sendButton.setActionCommand(UiTranslator.SEND);


        area.setSize(new Dimension(400, 100));
        sendButton.setSize(new Dimension(50, 30));
        add(area);
        add(sendButton);
    }
}
