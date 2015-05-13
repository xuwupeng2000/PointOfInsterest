package com.example.authenticator;

import java.net.URI;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.util.Log;
// we do not use this right now but it could be usefull. it is from jack's system.
public class AutNetHelper {

	public AutNetHelper() {
		
	}
	
	public String checkAut(String username, String password, String uri) throws Exception{
    	JSONObject json = new JSONObject();
    	json.put("username", username);
    	json.put("password", password);
    	
    	HttpEntity entity = new StringEntity(json.toString(),"UTF-8");
		HttpPost post = new HttpPost();
    	post.setURI(new URI(uri));
    	post.addHeader(entity.getContentType());
    	post.setEntity(entity);
    	HttpClient client = new DefaultHttpClient();
    	HttpResponse resp = client.execute(post);
    	String res = EntityUtils.toString(resp.getEntity());   
		Log.i("aut", "this is the checkAut "+json.toString());
		Log.i("aut","this is the aut check res from server "+ res);
		return res;
	}
	
	public String createNewAccount(String username, String password, String uri) throws Exception{
		JSONObject json = new JSONObject();
    	json.put("username", username);
    	json.put("password", password);
    	
    	HttpEntity entity = new StringEntity(json.toString(),"UTF-8");
		HttpPost post = new HttpPost();
    	post.setURI(new URI(uri));
    	post.addHeader(entity.getContentType());
    	post.setEntity(entity);
    	HttpClient client = new DefaultHttpClient();
    	HttpResponse resp = client.execute(post);
    	String res = EntityUtils.toString(resp.getEntity());   
		Log.i("aut","this is CREATE RES "+ res);
		return res;
	}
}
