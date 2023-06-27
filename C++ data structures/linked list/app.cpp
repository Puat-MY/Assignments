#include <iostream>
#include	<fstream>
#include	"List.h"
#include	"Student.h"

using namespace std;

bool CreateStuList(const char *, List *);
bool DeleteStudent(List *, char *);
bool DisplayStudent(List, int);
bool AddExamResult(const char *, List *);
bool FilterStudent(List, List *, char *, int, int);
bool recalculateResult(List, char *);
bool checkEligibleFYP(List, char *);
int menu();
void DisplayStudentOutput(Student, ostream&, int);
const char* getRevisedGrade(Subject);
double getRevisedGradePoint(Subject);
double calculateRevisedGPA(Exam);
double calculateRevisedCGPA(Student);
void printSubjectRevised(Subject);
void printExamRevised(Exam);

using namespace std;


int main() {
	List student_list;
	const char* student_file = "student.txt"; //student records file
	const char* exam_file = "exam.txt"; //exam records file
	int opt = 0;
	do {
		opt = menu();
		if (opt < 0 || opt>7) {
			cin.clear();
			cin.ignore(numeric_limits<streamsize>::max(), '\n');
			cout << "Option out of range!";
		}
		else {
			if (opt == 0) {
				cout << "Program exited. Press ENTER to quit.";
				getchar(); getchar();
				cout << endl;
			}
			else {
				if (opt == 1) { // Create student list
					if (!CreateStuList(student_file, &student_list)) { //warning if file reading was not success
						cout << "ERROR: Could not read student record! Please:\n"
							<< "1. Check the filename. The filename must be \"" << student_file << "\".\n"
							<< "2. Make sure " << student_file << " is in same directory with \"app.cpp\".\n"
							<< "3. Make sure " << student_file << " is not empty.\n"
							<< "If error still occurs although all above were fine, please terminate and restart the program.\n";
					}
				}
				else if (opt == 2) { //Delete student record
					char* delete_id = new char;
					cout << "Please enter the ID of student that you want to delete:\n"
						<< ">> ";
					cin >> delete_id;
					if (!DeleteStudent(&student_list, delete_id)) {
						cout << "ERROR: Could not delete student record! Please:\n"
							<< "1. Make sure the student id exists in list.\n"
							<< "2. Make sure there is no typo in student id.\n"
							<< "3. Run \"(1) Create student list\" from menu.\n";
					}
				}
				else if (opt == 3) { //Print student list
					cout << "Please select the destination of printing:\n"
						<< "(1) Screen\n"
						<< "(2) File (student_result.txt)\n"
						<< "(0) Cancel\n"
						<< ">> ";
					int select = 0;
					do {
						if (!(cin >> select)) {
							cin.clear();
							cin.ignore(numeric_limits<streamsize>::max(), '\n');
							select = -1;
						}
						if (select < 0 || select > 2) {
							cout << "Please select only 0, 1, or 2! >> ";
						}
						else {
							if (select == 0) {
								cout << "Operation cancelled.\n";
							}
							else {
								cout << "---------------PRINTING STUDENT RECORDS---------------\n\n";
								if (!DisplayStudent(student_list, select)) {
									cout << "ERROR: Could not print student list!\n"
										<< "Please run \"(1) Create student list\" from menu.\n";
								}
							}
						}
					} while (select < 0 || select > 2);
				}
				else if (opt == 4) { //Add exam result
					if (!AddExamResult(exam_file, &student_list)) {
						cout << "ERROR: Could not read examination record! Please:\n"
							<< "1. Check the filename. The filename must be \"" << exam_file << "\".\n"
							<< "2. Make sure " << exam_file << " is in same directory with \"app.cpp\".\n"
							<< "3. Make sure " << exam_file << " is not empty.\n"
							<< "4. Run \"(1) Create student list\" from menu.\n";
					}
				}
				else if (opt == 5) {//Recalculate result
					char* recalc_id = new char;
					cout << "Enter the student id to recalculate result based on revised grading.\n"
						<< ">> ";
					cin >> recalc_id;
					if (!recalculateResult(student_list, recalc_id)) {
						cout << "ERROR: Could not find id " << recalc_id << " in list! Please:\n"
							<< "1. Make sure no typo in student id.\n"
							<< "2. Run \"(1) Create student list\" from menu.";
					}
				}
				else if (opt == 6) { //Filter student records
					List student_list_filtered;
					char* course = new char;
					int year = 0;
					int totalcredit = 0;
					cout << "Please type in the tags for filtering student list.\n";
					cout << "Course (E.g., CS) >> ";
					cin >> course;
					cout << "Year (E.g., 2012) >> ";
					if (!(cin >> year)) {
						cin.clear();
						cin.ignore(numeric_limits<streamsize>::max(), '\n');
					}
					cout << "Minimum Credit Hours >> ";
					if (!(cin >> totalcredit)) {
						cin.clear();
						cin.ignore(numeric_limits<streamsize>::max(), '\n');
					}
					cout << "\n\n";
					cout << "---------------PRINTING FILTERED STUDENT LIST---------------\n";
					if (!FilterStudent(student_list, &student_list_filtered, course, year, totalcredit)) {
						cout << "ERROR: Could not filter student list! Please:\n"
							<< "1. Make sure no typo in every tag (course, year, total credit hours).\n"
							<< "2. Run \"(1) Create student list\" from menu.\n";
					}
					else {
						DisplayStudent(student_list_filtered, 1);
						cout << "Finish printing filtered student list.\n";
					}
				}
				else { //opt == 7, Check eligible FYP student
					char* id = new char;
					cout << "Please enter the id of student to check for FYP eligibility.\n"
						<< ">> ";
					cin >> id;
					if (!checkEligibleFYP(student_list, id)) {
						cout << "ERROR: Fail to check FYP eligiblity!\n"
							<< "Please run \"(1) Create student list\" from menu.\n";
					}
				}
				cout << "Press ENTER to return to menu.";
				getchar(); getchar();
			}
		}
		cout << "\n\n";
	}while (opt != 0);
	return 0;
}

