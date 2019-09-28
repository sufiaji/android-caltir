package com.gravitazi.android.caltir.main;

import java.util.ArrayList;

import com.gravitazi.android.caltir.R;
import com.gravitazi.android.caltir.models.CAdapter;
import com.gravitazi.android.caltir.models.CConstants;
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

public class ANewAdditional extends Activity {
	
	private ArrayList<CItem> mListItems;
	private CAdapter mItemAdapter;
	private ListView mList;
	private static ANewAdditional mNewAdd;
	
	public static ANewAdditional getInstance() {
		return mNewAdd;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getActionBar().setTitle("Additional fees");
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_new_add);
		mList = (ListView) findViewById(R.id.idListItem);
		mList.setItemsCanFocus(true);
		CDataProvider.getInstance().initAddItems();
		mListItems = CDataProvider.getInstance().getAddItems();
		mItemAdapter = new CAdapter(this, R.layout.row_add, mListItems);
		mList.setAdapter(mItemAdapter);		
		mNewAdd = this;
	}
	
	public void onNext(View v) {
		// validation
		boolean isEmpty = false;
		for(CItem item : mListItems) {
			float priceTotal = item.getPriceTotal();
			String name = item.getName().toString().trim();
			if(priceTotal > 0 && !name.equals("")) {
				
			} else if(priceTotal == 0 && name.equals("")) {
				
			} else {
				isEmpty = true;
				break;
			}
		}	
		if(isEmpty) {
			Toast.makeText(getApplicationContext(), "Item name/price cannot be empty", Toast.LENGTH_SHORT).show();
		} else {			
			CDataProvider.getInstance().setPageStage(CConstants.C_PAGE_SHARED);
			CDataProvider.getInstance().setAddItems(mListItems);
			startActivity(new Intent(ANewAdditional.this, ANewShared.class));
		}			
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
		ANewTax.getInstance().finish();
		ANewItem.getInstance().finish();
		ANewSession.getInstance().finish();
		finish();
	}

	private void addItem() {
		CItem item = new CItem("", 1, 0);
		mListItems.add(item);
		mItemAdapter.notifyDataSetChanged();
		CDataProvider.getInstance().setAddItems(mListItems);
		mList.smoothScrollToPosition(mList.getCount());
	}
}
