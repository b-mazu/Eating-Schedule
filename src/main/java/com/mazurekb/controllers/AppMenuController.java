package com.mazurekb.controllers;

import java.io.IOException;
import java.time.LocalDate;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;


public class AppMenuController {

	private MainController mainController;

	@FXML
	private DatePickerController DatePickerController = new DatePickerController();
	@FXML
	private ProfileController profileController;
	

	public void setMainController(MainController mainController) {
		this.mainController = mainController;
		DatePickerController.calculateBMI();
		DatePickerController.calendar.setValue(LocalDate.now());
		DatePickerController.pickedDate = LocalDate.now();
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
