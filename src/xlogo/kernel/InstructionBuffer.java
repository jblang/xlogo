package xlogo.kernel;

public class InstructionBuffer {
	/**
	 * This integer represents the max Characters in the StringBuffer
	 */
	
	private final static int MAX_CHARACTERS=15000;
	/**
	 * The main StringBuffer containing instruction
	 */
	private StringBuffer mainBuffer;
	/**
	 * If the main StringBuffer is very large, Xlogo could become very slow.
	 * If main StringBuffer should contain more than MAX_CHARACTERS, those characters are saved in this StringBuffer 
	 */
	private StringBuffer stock;
	
	InstructionBuffer(){
		clear();
	}
	InstructionBuffer(String s){
		mainBuffer=new StringBuffer(s);
		stock=new StringBuffer();
	}
	/**
	 * Inserts some instructions at the beginning of the StringBuffer mainBuffer 
	 * @param sb: The code to insert
	 */
	void insertCode(StringBuffer sb){
		if (sb.length()>InstructionBuffer.MAX_CHARACTERS) {
			// insert current mainBuffer to stock
			if (mainBuffer.length()!=0) stock.insert(0,mainBuffer);
			// Copy MAX_CHARACTERS into mainBuffer
			mainBuffer=new StringBuffer(sb.substring(0, InstructionBuffer.MAX_CHARACTERS));
			// All remaining characters into stock
			stock.insert(0,sb.substring(InstructionBuffer.MAX_CHARACTERS));
		}
		else {
			mainBuffer.insert(0, sb);
		}
		
	}
	/**
	 * returns the total length of the two Buffer
	 */
	protected int getLength(){
		return mainBuffer.length()+stock.length();
	}
	/**
	 * Inserts the String s at the beginning of the mainBuffer
	 * @param s
	 */
	protected void insert(String s){
		mainBuffer.insert(0, s);
	}
	/**
	 * Search for the String s , first in mainBuffer and then in stock
	 * if it isn't found in any buffer returns -1
	 * @param s: The String to search
	 */
	protected int indexOf(String s){
		int index=mainBuffer.indexOf(s);
		if (index==-1){
			index=stock.indexOf(s);
			if (index==-1) return -1;
			else return index+mainBuffer.length();
		}
		return index;
		
	}
	/**
	 * Search for the String s , first in mainBuffer and then in stock
	 * if not found in any buffer returns -1
	 * @param s: The String to search
	 */
	protected int indexOf(String s, int fromIndex){
		int index=-1;
		if (fromIndex<mainBuffer.length()) index=mainBuffer.indexOf(s,fromIndex);
		if (index==-1){
			int from=0;
			if (fromIndex>=mainBuffer.length()) from=fromIndex-mainBuffer.length();
			index=stock.indexOf(s,from);
			if (index==-1) return -1;
			else return index+mainBuffer.length();
		}
		return index;
		
	}
	
	
	/**
	 * Delete all code from offset start to offset end
	 * @param start Start offset
	 * @param end End offset
	 */
	protected void delete(int start, int end){
		if (end<=mainBuffer.length()) mainBuffer.delete(start, end);
		else {
			stock.delete(0, end-mainBuffer.length());
			mainBuffer=new StringBuffer();
			transferStock();
		}
		if (mainBuffer.length()==0){
			// if there are instruction in stock yet
			if (stock.length()!=0) transferStock();
		}
		
	}
	/**
	 * Transfers MAX_CHARCATERS from the buffer stock to mainBuffer
	 */
	
	private void transferStock(){
		if (stock.length()>InstructionBuffer.MAX_CHARACTERS){
			mainBuffer.append(stock.substring(0, InstructionBuffer.MAX_CHARACTERS));			
			stock.delete(0, InstructionBuffer.MAX_CHARACTERS);
		}
		else {
			mainBuffer.append(stock);
			stock=new StringBuffer();
		}
	}
	/**
	 * Returns next Word 
	 * @return a String which represents the next word
	 */
	protected String getNextWord() {
		StringBuffer mot = new StringBuffer();
		char caractere;
		for (int i = 0; i < mainBuffer.length(); i++) {
			caractere = mainBuffer.charAt(i);
			if (caractere == ' ') {
				return mot.toString();
			} else
				mot.append(caractere);
			if (i==mainBuffer.length()-1&& stock.length()!=0){
				transferStock();
			}
		}
		// System.out.println("mot: "+mot);
		return mot.toString();
	}
 /**
  * Deletes the String mot from the mainBuffer instructions
  * @param mot The string to delete
  */
	protected void deleteFirstWord(String mot) {
		if (mainBuffer.length() > mot.length())
			mainBuffer = mainBuffer.delete(0, mot.length() + 1);
		else
			mainBuffer = new StringBuffer();
		if (mainBuffer.length()==0){
			// if there are instruction in stock yet
			if (stock.length()!=0) transferStock();
		}
	}
	/**
	 * Return Character at the chosen index. this methos search first in mainBuffer and then in stock
	 * @param index The chosen index
	 * @return A character
	 */
	protected char charAt(int index){
		if (index<mainBuffer.length()) return mainBuffer.charAt(index);
		return stock.charAt(index-mainBuffer.length());
		
	}
	/**
	 * Clear the both buffers
	 */
	public void clear(){
		mainBuffer=new StringBuffer();
		stock=new StringBuffer();
	}
	public String toString(){
		return mainBuffer.toString()+stock.toString();
//		return "MainBuffer:"+mainBuffer.toString()+":stock:"+stock.toString();
	}
}
