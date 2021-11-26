/**
 *
 * AVLTree
 *
 * An implementation of an AVL Tree with
 * distinct integer keys and info.
 *
 */

public class AVLTree {
	IAVLNode VIRTUAL_NODE = new AVLNode(-1, null, null, null, null); // VE's parents won't be maintained
	IAVLNode root, min, max;
	
	/*
	 * Constructor for an AVL tree. Complexity O(1).
	 */
	public AVLTree() {
		this.root = this.min = this.max = VIRTUAL_NODE;
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

  /**
   * Helper function for search().
   * Returns the IAVLNode whose key is k, in the sub-tree whose root is node. 
   * If such IAVLNode doesn't exist in the sub-tree, returns the last node encountered.
   * Special case - the tree is empty, for which we'll return null.
   * Complexity O(log n). 
   */
  private IAVLNode nodeSearch(int k, IAVLNode node) {
	  if (node == VIRTUAL_NODE) { // Edge case for when the tree is empty.
		  return null;
	  }
	  if (node.getKey() == k) { // Found k
		  return node;
	  }
	  else if (node.getKey() > k) { // k is lesser than current node
		  if (node.getLeft() == VIRTUAL_NODE) { // If k doesn't exist in the tree
			  return node;
		  }
		  else {
			  return nodeSearch(k, node.getLeft()); // Go left
		  }
	  }
	  else { // k is greater than current node
		  if (node.getRight() == VIRTUAL_NODE) { // If k doesn't exist in the tree 
			  return node;
		  }
		  else {
			  return nodeSearch(k, node.getRight()); // Go right
		  }
	  }
  }

  private boolean isLeftSon(IAVLNode targetNode) {
	  IAVLNode targetNodeParent = targetNode.getParent();
	  if (targetNodeParent == null) { // node is root
		  return false;
	  }
	  return targetNodeParent.getLeft().getKey() == targetNode.getKey(); // return true if the target node is a left son of his parent, false otherwise.
  }

	private boolean isRightSon(IAVLNode targetNode) {
		IAVLNode targetNodeParent = targetNode.getParent();
		if (targetNodeParent == null) { // node is root
			return false;
		}
		return targetNodeParent.getRight().getKey() == targetNode.getKey(); // return true if the target node is a right son of his parent, false otherwise.
	}

	private boolean isLeaf(IAVLNode targetNode) {
	  		return targetNode.getRight() == VIRTUAL_NODE && targetNode.getLeft() == VIRTUAL_NODE;
	}

	private boolean isRoot(IAVLNode targetNode) {
		return targetNode.getParent() == null;
	}

	private boolean isUnaryRight(IAVLNode targetNode) {
	  return targetNode.getRight() != VIRTUAL_NODE && targetNode.getLeft() == VIRTUAL_NODE;
	}

	private boolean isUnaryLeft(IAVLNode targetNode) {
		return targetNode.getRight() == VIRTUAL_NODE && targetNode.getLeft() != VIRTUAL_NODE;
	}


	/**
	 * @ret IAVL Node
	 * @abst finds the successor of a given IAVL Node
	 * Complexity O(log n).
	 */
	private IAVLNode findSuccessor(IAVLNode node) {
		if (node == this.max) { // this node is the maximum so it has not successor
			return null;
		}
		if (node.getRight() != VIRTUAL_NODE) { // if our node has right son, it is not max and his successor is in its right subtree.
			node = node.getRight();
			while (node.getLeft() != VIRTUAL_NODE) {
				node = node.getLeft();
			}
			return node;
		}

		else { // has no right children
			IAVLNode parentNode = node.getParent();
			while (parentNode != VIRTUAL_NODE && node == parentNode.getRight()) { // go up the tree to find the first parent that our node is it's left son
				node = parentNode;
				parentNode = node.getParent();
			}
			return parentNode;
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
	if (this.empty()) { // If the tree is empty
		return null;
	}
	IAVLNode searchedNode = this.nodeSearch(k, this.root);
	if (searchedNode.getKey() != k) { // To make sure the node exists in the tree 
		return null;
	}
	return searchedNode.getValue();
  }

  /*
   * Helper function for insert(), delete(), join() & split.
   * Given a node, corrects its size.
   * Complexity O(1).
   */
  private void sizeCorrect(IAVLNode node) {
	  node.setSize(node.getLeft().getSize() + node.getRight().getSize() + 1);
  }
  
  
  /*
   * Helper function for insertRebalance(), deleteRebalance() & joinRebalance(). 
   * Given a child node, rotates the child and its parent.
   * The function also handles height changes.
   * Complexity O(1).
   */
  private void rotate(IAVLNode node) {
	  /* TO DO - fix edge case for when p is root (p's parent is null, need to
	   * update this.root
	   */
	  IAVLNode p = node.getParent();
	  if (p.getRight() == node) { // If node is on the right of p.
		  p.setRight(node.getLeft());
		  p.getRight().setParent(p);
		  node.setLeft(p);
	  }
	  else { // If node is on the left of p.
		  p.setLeft(node.getRight());
		  p.getLeft().setParent(p);
		  node.setRight(p);
	  }
	  // Fix parent of p and parent of node.
	  if (!this.isRoot(p)) { // If parent isn't root, fix its parent's son
		  if (p.getParent().getLeft() == p) {
			  p.getParent().setLeft(node);
		  }
		  else {
			  p.getParent().setRight(node);
		  }
	  }
	  else { // Otherwise, set the node to be the root
		  this.root = node;
	  }
	  node.setParent(p.getParent());
	  p.setParent(node);
	  // Fix heights of node and of parent.
	  p.setHeight(1 + Math.max(p.getLeft().getHeight(), p.getRight().getHeight()));
	  node.setHeight(1 + Math.max(node.getLeft().getHeight(), p.getRight().getHeight()));
	  // Fix sizes of node and of parent.
	  this.sizeCorrect(p);
	  this.sizeCorrect(node);
  }
  
  /*
   * Helper function for insert().
   * Given inserted node, re-balance the tree up-to the root.
   * Return # of re-balance operations made.
   * Handles corrections of heights and sizes of affected nodes.
   * Complexity O(logn). 
   */
  private int insertRebalance(IAVLNode node) {
	IAVLNode p = node.getParent();
	if (p == null) { // Got to the root, no more re-balances needed.
		return 0;
	}
	else {
		if (p.getHeight() == node.getHeight()) { // If there exists a problem in the tree 
			if (p.getLeft() == node) { // If the inserted node was to the left
				IAVLNode other = p.getRight();
				if (p.getHeight() == other.getHeight() + 1) { // If the other node has a diff. of 1
					p.setHeight(p.getHeight() + 1); // Promote parent and re-balance upwards.
					this.sizeCorrect(p); // Correct size of parent.
					return 1 + this.insertRebalance(p);
				}
				else {
					IAVLNode l = node.getLeft();
					IAVLNode r = node.getRight();
					// If the node is a (1,2) node
					if (node.getHeight() == l.getHeight() + 1 && node.getHeight() == r.getHeight() + 2) {
						// Rotate to the right, demote parent, promote node. 
						this.rotate(node);
						return 3 + this.insertRebalance(node); // Problem solved, climbing up to fix sizes.
					}
					else { // If the node is a (2,1) node
						// Rotate r twice (to the left and to the right), 
						// demote parent and node, promote r.
						this.rotate(r); // Rotate to the left.
						this.rotate(r); // Rotate to the right.
						return 5 + this.insertRebalance(r); // Problem solved, climbing up to fix sizes.
					}
				}
			}
			else { // If the inserted node was to the left
				IAVLNode other = p.getLeft();
				if (p.getHeight() == other.getHeight() + 1) { // If the other node has a diff. of 1
					p.setHeight(p.getHeight() + 1); // Promote parent and re-balance upwards
					this.sizeCorrect(p); // Correct size of parent.
					return 1 + this.insertRebalance(p);
				}
				else {
					IAVLNode l = node.getLeft();
					IAVLNode r = node.getRight();
					// If the node is a (2,1) node
					if (node.getHeight() == l.getHeight() + 2 && node.getHeight() == r.getHeight() + 1) {
						// Rotate to the left, demote parent, promote node. 
						this.rotate(node);
						return 3 + this.insertRebalance(node); // Problem solved, climbing up to fix sizes.
					}
					else { // If the node is a (1,2) node 
						// Rotate l twice (to the right and to the left), 
						// demote parent and node, promote l.
						this.rotate(l); // Rotate to the right.
						this.rotate(l); // Rotate to the left.
						return 5 + this.insertRebalance(l); // Problem solved, climbing up to fix sizes.
					}
				}
			}
		}
		else { // If there's no problem
			this.sizeCorrect(p); // Correct size of parent.
			return 0 + this.insertRebalance(p); // No problem, climing up to fix sizes.
		}
	}
 }
  
  /**
   * public int insert(int k, String i)
   *
   * Inserts an item with key k and info i to the AVL tree.
   * The tree must remain valid, i.e. keep its invariants.
   * Returns the number of re-balancing operations, or 0 if no re-balancing operations were necessary.
   * A promotion/rotation counts as one re-balance operation, double-rotation is counted as 2.
   * Returns -1 if an item with key k already exists in the tree.
   * Handles height and size corrections for the nodes in the tree after the insertion.
   * Complexity O(logn).
   */
   public int insert(int k, String i) {
	   if (this.root == VIRTUAL_NODE) { // Special case for insertion when tree is empty.
		   this.root = new AVLNode(k, i, VIRTUAL_NODE, VIRTUAL_NODE, null);
		   return 0;
	   }
	   IAVLNode parent = nodeSearch(k, this.root); // Find where to insert
	   if (parent.getKey() == k) { // Make sure key isn't in tree
		   return -1;
	   }
	   else {
		   boolean notLeaf = (parent.getLeft() != VIRTUAL_NODE | parent.getRight() != VIRTUAL_NODE);
		   IAVLNode child = new AVLNode(k, i, VIRTUAL_NODE, VIRTUAL_NODE, parent); // Create new node
		   if (parent.getKey() > k) { // Insert on the left side of parent
			   parent.setLeft(child); 
		   }
		   else {
			   parent.setRight(child);
		   }
		   if (this.max.getKey() < child.getKey()) { // Update max
			   this.max = child;
		   }
		   if (this.min.getKey() > child.getKey()) { // Update min
			   this.min = child;
		   }
		   if (notLeaf) { // If parent wasn't a leaf, the tree doesn't need re-balancing 
			   return 0;
		   }
		   else { // Otherwise, re-balance it
			   int rebalances = this.insertRebalance(child);
			   return rebalances;
		   }
	   } 	   
   }

	/**
	 * @pre: parentNode != null && targetNode != null
	 Rerturn the height differences between two nodes
	 */
   private int nodeDistance(IAVLNode node1, IAVLNode node2) {
	   return Math.abs(node1.getHeight() - node2.getHeight());
   }

   /**
		Cases to rebalance:
			1. (2,2)
			2. (3,1) -> (1,1)
			3. (3,1) -> (2,1)
			4. (3,1) -> (1,2)
	*/
   private int deleteRebalance(IAVLNode rebalanceNode) {
	   if (rebalanceNode == null) { // got to the root
		   return 0;
	   }

	   IAVLNode leftNode = rebalanceNode.getLeft();
	   IAVLNode rightNode = rebalanceNode.getRight();

	   int leftDiff = nodeDistance(leftNode, rebalanceNode);
	   int rightDiff = nodeDistance(rightNode, rebalanceNode);

	   if (leftDiff == 2 && rightDiff == 2) { // case 1
		   this.sizeCorrect(rebalanceNode);
		   return 1 + deleteRebalance(rebalanceNode.getParent()); // Problem is either fixed or moved up
	   }

	   else if (leftDiff == 3 && rightDiff == 1) { // the base is (3,1)
		   IAVLNode rightRightNode = rightNode.getRight();
		   IAVLNode rightLeftNode = rightNode.getRight();

		   int rightRightDiff = nodeDistance(rightNode, rightRightNode);
		   int rightLeftDiff = nodeDistance(rightNode, rightLeftNode);

		   if (rightLeftDiff == 1 && rightRightDiff == 1) { // case 2 [(3,1) -> (1,1)] - rotate left, demote rebalanceNode, promote its right son
			   this.rotate(rightNode);
			   return 3 + deleteRebalance(rightNode); // Problem solved, climbing up to fix sizes.
		   }
		   else if (rightLeftDiff == 2 && rightRightDiff == 1) { // case 3 [(3,1) -> (2,1)]- rotate left, demote z twice
			   this.rotate(rightNode);
			   return 3 + deleteRebalance(rightNode); // Problem is either fixed or moved up
		   }
		   else { // case 4 [(3,1) -> (1,2)] - double rotation (we trust here that the previous tree was correct)
			   this.rotate(rightLeftNode);
			   this.rotate(rightLeftNode);
			   return 5 + deleteRebalance(rightLeftNode);
		   }
	   }

	   else if (leftDiff == 1 && rightDiff == 3) { // now base id (1,3) which is symmetric
		   IAVLNode leftRightNode = leftNode.getRight();
		   IAVLNode leftLeftNode = leftNode.getLeft();

		   int leftRightDiff = nodeDistance(leftNode, leftRightNode);
		   int leftLeftDiff = nodeDistance(leftNode, leftLeftNode);

		   if (leftRightDiff == 1 && leftLeftDiff == 1) { // Symmetric case 2 [(3,1) -> (1,1)] - rotate right, demote rebalanceNode, promote its left son
			   this.rotate(leftNode);
			   return 3 + deleteRebalance(leftNode); // Problem solved, climbing up to fix sizes.
		   }

		   else if (leftRightDiff == 2 && leftLeftDiff == 1) { // case 3 [(3,1) -> (2,1)] - rotate right, demote twice
			   this.rotate(leftNode);
			   return 3 + deleteRebalance(leftNode); // Problem is either fixed or moved up
		   }

		   else { // case 4 [(3,1) -> (1,2)] - double rotation (we trust here that the previous tree was correct)
			   this.rotate(leftRightNode);
			   this.rotate(leftRightNode);
			   return 5 + deleteRebalance(leftRightNode);
		   }
	   }

	   else { // No problem, climb up to fix sizes
		   this.sizeCorrect(rebalanceNode);
		   return deleteRebalance(rebalanceNode.getParent());
	   }

   }

  /**
   * public int delete(int k)
   *
   * Deletes an item with key k from the binary tree, if it is there.
   * The tree must remain valid, i.e. keep its invariants.
   * Returns the number of re-balancing operations, or 0 if no re-balancing operations were necessary.
   * A promotion/rotation counts as one re-balance operation, double-rotation is counted as 2.
   * Returns -1 if an item with key k was not found in the tree.
   * Deletion 2 easy cases: 1. leaf - simple removal; 2. unary - bypass
   * Deletion complicated case: "healthy" node - find its successor (it has a right node!), replace the node with its
   * successor and bypass its original place in the tree (unary node).
   */
   public int delete(int k)
   {
	   if (this.root == VIRTUAL_NODE) { // Special case when the tree is empty.
		   return -1;
	   }

	   IAVLNode targetNode = nodeSearch(k, this.root); // find the node to delete
	   if (targetNode.getKey() != k) { // if key does not exist, return -1
		   return -1;
	   }

	   if (isLeaf(targetNode)) { // target node is a leaf
		   if (isRoot(targetNode)) {
			   this.root = VIRTUAL_NODE;
			   this.max = VIRTUAL_NODE;
			   this.min = VIRTUAL_NODE;
			   return 0;
		   }

		   IAVLNode targetNodeParent = targetNode.getParent(); // target node is not a root

		   if (isLeftSon(targetNode)) { // target node is a left son (min need to be checked)
			   targetNodeParent.setLeft(VIRTUAL_NODE); // bypass target node
			   if (targetNode == this.min) {
				   this.min = targetNodeParent; // target node is a leaf and a left son
			   }
			   return 0;
		   }

		   else if (targetNode.getKey() == targetNodeParent.getRight().getKey()) { // target node is a right son (max need to be checked)
			   targetNodeParent.setRight(VIRTUAL_NODE);
			   if (targetNode == this.max) {
				   this.max = targetNodeParent; // target node is a leaf and a right son
			   }
			   return 0;
		   }
	   }

	   else if (isUnaryRight(targetNode)) { // target node has only right son (min must be checked)
		   if (isRoot(targetNode)) {
			   this.root = targetNode.getRight();
			   this.min = targetNode.getRight(); // target node is a root that has only right sub-tree
		   }

		   else { // target node is not a root
			   IAVLNode targetNodeParent = targetNode.getParent();
			   if (isLeftSon(targetNode)) { // target node is a left son
				   targetNodeParent.setLeft(targetNode.getRight()); // bypass target node by setting its son as the new left son of his parent

				   if (this.min == targetNode) { // target node is a left son that and has only right son
					   IAVLNode newMinNode = targetNode.getRight();
					   while (newMinNode.getLeft() != VIRTUAL_NODE) { // find the minimum in right sub-tree
						   newMinNode = newMinNode.getLeft();
					   }
					   this.min = newMinNode;
				   }
			   }

			   else { // target node is a right son (no need to check min or max)
				   targetNodeParent.setRight(targetNode.getRight()); // bypass target node by setting its son as the new right son of his parent
			   }
		   }
		   return 0;
	   }

	   else if (isUnaryLeft(targetNode)) { // target node has only left son
		   if (isRoot(targetNode)) {
			   this.root = targetNode.getLeft();
			   this.max = targetNode.getLeft(); // target node is a root that has only left sub-tree
		   }


		   else { // target node is not a root
			   IAVLNode targetNodeParent = targetNode.getParent();
			   if (isLeftSon(targetNode)) { // target node is a left son (no need to check min max)
				   targetNodeParent.setLeft(targetNode.getLeft());
			   }

			   else { // target node is a right son (max must be checked)
				   targetNodeParent.setRight(targetNode.getLeft());
				   if (this.max == targetNode) {
					   IAVLNode newMaxNode = targetNode.getLeft();
					   while (newMaxNode.getRight() != VIRTUAL_NODE) {
						   newMaxNode = newMaxNode.getRight();
					   }
					   this.max = newMaxNode;
				   }
			   }

		   }
		   return 0;
	   }

	   else { // Complicated case - target node has two sons
		   IAVLNode successorNode = findSuccessor(targetNode); // find the successor
		   delete(successorNode.getKey());

		   // Now I want to make the successor to become the target node
		   successorNode.setRight(targetNode.getRight()); // set the successor right son
		   successorNode.setRight(targetNode.getLeft()); // set the successor left son left son

		   if (targetNode.getParent() != null) {
			   IAVLNode parentNode = targetNode.getParent(); // target node is not the root
			   if (parentNode.getRight() == targetNode) { // set the successor as the parent new son
				   parentNode.setRight(successorNode);
			   }

			   else {  // set the successor as the parent's new son
				   parentNode.setLeft(successorNode);
			   }
		   }
	   }








	   return 421;	// to be replaced by student code
   }

   /**
    * public String min()
    *
    * Returns the info of the item with the smallest key in the tree,
    * or null if the tree is empty.
    * Complexity O(1).
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
    * Complexity O(n) (each node is visited once, constant work in each node).
    */
   private int inorderKeys(IAVLNode node, int[] arr, int pointer){
	   if (node.getLeft() != VIRTUAL_NODE) {
		   pointer = this.inorderKeys(node.getLeft(), arr, pointer);
	   }
	   arr[pointer] = node.getKey();
	   pointer++;
	   if (node.getRight() != VIRTUAL_NODE) {
		   pointer = this.inorderKeys(node.getRight(), arr, pointer);
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
	  int[] arr = new int[this.root.getSize()];
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
		   pointer = this.inorderValues(node.getLeft(), arr, pointer);
	   }
	   arr[pointer] = node.getValue();
	   pointer++;
	   if (node.getRight() != VIRTUAL_NODE) {
		   pointer = this.inorderValues(node.getRight(), arr, pointer);
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
	  String[] arr = new String[this.root.getSize()];
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
	   return this.root.getSize();
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
   
   /*
    * Helper function for join().
    * Re-balances the tree upwards and returns the number of re-balance operations.
    * Complexity O(|tree.rank - t.rank| + 1)
    */
   private int joinRebalance(IAVLNode node) {
	   IAVLNode p = node.getParent();
	   if (p == null) { // If node == root, we're done
		   return 0;
	   }
	   if (node.getHeight() != p.getHeight()) { // If we're in balance, we're done
		   sizeCorrect(p); // Correct size of parent.
		   return 0 + insertRebalance(p); // Problem solved, climbing up to fix sizes.
	   }
	   else {
		   // Get the sibling of the node
		   IAVLNode other;
		   if (p.getLeft() == node) {
			   other = p.getRight();
		   }
		   else {
			   other = p.getLeft();
		   }
		   // If parent is (0,1)/(1,0), or if parent is (0,2)/(2,0) node is (1,2)/(2,1), use insertRebalance()
		   if (p.getHeight() == other.getHeight() + 1 | 
				   !(node.getLeft().getHeight() == node.getRight().getHeight())) {
			   return insertRebalance(node);
		   }
		   else { // Otherwise, rotate on the node and promote it, than continue fixing up
			   rotate(node);
			   return 2 + joinRebalance(node);
		   }
	   }
   }   
   
   /**
    * public int join(IAVLNode x, AVLTree t)
    *
    * joins t and x with the tree. 	
    * Returns the complexity of the operation (|tree.rank - t.rank| + 1).
	*
	* precondition: keys(t) < x < keys() or keys(t) > x > keys(). t/tree might be empty (rank = -1).
    * postcondition: none
    * Complexity O(|tree.rank - t.rank| + 1).
    */   
   public int join(IAVLNode x, AVLTree t)
   {
	   // Edge cases:
	   if (this.empty() & t.empty()) { // This is an extreme edge case, counting "init." of tree with x 
		   return 1; // Not actually creating a tree - we couldn't ref. it so it's a waste.
	   }
	   if (this.empty()) { // Tree is empty - insert x to t.
		   return t.insert(x.getKey(), x.getValue());
	   }
	   if (t.empty()) { // T is empty - insert x to tree.
		   return this.insert(x.getKey(), x.getValue());
	   }
	   
	   if (this.root.getHeight() < t.root.getHeight()) { // We want tree to have the greater rank.
		   return t.join(x, this);
	   }
	   IAVLNode b = this.getRoot();
	   IAVLNode a = t.getRoot();
	   int cnt = 0;
	   if (b.getKey() > x.getKey()) { // Bigger tree on right, smaller tree on left.
		   // Get to the first node on the left vertex of tree whose rank isn't greater than the root of t 
		   while (b.getHeight() > a.getHeight()) { 
			   b = b.getLeft();
			   cnt++;
		   }
		   // Fix pointer of the tree nodes, and fix height of x
		   x.setParent(b.getParent());
		   x.setLeft(a);
		   a.setParent(x);
		   b.getParent().setLeft(x);
		   x.setRight(b);
		   b.setParent(x);
		   x.setHeight(a.getHeight()+1);
	   }
	   else { // Bigger tree on left, smaller tree on right.
		   // Get to the first node on the right vertex of tree whose rank isn't greater than the root of t 
		   while (b.getHeight() > a.getHeight()) { 
			   b = b.getRight();
			   cnt++;
		   }
		   // Fix pointer of the tree nodes, and fix height of x
		   x.setParent(b.getParent());
		   x.setRight(a);
		   a.setParent(x);
		   b.getParent().setRight(x);
		   x.setLeft(b);
		   b.setParent(x);
		   x.setHeight(a.getHeight()+1);
	   }
	   // Correct sizes and re-balance if needed
	   this.sizeCorrect(x); 
	   this.joinRebalance(x);
	   return cnt;	   
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
    	public void setSize(int size); // Sets the size of the sub-tree rooted by the node.
    	public int getSize(); // Returns the size of the sub-tree rooted by the node.
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
	  	// Each node holds a key, a value, its height, its size, its sons and its parent
	  	// height(node) = max(height(left), height(right)) + 1
	  	// size(node) = 1 + size(left) + size(right)
	  	int key, height, size;  
		String value;
		IAVLNode left, right, parent;
		
		
		/*
		 * A constructor for an AVLNode. O(1) complexity.
		 * Special case - the node is virtual (hence its size is 0, its height is -1 and
		 * its l and r sons are null)
		 */
		public AVLNode(int k, String v, IAVLNode l, IAVLNode r, IAVLNode p) {
			this.left = l;
			this.right = r;
			this.parent = p;
			if (l == null & r == null) { // TODO: Check with yoni why only one '&'?
				this.height = -1;
				this.key = -1;
				this.size = 0;
			}
			else {
				this.height = Math.max(l.getHeight(), r.getHeight()) + 1; // TODO: what if one of r or l is null
				this.size = 1 + l.getSize() + r.getSize();
				this.key = k; // TODO: what if k is null
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
		
		/*
		 * Sets the size of the sub-tree rooted by an AVLNode. O(1) complexity.
		 */
		public void setSize(int size) {
			this.size = size;
		}
		
		/*
		 * Returns the size of the sub-tree rooted by an AVLNode. O(1) complexity.
		 */
		public int getSize() {
			return this.size;
		}
  }

	static void print_mat(String[][] mat) {
		int height = mat.length;
		int width = mat[0].length;
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				if (mat[i][j] != null) {
					System.out.print(mat[i][j]);
				} else {
					System.out.print(" ");
				}
				//System.out.print(mat[i][j] + " ");
			}
			System.out.println();

		}
	}

	static void fill_mat(AVLTree.IAVLNode root, String[][] mat, int i, int j) {
		if (!root.isRealNode()) {
			return;
		}
		int height = root.getHeight() + 1;
		int width = 6 * (int) Math.pow(2, height);
		mat[i][j + width / 2] = Integer.toString(root.getKey());
		fill_mat(root.getLeft(), mat, i + 2, j);
		fill_mat(root.getRight(), mat, i + 2, j - 1 + width / 2);
	}

	static void print_tree(AVLTree tree) {
		if (tree.empty()) {
			System.out.println("empty tree lol");
			return;
		}
		AVLTree.IAVLNode root = tree.getRoot();
		int height = root.getHeight() + 1;
		int width = 6 * (int) Math.pow(2, height);
		String[][] mat = new String[2 * height][width];
		fill_mat(root, mat, 0, 0);
		print_mat(mat);

	}

  public static void main(String [] args) {
	  AVLTree myTree = new AVLTree();
	  myTree.insert(1, "hello");
	  myTree.insert(4, "hello");
	  myTree.insert(7, "hello");
	  myTree.insert(3, "asd");
	  myTree.insert(0, "asd");
	  myTree.insert(2, "asd");
	  myTree.insert(8, "asd");
	  print_tree(myTree);

  }

}
  
