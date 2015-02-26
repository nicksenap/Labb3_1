import java.io.*;
import java.util.*;

/**
 * Class to store Huffman trees.
 */
class HuffmanTree implements Comparable {
   private HuffmanTree left, right;    // The subbranches of this node.
   private Integer value,              // The value this node represents, if any.
            weight;                    // The weight of this tree.

   /**
    * Creates a tree from two subtrees.
    */
   public HuffmanTree( HuffmanTree l, HuffmanTree r ) {
      left = l;
      right = r;
      value = -1;
      weight = l.getWeight() + r.getWeight();   // The weight is just the sum of the subtrees.
   }

   /**
    * Creates a node with just a value and a weight.
    */
   public HuffmanTree( Integer v, Integer w ) {
      left = null;
      right = null;
      value = v;
      weight = w;
   }

   /**
    * Creates a tree with just a value.
    */
   public HuffmanTree( Integer v ) {
      this( v, 0 );
   }

   /**
    * Creates a tree by reconstructing it from a savefile, opened with a
    * BitFileReader.  Don't forget to close() the reader afterwards!
    */
   protected HuffmanTree( BitFileReader bfr ) throws IOException {
      // Read a bit.  If it's 1, this should branch out.
      if( bfr.readBit() ) {
         left = new HuffmanTree( bfr );      // First read the left branch.
         right = new HuffmanTree( bfr );     // Then the right branch.
         value = -1;                         // No value.
         weight = 0;                         // Weight is irrelevant now.
      }
      else {                                 // Else it's a leaf.
         left = null;
         right = null;
         value = bfr.readByte();             // Read an entire byte as the value.
         weight = 0;
      }
   }

   /**
    * Whether this node has a value (i.e. is a leaf).
    */
   public Boolean hasValue() {
      return value >= 0;
   }

   /**
    * Traverse the tree depending on a truth value.  True returns the right
    * branch, false the left.
    */
   public HuffmanTree pickBranch( Boolean r ) {
      return ( r ? right : left );
   }

   /**
    * Returns the value of this node.
    */
   public Integer getValue() {
      return value;
   }

   /**
    * Returns the weight of this tree.
    */
   public Integer getWeight() {
      return weight;
   }

   /**
    * Saves this tree to an opened BitFileWriter.  Don't forget to close() the
    * writer afterwards!
    */
   private void saveToFile( BitFileWriter bfw ) throws IOException {
      if( hasValue() ) {               // If it is a leaf (has a value)...
         bfw.writeBit( false );        // ...then write a 0...
         bfw.writeByte( getValue() );  // ...and then the value.
      }
      else {
         bfw.writeBit( true );         // Else write 1...
         left.saveToFile( bfw );       // ...then the left branch...
         right.saveToFile( bfw );      // ...and last the right branch.
      }
   }

   /**
    * Saves this tree to a file.
    */
   public void saveToFile( String filename ) throws IOException {
      BitFileWriter bfw = new BitFileWriter( filename );

      saveToFile( bfw );
      bfw.close();
   }

   /**
    * Compares this tree to another.  This merely compares their weight,
    * enabling the construction of a Huffman tree.
    */
   public int compareTo( Object t ) {
      HuffmanTree tr = (HuffmanTree)t;
      return getWeight().compareTo( tr.getWeight() );
   }

   /**
    * Builds a dictionary of byte-to-huffman-code.
    * The resultant dictionary is stored in dic.
    * soFar is the code that's been built so far.
    * dummy is an empty Boolean array, to make toArray() do as we want.
    */
   private void buildDictionary( HashMap<Integer, Boolean[]> dic, ArrayList<Boolean> soFar, Boolean[] dummy ) {
      if( hasValue() ) {   // If this is a leaf, add the value to the dictioanry.
         dic.put( getValue(), soFar.toArray( dummy ) );
         return;
      }

      Integer where = soFar.size();

      // Else add 0 and do it for the left branch.
      soFar.add( false );
      left.buildDictionary( dic, soFar, dummy );

      // Then change the last to 1 and do it for the right.
      soFar.set( where, true );
      right.buildDictionary( dic, soFar, dummy );

      // Lastly remove the new entry to make the recursion work.
      soFar.remove( where.intValue() );
   }

   /**
    * Returns a dictionary of byte-to-huffman-code.
    */
   public HashMap<Integer, Boolean[]> getDictionary() {
      HashMap<Integer, Boolean[]> dic = new HashMap<Integer, Boolean[]>();
      ArrayList<Boolean> soFar = new ArrayList<Boolean>();

      buildDictionary( dic, soFar, new Boolean[0] );

      return dic;
   }

   public String toString() {
      HashMap<Integer, Boolean[]> dic  = getDictionary();
      String ret = "";

      for( Map.Entry<Integer, Boolean[]> entry : dic.entrySet() ) {
         ret += (char)entry.getKey().byteValue() + ": ";
         for( Boolean b : entry.getValue() )
            ret += b ? "1" : "0";
         ret += "\n";
      }
      return ret;
   }
}

