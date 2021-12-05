import java.util.Random;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
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
	IAVLNode root;
	
	/*
	 * Constructor for an AVLTree. Complexity O(1).
	 */
	public AVLTree() {
		this.root = VIRTUAL_NODE;
	}

	/*
	 * Alternative constructor for an AVLTree receiving an IAVLNode to be used as the root. 
	 * Complexity O(1).
	 */
	public AVLTree(IAVLNode root) { 
		this.root = root;
	}
	
	/**
	 * public boolean empty()
	 *
	 * Returns true if and only if the tree is empty.
	 * Complexity O(1).
	 */
	public boolean empty() {
	  return !this.root.isRealNode();
	}

	/**
	 * Helper function for search().
	 * Returns the IAVLNode whose key is k, in the sub-tree whose root is node. 
	 * If such IAVLNode doesn't exist in the sub-tree, returns the last node encountered.
	 * Special case - the tree is empty, for which we'll return null.
	 * Complexity O(log n). 
	 */
	private IAVLNode nodeSearch(int k, IAVLNode node) {
		if (!node.isRealNode()) { // Edge case for when the tree is empty.
			return null;
		}
		if (node.getKey() == k) { // Found k
			return node;
		}
		else if (node.getKey() > k) { // k is lesser than current node
			if (!node.getLeft().isRealNode()) { // If k doesn't exist in the tree
				return node;
			}
			else {
				return nodeSearch(k, node.getLeft()); // Go left
			}
		}
		else { // k is greater than current node
			if (!node.getRight().isRealNode()) { // If k doesn't exist in the tree
				return node;
			}
			else {
				return nodeSearch(k, node.getRight()); // Go right
			}
		}
	}

	/*
	 * Helper function for delete().
	 * Checks if received node is a left son of its parent.
	 * Complexity O(1).
	 */
	private boolean isLeftSon(IAVLNode node) {
		IAVLNode parent = node.getParent();
		if (parent == null) { // node is root
			return false;
		}
		return parent.getLeft().getKey() == node.getKey(); // return true if the target node is a left son of his parent, false otherwise.
	}
	
	/*
	 * Helper function for delete().
	 * Checks if received node is a leaf.
	 * Complexity O(1).
	 */
	private boolean isLeaf(IAVLNode node) {
	  		return !node.getLeft().isRealNode() && !node.getRight().isRealNode();
	}

	/*
	 * Helper function for delete().
	 * Checks if received node is the root of the tree.
	 * Complexity O(1).
	 */
	private boolean isRoot(IAVLNode node) {
		return node.getParent() == null;
	}
	
	/*
	 * Helper function for delete().
	 * Checks if received node is an unary node with a left son.
	 * Complexity O(1).
	 */
	private boolean isUnaryLeft(IAVLNode node) {
		return node.getLeft().isRealNode() && !node.getRight().isRealNode();
	}
	
	/*
	 * Helper function for delete().
	 * Checks if received node is an unary node with a right son.
	 * Complexity O(1).
	 */
	private boolean isUnaryRight(IAVLNode node) {
		return !node.getLeft().isRealNode() && node.getRight().isRealNode();
	}

	/*
	 * Helper function for delete().
	 * Returns the successor of the received node (if it exists). 
	 * Complexity O(log n).
	 */
	private IAVLNode findSuccessor(IAVLNode node) {
		if (node == this.root.getMax()) { // this node is the maximum so it has not successor
			return null;
		}
		if (node.getRight().isRealNode()) { // if a right son exists, the successor is in the right sub-tree
			IAVLNode newNode = node.getRight();
			while (newNode.getLeft().isRealNode()) {
				newNode = newNode.getLeft();
			}
			return newNode;
		}
		else { // No right sons, successor is upstream
			IAVLNode parent = node.getParent();
			IAVLNode newNode = node;
			while (newNode == parent.getRight()) { // Go upstream until finding the first ancestor to the right of node
				newNode = parent;
				parent = newNode.getParent();
			}
			return parent;
		}
	}

	/*
	* public String search(int k)
	*
	* Returns the info of an item with key k if it exists in the tree.
	* otherwise, returns null.
	* Uses the nodeSearch helper function.
	* Complexity O(log n)
	*/
	public String search(int k) {
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
	 * Helper function for insert(), delete(), join() & split().
	 * Given a node, corrects its size, min and max fields.
	 * Complexity O(1).
	 */
	private void fieldCorrect(IAVLNode node) {
		node.setSize(node.getLeft().getSize() + node.getRight().getSize() + 1);
		node.setMin(node);
		node.setMax(node);
		if (node.getLeft().isRealNode()) {
			node.setMin(node.getLeft().getMin());
		}
		if (node.getRight().isRealNode()) {
			node.setMax(node.getRight().getMax());
		}
	}
  
	/*
	 * Helper function for insertRebalance(), deleteRebalance() & joinRebalance(). 
	 * Given a child node, rotates the child and its parent.
	 * The function also handles height changes.
	 * Complexity O(1).
	 */
	private void rotate(IAVLNode node) {
		IAVLNode parent = node.getParent();
		if (parent.getRight() == node) { // If node is on the right of p.
			parent.setRight(node.getLeft());
			parent.getRight().setParent(parent);
			node.setLeft(parent);
		}
		else { // If node is on the left of p.
			parent.setLeft(node.getRight());
			parent.getLeft().setParent(parent);
			node.setRight(parent);
		}
		// Fix parent of p and parent of node.
		if (!this.isRoot(parent)) { // If parent isn't root, fix its parent's son
			if (parent.getParent().getLeft() == parent) {
				parent.getParent().setLeft(node);
			}
			else {
				parent.getParent().setRight(node);
			}
		}
		else { // Otherwise, set the node to be the root
			this.root = node;
		}
		node.setParent(parent.getParent());
		parent.setParent(node);
		// Fix heights of node and of parent.
		parent.setHeight(1 + Math.max(parent.getLeft().getHeight(), parent.getRight().getHeight()));
		node.setHeight(1 + Math.max(node.getLeft().getHeight(), node.getRight().getHeight()));
		// Fix sizes of node and of parent.
		this.fieldCorrect(parent);
		this.fieldCorrect(node);
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
						this.fieldCorrect(p); // Correct size of parent.
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
				else { // If the inserted node was to the right
					IAVLNode other = p.getLeft();
					if (p.getHeight() == other.getHeight() + 1) { // If the other node has a diff. of 1
						p.setHeight(p.getHeight() + 1); // Promote parent and re-balance upwards
						this.fieldCorrect(p); // Correct size of parent.
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
				this.fieldCorrect(p); // Correct size of parent.
				return this.insertRebalance(p); // No problem, climing up to fix sizes.
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
	   if (this.empty()) { // Special case for insertion when tree is empty.
		   this.root = new AVLNode(k, i, VIRTUAL_NODE, VIRTUAL_NODE, null);
		   return 0;
	   }
	   IAVLNode parent = nodeSearch(k, this.root); // Find where to insert
	   if (parent.getKey() == k) { // Make sure key isn't in tree
		   return -1;
	   }
	   else {
		   IAVLNode child = new AVLNode(k, i, VIRTUAL_NODE, VIRTUAL_NODE, parent); // Create new node
		   if (parent.getKey() > k) { // Insert on the left side of parent
			   parent.setLeft(child); 
		   }
		   else {
			   parent.setRight(child);
		   }
		   return this.insertRebalance(child); // Re-balance and validate fields of nodes.   
		   }
   } 	   

	/* 
	 * Helper function for delete().
	 * @pre: parentNode != null && targetNode != null
	 * Returns the height differences between two nodes
	 */
   private int nodeDistance(IAVLNode node1, IAVLNode node2) {
	   return Math.abs(node1.getHeight() - node2.getHeight());
   }

   /**
    * /*
	 * Helper function for insert().
	 * Given inserted node, re-balance the tree up-to the root.
	 * Return # of re-balance operations made.
	 * Handles corrections of heights and sizes of affected nodes.
	 * Complexity O(logn). 
	 *
	 * Cases to rebalance:
	 * 1. (2,2)
	 * 2. (3,1) -> (1,1)
	 * 3. (3,1) -> (2,1)
	 * 4. (3,1) -> (1,2)
	 */
   private int deleteRebalance(IAVLNode node) {
	   if (node == null) { // Got to the root
		   return 0;
	   }

	   IAVLNode leftNode = node.getLeft();
	   IAVLNode rightNode = node.getRight();

	   int leftDiff = nodeDistance(leftNode, node);
	   int rightDiff = nodeDistance(rightNode, node);

	   if (leftDiff == 2 && rightDiff == 2) { // case 1
		   this.fieldCorrect(node);
		   node.setHeight(node.getHeight() - 1); // Demote node
		   return 1 + this.deleteRebalance(node.getParent()); // Problem is either fixed or moved up
	   }

	   else if (leftDiff == 3 && rightDiff == 1) { // The base case is (3,1)
		   IAVLNode rightRightNode = rightNode.getRight();
		   IAVLNode rightLeftNode = rightNode.getLeft();

		   int rightRightDiff = nodeDistance(rightNode, rightRightNode);
		   int rightLeftDiff = nodeDistance(rightNode, rightLeftNode);

		   if (rightLeftDiff == 1 && rightRightDiff == 1) { // case 2 [(3,1) -> (1,1)] - rotate left, demote node, promote its right son
			   this.rotate(rightNode);
			   return 3 + this.deleteRebalance(rightNode); // Problem solved, climbing up to fix sizes.
		   }
		   else if (rightLeftDiff == 2 && rightRightDiff == 1) { // case 3 [(3,1) -> (2,1)]- rotate left, demote z twice
			   this.rotate(rightNode);
			   return 3 + this.deleteRebalance(rightNode); // Problem is either fixed or moved up
		   }
		   else { // case 4 [(3,1) -> (1,2)] - double rotation (we trust here that the previous tree was correct)
			   this.rotate(rightLeftNode);
			   this.rotate(rightLeftNode);
			   return 5 + this.deleteRebalance(rightLeftNode);
		   }
	   }

	   else if (leftDiff == 1 && rightDiff == 3) { // now base id (1,3) which is symmetric
		   IAVLNode leftRightNode = leftNode.getRight();
		   IAVLNode leftLeftNode = leftNode.getLeft();

		   int leftRightDiff = nodeDistance(leftNode, leftRightNode);
		   int leftLeftDiff = nodeDistance(leftNode, leftLeftNode);

		   if (leftRightDiff == 1 && leftLeftDiff == 1) { // Symmetric case 2 [(3,1) -> (1,1)] - rotate right, demote rebalanceNode, promote its left son
			   this.rotate(leftNode);
			   return 3 + this.deleteRebalance(leftNode); // Problem solved, climbing up to fix sizes.
		   }

		   else if (leftRightDiff == 2 && leftLeftDiff == 1) { // case 3 [(3,1) -> (2,1)] - rotate right, demote twice
			   this.rotate(leftNode);
			   return 3 + this.deleteRebalance(leftNode); // Problem is either fixed or moved up
		   }

		   else { // case 4 [(3,1) -> (1,2)] - double rotation (we trust here that the previous tree was correct)
			   this.rotate(leftRightNode);
			   this.rotate(leftRightNode);
			   return 5 + this.deleteRebalance(leftRightNode);
		   }
	   }

	   else { // No problem, climb up to fix sizes
		   this.fieldCorrect(node);
		   return this.deleteRebalance(node.getParent());
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
   	public int delete(int k) {
   		if (this.empty()) { // Special case when the tree is empty.
   			return -1;
   		}
   		IAVLNode node = nodeSearch(k, this.root); // find the node to delete
   		if (node.getKey() != k) { // if key does not exist, return -1
   			return -1;
   		}
   		else { // key exists in the tree
   			if (isLeaf(node)) { // target node is a leaf
   				if (isRoot(node)) { // We delete the root which is the only node in the tree
   					this.root = VIRTUAL_NODE;
   					return 0;
   				}
   				else { // target node is not a root
   					IAVLNode parent = node.getParent(); // target node is not a root
   					if (isLeftSon(node)) { // target node is a left son
   						parent.setLeft(VIRTUAL_NODE); // bypass target node
   					}
   					else { // target node is a right son
   						parent.setRight(VIRTUAL_NODE); // bypass target node
   					}
   					return this.deleteRebalance(parent);
   				}
   			}
   			else if (isUnaryRight(node)) { // target node has only right son
   				if (isRoot(node)) {
   					this.root = node.getRight();
   					return 0;
   				}
   				else { // target node is not a root
   					IAVLNode parent = node.getParent();
   					if (isLeftSon(node)) { // target node is a left son
   						parent.setLeft(node.getRight()); // bypass target node by setting its left son as the new left son of his parent
   						node.getRight().setParent(parent); // set the right node parent of target as the parent of target
   					}
   					else { // target node is a right son
   						parent.setRight(node.getRight()); // bypass target node by setting its right son as the new right son of his parent
   						node.getRight().setParent(parent); // set the right node parent of target as the parent of target
   					}
   					return this.deleteRebalance(parent);
   				}
   			}
   			else if (isUnaryLeft(node)) { // target node has only left son
   				if (isRoot(node)) {
   					this.root = node.getLeft();
   					return 0;
   				}
   				else { // target node is not a root
   					IAVLNode parent = node.getParent();
   					if (isLeftSon(node)) { // target node is a left son
   						parent.setLeft(node.getLeft()); // bypass target node by setting its left son as the new left son of his parent
   						node.getLeft().setParent(parent); // set the left node parent of target as the parent of target
   						}
   					else { // target node is a right son
   						parent.setRight(node.getLeft()); // bypass target node by setting its left son as the new right son of his parent
   						node.getLeft().setParent(parent); // set the left node parent of target as the parent of target
   					}
   					return this.deleteRebalance(parent);
   				}
   			}
   			else { // Complicated case - target node has two sons
   				IAVLNode successorNode = findSuccessor(node); // find the successor  (I know for sure that it has one because the node has right son)
   				int rebalancingValue = delete(successorNode.getKey());
   				// Now I want to make the successor to become the target node
   				successorNode.setHeight(node.getHeight()); // set successor height to be target node height
   				successorNode.setSize(node.getSize()); // set successor size to be target node size
   				successorNode.setMax(node.getMax());
   				successorNode.setMin(node.getMin()); // set successor min to be target node min
   				if (!node.getRight().isRealNode()) { // set the max of successor as itself
   					successorNode.setMax(successorNode);
   				}
   				else { // set successor max to be target node max
   					successorNode.setMax(node.getMax());
   				}
   				if (!node.getLeft().isRealNode()) { // set the min of successor as itself
   					successorNode.setMin(successorNode);
   				}
   				else { // set successor min to be target node min
   					successorNode.setMin(node.getMin());
   				}
   				if (node.getRight() != successorNode){ // for case when successor is target node right son
   					successorNode.setRight(node.getRight()); // set the successor right son
   					successorNode.getRight().setParent(successorNode);
   				}
   				else {
   					successorNode.setRight(VIRTUAL_NODE);
   					successorNode.setSize(node.getLeft().getSize() + 1);
   				}
   				if (node.getLeft() != successorNode) { // for cases when successor is target node left son
   					successorNode.setLeft(node.getLeft()); // set the successor left son
   					successorNode.getLeft().setParent(successorNode);
   				}
   				else {
   					successorNode.setLeft(VIRTUAL_NODE);
   					successorNode.setSize(node.getRight().getSize() + 1);
   				}
   				if (node.getParent() != null) {
   					IAVLNode parent = node.getParent(); // target node is not the root
   					successorNode.setParent(parent); // Set the target node parent as the successors parent
   					if (parent.getRight() == node) { // set the successor as the parent's new right son
   						parent.setRight(successorNode);
   					}
   					else {  // set the successor is the parent's new left son
   						parent.setLeft(successorNode);
   					}
   					this.fieldCorrect(parent);
   				}
   				else { // target node is the root
   					successorNode.setParent(null);
   					this.root = successorNode;
   				}
   				return rebalancingValue;
   			}
   		}
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
   		return this.root.getMin().getValue();
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
   		return this.root.getMax().getValue();
   	}

   	/*
   	 * Helper function for keysToArray().
   	 * Inserts the keys inorder to arr.
   	 * Returns the pointer after making the insertions (so we could keep track of where
   	 * to insert).
   	 * Complexity O(n) (each node is visited once, constant work in each node).
   	 */
   	private int inorderKeys(IAVLNode node, int[] arr, int pointer){
   		if (node.getLeft().isRealNode()) {
		   pointer = this.inorderKeys(node.getLeft(), arr, pointer);
   		}
   		arr[pointer] = node.getKey();
   		pointer++;
   		if (node.getRight().isRealNode()) {
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
   		if (node.getLeft().isRealNode()) {
   			pointer = this.inorderValues(node.getLeft(), arr, pointer);
   		}
   		arr[pointer] = node.getValue();
   		pointer++;
   		if (node.getRight().isRealNode()) {
   			pointer = this.inorderValues(node.getRight(), arr, pointer);
   		}
   		return pointer;
   	}
  
   	/*
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

   	/*
   	 * public int size()
   	 *
   	 * Returns the number of nodes in the tree.
   	 * Complexity O(1).
   	 */
   	public int size()
   	{
   		return this.root.getSize();
   	}
   
   	/*
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
   
   	/*
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
   		IAVLNode node = this.nodeSearch(x, this.root);
   		IAVLNode rightNode = node.getRight();
   		IAVLNode leftNode = node.getLeft();
   		AVLTree biggersTree = new AVLTree(rightNode);
   		AVLTree smallersTree = new AVLTree(leftNode);
   		while (!isRoot(node)) {
   			if (this.isLeftSon(node)) {
   				AVLTree.IAVLNode scratchParentNode = this.new AVLNode(node.getParent().getKey(), node.getParent().getValue(), this.VIRTUAL_NODE, this.VIRTUAL_NODE, null);
   				AVLTree newBiggers = new AVLTree(node.getParent().getRight());
   				biggersTree.join(scratchParentNode, newBiggers);
   				if (biggersTree.root.getHeight() < newBiggers.root.getHeight()) {
   					biggersTree.root = newBiggers.root;
   				}
   			}	
   			else {
   				AVLTree.IAVLNode scratchParentNode = this.new AVLNode(node.getParent().getKey(), node.getParent().getValue(), this.VIRTUAL_NODE, this.VIRTUAL_NODE, null);
   				AVLTree newSmallers = new AVLTree(node.getParent().getLeft());
   				smallersTree.join(scratchParentNode, newSmallers);
   				if (smallersTree.root.getHeight() < newSmallers.root.getHeight()) {
   					smallersTree.root = newSmallers.root;
   				}
   			}
   			node = node.getParent();
   		}
   		return new AVLTree[] {smallersTree, biggersTree};
   	}
   
   	/*
   	 * Helper function for join().
   	 * Re-balances the tree upwards and returns the number of re-balance operations.
   	 * Complexity O(|tree.rank - t.rank| + 1)
   	 */
   	private int joinRebalance(IAVLNode node) {
   		IAVLNode parent = node.getParent();
   		if (parent == null) { // If node == root, we're done
   			return 0;
   		}
   		if (node.getHeight() != parent.getHeight()) { // If we're in balance, we're done
   			fieldCorrect(parent); // Correct size of parent.
   			return insertRebalance(parent); // Problem solved, climbing up to fix sizes.
   		}
   		else {
   			// Get the sibling of the node
   			IAVLNode other;
   			if (parent.getLeft() == node) {
   				other = parent.getRight();
   			}
   			else {
   				other = parent.getLeft();
   			}
   			// If parent is (0,1)/(1,0), or if parent is (0,2)/(2,0) node is (1,2)/(2,1), use insertRebalance()
   			if (parent.getHeight() == other.getHeight() + 1 || (parent.getHeight() == other.getHeight() + 2 &&
   					!(node.getLeft().getHeight() == node.getRight().getHeight()))) {
   				fieldCorrect(node);
   				return insertRebalance(node);
   			}
   			else { // Otherwise, rotate on the node and promote it, than continue fixing up
   				rotate(node);
   				fieldCorrect(node);
   				return 2 + joinRebalance(node);
   			}
   		}
   	}   
   
   	/*
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
   		if (this.root.getHeight() < t.root.getHeight()) { // We want tree to have the greater rank.
   			return t.join(x, this);
   		}
   		// Edge cases:
   		if (this.empty() & t.empty()) { // This is an extreme edge case, counting "init." of tree with x 
   			return 1; // Not actually creating a tree - we couldn't ref. it so it's a waste.
   		}
   		if (t.empty()) { // T is empty - insert x to tree.
   			int cost = this.root.getHeight();
   			this.insert(x.getKey(), x.getValue());
   			return cost + 1;
   		}
   		IAVLNode b = this.getRoot();
   		IAVLNode a = t.getRoot();
   		int cost = b.getHeight() - a.getHeight();
   		if (b.getKey() > x.getKey()) { // Bigger tree on right, smaller tree on left.
   			// Get to the first node on the left vertex of tree whose rank isn't greater than the root of t
   			if (this.nodeDistance(a, b) <= 1) {
   				x.setLeft(a);
   				x.setRight(b);
   				a.setParent(x);
   				b.setParent(x);
   				x.setHeight(b.getHeight() + 1);
   				fieldCorrect(x);
   				this.root = x;
   				return cost + 1;
   			}
   			while (b.getHeight() > a.getHeight()) {
   				b = b.getLeft();
   			}
   			// Fix pointer of the tree nodes, and fix height of x
   			x.setParent(b.getParent());
   			x.setLeft(a);
   			a.setParent(x);
   			b.getParent().setLeft(x);
   			x.setRight(b);
   			b.setParent(x);
   			x.setHeight(Math.max(a.getHeight(), b.getHeight())+1);
   		}
   		else { // Bigger tree on left, smaller tree on right.
   			// Get to the first node on the right vertex of tree whose rank isn't greater than the root of t
   			if (this.nodeDistance(a, b) <= 1) {
   				x.setLeft(b);
   				x.setRight(a);
   				a.setParent(x);
   				b.setParent(x);
   				x.setHeight(b.getHeight() + 1);
   				fieldCorrect(x);
   				this.root = x;
   				return cost + 1;
   			}
   			while (b.getHeight() > a.getHeight()) {
   				b = b.getRight();
   			}
   			// Fix pointer of the tree nodes, and fix height of x
   			x.setParent(b.getParent());
   			x.setRight(a);
   			a.setParent(x);
   			b.getParent().setRight(x);
   			x.setLeft(b);
   			b.setParent(x);
   			x.setHeight(Math.max(a.getHeight(), b.getHeight())+1);
   		}
   		// Correct sizes and re-balance if needed
   		this.fieldCorrect(x);
   		this.joinRebalance(x);
   		return cost + 1;	   
   	}
   
   	/** 
	 * public interface IAVLNode
	 * ! Do not delete or modify this - otherwise all tests will fail !
	 */
	public interface IAVLNode{	
		public int getKey(); // Returns this's key (for virtual node return -1).
		public String getValue(); // Returns this's value [info], for virtual node returns null.
		public void setLeft(IAVLNode node); // Sets left child.
		public IAVLNode getLeft(); // Returns left child, if there is no left child returns null.
		public void setRight(IAVLNode node); // Sets right child.
		public IAVLNode getRight(); // Returns right child, if there is no right child return null.
		public void setParent(IAVLNode node); // Sets parent.
		public IAVLNode getParent(); // Returns the parent, if there is no parent return null.
		public boolean isRealNode(); // Returns True if this is a non-virtual AVL node.
    	public void setHeight(int height); // Sets the height of this.
    	public int getHeight(); // Returns the height of this (-1 for virtual nodes).
    	public void setSize(int size); // Sets the size of the sub-tree rooted by this.
    	public int getSize(); // Returns the size of the sub-tree rooted by this.
    	public void setMin(IAVLNode node); // Sets the minimal node in the sub-tree rooted by this.
    	public IAVLNode getMin(); // Returns the minimal node in the sub-tree rooted by this.
    	public void setMax(IAVLNode node); // Sets the maximal node in the sub-tree rooted by this.
    	public IAVLNode getMax(); // Returns the maximal node in the sub-tree rooted by this.
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
		/* Each node holds a key, a value, its height, its size, its sons and its parent.
		 * height(node) = max(height(left), height(right)) + 1
		 * size(node) = 1 + size(left) + size(right)
	  	 * Each node also holds the minimal and maximal nodes of its sub-tree.
	  	 */
	  	int key, height, size;  
		String value;
		IAVLNode left, right, parent, min, max;

		/*
		 * A constructor for an AVLNode. O(1) complexity.
		 * Special case - the node is virtual (hence its size is 0, its height is -1 and
		 * its l and r sons are null)
		 */
		public AVLNode(int k, String v, IAVLNode l, IAVLNode r, IAVLNode p) {
			this.left = l;
			this.right = r;
			this.parent = p;
			this.min = this.max = this;
			if (l == null && r == null) {
				this.height = -1;
				this.key = -1;
				this.size = 0;
			}
			else {
				this.height = Math.max(l.getHeight(), r.getHeight()) + 1;
				this.size = 1 + l.getSize() + r.getSize();
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
		
		/*
		 * Sets the minimal node of the sub-tree rooted by an AVLNode. O(1) complexity.
		 * Maintenance is kept by insert(), delete(), join(), split().
		 */
		public void setMin(IAVLNode node) {
			this.min = node;
		}
		
		/*
		 * Returns the minimal node of the sub-tree rooted by an AVLNode. O(1) complexity.
		 * Maintenance is kept by insert(), delete(), join(), split().
		 */
		public IAVLNode getMin() {
			return this.min;
		}
		
		/*
		 * Sets the maximal node of the sub-tree rooted by an AVLNode. O(1) complexity.
		 * Maintenance is kept by insert(), delete(), join(), split().
		 */
		public void setMax(IAVLNode node) {
			this.max = node;
		}
		
		/*
		 * Returns the maximal node of the sub-tree rooted by an AVLNode. O(1) complexity.
		 * Maintenance is kept by insert(), delete(), join(), split().
		 */
		public IAVLNode getMax() {
			return this.max;
		}
	}
	public static void main(String[] args) {
	}
}
