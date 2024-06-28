package application;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ResourceBundle;
import java.util.TreeSet;


import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.ImageView;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;

public class ControllerServizi extends Persona implements Initializable{
	
	private Stage stage;
	private Scene scene;
	private Parent root;
	
	private String idUtente;
	
	private TreeSet<String> servizi = new TreeSet<String>(); // salva i servizi del db
	
	@FXML
	private BorderPane borderPane;
	@FXML
	private ImageView myImage;
	@FXML
	private Button prenotaBtn;
	@FXML
	private VBox vBox;
	
	
	public void initialize(URL arg0, ResourceBundle arg1) {
		mainPane = borderPane;
		
		borderPane.widthProperty().addListener((observable, oldValue, newValue) -> {
			myImage.setFitWidth(borderPane.getWidth() / 2.5);
        });

		borderPane.heightProperty().addListener((observable, oldValue, newValue) -> {
			myImage.setFitHeight(borderPane.getHeight());
	    });		
		
		
		FadeTransition fade = new FadeTransition();
		  fade.setNode(myImage);
		  fade.setDuration(Duration.millis(1000));
		  fade.setInterpolator(Interpolator.LINEAR);
		  fade.setFromValue(0);
		  fade.setToValue(1);
		  fade.play();
		  
		riempiServizi();
		getServizio().getItems().addAll(servizi);
		
		// disabilita le celle della choice box dei servizi
		getServizio().setCellFactory(new Callback<>() {
		     @Override public ListCell<String> call(ListView<String> p) {
		         return new ListCell<>() {
		             @Override protected void updateItem(String item, boolean empty) {
		                 super.updateItem(item, empty);
		                 if (item == null || empty) {
		                     setText(null);
		                 } else {
		                	 setText(item);	 
		                	 try {
	                        		Class.forName("com.mysql.cj.jdbc.Driver");
	                    			Connection connection = DriverManager.getConnection(getUrl(),getUsernameDB(),getPasswordDB());
	                    			PreparedStatement statement = connection.prepareStatement("SELECT TipoServizio FROM Prenotazioni, Servizi Where Prenotazioni.IdServizio = Servizi.IdServizio and IdUtente = ?");
	                   			 	statement.setString(1,idUtente);
	                   			 	ResultSet resultSet = statement.executeQuery();
	                   			 	
	                   			 	
	                   			 	// se non ci sono prenotazioni non fa selezionare
	                   			 	if (!resultSet.isBeforeFirst()) {
	                                    if (item.equals("Ritiro passaporto")) {
	                                        setDisable(true);
	                                        setStyle(" -fx-opacity: 0.4 ;");
	                                    }
	                   			 	}else {
	                   			 		int count = 0;
	                   			 		
	                   			 		while (resultSet.next()) {
	                   			 			count++;
	                   			 		}
	                   			 		
	                   			 		System.out.println(count);
	                   			 		// se ci sono 2 prenotazioni non fa selezionare ne ritiro ne rilascio, altrimenti fa selezionare il rilascio
	                   			 		if (count == 2) {
		                   			 		setDisable(true);
	                                        setStyle(" -fx-opacity: 0.4 ;");
	                   			 		}else {
		                   			 		setDisable(false);
	                                        setStyle(" -fx-opacity: 1;");
	                                        
		                   			 		if (!item.equals("Ritiro passaporto")) {
			                   			 		setDisable(true);
		                                        setStyle(" -fx-opacity: 0.4 ;");
		                                    }
	                   			 		}
	                   			 	}
	                   			 /*if (!resultSet.next()) {
	                                    if (item.equals("Ritiro passaporto")) {
	                                        setDisable(true);
	                                        setStyle(" -fx-opacity: 0.4 ;");
	                                    }
	                                    	
	                                }else {
	                                	setDisable(true);
	                                	setStyle(" -fx-opacity: 0.4 ;");
                                     
	                                	if (item.equals("Ritiro passaporto")) {
	                                        setDisable(false);
	                                        setStyle(" -fx-opacity: 1 ;");
	                                    }
									}*/
	                        	}catch (Exception e) {
									// TODO: handle exception
								}
		                 }
		            }
		       };
		   }
		});
		// quando si seleziona qualcosa nelle choise box vengono chiamati i metodi tra parentesi
		getServizio().setOnAction(super::riempiRegioni);
		getRegione().setOnAction(super::riempiProvince);
		getProvincia().setOnAction(super::riempiCittà);
		getComune().setOnAction(super::riempiVia);
		
    }
	
	
	/**
	 * prende i dati dalla schermata precedente
	 * 
	 * @param idUtente
	 */
	public void passaDati(String idUtente, double width, double height) {
		borderPane.setPrefWidth(width);
		borderPane.setPrefHeight(height);
		
		borderPane.setMinWidth(1240);
		borderPane.setMinHeight(720);
		
		this.idUtente = idUtente;
		
		try {
 			Class.forName("com.mysql.cj.jdbc.Driver");
 			Connection connection = DriverManager.getConnection(getUrl(),getUsernameDB(),getPasswordDB());
 			PreparedStatement statement = connection.prepareStatement("SELECT TipoServizio, DataPrenotazione, OraPrenotazione FROM Prenotazioni, Servizi Where Prenotazioni.IdServizio = Servizi.IdServizio and IdUtente = ?");
 			statement.setString(1,idUtente);
 			ResultSet resultSet = statement.executeQuery();
 		  
 			
 			LocalDate data = null;
  			LocalTime ora = null;
  			if (resultSet.isBeforeFirst()) {
  				int count = 0;
  				while (resultSet.next()) {
 			 		count++;
 			 		data = resultSet.getDate(2).toLocalDate();
 			 		ora = resultSet.getTime(3).toLocalTime();
 			 		/*System.out.println(resultSet.getString(1));
 			 		System.out.println(resultSet.getString(2));
 			 		System.out.println(resultSet.getString(3));*/
 			 	}
  				
  				
  				if (count == 2 && LocalDate.now().isEqual(data) && LocalTime.now().isAfter(ora)) {
  					statement = connection.prepareStatement("DELETE FROM Prenotazioni WHERE IdUtente = ? and DataPrenotazione <= ? and OraPrenotazione <= ?");
  		 			statement.setString(1,idUtente);
  		 			statement.setString(2, LocalDate.now().toString());
  		 			statement.setString(3, LocalTime.now().toString());
  		 			//statement.setString(3, ora);
  		 			System.out.println("righe: " + statement.executeUpdate());
  				}
  			}
  			 	
  			 	
       	}catch (Exception e) {
       		System.out.println(e);
       	}
		
	}
	
