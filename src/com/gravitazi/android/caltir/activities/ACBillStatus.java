package com.gravitazi.android.caltir.activities;

import java.util.ArrayList;
import java.util.HashMap;

import com.gravitazi.android.caltir.R;
import com.gravitazi.android.caltir.main.ANewShared;
import com.gravitazi.android.caltir.models.CBillTo;
import com.gravitazi.android.caltir.models.CConstants;
import com.gravitazi.android.caltir.models.CDataProvider;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ACBillStatus extends Activity implements OnItemClickListener {
	
	private ListView mListView;
	private CStatusAdapter mAdapter;
	private ArrayList<CBillTo> mList;
	private ParseObject session;
	private static ACBillStatus mBillStatus;
	
	public static ACBillStatus getInstance() {
		return mBillStatus;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bill_status);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		mListView = (ListView) findViewById(R.id.idList);
		mListView.setOnItemClickListener(this);		
		session = CDataProvider.getInstance().getHistory()
				.get(CDataProvider.getInstance().getSelectedSessionPosition());
		
		mList = CDataProvider.getInstance().getBillTo();
		mAdapter = new CStatusAdapter(this, R.layout.row_bill_status, mList);
		mListView.setAdapter(mAdapter);
		TextView tTotal = (TextView) findViewById(R.id.idTextTotal);
		tTotal.setText("Total: " + Float.toString((float) session.getDouble(CConstants.C_SESSION_PRICE_TOTAL)));
		mBillStatus = this;
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
		
	private class CStatusAdapter extends ArrayAdapter<CBillTo> {
		
		private ArrayList<CBillTo> _list;
		private int _layoutID;
		private Context _context;

		public CStatusAdapter(Context context, int resource, ArrayList<CBillTo> billTo) {
			// objects is a list of BilledTo class (Session.billedTo)
			super(context, resource, billTo);
			_list = billTo;
			_layoutID = resource;
			_context = context;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView == null) {
				LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(_layoutID, null);
			}
			
			CBillTo sBillTo = _list.get(position);
			if(sBillTo != null) {
				TextView tName = (TextView) convertView.findViewById(R.id.idRowPerson);
				tName.setText(sBillTo.getUsername());
				TextView tStatus = (TextView) convertView.findViewById(R.id.idRowStatus);
				if(sBillTo.getStatus() == CConstants.C_STATUS_INIT) {
					tStatus.setText("UNPAID");
					tStatus.setBackgroundColor(Color.RED);
				} else if(sBillTo.getStatus() == CConstants.C_STATUS_PARTIAL) {
					tStatus.setText("CONFIRM");
					tStatus.setBackgroundColor(Color.YELLOW);
				} else {
					tStatus.setText("PAID");
					tStatus.setBackgroundColor(Color.GREEN);
				}
				TextView tPrice = (TextView) convertView.findViewById(R.id.idRowPrice);		
				tPrice.setText(Float.toString(sBillTo.getPrice()));				
			}
			return convertView;
		}
		
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		// if current user is the creator of this session
		String objidCreator = CDataProvider.getInstance().getPersonCreator().getObjectId();
		String objidCurrent = ParseUser.getCurrentUser().getObjectId();
		CBillTo billTo = mList.get(position);
		CDataProvider.getInstance().setSelectedBillToPosition(position);
		// if this user open his/her own session... means this user is creator of this session
		if(objidCreator.equals(objidCurrent)) {
			// this user open other user's bill
			if(!objidCurrent.equals(billTo.getParseUser().getObjectId())) {
				if(billTo.getStatus() == CConstants.C_STATUS_INIT) {
					// show dialog to send reminder asking for paying
					showModalDialog(1, ParseUser.getCurrentUser(), billTo.getParseUser());
				} else if(billTo.getStatus() == CConstants.C_STATUS_PARTIAL) {
					// go to page Approval for approving
					CDataProvider.getInstance().setPageStage(CConstants.C_PAGE_APPROVAL);
					CDataProvider.getInstance().setPageReadOnly(false);
					startActivity(new Intent(ACBillStatus.this, ACSummaryPayment.class));
				} else if(billTo.getStatus() == CConstants.C_STATUS_DONE) {
					// go to page Approval-read only
					CDataProvider.getInstance().setPageStage(CConstants.C_PAGE_APPROVAL);
					CDataProvider.getInstance().setPageReadOnly(true);
					startActivity(new Intent(ACBillStatus.this, ACSummaryPayment.class));
				}
			} else {
				// if this user open his/her own bill status...
				if(billTo.getStatus() == CConstants.C_STATUS_INIT) 
					updatePayment();
				else {
					// go to page Approval-read only
					CDataProvider.getInstance().setPageStage(CConstants.C_PAGE_APPROVAL);
					CDataProvider.getInstance().setPageReadOnly(true);
					startActivity(new Intent(ACBillStatus.this, ACSummaryPayment.class));
				}
			}
		// if current user is not the creator of this session
		} else {
			// he select his own item ...
			if(objidCurrent.equals(billTo.getParseUser().getObjectId())) {				
				if(billTo.getStatus() == CConstants.C_STATUS_INIT) {
					// go to page payment for paying
					updatePayment();
				} else if(billTo.getStatus() == CConstants.C_STATUS_PARTIAL) {
					// show dialog to ask user to select send notification asking for approval OR edit the previous payment
					showModalDialog(2, ParseUser.getCurrentUser(), (ParseUser) session.getList(CConstants.C_SESSION_ARRAY_CREATOR).get(0));
				} else if(billTo.getStatus() == CConstants.C_STATUS_DONE) {
					// go to page Approval-read only
					CDataProvider.getInstance().setPageStage(CConstants.C_PAGE_APPROVAL);
					CDataProvider.getInstance().setPageReadOnly(true);
					startActivity(new Intent(ACBillStatus.this, ACSummaryPayment.class));
				}
			// he select other person's item
			} else {
				// display other person's payment
			}
		}
	}

	private void showModalDialog(final int type, final ParseUser userFrom, final ParseUser userTo) {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		if(type == 1) {
			alert.setTitle("Asking " + userTo.getString("name") + " to pay?");
			alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int whichButton) {
		        	String msg = "A gentle reminder from " + userFrom.getString("name") + " to pay the bill in session " + session.getString(CConstants.C_SESSION_NAME);
		        	sendNotification(type, userTo, msg);
		        }
		    });

		    alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int whichButton) {
	            	dialog.cancel();
	            }
	        });
		    
		} else {
			alert.setTitle("Edit your payment or send notification to " + userTo.getString("name") + " to approve your payment?");
			alert.setPositiveButton("Send Notif.", new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int whichButton) {
		        	String msg = "A gentle reminder from " + userFrom.getString("name") + " to approve her/his bill in session " + session.getString(CConstants.C_SESSION_NAME);
		        	sendNotification(type, userTo, msg);
		        }
		    });
			
			alert.setNeutralButton("Edit", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int whichButton) {
	            	updatePayment();
	            }
	        });

		    alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int whichButton) {
	            	dialog.cancel();
	            }
	        });
		}
	    alert.show();
	}

	protected void updatePayment() {
		CDataProvider.getInstance().setPageStage(CConstants.C_PAGE_PAYMENT);
		startActivity(new Intent(ACBillStatus.this, ANewShared.class));
	}

	protected void sendNotification(int type, final ParseUser userTo, String msg) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("recipientId", userTo.getObjectId());
		params.put("message", msg);
		ParseCloud.callFunctionInBackground("sendPushToUser", params, new FunctionCallback<String>() {

			@Override
			public void done(String object, ParseException e) {
				if (e == null)
					Toast.makeText(getApplicationContext(), "Notification sent to: " + userTo.getObjectId(), Toast.LENGTH_SHORT).show();
				else
					Toast.makeText(getApplicationContext(), "Fail Notif: " + e.getMessage().toString(), Toast.LENGTH_LONG).show();
			}
		   
		});
	}
	
}
