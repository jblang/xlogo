package xlogo.gui;

import xlogo.document.LogoDocument;

import javax.swing.text.BadLocationException;


public class EditorTextPane extends EditorTextFacade {
    private final LogoDocument dsd;

    EditorTextPane(Editor editor) {
        super(editor);
        dsd = new LogoDocument();
        jtc = new HighlightedTextPane(this);
        initGui();
        initHighlight();
    }

    protected void initHighlight() {
        jtc.setDocument(dsd);
        dsd.addUndoableEditListener(new MyUndoableEditListener());
    }

    protected LogoDocument getDsd() {
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
        ((HighlightedTextPane) jtc).setActive(b);
    }

    protected boolean supportHighlighting() {
        return true;
    }

}
