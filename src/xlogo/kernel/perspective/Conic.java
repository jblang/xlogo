package xlogo.kernel.perspective;

import xlogo.gui.Application;

import java.math.BigDecimal;

public class Conic {
    private final BigDecimal un = BigDecimal.ONE;
    private final BigDecimal zero = BigDecimal.ZERO;
    BigDecimal a, b, c, d, e, f;
    BigDecimal deux = new BigDecimal(2);
    BigDecimal[][] A = new BigDecimal[2][2];
    private final Application app;
    private final BigDecimal[] eigenValue = new BigDecimal[2];
    private final BigDecimal[][] eigenVect = new BigDecimal[2][2];
    private final BigDecimal[] center = new BigDecimal[2];
    private double halfXAxis, halfYAxis;

    public Conic(Application app, BigDecimal[] v) {
        this.app = app;
        this.a = v[0];
        this.b = v[1];
        this.c = v[2];
        this.d = v[3];
        this.e = v[4];
        this.f = v[5];
        A[0][0] = a;
        A[1][1] = c;
        // b/2
        A[0][1] = b.divide(deux);
        A[1][0] = b.divide(deux);
        drawConic();
    }

    private void drawConic() {
        calculateEigenVect();
        // ellipse
        BigDecimal det = det(A);
        if (det.compareTo(zero) == 1) {
            // Looking for center
            calculateCenter();
            // The main equation becomes with the center as Origin:
            // aX^2+bXY+cY^2+omega=0

//			double omega=a*Math.pow(center[0],2)+c*Math.pow(center[1],2)
//				+b*center[0]*center[1]+d*center[0]+e*center[1]+f;

            BigDecimal omega = a.multiply(center[0].pow(2)).add(c.multiply(center[1].pow(2))).add(center[0].multiply(center[1]).multiply(b))
                    .add(d.multiply(center[0])).add(e.multiply(center[1])).add(f);

            // We apply the rotation with the Eigen Vect Matrix
            // Now the equation becomes: lambda*X^2+mu*Y^2+omega=0
            // with lambda and mu the eigen Values
            // 1/sqrt(eigenValue[0]/-omega)
            halfXAxis = 1 / Math.sqrt(eigenValue[0].divide(omega.negate(), 20, BigDecimal.ROUND_HALF_EVEN).doubleValue());
            // 1/sqrt(eigenValue[1]/-omega)
            halfYAxis = 1 / Math.sqrt(eigenValue[1].divide(omega.negate(), 20, BigDecimal.ROUND_HALF_EVEN).doubleValue());
            double angle = Math.atan(eigenVect[1][0].divide(eigenVect[0][0], 20, BigDecimal.ROUND_HALF_EVEN).doubleValue());
            //	System.out.println(toString());
            app.getDrawPanel().drawEllipseArc(halfXAxis, halfYAxis, angle,
                    center[0].doubleValue(), center[1].doubleValue(), 0, 360);

        }
        // hyperbola
        else if (det.compareTo(zero) == -1) {
            calculateCenter();

        }
        //parabola
        else {
        }
    }

    private void calculateEigenValue() {
        // Polynom: acX^2-(a+c)X+A[0][1]^2
        // tmp=a+c
        BigDecimal tmp = A[0][0].add(A[1][1]);
        // Discriminant delta=(a-c)^2+4*A[0][1]^2
        BigDecimal delta = A[0][0].subtract(A[1][1]).pow(2).add(new BigDecimal(4).multiply(A[0][1].pow(2)));
        // calculate the eigen Values
        eigenValue[0] = tmp.subtract(sqrt(delta)).divide(deux);
        eigenValue[1] = tmp.add(sqrt(delta)).divide(deux);
    }

    private void calculateEigenVect() {
        if (A[0][1].compareTo(zero) == 0) {
            eigenValue[0] = A[0][0];
            eigenValue[1] = A[1][1];
            eigenVect[0][0] = un;
            eigenVect[1][0] = zero;
            eigenVect[0][1] = zero;
            eigenVect[1][1] = un;
        } else {
            calculateEigenValue();
            BigDecimal tmp = A[0][0].subtract(eigenValue[0]);
            eigenVect[0][0] = un;
//			System.out.println("pb: "+tmp+"\n"+A[0][1]);
            eigenVect[1][0] = tmp.negate().divide(A[0][1], 20, BigDecimal.ROUND_HALF_EVEN);
            // vector length and then normalize
            tmp = sqrt(eigenVect[0][0].pow(2).add(eigenVect[1][0].pow(2)));
            eigenVect[0][0] = eigenVect[0][0].divide(tmp, 20, BigDecimal.ROUND_HALF_EVEN);
            eigenVect[1][0] = eigenVect[1][0].divide(tmp, 20, BigDecimal.ROUND_HALF_EVEN);
            eigenVect[0][1] = eigenVect[1][0].negate();
            eigenVect[1][1] = un;

        }
    }

    private void calculateCenter() {
        // System determinant
        // df/dx=0 and df/dy=0

        //double det=4*a*c-Math.pow(b,2);
        BigDecimal det = new BigDecimal(4).multiply(a).multiply(c).subtract(b.pow(2));
        //xCenter=(-2*c*d+b*e)/det;
        center[0] = (deux.negate().multiply(c).multiply(d).add(b.multiply(e))).divide(det, 20, BigDecimal.ROUND_HALF_EVEN);
        //yCenter=(-2*a*e+b*d)/det;
        center[1] = (deux.negate().multiply(a).multiply(e).add(b.multiply(d))).divide(det, 20, BigDecimal.ROUND_HALF_EVEN);
    }

    private BigDecimal det(BigDecimal[][] M) {
        return M[0][0].multiply(M[1][1]).subtract(M[0][1].multiply(M[1][0]));
    }

    public String toString() {
        String st = "centre: " + center[0] + " " + center[1] + "\n" +
                "Valeurs propres: " + eigenValue[0] + " " + eigenValue[1] + "\n" +
                "Vecteurs propres:\n" + eigenVect[0][0] + " " + eigenVect[0][1] + "\n" + eigenVect[1][0] + " " + eigenVect[1][1] + "\n" +
                "Demi Axe: X " + halfXAxis + " Y " + halfYAxis;
        return st;
    }

    private BigDecimal sqrt(BigDecimal b) {
        BigDecimal precision = new BigDecimal(0.00000000001);
        int scale = precision.scale();
        if (b.scale() < scale) {
            b = b.setScale(scale);
        }
        // Initial guess
        // G = A/2
        BigDecimal g = new BigDecimal(Math.sqrt(b.doubleValue()));

        // Iterate until we're done
        while (true) {
            // G* = ((A/G)+G)/2
            BigDecimal gStar = b.divide(g, BigDecimal.ROUND_HALF_EVEN).add(g).divide(deux, BigDecimal.ROUND_HALF_EVEN);
            BigDecimal delta = gStar.subtract(g);
            delta = delta.abs();
            if (delta.compareTo(precision) < 0)
                break;
            g = gStar;
        }
        return g;
    }
}
	
