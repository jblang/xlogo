package xlogo.kernel;

import com.sun.j3d.utils.geometry.Text2D;
import xlogo.Config;
import xlogo.Logo;
import xlogo.gui.Application;
import xlogo.kernel.gui.GuiButton;
import xlogo.kernel.gui.GuiComponent;
import xlogo.kernel.gui.GuiMap;
import xlogo.kernel.gui.GuiMenu;
import xlogo.kernel.perspective.*;
import xlogo.utils.Utils;

import javax.imageio.ImageIO;
import javax.media.j3d.Appearance;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.swing.*;
import javax.vecmath.Color3f;
import javax.vecmath.Matrix3d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.font.TextLayout;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.awt.image.ReplicateScaleFilter;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

/**
 * Title :        XLogo
 * Description :  XLogo is an interpreter for the Logo
 * programming language
 *
 * @author Loïc Le Coq
 */
public class DrawPanel extends JPanel implements MouseMotionListener, MouseListener {
    /**
     * Animation mode:
     */
    public final static boolean MODE_ANIMATION = false;
    /**
     * Classic mode
     */
    public final static boolean MODE_CLASSIC = true;
    /**
     * default predefined colors
     */
    public static final Color[] defaultColors = {Color.BLACK, Color.RED, Color.GREEN, Color.YELLOW, Color.BLUE,
            Color.MAGENTA, Color.CYAN, Color.WHITE, Color.GRAY, Color.LIGHT_GRAY, new Color(128, 0, 0), new Color(0, 128, 0),
            new Color(0, 0, 128), new Color(255, 128, 0), Color.PINK, new Color(128, 0, 255), new Color(153, 102, 0)};
    /**
     * WINDOW MODE: 0 <br>
     * Turtles can go out the drawing area
     */
    protected static final int WINDOW_CLASSIC = 0;
    /**
     * WRAP MODE: 1 <br>
     * Turtles can go out the drawing area and reappear on the other side
     */
    protected static final int WINDOW_WRAP = 1;
    /**
     * CLOSE MODE: 2 <br>
     * Turtles can't go out the drawing area
     */
    protected static final int WINDOW_CLOSE = 2;
    /**
     * Perspective MODE <br>
     * The screen is a projection of the 3D universe
     */
    protected static final int WINDOW_3D = 3;
    protected final static int RECORD_3D_NONE = 0;
    protected final static int RECORD_3D_POLYGON = 1;
    protected final static int RECORD_3D_LINE = 2;
    protected final static int RECORD_3D_POINT = 3;
    protected final static int RECORD_3D_TEXT = 4;
    private final static int RECORD_2D_NONE = 0;
    private final static int RECORD_2D_POLYGON = 1;
    /**
     * Boolean for animation mode
     */
    public static boolean classicMode = true; // true si classique false si animation
    /**
     * This Image is used for Buffering the drawing
     */
    private BufferedImage image;
    /**
     * The scale for the zoom
     */
    public static double zoom = 1;
    /**
     * this int indicates the window mode, default 0
     */
    protected static int windowMode = 0;
    /**
     * Boolean that indicates if the interpreter is recording polygon in 3D Mode
     */
    protected static int record3D = 0;
    protected static Element3D poly;
    /**
     * Boolean that indicates if the interpreter is recording polygon in 2D Mode
     */
    private static int record2D = 0;
    public Turtle turtle;
    public Turtle[] turtles;
    /**
     * When a turtle is active on screen, its number is added to this stack
     */
    public Stack<String> visibleTurtles;
    /**
     * The id for the drawing font (with primitive label)
     */
    protected int drawingFont;
    /**
     * The First clicked point when the rectangular selection is created
     */
    Point dragOrigin;
    /**
     * The default drawing area color
     */
    private Color screenColor = Color.white;
    private Line2D line;
    private Rectangle2D rect;
    private final Application app;
    /**
     * Graphics of the BufferedImage dessin
     */
    private Graphics2D g;
    /**
     * All Gui Objects on the drawing area are stored in the GuiMap gm
     */
    private final GuiMap guiMap;
    /**
     * The Stroke for the triangle turtle
     */
    private final BasicStroke triangleStroke = new BasicStroke(1);
    /**
     * Tools for 3D Mode
     */
    private World3D world3D = null;
    private Vector<Point2D.Double> stackTriangle;
    private double[] coords;
    private double oldx, oldy, x1, y1, x2, y2;
    // Were used for clipping
    private Arc2D arc;
    /**
     * Button number when user click on the drawing area
     */
    private int mouseButton = 0;    // Numéro du bouton de souris appuyé sur la zone de dessin
    /**
     * Last coords for last mouse event
     */
    private String mousePosition = "[ 0 0 ] ";    // Coordonnées du point du dernier événement souris
    /**
     * Notify if a mouse event has occured
     */
    private boolean readMouse = false; //Indique si un événement souris est intervenu depuis le debut du programme
    /**
     * The rectangular selection zone
     */
    private Rectangle selection;
    /**
     * Color for the rectangular selection
     */
    private Color selectionColor;
    private Point2D.Double pt;
    private Vector<Point2D.Double> centers;
    private javax.swing.JComponent gui;
    private double scaleX = 1.0;
    private double scaleY = 1.0;

