package com.gravitazi.android.caltir.main;

import java.util.ArrayList;
import java.util.HashMap;
import com.gravitazi.android.caltir.R;
import com.gravitazi.android.caltir.models.CConstants;
import com.gravitazi.android.caltir.models.CDataProvider;
import com.gravitazi.android.caltir.models.CItem;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class ANewSummary extends Activity {
	
	private ArrayList<CItem> mainItems;
	private ArrayList<CItem> addItems;
	private ArrayList<CItem> sharedItems;
	private ArrayList<ParseUser> billTo;
	private ProgressBar mPb;
	private Button mBtnSave;
	private float mTaxInPrice;
	private final String mDevider = "--------------------------------------";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_summary);	
		getActionBar().setDisplayHomeAsUpEnabled(true);
		LinearLayout l1 = (LinearLayout) findViewById(R.id.idLin1);
		LinearLayout l2 = (LinearLayout) findViewById(R.id.idLin2);
		LinearLayout l3 = (LinearLayout) findViewById(R.id.idLin3);
		LinearLayout l4 = (LinearLayout) findViewById(R.id.idLin4);
		LinearLayout l5 = (LinearLayout) findViewById(R.id.idLin5);
		l5.setVisibility(View.GONE);
		LinearLayout l6 = (LinearLayout) findViewById(R.id.idLin6);
		mPb = (ProgressBar) findViewById(R.id.idProgress);
		mBtnSave = (Button) findViewById(R.id.idButSave);
		mPb.setVisibility(View.INVISIBLE);
		//
		preProcess();
		
		// main items
		int i = 0;
		TextView tv1 = (TextView) findViewById(R.id.idText1);	
		float _totalMain = 0;
		for(CItem item : mainItems) {
			_totalMain += item.getPriceTotal();
			if(i==0) {
				tv1.setText("Main Item(s)\n\nItem: " + item.getName() + "\nQty: " + String.valueOf(item.getQty()) 
						+ "\nSubtotal: " + String.valueOf(item.getPriceTotal()) + "\n" + mDevider  + "\n");
			} else {
				tv1.setText(tv1.getText().toString() + "Item: " + item.getName() + "\nQty: " + String.valueOf(item.getQty()) 
						+ "\nSubtotal: " + String.valueOf(item.getPriceTotal()) + "\n" + mDevider  + "\n");
			}
			i++;
		}
		mTaxInPrice = 0;
		float tax = CDataProvider.getInstance().getTax();
		if(tax > 0) {
			mTaxInPrice = _totalMain * tax / 100;
			tv1.setText(tv1.getText().toString() + "Tax: " + Float.toString(tax) + "%" + " (" + Float.toString(mTaxInPrice) + ")" + "\n" + mDevider + "\n");
		}
		_totalMain += mTaxInPrice;
		tv1.setText(tv1.getText().toString() + "Total: " + Float.toString(_totalMain));
		if(mainItems.size() == 0)
			l1.setVisibility(View.GONE);
		
		// additional items
		i = 0;
		float _totalAdd = 0;
		TextView tv2 = (TextView) findViewById(R.id.idText2);		
		for(CItem item : addItems) {
			if(item.getPriceTotal() == 0) {
				addItems.remove(item);
				continue;
			}
			_totalAdd += item.getPriceTotal();
			if(i==0) {
				tv2.setText("Additional Item(s)\n\nItem: " + item.getName() 
						+ "\nSubtotal: " + String.valueOf(item.getPriceTotal()) + "\n" + mDevider + "\n");
			} else {
				tv2.setText(tv2.getText().toString() + "Item: " + item.getName() 
						+ "\nSubtotal: " + String.valueOf(item.getPriceTotal()) + "\n" + mDevider + "\n");
			}
			i++;
		}
		tv2.setText(tv2.getText().toString() + "Total: " + Float.toString(_totalAdd));
		if(addItems.size() == 0)
			l2.setVisibility(View.GONE);
		
		// shared items
		i = 0;
		float _totalShared = 0;
		TextView tv3 = (TextView) findViewById(R.id.idText3);		
		for(CItem item : sharedItems) {
			if(item.getQty() == 0) {
				sharedItems.remove(item);
				continue;
			}				
			_totalShared += item.getPriceTotal();
			if(i==0) {
				tv3.setText("Shared Item(s)\n\nItem: " + item.getName() + "\nQty: " + String.valueOf(item.getQty()) + "\nSubtotal: " + String.valueOf(item.getPriceTotal()) + "\n");
			} else {
				tv3.setText(tv3.getText().toString() + item.getName() + "\nQty: " + String.valueOf(item.getQty()) + "\nSubtotal: " + String.valueOf(item.getPriceTotal()) + "\n");
			}
			i++;
		}
		tv3.setText(tv3.getText().toString() + "Total: " + Float.toString(_totalShared));
		if(sharedItems.size() == 0)
			l3.setVisibility(View.GONE);
		
		// bill to
		i = 0;
		TextView tv4 = (TextView) findViewById(R.id.idText4);		
		for(ParseUser item : billTo) {
			if(i==0) {
				tv4.setText("Bill to \n\n" + item.getString("name") + "(" + item.getEmail() + ")");
			} else {
				tv4.setText(tv4.getText().toString() + ", " + item.getString("name") + "(" + item.getEmail() + ")");
			}
			i++;
		}
		if(billTo.size() == 0)
			l4.setVisibility(View.GONE);
		
		// Total
		TextView tv5 = (TextView) findViewById(R.id.idText5);
		tv5.setText("Grand Total: " + Float.toString(_totalMain + _totalAdd));
		
		// attachment
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
			l6.setVisibility(View.GONE);
		}
	}
	
	private void preProcess() {
		mainItems = CDataProvider.getInstance().getMainItems();
		addItems = CDataProvider.getInstance().getAddItems();
		sharedItems = CDataProvider.getInstance().getSharedItems();
		billTo = CDataProvider.getInstance().getPersonToBeBilled();
		
		// process mainitems, delete unnecessary items 
		ArrayList<Integer> removePos = new ArrayList<Integer>();
		removePos.clear();
		for(int i=0;i<mainItems.size();i++) {
			CItem item = mainItems.get(i);
			if(item.getQty() == 0) 
				removePos.add(i);
		}
		for(Integer i : removePos)
			mainItems.remove(i);
		
		// process additional items, delete unnecessary items
		removePos.clear();
		for(int i=0;i<addItems.size();i++) {
			CItem item = addItems.get(i);
			if(item.getQty() == 0) 
				removePos.add(i);
		}
		for(Integer i : removePos)
			addItems.remove(i);
		
		// process shared items, delete unnecessary items
		removePos.clear();
		for(int i=0;i<sharedItems.size();i++) {
			CItem item = sharedItems.get(i);
			if(item.getQty() == 0) 
				removePos.add(i);
		}
		for(Integer i : removePos)
			sharedItems.remove(i);
		
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
				cancel();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void cancel() {
		ANewBillTo.getInstance().finish();
		ANewAttach.getInstance().finish();
		ANewShared.getInstance().finish();
		ANewAdditional.getInstance().finish();
		ANewTax.getInstance().finish();
		ANewItem.getInstance().finish();
		ANewSession.getInstance().finish();
		finish();
	}

	public void onNext(View v) {
		saveDataToParse();		
	}

	private void callMainActivity() {
		// TODO Auto-generated method stub
		startActivity(new Intent(ANewSummary.this, AMain.class));
	}

	private void killAllActivities() {
		// TODO Auto-generated method stub
//		ANewReminder.getInstance().finish();
		ANewBillTo.getInstance().finish();
		ANewAttach.getInstance().finish();
		ANewShared.getInstance().finish();
		ANewAdditional.getInstance().finish();
		ANewTax.getInstance().finish();
		ANewItem.getInstance().finish();
//		ANewCurr.getInstance().finish();
		ANewSession.getInstance().finish();
		AMain.getInstance().finish();
		finish();
	}

	private void saveDataToParse() {
		mPb.setVisibility(View.VISIBLE);
		mBtnSave.setEnabled(false);
		// main items
		float totalPrice = mTaxInPrice;
		ArrayList<ParseObject> arrayItems = new ArrayList<ParseObject>();		
		for(CItem item : mainItems) {
			ParseObject mainItemObject = new ParseObject("Item");
			mainItemObject.put(CConstants.C_ITEMS_NAME, item.getName());
			mainItemObject.put(CConstants.C_ITEMS_QTY, item.getQty());
			mainItemObject.put(CConstants.C_ITEMS_PRICE, item.getPriceTotal());
			mainItemObject.put(CConstants.C_ITEMS_IS_ADDITIONAL, false);
			// is it include in "Bill to All"?
			for(CItem sItm : sharedItems) {
				if(sItm.getName().equals(item.getName())) {
					mainItemObject.put(CConstants.C_ITEMS_QTY_BILL_ALL, sItm.getQty());
					break;
				} else {
					mainItemObject.put(CConstants.C_ITEMS_QTY_BILL_ALL, 0);
				}
			}
			totalPrice = totalPrice + item.getPriceTotal();
			arrayItems.add(mainItemObject);
		}
		// additional items
		ArrayList<CItem> addItems = CDataProvider.getInstance().getAddItems();
		for(CItem item : addItems) {
			ParseObject addItemObject = new ParseObject("Item");
			addItemObject.put(CConstants.C_ITEMS_NAME, item.getName());
			addItemObject.put(CConstants.C_ITEMS_QTY, 1);
			addItemObject.put(CConstants.C_ITEMS_PRICE, item.getPriceTotal());
			addItemObject.put(CConstants.C_ITEMS_IS_ADDITIONAL, true);
			addItemObject.put(CConstants.C_ITEMS_QTY_BILL_ALL, 1);
			arrayItems.add(addItemObject);
			totalPrice = totalPrice + item.getPriceTotal();
		}
		// billto
		// construct BilledTo list from ParseUser list
		ArrayList<ParseObject> billedTo = new ArrayList<ParseObject>();		
		for(ParseUser user : billTo) {
			ParseObject userBilled = new ParseObject("BilledTo");
			ArrayList<ParseUser> users = new ArrayList<ParseUser>();
			users.add(user);
			userBilled.put(CConstants.C_BILLTO_ARRAY_USER, users);
			userBilled.put(CConstants.C_BILLTO_USERID, user.getObjectId());
			userBilled.put(CConstants.C_BILLTO_PRICE, 0);
			userBilled.put(CConstants.C_BILLTO_STATUS, CConstants.C_STATUS_INIT);
			billedTo.add(userBilled);
		}
		// session
		ParseObject session = new ParseObject("Session");
		ArrayList<ParseUser> myUsers = new ArrayList<ParseUser>();
		myUsers.add(ParseUser.getCurrentUser());
		session.put(CConstants.C_SESSION_ARRAY_CREATOR, myUsers);
		session.put(CConstants.C_SESSION_ID_CREATOR, ParseUser.getCurrentUser().getObjectId());
		session.put(CConstants.C_SESSION_NAME, CDataProvider.getInstance().getSessionName());
//		session.put(CConstants.C_SESSION_CURRENCY, CDataProvider.getInstance().getCurrency());
//		session.put(CConstants.C_SESSION_REMINDERTIME, CDataProvider.getInstance().getReminderTime());
//		session.put(CConstants.C_SESSION_REMINDERHOUR, CDataProvider.getInstance().getReminderHour());
		session.put(CConstants.C_SESSION_ARRAY_ITEMS, arrayItems);
		session.put(CConstants.C_SESSION_ARRAY_BILLEDTO, billedTo);
		ArrayList<ParseUser> users = new ArrayList<ParseUser>();
		for(ParseUser user : billTo) {
			users.add(user);
		}
		session.put(CConstants.C_SESSION_ARRAY_BILLEDTO_USERS, users);
		session.put(CConstants.C_SESSION_STATUS, CConstants.C_STATUS_INIT);
		session.put(CConstants.C_SESSION_PRICE_TOTAL, totalPrice);
		if(CDataProvider.getInstance().getAttachment() != null)
			session.put(CConstants.C_SESSION_IMAGE, CDataProvider.getInstance().getAttachment());
		
		session.put(CConstants.C_SESSION_TAXPERCENT, CDataProvider.getInstance().getTax());
		session.put(CConstants.C_SESSION_TAXPRICE, mTaxInPrice);
		
		session.saveInBackground(new SaveCallback() {
			
			@Override
			public void done(ParseException e) {
				mPb.setVisibility(View.INVISIBLE);
				mBtnSave.setEnabled(true);
				if(e != null) {
					String msg = e.getMessage();
					Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(getApplicationContext(), "Session saved successfully", Toast.LENGTH_LONG).show();
					sendNotif();
					killAllActivities();
					callMainActivity();
				}	
			}
		});
		
	}

	protected void sendNotif() {
		for(ParseUser user : billTo) {
			HashMap<String, Object> params = new HashMap<String, Object>();
			String message = ParseUser.getCurrentUser().getString("name") + ": New bill arrived for " + CDataProvider.getInstance().getSessionName() + ", please check it out!";
			final String userID = user.getObjectId();
			params.put("recipientId", userID);
			params.put("message", message);
			ParseCloud.callFunctionInBackground("sendPushToUser", params, new FunctionCallback<String>() {
	
				@Override
				public void done(String object, ParseException e) {
					if (e == null)
						Toast.makeText(getApplicationContext(), "Notification sent to: " + userID, Toast.LENGTH_SHORT).show();
					else
						Toast.makeText(getApplicationContext(), "Fail Notif: " + e.getMessage().toString(), Toast.LENGTH_LONG).show();
				}
			   
			});
		}
	}

}
