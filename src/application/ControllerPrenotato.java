package application;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;


public class ControllerPrenotato extends ControllerMenu implements Initializable{
	
	private String servizio;
	
	@FXML
	private BorderPane borderPane;
	@FXML
	private ImageView myImage;
	@FXML
	private Label label1;
	@FXML
	private Label label2;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		mainPane = borderPane;

		borderPane.widthProperty().addListener((observable, oldValue, newValue) -> {
        	myImage.setFitWidth(borderPane.getWidth() / 3);
        });

		borderPane.heightProperty().addListener((observable, oldValue, newValue) -> {
			myImage.setFitHeight(borderPane.getHeight());
	    });
				
	}

	public void passaDati(double width, double height, String servizio) {
		borderPane.setPrefWidth(width);
		borderPane.setPrefHeight(height);
		
		borderPane.setMinWidth(1240);
		borderPane.setMinHeight(720);
		
		
		this.servizio = servizio;
		
		impostaTesto();
	}
	
	public void impostaTesto() {
		if (!servizio.equals("Ritiro passaporto")){
			label1.setText("Ricorda di portare con te al momento della presentazione della richiesta tutti i documenti necessari:");
			label2.setText("\t• modulo di richiesta compilato\n\t• marca da bollo\n\t• ricevuta del versamento su C/C postale\n\t• due fototessera su sfondo bianco\n\t• passaporto precedente");
		}else {
			//label2.setText("");
		}
	}
}
