/**
 * Title :        XLogo
 * Description :  XLogo is an interpreter for the Logo
 * programming language
 *
 * @author Loïc Le Coq
 */

package xlogo.kernel;

import xlogo.Application;
import xlogo.Config;
import xlogo.Logo;
import xlogo.utils.Utils;
import xlogo.utils.myException;

import java.awt.*;
import java.awt.geom.GeneralPath;
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
    public Color couleurcrayon = Color.black;
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
    public double corX;
    /**
     * The Y coordinates on the screen
     */
    public double corY;
    public double angle;
    /**
     * The X coordinates in real World (3D or 2D)
     */
    public double X = 0;
    /**
     * The Y coordinates in real World (3D or 2D)
     */
    public double Y = 0;
    /**
     * The Z coordinates in real World (3D or 2D)
     */
    public double Z = 0;
    Color couleurmodedessin = Color.black;
    BasicStroke crayon = null;
    int police = 12;
    // Image for the turtle
    // If null then draw the triangle
    Image tort = null;
    GeneralPath triangle;
    int largeur = 0, hauteur = 0, gabarit = 0;
    private final Application app;
    private int labelHorizontalAlignment = 0;
    private int labelVerticalAlignment = 0;
    /**
     * This is the rotation Matrix (3x3) in 3D world
     */
    private double[][] rotationMatrix = identity;
    private boolean pendown = true;
    private boolean penReverse = false;
    private boolean visible = true;
    private int shape = Config.activeTurtle;
    private float penWidth = 0; // half of the pen width

    {
        identity[0][0] = identity[1][1] = identity[2][2] = 1;
        identity[0][1] = identity[0][2] = identity[1][2] = 0;
        identity[1][0] = identity[2][1] = identity[2][0] = 0;
    }

    public Turtle(Application app) {
        this.app = app;
        fixe_taille_crayon(1);
        String chemin = "tortue" + Config.activeTurtle + ".png";
        couleurcrayon = Config.pencolor;
        couleurmodedessin = Config.pencolor;
        if (Config.activeTurtle == 0) {
            tort = null;
            largeur = 26;
            hauteur = 26;
        } else {
            //ON teste tout d'abord si le chemin est valide
            if (null == Utils.class.getResource(chemin)) chemin = "tortue1.png";
            tort = Toolkit.getDefaultToolkit().getImage(Utils.class.getResource(chemin));
            MediaTracker tracker = new MediaTracker(app);
            tracker.addImage(tort, 0);
            try {
                tracker.waitForID(0);
            } catch (InterruptedException e1) {
            }
            largeur = tort.getWidth(app);
            hauteur = tort.getHeight(app);
            double largeur_ecran = Toolkit.getDefaultToolkit().getScreenSize().getWidth();
            // On fait attention à la résolution de l'utilisateur
            double facteur = largeur_ecran / 1024.0;
            if ((int) (facteur + 0.001) != 1) {
                //tort = tort.getScaledInstance((int) (facteur * largeur), (int) (facteur * hauteur), Image.SCALE_SMOOTH);
                tracker = new MediaTracker(app);
                tracker.addImage(tort, 0);
                try {
                    tracker.waitForID(0);
                } catch (InterruptedException e1) {
                }
            }
            largeur = tort.getWidth(app);
            hauteur = tort.getHeight(app);
        }
        gabarit = Math.max(hauteur, largeur);
        corX = Config.imageWidth / 2;
        corY = Config.imageHeight / 2;
        angle = Math.PI / 2;
        heading = 0.0;
        pitch = 0;
        roll = 0;
        X = 0;
        Y = 0;
        Z = 0;
    }

    protected void init() {
        corX = Config.imageWidth / 2;
        corY = Config.imageHeight / 2;
        X = 0;
        Y = 0;
        Z = 0;
        heading = 0;
        pitch = 0;
        roll = 0;
        rotationMatrix = identity;
        angle = Math.PI / 2;
        pendown = true;
        fixe_taille_crayon(1);
        couleurcrayon = Config.pencolor;
        couleurmodedessin = Config.pencolor;
        penReverse = false;
    }

    void drawTriangle() {
        if (null == tort) {
            if (null == triangle) {
                triangle = new GeneralPath();
            } else triangle.reset();
            if (DrawPanel.WINDOW_MODE != DrawPanel.WINDOW_3D) {
                triangle.moveTo((float) (corX - 10.0 * Math.sin(angle)),
                        (float) (corY - 10.0 * Math.cos(angle)));
                triangle.lineTo((float) (corX + 24.0 * Math.cos(angle)),
                        (float) (corY - 24.0 * Math.sin(angle)));
                triangle.lineTo((float) (corX + 10.0 * Math.sin(angle)),
                        (float) (corY + 10.0 * Math.cos(angle)));
                triangle.lineTo((float) (corX - 10.0 * Math.sin(angle)),
                        (float) (corY - 10.0 * Math.cos(angle)));
            } else {
                double[] screenCoord = new double[2];
                // The triangle has coordinates: (-10,0,0);(0,24,0);(10,0,0)
                double[] x1 = new double[3];
                x1[0] = X - 20 * rotationMatrix[0][0];
                x1[1] = Y - 20 * rotationMatrix[1][0];
                x1[2] = Z - 20 * rotationMatrix[2][0];
                screenCoord = app.getArdoise().toScreenCoord(x1, false);
                triangle.moveTo((float) screenCoord[0], (float) screenCoord[1]);
                x1[0] = X + 48 * rotationMatrix[0][1];
                x1[1] = Y + 48 * rotationMatrix[1][1];
                x1[2] = Z + 48 * rotationMatrix[2][1];
                screenCoord = app.getArdoise().toScreenCoord(x1, false);
                triangle.lineTo((float) screenCoord[0], (float) screenCoord[1]);
                x1[0] = X + 20 * rotationMatrix[0][0];
                x1[1] = Y + 20 * rotationMatrix[1][0];
                x1[2] = Z + 20 * rotationMatrix[2][0];
                screenCoord = app.getArdoise().toScreenCoord(x1, false);
                triangle.lineTo((float) screenCoord[0], (float) screenCoord[1]);
                triangle.closePath();


                // the "aileron" has coordinates: (0,10,0);(0,0,10);(0,0,0)
                x1[0] = X + 15 * rotationMatrix[0][1];
                x1[1] = Y + 15 * rotationMatrix[1][1];
                x1[2] = Z + 15 * rotationMatrix[2][1];
                screenCoord = app.getArdoise().toScreenCoord(x1, false);
                triangle.moveTo((float) screenCoord[0], (float) screenCoord[1]);
                x1[0] = X + 15 * rotationMatrix[0][2];
                x1[1] = Y + 15 * rotationMatrix[1][2];
                x1[2] = Z + 15 * rotationMatrix[2][2];
                screenCoord = app.getArdoise().toScreenCoord(x1, false);
                triangle.lineTo((float) screenCoord[0], (float) screenCoord[1]);
                x1[0] = X;
                x1[1] = Y;
                x1[2] = Z;
                screenCoord = app.getArdoise().toScreenCoord(x1, false);
                triangle.lineTo((float) screenCoord[0], (float) screenCoord[1]);
            }
        }
    }


    public void fixe_taille_crayon(float nb) {
        if (nb < 0) nb = 1;
        else if (Config.maxPenWidth != -1 && nb > Config.maxPenWidth) nb = 1;
        if (Config.penShape == 0) crayon = new BasicStroke(nb, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER);
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
        return pendown;
    }

    protected void setPenDown(boolean b) {
        pendown = b;
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
        if (DrawPanel.WINDOW_MODE == DrawPanel.WINDOW_3D) return X;
        return corX - Config.imageWidth / 2;

    }

    protected double getY() {
        if (DrawPanel.WINDOW_MODE == DrawPanel.WINDOW_3D) return Y;
        return Config.imageHeight / 2 - corY;

    }

    protected int getLabelHorizontalAlignment() {
        return labelHorizontalAlignment;
    }

    protected int getLabelVerticalAlignment() {
        return labelVerticalAlignment;
    }

    public String getFontJustify() {
        StringBuffer sb = new StringBuffer("[ ");
        sb.append(labelHorizontalAlignment);
        sb.append(" ");
        sb.append(labelVerticalAlignment);
        sb.append(" ] ");
        return new String(sb);
    }

    protected void setFontJustify(String list) throws myException {
        StringTokenizer st = new StringTokenizer(list);
        int i = 0;
        while (st.hasMoreTokens()) {
            String s = st.nextToken();
            try {
                int j = Integer.parseInt(s);
                if (j < 0 || j > 2) throw new myException(app, list + " " + Logo.messages.getString("pas_argument"));
                else {
                    if (i == 0) labelHorizontalAlignment = j;
                    else if (i == 1) labelVerticalAlignment = j;
                }
            } catch (NumberFormatException e) {
                throw new myException(app, list + " " + Logo.messages.getString("pas_argument"));
            }

            i++;
        }
        if (i != 2) throw new myException(app, list + " " + Logo.messages.getString("pas_argument"));
    }
}