bool CreateStuList(const char* filename, List* list) {
	cout << "Fetching " << filename <<"...\n";
	ifstream read_file(filename);
	if (!read_file || read_file.peek() == -1) {
		cout << "ERROR: Cannot open " << filename << "!\n";
		return false;
	}
	cout << filename <<" was found.\nReading student records...\n";
	if (read_file.peek() == -1) {
		cout << "ERROR: " << filename << " is empty!\n";
		return false;
	}
	Student create_stu;
	string skip = ""; //declared to skip unnecessary file inputs
	bool lastLineWasBlank = false;
	while (!read_file.eof()) { //read file records
		for (int i = 0; i < 4; i++) {
			//what we want: input in every line after "="
			skip = "";
			while (skip != "=") { //stops until "=" was read into skip
				if (!(read_file >> skip)) {
					lastLineWasBlank = true;
					break;
				}
			}
			if (lastLineWasBlank) break;
			read_file.ignore();
			switch (i) {
			case 0: read_file >> create_stu.id; break;
			case 1: read_file.getline(create_stu.name, 30); break;
			case 2: read_file >> create_stu.course; break;
			case 3: read_file >> create_stu.phone_no; break;
			}
		}
		list->List::insert(create_stu);
	}
	read_file.close();
	cout << "Records read successfully.\nRemoving duplicates...\n";
	//remove duplicate records
	Node* ptr;
	for (int i = 1; i < list->List::size(); i++) {
		ptr = list->List::find(i);
		if (ptr->item.Student::compareID(ptr->next->item)) {
			list->List::remove(i);
			i--;
		}
	}
	cout << "Duplicates removed.\n" << list->List::size() << " unique records read.\n";
	return true;
}

