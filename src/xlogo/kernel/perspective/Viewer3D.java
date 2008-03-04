package xlogo.kernel.perspective;

import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Toolkit;
import java.util.*;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import java.awt.image.BufferedImage;
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
import javax.media.j3d.VirtualUniverse;
import java.awt.GridBagLayout;
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
import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.universe.ViewingPlatform;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
public class Viewer3D extends JFrame implements ActionListener{
	private ImageIcon iscreenshot=new ImageIcon(Utils.dimensionne_image("screenshot.png",this));
	private JButton screenshot;
	private static final long serialVersionUID = 1L;
	private World3D w3d;
	private Canvas3D canvas3D;
	private BranchGroup scene;
	public Viewer3D(World3D w3d){
		this.w3d=w3d;
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
		setIconImage(Toolkit.getDefaultToolkit().createImage(Utils.class.getResource("icone.png")));
		setTitle(Logo.messages.getString("3d.viewer"));
		// Create my own universe;
		GraphicsConfiguration config =
			   SimpleUniverse.getPreferredConfiguration () ;
		canvas3D = new Canvas3D (config) ;
		VirtualUniverse universe = new VirtualUniverse () ;
		javax.media.j3d.Locale locale = new javax.media.j3d.Locale (universe) ;
		ViewPlatform viewPlatform = new ViewPlatform () ;
		PhysicalBody physicalBody = new PhysicalBody () ;
		PhysicalEnvironment physicalEnvironment = new PhysicalEnvironment () ;
		View view = new View () ;
		view.addCanvas3D (canvas3D) ;
		view.setPhysicalBody (physicalBody) ;
		view.setPhysicalEnvironment (physicalEnvironment) ;
		view.attachViewPlatform (viewPlatform) ;
		ViewingPlatform viewingPlatform = new ViewingPlatform () ;
		viewingPlatform.setViewPlatform (viewPlatform) ;
		
		// Install the camera at the valid position with correct orientation
		TransformGroup tg=viewingPlatform.getViewPlatformTransform();
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
		viewingPlatform.setViewPlatformBehavior(ob);
		viewingPlatform.compile () ;
		// Create scene Graph
		createSceneGraph () ;
		locale.addBranchGraph (viewingPlatform) ;
		locale.addBranchGraph (scene) ;

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
		setVisible(true);
	}

	private void add3DObjects(){
		BranchGroup bg=new BranchGroup();

		if (null!=DrawPanel.listPoly){
			for (int i=0;i<DrawPanel.listPoly.size();i++){
				//Shape3D s=DrawPanel.listPoly.get(i);	
			
	/*		Material mat=new Material(new Color3f(1.0f,1.0f,1.0f),new Color3f(0.0f,0f,0f),
					new Color3f(1f,1.0f,1.0f),new Color3f(1f,1f,1f),64);
			Appearance appear=new Appearance();
			appear.setPolygonAttributes(new PolygonAttributes(PolygonAttributes.POLYGON_FILL,
					PolygonAttributes.CULL_BACK,0));
			appear.setMaterial(mat);
			s.setAppearance(appear);*/
				bg.addChild(DrawPanel.listPoly.get(i));
			}
//			System.out.println(bg.getParent());
			if (null==DrawPanel.listText) scene.addChild(bg);
			DrawPanel.listPoly=new Vector<Shape3D>();
		}   

       	if (null!=DrawPanel.listText){;
    		for (int i=0;i<DrawPanel.listText.size();i++){
    			bg.addChild(DrawPanel.listText.get(i));
    		}
			scene.addChild(bg);
    		DrawPanel.listText=new Vector<TransformGroup>();
    	}
	}
	
	private void createSceneGraph() {
		// Create the root of the branch graph
		scene= new BranchGroup();
		scene.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);

//		TransformGroup mainGroup=new TransformGroup();
//		scene.addChild(mainGroup);
		add3DObjects();
		PointLight light=new PointLight();
		light.setColor(new Color3f(1f,1f,1f));
		light.setPosition((float)w3d.xCamera/1000,(float)w3d.yCamera/1000,(float)w3d.zCamera/1000);
		light.setAttenuation(1,0,0);
		light.setInfluencingBounds(new BoundingSphere(new Point3d(w3d.xCamera/1000,w3d.yCamera/1000,w3d.zCamera/1000),Double.MAX_VALUE));
		scene.addChild(light);
		PointLight light2=new PointLight();
		light2.setColor(new Color3f(1f,1f,1f));
		light2.setPosition(-(float)w3d.xCamera/1000,-(float)w3d.yCamera/1000,-(float)w3d.zCamera/1000);
		light2.setAttenuation(1,0,0);
		light2.setInfluencingBounds(new BoundingSphere(new Point3d(-w3d.xCamera/1000,-w3d.yCamera/1000,-w3d.zCamera/1000),Double.MAX_VALUE));
		scene.addChild(light2);
	}
	public void update(){
		add3DObjects();
	
	}
}
