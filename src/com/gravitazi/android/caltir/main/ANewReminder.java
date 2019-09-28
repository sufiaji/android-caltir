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
import android.widget.Spinner;

public class ANewReminder extends Activity {
	
	private Spinner mSpinnerHour;
	private Spinner mSpinnerTime;
	private static ANewReminder mNewReminder;
	
	public static ANewReminder getInstance() {
		return mNewReminder;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_reminder);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		mSpinnerHour = (Spinner) findViewById(R.id.idSpinHour);
		mSpinnerTime = (Spinner) findViewById(R.id.idSpinTime);
		mNewReminder = this;
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
		CDataProvider.getInstance().setReminderHour(mSpinnerHour.getSelectedItem().toString());
		CDataProvider.getInstance().setReminderTime(Integer.valueOf(mSpinnerTime.getSelectedItem().toString()));
		startActivity(new Intent(ANewReminder.this, ANewSummary.class));
	}

}
