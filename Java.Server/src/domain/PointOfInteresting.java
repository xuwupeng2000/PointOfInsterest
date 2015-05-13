package domain;

import com.javadocmd.simplelatlng.LatLng;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;



public class PointOfInteresting {
        private int poi_id;
	private int lat;
	private int lng;
	private String address;
	private String title;
	private String body;	
        private int owner;
	
        public int getPOI_id(){
            return poi_id;
        }
        public void setPOI_id(int id){
            this.poi_id=id;
        }
        public int getOwner(){
            return owner;
        }
        public void setOwner(int owner){
            this.owner=owner;
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
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
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
	/*
	 * return true if the geo Value are the same 
	 */
	public boolean IsGeopointSame(int lat,int lng){
		
		if(lat==this.lat&&lng==this.lng){
			return true;
		}
		else{
			return false;
		}
	}

	
}