bool DeleteStudent(List* list, char* id) {
	if (list->List::empty()) { //list is empty
		cout << "ERROR: List is empty!\n";
		return false;
	}
	//removal of student record
	Student delete_stu;
	for (int i = 1; i <= list->List::size(); i++) {
		list->List::get(i, delete_stu);
		if (strcmp(id, delete_stu.id) == 0) { //strcmp returns 0 if two strings are exactly equal
			cout << "ID " << delete_stu.id << " found. Removing...\n";
			list->List::remove(i);
			cout << "1 record was deleted.\n" << list->List::size() << " record(s) left.";
			return true;
		}
	}
	cout << "ERROR: Student id not found!\n";
	return false;
}

bool DisplayStudent(List list, int source) {
	if (list.List::empty()) { //list is empty
		cout << "ERROR: List is empty!\n";
		return false;
	}

	Student display_stu;
	ofstream write_file("student_result.txt");
	for (int i = 1; i <= list.count; i++) {
		list.List::get(i, display_stu);
		switch (source) {
		case 1:
			DisplayStudentOutput(display_stu, cout, i);
			break;
		case 2:
			DisplayStudentOutput(display_stu, write_file, i);
			break;
		}
	}
	write_file.close();
	cout << "Finished printing student list to ";
	if (source == 1) { cout << "screen."; }
	else { cout << "student_result.txt."; }
	cout << "\n";
	return true;
}

bool AddExamResult(const char* filename, List* list) {
	if (list->List::empty()) {
		cout << "ERROR: List is empty!\n";
		return false;
	}
	cout << "Fetching " << filename << "...\n";
	ifstream read_file(filename);
	if (!read_file) {
		cout << "ERROR: Cannot open " << filename << "!\n";
		return false;
	}
	cout << filename << " was found.\nReading examination records...\n";
	if (read_file.peek() == -1) {
		cout << "ERROR: " << filename << " is empty!";
		return false;
	}
	//read exam results
	Student addExam_stu;
	char* id = new char;
	while (!read_file.eof()) {
		read_file >> id;
		for (int i = 1; i <= list->List::size(); i++) { //traverse through the list to find the id
			list->List::get(i, addExam_stu);
			if (strcmp(id, addExam_stu.id) == 0) { //id was found, begin reading exam record
				read_file >> addExam_stu.exam[addExam_stu.exam_cnt].trimester
					>> addExam_stu.exam[addExam_stu.exam_cnt].year
					>> addExam_stu.exam[addExam_stu.exam_cnt].numOfSubjects;
				for (int i = 0; i < addExam_stu.exam[addExam_stu.exam_cnt].numOfSubjects; i++) {
					read_file >> addExam_stu.exam[addExam_stu.exam_cnt].sub[i].subject_code
						>> addExam_stu.exam[addExam_stu.exam_cnt].sub[i].subject_name
						>> addExam_stu.exam[addExam_stu.exam_cnt].sub[i].credit_hours
						>> addExam_stu.exam[addExam_stu.exam_cnt].sub[i].marks;
				}
				addExam_stu.exam[addExam_stu.exam_cnt].Exam::calculateGPA();
				addExam_stu.exam_cnt++;
				addExam_stu.Student::calculateCurrentCGPA();
				list->List::set(i, addExam_stu);
				break; //exit loop once record was stored under the specific student
			}
		}
	}
	read_file.close();
	cout << "Records read successfully.\nRemoving duplicates...\n";
	//remove duplicates
	for (int i = 1; i <= list->List::size(); i++) {
		list->List::get(i, addExam_stu);
		for (int j = 0; j < addExam_stu.exam_cnt; j++) {
			for (int k = j + 1; k < addExam_stu.exam_cnt; k++) {
				if (addExam_stu.exam[j].year == addExam_stu.exam[k].year &&
					addExam_stu.exam[j].trimester == addExam_stu.exam[k].trimester) { //same exam means same year && trimester
					addExam_stu.exam_cnt--;
					for (int p = k; p < addExam_stu.exam_cnt; p++) { //shift all exam to left by 1 index
						addExam_stu.exam[p] = addExam_stu.exam[p + 1];
					}
					addExam_stu.exam[addExam_stu.exam_cnt] = Exam(); //set all items in duplicate to 0
					addExam_stu.Student::calculateCurrentCGPA(); //recalculate cgpa
					k--;
				}
			}
		}
		list->List::set(i, addExam_stu);
	}
	cout << "Duplicates removed.\nSorting (latest to oldest)...\n";
	//sort exam in descending order of year and trimester
	double front = 0, back = 0;
	Exam temp;
	for (int i = 1; i <= list->List::size(); i++) {
		list->List::get(i, addExam_stu);
		for (int j = 0; j < addExam_stu.exam_cnt; j++) {
			front = addExam_stu.exam[j].year + (double)addExam_stu.exam[j].trimester / 100;
			for (int k = j + 1; k < addExam_stu.exam_cnt; k++) {
				back = addExam_stu.exam[k].year + (double)addExam_stu.exam[k].trimester / 100;
				if (front < back) {
					temp = addExam_stu.exam[j];
					addExam_stu.exam[j] = addExam_stu.exam[k];
					addExam_stu.exam[k] = temp;
				}
			}
		}
		list->List::set(i, addExam_stu);
	}
	cout << "Finish sorting.\n";
	return true;
}

