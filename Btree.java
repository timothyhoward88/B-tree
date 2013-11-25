import java.util.LinkedList;
import java.util.Queue;



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
       
        //TODO: Throw IO exception in the constructor
        
        /**
         * 
         * @param degree
         * 		Degree of the B-Tree
         * @param seqLength
         * 		DNA sequence length (1 <= sequence length <= 31).
         * Size should be taken care of in the class creating the B-Tree.
         */
        public Btree(int degree, int seqLength){
                this.degree = degree;
                this.sequenceLength = seqLength;
                this.root = new BTreeNode(metaSize);	//Use metaSize as the offset for the first node.
                this.nodeSize=(2*degree+2)*4+(2*degree-1)*12;
        }
        
        /**
         * Used to deal with root insert/split operations, then acts
         * as a wrapper method to call the private method to recursively
         * traverse and insert.
         * @param value
         * 		the value to be inserted into the tree
         */
        public void insert(long value){
        	
        	//Insert if root node is the only leaf
        	if(root.numChildren == 0 && root.numItems < 2*degree-1){
        		root.insert(value);
        		return;
        	}
        	
        	//Create new root if root is full
        	if(root.numItems == 2*degree-1){
        		current = root;
        		TreeObject sentUp = current.getClimbingObject();
        		sibling = current.split();
        		root = new BTreeNode(sentUp, current, sibling);
        		current.setParent(root);
        		sibling.setParent(root);
        		for(int i = 0; i < sibling.numChildren; i++){
        			sibling.children[i].parentNode = sibling;
        		}
        	}
                insert(value, root);         
        }
        
        /**
         * Inserts the value into the tree
         * @param value
         * 		Value to be inserted
         * @param currNode
         * 		the current operating node
         */
        private void insert(long value, BTreeNode currNode){
        	current = currNode;
        	
        	//Base Case: Insertion into a leaf node
        	if(current.numChildren == 0 && current.numItems < 2*degree-1){
        		current.insert(value);
        		return;
        	}
        	
        	//Recursively traverse children until leaf node is found
        	if(current.numChildren > 0 && current.numItems < 2*degree-1){
        		insert(value, current.getPath(value));
        		return;
        	}
        	
        	//Sends the value to the parent node and continues insertion
        	if(current.numItems == 2*degree-1){
        		TreeObject sentUp = current.getClimbingObject();
        		sibling = current.split();
        		sibling.parentNode = current.parentNode;
        		current.parentNode.insertUp(sentUp, sibling);
        		
        		//Set the new node as the parent to its children
        		for(int i = 0; i < sibling.numChildren; i++){
        			sibling.children[1].parentNode = sibling;
        		}
        		
        		if(sentUp.value > value)
        			insert(value, current);
        		else
        			insert(value, sibling);
        	}	
        	
        }
        
        public void printTreeLevelOrder(){
        	
        	Queue<BTreeNode> q = new LinkedList<BTreeNode>();
        	q.add(root);
        	int nodeNum = 1;
        	
        	while(!q.isEmpty()){
        		BTreeNode temp = q.remove();
        		System.out.println("Node  " + nodeNum);
        		nodeNum++;
        		System.out.println(temp);
        		
        		int i = 0;
        		
        		while(i< temp.numChildren){
        			q.offer(temp.children[i]);
        			i++;
        		}
        	}
        	
        }
        
        public void printTreeInOrder(){
        	
        	
        	
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
                private BTreeNode[] children;//TODO: Modify for location not reference
                
                /*Objects containing long sequence value and frequency count*/
                private TreeObject[] data;
                
                //TODO: For Testing!
                private BTreeNode parentNode;
                
                /**
                 * Create the first node of an empty tree.
                 * @param location
                 * 		The offset of the node on the disk.
                 */
                public BTreeNode(int location){
                        this.children = new BTreeNode[degree*2];//TODO: Modify for location not reference
                        this.data = new TreeObject [degree*2-1];
                        this.location = location;//replace w/ expression for location in file
                        this.parentNode = null;//TODO: Modify for location not reference
                        numItems=numChildren=0;
                }
                
                /**create a new root when the former root is split.
                 * @param sentUp
                 * 		Data sent up
                 * @param child0
                 * 		Left child
                 * @param child1
                 * 		Right child
                 */
                //TODO: Modify for location not reference
                public BTreeNode(TreeObject sentUp,BTreeNode child0,BTreeNode child1){
                       this. children = new BTreeNode[degree*2];
                        this.children[0] = child0;
                        this.children[1] = child1;
                        this.numChildren = 2;
                        this.parentNode = null;
                        
                        this.data = new TreeObject [degree*2-1];
                        this.data[0] = sentUp; //new TreeObject(sentUp);
                        this.numItems = 1;
                        
                        location=-1;
                }
                
                /** create a new sibling node when a full node is split.
                 * @param value
                 * @param myHalf
                 * @param myKids
                 */
                //TODO: Modify for location not reference
                public BTreeNode(TreeObject[] myHalf,BTreeNode[] myKids){
                		this.children = new BTreeNode[degree*2];//TODO: Modify for location not reference
                		this.data = new TreeObject [degree*2-1];
                		this.numItems = degree-1;
                		numChildren = 0;
                		
                		for(int i = 0; i<myKids.length; i++){
                		
                			if(myKids[i] != null)
                				this.numChildren++;
                		}
                		//TODO: Check how the array is going to be passed in to
                		// 		Properly increment the counts and not count null
                		//		values.
                		for(int i = 0; i<myHalf.length; i++){
                			this.data[i] = myHalf[i];
                		}
                		
                		for(int i = 0; i<myKids.length; i++){
                			this.children[i] = myKids[i];	
                		}
                		
                        location=-1;
                }
                
//                /** read a node from disk (or cache, possibly)
//                 * @param offset
//                 */
//                public BTreeNode(int offset){
//                        location=offset;
//                }
            
                /**
                 * Inserts a value into an existing node when a
                 * split occurs.
                 * @param sentUp
                 * 		The TreeObject being sent up to this node
                 * @param rightChild
                 * 		The new right child node
                 */
                public void insertUp(TreeObject sentUp, BTreeNode rightChild){
                	
                	int i = numItems;
                   
                	while (i > 0 && sentUp.value < data[i-1].value) {
                            data[i] = data[i-1];
                            
                            //Move the child nodes accordingly if this isn't a leaf node
                           this.children[i+1] = this.children[i];
                    }
                    data[i] = sentUp;
                    children[i+1] = rightChild;
                    this.numItems++;
                    this.numChildren++;
                }
                
                /**
                 * Inserts a given value into the BTree
                 * @param value
                 * 		The long value to be inserted
                 * @return
                 * 		True if the value was inserted, False
                 * 	if the Node is full
                 */
                public void insert(long value){
                	
                	if(numItems == 0){
                		data[0] = new TreeObject(value);
                		numItems++;
                		return;
                	}
                	
                        //Increment frequency if already exists
                		for(int i = 0; i < numItems; i++){
                			
                			if(value == data[i].value){
                				data[i].incrementFrequency();
                				return;
                			}
                		}
                		
                		int i = numItems;
                        while (i > 0 && value < data[i-1].value) {
                                data[i] = data[i-1];
                                
                                //Move the child nodes accordingly if this isn't a leaf node
                                if(numChildren != 0)
                                	this.children[i+1] = this.children[i];
                                i--;
                        }
                        data[i] = new TreeObject(value);
                        this.numItems++;
                }

                /**
                 * Clears the second half of the node for the
                 * split. 
                 * @return
                 * 		The new node created from the split.
                 */
                //TODO: Modify to reference location and references
                public BTreeNode split() {
                      TreeObject[] temp = new TreeObject[degree*2-1];
                      BTreeNode[] newChildren = new BTreeNode[degree*2];
                      
                      //Set the first value so we can assign both arrays in the for loop
                      newChildren[0] = this.children[degree];
                      
                      
                      
                      //Set the new reference and data arrays
                      for(int i = degree; i < numItems; i++){
                    	  temp[i-degree] = data[i];
                    	  newChildren[i-degree+1] = this.children[i+1];
                    	  this.data[i] = null;
                    	  this.children[1 + i] = null;
                      }
                      
                    //Clear the spots in the array that didn't get cleared in the loop.
                      this.children[degree] = null;
                      this.data[degree-1] = null;
                      this.numItems = degree-1;
                      
                      int newChildCount = 0;
                      for(int i = 0; i<children.length; i++){
                    	  if(children[i] != null)
                    		  newChildCount++;
                      }
                      this.numChildren = newChildCount;
                                           
                      BTreeNode newNode = new BTreeNode(temp, newChildren);
                      newNode.parentNode = this.parentNode;
                      
                      return newNode;  
                }
                
                /**
                 * 
                 * @return
                 * 		The middle object getting sent to the parent
                 * node
                 */
                public TreeObject getClimbingObject(){
                	return this.data[degree-1];
                }
                
                //TODO: Modify to reference offset location
                public void setParent(BTreeNode parent){
                	this.parentNode = parent;
                }
                
                /**
                 * 
                 * @param value
                 * 		value being compared for insertion
                 * @return
                 * 		child node to be checked next
                 */
               public BTreeNode getPath(long value){
            	   
            	   int i = 0;
            	   BTreeNode path = children[0];
            	   
            	   while(i< numChildren && data[i] != null){
            		   
            		if(data[i].value < value){ 
            			path = children[i+1];
            			i++;
            		}
            		else
            			break;
            	   }
            	   
            	   return path;
               }
               
               
               public String toString(){
            	   String retVal = "";
            	   for(int i = 0; i < numItems; i++){
            		   retVal += "\t" + data[i].toString();
            	   }
            	   
            	   retVal += "\n # items \t # children \n    " + numItems + "\t\t   " + numChildren;
            	   return retVal;
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
            public final char[] codes = {'a','c','g','t'};
            
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
