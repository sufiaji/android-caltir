package com.gravitazi.android.caltir.main;

import com.gravitazi.android.caltir.R;
import com.gravitazi.android.caltir.R.id;
import com.gravitazi.android.caltir.R.layout;
import com.gravitazi.android.caltir.models.CConstants;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

public class ALogin extends Activity {
	
	private ProgressBar mPb;
	private EditText mEmail;
	public final String PASSWORD_STRING = "1234";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_login);
		mPb = (ProgressBar) findViewById(R.id.pbLogin);
		mPb.setVisibility(View.INVISIBLE);
		mEmail = (EditText) findViewById(R.id.editEmail);
	}
	
	public void onLogin(View v) {
		mPb.setVisibility(View.VISIBLE);
		// validation		
		final String emailStr = mEmail.getText().toString().trim();	
		final String passStr = emailStr + PASSWORD_STRING;
		if(emailStr.isEmpty()) {
			showError("Email cannot be empty");
			return;
		}
		if(emailStr.contains(" ") || !emailStr.contains("@")) {
			showError("Email not valid");
			return;
		}
		// try to signup
		ParseUser _user = new ParseUser();
		_user.setUsername(emailStr);
		_user.setEmail(emailStr);
		_user.setPassword(passStr);
		_user.put("name", emailStr);
		_user.signUpInBackground(new SignUpCallback() {

			@Override
			public void done(ParseException e) {
				if(e != null && ( e.getCode() == ParseException.USERNAME_TAKEN || e.getCode() == ParseException.EMAIL_TAKEN)) {
					login(emailStr, passStr);
				} else if(e == null) {
					login(emailStr, passStr);
				} else {
					showError("Unknown error occured.");
				}
			}
			
		});
		
	}

	protected void showError(String msg) {
		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
		mPb.setVisibility(View.INVISIBLE);
	}

	protected void login(String email, String password) {
		ParseUser.logInInBackground(email, password, new LogInCallback() {

			@Override
			public void done(ParseUser user, ParseException e) {
				if(e == null) {
					saveInstallation(user.getObjectId());
					callMainPage();
				} else 
					showError("Unknown error occured.");
			}
			
		});
		
	}

	protected void saveInstallation(String objid) {
		ParseInstallation pi = ParseInstallation.getCurrentInstallation();
		pi.put(CConstants.C_INSTAL_USERID, objid);
		pi.saveInBackground();
	}

	protected void callMainPage() {
		mPb.setVisibility(View.INVISIBLE);
		Intent i = new Intent(ALogin.this, AMain.class);
		startActivity(i);
		finish();
	}

}
