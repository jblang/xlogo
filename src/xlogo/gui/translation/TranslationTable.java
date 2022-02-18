package xlogo.gui.translation;

import xlogo.Logo;
import xlogo.gui.Searchable;

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.*;

public class TranslationTable extends JPanel implements Searchable {
    private static final long serialVersionUID = 1L;
    int selectedRow = 0;
    int selectedColumn = 0;
    private JTable table;
    private JScrollPane scrollPane;
    private final UiTranslator tx;
    private final String id;
    private final String action;
    private final String bundle;
    private Vector<String> keys;

    protected TranslationTable(UiTranslator tx, String action, String id, String bundle) {
        this.tx = tx;
        this.action = action;
        this.id = id;
        this.bundle = bundle;
        initGui();
    }

    public void setColumnSize() {
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(200);
        }
    }

    protected String getValue(int a, int b) {
        return table.getValueAt(a, b).toString();
    }

    private void initGui() {
        setLayout(new java.awt.BorderLayout());
        table = new JTable(new MyModel(bundle, action, id));

        MultiLineCellEditor editor = new MultiLineCellEditor(table);
        table.setDefaultEditor(String.class, editor);

        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        scrollPane = new JScrollPane(table);
        //table.setFillsViewportHeight(true);
        // Only one selection is possible
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // You can only select Cell
        table.setCellSelectionEnabled(true);
        setColumnSize();
        add(scrollPane, java.awt.BorderLayout.CENTER);

    }

    protected Vector<String> getKeys() {
        return keys;
    }

    public boolean find(String element, boolean forward) {
        int row = table.getSelectedRow();
        int col = table.getSelectedColumn();
        if (row != -1) {
            if (row == table.getRowCount() - 1) {
                selectedRow = 0;
                if (col == table.getColumnCount() - 1) selectedColumn = 0;
                else selectedColumn = col + 1;
            } else selectedRow = row + 1;
        }
        int start = selectedRow;
        for (int c = selectedColumn; c < table.getColumnCount(); c++) {
            for (int r = start; r < table.getRowCount(); r++) {
                String value = table.getValueAt(r, c).toString();
                int index = value.indexOf(element);
                if (index != -1) {
                    table.clearSelection();
                    if (r == table.getRowCount() - 1) {
                        selectedRow = 0;
                        if (c == table.getColumnCount() - 1) selectedColumn = 0;
                        else selectedColumn = c + 1;
                    } else {
                        selectedRow = r + 1;
                        selectedColumn = c;
                    }
                    boolean b = table.editCellAt(r, c);
                    if (!b) {
                        table.changeSelection(r, c, false, false);
                    }
//					 System.out.println(selectedRow+" "+selectedColumn);
                    return true;
                }
            }
            start = 0;
        }
        selectedColumn = 0;
        selectedRow = 0;
        table.changeSelection(0, 0, false, false);
        return false;
    }

    public void replace(String element, boolean forward) {

    }

    public void replaceAll(String element, String substitute) {

    }

    public void removeHighlight() {
        tx.resetSearchFrame();
    }

    class MultiLineCellEditor extends AbstractCellEditor implements TableCellEditor {
        private static final long serialVersionUID = 1L;
        MyTextArea textArea;
        JTable table;

        public MultiLineCellEditor(JTable ta) {
            super();
            table = ta;
            // this component relies on having this renderer for the String class
            MultiLineCellRenderer renderer = new MultiLineCellRenderer();
            table.setDefaultRenderer(String.class, renderer);
//	                JOptionPane.showMessageDialog(null,"jui ds l'editor","h",JOptionPane.INFORMATION_MESSAGE);
            textArea = new MyTextArea();
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            for (int i = 0; i < table.getRowCount(); i++) updateRow(i);
        }

        /**
         * This method determines the height in pixel of a cell given the text it contains
         */
        private int cellHeight(int row, int col) {
            if (row == table.getEditingRow() && col == table.getEditingColumn())
                return textArea.getPreferredSize().height;
            else
                return table.getDefaultRenderer(String.class).getTableCellRendererComponent(table,
                        table.getModel().getValueAt(row, col), false, false, row, col).getPreferredSize().height;
        }

        void cellGrewEvent(int row, int column) {
            updateRow(row);
        }

        void cellShrankEvent(int row, int column) {
            updateRow(row);
        }

        void updateRow(int row) {
            int maxHeight = 0;
            for (int j = 0; j < table.getColumnCount(); j++) {
                int ch;
                if ((ch = cellHeight(row, j)) > maxHeight) {
                    maxHeight = ch;
                }
            }
            table.setRowHeight(row, maxHeight);
        }

        public Object getCellEditorValue() {
            return textArea.getText();
        }

        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected,
                                                     int row, int column) {
            textArea.setText(table.getValueAt(row, column).toString());
            textArea.rowEditing = row;
            textArea.columnEditing = column;
            textArea.lastPreferredHeight = textArea.getPreferredSize().height;

            return textArea;

        }

        class MyTextArea extends JTextArea implements KeyListener {
            private static final long serialVersionUID = 1L;
            int lastPreferredHeight = 0;
            int rowEditing;
            int columnEditing;

            MyTextArea() {
                addKeyListener(this);
                // This is a fix to Bug Id 4256006
                addAncestorListener(new AncestorListener() {
                    public void ancestorAdded(AncestorEvent e) {
                        requestFocus();
                    }

                    public void ancestorMoved(AncestorEvent e) {
                    }

                    public void ancestorRemoved(AncestorEvent e) {
                    }
                });
            }

            public void keyPressed(KeyEvent e) {
            }

            public void keyReleased(KeyEvent e) {
            }

            public void keyTyped(KeyEvent e) {
                if (getPreferredSize().getHeight() > lastPreferredHeight) {
                    lastPreferredHeight = getPreferredSize().height;
                    cellGrewEvent(rowEditing, columnEditing);
                    // this will trigger the addition of extra lines upon the cell growing and
                    // prevent all the text being lost when the cell grows to the point of requiring
                    // scrollbars
                    table.setValueAt(getText(), rowEditing, columnEditing);
                } else if (getPreferredSize().getHeight() < lastPreferredHeight) {
                    lastPreferredHeight = getPreferredSize().height;
                    cellShrankEvent(rowEditing, columnEditing);
                } else if (table.getValueAt(rowEditing, columnEditing).equals(""))
                    table.setValueAt(getText(), rowEditing, columnEditing);

            }
        }
    }

    class MyModel extends AbstractTableModel {
        private static final long serialVersionUID = 1L;
        String action;
        String id;
        private final String[] columnNames;
        private String[][] rowData;

        MyModel(String bundle, String action, String id) {
            this.action = action;
            this.id = id;

            if (action.equals(UiTranslator.CREATE)) {
                // Initilaize all Column Names
                String[] tmp = Logo.translationLanguage;
                columnNames = new String[tmp.length + 1];
                columnNames[0] = id;
                for (int i = 1; i < columnNames.length; i++) {
                    columnNames[i] = tmp[i - 1];
                }
            } else columnNames = Logo.translationLanguage;
            buildRowData(bundle, action, id);
        }

        public String getColumnName(int col) {
            return columnNames[col];
        }

        public int getRowCount() {
            return rowData.length;
        }

        public int getColumnCount() {
            return columnNames.length;
        }

        public Object getValueAt(int row, int col) {
            return rowData[row][col];
        }

        public boolean isCellEditable(int row, int col) {
            if (action.equals(UiTranslator.CONSULT)) return false;
            else if (action.equals(UiTranslator.CREATE)) {
                return col == 0;
            } else if (action.equals(UiTranslator.MODIFY)) {
                return col == Integer.parseInt(id);
            } else if (action.equals(UiTranslator.COMPLETE)) {
                if (col == Integer.parseInt(id)) {

                    return rowData[row][col].equals("");
                } else return false;
            }
            return true;
        }

        public void setValueAt(Object value, int row, int col) {
            rowData[row][col] = value.toString();
            fireTableCellUpdated(row, col);
        }

        public Class<? extends Object> getColumnClass(int c) {
            return getValueAt(0, c).getClass();

        }

        private void buildRowData(String bundle, String action, String id) {
            ResourceBundle[] rb = new ResourceBundle[getColumnCount()];
            // initialize all ResourceBundle
            for (int i = 0; i < getColumnCount(); i++) {
                Locale locale = Logo.getLocale(i);
                // In CREATE Mode, when i=getColumnCount(), the last locale is null
                if (null == locale) break;
                rb[i] = ResourceBundle.getBundle(bundle, locale);
            }
            keys = new Vector<String>();
            Enumeration<String> en = rb[0].getKeys();
            while (en.hasMoreElements()) {
                String value = en.nextElement();
                keys.add(value);
            }
            Collections.sort(keys);
            rowData = new String[keys.size()][getColumnCount()];
            int row = 0;
            for (int j = 0; j < keys.size(); j++) {
                String key = keys.get(j);

                for (int i = 0; i < getColumnCount(); i++) {
                    if (action.equals(UiTranslator.CREATE)) {
                        if (i == 0) rowData[row][0] = "";
                        else rowData[row][i] = rb[i - 1].getString(key);
                    } else {
                        String element = rb[i].getString(key);
                        if (element.equals("")) {
                            rowData[row][i] = "";
                        } else rowData[row][i] = element;
                    }
                }
                row++;
            }
        }
    }

    class MultiLineCellRenderer extends JTextArea implements TableCellRenderer {
        private static final long serialVersionUID = 1L;

        public MultiLineCellRenderer() {
            setEditable(false);
            setLineWrap(true);
            setWrapStyleWord(true);
        }

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

            if (value instanceof String) {
                setText((String) value);
                // set the table's row height, if necessary
                //updateRowHeight(row,getPreferredSize().height);
            } else
                setText("");
            return this;
        }
    }

}
