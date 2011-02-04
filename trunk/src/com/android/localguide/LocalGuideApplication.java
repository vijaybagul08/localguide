package com.android.localguide;

import java.util.ArrayList;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class LocalGuideApplication extends Application {

	String mTwitterUserName;
	String mFacebookUserName;
	ArrayList<favoriteItem> favoritesList;
	
	class favoriteItem
	{
       	public String title;
    	public String streetAddress;
    	public String phoneNumber;
    	public String latitude;
    	public String longitude;
    	favoriteItem(String atitle, String address, String number,String lat,String along)
    	{
    		title = atitle;
    		streetAddress = address;
    		phoneNumber = number;
    		latitude = lat;
    		longitude = along;
    	}		
	}
	
	public void onCreate()
	{
        super.onCreate();
        favoritesList = new ArrayList<favoriteItem>();
	    loadFromDataBase();
	}
	
	public void updateTwitterUserName(String username)
	{
		mTwitterUserName = username;
	}

	public String getTwitterUserName()
	{
		return mTwitterUserName;
	}

	
	public void updateFacebookUserName(String username)
	{
		mFacebookUserName = username;
	}

	public String getFacebookUserName()
	{
		return mFacebookUserName;
	}

	public boolean addToFavorites(String title,String address,String phoneNumber,String lat,String along)
	{
		/*Check if the user has already added the item to favorites list*/
		
		for(int i=0;i<favoritesList.size();i++)
		{
			if(favoritesList.get(i).title.contains(title) == true)
			{
				/* Already added */
				return false;
			}
		}
		/* Not found in the list so new element is added */
		
    	favoriteItem item = new favoriteItem(title,address,phoneNumber,lat,along);
    	favoritesList.add(item);
    	return true;
	}
	
	public void deleteFavorites(String title)
	{
		int position=0;
		for(int i=0;i<favoritesList.size();i++)
		{
			if(favoritesList.get(i).title.contains(title) == true)
			{
				/* Element found */
				position = i;
				break;
			}
		}
		favoritesList.remove(position);
	}
	
	public ArrayList<favoriteItem>	 getFavoritesList()
	{
		return favoritesList;
	}
	
	public void loadFromDataBase()
	{
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Editor editor = null;
        mTwitterUserName = prefs.getString("twitterusername", "");
        mFacebookUserName = prefs.getString("facebookusername", "");
        
        int count = prefs.getInt("FavoritesCount", 0);

        
        for (int i = 0; i < count; i++) {
        	String title = prefs.getString("title" + i, "");
        	String address = prefs.getString("streetaddress" + i, "");
        	String phonenumber = prefs.getString("phonenumber" + i, "");
        	String lat= prefs.getString("latitude" + i, "");
        	String along= prefs.getString("longitude" + i, "");
        	favoriteItem item = new favoriteItem(title,address,phonenumber,lat,along);
        	favoritesList.add(item);
        }
			
	}
	
	public void saveToDataBase()
	{
	 	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Editor editor = null;;
        editor = prefs.edit();
        editor.putString("twitterusername", mTwitterUserName);
        editor.putString("facebookusername", mFacebookUserName);
        
        int count = favoritesList.size();
        editor.putInt("FavoritesCount", count);
       
       for (int i = 0; i < count; i++) {
        	favoriteItem data = favoritesList.get(i);
        	
        	if(count != 0) {
        		// TODO:
                editor.putString("title" + i, data.title);
                editor.putString("streetaddress" + i, data.streetAddress);
                editor.putString("phonenumber" + i, data.phoneNumber);
                editor.putString("latitude" + i, data.latitude);
                editor.putString("longitude" + i, data.longitude);
        	}
        } 
        
        editor.commit();
        favoritesList.clear();		
	}
}
