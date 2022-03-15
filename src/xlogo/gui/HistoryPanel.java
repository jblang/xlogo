package xlogo.gui;

import xlogo.Logo;
import xlogo.gui.preferences.FontPanel;
import xlogo.utils.ExtensionFilter;
import xlogo.utils.LogoException;
import xlogo.utils.Utils;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.text.rtf.RTFEditorKit;
import java.awt.*;
import java.awt.event.*;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Title :        XLogo
 * Description :  XLogo is an interpreter for the Logo
 * programming language
 *
 * @author Loïc Le Coq
 */
public class HistoryPanel extends JPanel {
    public static int printFontId = FontPanel.getFontId(Logo.config.getFont());
    private final JScrollPane scrollPane = new JScrollPane();
    private final HistoryTextPane textPane = new HistoryTextPane();
    private final StyledDocument document;
    private final BorderLayout borderLayout1 = new BorderLayout();
    private final Application app;

    private Color textColor = Color.BLUE;
    private int fontSize = 12;

    private final MutableAttributeSet normal = new SimpleAttributeSet();
    private final MutableAttributeSet error = new SimpleAttributeSet();
    private final MutableAttributeSet comment = new SimpleAttributeSet();
    private final MutableAttributeSet primitive = new SimpleAttributeSet();


    public HistoryPanel(Application app) {
        this.app = app;
        try {
            initGui();
        } catch (Exception e) {
            e.printStackTrace();
        }
        document = textPane.getStyledDocument();
    }

    public int getFontSize() {
        return fontSize;
    }

    public void clearText() {
        textPane.setText("");
    }

    public void setText(String style, String text) {
        try {
            int length = textPane.getDocument().getLength();
            if (text.length() > 32000) throw new LogoException(app, Logo.messages.getString("chaine_trop_longue"));
            if (length + text.length() < 65000) {
                try {
                    int offset = document.getLength();
                    document.insertString(offset, text, null);
                    switch (style) {
                        case "erreur":
                            document.setCharacterAttributes(offset, text.length(), error, true);
                            break;
                        case "commentaire":
                            document.setCharacterAttributes(offset, text.length(), comment, true);
                            break;
                        case "perso":
                            document.setCharacterAttributes(offset, text.length(), primitive, true);
                            break;
                    }
                    textPane.setCaretPosition(document.getLength());
                } catch (BadLocationException ignored) {
                }
            } else {
                clearText();
            }
        } catch (LogoException ignored) {
        }
    }

    private void initGui() {
        this.setLayout(borderLayout1);
        textPane.setEditable(false);
        this.add(scrollPane, BorderLayout.CENTER);
        scrollPane.getViewport().add(textPane, null);
    }


    public void updateText() {
        textPane.setMenuText();
    }

    public void changeLanguage() {
    }

    public StyledDocument getStyledDocument() {
        return textPane.getStyledDocument();
    }

    public Color getTextColor() {
        return textColor;
    }

    public void setTextColor(Color color) {
        textColor = color;
        StyleConstants.setForeground(primitive, textColor);
    }

    public void setFontSize(int size) {
        fontSize = size;
        StyleConstants.setFontSize(primitive, fontSize);
    }

    public void setFontNumber(int id) {
        StyleConstants.setFontFamily(primitive, FontPanel.fonts[id].getName());
    }

    public void setBold(boolean b) {
        StyleConstants.setBold(primitive, b);
    }

    public void setItalic(boolean b) {
        StyleConstants.setItalic(primitive, b);
    }

    public void setUnderline(boolean b) {
        StyleConstants.setUnderline(primitive, b);
    }

    public void setSuperscript(boolean b) {
        StyleConstants.setSuperscript(primitive, b);
    }

    public void setSubscript(boolean b) {
        StyleConstants.setSubscript(primitive, b);
    }

    public void setStrikeThrough(boolean b) {
        StyleConstants.setStrikeThrough(primitive, b);
    }

    public boolean isBold() {
        return StyleConstants.isBold(primitive);
    }

    public boolean isItalic() {
        return StyleConstants.isItalic(primitive);
    }

    public boolean isUnderline() {
        return StyleConstants.isUnderline(primitive);
    }

    public boolean isSuperscript() {
        return StyleConstants.isSuperscript(primitive);
    }

    public boolean isSubscript() {
        return StyleConstants.isSubscript(primitive);
    }

    public boolean isStrikethrough() {
        return StyleConstants.isStrikeThrough(primitive);
    }

