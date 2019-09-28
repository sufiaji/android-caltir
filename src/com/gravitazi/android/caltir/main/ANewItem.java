package com.gravitazi.android.caltir.main;

import java.util.ArrayList;

import com.gravitazi.android.caltir.R;
import com.gravitazi.android.caltir.models.CAdapter;
import com.gravitazi.android.caltir.models.CDataProvider;
import com.gravitazi.android.caltir.models.CItem;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

public class ANewItem extends Activity {
	
	private ListView mList;
	private CAdapter mItemAdapter;
	private ArrayList<CItem> mItemsForList;
	private static ANewItem mNewItem;
	
	public static ANewItem getInstance() {
		return mNewItem;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_item);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		mList = (ListView) findViewById(R.id.idListItem);
		mList.setItemsCanFocus(true);
		CDataProvider.getInstance().initMainItems();
		mItemsForList = CDataProvider.getInstance().getMainItems();
		mItemAdapter = new CAdapter(this, R.layout.row_item, mItemsForList);
		mList.setAdapter(mItemAdapter);	
		mNewItem = this;
	}
	
	public void onNext(View v) {
		boolean isEmpty = false;
		CDataProvider.getInstance().setMainItems(mItemsForList);
		for(CItem item : mItemsForList) {
			if(item.getName().equals("") || item.getName() == null || item.getPriceTotal() == 0 || item.getQty() == 0) {
				isEmpty = true;
				break;
			}
		}
		if(isEmpty)
			Toast.makeText(getApplicationContext(), "Item name/price/quantity cannot be empty", Toast.LENGTH_SHORT).show();
		else
			startActivity(new Intent(ANewItem.this, ANewTax.class));
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_new, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
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
		ANewSession.getInstance().finish();
		finish();
	}

	private void addItem() {
		CItem item = new CItem("", 0, 0);
		mItemsForList.add(item);
		mItemAdapter.notifyDataSetChanged();
		CDataProvider.getInstance().setMainItems(mItemsForList);
		mList.smoothScrollToPosition(mList.getCount());		
	}
	
}
