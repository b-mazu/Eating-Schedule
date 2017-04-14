package com.mazurekb.controllers;

import java.io.IOException;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.mazurekb.main.HibernateUtil;
import com.mazurekb.sql.Schedule;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;

public class ScheduleController {

	private MainController mainController;

	@FXML
	private TreeView TreeView;
	@FXML
	private ChoiceBox productChoiceBox1;
	@FXML
	private DatePicker datePicker;
	@FXML
	private TextField hour;
	@FXML
	private TextField min;
	@FXML
	private Label info;
	@FXML
	private TableView TableView;
	@FXML
	private TableColumn<Schedule, Time> timeTable;
	@FXML
	private TableColumn<Schedule, String> mealsTable;

	private LocalDate pickedDate;

	private static String selected_product;
	private static String last_selected;
	private static BigInteger selectedSchedule;

	private static String productType;

	private static TreeItem<String> rootItem = new TreeItem<String>();
	private static List<String> ifSelected = new ArrayList<String>();
	
	private ObservableList data = FXCollections.observableArrayList();

	public void setMainController(MainController mainController) {
		this.mainController = mainController;
		observableMealValue();
		getSelectedSchedule();
		choiceBox();
		fillTreeView();
	}


	private void clear() {
		rootItem.getChildren().clear();
	}

