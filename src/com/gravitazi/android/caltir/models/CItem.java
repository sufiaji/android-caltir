package com.gravitazi.android.caltir.models;

public class CItem {
	
	private float mPrice = 0;
	private float mQty = 0;
	private String mName;
	private float mSinglePrice;
	private String mParseID;
	private int mLocalID;
	private float mTaxPrice;
	private int mItemType;
	
	public CItem() {
		
	}
	
	public CItem(String name, float qty, float price) {
		mPrice = price;
		mQty = qty;
		mName = name;
		mSinglePrice = getSinglePrice();
	}
	
	public CItem(String id, String name, float qty, float price) {
		mParseID = id;
		mPrice = price;
		mQty = qty;
		mName = name;
		mSinglePrice = getSinglePrice();
	}
	
//	public CItem(int id, String name, float qty, float price) {
//		mPrice = price;
//		mQty = qty;
//		mName = name;
//		mSinglePrice = getSinglePrice();
//		mLocalID = id;
//	}
	
	public int getLocalId() {
		return mLocalID;
	}
	
	public String getParseId() {
		return mParseID;
	}
	
	public void setLocalId(int id) {
		mLocalID = id;
	}
	
	public void setParseId(String id) {
		mParseID = id;
	}
	private float getSinglePrice() {
		if(mPrice == 0) return 0;
		if(mQty == 0) return 0;
		float singlePrice = 0;
		singlePrice = mPrice / mQty;
		return singlePrice;				
	}
	
	public float getPriceSingle() {
		return mSinglePrice;
	}
	
	public float getPriceTotal() {
		return mPrice;
	}
	
	public void setPriceTotal(float price) {
		mPrice = price;
		mSinglePrice = getSinglePrice();
	}
	
	public String getName() {
		return mName;
	}
	
	public void setName(String name) {
		mName = name;
	}
	
	public float getQty() {
		return mQty;
	}
	
	public void setQty(float qty) {
		mQty = qty;
		mSinglePrice = getSinglePrice();
	}
	
	public void setTaxPrice(float t) {
		mTaxPrice = t;
	}
	
	public float getTaxPrice() {
		return mTaxPrice;
	}
	
	public void setItemType(int t) {
		mItemType = t;
	}
	
	public int getItemType() {
		return mItemType;
	}

}
