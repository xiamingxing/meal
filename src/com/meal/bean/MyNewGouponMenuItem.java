package com.meal.bean;

import java.io.Serializable;

public class MyNewGouponMenuItem implements Serializable{

	private String name;

	private String photo;

	private double price;
	
	private Long mid;

	private int count;
	
	private MyNewGouponMenuItem(){
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public Long getMid() {
		return mid;
	}

	public void setMid(Long mid) {
		this.mid = mid;
	}

}
