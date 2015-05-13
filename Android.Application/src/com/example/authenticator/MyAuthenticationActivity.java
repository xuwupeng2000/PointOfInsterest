package com.example.authenticator;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.client.R;

//this is a hecking job, need to be marged into your register system, this is not my ideal. it is a android syncing and auth system requement.
public class MyAuthenticationActivity extends AccountAuthenticatorActivity{
	private AccountManager myAccountManager;
	private AutNetHelper anh;
	private EditText usernameET;
	private EditText passwdET;
	private Context ctx;
//	private String CheckAutURI = "http://192.168.1.5:8080/NewServlets/AutServlet";
//	private String CreateNewAccountURI = "http://192.168.1.5:8080/NewServlets/CreateAccServlet"; 
	
	protected void onCreate(Bundle icicle) {
			super.onCreate(icicle);
			setContentView(R.layout.aui);
			usernameET = (EditText)findViewById(R.id.username);
			passwdET = (EditText)findViewById(R.id.passwd);
			myAccountManager = AccountManager.get(this);
			ctx = this;
	}	
	 
	 public void loginAccount(View view) {
	        String username = usernameET.getText().toString();
	        String password = passwdET.getText().toString();
	        if(username.isEmpty() || password.isEmpty()){
	        	Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show();
	        }
	        else{
//	        	anh = new AutNetHelper();
//	        	try{
//		        	String res = anh.checkAut(username, password, CheckAutURI);
//		        	Log.i("aut", "this is the autcheck from server "+res);
//		        	if(res.equalsIgnoreCase("pass")){
//		        		final Account account = new Account(username, com.example.auth.Constants.ACCOUNT_TYPE);
//			        	setupAccount(account,"abc");
//			        	//go to main activity
//			        	Intent intent = new Intent(this, MainActivity.class);
//			        	startActivity(intent);
//		        	}
//		        	else{
//			        	Toast.makeText(this, "Wrong password or username", Toast.LENGTH_SHORT).show();
//		        	}
//	        	}catch(Exception e){
//	        		e.printStackTrace();
//	        	}
	        }
	    }
	 
	 public void createAccount(View view){
	        Log.i("account", "create a new account ...");
	        //create a new account and add to the account list
//	        String username = usernameET.getText().toString();
	        String username = "POIUser";
	        String password = passwdET.getText().toString();
	        if(username.isEmpty() || password.isEmpty()){
	        	Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show();
	        }
	        else{
				//go to servlet to create a account
//				anh = new AutNetHelper();
//				try{
//					String res = anh.createNewAccount(username, password, CreateNewAccountURI);
//					Log.i("aut", "this is the create aut res "+ res);
//					//if the username is not unquine then tell user change one
//					if(res.equalsIgnoreCase("ok")){
//						final Account account = new Account(username, com.wu.Constants.ACCOUNT_TYPE);
//			        	setupAccount(account,"abc");
//			        	Log.i("aut", "it is ok");
//			        	//if success then go to main activity
//			        	Intent intent = new Intent(this, MainActivity.class);
//			        	startActivity(intent);
//					}
//					else{
//			        	Log.i("aut", "it is NOT OK");
//			        	Toast.makeText(ctx, "Sorry this username has been used", Toast.LENGTH_SHORT).show();
//					}
//				}catch(Exception e){
//					e.printStackTrace();
//				}
	        	final Account account = new Account(username, com.example.auth.Constants.ACCOUNT_TYPE);
	        	setupAccount(account,"abc");
				
	        }
	 }
	 
	 //whatever user did, if it can pass setup his account to syncable and add to the account list
	 public void setupAccount(Account account, String passwd){
	        myAccountManager.addAccountExplicitly(account, passwd, null);
		 	ContentResolver.setSyncAutomatically(account, "com.wu.poiprovider" , true);
	        final Intent intent = new Intent();
	        intent.putExtra(AccountManager.KEY_BOOLEAN_RESULT, true);
	        setAccountAuthenticatorResult(intent.getExtras());
	        setResult(RESULT_OK, intent);
	        finish();
	 }

	 
}
