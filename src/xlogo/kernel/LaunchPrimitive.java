/**
 * Title :        XLogo
 * Description :  XLogo is an interpreter for the Logo 
 * 						programming language
 * @author Loïc Le Coq
 */
package xlogo.kernel;
import java.util.Stack;
import java.util.Iterator;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Calendar;
import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import javax.swing.JTextArea;
import java.awt.image.BufferedImage;
import javax.imageio.*;
import java.io.*;
import javax.swing.JOptionPane;
import javax.swing.ImageIcon;
import javax.swing.Icon;
import javax.vecmath.Point3d;
import javax.media.j3d.TransformGroup;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import javax.media.j3d.Shape3D;
import xlogo.utils.Utils;
import xlogo.gui.Lis;
import xlogo.gui.preferences.*;
import xlogo.utils.myException;
import xlogo.kernel.DrawPanel;
import xlogo.Config;
import xlogo.gui.HistoryPanel;
import xlogo.Application;
import xlogo.Logo;
import xlogo.kernel.network.*;
import xlogo.kernel.gui.*;
import xlogo.kernel.perspective.ElementPolygon;
import xlogo.kernel.perspective.ElementLine;
import xlogo.kernel.perspective.ElementPoint;
/**
 * When a primitive or a procedure has all arguments,
 * LauchPrimitive executes the appropriate code.
 * **/
