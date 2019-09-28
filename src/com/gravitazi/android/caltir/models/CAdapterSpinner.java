package com.gravitazi.android.caltir.models;

import java.util.ArrayList;

import com.gravitazi.android.caltir.R;
import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

public class CAdapterSpinner extends ArrayAdapter<CItem> {
		
	private ArrayList<CItem> _list;
	private Context _context;
	private int _layoutID;
	ArrayList<String> spinnerList;

	public CAdapterSpinner(Context context, int resource, ArrayList<CItem> list) {
		super(context, resource, list);
		_list = list;
		_context = context;
		_layoutID = resource;
		spinnerList = CDataProvider.getInstance().getAllMainItemName();
	}
					
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		CItem item = _list.get(position);
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(_layoutID, null);
			Spinner spinnerName = (Spinner) convertView.findViewById(R.id.idRowSpin);
			spinnerName.setOnItemSelectedListener(new COnItemSelectedListener(convertView));
			EditText eqty = (EditText) convertView.findViewById(R.id.idRowQty);
			eqty.addTextChangedListener(new CTextWatcher(convertView, R.id.idRowQty));
		}
		
		ArrayAdapter<String> sAdapter = new ArrayAdapter<String>(_context, android.R.layout.simple_spinner_item, spinnerList);
		sAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		Spinner spinnerName = (Spinner) convertView.findViewById(R.id.idRowSpin);
		spinnerName.setTag(item);
		spinnerName.setAdapter(sAdapter);
		if(!item.getName().equals("")) {
			for(int i=0;i<spinnerList.size();i++) {
				String name = spinnerList.get(i);
				if(item.getName().equals(name)) {
					spinnerName.setSelection(i);
				}
			}
		}
		
		EditText eqty = (EditText) convertView.findViewById(R.id.idRowQty);
		eqty.setTag(item);
		eqty.setText(String.valueOf(item.getQty()));
		
		ImageButton btnDel = (ImageButton) convertView.findViewById(R.id.idRowDel);
		btnDel.setTag(item);
		btnDel.setOnClickListener(new OnClickListener() {
			   
		       @Override
		       public void onClick(View v) 
		       {
		    	   ImageButton btnDel = (ImageButton) v;
		    	   CItem item  = (CItem) btnDel.getTag();
		    	   _list.remove(item);
		    	   notifyList();
		       }

		    });	
		
		return convertView;
	}
	
	private void notifyList() {
		notifyDataSetChanged();
	}
	
	private class COnItemSelectedListener implements OnItemSelectedListener {
		
		private View view;
		
		public COnItemSelectedListener(View v) {
			view = v;
		}

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			Spinner spinnerName = (Spinner) view.findViewById(R.id.idRowSpin);
			CItem item = (CItem) spinnerName.getTag();
			if(spinnerList != null && spinnerList.size() > 0) {
				String selectedName = spinnerList.get(arg2).toString();
				item.setName(selectedName);
				EditText tqty = (EditText) view.findViewById(R.id.idRowQty);
				String sQty = tqty.getText().toString().trim();
				if(sQty.length() > 0) {
					try {
						float qty = Float.valueOf(sQty);
						CItem mainItem = CDataProvider.getInstance().getSingleMainItem(selectedName);
						if(mainItem != null) {
							float price = mainItem.getPriceSingle() * qty;
							item.setPriceTotal(price);	
							EditText tprice = (EditText) view.findViewById(R.id.idRowPrice);
							tprice.setText(String.valueOf(price));
						}
					} catch (Exception e) {
					}
				}
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			Spinner spinnerName = (Spinner) view.findViewById(R.id.idRowSpin);
			CItem item = (CItem) spinnerName.getTag();
			if(spinnerList != null && spinnerList.size() > 0) {
				String selectedName = spinnerList.get(0).toString();
				item.setName(selectedName);
				EditText tqty = (EditText) view.findViewById(R.id.idRowQty);
				String sQty = tqty.getText().toString().trim();
				if(sQty.length() > 0) {
					try {
						float qty = Float.valueOf(sQty);
						CItem mainItem = CDataProvider.getInstance().getSingleMainItem(selectedName);
						if(mainItem != null) {
							float price = mainItem.getPriceSingle() * qty;
							item.setPriceTotal(price);	
							EditText tprice = (EditText) view.findViewById(R.id.idRowPrice);
							tprice.setText(String.valueOf(price));
						}
					} catch (Exception e) {
					}
				}
			}
		}		
	}
	
	private class CTextWatcher implements TextWatcher {
		
		private View view;
		private int resId;
		
		public CTextWatcher(View v, int id) {
			view = v;
			resId = id;
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		   //do nothing
		}
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
		   //do nothing
		}
		
		@Override
		public void afterTextChanged(Editable s) {
			EditText edt = (EditText) view.findViewById(resId);
			CItem item = (CItem) edt.getTag();
			if(resId == R.id.idRowName) {				
				item.setName(s.toString().trim());
			} else if(resId == R.id.idRowPrice) {
//				String price = s.toString().trim();
//				if(price.length() > 0) {
//					item.setPriceTotal(Float.valueOf(price));
//				}
			} else if(resId == R.id.idRowQty) {
				String sqty = s.toString().trim();
				if(sqty.length() > 0) {
					float qty = Float.valueOf(sqty);
					item.setQty(Float.valueOf(qty));
					Spinner spinnerName = (Spinner) view.findViewById(R.id.idRowSpin);
					String name = spinnerName.getSelectedItem().toString();
					CItem mainItem = CDataProvider.getInstance().getSingleMainItem(name);
					if(mainItem != null) {
						if(qty <= mainItem.getQty()) {
							float price = mainItem.getPriceSingle() * qty;
							item.setPriceTotal(price);	
							EditText tprice = (EditText) view.findViewById(R.id.idRowPrice);
							tprice.setText(String.valueOf(price));
							EditText tqty = (EditText) view.findViewById(resId);
							tqty.setBackgroundColor(Color.WHITE);
						} else {
							EditText tqty = (EditText) view.findViewById(resId);
							tqty.setBackgroundColor(Color.RED);
							Toast.makeText(_context, "Quantity cannot more than " + mainItem.getQty(), Toast.LENGTH_SHORT).show();
						}
					}
				}
			}
		}
	}
	
}