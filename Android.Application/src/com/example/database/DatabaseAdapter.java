package com.example.database;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

import com.example.client.POI;

public class DatabaseAdapter {
    private DatabaseHelper myDbHelper;
    private SQLiteDatabase myDb;
    private final Context ctx;

   
    private static class DatabaseHelper extends SQLiteOpenHelper {
    	 private static final String DATABASE_NAME = "POI_CLIENT_DATABASE";
    	 private static final int DATABASE_VERSION = 4;
    	    
        DatabaseHelper(Context ctx) {
            super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
        	
            // virtual table for FTS3 search
        	db.execSQL("CREATE VIRTUAL TABLE POI USING fts3(" + 
        			"poiid varchar(200) not null," + 
        			"lat int not null," +
        			"lng int not null," +
        			"title text," +
        			"des text," +
        			"username varchar(50) not null," +
        			"state varchar(50) DEFAULT 'non-deleted'," +
        			"updatetime long not null);"); //I should use default = getdate(), i forgot it.
        			
        	db.execSQL("CREATE TABLE USERS(" + 
        			"userid varchar(200) not null," + 
        			"username varchar(200) not null," +
        			"updatetime long not null," +
        			"password varchar(200) not null);");
        	
        	db.execSQL("CREATE TABLE PIC(" + 
        			"picid varchar(200) not null," + 
        			"poiid varchar(200) not null," +
        			"username varchar(200) ," +
        			"smallpic text," +
        			"bigpic text," +
        			"updatetime long not null," +
        			"FOREIGN KEY(poiid) references POI(poiid));");
        	
        	db.execSQL("CREATE TABLE RAT(" + 
        			"ratid varchar(200) not null," +
        			"poiid varchar(200) not null," + 
        			"username varchar(200) not null," +
        			"rate int," +
        			"updatetime long not null," +
        			"FOREIGN KEY(username) references USERS(username)," +
        			"FOREIGN KEY(poiid) references POI(poiid));");
        	
        	db.execSQL("CREATE TABLE COM(" + 
        			"comid varchar(200) not null," + 
        			"poiid varchar(200) not null," +
        			"username varchar(200) not null," +
        			"content text," +
        			"updatetime long not null," +
        			"FOREIGN KEY(username) references USERS(username)," +
        			"FOREIGN KEY(poiid) references POI(poiid));");
        	
        	db.execSQL("CREATE TABLE KEY(" + 
        			"tagid varchar(200) not null ," + 
        			"content varchar(200) ," +
        			"updatetime long not null);");
        	
        	db.execSQL("CREATE TABLE TAG(" + 
        			"tagid varchar(200) not null," + 
        			"poiid varchar(200) not null," +
        			"updatetime long not null," +
        			"FOREIGN KEY(tagid) references KEY(tagid)," +
        			"FOREIGN KEY(poiid) references POI(poiid));");
        	
        	db.execSQL("CREATE TABLE UPDATETIME(" + 
        			"fixlabel varchar(200) not null," +
        			"time long not null);");
        	Log.i("db", "successfully created the data schema");
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        	db.execSQL("drop table if exists COM;");
    		db.execSQL("drop table if exists KEY;");
    		db.execSQL("drop table if exists POI;");
    		db.execSQL("drop table if exists TAG;");
    		db.execSQL("drop table if exists USERS;");
    		db.execSQL("drop table if exists RAT;");
    		db.execSQL("drop table if exists PIC;");      
    		db.execSQL("drop table if exists UPDATETIME;");      
        }
    }
    
    public DatabaseAdapter(Context ctx) {
        this.ctx = ctx;
    }
 
    public void open() throws SQLException {
        myDbHelper = new DatabaseHelper(ctx);
        myDb = myDbHelper.getWritableDatabase();
    	Log.e("db", "got a writeable database");
    }
    
    public void close() {
        myDbHelper.close();
    }
    
    public Cursor fetchPicFromLocalDatabase(String poiid){
    	Cursor cur = myDb.query("PIC", new String[]{"poiid","picid","bigpic"}, "poiid="+"'"+poiid+"'", null, null, null, null);
    	return cur;
    }
    
    public void deletePOIpicture(String picid){
    	ContentValues values = new ContentValues();
    	long timestamp =  Calendar.getInstance().getTimeInMillis();

		values.put("picid", picid);
		values.put("bigpic", "null");
		values.put("smallpic", "null");
		values.put("updatetime", timestamp);//we need the new updatetime then it will give to the server, then everyone else is not able to view this pic too
		values.put("username", "null");
		myDb.update("pic", values, "picid = "+"'"+picid+"'", null);
    	Log.i("db", "This picture has been deleted "+values.toString());
    }
    
    public void savePicToLocalDatabase(String poiid, String picid, String bigPicJSONString, String smallPicJSONString, String timeupdate, String username){
    	Cursor cur = myDb.query("PIC", new String[]{"picid"}, "picid="+"'"+picid+"'", null, null, null, null);
    	Log.i("db", "savePic passed");
    	if(!cur.moveToFirst()){
    		ContentValues values = new ContentValues();
        	long timestamp =  Calendar.getInstance().getTimeInMillis();
    		values.put("poiid", poiid);
    		values.put("picid", picid);
    		values.put("bigpic", bigPicJSONString);
    		values.put("smallpic", smallPicJSONString);
    		values.put("updatetime", timestamp);
    		values.put("username", username);
    		myDb.insert("PIC", null, values);
    		Log.i("db", "a new pic was inserted into local database. it is "+values.toString());
    	}
    }
    
    
    public Cursor fetchRate(String poiid, String username){
    	Cursor cur = myDb.query("RAT", new String[]{"rate"}, "poiid="+"'"+poiid+"'"+" and username="+"'"+username+"'", null, null, null, null);
    	return cur;
    }
    
    //this is a method to save data from server
    public void savePOItoDatabaseFromJSONString(String poiList) throws Exception {
    	ObjectMapper mapper = new ObjectMapper();
    	JsonNode rootNode = mapper.readValue(poiList, JsonNode.class);
    	Iterator<JsonNode> iterator = rootNode.getElements();
    	while(iterator.hasNext()){
    		String poiStr = iterator.next().getTextValue();
    		Log.i("db", "this is a poi element "+poiStr);
    		JsonNode poiNode = mapper.readValue(poiStr, JsonNode.class);
        	String poiid = poiNode.path("poiid").getTextValue();
        	Log.i("db", "this is the poiid from poi JSON String "+poiid);
        	Cursor cur = myDb.query("poi", new String[]{"poiid"}, "poiid = "+"'"+poiid+"'", null, null, null, null);
        	//if it is old one we just update it, sometime people change something of poi and its id wont be changed so we like to update otherthing
        	if(cur.getCount()>0){
        		Log.i("db", "old poi, update");
        		ContentValues values = new ContentValues();
            	int lat = poiNode.path("lat").getIntValue();
            	int lng = poiNode.path("lng").getIntValue();
            	String title = poiNode.path("title").getTextValue();
            	String des = poiNode.path("des").getTextValue();
            	String state = poiNode.path("state").getTextValue();
            	long timestamp = poiNode.path("updatetime").getLongValue();
            	String username = poiNode.path("username").getTextValue();
            	values.put("poiid", poiid);// we can update the poiid also, but sure we also can ignore it
            	values.put("title", title);
            	values.put("state", state);
            	values.put("des", des);
            	values.put("lat", lat);
            	values.put("lng", lng);
            	values.put("updatetime", timestamp);
            	values.put("username", username);
        		myDb.update("poi", values, "poiid = "+"'"+poiid+"'", null);
            	Log.i("db", "update a poi from a server's JSON String which is "+values.toString());
        	}
        	//if it is a brand new poi insert it into database 
        	else{
        		Log.i("db", "new poi, insert");
            	ContentValues values = new ContentValues();
            	int lat = poiNode.path("lat").getIntValue();
            	int lng = poiNode.path("lng").getIntValue();
            	String title = poiNode.path("title").getTextValue();
            	String des = poiNode.path("des").getTextValue();
            	String state = poiNode.path("state").getTextValue();
            	long timestamp = poiNode.path("updatetime").getLongValue();
            	String username = poiNode.path("username").getTextValue();
            	values.put("poiid", poiid);
            	values.put("title", title);
            	values.put("des", des);
            	values.put("lat", lat);
            	values.put("lng", lng);
            	values.put("state", state);
            	values.put("updatetime", timestamp);
            	values.put("username", username);
            	myDb.insert("POI", null, values);
            	Log.i("db", "insert a poi from a server's JSON String which is "+values.toString());
        	}
    	}
    	
    	
    }
    
    //the database method for save poi to database if you add it via UI
    public void savePOItoDatabaseFromUI(POI poi){
    	ContentValues values = new ContentValues();
    	String poiid = poi.getPoiid();
    	int lat = poi.getLat();
    	int lng = poi.getLng();
    	String title = poi.getTitle();
    	String des = poi.getDes();
    	long timestamp =  Calendar.getInstance().getTimeInMillis();
    	String username = poi.getUsername();
    	values.put("poiid", poiid);
    	values.put("title", title);
    	values.put("des", des);
    	values.put("lat", lat);
    	values.put("lng", lng);
    	values.put("state","non-deleted");
    	values.put("updatetime", timestamp);
    	values.put("username", username);
    	myDb.insert("POI", null, values);
//    	Log.i("db", Long.toString(timestamp));
    	Log.i("db", "insert a POI from UI");
    }
    
    //fetch the last update time from database, it will be use for syncing. we only fetch the new poi
    public long fetchLastUpdateTime(){
    	long lastTime = 0;
    	Cursor cur = myDb.rawQuery("SELECT time FROM UPDATETIME WHERE fixlabel = 'first'", null);
    	cur.moveToFirst();
    	if(cur.moveToFirst()){
        	lastTime = cur.getLong(cur.getColumnIndex("time"));
        	Log.i("db", "we have a time it is: "+Long.toString(lastTime));
    	}
    	else{
    		lastTime = 0;
    		ContentValues values = new ContentValues();
    		values.put("fixlabel", "first");
    		values.put("time", lastTime);
    		myDb.insert("UPDATETIME", null, values);
    	}
    	Log.i("db", "lastTime = "+Long.toString(lastTime));
    	return lastTime;
    }
    
    //get a new time after update and download
    public void setNewLastUpdateTIme(){
    	long newLastTime = Calendar.getInstance().getTimeInMillis();
		ContentValues values = new ContentValues();
		values.put("time", newLastTime);
		myDb.update("UPDATETIME", values, "fixlabel = 'first'", null);
    	Log.i("db", "new updated lastTime = "+Long.toString(newLastTime)+" has been inserted into database which is a new last time");
    }
    
   
    
    public void updateDesAndTitleOfPOIinLocalDatabase(String title, String des, String poiid){
    	ContentValues values = new ContentValues();
    	long timestamp =  Calendar.getInstance().getTimeInMillis();
    	values.put("title", title);
    	values.put("des", des);
    	values.put("updatetime", timestamp);
    	Log.i("db", "this is the id i want to update: "+poiid);
    	myDb.update("poi", values, "poiid = "+"'"+poiid+"'", null);
//    	String updateSQL = "UPDATE poi SET title= 'how are you' , des= 'this is a hacking job' WHERE poiid ="+"'"+poiid+"'";
//    	Log.i("db", updateSQL);
//    	myDb.execSQL(updateSQL);
    	Log.i("db", "successfully update a poi of localdatabase");
    	
    }
    

    public void deletePOIofLocalDatabase(String poiid){
    	ContentValues values = new ContentValues();
    	long timestamp =  Calendar.getInstance().getTimeInMillis();
    	values.put("state", "deleted");
    	values.put("updatetime", timestamp);
    	myDb.update("poi", values, "poiid = "+"'"+poiid+"'", null);
    }
    
    //get owner name of POI 
    public String fetchNameByPoiid(String poiid){
    	String Owner="NOT FOUND";
    	//Cursor cur=myDb.query("poi", new String[]{"username"}, "poiid = "+"'"+poiid+"'", null, null, null, null);
    	Cursor cur=myDb.rawQuery("select username from poi where poiid='"+poiid+"'", null);
    	if (cur.moveToFirst()){
    		Owner=cur.getString(cur.getColumnIndex("username"));
    	}
    	cur.close();
    	return Owner;
    }
    //fetch all poi from database return a cursor
    public Cursor fetchPOIs(){
    	Log.i("now", "fatch pois from database");
    	Cursor cur = myDb.query("POI", new String[]{"poiid","title","des","lat","lng","updatetime","username","state"}, "state = 'non-deleted'", null, null, null, null);
    	return cur;
    }
    
    /**
     * Returns a single POI from the database
     * @param poiid
     * @return cursor with POI
     */
    public Cursor fetchAPOI(String poiid){
    	Cursor cur = myDb.query("POI", new String[]{"poiid","title","des","lat","lng","updatetime","username","state"}, "poiid="+"'"+poiid+"'", null, null, null, null);
    	return cur;
    }
   
    public Cursor fetchNewPOIs(long lasttime){
    	Cursor cur = myDb.query("POI", new String[]{"poiid","title","des","lat","lng","updatetime","username","state"}, "updatetime>"+Long.toString(lasttime), null, null, null, null);
    	return cur;
    }
    
    /**
     * gets all current tagging keywords in the database
     * @return cursor of all keywords
     */
    public Cursor fetchAllTags(){
    	Cursor cur = myDb.query("KEY", new String[]{"content"}, null, null, null, null, null);
    	return cur;
    }
    
    /**
     * gets all the tag-ids for the keywords in the database
     * @return cursor of tags
     */
    public Cursor fetchAllTagIds(){
    	Cursor cur = myDb.query("KEY", new String[]{"tagid"}, null, null, null, null, null);
    	return cur;
    }
    
    /**
     * the method title says it all, so gets all tag ids related to a particular POI
     * @param poiid
     * @return cursor of tag-ids
     */
    public Cursor fetchAllTagsForOnePOI(String poiid){
    	Cursor cur = myDb.query("TAG", new String[]{"tagid"}, "poiid="+"'"+poiid+"'", null, null, null, null);
    	return cur;
    }
    
    /**
     * returns all POI related to one particular tag
     * @param tagid
     * @return cursor of POIs
     */
    public Cursor fetchAllPOIsForOneTag(String tagid){
    	Cursor cur = myDb.query("TAG", new String[]{"poiid"}, "tagid="+"'"+tagid+"'", null, null, null, null);
    	return cur;
    }
    
    /**
     * gets the keyword for a particular tag
     * @param tagid
     * @return keyword
     */
    public Cursor fetchAllTags(String tagid){
    	Cursor cur = myDb.query("KEY", new String[]{"content"}, "tagid="+"'"+tagid+"'", null, null, null, null);
    	return cur;
    }
    
    /**
     * gets the tagid for a certain keyword
     * @param content
     * @return returns tagid
     */
    public String fetchAllTagid(String content){
    	Cursor cur = myDb.query("KEY", new String[]{"tagid"}, "content="+"'"+content+"'", null, null, null, null);
    	if(cur.moveToFirst()){
				return cur.getString(cur.getColumnIndex("tagid"));
    	}
    	return "";
    }
    
    public Cursor fetchNewTags(long lasttime){
    	Cursor cur = myDb.query("TAG", new String[]{"poiid","tagid","updatetime"}, "updatetime>"+Long.toString(lasttime), null, null, null, null);
    	return cur;
    }
    
    public Cursor fetchAllComs(){
    	Cursor cur = myDb.query("COM", new String[]{"comid","poiid","username","content","updatetime"}, null, null, null, null, null);
    	return cur;
    }
    
    public Cursor fetchNewComs(long lasttime){
    	Cursor cur = myDb.query("COM", new String[]{"comid","poiid","username","content","updatetime"}, "updatetime>"+Long.toString(lasttime), null, null, null, null);
    	return cur;
    }
    
    public Cursor fetchAllRates(){
    	Cursor cur = myDb.query("RAT", new String[]{"ratid","poiid","username","rate","updatetime"}, null, null, null, null, null);
    	return cur;
    }
    
    public Cursor fetchNewRates(long lasttime){
    	Cursor cur = myDb.query("RAT", new String[]{"ratid","poiid","username","rate","updatetime"}, "updatetime>"+Long.toString(lasttime), null, null, null, null);
    	return cur;
    }
    
    public Cursor fetchAllPhotos(){
    	Cursor cur = myDb.query("PIC", new String[]{"picid","poiid","smallpic","bigpic","updatetime"}, null, null, null, null, null);
    	return cur;
    }
    
    public Cursor fetchNewPhotos(long lasttime){
    	Cursor cur = myDb.query("PIC", new String[]{"picid","poiid","smallpic","bigpic","updatetime","username"}, "updatetime>"+Long.toString(lasttime), null, null, null, null);
    	return cur;
    }
    
    public Cursor fetchNewKeys(long lasttime){
    	Cursor cur = myDb.query("KEY", new String[]{"tagid","content","updatetime"}, "updatetime>"+Long.toString(lasttime), null, null, null, null);
    	return cur;
    }
    
    /**
     * Adds a new key to the database, using the POIid + content as a tagid (unique :) )
     * @param content
     * @param poiid
     */
    public void addNewKey(String content, String poiid){
    	String tagid = poiid+content;
    	long timestamp =  Calendar.getInstance().getTimeInMillis();

    		ContentValues values = new ContentValues();
        	values.put("tagid", tagid);
        	values.put("content", content);
        	values.put("updatetime", timestamp);
        	myDb.insert("KEY", null, values);
        	Log.i("db", "a new key was inserted into local database!it is "+ values.toString());
        	
    }
    
    /**
     * joins a tag and POI together (references)
     * @param tagid
     * @param poiid
     */
    public void addNewTag(String tagid, String poiid){
    	//possible issue
    	long timestamp =  Calendar.getInstance().getTimeInMillis();
    	//need check the tagid is unique
    	Cursor cur = myDb.query("TAG", new String[]{"tagid"}, "tagid="+"'"+tagid+"'" + "AND poiid="+"'"+poiid+"'", null, null, null, null);
    	if(!cur.moveToFirst()){
    		ContentValues values = new ContentValues();
        	values.put("tagid", tagid);
        	values.put("poiid", poiid);
        	values.put("updatetime", timestamp);
        	myDb.insert("TAG", null, values);
        	Log.i("db", "a new tag was inserted into local database!it is "+ values.toString());
    	}
    	else{
    		//tell the user we already have this tag for this poi
    	}
    }
    
    public Cursor fetchOldComments(String poiid){
    	Cursor cur = myDb.query("COM", new String[]{"username","content"}, "poiid="+"'"+poiid+"'", null, null, null, null);
    	return cur;
    }
    
    public void addNewComment(String content, String poiid, String username){
    	//possible issue how about the comment is real big?
    	String comid = poiid+content;
    	long timestamp =  Calendar.getInstance().getTimeInMillis();
    	//need check the comid is unique, we dont want to people repeat some thing again and again
    	Cursor cur = myDb.query("COM", new String[]{"comid"}, "comid="+"'"+comid+"'", null, null, null, null);
    	if(!cur.moveToFirst()){
    		ContentValues values = new ContentValues();
    		values.put("comid", comid);
    		values.put("poiid", poiid);
    		values.put("username", username);
    		values.put("content", content);
    		values.put("updatetime", timestamp);
    		myDb.insert("COM", null, values);
    		Log.i("db", "a new comment was inserted into local database. it is "+values.toString());
    	}
    }
    
    public Cursor fatchOldRates(String poiid){
    	Cursor cur = myDb.query("RAT", new String[]{"username","rate"}, "poiid="+"'"+poiid+"'", null, null, null, null);
    	return cur;
    }
    
    /**
     * updates a user's rating for a particular POI
     * @param rate
     * @param poiid
     * @param username
     */
    public void ratePOI(int rate, String poiid, String username){
    	String ratid = poiid+username+Integer.toString(rate);
    	long timestamp =  Calendar.getInstance().getTimeInMillis();
    	// need to check the ratid is unique, you cant rate one poi again and again
    	Cursor cur = myDb.query("RAT", new String[]{"ratid"}, "ratid="+"'"+ratid+"'", null, null, null, null);
    	if(!cur.moveToFirst()){
    		ContentValues values = new ContentValues();
    		values.put("ratid", ratid);
    		values.put("poiid", poiid);
    		values.put("username", username);
    		values.put("rate", rate);
    		values.put("updatetime", timestamp);
    		myDb.insert("RAT", null, values);
    		Log.i("db", "a rate of poi has been inserted into local database. it is "+values.toString());
    	}

    }
    
    /**
     *  call this method after update poi to server where server also will delete these useless poi and revelant info
     */
   public void cleanDeletedPOIInfoOnLocalDatabase(){
	   int count = 0;
	   Cursor cur = myDb.query("POI", new String[]{"poiid"}, "state='deleted'", null, null, null, null);
	   for(cur.moveToFirst();!cur.isAfterLast();cur.moveToNext()){
		   String poiid = cur.getString(cur.getColumnIndex("poiid"));
		   myDb.delete("rat", "poiid="+"'"+poiid+"'", null);
		   myDb.delete("com", "poiid="+"'"+poiid+"'", null);
		   Cursor curTag = myDb.query("tag", new String[]{"tagid"}, "poiid="+"'"+poiid+"'", null, null, null, null);
		   if(curTag.moveToFirst()){
			   String tagid = curTag.getString(cur.getColumnIndex("tagid"));
			   myDb.delete("key", "tagid="+"'"+tagid+"'", null);
		   }
		   myDb.delete("tag", "poiid="+"'"+poiid+"'", null);
		   myDb.delete("pic", "poiid="+"'"+poiid+"'", null);
		   myDb.delete("poi", "poiid="+"'"+poiid+"'", null);
		   
		   count++;
	   }
	   Log.i("db", "delete count"+Integer.toString(count));
//    	myDb.delete("poi", "state='deleted'", null);
    }
   
   public void saveComsToDatabaseFromJSONString(String comsList) throws Exception{
	   ObjectMapper mapper = new ObjectMapper();
	   JsonNode rootNode = mapper.readValue(comsList, JsonNode.class);
	   Iterator <JsonNode> iterator = rootNode.getElements();
	   while(iterator.hasNext()){
		   //single coment string
		   String comStr = iterator.next().getTextValue();
		   JsonNode comNode = mapper.readValue(comStr, JsonNode.class);
		   String comid = comNode.path("comid").getTextValue();
		   Cursor cur = myDb.query("COM", new String[]{"comid"}, "comid="+"'"+comid+"'", null, null, null, null);
		   //insert
		   if(!cur.moveToFirst()){
			   String poiid = comNode.path("poiid").getTextValue();
			   String username = comNode.path("username").getTextValue();
			   String content = comNode.path("content").getTextValue();
			   long updatetime = comNode.path("updatetime").getLongValue();
			   ContentValues values = new ContentValues();
			   values.put("comid", comid);
			   values.put("poiid", poiid);
			   values.put("username", username);
			   values.put("content", content);
			   values.put("updatetime", updatetime);
			   myDb.insert("COM", null, values);
			   Log.i("db", "a new com has been " +values.toString());
		   }
		   //update database
		   else{
			   
		   }
	   }
   }
   
   public void saveTagsToDatabaseFromJSONString(String tagsList) throws Exception{
	   ObjectMapper mapper = new ObjectMapper();
	   JsonNode rootNode = mapper.readValue(tagsList, JsonNode.class);
	   Iterator <JsonNode> iterator = rootNode.getElements();
	   while(iterator.hasNext()){
		   //single tag string
		   String tagStr = iterator.next().getTextValue();
		   JsonNode tagNode = mapper.readValue(tagStr, JsonNode.class);
		   String tagid = tagNode.path("tagid").getTextValue();
		   Cursor cur = myDb.query("TAG", new String[]{"tagid"}, "tagid="+"'"+tagid+"'", null, null, null, null);
		   //insert
		   if(!cur.moveToFirst()){
			   String poiid = tagNode.path("poiid").getTextValue();
			   long updatetime = tagNode.path("updatetime").getLongValue();
			   ContentValues values = new ContentValues();
			   values.put("tagid", tagid);
			   values.put("poiid", poiid);
			   values.put("updatetime", updatetime);
			   myDb.insert("TAG", null, values);
			   Log.i("db", "a new tag has been " +values.toString());
		   }
		   //update database
		   else{
			   
		   }
	   }
   }
   
   public void saveKeysToDatabaseFromJSONString(String keysList) throws Exception{
	   ObjectMapper mapper = new ObjectMapper();
	   JsonNode rootNode = mapper.readValue(keysList, JsonNode.class);
	   Iterator <JsonNode> iterator = rootNode.getElements();
	   while(iterator.hasNext()){
		   //single tag string
		   String keyStr = iterator.next().getTextValue();
		   JsonNode keyNode = mapper.readValue(keyStr, JsonNode.class);
		   String content = keyNode.path("content").getTextValue();
		   Cursor cur = myDb.query("KEY", new String[]{"content"}, "content="+"'"+content+"'", null, null, null, null);
		   //insert
		   if(!cur.moveToFirst()){
			   String tagid = keyNode.path("tagid").getTextValue();
			   long updatetime = keyNode.path("updatetime").getLongValue();
			   ContentValues values = new ContentValues();
			   values.put("tagid", tagid);
			   values.put("content", content);
			   values.put("updatetime", updatetime);
			   myDb.insert("KEY", null, values);
			   Log.i("db", "a new key has been " +values.toString());
		   }
		   //update database
		   else{
			   
		   }
	   }
   }
   
   public void saveRatesToDatabaseFromJSONString(String ratesList) throws Exception{
	   Log.i("rat", "this is the ratesList: "+ratesList);
	   ObjectMapper mapper = new ObjectMapper();
	   JsonNode rootNode = mapper.readValue(ratesList, JsonNode.class);
	   Iterator <JsonNode> iterator = rootNode.getElements();
	   while(iterator.hasNext()){
		   //single tag string
		   String ratesStr = iterator.next().getTextValue();
		   Log.i("rat", "this is the ratStc: " + ratesStr);
		   JsonNode ratNode = mapper.readValue(ratesStr, JsonNode.class);
		   String ratid = ratNode.path("ratid").getTextValue();
		   Log.i("rat", "rat ratid= "+ratid);
		   Cursor cur = myDb.query("RAT", new String[]{"ratid"}, "ratid="+"'"+ratid+"'", null, null, null, null);
		   //insert
		   if(!cur.moveToFirst()){
			   String poiid = ratNode.path("poiid").getTextValue();
			   String username = ratNode.path("username").getTextValue();
			   int rate = ratNode.path("rate").getIntValue();
			   long updatetime = ratNode.path("updatetime").getLongValue();
			   Log.i("rat", "rat username= "+username);
			   Log.i("rat", "rat ratid= "+ratid);
			   Log.i("rat", "rat poiid= "+poiid);
			   ContentValues values = new ContentValues();
			   values.put("ratid", ratid);
			   values.put("poiid", poiid);
			   values.put("username", username);
			   values.put("rate", rate);
			   values.put("updatetime", updatetime);
			   myDb.insert("RAT", null, values);
			   Log.i("db", "a new rate has been " +values.toString());
		   }
		   //update database
		   else{
			   
		   }
	   }
   }
   
   public void svaePicToDatabaseFromJSONString(String picsList) throws Exception{
	   ObjectMapper mapper = new ObjectMapper();
	   JsonNode rootNode = mapper.readValue(picsList, JsonNode.class);
	   Iterator <JsonNode> iterator = rootNode.getElements();
	   while(iterator.hasNext()){
		   //single tag string
		   String picStr = iterator.next().getTextValue();
		   JsonNode picNode = mapper.readValue(picStr, JsonNode.class);
		   String picid = picNode.path("picid").getTextValue();
		   Cursor cur = myDb.query("PIC", new String[]{"picid"}, "picid="+"'"+picid+"'", null, null, null, null);
		   //insert
		   if(!cur.moveToFirst()){
			   String poiid = picNode.path("poiid").getTextValue();
			   String username = picNode.path("username").getTextValue();
			   String smallpic = picNode.path("samllpic").getTextValue();
			   String bigpic = picNode.path("bigpic").getTextValue();
			   long updatetime = picNode.path("updatetime").getLongValue();
			   ContentValues values = new ContentValues();
			   values.put("picid", picid);
			   values.put("poiid", poiid);
			   values.put("username", username);
			   values.put("smallpic", smallpic);
			   values.put("bigpic", bigpic);
			   values.put("updatetime", updatetime);
			   myDb.insert("PIC", null, values);
			   Log.i("db", "a new pic has been " +values.toString());
		   }
		   //update database
		   else{
			   
		   }
	   }
   }
   
   /**
    * FTS for the POI database, looking at title and body of POIs
    * @param search
    * @return cursor of POIids
    */
   public Cursor searchit(String search) {
       Cursor cursor = null;
           cursor = myDb.query(true, "POI", new String[] { "poiid", "title", "des" }, "POI" + " MATCH ?", new String[] { search + "*" }, null, null, null, null);
           return cursor;
   }
   
   /**
    * takes an arraylist and populates it with all values from cursor as specified
    * by the parameter 'hmm', i.e. if hmmm == "content" it returns all strings from
    * the column names "content".
    * @param x
    * @param cur
    * @param hmmm
    */
   public void fetchTags(ArrayList<String> x, Cursor cur, String hmmm) {
		String value = "";
		if (cur.moveToFirst()) {
			for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
				value = cur.getString(cur.getColumnIndex(hmmm));
				x.add(value);
			}
		}
		cur.close();

	}
}
   
   

   
   
   
   
   
   
