package com.rahulshettyacademy.controller;

import org.springframework.stereotype.Component;

@Component
public class AllCourseDetails {

	private  int price;
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}
	public String getCourseName() {
		return courseName;
	}
	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	private String courseName;
	private String category;
	private  String id;
	
	
	
}
