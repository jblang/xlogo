package xlogo.gui;

import javax.swing.JTextArea;

public class EditorTextArea extends EditorTextZone {
	EditorTextArea(Editor editor){
		super(editor);
		jtc=new JTextArea();
		initGui();
	}
	public void setActive(boolean b){}
	protected void ecris(String s){
		int deb=jtc.getCaretPosition();
		((JTextArea)jtc).insert(s,deb);
		jtc.setCaretPosition(deb+s.length());
		
	}
}
