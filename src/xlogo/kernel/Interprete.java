/**
 * Title :        XLogo
 * Description :  XLogo is an interpreter for the Logo 
 * 						programming language
 * @author Loïc Le Coq
 */
package xlogo.kernel;

import java.util.Stack;
import java.util.HashMap;
import xlogo.utils.myException;
import xlogo.Application;
import xlogo.Logo;

public class Interprete {

	private LaunchPrimitive lanceprim;
	private Application app;
	private Kernel kernel;
	private Workspace wp;
	protected static boolean renvoi_instruction = false; // Si une primitive
															// renvoie une série
															// d'instruction à
															// évaluer (si,
															// exec).
	public static Stack<String> calcul = new Stack<String>(); // Pile contenant les nombres
	protected static Stack<HashMap<String,String>> stockvariable = new Stack<HashMap<String,String>>();
	protected static boolean stop = false;
	protected static Stack<String> nom = new Stack<String>(); // contient les noms des
												// procédures attendant
												// parmamètres à recevoir
	public static Stack<String> en_cours = new Stack<String>(); // contient les procédures
												// actuellement en cours
												// d'exécution
	public static StringBuffer instruction = new StringBuffer(); // String
																	// contenant
																	// les
																	// chaînes
																	// représentant
																	// les
																	// instructions
	public static StringBuffer actionInstruction=new StringBuffer();
	
	protected static HashMap<String,String> locale = new HashMap<String,String>(); // Pile contenant les
														// noms des variables
														// locales
	// protected static Stack valeur = new Stack(); // Pile contenant les
	// valeurs de ses variables.

	protected static boolean operande = false; // si l'élément trouvé est un
												// nombre
	protected static boolean operateur = false; // si l'élément trouvé est un
												// opérateur
	protected static boolean drapeau_ouvrante = false; // si l'élément trouvé
														// est une parenthèse
														// ouvrante
	protected static boolean drapeau_fermante = false; // si l'élément trouvé
														// est une parenthèse
														// fermante
	public static String lineNumber="";
	// private TreeParser tp;
	/*
	 * public Interprete(Application cadre){ this.cadre=cadre;
	 * 
	 * lanceprim=new LaunchPrimitive(cadre); cadre.error=false; }
	 */
	public Interprete(Application app) {
		this.kernel = app.getKernel();
		this.app = app;
		wp = kernel.getWorkspace();
		lanceprim = new LaunchPrimitive(app, wp);
		app.error = false;
	}

