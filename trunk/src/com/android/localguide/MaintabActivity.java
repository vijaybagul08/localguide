package com.android.localguide;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TabHost;

public class MaintabActivity extends TabActivity{
	TabHost tabHost;
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.maintab);
	    Resources res = getResources(); // Resource object to get Drawables
	    tabHost = getTabHost();  // The activity TabHost
	    TabHost.TabSpec spec;  // Resusable TabSpec for each tab
	    Intent intent;  // Reusable Intent for each tab

	    // Create an Intent to launch an Activity for the tab (to be reused)
	    intent = new Intent().setClass(this, FavoritesScreen.class);

	    // Initialize a TabSpec for each tab and add it to the TabHost
	    spec = tabHost.newTabSpec("Favorites").setIndicator("Favorites",
	                      res.getDrawable(R.drawable.favorite))
	                  .setContent(intent);
	    tabHost.addTab(spec);

	    // Do the same for the other tabs
	    intent = new Intent().setClass(this, WelcomeScreen.class);
	    spec = tabHost.newTabSpec("LocalGuide").setIndicator("LocalGuide",
	                      res.getDrawable(R.drawable.find))
	                  .setContent(intent);
	    tabHost.addTab(spec);

	    intent = new Intent().setClass(this, Information.class);
	    spec = tabHost.newTabSpec("Help").setIndicator("Help",
	                      res.getDrawable(R.drawable.info))
	                  .setContent(intent);
	    tabHost.addTab(spec);

	    tabHost.setCurrentTab(1);
	    for(int i=0;i<tabHost.getTabWidget().getChildCount();i++)
	    	{
	    	       tabHost.getTabWidget().getChildAt(i).setBackgroundDrawable(this.getResources().getDrawable(R.color.list_elements_bkg));
	    	}

	}
	
	public void onTabChanged(String tabId) {
		// TODO Auto-generated method stub
		for(int i=0;i<tabHost.getTabWidget().getChildCount();i++)
		{
		tabHost.getTabWidget().getChildAt(i).setBackgroundColor(Color.parseColor("#7392B5"));
		}

		tabHost.getTabWidget().getChildAt(tabHost.getCurrentTab()).setBackgroundColor(Color.parseColor("#4E4E9C"));
		}
}
