package xlogo.kernel.perspective;

import javax.media.j3d.AmbientLight;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.Light;
import javax.media.j3d.PointLight;
import javax.media.j3d.SpotLight;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

public class LightConfig{
	protected final static int LIGHT_OFF=0;
	protected final static int LIGHT_AMBIENT=1;
	protected final static int LIGHT_DIRECTIONAL=2;
	protected final static int LIGHT_POINT=3;
	protected final static int LIGHT_SPOT=4;
	protected final static float DEFAULT_ANGLE=15;
	private int type=LIGHT_OFF;
	private Color3f color;
	private Point3f position;
	private Vector3f direction;
	private float angle=DEFAULT_ANGLE;
	LightConfig(int type){
		this.type=type;
	}
	LightConfig(int type,Color3f color, Point3f position){
		this.type=type;
		this.color=color;
		this.position=position;
	}
	Light getLight(){
		switch(type){
			case LIGHT_AMBIENT:
				return new AmbientLight(color);
			case LIGHT_DIRECTIONAL:
				return new DirectionalLight(color,direction);
			case LIGHT_POINT:
				Light light=new PointLight(color,position,new Point3f(1,0,0));
				light.setInfluencingBounds(new BoundingSphere(new Point3d(position), Double.MAX_VALUE));
				return light;
			case LIGHT_SPOT:
				return new SpotLight(color,position,new Point3f(1,0,0),direction,angle,64);
		}
		return null;
	}
	int getType(){
		return type;
	}
	void setType(int t){
		type=t;
	}
	Color3f getColor(){
		return color;
	}
	void setColor(Color3f c){
		color=c;
	}
	Point3f getPosition(){
		return position;
	}
	void setPosition(Point3f p){
		position=p;
	}
	Vector3f getDirection(){
		return direction;
	}
	void setDirection (Vector3f v){
		direction=v;
	}
	float getAngle(){
		return angle;
	}
	void  setAngle(float f){
		angle=f;
	}
}