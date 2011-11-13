package com.android.localguide;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;

public class SplashScreen extends Activity{

	protected boolean _active = true;
	protected int _splashTime = 2000; 
	TextView mTitle;
	TextView mAuthor;
	static Typeface mFont;	
	public void onCreate(Bundle savedInstanceState)
	{
		  super.onCreate(savedInstanceState);
		    setContentView(R.layout.splash);
		    mTitle = (TextView) findViewById(R.id.title);
		    mAuthor = (TextView) findViewById(R.id.author);
		    mTitle.setTypeface(getTypeface(this,"val.ttf"));
		    mAuthor.setTypeface(getTypeface(this,"val.ttf"));
		    Thread splashTread = new Thread() {
		        @Override
		        public void run() {
		            try {
		                int waited = 0;
		                while(_active && (waited < _splashTime)) {
		                    sleep(100);
		                    if(_active) {
		                        waited += 100;
		                    }
		                }
		            } catch(InterruptedException e) {
		                // do nothing
		            } finally {
		                finish();
		                startActivity(new Intent("com.android.localguideTabScreen"));
		            }
		        }
		    };
		    splashTread.start();
	}
	public static Typeface getTypeface(Context context, String typeface) {
	    if (mFont == null) {
	        mFont = Typeface.createFromAsset(context.getAssets(), typeface);
	    }
	    return mFont;
	}
}
