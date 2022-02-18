package xlogo.kernel.perspective;

import javax.media.j3d.*;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

public class Light extends BranchGroup {
    protected final static int LIGHT_OFF = 0;
    protected final static int LIGHT_AMBIENT = 1;
    protected final static int LIGHT_DIRECTIONAL = 2;
    protected final static int LIGHT_POINT = 3;
    protected final static int LIGHT_SPOT = 4;
    protected final static float DEFAULT_ANGLE = 15;
    private int type = LIGHT_OFF;
    private Color3f color;
    private Point3f position;
    private Vector3f direction;
    private float angle = DEFAULT_ANGLE;
    private javax.media.j3d.Light light;

    Light(int type) {
        super();
        this.type = type;
    }

    Light(int type, Color3f color, Point3f position) {
        super();
        this.type = type;
        this.color = color;
        this.position = position;
    }

    /**
     * This method creates a light according to each parameter:<br>
     * type, color, position, direction and angle
     */
    void createLight() {
        this.setCapability(BranchGroup.ALLOW_DETACH);
        this.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        switch (type) {
            case LIGHT_OFF:
                light = null;
                break;
            case LIGHT_AMBIENT:
                light = new AmbientLight(color);
                light.setInfluencingBounds(new BoundingSphere(new Point3d(position), Double.MAX_VALUE));
                break;
            case LIGHT_DIRECTIONAL:
                light = new DirectionalLight(color, direction);
                light.setInfluencingBounds(new BoundingSphere(new Point3d(position), Double.MAX_VALUE));
                break;
            case LIGHT_POINT:
                light = new PointLight(color, position, new Point3f(1, 0, 0));
                light.setInfluencingBounds(new BoundingSphere(new Point3d(position), Double.MAX_VALUE));
                break;
            case LIGHT_SPOT:
                light = new SpotLight(color, position, new Point3f(1, 0, 0), direction, (float) Math.toRadians(angle), 64);
                light.setInfluencingBounds(new BoundingSphere(new Point3d(position), Double.MAX_VALUE));
                break;
        }
        if (null != light) addChild(light);
    }

    /**
     * This method returns the light type
     *
     * @return an integer which represents the light type
     */
    int getType() {
        return type;
    }

    void setType(int t) {
        type = t;
    }

    Color3f getColor() {
        return color;
    }

    void setColor(Color3f c) {
        color = c;
    }

    Point3f getPosition() {
        return position;
    }

    void setPosition(Point3f p) {

        position = p;
    }

    Vector3f getDirection() {
        return direction;
    }

    void setDirection(Vector3f v) {
        direction = v;
    }

    float getAngle() {
        return angle;
    }

    void setAngle(float f) {
        angle = f;
    }

}