public class LaunchPrimitive {
/**
 * Default Application frame
 */	
	private Application cadre;
	/**
	 * Default kernel
	 */
	private Kernel kernel;
	/**
	 * Default workspace
	 */
	private Workspace wp;
	private Procedure procedure;
//	private MathContext mc=MathContext.DECIMAL64;
	/** 
	 * This is the start for the String returned by primitive or procedure.<br>
	 * It is "\"" for words and "" for numbers. <br><br>
	 * 
	 * Ceci est le début de la chaine générique renvoyé par les primitives<br>
	 * Elle vaut "\"" pour les mots et "" pour les nombres<br>
	**/
	private String debut_chaine = "";
	/**   
	 *  When we launch the primitive "listentcp", we have to save workspaces 
	 * **/
	private Stack<Workspace> savedWorkspace;
/**
 * @param cadre Default frame Application
 * @param wp Default workspace
 **/
	public LaunchPrimitive(Application cadre,Workspace wp) {
		this.wp=wp;
		this.cadre = cadre;
		this.kernel=cadre.getKernel();
	}
	/**
	 * Execute the primitive number "id" with the arguments contained in "param"<br>
	 * <ul><li>
	 * if id<0: it is a procedure. <br>
	 * For example, if id=-3, it is procedure number -i-2=-(-3)-2=1
	 *  </li>
	 *  <li>
	 *  if d>=0: it is primitive number "id"</li></ul>
	 * @param id The number representing the procedure or the primitive
	 * @param param The Stack that contains all arguments
	 */
	protected void execute(int id, Stack<String> param) {
		//identifiant procédure ou primitive, valeur des paramètres
		if (id < 0) {
			procedure = wp.getProcedure(-id - 2);
			Interprete.stockvariable.push(Interprete.locale);
			Interprete.locale=new HashMap<String,String>();
			// Read local Variable
			int optSize=procedure.optVariables.size();
			int normSize=procedure.variable.size();
			for (int j=0;j<optSize+normSize;j++){
				// Add local Variable
				if (j<normSize)
					{
						Interprete.locale.put(procedure.variable.get(j), param.get(j));
					}	// add optional variables
				else {
					String value="";
					if (j<param.size()) value=param.get(j);
					else value=procedure.optVariablesExp.get(j-param.size()).toString();
					Interprete.locale.put(procedure.optVariables.get(j-normSize), value);
					
				}
			}
			// Add Optionnal variable
			if (Kernel.mode_trace) {
				StringBuffer buffer=new StringBuffer();
				for(int i=0;i<Interprete.en_cours.size();i++) buffer.append("  ");
				buffer.append(procedure.name);
				for (int i=0;i<param.size();i++) buffer.append(" "+Utils.SortieTexte(param.get(i)));
				String msg=buffer + "\n";
				cadre.ecris("normal", msg);
			}
			Interprete.en_cours.push(procedure.name);
			if (Interprete.instruction.length()==0)
				Interprete.instruction.append(procedure.instr + "\n ");
			else {
				Interprete.instruction.insert(0, procedure.instr + "\n ");
			}
//			System.out.println("a"+Interprete.instruction+"a");
			Interprete.nom.push("\n");
		} else {
			switch (id) {
			case 0: //av
				delay();
				try {
					cadre.getArdoise().av(number(param.pop()));
				} catch (myException e) {
				}
				break;
			case 1: //re
				delay();
				try {
					cadre.getArdoise().av(-number(param.pop()));
				} catch (myException e) {
				}
				break;
			case 2: //td
				delay();
				try {
					cadre.getArdoise().td(number(param.pop()));
				} catch (myException e) {
				}
				break;
			case 3: //tg
				delay();
				try {
					cadre.getArdoise().td(-number(param.pop()));

				} catch (myException e) {
				}
				break;
			case 4: //puissance
				try {
					double p = Math.pow(number(param.get(0)),
							number(param.get(1)));
					Interprete.calcul.push(teste_fin_double(p));
					Interprete.operande = true;
				} catch (myException e) {
				}
				break;
			case 5: //repete
				try {
					String liste = getList(param.get(1));
					kernel.primitive.repete(getInteger(param.get(0)), liste);
				} catch (myException e) {
				}
				break;
			case 6: //ve
				cadre.getArdoise().videecran();
				break;
			case 7: //ct
				if (kernel.getActiveTurtle().isVisible()) {
					cadre.getArdoise().ct_mt();
					cadre.getArdoise().tortues_visibles.remove(String
							.valueOf(kernel.getActiveTurtle().id));
				}
				kernel.getActiveTurtle().setVisible(false);
				break;
			case 8: //mt
				if (!kernel.getActiveTurtle().isVisible()) {
					cadre.getArdoise().ct_mt();
					cadre.getArdoise().tortues_visibles.push(String.valueOf(kernel.getActiveTurtle().id));
				}
				kernel.getActiveTurtle().setVisible(true);
				break;
			case 9: //ecris, ec
				int size=param.size();
				String result="";
				String mot;
				for(int i=0;i<size;i++){
					String par = param.get(i).trim();
					if (isList(par))
						par = formatList(par.substring(1, par.length() - 1));
					mot = getWord(param.get(i));
					if (null == mot)
						result+=Utils.SortieTexte(par)+" ";
					else
						result+=Utils.SortieTexte(mot)+" ";	
				}
				cadre.ecris("perso", result + "\n");
				break;
			case 10: // si // if
				try {
					String liste = getList(param.get(1));
					liste=new String(Utils.decoupe(liste));
					String liste2 = null;
					boolean predicat = predicat(param.get(0));
					if (Interprete.instruction.length()!=0) {
						try {
							String element = Interprete.getNextWord();
							//	System.out.println("a"+element+"a");
							if (element.startsWith("\\l")){
								Interprete.deleteFirstWord(element);
								Interprete.lineNumber=element+" ";
							}
							if (Interprete.instruction.charAt(0)=='[') {
								Interprete.deleteFirstWord("[");
								liste2 = getFinalList(kernel.listSearch());
								liste2=new String(Utils.decoupe(liste2));
							}
						} catch (Exception e) {
						}
					}
					kernel.primitive.si(predicat, liste, liste2);
					Interprete.renvoi_instruction=true;
				} catch (myException e) {
				}
				break;
			case 11: // STOP
				try {
					kernel.primitive.stop();
				}
				catch(myException e){}
				break;
			case 12: //origine
				delay();
				cadre.getArdoise().origine();
				break;
			case 13: //fpos
				delay();
				try {
					String list=getFinalList(param.get(0));
					cadre.getArdoise().fpos(list);
				} catch (myException e) {
				}
				break;
			case 14: //fixex
				delay();
				try {
					if (DrawPanel.etat_fenetre!=DrawPanel.WINDOW_3D){
						double x = number(param.get(0));
						double y = Config.imageHeight/2 - kernel.getActiveTurtle().corY;
						cadre.getArdoise().fpos(x + " " + y);
					}
					else cadre.getArdoise().fpos(number(param.get(0))+" "+kernel.getActiveTurtle().Y+" "
							+kernel.getActiveTurtle().Z);
				} catch (myException e) {
				}
				break;
			case 15: //fixey
				delay();
				try {
					if (DrawPanel.etat_fenetre!=DrawPanel.WINDOW_3D){
						double y = number(param.get(0));
						double x = kernel.getActiveTurtle().corX - Config.imageWidth/2;
						cadre.getArdoise().fpos(x + " " + y);
					}
					else cadre.getArdoise().fpos(kernel.getActiveTurtle().X+" "+number(param.get(0))
							+" "+kernel.getActiveTurtle().Z);
						
				} catch (myException e) {
				}
				break;
			case 16: //fixexy
				delay();
				try {
					primitive2D("drawing.fixexy");
					cadre.getArdoise().fpos(number(param.get(0)) + " "
							+ number(param.get(1)));
				} catch (myException e) {
				}
				break;
			case 17: // fixecap
				delay();
				try {
					if (DrawPanel.etat_fenetre!=DrawPanel.WINDOW_3D)
					cadre.getArdoise().td(360 - kernel.getActiveTurtle().heading
							+ number(param.pop()));
					else{
        					cadre.getArdoise().setHeading(number(param.pop()));						
					}
				} catch (myException e) {
				}
				break;
			case 18: //lc
				kernel.getActiveTurtle().setPenDown(false);
				break;
			case 19: //bc
				kernel.getActiveTurtle().setPenDown(true);
				break;
			case 20: //gomme
				kernel.getActiveTurtle().setPenDown(true);
				// if mode penerase isn't active yet
				if (kernel.getActiveTurtle().couleurmodedessin.equals(kernel.getActiveTurtle().couleurcrayon)){
					kernel.getActiveTurtle().couleurmodedessin = kernel.getActiveTurtle().couleurcrayon;
					kernel.getActiveTurtle().couleurcrayon = cadre.getArdoise().getBackgroundColor();
				}
				break;
			case 21: // inversecrayon
				kernel.getActiveTurtle().setPenDown(true);
				kernel.getActiveTurtle().setPenReverse (true);
				break;
			case 22: //dessine
				kernel.getActiveTurtle().setPenReverse(false);
				kernel.getActiveTurtle().setPenDown(true);
				kernel.getActiveTurtle().couleurcrayon = kernel.getActiveTurtle().couleurmodedessin;
				break;
			case 23: //somme
				add(param);
				break;
			case 24: //difference
				substract(param);
				break;
			case 25: //moins (opposé)
				try {
					BigDecimal a = numberDecimal(param.get(0));
					Interprete.calcul.push(a.negate().toString());
					Interprete.operande = true;
				} catch (myException e) {
				}
				break;
			case 26: //produit
				multiply(param);
			break;
			case 27: //div
				divide(param);
			break;
			case 28: //reste
				try {
					int a = getInteger(param.get(0));
					int b = getInteger(param.get(1));
					if (b == 0)
						throw new myException(cadre, Logo.messages
								.getString("division_par_zero"));
					Interprete.calcul.push(teste_fin_double(a % b));
					Interprete.operande = true;
				} catch (myException e) {
				}
				break;
			case 29: //retourne
				try {
					kernel.primitive.retourne(param.get(0));
				} catch (myException e) {
				}
				break;
			case 30: // *
				multiply(param);
				break;
			case 31: // diviser /
				divide(param);
				break;
			case 32: // +
				add(param);
			break;
			case 33: // -
				substract(param);
			break;
			case 34: // =
				equal(param);
				break;
			case 35: // <
				try {
					double a = number(param.get(0));
					double b = number(param.get(1));
					if (a < b)
						Interprete.calcul.push(Logo.messages.getString("vrai"));
					else
						Interprete.calcul.push(Logo.messages.getString("faux"));
				} catch (myException e) {
				}
				Interprete.operande = true;
				break;
			case 36: // >
				try {
					double a = number(param.get(0));
					double b = number(param.get(1));
					if (a > b)
						Interprete.calcul.push(Logo.messages.getString("vrai"));
					else
						Interprete.calcul.push(Logo.messages.getString("faux"));
				} catch (myException e) {
				}
				Interprete.operande = true;
				break;
			case 37: // |
				try {
					boolean b1 = predicat(param.get(0));
					boolean b2 = predicat(param.get(1));
					b1 = b1 | b2;
					if (b1)
						Interprete.calcul.push(Logo.messages.getString("vrai"));
					else
						Interprete.calcul.push(Logo.messages.getString("faux"));
				} catch (myException e) {
				}
				Interprete.operande = true;
				break;
			case 38: // &
				try {
					boolean b1 = predicat(param.get(0));
					boolean b2 = predicat(param.get(1));
					b1 = b1 & b2;
					if (b1)
						Interprete.calcul.push(Logo.messages.getString("vrai"));
					else
						Interprete.calcul.push(Logo.messages.getString("faux"));
				} catch (myException e) {
				}
				Interprete.operande = true;
				break;
			case 39: //opérateur interne \n signalant une fin de procédure
				Interprete.locale =  Interprete.stockvariable.pop();
				if (Interprete.nom.peek().equals("\n")){
					Interprete.nom.pop();
					Interprete.lineNumber="";
				}
				else {
					try {
						throw new myException(cadre, Logo.messages
								.getString("pas_assez_de")
								+ " " + Interprete.nom.peek());
					} catch (myException e) {
					}
				}
				if (!Interprete.nom.isEmpty()){
					try{
						throw new myException(cadre,Interprete.en_cours.peek()+" "+Logo.messages.getString("ne_renvoie_pas")+" "+Interprete.nom.peek().toString());
					}
					catch(myException e){}
				}
				if (!Interprete.en_cours.isEmpty()) Interprete.en_cours.pop();
				break;
			case 40: // opérateur interne \ signalant une fin de boucle
				LoopProperties bp=Primitive.stackLoop.peek();
				String idb=bp.getId();
				 // Si c'est repete
				if (idb.equals("repete")){
					BigDecimal compteur=bp.getCompteur();
					BigDecimal fin=bp.getFin();
					if (compteur.compareTo(fin) < 0) {
						bp.incremente();
						Primitive.stackLoop.pop();
						Primitive.stackLoop.push(bp);
						Interprete.instruction.insert(0, bp.getInstr()+ "\\ ");	
					}
					else if (compteur.compareTo(fin)==0){
						Primitive.stackLoop.pop();
					}
				} 
				else if (idb.equals("repetepour")) { // si c'est repetepour
					BigDecimal inc=bp.getIncrement();
					BigDecimal compteur=bp.getCompteur();
					BigDecimal fin=bp.getFin();
					
					if ((inc.compareTo(BigDecimal.ZERO)==1&& (compteur.add(inc).compareTo(fin) <=0))
							||(inc.compareTo(BigDecimal.ZERO)==-1&&(compteur.add(inc).compareTo(fin)>=0))){
						bp.incremente();
						bp.AffecteVar(false);
						Primitive.stackLoop.pop();
						Primitive.stackLoop.push(bp);
						Interprete.instruction.insert(0, bp.getInstr()+ "\\ ");			
					}
					else {
						bp.DeleteVar();
						Primitive.stackLoop.pop();
					}
				}
				break;
			case 41: //pos
				Interprete.operande = true;
				if (DrawPanel.etat_fenetre!=DrawPanel.WINDOW_3D){
					long a = Math.round(kernel.getActiveTurtle().corX - Config.imageWidth/2);
					long b = Math.round(Config.imageHeight/2 - kernel.getActiveTurtle().corY);
					Interprete.calcul.push("[ " + a + " " + b + " ] ");
				}
				else {
					Interprete.calcul.push("[ "+kernel.getActiveTurtle().X+" "
							+kernel.getActiveTurtle().Y+" "+kernel.getActiveTurtle().Z+" ] ");
					
				}
				break;
			case 42: //cap
				Interprete.operande = true;
				Interprete.calcul.push(teste_fin_double(kernel.getActiveTurtle().heading));
				break;
			case 43: //arrondi
				Interprete.operande = true;
				try {
					Interprete.calcul.push(String.valueOf(Math
							.round(number(param.get(0)))));
				} catch (myException e) {
				}
				break;
			case 44: //log10
				Interprete.operande = true;
				try {
					double nombre = number(param.get(0));
					if (nombre < 0 || nombre == 0) {
						String log=Utils.primitiveName("arithmetic.log10");
						throw new myException(cadre, log + " "
								+ Logo.messages.getString("attend_positif"));
					}
					Interprete.calcul.push(teste_fin_double(Math.log(nombre)
							/ Math.log(10)));
				} catch (myException e) {
				}
				break;
			case 45: //sin
				try {
					Interprete.calcul.push(teste_fin_double(Math.sin(Math
							.toRadians(number(param.get(0))))));
				} catch (myException e) {
				}
				Interprete.operande = true;
				break;
			case 46: //cos
				try {
					Interprete.calcul.push(teste_fin_double(Math.cos(Math
							.toRadians(number(param.get(0))))));
				} catch (myException e) {
				}
				Interprete.operande = true;
				break;
			case 47: //ou
				ou(param);
				break;
			case 48: //et
				et(param);
			break;
			case 49: //non
				try {
					Interprete.operande = true;
					boolean b1 = predicat(param.get(0));
					if (b1)
						Interprete.calcul.push(Logo.messages.getString("faux"));
					else
						Interprete.calcul.push(Logo.messages.getString("vrai"));
				} catch (myException e) {
				}
				break;
			case 50: //liste
				String liste = "[ ";
				Interprete.operande = true;
				String mot2;
				for(int i=0;i<param.size();i++){
					mot2 = param.get(i);	
					mot = getWord(param.get(i));
					if (null == mot){
						liste += mot2;
					//	System.out.println("a"+mot2+"a");
						}
					else {
						if (mot.equals("")) mot="\\v";
						liste += mot + " ";
					}
				}
				Interprete.calcul.push(liste + "] ");
				break;
			case 51: //phrase
				liste = "[ ";
				Interprete.operande = true;
				for (int i = 0; i < param.size(); i++) {
					mot = getWord(param.get(i));
					mot2 = param.get(i).trim();
					if (null == mot) {
						if (isList(mot2))
							liste += mot2.substring(1, mot2.length() - 1)
									.trim()
									+ " ";
						else
							liste += mot2 + " ";
					} else
						{	if (mot.equals("")) mot="\\v"; 
							liste += mot + " ";
						}
				}
				Interprete.calcul.push(liste + "] ");
				break;
			case 52: //metspremier
				try {
					liste = getFinalList(param.get(1));
					Interprete.operande = true;
					mot = getWord(param.get(0));
					if (null!=mot&& mot.equals("")) mot="\\v";
					if (null == mot){
						if (!liste.equals(""))
						Interprete.calcul.push("[ "
								+ param.get(0).trim() + " "
								+ liste.trim() + " ] ");
						else 
							Interprete.calcul.push("[ "
									+ param.get(0).trim() + " ] ");
						}
					else{
						if (!liste.equals(""))
						Interprete.calcul.push("[ " + mot + " " + liste.trim()
								+ " ] ");
						else Interprete.calcul.push("[ " + mot	+ " ] ");
					}
				} catch (myException e) {
				}
				break;
			case 53: //metsdernier
				try {
					liste = getFinalList(param.get(1)).trim();
					Interprete.operande = true;
					mot = getWord(param.get(0));
					if (null!=mot && mot.equals("")) mot="\\v";
					if (null == mot){ // Si c'est une liste
							Interprete.calcul.push(("[ " + liste).trim() + " "
								+ param.get(0).trim() + " ] ");
						
					} 
					else
						Interprete.calcul.push(("[ " + liste).trim() +" "+mot+" ] ");
				} catch (myException e) {
				}
				break;
			case 54: // inverse liste
				try {
					liste = getFinalList(param.get(0)).trim();
					Interprete.operande = true;
					StringTokenizer st = new StringTokenizer(liste);
					liste = " ] ";
					String element = "";
					while (st.hasMoreTokens()) {
						element = st.nextToken();
						if (element.equals("["))
							element = extractList(st);
						liste = " " + element + liste;
					}
					Interprete.calcul.push("[" + liste);
				} catch (myException e) {
				}
				break;
			case 55: //choix
				Interprete.operande = true;
				mot = getWord(param.get(0));
				if (null == mot) {
					try {
						liste = getFinalList(param.get(0));
						int nombre = (int) Math.floor(numberOfElements(liste)
								* Math.random()) + 1;
						String tmp=item(liste, nombre);
						if (tmp.equals("\"\\v")) tmp="\"";
						Interprete.calcul.push(tmp);
					} catch (myException e) {
					}
				} else {
					int nombre = (int) Math.floor(Math.random() * getWordLength(mot))+1;
					String st="";
					try{
						st=itemWord(nombre,mot);
						Double.parseDouble(st);
						Interprete.calcul.push(st);
					}
					catch(NumberFormatException e1){
						Interprete.calcul.push(debut_chaine+ st);}
					catch(myException e2){}
					} 
				break;
			case 56: //enleve
				Interprete.operande = true;
				try {
					liste = getFinalList(param.get(1));
					StringTokenizer st = new StringTokenizer(liste);
					liste = "[ ";
					mot = getWord(param.get(0));
					String str;
					if(null!=mot&&mot.equals("")) mot="\\v";
					if (null == mot)
						mot = param.get(0).trim();
					
					while (st.hasMoreTokens()) {
						str = st.nextToken();
						if (str.equals("["))
							str = extractList(st);
						if (!str.equals(mot))
							liste += str + " ";
					}
					Interprete.calcul.push(liste.trim() + " ] ");
				} catch (myException e) {
				}
				break;
			case 57: //item
				Interprete.operande = true;
				try {
					mot = getWord(param.get(1));
					if (null == mot)
						Interprete.calcul.push(item(getFinalList(param.get(1)
								), getInteger(param.get(0))));
					else {
						int i = getInteger(param.get(0));
						if (i < 1 || i > getWordLength(mot))
							throw new myException(cadre, "item n'aime pas "
									+ i + " comme argument pour le mot " + mot
									+ ".");
						else{
							String st= itemWord(i,mot);
							try{
								Double.parseDouble(st);
								Interprete.calcul.push(st);
							}
							catch(NumberFormatException e1){
								Interprete.calcul.push(debut_chaine+st);
							}
						}
					}
				} catch (myException e) {
				}
				break;
			case 58: //saufdernier
				Interprete.operande = true;
				mot = getWord(param.get(0));
				if (null == mot) {
					try {
						liste = getFinalList(param.get(0)).trim();
						String element = item(liste, numberOfElements(liste));
						int longueur = element.length();

						if (element.startsWith("\"")||element.startsWith("["))
							longueur--;
						Interprete.calcul.push("[ "
								+ liste.substring(0, liste.length() - longueur)
								+ "] ");
					} catch (myException e) {
					}
				} 
				else if(mot.equals("")) {
					try{
					throw new myException(cadre,Logo.messages.getString("mot_vide"));}
				catch(myException e1){}	
				} 
				else if (getWordLength(mot) == 1)
					Interprete.calcul.push("\"");
				else {
					String tmp=mot.substring(0, mot.length() - 1);
					if (tmp.endsWith("\\")) tmp=tmp.substring(0, tmp.length() - 1);
					try{
						Double.parseDouble(tmp);
						Interprete.calcul.push(tmp );						
					}
					catch(NumberFormatException e){
						Interprete.calcul.push(debut_chaine+tmp );
					}
				}
				break;
			case 59: //saufpremier
				Interprete.operande = true;
				mot = getWord(param.get(0));
				if (null == mot) {
					try {
						liste = getFinalList(param.get(0)).trim();
						String element = item(liste, 1);
						int longueur = element.length();
						if (element.startsWith("\"")||element.startsWith("["))
							longueur--;
						Interprete.calcul.push("["
								+ liste.substring(longueur, liste.length())
								+ " ] ");
					} catch (myException e) {
					}
				} 
				else if(mot.equals("")) {
					try{
					throw new myException(cadre,Logo.messages.getString("mot_vide"));}
				catch(myException e1){}	
				} 
				else if (getWordLength(mot) == 1)
					Interprete.calcul.push("\"");
				else {
					if (!mot.startsWith("\\")) mot=mot.substring(1);
					else mot=mot.substring(2);
					try{ 
						Double.parseDouble(mot);
						Interprete.calcul.push(mot);}
					catch(NumberFormatException e){
						Interprete.calcul.push(debut_chaine + mot);
					}
				}
				break;
			case 60: //dernier
				Interprete.operande = true;
				mot = getWord(param.get(0));
				if (null == mot) { // Si c'est une liste
					try {
						liste = getFinalList(param.get(0));
						Interprete.calcul
								.push(item(liste, numberOfElements(liste)));
					} catch (myException e) {
					}
				} else if (getWordLength(mot) == 1)
					Interprete.calcul.push(debut_chaine + mot);
				else {
					String st="";
					try{
						st=itemWord(getWordLength(mot),mot);
						Double.parseDouble(st);
						Interprete.calcul.push(st);
					}
					catch(NumberFormatException e1){
						Interprete.calcul.push(debut_chaine + st);
					}
					catch(myException e2){}
				} 
				break;
			case 61: //premier
				Interprete.operande = true;
				mot = getWord(param.get(0));
				if (null == mot) { //SI c'est une liste
					try {
						liste = getFinalList(param.get(0));
			//			System.out.println("b"+item(liste, 1)+"b");
						Interprete.calcul.push(item(liste, 1));
					} catch (myException e) {
					}
				} else if (getWordLength(mot) == 1)
					Interprete.calcul.push(debut_chaine + mot);	
				else{
					String st="";
					try{
						st=itemWord(1,mot);
						Double.parseDouble(st);
						Interprete.calcul.push(st);
					}
					catch(myException e1){}
					catch(NumberFormatException e2){
						Interprete.calcul.push(debut_chaine + st);	
					}
				}
				break;
			case 62: //compte
				Interprete.operande = true;
				mot = getWord(param.get(0));
				if (null == mot) {
					try {
						liste = getFinalList(param.get(0));
						Interprete.calcul.push(String
								.valueOf(numberOfElements(liste)));
					} catch (myException e) {
					}
				} else
					Interprete.calcul.push(String.valueOf(getWordLength(mot)));
				break;
			case 63: //mot?
				mot = getWord(param.get(0));
				if (null == mot)
					Interprete.calcul.push(Logo.messages.getString("faux"));
				else
					Interprete.calcul.push(Logo.messages.getString("vrai"));
				Interprete.operande = true;
				break;
			case 64: //nombre?
				try {
					Double.parseDouble(param.get(0));
					Interprete.calcul.push(Logo.messages.getString("vrai"));
				} catch (NumberFormatException e) {
					Interprete.calcul.push(Logo.messages.getString("faux"));
				}
				Interprete.operande = true;
				break;
			case 65: //liste?
				liste = param.get(0).trim();
				if (isList(liste))
					Interprete.calcul.push(Logo.messages.getString("vrai"));
				else
					Interprete.calcul.push(Logo.messages.getString("faux"));
				Interprete.operande = true;
				break;
			case 66: //vide?
				liste = param.get(0).trim();
				mot = getWord(param.get(0));
				if (null == mot) { //si c'est une liste ou un nombre
					try {
						liste = getFinalList(liste).trim();
						if (liste.equals(""))
							Interprete.calcul.push(Logo.messages
									.getString("vrai"));
						else
							Interprete.calcul.push(Logo.messages
									.getString("faux"));
					} catch (myException e) {
					}
				} else { // Si c'est un mot
					if (mot.equals(""))
						Interprete.calcul.push(Logo.messages.getString("vrai"));
					else
						Interprete.calcul.push(Logo.messages.getString("faux"));
				}
				Interprete.operande = true;
				break;
			case 67: //egal?
				equal(param);
				break;
			case 68: //precede?
				try {
					precede(param);
				} catch (myException e) {
				}
				break;
			case 69: //membre ?
				try {
					membre(param, id);
				} catch (myException e) {
				} 
				break;
			case 70: //racine
				Interprete.operande = true;
				try {
					double nombre = number(param.get(0));
					if (nombre < 0) {
						String racine=Utils.primitiveName("arithmetic.racine");
						throw new myException(cadre, racine + " "
								+ Logo.messages.getString("attend_positif"));
					}
					Interprete.calcul.push(teste_fin_double(Math.sqrt(nombre)));
				} catch (myException e) {
				}
				break;
			case 71: //membre
				try {
					membre(param, id);
				} catch (myException e) {
				}
				break;
			case 72: //donne
				try {
					donne(param);
					Interprete.operande=false;
				} catch (myException e) {
				}
				
				break;
			case 73: //locale
				try {
					locale(param);
					Interprete.operande=false;
				} catch (myException e) {
				}
				break;
			case 74: //donnelocale
				try {
					locale(param);
					donne(param);
					Interprete.operande=false;
				} catch (myException e) {
				}
				break;
			case 75: //fcc
				try {
					Color color=null;
					if (isList(param.get(0))) {
						try {
							color=rgb(param.get(0),Utils.primitiveName("fcc"));
							} catch (myException e) {
						}
					} else {
						int coul=getInteger(param.get(0)) % DrawPanel.defaultColors.length;
						if (coul<0) coul+=DrawPanel.defaultColors.length;
						color=DrawPanel.defaultColors[coul];
					}
					cadre.getArdoise().fcc(color);
				} catch (myException e) {
				}
				break;
			case 76: //fcfg
				try {
					Color color = null;
					if (isList(param.get(0))) {
						try {
							color = rgb(param.get(0),Utils.primitiveName("fcfg"));
						} catch (myException e) {
						}
					} else {
						int coul=getInteger(param.get(0)) % DrawPanel.defaultColors.length;
						if (coul<0) coul+=DrawPanel.defaultColors.length;
						color = DrawPanel.defaultColors[coul];
					}
					cadre.getArdoise().fcfg(color);
				} catch (myException e) {
				}
				break;
			case 77: //hasard
				Interprete.operande = true;
				try {
					int i = getInteger(param.get(0));
					i = (int) Math.floor(Math.random() * i);
					Interprete.calcul.push(String.valueOf(i));
				} catch (myException e) {
				}
				break;
			case 78: //attends
				try {
					int temps = getInteger(param.get(0));
					if (temps < 0) {
						String attends = Utils.primitiveName("attends");
						throw new myException(cadre, attends + " "
								+ Logo.messages.getString("attend_positif"));
					}
					else {
						int nbsecondes = (int) (temps / 60);
						int reste = temps % 60;
						for (int i = 0; i < nbsecondes; i++) {
							Thread.sleep(1000);
							if (cadre.error)
								break;
						}
						if (!cadre.error)
							Thread.sleep(reste*50/3);
					} 
					
				}catch (myException e1) {
				} catch (InterruptedException e2) {
				}
				break;
			case 79: //imts
				for (int i = 0; i < wp.getNumberOfProcedure(); i++) {
					Procedure proc = wp.getProcedure(i);
					if (proc.affichable)
						cadre.ecris("commentaire", proc.name
								+ "\n");
				}
				break;
			case 80: //effacenom
				String nom = getWord(param.get(0));
				for (int i = 0; i < wp.getNumberOfProcedure(); i++) {
					Procedure procedure = wp.getProcedure(i);
					if (procedure.name.equals(nom)
							&& procedure.affichable == true) {
						wp.deleteProcedure(i);
						break;
					}
				}
				break;
			case 81: //effacevariable
				mot = getWord(param.get(0));
				if (!Interprete.locale.isEmpty()) {
					if (Interprete.locale.containsKey(mot)) {
						Interprete.locale.remove(mot);
					}
				} else {
						wp.deleteVariable(mot);
					}

				break;
			case 82: //effacenoms,efns
				wp.deleteAllProcedures();
				wp.deleteAllVariables();
				cadre.setEnabled_New(false);
				break;
			case 83: //mot
				Interprete.operande = true;
				result="";
				for(int i=0;i<param.size();i++){
					mot = getWord(param.get(i));
					if (null == mot) 						
						try {
							throw new myException(cadre, param.get(i) + " "
									+ Logo.messages.getString("error.word"));
						} catch (myException e) {
						}
					result+=mot;
				}
				try{Double.parseDouble(result);}
				catch(NumberFormatException e){
					result="\""+result;
				}
				Interprete.calcul.push(result);
				break;
			case 84: //etiquette
				String par = param.get(0).trim();
				if (isList(par))
					par = formatList(par.substring(1, par.length() - 1));
				mot = getWord(param.get(0));
				if (null == mot)
					cadre.getArdoise().etiquette(Utils.SortieTexte(par));
				else
					cadre.getArdoise().etiquette(Utils.SortieTexte(mot));
				break;
			case 85: ///trouvecouleur
				if (kernel.getActiveTurtle().isVisible())
					cadre.getArdoise().montrecacheTortue(false);
				try {
					liste = getFinalList(param.get(0));
					Color r=cadre.getArdoise().guessColorPoint(liste);
					Interprete.operande = true;
					Interprete.calcul.push("[ " + r.getRed() + " "
							+ r.getGreen() + " " + r.getBlue() + " ] ");
				} catch (myException e) {
				}
				if (kernel.getActiveTurtle().isVisible())
					cadre.getArdoise().montrecacheTortue(true);
				break;
			case 86: //fenetre
				cadre.getArdoise().setWindowMode(DrawPanel.WINDOW_CLASSIC);
				break;
			case 87: //enroule
				cadre.getArdoise().setWindowMode(DrawPanel.WINDOW_WRAP);
				break;
			case 88: //clos
				cadre.getArdoise().setWindowMode(DrawPanel.WINDOW_CLOSE);
				break;
			case 89: // videtexte
				cadre.getHistoryPanel().vide_texte();
				break;
			case 90: // chargeimage
				BufferedImage image = null;
				try {
					image = getImage(param.get(0));
				} catch (myException e) {
				}
				if (null != image)
					cadre.getArdoise().chargeimage(image);
				break;
			case 91: //ftc, fixetaillecrayon
				try {
					double nombre = number(param.get(0));
					if (nombre < 0)
						nombre = Math.abs(nombre);

					kernel.getActiveTurtle().fixe_taille_crayon((float) nombre);
					cadre.getArdoise().setStroke(kernel.getActiveTurtle().crayon);
				} catch (myException e) {
				}
				break;
			case 92: //tantque
				try {
					String li1 = getList(param.get(0));
					li1=new String(Utils.decoupe(li1));
					String li2 = getList(param.get(1));
					li2=new String(Utils.decoupe(li2));
					String instr="\\siwhile "+li1+ "[ " + li2+ "] ";
					bp=new LoopProperties(BigDecimal.ONE,BigDecimal.ZERO,BigDecimal.ONE,instr,"tantque");
					Primitive.stackLoop.push(bp);
					Interprete.instruction.insert(0, instr+"\\ ");
				} catch (myException e) {
				}

				break;
			case 93: //lis
				try {
					liste = getFinalList(param.get(0));
					mot = getWord(param.get(1));
					if (null == mot)
						throw new myException(cadre, Logo.messages
								.getString("error.word"));
					java.awt.FontMetrics fm = cadre.getGraphics()
							.getFontMetrics(Config.police);
					int longueur = fm.stringWidth(liste) + 100;
					Lis lis = new Lis(liste, longueur);
					while (lis.isVisible()) {
						try {
							Thread.sleep(50);
						} catch (InterruptedException e) {
						}
					}
					param = new Stack<String>();
					param.push("\"" + mot);
					String phrase=lis.getText();
					//phrase="[ "+Logo.rajoute_backslash(phrase)+" ] ";
					StringBuffer tampon=new StringBuffer();
					for(int j=0;j<phrase.length();j++){
						char c=phrase.charAt(j);
						if (c=='\\') tampon.append("\\\\");
						else tampon.append(c);
					}
					int offset=tampon.indexOf(" ");
					if(offset!=-1){
						tampon.insert(0,"[ ");
						tampon.append(" ] ");
					}
					else {
						try {
							Double.parseDouble(phrase);
						}
						catch(NumberFormatException e){
							tampon.insert(0,"\"");
						}
					}
					phrase=new String(tampon);
					param.push(phrase);
					donne(param);
					String texte = liste + "\n" + phrase;
					cadre.ecris("commentaire", Utils.SortieTexte(texte) + "\n");
					cadre.focus_Commande();
					lis.dispose();
					cadre.focus_Commande();
				} catch (myException e) {
				}
				break;
			case 94: //touche?
				Interprete.operande = true;
				if (cadre.getCar() != -1)
					Interprete.calcul.push(Logo.messages.getString("vrai"));
				else
					Interprete.calcul.push(Logo.messages.getString("faux"));
				break;
			case 95: //siwhile --> Evalue l'expression test du while
				try {
					liste = getFinalList(param.get(1));
					boolean predicat = predicat(param.get(0));
					kernel.primitive.whilesi(predicat, liste);
				} catch (myException e) {
				}
				break;
			case 96: //liscar
				while (cadre.getCar() == -1) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
					}
					if (myException.lance)
						break;
				}
				Interprete.calcul.push(String.valueOf(cadre.getCar()));
				Interprete.operande = true;
				cadre.setCar(-1);
				break;
			case 97: //remplis
				cadre.getArdoise().remplis();
				break;
			case 98: // point
				if (kernel.getActiveTurtle().isVisible())
					cadre.getArdoise().montrecacheTortue(false);
				try {
					cadre.getArdoise().point(getFinalList(param.get(0)));
				} catch (myException e) {
				}
				if (kernel.getActiveTurtle().isVisible())
					cadre.getArdoise().montrecacheTortue(true);
				break;
			case 99: //vers=towards vers
				try {
					Interprete.operande=true;
					if (DrawPanel.etat_fenetre!=DrawPanel.WINDOW_3D){
						double angle = cadre.getArdoise().vers2D(getFinalList(param.get(0)));
						Interprete.calcul.push(teste_fin_double(angle));
					}
					else{
						double[] orientation=cadre.getArdoise().vers3D(getFinalList(param.get(0)));
						Interprete.calcul.push("[ "+orientation[0]+" "+orientation[1]+" "+orientation[2]+" ] ");
					}
				} catch (myException e) {
				}
				break;
			case 100: //distance
				try {
					Interprete.operande=true;
					double distance = cadre.getArdoise().distance(getFinalList(param.get(0)));
					Interprete.calcul.push(teste_fin_double(distance));
				} catch (myException e) {
				}
				break;
			case 101: //couleurcrayon
				Interprete.operande = true;
				Interprete.calcul.push("[ "
						+ kernel.getActiveTurtle().couleurcrayon.getRed() + " "
						+ kernel.getActiveTurtle().couleurcrayon.getGreen() + " "
						+ kernel.getActiveTurtle().couleurcrayon.getBlue() + " ] ");
				break;
			case 102: //couleurfond
				Interprete.operande = true;
				Color color=cadre.getArdoise().getBackgroundColor();
				Interprete.calcul.push("[ " + color.getRed() + " "
						+ color.getGreen() + " "
						+ color.getBlue() + " ] ");
				break;
			case 103: // bc?
				Interprete.operande = true;
				if (kernel.getActiveTurtle().isPenDown())
					Interprete.calcul.push(Logo.messages.getString("vrai"));
				else
					Interprete.calcul.push(Logo.messages.getString("faux"));
				break;
			case 104: //visible?
				Interprete.operande = true;
				if (kernel.getActiveTurtle().isVisible())
					Interprete.calcul.push(Logo.messages.getString("vrai"));
				else
					Interprete.calcul.push(Logo.messages.getString("faux"));
				break;
			case 105: // prim?
				try{
					Interprete.operande = true;
					mot = getWord(param.get(0));
					if (null == mot)
						throw new myException(cadre, param.get(0) + " "
							+ Logo.messages.getString("error.word"));
					if (Primitive.primitives.containsKey(mot))
					Interprete.calcul.push(Logo.messages.getString("vrai"));
					else
						Interprete.calcul.push(Logo.messages.getString("faux"));
				}
				catch(myException e){}
				break;
			case 106: //proc?
				Interprete.operande = true;
				boolean test = false;
				mot = getWord(param.get(0));
				for (int i = 0; i < wp.getNumberOfProcedure(); i++) {
					if (wp.getProcedure(i).name.equals(mot))
						test = true;
				}
				if (test)
					Interprete.calcul.push(Logo.messages.getString("vrai"));
				else
					Interprete.calcul.push(Logo.messages.getString("faux"));
				break;
			case 107: //exec
				try {
					mot = getWord(param.get(0));
					if (null == mot){
						mot = getList(param.get(0).trim());						
						mot=new String(Utils.decoupe(mot));
					}
					else mot=mot+" ";
					Interprete.instruction.insert(0,mot);
					Interprete.renvoi_instruction=true;
				} catch (myException e) {
				}
				break;
			case 108: //catalogue
				String str=Utils.SortieTexte(Config.defaultFolder);
				File f = new File(str);
				String fichier = "";
				String dossier = "";
				int nbdossier = 0;
				int nbfichier = 0;
				String[] l = f.list();
				for (int i = 0; i < l.length; i++) {
					if ((new File(str + File.separator
							+ l[i])).isDirectory()) {
						nbdossier++;
						if (nbdossier % 5 == 0)
							dossier += l[i] + "\n";
						else
							dossier += l[i] + " ";
					} else {
						nbfichier++;
						if (nbfichier % 5 == 0)
							fichier += l[i] + "\n";
						else
							fichier += l[i] + " ";
					}
				}
				String texte = "";
				if (!dossier.equals(""))
					texte += Logo.messages.getString("repertoires") + ":\n"
							+ dossier + "\n";
				if (!fichier.equals(""))
					texte += Logo.messages.getString("fichiers") + ":\n"
							+ fichier + "\n";
				cadre.ecris("commentaire", texte);
				break;
			case 109: // frepertoire
				try {
					liste = getWord(param.get(0));
					if (null == liste)
						throw new myException(cadre, param.get(0) + " "
								+ Logo.messages.getString("error.word"));
					String chemin = Utils.SortieTexte(liste);
                   			if ((new File(chemin)).isDirectory()&&!chemin.startsWith("..")){
						Config.defaultFolder = Utils.rajoute_backslash(chemin);
					}
					else throw new myException(cadre, liste
								+ " "
								+ Logo.messages
										.getString("erreur_pas_repertoire"));
				} catch (myException e) {
				}
				break;
			case 110: //repertoire
				Interprete.operande = true;
				Interprete.calcul.push("\"" + Config.defaultFolder );
				break;
			case 111: //sauve
				try {
					mot = getWord(param.get(0));
					if (null == mot)
						throw new myException(cadre, Logo.messages
								.getString("error.word"));
					liste = getFinalList(param.get(1));
					StringTokenizer st = new StringTokenizer(liste);
					Stack<String> pile = new Stack<String>();
					while (st.hasMoreTokens())
						pile.push(st.nextToken());
					saveProcedures(mot, pile);
				} catch (myException e) {
				}
				break;
			case 112: //sauved
				try {
					mot = getWord(param.get(0));
					if (null == mot)
						throw new myException(cadre, param.get(0) + " "
								+ Logo.messages.getString("error.word"));
					saveProcedures(mot, null);
				} catch (myException e) {
				}
				break;
			case 113: //ramene
				try {
					mot = getWord(param.get(0));
					if (null == mot)
						throw new myException(cadre, param.get(0) + " "
								+ Logo.messages.getString("error.word"));
					String path=Utils.SortieTexte(Config.defaultFolder) + File.separator + mot;
					try{
						String txt=Utils.readLogoFile(path);
						cadre.editeur.setEditorText(txt);
					}
					catch(IOException e1){
						throw new myException(cadre, 
								Logo.messages.getString("error.iolecture"));}
					try {
						cadre.editeur.analyseprocedure();
						if (!cadre.isEnabled_new())
							cadre.setEnabled_New(true);
					} catch (Exception e3) {
						System.out.println(e3.toString());
					}
					cadre.editeur.setEditorText("");
				} catch (myException e) {
				}

				break;
			case 114: //pi
				Interprete.operande = true;
				Interprete.calcul.push(String.valueOf(Math.PI));
				break;
			case 115: //tangente
				try {
					Interprete.calcul.push(teste_fin_double(Math.tan(Math
							.toRadians(number(param.get(0))))));
				} catch (myException e) {
				}
				Interprete.operande = true;
				break;
			case 116: //acos
				try {
					Interprete.calcul.push(teste_fin_double(Math.toDegrees(Math
							.acos(number(param.get(0))))));
				} catch (myException e) {
				}
				Interprete.operande = true;
				break;
			case 117: //asin
				try {
					Interprete.calcul.push(teste_fin_double(Math.toDegrees(Math
							.asin(number(param.get(0))))));
				} catch (myException e) {
				}
				Interprete.operande = true;

				break;
			case 118: //atan
				try {
					Interprete.calcul.push(teste_fin_double(Math.toDegrees(Math
							.atan(number(param.get(0))))));
				} catch (myException e) {
				}
				Interprete.operande = true;

				break;
			case 119: //vrai
				Interprete.operande = true;
				Interprete.calcul.push(Logo.messages.getString("vrai"));
				break;
			case 120: //faux
				Interprete.operande = true;
				Interprete.calcul.push(Logo.messages.getString("faux"));
				break;
			case 121: //forme
				try{
					primitive2D("turtle.forme");
					Interprete.operande = true;
					Interprete.calcul.push(String.valueOf(kernel.getActiveTurtle().getShape()));
				}
				catch(myException e){}
				break;
			case 122: //fixeforme setshape
				try {
					primitive2D("turtle.fforme");
					int i = getInteger(param.get(0));
					if (kernel.getActiveTurtle().id == 0) {
						Config.activeTurtle = i;
					}
					String chemin = "tortue" + i + ".png";
					kernel.change_image_tortue(chemin);
				} catch (myException e) {
				}
				break;
			case 123: //definis
				try {
					mot = getWord(param.get(0));
					if (null == mot)
						throw new myException(cadre, param.get(0)+ " " + Logo.messages.getString("error.word"));
					if (mot.equals("")) new myException(cadre, Logo.messages.getString("procedure_vide"));
					String liste1 = getFinalList(param.get(1));
					String liste2 = getFinalList(param.get(2));
					StringTokenizer st = new StringTokenizer(liste1);
					String definition = Logo.messages.getString("pour") + " "
							+ mot + " ";
					while (st.hasMoreTokens()) {
						definition += ":" + st.nextToken() + " ";
					}
					definition += "\n" + liste2 + "\n"
							+ Logo.messages.getString("fin");
					cadre.editeur.setEditorText(definition);
				} catch (myException e) {
				}
				try {
					cadre.editeur.analyseprocedure();
					cadre.editeur.setEditorText("");
				} catch (Exception e2) {
				}

				break;

			case 124: //tortue
				Interprete.operande = true;
				Interprete.calcul.push(String.valueOf(kernel.getActiveTurtle().id));
				break;
			case 125: //tortues
				Interprete.operande = true;
				String li = "[ ";
				for (int i = 0; i < cadre.getArdoise().tortues.length; i++) {
					if (null != cadre.getArdoise().tortues[i])
						li += String.valueOf(i) + " ";
				}
				li += "]";
				Interprete.calcul.push(li);
				break;
			case 126: //fixetortue
				try {
					int i = Integer.parseInt(param.get(0));
					if (i > -1 && i < Config.maxTurtles) {
						if (null == cadre.getArdoise().tortues[i]) {
							cadre.getArdoise().tortues[i] = new Turtle(cadre);
							cadre.getArdoise().tortues[i].id = i;
							cadre.getArdoise().tortues[i].setVisible(false);
						}
						cadre.getArdoise().tortue = cadre.getArdoise().tortues[i];
						cadre.getArdoise().setStroke(kernel.getActiveTurtle().crayon);
						String police = cadre.getArdoise().getGraphicsFont().getName();
						cadre.getArdoise()
								.setGraphicsFont(new java.awt.Font(police,
										java.awt.Font.PLAIN,
										(int) kernel.getActiveTurtle().police));

					} else {
						try {
							throw new myException(cadre, Logo.messages
									.getString("tortue_inaccessible"));
						} catch (myException e) {
						}
					}
				} catch (NumberFormatException e) {
					try {
						getInteger(param.get(0));
					} catch (myException e1) {
					}
				}
				break;
			case 127: //police
				Interprete.operande = true;
				Interprete.calcul.push(String.valueOf(kernel.getActiveTurtle().police));
				break;
			case 128: //fixetaillepolice
				try {
					int taille = getInteger(param.get(0));
					kernel.getActiveTurtle().police = taille;
					Font police = Config.police;
					cadre.getArdoise().setGraphicsFont(police
							.deriveFont((float) kernel.getActiveTurtle().police));
				} catch (myException e) {
				}

				break;
			case 129: //tuetortue
				try {
					id = Integer.parseInt(param.get(0));
					if (id > -1 && id < Config.maxTurtles) {
						//On compte le nombre de tortues à l'écran
						int compteur = 0;
						int premier_dispo = -1;
						for (int i = 0; i < Config.maxTurtles; i++) {
							if (null != cadre.getArdoise().tortues[i]) {
								if (i != id && premier_dispo == -1)
									premier_dispo = i;
								compteur++;
							}
						}
						//On vérifie que ce n'est pas la seule tortue
						// dispopnible:
						if (null != cadre.getArdoise().tortues[id]) {
							if (compteur > 1) {
								int tortue_utilisee = kernel.getActiveTurtle().id;
								cadre.getArdoise().tortue = cadre.getArdoise().tortues[id];
								cadre.getArdoise().ct_mt();
								cadre.getArdoise().tortue = cadre.getArdoise().tortues[tortue_utilisee];
								cadre.getArdoise().tortues[id] = null;
								if (cadre.getArdoise().tortues_visibles.search(String
										.valueOf(id)) > 0)
									cadre.getArdoise().tortues_visibles.remove(String
											.valueOf(id));
								if (kernel.getActiveTurtle().id == id) {
									cadre.getArdoise().tortue = cadre.getArdoise().tortues[premier_dispo];
									cadre.getArdoise()
											.setStroke(kernel.getActiveTurtle().crayon); //on
																			 // adapte
																			 // le
																			 // nouveau
																			 // crayon
									String police = cadre.getArdoise().getGraphicsFont()
											.getName();
									cadre.getArdoise()
											.setFont(new java.awt.Font(police,
													java.awt.Font.PLAIN,
													(int) kernel.getActiveTurtle().police));

								}
							} else {
								try {
									throw new myException(cadre, Logo.messages
											.getString("seule_tortue_dispo"));
								} catch (myException e) {
								}
							}
						}
					}
				} catch (NumberFormatException e) {
					try {
						getInteger(param.get(0));
					} catch (myException e1) {
					}
				}
				break;
			case 130: //sequence
				try {
					liste = getFinalList(param.get(0));
					cadre.getSon().cree_sequence(Utils.decoupe(liste).toString());
				} catch (myException e) {
				}

				break;
			case 131: //instrument
				Interprete.operande = true;
				Interprete.calcul.push(String
						.valueOf(cadre.getSon().getInstrument()));
				break;
			case 132: //fixeinstrument
				try {
					int i = getInteger(param.get(0));
					cadre.getSon().setInstrument(i);
				} catch (myException e) {
				}

				break;
			case 133: //joue
				cadre.getSon().joue();
				break;
			case 134: //effacesequence
				cadre.getSon().efface_sequence();
				break;
			case 135: //indexsequence
				Interprete.operande = true;
				double d = (double) cadre.getSon().getTicks() / 64;
				Interprete.calcul.push(teste_fin_double(d));

				break;
			case 136: //fixeindexsequence
				try {
					int i =  getInteger(param.get(0));
					cadre.getSon().setTicks(i * 64);
				} catch (myException e) {
				}
				break;
			case 137://fpt
				try {
					int i =  getInteger(param.get(0));
					cadre.getHistoryPanel().getDsd().fixepolice(i);
				} catch (myException e) {
				}
				break;
			case 138: //ptexte
				Interprete.operande = true;
				Interprete.calcul.push(String.valueOf(cadre.getHistoryPanel()
						.police()));
				break;
			case 139: //fct,fixecouleurtexte
				try {
					if (isList(param.get(0))) {
						cadre.getHistoryPanel().getDsd().fixecouleur(rgb(param.get(0),Utils.primitiveName("fct")));
					} else {
						int coul=getInteger(param.get(0)) % DrawPanel.defaultColors.length;
						if (coul<0) coul+=DrawPanel.defaultColors.length;
						cadre.getHistoryPanel().getDsd().fixecouleur(DrawPanel.defaultColors[coul]);
					}
				} catch (myException e) {
				}
				break;
			case 140: //couleurtexte
				Interprete.operande = true;
				Color c = cadre.getHistoryPanel().getCouleurtexte();
				Interprete.calcul.push("[ " + c.getRed() + " " + c.getGreen()
						+ " " + c.getBlue() + " ] ");
				break;
			case 141: // lissouris
				while (!cadre.getArdoise().get_lissouris()) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
					}
					if (myException.lance)
						break;
				}
				Interprete.calcul.push(String.valueOf(cadre.getArdoise()
						.get_bouton_souris()));
				Interprete.operande = true;
				break;
			case 142: //possouris
				Interprete.calcul.push(cadre.getArdoise().get_possouris());
				Interprete.operande = true;
				break;
			case 143: //msg message
				try {
					liste = getFinalList(param.get(0));
					StringTokenizer st = new StringTokenizer(liste); // On
																	 // découpe
																	 // le
																	 // message
																	 // en
																	 // tranche
																	 // de
																	 // longueurs
																	 // acceptables
					java.awt.FontMetrics fm = cadre.getGraphics()
							.getFontMetrics(Config.police);
					liste = "";
					String tampon = "";
					while (st.hasMoreTokens()) {
						tampon += st.nextToken() + " ";
						if (fm.stringWidth(tampon) > 200) {
							liste += tampon + "\n";
							tampon = "";
						}
					}
					liste += tampon;
					liste=Utils.SortieTexte(liste);
					JTextArea jt = new JTextArea(liste);
					jt.setEditable(false);
					jt.setBackground(new Color(255, 255, 177));
					jt.setFont(Config.police);
					ImageIcon icone = new ImageIcon(Utils.class
							.getResource("icone.png"));
					JOptionPane.showMessageDialog(cadre, jt, "",
							JOptionPane.INFORMATION_MESSAGE, (Icon) icone);
				} catch (myException e) {
				}
				break;
			case 144: //date
				Interprete.operande = true;
				Calendar cal = Calendar.getInstance(Logo
						.generateLocale(Config.langage));
				int jour = cal.get(Calendar.DAY_OF_MONTH);
				int mois = cal.get(Calendar.MONTH) + 1;
				int annee = cal.get(Calendar.YEAR);
				Interprete.calcul.push("[ " + jour + " " + mois + " " + annee
						+ " ] ");
				break;
			case 145: //heure
				Interprete.operande = true;
				cal = Calendar.getInstance(Logo.generateLocale(Config.langage));
				int heure = cal.get(Calendar.HOUR);
				int minute = cal.get(Calendar.MINUTE);
				int seconde = cal.get(Calendar.SECOND);
				Interprete.calcul.push("[ " + heure + " " + minute + " "
						+ seconde + " ] ");
				break;
			case 146: //temps
				Interprete.operande = true;
				long heure_actuelle = Calendar.getInstance().getTimeInMillis();
				Interprete.calcul
						.push(String
								.valueOf((heure_actuelle - Config.heure_demarrage) / 1000));
				break;
			case 147: //debuttemps
				try {
					int temps = getInteger(param.get(0));
					Kernel.chrono = Calendar.getInstance().getTimeInMillis()
							+ 1000 * temps;
				} catch (myException e) {
				}
				break;
			case 148: //fintemps?
				Interprete.operande = true;
				if (Calendar.getInstance().getTimeInMillis() > Kernel.chrono)
					Interprete.calcul.push(Logo.messages.getString("vrai"));
				else
					Interprete.calcul.push(Logo.messages.getString("faux"));
				break;
			case 149: // fnp fixenompolice
				try {
					int int_police = getInteger(param.get(0));
					cadre.getArdoise().police_etiquette = int_police
							% Panel_Font.fontes.length;
				} catch (myException e) {
				}
				break;
			case 150: // np nompolice
				Interprete.operande = true;
				Interprete.calcul.push("[ "
						+ cadre.getArdoise().police_etiquette
						+ " [ "
						+ Panel_Font.fontes[cadre.getArdoise().police_etiquette]
								.getFontName() + " ] ] ");
				break;
			case 151: // fnpt fixenompolicetexte
				try {
					int int_police = getInteger(param.get(0));
					HistoryPanel.fontPrint = int_police
							% Panel_Font.fontes.length;
					cadre.getHistoryPanel().getDsd().fixenompolice(int_police);
				} catch (myException e) {
				}

				break;
			case 152: //npt nompolicetexte
				Interprete.operande = true;
				Interprete.calcul.push("[ "
						+ HistoryPanel.fontPrint
						+ " [ "
						+ Panel_Font.fontes[HistoryPanel.fontPrint]
								.getFontName() + " ] ] ");
				break;
			case 153: //listeflux
				liste = "[ ";
				for (int i = 0; i < Kernel.chemin_flux.size(); i++) {
					liste += "[ " + Kernel.description_flux.get(i)
							+ " " + Kernel.chemin_flux.get(i) + " ] ";
				}
				liste += "] ";
				Interprete.operande = true;
				Interprete.calcul.push(liste);
				break;
			case 154: //lisligneflux
				try {
					int ident = getInteger(param.get(0));
					int index = Kernel.description_flux.search(String
							.valueOf(ident));
					if (index == -1)
						throw new myException(cadre, Logo.messages
								.getString("flux_non_disponible")
								+ " " + ident);
					else {
						BufferedReader bfr;
						int index_pile = Kernel.flux.size() - index;
						if (null == Kernel.flux.get(index_pile))
							bfr = new BufferedReader(
									new FileReader(Kernel.chemin_flux.get(index_pile)));
						else if (Kernel.flux.get(index_pile) instanceof BufferedReader)
							bfr = (BufferedReader) (Kernel.flux.get(index_pile));
						else
							throw new myException(cadre, Logo.messages
									.getString("flux_lecture"));
						if (Kernel.fin_flux.get(index_pile).equals("1")) {
							throw new myException(cadre, Logo.messages
									.getString("fin_flux")
									+ " " + ident);
						} else {
							String ligne = bfr.readLine();
							if (null == ligne) {
								Kernel.fin_flux.set(index_pile, "1");
								throw new myException(cadre, Logo.messages
										.getString("fin_flux")
										+ " " + ident);
							}
							Kernel.flux.set(index_pile, bfr);
							Interprete.operande = true;
							Interprete.calcul.push("[ " + Utils.rajoute_backslash(ligne.trim()) + " ] ");
						}
					}
				} catch (FileNotFoundException e1) {
					try {
						throw new myException(cadre, Logo.messages
								.getString("error.iolecture"));
					} catch (myException e5) {
					}
				} catch (IOException e2) {
				} catch (myException e) {
				}
				break;
			case 155: //liscaractereflux
				try {
					int ident = getInteger(param.get(0));
					int index = Kernel.description_flux.search(String
							.valueOf(ident));
					if (index == -1)
						throw new myException(cadre, Logo.messages
								.getString("flux_non_disponible")
								+ " " + ident);
					else {
						BufferedReader bfr;
						int index_pile = Kernel.flux.size() - index;
						if (null == Kernel.flux.get(index_pile))
							bfr = new BufferedReader(
									new FileReader(Kernel.chemin_flux.get(index_pile)));
						else if (Kernel.flux.get(index_pile) instanceof BufferedReader)
							bfr = (BufferedReader) (Kernel.flux.get(index_pile));
						else
							throw new myException(cadre, Logo.messages
									.getString("flux_lecture"));
						if (Kernel.fin_flux.get(index_pile).equals("1")) {
							throw new myException(cadre, Logo.messages
									.getString("fin_flux")
									+ " " + ident);
						} else {
							int ligne = bfr.read();
							if (ligne == -1) {
								Kernel.fin_flux.set(index_pile, "1");
								throw new myException(cadre, Logo.messages
										.getString("fin_flux")
										+ " " + ident);
							}
							Kernel.flux.set(index_pile, bfr);
							Interprete.operande = true;
							String car=String.valueOf(ligne);
							if (car.equals("\\")) car="\\\\";
							Interprete.calcul.push(car);
						}
					}
				} catch (FileNotFoundException e1) {
					try {
						throw new myException(cadre, Logo.messages
								.getString("error.iolecture"));
					} catch (myException e5) {
					}
				} catch (IOException e2) {
				} catch (myException e) {
				}
				break;
			case 156: //ecrisligneflux
				try {
					int ident = getInteger(param.get(0));
					int index = Kernel.description_flux.search(String
							.valueOf(ident));
					liste = getFinalList(param.get(1));
					if (index == -1)
						throw new myException(cadre, Logo.messages
								.getString("flux_non_disponible")
								+ " " + ident);
					else {
						BufferedWriter bfw;
						int index_pile = Kernel.flux.size() - index;
						if (null == Kernel.flux.get(index_pile))
							bfw = new BufferedWriter(
									new FileWriter(Kernel.chemin_flux.get(index_pile)));
						else if (Kernel.flux.get(index_pile) instanceof BufferedWriter)
							bfw = (BufferedWriter) (Kernel.flux.get(index_pile));
						else
							throw new myException(cadre, Logo.messages
									.getString("flux_ecriture"));
						PrintWriter pw = new PrintWriter(bfw);
						pw.println(Utils.SortieTexte(liste));
						Kernel.flux.set(index_pile, bfw);
					}
				} catch (FileNotFoundException e1) {
				} catch (IOException e2) {
				} catch (myException e) {
				}
				break;
			case 157: //finficher?
				try {
					int ident = getInteger(param.get(0));
					int index = Kernel.description_flux.search(String
							.valueOf(ident));
					if (index == -1)
						throw new myException(cadre, Logo.messages
								.getString("flux_non_disponible")
								+ " " + ident);
					else {
						BufferedReader bfr;
						int index_pile = Kernel.flux.size() - index;
						if (null == Kernel.flux.get(index_pile))
							bfr = new BufferedReader(
									new FileReader(Kernel.chemin_flux.get(index_pile)));
						else if (Kernel.flux.get(index_pile) instanceof BufferedReader)
							bfr = (BufferedReader) (Kernel.flux.get(index_pile));
						else
							throw new myException(cadre, Logo.messages
									.getString("flux_lecture"));
						if (Kernel.fin_flux.get(index_pile).equals("1")) {
							Interprete.operande = true;
							Interprete.calcul.push(Logo.messages
									.getString("vrai"));
						} else {
							bfr.mark(2);
							int ligne = bfr.read();
							if (ligne == -1) {
								Interprete.operande = true;
								Interprete.calcul.push(Logo.messages
										.getString("vrai"));
								Kernel.fin_flux.set(index_pile, "1");
							} else {
								Interprete.operande = true;
								Interprete.calcul.push(Logo.messages
										.getString("faux"));
							}
							bfr.reset();
						}
					}
				} catch (FileNotFoundException e1) {
				} catch (IOException e2) {
				} catch (myException e) {
				}
				break;
			case 158: //ouvreflux
				try {
	
					mot = getWord(param.get(1));
					if (null == mot)
						throw new myException(cadre, param.get(0) + " "
								+ Logo.messages.getString("error.word"));
					liste = Utils.SortieTexte(Config.defaultFolder + File.separator + mot);
					String ident = String.valueOf(getInteger(param.get(0)));
					if (Kernel.description_flux.search(ident) == -1)
						Kernel.description_flux.push(ident);
					else
						throw new myException(cadre, ident + " "
								+ Logo.messages.getString("flux_existant"));
					Kernel.chemin_flux.push(liste);
					Kernel.fin_flux.push("0");
					Kernel.flux.push(null);
				} catch (myException e) {
				}
				break;
			case 159: //fermeflux
				try {
					int ident = getInteger(param.get(0));
					int index = Kernel.description_flux.search(String
							.valueOf(ident));
					if (index == -1)
						throw new myException(cadre, Logo.messages
								.getString("flux_non_disponible")
								+ " " + ident);
					else {
						int index_pile = Kernel.flux.size() - index;
						if (Kernel.flux.get(index_pile) instanceof BufferedReader) { //Le
																				   // flux
																				   // est
																				   // un
																				   // BufferedReader
							BufferedReader bfr = (BufferedReader) (Kernel.flux
									.get(index_pile));
							bfr.close();
						} else if (Kernel.flux.get(index_pile) instanceof BufferedWriter) { //Le
																						  // flux
																						  // est
																						  // un
																						  // BufferedWriter
							BufferedWriter bfw = (BufferedWriter) (Kernel.flux
									.get(index_pile));
							bfw.close();
						}
						Kernel.flux.remove(index_pile);
						Kernel.chemin_flux.remove(index_pile);
						Kernel.description_flux.remove(index_pile);
						Kernel.fin_flux.remove(index_pile);
					}
				} catch (IOException e2) {
				} catch (myException e) {
				}
				break;
			case 160: // ajouteligneflux
				try {
					int ident = getInteger(param.get(0));
					int index = Kernel.description_flux.search(String
							.valueOf(ident));
					liste = getFinalList(param.get(1));
					if (index == -1)
						throw new myException(cadre, Logo.messages
								.getString("flux_non_disponible")
								+ " " + ident);
					else {
						BufferedWriter bfw;
						int index_pile = Kernel.flux.size() - index;
						if (null == Kernel.flux.get(index_pile))
							bfw = new BufferedWriter(
									new FileWriter(Kernel.chemin_flux.get(
											index_pile), true));
						else if (Kernel.flux.get(index_pile) instanceof BufferedWriter)
							bfw = (BufferedWriter) (Kernel.flux.get(index_pile));
						else
							throw new myException(cadre, Logo.messages
									.getString("flux_ecriture"));
						PrintWriter pw = new PrintWriter(bfw);
						pw.println(Utils.SortieTexte(liste));
						Kernel.flux.set(index_pile, bfw);
					}
				} catch (FileNotFoundException e1) {
				} catch (IOException e2) {
				} catch (myException e) {
				}

				break;
			case 161: // souris?
				Interprete.operande = true;
				if (cadre.getArdoise().get_lissouris())
					Interprete.calcul.push(Logo.messages.getString("vrai"));
				else
					Interprete.calcul.push(Logo.messages.getString("faux"));
				break;
			case 162: // listevariables
				Interprete.operande = true;
				liste = "[ ";
				Iterator<String> it=Interprete.locale.keySet().iterator();
				while (it.hasNext()) {
					String name=it.next();
					liste += name + " ";
				}
				it =wp.globale.keySet().iterator();
				while(it.hasNext()){
					String key=it.next();
					if (!Interprete.locale.containsKey(key))
						liste += key.toString() + " ";
				}
				liste += "] ";
				Interprete.calcul.push(liste);
				break;
			case 163: // chose
				mot = getWord(param.get(0));
				try {
					if (null == mot) {
						throw new myException(cadre, Logo.messages
								.getString("error.word"));
					} // si c'est une liste
					else if (debut_chaine.equals("")) {
						throw new myException(cadre, Logo.messages
								.getString("erreur_variable"));
					} // si c'est un nombre
					Interprete.operande = true;
					String value;
					mot = mot.toLowerCase();
					if(!Interprete.locale.containsKey(mot)) {
						if (!wp.globale.containsKey(mot))
							throw new myException(cadre, mot
									+ " "
									+ Logo.messages
											.getString("erreur_variable"));
						else
							value = wp.globale.get(mot);
					} else {
						value = Interprete.locale.get(mot);
					}
					if (null == value)
						throw new myException(cadre, mot + "  "
								+ Logo.messages.getString("erreur_variable"));
					Interprete.calcul.push(value);
				} catch (myException e) {
				}
				break;
			case 164: //nettoie
				cadre.getArdoise().nettoie();
				break;
			case 165: //tape
				par = param.get(0).trim();
				if (isList(par))
					par = formatList(par.substring(1, par.length() - 1));
				mot = getWord(param.get(0));
				if (null == mot)
					cadre.ecris("perso", Utils.SortieTexte(par));
				else
					cadre.ecris("perso", Utils.SortieTexte(mot));
				break;
			case 166: //cercle
				try {
					cadre.getArdoise().circle((number(param.pop())));
				} catch (myException e) {
				}
				break;
			case 167: //arc
				try{
				cadre.getArdoise().arc(number(param.get(0)),number(param.get(1)),number(param.get(2)));
				}
				catch(myException e){}
			break;
			case 168: //rempliszone
				cadre.getArdoise().rempliszone();
				break;
			case 169: //animation
				cadre.getArdoise().setAnimation(true);
				Interprete.operande=false;
			break;
			case 170: //rafraichis
				if (DrawPanel.classicMode==DrawPanel.MODE_ANIMATION){
					cadre.getArdoise().refresh();
				}
				break;
			
			case 171: //tailledessin
   				Interprete.operande=true;
   				StringBuffer sb=new StringBuffer();
   				sb.append("[ ");
   				sb.append(Config.imageWidth);
   				sb.append(" ");
   				sb.append(Config.imageHeight);
   				sb.append(" ] ");
   				Interprete.calcul.push(new String(sb));
				break;
			case 172: //quotient
				try{
					Interprete.operande=true;
					double aa = number(param.get(0));
					double bb = number(param.get(1));
					Interprete.calcul.push(String.valueOf((int)(aa/bb)));
				}
				catch(myException e){}
				
				
				break;
			case 173: //entier?
				Interprete.operande=true;
				try {
					double ent = number(param.get(0));
					if ((int)ent==ent) Interprete.calcul.push(Logo.messages.getString("vrai"));
					else Interprete.calcul.push(Logo.messages.getString("faux"));
					} 
				catch (myException e) {}		
			break;
			case 174: //fixeseparation
				try {
					double nombre = number(param.get(0));
					if (nombre < 0||nombre>1) throw new myException(cadre,nombre+" "+Logo.messages.getString("entre_zero_un"));
					cadre.jSplitPane1.setResizeWeight(nombre);
					cadre.jSplitPane1.setDividerLocation(nombre);
				} catch (myException e) {}
			break;
			case 175: //separation
				Interprete.operande=true;
				Interprete.calcul.push(teste_fin_double(cadre.jSplitPane1.getResizeWeight()));
			break;
			case 176: //tronque
				Interprete.operande=true;
				try {
					BigDecimal ent = numberDecimal(param.get(0));
					Interprete.calcul.push(ent.toBigInteger().toString());
					} 
				catch (myException e) {}
			break;
			case 177: //trace
				Kernel.mode_trace=true;
				Interprete.operande=false;
			break;
			case 178://changedossier
				Interprete.operande=false;
				try {
					mot = getWord(param.get(0));
					if (null == mot)
						throw new myException(cadre, param.get(0) + " "
								+ Logo.messages.getString("error.word"));
					String chemin="";
					if (Config.defaultFolder.endsWith(File.separator)) chemin=Utils.SortieTexte(Config.defaultFolder+mot);
					else chemin = Utils.SortieTexte(Config.defaultFolder+Utils.rajoute_backslash(File.separator)+mot); 
 					if ((new File(chemin)).isDirectory()){
						try{
							Config.defaultFolder=Utils.rajoute_backslash((new File(chemin)).getCanonicalPath());		
						}
						catch(NullPointerException e1){}
						catch(IOException e2){}
					}
					else
						throw new myException(cadre, Utils.rajoute_backslash(chemin)
								+ " "
								+ Logo.messages
										.getString("erreur_pas_repertoire"));
				} catch (myException e) {
				}
				
			break;
			case 179://unicode 
				try{
					mot=getWord(param.get(0));
					if (null == mot)
						throw new myException(cadre, param.get(0) + " "
								+ Logo.messages.getString("error.word"));
					else if (getWordLength(mot)!=1) throw new myException(cadre, param.get(0) + " "
							+ Logo.messages.getString("un_caractere"));
					else {
						Interprete.operande=true;
						String st=String.valueOf((int)Utils.SortieTexte(itemWord(1,mot)).charAt(0));
						Interprete.calcul.push(st);
					}
				}
				catch(myException e){}
			break;
			case 180://caractere 
				try{
					int i=getInteger(param.get(0));
					if (i<0||i>65535) throw new myException(cadre,param.get(0)+" "+Logo.messages.getString("nombre_unicode"));
					else {
						String st="";
						Interprete.operande=true;
						if (i==92) st="\"\\\\";
						else if (i==10) st="\"\\n";
						else if (i==32) st="\"\\e";
						else {
						 st=String.valueOf((char)i);
						 try{
						 	Double.parseDouble(st);
						 }
						 catch(NumberFormatException e){
						 	st="\""+st;
						 }
						}
						Interprete.calcul.push(st);
					}
				}
				catch(myException e){}
			break;
			case 181: //stoptout
				cadre.error=true;
			break;
			case 182: //compteur
				boolean erreur=false;
				if (!Primitive.stackLoop.isEmpty()){
					bp=Primitive.stackLoop.peek();
					if (bp.getId().equals("repete")){
						Interprete.operande=true;
						Interprete.calcul.push(bp.getCompteur().toString());
					}
					else erreur=true;
				}
				else erreur=true;
				if (erreur){
					try{
					throw new myException(cadre,Logo.messages.getString("erreur_compteur"));
					}
					catch(myException e){}
				}
			break;
			case 183: //repetepour
				try{
					String li2 = getList(param.get(1));
					li2=new String(Utils.decoupe(li2));
					String li1 = getFinalList(param.get(0));
					int nb=numberOfElements(li1);
					if (nb<3||nb>4) throw new myException(cadre,Logo.messages.getString("erreur_repetepour"));
					StringTokenizer st=new StringTokenizer(li1);
					String var=st.nextToken().toLowerCase();
					BigDecimal deb=numberDecimal(st.nextToken());
					BigDecimal fin=numberDecimal(st.nextToken());
					BigDecimal increment=BigDecimal.ONE;
					if (nb==4) increment=numberDecimal(st.nextToken());
					if (var.equals("")) throw new myException(cadre,Logo.messages.getString("variable_vide"));
					try{Double.parseDouble(var);
						throw new myException(cadre,Logo.messages.getString("erreur_nom_nombre_variable"));
					}
					catch(NumberFormatException e){
						bp=new LoopProperties(deb,fin,increment,li2,"repetepour",var);
						bp.AffecteVar(true);

						if ((increment.compareTo(BigDecimal.ZERO)==1&&fin.compareTo(deb)>=0)
								||(increment.compareTo(BigDecimal.ZERO)==-1&&fin.compareTo(deb)<=0)){
							Interprete.instruction.insert(0, li2 + "\\ ");
							Primitive.stackLoop.push(bp);
						}
					}
				}
				catch(myException e){}			
			break;	
			case 184: //absolue
				try {
					BigDecimal e=numberDecimal(param.get(0));
					Interprete.calcul.push(e.abs().toString());
					Interprete.operande = true;
				} catch (myException e) {
			}
			break;
			case 185: //remplace
				try{
					String reponse="";
					liste=getFinalList(param.get(0));
					int entier=getInteger(param.get(1));
					mot=getWord(param.get(2));
					if (null!=mot&& mot.equals("")) mot="\\v";
					if (null==mot) mot="[ "+getFinalList(param.get(2))+"]";
					char element;
					int compteur = 1;
					boolean espace=true;
					boolean crochet=false;
					boolean error=true;
					for (int j=0;j<liste.length();j++){
						if (compteur==entier) {error=false;compteur=j;break;}
						element=liste.charAt(j);
						if (element=='['){
							if (espace) crochet=true;
							espace=false;
						}
						if (element==' ') {
							espace=true;
							if (crochet) {
								crochet=false;
								j=extractList(liste,j);
								//System.out.println(j);
							}
							compteur++;
						}
					}
					if (error)
						throw new myException(cadre, Logo.messages.getString("y_a_pas")
								+ " " + entier + " "
								+ Logo.messages.getString("element_dans_liste") + liste
								+ "]");
					reponse="[ "+liste.substring(0,compteur)+mot;
					// On extrait le mot suivant
					if (compteur<liste.length()&&liste.charAt(compteur)=='['&&liste.charAt(compteur+1)==' '){
							compteur=extractList(liste,compteur+2);
							reponse+=liste.substring(compteur)+"] ";
						}
					
					else {
						for (int i=compteur+1;i<liste.length();i++){
							if (liste.charAt(i)==' ') {compteur=i;break;}
						}
						reponse+=liste.substring(compteur)+"] ";
					}				
					Interprete.operande=true;
					Interprete.calcul.push(reponse);
				}
				catch(myException e){}
			break;
			case 186: //ajoute
				try{
					String reponse="";
					liste=getFinalList(param.get(0));
					int entier=getInteger(param.get(1));
					mot=getWord(param.get(2));
					if (null!=mot&& mot.equals("")) mot="\\v";
					if (null==mot) mot="[ "+getFinalList(param.get(2))+"]";
					char element;
					int compteur = 1;
					boolean espace=true;
					boolean crochet=false;
					boolean error=true;
					for (int j=0;j<liste.length();j++){
						if (compteur==entier) {error=false;compteur=j;break;}
						element=liste.charAt(j);
						if (element=='['){
							if (espace) crochet=true;
							espace=false;
						}
						if (element==' ') {
							espace=true;
							if (crochet) {
								crochet=false;
								j=extractList(liste,j);
							}
							compteur++;
							if (j==liste.length()-1&&compteur==entier) {error=false;compteur=liste.length();}
						}
					}
					if (error && entier!=compteur)
						throw new myException(cadre, Logo.messages.getString("y_a_pas")
								+ " " + entier + " "
								+ Logo.messages.getString("element_dans_liste") + liste
								+ "]");
					if (!liste.trim().equals("")) reponse="[ "+liste.substring(0,compteur)+mot+" "+liste.substring(compteur)+"] ";
					else reponse="[ "+mot+" ] ";
					Interprete.operande=true;
					Interprete.calcul.push(reponse);
				}
				catch(myException e){}
			break;
			case 187: // gris
				colorCode(8);
			break;	
			case 188: // grisclair
				colorCode(9);
			break;	
			case 189: // rougefonce
				colorCode(10);
			break;	
			case 190: // vertfonce
				colorCode(11);
			break;	
			case 191: // bleufonce
				colorCode(12);
			break;	
			case 192: // orange
				colorCode(13);
			break;	
			case 193: // rose
				colorCode(14);
			break;	
			case 194: // violet
				colorCode(15);
			break;	
			case 195: // marron
				colorCode(16);
			break;
			case 196: // noir
				colorCode(0);
			break;	
			case 197: // rouge
				colorCode(1);
			break;	
			case 198: // vert
				colorCode(2);
			break;	
			case 199: // jaune
				colorCode(3);
			break;	
			case 200: // bleu
				colorCode(4);
			break;	
			case 201: // magenta
				colorCode(5);
			break;	
			case 202: // cyan
				colorCode(6);
			break;	
			case 203: // blanc
				colorCode(7);
			break;	
			case 204: // Parenthese fermante
				// Distinguons les deux cas : (3)*2 et (4+3)*2
				// Le 3 est ici a retourner au +
				boolean a_retourner = true; 
				//On enleve le "(" correspondant a la parenthese ouvrante de la pile nom
				// a condition que l'element attendant de la pile nom ne soit pas une procedure
				boolean est_procedure=false;
				int pos=Interprete.nom.lastIndexOf("(");
				if (pos==-1){
					// Parenthese fermante sans parenthese ouvrante au prealable
					try{throw new myException(cadre,Logo.messages.getString("parenthese_ouvrante"));}
					catch(myException e){}
				} 
				else { // Evitons l'erreur en cas de par exemple: "ec )" (parenthese fermante sans ouvrante)--> else a executer qu'en cas de non erreur
				if (Interprete.drapeau_ouvrante) {
					// parenthese vide
					try{throw new myException(cadre,Logo.messages.getString("parenthese_vide"));}
					catch(myException e){}
	
				}
				for(int j=pos;j<Interprete.nom.size();j++){
					String proc=Interprete.nom.get(j).toLowerCase();
					if (Primitive.primitives.containsKey(proc)) est_procedure=true;
					else {	
						for (int i=0;i<wp.getNumberOfProcedure();i++){
							if (wp.getProcedure(i).name.equals(proc)) 
								{est_procedure=true;break;}
							}
							if (est_procedure) break;
						}
					}
				}
				// Si une procedure est presente dans la pile nom, on garde les parenteses
