import junit.framework.*;
import junit.textui.*;

import java.io.*;

public class TestBitFileReader extends TestCase {

    protected BitFileReader bfreader;

    protected void setUp(){
        String filnamn = "test.txt";
        // Skapa fil
        try {
            FileWriter fstream = new FileWriter(filnamn);
            BufferedWriter out = new BufferedWriter(fstream);
            out.write("ABC");
            //Close the output stream
            out.close();
            bfreader = new BitFileReader(filnamn);
        }catch (Exception e){//Catch exception if any
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
   }

    public void testBokstav() throws IOException {
        // A = 65 = 0100 0001
	// File should contain ABC: 0100 0001 0100 0010 0100 0011
	
        Assert.assertEquals(bfreader.readBit().booleanValue(), false);
        Assert.assertEquals(bfreader.readBit().booleanValue(), true);
        Assert.assertEquals(bfreader.readBit().booleanValue(), false);
        Assert.assertEquals(bfreader.readBit().booleanValue(), false);
        Assert.assertEquals(bfreader.readBit().booleanValue(), false);
        Assert.assertEquals(bfreader.readBit().booleanValue(), false);
        Assert.assertEquals(bfreader.readBit().booleanValue(), false);

	// readByte() here: 1010 0001 = 128 + 32 + 1 = 161
        Assert.assertEquals(bfreader.readByte().intValue(), 161);
     
	// Last bit of B.
        Assert.assertEquals(bfreader.readBit().booleanValue(), false);

    }
   public static Test suite() {
      // Junit "parsar" klassen och metoder som borjar pa test
        // antas vara de som ska testas
      return new TestSuite(TestBitFileReader.class);
    }

    public static void main(String[] args) {

        // Testkör på kommandorad
      junit.textui.TestRunner.run(suite());
    }
}
