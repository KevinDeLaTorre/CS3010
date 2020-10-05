// Author     : Kevin De La Torre
// Class      : CS 3010.01  
// Assignment : Programming Project 1
// Description: Gaussian elimination with Scaled Partial Pivoting


import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;
import java.io.BufferedReader;

public class gaussianSPP{
    static final Scanner input = new Scanner( System.in );
    static int n = 0;

    public static double[] solve( double[][] a, double[] b ) {
        double[] scale = new double[ n ];
        int[] order = new int[n]; // maintains the order of the matrix
        
        // Make clones to not affect original matrices
        double[][] A = a.clone();
        double[] B = b.clone();

        // Initialize the order in default sequence
        for ( int i=0; i<n;i++ ) {
            order[ i ] = i;
        }

        // Fill scale array
        for ( int i = 0; i < n; i++ ) {
            for ( int j = 0; j < ( n ); j++ ) {
                if ( scale[ i ] < Math.abs( A[ i ][ j ] ) ) {
                    scale[ i ] = Math.abs( A[ i ][ j ] );
                }
            }
        }
        System.out.printf( "Original Matrix [ Scale Array: %s ]\n", Arrays.toString( scale ));
        printMatrix(A, B);
        System.out.println();

        for ( int i=0; i < (n-1); i++ ) {
            double[] scaleRatio = new double[ n - i ];
            int[] ratioOrder = new int[ n - i ];
            for ( int j=0,k=i; j<(n - i); j++,k++ ) {
                ratioOrder[ j ] = order[ k ];
            }
            int highestRatio= 0; // Keeps track of the location of the highest ratio in scale array
            for ( int j=0; j < ( n - i ); j++ ) {
                scaleRatio[ j ] = Math.abs( A[ order[ ratioOrder[ j ] ] ][ i ] ) / scale[ ratioOrder[ j] ];
                if ( scaleRatio[ j ] > scaleRatio[ highestRatio ] ) {
                    highestRatio = j;
                }
            }

            System.out.printf( "Scale Ratios of previous matrix [ %d ]: %s\n", i, Arrays.toString(scaleRatio) );
            int[] tmpOrder = order.clone();

            // If there was a change in the order of the matrix update the order array
            if ( order[ i ] != ratioOrder[ highestRatio ] ) {
                int tmpPos = ratioOrder[ highestRatio ];
                int iPos = findInArray(ratioOrder[ highestRatio ], order);
                order[ i ] = tmpPos;
                order[ iPos ] = i;
            }

            System.out.printf( "Order of this matrix ( in terms of original matrix ): %s\n", Arrays.toString(order));

            double[][] tmp = A.clone();
            for ( int j=0; j<n; j++ ) {
                tmp[ j ] = a[ order[ j ] ];
            }
            A = tmp;

            double[] tmpb = B.clone();
            for ( int j=0; j<n; j++ ) {
                B[ j ] = tmpb[ findInArray(order[ j ], tmpOrder)];
            }

            if ( i != (n-1) ) {
                for ( int j=(i+1); j<n; j++ ) {
                    double multiplier = (A[ j ][ i ] / A[ i ][ i ]);
                    for ( int k=0; k<n; k++ ) {
                        A[ j ][ k ] -= (A[ i ][ k ] * multiplier);
                    }
                    B[ j ] -= (B[ i ] * multiplier);
                }
            }
            
            printMatrix(A, B);
            System.out.println();
        }

        // back substitution
        double[] x = new double[n];
        for (int i = ( n - 1 ); i >= 0; i--) {
            double sum = 0;
            for (int j = (i + 1); j < n; j++) {
                sum += A[i][j] * x[j];
            }
            x[i] = (B[i] - sum) / A[i][i];
        }

        return x;
    }

    public static int findInArray( int x, int[] array ) {
        int t = 0;
        for ( int i=0; i<array.length; i++ ) {
            if ( x == array[ i ] ) {
                t = i;
            }
        }
        return t;
    }
    public static double[] getLine() {
        System.out.print( "Enter coefficients: " );
        String strLine = input.nextLine();

        return parseLine( strLine );
    }

    public static double[] parseLine( String line ) {
        double[] pLine = new double[ n + 1 ];
        String[] tmp = line.trim().split( " " );
        for ( int i = 0; i < ( n + 1 ); i++ ) {
            pLine[ i ] = Double.parseDouble(tmp[ i ] );
        }
        return pLine;
    }

    public static void printMatrix( double[][] matrix, double[] b ) {
        for ( int i=0; i < matrix.length; i++ ) {
            System.out.print( "|" );
            for ( int j=0; j < matrix[0].length; j++ ) {
                System.out.printf( " % -4.2f |", matrix[ i ][ j ] );
            }
            System.out.printf( " = | % -4.2f |", b[ i ]);
            System.out.println();
        }
    }

    public static void main(String[] args) {
        System.out.print( "Enter number of equations: " );
        n = input.nextInt();
        input.nextLine(); // Clear \n out of scanner buffer

        System.out.print( "Would you like to use a file( 1 ) or enter [coefficients+b] manually( 2 )? ");
        int response = input.nextInt();
        input.nextLine(); // Clear \n out of scanner buffer
        
        double[][] A = new double[ n ][ n ]; 
        double[] b = new double[ n ];

        if ( response == 1 ) {
            try {
                System.out.print( "Enter filename: " ); 
                String filename = input.nextLine();
                BufferedReader br = new BufferedReader(new FileReader( filename ) );
;

                for ( int i=0; i<n; i++ ) {
                    double [] line = parseLine( br.readLine() );
                    b[ i ] = line[ n ];
                    for ( int j = 0; j < ( n ); j++ ) {
                        A[ i ][ j ] = line[ j ];
                    }
                }
                br.close();
            } catch ( FileNotFoundException e ) {
                System.out.println( "Error reading file" );
                e.printStackTrace();
                input.close();
                System.exit(0);
            } catch ( IOException ioe ) {
                    System.out.println( "Error closing Buffered Reader." );
            }
        } else if ( response == 2 ) {
            if ( n >= 1 ) {

                for ( int i = 0; i < n; i++ ) {
                    double[] line = getLine();
                    b[ i ] = line[ n ];
                    for ( int j = 0; j < ( n ); j++ ) {
                        A[ i ][ j ] = line[ j ];
                }
            }

            }
                    }
        
        double[] x = solve(A, b);

        // print results
        for (int i = 0; i < n; i++) {
            System.out.printf( "X%d: %f\n", (i+1), x[ i ] );
        }

        input.close();
    }

}
