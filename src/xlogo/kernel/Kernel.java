package xlogo.kernel;

import java.util.ArrayList;
import java.awt.Color;
import xlogo.gui.Editor;


/**
 * Title :        XLogo
 * Description :  XLogo is an interpreter for the Logo 
 * 						programming language
 * @author Loïc Le Coq
 */
import xlogo.Application;
public class Kernel {
	protected static long chrono = 0;
	protected ArrayFlow flows=new ArrayFlow(); // Contient les flux de lecture ou d'écriture

	protected static boolean mode_trace=false; // true si le mode trace est enclenchée (permet de suivre les procédures)
	
	// interprete the user command and launch primitive and procedure
	private Interprete interprete;
	// For all drawing operation
//	protected DrawPanel dg;
	// For primitive 
	protected Primitive primitive = null;
	private Workspace wp;
	private Application app;

	private MyCalculator myCalculator;
	
	public Kernel(Application app){
		this.app=app;
		this.wp=new Workspace(app);
		initCalculator(-1);
	}
	
	
	 public Workspace getWorkspace(){
	 	return wp;	
	 }
	 public void setWorkspace(Workspace workspace){
		 	wp=workspace;
		 	app.editeur=new Editor(app);
		 	interprete.setWorkspace(wp);
		 }
	 protected String listSearch() throws xlogo.utils.myException{
	 	return interprete.chercheListe();
	 }
	public void fcfg(Color color){
	 	app.getArdoise().fcfg(color);
	 }
	public Turtle getActiveTurtle(){
		return app.getArdoise().tortue;
	}
	public MyCalculator getCalculator(){
		return myCalculator;
	}
	
	public void fcc(Color color){
		 	app.getArdoise().fcc(color);
		 }
		public void vide_ecran(){
			app.getArdoise().videecran();
		}
		public void setNumberOfTurtles(int i){
			app.getArdoise().setNumberOfTurtles(i);
		}

		 public void setDrawingQuality(int id){
			 app.getArdoise().setQuality(id);
		}
		 public Color getScreenBackground(){
			 	return app.getArdoise().getBackgroundColor();
			 }

			public void change_image_tortue(String chemin){
				app.getArdoise().change_image_tortue(app,chemin);
				app.editeur.generateTurtleImage();
			}
			 public void initGraphics(){
				 app.getArdoise().initGraphics();
			 }

	 public void buildPrimitiveTreemap(int id){
		 	primitive.buildPrimitiveTreemap(id);
		 }
	 public String execute(StringBuffer st) throws xlogo.utils.myException{
		 	return interprete.execute(st);
	 } 
	 
	 protected void initCalculator(int s){
		 myCalculator=new MyCalculator(s,app);
		 
	 }
	public void initPrimitive(){
	 	primitive=new Primitive(app);	
	 }
	
	
	 public void initInterprete(){
		interprete=new Interprete(app);
	 }
	 /**
	  * Returns the InstructionBuffer containing all commands to execute 
	  */
	  public InstructionBuffer getInstructionBuffer(){
		  return interprete.getInstructionBuffer();
	  }

	 class ArrayFlow extends ArrayList<MyFlow>{
		ArrayFlow(){
			super(); 
		}
		private static final long serialVersionUID = 1L;
		protected int search(int id){
			for(int i=0;i<size();i++){
				if (get(i).getId()==id) return i;
			}
			return -1;
		}
		
		 
	 }
}
