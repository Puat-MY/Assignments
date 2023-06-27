import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class UserInfo {
    // Data fields
    private String name;
    private String userID;

	//Get name method
    public void getName() {
    	Stage stage = new Stage();
    	
    	HBox firstnameBox = new HBox(10);
    	firstnameBox.setPadding(new Insets(10));
    	firstnameBox.setAlignment(Pos.CENTER);
    	Label firstname = new Label("First name:");
    	TextField firstnameTF = new TextField();
    	firstnameTF.setPrefColumnCount(20);
    	firstnameBox.getChildren().addAll(firstname, firstnameTF);
    	
    	HBox surnameBox = new HBox(18);
    	surnameBox.setAlignment(Pos.CENTER);
    	surnameBox.setPadding(new Insets(10));
    	Label surname = new Label("Surname:");
		TextField surnameTF = new TextField();
		surnameTF.setPrefColumnCount(20);
		surnameBox.getChildren().addAll(surname, surnameTF);
		
    	HBox buttonBox = new HBox(10);
    	buttonBox.setPadding(new Insets(10));
    	buttonBox.setAlignment(Pos.CENTER);
    	Button submit = new Button("Submit");
    	Button cancel = new Button("Cancel");
    	buttonBox.getChildren().addAll(submit, cancel);
    	
    	Text text = new Text();
    	
    	VBox mainBox = new VBox();
    	mainBox.setPadding(new Insets(10));
    	mainBox.setAlignment(Pos.TOP_LEFT);
    	mainBox.getChildren().addAll(firstnameBox, surnameBox, buttonBox, text);
    	
    	firstnameTF.setOnKeyPressed(e->{
			if(e.getCode() == KeyCode.ENTER) {
				this.name = firstnameTF.getText().trim() + " ";
				surnameTF.requestFocus();
			}
			if(e.getCode() == KeyCode.DOWN) {
				surnameTF.requestFocus();
			}
		});
		surnameTF.setOnKeyPressed(e->{
			if(e.getCode() == KeyCode.UP) {
				firstnameTF.requestFocus();
			}
			if(e.getCode() == KeyCode.DOWN || e.getCode() == KeyCode.ENTER) {
				submit.requestFocus();
			}
		});
		submit.setOnAction(e->{
			this.name = (firstnameTF.getText().trim() + " " + surnameTF.getText().trim()).trim();
			generateUserID();
			if(this.name == "") {
				this.name = "Guest";
		    	text.setText("No username is given."
		    			+ "\nUsername is set to \"Guest\"."
		    			+ "\n" + toString()
		    			+ "\nPress ESCAPE or Cancel to leave.");
			}
			else {
				text.setText(toString() + "\nPress ESCAPE or Cancel to leave.");
			}
		});
		submit.setOnKeyPressed(e->{
			if(e.getCode() == KeyCode.UP) {
				surnameTF.requestFocus();
			}
			if(e.getCode() == KeyCode.DOWN) {
				cancel.requestFocus();
			}
		});
		cancel.setOnAction(e->{
			stage.close();
		});
		cancel.setOnKeyPressed(e->{
			if(e.getCode() == KeyCode.UP) {
				submit.requestFocus();
			}
		});
		
		Scene scene = new Scene(mainBox, 400, 250);
		stage.setTitle("Login");
		stage.setScene(scene);
		stage.setResizable(false);
		stage.show();
		scene.setOnKeyPressed(e->{
			if(e.getCode() == KeyCode.ESCAPE) {
				stage.close();
			}
		});
    }

    //boolean check 'name' instance variable contains any spaces?
    private boolean checkName() {
        return this.name.contains(" ");
    }

    //generate User ID based on the 'name'
    private void generateUserID() {
    	//if 'name' contains spaces, splits it into firstname and surname
        if (checkName()) {
            String[] fullname = this.name.split(" ");
            String firstName = fullname[0];
            String surname = fullname[fullname.length - 1];
            this.userID = firstName.substring(0, 1) + surname;
        }else {//Otherwise, set to "guest".
        	this.userID = "guest";
        }
    }
    
    @Override
    public String toString() {
		return "User ID: " + userID + "\nUsername: " + name;
    }
    //Testing
    /*public String getUserID() {
    	generateUserID();
    	return this.userID;
    }
    
    public static void main(String[] args) {
    	//Create UserInfo object
    	UserInfo user1 = new UserInfo();
    	//Print the 'name'
    	System.out.println("Name: " + user1.getName());
    	//Print the 'user ID'
    	System.out.println("User ID: " + user1.getUserID());
    }*/
}
