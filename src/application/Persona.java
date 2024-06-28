package application;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;
import java.util.TreeSet;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;

public abstract class Persona extends ControllerMenu implements Initializable {
	private TreeSet<String> regioni = new TreeSet<String>();
	private TreeSet<String> province = new TreeSet<String>();
	private TreeSet<String> comuni = new TreeSet<String>();
	private TreeSet<String> vie = new TreeSet<String>();
	@FXML
	private ComboBox<String> servizioComboBox;
	@FXML
	private ComboBox<String> regioneComboBox;
	@FXML
	private ComboBox<String> provinciaComboBox;
	@FXML
	private ComboBox<String> comuneComboBox;
	@FXML
	private ComboBox<String> viaComboBox;
	@FXML
	private Button prenotaBtn;
	
	private int idSede=-1;

	private String url = "jdbc:mysql://localhost:3306/QuesturaDB";
	private String usernameDB = "root";
	private String passwordDB = "";
	public abstract void initialize(URL arg0, ResourceBundle arg1);
	
	/*Riempie le regioni disponibili nel suo choicebox*/
	protected void riempiRegioni(ActionEvent event) {
		//tira via tutte le regioni precedenti e disabilita i choicebox che sono "gerarchicamente" piu in basso di regione
		regioneComboBox.getItems().clear();
		regioni.clear();
		provinciaComboBox.setDisable(true);
		comuneComboBox.setDisable(true);
		viaComboBox.setDisable(true);
		try {
			//si connette al database e seleziona le regioni disponibili nella tabella sede 
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection connection = DriverManager.getConnection(url, usernameDB,passwordDB );
			PreparedStatement statement;
		
			statement= connection.prepareStatement("select Regione from Sede");
			ResultSet resultSet = statement.executeQuery();
			//aggiunge le regioni trovate nella tabella sede in un treeset in modo da non avere copioni
			while (resultSet.next())
				regioni.add(resultSet.getString(1));
			//aggiunge le regioni al choicebox e abilita il choicebox regione
			regioneComboBox.getItems().addAll(regioni);
			regioneComboBox.setDisable(false);
		}catch(Exception e) {
			System.out.println(e);
		}
	}
	
