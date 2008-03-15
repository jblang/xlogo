/**
 * Title : XLogo Description : XLogo is an interpreter for the Logo programming
 * language
 * 
 * @author Loïc Le Coq
 */
package xlogo.kernel;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.math.BigDecimal;
import xlogo.Config;
import xlogo.kernel.LoopProperties;
import xlogo.utils.Utils;
import xlogo.utils.myException;
import xlogo.Application;
import java.io.*;
import xlogo.Logo;
import java.util.Enumeration;
public class Primitive {
	//  float taille_crayon=(float)0;
	private Application cadre;
	protected static final int PRIMITIVE_NUMBER = 280;

	protected int[] parametres=new int[PRIMITIVE_NUMBER];
	protected boolean[] generalForm=new boolean[PRIMITIVE_NUMBER];
	
	// Treemap for primitives (better efficiency in searching)
	public static TreeMap<String,String> primitives = new TreeMap<String,String>(); 


	public static Stack<LoopProperties> stackLoop = new Stack<LoopProperties>();

	public Primitive() {
	}

	public Primitive(Application cadre) {
		this.cadre = cadre;
		//build treemap for primitives
		buildPrimitiveTreemap(Config.langage);
	}

	//Exécution des primitives
	public void buildPrimitiveTreemap(int id) {
	//	this.exportPrimCSV();
		primitives = new TreeMap<String,String>();
		Locale locale = Logo.generateLocale(Config.langage);
		ResourceBundle prim = ResourceBundle.getBundle("primitives", locale);
		try{
			BufferedReader bfr=new BufferedReader(
					new InputStreamReader(Primitive.class.getResourceAsStream("genericPrimitive")));
			int i=0;
			while(bfr.ready()){
				String line=bfr.readLine();
				// read the generic keyword for the primitive
				StringTokenizer cut=new StringTokenizer(line);
				// read the standard number of arguments for the primitive
				String cle = cut.nextToken();
				parametres[i]=Integer.parseInt(cut.nextToken());
				// Read if the primitive has a general form 
				// eg (sum 2 3 4 5) --> 14
				// eg (list 3 4 5) ---> [3 4 5]
				if (cut.hasMoreTokens()) {
					generalForm[i]=true;
				}
				else generalForm[i]=false;
				
				if (i == 39)
					primitives.put("\n", "39");
				// Internal Primitive siwhile
				else if (i==95){
					primitives.put("\\siwhile", "95");
				}
				// Internal Primitive \x
				else if (i==212)
					primitives.put("\\x","212");
				else {
					if (cle.length() != 1) {
						if (!cle.equals(">=")&&!cle.equals("<="))
						cle = prim.getString(cle);
					}
					StringTokenizer st = new StringTokenizer(cle);
					int compteur = 0;
					while (st.hasMoreTokens()) {
						primitives.put(st.nextToken(), String.valueOf(i
								+ Primitive.PRIMITIVE_NUMBER * compteur));
						compteur++;
					}
				}
				i++;
			}
//			System.out.println(i+ " primitives");
		}
		catch(IOException e){System.out.println("Impossible de lire le fichier d'initialisation des primitives");}
	}
	// primitive repeat
	protected void repete(int i, String st) {
		if (i > 0) {
			st = new String(Utils.decoupe(st));
			LoopProperties bp = new LoopProperties(BigDecimal.ONE,
					new BigDecimal(i), BigDecimal.ONE, st, "repete");
			stackLoop.push(bp);
			Interprete.instruction.insert(0, st + "\\ ");
		}
	}
	// primitive if
	protected void si(boolean b, String li, String li2) {
		if (b) {
			Interprete.instruction.insert(0, li);
		} else if (null != li2) {
			Interprete.instruction.insert(0, li2);
		}
	}
	// internal primitive for loop while.
	protected void whilesi(boolean b, String li) {
		if (b) {
			Interprete.instruction.insert(0, li
					+ Primitive.stackLoop.peek().getInstr());
		} else {
			try {
				Interprete.tueniveau("\\", cadre);
			} catch (myException e) {
			}
		}
	}
	// primitive stop
	protected void stop() throws myException{
		Interprete.operande = false;
		String car="";
		try {
			car=Interprete.tueniveau("\\", cadre);
		} catch (myException e) {
		}
		
		// A procedure has been stopped
		if (car.equals("\n")){
			String en_cours=Interprete.en_cours.pop();
			Interprete.locale = Interprete.stockvariable.pop();
			// Example: 	to bug
			//				fd stop
			//				end 
			// 				--------
			//				bug
			//				stop doesn't output to fd
			if (!Interprete.nom.isEmpty()&&!Interprete.nom.peek().equals("\n")){
			//	System.out.println(Interprete.nom);
				throw new myException(cadre, Utils.primitiveName("controls.stop")
						+ " " + Logo.messages.getString("ne_renvoie_pas") + " "
						+ Interprete.nom.peek());}
			else if (!Interprete.nom.isEmpty()){
				// Removing the character "\n"
				Interprete.nom.pop();
				// Example: 	to bug		|	to bug2
				// 				fd bug2		|	stop
				//				end			|	end
				//				------------------------
				// 				bug
				//				bug2 doesn't output to fd		
				if (!Interprete.nom.isEmpty()&&!Interprete.nom.peek().equals("\n")){
					//	System.out.println(Interprete.nom);
						throw new myException(cadre, en_cours
								+ " " + Logo.messages.getString("ne_renvoie_pas") + " "
								+ Interprete.nom.peek());}
			}
		}
	}
	// primitive output
	protected void retourne(String val) throws myException {
		Interprete.calcul.push(val);
		Interprete.operande = true;
		if (Kernel.mode_trace) {
			StringBuffer buffer = new StringBuffer();
			for (int i = 0; i < Interprete.en_cours.size() - 1; i++)
				buffer.append("  ");
			buffer.append(Interprete.en_cours.peek());
			buffer
					.append(" " + Utils.primitiveName("ret") + " "
							+ val);
			cadre.ecris("normal", Utils.SortieTexte(buffer.toString()) + "\n");
		}
		Interprete.en_cours.pop();
		Interprete.locale = Interprete.stockvariable.pop();
		if ((!Interprete.nom.isEmpty())
				&& Interprete.nom.peek().equals("\n")) {
			try {
				Interprete.tueniveau("\n", cadre);
			} catch (myException e) {
			}
			Interprete.nom.pop();
		} else if (!Interprete.nom.isEmpty())
			throw new myException(cadre, Utils.primitiveName("ret")
					+ " " + Logo.messages.getString("ne_renvoie_pas") + " "
					+ Interprete.nom.peek());
		else
			throw new myException(cadre, Logo.messages
					.getString("erreur_retourne"));
	}
	private void exportPrimCSV(){
		StringBuffer sb=new StringBuffer();
		Locale locale=new Locale("fr","FR");
		ResourceBundle prim_fr = ResourceBundle.getBundle("primitives", locale);	
		locale=new Locale("en","US");
		ResourceBundle prim_en = ResourceBundle.getBundle("primitives", locale);
		locale=new Locale("ar","MA");
		ResourceBundle prim_ar = ResourceBundle.getBundle("primitives", locale);
      	locale=new Locale("es","ES");
      	ResourceBundle prim_es = ResourceBundle.getBundle("primitives", locale);
        locale=new Locale("pt","BR");
        ResourceBundle prim_pt = ResourceBundle.getBundle("primitives", locale);
       	locale=new Locale("eo","EO");
       	ResourceBundle prim_eo = ResourceBundle.getBundle("primitives", locale);
       	locale=new Locale("de","DE");
       	ResourceBundle prim_de = ResourceBundle.getBundle("primitives", locale);
        //locale=new Locale("nl","NL");
       	ResourceBundle[] prim={prim_fr,prim_en,prim_es,prim_pt,prim_ar,prim_eo,prim_de};
		try{
			BufferedReader bfr=new BufferedReader(
					new InputStreamReader(Primitive.class.getResourceAsStream("genericPrimitive")));
			int i=0;
			while(bfr.ready()){
				String line=bfr.readLine();
				StringTokenizer cut=new StringTokenizer(line); 
				String cle = cut.nextToken();
				parametres[i]=Integer.parseInt(cut.nextToken());
				if (i!=39&&i!=95&&i!=212&&cle.length() != 1) {
					sb.append("$");
					sb.append(cle);
					sb.append("$");
					sb.append(";");
					for (int j=0;j<prim.length;j++){
						String txt=cle;
						txt= prim[j].getString(cle);
						sb.append("$");
						sb.append(txt);
						sb.append("$");
						sb.append(";");							
					}
					sb.append("\n");	
				}
				i++;
			}
			Utils.writeLogoFile("/home/loic/primTable.csv", new String(sb));
		}
		catch(Exception e){e.printStackTrace();}
	}

