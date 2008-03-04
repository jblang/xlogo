package xlogo.gui;
import java.awt.Graphics;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.util.Stack;
import javax.swing.event.*;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.SwingUtilities;
import xlogo.StyledDocument.DocumentLogo;
import xlogo.Config;

/**
 * Title :        XLogo
 * Description :  XLogo is an interpreter for the Logo 
 * 						programming language
 * @author Loïc Le Coq
 */
class ZoneEdition extends JTextPane implements Printable,CaretListener,Searchable{
	private static final long serialVersionUID = 1L;
	private DocumentLogo dsd=null;
	protected Stack<String> pages=null;
// Si la correspondance entre parenthese ou crochets est activée
	private boolean active=false;
// Dernière position allumée
	private int[] position=new int[2];

	void ecris(String mot){
		try{
			int deb=getCaretPosition();
			dsd.insertString(deb,mot,null);
			setCaretPosition(deb+mot.length());
		}
	catch(BadLocationException e){}
}
	public DocumentLogo getDsd(){
		return dsd;
	}
	public void setActive(boolean b){
		active=b;
	}
ZoneEdition(){
	dsd=new DocumentLogo();
	setDocument(dsd);
	addCaretListener(this);
}


public int print(Graphics g,PageFormat pf, int pi) throws PrinterException{
		if(pi<pages.size()){
    		setText(pages.get(pi));
    		g.translate((int)pf.getImageableX(),(int)pf.getImageableY());
        	paint(g);
  			return(Printable.PAGE_EXISTS);
		}
		else  return Printable.NO_SUCH_PAGE;
	}

// Teste si le caractère précédent est un backslash
private boolean TesteBackslash(String content,int pos){
	String caractere="";
	if (pos>0) caractere=content.substring(pos-1,pos);
	//System.out.println(caractere);
	if (caractere.equals("\\")) return true;
	return false;
}

class verif_parenthese implements Runnable{
	int pos;
	verif_parenthese(int pos){
	this.pos=pos;	
	}
	public void run(){
		if (active){
			active=false;
			int debut=position[0];
			int fin=position[0];
			if (debut!=-1){
				if (debut>0) debut--;
				if (fin<dsd.getLength()) fin++;
				try{			
					String content=dsd.getText(0,dsd.getLength());
					dsd.colore(content,debut,fin);	
				}
				catch(BadLocationException e){}
			}
			debut=position[1];
			fin=position[1];
			if (debut!=-1){
				if (debut>0) debut--;
				if (fin<dsd.getLength()) debut++;
				if (fin<dsd.getLength()) fin++;
				try{			
					String content=dsd.getText(0,dsd.getLength());
					dsd.colore(content,debut,fin);	
				}
				catch(BadLocationException e){}
			}
		}
		int length=dsd.getLength();
		try{
			String content=dsd.getText(pos,length-pos);
			int id=-1;
			if (length>pos)	{
				id="[]()".indexOf(content.substring(0,1));
			}
			if (id>-1&&!TesteBackslash(dsd.getText(0,pos),pos)){
				active=true;
				switch(id){
				case 0: 
					chercheApres(content,pos,"[","]");
					break;
				case 1:
					content=getText(0,pos);
					chercheAvant(content,pos,"[","]");
				break;
				case 2:
					chercheApres(content,pos,"(",")");
				break;
				case 3:
					content=getText(0,pos);
					chercheAvant(content,pos,"(",")");
				break;
				}
			}
		}
		catch(BadLocationException e1){}

	}
}
public void caretUpdate(CaretEvent e){
	int pos=e.getDot();
	if (Config.COLOR_ENABLED) SwingUtilities.invokeLater(new verif_parenthese(pos));
}
	void chercheApres(String content,int pos,String ouv,String fer){
		boolean continuer=true;
		int of_ouvrant;
		int of_fermant=0;
		int from_index_ouvrant=1;
		int from_index_fermant=1;
		while(continuer){
			of_ouvrant=content.indexOf(ouv,from_index_ouvrant);
			while (of_ouvrant!=-1&&TesteBackslash(content,of_ouvrant)) of_ouvrant=content.indexOf(ouv,of_ouvrant+1);
			of_fermant=content.indexOf(fer,from_index_fermant);
			while (of_fermant!=-1&&TesteBackslash(content,of_fermant)) of_fermant=content.indexOf(fer,of_fermant+1);
			if (of_fermant==-1) break;
			if (of_ouvrant!=-1 && of_ouvrant<of_fermant) {
				from_index_ouvrant=of_ouvrant+1;
				from_index_fermant=of_fermant+1;
			}
			else continuer=false;;
		}
		if (of_fermant!=-1) {
			dsd.Montre_Parenthese(of_fermant+pos);
			position[1]=of_fermant+pos;
		}
		else position[1]=-1;
		dsd.Montre_Parenthese(pos);
		position[0]=pos;
	}
	void chercheAvant(String content,int pos,String ouv,String fer){
		boolean continuer=true;
		int of_fermant=0;
		int of_ouvrant=0;
		int from_index_ouvrant=pos;
		int from_index_fermant=pos;
		while(continuer){
			of_ouvrant=content.lastIndexOf(ouv,from_index_ouvrant);
			while (of_ouvrant!=-1&&TesteBackslash(content,of_ouvrant)) of_ouvrant=content.lastIndexOf(ouv,of_ouvrant-1);			
			of_fermant=content.lastIndexOf(fer,from_index_fermant);
			while (of_fermant!=-1&&TesteBackslash(content,of_fermant)) of_fermant=content.lastIndexOf(fer,of_fermant-1);
			if (of_ouvrant==-1) break;
			if (of_ouvrant<of_fermant) {
				from_index_ouvrant=of_ouvrant-1;
				from_index_fermant=of_fermant-1;
			}
			else continuer=false;;
		}
		if (of_ouvrant!=-1) {
			dsd.Montre_Parenthese(of_ouvrant);
			position[0]=of_ouvrant;
		}
		else position[0]=-1;
		dsd.Montre_Parenthese(pos);
		position[1]=pos;
	
	
	}
	private StringBuffer text;
	private int startOffset,endOffset;
	
	public boolean find(String element,boolean forward){
		try{	
			int index;
			text=new StringBuffer(getText());
			// Find forward
			if (forward)
				index=text.indexOf(element, getCaretPosition());
			else index=text.lastIndexOf(element, getCaretPosition());
			if (index==-1){
				startOffset=0;
				endOffset=0;
				return false;
			}
			else {
				getHighlighter().removeAllHighlights();
				startOffset=index;
				endOffset=index+element.length();
				getHighlighter().addHighlight(startOffset, endOffset,
						DefaultHighlighter.DefaultPainter );
				if (forward) setCaretPosition(index+element.length());
				else if (index>1) setCaretPosition(index-1); 
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
		setText(text.toString());
		if (forward) setCaretPosition(endOffset);
		else if (startOffset>1) setCaretPosition(startOffset-1);

	}
	public void replaceAll(String element, String substitute){

		try {
			String string=getText().toString();
			string=string.replaceAll(element, substitute);
			setText(string);
		}
		catch(NullPointerException e2){}

	}
	public void removeHighlight(){
		getHighlighter().removeAllHighlights();
	}
	
}