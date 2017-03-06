package com.mazurekb.controllers;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.dialect.identity.SybaseAnywhereIdentityColumnSupport;
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
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.ComboBoxListCell;
import javafx.scene.layout.Pane;
import javafx.scene.control.TreeView;

public class CreateMealController {

	private MainController mainController;

	@FXML
	private ListView listView;
	@FXML
	private TreeView TreeView;
	@FXML
	private ChoiceBox productChoiceBox1;
	@FXML
	private Label productNameField;
	@FXML
	private Label kcalField;
	@FXML
	private Label proteinField;
	@FXML
	private Label fatField;
	@FXML
	private Label carboField;
	@FXML
	private Button updateButton;
	@FXML
	private Button addButton;
	@FXML
	private Label info;
	@FXML
	private TextField mealName;

	private static Integer kcal;
	private static Float protein;
	private static Float fat;
	private static Float carbo;

	private static String productType;
	private static String productType2;
	private static String selected_product;
	private static String last_selected;
	private static Number idx;

	private static TreeItem<String> rootItem = new TreeItem<String>();
	private static List<String> ifSelected = new ArrayList<String>();

	// private ProductListViewModel productListViewModel = new
	// ProductListViewModel();

	public void setMainController(MainController mainController) {
		this.mainController = mainController;
		choiceBox();
		fillTreeView();
		info.setText("");
	}

	/*
	 * UpdateButton is disabled if productNameField is not filled
	 *
	 * public void propertyBinding() {
	 * productNameField.textProperty().bindBidirectional(productListViewModel.
	 * getProductNameProperty());
	 * updateButton.disableProperty().bind(productListViewModel.
	 * getDisableUpdateButton());
	 * addButton.disableProperty().bind(productListViewModel.getDisableAddButton
	 * ()); }
	 */

	/*
	 * Method inserts new product to database, before that it checks if product
	 * of given name doesn't exist already in database
	 */
	private void clear() {
		rootItem.getChildren().clear();
	}

	private List getMealList() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Query query = session.createNativeQuery("SELECT name FROM Meals WHERE user_id = '" + selectUserID() + "'");
		List<String> mealList = query.getResultList();
		
		for(String e:mealList){
			if(!ifSelected.contains(e)) ifSelected.add(e);
		}
		
