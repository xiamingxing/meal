package com.meal.bean;

import java.io.Serializable;
import java.util.ArrayList;

public class MyNewGroupon implements Serializable{
	
	private Long gid;// 团购ID

	private double residueCost;
	
	private Long residueTime;
	
	private String sellerName;
	
	private String logo;
	
	private ArrayList<MyNewGouponMenuItem> menudata;
	
	private MyNewGroupon(){
		
	}

	public Long getGid() {
		return gid;
	}

	public void setGid(Long gid) {
		this.gid = gid;
	}

	public double getResidueCost() {
		return residueCost;
	}

	public void setResidueCost(double residueCost) {
		this.residueCost = residueCost;
	}

	public Long getResidueTime() {
		return residueTime;
	}

	public void setResidueTime(Long residueTime) {
		this.residueTime = residueTime;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getSellerName() {
		return sellerName;
	}

	public void setSellerName(String sellerName) {
		this.sellerName = sellerName;
	}

	public ArrayList<MyNewGouponMenuItem> getMenudata() {
		return menudata;
	}

	public void setMenudata(ArrayList<MyNewGouponMenuItem> menudata) {
		this.menudata = menudata;
	}

	
}
