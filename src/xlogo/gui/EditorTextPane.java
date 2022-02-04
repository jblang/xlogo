package xlogo.gui;

import xlogo.StyledDocument.DocumentLogo;

import javax.swing.text.BadLocationException;


public class EditorTextPane extends EditorTextZone {
    private final DocumentLogo dsd;

    EditorTextPane(Editor editor) {
        super(editor);
        dsd = new DocumentLogo();
        jtc = new ZoneEdition(this);
        initGui();
        initHighlight();
    }

    protected void initHighlight() {
        jtc.setDocument(dsd);
        dsd.addUndoableEditListener(new MyUndoableEditListener());
    }

    protected DocumentLogo getDsd() {
        return dsd;
    }

    protected void ecris(String mot) {
        try {
            int deb = jtc.getCaretPosition();
            dsd.insertString(deb, mot, null);
            jtc.setCaretPosition(deb + mot.length());
        } catch (BadLocationException e) {
        }
    }

    public void setActive(boolean b) {
        ((ZoneEdition) jtc).setActive(b);
    }

    protected boolean supportHighlighting() {
        return true;
    }

}
