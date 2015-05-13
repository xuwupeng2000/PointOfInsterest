package com.example.client;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;

import com.example.database.DatabaseAdapter;
import com.example.client.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.Toast;

public class IntentA extends Activity {

	private EditText mTitleText;
	private EditText mBodyText;
	AutoCompleteTextView textView;
	TextView tagtext;

	private int geolat = 0;
	private int geolng = 0;
	private String body;
	private String title;
	private String address;
	private String actionKeys = "action";
	private String poiid;
	private float newRate = 0;
	private float oldRate = -1;
	private boolean newish = false;
	private DatabaseAdapter dba;
	private ArrayList<String> all;
	private ArrayList<String> addedid;
	private ArrayList<String> added;
	private ArrayList<String> comp;
	
	Button buttonConfirm;
	Button buttonAddpic; 
	Button buttonDelete;
	Button buttonAddTag; 
	Button buttonDeleteTag;

	// private TextView geoText, addressText;
	private Context ctx;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.intenta);
		ctx = this;
		dba = new DatabaseAdapter(ctx);
		all = new ArrayList<String>();
		added = new ArrayList<String>();
		addedid = new ArrayList<String>();
		comp = new ArrayList<String>();

		Bundle extras = getIntent().getExtras();
		geolat = extras.getInt("lat");
		geolng = extras.getInt("lng");
		poiid = extras.getString("poiid");
		// address = extras.getString("address");
		body = extras.getString("body");
		title = extras.getString("title");
		actionKeys = extras.getString("actionKeys");// either create or update

		mTitleText = (EditText) findViewById(R.id.title);
		mBodyText = (EditText) findViewById(R.id.body);
		RatingBar ratingBar = (RatingBar) findViewById(R.id.yourratingbar);

		loadRate(poiid, getUserName());

		// set rating bar for user
		ratingBar.setRating((float) oldRate);

		ratingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {

			public void onRatingChanged(RatingBar bar, float rate,
					boolean fromUser) {
				newRate = (float) rate;
				newish = true;
			}

		});

		/////////////// tagging stuff
		dba.open();
		dba.fetchTags(all, dba.fetchAllTags(), "content");
		dba.fetchTags(comp, dba.fetchAllTagIds(), "tagid");
		dba.fetchTags(addedid, dba.fetchAllTagsForOnePOI(poiid), "tagid");
		for (String y : addedid) {
			dba.fetchTags(added, dba.fetchAllTags(y), "content");
		}

		dba.close();

		tagtext = (TextView) findViewById(R.id.tagview);
		tagtext.setText("");
		fillText();
		
		// add all current tags to autocomplete text box
		textView = (AutoCompleteTextView) findViewById(R.id.tagenter);
		String[] addedup = new String[all.size()];
		for (String g : all) {
			addedup[all.indexOf(g)] = g;
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				R.layout.list_item, all);
		textView.setAdapter(adapter);
        ///////////////////////////

		if (body != null) {
			mBodyText.setText(body);
		}
		if (title != null) {
			mTitleText.setText(title);
		}

		buttonConfirm = (Button) findViewById(R.id.confirmbtn);
		buttonAddpic = (Button) findViewById(R.id.addpicbtn);
		buttonAddpic.setText("Photo");
		buttonDelete = (Button) findViewById(R.id.deletebtn);
		buttonAddTag = (Button) findViewById(R.id.addtag);
		buttonDeleteTag = (Button) findViewById(R.id.deletetag);
		
		buttonAddpic.setEnabled(false);
		buttonDelete.setEnabled(false);
		mTitleText.setEnabled(false);
		mBodyText.setEnabled(false);
		buttonDeleteTag.setEnabled(false);

		// Toast.makeText(this, "poiid"+" \n "+ String.valueOf(poiid),
		// Toast.LENGTH_LONG).show();

		// comparing owners
		if (compareowner(poiid)) {
			buttonAddpic.setEnabled(true);
			buttonDelete.setEnabled(true);
			mTitleText.setEnabled(true);
			mBodyText.setEnabled(true);
		}
		if (actionKeys.equals("create")) {
			buttonAddpic.setEnabled(false);
			buttonDelete.setEnabled(true);
			mTitleText.setEnabled(true);
			mBodyText.setEnabled(true);
			buttonDelete.setText("Cancel");
		}

		// update/create button stuff
		buttonConfirm.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {

				title = mTitleText.getText().toString(); // extract title and
															// body
				body = mBodyText.getText().toString(); // extract title and body
				// sending to serve
				// if keys=create then create new
				// if keys=update then update point
				if (title.equals("") || body.equals("")) {
					Toast toast = Toast.makeText(
							getBaseContext(), // ... using toast pop-up
							"Title or Body\ncannot be empty",
							Toast.LENGTH_LONG);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.setDuration(1000);
					toast.show();

				} else {
				dba.open();

				// update rating
				if (newish) {
					dba.ratePOI((int) newRate, poiid, getUserName());
				}

				// update tags
				int index;
				for (String y : added) {
					if (!all.contains(y)) {
						dba.addNewKey(y, poiid);
						dba.addNewTag(poiid + y, poiid);
					} else {
						index = all.indexOf(y);
						if (!addedid.contains(comp.get(index))) {
							dba.addNewTag(comp.get(index), poiid);
						}
					}
				}
				// creating a new POI
				if (actionKeys.equals("create")) {
					String username = getUserName();
					long timestamp = Calendar.getInstance().getTimeInMillis();
		
						POI poi = new POI(poiid, title, body, geolat, geolng,
								timestamp, username);
						dba.savePOItoDatabaseFromUI(poi);
						dba.close();
						//adding pic confirm dialog
						if(!buttonAddpic.isEnabled()){
						buttonAddpic.setEnabled(true);
						AlertDialog.Builder alert = new AlertDialog.Builder(ctx);
						alert.setMessage("Do you want to add pic now?");

						alert.setPositiveButton("Yes",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
										Intent intent = new Intent();
										intent.putExtra("poiid", poiid.toString());
										intent.putExtra("username", getUserName());
										intent.setClass(IntentA.this, GalleryActivity.class);
										startActivityForResult(intent, 0);
									}
								});
						alert.setNegativeButton("No",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
										dialog.cancel();
										finish();										
									}
								});						
							alert.show();
						}else{
							finish();
						}
				}
				// updating a POI
				if (actionKeys.equals("update")) {
					dba.updateDesAndTitleOfPOIinLocalDatabase(title, body,
							poiid);

					dba.close();
					finish();
				}
				}
			}
		});

		// delete POI button stuff
		buttonDelete.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {

				AlertDialog.Builder alert = new AlertDialog.Builder(ctx);
				alert.setMessage("Do you want to delete");

				alert.setPositiveButton("Ok",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								DatabaseAdapter dba = new DatabaseAdapter(ctx);
								dba.open();
								dba.deletePOIofLocalDatabase(poiid);
								dba.close();
								finish();
							}
						});
				alert.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								dialog.cancel();
							}
						});
				alert.show();

			}

		});

		// add pic button stuff
		buttonAddpic.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.putExtra("poiid", poiid.toString());
				intent.putExtra("username", getUserName());
				intent.setClass(IntentA.this, GalleryActivity.class);
				startActivityForResult(intent, 0);
			}

		});

		// tag add button stuff
		buttonAddTag.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				if (!textView.getText().toString().equals("")) {
					if(!added.contains(textView.getText().toString()))
					added.add(textView.getText().toString());
					textView.setText("");
				}
				fillText();
			    buttonDeleteTag.setEnabled(true);		
			}
		});
		
		// tag delete button stuff
		buttonDeleteTag.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				deleteLastTag();
				if(added.size() <= addedid.size()){
					buttonDeleteTag.setEnabled(false);
				}
			}
		});
	}

	/**
	 * displays the current tags for the POI in the tag taxtbox
	 */
	public void fillText() {
		HashSet<String> hs = new HashSet<String>();
		hs.addAll(added);
		added.clear();
		added.addAll(hs);

		String newone = "";
		for (String f : added) {
			newone = newone + ", " + f;
		}
		if (newone.length() > 2) {
			tagtext.setText(newone.substring(2));
		} else {
			tagtext.setText(newone);
		}
		
	}
	
	/**
	 * deletes the last tag entered by the user
	 */
	public void deleteLastTag() {
		if(added.size() > addedid.size()){
			added.remove(added.size() - 1);
		}
		fillText();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		// saveState();
	}

	/**
	 * Gets the current user's username
	 * @return username
	 */
	public String getUserName() {
		// TODO adding the code for reading username form perfermance job
		return (POIMain.getUserName());
	}

	/**
	 * loads the current rate of the user for the POI being edited.
	 * If the user has not rated it yet, rating = 0
	 * @param poiid
	 * @param user
	 */
	private void loadRate(String poiid, String user) {
		dba.open();
		Cursor cur = dba.fetchRate(poiid, user);
		if (cur.moveToFirst()) {
			oldRate = cur.getInt(cur.getColumnIndex("rate"));
		}
		if (oldRate == -1)
			oldRate = 0;
		cur.close();
		dba.close();
	}

	/**
	 * Checks to see whether a POI is owned by the curent user
	 * @param poiid
	 * @return boolean of whether POI is owned by current user
	 */
	private boolean compareowner(String poiid) {
		DatabaseAdapter dba = new DatabaseAdapter(ctx);
		dba.open();
		boolean rs = false;
		// Log.i("DDDDDD","klfjhadflkja");
		Log.i("current owner", getUserName());
		Log.i("POI's owner", dba.fetchNameByPoiid(poiid));
		if (getUserName().equals(dba.fetchNameByPoiid(poiid))) {
			rs = true;
		}
		dba.close();
		return rs;
	}
	
	

}