bool recalculateResult(List list, char* id) {
	if (list.List::empty()) {
		cout << "ERROR: List is empty!\n";
		return false;
	}

	Student recalculate_stu;
	for (int i = 1; i <= list.count; i++) {//traverse through list to find student id
		list.List::get(i, recalculate_stu);
		if (strcmp(id, recalculate_stu.id) == 0) { //id was found
			cout << "---------------PRINTING RESULT BASED ON ORIGINAL GRADING---------------\n";
			recalculate_stu.Student::print(cout);
			if (recalculate_stu.exam_cnt == 0) {
				cout << "THIS STUDENT HAVEN'T TAKEN ANY EXAM YET\n\n\n";
			}
			else {
				for (int i = 0; i < recalculate_stu.exam_cnt; i++) {
					recalculate_stu.exam[i].Exam::print(cout);
				}
			}

			cout << "---------------PRINTING RESULT BASED ON REVISED GRADING---------------\n";
			recalculate_stu.current_cgpa = calculateRevisedCGPA(recalculate_stu); //recalculate CGPA using revised grading
			recalculate_stu.Student::print(cout);
			if (recalculate_stu.exam_cnt == 0) {
				cout << "THIS STUDENT HAVEN'T TAKE ANY EXAM YET\n\n\n";
			}
			else {
				for (int i = 0; i < recalculate_stu.exam_cnt; i++) {
					recalculate_stu.exam[i].gpa = calculateRevisedGPA(recalculate_stu.exam[i]); //recalculate GPA of every exam using revised grading
					printExamRevised(recalculate_stu.exam[i]);
				}
			}
			cout << "Recalculation of result was done.\n";
			return true;
		}
	}
	cout << "ERROR: Student id not found!\n";
	return false; //loop exited, but nothing was printed i.e. student id not found
}

bool FilterStudent(List list1, List* list2, char* course, int year, int totalcredit) {
	if (list1.List::empty()) {
		cout << "ERROR: List is empty!\n";
		return false;
	}
	if (!list2->List::empty()) {
		cout << "ERROR: List for filtering students is not empty!\n";
		return false;
	}
	
	Student filter_stu;
	for (int i = 1; i <= list1.count; i++) {
		list1.get(i, filter_stu);
		int enrollment_year = 0;
		char temp[3];
		strncpy(temp, filter_stu.id, 2);
		enrollment_year = ((int)temp[0] - '0') * 10 + ((int)temp[1] - '0') + 2000; //convert characters to integer, then add 2000
		if ((strcmp(course, filter_stu.course) == 0)
			&& (year == enrollment_year)
			&& (totalcredit <= filter_stu.totalCreditsEarned)) {
			list2->List::insert(filter_stu);
		}
	}
	
	if (list2->List::empty()) { //list2 is empty i.e. no record was inserted into list2
		cout << "No record match the applied filters.\n";
	}
	return true;
}

