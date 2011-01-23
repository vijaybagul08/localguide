package com.android.localguide;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class information extends Activity{
	String result;
	TextView address;
	private ArrayList<String> title;
	private ArrayList<String> streetaddress;
	private ArrayList<String> phonenumbers;
	static int totalcount = 0;
	static int currentaddress =0;
    
	public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
    setContentView(R.layout.options);
    
    Bundle bundle= getIntent().getExtras();
    result = bundle.getString("resultString");
    
    title = new ArrayList<String>();
    streetaddress = new ArrayList<String>();
    phonenumbers = new ArrayList<String>();
    
    udateAllDetails();
    
    ImageView next = (ImageView)findViewById(R.id.next);
    ImageView previous = (ImageView)findViewById(R.id.previous);
    address = (TextView) findViewById(R.id.address);
    next.setOnClickListener( new View.OnClickListener(){
	    public void onClick(View v)
	    {
	    	//updateNext();
	    	
	    	if(currentaddress < totalcount)
	    	{
	    		String text;
	    		text = title.get(currentaddress);
	    		text += streetaddress.get(currentaddress);
	    		text += phonenumbers.get(currentaddress);
	    		address.setText(text);
	    		currentaddress++;
	    	}
	    }
    });
		
    previous.setOnClickListener( new View.OnClickListener(){
	    public void onClick(View v)
	    {
	    //	updatePrevious();
	    }
    });
    
	}
	void udateAllDetails()
	{
		try
		{
		    JSONObject json=new JSONObject(result);
		    JSONArray ja;
		    json = json.getJSONObject("responseData");
		    ja = json.getJSONArray("results");
		    
		    int resultCount = ja.length();
		    totalcount = ja.length();
		    
		    for (int i = 0; i < resultCount; i++)
		      {
		      JSONObject resultObject = ja.getJSONObject(i);
		      title.add(resultObject.get("titleNoFormatting").toString());
		      
		      JSONArray addr;
		      addr = resultObject.getJSONArray("addressLines");
		      int count = addr.length();
		      String temp="";
		      for(int j=0;j<count;j++)
		      {
		          temp+=addr.getString(j);
		          if(j==0)
		        	  temp+=',';
		      }
		      streetaddress.add(temp);
		      
		      JSONObject phone;
		      JSONArray numbers;
		      
		      numbers = resultObject.getJSONArray("phoneNumbers");
		      phone = numbers.getJSONObject(0);
		      temp=phone.get("number").toString();
		      phonenumbers.add(temp);
		      }
		    
	    }
	    catch(Exception e)
	    {
	    	
	    
	    }
    }
	
}
