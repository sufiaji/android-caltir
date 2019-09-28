package com.gravitazi.android.caltir.main;

import java.util.ArrayList;
import java.util.List;

import com.gravitazi.android.caltir.R;
import com.gravitazi.android.caltir.activities.ACDetailMain;
import com.gravitazi.android.caltir.models.CBillTo;
import com.gravitazi.android.caltir.models.CConstants;
import com.gravitazi.android.caltir.models.CDataProvider;
import com.gravitazi.android.caltir.models.CItem;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class AMain extends Activity implements OnItemClickListener {
	
	private ListView mListView;
	private TextView mText;
	private ProgressBar mPb;
	private Button mBtnNext;
	private static AMain mMain;
	private CDataAdapter mAdapter;
	private ArrayList<ParseObject> mListSessions;
	
	public static AMain getInstance() {
		return mMain;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mListView = (ListView) findViewById(R.id.idListHistory);
		mText = (TextView) findViewById(R.id.idTextMain);
		mPb = (ProgressBar) findViewById(R.id.idProgress);	
		mBtnNext = (Button) findViewById(R.id.idButtonCreate);
		mText.setVisibility(View.INVISIBLE);
		mPb.setVisibility(View.VISIBLE);
		// list fill
		mListSessions = CDataProvider.getInstance().getHistory();				
		mAdapter = new CDataAdapter(this, R.layout.row_session, mListSessions);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);
		getSessionHistory(ParseUser.getCurrentUser().getObjectId());
		mMain = this;
	}
	
	private void getSessionHistory(String userID) {
		mText.setVisibility(View.INVISIBLE);
		mPb.setVisibility(View.VISIBLE);
		
    	ParseQuery<ParseObject> query1 = ParseQuery.getQuery(CConstants.T_SESSION);
    	query1.whereEqualTo(CConstants.C_SESSION_ID_CREATOR, userID);
    	
    	ParseQuery<ParseObject> query2 = ParseQuery.getQuery(CConstants.T_SESSION);
    	query2.whereEqualTo(CConstants.C_SESSION_ARRAY_BILLEDTO_USERS, ParseUser.getCurrentUser());
    	
    	ArrayList<ParseQuery<ParseObject>> qList = new ArrayList<ParseQuery<ParseObject>>();
    	qList.add(query1);
    	qList.add(query2);
    	
    	ParseQuery<ParseObject> query = ParseQuery.or(qList);
    	query.include("item");
    	query.include("billedTo");
    	query.include("billedTo.user");
    	query.setLimit(10);
    	query.findInBackground(new FindCallback<ParseObject>() {
			
			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				mListSessions.clear();
				if(e == null) {
					if(objects.size() > 0) {
						for(int i=0;i<objects.size();i++) {
							if(objects.get(i) != null) {
								mListSessions.add(objects.get(i));
							}
						}
						mText.setVisibility(View.INVISIBLE);
					} else {	
						mText.setVisibility(View.VISIBLE);
					}
					notifyDataChange();					
				} else {
					Toast.makeText(getApplicationContext(), "Error getting session histories", Toast.LENGTH_SHORT).show();
				}
				mPb.setVisibility(View.INVISIBLE);
			}
		});
	}

	protected void notifyDataChange() {
		mAdapter.notifyDataSetChanged();
		CDataProvider.getInstance().setHistory(mListSessions);		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.idMenuProfile:
				callProfilePage();
				return true;
			case R.id.idMenuRefresh:
				getSessionHistory(ParseUser.getCurrentUser().getObjectId());
				return true;
			case R.id.idMenuLogout:
				ParseUser.logOut();			
				startActivity(new Intent(AMain.this, ALogin.class));
				finish();
				return true;
			case R.id.idMenuHelp:
				startActivity(new Intent(AMain.this, AHelp.class));
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void callProfilePage() {
		Intent i = new Intent(AMain.this, AProfile.class);
		startActivity(i);
	}

	public void onCreateSession(View v) {
		CDataProvider.getInstance().refresh();
		CDataProvider.getInstance().init();
		callRestoPage();
	}

	private void callRestoPage() {
		startActivity(new Intent(AMain.this, ANewSession.class));
	}
	
	private class CDataAdapter extends ArrayAdapter<ParseObject> {
		
		private Context _context;
		private ArrayList<ParseObject> _list;
		private int _layoutID;

		public CDataAdapter(Context context, int resource, ArrayList<ParseObject> objects) {
			super(context, resource, objects);
			_context = context;
			_list = objects;
			_layoutID = resource;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			if(convertView == null) {
				LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(_layoutID, null);
			}
			
			ParseObject session = _list.get(position);
			if(session != null) {
				TextView tDate = (TextView) convertView.findViewById(R.id.idDateTime);
				tDate.setText(session.getCreatedAt().toString());
				TextView tName = (TextView) convertView.findViewById(R.id.idName);
				tName.setText(session.getString("sessionName"));
				TextView tPrice = (TextView) convertView.findViewById(R.id.idPrice);			
//				tPrice.setText(session.getString("currency") + " " + Double.toString(session.getDouble("priceTotal")));
				tPrice.setText(Double.toString(session.getDouble("priceTotal")));
				
				if(!session.getString(CConstants.C_SESSION_ID_CREATOR).equals(ParseUser.getCurrentUser().getObjectId())){
					TextView tIsCreator = (TextView) convertView.findViewById(R.id.idIsCreator);
					tIsCreator.setVisibility(View.GONE);
				}
				
				ImageView imStatus = (ImageView) convertView.findViewById(R.id.idStatus);
				if(session.getInt(CConstants.C_SESSION_STATUS) == CConstants.C_STATUS_INIT)
					imStatus.setBackgroundColor(getResources().getColor(R.color.red1));
				else if(session.getInt(CConstants.C_SESSION_STATUS) == CConstants.C_STATUS_PARTIAL)
					imStatus.setBackgroundColor(getResources().getColor(R.color.yellow));
				else if(session.getInt(CConstants.C_SESSION_STATUS) == CConstants.C_STATUS_DONE)
					imStatus.setBackgroundColor(getResources().getColor(R.color.green1));
				
				ImageView imb = (ImageView) convertView.findViewById(R.id.idImgB);
				ParseFile f = session.getParseFile(CConstants.C_SESSION_IMAGE);
				if(f != null) {
					try {
						byte[] data;
						data = f.getData();
						Bitmap b = BitmapFactory.decodeByteArray(data , 0, data.length);
						imb.setImageBitmap(b);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					imb.setImageDrawable(getResources().getDrawable(R.drawable.no_image));
				}
				
			}
			return convertView;
		}
		
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {				
		mPb.setVisibility(View.VISIBLE);	
		mBtnNext.setEnabled(false);
		CDataProvider.getInstance().setSelectedSessionPosition(position);
		CAsyncTask task = new CAsyncTask();
		task.execute();
	}
	
	private class CAsyncTask extends AsyncTask<Void, Void, Void> {
		
		private boolean isSuccess = true;
		
		@SuppressWarnings("unused")
		@Override
		protected Void doInBackground(Void... arg0) {
			// populate items to CDataProvider
			ArrayList<CItem> mainItems = new ArrayList<CItem>();
			ArrayList<CItem> addItems = new ArrayList<CItem>();
			ArrayList<CItem> sharedItems = new ArrayList<CItem>();
			// get selected session
			ParseObject session = mListSessions.get(CDataProvider.getInstance().getSelectedSessionPosition());
			// get attachment
			CDataProvider.getInstance().setAttachment(session.getParseFile(CConstants.C_SESSION_IMAGE));
			// get tax
			CDataProvider.getInstance().setTax((float) session.getDouble(CConstants.C_SESSION_TAXPERCENT));
			CDataProvider.getInstance().setTaxPrice((float) session.getDouble(CConstants.C_SESSION_TAXPRICE));
			// get Items:
			// each session will have MainItems, SharedItems and AdditionalItems
			List<ParseObject> sessionItems = session.getList("items");
			@SuppressWarnings("unchecked")
			List<ParseObject> sessionItems2 = (List<ParseObject>) session.get("items");
			int i1 = sessionItems.size();
			int i2 = sessionItems2.size();
			// loop for all Items
			for(ParseObject sessionItem : sessionItems) {
				try {
					// fetch each Item
					sessionItem.fetchIfNeeded();
					// name of Item
					String name = sessionItem.getString(CConstants.C_ITEMS_NAME);
					// qty of Item 
					float qty = (float) sessionItem.getDouble(CConstants.C_ITEMS_QTY);
					// price of Item
					float price = (float) sessionItem.getDouble(CConstants.C_ITEMS_PRICE);
					// populate Item to CItem: MainItem, AddItem, SharedItem
					CItem item = new CItem(sessionItem.getObjectId(), name, qty, price);
					// get Main Item
					if(!sessionItem.getBoolean(CConstants.C_ITEMS_IS_ADDITIONAL)) {
						mainItems.add(item);
						float qtyShared = (float) sessionItem.getDouble(CConstants.C_ITEMS_QTY_BILL_ALL);
						// get Shared Item
						if(qtyShared > 0) {							
							CItem shItem = new CItem(item.getParseId(), item.getName(), qtyShared, (qtyShared * price / qty));
							sharedItems.add(shItem);
						}
					}
					// get Additional Item
					else
						addItems.add(item);
				} catch (ParseException e) {
					e.printStackTrace();
					isSuccess = false;
					break;
				}
			}
			CDataProvider.getInstance().setMainItems(mainItems);
			CDataProvider.getInstance().setAddItems(addItems);
			CDataProvider.getInstance().setSharedItems(sharedItems);
			
			// populate persons to be billed to 
			ArrayList<ParseUser> billToUsers = new ArrayList<ParseUser>();	
			// items
			ArrayList<CItem> itemArray = new ArrayList<CItem>();
			// biullto
			ArrayList<CBillTo> billToArray = new ArrayList<CBillTo>();
			// get BilledTo (parse class)
			List<ParseObject> billedTos = session.getList("billedTo");
			for(ParseObject billedTo : billedTos) {
				try {
					// fetch each BilledTo class
					billedTo.fetchIfNeeded();
					// get BilledTo-user list (list of parse User) and populate this User class into billToUsers object
					List<ParseUser> users = billedTo.getList(CConstants.C_BILLTO_ARRAY_USER);					
					users.get(0).fetchIfNeeded(); 
					billToUsers.add(users.get(0));
					// each BilledTo row contains ItemPay class...
					// get ItemPay
					List<ParseObject> itemPays = billedTo.getList(CConstants.C_BILLTO_ARRAY_ITEM);
					if(itemPays != null && itemPays.size() > 0) {
						itemArray.clear();
						for(ParseObject itemPay : itemPays) {
							// fetch each itemPay
							itemPay.fetchIfNeeded();
							// populate itemPay to CItem							
							CItem item = new CItem(itemPay.getString(CConstants.C_ITEMPAY_ITEM_NAME), 
									   (float) itemPay.getDouble(CConstants.C_ITEMPAY_ITEM_QTY), 
										(float) itemPay.getDouble(CConstants.C_ITEMPAY_ITEM_PRICE));
							item.setParseId(itemPay.getObjectId());	
							item.setItemType(itemPay.getInt(CConstants.C_ITEMPAY_ITEMTYPE));
							// MainItem -> itemType = 1 -> need tax if any
							// SharedItem -> itemType = 2 -> need tax if any
							// AdditionalItem -> ItemType = 3;
							if(itemPay.getInt(CConstants.C_ITEMPAY_ITEMTYPE) == 1) {
								float taxPrice = (float) itemPay.getDouble(CConstants.C_ITEMPAY_TAXPRICE);
								item.setTaxPrice(taxPrice);
							} else if(itemPay.getInt(CConstants.C_ITEMPAY_ITEMTYPE) == 2) {
								float taxPrice = (float) itemPay.getDouble(CConstants.C_ITEMPAY_TAXPRICE);
								item.setTaxPrice(taxPrice);
							} else if(itemPay.getInt(CConstants.C_ITEMPAY_ITEMTYPE) == 3) {
								item.setTaxPrice(0);								
							}
							itemArray.add(item);
						}
					} else {
						itemArray.clear();
					}
//					ArrayList<CItem> itemArrayPut = new ArrayList<CItem>();
//					itemArrayPut = 
					// populate BilledTo to CBillTo
					// CBillTo is actually analog to BilledTo class...
					// which itemArray analog to ItemPay class.
					ArrayList<CItem> itemArrayPut = new ArrayList<CItem>();
					if(itemArray.size() > 0) {
						for(CItem item : itemArray) {
							itemArrayPut.add(item);
						}
					}
					CBillTo cBillTo = new CBillTo(billedTo.getObjectId(), 
												  users.get(0), 
												// already include Tax
												  (float) billedTo.getDouble(CConstants.C_BILLTO_PRICE), 
												  itemArrayPut, 
												  billedTo.getInt(CConstants.C_BILLTO_STATUS));
					billToArray.add(cBillTo);
					// end
				} catch (ParseException e) {
					e.printStackTrace();
					isSuccess = false;
					break;
				}
			}
			// contains ParseUser in BillTo class
			CDataProvider.getInstance().setPersonToBeBilled(billToUsers);
			// contains the payable items and its prices and also the corresponding users
			CDataProvider.getInstance().setBillTo(billToArray);
			
			// save person who create this session
			List<ParseUser> userCreators = session.getList("userCreator");
			ParseUser userCreator = userCreators.get(0);
			try {
				userCreator.fetchIfNeeded();
				CDataProvider.getInstance().setPersonCreator(userCreator);
			} catch (ParseException e) {
				e.printStackTrace();
				isSuccess = false;
			}
			
			// get reminder
//			CDataProvider.getInstance().setReminderTime(session.getInt(CConstants.C_SESSION_REMINDERTIME));
//			CDataProvider.getInstance().setReminderHour(session.getString(CConstants.C_SESSION_REMINDERHOUR));
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			mPb.setVisibility(View.INVISIBLE);
			mBtnNext.setEnabled(true);
			if(isSuccess) {				
				startActivity(new Intent(AMain.this, ACDetailMain.class));
			}
			else {
				Toast.makeText(getApplicationContext(), "Connection reset. Please try again later", Toast.LENGTH_SHORT).show();
			}
		}
	}
		
}
