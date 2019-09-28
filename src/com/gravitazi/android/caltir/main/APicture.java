package com.gravitazi.android.caltir.main;

import com.gravitazi.android.caltir.R;
import com.gravitazi.android.caltir.models.CConstants;
import com.gravitazi.android.caltir.models.CDataProvider;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

public class APicture extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_picture);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		ImageView imgv = (ImageView) findViewById(R.id.idImgPic);
		int index = CDataProvider.getInstance().getSelectedSessionPosition();
		ParseObject session = CDataProvider.getInstance().getHistory().get(index);
		ParseFile f = session.getParseFile(CConstants.C_SESSION_IMAGE);
		if(f != null) {
			try {
				byte[] data;
				data = f.getData();
				Bitmap b = BitmapFactory.decodeByteArray(data , 0, data.length);
				imgv.setImageBitmap(b);
			} catch (ParseException e) {
				e.printStackTrace();
				Toast.makeText(getApplicationContext(), "Opps...something went wrong...", Toast.LENGTH_SHORT).show();
				finish();
			}
		} else {
			imgv.setImageDrawable(getResources().getDrawable(R.drawable.no_image));
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {			
			case android.R.id.home:
				finish();
		        return true;		    				
		}
		return super.onOptionsItemSelected(item);
	}

}
