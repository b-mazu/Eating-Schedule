package com.mazurekb.controllers;

import java.time.LocalDate;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.mazurekb.main.HibernateUtil;
import com.mazurekb.sql.Schedule;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;

import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;

public class DatePickerController {

	@FXML
	public DatePicker calendar;
	@FXML
	private Label name;
	@FXML
	private Label bmi;
	public MenuScheduleController menuSchedule = new MenuScheduleController();
	public static LocalDate pickedDate;
	public Schedule schedule;

	@FXML
	public void button(){
		menuSchedule.getSchedule();
	}
	@FXML
	public void getDate() {
		pickedDate = calendar.getValue();
		menuSchedule.getSchedule();
		System.out.println(menuSchedule.data);
		menuSchedule.ref();
	}

	public void calculateBMI() {

		Float weight;
		Integer height;

		Session session = HibernateUtil.getSessionFactory().openSession();

		Query query = session.createNativeQuery(
				"SELECT weight FROM Users WHERE username = '" + LoginController.username_input + "'");
		weight = (Float) query.getSingleResult();

		query = session.createNativeQuery(
				"SELECT height FROM Users WHERE username = '" + LoginController.username_input + "'");
		height = (Integer) query.getSingleResult();

		HibernateUtil.shutdown();

		// round up to 2 decimal places
		float bmi_f = (weight / (((float) height / 100) * 2)) * (10 ^ 2);
		bmi_f = Math.round(bmi_f);
		bmi_f = (bmi_f / (10 ^ 2));

		name.setText(LoginController.username_input + "!");
		bmi.setText(Float.toString(bmi_f));

	}
}
