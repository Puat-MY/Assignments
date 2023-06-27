#include <iostream>
#include <fstream>
#include <algorithm>
#include "BST.h"


using namespace std;


BST::BST() {
	root = NULL;
	count = 0;
}


bool BST::empty() {
	if (count == 0) return true;
	return false;
}


int BST::size() {
	return count;
}


void BST::preOrderPrint() {
	if (root == NULL) return;// handle special case
	else preOrderPrint2(root);// do normal process
	cout << endl;
}


void BST::preOrderPrint2(BTNode *cur) {

	if (cur == NULL) return;
	cur->item.print(cout);
	preOrderPrint2(cur->left);
	preOrderPrint2(cur->right);
}


void BST::inOrderPrint() {
	if (root == NULL) return;// handle special case
	else inOrderPrint2(root);// do normal process
	cout << endl;
}


void BST::inOrderPrint2(BTNode *cur) {

	if (cur == NULL) return;

	inOrderPrint2(cur->left);
	cur->item.print(cout);
	inOrderPrint2(cur->right);
}


void BST::postOrderPrint() {
	if (root == NULL) return;// handle special case
	else postOrderPrint2(root);// do normal process
	cout << endl;
}


void BST::postOrderPrint2(BTNode *cur) {
	if (cur == NULL) return;
	postOrderPrint2(cur->left);
	postOrderPrint2(cur->right);
	cur->item.print(cout);
}



int BST::countNode() {
	int	counter = 0;
	if (root == NULL) return 0;
	countNode2(root, counter);
	return counter;
}


void BST::countNode2(BTNode *cur, int &count) {
	if (cur == NULL) return;
	countNode2(cur->left, count);
	countNode2(cur->right, count);
	count++;
}


bool BST::findGrandsons(type grandFather) {
	if (root == NULL) return false;
	return (fGS2(grandFather, root));
}


bool BST::fGS2(type grandFather, BTNode *cur) {
	if (cur == NULL) return false;
	//if (cur->item == grandFather) {
	if (cur->item.compare2(grandFather)){

		fGS3(cur, 0);// do another TT to find grandsons
		return true;
	}
	if (fGS2(grandFather, cur->left)) return true;
	return fGS2(grandFather, cur->right);
}


void BST::fGS3(BTNode *cur, int level) {
	if (cur == NULL) return;
	if (level == 2) {
		cur->item.print(cout);
		return;  // No need to search downward
	}
	fGS3(cur->left, level + 1);
	fGS3(cur->right, level + 1);
}



void BST::topDownLevelTraversal() {
	BTNode			*cur;
	Queue		    q;


	if (empty()) return; 	// special case
	q.enqueue(root);	// Step 1: enqueue the first node
	while (!q.empty()) { 	// Step 2: do 2 operations inside
		q.dequeue(cur);
		if (cur != NULL) {
			cur->item.print(cout);

			if (cur->left != NULL)
				q.enqueue(cur->left);

			if (cur->right != NULL)
				q.enqueue(cur->right);
		}
	}
}

//insert for BST
bool BST::insert(type newItem) {
	BTNode	*cur = new BTNode(newItem);
	if (!cur) return false;		// special case 1
	if (root == NULL) {
		root = cur;
		count++;
		return true; 			// special case 2
	}
	insert2(root, cur);			// normal
	count++;
	return true;
}


void BST::insert2(BTNode *cur, BTNode *newNode) {
	//if (cur->item > newNode->item) {
	if (cur->item.compare1(newNode->item)){
		if (cur->left == NULL)
			cur->left = newNode;
		else
			insert2(cur->left, newNode);
	}
	else {
		if (cur->right == NULL)
			cur->right = newNode;
		else
			insert2(cur->right, newNode);
	}
}



bool BST::remove(type item) {
	if (root == NULL) return false; 		// special case 1: tree is empty
	return remove2(root, root, item); 		// normal case
}

