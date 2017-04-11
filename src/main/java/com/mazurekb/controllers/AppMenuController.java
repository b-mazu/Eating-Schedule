package com.mazurekb.controllers;

import java.io.IOException;
import java.math.BigInteger;
import java.sql.Time;
import java.time.LocalDate;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.mazurekb.main.HibernateUtil;
import com.mazurekb.sql.Schedule;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.Pane;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class AppMenuController {

	private MainController mainController;

	@FXML
	private DatePickerController DatePickerController = new DatePickerController();
	private MenuScheduleController menuSchedule = new MenuScheduleController();
	@FXML
	private ProfileController profileController;
	

	public void setMainController(MainController mainController) {
		this.mainController = mainController;
		DatePickerController.calculateBMI();
		DatePickerController.calendar.setValue(LocalDate.now());
		DatePickerController.pickedDate = LocalDate.now();
		menuSchedule.getSchedule();
	}

	@FXML
	public void logout() {
		mainController.loadLoginScreen();
	}

	@FXML
	public void openProfile() {
		FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/fxml/Profile.fxml"));
		Pane pane = null;
		try {
			 pane = loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		ProfileController profileController = loader.getController();
		profileController.setMainController(mainController);
		mainController.setScreen(pane);		
	}

	@FXML
	public void openProductList() {
		FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/fxml/ProductList.fxml"));
		Pane pane = null;
		try {
			pane = loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		ProductListController productListController = loader.getController();
		productListController.setMainController(mainController);
		mainController.setScreen(pane);
	}

	@FXML
	public void openCreateMeal() {
		FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/fxml/CreateMeal.fxml"));
		Pane pane = null;
		try {
			pane = loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		CreateMealController createMealController = loader.getController();
		createMealController.setMainController(mainController);
		mainController.setScreen(pane);
	}

	@FXML
	public void openSchedule() {
		FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/fxml/Schedule.fxml"));
		Pane pane = null;
		try {
			pane = loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		ScheduleController scheduleController = loader.getController();
		scheduleController.setMainController(mainController);
		mainController.setScreen(pane);
	}

}
