package xlogo.kernel;

import java.util.Vector;

import xlogo.Logo;
import xlogo.utils.myException;
public class LogoList extends LogoArgument {
	private Vector<LogoArgument> list;
	LogoList(){
		list=new Vector<LogoArgument>();
	}
	LogoList(Vector<LogoArgument> list){
		this.list=new Vector<LogoArgument>(list);
	}
	protected boolean isList(){
		return true;
	}
	protected void addElement(LogoList arg){
		list.add(arg);
	}
	protected void addElement(LogoArgument arg){
		list.add(arg);
	}
	protected void addElement(LogoWord arg){
		list.add(arg);
	}
	protected void insertElement(LogoArgument arg,int id){
		list.insertElementAt(arg,id);
	}
	protected void removeElement(int id){
		list.remove(id);
	}
	
	// Return the list without brackets and white spaces
	protected String getValue(){
		StringBuffer sb=new StringBuffer("");
		for(int i=0;i<list.size();i++){
			LogoArgument arg=list.get(i);
			// List
			if (arg.isList()) sb.append(arg.toString());
			// Number
			else if (arg.isNumber()) sb.append(arg.toString());
			// Word
			else sb.append(arg.getValue()); 
			if (i!=list.size()-1) sb.append(" ");
		}
		return sb.toString();
	}
	// Return the list with brackets and white spaces
	public String toString(){
		StringBuffer sb=new StringBuffer("[ ");
		for(int i=0;i<list.size();i++){
			LogoArgument arg=list.get(i);
			// List
			if (arg.isList()) sb.append(arg.toString());
			// Number
			else if (arg.isNumber()) sb.append(arg.toString());
			// Word
			else sb.append(arg.getValue()); 
			sb.append(" ");
		}
		sb.append("]");
		return sb.toString();
	}
	protected void removeLineNumber(){
		for (int i=list.size()-1;i>0;i--){
			if (list.get(i).isLineNumber()) list.remove(i);
		}
	}
	protected LogoArgument getElement(int id){
		return list.get(id);
	}
	protected int getSize(){
		return list.size();
	}
	protected int getNumberOfElements(){
		int count=0;
		for(int i=0;i<list.size();i++){
			if (!list.get(i).isLineNumber()) count++;
		}
		return count;
	}
	protected LogoArgument clone(){
		return new LogoList(list);
	}
	protected static LogoList extractList() throws myException {
		LogoList list=new LogoList();
		String element = "";
		while (instructionBuffer.getLength() != 0) {
			element = instructionBuffer.getNextWord();
			// SI crochet ouvrant, on l'empile dans la pile de calcul
			if (element.equals("[")) {
				instructionBuffer.deleteFirstWord(element);
				list.addElement(chercheListe());
			}
			else if (element.equals("]")) { // Si on atteint un crochet fermant
				instructionBuffer.deleteFirstWord(element);
				// 1er cas: Pas de sous liste, on renvoie la liste
				return (list);
			} 
			else { // It's a word
				instructionBuffer.deleteFirstWord(element);
				boolean b=false;
				try{
					Double.parseDouble(element);
					b=true;
				}
				catch(NumberFormatException e){
				}
				LogoWord lw=new LogoWord(element,b);
				list.addElement(lw);
			}
		}
		if (true)
			throw new myException(app, Logo.messages
					.getString("erreur_crochet"));
		return (null);
	}
}