bool checkEligibleFYP(List list, char* id) {
	if (list.List::empty()) {
		cout << "ERROR: List is empty!\n";
		return false;
	}
	
	//search for student in list with id
	Student stu;
	for (int i = 1; i <= list.count; i++) {
		list.List::get(i, stu);
		if (strcmp(stu.id, id) == 0) {
			break;
		}
	}
	
	if (strcmp(id, stu.id) != 0) { //no record retrieved into stu i.e. id not found
		cout << "There is no student with id " << id << " in the list.\n"
			<< "Cannot check if student was eligible for FYP.\n";
	}
	else { //stu contains a record retrieved from list i.e. id was found
		int eligibility = 0;
		Subject iipspw, miniproj;
		strcpy(iipspw.subject_code, "UCCD2502");
		strcpy(miniproj.subject_code, "UCCD2513");
		if (stu.totalCreditsEarned >= 30) { //criteria 1 : credits >= 30
			eligibility++;
			for (int i = 0; i < stu.exam_cnt; i++) {
				for (int j = 0; j < stu.exam[i].numOfSubjects; j++) {
					if ((strcmp(stu.exam[i].sub[j].subject_code, iipspw.subject_code) == 0)) { //taken UCCD2502 IIPSPW
						if (stu.exam[i].sub[j].marks >= 50) { //passed UCCD2502 IIPSPW i.e. 2nd criteria fulfilled
							iipspw = stu.exam[i].sub[j];
							eligibility++;
						}
					}
					if ((strcmp(stu.exam[i].sub[j].subject_code, miniproj.subject_code) == 0)) { //taken UCCD2513 Mini Project
						if (stu.exam[i].sub[j].marks >= 50) { //oassed UCCD2513 Mini Project i.e. 3rd criteria fulfilled
							miniproj = stu.exam[i].sub[j];
							eligibility++;
						}
					}
				}
			}
		}
		cout << "\n\n";
		if (eligibility < 3) { //student does not fulfill all three criterias
			cout << "This student " << stu.name << " is not eligible to take FYP yet.\n\n\n";
		}
		else {
			cout << "This student is eligible for FYP. Below are the details of the student:\n\n";
			stu.Student::print(cout);
			cout << "Grade obtained for " << iipspw.subject_code << " " << iipspw.subject_name << " is " << iipspw.getGrade() << " (Marks: " << iipspw.marks << ").\n";
			cout << "Grade obtained for " << miniproj.subject_code << " " << miniproj.subject_name << " is " << miniproj.getGrade() << " (Marks: " << miniproj.marks << ").\n\n\n";
		}
	}
	return true;
}

int menu() {
	int opt = 0;
	cout << "Menu\n" << endl
		<< "(1) Create student list\n"
		<< "(2) Delete student record\n"
		<< "(3) Print student list\n"
		<< "(4) Add exam result\n"
		<< "(5) Recalculate result\n"
		<< "(6) Filter student records\n"
		<< "(7) Check eligible FYP student(s)\n"
		<< "(0) Exit" << endl
		<< "Please enter an option >> ";
	if (!(cin >> opt)) {
		opt = -1;
	}
	cout << "\n";
	return opt;
}

void DisplayStudentOutput(Student stu, ostream& out, int index) {
	out << "********************STUDENT " << index << "********************";
	stu.Student::print(out);
	if (stu.exam_cnt != 0) {
		for (int i = 0; i < stu.exam_cnt; i++) {
			stu.exam[i].Exam::print(out);
		}
	}
	else {
		out << "---------------------------------------\n"
			<< "THIS STUDENT HAVEN'T TAKEN ANY EXAM YET\n"
			<< "---------------------------------------\n";
	}
	out << "\n\n";
}

