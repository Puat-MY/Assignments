/* Important notices for the A2 template:
A. You will need to use this template to work on the Assignment 2 (A2)
B. You are not allowed to change the whole structure of template including adding additional functions or remove any existing functions
C. You will only need to fill in the codes with the parts that indicated with numbers and its descriptions
D. You will need to ensure all the COUT are exactly the same as shown in A2 doc as the codes are auto marked by a system. LOW marks or ZERO
   mark will be awarded if the system is unable to detect the correct COUT.
E. No extra decorations are allowed as you will be using the template to work on A2
F. You will need to define the parameters accordingly (including its datatype) as described below:
   day = parameter to store the day
   date = parameter to store the date
   veg_name = array to store the name of the items
   veg_code = array to store the code of the items
   veg_price = array to store the price of the items per kilogram
   weight_purc = array to store the purchasing weight provided by the user
   veg_count = parameter to store the number of items available in the list
*/

//Only these libraries are allowed to be defined and used
#include <iostream>
#include <iomanip>
#include <fstream>
#include <string>

using namespace std; 

#define SIZE 100 //global definition to determine the size of the array in A2
//1. Define the FUNCTION PROTOTYPE for all the listed functions
void readItemList(string& day, string& date, string veg_name[], string veg_code[], double veg_price[], double weight_purc[], int& veg_count);
void writeItemList(string day, string date, string veg_name[], string veg_code[], double veg_price[], double weight_purc[], int veg_count);
void addItemList(string veg_name[], string veg_code[], double veg_price[], double weight_purc[], int& veg_count);
void modifyItemList(string veg_name[], string veg_code[], double veg_price[], double weight_purc[], int veg_count);
void displayItemList(string veg_name[], string veg_code[], double veg_price[], double weight_purc[], int veg_count);
void printInvoice(string day, string date, string veg_name[], string veg_code[], double veg_price[], double weight_purc[], int veg_count);
double calculateGrandTotal(double veg_price[], double weight_purc[], int veg_count);

int main() {
	//2. Define all the necessary variables
	string day, date, veg_name[SIZE], veg_code[SIZE]; double veg_price[SIZE], weight_purc[SIZE]; int veg_count = 0;
	int select, cont;

	//Call readItemList with the required parameters
	readItemList(day, date, veg_name, veg_code, veg_price, weight_purc, veg_count);

	do {//do...while iteration is implemented to repeat the selection menu as below
		cout << "1. Display Item List" << endl;
		cout << "2. Add Item List" << endl;
		cout << "3. Modify Item List" << endl;
		cout << "4. Print Invoice" << endl;
		cout << "5. Quit" << endl;

		//User will key in and select one of the functions available
		cout << "Choice: ";
		cin >> select;

		//"cls" stand for clear screen which refreshes the screen, placing the cursor on original place.
		//For more information, refer to https://www.quora.com/What-is-system-CLS-for-in-c++
		system("cls");

		//cin.ignore() is to ignore or clear one or more characters from the input buffer
		//For more information, refer to https://www.tutorialspoint.com/what-is-the-use-of-cin-ignore-in-cplusplus
		cin.ignore();

		//3. if...else is implemented to select the function according to the user input
		if (select == 1)
			displayItemList(veg_name, veg_code, veg_price, weight_purc, veg_count); //Function call to display the item from the list
		else if (select == 2)
			addItemList(veg_name, veg_code, veg_price, weight_purc, veg_count); //Function call to add new items into the list
		else if (select == 3)
			modifyItemList(veg_name, veg_code, veg_price, weight_purc, veg_count); //Function call to modify the details of the items in the list
		else if (select == 4)
			printInvoice(day, date, veg_name, veg_code, veg_price, weight_purc, veg_count); //Function call to print the invoice of the items in the list
		else if (select == 5)
			break; //Break the loop
		else
			cout << "Not available" << endl;

		//Update the items in the text file whatever function is carried out
		writeItemList(day, date, veg_name, veg_code, veg_price, weight_purc, veg_count);

		//To check whether user wants to continue to perform these functions or not
		cout << "Continue? (1-yes, 2-no): ";
		cin >> cont;

		system("cls");
	} while (cont == 1); //the do...while will stop if the condition is false (user choose to stop the program)

	//4. Grand total of the items is calculated through the RETURN function 
	cout << "Grand Total of the bill = RM" << fixed << setprecision(2) << calculateGrandTotal(veg_price, weight_purc, veg_count) << endl;

	return 0;
}

//5. Function readItemList --> read (ifstream) all the items listed in "itemList.txt" 
//   and store the data into the respective parameters.
//   Hint: P1, date and veg_count should be reference parameters in this function
void readItemList(string& day, string& date, string veg_name[], string veg_code[], double veg_price[], double weight_purc[], int& veg_count) {
	ifstream read_file("itemList.txt");
	read_file >> day >> date;
	read_file.ignore();
	while (!read_file.eof()) {
		getline(read_file, veg_name[veg_count]);
		if (!veg_name[veg_count].empty()) {
			getline(read_file, veg_code[veg_count]);
			read_file >> veg_price[veg_count] >> weight_purc[veg_count];
			read_file.ignore();
			veg_count++;
		}
	}
	read_file.close();
}

//6. Function writeItemList --> write / update (ofstream) all the items to "itemList.txt"
void writeItemList(string day, string date, string veg_name[], string veg_code[], double veg_price[], double weight_purc[], int veg_count) {
	ofstream write_file("itemList.txt");
	write_file << day << "\n" << date;
	for (int i = 0; i < veg_count; i++) {
		write_file << "\n" << veg_name[i] << "\n" << veg_code[i] << "\n";
		write_file << fixed << setprecision(2) << veg_price[i] << " " << weight_purc[i];
	}
	write_file << endl;
	write_file.close();
}

