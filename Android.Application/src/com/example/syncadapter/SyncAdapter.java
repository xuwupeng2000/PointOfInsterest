package com.example.syncadapter;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.client.POIMain;
import com.example.database.DatabaseAdapter;



public class SyncAdapter extends AbstractThreadedSyncAdapter {
    private final Context ctx;
    private final String UpdatePOIsURI = POIMain.baseurl+"/UPOI";
    private final String DownloadPOIsURI = POIMain.baseurl + "/DPOI";
    private final String UpdateComsURI = POIMain.baseurl + "/UCOM";
    private final String DownloadComsURI =POIMain.baseurl + "/DCOM";
    private final String DownloadTagsURI =POIMain.baseurl + "/DTAG";
    private final String UpdateTagsURI = POIMain.baseurl + "/UTAG";
    private final String DownloadRatesURI =POIMain.baseurl + "/DRAT";
    private final String UpdateRatesURI = POIMain.baseurl + "/URAT";
    private final String DownloadPicsURI =POIMain.baseurl + "/DPIC";
    private final String UpdatePicsURI = POIMain.baseurl + "/UPIC";
    private final String UpdateKeysURI = POIMain.baseurl + "/UKEY";
    private final String DownloadKeysURI = POIMain.baseurl + "/DKEY";
    
    
    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        ctx = context;
    }
    
    public void onPerformSync(Account account, Bundle extras, String authority,ContentProviderClient provider, SyncResult syncResult) {
    	SyncingHelper sh = new SyncingHelper();
    	DatabaseAdapter dba = new DatabaseAdapter(ctx);
    	dba.open();
    	
    	//get lasttime
		long lasttime;
		lasttime = dba.fetchLastUpdateTime();
		Log.i("syn", "lasttime in syn = "+ Long.toString(lasttime));

		//update POIs
    	try {
    		Cursor cur = dba.fetchNewPOIs(lasttime);
			sh.updatePOIs(cur, UpdatePOIsURI);
		} catch (Exception e) {
			Toast.makeText(ctx, "Server not available or not working properly when updating new POIs", Toast.LENGTH_SHORT);
			e.printStackTrace();
		}
    	//download new POIs as JSON String
		try {
			String poiList = sh.downloadPOIs(lasttime, DownloadPOIsURI);
			//after these update and download we need setup a new last time
			//using the poiList JSON string, insert new POIs into database. if it is new we insert if it is old we update
			if(!poiList.equalsIgnoreCase("nothing")){
				Log.i("syn", "this is the poiList return from server "+poiList);
				dba.savePOItoDatabaseFromJSONString(poiList);
			}
			else{
				Log.i("syn", "nothing new in ths server");
			}
			//remove deleted poi and revelant info from local database
			dba.cleanDeletedPOIInfoOnLocalDatabase();
		} catch (Exception e) {
			Toast.makeText(ctx, "Server not available or not working properly when downloading new POIs", Toast.LENGTH_SHORT);
			e.printStackTrace();
		}
		
		//update coms
		try{
			Cursor cur = dba.fetchNewComs(lasttime);
			sh.updateComs(cur, UpdateComsURI);
		}catch(Exception e){
			Toast.makeText(ctx, "Server not available or not working properly wehn updating comments", Toast.LENGTH_SHORT);
			e.printStackTrace();
		}
		
		//download coms
		try{
			String comsList = sh.downloadComs(lasttime, DownloadComsURI);
			if(!comsList.equalsIgnoreCase("nothing") ){
				Log.i("syn", comsList);
				dba.saveComsToDatabaseFromJSONString(comsList);
			}
		}catch(Exception e){
			Toast.makeText(ctx, "Server not available or not working properly wehn downloading comments", Toast.LENGTH_SHORT);
			e.printStackTrace();
		}
		
		//update tags
		try{
			Cursor cur = dba.fetchNewTags(lasttime);
			sh.updateTags(cur, UpdateTagsURI);
		}catch(Exception e){
			Toast.makeText(ctx, "Server not available or not working properly wehn updating tags", Toast.LENGTH_SHORT);
			e.printStackTrace();
		}
		
		//download tags
		try{
			String tagsList = sh.downloadTags(lasttime, DownloadTagsURI);
			if(!tagsList.equalsIgnoreCase("nothing")){
				Log.i("syn", tagsList);
				dba.saveTagsToDatabaseFromJSONString(tagsList);
			}
		}catch(Exception e){
			Toast.makeText(ctx, "Server not available or not working properly when downloading tags", Toast.LENGTH_SHORT);
			e.printStackTrace();
		}
		
		
		//update keys
			try{
				Cursor cur = dba.fetchNewKeys(lasttime);
				sh.updateKeys(cur, UpdateKeysURI);
			}catch(Exception e){
				Toast.makeText(ctx, "Server not available or not working properly when updating keys", Toast.LENGTH_SHORT);
				e.printStackTrace();
			}
			
			//download keys
			try{
				String keysList = sh.downloadTags(lasttime, DownloadKeysURI);
				if(!keysList.equalsIgnoreCase("nothing")){
					Log.i("syn", keysList);
					dba.saveKeysToDatabaseFromJSONString(keysList);
				}
			}catch(Exception e){
				Toast.makeText(ctx, "Server not available or not working properly when downloading keys", Toast.LENGTH_SHORT);
				e.printStackTrace();
			}
		
		//update pics
		try{
			Cursor cur = dba.fetchNewPhotos(lasttime);
			sh.updatePhotos(cur, UpdatePicsURI);
		}catch(Exception e){
			Toast.makeText(ctx, "Server not available or not working properly when updating photos", Toast.LENGTH_SHORT);
			e.printStackTrace();
		}
		
		//download pics
		try{
			String picsList = sh.downloadPic(lasttime, DownloadPicsURI);
			if(!picsList.equalsIgnoreCase("nothing")){
				Log.i("pic","this is the pics List download from server "+ picsList);
				dba.svaePicToDatabaseFromJSONString(picsList);
			}
		}catch(Exception e){
			Toast.makeText(ctx, "Server not available or not working properly when downloading photos", Toast.LENGTH_SHORT);
			e.printStackTrace();
		}
		
		//update rates
		try{
			Cursor cur = dba.fetchNewRates(lasttime);
			sh.updateRates(cur, UpdateRatesURI);
		}catch(Exception e){
			Toast.makeText(ctx, "Server not available or not working properly when updating rates", Toast.LENGTH_SHORT);
			e.printStackTrace();
		}
		
		//download rates
		try{
			String ratesList = sh.downloadRates(lasttime, DownloadRatesURI);
			if(!ratesList.equalsIgnoreCase("nothing")){
				Log.i("syn", ratesList);
				dba.saveRatesToDatabaseFromJSONString(ratesList);
			}
		}catch(Exception e){
			Toast.makeText(ctx, "Server not available or not working properly when downloading rates", Toast.LENGTH_SHORT);
			e.printStackTrace();
		}
		
	
//		dba.setNewLastUpdateTIme();
		dba.close();
    	Log.i("syn", "syncing... finish!");
        } 
    
}
