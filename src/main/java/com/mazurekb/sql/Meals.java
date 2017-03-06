package com.mazurekb.sql;


import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

@Entity
public class Meals {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY )
	private long id;
	
	@Column(unique = true, nullable = false)
	private String name;
	private long user_id;
	
	
	private int kcal;
	
	private float fat;
	
	private float carbohydrates;

	private float protein;
	

	@ManyToMany(cascade=CascadeType.ALL)  
    @JoinTable(name="meal_products", joinColumns = @JoinColumn(name="meal_id"), inverseJoinColumns=@JoinColumn(name="product_id"))  
	private List<Products> meal_products;


	public long getId() {
		return id;
	}


	public void setId(long id) {
		this.id = id;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public long getUser_id() {
		return user_id;
	}


	public void setUser_id(long user_id) {
		this.user_id = user_id;
	}


	public int getKcal() {
		return kcal;
	}


	public void setKcal(int kcal) {
		this.kcal = kcal;
	}


	public float getFat() {
		return fat;
	}


	public void setFat(float fat) {
		this.fat = fat;
	}


	public float getCarbohydrates() {
		return carbohydrates;
	}


	public void setCarbohydrates(float carbohydrates) {
		this.carbohydrates = carbohydrates;
	}


	public float getProtein() {
		return protein;
	}


	public void setProtein(float protein) {
		this.protein = protein;
	}


	public List<Products> getMeal_products() {
		return meal_products;
	}


	public void setMeal_products(List<Products> meal_products) {
		this.meal_products = meal_products;
	}
	
	
}
