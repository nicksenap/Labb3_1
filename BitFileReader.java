import java.io.*;

/**
 * Class to read from a file bit by bit.
 */
public class BitFileReader {
   private BufferedInputStream reader; // Our internal file reader.
   private Integer lastReadBit,        // How many bits remain to be read from the buffered byte.
            lastReadByte;              // The buffered byte read from the file.

   /**
    * Creates a BitFileReader object from a file name; opens the file for reading.
    */
   public BitFileReader( String filename ) throws FileNotFoundException, SecurityException, IOException {
      reader = new BufferedInputStream( new FileInputStream( filename ) );
      loadByte();       // Load the first byte into the buffer.
      lastReadBit = 8;  // Nothing read yet, there are still 8 bits left to read from the byte.
   }

   /**
    * Reads the next bit from the file.  Returns false if the next bit was 0,
    * and true if it was 1.
    */
   public Boolean readBit() throws IOException {
      // The bit we want is counted from the end.
      Integer lastBit = ( ( lastReadByte >> --lastReadBit ) & 1 );

      // If lastReadBit is 0 we need to read a new byte.
      if( lastReadBit == 0 ) {
         loadByte();
         lastReadBit = 8;
      }

      return lastBit == 1;
   }

   /**
    * Reads 8 bits from the file and returns them as an integer.
    */
   public Integer readByte() throws IOException {
      Integer last = lastReadByte;     // We're going to need to load one more
      loadByte();                      // byte no matter what.

      // Since we are reading 8 bits, lastReadBit will stay the same.  Just combine
      // the bits we need from the previous and the new read byte.
      return ( ( last << ( 8 - lastReadBit ) ) | ( lastReadByte >> lastReadBit ) ) & 0xff;
   }

   /**
    * Returns true iff the file-reading has reached EOF.
    */
   public Boolean isAtEnd() {
      return lastReadByte == -1 || reader == null;
   }

   /**
    * Closes the file reader.  Always do this when you're done!
    */
   public void close() throws IOException{
      if( reader != null )
         reader.close();
   }

   /**
    * Reads a byte from the file and stores it in the buffer.
    */
   protected void loadByte() throws IOException {
      lastReadByte = reader.read();
   }

   /**
    * main method: prints the contents of a file (as 1s and 0s) in a human-readable
    * format.
    */
   public static void main( String[] args ) {
      if( args.length < 1 ) {
         System.out.println( "Usage: java BitFileReader filename" );
         System.exit( 0 );
         return;
      }

      String filename = args[0];
      BitFileReader rdr = null;

      try {
         rdr = new BitFileReader( filename );

         for( Integer i = 0; !rdr.isAtEnd(); i++ ) {
            if( i % 8 == 0 && i != 0 )
               System.out.print( " , " );
            else if( i % 4 == 0 && i != 0 )
               System.out.print( " " );
            System.out.print( rdr.readBit() ? "1" : "0" );
         }
      }
      catch( IOException e ) {
         e.printStackTrace();
      }
      finally {
         try {
            if( rdr != null )
               rdr.close();
         }
         catch( IOException e) {
            e.printStackTrace();
         }
      }

      System.out.println();
   }
}
