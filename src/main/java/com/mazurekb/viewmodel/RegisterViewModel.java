package com.mazurekb.viewmodel;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class RegisterViewModel {
	
	private BooleanProperty disableRegisterButton = new SimpleBooleanProperty(false);	
	private StringProperty userProperty = new SimpleStringProperty();	
	private StringProperty passProperty = new SimpleStringProperty();	

	public RegisterViewModel() {
		disableRegisterButton.bind(userProperty.isEmpty());
		disableRegisterButton.bind(passProperty.isEmpty());
	}

	public BooleanProperty getDisableRegisterButton() {
		return disableRegisterButton;
	}

	public void setDisableRegisterButton(BooleanProperty disableRegisterButton) {
		this.disableRegisterButton = disableRegisterButton;
	}

	public StringProperty getUserProperty() {
		return userProperty;
	}

	public void setUserProperty(StringProperty userProperty) {
		this.userProperty = userProperty;
	}

	public StringProperty getPassProperty() {
		return passProperty;
	}

	public void setPassProperty(StringProperty passProperty) {
		this.passProperty = passProperty;
	}
}
