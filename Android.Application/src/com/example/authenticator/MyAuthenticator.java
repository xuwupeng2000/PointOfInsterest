package com.example.authenticator;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.SyncStateContract.Constants;
import android.util.Log;

public class MyAuthenticator extends AbstractAccountAuthenticator {
	private Context ctx = null; 
	public MyAuthenticator(Context ctx) {
		super(ctx);
		this.ctx = ctx;
	}

	public Bundle addAccount(AccountAuthenticatorResponse response,
			String accountType, String authTokenType,
			String[] requiredFeatures, Bundle options)
			throws NetworkErrorException {
		  Log.i("add", "try to add");
		  final Bundle result;  
		  final Intent intent;  
		  intent = new Intent(ctx, MyAuthenticationActivity.class);  
		  intent.putExtra(Constants.ACCOUNT_TYPE, authTokenType);  
		  intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);  
		  result = new Bundle();  
		  result.putParcelable(AccountManager.KEY_INTENT, intent);  
		  return result;  
	}

	public Bundle confirmCredentials(AccountAuthenticatorResponse response,
			Account account, Bundle options) throws NetworkErrorException {
		return null;
	}

	public Bundle editProperties(AccountAuthenticatorResponse response,
			String accountType) {
		return null;
	}

	public Bundle getAuthToken(AccountAuthenticatorResponse response,
			Account account, String authTokenType, Bundle options)
			throws NetworkErrorException {
		return null;
	}

	public String getAuthTokenLabel(String authTokenType) {
		return null;
	}

	public Bundle hasFeatures(AccountAuthenticatorResponse response,
			Account account, String[] features) throws NetworkErrorException {
		return null;
	}

	public Bundle updateCredentials(AccountAuthenticatorResponse response,
			Account account, String authTokenType, Bundle options)
			throws NetworkErrorException {
		return null;
	}

}
