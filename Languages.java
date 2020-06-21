//[72]//////////////////////////////////////////////////////////////////
//[80]//////////////////////////////////////////////////////////////////////////
//[64]//////////////////////////////////////////////////////////
//[56]//////////////////////////////////////////////////
//[48]//////////////////////////////////////////
//[40]//////////////////////////////////
import java.io.*;
import java.util.Comparator;
import java.util.Arrays;
import java.util.Vector;
import org.apache.commons.cli.*;
//[80]//////////////////////////////////////////////////////////////////////// -
// IV. LANGUAGES PROBLEM                                                       -
// You are given an ASCII text file which contains some natural language text  -
// (eg. English, French, German, etc.) where the language used is unknown, but -
// the characteristic of the language is the Latin style (Latin alphabet).     -
//[40]//////////////////////////////// *                                       .
// The problem is:                     *
// Analyze the contents of this file   *
// to determine the language used.     *
//[56]/////////////////////////////////\////////////// ,
// (A) Write a program which will read the contents of ,
// the file and count the number of characters in it.  ,
// Print the total.                                    ,
//[64]/////////////////////////////////////////////////|////// _
// (B) Modify the program in (A) to also count the number of   _
// occurrences of each letter of the alphabet, converting      _
// delimiters and punctuation to the space (' ') character,    _
// and lowercase characters to uppercase ones, i.e. the only   _
// characters considered in the count will be elements of the  _
// set [' ','A'..'Z'].                                         _
// Sort the characters in this set in order of frequency, i.e. _
// the most frequently occurring characters appearing at the   _
// start of the list. Print the sorted list out.               _
//[72]/////////////////////////////////////////////////////////|////// =
// (C) Modify the program written in (B) to count the frequency of     =
// characters. Normalise the counts, i.e. divide the frequency of each =
// character by the total number of characters read; this will give    =
// you a relative frequency which is independent of the number of the  =
// characters in the text. Write the relative frequency counts to a    =
// data file.                                                          =
//[48]//////////////////////////////////////// '                       +
// (D) Extend the program written in (C) so    '
// that it may accept to read a text data file '
// and compare it with given text files of     '
// known languages. The comparison method will '
// be of your own invention. The result should '
// be a report of the language which the       '
// program thinks the original text is written '
// in.                                         '
//[56]/////////////////////////////////////////`////// :
// Note:                                               :
// The given text files of the known languages are     :
// under the names ITA, FRA, etc. for Italian, French, :
// etc. respectively. The text data file has the name  :
// TEXT.                                               :
//[80]/////////////////////////////////////////////////;////////////////////////
class Cntr {          // Counter
    public char   ch; // character
    public double fq; // frequency
    Cntr( char ch, double fq ){
        this.ch = ch;
        this.fq = fq;
    }
    public String toString(){
        return String.format( "( %c, %.4f )", ch, fq );
    }
}
//[72]//////////////////////////////////////////////////////////////////
class CntrComp implements Comparator< Cntr >{
    public int compare( Cntr a, Cntr b ){
        final double f = a.fq;
        final double g = b.fq;
        return ( f > g )? -1: ( f == g )? 0: 1; // Stack Overflow
    }
}
//[80]//////////////////////////////////////////////////////////////////////////
class Opts {
    static public final String PATH = "./txt/";
    public char   mode = 'A';
    public String fnom = String.format( "%s/TEXT", PATH );
    Opts( String [] args ){
        // https://commons.apache.org/proper/commons-cli/usage.html
        Options options = new Options( );
        options.addOption( "mode", true, "operation mode: A, B, C or D" );
        options.addOption( "fnom", true, "file name" );
        try {
            CommandLine cmd = ( new DefaultParser( )).parse( options, args );
            String optval = cmd.getOptionValue( "mode" );
            if( optval != null )
                mode = optval.charAt( 0 );
            optval = cmd.getOptionValue( "fnom" );
            if( optval != null )
                fnom = String.format( "%s/%s", PATH, optval );
        } catch( ParseException e ){
            System.out.print( e.getMessage( ));
            System.exit( -1 );
        }
    }
}
//[64]//////////////////////////////////////////////////////////
class Point {
    public double[] dat;
    public String  lab;
    Point( double[] dat, String lab ){
        this.dat = dat;
        this.lab = lab;
    }
    Point( Cntr[] cntr ){
        dat = new double[ Languages.SETSIZE ];
        lab = "";
        for( int i = 0; i < Languages.SETSIZE; ++i ){
            dat[ i ] = cntr[ i ].fq;
        }
    }
    // L2 Norm
    public double L2( Point that ){
        double d;
        double sum = 0;
        for( int j = 0; j < Languages.SETSIZE; ++j ){
            d = dat[ j ] - that.dat[ j ];
            sum += d * d;
        }
        return sum;
    }
    public String toString( ){
        String s = "";
        for( int j = 0; j < Languages.SETSIZE; ++j ){
            s += String.format( "%.4f ", dat[ j ]);
        }
        return s + lab;
    }
}
//[80]//////////////////////////////////////////////////////////////////////////
public class Languages {
    private static final int BUFSIZE = 4096; // what is this?
    private static final int A_CODE  = Character.getNumericValue( 'A' );
    private static final int Z_CODE  = Character.getNumericValue( 'Z' );
    private static final char SPACE  = ' ';
    private static final char [] SET = {
        ' ','A','B','C','D','E','F','G','H',
        'I','J','K','L','M','N','O','P','Q',
        'R','S','T','U','V','W','X','Y','Z',
    };
    public static final int SETSIZE = SET.length;
    private static char [] buf = new char [ BUFSIZE ];
    //[56]//////////////////////////////////////////////////
    private static int A( String fname ){
        int n; // number of read characters
        String msg;
        try {
            FileReader fr = new FileReader( fname );
            n = fr.read( buf );
            msg = String.format( "( A ) %s, Total: %d", fname, n );
            fr.close();
        } catch ( Exception e ){
            n = -1;
            msg = e.getMessage();
        }
        System.out.println( msg );
        return n;
    } 
    //[48]//////////////////////////////////////////
    private static int getCode( char ch ){
        return Character.getNumericValue( ch );
    }
    //[40]//////////////////////////////////
    private static int hash( char key ){
        int code = getCode( key );

        if( code < A_CODE ){
            return 0;
        } else if ( code > Z_CODE ){
            return 0;
        } else {
            return code - A_CODE + 1;
        }
    }
    //[56]//////////////////////////////////////////////////
    private static String dumpCntr( Cntr [] c ){
        String dump = "";
        for( int j = 0; j < SETSIZE; ++j ){
            dump += String.format( "%2d: %s%n", j, c[ j ]);
        }
        return dump;
    }
    //[64]//////////////////////////////////////////////////////////
    private static Cntr [] B( String fname ){
        System.out.println( "( B )" );
        int        nch = A( fname ); // read <fname/> into <buf>
        double [] freq = new double [ SETSIZE ]; // initialized to 0
        for( int j = 0; j < nch; ++j ){
            int i = hash( buf[ j ]);
            ++freq[ i ];
        }
        Cntr [] c = new Cntr[ SETSIZE ];
        for( int j = 0; j < SETSIZE; ++j ){
            c[ j ] = new Cntr( SET[ j ], freq[ j ]);
        }
        Cntr [] s = c.clone();
        Arrays.sort( s, new CntrComp());
        System.out.print( dumpCntr( s ));
        return c;
    }
    //[64]//////////////////////////////////////////////////////////
    // Normalize the frequencies of text file <fnom/>.            __
    static public Cntr [] normalize( String fnom ){ //            --
        // [ 0. Get <fnom/> counters ]                            --
        Cntr [] cntr = B( fnom ); // boom                         >>
        // [ 1. Take the sum of all characters ]                  ,,
        double sum = 0; // yeah!                                  __
        for( Cntr c: cntr ){ sum += c.fq; } // ]:{                ..
        // [ 2. Normalize ]                                       //
        for( Cntr c: cntr ){ c.fq /= sum; } // *#:z               \\_
        return cntr; //                                           ;;
    } //                                                          ||
    //[72]////////////////////////////////////////////////////////''//////||
    static public Cntr [] C( String fnom ){ //                            **
        System.out.println( "( C )" ); //                                 --
        Cntr [] cntr = normalize( fnom ); //                              ,,
        try { //                                                          ==
            // [ 3. Open File Writer ]                                    ``
            final String onom = fnom + ".fq"; //                          ^^
            FileWriter fw = new FileWriter( onom ); // 8|                 88
            // [ 4. Oufut the frequencies ]                               ((
            fw.write( dumpCntr( cntr )); // ..                            {{
            // [ 5. Finalize ]                                            ^^
            fw.close( ); // :)                                            <<
        } catch( Exception e ){ //                                        ::
            System.out.print( e.getMessage( )); //                        ;;
        } //                                                              ##
        return cntr; // bye                                               <<
    } //                                                                  ==
    //[72]//////////////////////////////////////////////////////////////////
    static private Vector< Point >load( ){
        Vector< Point >vec = new Vector<>( );
        String fnom = String.format( "%s/fq", Opts.PATH );
        try {
            BufferedReader br = new BufferedReader( new FileReader( fnom ));
            String line;
            while(( line = br.readLine( )) != null ){
                String [] s = line.split( "\\s+" );
                String lab = s[ SETSIZE ];
                double [] fq = new double [ SETSIZE ];
                for( int j = 0; j < SETSIZE; ++j ){
                    fq[ j ] = Float.parseFloat( s[ j ]);
                }
                vec.add( new Point( fq, lab ));
            }
        } catch( Exception e ){
            System.out.print( e.getMessage( ));
        }
        return vec;
    }
    //[72]//////////////////////////////////////////////////////////////////
    static public void D( String fnom ){ //                               ;;
        System.out.println( "( D )" ); //                                 --
        Cntr[] cntr        = normalize( fnom ); //                        ,,
        Point p            = new Point( cntr ); //                        __
        Vector< Point >vec = load( ); //                                  ::
        final int n        = vec.size( ); //                              ==
        int i              = 0; //                                        **
        double min         = p.L2( vec.get( i )); //                      ;;
        for( int j = 1; j < n; ++j ){ //                                  <<
            Point  q = vec.get( j ); //                                   ==
            double d = p.L2( q ); //                                      >>
            if( d < min ){ //                                             <<
                i   = j; //                                               --
                min = d; //                                               ;;
            } //                                                          ``
        } //                                                              ^^
        System.out.println( vec.get( i ).lab );//                         ``
    } ////////////////////////////////////////////////////////////////////;;
    //[64]//////////////////////////////////////////////////////////
    private static void tëst( ){
        System.exit(-1);
    }    
    //[72]//////////////////////////////////////////////////////////////////
    //[**]//////////////////////////////// ARGS="-mode D -fnom FRA" make run
    public static void main( String [] args ){
        Opts oo = new Opts( args );
        switch( oo.mode ){
        case 'A':
            A( oo.fnom );
            break;
        case 'B':
            B( oo.fnom );
            break;
        case 'C':
            C( oo.fnom );
            break;
        case 'D':
            D( oo.fnom );
            break;
        default:
            break;
        }
    }
}
//[72]///////////////////////////////////////////////////////////// log:
