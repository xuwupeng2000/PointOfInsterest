package com.example.syncadapter;

import java.io.StringWriter;
import java.net.URI;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONObject;

import android.database.Cursor;
import android.util.Log;

public class SyncingHelper {
//    private static List<POI> list;
//    private static POI poi;
	public SyncingHelper(){
		
	}

    public void updateTags(Cursor cur, String uri) throws Exception{
		ArrayList<String> list = new ArrayList<String>();
    	ObjectMapper mapper = new ObjectMapper();
    	for(cur.moveToFirst();!cur.isAfterLast();cur.moveToNext()){
    		String tagid = cur.getString(cur.getColumnIndex("tagid"));
    		String poiid = cur.getString(cur.getColumnIndex("poiid"));
    		long updatetime = cur.getLong(cur.getColumnIndex("updatetime"));
    		
    		JSONObject json = new JSONObject();
    		json.put("tagid", tagid);
			json.put("poiid", poiid);
			json.put("updatetime", updatetime);
			list.add(json.toString());
    	}
    	StringWriter sw = new StringWriter();
		mapper.writeValue(sw, list);
    	HttpEntity entity = new StringEntity(sw.toString(),"UTF-8");
		HttpPost post = new HttpPost();
    	post.setURI(new URI(uri));
    	post.addHeader(entity.getContentType());
    	post.setEntity(entity);
    	HttpClient client = new DefaultHttpClient();
    	client.execute(post);
    	Log.i("net", "send to server String tagList: "+sw.toString());
    	Log.i("net", "updateTags succed");
    }
    
    
    public void updateKeys(Cursor cur, String uri) throws Exception{
		ArrayList<String> list = new ArrayList<String>();
    	ObjectMapper mapper = new ObjectMapper();
    	for(cur.moveToFirst();!cur.isAfterLast();cur.moveToNext()){
    		String tagid = cur.getString(cur.getColumnIndex("tagid"));
    		String content = cur.getString(cur.getColumnIndex("content"));
    		long updatetime = cur.getLong(cur.getColumnIndex("updatetime"));
    		
    		JSONObject json = new JSONObject();
    		json.put("tagid", tagid);
			json.put("content", content);
			json.put("updatetime", updatetime);
			list.add(json.toString());
    	}
    	StringWriter sw = new StringWriter();
		mapper.writeValue(sw, list);
    	HttpEntity entity = new StringEntity(sw.toString(),"UTF-8");
		HttpPost post = new HttpPost();
    	post.setURI(new URI(uri));
    	post.addHeader(entity.getContentType());
    	post.setEntity(entity);
    	HttpClient client = new DefaultHttpClient();
    	client.execute(post);
    	Log.i("net", "send to server String keyList* * *: "+sw.toString());
    	Log.i("net", "updateTags succed");
    }
    
    
    
    //update all coms if we want to the coms after lasttime then change the sql in databasehelper
    //i do that for testing otherwise there is no download at all, cuz basicly no coms or pois can newer then you, you are the only one update these suffs
    public void updateComs(Cursor cur, String uri) throws Exception{
    	ArrayList<String> list = new ArrayList<String>();
    	ObjectMapper mapper = new ObjectMapper();
    	
    	for(cur.moveToFirst();!cur.isAfterLast();cur.moveToNext()){
    		String comid = cur.getString(cur.getColumnIndex("comid"));
    		String poiid = cur.getString(cur.getColumnIndex("poiid"));
    		String username = cur.getString(cur.getColumnIndex("username"));
    		String content = cur.getString(cur.getColumnIndex("content"));
    		long updatetime = cur.getLong(cur.getColumnIndex("updatetime"));
    		
    		JSONObject json = new JSONObject();
    		json.put("comid", comid);
			json.put("poiid", poiid);
			json.put("username", username);
			json.put("content", content);
			json.put("updatetime", updatetime);
			list.add(json.toString());
        	Log.i("net", "this is a single comment "+json.toString());
        	Log.i("net", "updateComs succed");
    	}
    	StringWriter sw = new StringWriter();
		mapper.writeValue(sw, list);
    	HttpEntity entity = new StringEntity(sw.toString(),"UTF-8");
		HttpPost post = new HttpPost();
    	post.setURI(new URI(uri));
    	post.addHeader(entity.getContentType());
    	post.setEntity(entity);
    	HttpClient client = new DefaultHttpClient();
    	client.execute(post);
    	Log.i("net", "send to server String comsList String"+sw.toString());
    	Log.i("net", "updateTags succed");
    }
    
    public void updateRates(Cursor cur, String uri) throws Exception{
    	ArrayList<String> list = new ArrayList<String>();
    	ObjectMapper mapper = new ObjectMapper();

    	for(cur.moveToFirst();!cur.isAfterLast();cur.moveToNext()){
    		String ratid = cur.getString(cur.getColumnIndex("ratid"));
    		String poiid = cur.getString(cur.getColumnIndex("poiid"));
    		String username = cur.getString(cur.getColumnIndex("username"));
    		int rate = cur.getInt(cur.getColumnIndex("rate"));
    		long updatetime = cur.getLong(cur.getColumnIndex("updatetime"));
    		
    		JSONObject json = new JSONObject();
    		json.put("ratid", ratid);
			json.put("poiid", poiid);
			json.put("username", username);
			json.put("rate", rate);
			json.put("updatetime", updatetime);
			
        	Log.i("net", "updateRates succed");
        	list.add(json.toString());
    	}
    	
    	StringWriter sw = new StringWriter();
		mapper.writeValue(sw, list);
    	HttpEntity entity = new StringEntity(sw.toString(),"UTF-8");
		HttpPost post = new HttpPost();
    	post.setURI(new URI(uri));
    	post.addHeader(entity.getContentType());
    	post.setEntity(entity);
    	HttpClient client = new DefaultHttpClient();
    	client.execute(post);
    	Log.i("net", "send to server String ratesList String"+sw.toString());
    	Log.i("net", "updateRates succed");
    }
    
    public void updatePhotos(Cursor cur, String uri) throws Exception{
    	ArrayList<String> list = new ArrayList<String>();
    	ObjectMapper mapper = new ObjectMapper();
    	
    	for(cur.moveToFirst();!cur.isAfterLast();cur.moveToNext()){
    		String picid = cur.getString(cur.getColumnIndex("picid"));
    		String poiid = cur.getString(cur.getColumnIndex("poiid"));
    		String username = cur.getString(cur.getColumnIndex("username"));
    		String smallpic = cur.getString(cur.getColumnIndex("smallpic"));
    		String bigpic = cur.getString(cur.getColumnIndex("bigpic"));
    		long updatetime = cur.getLong(cur.getColumnIndex("updatetime"));
    		
    		JSONObject json = new JSONObject();
    		json.put("picid", picid);
			json.put("poiid", poiid);
			json.put("username", username);
			json.put("smallpic", smallpic);
			json.put("bigpic", bigpic);//hacking
			json.put("updatetime", updatetime);
			Log.i("net", "making picStr in the client side: " + json.toString());
			Log.i("net","send to server String picid: "+picid);
        	list.add(json.toString());
    	}
    	
    	StringWriter sw = new StringWriter();
		mapper.writeValue(sw, list);
    	HttpEntity entity = new StringEntity(sw.toString(),"UTF-8");
		HttpPost post = new HttpPost();
    	post.setURI(new URI(uri));
    	post.addHeader(entity.getContentType());
    	post.setEntity(entity);
    	HttpClient client = new DefaultHttpClient();
    	client.execute(post);
    	Log.i("net", "send to server String picsList going to update to server"+sw.toString());
    	Log.i("net", "updatePics succed");
    }
    
    //update all the poi, after we will only update the new poi which be created after the lastupdate time. the time already in the client side database
    public void updatePOIs(Cursor cur, String uri) throws Exception {
    	ArrayList<String> list = new ArrayList<String>();
    	ObjectMapper mapper = new ObjectMapper();
		for(cur.moveToFirst();!cur.isAfterLast();cur.moveToNext()){
			String poiid = cur.getString(cur.getColumnIndex("poiid"));
			String title = cur.getString(cur.getColumnIndex("title"));
			String des = cur.getString(cur.getColumnIndex("des"));
			String state = cur.getString(cur.getColumnIndex("state"));
			int lat = cur.getInt(cur.getColumnIndex("lat"));
			int lng = cur.getInt(cur.getColumnIndex("lng"));
			long timestamp = cur.getLong(cur.getColumnIndex("updatetime"));
			String username = cur.getString(cur.getColumnIndex("username"));
			Log.i("net",Long.toString(timestamp));
			
			JSONObject json = new JSONObject();
			json.put("poiid", poiid);
			json.put("title", title);
			json.put("des", des);
			json.put("lat", lat);
			json.put("lng", lng);
			json.put("updatetime", timestamp);
			json.put("username", username);
			json.put("state", state);
			list.add(json.toString());
    	}
		StringWriter sw = new StringWriter();
		mapper.writeValue(sw, list);
    	HttpEntity entity = new StringEntity(sw.toString(),"UTF-8");
		HttpPost post = new HttpPost();
    	post.setURI(new URI(uri));
    	post.addHeader(entity.getContentType());
    	post.setEntity(entity);
    	HttpClient client = new DefaultHttpClient();
    	client.execute(post);
    	Log.i("net", "send to server String poisList String"+sw.toString());
    	Log.i("net", "updatePOIs succed");
    }
    