	/**
	 * prende dal db i servizi
	 * 
	 */
	private void riempiServizi() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection connection = DriverManager.getConnection(getUrl(),getUsernameDB(),getPasswordDB());
			PreparedStatement statement = connection.prepareStatement("SELECT `TipoServizio` FROM `Servizi`");
			ResultSet resultSet = statement.executeQuery();
			
			 while (resultSet.next())
				 servizi.add(resultSet.getString(1));
					 
		}catch(Exception e) {
			System.out.println(e);
		}
	}
	
	public void avanti(ActionEvent event) {
		// se nelle choice box non si seleziona niente esce un pop up di errore, 
		// altrimenti passa idUtente, servizio, regione, provincia, città, via alla schermata successiva
		if(getServizio().getValue() == null || getRegione().getValue() == null || getProvincia().getValue() == null || getComune().getValue() == null || getVia().getValue() == null) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Campi non compilati");
			alert.setHeaderText("Devi compilare tutti i campi prima di poter accedere alla prossima sezione");
			if(alert.showAndWait().get() == ButtonType.OK) {
				System.out.println("ho capito");
			}
		}else {
			try {
				String servizio = getServizio().getValue(); 
				String regione = getRegione().getValue(); 
				String provincia = getProvincia().getValue(); 
				String città = getComune().getValue(); 
				
				String via = getVia().getValue();
				int commaIndex = via.indexOf(",");
		        if (commaIndex != -1)
		        	via = via.substring(commaIndex + 1).trim();
			
				
				FXMLLoader loader = new FXMLLoader(getClass().getResource("DataOraScene.fxml"));
				root = loader.load();
				
				ControllerDataOra controllerDataOra = loader.getController();
				controllerDataOra.passaDati(idUtente, servizio, regione, provincia, città, via, borderPane.getWidth(), borderPane.getHeight());
				
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