//				System.out.println(Primitive.primitives.containsKey("puissance")+" "+est_procedure);
				if (est_procedure) {
					Interprete.instruction.insert(0,") ");
				}
				// Sinon on les enleve avec leurs imbrications eventuelles
					else {
						if (Interprete.en_cours.isEmpty()||!Interprete.en_cours.peek().equals("("))
						{try{throw new myException(cadre,Logo.messages.getString("parenthese_ouvrante"));}
						catch(myException e){}}
					else Interprete.en_cours.pop();
						if (!Interprete.nom.isEmpty()) {
							if (Interprete.nom.peek().equals("(")) a_retourner=false;
							pos=Interprete.nom.lastIndexOf("(");
							if (pos==-1){
								// Parenthese fermante sans parenthese ouvrante au prelable
								try{throw new myException(cadre,Logo.messages.getString("parenthese_ouvrante"));}
								catch(myException e){}
							} 
							else {
								Interprete.nom.removeElementAt(pos);
								// S'il y a imbrication de parentheses (((20)))
								pos--;
								while (Interprete.getNextWord().equals(")")&&(pos>-1)){
									if (!Interprete.nom.isEmpty()&&Interprete.nom.get(pos).equals("(")){
										Interprete.deleteFirstWord(")");
										Interprete.nom.removeElementAt(pos);
										pos--;
									}
									else break;
								}
							}           	
						}
					}
				if (Interprete.calcul.isEmpty()){
                	Interprete.operande=false;
                }
                else {
                	Interprete.operande = true;
                    Interprete.drapeau_fermante=a_retourner;
                }
                break;
                case 205: //fixestyle
                	try{
                   		boolean gras=false;
                		boolean italique=false;
						boolean souligne=false;
						boolean exposant=false;
						boolean indice=false;
						boolean barre=false;
						mot = getWord(param.get(0));
						if (null == mot) liste = getFinalList(param.get(0));
                		else liste=mot;
						if (liste.trim().equals("")) liste=Logo.messages.getString("style.none");
                		StringTokenizer st=new StringTokenizer(liste);
                		while (st.hasMoreTokens()){
                			String element=st.nextToken().toLowerCase();
                			if (element.equals(Logo.messages.getString("style.underline").toLowerCase())){
                				souligne=true;
                			}
                			else if (element.equals(Logo.messages.getString("style.bold").toLowerCase())){
                				gras=true;
                			}
                			else if (element.equals(Logo.messages.getString("style.italic").toLowerCase())){
                				italique=true;
                			}
                			else if (element.equals(Logo.messages.getString("style.exposant").toLowerCase())){
                				exposant=true;
                			}
                			else if (element.equals(Logo.messages.getString("style.subscript").toLowerCase())){
                				indice=true;
                			}
                			else if (element.equals(Logo.messages.getString("style.strike").toLowerCase())){
                				barre=true;
                			}
                			else if (element.equals(Logo.messages.getString("style.none").toLowerCase())) {}
                			else throw new myException(cadre,Logo.messages.getString("erreur_fixestyle"));
                		}
                		cadre.getHistoryPanel().getDsd().fixegras(gras);
                		cadre.getHistoryPanel().getDsd().fixeitalique(italique);
                		cadre.getHistoryPanel().getDsd().fixesouligne(souligne);
                		cadre.getHistoryPanel().getDsd().fixeexposant(exposant);
                		cadre.getHistoryPanel().getDsd().fixeindice(indice);
                		cadre.getHistoryPanel().getDsd().fixebarre(barre);
                	}
                	catch(myException e){}
                break;
                case 206: //style
                	StringBuffer buffer=new StringBuffer();
                	int compteur=0;
                	if (cadre.getHistoryPanel().getDsd().estgras()) {buffer.append(Logo.messages.getString("style.bold").toLowerCase()+" "); compteur++;}
                	if (cadre.getHistoryPanel().getDsd().estitalique()) {buffer.append(Logo.messages.getString("style.italic").toLowerCase()+" "); compteur++;}
                	if (cadre.getHistoryPanel().getDsd().estsouligne()) {buffer.append(Logo.messages.getString("style.underline").toLowerCase()+" "); compteur++;}
                	if (cadre.getHistoryPanel().getDsd().estexposant()) {buffer.append(Logo.messages.getString("style.exposant").toLowerCase()+" "); compteur++;}
                	if (cadre.getHistoryPanel().getDsd().estindice()) {buffer.append(Logo.messages.getString("style.subscript").toLowerCase()+" "); compteur++;}
                	if (cadre.getHistoryPanel().getDsd().estbarre()) {buffer.append(Logo.messages.getString("style.strike").toLowerCase()+" "); compteur++;}
                	Interprete.operande=true;
                	if (compteur==0) Interprete.calcul.push("\""+Logo.messages.getString("style.none").toLowerCase());
                	else if (compteur==1) Interprete.calcul.push("\""+new String(buffer).trim());
                	else if (compteur>1)  Interprete.calcul.push("[ "+new String(buffer)+"]");
                break;
                case 207: // listaillefenetre
                		Interprete.operande=true;
                		java.awt.Point p=cadre.jScrollPane1.getViewport().getViewPosition();
                		Rectangle rec=cadre.jScrollPane1.getVisibleRect();
                		sb=new StringBuffer();
                		int x1=p.x-Config.imageWidth/2;
                		int y1=Config.imageHeight/2-p.y;
                		int x2=x1+rec.width-cadre.jScrollPane1.getVerticalScrollBar().getWidth();
                		int y2=y1-rec.height+cadre.jScrollPane1.getHorizontalScrollBar().getHeight();
                		sb.append("[ ");
                		sb.append(x1);
                		sb.append(" ");
                		sb.append(y1);
                		sb.append(" ");
                		sb.append(x2);
                		sb.append(" ");
                		sb.append(y2);
                		sb.append(" ] ");
                		Interprete.calcul.push(new String(sb));
                	break;
               		case 208: // LongueurEtiquette
        				try {
        					mot=getWord(param.get(0));
        					if (null!=mot) mot=Utils.SortieTexte(mot); 
        					else mot=getFinalList(param.get(0)).trim();
        					Interprete.operande = true;
        					java.awt.FontMetrics fm = cadre.getArdoise().getGraphics()
							.getFontMetrics(cadre.getArdoise().getGraphicsFont());
        					int longueur = fm.stringWidth(mot);
        					Interprete.calcul.push(String.valueOf(longueur));
        				} catch (myException e) {
        				}
               		break;
               		case 209: // envoietcp // enviatcp etcp
               			Interprete.operande=true;
           				mot = getWord(param.get(0));
           				if (null == mot){
           					try{
               					throw new myException(cadre, param.get(0) + " "
               							+ Logo.messages.getString("error.word"));		
           					}
           					catch(myException e){}
           				}
           				mot = mot.toLowerCase();
           				liste="";
           				try{
           					liste=getFinalList(param.get(1));
               				NetworkClientSend ncs=new NetworkClientSend(cadre,mot,liste);
               				Interprete.calcul.push("[ "+ncs.getAnswer() +" ] ");
           				}
           				catch(myException e){}
