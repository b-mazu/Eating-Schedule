package com.mazurekb.controllers;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.query.Query;

import com.mazurekb.main.HibernateUtil;
import com.mazurekb.viewmodel.ProductListViewModel;
import com.mazurekb.viewmodel.RegisterViewModel;

import javafx.beans.binding.NumberBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.ComboBoxListCell;
import javafx.scene.layout.Pane;

public class ProductListController {

	private MainController mainController;

	@FXML
	private ListView listView;
	@FXML
	private ChoiceBox productChoiceBox1;
	@FXML
	private ChoiceBox productChoiceBox2;
	@FXML
	private TextField productNameField;
	@FXML
	private TextField kcalField;
	@FXML
	private TextField proteinField;
	@FXML
	private TextField fatField;
	@FXML
	private TextField carboField;
	@FXML
	private Button updateButton;
	@FXML
	private Button addButton;
	@FXML
	private Label info;

	private static Integer kcal;
	private static Float protein;
	private static Float fat;
	private static Float carbo;

	private static String table;
	private static String table2;
	private static String selected_product;

	private ProductListViewModel productListViewModel = new ProductListViewModel();

	public void setMainController(MainController mainController) {
		this.mainController = mainController;
		choiceBox();
		propertyBinding();
		info.setText("");
	}

	/*
	UpdateButton is disabled if productNameField is not filled
	*/
	public void propertyBinding() {
		productNameField.textProperty().bindBidirectional(productListViewModel.getProductNameProperty());
		updateButton.disableProperty().bind(productListViewModel.getDisableUpdateButton());
		addButton.disableProperty().bind(productListViewModel.getDisableAddButton());
	}
	/*
	Method inserts new product to database, 
	before that it checks if product of given 
	name doesn't exist already in database
	*/
	@FXML
	private void addButton() {
		isNumber();
		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		Query query = session
				.createQuery("Select 1 from " + table2 + " WHERE productname = '" + productNameField.getText() + "'");
		if (query.uniqueResult() != null) {
			info.setText("That product name is already used!");
			HibernateUtil.shutdown();
		} else {
			query = session.createNativeQuery("INSERT INTO " + table2
					+ " (productname, kcal, fat, protein, carbohydrates) VALUES('" + productNameField.getText() + "','"
					+ kcal + "','" + fat + "','" + protein + "','" + carbo + "')");
			query.executeUpdate();

			session.getTransaction().commit();
			HibernateUtil.shutdown();
			info.setText("Product added!");
			fillListView();
		}
	}

	/*
	Method updates selected product in database by given data in TextFields
	*/
	@FXML
	private void updateButton() {
		isNumber();
		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();

		Query query = session.createNativeQuery("UPDATE " + table2 + " SET productname = '" + productNameField.getText()
				+ "', kcal = '" + kcal + "',fat = '" + fat + "',carbohydrates = '" + carbo + "',protein = '" + protein
				+ "' WHERE  productname = '" + selected_product + "'");
		query.executeUpdate();

		session.getTransaction().commit();
		HibernateUtil.shutdown();
		info.setText("Product Updated!");
	}

	/*
	Method deletes selected product from database
	*/
	@FXML
	private void deleteButton() {
		try {
			Session session = HibernateUtil.getSessionFactory().openSession();
			session.beginTransaction();
			Query query = session
					.createNativeQuery("DELETE FROM " + table + " WHERE productname = '" + selected_product + "'");
			query.executeUpdate();
			session.getTransaction().commit();
			HibernateUtil.shutdown();
			fillListView();
			info.setText("Product Deleted!");
		} catch (HibernateException e) {
			
			e.printStackTrace();
		}
	}

	/*
	Option to return to previous window
	*/
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

	/*
	Method checks if given by user data in TextFields are Integer/Float
	If yes, value of String is extracted to numbers
	If not, value of static variables are set to 0
	*/
	public void isNumber() {
		if (kcalField.getText() == null || !isInteger(kcalField.getText())) {
			kcal = 0;
		} else
			kcal = Integer.valueOf(kcalField.getText());

		if (fatField.getText() == null || !isFloat(fatField.getText())) {
			fat = 0f;
		} else
			fat = Float.valueOf(fatField.getText());

		if (carboField.getText() == null || !isFloat(carboField.getText())) {
			carbo = 0f;
		} else
			carbo = Float.valueOf(carboField.getText());
		if (proteinField.getText() == null || !isFloat(proteinField.getText())) {
			protein = 0f;
		} else
			protein = Float.valueOf(proteinField.getText());
	}

	/*
	Method Selects all product names from selected table in ChoiceBox1
	*/
	public List selectProductList() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Query query = session.createNativeQuery("SELECT productname FROM " + table);
		List<String> productList = query.getResultList();
		HibernateUtil.shutdown();

		return productList;
	}

	/*
	Method sets options in ChoiceBox 1 and ChoiceBox 2
	Listeners checks if selected position from List was changed
	If yes, picked name of table is set as reference
	*/
	private void choiceBox() {
		ObservableList<String> checkbox_list = FXCollections.observableArrayList("Bakery", "Beverage", "Diary", "Meat",
				"Snack", "Other");
		final String[] products_strings = new String[] { "BakeryProducts", "BeverageProducts", "DiaryProducts",
				"MeatProducts", "SnackProducts", "OtherProducts" };

		productChoiceBox1.setItems(checkbox_list);
		productChoiceBox2.setItems(checkbox_list);

		productChoiceBox1.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue ov, Number value, Number new_value) {
				table = products_strings[new_value.intValue()];
				productChoiceBox2.getSelectionModel().select(new_value.intValue());
				fillListView();
			}
		});

		productChoiceBox2.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue ov, Number value, Number new_value) {
				table2 = products_strings[new_value.intValue()];
			}
		});
	}

	/*
	Method fills ListView with product names from selected table name in ChoiceBox 1
	*/
	private void fillListView() {
		if (table != null) {
			ObservableList<String> items = FXCollections.observableArrayList();
			items.addAll(selectProductList());
			listView.setItems(items);
			System.out.println(listView.getItems());
			pickProduct();
		}
	}

	/*
	Method fills TextField with data from database of picked product name from ListView
	*/
	public void fillProductParameters() {
		if (selected_product != null) {
			Session session = HibernateUtil.getSessionFactory().openSession();

			Query query = session.createNativeQuery(
					"SELECT productname FROM " + table + " WHERE productname = '" + selected_product + "'");
			productNameField.setText((String) query.getSingleResult());
			query = session
					.createNativeQuery("SELECT kcal FROM " + table + " WHERE productname = '" + selected_product + "'");
			kcalField.setText(query.getSingleResult().toString());
			query = session.createNativeQuery(
					"SELECT protein FROM " + table + " WHERE productname = '" + selected_product + "'");
			proteinField.setText(query.getSingleResult().toString());
			query = session
					.createNativeQuery("SELECT fat FROM " + table + " WHERE productname = '" + selected_product + "'");
			fatField.setText(query.getSingleResult().toString());
			query = session.createNativeQuery(
					"SELECT carbohydrates FROM " + table + " WHERE productname = '" + selected_product + "'");
			carboField.setText(query.getSingleResult().toString());

			HibernateUtil.shutdown();
		}
	}

	/*
	Listener sets name of selected Product from ListView to static variable
	*/
	public void pickProduct() {
		listView.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue ov, Number value, Number new_value) {
				selected_product = (String) listView.getSelectionModel().getSelectedItem();
				fillProductParameters();
			}
		});
	}

}
