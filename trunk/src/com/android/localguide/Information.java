package com.android.localguide;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.localguide.FaceBookClient.FaceBookAuthenticationCallBack;

public class Information extends Activity implements FaceBookAuthenticationCallBack{

	TextView information;
	Button twitter;
	Button facebook;
	Handler mHandler = new H();

	private int TWITTER = 1;
	private int FACEBOOK = 2;
	LocalGuideApplication app;
	
	public void onCreate(Bundle savedState)
	{
		super.onCreate(savedState);
		setContentView(R.layout.information);
		app = (LocalGuideApplication)this.getApplication();
		
		twitter = (Button )findViewById(R.id.twitter);
		
		if(app.isTwitterAutheticated())
			twitter.setText("Sign In");
		else
			twitter.setText("Try Different User");
		
		twitter.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				mHandler.sendEmptyMessage(TWITTER);
			}
			
		});
		facebook = (Button )findViewById(R.id.facebook);
		
		if(app.isFacebookAuthenticated())
			facebook.setText("Sign In");
		else
			facebook.setText("Try Different User");

		facebook.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				mHandler.sendEmptyMessage(FACEBOOK);
			}
			
		});
	}

	class H extends Handler
	{
		public void handleMessage(Message m)
		{
			if(m.what == TWITTER) 
			{
				TwitterClient client = new TwitterClient(Information.this);
				client.initialize();
				client.authenticate();				
			}
			else if(m.what == FACEBOOK);
			{
				FaceBookClient client = new FaceBookClient(Information.this,Information.this);
				client.initialize();
			}
		}
	}
	
	public void onFaceBookAuthenticateCompleted(int response,String token)
	{
		switch(response)
		{
		case FaceBookAuthenticationCallBack.AUTHENTICAION_SUCCESSFULL:
			app.updateFacebookToken(token);
			app.SetFacebookAuthenticated(true);
			break;
		case FaceBookAuthenticationCallBack.AUTHENTICAION_FAILURE:
			break;
			
		}
	}
	
}
