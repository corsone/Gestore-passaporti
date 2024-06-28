package application;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class ControllerHome extends ControllerMenu implements Initializable{
	private Stage stage;
	private Scene scene;
	private Parent root;
	
	@FXML
	private BorderPane borderPane;
	@FXML
	private ImageView passaportoImg;
	@FXML
	private ImageView myImage;
	@FXML
	private ImageView myImage2;
	@FXML
	private HBox hBox;
	@FXML
	private BorderPane imagePane1;
	
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		mainPane = borderPane;
		
		borderPane.widthProperty().addListener((observable, oldValue, newValue) -> {
        	myImage.setFitWidth(borderPane.getWidth() / 2);
        	myImage2.setFitWidth(borderPane.getWidth() / 2);
        });

		borderPane.heightProperty().addListener((observable, oldValue, newValue) -> {
			myImage.setFitHeight(borderPane.getHeight() / 2);
			myImage2.setFitHeight(borderPane.getHeight() / 2);
	    });
		
		
		// TODO Auto-generated method stub	
		
		FadeTransition fade = new FadeTransition();
		  fade.setNode(myImage);
		  fade.setDuration(Duration.millis(1000));
		  fade.setInterpolator(Interpolator.LINEAR);
		  fade.setFromValue(0);
		  fade.setToValue(1);
		  fade.play();
		  
		  FadeTransition fade1 = new FadeTransition();
		  fade1.setNode(myImage2);
		  fade1.setDuration(Duration.millis(1000));
		  fade1.setInterpolator(Interpolator.LINEAR);
		  fade1.setFromValue(0);
		  fade1.setToValue(1);
		  fade1.play();
		
	}
	
	public void passaDati(double width, double height) {
		borderPane.setPrefWidth(width);
		borderPane.setPrefHeight(height);
		
		borderPane.setMinWidth(1240);
		borderPane.setMinHeight(720);
	}
	
	public void loginCittadini(ActionEvent event) {
		try {
			
			FXMLLoader loader = new FXMLLoader(getClass().getResource("LoginScene.fxml"));
			root = loader.load();
			
			ControllerLogin controllerLogin = loader.getController();
			controllerLogin.passaDati(mainPane.getWidth(), mainPane.getHeight());
			
			//root = FXMLLoader.load(getClass().getResource("LoginScene.fxml"));
			stage = (Stage)(((Node) event.getSource()).getScene().getWindow());
			scene = new Scene(root);
			stage.setScene(scene);
			stage.show();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void loginQuestura(ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("LoginPersonaleScene.fxml"));
			root = loader.load();
			
			ControllerLoginPersonale controllerLoginPersonale = loader.getController();
			controllerLoginPersonale.passaDati(mainPane.getWidth(), mainPane.getHeight());
			
			//root = FXMLLoader.load(getClass().getResource("LoginPersonaleScene.fxml"));
			stage = (Stage)(((Node) event.getSource()).getScene().getWindow());
			scene = new Scene(root);
			stage.setScene(scene);
			stage.show();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}