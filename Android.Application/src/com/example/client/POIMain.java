package com.example.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.database.DatabaseAdapter;
import com.example.utilizes.SpotBalloon;
import com.example.utilizes.UserUtilizes;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;

public class POIMain extends MapActivity {

	MapController mapController;
	private static myItemizedOverlay markers;
	public static Geocoder gc;
	private static MapView mapView;
	private static GeoPoint currentGeoPoint;// current point
	private static int Range = 50;

	private List<Address> foundAdresses = new ArrayList<Address>();
	private List<String> listAddress = new ArrayList<String>();
	public static final int INSERT_ID = 1;
	public static final int CHOSE_ID = 2;
	public static final int LOG_IN = 3;
	public static final int LOG_OUT = 4;

	public static final String baseurl = "http://10.1.1.21:8080/POI_service";
	public static final String url = baseurl + "AndroidResponse";

	public static Context mContext;
	public AutoCompleteTextView searchView;
	public static ArrayList<String> found;
	public static boolean finded;
	public static ArrayList<String> addedup;

	private static final Drawable orangeBalloon = new SpotBalloon(255, 255,
			140, 0);

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		mContext = this;
		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);

		if(isInternetOn()) {
			// INTERNET IS AVAILABLE, DO STUFF..
			Toast.makeText(this, "Wireless working", Toast.LENGTH_SHORT).show();
			}else{
			// NO INTERNET AVAILABLE, DO STUFF..
			Toast.makeText(this, "Turn On your wireless", Toast.LENGTH_SHORT).show();
			}
		
		mapController = mapView.getController();
		Drawable drawable = this.getResources().getDrawable(
				R.drawable.androidmarker);
		// List<Overlay> overlays = mapView.getOverlays();
		markers = new myItemizedOverlay(drawable, this, mapView);
		mapView.getOverlays().add(markers);
		settingLocation();// setting for GPS location and related
		loadPOIsfromDatabase(currentGeoPoint, Range);// with range

		gc = new Geocoder(this);

		// hacking the login widows for testing,
		 SharedPreferences mPerferences = PreferenceManager
		 .getDefaultSharedPreferences(this);
		 SharedPreferences.Editor mEditor = mPerferences.edit();
		 //
		 // // save name userID and state of login.
		 mEditor.putString("username", "INFO402");
		 mEditor.putInt("userID", (int) ("INFO401" + "password").hashCode());
		 mEditor.putBoolean("isLogined", true);// true is logged
		 mEditor.commit();
		// *********end of hacking

	}

	
	public final boolean isInternetOn() {
		ConnectivityManager connec =  (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		// ARE WE CONNECTED TO THE NET
		if ( connec.getNetworkInfo(0).getState() == State.CONNECTED ||
		connec.getNetworkInfo(0).getState() == State.CONNECTING ||
		connec.getNetworkInfo(1).getState() == State.CONNECTING ||
		connec.getNetworkInfo(1).getState() == State.CONNECTED ) {
		// MESSAGE TO SCREEN FOR TESTING (IF REQ)
		//Toast.makeText(this, connectionType + ” connected”, Toast.LENGTH_SHORT).show();
		return true;
		} else if ( connec.getNetworkInfo(0).getState() == State.DISCONNECTED ||  connec.getNetworkInfo(1).getState() == State.DISCONNECTED  ) {
		//System.out.println(“Not Connected”);
		return false;
		}
		return false;
		}

		
	
	public static void messger() {
		Toast.makeText(mContext, "Your Current Loation!", Toast.LENGTH_SHORT);
	}

	// load POIs from local database within range
	public static void loadPOIsfromDatabase(GeoPoint p, int range) {
		DatabaseAdapter dba = new DatabaseAdapter(mContext);
		dba.open();
		if (finded) {
			for (String poiid : found) {
				getPOIsFromCursor(dba.fetchAPOI(poiid), range, p);
			}
		} else {
			Cursor cur = dba.fetchPOIs();
			getPOIsFromCursor(cur, range, p);
		}
		dba.close();
		mapView.invalidate();
	}

	/**
	 * Adds loaded POIs to the overlay
	 * 
	 * @param cur
	 * @param range
	 * @param p
	 */
	public static void getPOIsFromCursor(Cursor cur, int range, GeoPoint p) {

		for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
			String poiid = cur.getString(cur.getColumnIndex("poiid"));
			String title = cur.getString(cur.getColumnIndex("title"));
			String des = cur.getString(cur.getColumnIndex("des"));
			int lat = cur.getInt(cur.getColumnIndex("lat"));
			int lng = cur.getInt(cur.getColumnIndex("lng"));
			long timestamp = cur.getLong(cur.getColumnIndex("updatetime"));
			String username = cur.getString(cur.getColumnIndex("username"));
			POI poi = new POI(poiid, title, des, lat, lng, timestamp, username);

			if ((poi.distanceto(p.getLatitudeE6(), p.getLongitudeE6())) < range) {
				markers.addOverlay(poi);
			}
		}
		// show your current location with different marker
		POI cp = new POI("poiid", "Your Location", "gps", p.getLatitudeE6(),
				p.getLongitudeE6(), 0, "owner");
		cp.setMarker(orangeBalloon);
		markers.addOverlay(cp);

		cur.close();
	}

	/**
	 * Saves POI?
	 * 
	 * @param poi
	 */
	public void savePOI(POI poi) {
		// save it to database
		DatabaseAdapter dba = new DatabaseAdapter(mContext);
		dba.open();
		dba.savePOItoDatabaseFromUI(poi);
		dba.close();
		Log.i("saved", poi.getTitle());
	}

	public static MapView getMapView() {
		return mapView;
	}

	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);

		if (resultCode == RESULT_OK) {
			Bundle bundle = intent.getExtras();
			if (bundle != null) {

			}
		}
	}

	/**
	 * Finds whether a user is logged in
	 * 
	 * @return whether logged in
	 */
	public static Boolean getStateLogin() {
		SharedPreferences mPerferences = PreferenceManager
				.getDefaultSharedPreferences(mContext);
		return mPerferences.getBoolean("isLogined", false);
	}

	/**
	 * Returns the username of the current user
	 * 
	 * @return username
	 */
	public static String getUserName() {
		SharedPreferences mPerferences = PreferenceManager
				.getDefaultSharedPreferences(mContext);
		return mPerferences.getString("username", "notlogined");

	}

	/**
	 * Gets the current GPS location
	 */
	private void settingLocation() {

		int LONG;
		int LAT;

		LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Location userLocation = null;
		List<String> providers = mlocManager.getProviders(true);
		for (int i = providers.size() - 1; i >= 0; i--) {
			userLocation = mlocManager.getLastKnownLocation(providers.get(i));
			if (userLocation != null) {
				break;
			}
		}

		LAT = (int) (userLocation.getLatitude() * 1E6);
		LONG = (int) (userLocation.getLongitude() * 1E6);

		currentGeoPoint = new GeoPoint(LAT, LONG);

		mapController.animateTo(currentGeoPoint);
		mapController.setCenter(currentGeoPoint);
		mapController.setZoom(14);
	}

	@Override
	protected void onResume() {
		// refresh();
		super.onResume();
		markers.clearall();
		loadPOIsfromDatabase(currentGeoPoint, Range);// with range
		settingLocation();
		mapView.invalidate();

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case INSERT_ID:
			if (!finded) { // can't do this when searching
				InputAddress();
			} else {
				markers.clearall();
				finded = false;
				loadPOIsfromDatabase(currentGeoPoint, Range);
			}
			break;
		case CHOSE_ID:
			Dialog d = choseRange();
			d.show();
			//choseRange();
			break;
		case LOG_IN:
			UserUtilizes utilizes = new UserUtilizes(POIMain.this);
			utilizes.Login();
			break;
		case LOG_OUT:
			UserUtilizes uti = new UserUtilizes(POIMain.this);
			uti.LogOut();
			// UserUtilizes.LogOut(POIMain.this);
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	//Changes the value of filter range value. Called by Dialog 
	public void changeRange(double range){
		if(range < 1){
			Toast msg = Toast.makeText(POIMain.this , "Thats too small. Keep it above 0km aye...", Toast.LENGTH_LONG);
			msg.setGravity(Gravity.CENTER, msg.getXOffset() / 2, msg.getYOffset() / 2);
			msg.show();
		} else if(range > 101){
			Toast msg = Toast.makeText(POIMain.this , "Thats too big. Keep it below 100km aye...", Toast.LENGTH_LONG);
			msg.setGravity(Gravity.CENTER, msg.getXOffset() / 2, msg.getYOffset() / 2);
			msg.show();
		} else {
			this.Range = (int)range;
			// refresh the overlay
			markers.clearall();
			loadPOIsfromDatabase(currentGeoPoint, Range);// with range												
			mapView.invalidate();
		}
	}
	
	public void badRangeInput(){
		Toast msg = Toast.makeText(POIMain.this , "That was not a valid range", Toast.LENGTH_LONG);
		msg.setGravity(Gravity.CENTER, msg.getXOffset() / 2, msg.getYOffset() / 2);
		msg.show();
	}
	
	//Creates dialog used to set the range to filter objects from
	private Dialog choseRange() {
	    final View layout = View.inflate(this, R.layout.rangedialog, null);

	    final EditText rangeEdit = ((EditText) layout.findViewById(R.id.myEditText));

	    AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    builder.setIcon(0);

	    builder.setTitle("Set Filter Range (km)");
	    builder.setPositiveButton("Save", new Dialog.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) {
	        	try {
	        		changeRange(Double.valueOf(rangeEdit.getText().toString()));
	        	} catch (Exception e) {
	        		badRangeInput();
	        		//Bad input received, range will remain the same
	        	}
	        }
	    });
	    builder.setView(layout);
	    return builder.create();
	 }

	/*
	 * Method for choose Range to show points
	 */
