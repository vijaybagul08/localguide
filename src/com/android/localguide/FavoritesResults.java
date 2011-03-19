package com.android.localguide;

import java.util.ArrayList;

import com.android.localguide.LocalGuideApplication.favoriteItem;

import android.app.Activity;
import android.os.Bundle;

public class FavoritesResults extends Activity{
	
	private int currentaddress;
	ArrayList<favoriteItem> mFavList;
	LocalGuideApplication app;
	
	public void onCreate(Bundle savedInstance)
	{
		super.onCreate(savedInstance);
		setContentView(R.layout.favoritesresult);
		app = (LocalGuideApplication)this.getApplication();
		mFavList = app.getFavoritesList();
  	
	    Bundle bundle= getIntent().getExtras();
	    currentaddress =  bundle.getInt("position");
	    
	}

}
