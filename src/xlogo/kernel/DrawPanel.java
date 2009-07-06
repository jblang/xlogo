package xlogo.kernel;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Point;
import javax.swing.JViewport;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Transparency;
import java.awt.geom.*;
import javax.vecmath.Color3f;
import java.awt.FontMetrics;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.geom.Arc2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.font.TextLayout;
import java.awt.Dimension;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.CropImageFilter;
import java.awt.image.ReplicateScaleFilter;
import java.awt.image.FilteredImageSource;

import com.sun.j3d.utils.geometry.Text2D;

import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import javax.swing.JPanel;
import javax.imageio.ImageIO;
import javax.media.j3d.*;
import javax.vecmath.Vector3d;
import javax.vecmath.Matrix3d;
import javax.vecmath.Point3d;
import java.util.Stack;
import java.util.Set;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;
import java.awt.event.*;
import xlogo.Application;
import xlogo.gui.preferences.Panel_Font;
import xlogo.utils.Utils;
import xlogo.utils.myException;
import xlogo.Config;
import xlogo.Logo;
import xlogo.kernel.gui.*;
import xlogo.kernel.perspective.*;
/**
 * Title :        XLogo
 * Description :  XLogo is an interpreter for the Logo 
 * 						programming language
 * @author Loïc Le Coq
 */
 public class DrawPanel extends JPanel implements MouseMotionListener,MouseListener {

	private static final long serialVersionUID = 1L;
	public Turtle tortue;
	public Turtle[] tortues; 
	
	/**
	 * When a turtle is active on screen, its number is added to this stack 
	 * 
	 */
	public Stack<String> tortues_visibles;

	/**
	 * this int indicates the window mode, default 0
	 */
	protected static int WINDOW_MODE = 0;
	/**
	 *  WINDOW MODE: 0 <br>
	 *  Turtles can go out the drawing area
	 */
	protected static final int WINDOW_CLASSIC=0;
	/**
	 *  WRAP MODE: 1 <br>
	 *  Turtles can go out the drawing area and reappear on the other side
	 */
	protected static final int WINDOW_WRAP=1;
	/**
	 * CLOSE MODE: 2 <br>
	 * Turtles can't go out the drawing area
	 */
	protected static final int WINDOW_CLOSE=2;
	/**
	 * Perspective MODE <br>
	 * The screen is a projection of the 3D universe 
	 */
	protected static final int WINDOW_3D=3;

	/** Boolean for animation mode */
	public static boolean classicMode=true; // true si classique false si animation
	/** Animation mode:  */
	public final static boolean MODE_ANIMATION=false;
	/** Classic mode */
	public final static boolean MODE_CLASSIC=true;
	
	/**
	 * 	default predefined colors 
	 */
	public static final Color[] defaultColors={Color.BLACK,Color.RED,Color.GREEN,Color.YELLOW,Color.BLUE,
			Color.MAGENTA,Color.CYAN,Color.WHITE,Color.GRAY,Color.LIGHT_GRAY,new Color(128,0,0),new Color(0,128,0),
			new Color(0,0,128),new Color(255,128,0),Color.PINK,new Color(128,0,255),new Color(153,102,0)};

	
	/** The id for the drawing font (with primitive label)*/
	protected int police_etiquette;
	/** The default drawing area color */
	private Color couleurfond = Color.white;
	
	private Shape shape=null;

	private Line2D line; 
	private Rectangle2D rec;
	private Application cadre;
	/** This Image is used for Buffering the drawing*/
	public static BufferedImage dessin;
	/** Graphics of the BufferedImage dessin*/
	private Graphics2D g;
	/** The scale for the zoom*/
	public static double zoom=1;

	
	/** All Gui Objects on the drawing area are stored in the GuiMap gm*/
	private GuiMap gm;
	/** The Stroke for the triangle turtle*/
	private BasicStroke crayon_triangle = new BasicStroke(1);
	/**
	 * Tools for 3D Mode
	 */
	private World3D w3d=null;
	/**
	 * Boolean that indicates if the interpreter is recording polygon in 3D Mode
	 */
	protected static int record3D=0;
	protected final static int record3D_NONE=0;
	protected final static int record3D_POLYGON=1;
	protected final static int record3D_LINE=2;
	protected final static int record3D_POINT=3;
	protected final static int record3D_TEXT=4;
	
	/**
	 * Boolean that indicates if the interpreter is recording polygon in 2D Mode
	 */
	private static int record2D=0;
	private final static int record2D_NONE=0;
	private  final static int record2D_POLYGON=1;
	private Vector<Point2D.Double> stackTriangle;
	
	
	protected static Element3D poly;
	
	private double[] coords;
	private double oldx, oldy,x1,y1,x2,y2;
	// Were used for clipping
	//	private double nx,ny,vx,vy,factor,length;
	//	private GeneralPath gp;
	// private Arc2D clipArc;
		private Arc2D arc;
		/** Button number when user click on the drawing area*/
	private int bouton_souris=0;  	// Numéro du bouton de souris appuyé sur la zone de dessin
	/** Last coords for last mouse event*/
	private String possouris="[ 0 0 ] ";	// Coordonnées du point du dernier événement souris
		
	/** Notify if a mouse event has occured*/	
	private boolean lissouris=false; //Indique si un événement souris est intervenu depuis le debut du programme
	/**
	 * The rectangular selection zone
	 */
	private Rectangle selection;
	/**
	 * Color for the rectangular selection
	 */
	private Color colorSelection;
	/**
	 * The First clicked point when the rectangular selection is created
	 */
	Point origine;
	public DrawPanel(Application cadre){
		this.gm=cadre.getKernel().getWorkspace().getGuiMap();
		 setLayout(null);
		 this.setPreferredSize(new Dimension(
				 (int)(Config.imageWidth*zoom),(int)( Config.imageHeight*zoom)));
		 this.cadre=cadre;
    	addMouseListener(this);
    	addMouseMotionListener(this);
		initGraphics();
	}
	/**
	 * This method is used to draw for primitive "forward" and "backward"
	 * @param arg Number of steps
	 */
	protected void av(double arg) {
	//	Graphics2D g=(Graphics2D)dessin.getGraphics();
		
		oldx = tortue.corX;
		oldy = tortue.corY;
		if (DrawPanel.WINDOW_MODE == DrawPanel.WINDOW_CLASSIC) { //mode fenetre
			montrecacheTortue(false);
		
			tortue.corX = tortue.corX + arg
					* Math.cos(tortue.angle);
			tortue.corY = tortue.corY - arg
					* Math.sin(tortue.angle);
			if (tortue.isPenDown()) {
				if (tortue.isPenReverse()) {
					g.setColor(couleurfond);
					g.setXORMode(tortue.couleurcrayon);
				} else {
					g.setColor(tortue.couleurcrayon);
					g.setPaintMode();
				}
				if (null==line) line=new Line2D.Double();
			/*	if (null==gp) gp=new GeneralPath();
				else gp.reset();*/
				if (oldx < tortue.corX){
					x1=oldx;y1=oldy;x2=tortue.corX;y2=tortue.corY;
				}
				if (oldx>tortue.corX){
					x2=oldx;y2=oldy;x1=tortue.corX;y1=tortue.corY;
				}
				else if (oldx==tortue.corX){
					if (oldy<tortue.corY){
						x2=oldx;y2=oldy;x1=tortue.corX;y1=tortue.corY;						
						}
					else{ 
						x1=oldx;y1=oldy;x2=tortue.corX;y2=tortue.corY;
					}
				}
				
				line.setLine(x1,y1,x2,y2);
								
				/*
				// perpendicular vector
				nx=y1-y2;
				ny=x2-x1;
				length=Math.sqrt(nx*nx+ny*ny);
				if (length!=0){
					factor=(1+tortue.getPenWidth())/length;
					vx=x2-x1;
					vy=y2-y1;
					gp.moveTo((float)(x1-vx*factor-nx*factor),
						(float)(y1-vy*factor-ny*factor));
					gp.lineTo((float)(x1-vx*factor+nx*factor),
						(float)(y1-vy*factor+ny*factor));		
					gp.lineTo((float)(x2+vx*factor+nx*factor),
						(float)(y2+vy*factor+ny*factor));
					gp.lineTo((float)(x2+vx*factor-nx*factor),
						(float)(y2+vy*factor-ny*factor));
					gp.lineTo((float)(x1-vx*factor-nx*factor),
						(float)(y1-vy*factor-ny*factor));	
				}
				else{
					float width=tortue.getPenWidth()+0.5f;
					gp.moveTo((float)(x1-width),
							(float)(y1-width));
					gp.lineTo((float)(x1+width),
							(float)(y1-width));		
					gp.lineTo((float)(x1+width),
							(float)(y1+width));
					gp.lineTo((float)(x1-width),
							(float)(y1+width));
					gp.lineTo((float)(x1-width),
							(float)(y1-width));
				}
				shape=gp;*/
				tryRecord2DMode(tortue.corX,tortue.corY);	
				g.draw(line);
				 //if (!tortue.isVisible()) 
					 clip();
		//		g.dispose();
			}
			montrecacheTortue(true);
		} else if (DrawPanel.WINDOW_MODE == DrawPanel.WINDOW_WRAP) { //mode enroule
			trace_enroule(arg, oldx, oldy);
		} else if (DrawPanel.WINDOW_MODE == DrawPanel.WINDOW_CLOSE) { //mode clos
			try {
				trace_ferme(oldx, oldy, arg);
			} catch (myException e) {
			}
		}
		else if (DrawPanel.WINDOW_MODE==DrawPanel.WINDOW_3D){
			montrecacheTortue(false);
    		tortue.X=tortue.X+arg*tortue.getRotationMatrix()[0][1];
    		tortue.Y=tortue.Y+arg*tortue.getRotationMatrix()[1][1];
    		tortue.Z=tortue.Z+arg*tortue.getRotationMatrix()[2][1];
       		
    		double tmp[]=new double[3];
    		tmp[0]=tortue.X;
     		tmp[1]=tortue.Y;
     		tmp[2]=tortue.Z;
     		
     		tmp=this.toScreenCoord(tmp,true);
    		tortue.corX = tmp[0];
			tortue.corY = tmp[1];

						
			if (tortue.isPenDown()) {
				if (tortue.isPenReverse()) {
					g.setColor(couleurfond);
					g.setXORMode(tortue.couleurcrayon);
				} else {
					g.setColor(tortue.couleurcrayon);
					g.setPaintMode();
				}
				if (null==line) line=new Line2D.Double();

				if (oldx < tortue.corX){
					x1=oldx;y1=oldy;x2=tortue.corX;y2=tortue.corY;
				}
				if (oldx>tortue.corX){
					x2=oldx;y2=oldy;x1=tortue.corX;y1=tortue.corY;
				}
				else if (oldx==tortue.corX){
					if (oldy<tortue.corY){
						x2=oldx;y2=oldy;x1=tortue.corX;y1=tortue.corY;						
						}
					else{ 
						x1=oldx;y1=oldy;x2=tortue.corX;y2=tortue.corY;
					}
				}
				
				line.setLine(x1,y1,x2,y2);
								
				g.draw(line);
					 clip();
			}
			montrecacheTortue(true);
		}
	}
	
	/**
	 * This method is used for drawing with primitive "right" or "left"
	 * @param arg The angle 
	 */
	protected void td(double arg) {
//		System.out.println(tortue.angle);
		if (tortue.isVisible())
			montrecacheTortue(false);
		if (!enabled3D()){
			tortue.heading = ((tortue.heading + arg) % 360 + 360) % 360;
			tortue.angle = Math.toRadians(90 - tortue.heading);
		}
		else{
			tortue.setRotationMatrix(w3d.multiply(tortue.getRotationMatrix(),w3d.rotationZ(-arg)));
			double[] tmp=w3d.rotationToEuler(tortue.getRotationMatrix());
			tortue.heading=tmp[2];
			tortue.roll=tmp[1];
			tortue.pitch=tmp[0];
		}
		if (tortue.isVisible())
			montrecacheTortue(true);
		Interprete.operande = false;
	}
	/**
	 * This method is used for drawing with primitive "rightroll" or "leftroll"
	 * @param arg
	 */
	protected void rightroll(double arg) {
//		System.out.println(tortue.angle);
		if (tortue.isVisible())
			montrecacheTortue(false);
		if (enabled3D()){
			tortue.setRotationMatrix(w3d.multiply(tortue.getRotationMatrix(),w3d.rotationY(-arg)));
			double[] tmp=w3d.rotationToEuler(tortue.getRotationMatrix());
			tortue.heading=tmp[2];
			tortue.roll=tmp[1];
			tortue.pitch=tmp[0];
		}
		if (tortue.isVisible())
			montrecacheTortue(true);
		Interprete.operande = false;
	}	
	/**
	 * This method is used for drawing with primitive "uppitch" or "downpitch"
	 * @param arg
	 */
	protected void uppitch(double arg) {
//		System.out.println(tortue.angle);
		if (tortue.isVisible())
			montrecacheTortue(false);
		if (enabled3D()){
			tortue.setRotationMatrix(w3d.multiply(tortue.getRotationMatrix(),w3d.rotationX(arg)));
			double[] tmp=w3d.rotationToEuler(tortue.getRotationMatrix());
			tortue.heading=tmp[2];
			tortue.roll=tmp[1];
			tortue.pitch=tmp[0];
		}
		if (tortue.isVisible())
			montrecacheTortue(true);
		Interprete.operande = false;
	}
	/**
	 * This method set the turtle's Roll
	 * @param arg The new roll 
	 */
	protected void setRoll(double arg){
		if (tortue.isVisible())
			montrecacheTortue(false);
		tortue.roll=arg;
		tortue.setRotationMatrix(w3d.EulerToRotation(-tortue.roll, tortue.pitch, -tortue.heading));
		if (tortue.isVisible())
			montrecacheTortue(true);
		Interprete.operande=false;
	}
	/**
	 * This method set the turtle's heading
	 * @param arg The new heading 
	 */
	protected void setHeading(double arg){
		if (tortue.isVisible())
			montrecacheTortue(false);
		tortue.heading=arg;
		tortue.setRotationMatrix(w3d.EulerToRotation(-tortue.roll, tortue.pitch, -tortue.heading));
		if (tortue.isVisible())
			montrecacheTortue(true);
		Interprete.operande=false;
	}
	/**
	 * This method set the turtle's pitch
	 * @param arg The new pitch
	 */
	protected void setPitch(double arg){
		if (tortue.isVisible())
			montrecacheTortue(false);
		tortue.pitch=arg;
		tortue.setRotationMatrix(w3d.EulerToRotation(-tortue.roll, tortue.pitch, -tortue.heading));
		if (tortue.isVisible())
			montrecacheTortue(true);
		Interprete.operande=false;
	}
	/**
	 * 
	 * This method set the turtle's orientation
	 * @param arg The new orientation
	 * @throws myException If the list doesn't contain three numbers
	 */
	protected void setOrientation(String arg) throws myException{
		initCoords();
		if (tortue.isVisible())
			montrecacheTortue(false);
		extractCoords(arg,Utils.primitiveName("3d.setorientation"));			
		tortue.roll = coords[0];
		tortue.pitch = coords[1];				
		tortue.heading = coords[2];
		tortue.setRotationMatrix(w3d.EulerToRotation(-tortue.roll, tortue.pitch, -tortue.heading));
		if (tortue.isVisible())
			montrecacheTortue(true);
		Interprete.operande=false;
	}
	/**
	 * Primitive "origine"
	 */
		protected void origine(){ // primitive origine
			try {
				if (!enabled3D())
					fpos("0 0");
				else fpos("0 0 0");
			} catch (myException e) {
			}
			if (tortue.isVisible())
				montrecacheTortue(false);
			tortue.heading = 0;
			tortue.angle = Math.PI / 2;
			tortue.roll=0;
			tortue.pitch=0;
			if (enabled3D()) 
				tortue.setRotationMatrix(w3d.EulerToRotation(-tortue.roll, tortue.pitch, -tortue.heading));
			if (tortue.isVisible())
				montrecacheTortue(true);
		}
		
		
		/**
		 * Primitive distance
		 * @param liste The coords 
		 * @param nom
		 * @return The distance from the turtle position to this point
		 * @throws myException If bad format list
		 */
		protected double distance(String liste) throws myException {
	
			initCoords();
			extractCoords(liste,Utils.primitiveName("distance"));			
			double distance;
			if (!enabled3D()){
				coords=this.toScreenCoord(coords,false);
				distance = Math.sqrt(Math.pow(tortue.corX - coords[0], 2)
						+ Math.pow(tortue.corY - coords[1], 2));
			}
			else distance= Math.sqrt(Math.pow(tortue.X - coords[0], 2)
						+ Math.pow(tortue.Y - coords[1], 2)+Math.pow(tortue.Z - coords[2], 2));
			return distance;
		}
		protected double[] vers3D(String liste) throws myException{
			double[] tmp=new double [3];
			initCoords();
			extractCoords(liste,Utils.primitiveName("vers"));
			tmp[0]=coords[0]-tortue.X;
			tmp[1]=coords[1]-tortue.Y;
			tmp[2]=coords[2]-tortue.Z;
			double length=Math.sqrt(Math.pow(tmp[0],2)+Math.pow(tmp[1],2)+Math.pow(tmp[2],2));
			if (length==0) return tmp;
			tmp[0]=tmp[0]/length;
			tmp[1]=tmp[1]/length;
			tmp[2]=tmp[2]/length;
			double heading=Math.acos(tmp[1]);
			double f=Math.sin(heading);
			double tr_x=-tmp[0]/f;
			double tr_y=-tmp[2]/f;
			double roll=Math.atan2(tr_y, tr_x);
			tmp[0]=-Math.toDegrees(roll);
			tmp[1]=0;
			tmp[2]=-Math.toDegrees(heading);
			for (int i=0;i<3;i++){
				if (tmp[i]<0) tmp[i]+=360;
			}
			return tmp;
		}
		
		/**
		 * Primitive towards in 2D MODE 
		 * @param liste the coordinate for the point
		 * @return the rotation angle 
		 * @throws myException if Bad format List
		 */
		protected double vers2D(String liste) throws myException{
			initCoords();
			extractCoords(liste,Utils.primitiveName("vers"));		
			double angle;
			coords=this.toScreenCoord(coords, false);
			if (tortue.corY == coords[1]) {
				if (coords[0] > tortue.corX)
					angle = 90;
				else if (coords[0] == tortue.corX)
					angle = 0;
				else
					angle = 270;
			} 
			else if (tortue.corX == coords[0]) {
				if (tortue.corY > coords[1])
					angle = 0;
				else
					angle = 180;
			} 	
			else {
				angle = Math.toDegrees(Math.atan(Math
						.abs(coords[0] - tortue.corX)
						/ Math.abs(tortue.corY - coords[1])));
		//		System.out.println(coords[0] - tortue.corX+" "+Math.abs(tortue.corY - coords[1])+" "+angle);
				if (coords[0] > tortue.corX && coords[1] > tortue.corY)
					angle = 180 - angle; // 2eme quadrant
				else if (coords[0] < tortue.corX && coords[1] > tortue.corY)
					angle = 180 + angle; // 3eme quadrant
				else if (coords[0] < tortue.corX && coords[1] < tortue.corY)
					angle = 360 - angle; // 4eme quadrant
			}
			return angle;	
		}
		/**
		 * Draw with the primitive "setposition" in 2D mode or 3D
		 * @param liste The list with the coordinates to move
		 * @throws myException If the coordinates are invalid
		 */
			protected void fpos(String liste) throws myException {
				initCoords();
				oldx = tortue.corX;
				oldy = tortue.corY;
				extractCoords(liste,Utils.primitiveName("drawing.fpos"));			
				montrecacheTortue(false);
				if (enabled3D()) {
					tortue.X = coords[0];
					tortue.Y = coords[1];				
					tortue.Z = coords[2];
				}
				coords=toScreenCoord(coords,true);

				tortue.corX=coords[0];
				tortue.corY=coords[1];
				if (tortue.isPenDown()) {
					if (tortue.isPenReverse()) {
						g.setColor(couleurfond);
						g.setXORMode(tortue.couleurcrayon);
					} else {
						g.setColor(tortue.couleurcrayon);
						g.setPaintMode();
					}
					if (null==line) line=new Line2D.Double();
					if (oldx < tortue.corX){
						x1=oldx;y1=oldy;x2=tortue.corX;y2=tortue.corY;
					}
					if (oldx>tortue.corX){
						x2=oldx;y2=oldy;x1=tortue.corX;y1=tortue.corY;
					}
					else if (oldx==tortue.corX){
						if (oldy<tortue.corY){
							x2=oldx;y2=oldy;x1=tortue.corX;y1=tortue.corY;						
							}
						else{ 
							x1=oldx;y1=oldy;x2=tortue.corX;y2=tortue.corY;
						}
					}
					line.setLine(x1,y1,x2,y2);
					tryRecord2DMode(tortue.corX,tortue.corY);
					g.draw(line);
					clip();
				}
				montrecacheTortue(true);
			}
	public void drawEllipseArc(double xAxis,double yAxis, double angleRotation,double xCenter,double yCenter, double angleStart, double angleExtent){
		montrecacheTortue(false);
		arc=new Arc2D.Double(-xAxis,-yAxis,2*xAxis,2*yAxis,angleStart,angleExtent,Arc2D.Double.OPEN);
		if (tortue.isPenReverse()) {
			g.setColor(couleurfond);
			g.setXORMode(tortue.couleurcrayon);
		} else {
			g.setColor(tortue.couleurcrayon);
			g.setPaintMode();
		}
		double tmpx=Config.imageWidth/2+xCenter;
		double tmpy=Config.imageHeight/2-yCenter;
		g.translate(tmpx, tmpy);
		g.rotate(-angleRotation);
		g.draw(arc);
		g.rotate(angleRotation);
		g.translate(-tmpx, -tmpy);
	/*	if (null==clipArc) clipArc=new Arc2D.Double();
		clipArc.setArcByCenter(tortue.corX,tortue.corY,
				rayon+2+tortue.getPenWidth(),0,360, Arc2D.OPEN);*/
		clip();
		montrecacheTortue(true); // on efface la tortue si elle st visible
	}
	/**
	 * This method draw an arc on the drawing area
	 * @param rayon The radius
	 * @param pangle Starting angle
	 * @param fangle End angle
	 */
	protected void arc(double rayon, double pangle, double fangle) {
		// Put fangle and pangle between 0 and 360
		fangle = ((90 - fangle) % 360); 
		pangle = ((90 - pangle) % 360);
		if (fangle<0) fangle+=360;
		if (pangle<0) pangle+=360;
		// Calculate angle extend
		double angle=pangle-fangle;
		if (angle<0) angle+=360;
		montrecacheTortue(false);
		if (null==arc) arc=new Arc2D.Double();
		if (!enabled3D()){
			if (DrawPanel.WINDOW_MODE==DrawPanel.WINDOW_WRAP) centers=new Vector<Point2D.Double>(); 
			arc2D(tortue.corX,tortue.corY,rayon,fangle,pangle);

	/*	if (null==gp) gp=new GeneralPath();
		else gp.reset();
		gp.moveTo((float)(tortue.corX-rayon-tortue.getPenWidth()),
				(float)(tortue.corY-rayon-tortue.getPenWidth());
		gp.lineTo((float)(tortue.corX-rayon-tortue.getPenWidth()),
				(float)(tortue.corY-rayon-tortue.getPenWidth()));
		gp.lineTo((float)(tortue.corX-rayon-tortue.getPenWidth()),
				(float)(tortue.corY-rayon-tortue.getPenWidth()));
		gp.lineTo((float)(tortue.corX-rayon-tortue.getPenWidth()),
				(float)(tortue.corY-rayon-tortue.getPenWidth()));
		gp.lineTo((float)(tortue.corX-rayon-tortue.getPenWidth()),
				(float)(tortue.corY-rayon-tortue.getPenWidth()));*/
/*		if (null==rec) rec=new Rectangle2D.Double();
		rec.setRect(tortue.corX-rayon-tortue.getPenWidth(),
			tortue.corY-rayon-tortue.getPenWidth(),
			2*(rayon+tortue.getPenWidth()),2*(rayon+tortue.getPenWidth()));*/
		clip();
		
		}
		else{
			arcCircle3D(rayon,fangle,angle);
		}
		montrecacheTortue(true); 
	}
	private void arc2D(double x, double y, double radius,double fangle, double pangle){
		arc.setArcByCenter(x,y,radius,
				fangle,pangle, Arc2D.OPEN);
		if (tortue.isPenReverse()) {
			g.setColor(couleurfond);
			g.setXORMode(tortue.couleurcrayon);
		} else {
			g.setColor(tortue.couleurcrayon);
			g.setPaintMode();
		}
		g.draw(arc);
		clip();
		if (DrawPanel.WINDOW_MODE==DrawPanel.WINDOW_WRAP){
			if (x+radius>Config.imageWidth&& x<=Config.imageWidth){
				pt=new Point2D.Double(-Config.imageWidth+x,y);
				if (! centers.contains(pt))	{
					centers.add(pt);
					arc2D(-Config.imageWidth+x,y,radius,fangle,pangle);
				}
			}
			if (x-radius<0&& x>=0){
				pt=new Point2D.Double(Config.imageWidth+x,y);
				if (! centers.contains(pt))	{
					centers.add(pt);
					arc2D(Config.imageWidth+x,y,radius,fangle,pangle);
				}
			}
			if (y-radius<0&& y>=0){
				pt=new Point2D.Double(x,Config.imageHeight+y);
				if (! centers.contains(pt))	{
					centers.add(pt);
					arc2D(x,Config.imageHeight+y,radius,fangle,pangle);
				}
			}
			if (y+radius>Config.imageHeight&&y<=Config.imageHeight){
				pt=new Point2D.Double(x,-Config.imageHeight+y);
				if (! centers.contains(pt))	{
					centers.add(pt);
					arc2D(x,-Config.imageHeight+y,radius,fangle,pangle);
				}
			}
		}
	}
	
	
	private void arcCircle3D(double radius,double angleStart,double angleExtent){
		if (null==arc) arc=new Arc2D.Double();
		arc.setArcByCenter(0,0,radius,
					angleStart,angleExtent, Arc2D.OPEN);
		Shape s=transformShape(arc);
		if (tortue.isPenReverse()) {
			g.setColor(couleurfond);
			g.setXORMode(tortue.couleurcrayon);
		} else {
			g.setColor(tortue.couleurcrayon);
			g.setPaintMode();
		}
		g.draw(s);
		if (DrawPanel.record3D==DrawPanel.record3D_LINE||DrawPanel.record3D==DrawPanel.record3D_POLYGON){
			recordArcCircle3D(radius,angleStart,angleExtent);
		}
	}
	
	
	/**
	 * 	
	 * returns the color for the pixel "ob" 
	 * @param liste: The list containing the coordinates of the pixel
	 * @return Color of this pixel
	 * @throws myException If the list doesn't contain coordinates 
	 */
		protected Color guessColorPoint(String liste) throws myException {
			initCoords();
			extractCoords(liste,Utils.primitiveName("tc"));
			coords=toScreenCoord(coords,false);
			int couleur = -1;
			int x=(int)coords[0];
			int y=(int)coords[1];
			if (0 < x && x < Config.imageWidth && 0 < y && y < Config.imageHeight) {
				couleur = DrawPanel.dessin.getRGB(x, y);
			}
			return new Color(couleur);
		}
		/**
		 * This method draw a circle from the turtle position on the drawing area
		 * @param radius The radius of the circle
		 */
	protected void circle(double radius) {
		montrecacheTortue(false);
		if (null==arc) arc=new Arc2D.Double();
		if (!enabled3D()){
			if (DrawPanel.WINDOW_MODE==DrawPanel.WINDOW_WRAP) centers=new Vector<Point2D.Double>(); 
			circle2D(tortue.corX,tortue.corY,radius);
	/*	if (null==clipArc) clipArc=new Arc2D.Double();
		clipArc.setArcByCenter(tortue.corX,tortue.corY,
				rayon+2+tortue.getPenWidth(),0,360, Arc2D.OPEN);*/
		}
		else{			
			circle3D(radius);
		}
		montrecacheTortue(true); // on efface la tortue si elle st visible
	}
	/**
	 * This method draws a circle in 2D mode
	 * in WRAP mode, makes recursion to draw all circle part on the screen
	 * @param x  x circle center 
	 * @param y y circle center
	 * @param circle radius
	 */
	private Point2D.Double pt;
	private  Vector <Point2D.Double> centers;
	private void circle2D(double x,double y, double radius){
		
		arc.setArcByCenter(x,y,radius,
				0,360, Arc2D.OPEN);
		
		if (tortue.isPenReverse()) {
				g.setColor(couleurfond);
				g.setXORMode(tortue.couleurcrayon);
			} else {
				g.setColor(tortue.couleurcrayon);
				g.setPaintMode();
			}
			g.draw(arc);
			clip();
			if (DrawPanel.WINDOW_MODE==DrawPanel.WINDOW_WRAP){
				if (x+radius>Config.imageWidth&& x<=Config.imageWidth){
					pt=new Point2D.Double(-Config.imageWidth+x,y);
					if (! centers.contains(pt))	{
						centers.add(pt);
						circle2D(-Config.imageWidth+x,y,radius);
					}
				}
				if (x-radius<0&& x>=0){
					pt=new Point2D.Double(Config.imageWidth+x,y);
					if (! centers.contains(pt))	{
						centers.add(pt);
						circle2D(Config.imageWidth+x,y,radius);
					}
				}
				if (y-radius<0&& y>=0){
					pt=new Point2D.Double(x,Config.imageHeight+y);
					if (! centers.contains(pt))	{
						centers.add(pt);
						circle2D(x,Config.imageHeight+y,radius);
					}
				}
				if (y+radius>Config.imageHeight&&y<=Config.imageHeight){
					pt=new Point2D.Double(x,-Config.imageHeight+y);
					if (! centers.contains(pt))	{
						centers.add(pt);
						circle2D(x,-Config.imageHeight+y,radius);
					}
				}
			}		
	}
	
	/**
	 * used for drawing with primitive "dot"
	 * @param liste The list with the dot coordinates
	 * @throws myException If the list is invalid coordinates
	 */
	protected void point(String liste) throws myException {
		initCoords();
		extractCoords(liste,Utils.primitiveName("drawing.point"));
		coords=toScreenCoord(coords,true);
//		System.out.println(coords[0]+" "+coords[1]+" "+Config.imageHeight+" "+Config.imageWidth);
		if (coords[0]>0 && coords[1]>0 && coords[0]<Config.imageWidth && coords[1] < Config.imageHeight) {
			if (tortue.isPenReverse()) {
				g.setColor(couleurfond);
				g.setXORMode(tortue.couleurcrayon);
				
			} else {
				g.setColor(tortue.couleurcrayon);
				g.setPaintMode();
			}	
			if (rec==null) rec=new Rectangle2D.Double();
			 // High quality
			if (Config.quality==Config.QUALITY_HIGH){
				double width=tortue.getPenWidth();
				rec.setRect(coords[0]-width+0.5,coords[1]-width+0.5,
						2*width,2*width);
			}
			// Normal or Low Quality
			else{
				// penWidth is 2k or 2k+1??
				int intWidth=(int)(2*tortue.getPenWidth()+0.5);
				if (intWidth%2==1){
					double width=tortue.getPenWidth()-0.5;
//					System.out.println(coords[0]+" "+coords[1]);
					rec.setRect(coords[0]-width,coords[1]-width,
							2*width+1,2*width+1);
				}
				else {
					double width=tortue.getPenWidth();
					rec.setRect(coords[0]-width,coords[1]-width,
							2*width,2*width);	
				}
			}
				if (Config.penShape==Config.PEN_SHAPE_SQUARE){
					g.fill(rec);
				}
				else if (Config.penShape==Config.PEN_SHAPE_OVAL){
					if (null==arc) arc=new Arc2D.Double();
					arc.setArcByCenter(coords[0],coords[1],0,0,360,Arc2D.OPEN);
					g.draw(arc);
				}
				clip();
			}
		}
	
	
	
/**
 * 
 */
	
	private void circle3D(double radius){
		
		// In camera world,
		// the circle is the intersection of
		// - a plane with the following equation: ax+by+cz+d=0 <-> f(x,y,z)=0
		// - and a sphere with the following equation: (x-tx)^2+(y-ty)^2+(z-tz)^2=R^2 <-> g(x,y,z)=0
		// I found the cone equation resolving f(x/lambda,y/lambda,z/lambda)=0=g(x/lambda,y/lambda,z/lambda)
		
		double[] v=new double[3];
		for(int i=0;i<3;i++){
			v[i]=tortue.getRotationMatrix()[i][2];
		}
		v[0]+=w3d.xCamera;
		v[1]+=w3d.yCamera;
		v[2]+=w3d.zCamera;
		w3d.toCameraWorld(v);
		// Now v contains coordinates of a normal vector to the plane in camera world coordinates
		double a=v[0];
		double b=v[1];
		double c=v[2];
		
		// We convert the turtle coordinates
		v[0]=tortue.X;
		v[1]=tortue.Y;
		v[2]=tortue.Z;
		w3d.toCameraWorld(v);
		
		double x=v[0];
		double y=v[1];
		double z=v[2];
		// We calculate the number d for the plane equation
		double d=-a*x-b*y-c*z;
		
		// We have to work with Bigdecimal because of precision problems
		
		BigDecimal[] big=new BigDecimal[6];
		BigDecimal bx=new BigDecimal(x);
		BigDecimal by=new BigDecimal(y);
		BigDecimal bz=new BigDecimal(z);
		BigDecimal ba=new BigDecimal(a);
		BigDecimal bb=new BigDecimal(b);
		BigDecimal bc=new BigDecimal(c);
		BigDecimal bd=new BigDecimal(d);
		BigDecimal deux=new BigDecimal("2");
		BigDecimal screenDistance=new BigDecimal(w3d.screenDistance);
		BigDecimal bradius=new BigDecimal(String.valueOf(radius));
		
		// Now we calculate the coefficient for the conic ax^2+bxy+cy^2+dx+ey+f=0
		// Saved in an array
		
		// lambda=(x*x+y*y+z*z-radius*radius);
		BigDecimal lambda=bx.pow(2).add(by.pow(2)).add(bz.pow(2)).subtract(bradius.pow(2));
		
		// x^2 coeff
		//	d*d+2*d*x*a+a*a*lambda;
		big[0]=bd.pow(2).add(bd.multiply(bx).multiply(ba).multiply(deux)).add(ba.pow(2).multiply(lambda));
		// xy coeff
		// 2*d*x*b+2*d*y*a+2*a*b*lambda;
		big[1]=deux.multiply(bd).multiply(bx).multiply(bb).add(deux.multiply(bd).multiply(by).multiply(ba)).add(deux.multiply(ba).multiply(bb).multiply(lambda));
		// y^2 coeff
		// d*d+2*d*y*b+b*b*lambda;
		big[2]=bd.pow(2).add(bd.multiply(by).multiply(bb).multiply(deux)).add(bb.pow(2).multiply(lambda));
		// x coeff
		// 2*w3d.screenDistance*(d*x*c+d*z*a+lambda*a*c);
		big[3]=deux.multiply(screenDistance).multiply(bd.multiply(bx).multiply(bc).add(bd.multiply(bz).multiply(ba)).add(lambda.multiply(ba).multiply(bc)));
		// y coeff
		// 2*w3d.screenDistance*(d*y*c+d*z*b+lambda*b*c);
		big[4]=deux.multiply(screenDistance).multiply(bd.multiply(by).multiply(bc).add(bd.multiply(bz).multiply(bb)).add(lambda.multiply(bb).multiply(bc)));
		// Numbers
		// Math.pow(w3d.screenDistance,2)*(d*d+2*d*z*c+lambda*c*c);
		big[5]=screenDistance.pow(2).multiply(bd.pow(2).add(deux.multiply(bd).multiply(bz).multiply(bc)).add(lambda.multiply(bc.pow(2))));
		new Conic(cadre,big);
		if (DrawPanel.record3D==DrawPanel.record3D_LINE||DrawPanel.record3D==DrawPanel.record3D_POLYGON){
			recordArcCircle3D(radius,0,360);
		}
	}
	/**
	 * This method records this circle in the polygon's List 
	 * @param radius The circle's radius
	 * @param angleStart The starting Angle
	 * @param angleExtent The angle for the sector
	 */
	public void recordArcCircle3D(double radius,double angleStart,double angleExtent){
		double[][] d=tortue.getRotationMatrix();
		Matrix3d m=new Matrix3d(d[0][0],d[0][1],d[0][2],d[1][0],d[1][1],d[1][2],d[2][0],d[2][1],d[2][2]);
		// Vector X
		Point3d v1=new Point3d(radius/1000,0,0);
		Transform3D t=new Transform3D(m,new Vector3d(),1);
		t.transform(v1);
		// Vector Y
		Point3d v2=new Point3d(0,radius/1000,0);
		t.transform(v2);

		// Turtle position
		Point3d pos=new Point3d(tortue.X/1000,tortue.Y/1000,tortue.Z/1000);
		int indexMax=(int)angleExtent;
		if (indexMax!=angleExtent) indexMax+=2;
		else indexMax+=1;
		if (null!=DrawPanel.poly&&DrawPanel.poly.getVertexCount()>1) {
			try{DrawPanel.poly.addToScene();}
			catch(myException e){}
		}
		if (DrawPanel.record3D==DrawPanel.record3D_POLYGON) {
			DrawPanel.poly=new ElementPolygon(cadre);
			DrawPanel.poly.addVertex(pos, tortue.couleurcrayon);
		}
		else {
			DrawPanel.poly=new ElementLine(cadre);
		}

		for(int i=0;i<indexMax-1;i++){
			Point3d tmp1=new Point3d(v1);
			tmp1.scale(Math.cos(Math.toRadians(angleStart+i)));
			Point3d  tmp2=new Point3d(v2);
			tmp2.scale(Math.sin(Math.toRadians(angleStart+i)));		
			tmp1.add(tmp2);
			tmp1.add(pos);
			DrawPanel.poly.addVertex(tmp1, tortue.couleurcrayon);
		}
		Point3d tmp1=new Point3d(v1);
		tmp1.scale(Math.cos(Math.toRadians(angleStart+angleExtent)));
		Point3d  tmp2=new Point3d(v2);
		tmp2.scale(Math.sin(Math.toRadians(angleStart+angleExtent)));		
		tmp1.add(tmp2);
		tmp1.add(pos);
		DrawPanel.poly.addVertex(tmp1, tortue.couleurcrayon);
	}
	
/**
 * Load an image and draw it on the drawing area 
 * @param image The image to draw
 */
	protected void chargeimage(BufferedImage image) {
		if (tortue.isVisible())
			montrecacheTortue(false);
		g.setPaintMode();
		g.translate(tortue.corX, tortue.corY);
		g.rotate(-tortue.angle);
		g.drawImage(image, null, 0,0);
		g.rotate(tortue.angle);
		g.translate(-tortue.corX, -tortue.corY);

		clip();
//		repaint();
/*		if (null==rec) rec=new Rectangle2D.Double();
		rec.setRect(tortue.corX,tortue.corY,
				image.getWidth(),image.getHeight());*/
		if (tortue.isVisible())
			montrecacheTortue(true);
	}
	/**
	 * To guess the length before going out the drawing area in WRAP mode
	 * @param mini The minimum distance before leaving
	 * @param maxi The maximum distance before leaving
	 * @param oldx The X turtle location
	 * @param oldy The Y turtle location
	 * @return the number of steps (Recursive dichotomy)
	 */
	private double trouve_longueur(double mini, double maxi, double oldx, double oldy) {
		// renvoie la longueur dont on peut encore avancer
		if (Math.abs(maxi - mini) < 0.5){
			return (mini);}
		else {
			double milieu = (mini + maxi) / 2;
			double nx = oldx + milieu * Math.cos(tortue.angle);
			double ny = oldy - milieu * Math.sin(tortue.angle);
			if (nx < 0 || nx > Config.imageWidth|| ny < 0 || ny > Config.imageHeight)
				return trouve_longueur(mini, milieu, oldx, oldy);
			else
				return trouve_longueur(milieu, maxi, oldx, oldy);
		}
	}
/**
 * This method is used for drawing with primitive forward, backward in WRAP MODE
 * @param arg the length to forward
 * @param oldx X position
 * @param oldy Y position
 */
	private void trace_enroule(double arg, double oldx, double oldy) {
		boolean re = false;
		if (arg < 0) {
			re = true;
		}
		double diagonale=Math.sqrt(Math.pow(Config.imageWidth,2)+Math.pow(Config.imageHeight,2))+1;
		double longueur;
		if (re)
			longueur = trouve_longueur(0, -diagonale, oldx, oldy);
		else
			longueur = trouve_longueur(0, diagonale, oldx, oldy);
//		System.out.println(diagonale+" "+oldx+" "+oldy);
		while (Math.abs(longueur) < Math.abs(arg)) {
		//	System.out.println(Math.abs(longueur)+" "+Math.abs(arg));
			arg -= longueur;
			DrawPanel.WINDOW_MODE = DrawPanel.WINDOW_CLASSIC;
			av(longueur);
			//System.out.println(Math.abs(longueur)+" "+Math.abs(arg));
			if (cadre.error)
				break; //permet d'interrompre avec le bouton stop
			DrawPanel.WINDOW_MODE = DrawPanel.WINDOW_WRAP;
			if (Config.turtleSpeed != 0) {
				try {
					Thread.sleep(Config.turtleSpeed * 5);
				} catch (InterruptedException e) {
				}
			}
			if (tortue.isVisible())
				this.montrecacheTortue(false);
				if (re) tortue.heading=(tortue.heading+180)%360;
			if (tortue.corX > Config.imageWidth-1
					&& (tortue.heading < 180 && tortue.heading != 0)) {
				tortue.corX = 0;
				if (tortue.corY > Config.imageHeight-1
						&& (tortue.heading > 90 && tortue.heading < 270))
					tortue.corY = 0;
				else if (tortue.corY < 1
						&& (tortue.heading < 90 || tortue.heading > 270))
					tortue.corY = Config.imageHeight;
			} else if (tortue.corX < 1 && tortue.heading > 180) {
				tortue.corX = Config.imageWidth;
				if (tortue.corY > Config.imageHeight-1
						&& (tortue.heading > 90 && tortue.heading < 270))
					tortue.corY = 0;
				else if (tortue.corY < 1
						&& (tortue.heading < 90 || tortue.heading > 270))
					tortue.corY = Config.imageHeight;
			} else if (tortue.corY > Config.imageHeight-1)
				tortue.corY = 0;
			else if (tortue.corY < 1)
				tortue.corY = Config.imageHeight;
			if (re) tortue.heading=(tortue.heading+180)%360;
			if (tortue.isVisible())
				this.montrecacheTortue(true);
			if (re)
				longueur = trouve_longueur(0, -diagonale, tortue.corX,
						tortue.corY);
			else
				longueur = trouve_longueur(0, diagonale, tortue.corX,
						tortue.corY);
		}
		DrawPanel.WINDOW_MODE = DrawPanel.WINDOW_CLASSIC;
		if (!cadre.error)
			av(arg);
		DrawPanel.WINDOW_MODE = DrawPanel.WINDOW_WRAP;
	}
/**
 * This method is used for drawing with primitive forward, backward in CLOSE MODE
 * @param oldx X position
 * @param oldy Y position
 * @param arg The length to forward
 * @throws myException
 */
	private void trace_ferme(double oldx, double oldy, double arg) throws myException {
		boolean re = false;
		double longueur;
		double diagonale=Math.sqrt(Math.pow(Config.imageWidth,2)+Math.pow(Config.imageHeight,2))+1;
		if (arg < 0)
			re = true;
		if (re)
			longueur = trouve_longueur(0, -diagonale, oldx, oldy);
		else
			longueur = trouve_longueur(0, diagonale, oldx, oldy);
		if (Math.abs(longueur) < Math.abs(arg))
			throw new myException(cadre, Logo.messages
					.getString("erreur_sortie1")
					+ "\n"
					+ Logo.messages.getString("erreur_sortie2")
					+ Math.abs((int) (longueur)));
		else {
			DrawPanel.WINDOW_MODE = DrawPanel.WINDOW_CLASSIC;
			av(arg);
			DrawPanel.WINDOW_MODE = DrawPanel.WINDOW_CLOSE;
		}
	}
	/**
	 * This method extract coords from a list <br>
	 * X is stored in coords(0], Y stored in coords[1], Z Stored in coords[2]
	 * @param liste The list
	 * @param prim The calling primitive
	 * @throws myException If List isn't a list coordinate
	 */
	
	private void extractCoords(String liste,String prim)throws myException{
		StringTokenizer st = new StringTokenizer(liste);
		try {
			for(int i=0;i<coords.length;i++){
			coords[i]=1;
			if (!st.hasMoreTokens())
				throw new myException(cadre, prim
						+ " " + Logo.messages.getString("n_aime_pas") + liste
						+ Logo.messages.getString("comme_parametre"));
			String element = st.nextToken();
			if (element.equals("-")) {
				if (st.hasMoreTokens())
					element = st.nextToken();
				coords[i] = -1;
			}
			coords[i] = coords[i] * Double.parseDouble(element);
			}
		
			} catch (NumberFormatException e) {
			throw new myException(cadre, prim
					+ " " + Logo.messages.getString("n_aime_pas") + liste
					+ Logo.messages.getString("comme_parametre"));
		}
		if (st.hasMoreTokens())
			throw new myException(cadre, prim
					+ " " + Logo.messages.getString("n_aime_pas") + liste
					+ Logo.messages.getString("comme_parametre"));
	}
	/**
	 * This method sets the drawing area to perspective mode 
	 */
	
	protected void perspective(){
    	if (!enabled3D()) {
    		Config.drawXAxis=false;
    		Config.drawYAxis=false;
    		Config.drawGrid=false;
    		change_image_tortue(cadre,"tortue0.png");
        	montrecacheTortue(false);
    		DrawPanel.WINDOW_MODE=DrawPanel.WINDOW_3D;
        	w3d=new World3D();
        	montrecacheTortue(true);        	
    	}
	}
	/**
	 * This method sets the drawing area to Wrap, Close or Window mode
	 * @param id The window Mode
	 */
	protected void setWindowMode(int id){
		if (DrawPanel.WINDOW_MODE!=id) {
    		montrecacheTortue(false);
    		DrawPanel.WINDOW_MODE=id;
        	w3d=null;
    		montrecacheTortue(true);
    	}
	}
	
	
	/**
	 * This method converts the coordinates contained in "coords" towards the coords on the drawing area
	 */
	double[] toScreenCoord(double[] coord,boolean drawPoly){
		// If Mode perspective is active
		if (enabled3D()){
	//		w3d.toScreenCoord(coord);	
			// camera world
			// If we have to record the polygon coordinates
    		if (DrawPanel.record3D!=DrawPanel.record3D_NONE&&DrawPanel.record3D!=DrawPanel.record3D_TEXT&&drawPoly){
				
    			DrawPanel.poly.addVertex(new Point3d(coord[0]/1000,coord[1]/1000,coord[2]/1000),tortue.couleurcrayon);
			}    
			
			w3d.toCameraWorld(coord);
 		
    		// Convert to screen Coordinates
    		w3d.cameraToScreen(coord);    
		}
		// Mode2D
		else {
			coord[0]=Config.imageWidth/2+coord[0];
			coord[1]=Config.imageHeight/2-coord[1];
		}
		return coord;
	}

 
	 
	/**
	 * This method creates an instance of coord with the valid size:<br>
	 * size 2 for 2D coordinates<br>
	 * size 3 for 3D coordinates
	 */
	 
	private void initCoords(){

		if (null==coords) coords=new double[2];
		if (enabled3D()){
			if (coords.length!=3) coords=new double[3];
			}		
		else  {
			if (coords.length!=2) coords=new double[2];
		}
	}
	public boolean  enabled3D(){
		return (DrawPanel.WINDOW_MODE==DrawPanel.WINDOW_3D);
	}

	/**
	 * For hideturtle and showturtle
	 */
	protected void ct_mt() {
		if (null == tortue.tort) {
			g.setXORMode(couleurfond);
			g.setColor(tortue.couleurcrayon);
			tortue.drawTriangle();
			BasicStroke crayon_actuel = (BasicStroke) g.getStroke();
			if (crayon_actuel.getLineWidth() == 1)
				g.draw(tortue.triangle);
			else {
				g.setStroke(crayon_triangle);
				g.draw(tortue.triangle);
				g.setStroke(crayon_actuel);
			}
		} else {
			g.setXORMode(couleurfond);
			double angle = Math.PI / 2 - tortue.angle;
			float x = (float) (tortue.corX * Math.cos(angle) + tortue.corY
					* Math.sin(angle));
			float y = (float) (-tortue.corX * Math.sin(angle) + tortue.corY
					* Math.cos(angle));
			g.rotate(angle);
			g.drawImage(tortue.tort, (int) x - tortue.largeur / 2,
					(int) y - tortue.hauteur / 2, this);
			g.rotate(-angle);
		}
/*		if (null==rec) rec=new Rectangle2D.Double();
		rec.setRect(tortue.corX - tortue.gabarit,
				tortue.corY - tortue.gabarit,
				tortue.gabarit * 2,
				tortue.gabarit * 2);
	*/
		clip();
	
/*		clip((int) (tortue.corX - tortue.gabarit),
				(int) (tortue.corY - tortue.gabarit),
				tortue.gabarit * 2, tortue.gabarit * 2);*/
	}
	/**
	 * When the turtle has to be redrawn, this method erase the turtle on the drawing screen
	 * 
	 */
	protected void montrecacheTortue(boolean b) {
			g.setColor(couleurfond);
			for (int i = 0; i < tortues_visibles.size(); i++) {
				int id = Integer.parseInt(tortues_visibles.get(i));
				// Turtle triangle
				if (null == tortues[id].tort) {
					g.setXORMode(couleurfond);
					g.setColor(tortues[id].couleurmodedessin);
					tortues[id].drawTriangle();
					BasicStroke crayon_actuel = (BasicStroke) g.getStroke();
					if (crayon_actuel.getLineWidth() == 1)
						g.draw(tortues[id].triangle);
					else {
						g.setStroke(crayon_triangle);
						g.draw(tortues[id].triangle);
						g.setStroke(crayon_actuel);
					}
				} else {
					// Image turtle
					g.setXORMode(couleurfond);
					double angle = Math.PI / 2 - tortues[id].angle;
					float x = (float) (tortues[id].corX * Math.cos(angle) + tortues[id].corY
							* Math.sin(angle));
					float y = (float) (-tortues[id].corX * Math.sin(angle) + tortues[id].corY
							* Math.cos(angle));
					g.rotate(angle);
					g.drawImage(tortues[id].tort, (int) x
							- tortues[id].largeur / 2, (int) y
							- tortues[id].hauteur / 2, this);
					g.rotate(-angle);
				}
				/*if (null==rec) rec=new Rectangle2D.Double();
				rec.setRect(tortues[id].corX - tortues[id].gabarit,
						tortues[id].corY - tortues[id].gabarit,
						tortues[id].gabarit * 2,
						tortues[id].gabarit * 2);
				shape=rec;*/
				if (b) clip();
			}
		}
	
	
	
/*	private void montrecacheTortue() {
	//	Graphics2D g=(Graphics2D)dessin.getGraphics();
		g.setColor(couleurfond);
		for (int i = 0; i < tortues_visibles.size(); i++) {
			int id = Integer.parseInt(String.valueOf(tortues_visibles
					.get(i)));

			if (null == tortues[id].tort) {
				g.setXORMode(couleurfond);
				g.setColor(tortues[id].couleurmodedessin);
				tortues[id].coord();
				BasicStroke crayon_actuel = (BasicStroke) g.getStroke();
				if (crayon_actuel.getLineWidth() == 1)
					g.draw(tortues[id].triangle);
				else {
					g.setStroke(crayon_triangle);
					g.draw(tortues[id].triangle);
					g.setStroke(crayon_actuel);
				}
			} else {
				g.setXORMode(couleurfond);
				double angle = Math.PI / 2 - tortues[id].angle;
				float x = (float) (tortues[id].corX * Math.cos(angle) + tortues[id].corY
						* Math.sin(angle));
				float y = (float) (-tortues[id].corX * Math.sin(angle) + tortues[id].corY
						* Math.cos(angle));
				g.rotate(angle);
				g.drawImage(tortues[id].tort, (int) x
						- tortues[id].largeur / 2, (int) y
						- tortues[id].hauteur / 2, cadre.getArdoise());
				g.rotate(-angle);
			}
	/*		if (null==rec) rec=new Rectangle2D.Double();
			rec.setRect(tortues[id].corX - tortues[id].gabarit,
					tortues[id].corY - tortues[id].gabarit,
					tortues[id].gabarit * 2,
					tortues[id].gabarit * 2);
	
			clip();
		//	g.dispose();
		}
	}
*/
	/**
	 * Primitive clearscreen
	 */
	protected void videecran() {
		// Delete all Gui Component
		Set<String> set=gm.keySet();
		Iterator<String> it=set.iterator();
		while(it.hasNext()){
			String element=it.next();
			gui=gm.get(element).getGuiObject();
			it.remove();
			if (SwingUtilities.isEventDispatchThread()){
				remove(gui);
				validate();
			}
			else {
				try{
					SwingUtilities.invokeAndWait(new Runnable(){
						public void run(){
							remove(gui);
							validate();						
						}
					});
				}
				catch(Exception e){}
			}
		}
		
		
		// Delete List Polygon in 3D mode
//		DrawPanel.listPoly=new Vector<Shape3D>();
//		DrawPanel.listText=new Vector<TransformGroup>();
		// Erase the 3d viewer if visible
		if (null!=cadre.getViewer3D())	{
			cadre.getViewer3D().clearScreen();
			System.gc();
		}
		
		
		g.setPaintMode();
		couleurfond=Config.screencolor;
		g.setColor(Config.screencolor);
		g.fillRect(0, 0, Config.imageWidth,Config.imageHeight);
		stopRecord2DPolygon();
		
		// Draw Grid 
		g.setStroke(new BasicStroke(1));
		drawGrid();
		drawXAxis();
		drawYAxis();
		// Init Turtles
		if (null == tortues[0])
			tortues[0] = new Turtle(cadre);
		// The active turtle will be the turtle 0
		tortue = tortues[0]; 
		tortue.id = 0;
		// We delete all other turtles
		for (int i = 1; i < tortues.length; i++) { 
			tortues[i] = null;
		}
		tortues_visibles.removeAllElements();
		tortues_visibles.push("0");
		g.setColor(tortue.couleurcrayon);
		clip();
		tortue.init();
		tortue.setVisible(true);
		g.setStroke(new BasicStroke(1));
		montrecacheTortue(true);
		// Update the selection frame
		updateColorSelection();
	}
	/**
	 * Primitive wash
	 */
	protected void nettoie() {
		stopRecord2DPolygon();
		g.setPaintMode();
		g.setColor(couleurfond);
		g.fillRect(0, 0, Config.imageWidth,Config.imageHeight);

		drawGrid();
		/* Réinitialiser les tortues
		if (null == tortues[0])
			tortues[0] = new Tortue(cadre);
		tortue = tortues[0]; //la tortue active sera à présent la
		// numéro 0
		tortue.id = 0;
		for (int i = 1; i < tortues.length; i++) { //On élimine les
			// autres tortues
			tortues[i] = null;
		}
		tortues_visibles.removeAllElements();
		tortues_visibles.push("0");*/
		g.setColor(tortue.couleurcrayon);
		clip();

		if (tortue.isVisible())
			montrecacheTortue(true);
		else
			tortues_visibles=new Stack<String>();
	}
	/**
	 * Used for primitive fillzone
	 * @param x
	 * @param y
	 * @param increment
	 * @param couleur_frontiere
	 * @return
	 */
	
	private int bornes_remplis_zone(int x, int y, int increment, int couleur_frontiere) {
//		System.out.println(x+" "+y);
		while (!meme_couleur(DrawPanel.dessin.getRGB(x, y) ,couleur_frontiere)) {
			DrawPanel.dessin.setRGB(x, y, couleur_frontiere);
			x = x + increment;
			if (!(x > 0 && x < Config.imageWidth-1))
				break;
		}
		return x - increment;
	}
	/**
	 * Are the two color equals?
	 * @param col1 The first color
	 * @param col2 The second color
	 * @return true or false
	 */
	private boolean meme_couleur(int col1,int col2){
/*		if (Config.quality==Logo.QUALITY_HIGH){
			int rouge1 = (col1 >> 16) & 0xFF;
			int vert1 = (col1 >> 8) & 0xFF;
			int bleu1 = col1 & 0xFF;
			int rouge2 = (col2 >> 16) & 0xFF;
			int vert2 = (col2 >> 8) & 0xFF;
			int bleu2 = col2 & 0xFF;
			int tolerance=120;
			int diff_rouge=rouge1-rouge2;
			int diff_bleu=bleu1-bleu2;
			int diff_vert=vert1-vert2;
			boolean rouge;boolean vert; boolean bleu;
			if (rouge1>rouge2){
				if (rouge1-rouge2< 128 -rouge2/2) rouge=true;
				else rouge=false;
			}
			else{
				if (rouge2-rouge1<rouge2/2) rouge=true;
				else rouge=false;
			}
			if (vert1>vert2){
				if (vert1-vert2< 128 -vert2/2) vert=true;
				else vert=false;
			}
			else{
				if (vert2-vert1<vert2/2) vert=true;
				else vert=false;
			}
			if (bleu1>bleu2){
				if (bleu1-bleu2< 128 -bleu2/2) bleu=true;
				else bleu=false;
			}
			else{
				if (bleu2-bleu1<bleu2/2) bleu=true;
				else bleu=false;
			}

		return rouge&&bleu&&vert;	
//			if (Math.abs(rouge1-rouge2)<tolerance&&Math.abs(vert1-vert2)<tolerance&&Math.abs(bleu1-bleu2)<tolerance&&Math.abs(rouge1+bleu1+vert1-rouge2-bleu2-vert2)<450)
//			return true;
	//	else return false;
		}
		else{*/
			return (col1==col2);
		//}
	}
	/**
	 * Primitive fillzone
	 */
	protected void rempliszone() {
		montrecacheTortue(false);
		int x = (int) (tortue.corX + 0.5);
		int y = (int) (tortue.corY + 0.5);
		if (x > 0 & x < Config.imageWidth & y > 0 & y < Config.imageHeight) {
			int couleur_origine = DrawPanel.dessin.getRGB(x, y);
			int couleur_frontiere = tortue.couleurcrayon.getRGB();
		//	System.out.println(couleur_origine+" " +couleur_frontiere);
			Stack<Point> pile_germes = new Stack<Point>();
			boolean couleurs_differentes = !meme_couleur(couleur_origine,couleur_frontiere);
			if (couleurs_differentes)
				pile_germes.push(new Point(x, y));
			while (!pile_germes.isEmpty()) {

				Point p = pile_germes.pop();
				int xgerme = p.x;
				int ygerme = p.y;
				int xmax = bornes_remplis_zone(xgerme, ygerme, 1,
						couleur_frontiere);
				int xmin=0;
				if (xgerme>0) xmin = bornes_remplis_zone(xgerme - 1, ygerme, -1,
						couleur_frontiere);
				boolean ligne_dessus = false;
				boolean ligne_dessous = false;
				for (int i = xmin; i < xmax + 1; i++) {
					//on recherche les germes au dessus et au dessous
					if (ygerme > 0
							&& meme_couleur(DrawPanel.dessin.getRGB(i, ygerme - 1) ,couleur_frontiere)) {
						if (ligne_dessus)
							pile_germes.push(new Point(i - 1, ygerme - 1));
						ligne_dessus = false;
					} else {
						ligne_dessus = true;
						if (i == xmax && ygerme > 0)
							pile_germes.push(new Point(xmax, ygerme - 1));
					}
					if (ygerme < Config.imageHeight-1
							&& meme_couleur(DrawPanel.dessin.getRGB(i, ygerme + 1),couleur_frontiere)) {
						if (ligne_dessous)
							pile_germes.push(new Point(i - 1, ygerme + 1));
						ligne_dessous = false;
					} else {
						ligne_dessous = true;
						if (i == xmax && ygerme < Config.imageHeight-1)
							pile_germes.push(new Point(xmax, ygerme + 1));
					}
				}
			}
			clip();
			montrecacheTortue(true);
		}
	}
	/**
	 * Used for primitive "fill"
	 * @param x
	 * @param y
	 * @param increment
	 * @param couleur_crayon
	 * @param couleur_origine
	 * @return
	 */
	private int bornes_remplis(int x, int y, int increment, int couleur_crayon,
			int couleur_origine) {
		while (DrawPanel.dessin.getRGB(x, y) == couleur_origine) {
			DrawPanel.dessin.setRGB(x, y, couleur_crayon);
			x = x + increment;
			if (!(x > 0 && x < Config.imageWidth-1))
				break;
		}
		return x - increment;
	}
	/**
	 * Primitive "fill"
	 */
	protected void remplis() {
		montrecacheTortue(false);
		int x = (int) (tortue.corX + 0.5);
		int y = (int) (tortue.corY + 0.5);
		if (x > 0 & x < Config.imageWidth & y > 0 & y < Config.imageHeight) {
		int couleur_origine = DrawPanel.dessin.getRGB(x, y);
		int couleur_crayon = tortue.couleurcrayon.getRGB();
		if (x > 0 & x < Config.imageWidth & y > 0 & y < Config.imageHeight) {
			Stack<Point> pile_germes = new Stack<Point>();
			boolean couleurs_differentes = !(couleur_origine == couleur_crayon);
			if (couleurs_differentes)
				pile_germes.push(new Point(x, y));
			while (!pile_germes.isEmpty()) {
				Point p =  pile_germes.pop();
				int xgerme = p.x;
				int ygerme = p.y;
				//			System.out.println(xgerme+" "+ygerme);
				int xmax = bornes_remplis(xgerme, ygerme, 1, couleur_crayon,
						couleur_origine);
				int xmin=0;
				if (xgerme>0) xmin = bornes_remplis(xgerme - 1, ygerme, -1,
						couleur_crayon, couleur_origine);
				//				System.out.println("xmax "+xmax+"xmin "+xmin);
				boolean ligne_dessus = false;
				boolean ligne_dessous = false;
				for (int i = xmin; i < xmax + 1; i++) {
					//on recherche les germes au dessus et au dessous
					if (ygerme > 0
							&& DrawPanel.dessin.getRGB(i, ygerme - 1) != couleur_origine) {
						if (ligne_dessus)
							pile_germes.push(new Point(i - 1, ygerme - 1));
						ligne_dessus = false;
					} else {
						ligne_dessus = true;
						if (i == xmax && ygerme > 0)
							pile_germes.push(new Point(xmax, ygerme - 1));
					}
					if (ygerme < Config.imageHeight-1
							&& DrawPanel.dessin.getRGB(i, ygerme + 1) != couleur_origine) {
						if (ligne_dessous)
							pile_germes.push(new Point(i - 1, ygerme + 1));
						ligne_dessous = false;
					} else {
						ligne_dessous = true;
						if (i == xmax && ygerme < Config.imageHeight-1)
							pile_germes.push(new Point(xmax, ygerme + 1));
					}
				}
			}
			clip();
			montrecacheTortue(true);
		}
		}
	}
	/**
	 * Primitive "label"
	 * @param mot The word to write on the drawing area
	 */
	protected void etiquette(String mot) {
		//	Graphics2D g = (Graphics2D) Ardoise.dessin.getGraphics();
		montrecacheTortue(false);
		if (!enabled3D()){
			double angle = Math.PI / 2 - tortue.angle;
			if(DrawPanel.WINDOW_MODE==DrawPanel.WINDOW_WRAP) centers=new Vector<Point2D.Double>();
			etiquette2D(tortue.corX,tortue.corY,angle,mot);
/*			g.rotate(angle);
			g.setPaintMode();
			g.setColor(tortue.couleurcrayon);
			float x = (float) (tortue.corX * Math.cos(angle) + tortue.corY
				* Math.sin(angle));
			float y = (float) (-tortue.corX * Math.sin(angle) + tortue.corY
				* Math.cos(angle));
			g.setFont(Panel_Font.fontes[police_etiquette]
				.deriveFont((float) tortue.police));
			g.drawString(mot, x, y);
			g.rotate(-angle);*/
		}
		else{
			FontRenderContext frc=g.getFontRenderContext();
			GlyphVector gv=g.getFont().createGlyphVector(frc, mot);
			Shape outline=gv.getOutline(0, 0);
			Shape s=transformShape(outline);
			g.setPaintMode();
			g.setColor(tortue.couleurcrayon);
			g.fill(s);
			if (record3D==DrawPanel.record3D_TEXT){
				Text2D text=new Text2D(
						mot,new Color3f(tortue.couleurcrayon),Panel_Font.fontes[police_etiquette].getName(),
						tortue.police,Font.PLAIN);
				
				text.setRectangleScaleFactor(0.001f);
				Appearance appear=text.getAppearance();
				PolygonAttributes pa=new PolygonAttributes();
				pa.setCullFace(PolygonAttributes.CULL_NONE);
				pa.setBackFaceNormalFlip(true);
				appear.setPolygonAttributes(pa);
				text.setAppearance(appear);
//				if (null==DrawPanel.listText) DrawPanel.listText=new Vector<TransformGroup>();
				TransformGroup tg=new TransformGroup();
				double[][] d=tortue.getRotationMatrix();
				Matrix3d m=new Matrix3d(d[0][0],d[0][1],d[0][2],d[1][0],d[1][1],d[1][2],d[2][0],d[2][1],d[2][2]);
				Transform3D t=new Transform3D(m,new Vector3d(tortue.X/1000,tortue.Y/1000,tortue.Z/1000),1);
				tg.setTransform(t);
				tg.addChild(text);
				cadre.getViewer3D().add2DText(tg);
//				DrawPanel.listText.add(tg);
			}
			
			
		}
		montrecacheTortue(true);
		if (classicMode) repaint();
	}
	private void etiquette2D(double x,double y, double angle, String word){
		g.setPaintMode();
		g.setColor(tortue.couleurcrayon);
		Font f=Panel_Font.fontes[police_etiquette]
		         				.deriveFont((float) tortue.police);
		g.setFont(f);   
		g.translate(x, y);
		g.rotate(angle);
		FontRenderContext frc = g.getFontRenderContext();
		TextLayout layout = new TextLayout(word, f, frc);
		Rectangle2D bounds = layout.getBounds();        
	    float height=(float)bounds.getHeight();
	    float width=(float)bounds.getWidth();
	    float x1=0,y1=0;
	    switch(tortue.getLabelHorizontalAlignment()){
	    	case Turtle.LABEL_HORIZONTAL_ALIGNMENT_LEFT:
	    		x1=0;
	    	break;
	    	case Turtle.LABEL_HORIZONTAL_ALIGNMENT_CENTER:
	    		x1=-width/2;
	    	break;
	    	case Turtle.LABEL_HORIZONTAL_ALIGNMENT_RIGHT:
	    		x1=-width;
	    	break;
	    }
	    switch(tortue.getLabelVerticalAlignment()){
    	case Turtle.LABEL_VERTICAL_ALIGNMENT_BOTTOM:
    		y1=0;
    	break;
    		case Turtle.LABEL_VERTICAL_ALIGNMENT_CENTER:
    		y1=height/2;
    		break;
    		case Turtle.LABEL_VERTICAL_ALIGNMENT_TOP:
    			y1=height;
    		break;
	    }
	    layout.draw(g, x1, y1);
	    g.drawString(word, x1, y1);
		g.rotate(-angle);
		g.translate(-x, -y);
		if (DrawPanel.WINDOW_MODE==DrawPanel.WINDOW_WRAP){
		    Rectangle2D.Double rec=new Rectangle2D.Double(0,0,width,height);
		    AffineTransform at=new AffineTransform();
		    at.translate(x, y);
		    at.rotate(angle);
		    Rectangle2D bound =at.createTransformedShape(rec).getBounds2D();
		    double right= bound.getX()+bound.getWidth()-x;
		    double left= x-bound.getX();
		    double up=y-bound.getY();
		    double down=bound.getY()+bound.getHeight()-y;
			if (x+right>Config.imageWidth&& x<=Config.imageWidth){
				pt=new Point2D.Double(-Config.imageWidth+x,y);
				if (! centers.contains(pt))	{
					centers.add(pt);
					etiquette2D(-Config.imageWidth+x,y,angle,word);
				}
			}
			if (x-left<0&& x>=0){
				pt=new Point2D.Double(Config.imageWidth+x,y);
				if (! centers.contains(pt))	{
					centers.add(pt);
					etiquette2D(Config.imageWidth+x,y,angle,word);
				}
			}
			if (y-up<0&& y>=0){
				pt=new Point2D.Double(x,Config.imageHeight+y);
				if (! centers.contains(pt))	{
					centers.add(pt);
					etiquette2D(x,Config.imageHeight+y,angle,word);
				}
			}
			if (y+down>Config.imageHeight&&y<=Config.imageHeight){
				pt=new Point2D.Double(x,-Config.imageHeight+y);
				if (! centers.contains(pt))	{
					centers.add(pt);
					etiquette2D(x,-Config.imageHeight+y,angle,word);
				}
			}
		}
	}
	
	/**
	 * This method transform a plane 2D shape in the shape corresponding to the turtle plane 
	 * @param s the first shape
	 * @return the new shape after transformation
	 */
	private Shape transformShape(Shape s){
		PathIterator it=s.getPathIterator(null);
		double[] d=new double[6];
		double[][] coor=new double[3][1];
		GeneralPath gp=new GeneralPath();
		double[] end=new double[3];
		double[] ctl1=new double[3];
		double[] ctl2=new double[3];
		boolean b=false;
		while(!it.isDone()){
			it.next();
			int id=0;
			if (!it.isDone()) id=it.currentSegment(d);
			else break;
			coor[0][0]=d[0];
			coor[1][0]=-d[1];
			coor[2][0]=0;
			coor=w3d.multiply(tortue.getRotationMatrix(), coor);

			end[0]=coor[0][0]+tortue.X;
			end[1]=coor[1][0]+tortue.Y;
			end[2]=coor[2][0]+tortue.Z;
			w3d.toScreenCoord(end);
			
			if (id==PathIterator.SEG_MOVETO)
				gp.moveTo((float)end[0], (float)end[1]);
			else if (id==PathIterator.SEG_LINETO)
				{
				if (!b) {
					b=true;
					gp.moveTo((float)end[0], (float)end[1]);
				}
				else gp.lineTo((float)end[0], (float)end[1]);
				}
			else if (id==PathIterator.SEG_CLOSE){
				gp.closePath();
			}
			else {
				if (!b) {
					b=true;
					Point2D p=null;
					if (s instanceof Arc2D.Double)
					 p=((Arc2D.Double)s).getStartPoint();
					else if (s instanceof GeneralPath) 
						 p=((GeneralPath)s).getCurrentPoint();
					coor[0][0]=p.getX();
					coor[1][0]=-p.getY();
					coor[2][0]=0;
					coor=w3d.multiply(tortue.getRotationMatrix(), coor);
					ctl1[0]=coor[0][0]+tortue.X;
					ctl1[1]=coor[1][0]+tortue.Y;
					ctl1[2]=coor[2][0]+tortue.Z;
					w3d.toScreenCoord(ctl1);
					gp.moveTo((float)ctl1[0], (float)ctl1[1]);
				}
				coor[0][0]=d[2];
				coor[1][0]=-d[3];
				coor[2][0]=0;
				coor=w3d.multiply(tortue.getRotationMatrix(), coor);
				ctl1[0]=coor[0][0]+tortue.X;
				ctl1[1]=coor[1][0]+tortue.Y;
				ctl1[2]=coor[2][0]+tortue.Z;
				w3d.toScreenCoord(ctl1);
				if(id==PathIterator.SEG_QUADTO){
					QuadCurve2D qc=new QuadCurve2D.Double(gp.getCurrentPoint().getX(),gp.getCurrentPoint().getY()
							,end[0], end[1],ctl1[0], ctl1[1]); 
					gp.append(qc, true);}
				else if (id==PathIterator.SEG_CUBICTO){
					coor[0][0]=d[4];
					coor[1][0]=-d[5];
					coor[2][0]=0;
					coor=w3d.multiply(tortue.getRotationMatrix(), coor);

					ctl2[0]=coor[0][0]+tortue.X;
					ctl2[1]=coor[1][0]+tortue.Y;
					ctl2[2]=coor[2][0]+tortue.Z;
					
					w3d.toScreenCoord(ctl2);
					CubicCurve2D qc=new CubicCurve2D.Double(gp.getCurrentPoint().getX(),gp.getCurrentPoint().getY()
							,end[0], end[1],ctl1[0], ctl1[1],ctl2[0], ctl2[1]); 
					gp.append(qc, true);
				}
				}
			}
			return gp;	
		}
	public World3D getWorld3D(){
		return w3d;
	}
	/**
	 * primitive setscreencolor
	 * @param color The Color of the nackground screen
	 */
	protected void fcfg(Color color) {
		couleurfond=color;
		updateColorSelection();
		if (enabled3D()){
			if (cadre.getViewer3D()!=null){
				cadre.getViewer3D().updateBackGround(couleurfond);
			}
		}
		nettoie();
	}
	/**
	 * Primitive setpencolor
	 * @param color The pen Color
	 */
	protected void fcc(Color color) {
		if (tortue.isVisible()&&null==tortue.tort) montrecacheTortue(false);
		tortue.couleurcrayon = color;
		tortue.couleurmodedessin = color;
		if (tortue.isVisible()&&null==tortue.tort) montrecacheTortue(true);
	}

		/**
		 * Primitive "guiposition"
		 * @param id The id for the gui Object
		 * @param liste The Coordinates list
		 * @param name The translated name for the primitive "guiposition"
		 * @throws myException If coordinates list is invalid
		 */
	protected void guiposition(String id, String liste,String name) throws myException{
		if (guiExist(id)){
			initCoords();
			extractCoords(liste,name);
			coords=toScreenCoord(coords,false);
			gm.get(id).setLocation((int)coords[0],(int)coords[1]);
		}
	}
	/**
	 * Draw the Gui object refered with "id" 
	 * @param id The Gui Object Id
	 * @throws myException If this object doesn't exist
	 */
	protected void guiDraw(String id) throws myException{
		if (guiExist(id)){
			GuiComponent gc=gm.get(id);
			add(gc.getGuiObject());
			validate();
			repaint();
		//	updateGuiComponents();
		}
	}
	private javax.swing.JComponent gui;
	/**
	 * This method erases a Gui on the drawing area
	 * @param id The Gui Object id
	 * @throws myException
	 */
	
	protected void guiRemove(String id) throws myException{
		if (guiExist(id)){
			gui=gm.get(id).getGuiObject();
			gm.remove(id);
			if (SwingUtilities.isEventDispatchThread()){
				remove(gui);
				validate();
			}
			else {
				try{
					SwingUtilities.invokeAndWait(new Runnable(){
						public void run(){
							remove(gui);
							validate();						
						}
					});
				}
				catch(Exception e){}
			}
			repaint();
		}
	}
	private StringBuffer extractList(String list) throws myException{
		StringBuffer sb=new StringBuffer();
		int compteur=0;
		int i=0;
		while(list.length()!=0){
			char c=list.charAt(i);
			if (c=='[') compteur++;
			else if (c==']') {
				if (compteur==0) return sb;
				else compteur--;
			}
			sb.append(c);
			i++;
		}
		throw new myException(cadre,"[ "+list+" "+Logo.messages.getString("pas_liste"));
	}
	
	protected void guiAction(String id, String liste) throws myException{
		if (guiExist(id)){
			GuiComponent gc=gm.get(id);
			// If gc is a JButton
			if (gc.isButton()){
				((GuiButton)gc).setAction(Utils.decoupe(liste));
				if (!gc.hasAction()){
					((javax.swing.JButton)gc.getGuiObject()).addActionListener(gc);
					gc.hasAction=true;
				}
			}
			// gc is a JcomboBox
			else if (gc.isMenu()){
				liste=liste.trim();
				int i=0;
				while(liste.length()!=0){
					if (liste.charAt(0)=='['){
						liste=liste.substring(1).trim();
						StringBuffer sb=extractList(liste);	
						liste=liste.substring(sb.length()+1).trim();
						((GuiMenu)gc).setAction(sb, i);
						i++;
					}
					else  throw new myException(cadre,liste.charAt(0)+" "+Logo.messages.getString("pas_liste"));
				}
				GuiMenu gm=(GuiMenu)gc;
				if (!gm.hasAction){
					gm.hasAction=true;
					((javax.swing.JComboBox)gc.getGuiObject()).addActionListener(gm);
				}
			}
		}
	}
	private boolean guiExist(String id) throws myException{
		if (gm.containsKey(id.toLowerCase())) return true;
		else throw new myException(cadre,Logo.messages.getString("no_gui")+" "+id);
	}
//	boolean access=false;
	private void clip(){
		if (classicMode){
			
			//access=true;
//			refresh();
			
			repaint();
	/*		if (SwingUtilities.isEventDispatchThread()){
				repaint();
			}
			else {
				try {
	
				SwingUtilities.invokeLater(new Runnable(){
					public void run(){
						repaint();			
					}
				});
			}
			catch(Exception e2){}
			}*/
		}
		/*Rectangle rec1=cadre.jScrollPane1.getViewport().getViewRect();
		boolean inter=sh.intersects(rec1);
		if (inter){
			if (classicMode){
				repaint();
			}
		}*/
	}
	public void setQuality(int id){
		if (id==Config.QUALITY_HIGH){
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		}
		else if(id==Config.QUALITY_LOW){
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_OFF);
			g.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_SPEED);
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
		}
		else { //normal
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_DEFAULT);
			g.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_DEFAULT);
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT);
		}
	}
	public  void change_image_tortue(Application cadre, String chemin) {
		if (tortue.isVisible())
			montrecacheTortue(false);
		if (chemin.equals("tortue0.png")) {
			tortue.tort = null;
			tortue.largeur = 26;
			tortue.hauteur = 26;
		} else {
			//ON teste tout d'abord si le chemin est valide
			if (null == Utils.class.getResource(chemin))
				chemin = "tortue1.png";
			tortue.tort = Toolkit.getDefaultToolkit().getImage(
					Utils.class.getResource(chemin));
			MediaTracker tracker = new MediaTracker(cadre);
			tracker.addImage(tortue.tort, 0);
			try {
				tracker.waitForID(0);
			} catch (InterruptedException e1) {
			}
			double largeur_ecran = Toolkit.getDefaultToolkit().getScreenSize()
					.getWidth();
			// On fait attention à la résolution de l'utilisateur
			double facteur = largeur_ecran / 1024.0;

			if ((int) (facteur + 0.001) != 1) {
				tortue.largeur = tortue.tort.getWidth(cadre);
				tortue.hauteur = tortue.tort.getHeight(cadre);
				tortue.tort = tortue.tort.getScaledInstance(
						(int) (facteur * tortue.largeur),
						(int) (facteur * tortue.hauteur),
						Image.SCALE_SMOOTH);
				tracker = new MediaTracker(cadre);
				tracker.addImage(tortue.tort, 0);
				try {
					tracker.waitForID(0);
				} catch (InterruptedException e1) {
				}
			}
			tortue.largeur = tortue.tort.getWidth(cadre);
			tortue.hauteur = tortue.tort.getHeight(cadre);
		}
		tortue.gabarit = Math.max(tortue.hauteur,
				tortue.largeur);
		if (tortue.isVisible())
			montrecacheTortue(true);

	}
	// animation 
	protected void setAnimation(boolean predic){
		if (predic==classicMode){
			if (predic) {
				cadre.getHistoryPanel().active_animation();
			}
			else {
				cadre.getHistoryPanel().stop_animation();
				repaint();
			}
		}
	}
	
	protected void setGraphicsFont(Font f){
		g.setFont(f);
	}
	protected Font getGraphicsFont(){
		return g.getFont();
	}
	protected void setStroke(Stroke st){
		g.setStroke(st);
	}
	public Color getBackgroundColor(){
		return couleurfond;
	}
	protected void setBackgroundColor(Color c){
		couleurfond=c;
	}
	protected void updateColorSelection(){
    	float r=(255-couleurfond.getRed())/255;
    	float v=(255-couleurfond.getGreen())/255;
		float b=(255-couleurfond.getBlue())/255;
		colorSelection=new Color(r,v,b,0.2f);
	}
	public void setNumberOfTurtles(int id){
		Config.maxTurtles = id;
		Turtle[] tampon = (Turtle[]) tortues.clone();
		tortues = new Turtle[id];
		int borne_sup=Math.min(tampon.length,tortues.length);
		for(int i=0;i<borne_sup;i++){
			tortues[i]=tampon[i];
		}
		for(int i=tortues_visibles.size()-1;i>-1;i--){
			int integer=Integer.parseInt(tortues_visibles.get(i));
			if (integer>=id){
				tortues_visibles.remove(i);
			}
		}
	}
	protected void initGraphics(){
		police_etiquette=Application.police;
		//		 init all turtles
		tortues = new Turtle[Config.maxTurtles];
		tortues_visibles=new Stack<String>();
		tortue=new Turtle(cadre);
		tortues[0] = tortue;
		tortue.id = 0;
		tortues_visibles.push("0");
		for (int i = 1; i < tortues.length; i++) { 
			// All other turtles are null
				tortues[i] = null;
		}
		g=(Graphics2D)dessin.getGraphics();
		couleurfond=Config.screencolor;
		setQuality(Config.quality);
	    g.setColor(Config.screencolor);
	    g.fillRect(0,0,Config.imageWidth,Config.imageHeight);
	    g.setColor(Config.screencolor);
	    if (!enabled3D()){
	    	drawGrid();
	    	drawXAxis();
	    	drawYAxis();	
	    }
	    	MediaTracker tracker;	    
	    	if (0==Config.activeTurtle) {
	    		g.setXORMode(couleurfond);
	    		tortue.drawTriangle();
	    		g.setColor(tortue.couleurcrayon);
	    		g.draw(tortue.triangle);
	    	}
	    	else {
	    		g.setXORMode(couleurfond);
	    		tracker=new MediaTracker(cadre);
	    		tracker.addImage(tortue.tort,0);
	    		try{tracker.waitForID(0);}
	    		catch(InterruptedException e){}
	    		if (tracker.checkID(0))  g.drawImage(tortue.tort, Config.imageWidth/2 - tortue.largeur / 2,
	                    Config.imageHeight/2 - tortue.hauteur/2, this);
	    		}
	    	updateColorSelection();
	}
	
	private void resizeAllGuiComponents(double d){
		// Resize all GuiComponent
		Set<String> set=gm.keySet();
		Iterator<String> it=set.iterator();
		while (it.hasNext()){
			String element=it.next();
			GuiComponent gui=gm.get(element);
			gui.getGuiObject().setSize((int)(gui.getOriginalWidth()*d),
					(int)(gui.getOriginalHeight()*d) );
			Font f=gui.getGuiObject().getFont();
			gui.getGuiObject().setFont(f.deriveFont((float)(Config.police.getSize()*d)));
			double x=gui.getLocation().x/zoom;
			double y=gui.getLocation().y/zoom;
			gui.setLocation((int)(x*d),(int)(y*d));
			
		}
		
	}
	
	
	/**
	 * Make a zoom on the drawing area
	 * @param d The absolute factor
	 */
	public void zoom(double d, boolean zoomIn){
		// Disable zoom buttons
		cadre.setZoomEnabled(false);
		
		javax.swing.JViewport jv=cadre.scrollArea.getViewport();
		Point p=jv.getViewPosition();
		Rectangle r=jv.getVisibleRect();

	
	// If a selection rectangle is displaying on the drawing area
	// And If zoomout has been pressed
	// Zooming on the rectangular selection 
		if (null!=selection&&cadre.commande_isEditable()&&zoomIn){
			int originalWidth=jv.getWidth();
			double width=selection.getWidth();
			d=zoom*originalWidth/width;
			p=selection.getLocation();
			r.width=selection.width;
			// adjust height in the same ratio as width
			r.height=r.height*(int)width/originalWidth;
			// erase selection
			selection=null;
		}
		// Resize all Gui Components on the drawing area
		resizeAllGuiComponents(d);
	
		double oldZoom=zoom;
		zoom=d;

		/* 
		 * 		-------------------------------------
		 * 		|									|
		 *      |  	-------------------------		|
		 * 		|	|						|		|
		 * 		|	|						|		|
		 * 		|	|			x--	dx-----	|  --> CenterView Point of the rectangle
		 * 		|	|			|			|		|
		 * 		|	|			dy			|		|
		 * 		|	-------------------------		|
		 * 		-------------------------------------
		 * */
		
		double dx=Math.min(r.width,Config.imageWidth*oldZoom)/2;
		double dy=Math.min(r.height,Config.imageHeight*oldZoom)/2;
		Point centerView=new Point((int)(p.x+dx),(int)(p.y+dy));

		// Dynamically modify the drawing Area size
		setPreferredSize(new java.awt.Dimension(
				(int)(Config.imageWidth*zoom)
				,(int)(Config.imageHeight*zoom)));

		SwingUtilities.invokeLater(new PositionJViewport(jv, 
				new Point((int)(centerView.x/oldZoom*zoom-dx),
						(int)(centerView.y/oldZoom*zoom-dy))));	

	}
	private Color getTransparencyColor(int color,int trans){
		Color c=new Color(color);
		return new Color(c.getRed(),c.getGreen(),c.getBlue(),trans);
	}
	/**
	 * Draw the horizontal axis
	 */ 
	private void drawXAxis(){
		if (Config.drawXAxis){
			g.setColor(getTransparencyColor(Config.axisColor,128));
			g.drawLine(0,Config.imageHeight/2,Config.imageWidth,Config.imageHeight/2);
			for (int i=Config.imageWidth/2%Config.XAxis;i<Config.imageWidth;i=i+Config.XAxis){
				g.drawLine(i, Config.imageHeight/2-2, i, Config.imageHeight/2+2);
				g.setFont(new Font("Dialog",Font.PLAIN,10));
				String tick=String.valueOf(i-Config.imageWidth/2);
				FontMetrics fm=g.getFontMetrics();
				int back=fm.stringWidth(String.valueOf(tick))/2;
				// if the both axes are drawn, the zero has to translated
				// So we don't draw the zero
				if (i!=Config.imageWidth/2||!Config.drawYAxis)   g.drawString(tick, i-back, Config.imageHeight/2+20);
			}
		}
	}
	/**
	 * Draw the vertical axis
	 */ 
	private void drawYAxis(){
		if (Config.drawYAxis){
			g.setColor(getTransparencyColor(Config.axisColor,128));
			g.drawLine(Config.imageWidth/2,0,Config.imageWidth/2,Config.imageHeight);
			for (int i=Config.imageHeight/2%Config.YAxis;i<Config.imageHeight;i=i+Config.YAxis){
				g.drawLine( Config.imageWidth/2-2, i, Config.imageWidth/2+2,i);
				g.setFont(new Font("Dialog",Font.PLAIN,10));
				String tick=String.valueOf(Config.imageHeight/2-i);
				// If both axes are drawn, zero is translated
				if (i==Config.imageHeight/2&&Config.drawXAxis) g.drawString("0", Config.imageWidth/2+10, i-5);
				else  g.drawString(tick, Config.imageWidth/2+10, i+5);
			}
		}
	}
	private void drawGrid(){
		if (Config.drawGrid){
			g.setStroke(new BasicStroke(1));
			g.setColor(getTransparencyColor(Config.gridColor,100));
						for (int i=Config.imageWidth/2%Config.XGrid;i<Config.imageWidth;i=i+Config.XGrid)
				g.drawLine(i, 0, i, Config.imageHeight);

			for (int i=Config.imageHeight/2%Config.YGrid;i<Config.imageHeight;i=i+Config.YGrid)
				g.drawLine(0,i, Config.imageWidth, i);
		}
	}
	// In animation mode, we have to wait for the drawing to be finished before modifying graphics.
	// Thread must be synchronized.
	protected synchronized void refresh(){
		repaint();
		try{
			wait();
		}
		catch(InterruptedException e){}		
	
	}

  protected synchronized void paintComponent(Graphics graph){
	  super.paintComponent(graph);
	  Graphics2D g2d=(Graphics2D)graph;
	  if (null==shape){
		  g2d.setClip(cadre.scrollArea.getViewport().getViewRect());
	  }
	  else {
		  g2d.setClip(shape);
		  shape=null;
	  }
	  g2d.scale(DrawPanel.zoom,DrawPanel.zoom);
	  g2d.drawImage(dessin,0,0,this);
	  g2d.scale(1/DrawPanel.zoom,1/DrawPanel.zoom);
	  if (!Affichage.execution_lancee&&null!=selection&&cadre.commande_isEditable()){
		  g2d.setColor(colorSelection);
		  g2d.fillRect(selection.x, selection.y, selection.width, selection.height);
	  }
	  notify();
  }
	 public void active_souris(){
		 lissouris=false;
	 }
	 public boolean get_lissouris(){
		 return lissouris;
	 }
	 public int get_bouton_souris(){
		 lissouris=false;
		 return bouton_souris;
	 }
	 public String get_possouris(){
		 lissouris=false;
		 return possouris;
	 }
	  public void mousePressed(MouseEvent e){
		 if (!Affichage.execution_lancee) {			 
			 selection=new Rectangle();
			 origine=new Point(e.getPoint());
			 selection.setSize(0, 0);
		 }
	 }
	 public void mouseReleased(MouseEvent e){}
	 public void mouseClicked(MouseEvent ev){
		 if (!Affichage.execution_lancee){
			 selection=null;
			 origine=null;
			 repaint();
		 }
		 else{
			 lissouris=true;
			 bouton_souris=ev.getButton();
			 Point point=ev.getPoint();
			 possouris="[ "+(point.x-Config.imageWidth/2)+" "+(Config.imageHeight/2-point.y)+" ] ";
		 }
	 }

	 public void mouseExited(MouseEvent e){
	 }
	 public void mouseEntered(MouseEvent e){
	 }
	 // Select an export area
	 public void mouseDragged(MouseEvent e){
		 if (!Affichage.execution_lancee&&null!=selection){
			 // First, we test if we need to move the scrollbars
			 	Point pos=e.getPoint();
				javax.swing.JViewport jv=cadre.scrollArea.getViewport();
				Point viewPosition=jv.getViewPosition();
				Rectangle r=jv.getVisibleRect();
				r.setLocation(viewPosition);
				// Is the point visible on screen?
				boolean b=r.contains(pos); 
				// Move the scroolPane if necessary
				if (!b){
					int x,y;
					if (pos.x<viewPosition.x) x=Math.max(0,pos.x);
					else if (pos.x>viewPosition.x+r.width) x=Math.min(pos.x-r.width,(int)(Config.imageWidth*zoom-r.width));
					else x=viewPosition.x;
					if (pos.y<viewPosition.y) y=Math.max(0,pos.y);
					else if (pos.y>viewPosition.y+r.height) y=Math.min(pos.y-r.height,(int)(Config.imageHeight*zoom-r.height));
					else y=viewPosition.y;
					jv.setViewPosition(new Point(x,y));
				}
				
			 // Then , drawing the selection area
			 
			 selection.setFrameFromDiagonal(origine, e.getPoint());
			 repaint();
		 }
	 }
	 public void mouseMoved(MouseEvent ev){
		 lissouris=true;
		 bouton_souris=0;
		 Point point=ev.getPoint();
		 possouris="[ "+(point.x-Config.imageWidth/2)+" "+(Config.imageHeight/2-point.y)+" ] ";
	   }
   protected void addToGuiMap(GuiComponent gc) throws xlogo.utils.myException{
	   gm.put(gc);
   }
   // This method modifies all Shape for any turtle on screen
   protected void updateAllTurtleShape(){
	   for (int i=0;i<tortues.length;i++){
		   if (null!=tortues[i]) tortues[i].fixe_taille_crayon(2*tortues[i].getPenWidth());
	   }
   }
   /**
    * Saves the a part of the drawing area as an image
    * @param name The image name
    * @param coords The upper left corner and the right bottom corner
    */
   protected void saveImage(String name, int[] coords){
	   BufferedImage buffer=getImagePart(coords);
	   String lowerName=name.toLowerCase();
	   String format="png";
	   if (lowerName.endsWith(".jpg")||lowerName.endsWith(".jpeg")) {
		   format="jpg";
	   }
	   else if (!lowerName.endsWith(".png")) {
		   name=name+".png";
	   }
	   name=Config.defaultFolder+File.separator+name;
	   try{
		   File f=new File(name);
		   ImageIO.write(buffer, format, f);
	   }
	   catch(IOException e){}
	   
   }
   /**
    * Return a part of the drawing area as an image
    * @return
    */
   private BufferedImage getImagePart(int[] coords){
	   Image pic=DrawPanel.dessin;
	   if (zoom!=1){
		  pic=createImage(new FilteredImageSource(pic.getSource(),
				 new ReplicateScaleFilter((int)(dessin.getWidth()*zoom),(int)(dessin.getHeight()*zoom))));
	   }
		 pic=createImage(new FilteredImageSource(pic.getSource(),
				 new CropImageFilter(coords[0],coords[1],coords[2],coords[3])));
		 return toBufferedImage(pic);
   }
   
   
   public BufferedImage getSelectionImage(){
	   Image pic=DrawPanel.dessin;
	   if (zoom!=1){
		  pic=createImage(new FilteredImageSource(pic.getSource(),
				 new ReplicateScaleFilter((int)(dessin.getWidth()*zoom),(int)(dessin.getHeight()*zoom))));
	   }
	   if (null!=selection){
		 int x=(int)(selection.getBounds().getX());
		 int y=(int)(selection.getBounds().getY());
		 int width=(int)(selection.getBounds().getWidth());
		 int height=(int)(selection.getBounds().getHeight());
		 pic=createImage(new FilteredImageSource(pic.getSource(),
				 new CropImageFilter(x,y,width,height)));
	}
		 return toBufferedImage(pic);
   }
