package com.gravitazi.android.caltir.models;

import android.content.Context;
import android.content.Intent;
import com.gravitazi.android.caltir.ASplash;
import com.parse.ParsePushBroadcastReceiver;

public class CPushReceiver extends ParsePushBroadcastReceiver {
	
	@Override
	protected void onPushOpen(Context context, Intent intent) {
//		super.onPushOpen(context, intent);
		Intent i = new Intent(context, ASplash.class);
        i.putExtras(intent.getExtras());
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
	}

}
