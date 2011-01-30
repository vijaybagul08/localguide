package com.android.localguide;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class Information extends Activity{

	TextView information;
	public void onCreate(Bundle savedState)
	{
		super.onCreate(savedState);
		setContentView(R.layout.information);
		information = (TextView)findViewById(R.id.information);
		information.setText("Welcome to local guide");
	}
}
