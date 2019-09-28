package com.gravitazi.android.caltir.models;

public class CConstants {
	
	public static final int C_USER_EXIST = 1;
	public static final int C_NO_USER_EXIST = 2;
	public static final int C_NO_INET_CONNECTION = 3;
	public static final int C_STATUS_INIT = 1;
	public static final int C_STATUS_PARTIAL = 2;	
	public static final int C_STATUS_DONE = 3;
	
	public static final String C_PAGE_PAYMENT = "PAGEPAYMENT";
	public static final String C_PAGE_SHARED = "PAGESHARED";
	public static final String C_PAGE_APPROVAL = "PAGEAPPROVAL";
//	public static final String C_PAGE_SUMMARY_PAYMENT = "PAGESUMMARYPAYMENT";
	public static final String C_TITLE_PAYMENT = "Select item(s) to pay for...";
	
	
	public static final String T_SESSION = "Session";
	public static final String T_BILLEDTO = "BilledTo";
	public static final String T_ITEM = "Item";
	
	public static final String C_INSTAL_USERID = "userID";
	
	public static final String C_SESSION_NAME = "sessionName";
	public static final String C_SESSION_ARRAY_CREATOR = "userCreator";
	public static final String C_SESSION_ID_CREATOR = "userIDCreator";	
	public static final String C_SESSION_ARRAY_ITEMS = "items";
	public static final String C_SESSION_ARRAY_BILLEDTO = "billedTo";
	public static final String C_SESSION_ARRAY_BILLEDTO_USERS = "billedToUser";
	public static final String C_SESSION_PRICE_TOTAL = "priceTotal";
	public static final String C_SESSION_CURRENCY = "currency";
	public static final String C_SESSION_REMINDERTIME = "reminderTime";
	public static final String C_SESSION_REMINDERHOUR = "reminderHour";
	public static final String C_SESSION_STATUS = "status";
	public static final String C_SESSION_IMAGE = "image";
	public static final String C_SESSION_TAXPERCENT = "taxPercent";
	public static final String C_SESSION_TAXPRICE = "taxPrice";
	
	public static final String C_BILLTO_ARRAY_USER = "user";
	public static final String C_BILLTO_USERID = "userID";
	public static final String C_BILLTO_PRICE = "price";
	public static final String C_BILLTO_ARRAY_ITEM = "items";
	public static final String C_BILLTO_STATUS  = "status";
	
	public static final String C_ITEMS_NAME = "itemName";
	public static final String C_ITEMS_PRICE = "itemPrice";
	public static final String C_ITEMS_QTY = "itemQty";
	public static final String C_ITEMS_QTY_BILL_ALL = "billAllQty";
	public static final String C_ITEMS_IS_ADDITIONAL = "isAdditional";
	
	public static final String C_ITEMPAY_ITEM_NAME = "itemName";
	public static final String C_ITEMPAY_USERID = "userID";
	public static final String C_ITEMPAY_ITEM_QTY = "itemQty";
	public static final String C_ITEMPAY_ITEM_PRICE = "itemPrice";
	public static final String C_ITEMPAY_TAXPRICE = "taxPrice";
	public static final String C_ITEMPAY_ITEMTYPE = "itemType";
	
	public static final int IMAGE_MAX_SIZE = 400;	

}
