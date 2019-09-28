package com.gravitazi.android.caltir;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseInstallation;
import android.app.Application;

public class CApp extends Application {
	
	private final String PARSE_ID = "ygmyxNMHUN6N7ut6kA6Nw2lZ1kCtHCzaLgz2zSig";
	private final String PARSE_KEY = "wb8wgKLNS2gNmJvuTHPtosgbW0cCL3344c3Uq3Ui";
	public static final int USER_EXIST = 1;
	public static final int NO_USER_EXIST = 2;
	public static final int NO_INET_CONNECTION = 3;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		// Parse Settings...
		Parse.enableLocalDatastore(this);
		Parse.initialize(this, PARSE_ID, PARSE_KEY);
		ParseACL defaultACL = new ParseACL();
		defaultACL.setPublicReadAccess(true);
		defaultACL.setPublicWriteAccess(true);		
		ParseACL.setDefaultACL(defaultACL, true);
		// Save the current Installation to Parse.
//		String objid = ParseUser.getCurrentUser().getObjectId();
		ParseInstallation pi = ParseInstallation.getCurrentInstallation();	
//		if(objid != null) {
//			pi.put(CConstants.C_INSTAL_USERID, objid);
//		}
		pi.saveInBackground();
	}
}
