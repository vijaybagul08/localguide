package com.mani.localguide;

import com.mani.localguide.R;

import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Prepares a OAuthConsumer and OAuthProvider 
 * 
 * OAuthConsumer is configured with the consumer key & consumer secret.
 * OAuthProvider is configured with the 3 OAuth endpoints.
 * 
 * Execute the OAuthRequestTokenTask to retrieve the request, and authorize the request.
 * 
 * After the request is authorized, a callback is made here.
 * 
 */
public class PrepareRequestTokenActivity extends Activity {

	final String TAG = getClass().getName();
	
    private OAuthConsumer consumer; 
    private OAuthProvider provider;
    LocalGuideApplication app;
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
    	try {
    		this.consumer = new CommonsHttpOAuthConsumer(Constants.CONSUMER_KEY, Constants.CONSUMER_SECRET);
    	    this.provider = new CommonsHttpOAuthProvider(Constants.REQUEST_URL,Constants.ACCESS_URL,Constants.AUTHORIZE_URL);
    	    this.provider.setOAuth10a(true);
    	} catch (Exception e) {
    		Log.e(TAG, "Error creating consumer / provider",e);
		}
    	app = (LocalGuideApplication)this.getApplication();
        Log.i(TAG, "Starting task to retrieve request token.");
		new OAuthRequestTokenTask(this,consumer,provider).execute();
	}

	public void error() {
		   LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
		   View layout;
	   	   layout = inflater.inflate(R.layout.custom_toast, null);
           TextView message = (TextView)layout.findViewById(R.id.message);
           message.setText("Error while accesing access token for twitter.");
           Toast toastView = new Toast(this);
           toastView.setView(layout);
           toastView.setDuration(Toast.LENGTH_LONG);
           toastView.setGravity(Gravity.CENTER, 0,0);
           toastView.show();
           finish();

	}
	/**
	 * Called when the OAuthRequestTokenTask finishes (user has authorized the request token).
	 * The callback URL will be intercepted here.
	 */
	@Override
	public void onNewIntent(Intent intent) {
		super.onNewIntent(intent); 
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		final Uri uri = intent.getData();
		if (uri != null && uri.getScheme().equals(Constants.OAUTH_CALLBACK_SCHEME)) {
			Log.i(TAG, "Callback received : " + uri);
			new RetrieveAccessTokenTask(this,consumer,provider,prefs).execute(uri);
			//finish();	
		}
	}
	
	public class RetrieveAccessTokenTask extends AsyncTask<Uri, Void, Void> {

		private Context	context;
		private OAuthProvider provider;
		private OAuthConsumer consumer;
		private SharedPreferences prefs;
		
		public RetrieveAccessTokenTask(Context context, OAuthConsumer consumer,OAuthProvider provider, SharedPreferences prefs) {
			this.context = context;
			this.consumer = consumer;
			this.provider = provider;
			this.prefs=prefs;
		}


		/**
		 * Retrieve the oauth_verifier, and store the oauth and oauth_token_secret 
		 * for future API calls.
		 */
		@Override
		protected Void doInBackground(Uri...params) {
			final Uri uri = params[0];
			final String oauth_verifier = uri.getQueryParameter(OAuth.OAUTH_VERIFIER);

			try {
				provider.retrieveAccessToken(consumer, oauth_verifier);

				app.updateTwitterToken(consumer.getToken(), consumer.getTokenSecret());
				String token = app.getTwitterAccessKey();
				String secret = app.getTwitterAccessSecret();
				
				consumer.setTokenWithSecret(token, secret);
				finish();
				
			} catch (Exception e) {
				//Log.e(TAG, "OAuth - Access Token Retrieval Error", e);
				error();
			}

			return null;
		}

	}	
	
}
