package com.android.localguide;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.localguide.FaceBookClient.FaceBookAuthenticationCallBack;

public class Information extends Activity implements FaceBookAuthenticationCallBack{

	TextView information;
	Button twitterButton;
	Button facebookButton;
	Handler mHandler = new H();

	private int TWITTER = 1;
	private int FACEBOOK = 2;
	private int FACEBOOK_SUCCESSFUL =3;
	private int TWITTER_AUTHENTICATE = 3;
	LocalGuideApplication app;
	TextView twitterText;
	TextView facebookText;
	TextView description;
	
	public void onCreate(Bundle savedState)
	{
		super.onCreate(savedState);
		setContentView(R.layout.information);
		app = (LocalGuideApplication)this.getApplication();
		twitterText = (TextView)findViewById(R.id.twittertext);
		facebookText = (TextView)findViewById(R.id.facebooktext);
		twitterButton = (Button )findViewById(R.id.twitter);
		description = (TextView) findViewById(R.id.description);
		String tmp = getString(R.string.description);
		String tmp1 ="     "+tmp;
		//description.setText(tmp1);
		if(app.isTwitterAutheticated())
			twitterButton.setText("Try Different User");
		else
			twitterButton.setText("Sign In");
			
		
		twitterButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				System.out.println("Twitter button click ************" );
				mHandler.sendEmptyMessage(TWITTER);
			}
			
		});
		facebookButton = (Button )findViewById(R.id.facebook);
		
		if(app.isFacebookAuthenticated())
			facebookButton.setText("Try Different User");			
		else
			facebookButton.setText("Sign In");


		facebookButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				mHandler.sendEmptyMessage(FACEBOOK);
			}
			
		});
//		TwitterClient client = new TwitterClient(Information.this,Information.this);
//		client.initialize();
//		client.authenticate();	
	}

	public void onResume() {
		super.onResume();
		System.out.println("On Resume information ********* ");
		if (app.isTwitterAutheticated() == true) {
			twitterButton.setText("Try different user");
		}
	}
	class H extends Handler
	{
		public void handleMessage(Message m)
		{
			if(m.what == TWITTER) 
			{
				System.out.println("Handle message is TWITTER**************** ");
			
//				Intent twitterIntent= new Intent();
//				twitterIntent.setClass(Information.this, TwitterActivity.class);
//				startActivityForResult(twitterIntent,TWITTER_AUTHENTICATE);
            	if (app.isTwitterAutheticated() == false) {
	
					Intent i = new Intent(getApplicationContext(), PrepareRequestTokenActivity.class);
					i.putExtra("tweet_msg","hello");
					//startActivity(i);
					startActivityForResult(i,TWITTER_AUTHENTICATE);
            	}
		        
			}
			else if(m.what == FACEBOOK)
			{
				System.out.println("Handle message is facebook**************** ");
				FaceBookClient client = new FaceBookClient(Information.this,Information.this);
				client.initialize();
			}else if(m.what == FACEBOOK_SUCCESSFUL){
				facebookButton.setText("Try Different User");
			}
		}
	}
	
	 protected void onActivityResult(int requestCode, int resultCode,
             Intent data) {
		 if (requestCode == TWITTER_AUTHENTICATE) {
             if (resultCode == RESULT_OK) {
            	 System.out.println("Information ************* on activity result ******* ");
//            	 Bundle bundle= data.getExtras();
//            	 String key = bundle.getString("AccessKey");
//            	 String secret = bundle.getString("AccessSecret");
//            	 String username = bundle.getString("UserName");
//            	 System.out.println("Key and secret is "+key +":::"+secret);
//            	 app.updateTwitterToken(key, secret);
            	 twitterButton.setText("Try different user");
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
			System.out.println("on facebook authentication complete set token is **** "+app.getFacebookToken());
		//	facebookText.setText(text);
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