    public Font getFont() {
        return FontPanel.fonts[HistoryPanel.printFontId].deriveFont(Font.BOLD, (float) fontSize);
    }

    public void setFont(Font font, int size) {

        String family = font.getName();

        StyleConstants.setFontSize(normal, size);
        StyleConstants.setFontFamily(normal, family);

        StyleConstants.setFontSize(comment, size);
        StyleConstants.setFontFamily(comment, family);

        StyleConstants.setFontSize(error, size);
        StyleConstants.setFontFamily(error, family);
    }

    class HistoryTextPane extends JTextPane implements ActionListener {
        private final JPopupMenu popupMenu = new JPopupMenu();
        private final JMenuItem copyMenuItem = new JMenuItem();
        private final JMenuItem selectAllMenuItem = new JMenuItem();
        private final JMenuItem saveMenuItem = new JMenuItem();

        HistoryTextPane() {
            popupMenu.add(copyMenuItem);
            popupMenu.add(selectAllMenuItem);
            popupMenu.add(saveMenuItem);
            selectAllMenuItem.addActionListener(this);
            copyMenuItem.addActionListener(this);
            saveMenuItem.addActionListener(this);
            setMenuText();
            MouseListener popupListener = new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (e.getButton() == 1) {
                        int i = getCaretPosition();
                        int selectionStart = findBounds(i, -1);
                        int selectionEnd = findBounds(i, 1);
                        if (selectionStart == 0) selectionStart = -1;
                        select(selectionStart + 1, selectionEnd - 2);
                        app.setCommandText(getSelectedText());
                        app.focusCommandLine();
                    }
                }

                public void mouseReleased(MouseEvent e) {
                    maybeShowPopup(e);
                    app.focusCommandLine();
                }

                public void mousePressed(MouseEvent e) {
                    maybeShowPopup(e);
                }

                private void maybeShowPopup(MouseEvent e) {
                    if (e.isPopupTrigger()) {
                        popupMenu.show(e.getComponent(), e.getX(), e.getY());
                    }
                }
            };
            addMouseListener(popupListener);
        }

        int findBounds(int i, int increment) {
            boolean cont = true;
            while (cont && i != 0) {
                select(i - 1, i);
                String t = textPane.getSelectedText();
                if (t.equals("\n")) {
                    cont = false;
                }
                i = i + increment;
            }
            return i;
        }

        void setMenuText() {
            selectAllMenuItem.setText(Logo.messages.getString("menu.edition.selectall"));
            copyMenuItem.setText(Logo.messages.getString("menu.edition.copy"));
            saveMenuItem.setText(Logo.messages.getString("menu.file.textzone.rtf"));
            selectAllMenuItem.setActionCommand(Logo.messages.getString("menu.edition.selectall"));
            copyMenuItem.setActionCommand(Logo.messages.getString("menu.edition.copy"));
            saveMenuItem.setActionCommand(Logo.messages.getString("menu.file.textzone.rtf"));
        }

        public void actionPerformed(ActionEvent e) {
            String cmd = e.getActionCommand();
            if (Logo.messages.getString("menu.edition.copy").equals(cmd)) {   //Copier
                copy();
            } else if (Logo.messages.getString("menu.edition.selectall").equals(cmd)) {   //Selectionner tout
                requestFocus();
                selectAll();
                app.focusCommandLine();
            } else if (cmd.equals(Logo.messages.getString("menu.file.textzone.rtf"))) {
                saveHistory();
            }
        }
    }

    public void saveHistory() {
        try {
            JFileChooser jf = new JFileChooser(Utils.unescapeString(Logo.config.getDefaultFolder()));
            String[] ext = {".rtf"};
            jf.addChoosableFileFilter(new ExtensionFilter(Logo.messages.getString("fichiers_rtf"), ext));
            int val = jf.showDialog(app, Logo.messages.getString("menu.file.save"));
            if (val == JFileChooser.APPROVE_OPTION) {
                String path = jf.getSelectedFile().getPath();
                if (!path.toLowerCase().endsWith(".rtf")) path += ".rtf";
                FileOutputStream myFileOutputStream = new FileOutputStream(path);
                RTFEditorKit rtfEditorKit = new RTFEditorKit();
                rtfEditorKit.write(myFileOutputStream, document, 0, document.getLength() - 1);
                myFileOutputStream.close();
            }
        } catch (IOException | BadLocationException | NullPointerException ignored) {
        }
    }
}