//7. Function addItemList --> add new items into the existing list 
//   Hint: veg_count should be a reference parameter as the number of items should be updated
void addItemList(string veg_name[], string veg_code[], double veg_price[], double weight_purc[], int& veg_count) {
	int select = 0;

	//8. This part is to confirm whether user want to key in new item or not.
	//   If yes, user will need to enter 1. Else, user will need to enter 2.
	//   An evaluation is carried out to ensure user only key in 1 or 2.
	//   However, the current evaluation is only able to check on numbers.
	//   Additional marks will be awarded to those who are able to check on the input other than numbers such as "abc" or "1abc".
	//   Tips: isdigit and stoi can be used. If you are using stoi with dev c++, you may face c++ 11 problem and please
	//	       refer to https://stackoverflow.com/questions/13613295/how-can-i-compile-c11-code-with-orwell-dev-c
	do {
		cout << "Add new item? (1-yes, 2-no): ";
		cin >> select;
		if (select == 0) {
			cin.clear();
		}
		string temp = "";
		getline(cin, temp);
		if (temp != "") {
			select = 0;
		}
	} while (select <= 0 || select >= 3);

	if (select == 1) {
		//9. User will key in the details of the new items
		//   The COUT of add item for this module is shown in the A2 doc
		//   Marks will not be awarded if your COUT different from the sample COUT
		cout << "New item's name: ";
		getline(cin, veg_name[veg_count]);
		cout << "New item's code: ";
		getline(cin, veg_code[veg_count]);
		cout << "Price of new item per KG (RM): ";
		cin >> veg_price[veg_count];
		cout << "Purchasing Weight (KG): ";
		cin >> weight_purc[veg_count];
		veg_count++;
	}
}

//10. Function modifyItemList --> modify the details of any existing items in the list
void modifyItemList(string veg_name[], string veg_code[], double veg_price[], double weight_purc[], int veg_count) {
	int select = 0;

	//Evaluation is not required in this function
	do {
		cout << "Modify item? (1-yes, 2-no): ";
		cin >> select;

	} while (select <= 0 || select >= 3);

	if (select == 1) {
		//11. User will key in the item to be modified through item code and find its match from the existing list
		//    If item is detected, all the details are to be keyed in 
		//    Else, it will have to cout "Item is not available." as shown in the A2 doc
		//    The COUT of modification for this module is shown in the A2 doc
		//    Marks will not be awarded if your COUT different from the sample COUT
		cin.ignore();
		cout << "Key in item code: ";
		string search_code; bool item_exist = false; int item_pos;
		getline(cin, search_code);

		for (int i = 0; i < veg_count; i++) {
			if (search_code == veg_code[i]) {
				item_exist = true;
				item_pos = i;
				break;
			}
		}

		if (item_exist == true) {
			cout << "Item found!\n";
			cout << "New item's name: ";
			getline(cin, veg_name[item_pos]);
			cout << "New item's code: ";
			getline(cin, veg_code[item_pos]);
			cout << "Price per KG (RM): ";
			cin >> veg_price[item_pos];
			cout << "Purchasing Weight (KG): ";
			cin >> weight_purc[item_pos];
		}
		else {
			cout << "Item is not available." << endl;
		}
	}
}

//12. Function displayItemList --> display the details of all the items in the list
//    The COUT of display items is shown in the A2 doc
//    Marks will not be awarded if your COUT different from the sample COUT
void displayItemList(string veg_name[], string veg_code[], double veg_price[], double weight_purc[], int veg_count) {
	for (int i = 0; i < veg_count; i++) {
		cout << "Item " << i + 1 << "\n";
		cout << "Name: " << veg_name[i] << "\n";
		cout << "Code: " << veg_code[i] << "\n";
		cout << "Price per KG: RM" << fixed << setprecision(2) << veg_price[i] << "\n";
		cout << "Purchasing Weight: " << fixed << setprecision(2) << weight_purc[i] << "KG" << endl;
		cout << endl;
	}
}

//13. Function printInvoice --> print out an invoice named "invoice.txt"
//    The output of the invoice is shown in the A2 doc
//    Marks will not be awarded if the output of your invoice is different from the sample invoice
void printInvoice(string day, string date, string veg_name[], string veg_code[], double veg_price[], double weight_purc[], int veg_count) {
	ofstream write_invoice("invoice.txt");
	double total = 0;
	write_invoice << "Date: " << date << "\n";
	write_invoice << "Day: " << day << "\n";
	write_invoice << "List of Items:\n";
	for (int i = 0; i < veg_count; i++) {
		write_invoice << i + 1 << ". " << veg_name[i] << " = ";
		write_invoice << fixed << setprecision(2) << weight_purc[i] << "KG * RM" << veg_price[i] << " = RM" << weight_purc[i] * veg_price[i];
		write_invoice << endl;
		total += (weight_purc[i] * veg_price[i]);
	}
	write_invoice << "Grand Total of the bill = RM" << fixed << setprecision(2) << total;
}

//14. Function calculateGrandTotal --> calculate the grand total of the items purchase and RETURN the grand total to the calling function
double calculateGrandTotal(double veg_price[], double weight_purc[], int veg_count) {
	double total = 0;
	for (int i = 0; i < veg_count; i++) {
		total += (veg_price[i] * weight_purc[i]);
	}
	return total;
}