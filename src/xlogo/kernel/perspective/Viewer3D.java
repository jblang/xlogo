package xlogo.kernel.perspective;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Toolkit;
import java.util.*;
import javax.media.j3d.Light;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import java.awt.image.BufferedImage;
import javax.media.j3d.Locale;
import javax.media.j3d.ImageComponent2D;
import javax.media.j3d.ImageComponent;
import javax.media.j3d.GraphicsContext3D ;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Raster;
import javax.vecmath.Point3f;

import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.Background;
import java.awt.GridBagLayout;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import java.awt.GridLayout;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import xlogo.utils.Utils;
import xlogo.utils.WriteImage;
import xlogo.Config;
import xlogo.Logo;
import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
import com.sun.j3d.utils.universe.SimpleUniverse;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
public class Viewer3D extends JFrame implements ActionListener{
	private final static String ACTION_SCREENSHOT="screenshot";
	private final static String ACTION_LIGHT0="light0";
	private final static String ACTION_LIGHT1="light1";
	private final static String ACTION_LIGHT2="light2";
	private final static String ACTION_LIGHT3="light3";
	private final static String ACTION_FOG="fog";
	
	
	// To store the Light attributes
	private LightConfig[] lightConfigs;
	
	// To store the Fog attributes
//	private FogConfig fogConfig;
	
	// Gui Components
	private ImageIcon iscreenshot=new ImageIcon(Utils.dimensionne_image("screenshot.png",this));
	private JButton screenshot;
	private static final long serialVersionUID = 1L;
	private World3D w3d;
	private Canvas3D canvas3D;
	/**
	 * Main scene's Branchgroup 
	 */
	private BranchGroup scene;
	/**
	 * Light's branchgroup
	 */
	private BranchGroup lightBranchgroup;
	private BranchManager branchManager;
	private SimpleUniverse universe;
	private Color3f backgroundColor;
	private Background back;
	private PanelLight panelLight;
	private PanelFog panelFog;
	
	
	
