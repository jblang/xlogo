/**
 * Title :        XLogo
 * Description :  XLogo is an interpreter for the Logo
 * programming language
 *
 * @author Loïc Le Coq
 */

package xlogo.kernel;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import xlogo.Logo;
import xlogo.gui.Application;
import xlogo.utils.LogoException;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.util.Objects;
import java.util.StringTokenizer;

public class Turtle {
    protected static final int LABEL_HORIZONTAL_ALIGNMENT_LEFT = 0;
    protected static final int LABEL_HORIZONTAL_ALIGNMENT_CENTER = 1;
    protected static final int LABEL_HORIZONTAL_ALIGNMENT_RIGHT = 2;
    protected static final int LABEL_VERTICAL_ALIGNMENT_BOTTOM = 0;
    protected static final int LABEL_VERTICAL_ALIGNMENT_CENTER = 1;
    protected static final int LABEL_VERTICAL_ALIGNMENT_TOP = 2;
    /**
     * Identity Matrix
     */
    private final double[][] identity = new double[3][3];
    public Color penColor;
    public int id = -1;
    /**
     * The turtle heading (degree)
     */
    public double heading;
    /**
     * The turtle roll (degree)
     */
    public double roll;
    /**
     * The turtle pitch (degree)
     */
    public double pitch;
    /**
     * The X coordinates on the screen
     */
    public double curX;
    /**
     * The Y coordinates on the screen
     */
    public double curY;
    public double angle;
    /**
     * The X coordinates in real World (3D or 2D)
     */
    public double X;
    /**
     * The Y coordinates in real World (3D or 2D)
     */
    public double Y;
    /**
     * The Z coordinates in real World (3D or 2D)
     */
    public double Z;
    Color imageColorMode;
    BasicStroke crayon = null;
    int police = 12;
    // Image for the turtle
    // If null then draw the triangle
    Image image = null;
    GeneralPath triangle;
    int width = 0, height = 0, template = 0;
    private final Application app;
    private int labelHorizontalAlignment = 0;
    private int labelVerticalAlignment = 0;
    /**
     * This is the rotation Matrix (3x3) in 3D world
     */
    private double[][] rotationMatrix = identity;
    private boolean penDown = true;
    private boolean penReverse = false;
    private boolean visible = true;
    private int shape = Logo.config.getActiveTurtle();
    private float penWidth = 0; // half of the pen width

    {
        identity[0][0] = identity[1][1] = identity[2][2] = 1;
        identity[0][1] = identity[0][2] = identity[1][2] = 0;
        identity[1][0] = identity[2][1] = identity[2][0] = 0;
    }

    public Turtle(Application app) {
        this.app = app;
        fixPenWidth(1);
        penColor = Logo.config.getPenColor();
        imageColorMode = Logo.config.getPenColor();
        if (Logo.config.getActiveTurtle() == 0) {
            image = null;
            width = 26;
            height = 26;
        } else {
            // first tests if the path is valid
            setImage(Logo.config.getActiveTurtle());
        }
        curX = Logo.config.getImageWidth() / 2.0;
        curY = Logo.config.getImageHeight() / 2.0;
        angle = Math.PI / 2;
        heading = 0.0;
        pitch = 0;
        roll = 0;
        X = 0;
        Y = 0;
        Z = 0;
    }

    protected void init() {
        curX = Logo.config.getImageWidth() / 2.0;
        curY = Logo.config.getImageHeight() / 2.0;
        X = 0;
        Y = 0;
        Z = 0;
        heading = 0;
        pitch = 0;
        roll = 0;
        rotationMatrix = identity;
        angle = Math.PI / 2;
        penDown = true;
        fixPenWidth(1);
        penColor = Logo.config.getPenColor();
        imageColorMode = Logo.config.getPenColor();
        penReverse = false;
    }

