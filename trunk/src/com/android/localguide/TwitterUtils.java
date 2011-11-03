package com.android.localguide;

import oauth.signpost.OAuth;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.http.AccessToken;
import android.content.SharedPreferences;

public class TwitterUtils {

	public static boolean isAuthenticated(LocalGuideApplication app ) {

		//String token = prefs.getString(OAuth.OAUTH_TOKEN, "");
		//String secret = prefs.getString(OAuth.OAUTH_TOKEN_SECRET, "");
		String token = app.getTwitterAccessKey();
		String secret = app.getTwitterAccessSecret();
		if( token.length() == 0 || secret.length() == 0) 
			return false;
		
		AccessToken a = new AccessToken(token,secret);
		Twitter twitter = new TwitterFactory().getInstance();
		twitter.setOAuthConsumer(Constants.CONSUMER_KEY, Constants.CONSUMER_SECRET);
		twitter.setOAuthAccessToken(a);
		
		try {
			twitter.getAccountSettings();
			return true;
		} catch (TwitterException e) {
			return false;
		}
	}
	
	public static void sendTweet(LocalGuideApplication app,String msg) throws Exception {
//		String token = prefs.getString(OAuth.OAUTH_TOKEN, "");
//		String secret = prefs.getString(OAuth.OAUTH_TOKEN_SECRET, "");
		String token = app.getTwitterAccessKey();
		String secret = app.getTwitterAccessSecret();
		
		AccessToken a = new AccessToken(token,secret);
		Twitter twitter = new TwitterFactory().getInstance();
		twitter.setOAuthConsumer(Constants.CONSUMER_KEY, Constants.CONSUMER_SECRET);
		twitter.setOAuthAccessToken(a);
        twitter.updateStatus(msg);
	}	
}