    //download all the new pois as a big JSON string
    public String downloadPOIs(long lasttime, String uri)throws Exception{
    	//send a timestamp
    	JSONObject json = new JSONObject();
		json.put("lasttime", lasttime);
    	HttpEntity entity = new StringEntity(json.toString(),"UTF-8");
		HttpPost post = new HttpPost();
    	post.setURI(new URI(uri));
    	post.addHeader(entity.getContentType());
    	post.setEntity(entity);
    	HttpClient client = new DefaultHttpClient();
    	HttpResponse resp = client.execute(post);
    	//new pois come back get a JSON list
    	HttpEntity resEntity = resp.getEntity();
    	String poiList = EntityUtils.toString(resEntity);
    	Log.i("net","send to server String poiList come back: "+ poiList);
    	//return the String
    	return poiList;
//    	mapper.re
    }
    
    public String downloadComs(long lasttime, String uri) throws Exception{
    	//send a timestamp
    	JSONObject json = new JSONObject();
    	json.put("lasttime", lasttime);
    	HttpEntity entity = new StringEntity(json.toString(),"UTF-8");
		HttpPost post = new HttpPost();
    	post.setURI(new URI(uri));
    	post.addHeader(entity.getContentType());
    	post.setEntity(entity);
    	HttpClient client = new DefaultHttpClient();
    	HttpResponse resp = client.execute(post);
    	HttpEntity resEntity = resp.getEntity();
    	String comsList = EntityUtils.toString(resEntity);
    	Log.i("net","send to server String comsList come back: "+ comsList);
    	return comsList;
    }
    
    public String downloadTags(long lasttime, String uri) throws Exception{
    	//send a timestamp and get a  JSON string result
    	JSONObject json = new JSONObject();
    	json.put("lasttime", lasttime);
    	HttpEntity entity = new StringEntity(json.toString(),"UTF-8");
		HttpPost post = new HttpPost();
    	post.setURI(new URI(uri));
    	post.addHeader(entity.getContentType());
    	post.setEntity(entity);
    	HttpClient client = new DefaultHttpClient();
    	HttpResponse resp = client.execute(post);
    	HttpEntity resEntity = resp.getEntity();
    	String tagsList = EntityUtils.toString(resEntity);
    	Log.i("net","send to server String tagsList come back: "+ tagsList);
    	return tagsList;
    }
    
    public String downloadRates(long lasttime, String uri) throws Exception{
    	//send a timestamp and get a  JSON string result
    	JSONObject json = new JSONObject();
    	json.put("lasttime", lasttime);
    	HttpEntity entity = new StringEntity(json.toString(),"UTF-8");
		HttpPost post = new HttpPost();
    	post.setURI(new URI(uri));
    	post.addHeader(entity.getContentType());
    	post.setEntity(entity);
    	HttpClient client = new DefaultHttpClient();
    	HttpResponse resp = client.execute(post);
    	HttpEntity resEntity = resp.getEntity();
    	String ratesList = EntityUtils.toString(resEntity);
    	Log.i("net","send to server String ratesList come back: "+ ratesList);
    	return ratesList;
    }
    
    public String downloadPic(long lasttime, String uri) throws Exception{
    	//send a timestamp and get a  JSON string result
    	JSONObject json = new JSONObject();
    	json.put("lasttime", lasttime);
    	HttpEntity entity = new StringEntity(json.toString(),"UTF-8");
		HttpPost post = new HttpPost();
    	post.setURI(new URI(uri));
    	post.addHeader(entity.getContentType());
    	post.setEntity(entity);
    	HttpClient client = new DefaultHttpClient();
    	HttpResponse resp = client.execute(post);
    	HttpEntity resEntity = resp.getEntity();
    	String picsList = EntityUtils.toString(resEntity);
    	Log.i("net","send to server String picsList come back: "+ picsList);
    	return picsList;
    }
 
    
}
