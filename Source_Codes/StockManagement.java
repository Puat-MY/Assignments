import java.util.ArrayList;
import java.util.Date;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class StockManagement extends Application{
	private static ArrayList<Product> product = new ArrayList<Product>(); //list of added products
	private static String[] choiceOfProducts = {"Refrigerator", "TV", "Electric Kettle"}; //list of available products in store
	private static UserInfo user = new UserInfo(); //user info
	private static int maxNum; //used to manage maximum product count to be stored in the system
	private static int menuChoice; //used by menuOfSystem to store correct values that trigger appropriate stock methods in menuChoice method
	
	//start a thread that loops infinitely to update date in real-time
	public Text clockThread(Stage primaryStage){
		Text time = new Text();
	    Thread clock = new Thread() {
	        public void run() {
	            for(;;) {
	            	time.setText("Current time: \n" + new Date());	
	                try {
						sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
	               if(!primaryStage.isShowing()) break; //stop the thread after program closed
	            } //while loop is not used because the thread starts before stage pops up -> date will not show if this happens
	        }
	    };
	    clock.start();
	    return time;
	}
	
	//display main menu
	public void start(Stage primaryStage) {
		
		BorderPane pane = new BorderPane(); //main pane
		
		//top node -> show clock, user info, and title
		BorderPane top = new BorderPane();
		top.setPadding(new Insets(10));
		
		//clock node
		Text date = clockThread(primaryStage); //start clock thread (updates real-time)
		date.setFill(Color.BLUE);
		date.setOnMouseEntered(e->{ //update clock manually by moving cursor onto it, used when the clock stops due to any reason
			date.setText("Current time:\n" + new Date());
		});
		top.setLeft(date);
		BorderPane.setAlignment(date, Pos.TOP_LEFT);
		
		//user info node
		Text userInfo = new Text(user.toString());
		userInfo.setFill(Color.BLUE);
		userInfo.setOnMouseEntered(e->{ //update user info manually by moving cursor onto it
			userInfo.setText(user.toString());
		});
		top.setRight(userInfo);
		BorderPane.setAlignment(userInfo, Pos.TOP_RIGHT);
		
		//title node
		Label title = new Label("Welcome to Storage Management System (SMS)");
		title.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.ITALIC, 20));
		top.setBottom(title);
		BorderPane.setAlignment(title, Pos.CENTER);
		
		//setup top node
		pane.setTop(top);
		BorderPane.setAlignment(pane, Pos.TOP_CENTER);
		
		//center node -> display group info
		//groupInfo node -> display member names and id, not editable
		TextArea groupInfo = new TextArea();
		groupInfo.setWrapText(true);
		groupInfo.setEditable(false);
		//add text to groupInfo node
		groupInfo.appendText("Group members:");
		ArrayList<String> namelist = new ArrayList<String>();
		namelist.add("Puat Eng Sing (20ACB05577)");
		namelist.add("Phua Wen Shen (20ACB02795)");
		namelist.add("Connie Tang Ming Xin (21ACB06403)");
		namelist.add("Teng Wai Kie (20ACB04876)");
		namelist.sort(null);
		for(int i = 0; i < namelist.size(); i++) {
			groupInfo.appendText("\n" + (i+1) + ": " + namelist.get(i));
		}
		groupInfo.appendText("\nPress ESCAPE to force exit without exit prompt.");
		groupInfo.appendText("\nMouse over time or user information if they are not updating.");
		groupInfo.setPrefRowCount(7);
		
		//setup center node
		pane.setCenter(groupInfo);
		BorderPane.setAlignment(groupInfo, Pos.TOP_CENTER);
		
		//bottom node -> main menu with 4 buttons
		HBox bottom = new HBox(10);
		bottom.setPadding(new Insets(10));
		Button login = new Button("Login");
		Button addProduct = new Button("Add Product");
		Button menu = new Button("Menu");
		Button exit = new Button("Exit");
		bottom.setAlignment(Pos.TOP_CENTER);
		bottom.getChildren().addAll(login, addProduct, menu, exit);
		pane.setBottom(bottom); //setup bottom node
		
		//set events to trigger when buttons were clicked
		login.setOnAction(e->{
			user.getName(); //call for user info stage
			userInfo.setText(user.toString()); //put user info up to userInfo text box
		});
		login.setOnKeyPressed(e->{
			//when UP arrow key is pressed, no change of focused node -> prevent user from selecting groupInfo
			if(e.getCode() == KeyCode.UP) {
				login.requestFocus();
			}
		});
		addProduct.setOnAction(e->{
			//call stage for product count input, then call stage for add product until maximum product count reached
			addProduct(product, maxNumberProductToStore(primaryStage));
		});
		addProduct.setOnKeyPressed(e->{
			//does same thing as line 131, and so on for menu and exit buttons
			if(e.getCode() == KeyCode.UP) {
				addProduct.requestFocus();
			}
		});
		menu.setOnAction(e->{
			Stage stage = new Stage();
			//call stage for menuChoice input, then call menuChoice method to trigger appropriate methods
			menuChoice(product, menuOfSystem(stage), stage);
		});
		menu.setOnKeyPressed(e->{
			if(e.getCode() == KeyCode.UP) {
				menu.requestFocus();
			}
		});
		exit.setOnAction(e->{
			//exit pop-up
			VBox box = new VBox(5);
			box.setPadding(new Insets(10));
			Text t1 = new Text("Thanks for using SMS.");
			Text t2 = new Text(user.toString());
			Text t3 = new Text("Press ESCAPE to exit.");
			box.getChildren().addAll(t1, t2, t3);
			
			Stage stage = new Stage();
			Scene scene = new Scene(box, 250, 100);
			stage.setTitle("Exit");
			stage.setResizable(false);
			stage.setScene(scene);
			stage.show();
			
			//close both pop-up and main window when user press ESCAPE
			scene.setOnKeyPressed(f->{
				if(f.getCode() == KeyCode.ESCAPE) {
					stage.close();
					primaryStage.close();
				}
			});
			
			//close the main window when pop-up window closes
			stage.setOnCloseRequest(f->{
				primaryStage.close();
			});
		});
		exit.setOnKeyPressed(e->{
			if(e.getCode() == KeyCode.UP) {
				exit.requestFocus();
			}
		});
		//setup scene & stage of main menu
		Scene scene = new Scene(pane, 500, 250);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Storage Management System");
		primaryStage.setResizable(false);
		primaryStage.show(); //then put it up on the screen
		login.requestFocus(); //select a default button (login)
		
		//close main window when user press ESCAPE
		scene.setOnKeyPressed(e->{
			if(e.getCode() == KeyCode.ESCAPE) {
				primaryStage.close();
			}
		});
		//if user tries to select groupInfo by mouse click, set the "select" to login button
		groupInfo.setOnMouseClicked(e->{
			login.requestFocus();
		});
		//if user tries to select groupInfo by keyboard, set the "select" to login button
		groupInfo.setOnKeyReleased(e->{
			login.requestFocus();
		});
	}
	
	//(1) Get the maximum number of products the user wish to store in the system.
	public static int maxNumberProductToStore(Stage primaryStage){
		maxNum = -1; //reset maxNum
		product.clear(); //reset product ArrayList
		
		//create sub-stage for this method to carry out individual functions
		Stage stage = new Stage();
		
		//main pane
		BorderPane pane = new BorderPane();
		pane.setPadding(new Insets(10));
		
		//top node -> subtitle
		Label subtitle = new Label("Please enter number of products to store in the system."
				+ "\nOnly positive integers and 0 are accepted. Enter 0 to exit the whole program."
				+ "\nNote that both maximum number of products and previously added products were resetted upon entering this prompt.");
		subtitle.setWrapText(true);
		
		pane.setTop(subtitle);
		BorderPane.setAlignment(subtitle, Pos.CENTER_LEFT);
		
		//center node -> take inputs
		VBox centerBox = new VBox(10);
		centerBox.setAlignment(Pos.CENTER);
		
		//input label & input field
		Label label = new Label("Number of Products to add: ");
		TextField tf = new TextField();
		
		//create a box to hold input label and field
		HBox fieldBox = new HBox(10);
		fieldBox.setAlignment(Pos.CENTER_LEFT);
		fieldBox.getChildren().addAll(label, tf);
		
		//error message node
		Text errmsg = new Text();
		errmsg.setFill(Color.RED);
		
		//buttons & button box
		Button submit = new Button("Submit");
		Button cancel = new Button("Cancel");
		HBox buttonBox = new HBox(10);
		buttonBox.setAlignment(Pos.CENTER);
		buttonBox.getChildren().addAll(submit, cancel);
		
		//setup them onto the center node
		centerBox.getChildren().addAll(fieldBox, errmsg, buttonBox);
		pane.setCenter(centerBox);
		
		//move to submit button when user press ENTER or DOWN arrow key
		tf.setOnKeyPressed(e->{
			if(e.getCode() == KeyCode.ENTER || e.getCode() == KeyCode.DOWN) {
				submit.requestFocus();
			}
		});
		
		submit.setOnAction(e->{
			//parse input text into maxNum with error handling
			try {
				maxNum = Integer.parseInt(tf.getText());
			}catch(NumberFormatException ex) { //triggered if input format is not valid such as NULL and String
				tf.setText("");
			}
			if(maxNum < 0) { //pop error message if maxNum is negative
				errmsg.setText("Invalid input format, only positive integers and 0 are accepted");
				tf.requestFocus();
			}
			else if (maxNum == 0){ //pop exit prompt if maxNum equals 0
				Stage exitStage = new Stage();
				BorderPane exitPane = new BorderPane();
				exitPane.setPadding(new Insets(10));
				Text exitText = new Text("It seems like you do not wish to add any product, do you want to exit?"
						+ "\nEnter 0 if you confirm to exit the program."
						+ "\nPress ESCAPE to close this prompt.");
				TextField exitTextField= new TextField();
				exitPane.setTop(exitText);
				BorderPane.setAlignment(exitText, Pos.TOP_LEFT);
				exitPane.setCenter(exitTextField);
				BorderPane.setAlignment(exitTextField, Pos.CENTER_LEFT);
				Scene exitScene = new Scene(exitPane, 350, 150);
				exitStage.setTitle("Exit");
				exitStage.setScene(exitScene);
				exitStage.setResizable(false);
				exitStage.show();
				exitText.setWrappingWidth(exitStage.getWidth() - 30);
				exitText.setTextAlignment(TextAlignment.JUSTIFY);
				exitTextField.setMaxWidth(exitStage.getWidth() - 30);
				exitTextField.setOnKeyPressed(f->{
					if(f.getCode() == KeyCode.ENTER) {
						if(exitTextField.getText().trim().equals("0")) {
							exitStage.close();
							stage.close();
							primaryStage.close();
						}
					}
					else if(f.getCode() == KeyCode.ESCAPE) {
						exitStage.close();
					}
				});
			}else { //otherwise (maxNum is positive integer), succesful input -> close window
				stage.close();
			}
		});
		submit.setOnKeyPressed(e->{ //control events triggered by arrow keys so user can perform task with their keyboard
			if(e.getCode() == KeyCode.UP) {
				tf.requestFocus();
			}
			else if (e.getCode() == KeyCode.DOWN) {
				cancel.requestFocus();
			}

		});
		cancel.setOnAction(e->{ //user cancels input -> reset maxNum (to prevent possible issues on other methods) & close window
			maxNum = -1;
			stage.close();
		});
		cancel.setOnKeyPressed(e->{ //see line 314
			if(e.getCode() == KeyCode.UP) {
				submit.requestFocus();
			}
		});
		
		Scene scene = new Scene(pane, 450, 200);
		scene.setOnKeyPressed(e->{ //setup ESCAPE key to exit window
			if(e.getCode() == KeyCode.ESCAPE) {
				stage.close();
			}
		});
		
		stage.setScene(scene);
		stage.setTitle("Add Product");
		stage.setResizable(false);
		stage.showAndWait(); //launch the sub-stage, then blocks caller so subsequent processes cannot start until maxNum is set
		return maxNum;
	}
	
	
	//(2) Display the contents of the products array
	public static void contentsOfTheProductsArray(ArrayList<Product> product, Button[] centerBoxButtons, BorderPane pane, Stage mainStage, TextField tf){
		//create another window to hold the brief content of products
		Stage stage = new Stage();
		
		//set the starting position of this window so it does not overlap with the stock window
		stage.setX(200);
		stage.setY(200);
		
		//main pane
		BorderPane innerPane = new BorderPane();
		innerPane.setPadding(new Insets(10));
		
		//center node -> holds product contents
		TextArea list = new TextArea();
		list.setEditable(false);
		innerPane.setCenter(list);
		
		//launch the stage
		Scene scene = new Scene(innerPane, 400, 400);
		stage.setScene(scene);
		stage.setTitle("List of Products");
		stage.show();
		
		//makes user able to go back to mainStage with only their keyboard instead of using mouse click
		list.setOnKeyPressed(e->{
			mainStage.requestFocus();
		});
		
		//add additional function to Open/Update List button (see addStock method)
		centerBoxButtons[0].setOnAction(e->{
			if(!stage.isShowing()) { //open up list stage if it is not on
				stage.show();
			}
			
			//put up product contents onto center node (TextArea)
			String text = "";
			list.setText("");
			if(product.size() == 0) {
				text += "No product is added yet.";
			}
			else {
				text += "Here are the products in stock:"
						+ "\n<product name> : <quantity>\n";
				for(int i = 1; i <= product.size(); i++) {
					text += "\n" + i + ". " + product.get(i-1).getName() 
							+ " : " + product.get(i - 1).getQuantity()
							+ (product.get(i-1).getStatus()? "" : " (Discontinued)");
				}
			}
			list.appendText(text);
		});
		centerBoxButtons[0].fire(); //trigger the Open/Update List button once whenever this method runs
		
		//add additional function to Close List button (see addStock method)
		centerBoxButtons[1].setOnAction(e->{
			if(stage.isShowing()) //close list stage if it is on
				stage.close(); 
		});
		
		//when main stage (stock menu) is closed, close the list stage.
		mainStage.setOnHidden(e->{
			stage.close();
		});
	}
	
	//(3) Display stock menu
	public static int menuOfSystem(Stage stage) {
		menuChoice = -1; //reset menuChoice
		
		//main pane with a vbox -> holds subtitle and buttons for this method
		//clicking on different buttons set menuChoice to different value -> trigger different methods
		VBox mainBox = new VBox(10);
		mainBox.setPadding(new Insets(10));
		mainBox.setAlignment(Pos.TOP_CENTER);
		Label subtitle = new Label("Select an option.\n"
				+ "You can use UP/DOWN arrow key to select.");
		subtitle.setAlignment(Pos.TOP_LEFT);
		mainBox.getChildren().add(subtitle);
		
		//buttons
		Button[] buttons = new Button[5];
		buttons[0] = new Button("View products");
		buttons[1] = new Button("Add Stock");
		buttons[2] = new Button("Deduct stock");
		buttons[3] = new Button("Discontinue product");
		buttons[4] = new Button("Exit");
		for(Button b: buttons) {
			b.setMinWidth(150);
			b.setTextAlignment(TextAlignment.CENTER);
			mainBox.getChildren().add(b);
		}
		
		//setup scene. Again, set ESCAPE key as an exit method
		Scene scene = new Scene(mainBox, 300, 250);
		scene.setOnKeyPressed(e->{
			if(e.getCode() == KeyCode.ESCAPE) {
				stage.close();
			}
		});
		buttons[0].setOnAction(e->{ //view products
			menuChoice = 1;
			stage.close();
		});
		buttons[1].setOnAction(e->{ //add stock
			menuChoice = 2;
			stage.close();
		});
		buttons[2].setOnAction(e->{ //deduct stock
			menuChoice = 3;
			stage.close();
		});
		buttons[3].setOnAction(e->{ //discontinue product
			menuChoice = 4;
			stage.close();
		});
		buttons[4].setOnAction(e->{ //exit
			menuChoice = 0;
			stage.close();
		});
		stage.setScene(scene);
		stage.setTitle("Menu");
		stage.setResizable(false);
		stage.showAndWait(); //launch the stock menu then blocks the caller so stock methods only can run after stock menu finishes setting up menuChoice
		return menuChoice;
	}
	
	//(4) Add Stock of a fixed amount of products determine by the user
	public static void addStock(ArrayList<Product> product, Stage stage) {
		
		//main pane -> holds nodes for two functionalities (add stock & view products)
		BorderPane pane = new BorderPane();
		pane.setPadding(new Insets(10));
		
		//top node -> holds nodes for add stock functionality
		VBox topBox = new VBox(10);
		topBox.setPadding(new Insets(10));
		topBox.setAlignment(Pos.TOP_CENTER);
		
		Label mainLabel = new Label("Add Stock (Enter 0 to exit.)");
		topBox.getChildren().add(mainLabel);
		
		Label[] labels = new Label[2];
		TextField[] tfs = new TextField[2];
		HBox[] hbs = new HBox[2];
		labels[0] = new Label("Index of product to add:");
		labels[1] = new Label("Quantity to add:");
		for(int i = 0; i < hbs.length; i++) {
			labels[i].setMinWidth(150);
			labels[i].setAlignment(Pos.CENTER_RIGHT);
			tfs[i]= new TextField();
			hbs[i] = new HBox(10);
			hbs[i].getChildren().addAll(labels[i], tfs[i]);
			topBox.getChildren().add(hbs[i]);
		}
		Button submit = new Button("Submit");
		Button cancel = new Button("Cancel");
		HBox buttonBox = new HBox(10);
		buttonBox.setAlignment(Pos.TOP_CENTER);
		buttonBox.getChildren().addAll(submit, cancel);
		
		Text errmsg = new Text();
		errmsg.setFill(Color.RED);
		topBox.getChildren().addAll(buttonBox, errmsg);
		
		pane.setTop(topBox);
		
		//center node -> holds nodes for view product functionality
		Label subtitle = new Label("View Product");
		Text text = new Text("The below buttons interact with List of Products window.");
		text.setTextAlignment(TextAlignment.CENTER);
		text.setFont(Font.font("System Regular", FontWeight.NORMAL, FontPosture.ITALIC, 11));
		text.setWrappingWidth(300);
		subtitle.setAlignment(Pos.TOP_CENTER);
		Button[] centerBoxButtons = new Button[2];
		centerBoxButtons[0] = new Button("Open/Update List");
		centerBoxButtons[1] = new Button("Close List");
		HBox centerButtonBox = new HBox(10);
		for(Button i: centerBoxButtons) {
			centerButtonBox.getChildren().add(i);
		}
		centerButtonBox.setAlignment(Pos.TOP_CENTER);
		
		VBox centerBox = new VBox(10);
		centerBox.setAlignment(Pos.TOP_CENTER);
		centerBox.getChildren().addAll(subtitle, text, centerButtonBox);
		
		pane.setCenter(centerBox);
		
		//setup scene & ESCAPE key
		Scene scene = new Scene(pane, 400, 500);
		scene.setOnKeyPressed(e->{
			if(e.getCode() == KeyCode.ESCAPE) {
				stage.close();
			}
		});
		//setup stage position
		stage.setX(800);
		stage.setY(200);
		stage.setTitle("Add Stock");
		stage.setScene(scene);
		stage.show();
		//first TextField -> product index
		tfs[0].setOnKeyPressed(e->{
			errmsg.setText(""); //reset error message whenever button is clicked
			if(e.getCode() == KeyCode.UP) { //setup UP arrow key to switch node
				tfs[0].requestFocus();
			}
			else if(e.getCode() == KeyCode.DOWN || e.getCode() == KeyCode.ENTER) { //setup DOWN arrow key & ENTER to switch node
				tfs[1].requestFocus();
				if(e.getCode() == KeyCode.ENTER) { //add additional checking to ENTER key because ENTER usually means "confirm input"
					if(tfs[0].getText() == "") { //null input, abort
						errmsg.setText("Product index cannot be blank.");
						tfs[0].requestFocus();
					}
					else {
						int temp = -1; //setup variable for checking input format
						try {
							temp = Integer.parseInt(tfs[0].getText());
						}catch(NumberFormatException ex) { //triggers on wrong format
							tfs[0].requestFocus();
						}
						//if exception was caught, no change to value of temp -> next line is true
						if (temp < 0 || temp > product.size()) { //invalid input (wrong format/too large), abort
							errmsg.setText("Invalid product index.");
							tfs[0].requestFocus();
						}
						else if(temp == 0) { //input is 0 -> close Add Stock stage
							stage.close();
						}
					}
				}
			}

		});
		//second TextField -> quantity, similar structure as first TextField
		tfs[1].setOnKeyPressed(e->{
			errmsg.setText("");
			if(e.getCode() == KeyCode.UP) {
				tfs[0].requestFocus();
			}
			else if (e.getCode() == KeyCode.DOWN || e.getCode() == KeyCode.ENTER){
				submit.requestFocus();
				if(e.getCode() == KeyCode.ENTER) {
					if(tfs[1].getText() == "") {
						errmsg.setText("Quantity cannot be blank.");
						tfs[1].requestFocus();
					}
					else {
						int temp = -1;
						try {
							temp = Integer.parseInt(tfs[1].getText());
						}catch(NumberFormatException ex) {
							tfs[1].requestFocus();
						}
						if(temp < 0) {
							errmsg.setText("Quantity must be 0 or a positive integer.");
							tfs[1].requestFocus();
						}
						else if (temp == 0) {
							stage.close();
						}
					}
				}
			}
		});
		submit.setOnAction(e->{
			int index = -1, quantity = -1; //two integers for checking both tfs[0] and tfs[1]
			if(tfs[0].getText() == "" || tfs[1].getText() == "") { //blank input, abort
				errmsg.setText("Blank inputs are not accepted.");
				tfs[0].requestFocus();
			}
			else {
				//try parsing TextFields as integer to check format of inputs
				try {
					index = Integer.parseInt(tfs[0].getText());
					quantity = Integer.parseInt(tfs[1].getText());
				}catch(NumberFormatException ex) {
					tfs[0].requestFocus();
				}
				//index and quantity do not change without proper parsing -> next line is true
				if(index < 0 || index > product.size()) { //invalid input at tfs[0], abort
					errmsg.setText("Invalid product index.");
					tfs[0].requestFocus();
				}
				else if (quantity < 0) { //invalid input at tfs[1], abort
					errmsg.setText("Quantity must be 0 or a positive integer.");
					tfs[1].requestFocus();
				}
				else { //correct input
					errmsg.setText("");
					//search for the product, then trigger addQuantity method which is under Product class
					for(Product i: product) {
						if(index - 1 == product.indexOf(i)) { //product found
							if(!i.getStatus()) { //discontinued product -> abort
								errmsg.setText("Product was discontinued, cannot add stock.");
								tfs[0].requestFocus();
							}
							else { //active product -> continue
								i.addQuantity(quantity);
								tfs[0].setText("");
								tfs[1].setText("");
								tfs[0].requestFocus();
							}
							break; //stop the loop right after addQuantity because this only needs to be done once -> subsequent loops are meaningless

						}
					}
				}
			}
			centerBoxButtons[0].fire(); //trigger Open/Update List button after change quantity to update List of Products stage
		});
		//setup UP and DOWN arrow keys to alter between nodes
		submit.setOnKeyPressed(e->{
			if(e.getCode() == KeyCode.UP) {
				tfs[1].requestFocus();
			}
			else if(e.getCode() == KeyCode.DOWN) {
				cancel.requestFocus();
			}
		});
		//user cancels input -> close add stock stage
		cancel.setOnAction(e->{
			stage.close();
		});
		//setup arrow keys
		cancel.setOnKeyPressed(e->{
			if(e.getCode() == KeyCode.UP) {
				submit.requestFocus();
			}
			else if(e.getCode() == KeyCode.DOWN) {
				centerBoxButtons[0].requestFocus();
			}
		});
		centerBoxButtons[0].setOnKeyPressed(e->{
			if(e.getCode() == KeyCode.UP) {
				cancel.requestFocus();
			}
			else if(e.getCode() == KeyCode.DOWN) {
				centerBoxButtons[1].requestFocus();
			}
		});
		centerBoxButtons[1].setOnKeyPressed(e->{
			if(e.getCode() == KeyCode.UP) {
				centerBoxButtons[0].requestFocus();
			}
		});
		//launch the List of Products stage
		contentsOfTheProductsArray(product, centerBoxButtons, pane, stage, tfs[1]);
		stage.requestFocus(); //then select stock stage
	}
	
	// (5) Deduct Stock of a fixed amount of products determine by the user
	public static void deductStock(ArrayList<Product> product, Stage stage) {
		//the documentation of this method is lighter
		//because it has very similar structure to addStock method except extra checking for quantity under submit.setOnAction
		
		//main pane
		BorderPane pane = new BorderPane();
		pane.setPadding(new Insets(10));
		
		//top node
		VBox topBox = new VBox(10);
		topBox.setPadding(new Insets(10));
		topBox.setAlignment(Pos.TOP_CENTER);
		
		Label mainLabel = new Label("Deduct Stock (Enter 0 to exit.)");
		topBox.getChildren().add(mainLabel);
		
		Label[] labels = new Label[2];
		TextField[] tfs = new TextField[2];
		HBox[] hbs = new HBox[2];
		labels[0] = new Label("Index of product to deduct:");
		labels[1] = new Label("Quantity to deduct:");
		for(int i = 0; i < hbs.length; i++) {
			labels[i].setMinWidth(150);
			labels[i].setAlignment(Pos.CENTER_RIGHT);
			tfs[i]= new TextField();
			hbs[i] = new HBox(10);
			hbs[i].getChildren().addAll(labels[i], tfs[i]);
			topBox.getChildren().add(hbs[i]);
		}
		Button submit = new Button("Submit");
		Button cancel = new Button("Cancel");
		HBox buttonBox = new HBox(10);
		buttonBox.setAlignment(Pos.TOP_CENTER);
		buttonBox.getChildren().addAll(submit, cancel);
		
		Text errmsg = new Text();
		errmsg.setFill(Color.RED);
		topBox.getChildren().addAll(buttonBox, errmsg);
		
		pane.setTop(topBox);
		
		//center node
		Label subtitle = new Label("View Product");
		Text text = new Text("The below buttons interact with List of Products window.");
		text.setTextAlignment(TextAlignment.CENTER);
		text.setFont(Font.font("System Regular", FontWeight.NORMAL, FontPosture.ITALIC, 11));
		text.setWrappingWidth(300);
		subtitle.setAlignment(Pos.TOP_CENTER);
		Button[] centerBoxButtons = new Button[2];
		centerBoxButtons[0] = new Button("Open/Update List");
		centerBoxButtons[1] = new Button("Close List");
		HBox centerButtonBox = new HBox(10);
		for(Button i: centerBoxButtons) {
			centerButtonBox.getChildren().add(i);
		}
		centerButtonBox.setAlignment(Pos.TOP_CENTER);

		VBox centerBox = new VBox(10);
		centerBox.setAlignment(Pos.TOP_CENTER);
		centerBox.getChildren().addAll(subtitle, text, centerButtonBox);
		
		pane.setCenter(centerBox);
		
		//launch Deduct Stock stage & setup position
		Scene scene = new Scene(pane, 400, 500);
		scene.setOnKeyPressed(e->{
			if(e.getCode() == KeyCode.ESCAPE) {
				stage.close();
			}
		});
		stage.setX(800);
		stage.setY(200);
		stage.setTitle("Deduct Stock");
		stage.setScene(scene);
		stage.show();
		
		//button triggers with proper error handling
		tfs[0].setOnKeyPressed(e->{ //product index
			errmsg.setText("");
			if(e.getCode() == KeyCode.UP) {
				tfs[0].requestFocus();
			}
			else if(e.getCode() == KeyCode.DOWN || e.getCode() == KeyCode.ENTER) {
				tfs[1].requestFocus();
				if(e.getCode() == KeyCode.ENTER) {
					if(tfs[0].getText() == "") {
						errmsg.setText("Product index cannot be blank.");
						tfs[0].requestFocus();
					}
					else {
						int temp = -1;
						try {
							temp = Integer.parseInt(tfs[0].getText());
						}catch(NumberFormatException ex) {
							tfs[0].requestFocus();
						}
						if (temp < 0 || temp > product.size()) {
							errmsg.setText("Invalid product index.");
							tfs[0].requestFocus();
						}
						else if(temp == 0) {
							stage.close();
						}
					}
				}
			}

		});
		tfs[1].setOnKeyPressed(e->{ //quantity
			errmsg.setText("");
			if(e.getCode() == KeyCode.UP) {
				tfs[0].requestFocus();
			}
			else if (e.getCode() == KeyCode.DOWN || e.getCode() == KeyCode.ENTER){
				submit.requestFocus();
				if(e.getCode() == KeyCode.ENTER) {
					if(tfs[1].getText() == "") {
						errmsg.setText("Quantity cannot be blank.");
						tfs[1].requestFocus();
					}
					else {
						int temp = -1;
						try {
							temp = Integer.parseInt(tfs[1].getText());
						}catch(NumberFormatException ex) {
							tfs[1].requestFocus();
						}
						if(temp < 0) {
							errmsg.setText("Quantity must be 0 or a positive integer.");
							tfs[1].requestFocus();
						}
						else if (temp == 0) {
							stage.close();
						}
					}
				}
			}
		});
		submit.setOnAction(e->{
			int index = -1, quantity = -1;
			if(tfs[0].getText() == "" || tfs[1].getText() == "") {
				errmsg.setText("Blank inputs are not accepted.");
				tfs[0].requestFocus();
			}
			else {
				try {
					index = Integer.parseInt(tfs[0].getText());
					quantity = Integer.parseInt(tfs[1].getText());
				}catch(NumberFormatException ex) {
					tfs[0].requestFocus();
				}
				if(index < 0 || index > product.size()) {
					errmsg.setText("Invalid product index.");
					tfs[0].requestFocus();
				}
				else {
					errmsg.setText("");
					//find matching product and trigger deductQuantity method under Product class
					for(Product i: product) {
						if(index - 1 == product.indexOf(i)) {
							if(!i.getStatus()) {
								errmsg.setText("Product was discontinued, cannot deduct stock.");
								tfs[0].requestFocus();
							}
							else {
								if(quantity > i.getQuantity()) { //quantity to deduct is higher than quantity in stock -> abort
									errmsg.setText("There is not enough stock to be deducted.");
									tfs[1].requestFocus();
								}
								else if (quantity < 0) { //quantity is negative -> abort
									errmsg.setText("Quantity must be 0 or a positive integer.");
									tfs[1].requestFocus();
								}
								else { //quantity is positive & within range -> continue
									i.deductQuantity(quantity);
									tfs[0].setText("");
									tfs[1].setText("");
									tfs[0].requestFocus();
								}
							}
							break; //only one time of operation needed -> break loop
						}
					}
				}
			}
			centerBoxButtons[0].fire();
		});
		//setup arrow keys
		submit.setOnKeyPressed(e->{
			if(e.getCode() == KeyCode.UP) {
				tfs[1].requestFocus();
			}
			else if(e.getCode() == KeyCode.DOWN) {
				cancel.requestFocus();
			}
		});
		//close Deduct Stock stage
		cancel.setOnAction(e->{
			stage.close();
		});
		cancel.setOnKeyPressed(e->{
			if(e.getCode() == KeyCode.UP) {
				submit.requestFocus();
			}
			else if(e.getCode() == KeyCode.DOWN) {
				centerBoxButtons[0].requestFocus();
			}
		});
		centerBoxButtons[0].setOnKeyPressed(e->{
			if(e.getCode() == KeyCode.UP) {
				cancel.requestFocus();
			}
			else if(e.getCode() == KeyCode.DOWN) {
				centerBoxButtons[1].requestFocus();
			}
		});
		centerBoxButtons[1].setOnKeyPressed(e->{
			if(e.getCode() == KeyCode.UP) {
				centerBoxButtons[0].requestFocus();
			}
		});
		//launch List of Products stage
		contentsOfTheProductsArray(product, centerBoxButtons, pane, stage, tfs[1]);
		stage.requestFocus();
	}
	
	// (6) Set Status of a product chosen by the user
	public static void setStatus(ArrayList<Product> product, Stage stage) {
		//this method, like deductProduct, has very similar structure to addProduct except little different checking under button events
		//light documentation ahead
		
		//main pane
		BorderPane pane = new BorderPane();
		pane.setPadding(new Insets(10));
		
		//top node
		VBox topBox = new VBox(10);
		topBox.setPadding(new Insets(10));
		topBox.setAlignment(Pos.TOP_CENTER);
		
		Label mainLabel = new Label("Update Status");
		Text guide = new Text("Enter 0 at Index of product to exit\n"
				+ "Status: 0 - Discontinued, 1 - Active");
		guide.setTextAlignment(TextAlignment.CENTER);
		guide.setWrappingWidth(300);
		topBox.getChildren().addAll(mainLabel, guide);
		
		Label[] labels = new Label[2];
		TextField[] tfs = new TextField[2];
		HBox[] hbs = new HBox[2];
		labels[0] = new Label("Index of product:");
		labels[1] = new Label("Status (0 / 1):");
		for(int i = 0; i < hbs.length; i++) {
			labels[i].setMinWidth(150);
			labels[i].setAlignment(Pos.CENTER_RIGHT);
			tfs[i]= new TextField();
			hbs[i] = new HBox(10);
			hbs[i].getChildren().addAll(labels[i], tfs[i]);
			topBox.getChildren().add(hbs[i]);
		}
		Button submit = new Button("Submit");
		Button cancel = new Button("Cancel");
		HBox buttonBox = new HBox(10);
		buttonBox.setAlignment(Pos.TOP_CENTER);
		buttonBox.getChildren().addAll(submit, cancel);
		
		Text errmsg = new Text();
		errmsg.setFill(Color.RED);
		topBox.getChildren().addAll(buttonBox, errmsg);
		
		pane.setTop(topBox);
		
		//center node
		Label subtitle = new Label("View Product");
		Text text = new Text("The below buttons interact with List of Products window.");
		text.setTextAlignment(TextAlignment.CENTER);
		text.setFont(Font.font("System Regular", FontWeight.NORMAL, FontPosture.ITALIC, 11));
		text.setWrappingWidth(300);
		subtitle.setAlignment(Pos.TOP_CENTER);
		Button[] centerBoxButtons = new Button[2];
		centerBoxButtons[0] = new Button("Open/Update List");
		centerBoxButtons[1] = new Button("Close List");
		HBox centerButtonBox = new HBox(10);
		for(Button i: centerBoxButtons) {
			centerButtonBox.getChildren().add(i);
		}
		centerButtonBox.setAlignment(Pos.TOP_CENTER);

		VBox centerBox = new VBox(10);
		centerBox.setAlignment(Pos.TOP_CENTER);
		centerBox.getChildren().addAll(subtitle, text, centerButtonBox);
		
		pane.setCenter(centerBox);
		
		//setup position & launch stage
		Scene scene = new Scene(pane, 400, 500);
		scene.setOnKeyPressed(e->{
			if(e.getCode() == KeyCode.ESCAPE) {
				stage.close();
			}
		});
		stage.setX(800);
		stage.setY(200);
		stage.setTitle("Deduct Stock");
		stage.setScene(scene);
		stage.show();
		//buttons & triggers
		tfs[0].setOnKeyPressed(e->{
			errmsg.setText("");
			if(e.getCode() == KeyCode.UP) {
				tfs[0].requestFocus();
			}
			else if(e.getCode() == KeyCode.DOWN || e.getCode() == KeyCode.ENTER) {
				tfs[1].requestFocus();
				if(e.getCode() == KeyCode.ENTER) {
					if(tfs[0].getText() == "") {
						errmsg.setText("Product index cannot be blank.");
						tfs[0].requestFocus();
					}
					else {
						int temp = -1;
						try {
							temp = Integer.parseInt(tfs[0].getText());
						}catch(NumberFormatException ex) {
							tfs[0].requestFocus();
						}
						if (temp < 0 || temp > product.size()) {
							errmsg.setText("Invalid product index.");
							tfs[0].requestFocus();
						}
						else if(temp == 0) {
							stage.close();
						}
					}
				}
			}
		});
		tfs[1].setOnKeyPressed(e->{ //status, input was fixed to either 0 or 1 only
			errmsg.setText("");
			if(e.getCode() == KeyCode.UP) {
				tfs[0].requestFocus();
			}
			else if (e.getCode() == KeyCode.DOWN || e.getCode() == KeyCode.ENTER){
				submit.requestFocus();
				if(e.getCode() == KeyCode.ENTER) {
					if(tfs[1].getText() == "") {
						errmsg.setText("Quantity cannot be blank.");
						tfs[1].requestFocus();
					}
					else {
						int temp = -1;
						try {
							temp = Integer.parseInt(tfs[1].getText());
						}catch(NumberFormatException ex) {
							tfs[1].requestFocus();
						}
						if(temp < 0 || temp > 1) { //limit input to 0 and 1 only
							errmsg.setText("Invalid input, only single digit 0 or 1 are accepted.");
							tfs[1].requestFocus();
						}
					}
				}
			}
		});
		submit.setOnAction(e->{
			int index = -1, status = -1;
			if(tfs[0].getText() == "" || tfs[1].getText() == "") {
				errmsg.setText("Blank inputs are not accepted.");
				tfs[0].requestFocus();
			}
			else {
				try {
					index = Integer.parseInt(tfs[0].getText());
					status = Integer.parseInt(tfs[1].getText());
				}catch(NumberFormatException ex) {
					tfs[0].requestFocus();
				}
				if(index < 0 || index > product.size()) {
					errmsg.setText("Invalid product index.");
					tfs[0].requestFocus();
				}
				else {
					errmsg.setText("");
					for(Product i: product) {
						if(index - 1 == product.indexOf(i)) {
							if(!i.getStatus() && status == 0) { //attempting to set "discontinued" product to "discontinued" -> abort
								errmsg.setText("Product was already discontinued.");
								tfs[0].requestFocus();
							}
							else if (i.getStatus() && status == 1) { //attempting to set "active" product to "active" -> abort
								errmsg.setText("Product was already active.");
								tfs[0].requestFocus();
							}
							else { //set discontinued to active OR active to discontinued -> continue
								i.setStatus((status == 1? true:false));
								tfs[0].setText("");
								tfs[1].setText("");
								tfs[0].requestFocus();
							}
							break;
						}
					}
				}
			}
			centerBoxButtons[0].fire();
		});
		//setup arrow keys
		submit.setOnKeyPressed(e->{
			if(e.getCode() == KeyCode.UP) {
				tfs[1].requestFocus();
			}
			else if(e.getCode() == KeyCode.DOWN) {
				cancel.requestFocus();
			}
		});
		//close Deduct Stock stage
		cancel.setOnAction(e->{
			stage.close();
		});
		cancel.setOnKeyPressed(e->{
			if(e.getCode() == KeyCode.UP) {
				submit.requestFocus();
			}
			else if(e.getCode() == KeyCode.DOWN) {
				centerBoxButtons[0].requestFocus();
			}
		});
		centerBoxButtons[0].setOnKeyPressed(e->{
			if(e.getCode() == KeyCode.UP) {
				cancel.requestFocus();
			}
			else if(e.getCode() == KeyCode.DOWN) {
				centerBoxButtons[1].requestFocus();
			}
		});
		centerBoxButtons[1].setOnKeyPressed(e->{
			if(e.getCode() == KeyCode.UP) {
				centerBoxButtons[0].requestFocus();
			}
		});
		//launch List of Products stage
		contentsOfTheProductsArray(product, centerBoxButtons, pane, stage, tfs[1]);
		stage.requestFocus();
	}
	
	// (7) show the switch case that takes in the menu choice, products array and scanner
	// refer to menuOfSystem()
	public static void menuChoice(ArrayList<Product> product, int menuChoice, Stage stage) {
		switch (menuChoice) { //call appropriate method on different value of menuChoice
		case 1:
			viewProduct(product, stage);
			break;
		case 2:
			addStock(product, stage);
			break;
		case 3:
			deductStock(product, stage);
			break;
		case 4: 
			setStatus(product, stage);
			break;
		case 0: //exit -> do nothing
			break;
		}
	}
	
	//(8) allow user to add products that exist in the store
	public static void addProduct(ArrayList<Product> product, int count) {
		//abort if there is no product to add
		//prevent user from launching Add Product stage without valid maxNum value
		if(count <= 0) return;
		
		//Add Product stage
		Stage stage = new Stage();
		
		//main pane -> holds method subtitle, a group of radiobuttons, and appropriate box for receiving list of inupts based on methods called to add product
		BorderPane pane = new BorderPane();
		pane.setPadding(new Insets(10));
		
		//right node -> subtitle & radio buttons
		Label subtitle = new Label("Select a product to add."
				+ "\nYou can use UP / DOWN arrow keys to choose."
				+ "\nPress ENTER to select. Press ESCAPE to exit.");
		
		ToggleGroup group = new ToggleGroup();
		RadioButton[] radiobuttons = new RadioButton[choiceOfProducts.length];
		for(int i = 0; i < radiobuttons.length; i++) {
			radiobuttons[i] = new RadioButton(choiceOfProducts[i]);
			radiobuttons[i].setToggleGroup(group);
		}

		VBox radioButtonBox = new VBox(10);
		radioButtonBox.getChildren().add(subtitle);
		radioButtonBox.setPadding(new Insets(10));
		for(RadioButton i: radiobuttons) {
			radioButtonBox.getChildren().add(i);
		}
		pane.setRight(radioButtonBox);
		
		//launch Add Product stage
		Scene scene = new Scene(pane, 600, 350);
		stage.setScene(scene);
		stage.setTitle("Add Product");
		stage.setResizable(false);
		stage.show();
		scene.setOnKeyPressed(e->{
			if(e.getCode() == KeyCode.ESCAPE) {
				stage.close();
			}
		});
		
		//setup radio button events
		radiobuttons[0].setOnAction(e->{ //refrigerator
			addRefrigerator(product, pane);
		});
		radiobuttons[0].setOnKeyPressed(e->{ //setup button to accept ENTER key as confirmation
			if(e.getCode() == KeyCode.ENTER) {
				addRefrigerator(product, pane);
			}
		});
		radiobuttons[1].setOnAction(e->{ //TV
			addTV(product, pane);
		});
		radiobuttons[1].setOnKeyPressed(e->{
			if(e.getCode() == KeyCode.ENTER) {
				addTV(product,pane);
			}
		});
		radiobuttons[2].setOnAction(e->{ //Kettle
			addKettle(product, pane);
		});
		radiobuttons[2].setOnKeyPressed(e->{
			if(e.getCode() == KeyCode.ENTER) {
				addKettle(product, pane);
			}
		});
		radiobuttons[0].setSelected(true);
		radiobuttons[0].fire(); //
	}
	//(9) add refrigerator product
	public static void addRefrigerator(ArrayList<Product> product, BorderPane pane) {
		//use main pane from addProduct to display list of required inputs
		
		//left node of main pane in addProduct -> display & get list of required inputs for adding a Refrigerator product
		VBox main = new VBox(5);
		main.setPadding(new Insets(10));
		main.setStyle("-fx-border-color: black"); //set border color
		main.setAlignment(Pos.TOP_CENTER);
		
		//arrays of nodes for the list of required inputs
		Label[] labels = new Label[7];
		TextField[] textFields = new TextField[7];
		HBox[] fieldBox = new HBox[7];
		
		//initialize arrays & add them to left node
		labels[0] = new Label("Product name:");
		labels[1] = new Label("Door design:");
		labels[2] = new Label("Color:");
		labels[3] = new Label("Capacity (in Litres):");
		labels[4] = new Label("Quantity available:");
		labels[5] = new Label("Price (RM):");
		labels[6] = new Label("Item number:");
		
		main.getChildren().add(new Label("Refrigerator"));
		for(int i = 0; i < fieldBox.length; i++) {
			textFields[i] = new TextField();
			fieldBox[i] = new HBox(10);
			fieldBox[i].getChildren().addAll(labels[i], textFields[i]);
			fieldBox[i].setAlignment(Pos.TOP_RIGHT);
			main.getChildren().add(fieldBox[i]);
		}
		//text to display error message
		Label errmsg = new Label();
		errmsg.setTextFill(Color.RED);
		errmsg.setMaxWidth(250); //set how far the error message can reach -> prevents it become hidden (...) in stage
		errmsg.setWrapText(true);
		errmsg.setTextAlignment(TextAlignment.JUSTIFY);
		main.getChildren().add(errmsg);
		pane.setLeft(main);
		
		textFields[0].requestFocus();
		textFields[0].setOnKeyPressed(e->{ //product name
			if(e.getCode() == KeyCode.ENTER) {
				if(textFields[0].getText() == "") {
					errmsg.setText("Product name cannot be blank.");
					textFields[0].requestFocus();
				}
				else {
					errmsg.setText("");
					textFields[1].requestFocus();
				}
			}
			else if(e.getCode() == KeyCode.DOWN) {
				textFields[1].requestFocus();
			}
		});
		textFields[1].setOnKeyPressed(e->{ //door design
			if(e.getCode() == KeyCode.UP) {
				textFields[0].requestFocus();
			}
			else if(e.getCode() == KeyCode.ENTER) {
				if(textFields[1].getText() == "") {
					errmsg.setText("Door design cannot be blank.");
					textFields[1].requestFocus();
				}
				else {
					errmsg.setText("");
					textFields[2].requestFocus();
				}
			}
			else if(e.getCode() == KeyCode.DOWN) {
				textFields[2].requestFocus();
			}
		});
		textFields[2].setOnKeyPressed(e->{ //color
			if(e.getCode() == KeyCode.UP) {
				textFields[1].requestFocus();
			}
			else if(e.getCode() == KeyCode.ENTER) {
				if(textFields[2].getText() == "") {
					errmsg.setText("Color cannot be blank.");
					textFields[2].requestFocus();
				}
				else {
					errmsg.setText("");
					textFields[3].requestFocus();
				}
			}
			else if(e.getCode() == KeyCode.ENTER || e.getCode() == KeyCode.DOWN) {
				textFields[3].requestFocus();
			}
		});
		textFields[3].setOnKeyPressed(e->{ //capacity
			if(e.getCode() == KeyCode.UP) { //setup arrow key
				textFields[2].requestFocus();
			}
			else if(e.getCode() == KeyCode.ENTER) {
				if(textFields[3].getText() == "") { //blank field -> abort
					errmsg.setText("Capacity cannot be blank.");
					textFields[3].requestFocus();
				}
				else { //not blank field -> continue
					textFields[4].requestFocus();
					//check for non-negative and non-zero double input
					double temp = -1;
					try {
						temp = Double.parseDouble(textFields[3].getText());
						errmsg.setText("");
					}catch(NumberFormatException ex) {
						textFields[3].requestFocus();
					}
					if(temp <= 0) { //error message
						errmsg.setText("Capacity must be a positive numeric value.");
						textFields[3].requestFocus();
					}
				}
			}
			else if(e.getCode() == KeyCode.DOWN) {
				textFields[4].requestFocus();
			}
		});
		textFields[4].setOnKeyPressed(e->{ //quantity
			if(e.getCode() == KeyCode.UP) {
				textFields[3].requestFocus();
			}
			else if(e.getCode() == KeyCode.ENTER) {
				if(textFields[4].getText() == "") {
					errmsg.setText("Quantity cannot be blank.");
					textFields[4].requestFocus();
				}
				else {
					textFields[5].requestFocus();
					int temp = -1;
					//check for non-negative integer input
					try {
						temp = Integer.parseInt(textFields[4].getText());
						errmsg.setText("");
					}catch(NumberFormatException ex) {
						textFields[4].requestFocus();
					}
					if(temp < 0) {
						errmsg.setText("Quantity must be 0 or a positive integer.");
						textFields[4].requestFocus();
					}
				}
			}
			else if(e.getCode() == KeyCode.DOWN) {
				textFields[5].requestFocus();
			}
		});
		textFields[5].setOnKeyPressed(e->{ //price
			if(e.getCode() == KeyCode.UP) {
				textFields[4].requestFocus();
			}
			else if(e.getCode() == KeyCode.ENTER) {
				if(textFields[5].getText() == "") {
					errmsg.setText("Price cannot be blank.");
					textFields[5].requestFocus();
				}
				else {
					textFields[6].requestFocus();
					double temp = -1;
					//check for non-negative double input
					try {
						temp = Double.parseDouble(textFields[5].getText());
						errmsg.setText("");
					}catch(NumberFormatException ex) {
						textFields[5].requestFocus();
					}
					if(temp < 0) {
						errmsg.setText("Price must be 0 or a positive numeric value.");
						textFields[5].requestFocus();
					}
				}
			}
			else if(e.getCode() == KeyCode.DOWN) {
				textFields[6].requestFocus();
			}
		});
		textFields[6].setOnKeyPressed(e->{ //item number (last field)
			if(e.getCode() == KeyCode.UP) {
				textFields[5].requestFocus();
			}
			else if(e.getCode() == KeyCode.ENTER) {
				if(textFields[6].getText() == "") {
					errmsg.setText("Item number cannot be blank.");
					textFields[6].requestFocus();
				}
				else {
					if(product.size() >= maxNum) {
						errmsg.setText("You had reached the maximum possible number of products.");
					}
					else {
						//since first five fields were checked already, a confirm on this field attempts to add the Refrigerator product into product ArrayList
						//effectively checking the last field (item number) as well
						try {
							product.add(new Refrigerator(
									textFields[0].getText(), //name
									textFields[1].getText(), //door design
									textFields[2].getText(), //color
									Double.parseDouble(textFields[3].getText()), //capacity
									Integer.parseInt(textFields[4].getText()), //quantity
									Double.parseDouble(textFields[5].getText()), //price
									Integer.parseInt(textFields[6].getText()))); //itemNumber
							for(TextField i: textFields) {
								i.setText("");
							}
							textFields[0].requestFocus();
							errmsg.setText("");
						}catch(NumberFormatException ex) {
							errmsg.setText("Item number must be an integer.");
							textFields[6].requestFocus();
						}
					}
				}
			}
		});
	}
	//(10) add TV product
	public static void addTV(ArrayList<Product> product, BorderPane pane) {
		//this method is similar to addRefrigerator with little different checking and variable names
		//light documentation ahead
		
		//left node
		VBox main = new VBox(5);
		main.setPadding(new Insets(10));
		main.setStyle("-fx-border-color: black");
		main.setAlignment(Pos.TOP_CENTER);
		
		Label[] labels = new Label[7];
		TextField[] textFields = new TextField[7];
		HBox[] fieldBox = new HBox[7];
		
		labels[0] = new Label("Product name:");
		labels[1] = new Label("Screen type:");
		labels[2] = new Label("Resolution:");
		labels[3] = new Label("Display size (inches):");
		labels[4] = new Label("Quantity available:");
		labels[5] = new Label("Price (RM):");
		labels[6] = new Label("Item number:");

		main.getChildren().add(new Label("TV"));
		for(int i = 0; i < fieldBox.length; i++) {
			textFields[i] = new TextField();
			fieldBox[i] = new HBox(10);
			fieldBox[i].getChildren().addAll(labels[i], textFields[i]);
			fieldBox[i].setAlignment(Pos.TOP_RIGHT);
			main.getChildren().add(fieldBox[i]);
		}
		Text errmsg = new Text();
		errmsg.setFill(Color.RED);
		main.getChildren().add(errmsg);
		pane.setLeft(main);
		
		//fields & input events
		textFields[0].requestFocus();
		textFields[0].setOnKeyPressed(e->{ //product name
			if(e.getCode() == KeyCode.ENTER) {
				if(textFields[0].getText() == "") {
					errmsg.setText("Product name cannot be blank.");
					textFields[0].requestFocus();
				}
				else {
					errmsg.setText("");
					textFields[1].requestFocus();
				}
			}
			else if(e.getCode() == KeyCode.DOWN) {
				textFields[1].requestFocus();
			}
		});
		textFields[1].setOnKeyPressed(e->{ //screen type
			if(e.getCode() == KeyCode.UP) {
				textFields[0].requestFocus();
			}
			else if(e.getCode() == KeyCode.ENTER) {
				if(textFields[1].getText() == "") {
					errmsg.setText("Screen type cannot be blank.");
					textFields[1].requestFocus();
				}
				else {
					errmsg.setText("");
					textFields[2].requestFocus();
				}
			}
			else if(e.getCode() == KeyCode.DOWN) {
				textFields[2].requestFocus();
			}
		});
		textFields[2].setOnKeyPressed(e->{ //resolution
			if(e.getCode() == KeyCode.UP) {
				textFields[1].requestFocus();
			}
			else if(e.getCode() == KeyCode.ENTER) {
				if(textFields[2].getText() == "") {
					errmsg.setText("Resolution cannot be blank.");
					textFields[2].requestFocus();
				}
				else {
					errmsg.setText("");
					textFields[3].requestFocus();
				}
			}
			else if(e.getCode() == KeyCode.ENTER || e.getCode() == KeyCode.DOWN) {
				textFields[3].requestFocus();
			}
		});
		textFields[3].setOnKeyPressed(e->{ //display size
			if(e.getCode() == KeyCode.UP) {
				textFields[2].requestFocus();
			}
			else if(e.getCode() == KeyCode.ENTER) {
				if(textFields[3].getText() == "") {
					errmsg.setText("Display size cannot be blank.");
					textFields[3].requestFocus();
				}
				else {
					textFields[4].requestFocus();
					errmsg.setText("");
					double temp = -1;
					//check non-negative and non-zero double input
					try {
						temp = Double.parseDouble(textFields[3].getText());
					}catch(NumberFormatException ex) {
						textFields[3].requestFocus();
					}
					if(temp <= 0) {
						errmsg.setText("Display size must be a positive numeric value above 0.");
						textFields[3].requestFocus();
					}
				}
			}
			else if(e.getCode() == KeyCode.DOWN) {
				textFields[4].requestFocus();
			}
		});
		textFields[4].setOnKeyPressed(e->{ //quantity
			if(e.getCode() == KeyCode.UP) {
				textFields[3].requestFocus();
			}
			else if(e.getCode() == KeyCode.ENTER) {
				if(textFields[4].getText() == "") {
					errmsg.setText("Quantity cannot be blank.");
					textFields[4].requestFocus();
				}
				else {
					textFields[5].requestFocus();
					errmsg.setText("");
					int temp = -1;
					//check non-negative integer input
					try {
						temp = Integer.parseInt(textFields[4].getText());
					}catch(NumberFormatException ex) {
						textFields[4].requestFocus();
					}
					if(temp < 0) {
						errmsg.setText("Quantity must be 0 or a positive integer.");
						textFields[4].requestFocus();
					}
				}
			}
			else if(e.getCode() == KeyCode.DOWN) {
				textFields[5].requestFocus();
			}
		});
		textFields[5].setOnKeyPressed(e->{ //price
			if(e.getCode() == KeyCode.UP) {
				textFields[4].requestFocus();
			}
			else if(e.getCode() == KeyCode.ENTER) {
				if(textFields[5].getText() == "") {
					errmsg.setText("Price cannot be blank.");
					textFields[5].requestFocus();
				}
				else {
					textFields[6].requestFocus();
					errmsg.setText("");
					double temp = -1;
					//check non-negative double input
					try {
						temp = Double.parseDouble(textFields[5].getText());
					}catch(NumberFormatException ex) {
						textFields[5].requestFocus();
					}
					if(temp < 0) {
						errmsg.setText("Price must be 0 or a positive numeric value.");
						textFields[5].requestFocus();
					}
				}
			}
			else if(e.getCode() == KeyCode.DOWN) {
				textFields[6].requestFocus();
			}
		});
		textFields[6].setOnKeyPressed(e->{ //item number
			if(e.getCode() == KeyCode.UP) {
				textFields[5].requestFocus();
			}
			else if(e.getCode() == KeyCode.ENTER) {
				if(textFields[6].getText() == "") {
					errmsg.setText("Item number cannot be blank.");
					textFields[6].requestFocus();
				}
				else {
					if(product.size() >= maxNum) {
						errmsg.setText("You had reached the maximum possible number of products.");
					}
					else {
						//check item number (integer) & add TV product if no error
						try {
							product.add(new TV(
									textFields[0].getText(), //name
									textFields[1].getText(), //screen type
									textFields[2].getText(), //resolution
									Double.parseDouble(textFields[3].getText()), //display size
									Integer.parseInt(textFields[4].getText()), //quantity
									Double.parseDouble(textFields[5].getText()), //price
									Integer.parseInt(textFields[6].getText()))); //itemNumber
							for(TextField i: textFields) {
								i.setText("");
							}
							textFields[0].requestFocus();
							errmsg.setText("");
						}catch(NumberFormatException ex) {
							errmsg.setText("Item number must be an integer.");
							textFields[6].requestFocus();
						}
					}
				}
			}
		});
	}
	
	//Extension: add electric kettle
	public static void addKettle(ArrayList<Product> product, BorderPane pane) {
		//this method is similar to addRefrigerator with little different checking and variable names
		//light documentation ahead
		
		//left node
		VBox main = new VBox(5);
		main.setPadding(new Insets(10));
		main.setStyle("-fx-border-color: black");
		main.setAlignment(Pos.TOP_CENTER);
		
		Label[] labels = new Label[7];
		TextField[] textFields = new TextField[7];
		HBox[] fieldBox = new HBox[7];
		
		labels[0] = new Label("Product name:");
		labels[1] = new Label("Material:");
		labels[2] = new Label("Rate (in Watts):");
		labels[3] = new Label("Capacity (in Litres):");
		labels[4] = new Label("Quantity available:");
		labels[5] = new Label("Price (RM):");
		labels[6] = new Label("Item number:");

		main.getChildren().add(new Label("Electric Kettle"));
		for(int i = 0; i < fieldBox.length; i++) {
			textFields[i] = new TextField();
			fieldBox[i] = new HBox(10);
			fieldBox[i].getChildren().addAll(labels[i], textFields[i]);
			fieldBox[i].setAlignment(Pos.TOP_RIGHT);
			main.getChildren().add(fieldBox[i]);
		}
		Text errmsg = new Text();
		errmsg.setFill(Color.RED);
		main.getChildren().add(errmsg);
		pane.setLeft(main);
		
		//input fields & events
		textFields[0].requestFocus();
		textFields[0].setOnKeyPressed(e->{ //product name
			if(e.getCode() == KeyCode.ENTER) {
				if(textFields[0].getText() == "") {
					errmsg.setText("Product name cannot be blank.");
					textFields[0].requestFocus();
				}
				else {
					errmsg.setText("");
					textFields[1].requestFocus();
				}
			}
			else if(e.getCode() == KeyCode.DOWN) {
				textFields[1].requestFocus();
			}
		});
		textFields[1].setOnKeyPressed(e->{ //material
			if(e.getCode() == KeyCode.UP) {
				textFields[0].requestFocus();
			}
			else if(e.getCode() == KeyCode.ENTER) {
				if(textFields[1].getText() == "") {
					errmsg.setText("Material cannot be blank.");
					textFields[1].requestFocus();
				}
				else {
					errmsg.setText("");
					textFields[2].requestFocus();
				}
			}
			else if(e.getCode() == KeyCode.DOWN) {
				textFields[2].requestFocus();
			}
		});
		textFields[2].setOnKeyPressed(e->{ //powerRate
			if(e.getCode() == KeyCode.UP) {
				textFields[1].requestFocus();
			}
			else if(e.getCode() == KeyCode.ENTER) {
				if(textFields[2].getText() == "") {
					errmsg.setText("Rate cannot be blank.");
					textFields[2].requestFocus();
				}
				else {
					errmsg.setText("");
					textFields[3].requestFocus();
					int temp = -1;
					//check non-negative and non-zero integer input
					try {
						temp = Integer.parseInt(textFields[2].getText());
					}catch(NumberFormatException ex) {
						textFields[2].requestFocus();
					}
					if(temp <= 0 ) {
						errmsg.setText("Rate must be a positive integer above 0.");
						textFields[2].requestFocus();
					}
				}
			}
			else if(e.getCode() == KeyCode.ENTER || e.getCode() == KeyCode.DOWN) {
				textFields[3].requestFocus();
			}
		});
		textFields[3].setOnKeyPressed(e->{ //capacity
			if(e.getCode() == KeyCode.UP) {
				textFields[2].requestFocus();
			}
			else if(e.getCode() == KeyCode.ENTER) {
				if(textFields[3].getText() == "") {
					errmsg.setText("Capacity cannot be blank.");
					textFields[3].requestFocus();
				}
				else {
					errmsg.setText("");
					textFields[4].requestFocus();
					double temp = -1;
					//check non-negative and non-zero double input
					try {
						temp = Double.parseDouble(textFields[3].getText());
					}catch(NumberFormatException ex) {
						textFields[3].requestFocus();
					}
					if(temp <= 0) {
						errmsg.setText("Capacity must be a positive numeric value.");
						textFields[3].requestFocus();
					}
				}
			}
			else if(e.getCode() == KeyCode.DOWN) {
				textFields[4].requestFocus();
			}
		});
		textFields[4].setOnKeyPressed(e->{ //quantity
			if(e.getCode() == KeyCode.UP) {
				textFields[3].requestFocus();
			}
			else if(e.getCode() == KeyCode.ENTER) {
				if(textFields[4].getText() == "") {
					errmsg.setText("Quantity cannot be blank.");
					textFields[4].requestFocus();
				}
				else {
					errmsg.setText("");
					textFields[5].requestFocus();
					int temp = -1;
					//check non-negative integer input
					try {
						temp = Integer.parseInt(textFields[4].getText());
					}catch(NumberFormatException ex) {
						textFields[4].requestFocus();
					}
					if(temp < 0) {
						errmsg.setText("Quantity must be 0 or a positive integer.");
						textFields[4].requestFocus();
					}
				}
			}
			else if(e.getCode() == KeyCode.DOWN) {
				textFields[5].requestFocus();
			}
		});
		textFields[5].setOnKeyPressed(e->{ //price
			if(e.getCode() == KeyCode.UP) {
				textFields[4].requestFocus();
			}
			else if(e.getCode() == KeyCode.ENTER) {
				if(textFields[5].getText() == "") {
					errmsg.setText("Price cannot be blank.");
					textFields[5].requestFocus();
				}
				else {
					errmsg.setText("");
					textFields[6].requestFocus();
					double temp = -1;
					//check non-negative double input
					try {
						temp = Double.parseDouble(textFields[5].getText());
					}catch(NumberFormatException ex) {
						textFields[5].requestFocus();
					}
					if(temp < 0) {
						errmsg.setText("Price must be 0 or a positive numeric value.");
						textFields[5].requestFocus();
					}
				}
			}
			else if(e.getCode() == KeyCode.DOWN) {
				textFields[6].requestFocus();
			}
		});
		textFields[6].setOnKeyPressed(e->{ //item number
			if(e.getCode() == KeyCode.UP) {
				textFields[5].requestFocus();
			}
			else if(e.getCode() == KeyCode.ENTER) {
				if(textFields[6].getText() == "") {
					errmsg.setText("Item number cannot be blank.");
					textFields[6].requestFocus();
				}
				else {
					if(product.size() >= maxNum) {
						errmsg.setText("You had reached the maximum possible number of products.");
					}
					else {
						//check item number (integer) & add Electric Kettle product if no exception
						try {
							product.add(new ElectricKettle(
									textFields[0].getText(), //name
									textFields[1].getText(), //material
									Integer.parseInt(textFields[2].getText()), //power rate
									Double.parseDouble(textFields[3].getText()), //capacity
									Integer.parseInt(textFields[4].getText()), //quantity
									Double.parseDouble(textFields[5].getText()), //price
									Integer.parseInt(textFields[6].getText()))); //itemNumber
							for(TextField i: textFields) {
								i.setText("");
							}
							textFields[0].requestFocus();
							errmsg.setText("");
						}catch(NumberFormatException ex) {
							errmsg.setText("Item number must be an integer.");
							textFields[6].requestFocus();
						}
					}
				}
			}
		});
	}
	
	//(11) display contents in product ArrayList
	public static void viewProduct(ArrayList<Product> product, Stage stage) {
		
		//main pane for displaying the product ArrayList
		BorderPane pane = new BorderPane();
		
		//only one node is in this pane to hold the contents of product ArrayList
		//TextArea is chosen as it allows scrolling and append text (useful for long ArrayList)
		TextArea list = new TextArea();
		list.setEditable(false);
		pane.setPadding(new Insets(10));
		String text = ""; //reset the text to be appended every time this method is called
		if(product.size() == 0) { //blank list -> append short text
			text = "There is no product added yet";
		} else {
			for(int i = 0; i < product.size(); i++) {
				text += "Product " + (i+1) + " (";
				//append types of product
				if(product.get(i) instanceof Refrigerator) { //Refrigerator
					text += "Refrigerator";
				}
				else if(product.get(i) instanceof TV){ //TV
					text += "TV";
				}
				else if(product.get(i) instanceof ElectricKettle) { //Electric Kettle
					text += "Electric Kettle";
				}
				text += ")\n" + product.get(i).toString() + "\n\n"; //trigger overridden toString() method to append product details into text
			}
		}
		list.setText(text); //set new text to TextArea every time this method is called
		pane.setCenter(list);
		
		//launch the View Product stage
		Scene scene = new Scene(pane, 600, 400);
		stage.setTitle("View Products");
		stage.setScene(scene);
		stage.setResizable(true);
		stage.show();
		scene.setOnKeyPressed(e->{
			if(e.getCode() == KeyCode.ESCAPE) {
				stage.close();
			}
		});
	}
	//(12) launch the GUI
	public static void main(String[] args) {
		//launch the application
		launch(args);
	}
}
