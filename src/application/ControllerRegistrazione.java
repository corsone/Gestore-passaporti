package application;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;
import java.util.TreeSet;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class ControllerRegistrazione extends ControllerMenu implements Initializable{
	
	private Stage stage;
	private Scene scene;
	private Parent root;
	
	private TreeSet<String> città = new TreeSet<String>();
	private TreeSet<String> regioni = new TreeSet<String>();
	private TreeSet<String> province = new TreeSet<String>();
	 
	@FXML
	private ComboBox<String> cittaComboBox;
	@FXML
	private ComboBox<String> regioneComboBox;
	@FXML
	private ComboBox<String> provinciaComboBox;	
	@FXML
	private TextField nome;
	@FXML
	private TextField cognome;
	@FXML
	private DatePicker dataDiNascita;
	@FXML
	private TextField codiceFiscale;
	@FXML
	private TextField email;
	@FXML
	private TextField password;
	@FXML
	private BorderPane borderPane;
		
		
	private String url = "jdbc:mysql://localhost:3306/QuesturaDB";
	private String usernameDB = "root";
	private String passwordDB = "";
	
	
	public void initialize(URL arg0, ResourceBundle arg1) {
		mainPane = borderPane;
		
		provinciaComboBox.setDisable(true);
		cittaComboBox.setDisable(true);
		
		riempiRegioni();
		regioneComboBox.setOnAction(this::riempiProvince);
		provinciaComboBox.setOnAction(this::riempiCittà);
    }
	
	public void passaDati(double width, double height) {
		borderPane.setPrefWidth(width);
		borderPane.setPrefHeight(height);
		
		borderPane.setMinWidth(1240);
		borderPane.setMinHeight(720);
	}
	
	
	/**
	 * prende dal db le regioni e le aggiunge alla combo box
	 * 
	 * @param event
	 */
	private void riempiRegioni() {				
		regioneComboBox.getItems().clear();
		regioni.clear();
		
		cittaComboBox.setDisable(true);
		provinciaComboBox.setDisable(true);
		
		
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection connection = DriverManager.getConnection(url, usernameDB,passwordDB );
			PreparedStatement statement = connection.prepareStatement("SELECT Regione FROM Comuni");
			ResultSet resultSet = statement.executeQuery();
			
			while(resultSet.next()) {
				regioni.add(resultSet.getString(1));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		regioneComboBox.getItems().addAll(regioni);
	}
	
	
	/**
	 * prende dal db le province e le aggiunge alla combo box
	 * 
	 * @param event
	 */
	private void riempiProvince(ActionEvent event) {		
		provinciaComboBox.getItems().clear();
		province.clear();
		
		cittaComboBox.setDisable(true);
		provinciaComboBox.setDisable(true);
		
		if (regioneComboBox.getValue() != null && !regioneComboBox.getValue().isEmpty()) {
			try {
				Class.forName("com.mysql.cj.jdbc.Driver");
				Connection connection = DriverManager.getConnection(url, usernameDB,passwordDB );
				PreparedStatement statement = connection.prepareStatement("select Provincia from Comuni where Regione = ?");
				statement.setString(1,regioneComboBox.getValue());
				ResultSet resultSet = statement.executeQuery();
				
				 while (resultSet.next())
					 province.add(resultSet.getString(1));
				
				 provinciaComboBox.getItems().addAll(province);
				 
				 provinciaComboBox.setDisable(false);
				 
				 
				
			}catch(Exception e) {
				System.out.println(e);
			}
		}
	}
	
	/**
	 * prende dal db le città e le aggiunge alla combo box
	 * 
	 * @param event
	 */
	private void riempiCittà(ActionEvent event) {	
		cittaComboBox.getItems().clear();
		città.clear();
		
		if (regioneComboBox.getValue() != null && !regioneComboBox.getValue().isEmpty()) {

			try {
				Class.forName("com.mysql.cj.jdbc.Driver");
				Connection connection = DriverManager.getConnection(url, usernameDB,passwordDB );
				PreparedStatement statement = connection.prepareStatement("select Comune from Comuni WHERE Regione = ? and Provincia = ?");
				statement.setString(1,regioneComboBox.getValue());
				statement.setString(2,provinciaComboBox.getValue());
				ResultSet resultSet = statement.executeQuery();
				
				 while (resultSet.next())
					 città.add(resultSet.getString(1));
				
				 cittaComboBox.getItems().addAll(città);
				 
				cittaComboBox.setDisable(false);
				 
				  
			}catch(Exception e) {
				System.out.println(e);
			}
		}
	}
	
	/**
	 * registra l'utente
	 * 
	 * @param event
	 */
	public void registrati(ActionEvent event) {
		if(regioneComboBox.getValue() == null || provinciaComboBox.getValue() == null || cittaComboBox.getValue() == null || password == null || email == null || nome == null || cognome == null) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Campi non compilati");
			alert.setHeaderText("Devi compilare tutti i campi prima di poter accedere alla prossima sezione");
			if(alert.showAndWait().get() == ButtonType.OK) {
				System.out.println("ho capito");
			}
		}else {
			
			try {	
				Class.forName("com.mysql.cj.jdbc.Driver");
				Connection connection = DriverManager.getConnection(url, usernameDB,passwordDB );
				
				PreparedStatement statement = connection.prepareStatement("SELECT * FROM Anagrafica WHERE Anagrafica.Nome = ? and Anagrafica.Cognome = ? and Anagrafica.DataNascita = ?  and Anagrafica.CodiceFiscale = ? and Anagrafica.CittàNascita = ? and Anagrafica.Regione = ? and Anagrafica.Provincia = ?");
				statement.setString(1,nome.getText());
				statement.setString(2,cognome.getText());
				statement.setString(3,dataDiNascita.getValue() + "");
				statement.setString(4,codiceFiscale.getText());
				statement.setString(5,cittaComboBox.getValue());
				statement.setString(6,regioneComboBox.getValue());
				statement.setString(7,provinciaComboBox.getValue());
				ResultSet resultSet = statement.executeQuery();
				
				// se non c'è nell'anagrafica non registra l'utente altrimenti lo registra
				if(!resultSet.next()) {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("Persona assente nell'anagrafica");
					alert.setHeaderText("Contatta la questura per avere maggiori informazioni");
					if(alert.showAndWait().get() == ButtonType.OK) {
						try {
							root = FXMLLoader.load(getClass().getResource("ContattaciScene.fxml"));
							stage = (Stage)(((Node) event.getSource()).getScene().getWindow());
							scene = new Scene(root);
							stage.setScene(scene);
							/*stage.setWidth(borderPane.getWidth());
							stage.setHeight(borderPane.getHeight());
							stage.setMinWidth(960);
							stage.setMinHeight(640);*/
							stage.show();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}else {
					//System.out.println("purtroppo esisti");	
					// controlla se esiste già un utente registrato con gli stessi dati, se non c'è lo registra
					statement = connection.prepareStatement("SELECT * FROM `utenti` WHERE utenti.Nome = ? and utenti.Cognome = ? and utenti.CittàNascita = ? and utenti.Regione = ? and utenti.Provincia = ? and utenti.DataNascita = ? and utenti.CodiceFiscale = ? and utenti.Email = ?");
					statement.setString(1,nome.getText());
					statement.setString(2,cognome.getText());
					statement.setString(3,cittaComboBox.getValue());
					statement.setString(4,regioneComboBox.getValue());
					statement.setString(5,provinciaComboBox.getValue());
					statement.setString(6,dataDiNascita.getValue() + "");
					statement.setString(7,codiceFiscale.getText());
					statement.setString(8,email.getText());
					resultSet = statement.executeQuery();
					
					if(!resultSet.next()) {
						
						
						//System.out.println("ti registro subito");
						
						statement = connection.prepareStatement("INSERT INTO `Utenti`(`Nome`, `Cognome`, `CittàNascita`, `Regione`, `Provincia`, `DataNascita`, `Email`, `Password`, `CodiceFiscale`) VALUES (?,?,?,?,?,?,?,?,?)");
						statement.setString(1,nome.getText());
						statement.setString(2,cognome.getText());
						statement.setString(3,cittaComboBox.getValue());
						statement.setString(4,regioneComboBox.getValue());
						statement.setString(5,provinciaComboBox.getValue());
						statement.setString(6,dataDiNascita.getValue() + "");
						statement.setString(7,email.getText());
						statement.setString(8,password.getText());
						statement.setString(9,codiceFiscale.getText());
						statement.executeUpdate();
			
				           // rows affected
				          // System.out.println("inserendo in login modificate "+row);							
					}else {
						Alert alert = new Alert(AlertType.ERROR);
						alert.setTitle("Account già esistente");
						alert.setHeaderText("Non è possibile creare più di un account");
						if(alert.showAndWait().get() == ButtonType.OK) {
							//System.out.println("ho capito");
						}
					}
					
					
				}
			}catch(Exception e) {
				System.out.println(e);
			}
			
			
			try {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("LoginScene.fxml"));
				root = loader.load();
				
				ControllerLogin controllerLogin = loader.getController();
				controllerLogin.passaDati(mainPane.getWidth(), mainPane.getHeight());
				
				//root = FXMLLoader.load(getClass().getResource("HomeScene.fxml"));
				stage = (Stage)(((Node) event.getSource()).getScene().getWindow());
				scene = new Scene(root);
				stage.setScene(scene);
				stage.show();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("registrazione avvenuta");
				alert.setHeaderText("la registrazione è avvenuta con successo!");
				if(alert.showAndWait().get() == ButtonType.OK) {
					//System.out.println("ho capito");

				}
	
		}
	
	}
	
	/**
	 *  ritorna alla schermata precedente
	 *  
	 * @param event
	 */
	public void back(ActionEvent event) {
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
			e.printStackTrace();
		}
	}
	
}