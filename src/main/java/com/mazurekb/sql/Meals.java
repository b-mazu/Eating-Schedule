package com.mazurekb.sql;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Meals {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY )
	private long id;
	
	
}
