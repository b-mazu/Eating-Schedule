package com.mazurekb.controllers;

import java.io.IOException;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.mazurekb.main.HibernateUtil;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

public class ProfileController {

	private MainController mainController;
	private final String USER_FIELD_DEFINED = LoginController.username_input;
	private final String PASS_FIELD_DEFINED = LoginController.password_input;

	@FXML
	private TextField userTextField;
	@FXML
	private PasswordField passField;
	@FXML
	private TextField emailTextField;
	@FXML
	private Label substatus;
	@FXML
	private Button subButton;
	@FXML
	private TextField ageTextField;
	@FXML
	private TextField heightTextField;
	@FXML
	private TextField weightTextField;
	
	private static String email;
	private static Integer age;
	private static Integer height;
	private static Float weight;

	public TextField getEmailTextField() {
		return emailTextField;
	}

	public TextField getAgeTextField() {
		return ageTextField;
	}

	public TextField getHeightTextField() {
		return heightTextField;
	}

	public TextField getWeightTextField() {
		return weightTextField;
	}

	public TextField getUserTextField() {
		return userTextField;
	}

	public PasswordField getPassField() {
		return passField;
	}

	public void setMainController(MainController mainController) {
		this.mainController = mainController;
		setTextFields();
	}
	
	/*
	Method puts selected info about User into TextField as a preset
	*/
	public void setTextFields(){
		getProfileInfo();
		getUserTextField().setText(USER_FIELD_DEFINED);
		getPassField().setText(PASS_FIELD_DEFINED);
		getEmailTextField().setText(email);
		getAgeTextField().setText(age.toString());
		getHeightTextField().setText(height.toString());
		getWeightTextField().setText(weight.toString());
	}

	/*
	Method selects info from DB about logged User
	*/
	public void getProfileInfo(){		
		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		Query query = session.createNativeQuery("SELECT email FROM users WHERE username = '" + USER_FIELD_DEFINED + "'");		
		email = (String) query.getSingleResult();
		query = session.createNativeQuery("SELECT age FROM users WHERE username = '" + USER_FIELD_DEFINED + "'");		
		age = (Integer) query.getSingleResult();
		query = session.createNativeQuery("SELECT height FROM users WHERE username = '" + USER_FIELD_DEFINED + "'");		
		height = (Integer) query.getSingleResult();
		query = session.createNativeQuery("SELECT weight FROM users WHERE username = '" + USER_FIELD_DEFINED + "'");	
		weight = (Float) query.getSingleResult();				
		session.getTransaction().commit();
		HibernateUtil.shutdown();
	}
	
	/*
	Method Updates profile info into DB by given data in TextFields by user
	*/
	@FXML
	public void subChanges() {
		email = emailTextField.getText();
		isNumber();
		Session session = HibernateUtil.getSessionFactory().openSession();
		Query query = session
				.createNativeQuery("UPDATE Users SET email = '" + email + "', age = '" + age + "', height = '" + height
						+ "', weight = '" + weight + "' WHERE username = '" + USER_FIELD_DEFINED + "'");

		session.beginTransaction();
		query.executeUpdate();
		session.getTransaction().commit();
		HibernateUtil.shutdown();
		substatus.setText("Profile info changed!");
	}

	@FXML
	public void getBack() {
		FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/fxml/AppMenu.fxml"));
		Pane pane = null;
		try {
			pane = loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		AppMenuController appmenuController = loader.getController();
		appmenuController.setMainController(mainController);
		mainController.setScreen(pane);
	}
	
	/*
	Method checks if given by user data in TextFields are Integer/Float
	If yes, value of String is extracted to numbers
	If not, value of static variables are set to 0
	*/
	public void isNumber() {
		if (ageTextField.getText() == null || !isInteger(ageTextField.getText())) {
			age = 1;
		} else
			age = Integer.valueOf(ageTextField.getText());

		if (heightTextField.getText() == null || !isInteger(heightTextField.getText())) {
			height = 1;
		} else
			height = Integer.valueOf(heightTextField.getText());

		if (weightTextField.getText() == null || !isFloat(weightTextField.getText())) {
			weight = 1f;
		} else
			weight = Float.valueOf(weightTextField.getText());
	}
	
	/*
	Checks if string can be parsed to Float
	*/
	boolean isFloat(String str) {
		try {
			Float.parseFloat(str);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	/*
	Checks if string can be parsed to Integer
	*/
	boolean isInteger(String str) {
		try {
			Integer.parseInt(str);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

}