	private void exportMessageCSV(){
		StringBuffer sb=new StringBuffer();
		Locale locale=new Locale("fr","FR");
		ResourceBundle lang_fr = ResourceBundle.getBundle("langage", locale);	
		locale=new Locale("en","US");
		ResourceBundle lang_en = ResourceBundle.getBundle("langage", locale);
		locale=new Locale("ar","MA");
		ResourceBundle lang_ar = ResourceBundle.getBundle("langage", locale);
      	locale=new Locale("es","ES");
      	ResourceBundle lang_es = ResourceBundle.getBundle("langage", locale);
        locale=new Locale("pt","BR");
        ResourceBundle lang_pt = ResourceBundle.getBundle("langage", locale);
       	locale=new Locale("eo","EO");
       	ResourceBundle lang_eo = ResourceBundle.getBundle("langage", locale);
       	locale=new Locale("de","DE");
       	ResourceBundle lang_de = ResourceBundle.getBundle("langage", locale);
        //locale=new Locale("nl","NL");
       	ResourceBundle[] lang={lang_fr,lang_en,lang_es,lang_pt,lang_ar
       			,lang_eo,lang_de};
		try{
			Enumeration<String> en=lang_fr.getKeys();
			while (en.hasMoreElements()){
				String cle=en.nextElement();
				sb.append("$");
				sb.append(cle);
				sb.append("$");
				sb.append(";");
				for (int j=0;j<lang.length;j++){
					String txt= lang[j].getString(cle);
					sb.append("$");
					sb.append(txt);
					sb.append("$");
					if (j!=lang.length-1) sb.append(";");					
				}
				
				sb.append("\n");
			}
			Utils.writeLogoFile("/home/loic/messageTable.csv", new String(sb));
		}
		catch(Exception e){e.printStackTrace();}
	}
}