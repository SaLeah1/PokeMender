package junior_is;

import org.apache.commons.math3.linear.*;
import org.apache.commons.math3.stat.correlation.Covariance;

public class Comparator {
    public double[] cosSim(double[] Vect1, double[] Vect2){
        // cos(theta) = dotProduct(vect1,vect2)/(len(vect1)*len*(vect2))
        RealVector Matrx1 = MatrixUtils.createRealVector(Vect1);
        RealVector Matrx2 = MatrixUtils.createRealVector(Vect2);
        System.out.println(Matrx1.cosine(Matrx2));
        return new double[]{0.0};
    }

    public static void main(String[] args) {
        Comparator c = new Comparator();
        c.cosSim(new double[]{0.0,0.1,0.2,0.3},new double[]{-0.0,-0.1,-0.2,-0.31});
    }
}
