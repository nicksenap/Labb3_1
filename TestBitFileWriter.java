import junit.framework.*;
import junit.textui.*;

import java.io.*;

public class TestBitFileWriter extends TestCase {

    protected void setUp(){
        
   }

    public void testBokstav() throws IOException {
        // A = 65 = 0100 0001
	// File should contain ABC: 0100 0001 0100 0010 0100 0011
	BitFileWriter bfwriter = new BitFileWriter( "testw.txt" );
        bfwriter.writeBit( false);
        bfwriter.writeBit( true);
        bfwriter.writeBit( false);
        bfwriter.writeBit( false);
        bfwriter.writeBit( false);
        bfwriter.writeBit( false);
        bfwriter.writeBit( false);

	// readByte() here: 1010 0001 = 128 + 32 + 1 = 161
        bfwriter.writeByte( 161);
     
	// Last bit of B.
        bfwriter.writeBit( false);

	bfwriter.writeByte( 67 );		// C
	bfwriter.close();

	FileReader fstream = new FileReader("testw.txt");
        BufferedReader inp = new BufferedReader(fstream);
        Assert.assertEquals( inp.readLine(), "ABC" );
        
        inp.close();
       
    }
   public static Test suite() {
      // Junit "parsar" klassen och metoder som borjar pa test
        // antas vara de som ska testas
      return new TestSuite(TestBitFileWriter.class);
    }

    public static void main(String[] args) {

        // Testkör på kommandorad
      junit.textui.TestRunner.run(suite());
    }
}
