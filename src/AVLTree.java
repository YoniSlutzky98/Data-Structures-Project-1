/**
 *
 * AVLTree
 *
 * An implementation of an AVL Tree with
 * distinct integer keys and info.
 *
 */

public class AVLTree {
	IAVLNode VIRTUAL_NODE = new AVLNode(-1, null, null, null, null);
	IAVLNode root, min, max;
	int count;
	
	/*
	 * Constructor for an AVL tree. Complexity O(1).
	 */
	public AVLTree() {
		this.root = this.min = this.max = VIRTUAL_NODE;
		this.count = 0;
	}
	
  /**
   * public boolean empty()
   *
   * Returns true if and only if the tree is empty.
   * Complexity O(1).
   */
  public boolean empty() {
	  return this.root == VIRTUAL_NODE;
  }

  /*
   * Helper function for search().
   * Returns the IAVLNode whose key is k, in the sub-tree whose root is node. 
   * If such IAVLNode doesn't exist in the sub-tree, returns null
   * Complexity O(log n). 
   */
  private IAVLNode nodeSearch(int k, IAVLNode node) {
	  if (node == VIRTUAL_NODE) {
		  return null;
	  }
	  if (node.getKey() == k) {
		  return node;
	  }
	  else if (node.getKey() > k) {
		  return nodeSearch(k, node.getLeft());
	  }
	  else {
		  return nodeSearch(k, node.getRight());
	  }
  }
  
 /**
   * public String search(int k)
   *
   * Returns the info of an item with key k if it exists in the tree.
   * otherwise, returns null.
   * Uses the nodeSearch helper function.
   * Complexity O(log n)
   */
  public String search(int k)
  {
	IAVLNode searchedNode = this.nodeSearch(k, this.root);
	if (searchedNode == null) {
		return null;
	}
	return searchedNode.getValue();
  }

  /**
   * public int insert(int k, String i)
   *
   * Inserts an item with key k and info i to the AVL tree.
   * The tree must remain valid, i.e. keep its invariants.
   * Returns the number of re-balancing operations, or 0 if no re-balancing operations were necessary.
   * A promotion/rotation counts as one re-balance operation, double-rotation is counted as 2.
   * Returns -1 if an item with key k already exists in the tree.
   */
   public int insert(int k, String i) {
	   this.count++; // Increase node count. 
	   
	   return 420;	// to be replaced by student code
   }

  /**
   * public int delete(int k)
   *
   * Deletes an item with key k from the binary tree, if it is there.
   * The tree must remain valid, i.e. keep its invariants.
   * Returns the number of re-balancing operations, or 0 if no re-balancing operations were necessary.
   * A promotion/rotation counts as one re-balance operation, double-rotation is counted as 2.
   * Returns -1 if an item with key k was not found in the tree.
   */
   public int delete(int k)
   {
	   this.count--; // Decrease node count.
	   return 421;	// to be replaced by student code
   }

   /**
    * public String min()
    *
    * Returns the info of the item with the smallest key in the tree,
    * or null if the tree is empty.
    * Complexity O(1)
    */
   public String min()
   {
	   return this.min.getValue();
   }

   /**
    * public String max()
    *
    * Returns the info of the item with the largest key in the tree,
    * or null if the tree is empty.
    * Complexity O(1).
    */
   public String max()
   {
	   return this.max.getValue();
   }

   /*
    * Helper function for keysToArray().
    * Inserts the keys inorder to arr.
    * Returns the pointer after making the insertions (so we could keep track of where
    * to insert).
    * Complexity O(n) (each node is visited once, constant work in each node)
    */
   private int inorderKeys(IAVLNode node, int[] arr, int pointer){
	   if (node.getLeft() != VIRTUAL_NODE) {
		   pointer = inorderKeys(node.getLeft(), arr, pointer);
	   }
	   arr[pointer] = node.getKey();
	   pointer++;
	   if (node.getRight() != VIRTUAL_NODE) {
		   pointer = inorderKeys(node.getRight(), arr, pointer);
	   }
	   return pointer;
   }
   
   
   /**
   * public int[] keysToArray()
   *
   * Returns a sorted array which contains all keys in the tree,
   * or an empty array if the tree is empty.
   * Complexity O(n).
   */
  public int[] keysToArray()
  {
	  if (this.empty()) {
		  return new int[] {};
	  }
	  int[] arr = new int[this.count];
	  this.inorderKeys(this.root, arr, 0);
	  return arr;
  }
 
  /*
   * Helper function for infoToArray().
   * Inserts the values inorder to arr.
   * Returns the pointer after making the insertions (so we could keep track of where
   * to insert).
   * Complexity O(n) (each node is visited once, constant work in each node).
   */
  private int inorderValues(IAVLNode node, String[] arr, int pointer){
	   if (node.getLeft() != VIRTUAL_NODE) {
		   pointer = inorderValues(node.getLeft(), arr, pointer);
	   }
	   arr[pointer] = node.getValue();
	   pointer++;
	   if (node.getRight() != VIRTUAL_NODE) {
		   pointer = inorderValues(node.getRight(), arr, pointer);
	   }
	   return pointer;
  }
  
