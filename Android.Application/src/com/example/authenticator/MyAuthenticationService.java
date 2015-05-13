package com.example.authenticator;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class MyAuthenticationService extends Service{
    private MyAuthenticator myAuthenticator;;

    
	@Override
	public void onCreate() {
		super.onCreate();
	    myAuthenticator = new MyAuthenticator(this);
	    Log.i("service", "service has been started");
	}

	public IBinder onBind(Intent arg0) {
		return myAuthenticator.getIBinder();
	}
}
