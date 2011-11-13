package com.android.localguide;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;

public class MaintabActivity extends TabActivity{
	TabHost tabHost;	
	static Typeface mFont;	

	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.maintab);
	    Resources res = getResources(); // Resource object to get Drawables
	    tabHost = getTabHost();  // The activity TabHost
	    TabHost.TabSpec spec;  // Resusable TabSpec for each tab
	    Intent intent;  // Reusable Intent for each tab

	    // Create an Intent to launch an Activity for the tab (to be reused)
	    intent = new Intent().setClass(this, WelcomeScreen.class);
	    spec = tabHost.newTabSpec(this.getString(R.string.localguide)).setIndicator(this.getString(R.string.localguide),
	                      res.getDrawable(R.drawable.find))
	                  .setContent(intent);
	    tabHost.addTab(spec);
	    
	    intent = new Intent().setClass(this, FavoritesScreen.class);

	    spec = tabHost.newTabSpec(this.getString(R.string.favorite)).setIndicator(this.getString(R.string.favorite),
	                      res.getDrawable(R.drawable.favorite_48))
	                  .setContent(intent);
	    tabHost.addTab(spec);

	    intent = new Intent().setClass(this, Information.class);
	    spec = tabHost.newTabSpec(this.getString(R.string.help)).setIndicator(this.getString(R.string.help),
	                      res.getDrawable(R.drawable.info))
	                  .setContent(intent);
	    
	    tabHost.addTab(spec);

	    tabHost.setCurrentTab(0);
	    
	    for(int i=0;i<tabHost.getTabWidget().getChildCount();i++)
	    	{
	    	       tabHost.getTabWidget().getChildAt(i).setBackgroundDrawable(this.getResources().getDrawable(R.drawable.rowselector));
	    	       TextView tv = (TextView) tabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title); 
	    	       tv.setTypeface(getTypeface(this,"quicksand_bold.ttf"));
	    	}
	}
 
	public static Typeface getTypeface(Context context, String typeface) {
	    if (mFont == null) {
	        mFont = Typeface.createFromAsset(context.getAssets(), typeface);
	    }
	    return mFont;
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
