package com.gravitazi.android.caltir.main;

import com.gravitazi.android.caltir.R;
import com.gravitazi.android.caltir.R.id;
import com.gravitazi.android.caltir.R.layout;
import com.parse.ParseUser;
import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class AProfile extends Activity {
	
	private EditText mEdName;
	private String mName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		mEdName = (EditText) findViewById(R.id.idEditName);		
		mName = ParseUser.getCurrentUser().getString("name");
		mEdName.setText(mName);
		EditText em = (EditText) findViewById(R.id.idEditEmail);
		em.setText(ParseUser.getCurrentUser().getEmail());
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {			
			case android.R.id.home:
				finish();
		        return true;		    				
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void onSave (View v) {
		String s = mEdName.getText().toString().trim();
		if(!s.equals(mName)) {
			ParseUser user = ParseUser.getCurrentUser();
			user.put("name", s);
			user.saveInBackground();
		}
		finish();		
	}

}