bool BST::remove2(BTNode *pre, BTNode *cur, type item) {

	// Turn back when the search reaches the end of an external path
	if (cur == NULL) return false;

	// normal case: manage to find the item to be removed
	//if (cur->item == item) {
	if (cur->item.compare2(item)){
		if (cur->left == NULL || cur->right == NULL)
			case2(pre, cur);	// case 2 and case 1: cur has less than 2 sons
		else
			case3(cur);		// case 3, cur has 2 sons
		count--;				// update the counter
		return true;
	}

	// Current node does NOT store the current item -> ask left sub-tree to check
	//if (cur->item > item)
	if (cur->item.compare1(item))
		return remove2(cur, cur->left, item);

	// Item is not in the left subtree, try the right sub-tree instead
	return remove2(cur, cur->right, item);
}


void BST::case2(BTNode *pre, BTNode *cur) {

	// special case: delete root node
	if (pre == cur) {
		if (cur->left != NULL)	// has left son?
			root = cur->left;
		else
			root = cur->right;

		free(cur);
		return;
	}

	if (pre->right == cur) {		// father is right son of grandfather? 
		if (cur->left == NULL)			// father has no left son?
			pre->right = cur->right;			// connect gfather/gson
		else
			pre->right = cur->left;
	}
	else {						// father is left son of grandfather?
		if (cur->left == NULL)			// father has no left son? 
			pre->left = cur->right;				// connect gfather/gson
		else
			pre->left = cur->left;
	}

	free(cur);					// remove item
}


void BST::case3(BTNode *cur) {
	BTNode		*is, *isFather;

	// get the IS and IS_parent of current node
	is = isFather = cur->right;
	while (is->left != NULL) {
		isFather = is;
		is = is->left;
	}

	// copy IS node into current node
	cur->item = is->item;

	// Point IS_Father (grandfather) to IS_Child (grandson)
	if (is == isFather)
		cur->right = is->right;		// case 1: There is no IS_Father    
	else
		isFather->left = is->right;	// case 2: There is IS_Father

	// remove IS Node
	free(is);
}


bool BST::deepestNodes() {
	if (root == NULL) return false; //empty tree
	Queue q1;
	int level = 0, height = 0;

	//search for deepest nodes
	getHeight(root, level, height); //get tree height
	getDeepestNode(root, level, height, &q1); //enqueue deepest nodes into queue

	//dequeue deepest nodes and print
	BTNode* temp = new BTNode(type());
	cout << "Deepest nodes of tree: ";
	while (!q1.empty()) { 
		q1.dequeue(temp);
		cout << temp->item.id << " ";
	}
	cout << endl;
	return true;
}


void BST::getHeight(BTNode* cur, int level, int& height) { //recursive function to get tree height
	if (cur == NULL) return; //reached end of tree
	if (level > height) { //set height to level if level > height i.e. level of current node in tree is deeper than previous node
		height = level;
	}
	//tree traversal is used because all nodes must be travelled to get the deepest external path
	getHeight(cur->left, level + 1, height); //traverse to left
	getHeight(cur->right, level + 1, height); //traverse to right
}


void BST::getDeepestNode(BTNode* cur, int level, int height, Queue* q) { //recursive function to enqueue deepest nodes into queue
	if (cur == NULL) return; //reached end of tree
	if (level == height) {
		q->enqueue(cur); //enqueue node if level is equal to height i.e. the node is at deepest level
	}
	//tree traversal is used due to same reason in getHeight
	getDeepestNode(cur->left, level + 1, height, q); //traverse to left
	getDeepestNode(cur->right, level + 1, height, q); //traverse to right
}


bool BST::display(int order, int source) {
	if (root == NULL) return false; //empty tree
	switch (source) {
	case 1: //print to screen
		displayPrint(root, order, cout);
		break;
	case 2: //print to file
		ofstream write("student-info.txt");
		displayPrint(root, order, write);
		write.close();
	}
	return true;
}


