package com.example.client;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix.ScaleToFit;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.example.client.R;
import com.example.database.DatabaseAdapter;
import com.example.client.POI;

/********************************
 * This class is responsible for displaying POIs on the mapView, and for
 * handling clicks on the screen, and for displaying a limited display of the
 * POI selected.
 */
public class myItemizedOverlay extends ItemizedOverlay<OverlayItem> implements
		GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {

	private ArrayList<POI> mOverlays = new ArrayList<POI>();
	Context mContext;

	MapView mapView;
	boolean started = false; // whether double click event handling has started
	float x;
	float y;
	long starttime; // start time for double-click event
	int count2;
	private DatabaseAdapter dba;
	private GestureDetector gesturedetector;

	public myItemizedOverlay(Drawable defaultMarker, Context context,
			MapView mpView) {
		super(boundCenterBottom(defaultMarker));
		mOverlays = new ArrayList<POI>();
		mContext = context;
		mapView = mpView;
		gesturedetector = new GestureDetector(mapView.getContext(), this);
		populate();
	}

	public void addOverlay(POI overlay) {
		mOverlays.add(overlay);
		populate();
	}

	public void addOverlay(GeoPoint location, String title, String body) {
		mOverlays.add((com.example.client.POI) new OverlayItem(location, title,
				body));
		populate();
	}

	@Override
	protected OverlayItem createItem(int i) {
		// TODO Auto-generated method stub
		return mOverlays.get(i);
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return mOverlays.size();
	}

	@Override
	protected boolean onTap(int index) {

		// final OverlayItem item = mOverlays.get(index);
		final POI poi = mOverlays.get(index);
		final String poiid = poi.getPoiid();
		final String username = POIMain.getUserName();
		// set up a displaying dialog

		final Dialog dialog = new Dialog(mContext);
		dialog.setContentView(R.layout.displaying);
		dialog.getWindow().setLayout(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);
		dialog.setTitle(poi.getTitle());
		dialog.setCancelable(true);

		// setting a image view
		ImageView img = (ImageView) dialog.findViewById(R.id.ImageView01);

		// average rating bar stuff
		RatingBar aveBar = (RatingBar) dialog
				.findViewById(R.id.averageratingbar);
		double aveRate = loadOldRates(poiid);
		aveBar.setRating((float) aveRate);

		TextView text = (TextView) dialog.findViewById(R.id.TextView01);
		text.setText(poi.getSnippet());

		//
		// to set the picture for the displaying box
		//
		File folder = new File(Environment.getExternalStorageDirectory()
				+ "/gallery_images2");
		if (folder.exists()) {
			String[] children = folder.list();
			for (int i = 0; i < children.length; i++) {
				new File(folder, children[i]).delete();
			}
		} else {
			folder.mkdir();
		}
		dba = new DatabaseAdapter(mContext);
		dba.open();

		img.setImageResource(R.drawable.noimg);
		img.setScaleType(ScaleType.CENTER);
		Cursor cur = dba.fetchPicFromLocalDatabase(poiid);
		
		for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
			String getpicid = cur.getString(cur.getColumnIndex("picid"));
			String getpicString = cur.getString(cur.getColumnIndex("bigpic"));

			if (!getpicString.equals("null")) {

				byte[] gzipBuff;
				try {
					gzipBuff = Base64.decode(getpicString.getBytes());
					ByteArrayOutputStream baos = new ByteArrayOutputStream(
							gzipBuff.length);
					baos.write(gzipBuff);
					File d = new File(Environment.getExternalStorageDirectory()
							+ "/gallery_images2/" + getpicid + ".jpg");
					FileOutputStream wo = new FileOutputStream(d);
					wo.write(baos.toByteArray());
					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inSampleSize = 3;
					Bitmap bitmap = BitmapFactory.decodeFile(
							Environment.getExternalStorageDirectory()
									+ "/gallery_images2/" + getpicid + ".jpg",
							options);
					img.setImageBitmap(bitmap);
					break;

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();

				}

			} else {
				img.setImageResource(R.drawable.noimg);
			}
		}// end image stuff
		dba.close();

		Button button_comment = (Button) dialog
				.findViewById(R.id.display_button_comment);

		button_comment.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				Intent intent = new Intent(mContext, CommentActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("poiid", poiid);
				bundle.putString("username", username);
				intent.putExtras(bundle);
				mContext.startActivity(intent);
			}

		});

		// for clicking on the image
		img.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.putExtra("poiid", poiid.toString());
				intent.setClass(mContext, GalleryActivity.class);
				intent.putExtra("viewing", true);
				mContext.startActivity(intent);
			}
		});

		// button do update
		Button button_update = (Button) dialog
				.findViewById(R.id.display_bt_updata);
		button_update.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				SharedPreferences mPerferences = PreferenceManager
						.getDefaultSharedPreferences(mContext);
				String name = mPerferences.getString("username", "notlogined");
				if (name == username) {
					GeoPoint p = poi.getPoint();
					int lat = p.getLatitudeE6();
					int lng = p.getLongitudeE6();
					// get details about this point from location arraylist
					// send to IntenA activity.
					Intent intent = new Intent(mContext, IntentA.class);
					intent.putExtra("lat", lat);
					intent.putExtra("lng", lng);
					intent.putExtra("body", poi.getSnippet());
					intent.putExtra("title", poi.getTitle());
					intent.putExtra("poiid", poi.getPoiid());
					intent.putExtra("actionKeys", "update");// action= create,
															// update,
															// delete,getPointsInRange
					mContext.startActivity(intent);
					dialog.dismiss();
				} else {
					AlertDialog.Builder alert = new AlertDialog.Builder(
							mContext);
					alert.setMessage("To edit.\nYou have to login");

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
		});
		// not allow to edit if not logined.
		if (!POIMain.getStateLogin()) {
			button_update.setEnabled(false);
			button_comment.setText("See Comments");
		}
		if (poi.getPoiid().equals("poiid")) {
			Toast.makeText(mContext, "Your Current Loation!",
					Toast.LENGTH_SHORT).show();
		} else {
			dialog.show();
		}
		populate();
		return true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event, MapView mapView) {

		return gesturedetector.onTouchEvent(event);
		// return false;
	}

	/******************
	 * clear items in overlays and for reloading
	 */
	public void clearall() {
		mOverlays = null;
		mOverlays = new ArrayList<POI>();
		setLastFocusedIndex(-1);
	}

	/******************
	 * returns the average rating of all users for a POI
	 */
	private int loadOldRates(String poiid) {
		dba = new DatabaseAdapter(mContext);
		dba.open();
		Cursor cur = dba.fatchOldRates(poiid);
		int aveRate = 0;
		int sumOfRate = 0;
		int rateCount = 1;
		if (cur.moveToFirst()) {
			sumOfRate = 0;
			rateCount = 0;
			for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
				int displayOldRate = cur.getInt(cur.getColumnIndex("rate"));
				rateCount++;
				sumOfRate = displayOldRate + sumOfRate;
			}
			aveRate = Math.round(sumOfRate / rateCount);
		}
		cur.close();
		dba.close();
		return aveRate;
	}


	/******************
	 * On double-click, create a new POI.
	 */
	@Override
	public boolean onDoubleTap(MotionEvent arg0) {
		// TODO Auto-generated method stub
		if (!POIMain.finded) {
			SharedPreferences mPerferences = PreferenceManager
					.getDefaultSharedPreferences(mContext);
			boolean isLogined = mPerferences.getBoolean("isLogined", false);
			if (isLogined) {
				GeoPoint p = mapView.getProjection().fromPixels(
						(int) arg0.getX(), (int) arg0.getY());
				Intent intent = new Intent(mContext, IntentA.class);
				// String address = Functions.getaddress(p);
				intent.putExtra("lat", p.getLatitudeE6());
				intent.putExtra("lng", p.getLongitudeE6());
				UUID uuid = UUID.randomUUID();
				intent.putExtra("poiid", uuid.toString());
				// intent.putExtra("address", address);
				intent.putExtra("actionKeys", "create");
				mContext.startActivity(intent);
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
		return true;

	}

	@Override
	public boolean onDoubleTapEvent(MotionEvent e) {

		return true;
	}

	@Override
	public boolean onSingleTapConfirmed(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onDown(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}
}

