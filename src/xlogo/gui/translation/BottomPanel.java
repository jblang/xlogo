package xlogo.gui.translation;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import xlogo.Logo;

import javax.swing.*;
import java.awt.*;

public class BottomPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private final GuiTranslator tx;
    private JTabbedPane jt;
    private TranslationTable messageTable;
    private TranslationTable primTable;
    private final String id;
    private final String action;
    private JButton searchButton;
    private final ImageIcon ichercher = new FlatSVGIcon("xlogo/icons/find.svg");

    protected BottomPanel(GuiTranslator tx, String action, String id) {
        this.tx = tx;
        this.action = action;
        this.id = id;
        initGui();
    }

    private void initGui() {
        setLayout(new BorderLayout());
        jt = new JTabbedPane();
        messageTable = new TranslationTable(tx, action, id, "langage");
        primTable = new TranslationTable(tx, action, id, "primitives");
        jt.add(primTable, Logo.messages.getString("primitives"));
        jt.add(messageTable, Logo.messages.getString("messages"));
        javax.swing.JScrollPane scroll = new javax.swing.JScrollPane(jt);

        add(scroll, BorderLayout.CENTER);
        searchButton = new JButton(ichercher);
        searchButton.setToolTipText(Logo.messages.getString("find"));
        searchButton.addActionListener(tx);
        searchButton.setActionCommand(GuiTranslator.SEARCH);
        searchButton.setSize(new java.awt.Dimension(100, 50));
        add(searchButton, BorderLayout.EAST);
    }

    protected String getPrimValue(int a, int b) {
        return primTable.getValue(a, b);
    }

    protected String getMessageValue(int a, int b) {
        String st = messageTable.getValue(a, b);
        return st;
    }

    protected TranslationTable getMessageTable() {
        return this.messageTable;
    }

    protected TranslationTable getPrimTable() {
        return this.primTable;
    }

    protected TranslationTable getVisibleTable() {
        if (jt.getSelectedIndex() == 0) return primTable;
        return messageTable;
    }
}
