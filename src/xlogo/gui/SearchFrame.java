package xlogo.gui;

import xlogo.Logo;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.text.Highlighter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

public class SearchFrame extends JDialog implements ActionListener {
    private static final long serialVersionUID = 1L;
    private final String FIND = "search.find";
    Highlighter.HighlightPainter cyanPainter;
    private JButton find;
    private JRadioButton backward, forward;
    private JPanel buttonPanel;
    private JComboBox comboFind;
    private ButtonGroup bg;
    private JLabel labelFind, labelResult;
    private final Searchable jtc;

    public SearchFrame(JFrame jf, Searchable jtc) {
        super(jf);
        this.jtc = jtc;
        initGui();
    }

    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        if (cmd.equals(FIND)) {
            find();

        }
    }

    private void find() {
        String element = comboFind.getSelectedItem().toString();
        // Add the element to the combobox
        addCombo(element, comboFind);
        boolean b = jtc.find(element, forward.isSelected());
        if (b) {
            // Found
            labelResult.setText("");
        } else {
            // Not found
            labelResult.setText(Logo.getString("search.error.notFound"));
        }
    }

    protected void processWindowEvent(WindowEvent e) {
        super.processWindowEvent(e);
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            jtc.removeHighlight();
        }
    }

    private void addCombo(String element, JComboBox combo) {
        boolean b = false;
        for (int i = 0; i < combo.getItemCount(); i++) {
            if (combo.getItemAt(i).equals(element)) {
                b = true;
                break;
            }
        }
        if (!b) {
            combo.insertItemAt(element, 0);
            int n = combo.getItemCount();
            if (n > 10) {
                combo.removeItemAt(n - 1);
            }
        }
    }

    protected void setText() {
        backward = new JRadioButton(Logo.getString("translation.search.backward"));
        forward = new JRadioButton(Logo.getString("translation.search.forward"));
        TitledBorder tb = BorderFactory.createTitledBorder(Logo.getString("search.direction"));
        buttonPanel.setBorder(tb);
        find = new JButton(Logo.getString("search.find"));
        labelFind = new JLabel(Logo.getString("search.find") + " :");
        setTitle(Logo.getString("search.replaceFind"));
    }

    private void initGui() {
        setTitle(Logo.getString("search.replaceFind"));
        // Init the RadioButton for the direction search
        backward = new JRadioButton(Logo.getString("translation.search.backward"));
        forward = new JRadioButton(Logo.getString("translation.search.forward"));
        forward.setSelected(true);
        bg = new ButtonGroup();
        bg.add(forward);
        bg.add(backward);
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.add(forward);
        buttonPanel.add(backward);
        TitledBorder tb = BorderFactory.createTitledBorder(Logo.getString("search.direction"));
        buttonPanel.setBorder(tb);

        // Init Buttons
        find = new JButton(Logo.getString("search.find"));
        find.addActionListener(this);
        find.setActionCommand(FIND);

        // Init JLabel and JCombobox
        labelFind = new JLabel(Logo.getString("search.find") + " :");
        labelResult = new JLabel();

        comboFind = new JComboBox();
        comboFind.setEditable(true);


        getContentPane().setLayout(new GridBagLayout());
        // Draw all

        getContentPane().add(labelFind, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
                10, 10, 10, 10), 0, 0));
        getContentPane().add(comboFind, new GridBagConstraints(1, 0, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(
                10, 10, 10, 10), 0, 0));
        getContentPane().add(buttonPanel, new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
                10, 10, 10, 10), 0, 0));
        getContentPane().add(labelResult, new GridBagConstraints(0, 3, 2, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
                10, 10, 10, 10), 0, 0));
        getContentPane().add(find, new GridBagConstraints(1, 2, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(
                10, 10, 10, 10), 0, 0));
    }
}
