package com.example.client;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;
import com.javadocmd.simplelatlng.LatLng;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;

public class POI extends OverlayItem{
	private String poiid;
	private int lat;
	private int lng;
	private String title;
	private String des;
	private long timestamp;
	private String username;


	
//	public POI(String title, String des, int lat, int lng, long timestamp, String username) {
//		super(new GeoPoint(lat,lng), title, des);
//		this.poiid = Integer.toString(lat)+Integer.toString(lng)+username;
//		this.title = title;
//		this.des = des;
//		this.lat = lat;
//		this.lng = lng;
//		this.timestamp = timestamp;
//		this.username = username;
//	}
	
	public POI(String poiid, String title, String des, int lat, int lng, long timestamp, String username) {
		super(new GeoPoint(lat,lng), title, des);
		this.poiid = poiid;
		this.title = title;
		this.des = des;
		this.lat = lat;
		this.lng = lng;
		this.timestamp = timestamp;
		this.username = username;
	}


	public String getPoiid() {
		return poiid;
	}


	public void setPoiid(String poiid) {
		this.poiid = poiid;
	}


	public int getLat() {
		return lat;
	}


	public void setLat(int lat) {
		this.lat = lat;
	}


	public int getLng() {
		return lng;
	}


	public void setLng(int lng) {
		this.lng = lng;
	}


	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	public String getDes() {
		return des;
	}


	public void setDes(String des) {
		this.des = des;
	}


	public long getTimestamp() {
		return timestamp;
	}


	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}


	public void setUsername(String username) {
		this.username = username;
	}


	public String getUsername() {
		return username;
	}
	 /**
	  * *Calculate the distance to current point
	  * Note: the result need to divide by 1E6
    * @param 
    */
	public int distanceto(int lat1,int lng1){

		LatLng point1=new LatLng(lat1/1E6,lng1/1E6);
		LatLng point2=new LatLng(this.lat/1E6,this.lng/1E6);
		double distance=LatLngTool.distance(point1,point2,LengthUnit.METER);
		return (int)distance;
	}

	
	
}