//	private void ChoseRange() {
//
//		final CharSequence[] items = { "2000", "5000", "10K" };
//
//		AlertDialog.Builder builder = new AlertDialog.Builder(this);
//		builder.setTitle("Pick a range");
//		builder.setSingleChoiceItems(items, -1,
//				new DialogInterface.OnClickListener() {
//					public void onClick(DialogInterface dialog, int item) {
//
//						// show the number of range choosed
//						Toast.makeText(getApplicationContext(), items[item],
//								Toast.LENGTH_SHORT).show();
//						if (item < 2) {
//							Range = Integer.valueOf(items[item].toString());
//						}
//						if (item > 2) {
//							Range = 10000;
//						}
//						// refresh the overlay
//						markers.clearall();
//						loadPOIsfromDatabase(currentGeoPoint, Range);// with
//																		// range
//						mapView.invalidate();
//
//						dialog.dismiss();
//						return;
//					}
//				});
//		builder.create().show();
//	}

	/*
	 * typing an physical address to add Point of interesting
	 */
	private void InputAddress() {

		SharedPreferences mPerferences = PreferenceManager
				.getDefaultSharedPreferences(mContext);
		boolean isLogined = mPerferences.getBoolean("isLogined", false);
		if (isLogined) {

			final AlertDialog.Builder alert = new AlertDialog.Builder(this);
			final EditText input = new EditText(this);
			alert.setTitle("Please Input Address");
			alert.setView(input);

			alert.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {

							String value = input.getText().toString().trim();
							if (value.length() != 0) {
								foundAddress(value);
								if (!listAddress.isEmpty()) {
									SelectAddress();
								}
							}
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

		} else {
			AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
			alert.setMessage("To create a new POI.\nYou have to login");

			alert.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							dialog.dismiss();
						}
					});
			alert.show();
		}
	}

	/*
	 * confirm the address found
	 */
	private void SelectAddress() {

		CharSequence[] cs = listAddress.toArray(new CharSequence[listAddress
				.size()]);

		final CharSequence[] items = cs;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Pick an address you want");
		builder.setItems(items, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialogInterface, int item) {

				// calling the adding intent activity
				Intent intent = new Intent(POIMain.this, IntentA.class);
				intent.putExtra("lat", (int) (foundAdresses.get(0)
						.getLatitude() * 1E6));
				intent.putExtra("lng", (int) (foundAdresses.get(0)
						.getLongitude() * 1E6));
				intent.putExtra("address", (String) items[item]);
				UUID uuid = UUID.randomUUID();
				intent.putExtra("poiid", uuid.toString());
				intent.putExtra("actionKeys", "create");// action= create,
														// update,
														// delete,getPointsInRange
				POIMain.this.startActivity(intent);

				return;
			}
		});
		builder.create().show();
	}

	/*
	 * found address inputed from
	 */
	private void foundAddress(String address) {

		try {
			foundAdresses = gc.getFromLocationName(address, 1);
			if (!foundAdresses.isEmpty()) {

				Address x = foundAdresses.get(0);
				StringBuilder sb = new StringBuilder();
				for (int j = 0; j < x.getMaxAddressLineIndex(); j++)
					sb.append(x.getAddressLine(j)).append("\n");
				sb.append(x.getLocality()).append("\n");
				// sb.append(address.getPostalCode()).append("\n");
				sb.append(x.getCountryName());
				// save the address to list
				listAddress.clear();
				listAddress.add(sb.toString());
			} else {
				AlertDialog.Builder dialog = new AlertDialog.Builder(this);
				dialog.setTitle("ERROR");
				dialog.setMessage("There is no address found!");
				dialog.show();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * if the search home key is pressed, start searching activity/dialog
	 */
	@Override
	public boolean onSearchRequested() {

		final Dialog dialog = new Dialog(mContext);
		dialog.setContentView(R.layout.searchable);
		dialog.setTitle("Search");
		dialog.setCancelable(true);

		// setting a image view
		searchView = (AutoCompleteTextView) dialog
				.findViewById(R.id.searchentered);

		Button find = (Button) dialog.findViewById(R.id.searchit);

		// set up the autocomplete text box
		addedup = new ArrayList<String>();

		DatabaseAdapter dba = new DatabaseAdapter(mContext);
		dba.open();
		dba.fetchTags(addedup, dba.fetchAllTags(), "content");
		dba.close();

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				R.layout.list_item, addedup);

		searchView.setAdapter(adapter);

		// when you click the button to search
		find.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				markers.clearall();
				DatabaseAdapter dba = new DatabaseAdapter(mContext);
				found = new ArrayList<String>();
				dba.open();
				String tosearch = searchView.getText().toString();
				if (!tosearch.equals("")) { // if the search is void
					if (addedup.contains(tosearch)) {
						String tagided = dba.fetchAllTagid(tosearch);
						dba.fetchTags(found,
								dba.fetchAllPOIsForOneTag(tagided), "poiid");
					}
					dba.fetchTags(found, dba.searchit(tosearch), "poiid");
				} else { // empty or null search
					AlertDialog.Builder dialog = new AlertDialog.Builder(
							mContext);
					dialog.setTitle("Sorry");
					dialog.setMessage("No items were found");
					dialog.show();
				}
				finded = true;
				dba.close();
				loadPOIsfromDatabase(currentGeoPoint, Range);
				dialog.dismiss();
			}

		});

		dialog.show();

		return true;
	}

	/**
	 * what to label the buttons in the main menu
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		menu.clear();
		SharedPreferences mPerferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		boolean isLogined = mPerferences.getBoolean("isLogined", false);
		if (finded) {
			menu.add(0, INSERT_ID, 0, R.string.stop_search);// change label of
															// button while
															// searching
		} else {
			menu.add(0, INSERT_ID, 0, R.string.menu_insert);// for adding
															// address
		}
		menu.add(0, CHOSE_ID, 0, R.string.menu_chose);// for chose the range
		if (!isLogined) {
			menu.add(0, LOG_IN, 3, R.string.login);
		} else {
			menu.add(0, LOG_OUT, 4, R.string.menu_logout);// for log out
		}
		return super.onPrepareOptionsMenu(menu);
	}

}