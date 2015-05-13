package com.example.client;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.example.client.*;
import com.google.gson.Gson;

import android.app.Activity;
import android.content.SharedPreferences;

import android.os.Bundle;
import android.preference.PreferenceManager;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class register extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);

		final EditText reg_edit_username = (EditText) findViewById(R.id.reg_edittext_username);
		final EditText reg_edit_password = (EditText) findViewById(R.id.reg_edittext_password);
		final EditText reg_edit_password2 = (EditText) findViewById(R.id.reg_edittext_password2);
		// final EditText
		// reg_edit_email=(EditText)findViewById(R.id.reg_edittext_email);
		Button reg_button_confirm = (Button) findViewById(R.id.reg_button_confirm);
		Button reg_button_exit = (Button) findViewById(R.id.reg_button_exit);

		reg_button_exit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});

		reg_button_confirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Varying with service or register in database
				String username = reg_edit_username.getText().toString();
				String password = reg_edit_password.getText().toString();
				String password2 = reg_edit_password2.getText().toString();
				// String email=reg_edit_email.getText().toString();

				// check input are null
				if ((!username.trim().isEmpty())
						&& (!password.trim().isEmpty())
						&& (!password2.trim().isEmpty())) {
					// check passwords are the same
					if (password.compareTo(password2) == 0) {
						ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
						postParameters.add(new BasicNameValuePair("username",
								username));
						postParameters.add(new BasicNameValuePair("password",
								password));
						;
						// calling create new account
						// return true if successful, false for not been created
						String rs = "";
						rs = Create(postParameters);
						if (!rs.equals("exsisted")) {
							
							// do login for user
//							SharedPreferences mPerferences = PreferenceManager
//									.getDefaultSharedPreferences(POIMain.mContext);
//							SharedPreferences.Editor mEditor = mPerferences
//									.edit();
//
//							mEditor.putString("username", username);
//							mEditor.putString("userID", password);
//							mEditor.putBoolean("isLogined", true);// true is
//																	// logged
//							mEditor.commit();
							Toast.makeText(
									getApplicationContext(),
									"account has been created.\nPlease login now",
									Toast.LENGTH_LONG).show();
							finish();

						} else {
							// re input?
							Toast.makeText(getApplicationContext(),
									"account already existsed",
									Toast.LENGTH_SHORT).show();
							// clear input field for re-type
							reg_edit_password.setText("");
							reg_edit_password2.setText("");
							reg_edit_username.setText("");
							// reg_edit_email.setText("");
						}
					} else {
						Toast.makeText(getApplicationContext(),
								"the passwords are not the same",
								Toast.LENGTH_SHORT).show();
						reg_edit_password.setText("");
						reg_edit_password2.setText("");
					}
				} else {
					Toast.makeText(getApplicationContext(),
							"inputs are incorrect", Toast.LENGTH_SHORT).show();
					reg_edit_password.setText("");
					reg_edit_password2.setText("");
					reg_edit_username.setText("");
					// reg_edit_email.setText("");
				}
			}
		});
	}

	/**
	 * @param postParameters
	 * @return true if account has been account, or false for not created
	 */
	private String Create(ArrayList<NameValuePair> postParameters) {
		String url = POIMain.baseurl + "/createAccount";
		String rs = null;
		try {
			String response = CustomHttpClient.executeHttpPost(url,
					postParameters);
			Gson gson = new Gson();
			rs = gson.fromJson(response, String.class);
			// Log.i("response",response);
			// Log.i("response from gson", rs);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rs;
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
	}
	// this is a testing

}