const char* getRevisedGrade(Subject sub) {
	if (sub.marks >= 86) {
		return "A";
	}
	else if (sub.marks >= 81) {
		return "A-";
	}
	else if (sub.marks >= 76) {
		return "B+";
	}

	else if (sub.marks >= 71) {
		return "B";
	}
	else if (sub.marks >= 66) {
		return "B-";
	}
	else if (sub.marks >= 61) {
		return "C+";
	}
	else if (sub.marks >= 56) {
		return "C";
	}
	else if (sub.marks >= 46) {
		return "D";
	}
	else if (sub.marks >= 41) {
		return "D-";
	}
	else if (sub.marks >= 36) {
		return "E";
	}
	else if (sub.marks >= 0) {
		return "F";
	}
	else {
		return "N/A";
	}
}

double getRevisedGradePoint(Subject sub) {
	if (sub.marks >= 86) {
		return 4;
	}
	else if (sub.marks >= 81) {
		return 3.7;
	}
	else if (sub.marks >= 76) {
		return 3.3;
	}

	else if (sub.marks >= 71) {
		return 3.0;
	}
	else if (sub.marks >= 66) {
		return 2.7;
	}
	else if (sub.marks >= 61) {
		return 2.3;
	}
	else if (sub.marks >= 56) {
		return 2;
	}
	else if (sub.marks >= 46) {
		return 1.67;
	}
	else if (sub.marks >= 41) {
		return 1.33;
	}
	else if (sub.marks >= 36) {
		return 1;
	}
	else if (sub.marks >= 0) {
		return 0;
	}
	else {
		return -1;
	}
}

double calculateRevisedGPA(Exam exam) { //recalculate GPA of every exam using revised grading
	double points_sum = 0;
	int hours_sum = 0;
	double revised_gpa = 0;

	for (int i = 0; i < exam.numOfSubjects; i++) {
		points_sum += (getRevisedGradePoint(exam.sub[i])*exam.sub[i].credit_hours);
		hours_sum += (exam.sub[i].credit_hours);
	}
	revised_gpa = points_sum / double(hours_sum);
	return revised_gpa;
}

double calculateRevisedCGPA(Student stu) { //recalculate CGPA of student using revised grading
	double points_sum = 0;
	Exam temp;
	int sub_hours_sum = 0;
	int total_hours_sum = 0;
	double revised_cgpa = 0;

	for (int i = 0; i < stu.exam_cnt; i++) {
		for (int j = 0; j < stu.exam[i].numOfSubjects; j++) {
			sub_hours_sum += stu.exam[i].sub[j].credit_hours;
		}
		points_sum += calculateRevisedGPA(stu.exam[i]) * sub_hours_sum;
		total_hours_sum += sub_hours_sum;
	}
	revised_cgpa = points_sum / (double)total_hours_sum;
	return revised_cgpa;
}

void printSubjectRevised(Subject sub) { //print the subs and their attributes base on revised grading
	cout << "\n"
		<< sub.subject_code << "\t" << setw(70) << left << sub.subject_name << setw(7) << right << sub.credit_hours <<
		"\t" << setw(10) << left << " " << getRevisedGrade(sub) << "\t  " << setprecision(5) << fixed << showpoint <<
		getRevisedGradePoint(sub);
}

void printExamRevised(Exam exam) {
	cout << "\n\n" << exam.printTrimester() << " " << exam.year << " Exam Results: " << endl;

	cout << "\n" << exam.numOfSubjects << " subjects taken.";
	cout << "\n___________________________________________________________________________________________________________________________";
	cout << "\nSubject Code\t" << setw(70) << left << "Subject Name" << "Credit Hours" << "\tGrade " << "\tGrade Point";
	cout << "\n___________________________________________________________________________________________________________________________";
	for (int i = 0; i < exam.numOfSubjects; i++)
		printSubjectRevised(exam.sub[i]);

	cout << "\nGPA: " << exam.gpa;

	cout << "\n\n";

}