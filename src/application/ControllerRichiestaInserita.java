package application;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class ControllerRichiestaInserita extends ControllerMenu implements Initializable{
	private Stage stage;
	private Scene scene;
	private Parent root;
	
	@FXML
	private BorderPane borderPane;
	@FXML
	private ImageView myImage;
	
	public void passaDati(double width, double height) {
		borderPane.setPrefWidth(width);
		borderPane.setPrefHeight(height);
		
		borderPane.setMinWidth(1240);
		borderPane.setMinHeight(720);
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		mainPane = borderPane;
		
		borderPane.widthProperty().addListener((observable, oldValue, newValue) -> {
        	myImage.setFitWidth(borderPane.getWidth() / 3);
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
	
	public void logOut(ActionEvent event) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Log out");
		alert.setHeaderText("Sei sicuro di voler uscire dal tuo profilo?");
		
		
		if(alert.showAndWait().get() == ButtonType.OK) {
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
