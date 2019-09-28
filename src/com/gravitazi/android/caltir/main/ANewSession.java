package com.gravitazi.android.caltir.main;

import com.gravitazi.android.caltir.R;
import com.gravitazi.android.caltir.R.id;
import com.gravitazi.android.caltir.R.layout;
import com.gravitazi.android.caltir.models.CDataProvider;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class ANewSession extends Activity {
	
	private static ANewSession mNewResto;
	
	public static ANewSession getInstance() {
		return mNewResto;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_resto);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		mNewResto = this;
//		getActionBar().setTitle("Resto Name");
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_new, menu);
		MenuItem menuItem = menu.findItem(R.id.idMenuAdd);
		menuItem.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_NEVER);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {			
			case android.R.id.home:
				finish();
		        return true;
			case R.id.idMenuCancel:
				finish();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void onNext(View v) {
		EditText e = (EditText) findViewById(R.id.idEditRestoName);
		String s = e.getText().toString().trim();		
		if(!s.equals("")) {
			CDataProvider.getInstance().setSessionName(e.getText().toString());
//			startActivity(new Intent(ANewResto.this, ANewCurr.class));
			startActivity(new Intent(ANewSession.this, ANewItem.class));
		} else {
			Toast.makeText(getApplicationContext(), "Please give a simple name of the session", Toast.LENGTH_SHORT).show();
		}
		
	}

}
