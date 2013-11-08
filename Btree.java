public class Btree {
	BTreeNode root,current,sibling;
	

	private class BTreeNode {
		int degree,parent;
		int[] children;

	}

	private class TreeObject {
		long data;
		int count;
		
		public TreeObject(long data){
			this.data=data;
			count=1;
		}
	}

}
