package xlogo.kernel.perspective;

import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Toolkit;
import java.util.*;
import java.awt.event.*;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import java.awt.image.BufferedImage;
import javax.media.j3d.Locale;
import javax.media.j3d.ImageComponent2D;
import javax.media.j3d.ImageComponent;
import javax.media.j3d.PhysicalBody;
import javax.media.j3d.PhysicalEnvironment;
import javax.media.j3d.PointLight;
import javax.media.j3d.GraphicsContext3D ;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Raster;
import javax.vecmath.Point3f;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.View;
import javax.media.j3d.ViewPlatform;
import javax.media.j3d.Background;
import javax.media.j3d.VirtualUniverse;
import java.awt.GridBagLayout;
import java.awt.Color;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import xlogo.kernel.DrawPanel;
import xlogo.utils.Utils;
import xlogo.utils.WriteImage;
import xlogo.Logo;
import xlogo.Config;
import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.universe.ViewingPlatform;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
public class Viewer3D extends JFrame implements ActionListener{
	//private boolean lock=false; 
	private ImageIcon iscreenshot=new ImageIcon(Utils.dimensionne_image("screenshot.png",this));
	private JButton screenshot;
	private static final long serialVersionUID = 1L;
	private World3D w3d;
	private Canvas3D canvas3D;
	private BranchGroup scene;
	private BranchManager branchManager;
	private SimpleUniverse universe;
	private Color3f backgroundColor;
	private Background back;
	public Viewer3D(World3D w3d,Color c){
		this.w3d=w3d;
		this.backgroundColor=new Color3f(c);
		initGui();
	}
	public void actionPerformed(ActionEvent e){
		String cmd=e.getActionCommand();
		if (cmd.equals("screenshot")){
			  WriteImage wi=new WriteImage(this,null);
			  int id =wi.chooseFile();
			  if (id==JFileChooser.APPROVE_OPTION){
		    	  GraphicsContext3D ctx = canvas3D.getGraphicsContext3D();
		    	  java.awt.Dimension scrDim = canvas3D.getSize();

		    		  // setting raster component
		    		  Raster ras =new Raster(new Point3f(-1.0f, -1.0f, -1.0f),Raster.RASTER_COLOR,0,0,scrDim.width,scrDim.height,
		    		  new ImageComponent2D(ImageComponent.FORMAT_RGB,new BufferedImage(scrDim.width, scrDim.height, java.awt.image.BufferedImage.TYPE_INT_RGB)),
		    		  null);
		    		  ctx.readRaster(ras);
		    		  BufferedImage img= ras.getImage().getImage();				  
		    		  wi.setImage(img);
				      wi.start();	    		  
			  }
		}
	}
	private void initGui(){
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setIconImage(Toolkit.getDefaultToolkit().createImage(Utils.class.getResource("icone.png")));
		setTitle(Logo.messages.getString("3d.viewer"));
		// Creation d'un composant de classe Canvas3D permettant de visualiser une scène 3D
		canvas3D = new Canvas3D (SimpleUniverse.getPreferredConfiguration ());
	    // Création d'un univers 3D rattaché au composant 3D
	    universe = new SimpleUniverse (canvas3D);
			
		// Install the camera at the valid position with correct orientation
		TransformGroup tg=universe.getViewingPlatform().getViewPlatformTransform();
		Transform3D trans=new Transform3D();			
		trans.setTranslation(new Vector3d(-w3d.xCamera/1000,-w3d.yCamera/1000,-w3d.zCamera/1000));
		Transform3D rot=new Transform3D();
		rot.lookAt(new Point3d(-w3d.xCamera/1000,-w3d.yCamera/1000,-w3d.zCamera/1000),
				new Point3d(0,0,0),new Vector3d(0,1,0));
		rot.invert();
		trans.mul(rot);	
		tg.setTransform(trans);			
		OrbitBehavior ob=new OrbitBehavior(canvas3D,OrbitBehavior.REVERSE_ALL);
		ob.setRotationCenter(new Point3d(0,0,0));
		ob.setSchedulingBounds(new BoundingSphere(new Point3d(0,0,0),2*w3d.r/1000));
		universe.getViewingPlatform().setViewPlatformBehavior(ob);
	
		// Create scene Graph

		// Create the root of the branch graph
		scene= new BranchGroup();
		branchManager=new BranchManager();
		scene.setName("Main Branch");
		scene.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
		scene.setCapability(BranchGroup.ALLOW_DETACH);
		
		createBackground(scene);
		addLights(scene);
		
		//createSceneGraph ();
	    // Rattachement de la scène 3D à l'univers
	    universe.addBranchGraph (scene);
	    	
		Dimension d=Toolkit.getDefaultToolkit().getScreenSize();
		int width=d.width*9/10;
		int height=d.height*9/10;
		setSize(width, height);
		getContentPane().setLayout(new GridBagLayout());
		screenshot=new JButton(iscreenshot);
		screenshot.addActionListener(this);
		screenshot.setMaximumSize(new Dimension(100,100));
		screenshot.setActionCommand("screenshot");
		getContentPane().add(canvas3D, new GridBagConstraints(0, 0, 1, 1, 2.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
						0, 0, 0, 0), 0, 0));		
		getContentPane().add(screenshot, new GridBagConstraints(1, 0, 1, 1, 0.1, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						10, 10, 10, 10), 0, 0));		
		getContentPane().validate();
	}
	/**
	 * This method adds a shape3D to the main scene
	 * @param s The Shape to add
	 */
	public void add3DObject(Shape3D s){
		branchManager.add3DObject(s);
	}
	public void add2DText(TransformGroup tg){
		branchManager.add2DText(tg);
	}
	
	public void insertBranch(){
		branchManager.insertBranch();
		branchManager=new BranchManager();
	}
	
	/**
	 * Add two lights for the main 3D scene
	 */
	private void addLights(BranchGroup bg){
		PointLight light=new PointLight();
		light.setColor(new Color3f(1f,1f,1f));
		light.setPosition((float)w3d.xCamera/1000,(float)w3d.yCamera/1000,(float)w3d.zCamera/1000);
		light.setAttenuation(1,0,0);
		light.setInfluencingBounds(new BoundingSphere(new Point3d(w3d.xCamera/1000,w3d.yCamera/1000,w3d.zCamera/1000),Double.MAX_VALUE));
		bg.addChild(light);
		PointLight light2=new PointLight();
		light2.setColor(new Color3f(1f,1f,1f));
		light2.setPosition(-(float)w3d.xCamera/1000,-(float)w3d.yCamera/1000,-(float)w3d.zCamera/1000);
		light2.setAttenuation(1,0,0);
		light2.setInfluencingBounds(new BoundingSphere(new Point3d(-w3d.xCamera/1000,-w3d.yCamera/1000,-w3d.zCamera/1000),Double.MAX_VALUE));
		bg.addChild(light2);
	}
	/**
	 * This methods erase all drawings on the 3D Viewer Drwing Area
	 */
	public void clearScreen(){
		Enumeration<Locale> locales=universe.getAllLocales();
		while(locales.hasMoreElements()){
			Locale lo=locales.nextElement();
			Enumeration<BranchGroup> en=lo.getAllBranchGraphs();
			while(en.hasMoreElements()){
				BranchGroup bg=en.nextElement();
//				System.out.println(bg.getName());
				if (null!=bg.getName()&&bg.getName().equals("Main Branch")){
					// Detach scene
					bg.detach();
					// Delete all nodes
					bg.removeAllChildren();
					// create background
					createBackground(bg);
					// Add lights
					addLights(bg);
					// Attach scene
					lo.addBranchGraph(bg);
				}
			}				
		}
	}
	/**
	 * This method creates the Background with the defined color
	 */
	public void createBackground(BranchGroup bg){
		back=new Background(backgroundColor);
		back.setApplicationBounds(new BoundingSphere()) ;
		back.setCapability(Background.ALLOW_COLOR_WRITE);
		bg.addChild(back);
	}
	class BranchManager{
		BranchGroup bg;
		BranchManager(){
			bg=new BranchGroup();
		}
		/**
		 * This method adds a shape3D to the main scene
		 * @param s The Shape to add
		 */
		void add3DObject(Shape3D s){
			bg.addChild(s);
		}
		void add2DText(TransformGroup tg){
			bg.addChild(tg);
		}
		void insertBranch(){
			bg.compile();
			scene.addChild(bg);
		}
	} 
}
