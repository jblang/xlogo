package xlogo;
import javax.media.j3d.*;
import javax.swing.JFrame;
import javax.vecmath.Vector3d;
import javax.vecmath.Point3d;
import javax.vecmath.*;

import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
//import com.sun.j3d.utils.geometry.*;
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.universe.ViewingPlatform;
import java.awt.GraphicsConfiguration;
public class Test extends JFrame{
	Test(){
		GraphicsConfiguration config =
			   SimpleUniverse.getPreferredConfiguration () ;

		Canvas3D canvas3D = new Canvas3D (config) ;
		getContentPane ().add (canvas3D) ;
		VirtualUniverse universe = new VirtualUniverse () ;
		javax.media.j3d.Locale locale = new javax.media.j3d.Locale (universe) ;
		ViewPlatform viewPlatform = new ViewPlatform () ;
		PhysicalBody physicalBody = new PhysicalBody () ;
		PhysicalEnvironment physicalEnvironment = new PhysicalEnvironment () ;
		View view = new View () ;
		// When the user resize the window, he can see less or more virtual world

		view.addCanvas3D (canvas3D) ;
		
		view.setPhysicalBody (physicalBody) ;
		view.setPhysicalEnvironment (physicalEnvironment) ;
		view.attachViewPlatform (viewPlatform) ;
	/*	view.setWindowResizePolicy(View.VIRTUAL_WORLD);
		view.setBackClipPolicy(View.VIRTUAL_EYE);
		view.setFrontClipPolicy(View.VIRTUAL_EYE);
		view.setBackClipDistance(100);
		view.setFrontClipDistance(0);*/
		ViewingPlatform viewingPlatform = new ViewingPlatform () ;
		viewingPlatform.setViewPlatform (viewPlatform) ;
		//viewingPlatform.setNominalViewingTransform () ;

		TransformGroup tg=viewingPlatform.getViewPlatformTransform();
		Transform3D trans=new Transform3D();
		trans.setTranslation(new Vector3d(1.500,1.500,1.500));
		
		Transform3D rot=new Transform3D();
		rot.lookAt(new Point3d(1.500,1.500,1.500),new Point3d(0,0,0),new Vector3d(0,1,0));
		rot.invert();
			trans.mul(rot);	
		tg.setTransform(trans);
		OrbitBehavior ob=new OrbitBehavior(canvas3D);
		ob.setRotationCenter(new Point3d(0,0,0));
		ob.setSchedulingBounds(new BoundingSphere(new Point3d(0,0,0),Double.MAX_VALUE));
		viewingPlatform.setViewPlatformBehavior(ob);
		viewingPlatform.compile () ;
		BranchGroup scene = createSceneGraph () ;
		locale.addBranchGraph (viewingPlatform) ;
		locale.addBranchGraph (scene) ;
		

		
		
		/*
		Transform3D rot=new Transform3D();
//		rot.setRotation(w3d.getMatrix());
//		trans.mul(rot);*/
		//
		setSize(500, 500);
		getContentPane().add(canvas3D);
		setVisible(true);
	}

	public BranchGroup createSceneGraph() {
		 // Create the root of the branch graph
		BranchGroup objRoot = new BranchGroup();
		
		for (int i=0;i<32;i++){
			QuadArray qa=new QuadArray(4,QuadArray.COORDINATES|QuadArray.COLOR_3);
			double t=2*Math.PI/32;
			qa.setCoordinate(0, new Point3d(0,0,0));
			qa.setCoordinate(1, new Point3d(Math.cos(t*i)*0.5, Math.sin(t*i)*0.5,0));
			qa.setCoordinate(2, new Point3d(Math.cos(t*i)*0.5, Math.sin(t*i)*0.5,0.5));
			qa.setCoordinate(3, new Point3d(0,0,0.5));
				Color3f c=new Color3f((float)Math.random(),(float)Math.random(),(float)Math.random());
				for (int j=0;j<4;j++){
				qa.setColor(j, c);
			}
			objRoot.addChild(new Shape3D(qa));
			QuadArray qa2=new QuadArray(4,QuadArray.COORDINATES|QuadArray.COLOR_3);
			qa2.setCoordinate(0, new Point3d(0,0,0));
			qa2.setCoordinate(3, new Point3d(Math.cos(t*i)*0.5, Math.sin(t*i)*0.5,0));
			qa2.setCoordinate(2, new Point3d(Math.cos(t*i)*0.5, Math.sin(t*i)*0.5,0.5));
			qa2.setCoordinate(1, new Point3d(0,0,0.5));
			for (int j=0;j<4;j++){
				qa2.setColor(j, c);
			}
			objRoot.addChild(new Shape3D(qa2));
		}
		
		

		return objRoot;
	}

}