  /**
   * public String[] infoToArray()
   *
   * Returns an array which contains all info in the tree,
   * sorted by their respective keys,
   * or an empty array if the tree is empty.
   * Complexity O(n).
   */
  public String[] infoToArray()
  {
	  if (this.empty()) {
		  return new String[] {};
	  }
	  String[] arr = new String[this.count];
	  this.inorderValues(this.root, arr, 0);
	  return arr;
  }

   /**
    * public int size()
    *
    * Returns the number of nodes in the tree.
    * Complexity O(1).
    */
   public int size()
   {
	   return this.count;
   }
   
   /**
    * public int getRoot()
    *
    * Returns the root AVL node, or null if the tree is empty
    * Complexity O(1).
    */
   public IAVLNode getRoot()
   {
	   if (this.empty()) {
		   return null;
	   }
	   return this.root;
   }
   
   /**
    * public AVLTree[] split(int x)
    *
    * splits the tree into 2 trees according to the key x. 
    * Returns an array [t1, t2] with two AVL trees. keys(t1) < x < keys(t2).
    * 
	* precondition: search(x) != null (i.e. you can also assume that the tree is not empty)
    * postcondition: none
    */   
   public AVLTree[] split(int x)
   {
	   return null; 
   }
   
   /**
    * public int join(IAVLNode x, AVLTree t)
    *
    * joins t and x with the tree. 	
    * Returns the complexity of the operation (|tree.rank - t.rank| + 1).
	*
	* precondition: keys(t) < x < keys() or keys(t) > x > keys(). t/tree might be empty (rank = -1).
    * postcondition: none
    */   
   public int join(IAVLNode x, AVLTree t)
   {
	   return -1;
   }

	/** 
	 * public interface IAVLNode
	 * ! Do not delete or modify this - otherwise all tests will fail !
	 */
	public interface IAVLNode{	
		public int getKey(); // Returns node's key (for virtual node return -1).
		public String getValue(); // Returns node's value [info], for virtual node returns null.
		public void setLeft(IAVLNode node); // Sets left child.
		public IAVLNode getLeft(); // Returns left child, if there is no left child returns null.
		public void setRight(IAVLNode node); // Sets right child.
		public IAVLNode getRight(); // Returns right child, if there is no right child return null.
		public void setParent(IAVLNode node); // Sets parent.
		public IAVLNode getParent(); // Returns the parent, if there is no parent return null.
		public boolean isRealNode(); // Returns True if this is a non-virtual AVL node.
    	public void setHeight(int height); // Sets the height of the node.
    	public int getHeight(); // Returns the height of the node (-1 for virtual nodes).
	}

   /** 
    * public class AVLNode
    *
    * If you wish to implement classes other than AVLTree
    * (for example AVLNode), do it in this file, not in another file. 
    * 
    * This class can and MUST be modified (It must implement IAVLNode).
    */
  public class AVLNode implements IAVLNode{
		int key, height; // Each node holds a key, a value, its height, its sons and its parent
		String value;
		IAVLNode left, right, parent;
		
		
		/*
		 * A constructor for an AVLNode. O(1) complexity.
		 * Special case - the node is a virtual one (hence its l and r sons are null)
		 */
		public AVLNode(int k, String v, IAVLNode l, IAVLNode r, IAVLNode p) {
			this.left = l;
			this.right = r;
			this.parent = p;
			if (l == null & r == null) {
				this.height = -1;
				this.key = -1;
			}
			else {
				this.height = Math.max(l.getHeight(), r.getHeight()) + 1;
				this.key = k;
				this.value = v;
			}
		}
	  
		/*
		 * Returns the key of an AVLNode. O(1) complexity.
		 */
	  	public int getKey()
		{
			return this.key;
		}
	  	
		/*
		 * Returns the value of an AVLNode. O(1) complexity.
		 */	  	
		public String getValue()
		{
			return this.value;
		}
		
		/*
		 * Sets the left child of an AVLNode. O(1) complexity.
		 */
		public void setLeft(IAVLNode node)
		{
			this.left = node;
		}
		
		/*
		 * Returns the left child of an AVLNode. O(1) complexity.
		 */
		public IAVLNode getLeft()
		{
			return this.left;
		}
		
		/*
		 * Sets the right child of an AVLNode. O(1) complexity.
		 */
		public void setRight(IAVLNode node)
		{
			this.right = node;
		}
		
		/*
		 * Returns the right child of an AVLNode. O(1) complexity.
		 */
		public IAVLNode getRight()
		{
			return this.right;
		}
		
		/*
		 * Sets the parent of an AVLNode. O(1) complexity.
		 */
		public void setParent(IAVLNode node)
		{
			this.parent = node;	
		}
		
		/*
		 * Returns the parent of an AVLNode. O(1) complexity.
		 */
		public IAVLNode getParent()
		{
			return this.parent;
		}
		
		/*
		 * Returns true if the node isn't virtual. O(1) complexity.
		 */
		public boolean isRealNode()
		{
			return this.key > -1;
		}
	    
		/*
		 * Sets the height of an AVLNode. O(1) complexity.
		 */
		public void setHeight(int height)
	    {
	    	this.height = height;
	    }
	    
		/*
		 * Returns the height of an AVLNode. O(1) complexity.
		 */
		public int getHeight()
	    {
	      return this.height;
	    }
  }

}
  
