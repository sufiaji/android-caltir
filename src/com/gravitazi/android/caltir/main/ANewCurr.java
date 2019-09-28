package com.gravitazi.android.caltir.main;

import com.gravitazi.android.caltir.R;
import com.gravitazi.android.caltir.R.id;
import com.gravitazi.android.caltir.R.layout;
import com.gravitazi.android.caltir.models.CDataProvider;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class ANewCurr extends Activity {
	
	private static ANewCurr mNewCurr;
	
	public static ANewCurr getInstance() {
		return mNewCurr;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_curr);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		mNewCurr = this;
//		getActionBar().setTitle("Set Your Currency");
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
	
	public void onNext(View v) {
		
		EditText e = (EditText) findViewById(R.id.idEditCurrName);
		String s = e.getText().toString().trim();
		if(!s.equals("")) {
			CDataProvider.getInstance().setCurrency(s);
		}
		startActivity(new Intent(ANewCurr.this, ANewItem.class));
	}
}
