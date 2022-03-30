package xlogo.gui.translation;

import xlogo.Logo;
import xlogo.gui.LanguageListRenderer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FirstPanel extends JPanel implements ActionListener {
    private JRadioButton consultButton;
    private JRadioButton modifyButton;
    private JRadioButton completeButton;
    private JRadioButton createButton;
    private final ButtonGroup group = new ButtonGroup();
    private JComboBox<String> comboLangModify;
    private JComboBox<String> comboLangComplete;
    private final UiTranslator tx;

    protected FirstPanel(UiTranslator tx) {
        this.tx = tx;
        initGui();
    }

    protected String getAction() {
        if (modifyButton.isSelected()) return UiTranslator.MODIFY;
        else if (completeButton.isSelected()) return UiTranslator.COMPLETE;
        else if (consultButton.isSelected()) return UiTranslator.CONSULT;
        else if (createButton.isSelected()) return UiTranslator.CREATE;
        return null;
    }

    protected String getLang() {
        if (modifyButton.isSelected()) return String.valueOf(comboLangModify.getSelectedIndex());
        return String.valueOf(comboLangComplete.getSelectedIndex());
    }

    private void initGui() {

        setLayout(new GridBagLayout());

        JLabel label = new JLabel(Logo.messages.getString("translatewant"));
        createButton = new JRadioButton(Logo.messages.getString("translatecreate"));
        modifyButton = new JRadioButton(Logo.messages.getString("translatemodify"));
        completeButton = new JRadioButton(Logo.messages.getString("translatecomplete"));
        consultButton = new JRadioButton(Logo.messages.getString("translateconsult"));
        createButton.setActionCommand(UiTranslator.CREATE);
        modifyButton.setActionCommand(UiTranslator.MODIFY);
        consultButton.setActionCommand(UiTranslator.CONSULT);
        completeButton.setActionCommand(UiTranslator.COMPLETE);
        createButton.addActionListener(this);
        completeButton.addActionListener(this);
        modifyButton.addActionListener(this);
        consultButton.addActionListener(this);
        comboLangModify = new JComboBox<>(Logo.translatedLanguages);
        comboLangModify.setRenderer(new LanguageListRenderer());
        comboLangComplete = new JComboBox<>(Logo.translatedLanguages);
        comboLangComplete.setRenderer(new LanguageListRenderer());
        JButton validButton = new JButton(Logo.messages.getString("pref.ok"));
        validButton.setActionCommand(UiTranslator.OK);
        validButton.addActionListener(tx);
        setSize(new Dimension(600, 120));
        validButton.setSize(new Dimension(100, 50));

        group.add(createButton);
        group.add(modifyButton);
        group.add(completeButton);
        group.add(consultButton);

        add(label, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
                GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(
                0, 0, 0, 0), 0, 0));
        add(createButton, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0,
                GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(
                0, 0, 0, 0), 0, 0));
        add(modifyButton, new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0,
                GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(
                0, 0, 0, 0), 0, 0));
        add(comboLangModify, new GridBagConstraints(1, 2, 1, 1, 1.0, 1.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(
                0, 0, 0, 0), 0, 0));
        add(completeButton, new GridBagConstraints(0, 3, 1, 1, 1.0, 1.0,
                GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(
                0, 0, 0, 0), 0, 0));
        add(comboLangComplete, new GridBagConstraints(1, 3, 1, 1, 1.0, 1.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(
                0, 0, 0, 0), 0, 0));
        add(consultButton, new GridBagConstraints(0, 4, 1, 1, 1.0, 1.0,
                GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(
                0, 0, 0, 0), 0, 0));
        add(validButton, new GridBagConstraints(2, 5, 1, 1, 1.0, 1.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(
                0, 0, 0, 0), 0, 0));
        comboLangComplete.setVisible(false);
        comboLangModify.setVisible(false);
    }

    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        switch (cmd) {
            case UiTranslator.MODIFY:
                comboLangComplete.setVisible(false);
                comboLangModify.setVisible(true);
                break;
            case UiTranslator.CREATE:
            case UiTranslator.CONSULT:
                comboLangComplete.setVisible(false);
                comboLangModify.setVisible(false);
                break;
            case UiTranslator.COMPLETE:
                comboLangComplete.setVisible(true);
                comboLangModify.setVisible(false);
                break;
        }
    }
}
