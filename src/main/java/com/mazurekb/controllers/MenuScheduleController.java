package com.mazurekb.controllers;

import java.math.BigInteger;
import java.sql.Time;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.mazurekb.main.HibernateUtil;
import com.mazurekb.sql.Schedule;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class MenuScheduleController {

	@FXML
	private TableView<Schedule> TableView = new TableView<Schedule>();
	@FXML
	private TableColumn<Schedule, Time> timeTable = new TableColumn<Schedule, Time>("time");
	@FXML
	private TableColumn<Schedule, String> mealsTable = new TableColumn<Schedule, String>("productName");

	
	private ObservableList<Schedule> data = FXCollections.observableArrayList();

	private void getSchedule() {	
		if (DatePickerController.pickedDate != null) {
			data.clear();		
			timeTable.setCellValueFactory(new PropertyValueFactory<Schedule, Time>("time"));
			mealsTable.setCellValueFactory(new PropertyValueFactory<Schedule, String>("productName"));
			
			Session session = HibernateUtil.getSessionFactory().openSession();
			Query query = session.createNativeQuery(
					"SELECT id FROM Schedule WHERE userId = '" + selectUserID() + "' AND date = '" + DatePickerController.pickedDate + "'");
			List<BigInteger> IdList = query.getResultList();
			query = session.createNativeQuery("SELECT time FROM Schedule WHERE userId = '" + selectUserID()
					+ "' AND date = '" + DatePickerController.pickedDate + "'");
			List<Time> TimeList = query.getResultList();
			query = session.createNativeQuery("SELECT productName FROM Schedule WHERE userId = '" + selectUserID()
					+ "' AND date = '" + DatePickerController.pickedDate + "'");
			List<String> MealList = query.getResultList();

			BigInteger[] IdListArray = new BigInteger[IdList.size()];
			Time[] TimeListArray = new Time[TimeList.size()];
			String[] MealListArray = new String[MealList.size()];

			IdList.toArray(IdListArray);
			TimeList.toArray(TimeListArray);
			MealList.toArray(MealListArray);

			for (int i = 0; i < TimeList.size(); i++) {
				data.add(new Schedule(IdListArray[i], TimeListArray[i], MealListArray[i]));
			}
			TableView.setItems(data);
			
			HibernateUtil.shutdown();
		}
	}

	private long selectUserID() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		Query query = session
				.createQuery("Select id from Users WHERE username = '" + LoginController.username_input + "'");
		long id = (Long) query.getSingleResult();
		session.getTransaction().commit();
		HibernateUtil.shutdown();
		return id;
	}
	
	@FXML
	private void showMealsButton(){
		getSchedule();
	}
	
}
