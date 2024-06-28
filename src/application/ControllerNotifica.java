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

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

public class ControllerNotifica extends ControllerMenu implements Initializable{

	private Stage stage;
	private Scene scene;
	private Parent root;
	
	private String url = "jdbc:mysql://localhost:3306/QuesturaDB";
	private String usernameDB = "root";
	private String passwordDB = "";
	

	private LocalDate dataSelezionata;
	private LocalTime oraSelezionata;
	private String servizioSelezionato;
	
	private String idServizio;
	private String idSede;
	private String idUtente;
	
	@FXML
	private BorderPane borderPane;
	@FXML
	private VBox vBox;
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

	
	public void passaDati(double width, double height, String idUtente) {
		borderPane.setPrefWidth(width);
		borderPane.setPrefHeight(height);
		borderPane.setMinWidth(1280);
		borderPane.setMinHeight(720);
		
		this.idUtente = idUtente;
		
		mostraNotifiche();
		
	}
	
	
	public void mostraNotifiche() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection connection = DriverManager.getConnection(url, usernameDB,passwordDB );
			//seleziona le notifiche con id utente di quello loggato
			PreparedStatement statement = connection.prepareStatement("SELECT DataNotifica, OraNotifica, TipoServizio, Via, Denominazione, Notifiche.IdServizio, Notifiche.IdSede "
																	+ "FROM Notifiche, Sede, Servizi "
																	+ "WHERE Notifiche.IdUtente = ? and Notifiche.IdSede = Sede.IdSede and Notifiche.DataNotifica = Servizi.Data and Notifiche.OraNotifica = Servizi.OraInizio and Servizi.IstanzaServizio > 0");
			statement.setString(1, idUtente);
			ResultSet resultSet = statement.executeQuery();
			
			
			
			if (resultSet.isBeforeFirst()) {
				ScrollPane scrollPane = new ScrollPane();
				
				vBox.getChildren().remove(myImage);
				vBox.getChildren().remove(label);
				
				while(resultSet.next()) {
					int i=0;
					dataSelezionata = resultSet.getDate(1).toLocalDate();
					oraSelezionata = resultSet.getTime(2).toLocalTime();
					servizioSelezionato = resultSet.getString(3);
					idServizio = resultSet.getString(6);
					idSede = resultSet.getString(7);
					
					System.out.println("data: " + dataSelezionata);
					System.out.println("ora: " + oraSelezionata);
					
					Label label = new Label();
					
					label.setMaxWidth(Double.MAX_VALUE);
					label.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect:  dropshadow(three-pass-box, rgba(0,0,0,0.8), 10, 0, 0, 0);");
					label.setPadding(new Insets(20, 10, 20, 10));
			
					
					TextFlow textFlow = new TextFlow();
					
					Text servizioRichiestoText = new Text("il servizio " + servizioSelezionato.toLowerCase() + " è disponibile");
	                servizioRichiestoText.setFont(Font.font("Arial", 25));
	                servizioRichiestoText.setFill(Color.web("#275576"));
	               
	                Text data = new Text("\nData: ");
	                Text ora = new Text("\nOra : ");
	                Text sede = new Text("\nSede: ");
	                
	                data.setFont(Font.font("Arial", 16));
	                ora.setFont(Font.font("Arial", 16));
	                sede.setFont(Font.font("Arial", 16));
	                
	                
	                data.setStyle("-fx-font-weight: bold;");
	                ora.setStyle("-fx-font-weight: bold;");
	                sede.setStyle("-fx-font-weight: bold;");
	                
	                Text dataScelta = new Text(dataSelezionata.toString());
	                Text oraScelta = new Text(oraSelezionata.toString());
	                Text sedeScelta = new Text(resultSet.getString(5) + ", " + resultSet.getString(4));
	                
	                dataScelta.setFont(Font.font("Arial", 16));
	                oraScelta.setFont(Font.font("Arial", 16));
	                sedeScelta.setFont(Font.font("Arial", 16));
	                
	                textFlow.setLineSpacing(10);
	                
	                textFlow.getChildren().addAll(servizioRichiestoText, data, dataScelta, ora, oraScelta, sede, sedeScelta);
	                
	                

	                label.setGraphic(textFlow);
	                label.setMaxHeight(10);
	               
	                
	                vBox.prefWidthProperty().bind(scrollPane.widthProperty().add(-30));

					VBox.setMargin(label, new Insets(40, 0, 20, 0));
					
					
					
					vBox.getChildren().add(label);
					
					Button prenota = new Button("prenota");
					prenota.setStyle("-fx-background-color:  #275576;");
					prenota.setTextFill(Color.WHITE);
					prenota.setPrefWidth(83);
					prenota.setPrefHeight(36);
					
					Button elimina = new Button("elimina");
					elimina.setStyle("-fx-background-color:  #f6404a;");
					elimina.setTextFill(Color.WHITE);
					elimina.setPrefWidth(83);
					elimina.setPrefHeight(36);
					
					elimina.setOnMouseEntered(event -> {
						elimina.setCursor(Cursor.HAND);
			        });
					
					
					LocalDate dataDate = dataSelezionata;
					LocalTime oraTime = oraSelezionata;
					elimina.setOnAction(new EventHandler<ActionEvent>() {
						
						@Override
						public void handle(ActionEvent arg0) {
		
							eliminaNotifica(arg0, dataDate, oraTime);
						}
					});
					
					
					
					prenota.setOnMouseEntered(event -> {
						prenota.setCursor(Cursor.HAND);
			        });
					
					prenota.setOnAction(new EventHandler<ActionEvent>() {
						
						@Override
						public void handle(ActionEvent arg0) {
							// TODO Auto-generated method stub
							prenotaServizio(arg0, dataDate, oraTime);
						}
					});
					
					
					HBox hBox = new HBox();
					hBox.setSpacing(20);
					hBox.setStyle("-fx-padding: 0 0 20 0");
					hBox.setAlignment(javafx.geometry.Pos.CENTER);
					
					hBox.getChildren().addAll(elimina, prenota);
					vBox.getChildren().add(hBox);
					
					
					Separator separator = new Separator();
					vBox.getChildren().add(separator);
					
					scrollPane.setContent(vBox);
			        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS); 
			        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); 
			        