/*               			try{
          				
             				liste = "[ ";
               				mot2 = getFinalList(param.get(0).toString());
                				liste += mot2 + " ]";
                				String rip = liste.substring(2,17);
//                			cadre.ecris("perso", rip + "\n"); 			//para debug
               				String rdat = "_" + liste.substring(18,23) + "*\n\r";
//               			cadre.ecris("perso", rdat + "\n"); 			//para debug
               				Socket echoSocket = null;
           			        	DataOutputStream tcpout = null;
               				BufferedReader tcpin = null;
              				String resp = null;
    					try {
               	    				echoSocket = new Socket(rip, 1948);
                   					tcpout = new DataOutputStream(echoSocket.getOutputStream());
                   					tcpin= new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
                					tcpout.writeBytes(rdat);
                					resp = tcpin.readLine();
//    readLine detiene el programa hasta que recibe una respuesta del robot. Que hacer si no recibe nada?
                					tcpout.close();
                					tcpin.close();
                					echoSocket.close();
    		                    	} catch (UnknownHostException e) {
    						throw new myException(cadre, Logo.messages.getString("erreur_tcp"));
    					} catch (IOException e) {
    						throw new myException(cadre, Logo.messages.getString("erreur_tcp"));
    					}
               				Interprete.calcul.push("[ " + resp + " ]");
                			} catch(myException e){}*/
           				break;
           			case 210: //ecoutetcp
           				Interprete.operande=false;
           				if (null==savedWorkspace) savedWorkspace=new Stack<Workspace>();
           				savedWorkspace.push(wp);
           				new NetworkServer(cadre);
           				
         				
           			break;
           			case 211: //executetcp
           				mot = getWord(param.get(0));
           				if (null == mot){
           					try{
               					throw new myException(cadre, param.get(0) + " "
               							+ Logo.messages.getString("error.word"));		
           					}
           					catch(myException e){}
           				}
           				mot = mot.toLowerCase();
           				liste="";
           				try{
           					liste=getFinalList(param.get(1));
               				new NetworkClientExecute(cadre,mot,liste);
           				}
           				catch(myException e){}
           			break;
           			case 212: // \x  internal operator to specify
           					  // the end of network instructions with "executetcp"
           					  // have to replace workspace
           				Interprete.operande=false;
           		        kernel.setWorkspace(savedWorkspace.pop());
           			break;
           			case 213: //chattcp
           				Interprete.operande=false;
           				mot = getWord(param.get(0));
           				if (null == mot){
           					try{
               					throw new myException(cadre, param.get(0) + " "
               							+ Logo.messages.getString("error.word"));		
           					}
           					catch(myException e){}
           				}
           				mot = mot.toLowerCase();
           				liste="";
           				try{
           					liste=getFinalList(param.get(1));
               				new NetworkClientChat(cadre,mot,liste);
           				}
           				catch(myException e){}
           			break;
           			case 214: // init
           				Interprete.operande=false;
           				// resize drawing zone if necessary
           				if (Config.imageHeight!=1000||Config.imageWidth!=1000){
           					Config.imageHeight=1000;
           					Config.imageWidth=1000;
           					cadre.resizeDrawingZone();
           				}
           				Config.drawGrid=false;
           				Config.drawXAxis=false;
           				Config.drawYAxis=false;
           				cadre.getArdoise().origine();
    					cadre.getArdoise().setBackgroundColor(Color.WHITE);
    					if (kernel.getActiveTurtle().id == 0) {
    						Config.activeTurtle = 0;
    					}
    					DrawPanel.etat_fenetre=DrawPanel.WINDOW_CLASSIC;
   						String chemin = "tortue0.png";
   						kernel.change_image_tortue(chemin);
    					cadre.getArdoise().fcfg(Color.WHITE);
    					cadre.getArdoise().fcc(Color.BLACK);
     					cadre.getArdoise().setAnimation(false);
    					Config.police=new Font("dialog",Font.PLAIN,12);
   						kernel.getActiveTurtle().police = 12;
   						cadre.getArdoise().setGraphicsFont(Config.police);
 						HistoryPanel.fontPrint = Panel_Font.police_id(Config.police);
   						cadre.getHistoryPanel().getDsd().fixepolice(12);
   						cadre.getHistoryPanel().getDsd().fixenompolice(HistoryPanel.fontPrint);
   						cadre.getHistoryPanel().getDsd().fixecouleur(Color.black);
   						Config.penShape=0;
       					Config.quality=0;
       					kernel.setDrawingQuality(Config.quality);
     					kernel.setNumberOfTurtles(16);
						Config.turtleSpeed=0;
						Kernel.mode_trace=false;
						DrawPanel.etat_fenetre=DrawPanel.WINDOW_CLASSIC;
           			break;
           			case 215: //tc taillecrayon
           				Interprete.operande=true;
           				double penwidth=2*kernel.getActiveTurtle().getPenWidth();
           				Interprete.calcul.push(String.valueOf(teste_fin_double(penwidth)));
           			break;
           			case 216: //setpenshape=ffc fixeformecrayon
           				Interprete.operande=false;
           				try{
           					int i=getInteger(param.get(0));
           					if (i!=Config.PEN_SHAPE_OVAL&&i!=Config.PEN_SHAPE_SQUARE){
           						String st=Utils.primitiveName("setpenshape")+" "+Logo.messages.getString("error_bad_values");
           						st+=" "+Config.PEN_SHAPE_SQUARE+" "+Config.PEN_SHAPE_OVAL;
           						throw new myException(cadre,st);
           					}
           					Config.penShape=i;
           				}
           				catch(myException e){}
           			break;
           			case 217: //penshape=fc formecrayon
           				Interprete.operande=true;
           				Interprete.calcul.push(String.valueOf(Config.penShape));
           			break;
           			case 218: //setdrawingquality=fqd fixequalitedessin
           				Interprete.operande=false;
           				try{
           					int i=getInteger(param.get(0));
           					if (i!=Config.QUALITY_NORMAL&&i!=Config.QUALITY_HIGH&&i!=Config.QUALITY_LOW){
           						String st=Utils.primitiveName("setdrawingquality")+" "+Logo.messages.getString("error_bad_values")+" 0 1 2";
           						throw new myException(cadre,st);
           					}
           					Config.quality=i;
           					kernel.setDrawingQuality(Config.quality);
           				}
           				catch(myException e){}
           				break;
           			case 219: //drawingquality=qd qualitedessin
           				Interprete.operande=true;
           				Interprete.calcul.push(String.valueOf(Config.quality));
           				break;
           			case 220: //setturtlesnumber=fmt fixemaxtortues
           				Interprete.operande=false;
           				try{
           					int i=getInteger(param.get(0));
           					if (i<0){
        						String fmt = Utils.primitiveName("setturtlesnumber");
        						throw new myException(cadre, fmt + " "
        								+ Logo.messages.getString("attend_positif"));
           					}
           					else if (i==0) i=1;
           					kernel.setNumberOfTurtles(i);
           				}
           				catch(myException e){}
           			break;
           			case 221: //turtlesnumber=maxtortues
           				Interprete.operande=true;
           				Interprete.calcul.push(String.valueOf(Config.maxTurtles));

           				break;
           			case 222: //setscreensize=ftd fixetailledessin
           				Interprete.operande=false;
           				try{
           					String prim=Utils.primitiveName("setscreensize");
           					liste=getFinalList(param.get(0));
           					int width,height;
           					StringTokenizer st = new StringTokenizer(liste);
           					try {
           						if (!st.hasMoreTokens())
           							throw new myException(cadre, prim
           									+ " " + Logo.messages.getString("n_aime_pas") + liste
           									+ Logo.messages.getString("comme_parametre"));
           						width = Integer.parseInt(st.nextToken().toString());
           						if (!st.hasMoreTokens())
           							throw new myException(cadre, prim
           									+ " " + Logo.messages.getString("n_aime_pas") + liste
           									+ Logo.messages.getString("comme_parametre"));
           						height = Integer.parseInt(st.nextToken().toString());
           					} catch (NumberFormatException e) {
           						throw new myException(cadre, prim
           								+ " " + Logo.messages.getString("n_aime_pas") + liste
           								+ Logo.messages.getString("comme_parametre"));
           					}
           					if (st.hasMoreTokens())
           						throw new myException(cadre, prim
           								+ " " + Logo.messages.getString("n_aime_pas") + liste
           								+ Logo.messages.getString("comme_parametre"));
           					boolean changement=false;
       						if (height!=Config.imageHeight) changement=true;
           					int tmp_hauteur=Config.imageHeight;
           					Config.imageHeight=height;
           					if (width!=Config.imageWidth) changement=true;
           					int tmp_largeur=Config.imageWidth;
           					Config.imageWidth=width;
       						if (Config.imageWidth<100 || Config.imageHeight<100) {
           							Config.imageWidth=1000;
           							Config.imageHeight=1000;
           					}
           					if (changement) {
           						int memoire_necessaire=Config.imageWidth*Config.imageHeight*4/1024/1024;
           						int memoire_image=tmp_hauteur*tmp_largeur*4/1024/1024;
           						long free=Runtime.getRuntime().freeMemory()/1024/1024;
           						long total=Runtime.getRuntime().totalMemory()/1024/1024;
           				/*		System.out.println("memoire nécessaire "+memoire_necessaire);
           						System.out.println("memoire image "+memoire_image);
           						System.out.println("memoire libre "+free);
           						System.out.println("memoire totale "+total);
           						System.out.println("memoire envisagee "+(total-free+memoire_necessaire-memoire_image));
           						System.out.println();*/
           						if (total-free+memoire_necessaire-memoire_image<Config.memoire*0.8){
           							cadre.resizeDrawingZone();
           						}
           						else{
           							Config.imageWidth=tmp_largeur;
           							Config.imageHeight=tmp_hauteur;
       								long conseil=64*((total-free+memoire_necessaire-memoire_image)/64)+64;
       								if (total-free+memoire_necessaire-memoire_image>0.8*conseil) conseil+=64;
       								if (conseil==Config.memoire) conseil+=64;
       								String message=Logo.messages.getString("erreur_memoire")+" "+conseil+"\n"+Logo.messages.getString("relancer");
       								JTextArea jt=new JTextArea(message);
       								jt.setEditable(false);
       								jt.setBackground(new Color(255,255,177));
       								jt.setFont(Config.police);
       								JOptionPane.showMessageDialog(cadre,jt,Logo.messages.getString("erreur"),JOptionPane.ERROR_MESSAGE);
           						}
           					}
           				}
           				catch(myException e){}
           			break;
           			case 223: //guibutton guibouton
           				try{
           					String ident=getWord(param.get(0));
           					if (null==ident)
        						throw new myException(cadre, param.get(0) + " "
        								+ Logo.messages.getString("error.word"));
        					mot=getWord(param.get(1));	
           					if (null==mot)
        						throw new myException(cadre, param.get(1) + " "
        								+ Logo.messages.getString("error.word"));
           					GuiButton gb=new GuiButton(ident.toLowerCase(),mot,cadre);
           					cadre.getArdoise().addToGuiMap(gb);
           				}
           				catch(myException e){}
           			break;
           			case 224: //guiaction
           				try{
           					String ident=getWord(param.get(0));
           					if (null==ident)
           						throw new myException(cadre, param.get(0) + " "
    								+ Logo.messages.getString("error.word"));
           					liste=getFinalList(param.get(1));
           					cadre.getArdoise().guiAction(ident,liste);
           				}
           				catch(myException e){}
           			break;
           			case 225: //guiremove
           				try{
           					String ident=getWord(param.get(0));
           					if (null==ident)
           						throw new myException(cadre, param.get(0) + " "
    								+ Logo.messages.getString("error.word"));
           					cadre.getArdoise().guiRemove(ident);
           				}
           				catch(myException e){}
           				
           			break;
           			case 226: //guiposition
           				try{
           					String ident=getWord(param.get(0));
           					if (null==ident)
        						throw new myException(cadre, param.get(0) + " "
        								+ Logo.messages.getString("error.word"));
        					liste=getFinalList(param.get(1));
        					cadre.getArdoise().guiposition(ident,liste,Utils.primitiveName("guiposition"));
           				}
           				catch(myException e){}
           			break;
           			case 227: //guidraw
           				try{
           					String ident=getWord(param.get(0));
           					if (null==ident)
        						throw new myException(cadre, param.get(0) + " "
        								+ Logo.messages.getString("error.word"));
           					cadre.getArdoise().guiDraw(ident);
           				}
           				catch(myException e){}
           			break;
           			case 228: //zoom
           				Interprete.operande = false;
           				try{
           					d=number(param.get(0));
           					if (d < 0) {
        						String name=Utils.primitiveName("zoom");
        						throw new myException(cadre, name + " "
        								+ Logo.messages.getString("attend_positif"));
        					}
           					cadre.getArdoise().zoom(d);
           				}
           				catch(myException e){}
           			break;
           			case 229: //grille
           				Interprete.operande = false;
        				try {
        					primitive2D("grille");
        					int[] args=new int[2];
        					for (int i=0;i<2;i++){
        						args[i] = getInteger(param.get(i));
        						if (args[i] < 0) {
        							String grille=Utils.primitiveName("grille");
        							throw new myException(cadre, grille + " "
        								+ Logo.messages.getString("attend_positif"));
        						}
        						else if (args[i]==0){
        							args[i]=1;
        						}
        					}
        					Config.drawGrid=true;
        					Config.XGrid=args[0];
        					Config.YGrid=args[1];
        					cadre.getArdoise().videecran();
        					
        				} catch (myException e) {
        				}
           			break;
           			case 230: //stopgrille
           				Interprete.operande=false;
    					Config.drawGrid=false;
    					cadre.getArdoise().videecran();
           			break;
           			case 231: //stopanimation
        				cadre.getArdoise().setAnimation(false);
        				Interprete.operande=false;
           			break;
           			case 232: //stoptrace
           				Kernel.mode_trace=false;
        				Interprete.operande=false;
           			break;
           			case 233: //guimenu
           				try{
           					String ident=getWord(param.get(0));
           					if (null==ident)
        						throw new myException(cadre, param.get(0) + " "
        								+ Logo.messages.getString("error.word"));
        					liste=getFinalList(param.get(1));	
           					GuiMenu gm=new GuiMenu(ident.toLowerCase(),liste,cadre);
        					cadre.getArdoise().addToGuiMap(gm);
           				}
           				catch(myException e){}
           			break;
           			case 234: //axis
           				
        				Interprete.operande = false;
        				try {
        					primitive2D("axis");
        					int nombre = getInteger(param.get(0));
        					if (nombre < 0) {
        						String name=Utils.primitiveName("axis");
        						throw new myException(cadre, name + " "
        								+ Logo.messages.getString("attend_positif"));
        					}
        					else if (nombre<25) nombre=25;
        					Config.drawXAxis=true;
        					Config.XAxis=nombre;
        					Config.drawYAxis=true;
        					Config.YAxis=nombre;
        					cadre.getArdoise().videecran();
        				} catch (myException e) {
        				}
               			break; 
               		case 235: //xaxis
        				Interprete.operande = false;
        				try {
        					primitive2D("xaxis");
        					int nombre = getInteger(param.get(0));
        					if (nombre < 0) {
        						String name=Utils.primitiveName("xaxis");
        						throw new myException(cadre, name + " "
        								+ Logo.messages.getString("attend_positif"));
        					}
        					else if (nombre<25) nombre=25;
        					Config.drawXAxis=true;
        					Config.XAxis=nombre;
        					cadre.getArdoise().videecran();
        				} catch (myException e) {
        				}
               			break; 
                   	case 236: //yaxis
        				Interprete.operande = false;
        				try {
        					primitive2D("yaxis");
        					int nombre = getInteger(param.get(0));
        					if (nombre < 0) {
        						String name=Utils.primitiveName("yaxis");
        						throw new myException(cadre, name + " "
        								+ Logo.messages.getString("attend_positif"));
        					}
        					else if (nombre<25) nombre=25;
        					Config.drawYAxis=true;
        					Config.YAxis=nombre;
        					cadre.getArdoise().videecran();
        				} catch (myException e) {
        				}
               			break;
                    case 237: //stopaxis
                    	Config.drawXAxis=false;
                    	Config.drawYAxis=false;
                    	Interprete.operande=false;
                    	cadre.getArdoise().videecran();
                      break;
                    case 238: //bye
                    	cadre.closeWindow();	
                    break;
                    case 239: //var? variable?
        				try{
        					Interprete.operande = true;
        					mot = getWord(param.get(0));
        					if (null == mot)
        						throw new myException(cadre, param.get(0) + " "
        							+ Logo.messages.getString("error.word"));
        					if (wp.globale.containsKey(mot)||Interprete.locale.containsKey(mot))
        					Interprete.calcul.push(Logo.messages.getString("vrai"));
        					else
        						Interprete.calcul.push(Logo.messages.getString("faux"));
        				}
        				catch(myException e){}
                     break;
                    case 240: //axiscolor= couleuraxes
        				Interprete.operande = true;
        				c=new Color(Config.axisColor);
        				Interprete.calcul.push("[ " + c.getRed() + " " + c.getGreen()
        						+ " " + c.getBlue() + " ] ");
                  
                       break;
                    case 241: // gridcolor=couleurgrille
        				Interprete.operande = true;
        				c=new Color(Config.gridColor);
        				Interprete.calcul.push("[ " + c.getRed() + " " + c.getGreen()
        						+ " " + c.getBlue() + " ] ");
                        break;
                    case 242: // grid?=grille?
         					Interprete.operande = true;
        					if (Config.drawGrid)
        					Interprete.calcul.push(Logo.messages.getString("vrai"));
        					else
        						Interprete.calcul.push(Logo.messages.getString("faux"));
        				break;
                    case 243: // xaxis?=axex?
       					Interprete.operande = true;
    					if (Config.drawXAxis)
    					Interprete.calcul.push(Logo.messages.getString("vrai"));
    					else
    						Interprete.calcul.push(Logo.messages.getString("faux"));
                        break;
                    case 244: // yaxis?=axey?
       					Interprete.operande = true;
    					if (Config.drawYAxis)
    					Interprete.calcul.push(Logo.messages.getString("vrai"));
    					else
    						Interprete.calcul.push(Logo.messages.getString("faux"));
                        break;
                    case 245: // setgridcolor=fcg fixecouleurgrille
        				Interprete.operande = false;
                    	try {
        					if (isList(param.get(0))) {
        						Config.gridColor=rgb(param.get(0),Utils.primitiveName("setgridcolor")).getRGB();
        					} else {
        						int coul=getInteger(param.get(0)) % DrawPanel.defaultColors.length;
        						if (coul<0) coul+=DrawPanel.defaultColors.length;
        						Config.gridColor=DrawPanel.defaultColors[coul].getRGB();
        					}
        				} catch (myException e) {
        				}
        				break;
                    case 246: // setaxiscolor=fca fixecouleuraxes
                    	Interprete.operande = false;
                    	try {
        					if (isList(param.get(0))) {
        						Config.axisColor=rgb(param.get(0),Utils.primitiveName("setaxiscolor")).getRGB();
        					} else {
        						int coul=getInteger(param.get(0)) % DrawPanel.defaultColors.length;
        						if (coul<0) coul+=DrawPanel.defaultColors.length;
        						Config.axisColor=DrawPanel.defaultColors[coul].getRGB();
        					}
        				} catch (myException e) {
        				}
                    	break;
                    case 247: //perspective

                    cadre.getArdoise().perspective();
                    
                    break;
                    case 248://rightroll=rd roulisdroite
        				delay();
        				try {
        					primitive3D("3d.rightroll");
        					cadre.getArdoise().rightroll(number(param.pop()));
        				} catch (myException e) {
        				}
                    break;
                    case 249://uppitch=cabre
        				delay();
        				try {
        					primitive3D("3d.uppitch");
        					cadre.getArdoise().uppitch(number(param.pop()));
        				} catch (myException e) {
        				}
                    	break;
                    case 250://leftroll=rg roulisgauche
        				delay();
        				try {
        					primitive3D("3d.leftroll");
        					cadre.getArdoise().rightroll(-number(param.pop()));
        				} catch (myException e) {
        				}
                    	break;
                    case 251://downpitch=pique
        				delay();
        				try {
        					primitive3D("3d.downpitch");
        					cadre.getArdoise().uppitch(-number(param.pop()));
        				} catch (myException e) {
        				}
                    	break;
                    case 252://roll=roulis
                    	try{
                    		primitive3D("3d.roll");
            				Interprete.operande = true;
            				Interprete.calcul.push(teste_fin_double(kernel.getActiveTurtle().roll));
                    	}
                    	catch(myException e){}
                        break;
                    case 253://pitch=cabrement tangage
                    	try{
                    		primitive3D("3d.pitch");
                    		Interprete.operande = true;
                    		Interprete.calcul.push(teste_fin_double(kernel.getActiveTurtle().pitch));
                    	}
                    	catch(myException e){}
                    		break;
                    case 254://setroll=fixeroulis
                    	try{
                    		primitive3D("3d.setroll");
            				delay();
            					cadre.getArdoise().setRoll(number(param.pop()));
            					}
                    	catch(myException e){}
                        break;
                    case 255://setpitch=fixetangage
                    	try{
                    		primitive3D("3d.setpitch");
            				delay();
        					cadre.getArdoise().setPitch(number(param.pop()));
                    	}
                    	catch(myException e){}
                    	break;
                    case 256://setorientation=fixeorientation
                    	try{
                    		delay();
                    		cadre.getArdoise().setOrientation(getFinalList(param.pop()));
                    		primitive3D("3d.setorientation");
                    	}
                    	catch(myException e){}
                    	break;
                    case 257: //orientation=orientation
                    	try{
                    		primitive3D("3d.orientation");
                    		Interprete.operande = true;
                    		String pitch=teste_fin_double(kernel.getActiveTurtle().pitch);
                    		String roll=teste_fin_double(kernel.getActiveTurtle().roll);
                    		String heading=teste_fin_double(kernel.getActiveTurtle().heading);
                    		Interprete.calcul.push("[ "+roll+" "+pitch+" "+heading+" ] ");            	
                    	}
                    	catch(myException e){}
                    	break;
                    case 258: //setxyz=fposxyz
                    	try{
                    		primitive3D("3d.setxyz");
        					cadre.getArdoise().fpos(number(param.get(0)) + " "
        							+ number(param.get(1))+" "+
        							number(param.get(2)));
                    	}
                    	catch(myException e){}
                    	break;
                    case 259: //setz=fixez
           				delay();
        				try {
        					primitive3D("3d.setz");
        					cadre.getArdoise().fpos(kernel.getActiveTurtle().X+" "+kernel.getActiveTurtle().Y
        							+" "+number(param.get(0)));
        						
        				} catch (myException e) {
        				}
        				break;
                    case 260: //pprop=dprop
                    	Interprete.operande=false;
                    	try{
                    		mot=getWord(param.get(0));
                    		if (null==mot) throw new myException(cadre,param.get(0)+" "+Logo.messages.getString("error.word"));
                    		mot2=getWord(param.get(1));
                    		if (null==mot2) throw new myException(cadre,param.get(1)+" "+Logo.messages.getString("error.word"));
                    		wp.addPropList(mot, mot2, param.get(2));
                    	}
                		catch(myException e){}
                    	break;
                    case 261: //gprop=rprop
                    	try{
                    	Interprete.operande=true;
                    	mot=getWord(param.get(0));
                		if (null==mot) throw new myException(cadre,param.get(0)+" "+Logo.messages.getString("error.word"));
                		mot2=getWord(param.get(1));
                		if (null==mot2) throw new myException(cadre,param.get(1)+" "+Logo.messages.getString("error.word"));
                		String value=wp.getPropList(mot, mot2);
                		if (value.startsWith("[")) value+=" ";
                		Interprete.calcul.push(value);
                    	}
                    	catch(myException e){}
                		break;
                    case 262: // remprop=efprop
                      	try{
                        	Interprete.operande=false;
                        	mot=getWord(param.get(0));
                    		if (null==mot) throw new myException(cadre,param.get(0)+" "+Logo.messages.getString("error.word"));
                    		mot2=getWord(param.get(1));
                    		if (null==mot2) throw new myException(cadre,param.get(1)+" "+Logo.messages.getString("error.word"));
                    		wp.removePropList(mot, mot2);
                    		}
                        	catch(myException e){}
                    	break;
                    case 263: //plist=lprop
                    	try{
                        	Interprete.operande=true;
                        	mot=getWord(param.get(0));
                    		if (null==mot) throw new myException(cadre,param.get(0)+" "+Logo.messages.getString("error.word"));
                    		Interprete.calcul.push(wp.displayPropList(mot));
                    		}
                        	catch(myException e){}
                    	
                    	break;
                    case 264: // polystart=polydef
                    	DrawPanel.record3D=DrawPanel.record3D_POLYGON;
                    	if (null==DrawPanel.listPoly) DrawPanel.listPoly=new java.util.Vector<Shape3D>();
                    	DrawPanel.poly=new ElementPolygon(cadre);
                    	break;
                    case 265: // polyend=polyfin
                    	DrawPanel.record3D=DrawPanel.record3D_NONE;
                    	try{
                    	DrawPanel.poly.addToList();
                    	}
                    	catch(myException e){}
                    	break;
                    case 266: //polyview=polyaf vue3d
                    	cadre.getArdoise().polyView();
                    	break;
                    case 267: // linestart=lignedef
                    	DrawPanel.record3D=DrawPanel.record3D_LINE;
                    	if (null==DrawPanel.listPoly) DrawPanel.listPoly=new java.util.Vector<Shape3D>();
                    	DrawPanel.poly=new ElementLine(cadre);
                    	DrawPanel.poly.addVertex(new Point3d(kernel.getActiveTurtle().X/1000,
                    			kernel.getActiveTurtle().Y/1000,
                    			kernel.getActiveTurtle().Z/1000),kernel.getActiveTurtle().couleurcrayon);
                    	break;
                    case 268: // lineend=lignefin
                    	DrawPanel.record3D=DrawPanel.record3D_NONE;
                    	try{
                    		DrawPanel.poly.addToList();
                    	}
                    	catch(myException e){}
                    	break;
                    case 269: // pointstart=pointdef
                    	DrawPanel.record3D=DrawPanel.record3D_POINT;
                    	if (null==DrawPanel.listPoly) DrawPanel.listPoly=new java.util.Vector<Shape3D>();
                    	DrawPanel.poly=new ElementPoint(cadre);
                    	break;
                    case 270: // pointend=pointfin
                    	DrawPanel.record3D=DrawPanel.record3D_NONE;
                    	try{
                    		DrawPanel.poly.addToList();
                    	}
                    	catch(myException e){}
                    	break;
                    case 271: // textstart=textedef
                    	DrawPanel.record3D=DrawPanel.record3D_TEXT;
                    	if (null==DrawPanel.listText) DrawPanel.listText=new java.util.Vector<TransformGroup>();
                    	DrawPanel.poly=null;
                    	break;
                    case 272: // textend=textefin
                    	DrawPanel.record3D=DrawPanel.record3D_NONE;
                    	break;
			}
		}
	}
	/**
	 * This method tests if the primitive name exist in 2D mode
	 * @param name The primitive name
	 * @throws myException
	 */
	private void primitive2D(String name) throws myException{
		if (DrawPanel.etat_fenetre==DrawPanel.WINDOW_3D) 
			throw new myException(cadre,Utils.primitiveName(name)+" "+Logo.messages.getString("error.primitive2D"));
	}
	/**
	 * This method tests if the primitive name exist in 2D mode
	 * @param name The primitive name
	 * @throws myException
	 */
	private void primitive3D(String name) throws myException{
		if (DrawPanel.etat_fenetre!=DrawPanel.WINDOW_3D) 
			throw new myException(cadre,Utils.primitiveName(name)+" "+Logo.messages.getString("error.primitive3D"));
	}
	/**
	 * Returns the code [r g b] for the color i
	 * @param i Integer representing the Color
	 */
	private void colorCode(int i){
		Interprete.operande=true;
		Color co=DrawPanel.defaultColors[i];
		Interprete.calcul.push("[ "+co.getRed()+" "+co.getGreen()+" "+co.getBlue()+" ]");
	}
	