		HibernateUtil.shutdown();
		return mealList;
	}

	private void fillTreeView() {
		if (!rootItem.getChildren().isEmpty())
			rootItem.getChildren().clear();
		List<String> meals = getMealList();
		for (String e : meals) {
			rootItem.getChildren().add(getMealProducts(e));
		}
		TreeView.setShowRoot(false);

		TreeView.setRoot(rootItem);
	}

	private String converter(String select, String from, String where, String where_text) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Query query = session.createNativeQuery(
				"SELECT " + select + " FROM " + from + " WHERE " + where + " = '" + where_text + "'");
		String result = query.getSingleResult().toString();
		System.out.println(result);
		HibernateUtil.shutdown();
		return result;
	}

	private TreeItem<String> getMealProducts(String meal) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		TreeItem<String> mealItem = new TreeItem<String>(meal);
		rootItem.setExpanded(true);
		List<String> mealList = new ArrayList<String>();

		Query query = session.createNativeQuery("SELECT product_id FROM meal_products WHERE meal_id = '"
				+ converter("id", "meals", "name", meal) + "'");
		List<BigInteger> products_id = query.getResultList();

		for (BigInteger e : products_id) {
			query = session.createNativeQuery("SELECT productName FROM products WHERE id = '" + e + "'");
			mealList.add(query.getSingleResult().toString());
		}

		for (String e : mealList) {
			TreeItem<String> item = new TreeItem<String>(e);
			mealItem.getChildren().add(item);
		}

		if (meal.equals(last_selected)) {
			mealItem.setExpanded(true);
		}

		return mealItem;

	}

	@FXML
	public void addProduct() {
		last_selected = getSelectedMealValue();

		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		Query query = session.createNativeQuery("INSERT INTO meal_products (meal_id,product_id) VALUES('"
				+ converter("id", "meals", "name", last_selected) + "','"
				+ converter("id", "products", "productname", selected_product) + "')");
		query.executeUpdate();
		session.getTransaction().commit();
		HibernateUtil.shutdown();
		fillTreeView();
	}

	@FXML
	public void deleteProduct() {
		String lastSelectedProduct = getSelectedMealValue();

		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		Query query = session.createNativeQuery("DELETE FROM meal_products WHERE product_id ="
				+ converter("id", "products", "productname", lastSelectedProduct) + " AND meal_id =" + converter("id", "meals", "name", last_selected));
		query.executeUpdate();
		session.getTransaction().commit();
		HibernateUtil.shutdown();
		fillTreeView();
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
	private void addButton() {
		isNumber();
		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		Query query = session.createQuery(
				"Select 1 from Meals WHERE name = '" + mealName.getText() + "' AND user_id = '" + selectUserID() + "'");
		if (query.uniqueResult() != null) {
			info.setText("That product name is already used!");
			HibernateUtil.shutdown();
		} else {
			query = session
					.createNativeQuery("INSERT INTO Meals (name, user_id, kcal, fat, protein, carbohydrates) VALUES('"
							+ mealName.getText() + "'," + selectUserID() + "," + 0 + "," + 0 + "," + 0 + "," + 0 + ")");
			query.executeUpdate();
			session.getTransaction().commit();
			HibernateUtil.shutdown();
			info.setText("Product added!");
			fillTreeView();
		}
	}

	private String getSelectedMealValue() {
		String item = TreeView.getSelectionModel().getSelectedItem().toString();
		String substring = item.substring(18, item.length() - 2);
		return substring;
	}

	/*
	 * Method updates selected product in database by given data in TextFields
	 */
	@FXML
	private void updateButton() {
		
		TreeView.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue ov, Number value, Number new_value) {
				
				idx = new_value;
				System.out.println(idx);
			}
		});
	}

	/*
	 * Method deletes selected product from database
	 */
	@FXML
	private void deleteButton() {
		try {
			Session session = HibernateUtil.getSessionFactory().openSession();
			session.beginTransaction();
			Query query = session
					.createNativeQuery("DELETE FROM Products WHERE productname = '" + selected_product + "'");
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
	 * Option to return to previous window
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
	 * Checks if string can be parsed to Float
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
	 * Checks if string can be parsed to Integer
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
	 * Method checks if given by user data in TextFields are Integer/Float If
	 * yes, value of String is extracted to numbers If not, value of static
	 * variables are set to 0
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
	 * Method Selects all product names from selected productType of products in
	 * ChoiceBox1
	 */
	public List selectProductList() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Query query = session
				.createNativeQuery("SELECT productname FROM Products WHERE productType = '" + productType + "'");
		List<String> productList = query.getResultList();
		HibernateUtil.shutdown();

		return productList;
	}

	/*
	 * Method sets options in ChoiceBox 1 and ChoiceBox 2 Listeners checks if
	 * selected position from List was changed If yes, picked name of
	 * productType is set as reference
	 */
	private void choiceBox() {
		ObservableList<String> checkbox_list = FXCollections.observableArrayList("Bakery", "Beverage", "Diary", "Meat",
				"Snack", "Other");
		final String[] products_strings = new String[] { "Bakery", "Beverage", "Diary", "Meat", "Snack", "Other" };

		productChoiceBox1.setItems(checkbox_list);

		productChoiceBox1.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue ov, Number value, Number new_value) {
				productType = products_strings[new_value.intValue()];
				fillListView();
			}
		});

	}

	/*
	 * Method fills ListView with product names from selected table name in
	 * ChoiceBox 1
	 */
	private void fillListView() {
		if (productType != null) {
			ObservableList<String> items = FXCollections.observableArrayList();
			items.addAll(selectProductList());
			listView.setItems(items);
			System.out.println(listView.getItems());
			pickProduct();
		}
	}

	/*
	 * Method fills TextField with data from database of picked product name
	 * from ListView
	 */
	public void fillProductParameters() {
		if (selected_product != null) {
			Session session = HibernateUtil.getSessionFactory().openSession();

			Query query = session.createNativeQuery(
					"SELECT productname FROM Products WHERE productname = '" + selected_product + "'");
			productNameField.setText((String) query.getSingleResult());
			query = session
					.createNativeQuery("SELECT kcal FROM Products WHERE productname = '" + selected_product + "'");
			kcalField.setText(query.getSingleResult().toString());
			query = session
					.createNativeQuery("SELECT protein FROM Products WHERE productname = '" + selected_product + "'");
			proteinField.setText(query.getSingleResult().toString());
			query = session
					.createNativeQuery("SELECT fat FROM Products WHERE productname = '" + selected_product + "'");
			fatField.setText(query.getSingleResult().toString());
			query = session.createNativeQuery(
					"SELECT carbohydrates FROM Products WHERE productname = '" + selected_product + "'");
			carboField.setText(query.getSingleResult().toString());

			HibernateUtil.shutdown();
		}
	}

	/*
	 * Listener sets name of selected Product from ListView to static variable
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
