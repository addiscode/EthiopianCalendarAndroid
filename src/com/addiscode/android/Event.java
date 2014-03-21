package com.addiscode.android;




public class Event {
	private int id;
	private String title;
	private String icon;
	private boolean isEthiopian;
	private int month,day, year;
	private boolean isLeapYearMovable;
	
	public static final int NO_EVENT = 0;
	
	public static final int IS_INTERNATIONAL = 1;
	
	public static final int IS_ETHIOPIAN = 2;
	
	
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public boolean isEthiopian() {
		return isEthiopian;
	}
	public void setEthiopian(boolean isEthiopian) {
		this.isEthiopian = isEthiopian;
	}
	public int getMonth() {
		return month;
	}
	public void setMonth(int month) {
		this.month = month;
	}
	public int getDay() {
		return day;
	}
	public void setDay(int day) {
		this.day = day;
	}
	public boolean isLeapYearMovable() {
		return isLeapYearMovable;
	}
	public void setLeapYearMovable(boolean isLeapYearMovable) {
		this.isLeapYearMovable = isLeapYearMovable;
	}
	
	

}
