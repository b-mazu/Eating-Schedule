package com.mazurekb.controllers;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import com.mazurekb.main.HibernateUtil;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

public class LoginController {

	private MainController mainController;
	
	@FXML
	private TextField username;
	@FXML
	private PasswordField password;
	@FXML
	private Label loginStatus;
	private DatePickerController datePicker;

	public static String username_input;
	public static String password_input;

	public String getUsername_input() {
		return username_input;
	}

	public static String getPassword_input() {
		return password_input;
	}

	public void setMainController(MainController mainController) {
		
		this.mainController = mainController;
	}

	@FXML
	public void loginButton() {
		if (checkInput()) {
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
	}

	@FXML
	public void registerButton() {
		FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/fxml/Register.fxml"));
		Pane pane = null;
		try {
			pane = loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		RegisterController registerController = loader.getController();
		registerController.setMainController(mainController);
		mainController.setScreen(pane);
	}

	public boolean checkInput() {
		username_input = username.getText();
		password_input = password.getText();

		Session session = HibernateUtil.getSessionFactory().openSession();
		Query query = session.createQuery("Select 1 from Users WHERE username = '" + username_input
				+ "' AND password = '" + password_input + "'");
		if (query.uniqueResult() != null) {
			HibernateUtil.shutdown();
			return true;
		} else {
			loginStatus.setText("Wrong login or password!");
			HibernateUtil.shutdown();
			return false;
		}
	}
}
