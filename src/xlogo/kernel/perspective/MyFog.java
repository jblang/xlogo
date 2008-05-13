package xlogo.kernel.perspective;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.LinearFog;
import javax.media.j3d.ExponentialFog;
import javax.media.j3d.Fog;
import javax.vecmath.Point3d;
import javax.media.j3d.BoundingSphere;

import javax.vecmath.Color3f;

public class MyFog extends BranchGroup{
	protected final static int FOG_OFF=0;
	protected final static int FOG_LINEAR=1;
	protected final static int FOG_EXPONENTIAL=2;

	private int type=FOG_OFF;
	private Color3f color;
	private float density=1;
	protected final static float DEFAULT_DENSITY=1;
	private Fog fog;
	MyFog(int type){
		super();
		setCapability(BranchGroup.ALLOW_DETACH);
		setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
		this.type=type;
	}
	/**
	 * This method creates a light according to each parameter:<br>
	 * type, color, position, direction and angle
	 */
	void createFog(){
		this.setCapability(BranchGroup.ALLOW_DETACH);
		this.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
		switch(type){
			case FOG_OFF:
				fog=null;
				break;
			case FOG_LINEAR:
				fog= new LinearFog(color);
				fog.setInfluencingBounds(new BoundingSphere(new Point3d(0,0,0), Double.MAX_VALUE));
				break;
			case FOG_EXPONENTIAL:
				fog= new ExponentialFog(color,density);
				
				fog.setInfluencingBounds(new BoundingSphere(new Point3d(0,0,0), Double.MAX_VALUE));
				break;
		}
		if (null!=fog) addChild(fog);
	}
	
	/**
	 * This method returns the light type
	 * @return an integer which represents the light type
	 */
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

	float getDensity(){
		return density;
	}
	void  setDensity(float f){
		density=f;
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