/**
 * 	Save all procedures whose name are contained in the Stack pile
 * @param fichier The patch to the saved file
 * @param pile Stack Stack containing all procedure names
 */
	private void saveProcedures(String fichier, Stack<String> pile) {//throws myException {
		try {
			String aecrire = "";
			boolean bool = true;
			if (!fichier.endsWith(".lgo"))
				fichier += ".lgo";
			String path=Utils.SortieTexte(Config.defaultFolder)+ File.separator + fichier;
			try {
				for (int i = 0; i < wp.getNumberOfProcedure(); i++) {
					Procedure procedure = wp.getProcedure(i);
					if (null == pile)
						bool = true;
					else
						bool = (pile.search(procedure.name) != -1);
					if (procedure.affichable && bool) {
						aecrire += Logo.messages.getString("pour") + " "
								+ procedure.name;
						for (int j = 0; j < procedure.nbparametre; j++) {
							aecrire += " :"
									+ procedure.variable.get(j);
						}
						aecrire += "\n" + procedure.instruction
								+ Logo.messages.getString("fin") + "\n\n";
					}
				}
			} catch (NullPointerException ex) {
			} //Si aucune procédure n'a été définie.
			Utils.writeLogoFile(path,aecrire);
		} catch (IOException e2) {
			cadre.ecris("erreur", Logo.messages
					.getString("error.ioecriture"));
		}
	}

