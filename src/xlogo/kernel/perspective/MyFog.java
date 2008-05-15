package xlogo.kernel.perspective;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.LinearFog;
import javax.media.j3d.ExponentialFog;
import javax.media.j3d.Fog;
import javax.media.j3d.BoundingSphere;

import javax.vecmath.Color3f;

public class MyFog extends BranchGroup{
	protected final static int FOG_OFF=0;
	protected final static int FOG_LINEAR=1;
	protected final static int FOG_EXPONENTIAL=2;

	private int type=FOG_OFF;
	private float density=1;
	private float backDistance=3.5f;
	private float frontDistance=0.5f;
	private Fog fog;
	Color3f color;
	MyFog(int type,Color3f color){
		super();
		setCapability(BranchGroup.ALLOW_DETACH);
		setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
		this.type=type;
		this.color=color;
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
				((LinearFog)fog).setBackDistance(backDistance);
				((LinearFog)fog).setFrontDistance(frontDistance);
				fog.setInfluencingBounds(new BoundingSphere());
				break;
			case FOG_EXPONENTIAL:
				fog= new ExponentialFog(color,density);
				fog.setInfluencingBounds(new BoundingSphere());
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
	float getDensity(){
		return density;
	}
	void  setDensity(float f){
		density=f;
	}
	float getBack(){
		return backDistance;
	}
	void  setBack(float f){
		backDistance=f;
	}
	float getFront(){
		return frontDistance;
	}
	void  setFront(float f){
		frontDistance=f;
	}
	
}