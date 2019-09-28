package com.gravitazi.android.caltir;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.gravitazi.android.caltir.main.ALogin;
import com.gravitazi.android.caltir.main.AMain;
import com.gravitazi.android.caltir.models.CDataProvider;
import com.parse.ParseAnalytics;
import com.parse.ParseUser;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
//import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

public class ASplash extends Activity {
	
	private final String PAGE_MAIN = "PAGE_MAIN";
	private final String PAGE_LOGIN = "PAGE_LOGIN";
	private final String STATE_ERROR = "ERROR";
	private ProgressBar mPb;
	private Handler mHandler;
	private Runnable mRunScheduler;
	private ScheduledThreadPoolExecutor mExecutor;
	private ScheduledFuture<?> mSched;
	private int _corePool = 7;
	private static final int FROM_SCHEDULED = 1;
	private CAsyncTask mTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_splash);
		mPb = (ProgressBar) findViewById(R.id.pbSplash);
		// Track app opens.
//		ParseAnalytics.trackAppOpened(getIntent());
//		ParseAnalytics.trackAppOpenedInBackground(getIntent());
		mTask = new CAsyncTask();
		mTask.execute();
		runCounter();
	}
	
	private void runCounter() {
		
		mExecutor = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(_corePool);		
		mHandler = new Handler(Looper.getMainLooper()) {
			
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case FROM_SCHEDULED:	
					int iteration = msg.getData().getInt("iteration");
					if(iteration <=  2) {
						Toast.makeText(getApplicationContext(), "Oops, internet connection is slow, still need more time...", Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(getApplicationContext(), "The internet connection is bad, please try again later.", Toast.LENGTH_SHORT).show();
						mTask.cancel(true);
						mSched.cancel(true);
						finishActivity();
					}
					break;
				default:
					super.handleMessage(msg);
					break;
				}
			}
		};
		
		mRunScheduler = new Runnable() {
			
			private int iteration = 0;
			
			@Override
			public void run() {
				Message msg = mHandler.obtainMessage();
				msg.what = FROM_SCHEDULED;
				Bundle b = new Bundle();
				b.putInt("iteration", iteration++);
                msg.setData(b);
                mHandler.sendMessage(msg);
			}
			
		};
		
		mSched = mExecutor.scheduleAtFixedRate(mRunScheduler, 5, 5 , TimeUnit.SECONDS);
		
	}

	protected void finishActivity() {
		finish();
	}

	private class CAsyncTask extends AsyncTask<Void, Void, Void> {
		
		private String next_page;

		@Override
		protected Void doInBackground(Void... params) {
			getStuffs();
			return null;
		}
		
		private void getStuffs() {
			ParseUser _user = ParseUser.getCurrentUser();
			
			if(_user != null && _user.getObjectId() != null) {
				int usr = CDataProvider.getInstance().parseCheckUserExist(_user);
				if(usr == CApp.USER_EXIST) {
					next_page = PAGE_MAIN;
				} else if(usr == CApp.NO_USER_EXIST) {
					ParseUser.logOut();
					next_page = PAGE_LOGIN;
				} else if(usr == CApp.NO_INET_CONNECTION) {
					next_page = STATE_ERROR;
				}
			} else {
				next_page = PAGE_LOGIN;
			}
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			mSched.cancel(true);
			if(next_page.equals(PAGE_MAIN)) {
				callMainPage();
			} else if(next_page.equals(PAGE_LOGIN)) {
				callLoginPage();
			} else {
				
			}
		}
	}

	public void callMainPage() {
		mPb.setVisibility(View.INVISIBLE);
		Intent i = new Intent(ASplash.this, AMain.class);
		startActivity(i);
		finish();
	}

	public void callLoginPage() {
		mPb.setVisibility(View.INVISIBLE);
		Intent i = new Intent(ASplash.this, ALogin.class);
		startActivity(i);
		finish();
	}

}
