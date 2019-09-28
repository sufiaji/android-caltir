package com.gravitazi.android.caltir.main;

import java.util.ArrayList;

import com.gravitazi.android.caltir.R;
import com.gravitazi.android.caltir.activities.ACSummaryPayment;
import com.gravitazi.android.caltir.models.CAdapterSpinner;
import com.gravitazi.android.caltir.models.CBillTo;
import com.gravitazi.android.caltir.models.CConstants;
import com.gravitazi.android.caltir.models.CDataProvider;
import com.gravitazi.android.caltir.models.CItem;
import com.parse.ParseUser;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ANewShared extends Activity {
	
	private ListView mList;
	private CAdapterSpinner mItemAdapter;
	private ArrayList<CItem> mListItems;
	private static ANewShared mNewShared;
	private CBillTo mSelectedBillTo;
	
	public static ANewShared getInstance() {
		return mNewShared;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_shared);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		mList = (ListView) findViewById(R.id.idListItem);
		mList.setItemsCanFocus(true);		
		mNewShared = this;
		preparePage();		
	}
	
	private void preparePage() {
		if(CDataProvider.getInstance().getPageState().equals(CConstants.C_PAGE_PAYMENT)) {
			TextView tv = (TextView) findViewById(R.id.idTitleShared);
			tv.setText(CConstants.C_TITLE_PAYMENT);
			ArrayList<CBillTo> CBillTos = CDataProvider.getInstance().getBillTo();
			for(CBillTo bt : CBillTos) {
				String objid = bt.getParseUser().getObjectId();
				String curjid = ParseUser.getCurrentUser().getObjectId();
				if(objid.equals(curjid)) {
					mSelectedBillTo = bt;
					break;
				}
			}
			if(mSelectedBillTo.getParseUser().getObjectId().equals(ParseUser.getCurrentUser().getObjectId()) && mSelectedBillTo.getItems().size() > 0) {
				// want to update
				// convert array of ItemPay to array of CItem (for update payable item purpose)
				ArrayList<CItem> items = mSelectedBillTo.getItems();
				ArrayList<CItem> mainItem = new ArrayList<CItem>();
				for(CItem item:items) {
					if(item.getItemType() == 1) {
						mainItem.add(item);
					}
				}
				CDataProvider.getInstance().setPayableItems(mainItem);
			} else {
				// want to pay for the first time
				CDataProvider.getInstance().initPayableItems();
			}
			mListItems = CDataProvider.getInstance().getPayableItems();
		} else if(CDataProvider.getInstance().getPageState().equals(CConstants.C_PAGE_SHARED)) {
			CDataProvider.getInstance().initSharedItems();
			mListItems = CDataProvider.getInstance().getSharedItems();
		}
		mItemAdapter = new CAdapterSpinner(this, R.layout.row_shared, mListItems);
		mList.setAdapter(mItemAdapter);
	}

	public void onNext(View v) {
		if(CDataProvider.getInstance().getPageState().equals(CConstants.C_PAGE_PAYMENT)) {
			startActivity(new Intent(ANewShared.this, ACSummaryPayment.class));
		}
		else {	
			boolean ck = checkItemValidity();
			if(ck) {
				CDataProvider.getInstance().setSharedItems(mListItems);
				startActivity(new Intent(ANewShared.this, ANewAttach.class));
			} else {
				Toast.makeText(getApplicationContext(), "Please input correct quantity.", Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	private boolean checkItemValidity() {
		boolean isOk = true;
		ArrayList<CItem> mainItems = CDataProvider.getInstance().getMainItems();
		for(CItem sItem : mListItems) {
			for(CItem mItem : mainItems) {
				if(mItem.getName().equals(sItem.getName())) {
					if(sItem.getQty() > mItem.getQty()) {
						isOk = false;
						break;
					}
				}
			}
			if(!isOk) break;
		}
		return isOk;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_new, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
			case R.id.idMenuAdd:
				addItem();
				return true;
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
		ANewAdditional.getInstance().finish();
		ANewTax.getInstance().finish();
		ANewItem.getInstance().finish();
		ANewSession.getInstance().finish();
		finish();
	}

	private void addItem() {
		CItem item = new CItem("", 0, 0);
		mListItems.add(item);
		mItemAdapter.notifyDataSetChanged();
		if(CDataProvider.getInstance().getPageState().equals(CConstants.C_PAGE_SHARED))
			CDataProvider.getInstance().setSharedItems(mListItems);
		else if(CDataProvider.getInstance().getPageState().equals(CConstants.C_PAGE_PAYMENT))
			CDataProvider.getInstance().setPayableItems(mListItems);
		mList.smoothScrollToPosition(mList.getCount());
	}
	
//	public class CItemAdapter extends ArrayAdapter<CItem> {
//		
//		private ArrayList<CItem> _list;
//		private Context _context;
//		private int _layoutID;
//		ArrayList<String> spinnerList;
//		private EditText vn;
//
//		public CItemAdapter(Context context, int resource, ArrayList<CItem> list) {
//			super(context, resource, list);
//			_list = list;
//			_context = context;
//			_layoutID = resource;
//			spinnerList = CDataProvider.getInstance().getAllMainItemName();
//		}
//		
//		public class viewHolderClass {
//			
//			private Spinner spinnerName;
//			private EditText tqty;
//			private EditText tprice;
//			private ImageButton bdel;
//			
//			public viewHolderClass(View v) {
//				spinnerName = (Spinner) v.findViewById(R.id.idRowSpin);
//				tqty = (EditText) v.findViewById(R.id.idRowQty);
//				tprice = (EditText) v.findViewById(R.id.idRowPrice);
//				bdel = (ImageButton) v.findViewById(R.id.idRowDel);
//				vn = tqty;
//			}
//		}
//		
//		@Override
//		public View getView(int position, View convertView, ViewGroup parent) {
//			
//			View row = convertView;
//			viewHolderClass holder = null;	
//			
//			if (row == null) {
//				LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//				row = inflater.inflate(_layoutID, parent, false);
//				holder = new viewHolderClass(row);
//				row.setTag(holder);
//			} else {
//				holder = (viewHolderClass) row.getTag();
//			}
//			
//			vn.setTag(position);
//			final viewHolderClass otherHolder = holder;
//			final CItem item = _list.get(position);						
//			
//			if (_list.get(position) != null) {	
//				// set spinner contents
//				ArrayAdapter<String> sAdapter = new ArrayAdapter<String>(_context, android.R.layout.simple_spinner_item, spinnerList);
//				sAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//				holder.spinnerName.setAdapter(sAdapter);
//				if(!_list.get(position).getName().equals("")) {
//					for(int i=0;i<spinnerList.size();i++) {
//						String name = spinnerList.get(i);
//						if(_list.get(position).getName().equals(name)) {
//							holder.spinnerName.setSelection(i);
//						}
//					}
//				}	
//				// spinner listener
//				holder.spinnerName.setOnItemSelectedListener(new OnItemSelectedListener() {
//
//					@Override
//					public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
//						if(spinnerList != null && spinnerList.size() > 0) {
//							String selectedName = spinnerList.get(arg2).toString();
//							item.setName(selectedName);
//							String sQty = otherHolder.tqty.getText().toString().trim();
//							if(sQty.length() > 0) {
//								try {
//									float qty = Float.valueOf(sQty);
//									CItem mainItem = CDataProvider.getInstance().getSingleMainItem(selectedName);
//									if(mainItem != null) {
//										float price = mainItem.getPriceSingle() * qty;
//										item.setPriceTotal(price);	
//										otherHolder.tprice.setText(String.valueOf(price));
//									}
//								} catch (Exception e) {
//								}
//							}
//							
//						}
//					}
//
//					@Override
//					public void onNothingSelected(AdapterView<?> arg0) {
//						if(spinnerList != null && spinnerList.size() > 0) {
//							String selectedName = spinnerList.get(0).toString();
//							item.setName(selectedName);
//							String sQty = otherHolder.tqty.getText().toString().trim();
//							if(sQty.length() > 0) {
//								try {
//									float qty = Float.valueOf(sQty);
//									CItem mainItem = CDataProvider.getInstance().getSingleMainItem(selectedName);
//									if(mainItem != null) {
//										float price = mainItem.getPriceSingle() * qty;
//										item.setPriceTotal(price);	
//										otherHolder.tprice.setText(String.valueOf(price));
//									}
//								} catch (Exception e) {
//								}
//							}
//						}
//					}
//				});
//				
//				// Quantity listener
//				holder.tqty.setText(String.valueOf(item.getQty()));
//				holder.tqty.addTextChangedListener(new TextWatcher() {					
//					@Override
//					public void onTextChanged(CharSequence s, int start, int before, int count) {
//						final String qtyString = s.toString().trim(); 
//						if(qtyString.length() > 0) {							
//							float qty = Float.valueOf(qtyString);
//							item.setQty(qty);
//							String name = otherHolder.spinnerName.getSelectedItem().toString();
//							CItem mainItem = CDataProvider.getInstance().getSingleMainItem(name);
//							if(mainItem != null) {
//								float price = mainItem.getPriceSingle() * qty;
//								item.setPriceTotal(price);	
//								otherHolder.tprice.setText(String.valueOf(price));
//							}
//						} else {
//							otherHolder.tprice.setText(String.valueOf(0));
//						}
//					}
//					
//					@Override
//					public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//						
//					}
//					
//					@Override
//					public void afterTextChanged(Editable s) {
//						final String qtyString = s.toString().trim(); 
//						if(qtyString.length() > 0) {							
//							float qty = Float.valueOf(qtyString);
//							item.setQty(qty);
//							String name = otherHolder.spinnerName.getSelectedItem().toString();
//							CItem mainItem = CDataProvider.getInstance().getSingleMainItem(name);
//							if(mainItem != null) {
//								float price = mainItem.getPriceSingle() * qty;
//								item.setPriceTotal(price);	
//								otherHolder.tprice.setText(String.valueOf(price));
//							}
//						} else {
//							otherHolder.tprice.setText(String.valueOf(0));
//						}
//					}
//				});
//				
//				holder.bdel.setOnClickListener(new OnClickListener() {
//				   
//			       @Override
//			       public void onClick(View v) 
//			       {
//			    	   int i = (Integer) vn.getTag();
//			    	   removeItem(i);
//			       }
//			    });						
//			}
//			return row;
//		}
//		
//	}
	
	private void removeItem(int p) {
		mListItems.remove(p);
		mItemAdapter.notifyDataSetChanged();
		if(CDataProvider.getInstance().getPageState().equals(CConstants.C_PAGE_SHARED))
			CDataProvider.getInstance().setSharedItems(mListItems);
		else if(CDataProvider.getInstance().getPageState().equals(CConstants.C_PAGE_PAYMENT))
			CDataProvider.getInstance().setPayableItems(mListItems);		
	}

}
