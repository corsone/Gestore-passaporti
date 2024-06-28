package application;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;
import java.util.TreeSet;
import javafx.event.ActionEvent;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;

public class ControllerContattaci extends ControllerMenu implements Initializable{
	private TreeSet<String> regioni = new TreeSet<String>();
	private TreeSet<String> province = new TreeSet<String>();
	
	
	@FXML
	private BorderPane borderPane;
	@FXML
	private ChoiceBox<String> regioneChoiceBox;
	@FXML
	private ChoiceBox<String> provinciaChoiceBox;
	@FXML
	private Label email;
	@FXML
	private Label telefono;
	@FXML
	private Label sito;
	@FXML
	private ImageView myImage;
	
	private String provinciaDaCercare;
	
	private String emailTrovata;
	private String telefonoTrovato;
	private String sitoTrovato;
	
	private String url = "jdbc:mysql://localhost:3306/QuesturaDB";
	private String usernameDB = "root";
	private String passwordDB = "";
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		mainPane = borderPane;
		
		
		borderPane.widthProperty().addListener((observable, oldValue, newValue) -> {
        	myImage.setFitWidth(borderPane.getWidth() / 3);
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
		
		riempiRegioni();
		
		regioneChoiceBox.setOnAction(this::riempiProvince);
		provinciaChoiceBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
				provinciaDaCercare=provinciaChoiceBox.getSelectionModel().getSelectedItem();
				riempiCampi();
				System.out.println("Pippo");
				System.out.println(emailTrovata);
				System.out.println(telefonoTrovato);
				System.out.println(sitoTrovato);
				email.setText(emailTrovata);
				telefono.setText(telefonoTrovato);
				sito.setText(sitoTrovato);
			}
		});
	}
	
	public void passaDati(double width, double height) {
		borderPane.setPrefWidth(width);
		borderPane.setPrefHeight(height);
		
		borderPane.setMinWidth(1240);
		borderPane.setMinHeight(720);
	}
	
	private void riempiRegioni() {
		regioni.clear();
		regioneChoiceBox.getItems().clear();
		provinciaChoiceBox.setDisable(true);
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection connection = DriverManager.getConnection(url, usernameDB,passwordDB );
			PreparedStatement statement = connection.prepareStatement("select Regione from Sede");
			ResultSet resultSet = statement.executeQuery();
			//aggiunge le città trovate nella tabella sede in un treeset in modo da non avere copioni
			while (resultSet.next())
				regioni.add(resultSet.getString(1));
			
			regioneChoiceBox.getItems().addAll(regioni);
		}catch(Exception e){
			System.out.println(e);
		}
		
	}
	
	private void riempiProvince(ActionEvent event) {	
		province.clear();
		provinciaChoiceBox.getItems().clear();
		
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection connection = DriverManager.getConnection(url, usernameDB,passwordDB );
			PreparedStatement statement = connection.prepareStatement("select Provincia from Sede where Regione=?");
			statement.setString(1, regioneChoiceBox.getValue());
			ResultSet resultSet = statement.executeQuery();
			//aggiunge le città trovate nella tabella sede in un treeset in modo da non avere copioni
			while (resultSet.next())
				province.add(resultSet.getString(1));
			//aggiunge le città al choicebox e abilita il choicebox città
			provinciaChoiceBox.getItems().addAll(province);
			provinciaChoiceBox.setDisable(false);
		}catch(Exception e) {
			System.out.println(e);
		}
	}
	
	private void riempiCampi() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection connection = DriverManager.getConnection(url,usernameDB,passwordDB);
			PreparedStatement statement= connection.prepareStatement("select Email,Telefono,Sito from Sede where Provincia=?");
			statement.setString(1,provinciaDaCercare);
			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				System.out.println(resultSet.getString(1));
				System.out.println(resultSet.getString(2));
				System.out.println(resultSet.getString(3));
				emailTrovata=resultSet.getString(1);
				telefonoTrovato=resultSet.getString(2);
				sitoTrovato=resultSet.getString(3);
			}
		}catch(Exception e) {
			System.out.println(e);
		}
	}
	
}