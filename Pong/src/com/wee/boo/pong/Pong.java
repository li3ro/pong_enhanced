package com.wee.boo.pong;

import com.wee.boo.pong.R;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

public class Pong extends Activity {
	private static final String TAG = "Pong";
	private Context context;
	private SplashView sv;
	private PongView pv;
	private Game g;
	private GamePlay gp;
	private TouchScreen ts;
	private Freq audio_freq;
	private GameState gs;
	private Splash s;
	private Handler h;
	private InterstitialAd mInterstitialView;
	private long lastBackPressedTime=0;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		android.util.Log.d(TAG, "onCreate() savedInstanceState == null: " + (savedInstanceState == null));
		if (savedInstanceState != null) {
			android.util.Log.d(TAG, "savedInstance not null, splash saved = " + savedInstanceState.getBoolean("splash"));
		}
		
		mInterstitialView = new InterstitialAd(this);
        mInterstitialView.setAdUnitId( getString(R.string.interstitial_ad_unit_id) );
        AdRequest adInterstitialRequest = new AdRequest.Builder().build();
        mInterstitialView.loadAd(adInterstitialRequest);
        
		pv = new PongView(this);
		audio_freq = new Freq();
		audio_freq.freq = 440;
		g = new Game(100, 100, 40, audio_freq, this);
		ts = new TouchScreen(g);
		pv.setOnTouchListener(ts);
		pv.set_game(g);
		gs = new GameState();
		if (savedInstanceState != null)
			gs.in_splash = savedInstanceState.getBoolean("splash");
		h = new Handler() {
			public void handleMessage(Message m) {
				if (m.what == 0)
					setContentView(sv);
				else if (m.what == 1)
					setContentView(pv);
				else if (m.what == 99) {
					if( mInterstitialView.isLoaded()) {
						mInterstitialView.show();
					} else {
						AdRequest adInterstitialRequest = new AdRequest.Builder().build();
				        mInterstitialView.loadAd(adInterstitialRequest);
					}
				}
			}
		};
		gs.set_handler(h);
		gs.set_game(g);
		sv = new SplashView(this, gs);
		gs.set_splash_view(sv);
		gs.set_pong_view(pv);
		gs.set_activity(this);
		gs.set_freq(audio_freq);
		setContentView(sv);

		android.util.DisplayMetrics metrics = new android.util.DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		android.util.Log.d(TAG, "xdpi " + metrics.xdpi + " ydpi "
				+ metrics.ydpi + " h " + metrics.heightPixels + " w "
				+ metrics.widthPixels + " density " + metrics.density
				+ " densityDpi " + metrics.densityDpi + " scaledDensity "
				+ metrics.scaledDensity);
	}

	public void onResume() {
		super.onResume();
		android.util.Log.d(TAG, "onResume()");
		gs.start();
	}

	public void onPause() {
		super.onPause();
		android.util.Log.d(TAG, "onPause()");
		gs.stop();
	}

	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		android.util.Log
				.d(TAG, "onSaveInstanceState called, setting splash to "
						+ gs.in_splash);
		outState.putBoolean("splash", gs.in_splash);
	}
	
	@Override
   	public boolean onKeyDown(int keyCode, KeyEvent event) {
   	    if ((keyCode == KeyEvent.KEYCODE_BACK))  {
   	    	Log.v(TAG, "back key pressed.");
   	    	if(lastBackPressedTime==0 || System.currentTimeMillis()-lastBackPressedTime > 2000) {
   	    		lastBackPressedTime = System.currentTimeMillis();
				Toast.makeText(context, "Back again to exit", Toast.LENGTH_SHORT).show();
				return false;
   	    	} else {
   	    		return super.onKeyDown(keyCode, event);
   	    	}
   	    }
   	    return super.onKeyDown(keyCode, event);
    }
}
