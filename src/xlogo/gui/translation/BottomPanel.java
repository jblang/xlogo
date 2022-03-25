package xlogo.gui.translation;

import xlogo.Logo;
import xlogo.resources.ResourceLoader;

import javax.swing.*;
import java.awt.*;

public class BottomPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private final UiTranslator translator;
    private JTabbedPane tabbedPane;
    private TranslationTable messageTable;
    private TranslationTable primitiveTable;
    private final String id;
    private final String action;

    protected BottomPanel(UiTranslator translator, String action, String id) {
        this.translator = translator;
        this.action = action;
        this.id = id;
        initGui();
    }

    private void initGui() {
        setLayout(new BorderLayout());
        tabbedPane = new JTabbedPane();
        messageTable = new TranslationTable(translator, action, id, "langage");
        primitiveTable = new TranslationTable(translator, action, id, "primitives");
        tabbedPane.add(primitiveTable, Logo.messages.getString("primitives"));
        tabbedPane.add(messageTable, Logo.messages.getString("messages"));
        javax.swing.JScrollPane scroll = new javax.swing.JScrollPane(tabbedPane);

        add(scroll, BorderLayout.CENTER);
        JButton searchButton = new JButton(ResourceLoader.getIcon("search"));
        searchButton.setToolTipText(Logo.messages.getString("find"));
        searchButton.addActionListener(translator);
        searchButton.setActionCommand(UiTranslator.SEARCH);
        searchButton.setSize(new java.awt.Dimension(100, 50));
        add(searchButton, BorderLayout.EAST);
    }

    protected String getPrimValue(int a, int b) {
        return primitiveTable.getValue(a, b);
    }

    protected String getMessageValue(int a, int b) {
        String st = messageTable.getValue(a, b);
        return st;
    }

    protected TranslationTable getMessageTable() {
        return this.messageTable;
    }

    protected TranslationTable getPrimitiveTable() {
        return this.primitiveTable;
    }

    protected TranslationTable getVisibleTable() {
        if (tabbedPane.getSelectedIndex() == 0) return primitiveTable;
        return messageTable;
    }
}