    void drawTriangle() {
        if (null == image) {
            if (null == triangle) {
                triangle = new GeneralPath();
            } else triangle.reset();
            if (DrawPanel.windowMode != DrawPanel.WINDOW_3D) {
                triangle.moveTo((float) (curX - 10.0 * Math.sin(angle)),
                        (float) (curY - 10.0 * Math.cos(angle)));
                triangle.lineTo((float) (curX + 24.0 * Math.cos(angle)),
                        (float) (curY - 24.0 * Math.sin(angle)));
                triangle.lineTo((float) (curX + 10.0 * Math.sin(angle)),
                        (float) (curY + 10.0 * Math.cos(angle)));
                triangle.lineTo((float) (curX - 10.0 * Math.sin(angle)),
                        (float) (curY - 10.0 * Math.cos(angle)));
            } else {
                double[] screenCoord;
                // The triangle has coordinates: (-10,0,0);(0,24,0);(10,0,0)
                double[] x1 = new double[3];
                x1[0] = X - 20 * rotationMatrix[0][0];
                x1[1] = Y - 20 * rotationMatrix[1][0];
                x1[2] = Z - 20 * rotationMatrix[2][0];
                screenCoord = app.getDrawPanel().toScreenCoord(x1, false);
                triangle.moveTo((float) screenCoord[0], (float) screenCoord[1]);
                x1[0] = X + 48 * rotationMatrix[0][1];
                x1[1] = Y + 48 * rotationMatrix[1][1];
                x1[2] = Z + 48 * rotationMatrix[2][1];
                screenCoord = app.getDrawPanel().toScreenCoord(x1, false);
                triangle.lineTo((float) screenCoord[0], (float) screenCoord[1]);
                x1[0] = X + 20 * rotationMatrix[0][0];
                x1[1] = Y + 20 * rotationMatrix[1][0];
                x1[2] = Z + 20 * rotationMatrix[2][0];
                screenCoord = app.getDrawPanel().toScreenCoord(x1, false);
                triangle.lineTo((float) screenCoord[0], (float) screenCoord[1]);
                triangle.closePath();


                // the "aileron" has coordinates: (0,10,0);(0,0,10);(0,0,0)
                x1[0] = X + 15 * rotationMatrix[0][1];
                x1[1] = Y + 15 * rotationMatrix[1][1];
                x1[2] = Z + 15 * rotationMatrix[2][1];
                screenCoord = app.getDrawPanel().toScreenCoord(x1, false);
                triangle.moveTo((float) screenCoord[0], (float) screenCoord[1]);
                x1[0] = X + 15 * rotationMatrix[0][2];
                x1[1] = Y + 15 * rotationMatrix[1][2];
                x1[2] = Z + 15 * rotationMatrix[2][2];
                screenCoord = app.getDrawPanel().toScreenCoord(x1, false);
                triangle.lineTo((float) screenCoord[0], (float) screenCoord[1]);
                x1[0] = X;
                x1[1] = Y;
                x1[2] = Z;
                screenCoord = app.getDrawPanel().toScreenCoord(x1, false);
                triangle.lineTo((float) screenCoord[0], (float) screenCoord[1]);
            }
        }
    }


    public void fixPenWidth(float nb) {
        if (nb < 0) nb = 1;
        else if (Logo.config.getMaxPenWidth() != -1 && nb > Logo.config.getMaxPenWidth()) nb = 1;
        if (Logo.config.getPenShape() == 0) crayon = new BasicStroke(nb, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER);
        else crayon = new BasicStroke(nb, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
        penWidth = nb / 2;
    }

    public float getPenWidth() {
        return penWidth;
    }

    protected int getShape() {
        return shape;
    }

    public void setShape(int id) {
        shape = id;
    }

    protected boolean isVisible() {
        return visible;
    }

    protected void setVisible(boolean b) {
        visible = b;
    }

    protected boolean isPenDown() {
        return penDown;
    }

    protected void setPenDown(boolean b) {
        penDown = b;
    }

    protected boolean isPenReverse() {
        return penReverse;
    }

    protected void setPenReverse(boolean b) {
        penReverse = b;
    }

    protected double[][] getRotationMatrix() {
        return rotationMatrix;
    }

    protected void setRotationMatrix(double[][] m) {
        rotationMatrix = m;
    }

    protected double getX() {
        if (DrawPanel.windowMode == DrawPanel.WINDOW_3D) return X;
        return curX - Logo.config.getImageWidth() / 2.0;

    }

    protected double getY() {
        if (DrawPanel.windowMode == DrawPanel.WINDOW_3D) return Y;
        return Logo.config.getImageHeight() / 2.0 - curY;

    }

    protected int getLabelHorizontalAlignment() {
        return labelHorizontalAlignment;
    }

    protected int getLabelVerticalAlignment() {
        return labelVerticalAlignment;
    }

    public String getFontJustify() {
        StringBuilder sb = new StringBuilder("[ ");
        sb.append(labelHorizontalAlignment);
        sb.append(" ");
        sb.append(labelVerticalAlignment);
        sb.append(" ] ");
        return new String(sb);
    }

    protected void setFontJustify(String list) throws LogoException {
        StringTokenizer st = new StringTokenizer(list);
        int i = 0;
        while (st.hasMoreTokens()) {
            String s = st.nextToken();
            try {
                int j = Integer.parseInt(s);
                if (j < 0 || j > 2) throw new LogoException(app, list + " " + Logo.messages.getString("pas_argument"));
                else {
                    if (i == 0) labelHorizontalAlignment = j;
                    else if (i == 1) labelVerticalAlignment = j;
                }
            } catch (NumberFormatException e) {
                throw new LogoException(app, list + " " + Logo.messages.getString("pas_argument"));
            }

            i++;
        }
        if (i != 2) throw new LogoException(app, list + " " + Logo.messages.getString("pas_argument"));
    }

    void setImage(int i) {
        if (i == 0) {
            image = null;
            this.width = 26;
            this.height = 26;
        } else {
            FlatSVGIcon svg = Logo.getTurtle(i);
            if (svg == null) {
                svg = Logo.getTurtle(1);
            }
            float factor = (float) 70 / (float) Objects.requireNonNull(svg).getIconHeight();
            this.width = (int)(svg.getIconWidth() * factor);
            this.height = (int)(svg.getIconHeight() * factor);
            image = svg.getImage().getScaledInstance(
                    (int) (this.height * app.getDrawPanel().getScaleX()),
                    (int) (this.width * app.getDrawPanel().getScaleY()),
                    Image.SCALE_SMOOTH
            );
            MediaTracker tracker = new MediaTracker(app);
            tracker.addImage(image, 0);
            try {
                tracker.waitForID(0);
            } catch (InterruptedException ignored) {
            }
        }
        template = Math.max(this.height, this.width);
    }
}