    public DrawPanel(Application app) {
        this.guiMap = app.getKernel().getWorkspace().getGuiMap();
        setLayout(null);
        this.setPreferredSize(new Dimension(
                (int) (Logo.config.getImageWidth() * zoom), (int) (Logo.config.getImageHeight() * zoom)));
        this.app = app;
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    /**
     * This method is used to draw for primitive "forward" and "backward"
     *
     * @param arg Number of steps
     */
    protected void move(double arg) {
        //	Graphics2D g=(Graphics2D)dessin.getGraphics();

        oldx = turtle.curX;
        oldy = turtle.curY;
        if (DrawPanel.windowMode == DrawPanel.WINDOW_CLASSIC) { //mode fenetre
            eraseTurtle(false);

            turtle.curX = turtle.curX + arg
                    * Math.cos(turtle.angle);
            turtle.curY = turtle.curY - arg
                    * Math.sin(turtle.angle);
            if (turtle.isPenDown()) {
                if (turtle.isPenReverse()) {
                    g.setColor(screenColor);
                    g.setXORMode(turtle.penColor);
                } else {
                    g.setColor(turtle.penColor);
                    g.setPaintMode();
                }
                if (null == line) line = new Line2D.Double();
                if (oldx < turtle.curX) {
                    x1 = oldx;
                    y1 = oldy;
                    x2 = turtle.curX;
                    y2 = turtle.curY;
                }
                if (oldx > turtle.curX) {
                    x2 = oldx;
                    y2 = oldy;
                    x1 = turtle.curX;
                    y1 = turtle.curY;
                } else if (oldx == turtle.curX) {
                    if (oldy < turtle.curY) {
                        x2 = oldx;
                        y2 = oldy;
                        x1 = turtle.curX;
                        y1 = turtle.curY;
                    } else {
                        x1 = oldx;
                        y1 = oldy;
                        x2 = turtle.curX;
                        y2 = turtle.curY;
                    }
                }

                line.setLine(x1, y1, x2, y2);
                tryRecord2DMode(turtle.curX, turtle.curY);
                g.draw(line);
                clip();
            }
            eraseTurtle(true);
        } else if (DrawPanel.windowMode == DrawPanel.WINDOW_WRAP) { //mode enroule
            traceWrap(arg, oldx, oldy);
        } else if (DrawPanel.windowMode == DrawPanel.WINDOW_CLOSE) { //mode clos
            try {
                traceClosed(oldx, oldy, arg);
            } catch (LogoException ignored) {
            }
        } else if (DrawPanel.windowMode == DrawPanel.WINDOW_3D) {
            eraseTurtle(false);
            turtle.X = turtle.X + arg * turtle.getRotationMatrix()[0][1];
            turtle.Y = turtle.Y + arg * turtle.getRotationMatrix()[1][1];
            turtle.Z = turtle.Z + arg * turtle.getRotationMatrix()[2][1];

            double[] tmp = new double[3];
            tmp[0] = turtle.X;
            tmp[1] = turtle.Y;
            tmp[2] = turtle.Z;

            tmp = this.toScreenCoord(tmp, true);
            turtle.curX = tmp[0];
            turtle.curY = tmp[1];


            if (turtle.isPenDown()) {
                if (turtle.isPenReverse()) {
                    g.setColor(screenColor);
                    g.setXORMode(turtle.penColor);
                } else {
                    g.setColor(turtle.penColor);
                    g.setPaintMode();
                }
                if (null == line) line = new Line2D.Double();

                if (oldx < turtle.curX) {
                    x1 = oldx;
                    y1 = oldy;
                    x2 = turtle.curX;
                    y2 = turtle.curY;
                }
                if (oldx > turtle.curX) {
                    x2 = oldx;
                    y2 = oldy;
                    x1 = turtle.curX;
                    y1 = turtle.curY;
                } else if (oldx == turtle.curX) {
                    if (oldy < turtle.curY) {
                        x2 = oldx;
                        y2 = oldy;
                        x1 = turtle.curX;
                        y1 = turtle.curY;
                    } else {
                        x1 = oldx;
                        y1 = oldy;
                        x2 = turtle.curX;
                        y2 = turtle.curY;
                    }
                }

                line.setLine(x1, y1, x2, y2);

                g.draw(line);
                clip();
            }
            eraseTurtle(true);
        }
    }

    /**
     * This method is used for drawing with primitive "right" or "left"
     *
     * @param arg The angle to rotate
     */
    protected void turn(double arg) {
        if (turtle.isVisible())
            eraseTurtle(false);
        if (!enabled3D()) {
            turtle.heading = ((turtle.heading + arg) % 360 + 360) % 360;
            turtle.angle = Math.toRadians(90 - turtle.heading);
        } else {
            turtle.setRotationMatrix(world3D.multiply(turtle.getRotationMatrix(), world3D.rotationZ(-arg)));
            double[] tmp = world3D.rotationToEuler(turtle.getRotationMatrix());
            turtle.heading = tmp[2];
            turtle.roll = tmp[1];
            turtle.pitch = tmp[0];
        }
        if (turtle.isVisible())
            eraseTurtle(true);
        Interpreter.returnValue = false;
    }

    /**
     * This method is used for drawing with primitive "rightroll" or "leftroll"
     *
     * @param arg left or right
     */
    protected void roll(double arg) {
        if (turtle.isVisible())
            eraseTurtle(false);
        if (enabled3D()) {
            turtle.setRotationMatrix(world3D.multiply(turtle.getRotationMatrix(), world3D.rotationY(-arg)));
            double[] tmp = world3D.rotationToEuler(turtle.getRotationMatrix());
            turtle.heading = tmp[2];
            turtle.roll = tmp[1];
            turtle.pitch = tmp[0];
        }
        if (turtle.isVisible())
            eraseTurtle(true);
        Interpreter.returnValue = false;
    }

    /**
     * This method is used for drawing with primitive "uppitch" or "downpitch"
     *
     * @param arg up or down
     */
    protected void pitch(double arg) {
        if (turtle.isVisible())
            eraseTurtle(false);
        if (enabled3D()) {
            turtle.setRotationMatrix(world3D.multiply(turtle.getRotationMatrix(), world3D.rotationX(arg)));
            double[] tmp = world3D.rotationToEuler(turtle.getRotationMatrix());
            turtle.heading = tmp[2];
            turtle.roll = tmp[1];
            turtle.pitch = tmp[0];
        }
        if (turtle.isVisible())
            eraseTurtle(true);
        Interpreter.returnValue = false;
    }

    /**
     * This method set the turtle's Roll
     *
     * @param arg The new roll
     */
    protected void setRoll(double arg) {
        if (turtle.isVisible())
            eraseTurtle(false);
        turtle.roll = arg;
        turtle.setRotationMatrix(world3D.EulerToRotation(-turtle.roll, turtle.pitch, -turtle.heading));
        if (turtle.isVisible())
            eraseTurtle(true);
        Interpreter.returnValue = false;
    }

    /**
     * This method set the turtle's heading
     *
     * @param arg The new heading
     */
    protected void setHeading(double arg) {
        if (turtle.isVisible())
            eraseTurtle(false);
        turtle.heading = arg;
        turtle.setRotationMatrix(world3D.EulerToRotation(-turtle.roll, turtle.pitch, -turtle.heading));
        if (turtle.isVisible())
            eraseTurtle(true);
        Interpreter.returnValue = false;
    }

    /**
     * This method set the turtle's pitch
     *
     * @param arg The new pitch
     */
    protected void setPitch(double arg) {
        if (turtle.isVisible())
            eraseTurtle(false);
        turtle.pitch = arg;
        turtle.setRotationMatrix(world3D.EulerToRotation(-turtle.roll, turtle.pitch, -turtle.heading));
        if (turtle.isVisible())
            eraseTurtle(true);
        Interpreter.returnValue = false;
    }

    /**
     * This method set the turtle's orientation
     *
     * @param arg The new orientation
     * @throws LogoException If the list doesn't contain three numbers
     */
    protected void setOrientation(String arg) throws LogoException {
        initCoords();
        if (turtle.isVisible())
            eraseTurtle(false);
        extractCoords(arg, Utils.primitiveName("3d.setorientation"));
        turtle.roll = coords[0];
        turtle.pitch = coords[1];
        turtle.heading = coords[2];
        turtle.setRotationMatrix(world3D.EulerToRotation(-turtle.roll, turtle.pitch, -turtle.heading));
        if (turtle.isVisible())
            eraseTurtle(true);
        Interpreter.returnValue = false;
    }

    /**
     * Primitive "origine"
     */
    protected void home() { // primitive origine
        try {
            if (!enabled3D())
                setPosition("0 0");
            else setPosition("0 0 0");
        } catch (LogoException ignored) {
        }
        if (turtle.isVisible())
            eraseTurtle(false);
        turtle.heading = 0;
        turtle.angle = Math.PI / 2;
        turtle.roll = 0;
        turtle.pitch = 0;
        if (enabled3D())
            turtle.setRotationMatrix(world3D.EulerToRotation(-turtle.roll, turtle.pitch, -turtle.heading));
        if (turtle.isVisible())
            eraseTurtle(true);
    }

    /**
     * Primitive distance
     *
     * @param liste The coords
     * @return The distance from the turtle position to this point
     * @throws LogoException If bad format list
     */
    protected double distance(String liste) throws LogoException {
        initCoords();
        extractCoords(liste, Utils.primitiveName("drawing.distance"));
        double distance;
        if (!enabled3D()) {
            coords = this.toScreenCoord(coords, false);
            distance = Math.sqrt(Math.pow(turtle.curX - coords[0], 2)
                    + Math.pow(turtle.curY - coords[1], 2));
        } else distance = Math.sqrt(Math.pow(turtle.X - coords[0], 2)
                + Math.pow(turtle.Y - coords[1], 2) + Math.pow(turtle.Z - coords[2], 2));
        return distance;
    }

    protected double[] vers3D(String liste) throws LogoException {
        double[] tmp = new double[3];
        initCoords();
        extractCoords(liste, Utils.primitiveName("drawing.towards"));
        tmp[0] = coords[0] - turtle.X;
        tmp[1] = coords[1] - turtle.Y;
        tmp[2] = coords[2] - turtle.Z;
        double length = Math.sqrt(Math.pow(tmp[0], 2) + Math.pow(tmp[1], 2) + Math.pow(tmp[2], 2));
        if (length == 0) return tmp;
        tmp[0] = tmp[0] / length;
        tmp[1] = tmp[1] / length;
        tmp[2] = tmp[2] / length;
        double heading = Math.acos(tmp[1]);
        double f = Math.sin(heading);
        double tr_x = -tmp[0] / f;
        double tr_y = -tmp[2] / f;
        double roll = Math.atan2(tr_y, tr_x);
        tmp[0] = -Math.toDegrees(roll);
        tmp[1] = 0;
        tmp[2] = -Math.toDegrees(heading);
        for (int i = 0; i < 3; i++) {
            if (tmp[i] < 0) tmp[i] += 360;
        }
        return tmp;
    }

    /**
     * Primitive towards in 2D MODE
     *
     * @param liste the coordinate for the point
     * @return the rotation angle
     * @throws LogoException if Bad format List
     */
    protected double to2D(String liste) throws LogoException {
        initCoords();
        extractCoords(liste, Utils.primitiveName("drawing.towards"));
        double angle;
        coords = this.toScreenCoord(coords, false);
        if (turtle.curY == coords[1]) {
            if (coords[0] > turtle.curX)
                angle = 90;
            else if (coords[0] == turtle.curX)
                angle = 0;
            else
                angle = 270;
        } else if (turtle.curX == coords[0]) {
            if (turtle.curY > coords[1])
                angle = 0;
            else
                angle = 180;
        } else {
            angle = Math.toDegrees(Math.atan(Math
                    .abs(coords[0] - turtle.curX)
                    / Math.abs(turtle.curY - coords[1])));
            //		System.out.println(coords[0] - tortue.corX+" "+Math.abs(tortue.corY - coords[1])+" "+angle);
            if (coords[0] > turtle.curX && coords[1] > turtle.curY)
                angle = 180 - angle; // 2eme quadrant
            else if (coords[0] < turtle.curX && coords[1] > turtle.curY)
                angle = 180 + angle; // 3eme quadrant
            else if (coords[0] < turtle.curX)
                angle = 360 - angle; // 4eme quadrant
        }
        return angle;
    }

    /**
     * Draw with the primitive "setposition" in 2D mode or 3D
     *
     * @param liste The list with the coordinates to move
     * @throws LogoException If the coordinates are invalid
     */
    protected void setPosition(String liste) throws LogoException {
        initCoords();
        oldx = turtle.curX;
        oldy = turtle.curY;
        extractCoords(liste, Utils.primitiveName("drawing.setposition"));
        eraseTurtle(false);
        if (enabled3D()) {
            turtle.X = coords[0];
            turtle.Y = coords[1];
            turtle.Z = coords[2];
        }
        coords = toScreenCoord(coords, true);

        turtle.curX = coords[0];
        turtle.curY = coords[1];
        if (turtle.isPenDown()) {
            if (turtle.isPenReverse()) {
                g.setColor(screenColor);
                g.setXORMode(turtle.penColor);
            } else {
                g.setColor(turtle.penColor);
                g.setPaintMode();
            }
            if (null == line) line = new Line2D.Double();
            if (oldx < turtle.curX) {
                x1 = oldx;
                y1 = oldy;
                x2 = turtle.curX;
                y2 = turtle.curY;
            }
            if (oldx > turtle.curX) {
                x2 = oldx;
                y2 = oldy;
                x1 = turtle.curX;
                y1 = turtle.curY;
            } else if (oldx == turtle.curX) {
                if (oldy < turtle.curY) {
                    x2 = oldx;
                    y2 = oldy;
                    x1 = turtle.curX;
                    y1 = turtle.curY;
                } else {
                    x1 = oldx;
                    y1 = oldy;
                    x2 = turtle.curX;
                    y2 = turtle.curY;
                }
            }
            line.setLine(x1, y1, x2, y2);
            tryRecord2DMode(turtle.curX, turtle.curY);
            g.draw(line);
            clip();
        }
        eraseTurtle(true);
    }

    public void drawEllipseArc(double xAxis, double yAxis, double angleRotation, double xCenter, double yCenter, double angleStart, double angleExtent) {
        eraseTurtle(false);
        arc = new Arc2D.Double(-xAxis, -yAxis, 2 * xAxis, 2 * yAxis, angleStart, angleExtent, Arc2D.Double.OPEN);
        if (turtle.isPenReverse()) {
            g.setColor(screenColor);
            g.setXORMode(turtle.penColor);
        } else {
            g.setColor(turtle.penColor);
            g.setPaintMode();
        }
        double tmpx = Logo.config.getImageWidth() / 2.0 + xCenter;
        double tmpy = Logo.config.getImageHeight() / 2.0 - yCenter;
        g.translate(tmpx, tmpy);
        g.rotate(-angleRotation);
        g.draw(arc);
        g.rotate(angleRotation);
        g.translate(-tmpx, -tmpy);
        clip();
        eraseTurtle(true); // on efface la tortue si elle st visible
    }

    /**
     * This method draw an arc on the drawing area
     *
     * @param rayon  The radius
     * @param pangle Starting angle
     * @param fangle End angle
     */
    protected void arc(double rayon, double pangle, double fangle) {
        // Put fangle and pangle between 0 and 360
        fangle = ((90 - fangle) % 360);
        pangle = ((90 - pangle) % 360);
        if (fangle < 0) fangle += 360;
        if (pangle < 0) pangle += 360;
        // Calculate angle extend
        double angle = pangle - fangle;
        if (angle < 0) angle += 360;
        eraseTurtle(false);
        if (null == arc) arc = new Arc2D.Double();
        if (!enabled3D()) {
            if (DrawPanel.windowMode == DrawPanel.WINDOW_WRAP) centers = new Vector<>();
            arc2D(turtle.curX, turtle.curY, rayon, fangle, angle);
            clip();

        } else {
            arcCircle3D(rayon, fangle, angle);
        }
        eraseTurtle(true);
    }

    private void arc2D(double x, double y, double radius, double fangle, double angle) {
        arc.setArcByCenter(x, y, radius,
                fangle, angle, Arc2D.OPEN);
        if (turtle.isPenReverse()) {
            g.setColor(screenColor);
            g.setXORMode(turtle.penColor);
        } else {
            g.setColor(turtle.penColor);
            g.setPaintMode();
        }
        g.draw(arc);
        clip();
        if (DrawPanel.windowMode == DrawPanel.WINDOW_WRAP) {
            if (x + radius > Logo.config.getImageWidth() && x <= Logo.config.getImageWidth()) {
                pt = new Point2D.Double(-Logo.config.getImageWidth() + x, y);
                if (!centers.contains(pt)) {
                    centers.add(pt);
                    arc2D(-Logo.config.getImageWidth() + x, y, radius, fangle, angle);
                }
            }
            if (x - radius < 0 && x >= 0) {
                pt = new Point2D.Double(Logo.config.getImageWidth() + x, y);
                if (!centers.contains(pt)) {
                    centers.add(pt);
                    arc2D(Logo.config.getImageWidth() + x, y, radius, fangle, angle);
                }
            }
            if (y - radius < 0 && y >= 0) {
                pt = new Point2D.Double(x, Logo.config.getImageHeight() + y);
                if (!centers.contains(pt)) {
                    centers.add(pt);
                    arc2D(x, Logo.config.getImageHeight() + y, radius, fangle, angle);
                }
            }
            if (y + radius > Logo.config.getImageHeight() && y <= Logo.config.getImageHeight()) {
                pt = new Point2D.Double(x, -Logo.config.getImageHeight() + y);
                if (!centers.contains(pt)) {
                    centers.add(pt);
                    arc2D(x, -Logo.config.getImageHeight() + y, radius, fangle, angle);
                }
            }
        }
    }

    private void arcCircle3D(double radius, double angleStart, double angleExtent) {
        if (null == arc) arc = new Arc2D.Double();
        arc.setArcByCenter(0, 0, radius,
                angleStart, angleExtent, Arc2D.OPEN);
        Shape s = transformShape(arc);
        if (turtle.isPenReverse()) {
            g.setColor(screenColor);
            g.setXORMode(turtle.penColor);
        } else {
            g.setColor(turtle.penColor);
            g.setPaintMode();
        }
        g.draw(s);
        if (DrawPanel.record3D == DrawPanel.RECORD_3D_LINE || DrawPanel.record3D == DrawPanel.RECORD_3D_POLYGON) {
            recordArcCircle3D(radius, angleStart, angleExtent);
        }
    }

    /**
     * returns the color for the pixel "ob"
     *
     * @param liste: The list containing the coordinates of the pixel
     * @return Color of this pixel
     * @throws LogoException If the list doesn't contain coordinates
     */
    protected Color guessColorPoint(String liste) throws LogoException {
        initCoords();
        extractCoords(liste, Utils.primitiveName("drawing.findcolor"));
        coords = toScreenCoord(coords, false);
        int couleur = -1;
        int x = (int) (coords[0] * scaleX);
        int y = (int) (coords[1] * scaleY);
        if (0 < x && x < image.getWidth() && 0 < y && y < image.getHeight()) {
            couleur = image.getRGB(x, y);
        }
        return new Color(couleur);
    }

    /**
     * This method draw a circle from the turtle position on the drawing area
     *
     * @param radius The radius of the circle
     */
    protected void circle(double radius) {
        eraseTurtle(false);
        if (null == arc) arc = new Arc2D.Double();
        if (!enabled3D()) {
            if (DrawPanel.windowMode == DrawPanel.WINDOW_WRAP) centers = new Vector<>();
            circle2D(turtle.curX, turtle.curY, radius);
        } else {
            circle3D(radius);
        }
        eraseTurtle(true); // on efface la tortue si elle st visible
    }

    private void circle2D(double x, double y, double radius) {

        arc.setArcByCenter(x, y, radius,
                0, 360, Arc2D.OPEN);

        if (turtle.isPenReverse()) {
            g.setColor(screenColor);
            g.setXORMode(turtle.penColor);
        } else {
            g.setColor(turtle.penColor);
            g.setPaintMode();
        }
        g.draw(arc);
        clip();
        if (DrawPanel.windowMode == DrawPanel.WINDOW_WRAP) {
            if (x + radius > Logo.config.getImageWidth() && x <= Logo.config.getImageWidth()) {
                pt = new Point2D.Double(-Logo.config.getImageWidth() + x, y);
                if (!centers.contains(pt)) {
                    centers.add(pt);
                    circle2D(-Logo.config.getImageWidth() + x, y, radius);
                }
            }
            if (x - radius < 0 && x >= 0) {
                pt = new Point2D.Double(Logo.config.getImageWidth() + x, y);
                if (!centers.contains(pt)) {
                    centers.add(pt);
                    circle2D(Logo.config.getImageWidth() + x, y, radius);
                }
            }
            if (y - radius < 0 && y >= 0) {
                pt = new Point2D.Double(x, Logo.config.getImageHeight() + y);
                if (!centers.contains(pt)) {
                    centers.add(pt);
                    circle2D(x, Logo.config.getImageHeight() + y, radius);
                }
            }
            if (y + radius > Logo.config.getImageHeight() && y <= Logo.config.getImageHeight()) {
                pt = new Point2D.Double(x, -Logo.config.getImageHeight() + y);
                if (!centers.contains(pt)) {
                    centers.add(pt);
                    circle2D(x, -Logo.config.getImageHeight() + y, radius);
                }
            }
        }
    }

    /**
     * used for drawing with primitive "dot"
     *
     * @param liste The list with the dot coordinates
     * @throws LogoException If the list is invalid coordinates
     */
    protected void point(String liste) throws LogoException {
        initCoords();
        extractCoords(liste, Utils.primitiveName("drawing.dot"));
        coords = toScreenCoord(coords, true);
//		System.out.println(coords[0]+" "+coords[1]+" "+Logo.config.imageHeight+" "+Logo.config.imageWidth);
        if (coords[0] > 0 && coords[1] > 0 && coords[0] < Logo.config.getImageWidth() && coords[1] < Logo.config.getImageHeight()) {
            if (turtle.isPenReverse()) {
                g.setColor(screenColor);
                g.setXORMode(turtle.penColor);

            } else {
                g.setColor(turtle.penColor);
                g.setPaintMode();
            }
            if (rect == null) rect = new Rectangle2D.Double();
            // High quality
            if (Logo.config.getDrawQuality() == Config.DRAW_QUALITY_HIGH) {
                double width = turtle.getPenWidth();
                rect.setRect(coords[0] - width + 0.5, coords[1] - width + 0.5,
                        2 * width, 2 * width);
            }
            // Normal or Low Quality
            else {
                // penWidth is 2k or 2k+1??
                int intWidth = (int) (2 * turtle.getPenWidth() + 0.5);
                if (intWidth % 2 == 1) {
                    double width = turtle.getPenWidth() - 0.5;
//					System.out.println(coords[0]+" "+coords[1]);
                    rect.setRect(coords[0] - width, coords[1] - width,
                            2 * width + 1, 2 * width + 1);
                } else {
                    double width = turtle.getPenWidth();
                    rect.setRect(coords[0] - width, coords[1] - width,
                            2 * width, 2 * width);
                }
            }
            if (Logo.config.getPenShape() == Config.PEN_SHAPE_SQUARE) {
                g.fill(rect);
            } else if (Logo.config.getPenShape() == Config.PEN_SHAPE_OVAL) {
                if (null == arc) arc = new Arc2D.Double();
                arc.setArcByCenter(coords[0], coords[1], 0, 0, 360, Arc2D.OPEN);
                g.draw(arc);
            }
            clip();
        }
    }

    /**
     *
     */

    private void circle3D(double radius) {

        // In camera world,
        // the circle is the intersection of
        // - a plane with the following equation: ax+by+cz+d=0 <-> f(x,y,z)=0
        // - and a sphere with the following equation: (x-tx)^2+(y-ty)^2+(z-tz)^2=R^2 <-> g(x,y,z)=0
        // I found the cone equation resolving f(x/lambda,y/lambda,z/lambda)=0=g(x/lambda,y/lambda,z/lambda)

        double[] v = new double[3];
        for (int i = 0; i < 3; i++) {
            v[i] = turtle.getRotationMatrix()[i][2];
        }
        v[0] += world3D.xCamera;
        v[1] += world3D.yCamera;
        v[2] += world3D.zCamera;
        world3D.toCameraWorld(v);
        // Now v contains coordinates of a normal vector to the plane in camera world coordinates
        double a = v[0];
        double b = v[1];
        double c = v[2];

        // We convert the turtle coordinates
        v[0] = turtle.X;
        v[1] = turtle.Y;
        v[2] = turtle.Z;
        world3D.toCameraWorld(v);

        double x = v[0];
        double y = v[1];
        double z = v[2];
        // We calculate the number d for the plane equation
        double d = -a * x - b * y - c * z;

        // We have to work with Bigdecimal because of precision problems

        BigDecimal[] big = new BigDecimal[6];
        BigDecimal bx = new BigDecimal(x);
        BigDecimal by = new BigDecimal(y);
        BigDecimal bz = new BigDecimal(z);
        BigDecimal ba = new BigDecimal(a);
        BigDecimal bb = new BigDecimal(b);
        BigDecimal bc = new BigDecimal(c);
        BigDecimal bd = new BigDecimal(d);
        BigDecimal deux = new BigDecimal("2");
        BigDecimal screenDistance = new BigDecimal(world3D.screenDistance);
        BigDecimal bradius = new BigDecimal(String.valueOf(radius));

        // Now we calculate the coefficient for the conic ax^2+bxy+cy^2+dx+ey+f=0
        // Saved in an array

        // lambda=(x*x+y*y+z*z-radius*radius);
        BigDecimal lambda = bx.pow(2).add(by.pow(2)).add(bz.pow(2)).subtract(bradius.pow(2));

        // x^2 coeff
        //	d*d+2*d*x*a+a*a*lambda;
        big[0] = bd.pow(2).add(bd.multiply(bx).multiply(ba).multiply(deux)).add(ba.pow(2).multiply(lambda));
        // xy coeff
        // 2*d*x*b+2*d*y*a+2*a*b*lambda;
        big[1] = deux.multiply(bd).multiply(bx).multiply(bb).add(deux.multiply(bd).multiply(by).multiply(ba)).add(deux.multiply(ba).multiply(bb).multiply(lambda));
        // y^2 coeff
        // d*d+2*d*y*b+b*b*lambda;
        big[2] = bd.pow(2).add(bd.multiply(by).multiply(bb).multiply(deux)).add(bb.pow(2).multiply(lambda));
        // x coeff
        // 2*w3d.screenDistance*(d*x*c+d*z*a+lambda*a*c);
        big[3] = deux.multiply(screenDistance).multiply(bd.multiply(bx).multiply(bc).add(bd.multiply(bz).multiply(ba)).add(lambda.multiply(ba).multiply(bc)));
        // y coeff
        // 2*w3d.screenDistance*(d*y*c+d*z*b+lambda*b*c);
        big[4] = deux.multiply(screenDistance).multiply(bd.multiply(by).multiply(bc).add(bd.multiply(bz).multiply(bb)).add(lambda.multiply(bb).multiply(bc)));
        // Numbers
        // Math.pow(w3d.screenDistance,2)*(d*d+2*d*z*c+lambda*c*c);
        big[5] = screenDistance.pow(2).multiply(bd.pow(2).add(deux.multiply(bd).multiply(bz).multiply(bc)).add(lambda.multiply(bc.pow(2))));
        new Conic(app, big);
        if (DrawPanel.record3D == DrawPanel.RECORD_3D_LINE || DrawPanel.record3D == DrawPanel.RECORD_3D_POLYGON) {
            recordArcCircle3D(radius, 0, 360);
        }
    }

    /**
     * This method records this circle in the polygon's List
     *
     * @param radius      The circle's radius
     * @param angleStart  The starting Angle
     * @param angleExtent The angle for the sector
     */
    public void recordArcCircle3D(double radius, double angleStart, double angleExtent) {
        double[][] d = turtle.getRotationMatrix();
        Matrix3d m = new Matrix3d(d[0][0], d[0][1], d[0][2], d[1][0], d[1][1], d[1][2], d[2][0], d[2][1], d[2][2]);
        // Vector X
        Point3d v1 = new Point3d(radius / 1000, 0, 0);
        Transform3D t = new Transform3D(m, new Vector3d(), 1);
        t.transform(v1);
        // Vector Y
        Point3d v2 = new Point3d(0, radius / 1000, 0);
        t.transform(v2);

        // Turtle position
        Point3d pos = new Point3d(turtle.X / 1000, turtle.Y / 1000, turtle.Z / 1000);
        int indexMax = (int) angleExtent;
        if (indexMax != angleExtent) indexMax += 2;
        else indexMax += 1;
        if (null != DrawPanel.poly && DrawPanel.poly.getVertexCount() > 1) {
            try {
                DrawPanel.poly.addToScene();
            } catch (LogoException ignored) {
            }
        }
        if (DrawPanel.record3D == DrawPanel.RECORD_3D_POLYGON) {
            DrawPanel.poly = new ElementPolygon(app);
            DrawPanel.poly.addVertex(pos, turtle.penColor);
        } else {
            DrawPanel.poly = new ElementLine(app);
        }

        for (int i = 0; i < indexMax - 1; i++) {
            Point3d tmp1 = new Point3d(v1);
            tmp1.scale(Math.cos(Math.toRadians(angleStart + i)));
            Point3d tmp2 = new Point3d(v2);
            tmp2.scale(Math.sin(Math.toRadians(angleStart + i)));
            tmp1.add(tmp2);
            tmp1.add(pos);
            DrawPanel.poly.addVertex(tmp1, turtle.penColor);
        }
        Point3d tmp1 = new Point3d(v1);
        tmp1.scale(Math.cos(Math.toRadians(angleStart + angleExtent)));
        Point3d tmp2 = new Point3d(v2);
        tmp2.scale(Math.sin(Math.toRadians(angleStart + angleExtent)));
        tmp1.add(tmp2);
        tmp1.add(pos);
        DrawPanel.poly.addVertex(tmp1, turtle.penColor);
    }

    /**
     * Load an image and draw it on the drawing area
     *
     * @param image The image to draw
     */
    protected void drawImage(BufferedImage image) {
        if (turtle.isVisible())
            eraseTurtle(false);
        g.setPaintMode();
        g.translate(turtle.curX, turtle.curY);
        g.rotate(-turtle.angle);
        g.drawImage(image, null, 0, 0);
        g.rotate(turtle.angle);
        g.translate(-turtle.curX, -turtle.curY);

        clip();
        if (turtle.isVisible())
            eraseTurtle(true);
    }

    /**
     * To guess the length before going out the drawing area in WRAP mode
     *
     * @param mini The minimum distance before leaving
     * @param maxi The maximum distance before leaving
     * @param oldx The X turtle location
     * @param oldy The Y turtle location
     * @return the number of steps (Recursive dichotomy)
     */
    private double findLength(double mini, double maxi, double oldx, double oldy) {
        // renvoie la longueur dont on peut encore avancer
        if (Math.abs(maxi - mini) < 0.5) {
            return (mini);
        } else {
            double middle = (mini + maxi) / 2;
            double nx = oldx + middle * Math.cos(turtle.angle);
            double ny = oldy - middle * Math.sin(turtle.angle);
            if (nx < 0 || nx > Logo.config.getImageWidth() || ny < 0 || ny > Logo.config.getImageHeight())
                return findLength(mini, middle, oldx, oldy);
            else
                return findLength(middle, maxi, oldx, oldy);
        }
    }

    /**
     * This method is used for drawing with primitive forward, backward in WRAP MODE
     *
     * @param arg  the length to forward
     * @param oldx X position
     * @param oldy Y position
     */
    private void traceWrap(double arg, double oldx, double oldy) {
        boolean re = arg < 0;
        double diagonale = Math.sqrt(Math.pow(Logo.config.getImageWidth(), 2) + Math.pow(Logo.config.getImageHeight(), 2)) + 1;
        double longueur;
        if (re)
            longueur = findLength(0, -diagonale, oldx, oldy);
        else
            longueur = findLength(0, diagonale, oldx, oldy);
//		System.out.println(diagonale+" "+oldx+" "+oldy);
        while (Math.abs(longueur) < Math.abs(arg)) {
            //	System.out.println(Math.abs(longueur)+" "+Math.abs(arg));
            arg -= longueur;
            DrawPanel.windowMode = DrawPanel.WINDOW_CLASSIC;
            move(longueur);
            //System.out.println(Math.abs(longueur)+" "+Math.abs(arg));
            if (app.error)
                break; //permet d'interrompre avec le bouton stop
            DrawPanel.windowMode = DrawPanel.WINDOW_WRAP;
            if (Logo.config.getTurtleSpeed() != 0) {
                try {
                    Thread.sleep(Logo.config.getTurtleSpeed() * 5L);
                } catch (InterruptedException ignored) {
                }
            }
            if (turtle.isVisible())
                this.eraseTurtle(false);
            if (re) turtle.heading = (turtle.heading + 180) % 360;
            if (turtle.curX > Logo.config.getImageWidth() - 1
                    && (turtle.heading < 180 && turtle.heading != 0)) {
                turtle.curX = 0;
                if (turtle.curY > Logo.config.getImageHeight() - 1
                        && (turtle.heading > 90 && turtle.heading < 270))
                    turtle.curY = 0;
                else if (turtle.curY < 1
                        && (turtle.heading < 90 || turtle.heading > 270))
                    turtle.curY = Logo.config.getImageHeight();
            } else if (turtle.curX < 1 && turtle.heading > 180) {
                turtle.curX = Logo.config.getImageWidth();
                if (turtle.curY > Logo.config.getImageHeight() - 1
                        && (turtle.heading > 90 && turtle.heading < 270))
                    turtle.curY = 0;
                else if (turtle.curY < 1
                        && (turtle.heading < 90 || turtle.heading > 270))
                    turtle.curY = Logo.config.getImageHeight();
            } else if (turtle.curY > Logo.config.getImageHeight() - 1)
                turtle.curY = 0;
            else if (turtle.curY < 1)
                turtle.curY = Logo.config.getImageHeight();
            if (re) turtle.heading = (turtle.heading + 180) % 360;
            if (turtle.isVisible())
                this.eraseTurtle(true);
            if (re)
                longueur = findLength(0, -diagonale, turtle.curX,
                        turtle.curY);
            else
                longueur = findLength(0, diagonale, turtle.curX,
                        turtle.curY);
        }
        DrawPanel.windowMode = DrawPanel.WINDOW_CLASSIC;
        if (!app.error)
            move(arg);
        DrawPanel.windowMode = DrawPanel.WINDOW_WRAP;
    }

    /**
     * This method is used for drawing with primitive forward, backward in CLOSE MODE
     *
     * @param oldx X position
     * @param oldy Y position
     * @param arg  The length to forward
     * @throws LogoException when value is more than length
     */
    private void traceClosed(double oldx, double oldy, double arg) throws LogoException {
        boolean re = false;
        double longueur;
        double diagonale = Math.sqrt(Math.pow(Logo.config.getImageWidth(), 2) + Math.pow(Logo.config.getImageHeight(), 2)) + 1;
        if (arg < 0)
            re = true;
        if (re)
            longueur = findLength(0, -diagonale, oldx, oldy);
        else
            longueur = findLength(0, diagonale, oldx, oldy);
        if (Math.abs(longueur) < Math.abs(arg))
            throw new LogoException(app, Logo
                    .getString("erreur_sortie1")
                    + "\n"
                    + Logo.getString("erreur_sortie2")
                    + Math.abs((int) (longueur)));
        else {
            DrawPanel.windowMode = DrawPanel.WINDOW_CLASSIC;
            move(arg);
            DrawPanel.windowMode = DrawPanel.WINDOW_CLOSE;
        }
    }

    /**
     * This method extract coords from a list <br>
     * X is stored in coords(0], Y stored in coords[1], Z Stored in coords[2]
     *
     * @param liste The list
     * @param prim  The calling primitive
     * @throws LogoException If List isn't a list coordinate
     */

    private void extractCoords(String liste, String prim) throws LogoException {
        StringTokenizer st = new StringTokenizer(liste);
        try {
            for (int i = 0; i < coords.length; i++) {
                coords[i] = 1;
                if (!st.hasMoreTokens())
                    throw new LogoException(app, prim
                            + " " + Logo.getString("n_aime_pas") + liste
                            + Logo.getString("comme_parametre"));
                String element = st.nextToken();
                if (element.equals("-")) {
                    if (st.hasMoreTokens())
                        element = st.nextToken();
                    coords[i] = -1;
                }
                coords[i] = coords[i] * Double.parseDouble(element);
            }

        } catch (NumberFormatException e) {
            throw new LogoException(app, prim
                    + " " + Logo.getString("n_aime_pas") + liste
                    + Logo.getString("comme_parametre"));
        }
        if (st.hasMoreTokens())
            throw new LogoException(app, prim
                    + " " + Logo.getString("n_aime_pas") + liste
                    + Logo.getString("comme_parametre"));
    }

    /**
     * This method sets the drawing area to perspective mode
     */

    protected void perspective() {
        if (!enabled3D()) {
            Logo.config.setXAxisEnabled(false);
            Logo.config.setYAxisEnabled(false);
            Logo.config.setGridEnabled(false);
            changeTurtleImage(0);
            eraseTurtle(false);
            DrawPanel.windowMode = DrawPanel.WINDOW_3D;
            world3D = new World3D();
            eraseTurtle(true);
        }
    }

    /**
     * This method sets the drawing area to Wrap, Close or Window mode
     *
     * @param id The window Mode
     */
    protected void setWindowMode(int id) {
        if (DrawPanel.windowMode != id) {
            eraseTurtle(false);
            DrawPanel.windowMode = id;
            world3D = null;
            eraseTurtle(true);
        }
    }

    /**
     * This method converts the coordinates contained in "coords" towards the coords on the drawing area
     */
    double[] toScreenCoord(double[] coord, boolean drawPoly) {
        // If Mode perspective is active
        if (enabled3D()) {
            //		w3d.toScreenCoord(coord);
            // camera world
            // If we have to record the polygon coordinates
            if (DrawPanel.record3D != DrawPanel.RECORD_3D_NONE && DrawPanel.record3D != DrawPanel.RECORD_3D_TEXT && drawPoly) {

                DrawPanel.poly.addVertex(new Point3d(coord[0] / 1000, coord[1] / 1000, coord[2] / 1000), turtle.penColor);
            }

            world3D.toCameraWorld(coord);

            // Convert to screen Coordinates
            world3D.cameraToScreen(coord);
        }
        // Mode2D
        else {
            coord[0] = Logo.config.getImageWidth() / 2.0 + coord[0];
            coord[1] = Logo.config.getImageHeight() / 2.0 - coord[1];
        }
        return coord;
    }

    /**
     * This method creates an instance of coord with the valid size:<br>
     * size 2 for 2D coordinates<br>
     * size 3 for 3D coordinates
     */

    private void initCoords() {

        if (null == coords) coords = new double[2];
        if (enabled3D()) {
            if (coords.length != 3) coords = new double[3];
        } else {
            if (coords.length != 2) coords = new double[2];
        }
    }

    public boolean enabled3D() {
        return (DrawPanel.windowMode == DrawPanel.WINDOW_3D);
    }

    /**
     * For hideturtle and showturtle
     */
    protected void toggleTurtle() {
        if (null == turtle.image) {
            g.setXORMode(screenColor);
            g.setColor(turtle.penColor);
            turtle.drawTriangle();
            BasicStroke crayon_actuel = (BasicStroke) g.getStroke();
            if (crayon_actuel.getLineWidth() == 1)
                g.draw(turtle.triangle);
            else {
                g.setStroke(triangleStroke);
                g.draw(turtle.triangle);
                g.setStroke(crayon_actuel);
            }
        } else {
            g.setXORMode(screenColor);
            double angle = Math.PI / 2 - turtle.angle;
            float x = (float) (turtle.curX * Math.cos(angle) + turtle.curY
                    * Math.sin(angle));
            float y = (float) (-turtle.curX * Math.sin(angle) + turtle.curY
                    * Math.cos(angle));
            g.rotate(angle);
            g.drawImage(
                    turtle.image,
                    (int) x - turtle.width / 2,
                    (int) y - turtle.height / 2,
                    turtle.width,
                    turtle.height,
                    this
            );
            g.rotate(-angle);
        }
        clip();
    }




    /**
     * When the turtle has to be redrawn, this method erase the turtle on the drawing screen
     */
    protected void eraseTurtle(boolean b) {
        g.setColor(screenColor);
        for (String visibleTurtle : visibleTurtles) {
            int id = Integer.parseInt(visibleTurtle);
            // Turtle triangle
            if (null == turtles[id].image) {
                g.setXORMode(screenColor);
                g.setColor(turtles[id].imageColorMode);
                turtles[id].drawTriangle();
                BasicStroke crayon_actuel = (BasicStroke) g.getStroke();
                if (crayon_actuel.getLineWidth() == 1)
                    g.draw(turtles[id].triangle);
                else {
                    g.setStroke(triangleStroke);
                    g.draw(turtles[id].triangle);
                    g.setStroke(crayon_actuel);
                }
            } else {
                // Image turtle
                g.setXORMode(screenColor);
                double angle = Math.PI / 2 - turtles[id].angle;
                float x = (float) (turtles[id].curX * Math.cos(angle) + turtles[id].curY
                        * Math.sin(angle));
                float y = (float) (-turtles[id].curX * Math.sin(angle) + turtles[id].curY
                        * Math.cos(angle));
                g.rotate(angle);
                g.drawImage(turtles[id].image,
                        (int) x - turtles[id].width / 2,
                        (int) y - turtles[id].height / 2,
                        turtles[id].width,
                        turtles[id].height,
                        this
                );
                g.rotate(-angle);
            }
            if (b) clip();
        }
    }

    /**
     * Primitive clearscreen
     */
    protected void clearScreen() {
        // Delete all Gui Component
        Set<String> set = guiMap.keySet();
        Iterator<String> it = set.iterator();
        while (it.hasNext()) {
            String element = it.next();
            gui = guiMap.get(element).getGuiObject();
            it.remove();
            if (SwingUtilities.isEventDispatchThread()) {
                remove(gui);
                validate();
            } else {
                try {
                    SwingUtilities.invokeAndWait(() -> {
                        remove(gui);
                        validate();
                    });
                } catch (Exception ignored) {
                }
            }
        }


        // Delete List Polygon in 3D mode
        // Erase the 3d viewer if visible
        if (null != app.getViewer3D()) {
            app.getViewer3D().clearScreen();
            System.gc();
        }


        g.setPaintMode();
        screenColor = Logo.config.getScreenColor();
        g.setColor(Logo.config.getScreenColor());
        g.fillRect(0, 0, Logo.config.getImageWidth(), Logo.config.getImageHeight());
        stopRecord2DPolygon();

        // Draw Grid
        g.setStroke(new BasicStroke(1));
        drawGrid();
        drawXAxis();
        drawYAxis();
        // Init Turtles
        if (null == turtles[0])
            turtles[0] = new Turtle(app);
        // The active turtle will be the turtle 0
        turtle = turtles[0];
        turtle.id = 0;
        // We delete all other turtles
        for (int i = 1; i < turtles.length; i++) {
            turtles[i] = null;
        }
        visibleTurtles.removeAllElements();
        visibleTurtles.push("0");
        g.setColor(turtle.penColor);
        clip();
        turtle.init();
        turtle.setVisible(true);
        g.setStroke(new BasicStroke(1));
        eraseTurtle(true);
        // Update the selection frame
        updateColorSelection();
    }

    /**
     * Primitive wash
     */
    protected void wash() {
        stopRecord2DPolygon();
        g.setPaintMode();
        g.setColor(screenColor);
        g.fillRect(0, 0, Logo.config.getImageWidth(), Logo.config.getImageHeight());

        drawGrid();
        g.setColor(turtle.penColor);
        clip();

        if (turtle.isVisible())
            eraseTurtle(true);
        else
            visibleTurtles = new Stack<>();
    }

    /**
     * Used for primitive fillzone
     */

    private int fillZoneBounds(int x, int y, int increment, int borderColor) {
        while (!isSameColor(image.getRGB(x, y), borderColor)) {
            image.setRGB(x, y, borderColor);
            x = x + increment;
            if (!(x > 0 && x < image.getWidth() - 1))
                break;
        }
        return x - increment;
    }

    /**
     * Are the two color equals?
     *
     * @param col1 The first color
     * @param col2 The second color
     * @return true or false
     */
    private boolean isSameColor(int col1, int col2) {
        return (col1 == col2);
    }

    /**
     * Primitive fillzone
     */
    protected void fillZone() {
        eraseTurtle(false);
        int x = (int) (turtle.curX * scaleX);
        int y = (int) (turtle.curY * scaleY);
        if (x > 0 & x < image.getWidth() & y > 0 & y < image.getHeight()) {
            int couleur_origine = image.getRGB(x, y);
            int couleur_frontiere = turtle.penColor.getRGB();
            Stack<Point> pile_germes = new Stack<>();
            boolean couleurs_differentes = !isSameColor(couleur_origine, couleur_frontiere);
            if (couleurs_differentes)
                pile_germes.push(new Point(x, y));
            while (!pile_germes.isEmpty()) {

                Point p = pile_germes.pop();
                int xgerme = p.x;
                int ygerme = p.y;
                int xmax = fillZoneBounds(xgerme, ygerme, 1,
                        couleur_frontiere);
                int xmin = 0;
                if (xgerme > 0) xmin = fillZoneBounds(xgerme - 1, ygerme, -1,
                        couleur_frontiere);
                boolean ligne_dessus = false;
                boolean ligne_dessous = false;
                for (int i = xmin; i < xmax + 1; i++) {
                    //on recherche les germes au dessus et au dessous
                    if (ygerme > 0
                            && isSameColor(image.getRGB(i, ygerme - 1), couleur_frontiere)) {
                        if (ligne_dessus)
                            pile_germes.push(new Point(i - 1, ygerme - 1));
                        ligne_dessus = false;
                    } else {
                        ligne_dessus = true;
                        if (i == xmax && ygerme > 0)
                            pile_germes.push(new Point(xmax, ygerme - 1));
                    }
                    if (ygerme < image.getHeight() - 1
                            && isSameColor(image.getRGB(i, ygerme + 1), couleur_frontiere)) {
                        if (ligne_dessous)
                            pile_germes.push(new Point(i - 1, ygerme + 1));
                        ligne_dessous = false;
                    } else {
                        ligne_dessous = true;
                        if (i == xmax && ygerme < image.getHeight() - 1)
                            pile_germes.push(new Point(xmax, ygerme + 1));
                    }
                }
            }
            clip();
            eraseTurtle(true);
        }
    }

    /**
     * Used for primitive "fill"
     */
    private int fillBounds(int x, int y, int increment, int penColor,
                           int originColor) {
        while (image.getRGB(x, y) == originColor) {
            image.setRGB(x, y, penColor);
            x = x + increment;
            if (!(x > 0 && x < image.getWidth() - 1))
                break;
        }
        return x - increment;
    }

    /**
     * Primitive "fill"
     */
    protected void fill() {
        eraseTurtle(false);
        int x = (int) (turtle.curX * scaleX);
        int y = (int) (turtle.curY * scaleY);
        if (x > 0 & x < image.getWidth() & y > 0 & y < image.getHeight()) {
            int couleur_origine = image.getRGB(x, y);
            int couleur_crayon = turtle.penColor.getRGB();
            if (x < image.getWidth() && y < image.getHeight()) {
                Stack<Point> pile_germes = new Stack<>();
                boolean couleurs_differentes = !(couleur_origine == couleur_crayon);
                if (couleurs_differentes)
                    pile_germes.push(new Point(x, y));
                while (!pile_germes.isEmpty()) {
                    Point p = pile_germes.pop();
                    int xgerme = p.x;
                    int ygerme = p.y;
                    //			System.out.println(xgerme+" "+ygerme);
                    int xmax = fillBounds(xgerme, ygerme, 1, couleur_crayon,
                            couleur_origine);
                    int xmin = 0;
                    if (xgerme > 0) xmin = fillBounds(xgerme - 1, ygerme, -1,
                            couleur_crayon, couleur_origine);
                    //				System.out.println("xmax "+xmax+"xmin "+xmin);
                    boolean ligne_dessus = false;
                    boolean ligne_dessous = false;
                    for (int i = xmin; i < xmax + 1; i++) {
                        //on recherche les germes au dessus et au dessous
                        if (ygerme > 0
                                && image.getRGB(i, ygerme - 1) != couleur_origine) {
                            if (ligne_dessus)
                                pile_germes.push(new Point(i - 1, ygerme - 1));
                            ligne_dessus = false;
                        } else {
                            ligne_dessus = true;
                            if (i == xmax && ygerme > 0)
                                pile_germes.push(new Point(xmax, ygerme - 1));
                        }
                        if (ygerme < image.getHeight() - 1
                                && image.getRGB(i, ygerme + 1) != couleur_origine) {
                            if (ligne_dessous)
                                pile_germes.push(new Point(i - 1, ygerme + 1));
                            ligne_dessous = false;
                        } else {
                            ligne_dessous = true;
                            if (i == xmax && ygerme < image.getHeight() - 1)
                                pile_germes.push(new Point(xmax, ygerme + 1));
                        }
                    }
                }
                clip();
                eraseTurtle(true);
            }
        }
    }

    /**
     * Primitive "label"
     *
     * @param mot The word to write on the drawing area
     */
    protected void label(String mot) {
        eraseTurtle(false);
        if (!enabled3D()) {
            double angle = Math.PI / 2 - turtle.angle;
            if (DrawPanel.windowMode == DrawPanel.WINDOW_WRAP) centers = new Vector<>();
            label2D(turtle.curX, turtle.curY, angle, mot);
        } else {
            FontRenderContext frc = g.getFontRenderContext();
            GlyphVector gv = g.getFont().createGlyphVector(frc, mot);
            Shape outline = gv.getOutline(0, 0);
            Shape s = transformShape(outline);
            g.setPaintMode();
            g.setColor(turtle.penColor);
            g.fill(s);
            if (record3D == DrawPanel.RECORD_3D_TEXT) {
                Text2D text = new Text2D(
                        mot, new Color3f(turtle.penColor), Application.fonts[drawingFont].getName(),
                        turtle.police, Font.PLAIN);
                text.setRectangleScaleFactor(0.001f);
                Appearance appear = text.getAppearance();
                PolygonAttributes pa = new PolygonAttributes();
                pa.setCullFace(PolygonAttributes.CULL_NONE);
                pa.setBackFaceNormalFlip(true);
                appear.setPolygonAttributes(pa);
                text.setAppearance(appear);
                TransformGroup tg = new TransformGroup();
                double[][] d = turtle.getRotationMatrix();
                Matrix3d m = new Matrix3d(d[0][0], d[0][1], d[0][2], d[1][0], d[1][1], d[1][2], d[2][0], d[2][1], d[2][2]);
                Transform3D t = new Transform3D(m, new Vector3d(turtle.X / 1000, turtle.Y / 1000, turtle.Z / 1000), 1);
                tg.setTransform(t);
                tg.addChild(text);
                app.getViewer3D().add2DText(tg);
            }
        }
        eraseTurtle(true);
        if (classicMode) repaint();
    }

    private void label2D(double x, double y, double angle, String word) {
        if (word.length() == 0) return;

        g.setPaintMode();
        g.setColor(turtle.penColor);
        Font f = Application.fonts[drawingFont]
                .deriveFont((float) turtle.police);
        g.setFont(f);
        g.translate(x, y);
        g.rotate(angle);
        FontRenderContext frc = g.getFontRenderContext();
        TextLayout layout = new TextLayout(word, f, frc);
        Rectangle2D bounds = layout.getBounds();
        float height = (float) bounds.getHeight();
        float width = (float) bounds.getWidth();
        float x1 = 0, y1 = 0;
        switch (turtle.getLabelHorizontalAlignment()) {
            case Turtle.LABEL_HORIZONTAL_ALIGNMENT_LEFT:
                x1 = 0;
                break;
            case Turtle.LABEL_HORIZONTAL_ALIGNMENT_CENTER:
                x1 = -width / 2;
                break;
            case Turtle.LABEL_HORIZONTAL_ALIGNMENT_RIGHT:
                x1 = -width;
                break;
        }
        switch (turtle.getLabelVerticalAlignment()) {
            case Turtle.LABEL_VERTICAL_ALIGNMENT_BOTTOM:
                y1 = 0;
                break;
            case Turtle.LABEL_VERTICAL_ALIGNMENT_CENTER:
                y1 = height / 2;
                break;
            case Turtle.LABEL_VERTICAL_ALIGNMENT_TOP:
                y1 = height;
                break;
        }
        layout.draw(g, x1, y1);
        g.drawString(word, x1, y1);
        g.rotate(-angle);
        g.translate(-x, -y);
        if (DrawPanel.windowMode == DrawPanel.WINDOW_WRAP) {
            Rectangle2D.Double rec = new Rectangle2D.Double(0, 0, width, height);
            AffineTransform at = new AffineTransform();
            at.translate(x, y);
            at.rotate(angle);
            Rectangle2D bound = at.createTransformedShape(rec).getBounds2D();
            double right = bound.getX() + bound.getWidth() - x;
            double left = x - bound.getX();
            double up = y - bound.getY();
            double down = bound.getY() + bound.getHeight() - y;
            if (x + right > Logo.config.getImageWidth() && x <= Logo.config.getImageWidth()) {
                pt = new Point2D.Double(-Logo.config.getImageWidth() + x, y);
                if (!centers.contains(pt)) {
                    centers.add(pt);
                    label2D(-Logo.config.getImageWidth() + x, y, angle, word);
                }
            }
            if (x - left < 0 && x >= 0) {
                pt = new Point2D.Double(Logo.config.getImageWidth() + x, y);
                if (!centers.contains(pt)) {
                    centers.add(pt);
                    label2D(Logo.config.getImageWidth() + x, y, angle, word);
                }
            }
            if (y - up < 0 && y >= 0) {
                pt = new Point2D.Double(x, Logo.config.getImageHeight() + y);
                if (!centers.contains(pt)) {
                    centers.add(pt);
                    label2D(x, Logo.config.getImageHeight() + y, angle, word);
                }
            }
            if (y + down > Logo.config.getImageHeight() && y <= Logo.config.getImageHeight()) {
                pt = new Point2D.Double(x, -Logo.config.getImageHeight() + y);
                if (!centers.contains(pt)) {
                    centers.add(pt);
                    label2D(x, -Logo.config.getImageHeight() + y, angle, word);
                }
            }
        }
    }

    /**
     * This method transform a plane 2D shape in the shape corresponding to the turtle plane
     *
     * @param s the first shape
     * @return the new shape after transformation
     */
    private Shape transformShape(Shape s) {
        PathIterator it = s.getPathIterator(null);
        double[] d = new double[6];
        double[][] coor = new double[3][1];
        GeneralPath gp = new GeneralPath();
        double[] end = new double[3];
        double[] ctl1 = new double[3];
        double[] ctl2 = new double[3];
        boolean b = false;
        while (!it.isDone()) {
            it.next();
            int id;
            if (!it.isDone()) id = it.currentSegment(d);
            else break;
            coor[0][0] = d[0];
            coor[1][0] = -d[1];
            coor[2][0] = 0;
            coor = world3D.multiply(turtle.getRotationMatrix(), coor);

            end[0] = coor[0][0] + turtle.X;
            end[1] = coor[1][0] + turtle.Y;
            end[2] = coor[2][0] + turtle.Z;
            world3D.toScreenCoord(end);

            if (id == PathIterator.SEG_MOVETO)
                gp.moveTo((float) end[0], (float) end[1]);
            else if (id == PathIterator.SEG_LINETO) {
                if (!b) {
                    b = true;
                    gp.moveTo((float) end[0], (float) end[1]);
                } else gp.lineTo((float) end[0], (float) end[1]);
            } else if (id == PathIterator.SEG_CLOSE) {
                gp.closePath();
            } else {
                if (!b) {
                    b = true;
                    Point2D p = null;
                    if (s instanceof Arc2D.Double)
                        p = ((Arc2D.Double) s).getStartPoint();
                    else if (s instanceof GeneralPath)
                        p = ((GeneralPath) s).getCurrentPoint();
                    coor[0][0] = Objects.requireNonNull(p).getX();
                    coor[1][0] = -p.getY();
                    coor[2][0] = 0;
                    coor = world3D.multiply(turtle.getRotationMatrix(), coor);
                    ctl1[0] = coor[0][0] + turtle.X;
                    ctl1[1] = coor[1][0] + turtle.Y;
                    ctl1[2] = coor[2][0] + turtle.Z;
                    world3D.toScreenCoord(ctl1);
                    gp.moveTo((float) ctl1[0], (float) ctl1[1]);
                }
                coor[0][0] = d[2];
                coor[1][0] = -d[3];
                coor[2][0] = 0;
                coor = world3D.multiply(turtle.getRotationMatrix(), coor);
                ctl1[0] = coor[0][0] + turtle.X;
                ctl1[1] = coor[1][0] + turtle.Y;
                ctl1[2] = coor[2][0] + turtle.Z;
                world3D.toScreenCoord(ctl1);
                if (id == PathIterator.SEG_QUADTO) {
                    QuadCurve2D qc = new QuadCurve2D.Double(gp.getCurrentPoint().getX(), gp.getCurrentPoint().getY()
                            , end[0], end[1], ctl1[0], ctl1[1]);
                    gp.append(qc, true);
                } else if (id == PathIterator.SEG_CUBICTO) {
                    coor[0][0] = d[4];
                    coor[1][0] = -d[5];
                    coor[2][0] = 0;
                    coor = world3D.multiply(turtle.getRotationMatrix(), coor);

                    ctl2[0] = coor[0][0] + turtle.X;
                    ctl2[1] = coor[1][0] + turtle.Y;
                    ctl2[2] = coor[2][0] + turtle.Z;

                    world3D.toScreenCoord(ctl2);
                    CubicCurve2D qc = new CubicCurve2D.Double(gp.getCurrentPoint().getX(), gp.getCurrentPoint().getY()
                            , end[0], end[1], ctl1[0], ctl1[1], ctl2[0], ctl2[1]);
                    gp.append(qc, true);
                }
            }
        }
        return gp;
    }

    public World3D getWorld3D() {
        return world3D;
    }

    /**
     * primitive setscreencolor
     *
     * @param color The Color of the background screen
     */
    protected void setScreenColor(Color color) {
        screenColor = color;
        updateColorSelection();
        if (enabled3D()) {
            if (app.getViewer3D() != null) {
                app.getViewer3D().updateBackground(screenColor);
            }
        }
        wash();
    }

    /**
     * Primitive setpencolor
     *
     * @param color The pen Color
     */
    protected void setPenColor(Color color) {
        if (turtle.isVisible() && null == turtle.image) eraseTurtle(false);
        turtle.penColor = color;
        turtle.imageColorMode = color;
        if (turtle.isVisible() && null == turtle.image) eraseTurtle(true);
    }

    /**
     * Primitive "guiposition"
     *
     * @param id    The id for the gui Object
     * @param liste The Coordinates list
     * @param name  The translated name for the primitive "guiposition"
     * @throws LogoException If coordinates list is invalid
     */
    protected void guiposition(String id, String liste, String name) throws LogoException {
        if (guiExist(id)) {
            initCoords();
            extractCoords(liste, name);
            coords = toScreenCoord(coords, false);
            guiMap.get(id).setLocation((int) coords[0], (int) coords[1]);
        }
    }

    /**
     * Draw the Gui object refered with "id"
     *
     * @param id The Gui Object Id
     * @throws LogoException If this object doesn't exist
     */
    protected void guiDraw(String id) throws LogoException {
        if (guiExist(id)) {
            GuiComponent gc = guiMap.get(id);
            add(gc.getGuiObject());
            validate();
            repaint();
            //	updateGuiComponents();
        }
    }

    /**
     * This method erases a Gui on the drawing area
     *
     * @param id The Gui Object id
     * @throws LogoException if gui id doesn't exist
     */

    protected void guiRemove(String id) throws LogoException {
        if (guiExist(id)) {
            gui = guiMap.get(id).getGuiObject();
            guiMap.remove(id);
            if (SwingUtilities.isEventDispatchThread()) {
                remove(gui);
                validate();
            } else {
                try {
                    SwingUtilities.invokeAndWait(() -> {
                        remove(gui);
                        validate();
                    });
                } catch (Exception ignored) {
                }
            }
            repaint();
        }
    }

    private StringBuffer extractList(String list) throws LogoException {
        StringBuffer sb = new StringBuffer();
        int compteur = 0;
        int i = 0;
        while (list.length() != 0) {
            char c = list.charAt(i);
            if (c == '[') compteur++;
            else if (c == ']') {
                if (compteur == 0) return sb;
                else compteur--;
            }
            sb.append(c);
            i++;
        }
        throw new LogoException(app, "[ " + list + " " + Logo.getString("pas_liste"));
    }

    protected void guiAction(String id, String liste) throws LogoException {
        if (guiExist(id)) {
            GuiComponent gc = guiMap.get(id);
            // If gc is a JButton
            if (gc.isButton()) {
                ((GuiButton) gc).setAction(Utils.formatCode(liste));
                if (!gc.hasAction()) {
                    ((javax.swing.JButton) gc.getGuiObject()).addActionListener(gc);
                    gc.hasAction = true;
                }
            }
            // gc is a JcomboBox
            else if (gc.isMenu()) {
                liste = liste.trim();
                int i = 0;
                while (liste.length() != 0) {
                    if (liste.charAt(0) == '[') {
                        liste = liste.substring(1).trim();
                        StringBuffer sb = extractList(liste);
                        liste = liste.substring(sb.length() + 1).trim();
                        ((GuiMenu) gc).setAction(sb, i);
                        i++;
                    } else throw new LogoException(app, liste.charAt(0) + " " + Logo.getString("pas_liste"));
                }
                GuiMenu gm = (GuiMenu) gc;
                if (!gm.hasAction) {
                    gm.hasAction = true;
                    ((JComboBox<?>) gc.getGuiObject()).addActionListener(gm);
                }
            }
        }
    }

    private boolean guiExist(String id) throws LogoException {
        if (guiMap.containsKey(id.toLowerCase())) return true;
        else throw new LogoException(app, Logo.getString("no_gui") + " " + id);
    }

    //	boolean access=false;
    private void clip() {
        if (classicMode) {
            repaint();
        }
    }

    public void setQuality(int id) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        if (id == Config.DRAW_QUALITY_HIGH) {
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        } else if (id == Config.DRAW_QUALITY_LOW) {
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        } else { //normal
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_DEFAULT);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT);
        }
    }

    public void changeTurtleImage(int i) {
        if (turtle.isVisible())
            eraseTurtle(false);
        turtle.setImage(i);
        if (turtle.isVisible())
            eraseTurtle(true);

    }

    // animation
    protected void setAnimation(boolean predic) {
        if (predic == classicMode) {
            if (predic) {
                classicMode = MODE_ANIMATION;
                app.getHistoryPanel().validate();
            } else {
                classicMode = MODE_CLASSIC;
                app.getHistoryPanel().validate();
                repaint();
            }
        }
    }

    protected Font getGraphicsFont() {
        return g.getFont();
    }

    protected void setGraphicsFont(Font f) {
        g.setFont(f);
    }

    protected void setStroke(Stroke st) {
        g.setStroke(st);
    }

    public Color getScreenColor() {
        return screenColor;
    }

    protected void resetScreenColor() {
        screenColor = Color.WHITE;
    }

    protected void updateColorSelection() {
        float r = (float) (255 - screenColor.getRed()) / 255;
        float v = (float) (255 - screenColor.getGreen()) / 255;
        float b = (float) (255 - screenColor.getBlue()) / 255;
        selectionColor = new Color(r, v, b, 0.2f);
    }

    public void setNumberOfTurtles(int id) {
        Logo.config.setMaxTurtles(id);
        Turtle[] tampon = turtles.clone();
        turtles = new Turtle[id];
        int borne_sup = Math.min(tampon.length, turtles.length);
        System.arraycopy(tampon, 0, turtles, 0, borne_sup);
        for (int i = visibleTurtles.size() - 1; i > -1; i--) {
            int integer = Integer.parseInt(visibleTurtles.get(i));
            if (integer >= id) {
                visibleTurtles.remove(i);
            }
        }
    }

    public void initGraphics() {
        AffineTransform t = ((Graphics2D) getGraphics()).getTransform();
        scaleX = t.getScaleX() * 2;
        scaleY = t.getScaleY() * 2;
        image = new BufferedImage((int)(scaleX * Logo.config.getImageWidth()), (int)(scaleY * Logo.config.getImageHeight()), BufferedImage.TYPE_INT_RGB);
        drawingFont = Application.fontId;
        //		 init all turtles
        turtles = new Turtle[Logo.config.getMaxTurtles()];
        visibleTurtles = new Stack<>();
        turtle = new Turtle(app);
        turtles[0] = turtle;
        turtle.id = 0;
        visibleTurtles.push("0");
        for (int i = 1; i < turtles.length; i++) {
            // All other turtles are null
            turtles[i] = null;
        }
        g = (Graphics2D) image.getGraphics();
        g.scale(scaleX, scaleY);
        screenColor = Logo.config.getScreenColor();
        setQuality(Logo.config.getDrawQuality());
        g.setColor(Logo.config.getScreenColor());
        g.fillRect(0, 0, Logo.config.getImageWidth(), Logo.config.getImageHeight());
        g.setColor(Logo.config.getScreenColor());
        if (!enabled3D()) {
            drawGrid();
            drawXAxis();
            drawYAxis();
        }
        MediaTracker tracker;
        if (0 == Logo.config.getActiveTurtle()) {
            g.setXORMode(screenColor);
            turtle.drawTriangle();
            g.setColor(turtle.penColor);
            g.draw(turtle.triangle);
        } else {
            g.setXORMode(screenColor);
            tracker = new MediaTracker(app);
            tracker.addImage(turtle.image, 0);
            try {
                tracker.waitForID(0);
            } catch (InterruptedException ignored) {
            }
            if (tracker.checkID(0)) g.drawImage(
                    turtle.image,
                    Logo.config.getImageWidth() / 2 - turtle.width / 2,
                    Logo.config.getImageHeight() / 2 - turtle.height / 2,
                    turtle.width,
                    turtle.height,
                    this);
        }
        updateColorSelection();
    }

    private void resizeAllGuiComponents(double d) {
        // Resize all GuiComponent
        Set<String> set = guiMap.keySet();
        for (String element : set) {
            GuiComponent gui = guiMap.get(element);
            gui.getGuiObject().setSize((int) (gui.getOriginalWidth() * d),
                    (int) (gui.getOriginalHeight() * d));
            Font f = gui.getGuiObject().getFont();
            gui.getGuiObject().setFont(f.deriveFont((float) (Logo.config.getFont().getSize() * d)));
            double x = gui.getLocation().x / zoom;
            double y = gui.getLocation().y / zoom;
            gui.setLocation((int) (x * d), (int) (y * d));

        }

    }


    /**
     * Make a zoom on the drawing area
     *
     * @param d The absolute factor
     */
    public void zoom(double d, boolean zoomIn) {
        // Disable zoom buttons
        app.setZoomEnabled(false);

        javax.swing.JViewport jv = app.scrollPane.getViewport();
        Point p = jv.getViewPosition();
        Rectangle r = jv.getVisibleRect();


        // If a selection rectangle is displaying on the drawing area
        // And If zoomout has been pressed
        // Zooming on the rectangular selection
        if (null != selection && app.isCommandEditable() && zoomIn) {
            int originalWidth = jv.getWidth();
            double width = selection.getWidth();
            d = zoom * originalWidth / width;
            p = selection.getLocation();
            r.width = selection.width;
            // adjust height in the same ratio as width
            r.height = r.height * (int) width / originalWidth;
            // erase selection
            selection = null;
        }
        // Resize all Gui Components on the drawing area
        resizeAllGuiComponents(d);

        double oldZoom = zoom;
        zoom = d;

        /*
         * 		-------------------------------------
         * 		|									|
         *      |  	-------------------------		|
         * 		|	|						|		|
         * 		|	|						|		|
         * 		|	|			x--	dx-----	|  --> CenterView Point of the rectangle
         * 		|	|			|			|		|
         * 		|	|			dy			|		|
         * 		|	-------------------------		|
         * 		-------------------------------------
         * */

        double dx = Math.min(r.width, Logo.config.getImageWidth() * oldZoom) / 2;
        double dy = Math.min(r.height, Logo.config.getImageHeight() * oldZoom) / 2;
        Point centerView = new Point((int) (p.x + dx), (int) (p.y + dy));

        // Dynamically modify the drawing Area size
        setPreferredSize(new java.awt.Dimension(
                (int) (Logo.config.getImageWidth() * zoom)
                , (int) (Logo.config.getImageHeight() * zoom)));

        SwingUtilities.invokeLater(new PositionJViewport(jv,
                new Point((int) (centerView.x / oldZoom * zoom - dx),
                        (int) (centerView.y / oldZoom * zoom - dy))));

    }

    private Color getTransparencyColor(int color, int trans) {
        Color c = new Color(color);
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), trans);
    }

    /**
     * Draw the horizontal axis
     */
    private void drawXAxis() {
        if (Logo.config.isXAxisEnabled()) {
            g.setColor(getTransparencyColor(Logo.config.getAxisColor(), 128));
            g.drawLine(0, Logo.config.getImageHeight() / 2, Logo.config.getImageWidth(), Logo.config.getImageHeight() / 2);
            for (int i = Logo.config.getImageWidth() / 2 % Logo.config.getXAxisSpacing(); i < Logo.config.getImageWidth(); i = i + Logo.config.getXAxisSpacing()) {
                g.drawLine(i, Logo.config.getImageHeight() / 2 - 2, i, Logo.config.getImageHeight() / 2 + 2);
                g.setFont(new Font("Dialog", Font.PLAIN, 10));
                String tick = String.valueOf(i - Logo.config.getImageWidth() / 2);
                FontMetrics fm = g.getFontMetrics();
                int back = fm.stringWidth(tick) / 2;
                // if the both axes are drawn, the zero has to translated
                // So we don't draw the zero
                if (i != Logo.config.getImageWidth() / 2 || !Logo.config.isYAxisEnabled())
                    g.drawString(tick, i - back, Logo.config.getImageHeight() / 2 + 20);
            }
        }
    }

    /**
     * Draw the vertical axis
     */
    private void drawYAxis() {
        if (Logo.config.isYAxisEnabled()) {
            g.setColor(getTransparencyColor(Logo.config.getAxisColor(), 128));
            g.drawLine(Logo.config.getImageWidth() / 2, 0, Logo.config.getImageWidth() / 2, Logo.config.getImageHeight());
            for (int i = Logo.config.getImageHeight() / 2 % Logo.config.getYAxisSpacing(); i < Logo.config.getImageHeight(); i = i + Logo.config.getYAxisSpacing()) {
                g.drawLine(Logo.config.getImageWidth() / 2 - 2, i, Logo.config.getImageWidth() / 2 + 2, i);
                g.setFont(new Font("Dialog", Font.PLAIN, 10));
                String tick = String.valueOf(Logo.config.getImageHeight() / 2 - i);
                // If both axes are drawn, zero is translated
                if (i == Logo.config.getImageHeight() / 2 && Logo.config.isXAxisEnabled())
                    g.drawString("0", Logo.config.getImageWidth() / 2 + 10, i - 5);
                else g.drawString(tick, Logo.config.getImageWidth() / 2 + 10, i + 5);
            }
        }
    }

    private void drawGrid() {
        if (Logo.config.isGridEnabled()) {
            g.setStroke(new BasicStroke(1));
            g.setColor(getTransparencyColor(Logo.config.getGridColor(), 100));
            for (int i = Logo.config.getImageWidth() / 2 % Logo.config.getXGridSpacing(); i < Logo.config.getImageWidth(); i = i + Logo.config.getXGridSpacing())
                g.drawLine(i, 0, i, Logo.config.getImageHeight());

            for (int i = Logo.config.getImageHeight() / 2 % Logo.config.getYGridSpacing(); i < Logo.config.getImageHeight(); i = i + Logo.config.getYGridSpacing())
                g.drawLine(0, i, Logo.config.getImageWidth(), i);
        }
    }

    // In animation mode, we have to wait for the drawing to be finished before modifying graphics.
    // Thread must be synchronized.
    protected synchronized void refresh() {
        repaint();
        try {
            wait();
        } catch (InterruptedException ignored) {
        }

    }

    protected synchronized void paintComponent(Graphics graph) {
        super.paintComponent(graph);
        Graphics2D g2d = (Graphics2D) graph;
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(image, 0, 0, (int) (Logo.config.getImageWidth() * zoom), (int) (Logo.config.getImageHeight() * zoom), this);
        if (!Animation.executionLaunched && null != selection && app.isCommandEditable()) {
            g2d.setColor(selectionColor);
            g2d.fillRect(selection.x, selection.y, selection.width, selection.height);
        }
        notify();
    }

    public void activateScrollListener() {
        readMouse = false;
    }

    public boolean get_lissouris() {
        return readMouse;
    }

    public int get_bouton_souris() {
        readMouse = false;
        return mouseButton;
    }

    public String get_possouris() {
        readMouse = false;
        return mousePosition;
    }

    public void mousePressed(MouseEvent e) {
        if (!Animation.executionLaunched) {
            selection = new Rectangle();
            dragOrigin = new Point(e.getPoint());
            selection.setSize(0, 0);
        }
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseClicked(MouseEvent ev) {
        if (!Animation.executionLaunched) {
            selection = null;
            dragOrigin = null;
            repaint();
        } else {
            readMouse = true;
            mouseButton = ev.getButton();
            Point point = ev.getPoint();
            mousePosition = "[ " + (point.x - Logo.config.getImageWidth() / 2) + " " + (Logo.config.getImageHeight() / 2 - point.y) + " ] ";
        }
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    // Select an export area
    public void mouseDragged(MouseEvent e) {
        if (!Animation.executionLaunched && null != selection) {
            // First, we test if we need to move the scrollbars
            Point pos = e.getPoint();
            javax.swing.JViewport jv = app.scrollPane.getViewport();
            Point viewPosition = jv.getViewPosition();
            Rectangle r = jv.getVisibleRect();
            r.setLocation(viewPosition);
            // Is the point visible on screen?
            boolean b = r.contains(pos);
            // Move the scroolPane if necessary
            if (!b) {
                int x, y;
                if (pos.x < viewPosition.x) x = Math.max(0, pos.x);
                else if (pos.x > viewPosition.x + r.width)
                    x = Math.min(pos.x - r.width, (int) (Logo.config.getImageWidth() * zoom - r.width));
                else x = viewPosition.x;
                if (pos.y < viewPosition.y) y = Math.max(0, pos.y);
                else if (pos.y > viewPosition.y + r.height)
                    y = Math.min(pos.y - r.height, (int) (Logo.config.getImageHeight() * zoom - r.height));
                else y = viewPosition.y;
                jv.setViewPosition(new Point(x, y));
            }

            // Then , drawing the selection area

            selection.setFrameFromDiagonal(dragOrigin, e.getPoint());
            repaint();
        }
    }

    public void mouseMoved(MouseEvent ev) {
        readMouse = true;
        mouseButton = 0;
        Point point = ev.getPoint();
        mousePosition = "[ " + (point.x - Logo.config.getImageWidth() / 2) + " " + (Logo.config.getImageHeight() / 2 - point.y) + " ] ";
    }

    protected void addToGuiMap(GuiComponent gc) throws LogoException {
        guiMap.put(gc);
    }

    // This method modifies all Shape for any turtle on screen
    protected void updateAllTurtleShape() {
        for (Turtle value : turtles) {
            if (null != value) {
                value.fixPenWidth(2 * value.getPenWidth());
            }
        }
    }

    /**
     * Saves the a part of the drawing area as an image
     *
     * @param name   The image name
     * @param coords The upper left corner and the right bottom corner
     */
    protected void saveImage(String name, int[] coords) {
        BufferedImage buffer = getImagePart(coords);
        String lowerName = name.toLowerCase();
        String format = "png";
        if (lowerName.endsWith(".jpg") || lowerName.endsWith(".jpeg")) {
            format = "jpg";
        } else if (!lowerName.endsWith(".png")) {
            name = name + ".png";
        }
        name = Logo.config.getDefaultFolder() + File.separator + name;
        try {
            File f = new File(name);
            ImageIO.write(buffer, format, f);
        } catch (IOException ignored) {
        }

    }

    /**
     * Return a part of the drawing area as an image
     *
     * @return the part of the image
     */
    private BufferedImage getImagePart(int[] coords) {
        Image pic = image;
        if (zoom != 1) {
            pic = createImage(new FilteredImageSource(pic.getSource(),
                    new ReplicateScaleFilter((int) (image.getWidth() * zoom), (int) (image.getHeight() * zoom))));
        }
        pic = createImage(new FilteredImageSource(pic.getSource(),
                new CropImageFilter(coords[0], coords[1], coords[2], coords[3])));
        return toBufferedImage(pic);
    }


    public BufferedImage getSelectionImage() {
        Image pic = image;
        if (zoom != 1) {
            pic = createImage(new FilteredImageSource(pic.getSource(),
                    new ReplicateScaleFilter((int) (image.getWidth() * zoom), (int) (image.getHeight() * zoom))));
        }
        if (null != selection) {
            int x = (int) (selection.getBounds().getX());
            int y = (int) (selection.getBounds().getY());
            int width = (int) (selection.getBounds().getWidth());
            int height = (int) (selection.getBounds().getHeight());
            pic = createImage(new FilteredImageSource(pic.getSource(),
                    new CropImageFilter(x, y, width, height)));
        }
        return toBufferedImage(pic);
    }

    //	 This method returns a buffered image with the contents of an image
    private BufferedImage toBufferedImage(Image image) {
        if (image instanceof BufferedImage)
            return (BufferedImage) image;

        // This code ensures that all the pixels in the image are loaded
        image = new ImageIcon(image).getImage();


        // Create a buffered image with a format that's compatible with the screen
        BufferedImage bimage = null;
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        try {
            // Determine the type of transparency of the new buffered image
            int transparency = Transparency.OPAQUE;

            // Create the buffered image
            GraphicsDevice gs = ge.getDefaultScreenDevice();
            GraphicsConfiguration gc = gs.getDefaultConfiguration();
            bimage = gc.createCompatibleImage(
                    image.getWidth(null), image.getHeight(null), transparency);
        } catch (HeadlessException e) {
            // The system does not have a screen
        }

        if (bimage == null) {
            // Create a buffered image using the default color model
            int type = BufferedImage.TYPE_INT_RGB;
            bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
        }

        // Copy image to buffered image
        Graphics g = bimage.createGraphics();

        // Paint the image onto the buffered image
        g.drawImage(image, 0, 0, null);
        g.dispose();

        return bimage;
    }

    private void tryRecord2DMode(double a, double b) {
        if (DrawPanel.record2D == DrawPanel.RECORD_2D_POLYGON) {
            // FillPolygon mode
            if (stackTriangle.size() == 3) {
                stackTriangle.remove(0);
                stackTriangle.add(new Point2D.Double(a, b));
            } else {
                stackTriangle.add(new Point2D.Double(a, b));
            }
            if (stackTriangle.size() == 3) {
                GeneralPath gp = new GeneralPath();
                Line2D.Double ld = new Line2D.Double(stackTriangle.get(0), stackTriangle.get(1));
                gp.append(ld, false);
                ld = new Line2D.Double(stackTriangle.get(1), stackTriangle.get(2));
                gp.append(ld, true);
                ld = new Line2D.Double(stackTriangle.get(2), stackTriangle.get(0));
                gp.append(ld, true);
                g.fill(gp);
            }
        }

    }

    protected void startRecord2DPolygon() {
        DrawPanel.record2D = DrawPanel.RECORD_2D_POLYGON;
        stackTriangle = new Vector<>();
        stackTriangle.add(new Point2D.Double(turtle.curX, turtle.curY));
    }

    protected void stopRecord2DPolygon() {
        DrawPanel.record2D = DrawPanel.RECORD_2D_NONE;
    }

    public double getScaleX() { return scaleX; }
    public double getScaleY() { return scaleY; }

    /**
     * Resize the dawing area
     * @param application
     */
    public void resizeDrawingZone(Application application) {
        if (null != application.animation) {
            application.animation.setPause(true);
        }
        // resize the drawing image
        SwingUtilities.invokeLater(() -> {

            MediaTracker tracker = new MediaTracker(this);
            try {
                tracker.waitForID(0);
            } catch (InterruptedException ignored) {
            }

            setPreferredSize(new Dimension(Logo.config.getImageWidth(), Logo.config.getImageHeight()));
            revalidate();
            initGraphics();
            //drawPanel.repaint();

            if (null != application.animation) application.animation.setPause(false);
        });

    }

    class PositionJViewport implements Runnable {
        JViewport jv;
        Point p;

        PositionJViewport(JViewport jv, Point p) {
            this.jv = jv;
            this.p = p;
        }

        public void run() {
            revalidate();
            //  I have to add those two lines because of a bug I don't understand
            // zoom 8 zoom 1 zoom 8
            // Sometimes after the method revalidate(), the left upper corner position
            // wasn't correct
            app.scrollPane.invalidate();
            app.scrollPane.validate();
            // End Bug

            jv.setViewPosition(p);
            repaint();

            app.setZoomEnabled(true);
        }
    }
}