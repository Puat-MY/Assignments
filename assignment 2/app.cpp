#include	<iostream>
#include	<fstream>
#include	<cstdlib>
#include	<cstdio>
#include	<ctime>
#include	"BST.h"
#include    "Student.h"

using namespace std;
bool readFile(const char*, BST*);
int menu();
bool findDuplicate(BTNode*, type);

int main() {
	int opt = 0, order = 0, source = 0, id = 0;
	BST tree, tree_clone, tree_temp;
	const char* filename = new char;
	string temporary_filename = "";
	type stu_clone;
	do {
		opt = menu();
		if (opt == 1) { //read data to BST
			tree_temp = tree; //backup for previously constructed tree (if readFile was ran more than once)
			int reset = 0;
			cout << "File to read (E.g. ABC.txt) >> ";
			cin >> temporary_filename;
			filename = temporary_filename.c_str();
			tree = BST(); //reset tree
			if (!readFile(filename, &tree)) {
				tree = tree_temp;
				cout << "Cannot read " << filename << ". What to do:\n"
					<< "1. Make sure the filename is correct i.e. there is no typo in filename.\n"
					<< "2. Make sure " << filename << " is in same directory as app.cpp.\n"
					<< "3. Make sure " << filename << " is not empty."
					<< "If error still persists after fixing above issues, please restart the program.\n";
			}
		}
		else if (opt == 2) { //print deepest nodes
			if (!tree.deepestNodes()) {
				cout << "Tree is empty.\n"
					<< "Please run \"(1) Read data to BST\" from menu.\n";
			}
		}
		else if (opt == 3) { //display student
			cout << "Select order of printing:\n"
				<< "(1) Ascending order\n(2) Descending order\n(3) Cancel\n"
				<< ">> ";
			while (!(cin >> order) || order < 1 || order > 3) {
				cin.clear();
				cin.ignore(numeric_limits<streamsize>::max(), '\n');
				cout << "Please enter only 1, 2 or 3 >> ";
			}
			if (order != 3) {
				cout << "Select destination of printing:\n"
					<< "(1) Screen\n(2) File (student-info.txt)\n(3) Cancel\n"
					<< ">> ";
				while (!(cin >> source) || source < 1 || source > 3) {
					cin.clear();
					cin.ignore(numeric_limits<streamsize>::max(), '\n');
					cout << "Please enter only 1, 2, or 3 >> ";
				}
			}
			if (order == 3 || source == 3) { //operation cancelled;
				cout << "Operation cancelled.\n";
			}
			else {
				if (!tree.display(order, source)) {
					cout << "Tree is empty.\n"
						<< "Please run \"(1) Read data to BST\" from menu.\n";
				}
			}
		}
		else if (opt == 4) { //clone subtree
			cout << "Enter ID >> ";
			while (!(cin >> stu_clone.id)) {
				cin.clear();
				cin.ignore(numeric_limits<streamsize>::max(), '\n');
				cout << "ID must be numerical! Please enter ID again >> ";
			}
			tree_clone = BST(); //empty tree_clone
			if (!tree_clone.CloneSubtree(tree, stu_clone)) {
				cout << "Cannot clone subtree.";
			}
			else {
				cout << "Records in original tree:\n";
				tree.preOrderPrint();
				cout << "***********************************************************************\n"
					<< "Records in cloned tree:\n";
				tree_clone.preOrderPrint();
			}
		}
		else if (opt == 5) { //print level nodes
			if (!(tree.printLevelNodes())) {
				cout << "ERROR: Tree is empty!\n"
					<< "Please run \"(1) Read data to BST\" from menu.\n";
			}
		}
		else if (opt == 6) { //print path
			if (!(tree.printPath())) {
				cout << "ERROR: Tree is empty!\n"
					<< "Please run \"(1) Read data to BST\" from menu.\n";
			}
		}
		else //opt == 7, exit
			break;
		if (opt != 7) {
			cout << "\nOperation " << opt << " finished.";
			getchar(); getchar();
		}
		cout << endl;
	} while (opt != 7);
	cout << "Program exited. Thank you for using the program.\n";
	system("pause");
	return 0;
}


bool readFile(const char* filename, BST* t1) {
	ifstream read(filename);
	if (!read) { //special case 1: cannot open file
		cout << "Cannot open " << filename << ".\n";
		return false;
	}
	if (read.peek() == -1) { //special case 2: file is blank
		cout << filename << " is empty.\n";
		return false;
	}
	cout << filename << " opened successfully.\n";
	type stu_read;
	bool lastLineWasBlank = false; //detect blank lines in the end of file
	int data_count = 7; //number of important data
	int dup_count = 0; //detect duplicates count
	int read_count = 0; //detect records count
	while (!read.eof()) {
		read_count++;
		for (int i = 0; i < data_count; i++) {
			string skip = "";
			while (skip != "=") { //skip unnecessary inputs
				if (!(read >> skip)) { //if the next line is blank, (read >> skip) would be false
					lastLineWasBlank = true; //set lastLineWasBlank to true because a blakn line was detected in the end of file
					break;
				}
			}
			if (lastLineWasBlank) break; //stop reading after a blank line was detected
			if (i == 0) read >> stu_read.id;
			else if (i == 1) read.getline(stu_read.name, 30);
			else if (i == 2) read.getline(stu_read.address, 100);
			else if (i == 3) read.getline(stu_read.DOB, 20);
			else if (i == 4) read.getline(stu_read.phone_no, 10);
			else if (i == 5) read.getline(stu_read.course, 5);
			else if (i == 6) read >> stu_read.cgpa;
		}
		if (lastLineWasBlank) { //handle special case: blank lines in the end of file
			read_count--;
			break;
		}
		if (findDuplicate(t1->root, stu_read)) { //feedback for duplicate record
			dup_count++;
		}
		else {
			t1->insert(stu_read);
		}
	}
	read.close();
	cout << "\nA total of " << read_count << " records were read from " << filename << ".\n";
	if (dup_count == 0) {
		cout << "No duplicate was found.\n";
	}
	else if (dup_count == 1) {
		cout << dup_count << " duplicate was found and skipped.\n";
	}
	else {
		cout << dup_count << " duplicates were found and skipped.\n";
	}
	cout << t1->size() << " unique records were read into tree.\n";
	return true;
}

int menu() {
	int opt;
	cout << "(1) Read data to BST\n"
		<< "(2) Print deepest nodes\n"
		<< "(3) Display student\n"
		<< "(4) Clone Subtree\n"
		<< "(5) Print Level Nodes\n"
		<< "(6) Print Path\n"
		<< "(7) Exit\n"
		<< "Select (1-7) >> ";
	while (!(cin >> opt) || (opt < 1 || opt > 7)) {
		cin.clear();
		cin.ignore(numeric_limits<streamsize>::max(), '\n');
		cout << "Invalid option. Please enter number only (1-7) >> ";
	}
	return opt;
}

bool findDuplicate(BTNode* cur, type stu) { //recursive function to find duplicate
	if (cur == NULL) return false; //reached end of tree
	if (cur->item.compare2(stu)) { //cur->item.id == stu.id, duplicate found
		return true;
	}
	if (cur->item.compare1(stu)) return findDuplicate(cur->left, stu); //cur->item.id > stu.id, traverse to left
	return findDuplicate(cur->right, stu); //root->item.id < stu.id, traverse to right
}