			        borderPane.getCenter().setStyle("-fx-padding: 20 50 20 50");
			        
			        borderPane.setCenter(scrollPane);

			        i++;
				}
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
	public void eliminaNotifica(ActionEvent event, LocalDate data, LocalTime ora) {
		System.out.println("sono in elimina servizio");
		
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection connection = DriverManager.getConnection(url, usernameDB,passwordDB );	
			PreparedStatement statement = connection.prepareStatement("DELETE FROM Notifiche where IdUtente = ? and DataNotifica = ? and OraNotifica = ?");
			statement.setString(1, idUtente);
			statement.setString(2, data.toString());
			statement.setString(3, ora.toString());
			statement.executeUpdate();
			
			cambiaNotifica(event);
			
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	public void prenotaServizio(ActionEvent event, LocalDate data, LocalTime ora) {
		int numeroDipendeti = 0;
		
		System.out.println("sono in prenota servizio");
		// guarda il numero dei dipendeti 
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection connection = DriverManager.getConnection(url, usernameDB,passwordDB );	
			PreparedStatement statement = connection.prepareStatement("select IstanzaServizio from Servizi, Sede WHERE Servizi.IdSede = Sede.IdSede and Servizi.Data = ? and Servizi.OraInizio = ?");						
			statement.setString(1, dataSelezionata + "");
			statement.setString(2, oraSelezionata + "");
			ResultSet resultSet = statement.executeQuery();
			
			while (resultSet.next()) {
				 System.out.println("\t\tnumero dipendenti: " + resultSet.getString(1));
				 numeroDipendeti = resultSet.getInt(1);
			 }
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		// se il numero dei dipendenti è > 0 chiede conferma per prenotare
		Alert alert;
		if (numeroDipendeti > 0) {
			numeroDipendeti--;
			ButtonType confermo = new ButtonType("Confermo", ButtonData.OK_DONE);
			alert = new Alert(AlertType.CONFIRMATION, "", confermo, ButtonType.CANCEL);
			alert.setTitle("Prenotazione");
			alert.setHeaderText("Confermi di voler prenotare per il servizio \"" + servizioSelezionato.toLowerCase() + "\" il " + dataSelezionata + " alle ore " + oraSelezionata);
			
			if(alert.showAndWait().get() == confermo) {
				try {
					Class.forName("com.mysql.cj.jdbc.Driver");
					Connection connection = DriverManager.getConnection(url, usernameDB,passwordDB );	
					PreparedStatement statement = connection.prepareStatement("INSERT INTO Prenotazioni (DataPrenotazione, OraPrenotazione, IdServizio, IdUtente, IdSede) values (?, ?, ?, ?, ?)");
					statement.setString(1,dataSelezionata + "");
					statement.setString(2,oraSelezionata + "");
					statement.setString(3,idServizio);
					statement.setString(4,idUtente);
					statement.setString(5, idSede);
					statement.executeUpdate();
					

					statement = connection.prepareStatement("UPDATE Servizi Set IstanzaServizio = ? where Data = ? and OraInizio = ?");
					statement.setString(1,numeroDipendeti + "");
					statement.setString(2,dataSelezionata + "");
					statement.setString(3,oraSelezionata + "");
					statement.executeUpdate();
					
					statement = connection.prepareStatement("DELETE FROM Notifiche where IdUtente = ?");
					statement.setString(1, idUtente);
					statement.executeUpdate();
					
					
					System.out.println("ho prenotato il " + dataSelezionata + " alle " + oraSelezionata);
					
					//riempiDisponibilità();
					
					try {
						FXMLLoader loader = new FXMLLoader(getClass().getResource("PrenotatoScene.fxml"));
						root = loader.load();
						
						ControllerPrenotato controllerPrenotato = loader.getController();
						controllerPrenotato.passaDati(borderPane.getWidth(), borderPane.getHeight(), servizioSelezionato);
						
						//root = FXMLLoader.load(getClass().getResource("PrenotatoScene.fxml"));
						stage = (Stage)(((Node) event.getSource()).getScene().getWindow());
						scene = new Scene(root);
						stage.setScene(scene);
						stage.show();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}catch (Exception e) {
					System.out.println(e);
				}
			}
		}else {
			alert = new Alert(AlertType.ERROR);
			alert.setTitle("Orario non disponibile");
			alert.setHeaderText("L'orario selezionato non è più disponibile");
			if(alert.showAndWait().get() == ButtonType.OK) {
				mostraNotifiche();
			}
		}
	}
}
