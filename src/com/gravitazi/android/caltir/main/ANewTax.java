package com.gravitazi.android.caltir.main;

import com.gravitazi.android.caltir.R;
import com.gravitazi.android.caltir.models.CDataProvider;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class ANewTax extends Activity {
	
	private EditText mTax;
	private static ANewTax mNewTax;
	
	public static ANewTax getInstance() {
		return mNewTax;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_tax);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		mTax = (EditText) findViewById(R.id.idTax);
		mTax.setText(Float.toString(0));	
		mNewTax = this;
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
		switch (item.getItemId()) {			
			case R.id.idMenuCancel:
				cancel();
				return true;
			case android.R.id.home:
				finish();
		        return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void cancel() {
		ANewItem.getInstance().finish();
		ANewSession.getInstance().finish();		
		finish();
	}

	public void onNext(View v) {
		if(!mTax.getText().toString().trim().equals("")) {
			float tax = Float.parseFloat(mTax.getText().toString().trim());
			CDataProvider.getInstance().setTax(tax);			
		} else {
			CDataProvider.getInstance().setTax(0);	
			CDataProvider.getInstance().setTaxPrice(0);
		}
		startActivity(new Intent(ANewTax.this, ANewAdditional.class));
	}

}
