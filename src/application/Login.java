package application;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public abstract class Login extends ControllerMenu implements Initializable{
	
	private Stage stage;
	private Scene scene;
	private Parent root;
	
	@FXML
	private Button loginBtn;
	@FXML
	private TextField email;
	@FXML
	private TextField password;
	@FXML
	private Label forErrorLabel;
	@FXML
	private BorderPane borderPane;
	@FXML
	private ImageView exit;
	
	String url = "jdbc:mysql://localhost:3306/QuesturaDB";
	String usernameDB = "root";
	String passwordDB = "";
	
	public void login(ActionEvent event) {
					
		try {
			
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection connection = DriverManager.getConnection(url, usernameDB,passwordDB );
			PreparedStatement statement = connection.prepareStatement("select * from Utenti where Email=? and Password=? and IsQuestura=?");
			//String miaquery = "select * from login  WHERE Email="+username.getText()+"and Password ="+ password.getText();
			statement.setString(1,email.getText());
			statement.setString(2,password.getText());
			statement.setString(3, String.valueOf(scopriQuestura()));
			
			System.out.println(email.getText());
			System.out.println(password.getText());
			System.out.println(scopriQuestura());
			ResultSet resultSet = statement.executeQuery();
			
			if(!resultSet.next()) {
				//System.out.println("ERRORE!!");
				forErrorLabel.setText("credenziali errate, riprovare!");
			}else {
				cambiaScena(event);
			}
		}catch(Exception e) {
			System.out.println(e.toString());
		}
	}
	
	protected abstract int  scopriQuestura();
	
	public abstract void cambiaScena(ActionEvent event);
	
	abstract public void initialize(URL arg0, ResourceBundle arg1);
	
	public void back(ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("HomeScene.fxml"));
			root = loader.load();
			
			ControllerHome controllerHome = loader.getController();
			controllerHome.passaDati(mainPane.getWidth(), mainPane.getHeight());
			
			//root = FXMLLoader.load(getClass().getResource("HomeScene.fxml"));
			stage = (Stage)(((Node) event.getSource()).getScene().getWindow());
			scene = new Scene(root);
			stage.setScene(scene);
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	protected TextField getEmail() {
		return this.email;
	}
}