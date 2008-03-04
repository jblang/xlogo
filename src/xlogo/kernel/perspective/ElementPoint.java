package xlogo.kernel.perspective;

import javax.media.j3d.PointArray;
import javax.vecmath.Color3f;

import xlogo.Application;
import xlogo.kernel.DrawPanel;
import xlogo.utils.myException;
import javax.media.j3d.Shape3D;


public class ElementPoint extends Element3D {
	public ElementPoint(Application app){
		super(app);
	}
	public void addToList() throws myException {
		int[] tab=new int[1];
		tab[0]=vertex.size();		
		PointArray point=new PointArray(vertex.size(),PointArray.COORDINATES|PointArray.COLOR_3);
		for (int i=0;i<vertex.size();i++){
			point.setCoordinate(i, vertex.get(i));
			point.setColor(i, new Color3f(color.get(i)));
		}		
		DrawPanel.listPoly.add(new Shape3D(point));
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