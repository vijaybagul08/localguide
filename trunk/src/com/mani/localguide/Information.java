package com.mani.localguide;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mani.localguide.R;
import com.mani.localguide.FaceBookClient.FaceBookAuthenticationCallBack;

public class Information extends Activity implements FaceBookAuthenticationCallBack{

	TextView information;
	Button twitterButton;
	Button facebookButton;
	Button rateButton;
	Handler mHandler = new H();
	final String FONT_TTF = "quicksand_book.ttf";

	private int TWITTER = 1;
	private int FACEBOOK = 2;
	private int FACEBOOK_SUCCESSFUL =3;
	private int TWITTER_AUTHENTICATE = 3;
	LocalGuideApplication app;
	Context mContext;
	TextView twitterText;
	TextView facebookText;
	TextView description;
	TextView description1;
	TextView description2;
	TextView authentication;
	TextView authenticationtext;
	TextView idnetify_location;
	TextView idnetify_locationtext;

	TextView call;
	TextView message;
	TextView phonebook;
	TextView direction;
	TextView twitter_options;
	TextView facebook_options;
	TextView favorites;

	static Typeface mFont;	
	static Typeface mFont1;
	
	public void onCreate(Bundle savedState)
	{
		super.onCreate(savedState);
		setContentView(R.layout.information);
		mContext = this;
		app = (LocalGuideApplication)this.getApplication();
		twitterText = (TextView)findViewById(R.id.twittertext);
		facebookText = (TextView)findViewById(R.id.facebooktext);
		twitterButton = (Button )findViewById(R.id.twitter);
		rateButton = (Button )findViewById(R.id.rate);
		description = (TextView) findViewById(R.id.description);
		description1 = (TextView) findViewById(R.id.description1);
		description2 = (TextView) findViewById(R.id.description2);
		authentication = (TextView) findViewById(R.id.authentication);
		authenticationtext = (TextView) findViewById(R.id.authenticationtext);
		idnetify_location = (TextView) findViewById(R.id.identify_location);
		idnetify_locationtext = (TextView) findViewById(R.id.location_detail);
		
		call = (TextView) findViewById(R.id.call);
		message = (TextView) findViewById(R.id.message);
		phonebook = (TextView) findViewById(R.id.phonebook);
		twitter_options = (TextView) findViewById(R.id.twitter_options);
		facebook_options = (TextView) findViewById(R.id.facebook_options);
		direction = (TextView) findViewById(R.id.direction);
		favorites = (TextView) findViewById(R.id.favorites);
		
		twitterText.setTypeface(getTypeface1(this,"quicksand_bold.ttf"));
		facebookText.setTypeface(getTypeface1(this,"quicksand_bold.ttf"));
		twitterButton.setTypeface(getTypeface(this,FONT_TTF));
		rateButton.setTypeface(getTypeface1(this,"quicksand_bold.ttf"));
		description.setTypeface(getTypeface(this,FONT_TTF));
		description1.setTypeface(getTypeface(this,FONT_TTF));
		description2.setTypeface(getTypeface(this,FONT_TTF));
		authentication.setTypeface(getTypeface1(this,"quicksand_bold.ttf"));
		authenticationtext.setTypeface(getTypeface(this,FONT_TTF));
		idnetify_location.setTypeface(getTypeface1(this,"quicksand_bold.ttf"));
		idnetify_locationtext.setTypeface(getTypeface(this,FONT_TTF));
		
		call.setTypeface(getTypeface1(this,"quicksand_bold.ttf"));
		message.setTypeface(getTypeface1(this,"quicksand_bold.ttf"));
		phonebook.setTypeface(getTypeface1(this,"quicksand_bold.ttf"));
		twitter_options.setTypeface(getTypeface1(this,"quicksand_bold.ttf"));
		facebook_options.setTypeface(getTypeface1(this,"quicksand_bold.ttf"));
		direction.setTypeface(getTypeface1(this,"quicksand_bold.ttf"));
		favorites.setTypeface(getTypeface1(this,"quicksand_bold.ttf"));
		
		String tmp = getString(R.string.description);
		String tmp1 ="     "+tmp;

		if(app.isTwitterAutheticated())
			twitterButton.setText(this.getString(R.string.different_user));
		else
			twitterButton.setText(this.getString(R.string.sign_in));
			
		twitterButton.setTypeface(getTypeface1(this,"quicksand_bold.ttf"));

		twitterButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				mHandler.sendEmptyMessage(TWITTER);
			}
			
		});
		facebookButton = (Button )findViewById(R.id.facebook);
		facebookButton.setTypeface(getTypeface1(this,"quicksand_bold.ttf"));

		if(app.isFacebookAuthenticated())
			facebookButton.setText(this.getString(R.string.different_user));			
		else
			facebookButton.setText(this.getString(R.string.sign_in));


		facebookButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				mHandler.sendEmptyMessage(FACEBOOK);
			}
			
		});

		rateButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse("market://details?id=com.mani.localguide"));
				startActivity(intent);
			}
			
		});

	}

	public void onResume() {
		super.onResume();
		if (app.isTwitterAutheticated() == true) {
			twitterButton.setText(this.getString(R.string.different_user));
		}
	}

	public static Typeface getTypeface(Context context, String typeface) {
	    if (mFont == null) {
	        mFont = Typeface.createFromAsset(context.getAssets(), typeface);
	    }
	    return mFont;
	}
	
	public static Typeface getTypeface1(Context context, String typeface) {
	    if (mFont1 == null) {
	        mFont1 = Typeface.createFromAsset(context.getAssets(), typeface);
	    }
	    return mFont1;
	}
	
	private boolean checkInternetConnection() {

		ConnectivityManager conMgr = (ConnectivityManager) mContext.getSystemService (mContext.CONNECTIVITY_SERVICE);

		// ARE WE CONNECTED TO THE NET

		if (conMgr.getActiveNetworkInfo() != null
		&& conMgr.getActiveNetworkInfo().isAvailable()
		&& conMgr.getActiveNetworkInfo().isConnected()) {
			return true;
		} else {
			return false;
		}

	} 

	
	class H extends Handler
	{
		public void handleMessage(Message m)
		{
			if(m.what == TWITTER) 
			{
				if(checkInternetConnection() == true) {
            	if (app.isTwitterAutheticated() == false) {
	
					Intent i = new Intent(getApplicationContext(), PrepareRequestTokenActivity.class);
					i.putExtra("tweet_msg","hello");
					startActivityForResult(i,TWITTER_AUTHENTICATE);
            	}
				}else {
					new ErrorDialog(mContext,mContext.getString(R.string.no_internet),mContext.getString(R.string.enable_internet),false).show();					
				}
			}
			else if(m.what == FACEBOOK)
			{
				if(checkInternetConnection() == true) {
				FaceBookClient client = new FaceBookClient(Information.this,Information.this);
				client.initialize();
				} else {
					new ErrorDialog(mContext,mContext.getString(R.string.no_internet),mContext.getString(R.string.enable_internet),false).show();
				}
			}else if(m.what == FACEBOOK_SUCCESSFUL){
				facebookButton.setText(Information.this.getString(R.string.different_user));
			}
		}
	}
	
	 protected void onActivityResult(int requestCode, int resultCode,
             Intent data) {
		 if (requestCode == TWITTER_AUTHENTICATE) {
             if (resultCode == RESULT_OK) {
            	 twitterButton.setText(this.getString(R.string.different_user));
             }
		 }
	 }
	public void onFaceBookAuthenticateCompleted(int response,String token,String username)
	{
		switch(response)
		{
		case FaceBookAuthenticationCallBack.AUTHENTICAION_SUCCESSFULL:
			String text="";
			text="Facebook \n";
			app.updateFacebookToken(token);
			text+=username;
			mHandler.sendEmptyMessage(FACEBOOK_SUCCESSFUL);
			break;
		case FaceBookAuthenticationCallBack.AUTHENTICAION_FAILURE:
			break;
			
		}
	}

	public void onBackPressed ()
	{
		 app.saveToDataBase();
		 this.finish();
	}
}
