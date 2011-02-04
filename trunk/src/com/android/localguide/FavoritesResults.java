package com.android.localguide;

import android.app.Activity;
import android.os.Bundle;

public class FavoritesResults extends Activity{
	
	private int currentaddress;
	
	public void onCreate(Bundle savedInstance)
	{
		super.onCreate(savedInstance);
		setContentView(R.layout.favoritesresult);
		
	    Bundle bundle= getIntent().getExtras();
	    currentaddress =  bundle.getInt("position");
	    
	}

}
