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

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.stage.Stage;

public class ControllerPersonale extends Persona implements Initializable {
	private TreeSet<LocalTime> oreDisponibiliTotali= new TreeSet<LocalTime>();
	private TreeSet<LocalTime> oreNonDisponibili=new TreeSet<LocalTime>();
	
	@FXML
	private ImageView myImage;
	@FXML
	private BorderPane borderPane;
	@FXML
	private ChoiceBox<LocalTime> oraChoiceBox;
	@FXML
	private DatePicker dataPicker;

	
	private String[] serviziDisponibili= {
			"Ritiro passaporto", "Rilascio passaporto per la prima volta",
			"Rilascio passaporto per scadenza dal precedente",
			"Rilascio per furto", "Rilascio per deterioramento",
			"Rilascio per altro"
	};
	
	private Stage stage;
	private Scene scene;
	private Parent root;
	
	public void passaDati(double width, double height) {
		borderPane.setPrefWidth(width);
		borderPane.setPrefHeight(height);
		
		borderPane.setMinWidth(1240);
		borderPane.setMinHeight(720);
	}
	
	public void inserimentoDati(ActionEvent event) {
		Alert alert;
		if(oraChoiceBox.getValue()!=null) {
			ButtonType confermo = new ButtonType("Confermo", ButtonData.OK_DONE);
			alert = new Alert(AlertType.CONFIRMATION, "", confermo, ButtonType.CANCEL);
			alert.setTitle("Prenotazione");
			alert.setHeaderText("Confermi di voler inserire per il servizio \"" + getServizio().getValue().toLowerCase() + "\" il " + dataPicker.getValue() + " alle ore " + oraChoiceBox.getValue());
			if(alert.showAndWait().get() == confermo) {
				try{
					/*STATEMENT PER INSERIRE I DATI*/
					Class.forName("com.mysql.cj.jdbc.Driver");
					System.out.println("Id sede: "+getIdSede());
					Connection connection = DriverManager.getConnection(getUrl(), getUsernameDB(),getPasswordDB());
					PreparedStatement statement = connection.prepareStatement("INSERT INTO Servizi (TipoServizio, Data, OraInizio, IdSede, IstanzaServizio) VALUES (?,?,?,?,?)");
						
					statement.setString(1, super.getServizio().getValue());
					statement.setString(2, dataPicker.getValue().toString());
					statement.setString(3, oraChoiceBox.getValue().toString());
					statement.setString(4, String.valueOf(getIdSede()));
					System.out.println("ciccio");
					int nIstanze= ricavaIstanze(connection);
					System.out.println(dataPicker.getValue().toString());
					System.out.println(super.getServizio().getValue());
					System.out.println(oraChoiceBox.getValue().toString());
					statement.setString((5),String.valueOf(nIstanze));
					System.out.println(nIstanze);
					System.out.println(String.valueOf(getIdSede()));
					statement.executeUpdate();
					//cambia la scena
					cambiaARichiesetaInserita(event);
				}catch(Exception e) {
					System.out.println(e.toString()+ "cicco pasticcio");
				}	
			}
		}else {
			//forErrorLabel.setText("Non hai inserito tutti i campi");
			alert = new Alert(AlertType.ERROR);
			alert.setTitle("Non hai inserito tutti i campi");
			alert.setHeaderText("Devi selezionare una data e un'ora per poter prenotare");
			if(alert.showAndWait().get() == ButtonType.OK) {
				System.out.println("ho capito");
			}
		}	
	}
	//INIZIO RIEMPIMENTO IN MODO INTELLIGENTE
	public void initialize(URL arg0, ResourceBundle arg1) {
		mainPane = borderPane;
		
		borderPane.widthProperty().addListener((observable, oldValue, newValue) -> {
        	myImage.setFitWidth(borderPane.getWidth() / 3);
        });

		borderPane.heightProperty().addListener((observable, oldValue, newValue) -> {
			myImage.setFitHeight(borderPane.getHeight());
	    });
		getServizio().getItems().addAll(serviziDisponibili);
		//!!!servizio attivva riempiREGIONI
		getServizio().setOnAction(this::riempiRegioni);
		getRegione().setOnAction(this::riempiProvince);
		getProvincia().setOnAction(this::riempiCittà);
		getComune().setOnAction(this::riempiVia);
		getVia().setOnAction(this::inizializzaData);
		dataPicker.setOnAction(this::inizializzaOra);	
    }
		
	protected void riempiRegioni(ActionEvent event) {
		dataPicker.setDisable(true);
		oraChoiceBox.setDisable(true);
		oraChoiceBox.getItems().clear();
		oreDisponibiliTotali.clear();
		oreNonDisponibili.clear();
		super.riempiRegioni(event);
		dataPicker.getEditor().clear();
	}
	
	protected void riempiProvince(ActionEvent event) {	
		super.riempiProvince(event);
		dataPicker.setDisable(true);
		oraChoiceBox.setDisable(true);
		dataPicker.getEditor().clear();
		oraChoiceBox.getItems().clear();
		oreDisponibiliTotali.clear();
		oreNonDisponibili.clear();
	}

	protected void riempiCittà(ActionEvent event) {	
		super.riempiCittà(event);
		dataPicker.getEditor().clear();
		dataPicker.setDisable(true);
		oraChoiceBox.setDisable(true);
		oraChoiceBox.getItems().clear();
		oreDisponibiliTotali.clear();
		oreNonDisponibili.clear();
	}
	
	protected void riempiVia(ActionEvent event) {
		super.riempiVia(event);
		dataPicker.getEditor().clear();
		dataPicker.setDisable(true);
		oraChoiceBox.setDisable(true);
		oraChoiceBox.getItems().clear();
		oreDisponibiliTotali.clear();
		oreNonDisponibili.clear();
	}
	
