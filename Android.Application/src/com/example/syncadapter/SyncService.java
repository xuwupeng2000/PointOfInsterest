package com.example.syncadapter;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class SyncService extends Service {
    private static final Object mySyncAdapterLock = new Object();//what is that?
    private static SyncAdapter mySyncAdapter = null;

    public void onCreate() {
        Log.i("syn", "syncing service is created");

        synchronized (mySyncAdapterLock) {
            if (mySyncAdapter == null) {
                mySyncAdapter = new SyncAdapter(getApplicationContext(), true);
                Log.i("syn", "i have created a adpter");
            }
        }
    }

	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
        Log.e("syn", "syncing service is started");
	}

    public IBinder onBind(Intent intent) {
        return mySyncAdapter.getSyncAdapterBinder();
    }
}
