package com.mywork.place;

public class CurrentTrip {
	
	private int trip_id;
	
	private double cur_lat;
	
	private double cur_long;
	
	private double radius;
	
	public CurrentTrip(int trip_id,double cur_lat,double cur_long,double radius){
		setTrip_id(trip_id);
		
		setCur_lat(cur_lat);
		
		setCur_long(cur_long);
		
		setRadius(radius);
	}

	public int getTrip_id() {
		return trip_id;
	}

	public double getCur_lat() {
		return cur_lat;
	}

	public double getCur_long() {
		return cur_long;
	}

	public double getRadius() {
		return radius;
	}

	public void setTrip_id(int trip_id) {
		this.trip_id = trip_id;
	}

	public void setCur_lat(double cur_lat) {
		this.cur_lat = cur_lat;
	}

	public void setCur_long(double cur_long) {
		this.cur_long = cur_long;
	}

	public void setRadius(double radius) {
		this.radius = radius;
	}
	
	

}
