import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

public class gaussianSPP{
    static final int maxLength = 4;
    static final Scanner input = new Scanner( System.in );
    static final char[] varNames = { 'X', 'Y', 'Z', 'V', 'W', 'J', 'K' };

    public static double[] solver(double[][] A, double[] b) {
        int n = b.length;
        double[] scale = new double[ maxLength - 1 ];

        for (int p = 0; p < n; p++) {

            // find pivot row and swap
            int max = p;
            for (int i = p + 1; i < n; i++) {
                if (Math.abs(A[i][p]) > Math.abs(A[max][p])) {
                    max = i;
                }
            }
            double[] temp = A[p]; A[p] = A[max]; A[max] = temp;
            double   t    = b[p]; b[p] = b[max]; b[max] = t;

            // pivot within A and b
            for (int i = p + 1; i < n; i++) {
                double alpha = A[i][p] / A[p][p];
                b[i] -= alpha * b[p];
                for (int j = p; j < n; j++) {
                    A[i][j] -= alpha * A[p][j];
                }
            }
        }

        // back substitution
        double[] x = new double[n];
        for (int i = n - 1; i >= 0; i--) {
            double sum = 0.0;
            for (int j = i + 1; j < n; j++) {
                sum += A[i][j] * x[j];
            }
            x[i] = (b[i] - sum) / A[i][i];
        }
        return x;
    }

    public static double[] solve( double[][] A, double[] B ) {
        int n = B.length;
        double[] x = new double[ n ];
        double[] scale = new double[ n ];

        // Fill scale array
        for ( int i = 0; i < n; i++ ) {
            for ( int j = 0; j < ( maxLength - 1 ); j++ ) {
                if ( scale[ i ] < Math.abs( A[ i ][ j ] ) ) {
                    scale[ i ] = Math.abs( A[ i ][ j ] );
                }
            }
        }

        for ( int i=0; i < n; i++ ) {
            double[] scaleRatio = new double[ n - i ];
            for ( int j=i; j < n; j++ ) {
                scaleRatio[ j ] = Math.abs( scale[ j ] / A[ j ][ i ] );
            }
            System.out.print( "Scale Rations [ " );
            System.out.print( i );
            System.out.print( " ]: " );
            System.out.println( Arrays.toString(scaleRatio) );
        }

        System.out.println( Arrays.toString( scale ));

        return x;
    }

    public static double[] getLine() {
        System.out.print( "Enter coefficients: " );
        String strLine = input.nextLine();

        return parseLine( strLine );
    }

    public static double[] parseLine( String line ) {
        double[] pLine = new double[ maxLength ];
        String[] tmp = line.split( " " );
        for ( int i = 0; i < maxLength; i++ ) {
            pLine[ i ] = Double.parseDouble(tmp[ i ] );
        }
        return pLine;
    }

    public static void printMatrix( double[][] matrix ) {
        for ( int i=0; i < matrix.length; i++ ) {
            System.out.print( "|" );
            for ( int j=0; j < matrix[0].length; j++ ) {
                System.out.printf( "% -4.2f |", matrix[ i ][ j ] );
            }
            System.out.println();
        }
    }

    public static void clearScanner() {
        while ( input.hasNext() )
            input.next();
    }

    public static void main(String[] args) {
        System.out.print( "Enter number of equations: " );
        int n = input.nextInt();
        input.nextLine(); // Clear \n out of scanner buffer

        System.out.print( "Would you like to use a file( 1 ) or enter [coefficients+b] manually( 2 )? ");
        int response = input.nextInt();
        input.nextLine(); // Clear \n out of scanner buffer
        
        double[][] A = new double[ n ][ maxLength-1 ];
        double[] b = new double[ n ];

        if ( response == 1 ) {
            try {
                System.out.print( "Enter filename: " ); 
                String filename = input.nextLine();
                filename = filename.replace( "\n", "" );
                System.out.println( filename );
                File file = new File( filename ); 
                System.out.printf( "Does file exist: %b\n", file.exists() );
                Scanner fReader = new Scanner( file );

                int count = 0;
                while( fReader.hasNextLine() ) {
                    System.out.println( fReader.nextLine() );
                    double[] line = parseLine( fReader.nextLine() );
                    System.out.println( line.toString() );
                    b[ count ] = line[ maxLength - 1 ];
                    for ( int j = 0; j < (maxLength-1); j++ ) {
                        A[ count++ ][ j ] = line[ j ];
                    }
                }
                fReader.close();
            } catch ( FileNotFoundException e ) {
                System.out.println( "Error reading file" );
                e.printStackTrace();
                input.close();
                System.exit(0);
            }
        } else if ( response == 2 ) {
            for ( int i = 0; i < n; i++ ) {
                double[] line = getLine();
                b[ i ] = line[ maxLength - 1 ];
                for ( int j = 0; j < (maxLength-1); j++ ) {
                    A[ i ][ j ] = line[ j ];
                }
            }
        }
        
        printMatrix(A);
        double[] x = solve(A, b);


        // print results
        for (int i = 0; i < n; i++) {
            System.out.print( varNames[ i ] );
            System.out.print( ": " );
            System.out.println( x[ i ] );
        }

        input.close();
    }

}