	public Viewer3D(World3D w3d,Color c){
		this.w3d=w3d;
		this.backgroundColor=new Color3f(c);
		initGui();
	}
	public void actionPerformed(ActionEvent e){
		String cmd=e.getActionCommand();
		// Take a screenshot
		if (cmd.equals(Viewer3D.ACTION_SCREENSHOT)){
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
		// Click on Button Light1
		else if (cmd.equals(Viewer3D.ACTION_LIGHT0)){
			new LightDialog(this,lightConfigs[0]);
		}
		// Click on Button Light2
		else if (cmd.equals(Viewer3D.ACTION_LIGHT1)){
			new LightDialog(this,lightConfigs[1]);
		}
		// Click on Button Light3
		else if (cmd.equals(Viewer3D.ACTION_LIGHT2)){
			new LightDialog(this,lightConfigs[2]);
		}
		// Click on Button Light4
		else if (cmd.equals(Viewer3D.ACTION_LIGHT3)){
			new LightDialog(this,lightConfigs[3]);
		}
		// Click on the Fog Button
		else if (cmd.equals(Viewer3D.ACTION_FOG)){
			System.out.println("fog");
			
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
		if (null==w3d) {
			w3d=new World3D();
		}
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
	
		// Create the root of the branch graph
		scene= new BranchGroup();
		branchManager=new BranchManager();
		scene.setName("Main Branch");
		// We can add New Branchgroup dynamically in the main scene
		scene.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
		// We can remove Branchgroup dynamically in the main scene
		scene.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
		
//		scene.setCapability(BranchGroup.ALLOW_DETACH);
		
		createBackground(scene);

		// Configure Lights
		lightConfigs=new LightConfig[4];
		initLights();
		
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
		screenshot.setActionCommand(Viewer3D.ACTION_SCREENSHOT);
		
		panelLight=new PanelLight(this);
		panelFog=new PanelFog(this);
		
		getContentPane().add(canvas3D, new GridBagConstraints(0, 0, 1, 3, 2.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
						0, 0, 0, 0), 0, 0));		
		getContentPane().add(screenshot, new GridBagConstraints(1, 0, 1, 1, 0.1, 0.8,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						10, 10, 10, 10), 0, 0));
		getContentPane().add(panelLight, new GridBagConstraints(1, 1, 1, 1, 0.1, 0.1,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						10, 10, 10, 10), 0, 0));
		getContentPane().add(panelFog, new GridBagConstraints(1, 2, 1, 1, 0.1, 0.1,
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
	 * This methods adds two default PointLight int the 3d Scene
	 */
	private void initLights(){
		// First Default Point Light
		Color3f color=new Color3f(1f,1f,1f);
		Point3f pos=new Point3f((float)w3d.xCamera/1000,(float)w3d.yCamera/1000,(float)w3d.zCamera/1000);
		lightConfigs[0]=new LightConfig(LightConfig.LIGHT_POINT,color, pos);

		// Second default Point Light
		pos=new Point3f(-(float)w3d.xCamera/1000,-(float)w3d.yCamera/1000,-(float)w3d.zCamera/1000);
		lightConfigs[1]=new LightConfig(LightConfig.LIGHT_POINT,color, pos);

		lightConfigs[2]=new LightConfig(LightConfig.LIGHT_OFF);
		lightConfigs[3]=new LightConfig(LightConfig.LIGHT_OFF);
		for (int i=0;i<4;i++){
			lightConfigs[i].createLight();
		}
		addAllLights(scene);
	}
	/**
	 * Add all lights in the main 3D scene
	 */
	private void addAllLights(BranchGroup bg){
		lightBranchgroup=new BranchGroup();
		lightBranchgroup.setCapability(BranchGroup.ALLOW_DETACH);
		lightBranchgroup.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
		lightBranchgroup.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
		for(int i=0;i<4;i++){
			Light l=lightConfigs[i].getLight();
			if (null!=l) {
				l.setCapability(BranchGroup.ALLOW_DETACH);
				lightBranchgroup.addChild(l);	
			}
		}
		bg.addChild(lightBranchgroup);
	}
	/**
	 * Remove a specific Light
	 */
	protected void removeLight(Light l){
		if (null!=l){
			lightBranchgroup.detach();
			lightBranchgroup.removeChild(l);
			scene.addChild(lightBranchgroup);
		}
	}
	/**
	 * add a light in the main scene
	 */
	protected void addLight(Light l){
		if (null!=l){
			lightBranchgroup.detach();
			lightBranchgroup.addChild(l);
			scene.addChild(lightBranchgroup);
		}
	}
	
	
	/**
	 * This methods erase all drawings on the 3D Viewer Drawing Area
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
					addAllLights(bg);
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
	class PanelLight extends JPanel{
		private static final long serialVersionUID = 1L;
		private JButton[] buttonLights;
		private Viewer3D viewer3d;
		PanelLight(Viewer3D viewer3d){
			this.viewer3d=viewer3d;
			initGui();
		}
		private void initGui(){
			buttonLights=new JButton[4];
			setLayout(new GridLayout(2,2,10,10));
			for(int i=0;i<4;i++){
				ImageIcon ilight=new ImageIcon(Utils.dimensionne_image("light"+i+".png",viewer3d));
				buttonLights[i]=new	JButton(ilight);
				add(buttonLights[i]);
				buttonLights[i].addActionListener(viewer3d);
				buttonLights[i].setActionCommand("light"+i);
			}
			TitledBorder tb=BorderFactory.createTitledBorder(Logo.messages.getString("3d.lights"));
			tb.setTitleFont(Config.police);
			setBorder(tb);
		}
	}
	class PanelFog extends JPanel{

		private static final long serialVersionUID = 1L;
		private JButton buttonFog;
		private Viewer3D viewer3d;
		PanelFog(Viewer3D viewer3d){
			this.viewer3d=viewer3d;
			initGui();
		}
		private void initGui(){
			ImageIcon ifog=new ImageIcon(Utils.dimensionne_image("fog.png",viewer3d));
			buttonFog=new JButton(ifog);
			buttonFog.setActionCommand(Viewer3D.ACTION_FOG);
			buttonFog.addActionListener(viewer3d);
			add(buttonFog);
			TitledBorder tb=BorderFactory.createTitledBorder(Logo.messages.getString("3d.fog"));
			tb.setTitleFont(Config.police);
			setBorder(tb);
		}	
	}	
	
}
