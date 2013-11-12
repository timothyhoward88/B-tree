/**
 * BTree Data Structure
 * 
 * @authors Mason Kinney, Timothy Howard
 */ 

public class Btree {
        
        /*Holds onto the root node, the node we are currently
          looking at, and a node used for splitting*/
        BTreeNode root, current, sibling;
        
        /*Holds onto the degree of the tree, the size of
          each node in bytes, the length of each sequence,
          and the size of the metadata*/
        public final int degree, sequenceLength,nodeSize,metaSize=4;
        
        /*The location of the root node on the disk*/
        private int rootLocation;
        
        
        /**
         * 
         * @param degree
         * 		Degree of the B-Tree
         * @param seqLength
         * 		DNA sequence length (1 <= sequence length <= 31).
         * Size should be taken care of in the class creating the B-Tree.
         */
        public Btree(int degree, int seqLength){
                this.degree=degree;
                this.sequenceLength = seqLength;
                this.root = new BTreeNode(metaSize);	//Use metaSize as the offset for the first node.
                this.nodeSize=(2*degree+2)*4+(2*degree-1)*12;
        }
        
        public void insert(long value){
                current=root;
                
        }
        

        /**
         *
         *
         */
        private class BTreeNode {
        	
        		/*Pointer values for the location on the disk, number
        		  of objects in the node and the number of children references */
                private int parent,numItems,numChildren;
                public final int location;
                
                /*Array for location of children nodes on the disk*/
                private int[] children;
                
                /*Objects containing long sequence value and frequency count*/
                private TreeObject[] data;
        
                
                /**
                 * Create the first node of an empty tree.
                 * @param location
                 * 		The offset of the node on the disk.
                 */
                public BTreeNode(int location){
                        this.children = new int[degree*2];
                        this.data = new TreeObject [degree*2-1];
                        this.location = location;//replace w/ expression for location in file
                        numItems=numChildren=0;
                }
                
                /**create a new root when the former root is split.
                 * @param sentUp
                 * @param child0
                 * @param child1
                 */
                public BTreeNode(long sentUp,int child0,int child1){
                        children=new int[degree*2];
                        children[0]=child0;
                        children[1]=child1;
                        numChildren=2;
                        
                        data=new TreeObject [degree*2-1];
                        data[0]=new TreeObject(sentUp);
                        numItems=1;
                        
                        location=-1;
                }
                
                /** create a new sibling node when a full node is split.
                 * @param value
                 * @param myHalf
                 * @param myKids
                 */
                public BTreeNode(long value,TreeObject[] myHalf,int[] myKids){
                        location=-1;
                }
                
//                /** read a node from disk (or cache, possibly)
//                 * @param offset
//                 */
//                public BTreeNode(int offset){
//                        location=offset;
//                }
                
                public void insert(long value){
                        if(numItems==data.length) split(value);//wrong, does not account for duplicates
                        int i=numItems;
                        while (i>0&&value<data[--i].value) {
                                data[i+1]=data[i];
                        }
                        if (data[i].value==value) data[i].incrementFrequency();
                        else data[i]=new TreeObject(value);
                        numItems++;
                }

                private void split(long value) {
                        // TODO Auto-generated method stub
                        
                }

        }
        
      /**
      * Tree Object class to hold onto the binary sequence value
      * and the frequency of the value in the used file.
      *
      */
        public class TreeObject implements Comparable<TreeObject>{
        	
            /*Sequence value*/
    		private long value;
    		
    		/*frequency in the file and length of the sequence*/
            private int frequency;
            
            /*Represents the characters for the DNA sequence*/
            public final char[] codes={'a','c','g','t'};
            
            /**
             * 
             * @param value
             * 		The long value representing the sequence
             * of the characters. This must be in a binary 
             * representation.
             * 	Ex - 0010 will be passed in as 2
             * 		 1000 will be passed in as 8
             * 
             * @param sequenceLength
             * 		The length of the sequence.
             */
            public TreeObject(long value){
                    this.value = value;
                    frequency=1;
            }
            
            /**
             * Increments the frequency of this sequence
             */
            public void incrementFrequency(){
            	this.frequency++;
            }
            
            /**
             * @return
             * 		the frequency of this sequence
             */
            public int getFrequency(){
            	return this.frequency;
            }
            
            /**
             * 
             * @return	The value held in this node. Used
             * as the key for comparisons.
             */
            public long getSequence(){
            	return this.value;
            }
            
            public String toString(){
            		
            	StringBuilder returnVal = new StringBuilder(sequenceLength);
            		
            	long mask;
                char next;
                for (int i=(sequenceLength-1)*2;i>=0;i-=2){
                         mask=3<<i;
                         next=codes[(int)(value&mask)>>i];
                         returnVal.append(next);
                 }
                    
                return returnVal.toString();
            }

            @Override
            public int compareTo(TreeObject that) {
                    
                    long diff = this.value - that.getSequence();
                    if (diff<0) return -1;
                    if (diff==0) return 0;
                    return 1;
            }
        }
 }
