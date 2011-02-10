package com.android.localguide;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.localguide.FaceBookClient.FaceBookAuthenticationCallBack;
import com.android.localguide.TwitterClient.TwitterAuthenticationCallBack;

public class Information extends Activity implements FaceBookAuthenticationCallBack,TwitterAuthenticationCallBack{

	TextView information;
	Button twitterButton;
	Button facebookButton;
	Handler mHandler = new H();

	private int TWITTER = 1;
	private int FACEBOOK = 2;
	LocalGuideApplication app;
	TextView twitterText;
	TextView facebookText;
	
	public void onCreate(Bundle savedState)
	{
		super.onCreate(savedState);
		setContentView(R.layout.information);
		app = (LocalGuideApplication)this.getApplication();
		twitterText = (TextView)findViewById(R.id.twittertext);
		facebookText = (TextView)findViewById(R.id.facebooktext);
		twitterButton = (Button )findViewById(R.id.twitter);
		
		if(app.isTwitterAutheticated())
			twitterButton.setText("Sign In");
		else
			twitterButton.setText("Try Different User");
		
		twitterButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				System.out.println("Twitter button click ************" );
				mHandler.sendEmptyMessage(TWITTER);
			}
			
		});
		facebookButton = (Button )findViewById(R.id.facebook);
		
		if(app.isFacebookAuthenticated())
			facebookButton.setText("Sign In");
		else
			facebookButton.setText("Try Different User");

		facebookButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				mHandler.sendEmptyMessage(FACEBOOK);
			}
			
		});
		TwitterClient client = new TwitterClient(Information.this,Information.this);
		client.initialize();
		client.authenticate();	
	}

	class H extends Handler
	{
		public void handleMessage(Message m)
		{
			if(m.what == TWITTER) 
			{
				System.out.println("Handle message is TWITTER**************** ");
				TwitterClient client = new TwitterClient(Information.this,Information.this);
				client.initialize();
				client.authenticate();				
			}
			else if(m.what == FACEBOOK)
			{
				System.out.println("Handle message is facebook**************** ");
				FaceBookClient client = new FaceBookClient(Information.this,Information.this);
				client.initialize();
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
			app.SetFacebookAuthenticated(true);
			text+=username;
			System.out.println("on facebook authentication complete set token is **** "+app.getFacebookToken());
		//	facebookText.setText(text);
		//	facebookButton.setText("Try Different User");
			break;
		case FaceBookAuthenticationCallBack.AUTHENTICAION_FAILURE:
			break;
			
		}
	}
	public void onTwitterAuthenticateCompleted(int response,String key,String secret,String username)
	{	
		switch(response)
		{
		case TwitterAuthenticationCallBack.AUTHENTICAION_SUCCESSFULL:
			app.updateTwitterToken(key, secret);
			app.SetTwitterAuthenticated(true);
			
			break;
		case TwitterAuthenticationCallBack.AUTHENTICAION_FAILURE:
			break;
		}
		
	}
	public void onBackPressed ()
	{
		 app.saveToDataBase();
		 this.finish();
	}
}
