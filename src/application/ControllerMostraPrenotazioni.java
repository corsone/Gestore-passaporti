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
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

public class ControllerMostraPrenotazioni extends ControllerMenu implements Initializable {
	private Stage stage;
	private Scene scene;
	private Parent root;
	
	private String url = "jdbc:mysql://localhost:3306/QuesturaDB";
	private String usernameDB = "root";
	private String passwordDB = "";
	
	private String idUtente;
	
	@FXML
	private VBox vBox;
	@FXML
	private BorderPane borderPane;
	@FXML
	private ImageView myImage;
	@FXML
	private Label label;
	
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
	
	
	public void passaDati(String idUtente, double width, double height) {
		borderPane.setPrefWidth(width);
		borderPane.setPrefHeight(height);
		
		borderPane.setMinWidth(1240);
		borderPane.setMinHeight(720);
		
		this.idUtente = idUtente;
		
		mostraPrenotazioni();
	}
	
	public void mostraPrenotazioni() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection connection = DriverManager.getConnection(url, usernameDB,passwordDB );
			PreparedStatement statement = connection.prepareStatement("SELECT DataPrenotazione, OraPrenotazione, TipoServizio, Via, Denominazione "
																	+ "FROM Prenotazioni, Sede, Servizi "
																	+ "WHERE Prenotazioni.IdUtente = ? and Prenotazioni.IdSede = Sede.IdSede and Prenotazioni.IdServizio = Servizi.IdServizio");
			statement.setString(1, idUtente);
			ResultSet resultSet = statement.executeQuery();
			
			
			
			if (resultSet.isBeforeFirst()) {
				
				vBox.getChildren().remove(myImage);
				vBox.getChildren().remove(label);
				
				while(resultSet.next()) {
					Label label = new Label();
					
					label.setMaxWidth(Double.MAX_VALUE);
					label.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect:  dropshadow(three-pass-box, rgba(0,0,0,0.8), 10, 0, 0, 0);");
					label.setPadding(new Insets(20, 10, 20, 10));
			
					
					TextFlow textFlow = new TextFlow();
					
					Text servizioRichiestoText = new Text(resultSet.getString(3));
	                servizioRichiestoText.setFont(Font.font("Arial", 25));
	                servizioRichiestoText.setFill(Color.web("#275576"));
	               
	                Text data = new Text("\nData Prenotazione: ");
	                Text ora = new Text("\nOra Prenotazione: ");
	                Text sede = new Text("\nSede: ");
	                
	                data.setFont(Font.font("Arial", 16));
	                ora.setFont(Font.font("Arial", 16));
	                sede.setFont(Font.font("Arial", 16));
	                
	                
	                data.setStyle("-fx-font-weight: bold;");
	                ora.setStyle("-fx-font-weight: bold;");
	                sede.setStyle("-fx-font-weight: bold;");
	                
	                Text dataSelezionata = new Text(resultSet.getString(1));
	                Text oraSelezionata = new Text(resultSet.getString(2));
	                Text sedeSelezionata = new Text(resultSet.getString(5) + ", " + resultSet.getString(4));
	                
	                dataSelezionata.setFont(Font.font("Arial", 16));
	                oraSelezionata.setFont(Font.font("Arial", 16));
	                sedeSelezionata.setFont(Font.font("Arial", 16));
	                
	                textFlow.setLineSpacing(10);
	                
	                textFlow.getChildren().addAll(servizioRichiestoText, data, dataSelezionata, ora, oraSelezionata, sede, sedeSelezionata);
	                
	                if (!resultSet.getString(3).equals("Ritiro passaporto")) {
	                	Text documenti = new Text("\nDocumenti necessari:\n");
	                	documenti.setFont(Font.font("Arial", 16));
	                	documenti.setStyle("-fx-font-weight: bold;");
	                	
		                Text documentiNecessari = new Text("\t• modulo di richiesta compilato\n\t• marca da bollo\n\t• ricevuta del versamento su C/C postale\n\t• due fototessera su sfondo bianco\n\t• passaporto precedente");
		                documentiNecessari.setFont(Font.font("Arial", 16));
		                
		                textFlow.getChildren().addAll(documenti, documentiNecessari);
	                }

	                label.setGraphic(textFlow);
	                label.setMaxHeight(10);
					
					
					VBox.setMargin(label, new Insets(5, 10, 5, 10));
					
					vBox.getChildren().add(label);
				}
			}
		} catch (Exception e) {
			System.out.println(e);
		}
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
