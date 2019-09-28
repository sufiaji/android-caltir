package com.gravitazi.android.caltir.main;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.gravitazi.android.caltir.R;
import com.gravitazi.android.caltir.models.CConstants;
import com.gravitazi.android.caltir.models.CDataProvider;
import com.parse.ParseFile;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class ANewAttach extends Activity {
	
	private static final int REQUEST_IMAGE_CAPTURE = 1;
	private static final int REQUEST_LOAD_IMAGE = 2;
	private String mCurrentPhotoPath;
	private Button btnCam;
	private Button btnDel;
	private ImageView mImageView;
	private Bitmap mBitmapImage;
	private ParseFile mPic;
	private static ANewAttach mNewAttach;
	
	public static ANewAttach getInstance() {
		return mNewAttach;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_attach);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		btnCam = (Button) findViewById(R.id.idCam);
		btnDel = (Button) findViewById(R.id.idDel);
		if(mBitmapImage == null) {
			btnDel.setEnabled(false);
		}
		mImageView = (ImageView) findViewById(R.id.idImgPic);
		if(!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {			
			btnCam.setEnabled(false);
		}
		mNewAttach = this;
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
		ANewShared.getInstance().finish();
		ANewAdditional.getInstance().finish();
		ANewTax.getInstance().finish();
		ANewItem.getInstance().finish();
		ANewSession.getInstance().finish();
		finish();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == RESULT_OK && requestCode == REQUEST_IMAGE_CAPTURE) {
			setPic();
		} else if(resultCode == RESULT_OK && requestCode == REQUEST_LOAD_IMAGE) {
			setImg(data);
		}
	}
	
	private void setImg(Intent data) {
		Bitmap bitmap = CDataProvider.decodeFileBitmapResize(this, data.getData(), CConstants.IMAGE_MAX_SIZE, CConstants.IMAGE_MAX_SIZE);
		mImageView.setImageBitmap(bitmap);
		mBitmapImage = bitmap;
		btnDel.setEnabled(true);
	}

	private void setPic() {
	    // Get the dimensions of the bitmap
	    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
	    bmOptions.inJustDecodeBounds = true;
	    BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);	    
	    
	    int photoW = bmOptions.outWidth;
	    int photoH = bmOptions.outHeight;
	    
	    // we want to make the max. size of pic to be uploaded is around 400pix	    
	    int sacle = 0;
	    if(photoW > photoH) {
	    	sacle = (int) Math.ceil((double) photoW / (double) CConstants.IMAGE_MAX_SIZE);
	    } else {
	    	sacle = (int) Math.ceil((double) photoH / (double) CConstants.IMAGE_MAX_SIZE);
	    }
	    
	    // Decode the image file into a Bitmap sized to fill the View
	    bmOptions.inJustDecodeBounds = false;
	    bmOptions.inSampleSize = sacle; 
	    bmOptions.inPurgeable = true;

	    Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
	    mImageView.setImageBitmap(bitmap);		
	    mBitmapImage = bitmap;
	    btnDel.setEnabled(true);
	}

	public void onNext(View v) {
		if(mBitmapImage != null) {
			ByteArrayOutputStream streamImg = new ByteArrayOutputStream();
			mBitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, streamImg);
		    byte[] dataImage = streamImg.toByteArray();
		    mPic = new ParseFile("bill.jpg", dataImage);
		    CDataProvider.getInstance().setAttachment(mPic);
		}
		startActivity(new Intent(ANewAttach.this, ANewBillTo.class));
	}
	
	public void onPic(View v) {
		// get picture from gallery
		Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) || Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
			i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		} else {
			i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
		}
        startActivityForResult(i, REQUEST_LOAD_IMAGE);
	}
	
	public void onCam(View v) {
		// get picture from Camera
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	    // Ensure that there's a camera activity to handle the intent
	    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
	        // Create the File where the photo should go
	        File photoFile = null;
	        try {
	            photoFile = createImageFile();
	        } catch (IOException ex) {
	            // Error occurred while creating the File
	           Toast.makeText(getApplicationContext(), "Error occurred while creating the File", Toast.LENGTH_SHORT).show();
	        }
	        // Continue only if the File was successfully created
	        if (photoFile != null) {
	            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
	            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
	        }
	    }

	}
	
	public void onDel(View v) {
		mImageView.setImageDrawable(getResources().getDrawable(R.drawable.no_image));
		mBitmapImage = null;
		btnDel.setEnabled(false);
	}
	
	@SuppressLint("SimpleDateFormat")
	private File createImageFile() throws IOException {
	    // Create an image file name
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	    String imageFileName = "BILL_" + timeStamp + "_";
	    File storageDir1 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
	    File image1 = File.createTempFile(
		        imageFileName,  /* prefix */
		        ".jpg",         /* suffix */
		        storageDir1      /* directory */
		    );
//		File storageDir2 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
//	    File image2 = File.createTempFile(
//	        imageFileName,  /* prefix */
//	        ".jpg",         /* suffix */
//	        storageDir2      /* directory */
//	    );

	    // Save a file: path for use with ACTION_VIEW intents
	    mCurrentPhotoPath = image1.getAbsolutePath();
	    return image1;
	}

}
