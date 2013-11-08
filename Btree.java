public class Btree {
	BTreeNode root,current,sibling;
	public final int degree,k,nodeSize,metaSize=4;
	private int rootLocation;
	
	public Btree(int degree,int k){
		this.degree=degree;
		this.k=k;
		root=new BTreeNode();
		nodeSize=(2*k+2)*4+(2*k-1)*12;
	}
	
	public void insert(long value){
		current=root;
		
	}
	

	/**
	 *
	 *
	 */
	private class BTreeNode {
		private int parent,numItems,numChildren;
		public final int location;
		private int[] children;
		private TreeObject[] data;
	
		
		/**
		 * create the first node of an empty tree.
		 */
		public BTreeNode(){
			children=new int[degree*2];
			data=new TreeObject [degree*2-1];
			location=-1;//replace w/ expression for location in file
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
		
		/** read a node from disk (or cache, possibly)
		 * @param offset
		 */
		public BTreeNode(int offset){
			location=offset;
		}
		
		public void insert(long value){
			if(numItems==data.length) split(value);//wrong, does not account for duplicates
			int i=numItems;
			while (i>0&&value<data[--i].value) {
				data[i+1]=data[i];
			}
			if (data[i].value==value) data[i].count++;
			else data[i]=new TreeObject(value);
			numItems++;
		}

		private void split(long value) {
			// TODO Auto-generated method stub
			
		}

	}
	
	

	private class TreeObject implements Comparable<TreeObject>{
		private long value;		
		private int count;
		public final char[] codes={'a','c','g','t'};
		
		public TreeObject(long value){
			this.value=value;
			count=1;
		}
		
		public String toString(){
			long mask;
			char next;
			StringBuilder rval=new StringBuilder(k);
			for (int i=(k-1)*2;i>=0;i-=2){
				mask=3<<i;
				next=codes[(int)(value&mask)>>i];
				rval.append(next);
			}
			
			return rval.toString();
		}

		@Override
		public int compareTo(TreeObject that) {
			// TODO Auto-generated method stub
			long diff =this.value- that.value;
			if (diff<0) return -1;
			if (diff==0) return 0;
			return 1;
		}
	}

}