	/*
	 * Method selects all products/meals from database, and puts them in List
	 * It also checks if given List doesn't contains some products/meals to avoid duplicated list
	 */
	private List getMealList() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		if (productType == "Meals") {
			Query query = session.createNativeQuery("SELECT name FROM Meals WHERE user_id = '" + selectUserID() + "'");
			List<String> mealList = query.getResultList();

			for (String e : mealList) {
				if (!ifSelected.contains(e))
					ifSelected.add(e);
			}

			HibernateUtil.shutdown();
			return mealList;
		} else {
			Query query = session
					.createNativeQuery("SELECT productname FROM Products WHERE productType = '" + productType + "'");
			List<String> mealList = query.getResultList();

			for (String e : mealList) {
				if (!ifSelected.contains(e))
					ifSelected.add(e);
			}

			HibernateUtil.shutdown();
			return mealList;
		}

	}

	/*
	 * Method fills TreeView with Meals/Products List
	 */
	private void fillTreeView() {

		if (!rootItem.getChildren().isEmpty())
			rootItem.getChildren().clear();
		List<String> meals = getMealList();
		if (productType == "Meals") {
			for (String e : meals) {
				rootItem.getChildren().add(getMealProducts(e));
			}
		} else {

			for (String e : meals) {
				TreeItem<String> mealItem = new TreeItem<String>(e);
				rootItem.getChildren().add(mealItem);
			}
		}

		TreeView.setShowRoot(false);

		TreeView.setRoot(rootItem);
	}

	/*
	 * Method returns values needed to be inserted into DB at addProduct method
	 */
	private String converter(String select, String from, String where, String where_text) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Query query = session.createNativeQuery(
				"SELECT " + select + " FROM " + from + " WHERE " + where + " = '" + where_text + "'");
		String result = query.getSingleResult().toString();
		HibernateUtil.shutdown();
		return result;
	}

	/*
	 * Method gets from DB products that goes into each specific Meal and fills RootItems(meals) with them
	 */
	private TreeItem<String> getMealProducts(String meal) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		TreeItem<String> mealItem = new TreeItem<String>(meal);
		rootItem.setExpanded(true);
		List<String> mealList = new ArrayList<String>();

		Query query = session.createNativeQuery(
				"SELECT product_id FROM meal_products WHERE meal_id = " + converter("id", "meals", "name", meal));

		if (!query.getResultList().isEmpty()) {

			List<BigInteger> products_id = query.getResultList();

			for (BigInteger e : products_id) {
				query = session.createNativeQuery("SELECT productName FROM products WHERE id = " + e);
				mealList.add(query.getSingleResult().toString());
			}

			for (String e : mealList) {
				TreeItem<String> item = new TreeItem<String>(e);
				mealItem.getChildren().add(item);
			}

			if (meal.equals(last_selected)) {
				mealItem.setExpanded(true);
			}

		}

		return mealItem;

	}

	/*
	 * Method returns parent of selected TreeItem(in this situation Meal name from picked product)
	 */
	private String getParent() {
		if (!TreeView.getSelectionModel().isEmpty()) {
			TreeItem<String> item = (TreeItem<String>) TreeView.getSelectionModel().getSelectedItem();
			String parent = item.getParent().toString().substring(18, item.getParent().toString().length() - 2);
			return parent;
		} else
			return null;

	}

	/*
	 * Method returns logged UserID from DB
	 */
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

	/*
	 * Method returns string of selected Meal from TreeView
	 */
	private String getSelectedMealValue() {
		if (!TreeView.getSelectionModel().isEmpty()) {
			String item = TreeView.getSelectionModel().getSelectedItem().toString();
			String substring = item.substring(18, item.length() - 2);
			return substring;
		} else
			return null;
	}

	/*
	 * Option to return to previous window
	 */
	@FXML
	private void getBack() {
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
	 * Method sets options in ChoiceBox 1 and ChoiceBox 2 Listeners checks if
	 * selected position from List was changed If yes, picked name of
	 * productType is set as reference
	 */
	private void choiceBox() {
		ObservableList<String> checkbox_list = FXCollections.observableArrayList("Meals", "Bakery", "Beverage", "Diary",
				"Meat", "Snack", "Other");
		final String[] products_strings = new String[] { "Meals", "Bakery", "Beverage", "Diary", "Meat", "Snack",
				"Other" };

		productChoiceBox1.setItems(checkbox_list);

		productChoiceBox1.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue ov, Number value, Number new_value) {
				productType = products_strings[new_value.intValue()];
				TreeView.getSelectionModel().clearSelection();
				fillTreeView();
			}
		});

	}

	@FXML
	private void getDate() {
		pickedDate = datePicker.getValue();
		getSchedule();
	}

	private int getHour() {
		return Integer.parseInt(hour.getText());
	}

	private int getMinute() {
		return Integer.parseInt(min.getText());
	}

	
	/*
	 * Method responsible for adding Meals to Schedule
	 * It also checks If time/date/meal value is proper
	 */
	@FXML
	private void addButton() {
		if (selected_product != null && getSelectedMealValue() != null && !hour.getText().isEmpty()
				&& !min.getText().isEmpty() && datePicker.getValue() != null && getHour() >= 0 && getHour() <= 23 && getMinute() >= 0 && getMinute() <= 59) {

			if (!getParent().equals("null"))
				last_selected = getParent();
			else
				last_selected = getSelectedMealValue();

			Session session = HibernateUtil.getSessionFactory().openSession();
			session.beginTransaction();

			Query query = session.createNativeQuery(
					"INSERT INTO schedule(date, productName, productid, producttype, time, userid) VALUES ('"
							+ pickedDate + "', '" + selected_product + "', " + getProductId() + ", '" + productType
							+ "', '" + getHour() + ":" + getMinute() + "', " + selectUserID() + ")");

			query.executeUpdate();
			session.getTransaction().commit();
			HibernateUtil.shutdown();
			getSchedule();

		} else
			info.setText("No product or meal was picked!");
	}

	/*
	 * Method observes if selected value in TreeView of products/meals changes
	 */
	private void observableMealValue() {
		TreeView.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue ov, Number value, Number new_value) {
				if (getSelectedMealValue() != null && getParent().equals("null")) {
					selected_product = getSelectedMealValue();
				}
			}
		});
	}

	/*
	 * Method gets from DB Product/Meal ID
	 */
	private BigInteger getProductId() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		if (productType == "Meals") {
			Query query = session.createNativeQuery("SELECT id FROM Meals WHERE name = '" + selected_product + "'");
			BigInteger id = (BigInteger) query.getSingleResult();
			HibernateUtil.shutdown();
			return id;
		} else {
			Query query = session.createNativeQuery("SELECT id FROM Products WHERE productName = '" + selected_product
					+ "' AND productType = '" + productType + "'");
			BigInteger id = (BigInteger) query.getSingleResult();
			HibernateUtil.shutdown();
			return id;
		}
	}

	
	/*
	 * Method checks if value in DatePicker is correct, If yes:
	 * It sets TableColumns and puts in them data (time of meal and name of it), that was collected from DB
	 */
	private void getSchedule() {
		if (datePicker.getValue() != null) {
			data.clear();
			timeTable.setCellValueFactory(new PropertyValueFactory<Schedule, Time>("time"));
			mealsTable.setCellValueFactory(new PropertyValueFactory<Schedule, String>("productName"));

			Session session = HibernateUtil.getSessionFactory().openSession();
			Query query = session.createNativeQuery(
					"SELECT id FROM Schedule WHERE userId = '" + selectUserID() + "' AND date = '" + pickedDate + "'");
			List<BigInteger> IdList = query.getResultList();
			query = session.createNativeQuery("SELECT time FROM Schedule WHERE userId = '" + selectUserID()
					+ "' AND date = '" + pickedDate + "'");
			List<Time> TimeList = query.getResultList();
			query = session.createNativeQuery("SELECT productName FROM Schedule WHERE userId = '" + selectUserID()
					+ "' AND date = '" + pickedDate + "'");
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
			
			SortedList<Schedule> sortedData = new SortedList<Schedule>(data);
			sortedData.comparatorProperty().bind(TableView.comparatorProperty());
			TableView.setItems(sortedData);
			TableView.getSortOrder().addAll(timeTable);
			

			HibernateUtil.shutdown();
		}
	}

	
	/*
	 * Method deletes selected Meal from Schedule List
	 */
	@FXML
	private void deleteButton() {
		if (TableView.getSelectionModel().isEmpty()) {
			info.setText("Pick Meal to remove it from Meal list!");
		} else {

			Session session = HibernateUtil.getSessionFactory().openSession();
			session.beginTransaction();
			Query query = session.createNativeQuery("DELETE FROM Schedule WHERE id = '" + selectedSchedule + "'");
			query.executeUpdate();
			session.getTransaction().commit();
			HibernateUtil.shutdown();
			getSchedule();
		}
	}
	
	/*
	 * Observable list of selection in Schedule TableView
	 */
	private void getSelectedSchedule() {
		TableView.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue ov, Number value, Number new_value) {
				if (getSelectedMealSchedule() != null) {
					selectedSchedule = getSelectedMealSchedule();
				}
			}
		});
	}

	/*
	 * Method returns ID of selected item from Schedule TableView
	 */
	private BigInteger getSelectedMealSchedule() {
		if (!TableView.getSelectionModel().isEmpty()) {
			Schedule item = (Schedule) TableView.getSelectionModel().getSelectedItem();
			return item.getId();
		} else
			return (BigInteger) null;
	}
}
