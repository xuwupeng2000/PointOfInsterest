package com.example.utilizes;

import java.util.ArrayList;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import com.example.client.CustomHttpClient;
import com.example.client.POIMain;
import com.example.client.register;
import com.google.gson.Gson;
import com.example.client.R;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class UserUtilizes {
	
	private static Context mContext;
	public UserUtilizes(Context mContext){
		UserUtilizes.mContext=mContext;
	}
	
	/**
	 * logOut from application
	 * @param mcontext
	 */
	public void LogOut() {

		// TODO Auto-generated method stub
		SharedPreferences mPerferences = PreferenceManager
				.getDefaultSharedPreferences(mContext);
		SharedPreferences.Editor mEditor = mPerferences.edit();
		mEditor.putString("username", "notlogined");
		mEditor.putString("userID", null);
		mEditor.putBoolean("isLogined", false);
		mEditor.commit();
		//System.exit(0);
	}	
	
/**
 * doing login and registering
 * @param mContext
 */
public  void Login()   {		
		
		final Dialog dialog = new Dialog(mContext);
		dialog.setContentView(R.layout.login_window);
		dialog.setTitle("Login");
		dialog.setCancelable(false);
		Button login_button=(Button) dialog.findViewById(R.id.log_button_login);
		final EditText log_username=(EditText)dialog.findViewById(R.id.username_edit);
		final EditText log_password=(EditText)dialog.findViewById(R.id.password_edit);
		
		login_button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				String name=log_username.getText().toString();
				String password=log_password.getText().toString();
				String userID=varyingAccount(name,password);
				Log.i("varingingACCOUNT", userID);
				if(!userID.equals("nofound")){				
					SharedPreferences mPerferences = PreferenceManager
							.getDefaultSharedPreferences(mContext);					
					SharedPreferences.Editor mEditor = mPerferences.edit();
					
					//save name userID and state of login.
					mEditor.putString("username", name);
					mEditor.putString("userID",	userID);
					mEditor.putBoolean("isLogined", true);//true is logged
					mEditor.commit();
					dialog.dismiss();
				}else
				{
					Toast.makeText(mContext, "username or password is incorrect",
							Toast.LENGTH_LONG).show();
				}
			}
		});
		
		Button exit_button=(Button)dialog.findViewById(R.id.log_button_exit);
		
		exit_button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// exit system
				dialog.dismiss();
				//System.exit(0);
			}
		});
		
		Button register_button=(Button)dialog.findViewById(R.id.log_button_register);
		register_button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//calling activity register
				Intent intent = new Intent(mContext,register.class);
				mContext.startActivity(intent);
				
			}
		});
		
		dialog.show();		
	}
	/**
	 * @param name
	 * @param password
	 * @return true the account existed
	 */
	private  String varyingAccount(String username,String password){
		String url=POIMain.baseurl+"/login";
		String rs=null;
		
		ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		postParameters.add(new BasicNameValuePair("username", username ));
		postParameters.add(new BasicNameValuePair("password", password) );
		
		try {
			String response = CustomHttpClient.executeHttpPost(url,
					postParameters);
			Gson gson=new Gson();
			rs=gson.fromJson(response, String.class);
			
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return rs;
		
	}

}
