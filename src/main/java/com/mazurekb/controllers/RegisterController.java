package com.mazurekb.controllers;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import com.mazurekb.main.HibernateUtil;
import com.mazurekb.sql.Users;
import com.mazurekb.viewmodel.RegisterViewModel;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RegisterController {

	private MainController mainController;

	@FXML
	private TextField userTextField;
	@FXML
	private PasswordField passField;
	@FXML
	private TextField emailTextField;
	@FXML
	private Label regstatus;
	@FXML
	private Button regButton;
	@FXML
	private TextField ageTextField;
	@FXML
	private TextField heightTextField;
	@FXML
	private TextField weightTextField;
	
	private static Integer age;
	private static Integer height;
	private static Float weight;
	
	private RegisterViewModel regViewModel = new RegisterViewModel();

	/*
	Method binds User/Pass Field properties with Register Button to prevent submitting empty User/Pass field by disabling Button itself
	*/
	public void propertyBinding() {
		userTextField.textProperty().bindBidirectional(regViewModel.getUserProperty());
		passField.textProperty().bindBidirectional(regViewModel.getPassProperty());
		regButton.disableProperty().bind(regViewModel.getDisableRegisterButton());
	}

	/*
	If user name is unique: Method register provided data from User and pushes it to DB	
	*/
	@FXML
	public void registerUser() {
		isNumber();
		Users user = new Users();
		user.setUsername(userTextField.getText());
		user.setPassword(passField.getText());
		user.setEmail(emailTextField.getText());
		user.setAge(age);
		user.setHeight(height);
		user.setWeight(weight);

		Session session = HibernateUtil.getSessionFactory().openSession();
		Query query = session.createQuery("Select 1 from Users WHERE username = '" + user.getUsername() + "'");
		if (query.uniqueResult() != null) {
			regstatus.setText("Username is taken");
			HibernateUtil.shutdown();
		} else {
			session.beginTransaction();
			session.save(user);
			session.getTransaction().commit();
			HibernateUtil.shutdown();
			regstatus.setText("Registration Complete!");
		}
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

	public void setMainController(MainController mainController) {
		this.mainController = mainController;
		propertyBinding();
	}

	@FXML
	public void getBack() {
		mainController.loadLoginScreen();
	}
}
