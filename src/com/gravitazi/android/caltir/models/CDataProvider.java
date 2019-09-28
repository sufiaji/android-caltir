package com.gravitazi.android.caltir.models;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class CDataProvider {
	
	// singleton instance
	private static CDataProvider mDataProvider = new CDataProvider();
	
	private ArrayList<CItem> mMainItems;
	private ArrayList<CItem> mAddItems;
	private ArrayList<CItem> mSharedItems;
	private ArrayList<CItem> mPayItems;
	private ArrayList<ParseUser> mBillToPersons;
	private ArrayList<ParseObject> mSession;	
	private int mMainId;
	private int mAddId;
	private int mSharedId;	
	private String mRestoName;
	private String mCurr;
	private int mReminderTime;
	private String mReminderHour;
	private int mSelectedDetailPos;
	private String mPageName;
	private ParseUser mCreator;
	private ArrayList<CBillTo> mBillTo;
	private ParseFile mImageAttachment;	
	private float mTax;
	private float mTaxPrice;
	private boolean mReadOnly;

	private int mSelectedBillToPosition;
	
	private CDataProvider() {
		init();
	}
	
	public void init() {
		// the items
		mMainItems = new ArrayList<CItem>();
		mAddItems = new ArrayList<CItem>();
		mSharedItems = new ArrayList<CItem>();
		// the person to be billed
		mBillToPersons = new ArrayList<ParseUser>();
		// the rest
		mReadOnly = false;
		mTax = 0;
		mTaxPrice = 0;
		mReminderTime = 1;
		mReminderHour = "minute(s)";
		mCurr = "";
		mRestoName = "";
		mImageAttachment = null;
		// the session
		mSession = new ArrayList<ParseObject>();
		// ******** for displaying ...
		mPayItems = new ArrayList<CItem>();
		mCreator = null;
		//
		mMainId = 0;
		mAddId = 0;
		mSharedId = 0;
	}
	
	public void refresh() {
		// the items
		mMainItems = null;
		mAddItems = null;
		mSharedItems = null;
		// the person to be billed
		mBillToPersons = null;
		mImageAttachment = null;
		// the session
//		mSession = null;
		// ******** for displaying ...
		mPayItems = null;
		mCreator = null;
		
	}

	public static CDataProvider getInstance() {
		return mDataProvider;
	}
	
	public void setPersonCreator(ParseUser user) {
		mCreator = user;
	}
	
	public ParseUser getPersonCreator() {
		return mCreator;
	}
	
	// @Parse
    // *** To check directly from Parse.com whether user is exist or not
    public int parseCheckUserExist(ParseUser user) {
    	// find user from table _User in Parse with foreground mode
    	ParseQuery<ParseUser> query = ParseUser.getQuery();
    	query.whereEqualTo("objectId", user.getObjectId());
    	List<ParseUser> users = null;
    	try {
    		users = query.find();
		} catch (ParseException e) {
			e.printStackTrace();
		}
    	if(users == null) {
    		// no inet connection
    		return CConstants.C_NO_INET_CONNECTION;
    	}
    	if(users.size()==0) {
    		// user not exist in Parse.com
    		return CConstants.C_NO_USER_EXIST;
    	} else {
    		return CConstants.C_USER_EXIST;
    	}
    }
    
    public ArrayList<ParseObject> getHistory() {    	
    	return mSession;
    }
    
    public void setHistory(ArrayList<ParseObject> hist) {
    	mSession = hist;
    }
    
    public void setSessionName(String name) {
    	mRestoName = name;
    }
    
    public String getSessionName() {
    	return mRestoName;
    }
    
    public void setCurrency(String curr) {
    	mCurr = curr;
    }
    
    public String getCurrency() {
    	return mCurr;
    }
    
    public ArrayList<CItem> getMainItems() {
    	return mMainItems;
    }
    
    public ArrayList<CItem> getAddItems() {
    	return mAddItems;
    }
    
    public ArrayList<CItem> getSharedItems() {
    	return mSharedItems;
    }
    
    public ArrayList<CItem> getPayableItems() {
    	return mPayItems;
    }
    
    public void setMainItems(ArrayList<CItem> items) {
    	mMainItems = items;
    }
    
    public void setAddItems(ArrayList<CItem> items) {
    	mAddItems = items;
    }
    
    public void setSharedItems(ArrayList<CItem> items) {
    	mSharedItems = items;
    }
    
    public void setPayableItems(ArrayList<CItem> items) {
    	mPayItems = items;
    }
    
    public void addMainItems() {
    	CItem item = new CItem("", 0, 0);
    	mMainItems.add(item);
    }
    
    public void addAddItems() {
    	CItem item = new CItem("", 1, 0);
    	mAddItems.add(item);
    }
    
    public void addSharedItems() {
    	CItem item = new CItem("", 1, 0);
    	mSharedItems.add(item);
    }
    
    public void clearMainItems() {
    	mMainItems.clear();
    }
    
    public void clearAddItems() {
    	mAddItems.clear();
    }
    
    public void clearSharedItems() {
    	mSharedItems.clear();
    }
    
    public void initMainItems() {
    	mMainItems.clear();
    	mMainItems.add(new CItem("", 0, 0));
    }
    
    public void initAddItems() {
    	mAddItems.clear();
    	mAddItems.add(new CItem("", 1, 0));
    }
    
    public void initSharedItems() {
    	mSharedItems.clear();
    	mSharedItems.add(new CItem("", 0, 0));
    }
    
    public void initPayableItems() {
    	mPayItems.clear();
    	mPayItems.add(new CItem("", 0, 0));
    }

	public int getMainId() {
		return mMainId++;
	}
       
	public int getAddId() {
		return mAddId++;
	}
	
	public int getSharedId() {
		return mSharedId++;
	}
	
	public void setPersonToBeBilled(ArrayList<ParseUser> users) {
		mBillToPersons = users;
	}
	
	public ArrayList<ParseUser> getPersonToBeBilled() {
		return mBillToPersons;
	}
	
	public void setBillTo(ArrayList<CBillTo> bt) {
		mBillTo = bt;
	}
	
	public ArrayList<CBillTo> getBillTo() {
		return mBillTo;
	}
	
	public ArrayList<String> getAllMainItemName() {
		ArrayList<String> names = new ArrayList<String>();
		for(CItem item : mMainItems) {
			names.add(item.getName());
		}
		return names;
	}
	
//	public ArrayList<String> getItemsNamePayable(int pos) {
//		ArrayList<String> names = new ArrayList<String>();
//		ParseObject session = mSession.get(pos);
//		List<ParseObject> items = session.getList(CConstants.C_SESSION_ARRAY_ITEMS);
//		for(ParseObject item : items) {
//			names.add(item.getString(CConstants.C_ITEMS_NAME));
//		}
//		return names;
//	}
	
	public CItem getSingleMainItem(String name) {
		CItem item = new CItem();
		for(CItem it : mMainItems) {
			if(it.getName().equals(name)) {
				item = it;
				break;
			}
		}
		return item;
	}
	
	public void setReminderTime(int time) {
		mReminderTime = time;
	}
	
	public int getReminderTime() {
		return mReminderTime;
	}
	
	public void setReminderHour(String hour) {
		mReminderHour = hour;
	}
	
	public String getReminderHour() {
		return mReminderHour;
	}
	
	public void setAttachment(ParseFile f) {
		mImageAttachment = f;
	}
	
	public ParseFile getAttachment() {
		return mImageAttachment;
	}
	
	public void setSelectedSessionPosition(int i) {
		mSelectedDetailPos = i;
	}
	
	public int getSelectedSessionPosition() {
		return mSelectedDetailPos;
	}
	
//	public void setSelectedDetailItems(ArrayList<ParseObject> items) {
//		mSelectedDetailItems = items;
//	}
//	
//	public ArrayList<ParseObject> getSelectedDetailItems() {
//		return mSelectedDetailItems;
//	}
	
	public String getPageState() {
		return mPageName;
	}
	
	public void setPageStage(String name) {
		mPageName = name;
	}
	
	public void setTax(float t) {
		mTax = t;
	}
	
	public float getTax() {
		return mTax;
	}
	
	public void setTaxPrice(float t) {
		mTaxPrice = t;
	}
	
	public float getTaxPrice() {
		return mTaxPrice;
	}

	public static Bitmap decodeFileBitmapResize(Context c, Uri data, int maxWidth, int maxHeight) {
		String[] filePathColumn = { MediaStore.Images.Media.DATA }; 
		
        Cursor cursor = c.getContentResolver().query(data, filePathColumn, null, null, null);
        cursor.moveToFirst(); 
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();
		Bitmap b = null;
		
		// Get the dimensions of the original bitmap
		BitmapFactory.Options bmOptions= new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds= true;
		BitmapFactory.decodeFile(picturePath, bmOptions);
		int photoW= bmOptions.outWidth;
		int photoH= bmOptions.outHeight;

		// Determine how much to scale down the image. 
		int scaleFactor= (int) Math.max(1.0, Math.min((double) photoW / (double)maxWidth, (double)photoH / (double)maxHeight));    //1, 2, 3, 4, 5, 6, ...
		scaleFactor= (int) Math.pow(2.0, Math.floor(Math.log((double) scaleFactor) / Math.log(2.0)));               //1, 2, 4, 8, ...

		// Decode the image file into a Bitmap sized to fill the View
		bmOptions.inJustDecodeBounds= false;
		bmOptions.inSampleSize= scaleFactor;
		bmOptions.inPurgeable= true;

		do
		{
		    try
		    {
		        Log.d("tag", "scaleFactor: " + scaleFactor);
		        scaleFactor*= 2;
		        b= BitmapFactory.decodeFile(picturePath, bmOptions);
		    }
		    catch(OutOfMemoryError e)
		    {
		        bmOptions.inSampleSize= scaleFactor;
		        Log.d("tag", "OutOfMemoryError: " + e.toString());
		    }
		}
		while(b == null && scaleFactor <= 256);
		return b;
	}
	
	public static Bitmap resizeBitmap(Bitmap bitmap, float maxWidth, float maxHeight) {
		float width = bitmap.getWidth();
		float height = bitmap.getHeight();
		if (width > maxWidth) {
			height = (maxWidth / width) * height;
			width = maxWidth;
		}
		if (height > maxHeight) {
			width = (maxHeight / height) * width;
			height = maxHeight;
		}
		return Bitmap.createScaledBitmap(bitmap, (int) width, (int) height, true);
	}

	public void setPageReadOnly(boolean b) {
		mReadOnly = b;
	}
	
	public boolean getPageReadOnly() {
		return mReadOnly;
	}

	public void setSelectedBillToPosition(int position) {
		mSelectedBillToPosition = position;
	}
	
	public int getSelectedBillToPosition() {
		return mSelectedBillToPosition;
	}
}