	String execute(StringBuffer instructions) throws myException { 
		if (!instructions.equals("")) {
			if (instruction.length() == 0)
				instruction.append(instructions);
			else
				instruction.insert(0, instructions);
		}

		// Object obca1,obca2,oban;
		while (instruction.length() != 0) {
			if (app.error && myException.lance)
				throw new myException(app, Logo.messages.getString("stop"));
			while (app.affichage.isOnPause()) { // Si l'on touche aux scrollbars
				try {
					wait();
				} catch (Exception e) {
				}
			}
			// System.out.println("en_cours d'execution "+"\n"+ en_cours+"\n\n");
		//	System.out.print("debut\n"+instruction+"\nfin\n\n");
			// System.out.println("nom "+nom);
			// System.out.println("calcul \n"+calcul+"\n\n");
			// System.out.println("nom "+nom.toString()+" locale "+locale+ "
			// "+valeur+" stockvariable "+stockvariable);
			// System.out.println("operande "+calcul+" "+operande+"debut
			// "+instruction);

			if (instruction.length() == 0)
				break;
			String element = getNextWord();
		//	System.out.println("a"+element+"a");

			if (element.startsWith("\\l")){
				if (operande) {
					break;	
				}
				deleteFirstWord(element);
				lineNumber=element+" ";
				element=getNextWord();
			}
			if (element == "")
				break;
			// si c'est une primitive ou une procedure
			String element_minuscule = element.toLowerCase();
			int i = isProcedure(element_minuscule);
			
			if (Primitive.primitives.containsKey(element_minuscule) || i > -1) {
				// identifiant de la primitive
				if (i == -1)
					i = Integer.valueOf(
							Primitive.primitives.get(element_minuscule)).intValue()
							% Primitive.PRIMITIVE_NUMBER;
				else
					i = -i - 2;
				// if (!calcul.empty()&&nom.isEmpty())
				// throw new
				// monException(cadre,Logo.messages.getString("que_faire")+"
				// "+calcul.pop() +" gdfdsf");
				// exécuter la procédure ou la primitive.
				Stack<String> param = new Stack<String>();
				
				if (29 < i && i < 39) { // Si c'est un opérateur infixé
					deleteLineNumber();
					operateur = true;
					operande = false;
					/*
					 * if (drapeau_ouvrante) { drapeau_ouvrante=false;
					 * 
					 * if (i!=32&&i!=33) throw new myException(app,element+"
					 * "+Logo.messages.getString("ne_peut_etre")); else
					 * param.push("0"); }
					 */
					// else
					if (calcul.isEmpty()) { // Si le + ou le - représente le
											// signe négatif ou positif
						if (i != 32 && i != 33)
							throw new myException(app, element + " "
									+ Logo.messages.getString("error.ne_peut_etre")); // d'un
																				// nombre
						if (nom.isEmpty())
							param.push("0");
						else {
							String st = nom.peek();
							if (!testoperateur(st))
								param.push("0");
							else if ("*/".indexOf(st) > -1) { // Si le signe -
																// ou + suit un
																// * ou /
								deleteFirstWord(element);
								if (st.equals("*"))
									instruction.insert(0, "* ");
								else
									instruction.insert(0, "/ ");
								if (i == 32)
									return ("1"); // Si c'est un plus
								else
									return ("-1"); // Si c'est un moins
							} else
								param.push("0");
						}
					} else if (nom.isEmpty()) {
						param.push(calcul.pop());
					} else {
						String st = nom.peek();
						if (testoperateur(st)) {
							// System.out.println("st "+st+" element "+element+"
							// "+prioriteinf(st,element));
							if (prioriteinf(st, element)) {
								param.push(calcul.pop());
							} else
								return (calcul.pop());
						} else
							param.push(calcul.pop());
					}
				} else if (operande && i != 204) {
					checkParenthesis();
					operande = false;
					break;
				} // Si ce n'est pas l'opérateur de fin de parenthèse, on sort
								
				/*Example:
				 * To test  |	Formatted Form:
				 * fd 5 	| 	fd 5 \l1 rt \l2
				 * rt 		| --> The \l2 can't be removed before be 
				 * end		|		sure the rt has noproblem
				 * */
				if (!element.equals("\n")) deleteLineNumber();
				deleteFirstWord(element);

				// Case with parenthensis
				// eg (sum 3 4 5)
				// eg (myProcedure 2 3 4 5)
				if (drapeau_ouvrante) {
					drapeau_ouvrante = false;
					int constantNumber = -1;
					if (!hasGeneralForm(i)) {
						// How many arguments for the procedure or the primitive
						// For primitive
						if (i > -1)
							constantNumber = kernel.primitive.parametres[i];
						// For procedure
						else
							constantNumber = wp.getProcedure(-i - 2).nbparametre;
					}
					// Looking for all arguments (Number undefined)
					nom.push(element);
					int j = 0;
					while (true) {
						try {
							operande = operateur = drapeau_ouvrante = false;
							if (getNextWord().equals(")")) {
								if (constantNumber != -1) {
									// If the primitive or the procedure doesn't
									// accept optional parameters
									if (j > constantNumber)
										throw new myException(
												app,
												Logo.messages
														.getString("too_much_arguments"));
									else if (j < constantNumber)
										throw new myException(
												app,
												Logo.messages
														.getString("pas_assez_de")
														+ " " + nom.peek());
								}
								break;
							}
							String a = execute(new StringBuffer());
							param.push(a);
						} catch (myException e) {
							throw e;
						}
						j++;
					}
					// If It's a procedure

					if (i < 0) {
						Procedure proc = wp.getProcedure(-i - 2);
						if (j > proc.nbparametre + proc.optVariables.size())
							throw new myException(app, Logo.messages
									.getString("too_much_arguments"));
						else if (j < proc.nbparametre)
							throw new myException(app, Logo.messages
									.getString("pas_assez_de")
									+ " " + nom.peek());
						// Searching for optional arguments that are not defined

						if (j < proc.optVariables.size() + proc.nbparametre) {
							j = j - proc.nbparametre;
							for (int c = j; c < proc.optVariables.size(); c++) {
								try {
									operande = operateur = drapeau_ouvrante = false;
									String a = execute( proc.optVariablesExp
											.get(c));
									param.push(a);
								} catch (myException e) {
									throw e;
								}
							}
						}
					}

				}
				// classic case: predefined number of arguments
				else {
					drapeau_ouvrante = false;
					// How many arguments for the procedure or the primitive
					int nbparametre = 0;
					// For primitive
					if (i > -1)
						nbparametre = kernel.primitive.parametres[i];
					// For procedure
					else
						nbparametre = wp.getProcedure(-i - 2).nbparametre;
					// Looking for each arguments
					int j = 0;
					nom.push(element);
					while (j < nbparametre) {
						try {
							operande = operateur = drapeau_ouvrante = false;

							String a = execute(new StringBuffer());
							param.push(a);
							j++;
						} catch (myException e) {
							throw e;
						}
					}
					// Looking for Optional arguments in case of procedure
					if (i < 0) {
						Procedure proc = wp.getProcedure(-i - 2);
						nbparametre = proc.optVariables.size();
						for (j = 0; j < nbparametre; j++) {
							try {
								operande = operateur = drapeau_ouvrante = false;
								String a = execute(proc.optVariablesExp
										.get(j));
								param.push(a);
							} catch (myException e) {
								throw e;
							}
						}
					}
				}

				// //////////////////////////////////////////////////////////////////////////////////////////
				// System.out.println(nom+" "+"debut "+instruction+"
				// fin\n"+param.toString());
				 //System.out.println(nom);
				nom.pop();
				if (!app.error)
					lanceprim.execute(i, param);
				if (app.error)
					break;
				if (drapeau_fermante && !calcul.empty()) {
					drapeau_fermante = false;
					operande = false;
					return calcul.pop();
				}

				// Tester si la procédure rend quelque chose lorsqu'attendu
				
				if (!operande) {
					// dans le cas des primitives exec ou si
					if (renvoi_instruction) {
						renvoi_instruction = false;
					} else {
						if (!nom.isEmpty() && !app.error
								&& !nom.peek().equals("\n")) {
							if (!element.equals("\n")) {
								// If it's the end of a loop
								// repeat 2 [fd 90 rt]
								if (element.equals("\\")) {
									// The loop had been executed, we have to remove
									// the loop instruction
									int offset=Interprete.instruction.indexOf(" \\ ");
									Interprete.instruction=Interprete.instruction.delete(0, offset+1);
									
									throw new myException(app, Logo.messages
											.getString("pas_assez_de")
											+ " " + nom.peek());
								}
								// (av 100) ---> OK
								// av av 20 ----> Bad
								if (!nom.peek().equals("("))
									throw new myException(
											app,
											element
													+ " "
													+ Logo.messages
															.getString("ne_renvoie_pas")
													+ " "
													+ nom.peek());
							}
						}
					}
				}
				else{
					// The primitive returns a word or a list.
					// There's no primitive or procedure waiting for it.
					if (!nom.isEmpty()&&nom.peek().equals("\n")) 
						throw new myException(app,Logo.messages.getString("error.whattodo")+" "
								+calcul.peek()+" ?"); 			 
				}
			}

			/* ********************************
			/ IF element IS A VARIABLE
			********************************* */
			else if (element.substring(0, 1).equals(":")
					&& element.length() > 1) {
				// System.out.println(operande);
				if (operande) {
					checkParenthesis();
					operande = false;
					break;
				}
				else deleteLineNumber();
				String value;
				String variableName = element_minuscule.substring(1,
						element_minuscule.length());
				// If the variable isn't local
				if (!locale.containsKey(variableName)) {
					// check it's a global variable
					if (!wp.globale.containsKey(variableName))
						throw new myException(app, variableName + " "
								+ Logo.messages.getString("error.novalue"));
					else
						value = wp.globale.get(variableName).toString();
				}
				// If the variable is local
				else {
					value = locale.get(variableName);
				}

				if (null == value)
					throw new myException(app, variableName + "  "
							+ Logo.messages.getString("error.novalue"));
				calcul.push(value);
				operande = true;
				operateur = false;
				drapeau_ouvrante = false;
				deleteFirstWord(element);
			} else {
				/* *****************************
				 * IF element IS A NUMBER ******
				 * ***************************/
				try {
					Double.parseDouble(element);
					String fin_entier = "";
					if (element.endsWith(".0")) {
						fin_entier = ".0";
						element = element.substring(0, element.length() - 2);
					}
					if (element.startsWith(".") || element.equals(""))
						element = "0" + element;
					calcul.push(element);
					if (operande) {
						checkParenthesis();
						calcul.pop();
						operande = false;
						break;
					}
					else deleteLineNumber();
					operande = true;
					operateur = false;
					drapeau_ouvrante = false;
					deleteFirstWord(element + fin_entier);
				} catch (NumberFormatException e) {
					/* *********************************
					 * IF element IS A SQUARE BRACKET [
					 * 			OPEN
					*********************************** */
					if (element.equals("[")) {

						// Utilité de cette ligne?
						// if (!calcul.isEmpty()&&operateur==false) break;
						if (operande) {
							checkParenthesis();
							break;
						}
						else deleteLineNumber();
						operande = true;
						operateur = false;
						drapeau_ouvrante = false;
						deleteFirstWord(element);
						String a = chercheListe();
						calcul.push(a);
					}
					/* ***************************
					 * IF element IS A PARENTHESIS
					 * 				OPEN			
					 * *********************** */
					else if (element.equals("(")) {
						if (operande) {
							checkParenthesis();
							break;
						}
						else deleteLineNumber();
						drapeau_ouvrante = true;

						Interprete.en_cours.push("(");
						int pos = chercheParenthese();
						if (pos == -1) {
							try {
								throw new myException(app, Logo.messages
										.getString("parenthese_fermante"));
							} catch (myException e1) {
							}
						}
						deleteFirstWord(element);
						// System.out.println("&&"+instruction);
						Interprete.nom.push("(");
					}
					/* **********************************
					 * IF element IS A WORD 
					 * ************************** */
					else if (element.substring(0, 1).equals("\"")) {
						try {
							String el = element.substring(1);
							Double.parseDouble(el);
							calcul.push(el);
						} catch (NumberFormatException e1) {
							calcul.push(element);
						}
						if (operande) {
							checkParenthesis();
							calcul.pop();
							operande = false;
							break;
						}
						else deleteLineNumber();
						operande = true;
						operateur = false;
						drapeau_ouvrante = false;
						deleteFirstWord(element);
					}

					// Si c'est le mot pour
					else if (element_minuscule.equals(Logo.messages
							.getString("pour"))) {
						deleteFirstWord(element);
						if (instruction.length() != 0)
							element = getNextWord().toLowerCase();
						else
							throw new myException(app, Logo.messages
									.getString("pas_assez_de")
									+ " "
									+ "\""
									+ Logo.messages.getString("pour") + "\"");
						if (Primitive.primitives.containsKey(element_minuscule)
								|| isProcedure(element_minuscule) != -1)
							throw new myException(app, element + " "
									+ Logo.messages.getString("existe_deja"));
						else {
							String definition = Logo.messages.getString("pour")
									+ " " + element + " ";
							deleteFirstWord(element);
							while (instruction.length() != 0) {
								element = getNextWord().toLowerCase();
								if (null == element)
									break;
								if (!element.substring(0, 1).equals(":")
										|| element.length() == 1)
									throw new myException(app, element
											+ " "
											+ Logo.messages
													.getString("pas_argument"));
								definition += element + " ";
								deleteFirstWord(element);
							}
							if (app.editeur.isVisible())
								throw new myException(app, Logo.messages
										.getString("ferme_editeur"));
							else {
								app.editeur.setVisible(true);
								app.editeur.setEditorText(definition + "\n"
										+ Logo.messages.getString("fin"));
							}
						}
					}
					// Si c'est le mot ed
					else if (element_minuscule.equals("ed")) {
						deleteFirstWord(element);
						if (!app.editeur.isVisible()) {
							Stack<String> a_editer = null;
							if (instruction.length() != 0
									&& getNextWord().equals("[")) {
								a_editer = new Stack<String>();
								deleteFirstWord("[");
								while (instruction.length() != 0) {
									String el = getNextWord().toLowerCase();
									a_editer.push(el);
									deleteFirstWord(el);
								}
							}
							for (int ij = 0; ij < wp.getNumberOfProcedure(); ij++) {
								Procedure procedure = wp.getProcedure(ij);
								if ((null == a_editer || a_editer
										.search(procedure.name) != -1)
										&& procedure.affichable) {
									app.editeur.setEditorStyledText(procedure.toString());
								}
							}
							app.editeur.setTitle(Logo.messages
									.getString("editeur"));
							app.editeur.setVisible(true);
							app.editeur.toFront();
						}

					} else {
						deleteLineNumber();
						throw new myException(app, Logo.messages
								.getString("je_ne_sais_pas")
								+ " " + element);
					}
				}
			}
			// System.out.println("instruction "+instruction+" calcul "+calcul);
		}
		/* ******************************
		 *****      END OF THE MAIN LOOP
		 * ******************************/
		// S'il n'y a rien à retourner.
		if (calcul.isEmpty()) {
			if (!nom.isEmpty()) {// &&!nom.peek().equals("\n")) {
				while ((!nom.isEmpty()) && nom.peek().equals("\n"))
					nom.pop();
				if (!nom.isEmpty()) {
					throw new myException(app, Logo.messages
							.getString("pas_assez_de")
							+ " " + nom.peek());
				}
			}
		}
		// Sinon on retourne la valeur contenue dans la pile de calcul.
		if (!calcul.isEmpty()) {
			// S'il y a une procédure de lancer
			// Ex: pour t -- 6 -- fin . Puis, av t.
			if ((!nom.isEmpty()) && nom.peek().equals("\n")) {
				String up = "";
				int id=0;
				while (!nom.isEmpty()&&nom.peek().equals("\n"))	{
					nom.pop();
					id++;
				}
				if (!nom.isEmpty())	{
					up=nom.peek().toString();
					try {
						throw new myException(app, en_cours.get(en_cours.size()-id)
								 + " "+ Logo.messages.getString("ne_renvoie_pas")
							+ " " + up);
					} catch (myException e) {}
				}
				else {
					try {
						throw new myException(app, Logo.messages
								.getString("error.whattodo")
								+ " " + calcul.peek() + " ?");
					} catch (myException e) {
					}
				}
/*				}
				
				if (!nom.isEmpty() && nom.peek().equals("\n")) {
					up = en_cours.get(en_cours.size() - 2).toString();
					try {
						throw new myException(app, en_cours.peek() + " "
								+ Logo.messages.getString("ne_renvoie_pas")
								+ " " + up);
					} catch (myException e) {
					}
				} else if (!nom.isEmpty()) {
					up = nom.peek().toString();
					try {
						throw new myException(app, en_cours.peek() + " "
								+ Logo.messages.getString("ne_renvoie_pas")
								+ " " + up);
					} catch (myException e) {
					}
				} else {
					try {
						throw new myException(app, Logo.messages
								.getString("que_faire")
								+ " " + calcul.peek() + " ?");
					} catch (myException e) {
					}}*/
				
			}
			// ///////////
			else {
				operande = false;
				return (calcul.pop());
			}
		}
		return ("");
	}

