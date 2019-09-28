package com.gravitazi.android.caltir.main;

import java.util.ArrayList;
import java.util.List;
import com.gravitazi.android.caltir.R;
import com.gravitazi.android.caltir.models.CDataProvider;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class ANewBillTo extends Activity {
	
	private ListView mList;
	private ArrayList<ParseUser> mPersons;
	private CPersonAdapter mAdapter;
	private ArrayList<Boolean> mSelectedPersonFlag;
	private ArrayList<ParseUser> mSelectedPerson;
	private EditText mEditSelectedPeople;
	private static ANewBillTo mNewBill;
	private ProgressBar mPb;
	
	public static ANewBillTo getInstance() {
		return mNewBill;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_billto);
		mList = (ListView) findViewById(R.id.idListItem);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		mEditSelectedPeople = (EditText) findViewById(R.id.idSelectedPeople);
//		mEditSelectedPeople.setText(ParseUser.getCurrentUser().getString("name"));
		mEditSelectedPeople.setText("You");
		mPersons = new ArrayList<ParseUser>();
		//
		mAdapter = new CPersonAdapter(this, R.layout.row_people, mPersons);
		mList.setAdapter(mAdapter);
		mSelectedPerson = new ArrayList<ParseUser>();
		mSelectedPersonFlag = new ArrayList<Boolean>();	
		mPb = (ProgressBar) findViewById(R.id.idProgress);
		mPb.setVisibility(View.INVISIBLE);
		mNewBill = this;
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
		switch (item.getItemId()) {			
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
		ANewAttach.getInstance().finish();
		ANewShared.getInstance().finish();
		ANewAdditional.getInstance().finish();
		ANewTax.getInstance().finish();
		ANewItem.getInstance().finish();
		ANewSession.getInstance().finish();
		finish();
	}

	public void onNext(View v) {
		mSelectedPerson.add(ParseUser.getCurrentUser());
		CDataProvider.getInstance().setPersonToBeBilled(mSelectedPerson);
//		startActivity(new Intent(ANewBillTo.this, ANewReminder.class));
		startActivity(new Intent(ANewBillTo.this, ANewSummary.class));
	}
	
	public void onFind(View v) {		
		EditText te = (EditText) findViewById(R.id.idEditFind);
		String name = te.getText().toString().trim();
		if(name != null) {
			hideKeyboard();
			mPb.setVisibility(View.VISIBLE);
			if(name.contains("@")) {
				findByEmail(name);
			} else {
				findByName(name);
			}
		}
	}
	

	private void hideKeyboard() {
		InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE); 

		inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                   InputMethodManager.HIDE_NOT_ALWAYS);		
	}

	private void findByName(final String pattern) {
		ParseQuery<ParseUser> query = ParseUser.getQuery();
    	query.whereContains("name", pattern);
    	query.whereNotEqualTo("name", ParseUser.getCurrentUser().getString("name"));
    	query.findInBackground(new FindCallback<ParseUser>() {
			
			@Override
			public void done(List<ParseUser> users, ParseException e) {
				mPersons.clear();
				if(e == null) {	
					if(users.size() == 0) {
						findByEmail(pattern);
					} else {
						for(ParseUser user : users) {
							mPersons.add(user);
						}
					}
					refreshList();
				} else {
					findByEmail(pattern);
				}
			}
		});
	}
	
	protected void findByEmail(String pattern) {
		ParseQuery<ParseUser> query = ParseUser.getQuery();
    	query.whereContains("email", pattern);
    	query.whereNotEqualTo("email", ParseUser.getCurrentUser().getEmail());
    	query.findInBackground(new FindCallback<ParseUser>() {
			
			@Override
			public void done(List<ParseUser> users, ParseException e) {
				mPersons.clear();
				if(e == null) {					
					for(ParseUser user : users) {
						mPersons.add(user);
					}
					refreshList();
				} else {
					Toast.makeText(getApplicationContext(), "Cannot find person with this email or username", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	protected void refreshList() {
		mPb.setVisibility(View.INVISIBLE);
		mAdapter.notifyDataSetChanged();
		mSelectedPersonFlag.clear();
		for(ParseUser user : mPersons)
			mSelectedPersonFlag.add(false);
	}

	public class CPersonAdapter extends ArrayAdapter<ParseUser> {
		
		private Context _context;
		private ArrayList<ParseUser> _persons;
		private int _layoutID;
		
		public CPersonAdapter(Context context, int resource, ArrayList<ParseUser> objects) {
			super(context, resource, objects);
			_context = context;
			_persons = objects;
			_layoutID = resource;			
		}
		
		public class viewHolderClass {
			
			private ImageView imgUser;
			private TextView tvName;
			private TextView tvEmail;
			private LinearLayout lin1;
			private LinearLayout lin2;
			
			public viewHolderClass(View v) {
				imgUser = (ImageView) v.findViewById(R.id.idImgUser);
				tvName = (TextView) v.findViewById(R.id.idName);
				tvEmail = (TextView) v.findViewById(R.id.idEmail);
				lin1 = (LinearLayout) v.findViewById(R.id.id_layout_row_people);
				lin2 = (LinearLayout) v.findViewById(R.id.id_layout_person);
			}
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			viewHolderClass holder = null;	
			if (row == null) {
				LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				row = inflater.inflate(_layoutID, parent, false);
				holder = new viewHolderClass(row);
				row.setTag(holder);
			} else {
				holder = (viewHolderClass) row.getTag();
			}
			
			final ParseUser user = _persons.get(position);				
			if (user != null) {	
				Bitmap bitmap;
				holder.tvName.setText(user.getString("name"));
				holder.tvEmail.setText(user.getEmail());
				holder.imgUser.setImageResource(R.drawable.ic_no_photo);
				final LinearLayout linx2 = holder.lin2;
				final int pos = position;
				if(mSelectedPersonFlag.get(position)) {
					holder.lin1.setBackgroundColor(getResources().getColor(R.color.blue_white));
					holder.lin2.setBackgroundColor(getResources().getColor(R.color.blue_white));
				} else {
					holder.lin1.setBackgroundColor(getResources().getColor(R.color.white));
					holder.lin2.setBackgroundColor(getResources().getColor(R.color.white));							
				}
				holder.lin1.setOnClickListener(new OnClickListener() {					
					@Override
					public void onClick(View v) {
						mSelectedPersonFlag.set(pos, !mSelectedPersonFlag.get(pos));
						LinearLayout li = (LinearLayout) v;
						if(mSelectedPersonFlag.get(pos)) {
							li.setBackgroundColor(getResources().getColor(R.color.blue_white));
							linx2.setBackgroundColor(getResources().getColor(R.color.blue_white));
							mSelectedPerson.add(user);
						} else {
							li.setBackgroundColor(getResources().getColor(R.color.white));
							linx2.setBackgroundColor(getResources().getColor(R.color.white));	
							mSelectedPerson.remove(user);
						}
//						mEditSelectedPeople.setText(ParseUser.getCurrentUser().getString("name"));
						mEditSelectedPeople.setText("You");
						for(int i=0;i<mSelectedPerson.size();i++) {
							mEditSelectedPeople.setText(mEditSelectedPeople.getText().toString() + ", " + mSelectedPerson.get(i).getString("name"));
						}
					}					
					
				});
				ParseFile avatar = (ParseFile) user.get("avatar");
				if(avatar != null) {
					byte[] imgData;
					try {
						imgData = avatar.getData();
						if (imgData != null) {
							bitmap = BitmapFactory.decodeByteArray(imgData , 0, imgData.length);
							holder.imgUser.setImageBitmap(bitmap);
						}
					} catch (ParseException e1) {
						e1.printStackTrace();
					}
				}
				
			}
			return row;
		}
		
	}

}
