package xlogo.kernel.perspective;

import xlogo.gui.GraphFrame;
import xlogo.utils.LogoException;

import javax.vecmath.Point3d;
import java.awt.*;
import java.util.Vector;

abstract public class Element3D {
    protected GraphFrame graphFrame;
    /**
     * Color for each vertex
     */
    protected Vector<Color> color;

    /**
     * Vertex coordinates in virtual world
     */
    protected Vector<Point3d> vertex;

    public Element3D(GraphFrame graphFrame) {
        this.graphFrame = graphFrame;
        vertex = new Vector<Point3d>();
        color = new Vector<Color>();

    }

    public void addVertex(Point3d p, Color c) {
        vertex.add(p);
        color.add(c);
    }

    /**
     * This method calculates all attributes for polygon and add it in the 3D Viewer
     */
    abstract public void addToScene() throws LogoException;

    /**
     * This method indicates if the Element3D is a Point.
     *
     * @return true if this Element3D is a Point, false otherwise
     */
    abstract public boolean isPoint();

    /**
     * This method indicates if the Element3D is a Polygon.
     *
     * @return true if this Element3D is a Polygon, false otherwise
     */
    abstract public boolean isPolygon();

    /**
     * This method indicates if the Element3D is a line.
     *
     * @return true if this Element3D is a line, false otherwise
     */
    abstract public boolean isLine();

    /**
     * This method indicates if the Element3D is a Text2D.
     *
     * @return true if this Element3D is a Text2D, false otherwise
     */
    abstract public boolean isText();

    public int getVertexCount() {
        return vertex.size();
    }
}
