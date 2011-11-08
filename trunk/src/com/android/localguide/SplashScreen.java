package com.android.localguide;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class SplashScreen extends Activity{

	protected boolean _active = true;
	protected int _splashTime = 2000; 
	public void onCreate(Bundle savedInstanceState)
	{
		  super.onCreate(savedInstanceState);
		    setContentView(R.layout.splash);
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
}