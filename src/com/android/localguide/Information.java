package com.android.localguide;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Information extends Activity{

	TextView information;
	Button twitter;
	public void onCreate(Bundle savedState)
	{
		super.onCreate(savedState);
		setContentView(R.layout.information);
//		information = (TextView)findViewById(R.id.information);
//		information.setText("Welcome to local guide");
		twitter = (Button )findViewById(R.id.twitter);
		twitter.setText("Twitter");
		twitter.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				TwitterClient client = new TwitterClient(Information.this);
				client.initialize();
				client.authenticate();
			}
		});
		
	}
}
