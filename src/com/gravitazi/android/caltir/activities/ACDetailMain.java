package com.gravitazi.android.caltir.activities;

import java.util.ArrayList;
import java.util.List;

import com.gravitazi.android.caltir.R;
import com.gravitazi.android.caltir.main.APicture;
import com.gravitazi.android.caltir.models.CBillTo;
import com.gravitazi.android.caltir.models.CConstants;
import com.gravitazi.android.caltir.models.CDataProvider;
import com.gravitazi.android.caltir.models.CItem;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ACDetailMain extends Activity {
	
	private static ACDetailMain mDetailMain;
	
	public static ACDetailMain getInstance() {
		return mDetailMain;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail_main);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		mDetailMain = this;
		displaySummary();
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
	
	private void displaySummary() {
		TextView tv1 = (TextView) findViewById(R.id.idText1);
		TextView tv2 = (TextView) findViewById(R.id.idText2);
		TextView tv3 = (TextView) findViewById(R.id.idText3);
		TextView tv4 = (TextView) findViewById(R.id.idText4);
//		TextView tv5 = (TextView) findViewById(R.id.idText5);
		TextView tv6 = (TextView) findViewById(R.id.idText6);
		TextView tvTax = (TextView) findViewById(R.id.idTextTax);
		tv1.setText("Main Item(s)\n\n");
		tvTax.setText("Main tax\n\n");
		tv2.setText("Additional Item(s)\n\n");
		tv3.setText("Shared Item(s)\n\n");
		tv4.setText("Person(s) to be billed\n");
//		tv5.setText("Reminder\n\n");
		
		float mainTax = CDataProvider.getInstance().getTax();
		float mainTaxPrice = CDataProvider.getInstance().getTaxPrice();
		ArrayList<ParseObject> listSession = CDataProvider.getInstance().getHistory();
		ParseObject session = listSession.get(CDataProvider.getInstance().getSelectedSessionPosition());
		//
		float mainSum = 0, addSum = 0, sharedSum = 0;
		// Main Items & Shared Items
		List<CItem> mainItems = CDataProvider.getInstance().getMainItems();
		List<CItem> sharedItems = CDataProvider.getInstance().getSharedItems();
		for(CItem item : mainItems) {
			mainSum = mainSum + item.getPriceTotal();
			tv1.setText(tv1.getText().toString() + item.getName() + "  " + item.getQty() + "  " + item.getPriceTotal() + "\n" );
			for(CItem sItem : sharedItems) {
				if(sItem.getName().equals(item.getName())) {
					sharedSum = sharedSum + sItem.getPriceTotal();
					tv3.setText(tv3.getText().toString() + sItem.getName() + "  " + sItem.getQty() + "  " + sItem.getPriceTotal() + "\n" );
				}
			}
		}
		// Additional Items
		List<CItem> addItems = CDataProvider.getInstance().getAddItems();
		for(CItem item : addItems) {
			addSum = addSum + item.getPriceTotal();
			tv2.setText(tv2.getText().toString() + item.getName() + "  " + item.getQty() + "  " + item.getPriceTotal() + "\n" );
		}
		
		tv1.setText(tv1.getText().toString() + "Total:  " + Float.toString(mainSum));
		tv2.setText(tv2.getText().toString() + "Total:  " + Float.toString(addSum));
		tv3.setText(tv3.getText().toString() + "Total:  " + Float.toString(sharedSum));
		
		// reminder
//		tv5.setText(tv5.getText().toString() + Integer.toString(session.getInt(CConstants.C_SESSION_REMINDERTIME)) + " " + session.getString(CConstants.C_SESSION_REMINDERHOUR));
		LinearLayout l5 = (LinearLayout) findViewById(R.id.idLin5);
		l5.setVisibility(View.GONE);
		
		// hide/display view
		if(addSum == 0) {
			LinearLayout l = (LinearLayout) findViewById(R.id.idLin2);
			l.setVisibility(View.GONE);
		}
		if(sharedSum == 0) {
			LinearLayout l = (LinearLayout) findViewById(R.id.idLin3);
			l.setVisibility(View.GONE);
		}
		if(mainTax == 0) {
			LinearLayout l = (LinearLayout) findViewById(R.id.idLinTax);
			l.setVisibility(View.GONE);
		} else {
			tvTax.setText(tvTax.getText().toString() + Float.toString(mainTax) + " (" + mainTaxPrice + ")");
		}
//		if(CDataProvider.getInstance().getReminderTime() == 0) {
//			LinearLayout l = (LinearLayout) findViewById(R.id.idLin5);
//			l.setVisibility(View.GONE);
//		}
		List<CBillTo> cbilltos = CDataProvider.getInstance().getBillTo();
		float paymentMade = 0;
		for(CBillTo cbillto : cbilltos) {
			// this already include tax
			float price = cbillto.getPrice();
			if(cbillto.getStatus() == CConstants.C_STATUS_DONE) 
				paymentMade += price;
			tv4.setText(tv4.getText().toString() + "\n" + cbillto.getUsername() + " (paid: " + Float.toString(price) + ")" );
		}
		
		// grand total summary
		float t = (float) session.getDouble(CConstants.C_SESSION_PRICE_TOTAL);
		tv6.setText("Grand Total: " 
					+ Float.toString(t)
					+ "\n"
					+ "Payment Made: " + Float.toString(paymentMade)
					+ "\n"
					+ "Payment Due: " + Float.toString(t - paymentMade));
		
		// bill image attachment
		ParseFile f = CDataProvider.getInstance().getAttachment();
		if(f != null) {
			byte[] imgData;
			try {
				imgData = f.getData();
				Bitmap bitmap = BitmapFactory.decodeByteArray(imgData , 0, imgData.length);
				ImageView imgV = (ImageView) findViewById(R.id.idImgAttach);
				imgV.setImageBitmap(bitmap);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
		} else {
			LinearLayout l = (LinearLayout) findViewById(R.id.idLin7);
			l.setVisibility(View.GONE);
		}
		
	}

	public void onStatus(View v) {
		startActivity(new Intent(ACDetailMain.this, ACBillStatus.class));
	}
	
	public void onImgClick(View v) {
		startActivity(new Intent(ACDetailMain.this, APicture.class));
	}

}
