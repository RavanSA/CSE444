import java.text.DecimalFormat;

/* CSE 444
 * Ravan Sadigli --> 20160807005 --> Homework1
 * Calculating Minkowski and Mahalonobis distance
 */
public class RavanSadigliHomework1 {

    public static void main ( String[] args ) {

        double[] a = {64, 580, 29}; // first point
        double[] b = {68, 590, 37}; // second point

        DecimalFormat df = new DecimalFormat("#.##");
        System.out.println("Minkowski distance is: " + df.format(calculateMinkowski( a, b ,3)));
        System.out.println("Mahalonobis distance is: " + df.format(calculateMahalonobis( a, b)));
    }


    /* Calculating Minkowski distance according to the formula
     * @param  x  first point
     * @param  y  second point
     * @param  R  parameter
     * @return distance between x and y points
     */
    static  double calculateMinkowski ( double[] x, double[] y, int R ) {
        double sum = 0;

        for ( int i = 0; i < R; i++ ) // formula impelemented in a for loop
           sum += Math.pow ( Math.abs ( x[i] - y[i] ), R );


        return  Math.pow ( sum, 1.0/R );
    }


    /* Calculating Mahalonobis distance according to the given data
     * @param x first point
     * @param y second point
     * @return distance between x and y points
     */
    static double calculateMahalonobis ( double[] x, double[] y ) {
        /* Formula (x-y) * S^-1 * (x-y)^t
         * S - covariance matrix
         * t - transpose of matrix
         */
        double[] datasetX = { 64, 66, 68, 69, 73 }; // Height
        double[] datasetY = { 580, 570, 590, 660, 600}; // Score
        double[] datasetZ = {29, 33, 37, 46, 55}; // Age

        double result = 0; // result stored here
        double det = 0; // determinant for inverse matrix
        int d = 3; // dimensions X, Y, Z

        double[][] subxy = new double[d][d]; // subtraction first and second point
        double[][] transpose = new double[subxy.length][1]; // transpose of the subtracted matrix
        double[][] covar = new double[d][d]; // covariance matrix
        double[][] inverseCovar = new double[d][d]; // inverse covariance matrix
        double[][] tmpmatrix = new double[d][d]; // temporary matrix getting from the matrix product

        // (x-y)
        for ( int i = 0; i < d; i++ ) {
            subxy[0][i] = x[i] - y[i]; // subtract first and second point
            transpose[i][0] = subxy[0][i]; //  transpose of subtracted matrix
        }

        // (S) - setting up the covariance matrix
        for ( int i = 0; i < d; i++ ) {

                if (i == 0) {
                    covar[i][d-3] = getCovariance(datasetX,datasetX);
                    covar[i][d-2] = getCovariance(datasetX,datasetY);
                    covar[i][d-1] = getCovariance(datasetX,datasetZ);
                } else if (i == 1) {
                    covar[i][d-3] = getCovariance(datasetY,datasetX);
                    covar[i][d-2] = getCovariance(datasetY,datasetY);
                    covar[i][d-1] = getCovariance(datasetY,datasetZ);
                } else if ( i == 2 ) {
                    covar[i][d-3] = getCovariance(datasetZ,datasetX);
                    covar[i][d-2] = getCovariance(datasetZ,datasetY);
                    covar[i][d-1] = getCovariance(datasetZ,datasetZ);
                }

        }

        // Calculating determinant for inverting the matrix
        for(int i = 0; i < d; i++)
            det = det + (covar[0][i] * (covar[1][(i+1)%d] * covar[2][(i+2)%d] - covar[1][(i+2)%d] * covar[2][(i+1)%d]));

        // (S^-1) - Finding the inverse of the covariance matrix
        for(int i = 0; i < d; ++i) {
            for(int j = 0; j < d; ++j)
               inverseCovar[i][j] =  (((covar[(j+1)%d][(i+1)%d] * covar[(j+2)%d][(i+2)%d]) - (covar[(j+1)%d][(i+2)%d] * covar[(j+2)%d][(i+1)%d]))/ det);
        }

        /* Multiplication by subtracting matrix and the inverse of the covariance matrix
        ( x - y ) *  ( S^-1 )*/
        for ( int i = 0; i < 1; i++ ) {
            for ( int j = 0; j < inverseCovar.length; j++ ) {
                tmpmatrix[i][j] = 0;
                for ( int k = 0; k < inverseCovar.length; k++) {
                    tmpmatrix[i][j] += subxy[0][k] * inverseCovar[k][j];
                }
            }
        }

        // Multiplication by temporary matrix and transpose of the matrix
        for ( int i = 0; i < transpose.length; i++ ) {
            result += tmpmatrix[0][i] * transpose[i][0];
        }

        // The final step is the square root of the result
        return Math.sqrt(result);
    }


    /* Calculating covariance for each cell to set up covariance matrix
     * @param arr1 first data set
     * @param arr2 second data set
     * @return covariance of datasets
     */
    static double getCovariance ( double[] arr1, double[] arr2 ) {
        double sum = 0;

        for ( int i = 0; i < arr1.length; i++ )
            sum = sum + (arr1[i] - mean(arr1)) *
                    (arr2[i] - mean(arr2)); // covariance formula

        return sum / (arr1.length - 1);
    }


    /* Calculating mean to calculate the covariance
     * @param arr data set
     * @return mean of dataset
     */
    static double mean(double[] arr){
        double total = 0;

        for (double v : arr) {
            total = total + v;
        }

        return total / arr.length;
    }

}