void BST::displayPrint(BTNode* cur, int order, ostream& out) { //recursive function to print nodes in user-specified order to destination
	if (cur == NULL) return;//empty tree
	switch (order) {
	case 1: //ascending - InOrder traversal
		displayPrint(cur->left, order, out);
		cur->item.print(out);
		displayPrint(cur->right, order, out);
		break;
	case 2: //descending - reversed InOrder traversal (start from right)
		displayPrint(cur->right, order, out);
		cur->item.print(out);
		displayPrint(cur->left, order, out);
	}
}


bool BST::CloneSubtree(BST t1, type item) {
	if (t1.empty()) {
		cout << "Tree is empty.\n";
		return false;
	} //original tree is empty
	if (!empty()) {
		cout << "BST for tree cloning is not empty.\n";
		return false;
	} //tree to store cloned subtree is not empty
	cloneMatchingNode(t1.root, item); //recursive function to find the matching node and produce the subtree clone
	if (empty()) {
		cout << "Item not found.\n";
		return false;
	} //cloned subtree is empty after find operation i.e. item not found or memory allocation failed
	return true; //subtree cloned successfully
}


void BST::cloneMatchingNode(BTNode* cur, type item) { //recursive function to find node
	if (cur == NULL) return; //reached end of tree
	if (cur->item.compare2(item)) { //cur->item.id == item.id, id was found in tree
		startCloning(cur, item); //start cloning
		return;
	}
	//BSTT is used to search for matching node
	if (cur->item.compare1(item)) cloneMatchingNode(cur->left, item); //cur->item.id > item.id, traverse to left
	else cloneMatchingNode(cur->right, item); //traverse to right
}


void BST::startCloning(BTNode* cur, type item) { //recursive function for cloning process
	if (cur == NULL) return; //reached end of an external path
	item = cur->item; //set item to cur->item
	insert(item); //insert into subtree
	//tree traversal is used to clone all nodes after the matching node
	startCloning(cur->left, item); //traverse to left
	startCloning(cur->right, item); //traverse to right
}


bool BST::printLevelNodes() {
	if (root == NULL) return false; //empty tree
	int level = 0, height = 0;
	getHeight(root, level, height); //use getHeight function to find tree height
	for (level = 1; level <= height + 1; level++) { //perform level traversal
		cout << "Level " << level << " nodes: ";
		printLevel(root, level);
		cout << "\n";
	}
	return true;
}


void BST::printLevel(BTNode* cur, int level){
	if (cur == NULL) return; //reached end of an external path
	if (level == 1) {
		cout << cur->item.id << " ";
	}
	else {
		printLevel(cur->left, level - 1); //traverse to left, track distance by using level
		printLevel(cur->right, level - 1); //traverse to right, track distance by using level
	}
}


bool BST::printPath() {
	if (root == NULL) return false; //empty tree
	if (root->left == NULL && root->right == NULL) { //special case: tree only has root node
		cout << "Tree only consists of root node, thus the external path is\n\n"
			<< root->item.id;
		return true;
	}
	const int maximum_path_length = 500; //size of external path array
	int external_path[maximum_path_length] = {}; //array for storing external paths
	int path_length = 0; //integer to track path length travelled
	cout << "Below are all the external paths for the tree:\n\n";
	printPath2(root, external_path, path_length);
	return true;
}


void BST::printPath2(BTNode* cur, int path[], int length) {
	if (cur == NULL) return;
	path[length++] = cur->item.id; //store cur->item into array, then +1 to length
	//the position to store cur->item is tracked using current depth of tree
	if (cur->left == NULL && cur->right == NULL) { //left and right are NULL means reached leaf node
		for (int i = 0; i < length; i++) { //print array using path length
			cout << path[i] << " ";
		}
		cout << endl;
	}
	else {
		printPath2(cur->left, path, length); //traverse to left
		printPath2(cur->right, path, length); //traverse to right
	}
}