	public void inizializzaData(ActionEvent event) {
		
		oraChoiceBox.setDisable(true);
	
		dataPicker.setDayCellFactory(picker -> new DateCell() {
		        public void updateItem(LocalDate date, boolean empty) {
		            super.updateItem(date, empty);
		            LocalDate today = LocalDate.now();
		            setDisable(date.compareTo(today) < 0 );
		        }
		    });
		dataPicker.setDisable(false);
	}
	
	public void inizializzaOra(ActionEvent event) {
		oraChoiceBox.getItems().clear();
		oreDisponibiliTotali.clear();
		oreNonDisponibili.clear();
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection connection = DriverManager.getConnection(getUrl(), getUsernameDB(),getPasswordDB());
			PreparedStatement statement= connection.prepareStatement("select OraInizio from Servizi where  IdSede=? and Data=?");
			//RICAVA ID SEDE E LA METTE NELLA CAMPO IDSEDE
			statement.setString(1, String.valueOf(getIdSede()));
			statement.setString(2, String.valueOf(dataPicker.getValue()));
			System.out.println("idSede:"+getIdSede());
			System.out.println("data:"+dataPicker.getValue());
			ResultSet resultSet = statement.executeQuery();
			riempiOreIniziali();
			while(resultSet.next()) {
				LocalTime tmp = LocalTime.parse(resultSet.getString(1));
				oreNonDisponibili.add(tmp);
			}
			System.out.println(oreDisponibiliTotali.toString());
			System.out.println(oreNonDisponibili.toString());
			oreDisponibiliTotali.removeAll(oreNonDisponibili);
			System.out.println(oreDisponibiliTotali.toString());
			
			oraChoiceBox.getItems().addAll(oreDisponibiliTotali);
			oraChoiceBox.setDisable(false);;
		}catch(Exception e) {
			System.out.println(e);
		}
	}
	
	private void riempiOreIniziali() {
		//visto che è disponibile selezionare la data odierna
		//se non viene selezionata una data odierna perforza la data selezionata è maggiore e possono essere inseriti tutti gli orari
		//altrimenti se la data è uguale seleziona solo quelli che hanno orario maggiore a quello attuale
		if(LocalDate.now()!= dataPicker.getValue() || LocalTime.now().compareTo(LocalTime.of(8, 0))<0)
			oreDisponibiliTotali.add(LocalTime.of(8,0));
		if(LocalDate.now()!= dataPicker.getValue() ||  LocalTime.now().compareTo(LocalTime.of(9, 0))<0)
			oreDisponibiliTotali.add(LocalTime.of(9,0));
		if(LocalDate.now()!= dataPicker.getValue() ||  LocalTime.now().compareTo(LocalTime.of(10, 0))<0)
			oreDisponibiliTotali.add(LocalTime.of(10,0));
		if(LocalDate.now()!= dataPicker.getValue() ||  LocalTime.now().compareTo(LocalTime.of(11, 0))<0)
			oreDisponibiliTotali.add(LocalTime.of(11,0));
		if(LocalDate.now()!= dataPicker.getValue() ||  LocalTime.now().compareTo(LocalTime.of(12, 0))<0)
			oreDisponibiliTotali.add(LocalTime.of(12,0));
		if(LocalDate.now()!= dataPicker.getValue() ||  LocalTime.now().compareTo(LocalTime.of(13, 0))<0)
			oreDisponibiliTotali.add(LocalTime.of(13,0));
		if(LocalDate.now()!= dataPicker.getValue() ||  LocalTime.now().compareTo(LocalTime.of(14, 0))<0)
			oreDisponibiliTotali.add(LocalTime.of(14,0));
		if(LocalDate.now()!= dataPicker.getValue() ||  LocalTime.now().compareTo(LocalTime.of(15, 0))<0)
			oreDisponibiliTotali.add(LocalTime.of(15,0));
		if(LocalDate.now()!= dataPicker.getValue() ||  LocalTime.now().compareTo(LocalTime.of(16, 0))<0)
			oreDisponibiliTotali.add(LocalTime.of(16,0));
		if(LocalDate.now()!= dataPicker.getValue() ||  LocalTime.now().compareTo(LocalTime.of(17, 0))<0)
			oreDisponibiliTotali.add(LocalTime.of(17,0));
		if(LocalDate.now()!= dataPicker.getValue() ||  LocalTime.now().compareTo(LocalTime.of(18, 0))<0)
			oreDisponibiliTotali.add(LocalTime.of(18,0));
	}
	
	public void cambiaARichiesetaInserita(ActionEvent event) throws IOException{
		FXMLLoader loader = new FXMLLoader(getClass().getResource("RichiestaInseritaScene.fxml"));
		root = loader.load();
		
		ControllerRichiestaInserita controllerRichiestaInserita = loader.getController();
		controllerRichiestaInserita.passaDati(mainPane.getWidth(), mainPane.getHeight());
		
		//root = FXMLLoader.load(getClass().getResource("RichiestaInseritaScene.fxml"));
		stage= (Stage)((Node)event.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
	
	private int ricavaIstanze(Connection connection) {
		int res=-1;
		try {
			PreparedStatement statement= connection.prepareStatement("SELECT NumeroDipendenti from Sede where IdSede=?");
			statement.setString(1, String.valueOf(getIdSede()));
			ResultSet resultSet=statement.executeQuery();
			if(!resultSet.next()) {
				System.out.println("Errore");
			}else {
				res=resultSet.getInt(1);
			}
		}catch(Exception e) {
			System.out.println(e);
		}
		return res;
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