/**
 * 	Returns the Image defined by the path "chemin"
 * @param chemin The absolute path for the image
 * @return  BufferedImage defined by the path "chemin"
 * @throws myException If Image format isn't valid(jpg or png)
 */	private BufferedImage getImage(String chemin) throws myException {
		BufferedImage image = null;
		chemin = getWord(chemin);
		if (!(chemin.endsWith(".png") || chemin.endsWith(".jpg")))
			throw new myException(cadre, Logo.messages
					.getString("erreur_format_image"));
		else {
			try {
				chemin = Utils.SortieTexte(chemin);
				File f = new File(Utils.SortieTexte(Config.defaultFolder)+File.separator+chemin);
				image = ImageIO.read(f);
			} catch (Exception e1) {
				throw new myException(cadre, Logo.messages
						.getString("error.iolecture"));
			}
		}
		return image;
	}
 /**
  * Create a local variable called "mot" with no value.
  * @param mot Variable name
  */
	private void createLocaleName(String mot){
		mot=mot.toLowerCase();
		if (!Interprete.locale.containsKey(mot)) {
			Interprete.locale.put(mot,null);
		}
	}
	/**
	 *  Create a new local variable
	 * @param param The variable name or a list of variable names
	 * @throws myException If "param" isn't a list containing all variable names, or a word 
	 */
	
	private void locale(Stack<String> param) throws myException {
		String li=param.get(0);
		if (LaunchPrimitive.isList(li)){
			li=getFinalList(li);
			StringTokenizer st=new StringTokenizer(li);
			while (st.hasMoreTokens()){
				String item=st.nextToken();
				isVariableName(item);
				createLocaleName(item);
			}
		}
		else{
		String mot = getWord(param.get(0));
		if (null != mot) {
			createLocaleName(mot);
		} else
			throw new myException(cadre, param.get(0)+ Logo.messages.getString("error.word"));
		}
	}
	
	/**
	 * 	returns the color defined by [r g b] contained in "ob"
	 * @param obj the list [r g b]
	 * @param name The name of the calling primitive
	 * @return The Object Color 
	 * @throws myException If the list doesn't contain 3 numbers
	 */

	private Color rgb(String obj,String name) throws myException {
		String liste = getFinalList(obj);
		StringTokenizer st = new StringTokenizer(liste);
		if (st.countTokens() != 3)
			throw new myException(cadre, name+" "+Logo.messages.getString("color_3_arguments"));
		int[] entier = new int[3];
		for (int i = 0; i < 3; i++) {
			String element=st.nextToken();
			try{
				entier[i] = (int)(Double.parseDouble(element)+0.5)% 256;
			}
			catch(NumberFormatException e){
				throw new myException(cadre,element+" "+Logo.messages.getString("pas_nombre"));
			}
			if (entier[i]<0) entier[i]+=256;
		}
		return (new Color(entier[0], entier[1], entier[2]));
	}
	
	/**
	 * Primitive member or member?
	 * @param param Stack that contains arguments for the primitive member
	 * @param id 69 --> member? or 70--> member 
	 * @throws myException Incorrect arguments
	 */
	private void membre(Stack<String> param, int id) throws myException {
		Interprete.operande = true;
		String mot_retourne = null;
		boolean b = false;
		String mot = getWord(param.get(1));
		String liste = "[ ";
		if (null == mot) { //on travaille sur une liste
			try {
				liste = getFinalList(param.get(1));
				StringTokenizer st = new StringTokenizer(liste);
				liste = "[ ";
				mot = getWord(param.get(0));
				String str;
				if (null!=mot&& mot.equals("")) mot="\\v";
				if (null == mot)
					mot = param.get(0).trim();
				while (st.hasMoreTokens()) {
					str = st.nextToken();
					if (str.equals("["))
						str = extractList(st);
					if (!liste.equals("[ "))
						liste += str + " ";
					if (str.equals(mot) && liste.equals("[ ")) {
						if (id == 69) {
							b = true;
							break;
						} else
							liste += str + " ";
					}
				}
			} catch (myException ex) {
			}
		} else { //on travaille sur un mot
			String mot2 = getWord(param.get(0));
			if (null != mot2) {
				boolean backslash=false;
				for (int i = 0; i < mot.length(); i++) {
					char c=mot.charAt(i);
					if (!backslash&&c=='\\') backslash=true;  
					else  {
						String tmp=Character.toString(c);
							if (backslash) tmp="\\"+tmp;  
						if (tmp.equals(mot2)){
							if (id == 69) {
								b = true;
								break;
							} else {
								if (!backslash)	mot_retourne = mot.substring(i, mot.length());
								else mot_retourne=mot.substring(i-1, mot.length());
								break;
							}	
						}
						backslash=false;
					}
				}
			}
		}
		if (!liste.equals("[ "))
			Interprete.calcul.push(liste + "] ");
		else if (null != mot_retourne){
			try{
				Double.parseDouble(mot_retourne);
				Interprete.calcul.push(mot_retourne);
			}
			catch(NumberFormatException e){
				Interprete.calcul.push(debut_chaine+mot_retourne);
			}
		}
		else if (b)
			Interprete.calcul.push(Logo.messages.getString("vrai"));
		else
			Interprete.calcul.push(Logo.messages.getString("faux"));
	}

	/**
	 *  Primitive before?
	 * @param param Stack that contains all arguments
	 * @throws myException Bad argument type
	 */

	
	private void precede(Stack<String> param) throws myException {
		Interprete.operande = true;
		boolean b = false;
		String ope[] = { "", "" };
		String mot = "";
		for (int i = 0; i < 2; i++) {
			mot = getWord(param.get(i));
			if (null == mot)
				throw new myException(cadre, param.get(i) + " "
						+ Logo.messages.getString("pas_mot"));
			else
				ope[i] = mot;
		}
		if (ope[1].compareTo(ope[0]) > 0)
			b = true;
		if (b)
			mot = Logo.messages.getString("vrai");
		else
			mot = Logo.messages.getString("faux");
		Interprete.calcul.push(mot);
	}
	
	/**
	 * / Primitive equal?
	 * @param param Stack that contains all arguments
	 */
	private void equal(Stack<String> param) {
		try {
			double ope1, ope2 = 0;
			ope1 = Double.parseDouble(param.get(0));
			ope2 = Double.parseDouble(param.get(1));
			if (ope2 == ope1)
				Interprete.calcul.push(Logo.messages.getString("vrai"));
			else
				Interprete.calcul.push(Logo.messages.getString("faux"));
		} catch (NumberFormatException e) {
			if (param.get(0).toString().equals(param.get(1).toString()))
				Interprete.calcul.push(Logo.messages.getString("vrai"));
			else
				Interprete.calcul.push(Logo.messages.getString("faux"));
		}
		Interprete.operande = true;
	}

	/**
	 * this method returns the boolean corresponding to the string st
	
	 * @param st true or false
	 * @return The boolean corresponding to the string st
	 * @throws myException If st isn't equal to true or false
	 */
	
	private boolean predicat(String st) throws myException {
		if (st.toLowerCase().equals(Logo.messages.getString("vrai")))
			return true;
		else if (st.toLowerCase().equals(Logo.messages.getString("faux")))
			return false;
		else
			throw new myException(cadre, st + " "
					+ Logo.messages.getString("pas_predicat"));

	}

	/**
	 *  This method converts st to double
	 * @param st The String
	 * @return The double corresponding to st
	 * @throws myException If st can't be convert
	 */
	
	private double number(String st) throws myException { //Si un nombre est un double
		try {
			return (Double.parseDouble(st));
		} catch (NumberFormatException e) {
			throw new myException(cadre, st + " "
					+ Logo.messages.getString("pas_nombre"));
		}
	}
	
	/**
	 * Erase unused Zeros in decimal Format
	 * @param bd The decimal number
	 * @return The formatted number
	 */
	 static protected String eraseZero(BigDecimal bd){
		 DecimalFormatSymbols dfs=new DecimalFormatSymbols();
		 dfs.setDecimalSeparator('.');
		 DecimalFormat df=new DecimalFormat("#####.################",dfs);
		 String st=df.format(bd);
		 return st;

	 }
	 /**
	  * 	Converts st to BigDecimal number
	  * @param st The String to convert
	  * @return The BigDecimal Number 
	  * @throws myException if st isn't a number
	  */

	private BigDecimal numberDecimal(String st) throws myException { //Si un nombre
									  // est un double
		// To improved with MathContext for JRE>1.5
		try {
//			BigDecimal bd=new BigDecimal(st.toString());
//			return(bd.round(mc));
	
			BigDecimal bd=new BigDecimal(st).setScale(16,BigDecimal.ROUND_HALF_EVEN);
			return (new BigDecimal(eraseZero(bd)));
			
		} catch (NumberFormatException e) {
			throw new myException(cadre, st + " "
					+ Logo.messages.getString("pas_nombre"));
		}
	}
	
	/**
	 * 	Returns the word contained in st.
	 *	 If it isn't a word, returns null
	 * @param st The Object to convert
	 * @return The word corresponding to st
	 */
	private String getWord(Object st) { // Si c'est un mot
		String liste = st.toString();
		if (liste.equals("\"")) {
			debut_chaine = "";
			return "";
		}
		if (liste.length() > 0 && liste.substring(0, 1).equals("\"")) {
			debut_chaine = "\"";
			return (liste.substring(1, liste.length()));
		} else
			try {
				Double.parseDouble(liste);
				debut_chaine = "";
				return liste;
			} catch (NumberFormatException e) {
			}
		return (null);
	}

	/**
	 * 	Test if the number contained in st is an integer
	 * @param st The Object to convert
	 * @return The integer corresponding to st
	 * @throws myException If it isn't an integer
	 */
	
	private int getInteger(String st) throws myException { //Si c'est un entier
		try {
/*			double ent = Double.parseDouble(st.toString());
			if (ent == 0)
				return 0;
			long enti = Math.round(ent);
			if (ent / enti == 1)
				return ((long) enti);
			else*/
			
				return Integer.parseInt(st);
		} catch (NumberFormatException e) {
			throw new myException(cadre, st + " "
					+ Logo.messages.getString("pas_entier"));
		}
	}
	/**
	 * 	Returns the list contained in the string li without any lineNumber
	 * @param li The String corresponding to the list
	 * @return A list without any line Number tag (\0, \1, \2 ...)
	 * @throws myException List bad format
	 */

	private String getFinalList(String li) throws myException {
		// remove line number
		li=li.replaceAll("\\\\l([0-9])+ ", "");
		// return list
		return getList(li);
	}
	
	/**
	 * 	Returns the list contained in the string li
	 * @param li The String corresponding to the list
	 * @return A list with line Number tag (\0, \1, \2 ...)
	 * @throws myException List bad format
	 */	
	private String getList(String li) throws myException {
		li = li.trim();
		//Retourne la liste sans crochets;
		if (li.substring(0, 1).equals("[")
				&& li.substring(li.length() - 1, li.length()).equals("]")) {
			li = li.substring(1, li.length() - 1).trim() + " ";
			if (!li.equals(" "))
				return li;
			else
				return ("");
		} else
			throw new myException(cadre, li + " "
					+ Logo.messages.getString("pas_liste"));			
	}
	
	/**
	 * Tests if "li" is a list
	 * @param li The String to test
	 * @return true if it is a list, else false
	 */	
	// 
	
	protected static boolean isList(String li) {
		li = li.trim();
		if (li.length() > 0 && li.substring(0, 1).equals("[")
				&& li.substring(li.length() - 1, li.length()).equals("]"))
			return (true);
		return false;
	}

	// Format the List (only one space between two elements)
	private String formatList(String li) {
		String tampon = "";
		String precedent = "";
		StringTokenizer st = new StringTokenizer(li, " []", true);
		String element = "";
		while (st.hasMoreTokens()) {
			element = st.nextToken();
			while (st.hasMoreTokens() && element.equals(" ")) {
				element = st.nextToken();
			}
			if (element.equals("]"))
				tampon = tampon.trim() + "] ";
			else if (element.equals("[")) {
				if (precedent.equals("["))
					tampon += "[";
				else
					tampon = tampon.trim() + " [";
			}
			else
				tampon += element + " ";
			precedent = element;
		}
		return (tampon.trim());
	}
	

	
	private String extractList(StringTokenizer st) {
		int compteur = 1;
		String crochet = "[ ";
		String element = "";
		while (st.hasMoreTokens()) {
			element = st.nextToken();
			if (element.equals("[")) {
				compteur++;
				crochet += "[ ";
			} else if (!element.equals("]"))
				crochet += element + " ";
			else if (compteur != 1) {
				compteur--;
				crochet += "] ";
			} else {
				crochet += element + " ";
				break;
			}
		}
		element = crochet;
		compteur = 0;
		return element.trim();
	}
	
	private int extractList(String st, int deb) {
		int compteur = 1;
		char element;
		boolean espace=false;
		boolean crochet_ouvert=false;;
		boolean crochet_ferme=false;
		for (int i=deb;i<st.length();i++) {
			element = st.charAt(i);
			if (element=='[') {
				if (espace) crochet_ouvert=true;
				espace=false;
				crochet_ferme=false;
			}
			else if (element==']') {
				if (espace) crochet_ferme=true;
				espace=false;
				crochet_ouvert=false;
			} 
			else if (element==' '){
				espace=true;
				if (crochet_ouvert) {
					compteur++;
					crochet_ouvert=false;
				}
				else if (crochet_ferme){
					crochet_ferme=false;
					if (compteur!=1) compteur--;
					else {compteur=i;break;}
				}
			}
		}
		return compteur;
	}

	// returns how many elements contains the list "liste" 	
	private int numberOfElements(String liste) { //calcule le nombre d'éléments dans une
									  // liste
		StringTokenizer st = new StringTokenizer(liste);
		int i = 0;
		String element = "";
		while (st.hasMoreTokens()) {
			element = st.nextToken();
			if (element.equals("["))
				element = extractList(st);
			i++;
		}
		return i;
	}

	// returns the item "i" from the list "liste"
	private String item(String liste, int i) throws myException { // retourne
															// l'élément i d'une
															// liste
		StringTokenizer st = new StringTokenizer(liste);
		String element = "";
		int j = 0;
		while (st.hasMoreTokens()) {
			j++;
			element = st.nextToken();
			if (element.equals("["))
				element = extractList(st);
			if (j == i)
				break;
		}
		if (j != i)
			throw new myException(cadre, Logo.messages.getString("y_a_pas")
					+ " " + i + " "
					+ Logo.messages.getString("element_dans_liste") + liste
					+ "]");
		else if (i == 0 && j == 0)
			throw new myException(cadre, Logo.messages.getString("liste_vide"));
		try {
			Double.parseDouble(element);
			return element;
		} // Si c'est un nombre, on le renvoie.
		catch (Exception e) {
		}
		if (element.startsWith("["))
			return element+" "; //C'est une liste, on la renvoie telle quelle.
		if (element.equals("\\v")) element="";
		return "\"" + element; //C'est forcément un mot, on le renvoie.
	}

	// Test if the name of the variable is valid
	private void isVariableName(String st) throws myException{
		if (st.equals("")) throw new myException(cadre,Logo.messages.getString("variable_vide"));
        if (":+-*/() []=<>&|".indexOf(st)>-1) throw new myException(cadre,st+" "+Logo.messages.getString("erreur_variable"));
        
		try{Double.parseDouble(st);
			throw new myException(cadre,Logo.messages.getString("erreur_nom_nombre_variable"));
		}
		catch(NumberFormatException e){
			
		}
		
	}
	
	// primitve make
	private void donne(Stack<String> param) throws myException {
		String mot = getWord(param.get(0));
		if (null == mot)
			throw new myException(cadre, param.get(0) + " "
					+ Logo.messages.getString("error.word"));
		mot = mot.toLowerCase();
		isVariableName(mot);
		if ( Interprete.locale.containsKey(mot)) {
			Interprete.locale.put(mot,param.get(1));
		} else {
			wp.globale.put(mot, param.get(1));
		}	
	}

	private void delay() {
		if (Config.turtleSpeed != 0) {
			try {
				Thread.sleep(Config.turtleSpeed * 5);
			} catch (InterruptedException e) {
			}
		}
	}

	private String teste_fin_double(double d) {
		String st = String.valueOf(d);
		if (st.endsWith(".0"))
			st = st.substring(0, st.length() - 2);
		return st;
	}

