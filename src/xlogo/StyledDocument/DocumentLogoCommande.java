package xlogo.StyledDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import java.util.*;
import xlogo.Application;
/**
 * Title :        XLogo
 * Description :  XLogo is an interpreter for the Logo 
 * 						programming language
 * @author Loïc Le Coq
 */

public class DocumentLogoCommande extends DocumentLogo {
	private static final long serialVersionUID = 1L;
	private Application cadre;
	public DocumentLogoCommande(Application cadre) {
		super();
		this.cadre=cadre;
	}
	public void insertString(int offset, String str, AttributeSet a) throws BadLocationException
	{
		StringBuffer buffer=new StringBuffer();
		StringTokenizer st=new StringTokenizer(str,"\n");
		while(st.hasMoreTokens()){
			buffer.append(st.nextToken());
		}
		if (str.equals("\n")){
			cadre.commande_actionPerformed();
			
		}
		else {
			str=new String(buffer);
			super.insertString(offset,  str, a);
		}
	}
}
