package com.gravitazi.android.caltir.models;

import java.util.ArrayList;

import com.parse.ParseUser;

public class CBillTo {
	
	private ParseUser mUser;
	private float mTotalPrice;
	private ArrayList<CItem> mPaidItems;
	private int mStatus;
	private String mId;

	public CBillTo() {
		
	}
	
	public CBillTo(String id, ParseUser user, float totalPrice, ArrayList<CItem> items, int status) {
		mUser = user;
		mTotalPrice = totalPrice;
		mPaidItems = items;
		mStatus = status;
		mId = id;
	}
	
	public String getId() {
		return mId;
	}
	
	public void setId(String id) {
		mId = id;
	}
	
	public String getUsername() {
		return mUser.getString("name");		
	}
	
	public int getStatus() {
		return mStatus;
	}
	
	public float getPrice() {
		return mTotalPrice;
	}
	
	public ParseUser getParseUser() {
		return mUser;
	}
	
	public ArrayList<CItem> getItems() {
		return mPaidItems;
	}
}