//	How many characters in the word "mot"
	private int getWordLength(String mot){//retourne le nombre de caractères d'un mot
	 	int compteur=0;
	 	boolean backslash=false;
	 	for(int i=0;i<mot.length();i++){
	 		if (!backslash&&mot.charAt(i)=='\\') backslash=true;
	 		else {backslash=false;compteur++;}
	 	}
	 	return compteur;
	 }
	// the character number "i" in the word "mot"
	private String itemWord(int entier,String mot) throws myException{
	 	String reponse="";
	 	int compteur=1;
	 	boolean backslash=false;
	 	if (mot.equals("")) throw new myException(cadre,Logo.messages.getString("mot_vide"));
	 	for(int i=0;i<mot.length();i++){
	 		char c=mot.charAt(i);
	 		if (!backslash&&c=='\\') backslash=true;
	 		else {
	 			if (compteur==entier){
	 				if (backslash) reponse="\\"+Character.toString(c);
	 				else reponse=Character.toString(c);
	 				break;
	 			}
	 			else {compteur++;backslash=false;}
	 		} 		
	 	}
	 return reponse;	
	 }
	  protected void setWorkspace(Workspace workspace){
	  	wp=workspace;
	  }

	  private void multiply(Stack<String> param){
		  int size=param.size();
		  BigDecimal product=new BigDecimal(1);
		  BigDecimal a;
			try {
				for (int i=0; i<size;i++){
					a = numberDecimal(param.get(i));
					product=product.multiply(a);	
				}
				Interprete.calcul.push(eraseZero(product));
				Interprete.operande = true;
			} catch (myException e) {
			}
	  }
	  private void divide(Stack<String> param){
		try {
			double a = number(param.get(0));
			double b = number(param.get(1));
			if (b == 0)
				throw new myException(cadre, Logo.messages
						.getString("division_par_zero"));
			Interprete.calcul.push(teste_fin_double(a / b));
			Interprete.operande = true;
		} catch (myException e) {
		}
	  }
	  
	  private void add(Stack<String> param){
		  int size=param.size();
			try {
				BigDecimal a;
				BigDecimal sum=BigDecimal.ZERO;
				for (int i=0;i<size;i++){
					a = numberDecimal(param.get(i));
					sum=sum.add(a);
				}
				Interprete.calcul.push(eraseZero(sum));
				Interprete.operande = true;
			} catch (myException e) {
			}
	  }
	  private void substract(Stack<String> param){
			try {
				BigDecimal a = numberDecimal(param.get(0));
				BigDecimal b = numberDecimal(param.get(1));
				Interprete.calcul.push(eraseZero(a.subtract(b)));
				Interprete.operande = true;
			} catch (myException e) {
			}
	  }
  private void ou(Stack<String> param){
		int size=param.size();
		boolean result=false;
		boolean b;
		try{
			for(int i=0;i<size;i++){
					b = predicat(param.get(i).toString());
					result=result|b;
				}
				if (result)
					Interprete.calcul.push(Logo.messages.getString("vrai"));
				else
					Interprete.calcul.push(Logo.messages.getString("faux"));
				Interprete.operande = true;
			} 
			catch (myException e) {}
	  }
  private void et(Stack<String> param){
		int size=param.size();
		boolean result=true;
		boolean b;
		try {
				for(int i=0;i<size;i++){
					b=predicat(param.get(i).toString());
					result = result & b;
				}
			Interprete.operande = true;
			if (result)
				Interprete.calcul.push(Logo.messages.getString("vrai"));
			else
				Interprete.calcul.push(Logo.messages.getString("faux"));
		} catch (myException e) {
		}
  }
}