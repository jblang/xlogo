package xlogo.kernel;

import java.util.Stack;
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
	protected static Stack flux=new Stack(); // Contient les flux de lecture ou d'écriture
	protected static Stack<String> chemin_flux=new Stack<String>(); // Contient le chemin absolu vers chacun des fichiers
	protected static Stack<String> description_flux=new Stack<String>(); // Contient les identifiants de chacun des fluxs
	protected static Stack<String> fin_flux=new Stack<String>(); // Contient 1 si la fin du flux est atteinte rien sinon
	protected static boolean mode_trace=false; // true si le mode trace est enclenchée (permet de suivre les procédures)
	
	// interprete the user command and launch primitive and procedure
	private Interprete interprete;
	// For all drawing operation
//	protected DrawPanel dg;
	// For primitive 
	protected Primitive primitive = null;
	private Workspace wp;
	private Application app;

	
	public Kernel(Application app){
		this.app=app;
		this.wp=new Workspace(app);

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
	public void initPrimitive(){
	 	primitive=new Primitive(app);	
	 }
	 public void initInterprete(){
		interprete=new Interprete(app);
	 }

}