	/*Riempie le province disponibili nel suo choicebox*/
	protected void riempiProvince(ActionEvent event) {	
		//tira via tutte le province precedenti e disabilita i choicebox che sono "gerarchicamente" piu in basso di province
		provinciaComboBox.getItems().clear();
		province.clear();
		comuneComboBox.setDisable(true);
		viaComboBox.setDisable(true);
		
		try {
			//si connette al database e seleziona le province disponibili nella tabella sede che hanno la regione selezionata nel choicebox
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection connection = DriverManager.getConnection(url, usernameDB,passwordDB );
			PreparedStatement statement;
			
			statement = connection.prepareStatement("select Provincia from Sede WHERE Sede.Regione = ?");
			statement.setString(1,regioneComboBox.getValue());
			ResultSet resultSet = statement.executeQuery();
			//aggiunge le regioni trovate nella tabella sede in un treeset in modo da non avere copioni
			while (resultSet.next())
				province.add(resultSet.getString(1));
			//aggiunge le province al choicebox e abilita il choicebox provincia
			provinciaComboBox.getItems().addAll(province);
			provinciaComboBox.setDisable(false);
			
		}catch(Exception e) {
			System.out.println(e);
		}
	}
	/*Riempie le città disponibili nel suo choicebox*/
	protected void riempiCittà(ActionEvent event) {	
		//tira via tutte le città precedenti e disabilita i choicebox che sono "gerarchicamente" piu in basso di città
		comuneComboBox.getItems().clear();
		comuni.clear();
		viaComboBox.setDisable(true);
		try {
			//si connette al database e seleziona le città disponibili nella tabella sede che hanno la provincia selezionata nel choicebox
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection connection = DriverManager.getConnection(url, usernameDB,passwordDB );
			PreparedStatement statement;
	
			statement= connection.prepareStatement("select Città from Sede, Servizi WHERE Sede.Regione = ? and Sede.Provincia = ?");
			statement.setString(1,regioneComboBox.getValue());
			statement.setString(2,provinciaComboBox.getValue());
			ResultSet resultSet = statement.executeQuery();
			//aggiunge le città trovate nella tabella sede in un treeset in modo da non avere copioni
			while (resultSet.next())
				comuni.add(resultSet.getString(1));
			//aggiunge le città al choicebox e abilita il choicebox città
			comuneComboBox.getItems().addAll(comuni);
			comuneComboBox.setDisable(false);
			
		}catch(Exception e) {
			System.out.println(e);
		}
	}
	/*Riempie le vie disponibili nel suo choicebox*/
	protected void riempiVia(ActionEvent event) {
		//tira via tutte le vie precedenti
		//disabilita da fare nella sottoclasse
		viaComboBox.getItems().clear();
		vie.clear();	
		try {
			//si connette al database e seleziona le vie disponibili nella tabella sede che hanno la via selezionata nel choicebox
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection connection = DriverManager.getConnection(url, usernameDB,passwordDB );
			PreparedStatement statement;
			
			statement = connection.prepareStatement("select Denominazione, Via from Sede, Servizi WHERE Sede.Regione = ? and Sede.Provincia = ? and Sede.Città = ?");
			statement.setString(1,regioneComboBox.getValue());
			statement.setString(2,provinciaComboBox.getValue());
			statement.setString(3,comuneComboBox.getValue());
			ResultSet resultSet = statement.executeQuery();
			//aggiunge le vie trovate nella tabella sede in un treeset in modo da non avere copioni
			while (resultSet.next())
				vie.add(resultSet.getString(1) + ", " + resultSet.getString(2));
			//aggiunge le vie al choicebox e abilita il choicobex via
			viaComboBox.getItems().addAll(vie);
			viaComboBox.setDisable(false);
		}catch(Exception e) {
			System.out.println(e);
		}
	}
	
	private void ricavaIdSede() {	
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection connection = DriverManager.getConnection(url, usernameDB,passwordDB );
			PreparedStatement statement = connection.prepareStatement("SELECT IdSede FROM Sede where Sede.Provincia = ? and Sede.Città = ? and Sede.Denominazione=? and Sede.Via = ?");
			
			String via = viaComboBox.getValue();
			int commaIndex = via.indexOf(",");
			String denominazione= via.substring(0,commaIndex);
	        if (commaIndex != -1)
	        	via = via.substring(commaIndex + 1).trim();
			
			System.out.println(provinciaComboBox.getValue());
			System.out.println(comuneComboBox.getValue());
			System.out.println(viaComboBox.getValue());
			
			statement.setString(1,provinciaComboBox.getValue());
			statement.setString(2,comuneComboBox.getValue());
			statement.setString(3, denominazione);
			statement.setString(4,via);
			ResultSet resultSet = statement.executeQuery();
			if(!resultSet.next()) {
				System.out.println("Errore");
			}else {
				idSede=resultSet.getInt(1);
			}
		}catch(Exception e) {
			System.out.println(e);
		}
	}
	/*Serve alle sottoclassi per prendersi i valori scelti*/
	protected ComboBox<String> getServizio(){
		return servizioComboBox;
	}
	protected ComboBox<String> getRegione(){
		return regioneComboBox;
	}
	protected ComboBox<String> getProvincia(){
		return provinciaComboBox;
	}
	protected ComboBox<String> getComune(){
		return comuneComboBox;
	}
	protected ComboBox<String> getVia(){
		return viaComboBox;
	}
	protected int getIdSede() {
		if(idSede==-1)
			ricavaIdSede();
		return idSede;
	}
	protected String getUrl() {
		return url;
	}
	protected String getUsernameDB() {
		return usernameDB;
	} 
	protected String getPasswordDB() {
		return passwordDB;
	}
}
