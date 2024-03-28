package junior_is;
import java.util.Arrays;

import org.apache.commons.math3.linear.*;
import org.apache.commons.math3.stat.correlation.Covariance;

public class PCAbot {
    public static void main(String[] args) {
        PCAbot c = new PCAbot();
        double[] top = new double[]{3.2,4.0,8.1};
        double[] bot = new double[]{2.6,0.1,2.9};
        double [][] a = new double[][]{top,bot};
        c.doThing(a);
    }

    public void doThing(double[][] pointsArray){
        // pointsArray = standardize(pointsArray); //note very slow
        RealMatrix rMat = MatrixUtils.createRealMatrix(pointsArray);
        System.out.println("Standardized Matrix: ");
        System.out.println(rMat);
        Covariance cov = new Covariance(rMat);
        RealMatrix covMat = cov.getCovarianceMatrix();
        System.out.println("Cov Matrix: ");
        System.out.println(covMat);
        EigenDecomposition ed = new EigenDecomposition(covMat);
        System.out.println("Eigenvalues as follows: ");
        for(double pr : ed.getRealEigenvalues()){
            System.out.println(pr);
        } 
    }
    public double[][] standardize(double[][] pointsArray){ // this cant be the best way to do this
        int h = pointsArray.length;
        int w = pointsArray[0].length;
        double N = h*w;
        double summation = 0;
        for(double[] row : pointsArray){
            summation += Arrays.stream(row).sum();
        }
        double avg = summation/N;
        summation = 0;
        for(double[] row : pointsArray){
            for (double item : row){
                summation += (item-avg)*(item-avg);
            }
        }
        double sigma = Math.sqrt(summation/N);
        double[][] newPointsArray = new double[h][w];
        int wP = 0;
        int hP = 0;
        for(double[] row : pointsArray){
            wP = 0;
            for (double item : row){
                double newPoint = (item-avg)/(sigma);
                newPointsArray[hP][wP] = newPoint;
                //System.out.println(newPoint);
                wP++;
            }
            hP++;
        }
        return newPointsArray;
    }
}
