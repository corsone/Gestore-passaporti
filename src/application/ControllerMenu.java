package application;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class ControllerMenu {
	private Stage stage;
	private Scene scene;
	private Parent root;
	
	@FXML
	private Label menu;
	@FXML
	private Label menuBack;
	@FXML
	protected BorderPane mainPane;
	
	public void cambiaHome(ActionEvent event) {
		try {

			FXMLLoader loader = new FXMLLoader(getClass().getResource("HomeScene.fxml"));
			root = loader.load();
			
			ControllerHome controllerHome = loader.getController();
			controllerHome.passaDati(mainPane.getWidth(), mainPane.getHeight());
			
			stage = (Stage)(((Node) event.getSource()).getScene().getWindow());
			scene = new Scene(root);
			stage.setScene(scene);			
			stage.show();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void cambiaContattaci(ActionEvent event) {
		try {
			
			FXMLLoader loader = new FXMLLoader(getClass().getResource("ContattaciScene.fxml"));
			root = loader.load();
			
			ControllerContattaci controllerContattaci = loader.getController();
			controllerContattaci.passaDati(mainPane.getWidth(), mainPane.getHeight());
			
			//root = FXMLLoader.load(getClass().getResource("ContattaciScene.fxml"));
			stage = (Stage)(((Node) event.getSource()).getScene().getWindow());
			scene = new Scene(root);
			stage.setScene(scene);
			stage.show();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void cambiaServizi(ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("ServiziScene.fxml"));
			root = loader.load();
			
			ControllerServizi controllerServizi = loader.getController();
			controllerServizi.passaDati(ControllerLogin.getIdUtente(), mainPane.getWidth(), mainPane.getHeight());
			
			stage = (Stage)(((Node) event.getSource()).getScene().getWindow());
			scene = new Scene(root);
			stage.setScene(scene);
			stage.show();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void cambiaServiziPersonale(ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("PersonaleScene.fxml"));
			root = loader.load();
			
			ControllerPersonale controllerPersonale = loader.getController();
			controllerPersonale.passaDati(mainPane.getWidth(), mainPane.getHeight());
			
			stage = (Stage)(((Node) event.getSource()).getScene().getWindow());
			scene = new Scene(root);
			stage.setScene(scene);
			stage.show();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void cambiaAreaUtente(ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("PrenotazioniScene.fxml"));
			root = loader.load();
			
			ControllerMostraPrenotazioni controllerMostraPrenotazioni = loader.getController();
			controllerMostraPrenotazioni.passaDati(ControllerLogin.getIdUtente(), mainPane.getWidth(), mainPane.getHeight());
			
			stage = (Stage)(((Node) event.getSource()).getScene().getWindow());
			scene = new Scene(root);
			stage.setScene(scene);
			stage.show();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void cambiaContattiLoggato(ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("ContattaciLoggatoScene.fxml"));
			root = loader.load();
			
			ControllerContattaci controllerContattaci = loader.getController();
			controllerContattaci.passaDati(mainPane.getWidth(), mainPane.getHeight());
		
			//root = FXMLLoader.load(getClass().getResource("ContattaciLoggatoScene.fxml"));	
			stage = (Stage)(((Node) event.getSource()).getScene().getWindow());
			scene = new Scene(root);
			stage.setScene(scene);
			stage.show();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void cambiaNotifica(ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("NotificaScene.fxml"));
			root = loader.load();
			
			ControllerNotifica controllerNotifica = loader.getController();
			controllerNotifica.passaDati(mainPane.getWidth(), mainPane.getHeight(), ControllerLogin.getIdUtente());
		
			//root = FXMLLoader.load(getClass().getResource("ContattaciLoggatoScene.fxml"));	
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
