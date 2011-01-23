package com.android.localguide;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class options2 extends Activity{
	public void OnCreate(Bundle savedInstanceState)
	{
		 super.onCreate(savedInstanceState);
		   setContentView(R.layout.information);
		    String info= "Local guide <br> Provides an easy way to search for anything around you.Gives you detailed result of address,phone numbers.<br/><br/> The search categories can be <br/><span style=color:yellow >(eg. pubs brimingham,theatres liverpool, hospital edinburgh)</span> </p>";
		    
		    TextView information = (TextView)findViewById(R.id.information);
		    information.setText(info);
	}


}
