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

		if(app.isTwitterAutheticated())
			twitterButton.setText(this.getString(R.string.different_user));
		else
			twitterButton.setText(this.getString(R.string.sign_in));
			
		
		twitterButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				mHandler.sendEmptyMessage(TWITTER);
			}
			
		});
		facebookButton = (Button )findViewById(R.id.facebook);
		
		if(app.isFacebookAuthenticated())
			facebookButton.setText(this.getString(R.string.different_user));			
		else
			facebookButton.setText(this.getString(R.string.sign_in));


		facebookButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				mHandler.sendEmptyMessage(FACEBOOK);
			}
			
		});
	}

	public void onResume() {
		super.onResume();
		if (app.isTwitterAutheticated() == true) {
			twitterButton.setText(this.getString(R.string.different_user));
		}
	}
	class H extends Handler
	{
		public void handleMessage(Message m)
		{
			if(m.what == TWITTER) 
			{
            	if (app.isTwitterAutheticated() == false) {
	
					Intent i = new Intent(getApplicationContext(), PrepareRequestTokenActivity.class);
					i.putExtra("tweet_msg","hello");
					startActivityForResult(i,TWITTER_AUTHENTICATE);
            	}
		        
			}
			else if(m.what == FACEBOOK)
			{
				FaceBookClient client = new FaceBookClient(Information.this,Information.this);
				client.initialize();
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
