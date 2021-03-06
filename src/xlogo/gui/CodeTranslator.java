package xlogo.gui;

import xlogo.Language;
import xlogo.Logo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
/**
 * Title :        XLogo
 * Description :  XLogo is an interpreter for the Logo
 * programming language
 *
 * @author Loïc Le Coq
 */

/**
 * Frame For translating Logo Code from a language to another
 * *
 */

public class CodeTranslator extends JFrame implements ActionListener {
    private static final long serialVersionUID = 1L;

    private final JComboBox sourceLanguageCombo = new JComboBox(Logo.getTranslatedLanguages());
    private final JComboBox targetLanguageCombo = new JComboBox(Logo.getTranslatedLanguages());
    private final JTextArea sourceTextArea = new JTextArea();
    private final JTextArea targetTextArea = new JTextArea();

    private final TreeMap<String, String> tre = new TreeMap<>();

    public CodeTranslator() {
        setTitle(Logo.getString("menu.tools.codeTranslator"));
        setIconImage(Logo.getAppIcon().getImage());
        getContentPane().setLayout(new BorderLayout());
        sourceLanguageCombo.setSelectedIndex(Logo.config.getLanguage().ordinal());
        targetLanguageCombo.setSelectedIndex(Logo.config.getLanguage().ordinal());

        JPanel sourceLanguagePanel = new JPanel();
        JLabel sourceLanguageLabel = new JLabel(Logo.getString("codeTranslator.translateFrom") + " ");
        sourceLanguagePanel.add(sourceLanguageLabel);
        sourceLanguagePanel.add(sourceLanguageCombo);
        JPanel targetLanguagePanel = new JPanel();
        JLabel targetLanguageLabel = new JLabel(" " + Logo.getString("codeTranslator.translateTo") + " ");
        targetLanguagePanel.add(targetLanguageLabel);
        targetLanguagePanel.add(targetLanguageCombo);

        JButton sourceCopyButton = new JButton(Logo.getIcon("copy"));
        JPanel sourceEditPanel = new JPanel();
        sourceEditPanel.add(sourceCopyButton);
        JButton sourceCutButton = new JButton(Logo.getIcon("cut"));
        sourceEditPanel.add(sourceCutButton);
        JButton sourcePasteButton = new JButton(Logo.getIcon("paste"));
        sourceEditPanel.add(sourcePasteButton);

        JPanel sourcePanel = new JPanel();
        sourcePanel.setLayout(new BorderLayout());
        JScrollPane sourceScrollPane = new JScrollPane();
        sourcePanel.add(sourceScrollPane, BorderLayout.CENTER);
        sourcePanel.add(sourceEditPanel, BorderLayout.SOUTH);
        sourcePanel.add(sourceLanguagePanel, BorderLayout.NORTH);

        getContentPane().add(sourcePanel, BorderLayout.WEST);

        JButton targetCopyButton = new JButton(Logo.getIcon("copy"));
        JPanel targetEditPanel = new JPanel();
        targetEditPanel.add(targetCopyButton);
        JButton targetCutButton = new JButton(Logo.getIcon("cut"));
        targetEditPanel.add(targetCutButton);
        JButton targetPasteButton = new JButton(Logo.getIcon("paste"));
        targetEditPanel.add(targetPasteButton);

        JPanel targetPanel = new JPanel();
        targetPanel.setLayout(new BorderLayout());
        JScrollPane targetScrollPane = new JScrollPane();
        targetPanel.add(targetScrollPane, BorderLayout.CENTER);
        targetPanel.add(targetEditPanel, BorderLayout.SOUTH);
        targetPanel.add(targetLanguagePanel, BorderLayout.NORTH);

        getContentPane().add(targetPanel, BorderLayout.EAST);
        JButton translateButton = new JButton(Logo.getString("codeTranslator.translateButton"));
        getContentPane().add(translateButton, BorderLayout.CENTER);

        sourceScrollPane.getViewport().add(sourceTextArea);
        targetScrollPane.getViewport().add(targetTextArea);
        sourceScrollPane.setPreferredSize(new Dimension(300, 300));
        targetScrollPane.setPreferredSize(new Dimension(300, 300));

        translateButton.addActionListener(this);
        targetCopyButton.addActionListener(this);
        targetCutButton.addActionListener(this);
        targetPasteButton.addActionListener(this);
        sourceCopyButton.addActionListener(this);
        sourceCutButton.addActionListener(this);
        sourcePasteButton.addActionListener(this);
        sourceCopyButton.setActionCommand("sourceCopyButton");
        sourceCutButton.setActionCommand("sourceCutButton");
        sourcePasteButton.setActionCommand("sourcePasteButton");
        targetPasteButton.setActionCommand("targetPasteButton");
        targetCopyButton.setActionCommand("targetCopyButton");
        targetCutButton.setActionCommand("targetCutButton");

        pack();
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        if (Logo.getString("codeTranslator.translateButton").equals(cmd)) {
            String texte = sourceTextArea.getText();
            texte = texte.replaceAll("\\t", "  ");
            ResourceBundle sourcePrimitives = generateLanguage(sourceLanguageCombo);
            ResourceBundle targetPrimitives = generateLanguage(targetLanguageCombo);
            Enumeration<String> en = sourcePrimitives.getKeys();
            while (en.hasMoreElements()) {
                String element = en.nextElement();
                String primitives = sourcePrimitives.getString(element);
                String primitives2 = targetPrimitives.getString(element);
                StringTokenizer st = new StringTokenizer(primitives);
                StringTokenizer st2 = new StringTokenizer(primitives2);
                int compteur = st.countTokens();
                for (int i = 0; i < compteur; i++) {
                    while (st2.hasMoreTokens()) element = st2.nextToken();
                    tre.put(st.nextToken(), element);
                }
            }
            // ajout des mots clés pour et fin
            int id = sourceLanguageCombo.getSelectedIndex();
            Locale locale = Language.byIndex(id).locale;
            ResourceBundle res1 = Logo.getLanguageBundle(locale);
            id = targetLanguageCombo.getSelectedIndex();
            locale = Language.byIndex(id).locale;
            ResourceBundle res2 = Logo.getLanguageBundle(locale);
            tre.put(res1.getString("interpreter.keyword.to"), res2.getString("interpreter.keyword.to"));
            tre.put(res1.getString("interpreter.keyword.end"), res2.getString("interpreter.keyword.end"));
            StringTokenizer st = new StringTokenizer(texte, " */+-\n|&()[]", true);
            String traduc = "";
            while (st.hasMoreTokens()) {
                String element = st.nextToken().toLowerCase();
                if (tre.containsKey(element)) traduc += tre.get(element);
                else traduc += element;
            }
            targetTextArea.setText(traduc);
        } else if ("sourceCopyButton".equals(cmd)) {
            sourceTextArea.copy();
        } else if ("sourceCutButton".equals(cmd)) {
            sourceTextArea.cut();
        } else if ("sourcePasteButton".equals(cmd)) {
            sourceTextArea.paste();
        } else if ("targetCopyButton".equals(cmd)) {
            targetTextArea.copy();
        } else if ("targetCutButton".equals(cmd)) {
            targetTextArea.cut();
        } else if ("targetPasteButton".equals(cmd)) {
            targetTextArea.paste();
        }
    }

    private ResourceBundle generateLanguage(JComboBox jc) { // fixe la langue utilisée pour les messages
        Locale locale = null;
        int id = jc.getSelectedIndex();
        locale = Language.byIndex(id).locale;
        return Logo.getPrimitiveBundle(locale);
    }
}
