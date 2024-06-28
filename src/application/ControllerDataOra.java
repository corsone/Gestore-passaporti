package application;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeSet;
import java.util.stream.Collectors;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class ControllerDataOra extends ControllerMenu implements Initializable{
	
	private Stage stage;
	private Scene scene;
	private Parent root;
	
	private int numberWeek; // indica il numero di settimane da aggiungere quando si vuole cambiare settimana
	private HashMap<LocalDate, ArrayList<LocalTime>> settimana = new HashMap<>(); // contiene i giorni e gli orari all'interno della settimana
	private HashMap<LocalDate, TreeSet<LocalTime>> disponibilità = new HashMap<>(); // contiene i giorni e gli orari messi a disposizione dalla questura 
	private HashMap<LocalDate, TreeSet<LocalTime>> occupate = new HashMap<>(); // contiene le date e le ore già prenotate
	
	
	private LocalDate today; // contiene la data in cui sto prenotando, oppure un mese dopo
	private LocalDate dataPrenotazione; // se ho gia fatto la prenotazione per il rilascio, contiene la data altrimenti null 
	private LocalDate dataSelezionata; // contiene la data selezionata dall'utente
	private LocalTime oraSelezionata; // contiene l'ora selezionata dall'utente
	private int xBottone = 0, yBottone = 0; // posizione del bottone schiacciato
	private int xPrecedente = 0, yPrecedente = 0; // posizione del bottone schiacciato prima del bottone corrente
	
	// dati passati dalla schermata precedente/informazioni utili nelle query
	private String idUtente;
	private String idServizio;
	private String idSede;
	private String servizio;
	private String regione;
	private String provincia;
	private String città;
	private String via;
	
	private String url = "jdbc:mysql://localhost:3306/QuesturaDB";
	private String usernameDB = "root";
	private String passwordDB = "";
	
	@FXML
	private BorderPane borderPane;
	@FXML
	private Button precedente;
	@FXML
	private Label lunedi;
	@FXML
	private Label martedi;
	@FXML
	private Label mercoledi;
	@FXML
	private Label giovedi;
	@FXML
	private Label venerdi;
	@FXML
	private Label sabato;
	@FXML
	private GridPane gridPane;
	@FXML
	private Button prenotaBtn;
	@FXML
	private Button bottone;
	@FXML
	private HBox hBox;
	@FXML
	private Label unMeseLabel;
	@FXML
	private Label unMeseColor;
	
	
	public void initialize(URL arg0, ResourceBundle arg1) {
		mainPane = borderPane;
	}
	
	
	/**
	 * prende i dati dalla schermata precedente
	 * 
	 * @param idUtente
	 * @param servizio
	 * @param regione
	 * @param provincia
	 * @param città
	 * @param via
	 */
	public void passaDati(String idUtente, String servizio, String regione, String provincia, String città, String via, double width, double height) {
		borderPane.setPrefWidth(width);
		borderPane.setPrefHeight(height);
		borderPane.setMinWidth(1280);
		borderPane.setMinHeight(720);
		
		this.idUtente = idUtente;
		this.servizio = servizio;
		this.regione = regione;
		this.provincia = provincia;
		this.città = città;
		this.via = via;
		
		numberWeek = 0;
		setGiorni();
	}
	
	/**
	 * se nella settimana non c'è il giorno in cui sto prenotando
	 * quando si schiaccia il bottone per tornare in dietro di una settimana diminuisce di 1 la variabile numberWeek
	 * in questo modo non posso andare piu indietro della settimana contenente il giorno corrente
	 *  
	 */
	public void settimanaPrecedente() {
		if (!settimana.containsKey(LocalDate.now())) {
			numberWeek -=1;	
			setGiorni();
		}
			
	}
	
	/**
	 * aggiunge 1 alla variabile numberWeek quando si schiaccia il bottone per andare in avanti di una settimana
	 * 
	 */
	public void settimanaSuccessiva() {
		numberWeek += 1;
		setGiorni();
	}
	
	/**
	 * setta i giorni della settimana per riempire le lable
	 * riempie l'hashmap settimana con le date e gli orari
	 * 
	 */
	public void setGiorni() {
		// se il servizio selezionato è "ritiro passaporto" fa passare un mese, altrimenti no
		if (servizio.equals("Ritiro passaporto")) {
			
			unMeseColor.setVisible(true);
			unMeseLabel.setVisible(true);
			
			try {
				Class.forName("com.mysql.cj.jdbc.Driver");
				Connection connection = DriverManager.getConnection(url, usernameDB,passwordDB );
				PreparedStatement statement = connection.prepareStatement("select DataPrenotazione from Prenotazioni WHERE idUtente = ?");
				statement.setString(1, idUtente);
				ResultSet resultSet = statement.executeQuery();
				
				while(resultSet.next())
					dataPrenotazione = resultSet.getDate(1).toLocalDate();
			
			}catch (Exception e) {
				// TODO: handle exception
			}
			
			
			
			// tutti i mesi con 31 giorni, tranne luglio, il mese dopo vanno a 1, altrimenti vanno a 30 o 31
			if (LocalDate.now().getMonth() != Month.JULY && LocalDate.now().getDayOfMonth() == 31) {
				today =  dataPrenotazione.plusDays(1).plusMonths(1);
			}else{
				today =  dataPrenotazione.plusMonths(1);
			}
		}else {	
			unMeseLabel.setText("data antecedente alla data odierna");
			unMeseColor.setStyle("-fx-background-color: f5f5f5; -fx-border-color: black;");
			
			// tutti i mesi con 31 giorni, tranne luglio, il mese dopo vanno a 1, altrimenti vanno a 30 o 31
			if (LocalDate.now().getMonth() != Month.JULY && LocalDate.now().getDayOfMonth() == 31) {
				today =  LocalDate.now().plusDays(1);
			}else{
				today =  LocalDate.now();
			}
		}
		
		// aggiunge alla data il numero di settimane
		LocalDate date = today.plusWeeks(numberWeek);
		

		DayOfWeek dayOfWeek = date.getDayOfWeek();
		if (dayOfWeek == DayOfWeek.SUNDAY) {
			dayOfWeek = date.plusDays(1).getDayOfWeek();
		}
			
		
		ArrayList<LocalTime> orari = new ArrayList<LocalTime>();
		
		orari.clear();
		settimana.clear();
		
		orari.add(LocalTime.of(8, 0));
		orari.add(LocalTime.of(9, 0));
		orari.add(LocalTime.of(10, 0));
		orari.add(LocalTime.of(11, 0));
		orari.add(LocalTime.of(12, 0));
		orari.add(LocalTime.of(13, 0));
		orari.add(LocalTime.of(14, 0));
		orari.add(LocalTime.of(15, 0));
		orari.add(LocalTime.of(16, 0));
		orari.add(LocalTime.of(17, 0));
		orari.add(LocalTime.of(18, 0));
		
		// setta le etichette delle date e aggiunge all'hashmap settimana le date e gli orari
		switch(dayOfWeek) {
			case MONDAY:
	            lunedi.setText("Lunedì\n" + date);
	            martedi.setText("Martedì\n" + date.plusDays(1));
	            mercoledi.setText("Mercoledì\n" + date.plusDays(2));
	            giovedi.setText("Giovedì\n" + date.plusDays(3));
	            venerdi.setText("Venerdì\n" + date.plusDays(4));
	            sabato.setText("Sabato\n" + date.plusDays(5));
	            
	            settimana.put(date, orari);
	            settimana.put(date.plusDays(1), orari);
	            settimana.put(date.plusDays(2), orari);
	            settimana.put(date.plusDays(3), orari);
	            settimana.put(date.plusDays(4), orari);
	            settimana.put(date.plusDays(5), orari);
	            
	            break;
	            
	        case TUESDAY:
	        	lunedi.setText("Lunedì\n" + date.minusDays(1));
	            martedi.setText("Martedì\n" + date);
	            mercoledi.setText("Mercoledì\n" + date.plusDays(1));
	            giovedi.setText("Giovedì\n" + date.plusDays(2));
	            venerdi.setText("Venerdì\n" + date.plusDays(3));
	            sabato.setText("Sabato\n" + date.plusDays(4));
	            
	            settimana.put(date.minusDays(1), orari);
	            settimana.put(date, orari);
	            settimana.put(date.plusDays(1), orari);
	            settimana.put(date.plusDays(2), orari);
	            settimana.put(date.plusDays(3), orari);
	            settimana.put(date.plusDays(4), orari);
	            
	            break;
	            
	        case WEDNESDAY:
	        	lunedi.setText("Lunedì\n" + date.minusDays(2));
	            martedi.setText("Martedì\n" + date.minusDays(1));
	            mercoledi.setText("Mercoledì\n" + date);
	            giovedi.setText("Giovedì\n" + date.plusDays(1));
	            venerdi.setText("Venerdì\n" + date.plusDays(2));
	            sabato.setText("Sabato\n" + date.plusDays(3));
	            
	            settimana.put(date.minusDays(2), orari);
	            settimana.put(date.minusDays(1), orari);
	            settimana.put(date, orari);
	            settimana.put(date.plusDays(1), orari);
	            settimana.put(date.plusDays(2), orari);
	            settimana.put(date.plusDays(3), orari);	            
	            break;
	            
	        case THURSDAY:
	        	lunedi.setText("Lunedì\n" + date.minusDays(3));
	            martedi.setText("Martedì\n" + date.minusDays(2));
	            mercoledi.setText("Mercoledì\n" + date.minusDays(1));
	            giovedi.setText("Giovedì\n" + date);
	            venerdi.setText("Venerdì\n" + date.plusDays(1));
	            sabato.setText("Sabato\n" + date.plusDays(2));
	            
	            settimana.put(date.minusDays(3), orari);
	            settimana.put(date.minusDays(2), orari);
	            settimana.put(date.minusDays(1), orari);
	            settimana.put(date, orari);
	            settimana.put(date.plusDays(1), orari);
	            settimana.put(date.plusDays(2), orari);
	            
	            
	            break;
	        case FRIDAY:
	        	lunedi.setText("Lunedì\n" + date.minusDays(4));
	            martedi.setText("Martedì\n" + date.minusDays(3));
	            mercoledi.setText("Mercoledì\n" + date.minusDays(2));
	            giovedi.setText("Giovedì\n" + date.minusDays(1));
	            venerdi.setText("Venerdì\n" + date);
	            sabato.setText("Sabato\n" + date.plusDays(1));
	            
	            settimana.put(date.minusDays(4), orari);
	            settimana.put(date.minusDays(3), orari);
	            settimana.put(date.minusDays(2), orari);
	            settimana.put(date.minusDays(1), orari);
	            settimana.put(date, orari);
	            settimana.put(date.plusDays(1), orari);
	            
	            break;
	            
	        case SATURDAY:
	        	lunedi.setText("Lunedì\n" + date.minusDays(5));
	            martedi.setText("Martedì\n" + date.minusDays(4));
	            mercoledi.setText("Mercoledì\n" + date.minusDays(3));
	            giovedi.setText("Giovedì\n" + date.minusDays(2));
	            venerdi.setText("Venerdì\n" + date.minusDays(1));
	            sabato.setText("Sabato\n" + date);
	            
	            settimana.put(date.minusDays(5), orari);
	            settimana.put(date.minusDays(4), orari);
	            settimana.put(date.minusDays(3), orari);
	            settimana.put(date.minusDays(2), orari);
	            settimana.put(date.minusDays(1), orari);
	            settimana.put(date, orari);
	            
	            break;
	            
	        default:
	            break;
		}
		
		// se all'interno della settimana c'è il giorno d'oggi il tasto per tornare alla settimana precedente si disabilita
		precedente.setDisable(settimana.containsKey(LocalDate.now())? true : false);
		
		riempiDisponibilità();
	}
	
	
	/**
	 * gestisce la logica dei bottoni per prenotare all'interno della tabella
	 * 
	 */
	public void riempiDisponibilità() {
		TreeSet<LocalDate> dateDisponibili = new TreeSet<LocalDate>();
		TreeSet<LocalDate> dateOccupate = new TreeSet<LocalDate>();
		disponibilità.clear();
		occupate.clear();
		try {
			// si salva l'id del servizio
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection connection = DriverManager.getConnection(url, usernameDB,passwordDB );
			PreparedStatement statement = connection.prepareStatement("select IdServizio from Servizi, Sede WHERE Servizi.IdSede = Sede.IdSede and Servizi.TipoServizio = ? and Sede.Regione = ? and Sede.Provincia = ? and Sede.Città = ? and Sede.Via = ?");
			statement.setString(1,servizio);
			statement.setString(2,regione);
			statement.setString(3,provincia);
			statement.setString(4,città);
			statement.setString(5,via);
			ResultSet resultSet = statement.executeQuery();
			
			while(resultSet.next())
				idServizio = resultSet.getString(1);
			// si salva l'id della sede
			statement = connection.prepareStatement("select IdSede from Sede WHERE Sede.Regione = ? and Sede.Provincia = ? and Sede.Città = ? and Sede.Via = ?");
			statement.setString(1,regione);
			statement.setString(2,provincia);
			statement.setString(3,città);
			statement.setString(4,via);
			resultSet = statement.executeQuery();
			
			while(resultSet.next())
				idSede = resultSet.getString(1);
			// si salva le date disponibili(quelle della tabella servizi)
			statement = connection.prepareStatement("select Data from Servizi, Sede WHERE Servizi.IdSede = Sede.IdSede and Sede.Regione = ? and Sede.Provincia = ? and Sede.Città = ? and Sede.Via = ?");
			//statement.setString(1,servizio);
			statement.setString(1,regione);
			statement.setString(2,provincia);
			statement.setString(3,città);
			statement.setString(4,via);
			resultSet = statement.executeQuery();
			
			 while (resultSet.next()) {
				 System.out.println(resultSet.getString(1));
				 dateDisponibili.add(resultSet.getDate(1).toLocalDate());
			 }
			// per ogni data disponibile si salva gli orari disponibili(quelle della tabella servizi)
			for(LocalDate date: dateDisponibili) {
				TreeSet<LocalTime> oreDisponibili = new TreeSet<LocalTime>();
				statement = connection.prepareStatement("select OraInizio from Servizi, Sede WHERE Servizi.IdSede = Sede.IdSede and Sede.Regione = ? and Sede.Provincia = ? and Sede.Città = ? and Sede.Via = ? and Servizi.Data = ?");
				//statement.setString(1,servizio);
				statement.setString(1,regione);
				statement.setString(2,provincia);
				statement.setString(3,città);
				statement.setString(4,via);
				statement.setString(5,date + "");
				resultSet = statement.executeQuery();
				while (resultSet.next()) {
					//System.out.println(resultSet.getString(1));
					oreDisponibili.add(resultSet.getTime(1).toLocalTime());
				}
				disponibilità.put(date, oreDisponibili);
			}
			
			// si salva le date occupate(quelle della tabella prenotazioni)
			statement = connection.prepareStatement("select DataPrenotazione from Prenotazioni, Servizi, Sede WHERE Prenotazioni.IdServizio = Servizi.IdServizio and Prenotazioni.IdSede = Sede.IdSede and Sede.Regione = ? and Provincia = ? and Città = ? and Via = ?");
			statement.setString(1,regione);
			statement.setString(2,provincia);
			statement.setString(3,città);
			statement.setString(4,via);
			resultSet = statement.executeQuery();
			 while (resultSet.next()) 
				 dateOccupate.add(resultSet.getDate(1).toLocalDate());
			 
			// per ogni data occupata si salva gli orari occupati(quelle della tabella prenotazioni)
			 for(LocalDate date: dateOccupate) {
					TreeSet<LocalTime> oreOccupate = new TreeSet<LocalTime>();
					statement = connection.prepareStatement("select OraPrenotazione from Prenotazioni, Servizi, Sede WHERE Prenotazioni.IdServizio = Servizi.IdServizio and Prenotazioni.IdSede = Sede.IdSede and Sede.Regione = ? and Provincia = ? and Città = ? and Via = ?");
					statement.setString(1,regione);
					statement.setString(2,provincia);
					statement.setString(3,città);
					statement.setString(4,via);
					resultSet = statement.executeQuery();
					while (resultSet.next()) 
						oreOccupate.add(resultSet.getTime(1).toLocalTime());
					
					occupate.put(date, oreOccupate);
				}
			 
			// ordina l'hash map
				settimana = settimana.entrySet()
		                .stream()           
		                .sorted(Map.Entry.comparingByKey())
		                .collect(Collectors.toMap(
		                                    Map.Entry::getKey,
		                                    Map.Entry::getValue,
		                                    (oldValue, newValue) -> oldValue,
		                                    LinkedHashMap::new));
				
			 // per ogni giorno e ora della settimana controlla se la data è stata gestita -> controlla se è libera
			 int r=1, c=1;
			 int numeroDipendenti= 0;
				for(LocalDate date: settimana.keySet()) {
					r=1;
					for(LocalTime time: settimana.get(date)) { 	
						// per ogni bottone imposta l'etichetta con il numero dei dipendenti
						numeroDipendenti = 0;
						statement = connection.prepareStatement("select IstanzaServizio from Servizi, Sede WHERE Servizi.IdSede = Sede.IdSede and Servizi.Data = ? and Servizi.OraInizio = ?");						
						statement.setString(1, date + "");
						statement.setString(2, time + "");
						resultSet = statement.executeQuery();
							
							
						 while (resultSet.next()) {
							 numeroDipendenti = resultSet.getInt(1);
						 }
						 
						
						if(c<7 && r<12) {
							((Button) getNodeFromGridPane(r, c)).setText("");
							
							// setta i colori e disable dei bottoni
							if (date.isBefore(LocalDate.now()) || (date.isEqual(LocalDate.now()) && time.isBefore(LocalTime.now()))){
								getNodeFromGridPane(r, c).setStyle(null);
								getNodeFromGridPane(r, c).setDisable(true);
								
							}else if(dataPrenotazione != null && (date.isAfter(LocalDate.now()) || date.isEqual(LocalDate.now())) && date.isBefore(today)) {
										getNodeFromGridPane(r, c).setStyle("-fx-background-color:  #ffad00;");
										getNodeFromGridPane(r, c).setDisable(true);
									}else if (!isGestita(date, time)) {
										getNodeFromGridPane(r, c).setDisable(false);
										getNodeFromGridPane(r, c).setStyle("-fx-background-color:  #d9d9d9;");
									}else {
										if (isLibera(date, time, numeroDipendenti)) {
											getNodeFromGridPane(r, c).setStyle("-fx-background-color: #adff2f;");
								    		getNodeFromGridPane(r, c).setDisable(false);
											 ((Button) getNodeFromGridPane(r, c)).setText(numeroDipendenti + "");
										}else{
											((Button) getNodeFromGridPane(r, c)).setText(0 + "");
											getNodeFromGridPane(r, c).setStyle("-fx-background-color: #ff4444;");
								       	 	getNodeFromGridPane(r, c).setDisable(true);
								       	 
										}	
					       	}							
						}
						r++;
					}
					c++;
				}		

		}catch(Exception e) {
			System.out.println(e);
		}
		
		// in base al bottone schiacciato si salva la posizione del bottone, l'ora selezionata e la data
		for(int c=1;c<7;c++) {
			int column = c;
			for(int r=1;r<12;r++) {
				int row = r;
				((Button) getNodeFromGridPane(r, c)).setOnAction(event -> {
					String data;
					switch(column) {
						case 1:
							data = ((Label)getNodeFromGridPane(0, column)).getText().replace("Lunedì\n", "");
							dataSelezionata = LocalDate.parse(data);
							break;
						case 2:
							data = ((Label)getNodeFromGridPane(0, column)).getText().replace("Martedì\n", "");
							dataSelezionata = LocalDate.parse(data);
							break;
						case 3:
							data = ((Label)getNodeFromGridPane(0, column)).getText().replace("Mercoledì\n", "");
							dataSelezionata = LocalDate.parse(data);
							break;
						case 4:
							data = ((Label)getNodeFromGridPane(0, column)).getText().replace("Giovedì\n", "");
							dataSelezionata = LocalDate.parse(data);
							break;
						case 5:
							data = ((Label)getNodeFromGridPane(0, column)).getText().replace("Venerdì\n", "");
							dataSelezionata = LocalDate.parse(data);
							break;
						case 6:
							data = ((Label)getNodeFromGridPane(0, column)).getText().replace("Sabato\n", "");
							dataSelezionata = LocalDate.parse(data);
							break;
						default:
							break;
					}
					
					oraSelezionata = LocalTime.parse(((Label)getNodeFromGridPane(row, 0)).getText());
					
					if (xBottone != 0 && yBottone != 0) {
						xPrecedente = xBottone;
						yPrecedente = yBottone;
						
						String coloreBottonePrecedente = ((Button)getNodeFromGridPane(yPrecedente, xPrecedente)).getStyle();
						
						if (coloreBottonePrecedente.equals("-fx-background-color: #87cefa;")) 
							((Button)getNodeFromGridPane(yPrecedente, xPrecedente)).setStyle("-fx-background-color: #adff2f;");
						else
							((Button)getNodeFromGridPane(yPrecedente, xPrecedente)).setStyle(coloreBottonePrecedente);
					}
					
					xBottone = column;
					yBottone = row;
					
					System.out.println("bottone in poszione: " +  xBottone + " " + yBottone);
					
					
					String coloreBottone = ((Button)getNodeFromGridPane(yBottone, xBottone)).getStyle();
					System.out.println(coloreBottone);
					
					((Button)getNodeFromGridPane(yBottone, xBottone)).setStyle("-fx-background-color: #87cefa;");
					
					
					if (coloreBottone.equals("-fx-background-color:  #d9d9d9;")){
						ButtonType confermo = new ButtonType("Confermo", ButtonData.OK_DONE);
						Alert alert = new Alert(AlertType.WARNING, "", confermo, ButtonType.CANCEL);
						alert.setTitle("Notifiche");
						alert.setHeaderText("Vuoi attivare la notifica per il " + dataSelezionata + " alle ore " + oraSelezionata + "?");
						
						if(alert.showAndWait().get() == confermo) {
							
							try {
								Class.forName("com.mysql.cj.jdbc.Driver");
								Connection connection = DriverManager.getConnection(url, usernameDB,passwordDB );	
								PreparedStatement statement = connection.prepareStatement("INSERT INTO Notifiche (DataNotifica, OraNotifica, IdServizio, IdUtente, IdSede) values (?, ?, ?, ?, ?)");
								statement.setString(1,dataSelezionata + "");
								statement.setString(2,oraSelezionata + "");
								statement.setString(3,idServizio);
								statement.setString(4,idUtente);
								statement.setString(5, idSede);
								statement.executeUpdate();
							}catch (Exception e) {
									System.out.println(e);
							}
							try {
								ButtonType rimaneQui = new ButtonType("Rimani Qui", ButtonData.CANCEL_CLOSE);
								ButtonType visualizzaNotifiche = new ButtonType("visualizza Notifiche", ButtonData.OK_DONE);
								alert = new Alert(AlertType.INFORMATION ,"", visualizzaNotifiche, rimaneQui);
								alert.setTitle("notifica attivata");
								alert.setHeaderText("notifica attivata con successo! Quando la data sarà disponibile verrai avvisato nella sezione dedicata alle notifiche");
								
								if(alert.showAndWait().get() == rimaneQui) {
									
								}else {
									FXMLLoader loader = new FXMLLoader(getClass().getResource("NotificaScene.fxml"));
									root = loader.load();
									
									ControllerNotifica controllerNotifica = loader.getController();
									controllerNotifica.passaDati(borderPane.getWidth(), borderPane.getHeight(), idUtente);
									
									stage = (Stage)(((Node) event.getSource()).getScene().getWindow());
									scene = new Scene(root);
									stage.setScene(scene);
									stage.show();
								}
								
								
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						((Button)getNodeFromGridPane(yBottone, xBottone)).setStyle(coloreBottone);
					}					
					
					
		        });
			}
		}
	}
		
	// ritorna l'oggetto in posizione row col della grid pane
	private Node getNodeFromGridPane (final int row, final int col) {
	    for (Node node : gridPane.getChildren()) {
	    	Integer columnIndex = GridPane.getColumnIndex(node);
	    	Integer rowInteger = GridPane.getRowIndex(node);
	    	
	        if(columnIndex != null && columnIndex.intValue() == col && rowInteger != null && rowInteger.intValue() == row) {
	            	return node;
	        }
	    }

	    return null;
	}
	
	// controlla se la data è stata gestita dal personale della questura
	private boolean isGestita(LocalDate date, LocalTime time) {
		for(LocalDate d: disponibilità.keySet()) {
			for(LocalTime t: disponibilità.get(d)) {
				if (date.isEqual(d) && time.equals(t)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	// controlla se la data e l'ora sono libere
	private boolean isLibera(LocalDate date, LocalTime time, int numeroDipendenti) {			
		for(LocalDate d: occupate.keySet()) {
			System.out.println("data " + d);
			for(LocalTime o: occupate.get(d)) {
				System.out.println("ora " + o);
				if (date.isEqual(d) && time.equals(o) && numeroDipendenti == 0) {
					
					return false;
				}
			}
		}
		
		return true;
	}
	

	// prenota
	public void prenota(ActionEvent event) {
		int numeroDipendeti = 0;
		
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
			alert.setHeaderText("Confermi di voler prenotare per il servizio \"" + servizio.toLowerCase() + "\" il " + dataSelezionata + " alle ore " + oraSelezionata);
			
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
					
					statement = connection.prepareStatement("DELETE FROM Notifiche WHERE idUtente = ?");
					statement.setString(1, idUtente);
					//statement.setString(2, servizio);
					statement.executeUpdate();
					
					System.out.println("ho prenotato il " + dataSelezionata + " alle " + oraSelezionata);
					
					//riempiDisponibilità();
					
					try {
						FXMLLoader loader = new FXMLLoader(getClass().getResource("PrenotatoScene.fxml"));
						root = loader.load();
						
						ControllerPrenotato controllerPrenotato = loader.getController();
						controllerPrenotato.passaDati(borderPane.getWidth(), borderPane.getHeight(), servizio);
						
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
		// sse non ha selezionato nessun bottone da un alert di errore
		}else if (xBottone == 0 && yBottone == 0) {
			alert = new Alert(AlertType.ERROR);
			alert.setTitle("Data e ora non selezionati");
			alert.setHeaderText("Devi selezionare una data e un'ora per poter prenotare");
			if(alert.showAndWait().get() == ButtonType.OK) {
				System.out.println("ho capito");
			}
		// se il numero di dipendenti è <= 0 da un alert di errore
		}else {
			alert = new Alert(AlertType.ERROR);
			alert.setTitle("Orario non disponibile");
			alert.setHeaderText("L'orario selezionato non è più disponibile");
			if(alert.showAndWait().get() == ButtonType.OK) {
				riempiDisponibilità();
			}
		}
		//System.out.println("data: " + dataSelezionata);
		//System.out.println("ora: " + oraSelezionata);
	}
	
	public void back(ActionEvent event) {
		
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("ServiziScene.fxml"));
			root = loader.load();
			
			ControllerServizi controllerServizi = loader.getController();
			controllerServizi.passaDati(idUtente, borderPane.getWidth(), borderPane.getHeight());
			
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