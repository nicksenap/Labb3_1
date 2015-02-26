import java.util.*;
import java.io.*;

/**
 * Class to encode and decode files using Huffman tree coding.
 */
class Huffman {
   static String TREE_FILE_EXTENSION = ".ht";      // Extension for the tree file.
   static String ENC_FILE_EXTENSION = ".htcode";   // Extension for the encoded file.

   /**
    * Encodes the given file, and saves the result in the filename plus the
    * extensions TREE_FILE_EXTENSION and ENC_FILE_EXTENSION.
    */
   static void HuffmanEncode( String filename ) throws FileNotFoundException, SecurityException, IOException {
      // For reading the input file.
      BufferedInputStream bis = new BufferedInputStream( new FileInputStream( filename ) );
      // Table of how often each byte occurs in the file.
      Integer[] frequencies = new Integer[0x100];

      // First read the file once to figure out the frequence of characters.
      Integer b;

      // Since it's an array of Integers, frequencies is full of nulls.
      for( Integer i = 0; i < 0x100; i++ )
         frequencies[i] = 0;

      try {
         while( ( b = bis.read() ) != -1 )   // As long as we can read...
            frequencies[b]++;                // ...increment the table.
      }
      catch( IOException e ) {
         throw( e );
      }
      finally {
         bis.close();
      }

      // Now we create a Huffman tree leaf for each value we found, and store
      // those in a priority queue, which is kept sorted on the weight we get
      // from the frequency table.
      PriorityQueue<HuffmanTree> pineapple = new PriorityQueue<HuffmanTree>();
      for( Integer i = 0; i < 0x100; i++ ) {
         if( frequencies[i] > 0 )
            pineapple.add( new HuffmanTree( i, frequencies[i] ) );
      }

      // As long as the queue has more than two members, combine the two with
      // lowest weight into a new tree, and add it to the queue.
      while( pineapple.size() > 1 ) {
         HuffmanTree right = pineapple.poll(), left = pineapple.poll();
         pineapple.add( new HuffmanTree( left, right ) );
      }

      // If the queue is empty the file was empty.  Just quit.
      if( pineapple.size() == 0 )
         return;

      // We've got our Huffman tree.  Save it to the tree save file.
      HuffmanTree htree = pineapple.poll();
      htree.saveToFile( filename + TREE_FILE_EXTENSION );

      // Get the Huffman dictionary from the tree.
      HashMap<Integer, Boolean[]> dic = htree.getDictionary();

      // Count the total length that the encoded file will have.  This is the
      // sum of the product of each occurance of each character and the length
      // of its Huffman code.
      Integer totalLength = 0;
      for( Integer i = 0; i < 0x100; i++ ) {
         if( frequencies[i] > 0 )
            totalLength += frequencies[i] * dic.get( i ).length;
      }

      // We need to pad the resultant file with a number of bits to make it an
      // even number of bytes.  Otherwise we'll run into garbage when decoding
      // the result.
      Integer padLength = 8 - totalLength % 8;

      // Open the file for reading again, now to encode each byte.
      bis = new BufferedInputStream( new FileInputStream( filename ) );

      // And a BitFileWriter to write the result.
      BitFileWriter codewriter;
      try {
         codewriter = new BitFileWriter( filename + ENC_FILE_EXTENSION );
      }
      catch( SecurityException e ) { 
         bis.close();
         throw( e );
      }
      catch( IOException e ) {
         bis.close();
         throw( e );
      }
      finally { }

      try {
         // First write the padding.  This is a number of 1s followed by a 0.
         for( Integer i = 1; i < padLength; i++ )
            codewriter.writeBit( true );
         codewriter.writeBit( false );

         // Then read each byte from the input file again, and write the bits
         // it's associated with in the dictionary to the output file.
         while( ( b = bis.read() ) != -1 ) {
            for( Boolean bit : dic.get( b ) )
               codewriter.writeBit( bit );
         }
      }
      catch( IOException e ) {
         throw( e );
      }
      finally {
         codewriter.close();
         bis.close();
      }

      // Done!
   }

   /**
    * Decodes a Huffman encoded file codefile, using the Huffman tree stored in
    * treefile, and writing the decoded file to outputfile.
    */
   static void HuffmanDecode( String treefile, String codefile, String outputfile ) throws FileNotFoundException, SecurityException, IOException {
      // First open the tree file in a BitFileReader.
      BitFileReader bfr = new BitFileReader( treefile );
      HuffmanTree htree;

      try {
         htree = new HuffmanTree( bfr );  // Reconstruct the tree using the BFR constructor.
      }
      catch( IOException e ) {
         throw( e );
      }
      finally {
         bfr.close();
      }

      // Next, open the encoded file for reading.
      bfr = new BitFileReader( codefile );

      try {
         // Read the padding: keep reading bits until the first 0 is encountered.
         while( !bfr.isAtEnd() && bfr.readBit() );
      }
      catch( IOException e ) {
         bfr.close();
         throw( e );
      }

      // Open the output file for writing.
      BufferedOutputStream outp;

      try {
         outp = new BufferedOutputStream( new FileOutputStream( outputfile ) );
      }
      catch( SecurityException e ) {
         bfr.close();
         throw( e );
      }
      catch( IOException e ) {
         bfr.close();
         throw( e );
      }
      finally { }

      HuffmanTree curtree = htree;

      try {
         while( !bfr.isAtEnd() ) {
            // Keep reading bit from the encoded file, and traverse the tree
            // according to the value.
            curtree = curtree.pickBranch( bfr.readBit() );

            // If we get to a leaf, output the value of the leaf, and start from
            // the beginning.
            if( curtree.hasValue() ) {
               outp.write( curtree.getValue() );
               curtree = htree;
            }
         }
      }
      catch( IOException e ) {
         throw( e );
      }
      finally {
         outp.close();
         bfr.close();
      }

      // Done!
   }

   /**
    * main() - for command-line functionality.
    * One argument given: encode the file provided.
    * Three arguments given: decode a file from tree file and encoded file, into
    * an output file.
    */
   public static void main( String[] args ) {
      if( args.length != 1 && args.length != 3 ) {
         System.out.println( "Usage - encoding: java Huffman <filename>" );
         System.out.println( "Usage - decoding: java Huffman <tree file name> <encoded file name> <output filename>" );
         System.exit( 0 );
         return;
      }

      if( args.length == 1 ) {            // Encoding.
         try {
            HuffmanEncode( args[0] );
            System.out.println( "Done." );
         }
         catch( FileNotFoundException e ) {
            System.out.println( "Couldn't find file." );
         }
         catch( SecurityException e ) {
            System.out.println( "Permission denied." );
         }
         catch( IOException e ) {
            System.out.println( "IO error." );
         }
         finally { }
      }
      else {                              // Decoding.
         try {
            HuffmanDecode( args[0], args[1], args[2] );
            System.out.println( "Done." );
         }
         catch( FileNotFoundException e ) {
            System.out.println( "Couldn't find file." );
         }
         catch( SecurityException e ) {
            System.out.println( "Permission denied." );
         }
         catch( IOException e ) {
            System.out.println( "IO error." );
         }
         finally { }
      }
   }
}
