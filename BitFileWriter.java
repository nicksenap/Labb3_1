import java.io.*;

/**
 * Class to write to a file bit by bit.
 */
public class BitFileWriter {
   private BufferedOutputStream writer;   // Our internal file reader.
   private Integer writtenBits,           // How many bits have been stored in the to-write byte.
            byteToWrite;                  // The byte we're going to write next.

   /**
    * Creates a BitFileWriter object from a file name; opens the file for writing.
    */
   public BitFileWriter( String filename ) throws SecurityException, IOException {
      writer = new BufferedOutputStream( new FileOutputStream( filename ) );
      byteToWrite = 0;
      writtenBits = 0;
   }

   /**
    * Writes a bit to the file.  Writes a 1 if bit is true, else a 0.
    */
   public void writeBit( Boolean bit ) throws IOException {
      byteToWrite <<= 1;      // Shift our to-write byte one step left.

      if( bit )
         byteToWrite |= 1;    // Add in a 1 if we're writing a 1.

      if( ++writtenBits == 8 ) {       // If we've acquired an entire byte...
         writer.write( byteToWrite );  // ...write it to the file...
         byteToWrite = 0;              // ...and reset our internal values.
         writtenBits = 0;
      }
   }

   /**
    * Writes 8 bits, taken from an int, to the file.
    */
   public void writeByte( Integer val ) throws IOException {
      byteToWrite <<= 8;               // Add the new bits to the end of our
      byteToWrite |= ( val & 0xff );   // byte to write.

      // Shift writtenBits to the right to get what to write to the file now.
      Integer writeNow = ( byteToWrite >> writtenBits );
      // Keep the rest to be written later.
      byteToWrite &= ~( writeNow << writtenBits );

      // Aaaand write it.
      writer.write( writeNow );
   }

   /**
    * Closes the file writer.  Always do this when you're done!
    */
   public void close() throws IOException {
      if( writtenBits > 0 ) {    // If there's anything left to write, write it.
         byteToWrite <<= ( 8 - writtenBits );

         writer.write( byteToWrite );
      }

      writer.close();
   }
}