//	 This method returns a buffered image with the contents of an image
   private BufferedImage toBufferedImage(Image image) {
       if (image instanceof BufferedImage)
			return (BufferedImage)image;
   
       // This code ensures that all the pixels in the image are loaded
       image = new ImageIcon(image).getImage();
   
  
       // Create a buffered image with a format that's compatible with the screen
       BufferedImage bimage = null;
       GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
       try {
           // Determine the type of transparency of the new buffered image
           int transparency = Transparency.OPAQUE;
   
           // Create the buffered image
           GraphicsDevice gs = ge.getDefaultScreenDevice();
           GraphicsConfiguration gc = gs.getDefaultConfiguration();
           bimage = gc.createCompatibleImage(
               image.getWidth(null), image.getHeight(null), transparency);
       } catch (HeadlessException e) {
           // The system does not have a screen
       }
   
       if (bimage == null) {
           // Create a buffered image using the default color model
           int type = BufferedImage.TYPE_INT_RGB;
           bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
       }
   
       // Copy image to buffered image
       Graphics g = bimage.createGraphics();
   
       // Paint the image onto the buffered image
       g.drawImage(image, 0, 0, null);
       g.dispose();
   
       return bimage;
   }
   class PositionJViewport implements Runnable{
	   JViewport jv;
	   Point p;
	   PositionJViewport(JViewport jv, Point p){
		   this.jv=jv;
		   this.p=p;
	   }
	   public void run(){
			revalidate();			
			cadre.calculateMargin();
		   //  I have to add those two lines because of a bug I don't understand
		   	// zoom 8 zoom 1 zoom 8
		   // Sometimes after the method revalidate(), the left upper corner position 
		   // wasn't correct 
			cadre.scrollArea.invalidate();
			cadre.scrollArea.validate();
		// End Bug 
			
			jv.setViewPosition(p);
			repaint();
			
			cadre.setZoomEnabled(true);
	   }
   }
   private void tryRecord2DMode(double a, double b){
		// FillPolygon mode
		if (DrawPanel.record2D==DrawPanel.record2D_POLYGON){
			if (stackTriangle.size()==3){
				stackTriangle.remove(0);
				stackTriangle.add(new Point2D.Double(a,b));
			}
			else{
				stackTriangle.add(new Point2D.Double(a,b));		
			}
			if (stackTriangle.size()==3){
				Path2D.Double path=new Path2D.Double();
				path.moveTo(stackTriangle.get(0).x, stackTriangle.get(0).y);
				path.lineTo(stackTriangle.get(1).x, stackTriangle.get(1).y);
				path.lineTo(stackTriangle.get(2).x, stackTriangle.get(2).y);
				path.closePath();
				g.fill(path);
			}
		}

   }
   protected void startRecord2DPolygon(){
	   DrawPanel.record2D=DrawPanel.record2D_POLYGON;
	   	stackTriangle=new Vector<Point2D.Double>();
	    stackTriangle.add(new Point2D.Double(tortue.corX,tortue.corX));
   }
   protected void stopRecord2DPolygon(){
		DrawPanel.record2D=DrawPanel.record2D_NONE;
   }
}