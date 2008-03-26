
package xlogo.kernel.perspective;
import javax.media.j3d.Appearance;
import javax.media.j3d.LineStripArray;
import javax.media.j3d.Material;
import javax.media.j3d.Shape3D;
import javax.vecmath.Color3f;

import xlogo.Application;
import xlogo.kernel.DrawPanel;
import xlogo.utils.myException;
public class ElementLine extends Element3D {
	public ElementLine(Application app){
		super(app);
	}
	public void addToScene() throws myException {
//		int[] tab=new int[1];
	//	tab[0]=vertex.size();		
		int size=vertex.size();
		if (size>1){
			int[] tab={size};
			LineStripArray line=new LineStripArray(size,LineStripArray.COORDINATES|LineStripArray.COLOR_3,tab);
			for (int i=0;i<size;i++){
				line.setCoordinate(i, vertex.get(i));
				//System.out.println("sommet "+(2*i-1)+" "+vertex.get(i).x+" "+vertex.get(i).y+" "+vertex.get(i).z+" ");
				line.setColor(i, new Color3f(color.get(i)));
			}			
			
/*			for (int i=0;i<line.getVertexCount();i++){
				double[] d=new double[3];
				line.getCoordinate(i, d);
				for(int j=0;j<d.length;j++){
					System.out.println(i+" "+d[j]);
				}
			}*/
			Shape3D s=new Shape3D(line);
			Appearance appear=new Appearance();
			Material mat=new Material(new Color3f(1.0f,1.0f,1.0f),new Color3f(0.0f,0f,0f),
					new Color3f(1f,1.0f,1.0f),new Color3f(1f,1f,1f),64);
			appear.setMaterial(mat);
			s.setAppearance(appear);
	//		DrawPanel.listPoly.add(s);
			app.getViewer3D().add3DObject(s);
		}
	}
	public boolean isLine() {
		return true;
	}
	public boolean isPoint() {
		return false;
	}
	public boolean isPolygon() {
		return false;
	}

	public boolean isText() {
		return false;
	}

}
