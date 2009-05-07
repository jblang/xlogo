package xlogo.gui;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.util.Stack;
import java.util.StringTokenizer;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.JTextComponent;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;
import xlogo.Popup;
/**
 * 
 * @author loic
 * This class is the generic component to display the text in the editor
 * If there are too many characters, it is a JTextArea <br>
 * Else it's a JtextPane that allows Syntax Highlighting
 */
public abstract class EditorTextZone implements Searchable, Printable {
	private Editor editor;
	private StringBuffer text;
	protected JTextComponent jtc;
	private Popup jpop;

	// When printing the text area, this stack stores each page  	
	private Stack<String> pages=null;
	// To remember undoable edits
	private UndoManager undoManager;
	private int startOffset,endOffset;
	/**
	 * Default constructor for this generic constructor
	 * 
	 * @param editor The parent editor
	 */
	EditorTextZone(Editor editor){
		this.editor=editor;
	}
	protected void initGui(){
	    // Adds the JPopup Menu
		jpop=new Popup(editor,jtc);
		MouseListener popupListener = new PopupListener();
	    jtc.addMouseListener(popupListener);
		undoManager=new UndoManager();
	}
	/**
	 * Erase all text
	 */
	protected void clearText(){
		jtc.setText("");
	}
	
	public boolean find(String element,boolean forward){
		try{	
			int index;
			text=new StringBuffer(jtc.getText());
			// Find forward
			if (forward)
				index=text.indexOf(element, jtc.getCaretPosition());
			else index=text.lastIndexOf(element, jtc.getCaretPosition());
			if (index==-1){
				startOffset=0;
				endOffset=0;
				return false;
			}
			else {
				jtc.getHighlighter().removeAllHighlights();
				startOffset=index;
				endOffset=index+element.length();
				jtc.getHighlighter().addHighlight(startOffset, endOffset,
						DefaultHighlighter.DefaultPainter );
				if (forward) jtc.setCaretPosition(index+element.length());
				else if (index>1) jtc.setCaretPosition(index-1); 
				return true;
			}
		}
		catch(NullPointerException e){} // If the combo is empty	
		catch(BadLocationException e){}
		return false;
	}
	public void replace(String element, boolean forward){
		text.delete(startOffset, endOffset);
		try{	
			text.insert(startOffset,element );
	
		}
		catch(NullPointerException err){}
		jtc.setText(text.toString());
		if (forward) jtc.setCaretPosition(endOffset);
		else if (startOffset>1) jtc.setCaretPosition(startOffset-1);

	}
	public void replaceAll(String element, String substitute){

		try {
			String string=jtc.getText().toString();
			string=string.replaceAll(element, substitute);
			jtc.setText(string);
		}
		catch(NullPointerException e2){}

	}
	public void removeHighlight(){
		jtc.getHighlighter().removeAllHighlights();
	}

	class MyUndoableEditListener implements UndoableEditListener{
		public void undoableEditHappened(UndoableEditEvent e){
		     UndoableEdit edit = e.getEdit();
		      // Include this method to ignore syntax changes
		      if (edit instanceof AbstractDocument.DefaultDocumentEvent &&
		         ((AbstractDocument.DefaultDocumentEvent)edit).getType() == 
		         AbstractDocument.DefaultDocumentEvent.EventType.CHANGE) {
		         return;
		      }
			// Remember the edit
		     undoManager.addEdit(edit);
//		     System.out.println(e.getEdit().getPresentationName());
			editor.updateUndoRedoButtons();
	}
}
	protected UndoManager getUndoManager(){
		return undoManager;
	}
	protected String getText(){
		return jtc.getText();
	}
	protected void requestFocus(){
		jtc.requestFocus();
	}
	protected JTextComponent getTextComponent(){
		return jtc;
	}
	protected boolean supportHighlighting(){
		return false;
	}

	protected void setFont(Font f){
		jtc.setFont(f);
	}
	protected Font getFont(){
		return jtc.getFont();
	}
	// To print the text Area
	
	public int print(Graphics g,PageFormat pf, int pi) throws PrinterException{
		if(pi<pages.size()){
    		jtc.setText(pages.get(pi));
    		g.translate((int)pf.getImageableX(),(int)pf.getImageableY());
        	jtc.paint(g);
  			return(Printable.PAGE_EXISTS);
		}
		else  return Printable.NO_SUCH_PAGE;
	}
	protected void actionPrint(){
    	Font font=jtc.getFont();
        String txt=jtc.getText();
        jtc.setFont(new Font(font.getFontName(),Font.PLAIN,10));
        jtc.setText(txt);
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintable(this,job.defaultPage());
        double h_imp=job.defaultPage().getImageableHeight();
         java.awt.FontMetrics fm = jtc.getFontMetrics(jtc.getFont());
        pages=new Stack<String>();
        StringTokenizer st = new StringTokenizer(txt, "\n");
        String page="";
     //   System.out.println("hauteur "+fm.getHeight()+" "+h_imp);
        int compteur=0;
        while (st.hasMoreTokens()) {
          String element = st.nextToken();
          compteur+=fm.getHeight();
          if (compteur>h_imp) {
            pages.push(page);
  	      page = element;
            compteur=fm.getHeight();
          }
          else page+= element+"\n";
        }
        if (!page.equals("")) pages.push(page);
        if (job.printDialog()) {
          try {
            job.print();
          }
          catch (PrinterException ex) {
            System.out.println(ex.getMessage());
          }
        }
        font=jtc.getFont();
        jtc.setFont(new Font(font.getFontName(),Font.PLAIN,12));
        jtc.setText(txt);
	}
	// Edit Actions
	protected void copy(){
		jtc.copy();
	}
	protected void cut(){
		jtc.cut();
	}
	protected void paste(){
		jtc.paste();
	}
	
	// setText Method for Text Zone
	protected void setText(String s){
		jtc.setText(s);
	} 
	protected abstract void ecris(String s);

	abstract void setActive(boolean b);
	class PopupListener extends MouseAdapter {

		public void mousePressed(MouseEvent e) {
			maybeShowPopup(e);
		}

		public void mouseReleased(MouseEvent e) {
			maybeShowPopup(e);
		}

		private void maybeShowPopup(MouseEvent e) {
			if (e.isPopupTrigger()) {
				jpop.show(e.getComponent(), e.getX(), e.getY());
			}
		}
	}	
}
