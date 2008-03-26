/**
 * Title :        XLogo
 * Description :  XLogo is an interpreter for the Logo 
 * 						programming language
 * @author Loïc Le Coq
 */
package xlogo.kernel.perspective;
import javax.media.j3d.Appearance;
import javax.media.j3d.Material;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.TriangleFanArray;
import javax.vecmath.Point3d;
import javax.media.j3d.Shape3D;
import javax.vecmath.Vector3f;
import javax.vecmath.Color3f;
import xlogo.Application;
import xlogo.Logo;
import xlogo.utils.myException;
import xlogo.kernel.DrawPanel;
/**
 * This class represent A polygon surface in 3D mode
 * @author loic
 *
 */
public class ElementPolygon extends Element3D{
	public ElementPolygon(Application app){
		super(app);
	}
	/**
	 * This method calculates all attributes for polygon and add it to the Polygon's list
	 */
	public void addToScene() throws myException{

		if (vertex.size()<3) throw new myException(app,Logo.messages.getString("error.3d.3vertex"));

		// Create normal vector

		Point3d origine=vertex.get(0);
		//System.out.println(" origine "+origine.x+" "+origine.y+" "+origine.z);

		Point3d point1;
		Vector3f vec1=null;
		Vector3f vec2=null;
		for (int i=1;i<vertex.size();i++){
			point1=vertex.get(i);
			if (!point1.equals(origine)) {
			//	System.out.println(" point1 "+point1.x+" "+point1.y+" "+point1.z);
				vec1=new Vector3f((float)(point1.x-origine.x),
						(float)(point1.y-origine.y),
						(float)(point1.z-origine.z));
				break;
			}
		}
		if (null==vec1) throw new myException(app,Logo.messages.getString("error.3d.emptypolygon"));
		for (int i=2;i<vertex.size();i++){
			point1=vertex.get(i);
			//System.out.println(" point1 "+point1.x+" "+point1.y+" "+point1.z);
			vec2=new Vector3f((float)(point1.x-origine.x),
					(float)(point1.y-origine.y),
					(float)(point1.z-origine.z));
			if (vec1.dot(vec2)==0) vec2=null;
			else{
				//System.out.println(" vec1 "+vec1.x+" "+vec1.y+" "+vec1.z);
				//System.out.println(" vec2 "+vec2.x+" "+vec2.y+" "+vec2.z);
				vec2.cross(vec1, vec2);
				vec2.normalize();
				vec1=new Vector3f(vec2);
				vec1.negate();
				//System.out.println("Après"+" vec1 "+vec1.x+" "+vec1.y+" "+vec1.z);
				//System.out.println("Après"+" vec2 "+vec2.x+" "+vec2.y+" "+vec2.z);
				//if (vec1.x!=0&& vec1.y!=0&& vec1.z!=0) 				System.out.println("coucou"+" vec1 "+vec1.x+" "+vec1.y+" "+vec1.z);
				break;
			}
			if (null==vec2) throw new myException(app,Logo.messages.getString("error.3d.emptypolygon"));
		}
		
		// Create Geometry
		
		int[] tab=new int[1];
		tab[0]=vertex.size();		
		TriangleFanArray tfa=new TriangleFanArray(vertex.size(),TriangleFanArray.COORDINATES|TriangleFanArray.COLOR_3|TriangleFanArray.NORMALS,tab);
	//	TriangleFanArray tfa2=new TriangleFanArray(vertex.size(),TriangleFanArray.COORDINATES|TriangleFanArray.COLOR_3|TriangleFanArray.NORMALS,tab);
		for (int i=0;i<vertex.size();i++){
			
			tfa.setCoordinate(i, vertex.get(i));
		//	tfa2.setCoordinate(i, vertex.get(vertex.size()-1-i));
	
			tfa.setColor(i, new Color3f(color.get(i)));
		//	tfa2.setColor(i, new Color3f(color.get(color.size()-i-1)));

			tfa.setNormal(i, vec2);
		//	tfa2.setNormal(i, vec1);
		
		
		}

		Shape3D s=new Shape3D(tfa);
		Appearance appear=new Appearance();
		Material mat=new Material(new Color3f(1.0f,1.0f,1.0f),new Color3f(0.0f,0f,0f),
				new Color3f(1f,1.0f,1.0f),new Color3f(1f,1f,1f),64);
		appear.setMaterial(mat);
		PolygonAttributes pa=new PolygonAttributes();
		pa.setCullFace(PolygonAttributes.CULL_NONE);
		pa.setBackFaceNormalFlip(true);
		appear.setPolygonAttributes(pa);

		s.setAppearance(appear);
		app.getViewer3D().add3DObject(s);
	//	DrawPanel.listPoly.add(s);
		//DrawPanel.listPoly.add(new Shape3D(tfa2));
//			System.out.println(DrawPanel.listPoly.size()+" "+vertex.get(i).x+" "+vertex.get(i).y+" "+vertex.get(i).z);
	}
	public boolean isPoint(){
		return false;
	}
	public boolean isPolygon(){
		return true;
	}
	public boolean isLine(){
		return false;
	}
	public boolean isText(){
		return false;
	}
}