	private int chercheParenthese() { // position ou s'arrete la prochaine
										// parenthese
		boolean continuer = true;
		int of_ouvrant;
		int of_fermant = 0;
		int from_index_ouvrant = 1;
		int from_index_fermant = 1;
		while (continuer) {
			of_ouvrant = instruction.indexOf("(", from_index_ouvrant);
			of_fermant = instruction.indexOf(")", from_index_fermant);
			if (of_fermant == -1)
				break;
			if (of_ouvrant != -1 && of_ouvrant < of_fermant) {
				from_index_ouvrant = of_ouvrant + 1;
				from_index_fermant = of_fermant + 1;
			} else
				continuer = false;
			;
		}
		return of_fermant;
	}

	protected String chercheListe() throws myException {
		String liste = "[ ";
		String element = "";
		while (instruction.length() != 0) {
			element = getNextWord();
			// SI crochet ouvrant, on l'empile dans la pile de calcul
			if (element.equals("[")) {
				calcul.push("[");
				deleteFirstWord(element);
				liste += "[ ";
			}

			else if (element.equals("]")) { // Si on atteint un crochet fermant
				deleteFirstWord(element);
				// if (((Stack)instruction.peek()).isEmpty()) instruction.pop();
				liste += "] ";
				if (calcul.empty()) {
					return (liste);
				} // 1er cas: rien dans la pile de calcul, on renvoie la liste
				else if (!calcul.peek().toString().equals("[")) {
					return (liste);
				} // 2eme cas: pas de crochet ouvrant en haut de la pile, idem
				else
					calcul.pop(); // 3eme cas: un crochet ouvrant en haut de
									// la pile, on l'enleve
			} 
			else {
				deleteFirstWord(element);
				liste += element + " ";
			}
		}
		if (true)
			throw new myException(app, Logo.messages
					.getString("erreur_crochet"));
		return (null);
	}

