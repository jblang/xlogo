package xlogo.kernel;
import java.util.Vector;
public class LexemBuffer {
	Vector<LogoLexem> mainBuffer;
	LexemBuffer(){
		mainBuffer=new Vector<LogoLexem>();
	}
	protected int size(){
		return mainBuffer.size();
	}
	protected LogoLexem getNextWord(){
		return mainBuffer.get(0);
	}
	protected void deleteFirstWord(){
		mainBuffer.remove(0);
	}
	public static Vector<LogoLexem> decoupe(String st) {  
		Vector<LogoLexem> buffer = new Vector<LogoLexem>();
		StringBuffer lexem=new StringBuffer();
		
		// If last character is a white space
		boolean espace=false;
		// If last character is a backslash
		boolean backslash=false;
		// If last character is a word
		boolean mot=false;
		// If last character is from a list
		boolean list=false;
		// If last character is from a procedure or a primitive
		boolean command=false;
		// If last character is from a variable
		boolean variable=false;
		int crochet_liste=0;
//		boolean variable=false;
		// If XLogo is running a program
		boolean execution_lancee=Affichage.execution_lancee;
		for(int i=0;i<st.length();i++){
			char c=st.charAt(i);
			if (c==' ') {
				if (!espace&&buffer.size()!=0) {
					if (backslash) lexem.append(" ");
					else {
						espace=true;
						if (mot) buffer.add(new LogoWord(lexem.toString()));
						else if (variable) buffer.add(new LogoVariable(lexem.toString()));
						else if (command) buffer.add(new Command(lexem.toString()));
						else if (list) buffer.add(new LogoList());
						mot=false;
						variable=false;
						list=false;
					}
					backslash=false;
				}
			}
			else if(c=='\\'&&!backslash) {
				espace=false;
				backslash=true;
			}
			else if(c=='\"'){
				if (espace&&crochet_liste<=0){
					mot=true;
				}
//				buffer.append(c);
				espace=false;
				backslash=false;
			}
			else if (c==':'){
				if (espace&&crochet_liste<=0){
					variable=true;
				}
//				buffer.append(c);
				espace=false;
				backslash=false;
			}
			else if (c=='['||c==']'||c=='('||c==')'){
				//Modifications apportées
				if (backslash) {
					lexem.append(c);
					backslash=false;
				}
				else {
					if (c=='[') crochet_liste++;
					else if (c==']') crochet_liste--;
					if (espace||buffer.length()==0) {buffer.append(c+" ");espace=true;}
					else {
						buffer.append(" "+c+" ");
						espace=true;
					}
				}
			}
			else if (c=='+'||c=='-'||c=='*'||c=='/'||c=='='||c=='<'||c=='>'||c=='&'||c=='|'){
				// à modifier (test + fin)
				if (mot||crochet_liste>0) {
					buffer.append(c);
					if (espace) espace=false;
				}
				else { 
					String op=String.valueOf(c);
					// Looking for operator <= or >=
					if (c=='<'||c=='>'){
						if (i+1<st.length()){
							if (st.charAt(i+1)=='='){
								op+="=";
								i++;
							}
						}
					}
					if (espace) buffer.append(op+" ");
					else {
						espace=true;
						buffer.append(" "+op+" ");
					}
				}
			}
			else{
				if (backslash){
					if (c=='n')	buffer.append("\\n");
					else if (c=='\\') buffer.append("\\\\"); 
					else if (c=='v'&& execution_lancee) buffer.append("\"");
					else if(c=='e'&& execution_lancee) buffer.append("\\e");
					else if (c=='#') buffer.append("\\#");
					else if (c=='l'&&execution_lancee) buffer.append("\\l");
					else { 
						buffer.append(c);
					}
				}
				else {
					buffer.append(c);	
				}
				backslash=false;
				espace=false;
			}
		}
		//System.out.println(buffer);
		// Remove the space when the user write only "*" or "+" in the command line
		//if (buffer.length()>0&&buffer.charAt(0)==' ') buffer.deleteCharAt(0);
		return (buffer);
	}
}
