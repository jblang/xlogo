package xlogo.gui;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.util.StringTokenizer;
import java.util.Stack;
import java.io.BufferedReader;
import java.io.StringReader;
import java.io.IOException;
import java.awt.print.*;
import java.awt.event.*;
import java.io.File;
import xlogo.utils.Utils;
import xlogo.utils.myException;
import xlogo.Application;
import xlogo.Popup;
import xlogo.Config;
import xlogo.kernel.Procedure;
import xlogo.kernel.Primitive;
import xlogo.kernel.Workspace;
import xlogo.Logo;
/**
 * Title :        XLogo
 * Description :  XLogo is an interpreter for the Logo 
 * 						programming language
 * @author Loïc Le Coq
 */

/* The main class for the Editor windows
 *  
 *  */
public class Editor extends JFrame implements ActionListener{
	private static final long serialVersionUID = 1L;
  private JToolBar menu=new JToolBar(JToolBar.HORIZONTAL);
  private JButton copier,coller,couper,imprimer,quit,lire,chercher,undo,redo;
  private boolean affichable=true;
  private JScrollPane jScrollPane1;
  private ZoneEdition zonedition;
  private JLabel labelCommand;
  private JTextField mainCommand;
  private JPanel panelCommand;
  private Popup jpop;
  private Application cadre;
  private Workspace wp;
  private ReplaceFrame sf;
  public Editor(Application cadre){
    this.cadre=cadre;
    this.wp=cadre.getKernel().getWorkspace();

    try {
      initGui();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  public Editor() {
  }
  protected void processWindowEvent(WindowEvent e) {
    if (e.getID()==WindowEvent.WINDOW_CLOSING){
      this.toFront();
    }
    else if (e.getID()==WindowEvent.WINDOW_ACTIVATED){
    	zonedition.requestFocus();
      
    }
  }
  public void analyseprocedure() throws EditeurException{
	  
	  String text=zonedition.getText();
	  text.replaceAll("\t", "  ");
	  StringReader sr=new StringReader(text);
	  BufferedReader br=new BufferedReader(sr);
	  String defineSentence="";
	  try{
		  while(br.ready()){
			  String comment="";
			  String line="";
			  String name="";
			  String body="";
			  Stack<String> variables=new Stack<String>();
			  Stack<String> optVariables=new Stack<String>();
			  Stack<StringBuffer> optVariablesExp=new Stack<StringBuffer>();
			  // Read and save the comments that appears before the procedure
			  while(br.ready()){
				  line=br.readLine();
				  if (null==line) break;
				  if (isComment(line)) comment+=line+"\n";
				  else {
				    if(!line.trim().equals("")) break;
				    else {comment+="\n";}
				  }
			  }
			  if (null==line) break;
		  	  // Read the first line
			  if (!comment.equals("")&&line.trim().equals(""))
				  structureException();
			  else {
				  StringTokenizer st=new StringTokenizer(line);
				  String token=st.nextToken();
				  // The first word must be "to" (or "pour" in French)
				  if (!token.toLowerCase().equals(Logo.messages.getString("pour").toLowerCase())) structureException();
				  // The second word must be the name of the procedure
				  if (st.hasMoreTokens()) name=st.nextToken().toLowerCase();
				  else structureException();
				  // Then, We read the variables
				  // :a :b :c :d .....
				  while(st.hasMoreTokens()){
					  token=st.nextToken();
					  if (token.startsWith(":")){
						  String var=ValidVariable(token);
						  variables.push(var);
					  }
					  // And finally, optional variables if there are some.
					  // [:a 100] [:b 20] [:c 234] ...........
					  else {
						  StringBuffer sb=new StringBuffer();
						  sb.append(token);
						  while(st.hasMoreTokens()){
							  sb.append(" ");
							  sb.append(st.nextToken());
						  }
					 
						  while (sb.length()>0){
							  if (sb.indexOf("[")!=0) structureException();
							  else {
								  sb.deleteCharAt(0);
								  String[] arg=new String[2];
								  extractList(sb,arg);
								  optVariables.push(arg[0].toLowerCase());
							//	  System.out.println(Utils.decoupe(arg[1]));
								  optVariablesExp.push(Utils.decoupe(arg[1]).append(" "));
							  }
						  }	 
					  }		 
				  }
			  }
			  // Then we read the body of the procedure until we find
			  // the word "end" (or "fin" in French)
			  boolean fin=false;
			  while(br.ready()){
				 line=br.readLine();
				  if (null==line) break;
				 if (line.trim().toLowerCase().equals(Logo.messages.getString("fin").toLowerCase())){
					 fin=true;
					 break;
				 }
				 else {
					 body+=line+"\n";
				 }
			 }
			 if (!fin) structureException();
			 defineSentence+=name+", ";
		     int id=isProcedure(name);
		     // If it's a new procedure
		     Procedure proc;
		     if (id==-1){
		    	 proc=new Procedure(name,variables.size(),variables,optVariables,optVariablesExp,affichable);
		    	 proc.instruction=body;
		    	 proc.comment=comment;
		    	 wp.procedureListPush(proc);
		     }
		     else {          // Si on redéfinit une procédure existante
		 		proc=wp.getProcedure(id);
		        proc.instruction=body;
		        proc.instr=null;
		    	 proc.comment=comment;
		        proc.variable=variables;
		        proc.optVariables=optVariables;
		        proc.optVariablesExp=optVariablesExp;
		        proc.nbparametre=variables.size();
		         wp.setProcedureList(id,proc);

		      }
		 	//	System.out.println(proc.toString());		     
		  }	 
//		    On crée les chaînes d'instruction formatées pour chaque procédure et les sauvegardes
		    for (int j=0;j<wp.getNumberOfProcedure();j++){
					Procedure pr=wp.getProcedure(j);
					pr.decoupe();
					pr.instruction_sauve=pr.instruction;
					pr.instr_sauve=pr.instr;
					pr.variable_sauve=new Stack<String>();
					for (int k=0;k<pr.variable.size();k++){
						pr.variable_sauve.push(pr.variable.get(k));
					}
//					System.out.println(pr.toString());
		    }

	    if (!defineSentence.equals("")&&affichable){
		      cadre.ecris("commentaire",Logo.messages.getString("definir")+" "+defineSentence.substring(0,defineSentence.length()-2)+".\n");
		      cadre.eraserUpdate();
		}
	  }	 
	catch(IOException e){}
  }
  private void structureException() throws EditeurException{
	  throw new EditeurException(this,Logo.messages.getString("erreur_editeur"));	  
  }
  
  // Check if the String st is a comment (starts with "#")
  private boolean isComment(String st)  {
	  if (st.trim().startsWith("#")) return true;
	  else return false;
  }
  // Check if the String var is a number
  private void isNumber(String var) throws EditeurException{
	  try{
		  Double.parseDouble(var);
		  structureException();
	  }
	  catch(NumberFormatException e){}
  }
  private void hasSpecialCharacter(String var) throws EditeurException{
      StringTokenizer check=new StringTokenizer(var, ":+-*/() []=<>&|", true);
      String mess=Logo.messages.getString("caractere_special_variable")+"\n"+Logo.messages.getString("caractere_special2")+"\n"+Logo.messages.getString("caractere_special3")+" :"+var;
      if (check.countTokens()>1) throw new EditeurException(this,mess);
      if (":+-*/() []=<>&|".indexOf(check.nextToken())>-1) throw new EditeurException(this,mess);	  
  }
  // Check if token is a valid variable
  //  and returns its name
  private String ValidVariable(String token) throws EditeurException{
      if (token.length()==1) structureException();
	  String var=token.substring(1,token.length());
      isNumber(var);
      hasSpecialCharacter(var);
      return var.toLowerCase();
	}
  private void extractList(StringBuffer sb,String[] args) throws EditeurException{
	    String variable="";
	    String expression="";
	    int compteur=1;
	    int id=0;
	    int id2=0;
	    boolean espace=false;
	    for (int i=0;i<sb.length();i++){
	    	char ch=sb.charAt(i);
	    	if (ch=='[') compteur++;
	    	else if (ch==']') {
	    			if (id==0){structureException();}
	    			compteur--;
	    		}
	    	else if (ch==' '){
	    		if (!variable.equals("")){
	    			if (!espace) id=i;
	    			espace=true;
	    		}
	    	}
	    	else {
	    		if (!espace) variable+=ch;
	    	}
	    	if (compteur==0) {id2=i;break;}
	    }
	    if (variable.startsWith(":")){
	    	variable=ValidVariable(variable);
	    }
	    else structureException();
	    if (compteur!=0) structureException(); 
	    expression=sb.substring(id+1,id2).trim();
	    if (expression.equals("")) structureException();
	  /*  System.out.println(id+" "+id2);
	    System.out.println("a"+expression+"a");
	    System.out.println("a"+variable+"a");*/
	    sb.delete(0, id2+1);
	    // delete unnecessary space
	     while(sb.length()!=0&&sb.charAt(0)==' ') sb.deleteCharAt(0);
	    args[0]=variable;
	    args[1]=expression;
	  }  
  
  
  private int isProcedure(String mot) throws EditeurException{   // vérifie si mot est une procédure
// Vérifier si c'est le nom d'une procédure de démarrage
    for (int i=0;i<wp.getNumberOfProcedure();i++){
      Procedure procedure=wp.getProcedure(i);
      if (procedure.name.equals(mot)&&procedure.affichable==false) throw new EditeurException(this,mot+" "+Logo.messages.getString("existe_deja"));
    }
// Vérifier si ce n'est pas un nombre:
   try{Double.parseDouble(mot);throw new EditeurException(this,Logo.messages.getString("erreur_nom_nombre_procedure"));}
   catch(NumberFormatException e){}
// Vérifier tout d'abord si le mot n'est pas une primitive.
    if (Primitive.primitives.containsKey(mot)) throw new EditeurException(this,mot+" "+Logo.messages.getString("existe_deja"));
    else{
    //ensuite s'il ne contient pas de caractères spéciaux "\"
    StringTokenizer decoupe=new StringTokenizer("a"+mot+"a",":\\+-*/() []=<>&|"); //on rajoute une lettre au mot au cas où le caractere spécial se trouve en début ou en fin de mot.
    if (decoupe.countTokens()>1) throw new EditeurException(this,Logo.messages.getString("caractere_special1")+"\n"+Logo.messages.getString("caractere_special2")+"\n"+Logo.messages.getString("caractere_special3")+" "+mot);
    }
    for (int i=0;i<wp.getNumberOfProcedure();i++){
      if (wp.getProcedure(i).name.equals(mot)) return(i);
    }
    return(-1);
  }

  /*String ligne(String texte){       // Extrait la ligne suivante de texte
    String ligne="";
    while(curseur<texte.length()){
      String caractere=texte.substring(curseur,curseur+1);
      curseur++;
      if (caractere.equals("\n")) return ligne;
      else ligne+=caractere;

    }
    return(ligne);
  }
  String mot(String ligne){       // Extrait le mot suivant dans la ligne
    String mot="";
    int i=0;
    while(i<ligne.length()){
      String caractere=ligne.substring(i,i+1);
      if (caractere.equals(" ")) {return mot;}
      else mot+=caractere;
      i++;
    }
    return(mot);
  }*/
  class EditeurException extends Exception{   // à générer en cas d'errreur dans la structure
	private static final long serialVersionUID = 1L;
	String message;
    Editor editeur;
	EditeurException(){}                      // des pour ... fin
    EditeurException(Editor editeur,String message){        // et des variables
    	this.editeur=editeur;
		MyTextAreaDialog jt=new MyTextAreaDialog(message);
		JOptionPane.showMessageDialog(this.editeur,jt,Logo.messages.getString("erreur"),JOptionPane.ERROR_MESSAGE);					
			for(int i=0;i<wp.getNumberOfProcedure();i++){ // On remémorise les anciennes définitions de procédures
				Procedure pr=wp.getProcedure(i);
				pr.variable=(Stack<String>)pr.variable_sauve.clone();
				pr.instr=pr.instr_sauve;
				pr.instruction=pr.instruction_sauve;
				pr.nbparametre=pr.variable.size();
			}
      editeur.toFront();
      editeur.zonedition.requestFocus();
    }
  }
  private void initGui() throws Exception {
    this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    
    // Init Toolbar button
    ImageIcon icopier,icoller,icouper,iimprimer,iquit,ichercher,iundo,iredo,iplay;
    icopier=new ImageIcon(Utils.dimensionne_image("editcopy.png",this));
    icoller=new ImageIcon(Utils.dimensionne_image("editpaste.png",this));
    icouper=new ImageIcon(Utils.dimensionne_image("editcut.png",this));
    iimprimer=new ImageIcon(Utils.dimensionne_image("fileprint.png",this));
    iquit=new ImageIcon(Utils.dimensionne_image("quit.png",this));
    ichercher=new ImageIcon(Utils.dimensionne_image("chercher.png",this));
    iundo=new ImageIcon(Utils.dimensionne_image("undo.png",this));
    iredo=new ImageIcon(Utils.dimensionne_image("redo.png",this));
    iplay=new ImageIcon(Utils.dimensionne_image("play.png",this));
    lire=new JButton();
    generateTurtleImage();
    copier=new JButton(icopier);
    coller=new JButton(icoller);
    couper=new JButton(icouper);
    imprimer=new JButton(iimprimer);
    quit=new JButton(iquit);
    chercher=new JButton(ichercher);
    undo=new JButton(iundo);
    redo=new JButton(iredo);
    
    // Init All other components
    labelCommand=new JLabel(Logo.messages.getString("mainCommand"),iplay,JLabel.LEFT);
    jScrollPane1 = new JScrollPane();
    zonedition = new ZoneEdition(this);
    mainCommand=new JTextField();
    panelCommand=new JPanel();
    jpop=new Popup(this,zonedition);
    sf=new ReplaceFrame(this,zonedition);
    

    copier.setToolTipText(Logo.messages.getString("menu.edition.copy"));
    couper.setToolTipText(Logo.messages.getString("menu.edition.cut"));
    coller.setToolTipText(Logo.messages.getString("menu.edition.paste"));
    imprimer.setToolTipText(Logo.messages.getString("imprimer_editeur"));
    lire.setToolTipText(Logo.messages.getString("lire_editeur"));
    quit.setToolTipText(Logo.messages.getString("quit_editeur"));
    chercher.setToolTipText(Logo.messages.getString("find"));
    undo.setToolTipText(Logo.messages.getString("editor.undo"));
    redo.setToolTipText(Logo.messages.getString("editor.redo"));
    
    copier.setActionCommand(Logo.messages.getString("menu.edition.copy"));
    couper.setActionCommand(Logo.messages.getString("menu.edition.cut"));
    coller.setActionCommand(Logo.messages.getString("menu.edition.paste"));
    imprimer.setActionCommand(Logo.messages.getString("imprimer_editeur"));
    lire.setActionCommand(Logo.messages.getString("lire_editeur"));
    quit.setActionCommand(Logo.messages.getString("quit_editeur"));
    chercher.setActionCommand(Logo.messages.getString("find"));
    undo.setActionCommand(Logo.messages.getString("editor.undo"));
    redo.setActionCommand(Logo.messages.getString("editor.redo"));
    
    undo.setEnabled(false);
    redo.setEnabled(false);
    
    lire.setMnemonic('Q');
    quit.setMnemonic('C');

    menu.add(lire);
    menu.addSeparator();
    menu.add(quit);
    menu.addSeparator();
    menu.add(imprimer);
    menu.addSeparator();
    menu.add(couper);
    menu.addSeparator();
    menu.add(copier);
    menu.addSeparator();
    menu.add(coller);
    menu.addSeparator();
    menu.add(chercher);
    menu.addSeparator();
    menu.add(undo);
    menu.addSeparator();
    menu.add(redo);
    
    imprimer.addActionListener(this);
    copier.addActionListener(this);
    couper.addActionListener(this);
    coller.addActionListener(this);
    lire.addActionListener(this);
    quit.addActionListener(this);
    chercher.addActionListener(this);
    undo.addActionListener(this);
    redo.addActionListener(this);
    
	MouseListener popupListener = new PopupListener();
    zonedition.addMouseListener(popupListener);
  
    labelCommand.setFont(Config.police);
    mainCommand.setFont(Config.police);
    initMainCommand();
    if (Config.mainCommand.length()<30) mainCommand.setPreferredSize(new Dimension(150,20));
    panelCommand.add(labelCommand);
    panelCommand.add(mainCommand);
    
    setIconImage(Toolkit.getDefaultToolkit().createImage(Utils.class.getResource("icone.png")));
    
    jScrollPane1.setPreferredSize(new Dimension(500, 500));
    this.setTitle(Logo.messages.getString("editeur"));
    this.getContentPane().add(menu, BorderLayout.NORTH);
    this.getContentPane().add(jScrollPane1, BorderLayout.CENTER);
    this.getContentPane().add(panelCommand,BorderLayout.SOUTH);
    jScrollPane1.getViewport().add(zonedition, null);
    sf=new ReplaceFrame(this,zonedition);
    pack();
  }

  public void actionPerformed(ActionEvent e){
	  String cmd=e.getActionCommand();
    if (cmd.equals(Logo.messages.getString("imprimer_editeur"))){
      Font font=zonedition.getFont();
      String txt=zonedition.getText();
      zonedition.setFont(new Font(font.getFontName(),Font.PLAIN,10));
      zonedition.setText(txt);
      PrinterJob job = PrinterJob.getPrinterJob();
      job.setPrintable(zonedition,job.defaultPage());
      double h_imp=job.defaultPage().getImageableHeight();
       java.awt.FontMetrics fm = zonedition.getFontMetrics(zonedition.getFont());
      zonedition.pages=new Stack<String>();
      StringTokenizer st = new StringTokenizer(txt, "\n");
      String page="";
   //   System.out.println("hauteur "+fm.getHeight()+" "+h_imp);
      int compteur=0;
      while (st.hasMoreTokens()) {
        String element = st.nextToken();
        compteur+=fm.getHeight();
        if (compteur>h_imp) {
          zonedition.pages.push(page);
	      page = element;
          compteur=fm.getHeight();
        }
        else page+= element+"\n";
      }
      if (!page.equals("")) zonedition.pages.push(page);
      if (job.printDialog()) {
        try {
          job.print();
        }
        catch (PrinterException ex) {
          System.out.println(ex.getMessage());
        }
      }
      font=zonedition.getFont();
      zonedition.setFont(new Font(font.getFontName(),Font.PLAIN,12));
      zonedition.setText(txt);
    }
    else if (cmd.equals(Logo.messages.getString("menu.edition.copy"))){zonedition.copy();}
    else if (cmd.equals(Logo.messages.getString("menu.edition.cut"))){zonedition.cut();}
    else if (cmd.equals(Logo.messages.getString("menu.edition.paste"))){zonedition.paste();}
    else if(cmd.equals(Logo.messages.getString("lire_editeur"))){
    	zonedition.setActive(false);
    	boolean visible=false;
      try{
      	analyseprocedure();
        this.zonedition.setText("");
		if (null!=cadre.tmp_path){
			Application.path=cadre.tmp_path;
			cadre.setTitle("XLOGO        "+Application.path);
			try {
				File f=new File(cadre.tmp_path);
				Config.defaultFolder= Utils.rajoute_backslash(f.getParent());
			}
			catch(NullPointerException e2){}
			cadre.tmp_path=null;
			cadre.setEnabled_Record(true);
			}
		if (!cadre.isEnabled_new()) cadre.setEnabled_New(true); //Si c'est la première fois qu'on enregistre, on active le menu nouveau
      }
      catch(EditeurException ex){visible=true;}
//			System.out.println(visible);
      setVisible(visible);
      cadre.focus_Commande();
      if (Config.eraseImage){ //Effacer la zone de dessin    
      	myException.lance=true;
		cadre.error=true;
		try{
			while(!cadre.commande_isEditable()) Thread.sleep(100);
		}
		catch(InterruptedException e1){}
				cadre.getKernel().vide_ecran();
				cadre.focus_Commande();
      }
      Config.mainCommand=mainCommand.getText();
      
    }
		// Si on quitte sans enregistrer
    else if (cmd.equals(Logo.messages.getString("quit_editeur"))){
    	zonedition.setActive(false);
      zonedition.setText("");
      setVisible(false);
	  if (Config.eraseImage){ //Effacer la zone de dessin    
		myException.lance=true;
		cadre.error=true;
		while(!cadre.commande_isEditable()) {
			try{
				Thread.sleep(100);
			}
			catch(InterruptedException e2){}
		}
		cadre.getKernel().vide_ecran();
	  }
		if (null!=cadre.tmp_path) {
			cadre.tmp_path=null;
		} 
		cadre.focus_Commande();
    }
    else if (cmd.equals(Logo.messages.getString("find"))){
    	if (!sf.isVisible()) {
    		sf.setSize(350, 350);
    		sf.setVisible(true);
    	}
    }
    // Undo Action
    else if (cmd.equals(Logo.messages.getString("editor.undo"))){
		zonedition.getUndoManager().undo();
		updateUndoRedoButtons();
    }
    // Redo Action
    else if (cmd.equals(Logo.messages.getString("editor.redo"))){
    	zonedition.getUndoManager().redo();
    	updateUndoRedoButtons();
    }
  }
  public void initMainCommand(){
	  mainCommand.setText(Config.mainCommand);
  } 
  // Change Syntax Highlighting for the editor
  public void initStyles(int c_comment,int sty_comment,int c_primitive,int sty_primitive,
		int c_parenthese, int sty_parenthese, int c_operande, int sty_operande){
  	zonedition.getDsd().initStyles(Config.coloration_commentaire,Config.style_commentaire,Config.coloration_primitive,Config.style_primitive,
			Config.coloration_parenthese,Config.style_parenthese,Config.coloration_operande,Config.style_operande);	
  }
  // Enable or disable Syntax Highlighting 
	public void setColoration(boolean b){
		zonedition.getDsd().setColoration(b);
	}
	
	public void setEditorFont(Font f){
		zonedition.setFont(f);
	}
	public void setAffichable(boolean b){
		affichable=b;
	}
	public boolean getAffichable(){
		return affichable;
	}
	public void setEditorText(String txt){
		zonedition.setText(txt);
	}
	public void setEditorStyledText(String txt){
		zonedition.ecris(txt);
	}
	public void focus_zonedition(){
		zonedition.requestFocus();
	}
	/**
	 * Create the Turtle Image for the Button "save change"
	 */
	public void generateTurtleImage(){
		  ImageIcon read=new ImageIcon(Utils.dimensionne_image("preview"+Config.activeTurtle+".png",this));
		    lire.setIcon(read);
	}
	/**
	 * This method displays the editor (if necessary)
	 * and adds all defined procedures
	 */
	public void open(){
	    if (!cadre.editeur.isVisible()) {
		      for(int i=0;i<wp.getNumberOfProcedure();i++){
			        Procedure procedure =wp.getProcedure(i);
			        setEditorStyledText(procedure.toString());
			      }
			      initMainCommand();
			      setTitle(Logo.messages.getString("editeur"));
			      discardAllEdits();
			      setVisible(true);
			      toFront();
			      focus_zonedition();
	    }
	    else {
	      cadre.editeur.setVisible(false);
	      cadre.editeur.setVisible(true);
	      cadre.editeur.focus_zonedition();
	    }
	}
	public void discardAllEdits(){
		   zonedition.getUndoManager().discardAllEdits();
		      updateUndoRedoButtons();
	}
	protected void updateUndoRedoButtons(){
		if (zonedition.getUndoManager().canRedo()) redo.setEnabled(true);
		else redo.setEnabled(false);
		if (zonedition.getUndoManager().canUndo()) undo.setEnabled(true);
		else undo.setEnabled(false);
	}
  
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

/*
    public int print(Graphics g,PageFormat pf, int pi) throws PrinterException{

      int h_imp=(int)pf.getImageableHeight(); //hauteur imprimable sur chaque page
      java.awt.FontMetrics fm = g.getFontMetrics(this.getFont());
      int hauteur_texte=fm.getHeight();
      System.out.println(hauteur_texte);

      if(pi<this.getHeight()/h_imp+1){
        g.translate((int)pf.getImageableX(),(int)pf.getImageableY()-pi*h_imp);
       // System.out.println((int)pf.getImageableY()-pi*h_imp);
        paint(g);
      return(Printable.PAGE_EXISTS);
    }
    else return Printable.NO_SUCH_PAGE;
  }

*
*
*
*
*
*
*
*
*
*
*
*
*
*
*
*
public void analyseprocedure()throws EditeurException{ 
//    pile_sauvegarde=new Object[cadre.primitive.procedure.size()];
  //  for(int i=0;i<pile_sauvegarde.length;i++){
    //  pile_sauvegarde[i]=cadre.primitive.procedure.get(i);
    //}
	
    String commentaire="";
    curseur=0;
    String texte=zonedition.getText();
    int i=0;
    int id=-1;
    String ligne="";
    String mot="";
    StringTokenizer st=new StringTokenizer(ligne);
    Procedure proc=null;

    while(curseur<texte.length()){
      boolean end=false;
      while (!st.hasMoreTokens()&&curseur<texte.length()){
        ligne=ligne(texte);
        st=new StringTokenizer(ligne); //virer les espaces inutiles
        if (st.hasMoreTokens()) {mot=st.nextToken();break;}
        else mot="";
      }
      if (mot=="") break;
      if (i==0){          //si c'est la ligne de définition
        if (!mot.toLowerCase().equals(Logo.messages.getString("pour"))) throw new EditeurException(this,Logo.messages.getString("erreur_editeur1")+"\n"+Logo.messages.getString("erreur_editeur2")+"\n"+Logo.messages.getString("erreur_editeur3"));
        if (st.hasMoreTokens()) mot=st.nextToken().toLowerCase();
        else throw new EditeurException(this,Logo.messages.getString("erreur_editeur1")+"\n"+Logo.messages.getString("erreur_editeur2")+"\n"+Logo.messages.getString("erreur_editeur3"));
        Stack variable=new Stack();
       	id=isProcedure(mot);
        if (id==-1){      // Si c'est une procédure nouvelle
          String nomproc=mot;
          int nbparam=0;
          while(st.hasMoreTokens()){ 
            nbparam++;
            mot=st.nextToken().toLowerCase();
            if (mot.substring(0,1).equals(":")&&mot.length()!=1) {
              String var=mot.substring(1,mot.length());
              try{	
              	Double.parseDouble(var);
  				throw new EditeurException(this,Logo.messages.getString("erreur_editeur1")+"\n"+Logo.messages.getString("erreur_editeur2")+"\n"+Logo.messages.getString("erreur_editeur3"));
              }
              catch(NumberFormatException e2){}
              StringTokenizer verif=new StringTokenizer(var, ":+-/(*) []=<>&|", true);
              String mess=Logo.messages.getString("caractere_special_variable")+"\n"+Logo.messages.getString("caractere_special2")+"\n"+Logo.messages.getString("caractere_special3")+" :"+var;
              if (verif.countTokens()>1) throw new EditeurException(this,mess);
              if (":+*-/() []=<>&|".indexOf(verif.nextToken())>-1) throw new EditeurException(this,mess);
              if (variable.search(var)==-1) variable.push(var);
            }
            else {throw new EditeurException(this,Logo.messages.getString("erreur_editeur1")+"\n"+Logo.messages.getString("erreur_editeur2")+"\n"+Logo.messages.getString("erreur_editeur3"));}
          }
       //   wp.procedureListPush(new Procedure(nomproc,nbparam,variable,wp.getNumberOfProcedure(),affichable));
        }
        else {          // Si on redéfinit une procédure existante
			    proc=wp.getProcedure(id);
          proc.variable.removeAllElements();
          proc.instruction="";
          proc.instr=null;
          int param=0;
					
					while(st.hasMoreTokens()){
            param++;
            mot=st.nextToken().toLowerCase();
            if (mot.substring(0,1).equals(":")&&mot.length()!=1) {
              String var=mot.substring(1,mot.length());
              if (proc.variable.search(var)==-1) proc.variable.push(var);
            }
            else {throw new EditeurException(this,Logo.messages.getString("erreur_editeur1")+"\n"+Logo.messages.getString("erreur_editeur2")+"\n"+Logo.messages.getString("erreur_editeur3"));}
          }
        proc.nbparametre=param;
        wp.setProcedureList(id,proc);
        }
      }
      else{ //si on est pas à la première ligne de définition.
        if (st.countTokens()!=0||!mot.toLowerCase().equals(Logo.messages.getString("fin"))){
          if (id==-1){      //Procédure nouvelle
            (wp.getProcedure(wp.getNumberOfProcedure()-1)).instruction+=ligne+"\n";
          }
          else{             // Procédure à redéfinir
            proc.instruction+=ligne+"\n";
          }
        }
        else end=true;      // Fin de définition
      }
      if (end) {
        i=0;
        end=false;
        if (id==-1) commentaire+=wp.procedureListPeek().name+", ";
        else {
          commentaire+=wp.getProcedure(id).name+", ";
          wp.setProcedureList(id,proc);
        }
      }
      else i++;
      while (st.hasMoreTokens()){st.nextToken();} // On vide st
    }
    if (i!=0) {     // On teste si erreur dans l'écriture des procédures
      if (id==-1) {
        wp.procedureListPop();
      }
//      else  cadre.primitive.procedure.remove(id);
      
      throw new EditeurException(this,Logo.messages.getString("erreur_editeur1")+"\n"+Logo.messages.getString("erreur_editeur2")+"\n"+Logo.messages.getString("erreur_editeur3"));
    }
//    On crée les chaînes d'instruction formatées pour chaque procédure et les sauvegardes
    for (int j=0;j<wp.getNumberOfProcedure();j++){
			Procedure pr=wp.getProcedure(j);
			pr.decoupe();
			pr.instruction_sauve=pr.instruction;
			pr.instr_sauve=pr.instr;
			pr.variable_sauve=new Stack();
			for (int k=0;k<pr.variable.size();k++){
				pr.variable_sauve.push(pr.variable.get(k));
			}
//			System.out.println(pr.toString());
    }
    if (!commentaire.equals("")&&affichable){
      cadre.ecris("commentaire",Logo.messages.getString("definir")+" "+commentaire.substring(0,commentaire.length()-2)+".\n");
      cadre.eraserUpdate();
    }
  }*/