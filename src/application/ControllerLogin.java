package application;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Duration;


public class ControllerLogin extends Login{
	
	String url = "jdbc:mysql://localhost:3306/QuesturaDB";
	String usernameDB = "root";
	String passwordDB = "";
	
	private Stage stage;
	private Scene scene;
	private Parent root;
	
	private static String idUtente;
	
	@FXML
	private BorderPane borderPane;
	@FXML
	private ImageView myImage;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		mainPane = borderPane;
		
		borderPane.widthProperty().addListener((observable, oldValue, newValue) -> {
        	myImage.setFitWidth(borderPane.getWidth() / 2);
        });

		borderPane.heightProperty().addListener((observable, oldValue, newValue) -> {
			myImage.setFitHeight(borderPane.getHeight());
	    });
		
	
		TranslateTransition translate = new TranslateTransition();
		translate.setNode(myImage);
		translate.setDuration(Duration.millis(1000));
		translate.setInterpolator(Interpolator.LINEAR);
		translate.setFromX(-500);
		translate.setToX(0);
		translate.play();
		
		FadeTransition fade = new FadeTransition();
		fade.setNode(myImage);
		fade.setDuration(Duration.millis(1000));
		fade.setInterpolator(Interpolator.LINEAR);
		fade.setFromValue(0);
		fade.setToValue(1);
		fade.play();

    }
	
	public void passaDati(double width, double height) {
		borderPane.setPrefWidth(width);
		borderPane.setPrefHeight(height);
		
		borderPane.setMinWidth(1240);
		borderPane.setMinHeight(720);
	}
	
	@Override
	public int scopriQuestura() {
		return 0;
	}
	
	public void registrati(ActionEvent event) {
		try {
			
			FXMLLoader loader = new FXMLLoader(getClass().getResource("RegistrazioneScene.fxml"));
			root = loader.load();
			
			ControllerRegistrazione controllerRegistrazione = loader.getController();
			controllerRegistrazione.passaDati(mainPane.getWidth(), mainPane.getHeight());
			
			stage = (Stage)(((Node) event.getSource()).getScene().getWindow());
			scene = new Scene(root);
			stage.setMinWidth(1240);
			stage.setMinHeight(720);
			stage.setScene(scene);
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void cambiaScena(ActionEvent event) {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection connection = DriverManager.getConnection(url, usernameDB,passwordDB );
			PreparedStatement statement=connection.prepareStatement("select IdUtente from Utenti  WHERE Email= ?");
			statement.setString(1,getEmail().getText());
			ResultSet resultSet = statement.executeQuery();
			
			
			while(resultSet.next())
				idUtente = resultSet.getString(1);
							
			FXMLLoader loader = new FXMLLoader(getClass().getResource("ServiziScene.fxml"));
			root = loader.load();
			
			ControllerServizi controllerServizi = loader.getController();
			controllerServizi.passaDati(idUtente, mainPane.getWidth(), mainPane.getHeight());
			
			stage = (Stage)(((Node) event.getSource()).getScene().getWindow());
			scene = new Scene(root);
			stage.setScene(scene);
			stage.show();
		}catch(Exception e) {
			System.out.println(e);
		}
	}
	
	static public String getIdUtente() {
		return idUtente;
	}
}