	private boolean testoperateur(String st) { // l'élément trouvé est-il un
												// opérateur
		int i = "+-*/<>=!&|".indexOf(st);
		if (i == -1)
			return (false);
		return (true);
	}

	private boolean prioriteinf(String pile, String str) { // teste si le
															// sommet de la pile
															// d'analyse
		/*
		 * if (parenthesefermante>0) return(false); // est un opérateur de
		 * priorité strictement else
		 */
		if ("*/".indexOf(str) > -1 && "*/".indexOf(pile) == -1)
			return (true); // inférieure
		else if ("+-".indexOf(str) > -1 && "=<>|&".indexOf(pile) > -1)
			return (true);
		else if ("=><".indexOf(str) > -1 && "|&".indexOf(pile) > -1)
			return (true);
		return (false);
	}

	private int isProcedure(String mot) { // vérifie si mot est une procédure
		for (int i = 0; i < wp.getNumberOfProcedure(); i++) {
			if (wp.getProcedure(i).name.equals(mot))
				return (i);
		}
		return (-1);
	}

	protected static String getNextWord() {
		String mot = "";
		char caractere;
		for (int i = 0; i < instruction.length(); i++) {
			caractere = instruction.charAt(i);
			if (caractere == ' ') {
				return mot;
			} else
				mot += caractere;
		}
		// System.out.println("mot: "+mot);
		return mot;
	}

