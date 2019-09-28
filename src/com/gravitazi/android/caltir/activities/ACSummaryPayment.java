package com.gravitazi.android.caltir.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.gravitazi.android.caltir.R;
import com.gravitazi.android.caltir.main.AMain;
import com.gravitazi.android.caltir.main.ANewShared;
import com.gravitazi.android.caltir.models.CBillTo;
import com.gravitazi.android.caltir.models.CConstants;
import com.gravitazi.android.caltir.models.CDataProvider;
import com.gravitazi.android.caltir.models.CItem;
import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class ACSummaryPayment extends Activity {	
	
	private ProgressBar mPb;
	private ParseObject mSession;
	private List<ParseObject> mBilledTos;
	private float mTaxPercent;
	private Button mBtnPay;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_summary_payment);
		mPb = (ProgressBar) findViewById(R.id.idProgress);
		mPb.setVisibility(View.INVISIBLE);
		mBtnPay = (Button) findViewById(R.id.idButtonPay);
		int mIndex = CDataProvider.getInstance().getSelectedSessionPosition();
		mSession = CDataProvider.getInstance().getHistory().get(mIndex);
		mBilledTos = mSession.getList(CConstants.C_SESSION_ARRAY_BILLEDTO);
		mTaxPercent = CDataProvider.getInstance().getTax() / 100;
		if(CDataProvider.getInstance().getPageState().equals(CConstants.C_PAGE_APPROVAL)) {
			if(CDataProvider.getInstance().getPageReadOnly()) {
				mBtnPay.setVisibility(View.GONE);
			} else {
				mBtnPay.setVisibility(View.VISIBLE);
				mBtnPay.setText("Approve");
			}
			displayApproval();
		} else if(CDataProvider.getInstance().getPageState().equals(CConstants.C_PAGE_PAYMENT)) {
			mBtnPay.setText("Pay");
			displaySummary();
		}
	}
	
	private void displayApproval() {
		TextView tvSel = (TextView) findViewById(R.id.idSelectedItem);
		TextView tvAdd = (TextView) findViewById(R.id.idAdditionalItem);
		TextView tvShd = (TextView) findViewById(R.id.idSharedItem);
		TextView tvTot = (TextView) findViewById(R.id.idTotalItem);
		
		int position = CDataProvider.getInstance().getSelectedBillToPosition();
		CBillTo cbillto = CDataProvider.getInstance().getBillTo().get(position);
		ArrayList<CItem> items = cbillto.getItems();
		ArrayList<CItem> mainItems = new ArrayList<CItem>();
		ArrayList<CItem> sharedItems = new ArrayList<CItem>();
		ArrayList<CItem> addItems = new ArrayList<CItem>();
		for(CItem item : items) {
			if(item.getItemType() == 1) {
				mainItems.add(item);
			} else if (item.getItemType() == 2) {
				sharedItems.add(item);
			} else if (item.getItemType() == 3) {
				addItems.add(item);
			}
		}
		
		float mainSum = 0, addSum = 0, shdSum = 0;
		float mainTax = 0, shdTax = 0;
//		ArrayList<CItem> selItems = CDataProvider.getInstance().getPayableItems();
		for(CItem item : mainItems) {
			mainTax += item.getTaxPrice();
			mainSum += item.getPriceTotal();
			tvSel.setText(tvSel.getText() + "\n" + item.getName() + " " + item.getQty() + " " + item.getPriceTotal());
		}
		tvSel.setText(tvSel.getText() + "\nTax: " + Float.toString(mainTax));
		mainSum += mainTax;
		
		if(sharedItems.size() > 0) {
			for(CItem item : sharedItems) {
				shdTax += item.getTaxPrice();
				shdSum += item.getPriceTotal();
				tvShd.setText(tvShd.getText() + "\n" + item.getName() + " " + item.getQty() + " " + item.getPriceTotal());
			}
			tvShd.setText(tvShd.getText() + "\nTax: " + Float.toString(shdTax));
			shdSum += shdTax;
		} else {
			tvShd.setVisibility(View.GONE);
		}
		
		if(addItems.size() > 0) {
			for(CItem item : addItems) {
				addSum += item.getPriceTotal();
				tvAdd.setText(tvAdd.getText() + "\n" + item.getName() + " " + item.getPriceTotal());
			}
		} else {
			tvAdd.setVisibility(View.GONE);
		}
		
		tvTot.setText(tvTot.getText() + Float.toString(addSum + mainSum + shdSum));
	}
	
	private void displaySummary() {
		TextView tvSel = (TextView) findViewById(R.id.idSelectedItem);
		TextView tvAdd = (TextView) findViewById(R.id.idAdditionalItem);
		TextView tvShd = (TextView) findViewById(R.id.idSharedItem);
		TextView tvTot = (TextView) findViewById(R.id.idTotalItem);
		
		float selSum = 0, addSum = 0, shdSum = 0;
		ArrayList<CItem> selItems = CDataProvider.getInstance().getPayableItems();
		for(CItem item : selItems) {
			selSum = selSum + item.getPriceTotal();
			tvSel.setText(tvSel.getText() + "\n" + item.getName() + " " + item.getQty() + " " + item.getPriceTotal());
		}
		float taxValMain = 0;
		if(mTaxPercent > 0) {
			taxValMain = selSum * mTaxPercent;
			tvSel.setText(tvSel.getText() + "\nTax: " + Float.toString(mTaxPercent * 100) + "% (" + Float.toString(taxValMain) + ")");
			selSum += taxValMain;
		}
		tvSel.setText(tvSel.getText() + "\nTotal: " + Float.toString(selSum));
		
		ArrayList<CItem> addItems = CDataProvider.getInstance().getAddItems();
		if(addItems.size() > 0) {
			for(CItem item : addItems) {
				float sQty = item.getQty() / (float) mBilledTos.size();
				float sPrice = item.getPriceTotal() / (float) mBilledTos.size();
				addSum = addSum + sPrice;
				tvAdd.setText(tvAdd.getText() + "\n" + item.getName() + " " + sQty + " " + sPrice);
			}
			tvAdd.setText(tvAdd.getText() + "\nTotal: " + Float.toString(addSum));
		} else {
			tvAdd.setVisibility(View.GONE);
		}
		
		ArrayList<CItem> sharedItems = CDataProvider.getInstance().getSharedItems();
		if(sharedItems.size() > 0) {
			for(CItem item : sharedItems) {
				float sQty = item.getQty() / (float) mBilledTos.size();
				float sPrice = item.getPriceTotal() / (float) mBilledTos.size();
				shdSum = shdSum + sPrice;
				tvShd.setText(tvShd.getText() + "\n" + item.getName() + " " + sQty + " " + sPrice);
			}
			float taxValShared = 0;
			if(mTaxPercent > 0) {
				taxValShared = shdSum * mTaxPercent;
				tvShd.setText(tvShd.getText() + "\nTax: " + Float.toString(mTaxPercent) + "% (" + Float.toString(taxValShared) + ")");
				shdSum += taxValShared;
			}
			tvShd.setText(tvShd.getText() + "\nTotal: " + Float.toString(shdSum));
		} else {
			tvShd.setVisibility(View.GONE);
		}
		
		tvTot.setText(tvTot.getText() + Float.toString(addSum + selSum + shdSum));
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
	
	public void onPay(View v) {
		if(CDataProvider.getInstance().getPageState().equals(CConstants.C_PAGE_PAYMENT)) {
			prepareSavingData();	
		} else if(CDataProvider.getInstance().getPageState().equals(CConstants.C_PAGE_APPROVAL)) {
			// approve the payment
			setStatusDone();
		}
	}

	private void setStatusDone() {
		mPb.setVisibility(View.VISIBLE);
		int position = CDataProvider.getInstance().getSelectedBillToPosition();
		final CBillTo cbillto = CDataProvider.getInstance().getBillTo().get(position);
		final String objid = cbillto.getId();
		final String name = cbillto.getUsername();
		ParseQuery<ParseObject> query = ParseQuery.getQuery("BilledTo");
		query.getInBackground(objid, new GetCallback<ParseObject>() {
			
			@Override
			public void done(ParseObject object, ParseException e) {
				if (e == null) {
					object.put(CConstants.C_BILLTO_STATUS, CConstants.C_STATUS_DONE);
					object.saveInBackground(new SaveCallback() {
						
						@Override
						public void done(ParseException e) {
							if(e == null) {
								Toast.makeText(getApplicationContext(), "Successfully approve the payment from " + name, Toast.LENGTH_SHORT).show();
								sendNotification(cbillto.getParseUser(), cbillto.getParseUser().getObjectId(), ParseUser.getCurrentUser().get("name") + " has approved your payment.");
								killAllActivities();
								callMainActivity();
							} else {
								Toast.makeText(getApplicationContext(), "Fail to update the approval status for " + name +" , please try again", Toast.LENGTH_SHORT).show();
							}
							mPb.setVisibility(View.INVISIBLE);
						}
					});
				} else {
					Toast.makeText(getApplicationContext(), "Fail to fetch all data for " + name, Toast.LENGTH_SHORT).show();
				}
				mPb.setVisibility(View.INVISIBLE);
			}
		});
	}

	private void prepareSavingData() {
		mPb.setVisibility(View.VISIBLE);		
		for(ParseObject billTo : mBilledTos) {
			if(billTo.getString(CConstants.C_BILLTO_USERID).equals(ParseUser.getCurrentUser().getObjectId())) {
				if(billTo.getInt(CConstants.C_BILLTO_STATUS) == CConstants.C_STATUS_PARTIAL) {
					// if the user want to update it's payment, so remove all items first
					List<ParseObject> itemArray = billTo.getList(CConstants.C_BILLTO_ARRAY_ITEM);
					ParseObject.deleteAllInBackground(itemArray);
					billTo.removeAll(CConstants.C_BILLTO_ARRAY_ITEM, itemArray);
					billTo.saveInBackground(new SaveCallback() {
						
						@Override
						public void done(ParseException e) {
							// second, populate new items data and save to Parse...
							if(e == null) 
								saveToParse();
							else {
								mPb.setVisibility(View.INVISIBLE);
								Toast.makeText(getApplicationContext(), "Error deleting data", Toast.LENGTH_SHORT).show();								
							}
						}
					});
				} else if(billTo.getInt(CConstants.C_BILLTO_STATUS) == CConstants.C_STATUS_INIT) {
					// if the first time user want to do payment
					saveToParse();
				} else if(billTo.getInt(CConstants.C_BILLTO_STATUS) == CConstants.C_STATUS_DONE) {
					// do nothing
					mPb.setVisibility(View.INVISIBLE);
				}				
				break;
			}
		}
	}

	protected void saveToParse() {		
		for(ParseObject billTo : mBilledTos) {
			if(billTo.getString(CConstants.C_BILLTO_USERID).equals(ParseUser.getCurrentUser().getObjectId())) {
				// populate Main Item
				ArrayList<CItem> selItems = CDataProvider.getInstance().getPayableItems();
				ArrayList<ParseObject> itemPays = new ArrayList<ParseObject>();
				float total = 0;
				for(CItem item : selItems) {
					ParseObject itemPay = new ParseObject("ItemPay");
					itemPay.put(CConstants.C_ITEMPAY_ITEM_NAME, item.getName());
					itemPay.put(CConstants.C_ITEMPAY_ITEM_QTY, item.getQty());
					itemPay.put(CConstants.C_ITEMPAY_ITEM_PRICE, item.getPriceTotal());
					itemPay.put(CConstants.C_ITEMPAY_USERID, ParseUser.getCurrentUser().getObjectId());
					itemPay.put(CConstants.C_ITEMPAY_ITEMTYPE, 1);
					float taxVal = item.getPriceTotal() * mTaxPercent;
					itemPay.put(CConstants.C_ITEMPAY_TAXPRICE, taxVal);
					itemPays.add(itemPay);
					total += item.getPriceTotal() + taxVal;
				}
				// populate Additional Item
				ArrayList<CItem> addItems = CDataProvider.getInstance().getAddItems();
				float totalAdd = 0;
				if(addItems.size() > 0) {
					for(CItem item : addItems) {
						float sQty = item.getQty() / (float) mBilledTos.size();
						float sPrice = item.getPriceTotal() / (float) mBilledTos.size();
						totalAdd += sPrice;
						ParseObject itemPay = new ParseObject("ItemPay");
						itemPay.put(CConstants.C_ITEMPAY_ITEM_NAME, item.getName());
						itemPay.put(CConstants.C_ITEMPAY_ITEM_QTY, sQty);
						itemPay.put(CConstants.C_ITEMPAY_ITEM_PRICE, sPrice);
						itemPay.put(CConstants.C_ITEMPAY_USERID, ParseUser.getCurrentUser().getObjectId());
						itemPay.put(CConstants.C_ITEMPAY_ITEMTYPE, 3);
						itemPay.put(CConstants.C_ITEMPAY_TAXPRICE, 0);
						itemPays.add(itemPay);
					}					
				}
				// populate Shared Item
				float totalShared = 0;
				ArrayList<CItem> sharedItems = CDataProvider.getInstance().getSharedItems();
				if(sharedItems.size() > 0) {
					for(CItem item : sharedItems) {
						float sQty = item.getQty() / (float) mBilledTos.size();
						float sPrice = item.getPriceTotal() / (float) mBilledTos.size();						
						ParseObject itemPay = new ParseObject("ItemPay");
						itemPay.put(CConstants.C_ITEMPAY_ITEM_NAME, item.getName());
						itemPay.put(CConstants.C_ITEMPAY_ITEM_QTY, sQty);
						// not yet include tax
						itemPay.put(CConstants.C_ITEMPAY_ITEM_PRICE, sPrice);
						itemPay.put(CConstants.C_ITEMPAY_USERID, ParseUser.getCurrentUser().getObjectId());
						itemPay.put(CConstants.C_ITEMPAY_ITEMTYPE, 2);
						// this is where the tax in price will be saved
						float taxVal = sPrice * mTaxPercent;
						itemPay.put(CConstants.C_ITEMPAY_TAXPRICE, taxVal);
						itemPays.add(itemPay);
						totalShared += sPrice + taxVal;
					}					
				}				
				
				total = total + totalAdd + totalShared;
				
				billTo.put(CConstants.C_BILLTO_ARRAY_ITEM, itemPays);
				// already include Tax
				billTo.put(CConstants.C_BILLTO_PRICE, total);
				String objidCreator = CDataProvider.getInstance().getPersonCreator().getObjectId();
				String objidCurrent =ParseUser.getCurrentUser().getObjectId();
				// if the session creator pay his bill then set the status direct to DONE
				if(objidCurrent.equals(objidCreator)) 
					billTo.put(CConstants.C_BILLTO_STATUS, CConstants.C_STATUS_DONE);
				else
					billTo.put(CConstants.C_BILLTO_STATUS, CConstants.C_STATUS_PARTIAL);
				billTo.saveInBackground(new SaveCallback() {
					
					@Override
					public void done(ParseException e) {
						mPb.setVisibility(View.INVISIBLE);
						if(e == null) {
							Toast.makeText(getApplicationContext(), "Successfully made the payment", Toast.LENGTH_SHORT).show();
							updateSessionStatus();
							killAllActivities();
							callMainActivity();
							updateSessionStatus();
						} else {
							Toast.makeText(getApplicationContext(), "Error saving data", Toast.LENGTH_SHORT).show();
						}
					}
				});
				break;
			}
			
		}
	}

	protected void updateSessionStatus() {
		int index = CDataProvider.getInstance().getSelectedSessionPosition();
		ParseObject Session = CDataProvider.getInstance().getHistory().get(index);
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("objid", Session.getObjectId());
		ParseCloud.callFunctionInBackground("updateSessionStatus2", params, new FunctionCallback<String>() {
			public void done(String result, ParseException e) {
			    if (e == null) {
			    	if(result.equals("Success")) {
			    		Toast.makeText(getApplicationContext(), "Success update status session by cloud code", Toast.LENGTH_SHORT).show();
			    	} else {
			    		Toast.makeText(getApplicationContext(), "Error update status session by cloud code", Toast.LENGTH_SHORT).show();
			    	}
			    } else {
			    	Toast.makeText(getApplicationContext(), "Error update status session by cloud code, e not null", Toast.LENGTH_SHORT).show();
			    }
			}
		});
	}

	protected void callMainActivity() {
		startActivity(new Intent(ACSummaryPayment.this, AMain.class));
	}

	protected void killAllActivities() {	
		if(CDataProvider.getInstance().getPageState().equals(CConstants.C_PAGE_PAYMENT)) {
			ANewShared.getInstance().finish();
		} else {
			
		}
		ACBillStatus.getInstance().finish();
		ACDetailMain.getInstance().finish();
		AMain.getInstance().finish();
		finish();
	}
	
	protected void sendNotification(ParseUser user, String oid, String msg) {
		HashMap<String, Object> params = new HashMap<String, Object>();
//		String objid = user.getObjectId();
		final String name = user.getString("name");
		params.put("recipientId", oid);
		params.put("message", msg);
		ParseCloud.callFunctionInBackground("sendPushToUser", params, new FunctionCallback<String>() {

			@Override
			public void done(String object, ParseException e) {
				if (e == null)
					Toast.makeText(getApplicationContext(), "Notification sent to: " + name, Toast.LENGTH_SHORT).show();
				else
					Toast.makeText(getApplicationContext(), "Fail to send notification: " + e.getMessage().toString(), Toast.LENGTH_LONG).show();
			}
		   
		});
	}

}
