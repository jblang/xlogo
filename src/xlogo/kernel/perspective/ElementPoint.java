package xlogo.kernel.perspective;

import javax.media.j3d.Appearance;
import javax.media.j3d.Material;
import javax.media.j3d.PointArray;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Point3d;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3d;

import xlogo.Application;
import xlogo.utils.myException;
import javax.media.j3d.Shape3D;

import com.sun.j3d.utils.geometry.Sphere;


public class ElementPoint extends Element3D {
	float pointWidth;
	public ElementPoint(Application app){
		super(app);
		pointWidth=app.getKernel().getActiveTurtle().getPenWidth();
	}
	public void addToScene() throws myException {
		if (vertex.size()==0) return;
		if (pointWidth==0.5){
			int[] tab=new int[1];
			tab[0]=vertex.size();
			PointArray point=new PointArray(vertex.size(),PointArray.COORDINATES|PointArray.COLOR_3);
			for (int i=0;i<vertex.size();i++){
				point.setCoordinate(i, vertex.get(i));
				point.setColor(i, new Color3f(color.get(i)));
			}		
			app.getViewer3D().add3DObject(new Shape3D(point));			
		}
		else {
			for (int i=0;i<vertex.size();i++){
				createBigPoint(vertex.get(i),new Color3f(color.get(i)));
			}
			
		}
	}
	private void createBigPoint(Point3d p,Color3f color){
		// Add a Sphere to main 3D scene.
		Appearance appear=new Appearance();
		Material mat=new Material(new Color3f(1.0f,1.0f,1.0f),color,//new Color3f(0.0f,0f,0f),
				new Color3f(1f,1.0f,1.0f),new Color3f(1f,1f,1f),64);
		appear.setMaterial(mat);
		PolygonAttributes pa=new PolygonAttributes();
		pa.setCullFace(PolygonAttributes.CULL_NONE);
		pa.setBackFaceNormalFlip(true);
		appear.setPolygonAttributes(pa);
		
		Sphere sphere=new Sphere(pointWidth/1000,appear);		
		
		TransformGroup tg=new TransformGroup();
		Transform3D transform = new Transform3D();
		transform.setTranslation(new Vector3d(p));
		tg.setTransform(transform);
		tg.addChild(sphere);
		app.getViewer3D().add2DText(tg);
	}
	public boolean isLine() {
		return false;
	}
	public boolean isPoint() {
		return true;
	}
	public boolean isPolygon() {
		return false;
	}

	public boolean isText() {
		return false;
	}

}