	protected static void deleteFirstWord(String mot) {
		if (instruction.length() > mot.length())
			instruction = instruction.delete(0, mot.length() + 1);
		else
			instruction = new StringBuffer();
	}

	protected static String tueniveau(String car, Application app)
			throws myException {
		boolean error=true;
		String caractere = "";
		String car2 = null;
		if (car.equals("\\"))
			car2 = "\n"; // Dans ce cas là on déroule jusqu'à la prochaine
							// fin de boucle ou procédure
		int marqueur = 0;
		for (int i = 0; i < instruction.length(); i++) {
			// System.out.println("a "+caractere);
			caractere = String.valueOf(instruction.charAt(i));
			if (caractere.equals(car) | caractere.equals(car2)) {
				marqueur = i;
				if (caractere.equals("\\") && i != instruction.length() - 1) {
					/*
					 * On test si le caractère "\" est bien un caractère de fin
					 * de boucle et non du style "\e" ou "\#"
					 */
					if (instruction.charAt(i + 1) == ' ')
					{
						error=false;
						break;
					}
				} else{
					error=false;
					break;
				}
			}
		}
		
		// System.out.println("a "+caractere);
		if (error) {
			if (car2.equals("\n"))
				throw new myException(app, Logo.messages
						.getString("erreur_stop"));
			else
				throw new myException(app, Logo.messages
						.getString("erreur_retourne"));
		}
		if (marqueur + 2 > instruction.length())
			instruction = new StringBuffer(" ");// instruction.substring(marqueur,instruction.length());
		else
			instruction = instruction.delete(0, marqueur + 2);
		if (caractere.equals("\n")) {
			Interprete.en_cours.pop();
			Interprete.locale = Interprete.stockvariable.pop();
		} else {
			Primitive.stackLoop.pop();
		}
		return (caractere);
	}

	protected void setWorkspace(Workspace workspace) {
		wp = workspace;
		lanceprim.setWorkspace(workspace);
	}


	private boolean hasGeneralForm(int i) {
		// If it's a procedure
		if (i < 0)
			return (!wp.getProcedure(-i - 2).optVariables.isEmpty());
		return kernel.primitive.generalForm[i];
	}
	private void checkParenthesis() throws myException{
		if (!nom.isEmpty()){
			String name=nom.peek();
			if (name.equals("(")) {
					throw new myException(app,Logo.messages.getString("too_much_arguments"));
				
			}
		}
		
	}
	private void deleteLineNumber(){
		lineNumber="";
	}
}