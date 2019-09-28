package com.gravitazi.android.caltir.models;

import java.util.ArrayList;

import com.gravitazi.android.caltir.R;
import com.gravitazi.android.caltir.R.id;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class CAdapter extends ArrayAdapter<CItem> {
		
	private ArrayList<CItem> _list;
	private Context _context;
	private int _layoutID;

	public CAdapter(Context context, int resource, ArrayList<CItem> list) {
		super(context, resource, list);
		_list = list;
		_context = context;
		_layoutID = resource;
	}
					
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		CItem item = _list.get(position);
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(_layoutID, null);
			EditText ename = (EditText) convertView.findViewById(R.id.idRowName);
			ename.addTextChangedListener(new CTextWatcher(convertView, R.id.idRowName));
			EditText eqty = (EditText) convertView.findViewById(R.id.idRowQty);
			eqty.addTextChangedListener(new CTextWatcher(convertView, R.id.idRowQty));
			EditText eprice = (EditText) convertView.findViewById(R.id.idRowPrice);
			eprice.addTextChangedListener(new CTextWatcher(convertView, R.id.idRowPrice));			
		}	
		
		EditText ename = (EditText) convertView.findViewById(R.id.idRowName);
		ename.setTag(item);
		ename.setText(item.getName());
		
		EditText eqty = (EditText) convertView.findViewById(R.id.idRowQty);
		eqty.setTag(item);
		eqty.setText(String.valueOf(item.getQty()));
		
		EditText eprice = (EditText) convertView.findViewById(R.id.idRowPrice);
		eprice.setTag(item);
		eprice.setText(String.valueOf(item.getPriceTotal()));

		ImageButton btnDel = (ImageButton) convertView.findViewById(R.id.idRowDel);
		btnDel.setTag(item);
		btnDel.setOnClickListener(new OnClickListener() {
			   
		       @Override
		       public void onClick(View v) 
		       {
		    	   if(_list.size() > 1) {
			    	   ImageButton btnDel = (ImageButton) v;
			    	   CItem item  = (CItem) btnDel.getTag();
			    	   _list.remove(item);
			    	   notifyList();
		    	   } else {
		    		   Toast.makeText(_context, "Cannot be deleted since this is the only item", Toast.LENGTH_SHORT).show();
		    	   }
		       }

		    });	
		
		return convertView;
	}
	
	private void notifyList() {
		notifyDataSetChanged();
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
				String price = s.toString().trim();
				if(price.length() > 0) {
					item.setPriceTotal(Float.valueOf(price));
				}
			} else if(resId == R.id.idRowQty) {
				String qty = s.toString().trim();
				if(qty.length() > 0) {
					item.setQty(Float.valueOf(qty));
				}
			}
		}
	}
	
}