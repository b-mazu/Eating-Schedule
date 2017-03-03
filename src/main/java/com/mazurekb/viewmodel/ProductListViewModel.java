package com.mazurekb.viewmodel;

import javafx.beans.property.BooleanProperty;

import javafx.beans.property.SimpleBooleanProperty;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ProductListViewModel {

	private BooleanProperty disableUpdateButton = new SimpleBooleanProperty(false);
	private BooleanProperty disableAddButton = new SimpleBooleanProperty(false);
	private StringProperty productNameProperty = new SimpleStringProperty();
	
	public BooleanProperty getDisableAddButton() {
		return disableAddButton;
	}

	public void setDisableAddButton(BooleanProperty disableAddButton) {
		this.disableAddButton = disableAddButton;
	}


	public BooleanProperty getDisableUpdateButton() {
		return disableUpdateButton;
	}

	public void setDisableUpdateButton(BooleanProperty disableUpdateButton) {
		this.disableUpdateButton = disableUpdateButton;
	}

	public StringProperty getProductNameProperty() {
		return productNameProperty;
	}

	public void setProductNameProperty(StringProperty productNameProperty) {
		this.productNameProperty = productNameProperty;
	}

	/*
	IF productNameProperty is Empty it Disables Add/Update Button
	*/
	public ProductListViewModel() {
		disableAddButton.bind(productNameProperty.isEmpty());
		disableUpdateButton.bind(productNameProperty.isEmpty());
	}
}
