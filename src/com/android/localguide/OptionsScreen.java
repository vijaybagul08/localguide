package com.android.localguide;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;

public class OptionsScreen extends Activity implements OptionsAddressLayout.MovementIndicator{
	
	String result;
	OptionsAddressLayout layout;
	private ArrayList<String> title;
	private ArrayList<String> streetaddress;
	private ArrayList<String> phonenumbers;
	static int totalcount = 0;
	static int currentaddress =0;
    TableLayout layout1;
    TableLayout layout2;
    MyAnimation animation;
    ImageView nextArrow;
    ImageView previousArrow;
    
	public void onCreate(Bundle savedInstanceState) {
		
	super.onCreate(savedInstanceState);
    setContentView(R.layout.options);
    
    layout1 = (TableLayout)findViewById(R.id.myTableLayout1);
    layout2 = (TableLayout)findViewById(R.id.myTableLayout);
    nextArrow = (ImageView)findViewById(R.id.next);
    previousArrow = (ImageView)findViewById(R.id.previous);
    layout = (OptionsAddressLayout)findViewById(R.id.addressLayout);
    layout.setParent(this);
    animation = new MyAnimation();
    
    /* get the results and position of list where user clicked from results activity */
    
    Bundle bundle= getIntent().getExtras();
    result = bundle.getString("resultString");
    currentaddress =  bundle.getInt("position");
    
    title = new ArrayList<String>();
    streetaddress = new ArrayList<String>();
    phonenumbers = new ArrayList<String>();
    
    updateAllDetails();
    
	String text;
	text = streetaddress.get(currentaddress);
	text+="\n";
	text += phonenumbers.get(currentaddress);
	
	layout.setTitle(title.get(currentaddress));
	layout.setAddress(text);
	layout1.startAnimation(animation);
    layout2.startAnimation(animation);
    previousArrow.setVisibility(View.INVISIBLE);
	nextArrow.setOnClickListener( new View.OnClickListener(){
	    public void onClick(View v)
	    {
	    	moveLeft();
	    }
    });
		
	previousArrow.setOnClickListener( new View.OnClickListener(){
	    public void onClick(View v)
	    {
	    	moveRight();
	    }
    });
    
	}
	
	public void moveLeft()
	{
		if(currentaddress >0)
    	{
    		currentaddress--;
    		String text;
    		text = streetaddress.get(currentaddress);
    		text+="\n";
    		text += phonenumbers.get(currentaddress);
    		layout.setTitle(title.get(currentaddress));
    		layout.setAddress(text);	
    		nextArrow.setVisibility(View.VISIBLE);
    	}
    	else
    		previousArrow.setVisibility(View.INVISIBLE);
	}
	
	public void moveRight()
	{
    	if(currentaddress < totalcount-1)
    	{
    		currentaddress++;
    		String text;
    		text = streetaddress.get(currentaddress);
    		text+="\n";
    		text += phonenumbers.get(currentaddress);
    		layout.setTitle(title.get(currentaddress));
    		layout.setAddress(text);	
    		previousArrow.setVisibility(View.VISIBLE);
    	}
    	else
    		nextArrow.setVisibility(View.INVISIBLE);
	}
	public void onMovementDetected(boolean isLeftMovement)
	{
		if(isLeftMovement)
		{
	    	moveLeft();
		}
		else
		{
			moveRight();
		}
	}
	void updateAllDetails()
	{
		try
		{
		    JSONObject json=new JSONObject(result);
		    JSONArray ja;
		    json = json.getJSONObject("responseData");
		    ja = json.getJSONArray("results");
		    
		    int resultCount = ja.length();
		    totalcount = ja.length();
		//    System.out.println("Totol count is ::::::::::::: "+totalcount+":::"+ja.toString());
		    for (int i = 0; i < resultCount; i++)
		      {
		      JSONObject resultObject = ja.getJSONObject(i);
		      title.add(resultObject.get("titleNoFormatting").toString());
		      System.out.println("title is :::::::: "+title.get(i));
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
		      System.out.println("title is :::::::: "+streetaddress.get(i));
		      JSONObject phone;
		      JSONArray numbers;
		      String phNumber="Ph : ";
		      temp="";
		      if(resultObject.has("phoneNumbers") == true )
		      {
		      numbers = resultObject.getJSONArray("phoneNumbers");
		      if(numbers !=null )
		      {
		    	  if(numbers.length() > 0)
		    	  {
		    	  System.out.println("Phone numbers is ::::::: "+numbers.toString());
		    	  phone = numbers.getJSONObject(0);
		    	  temp=phone.get("number").toString();
		    	  }
		      }
		      else
		      {
		    	  
		      }
		      }
		      phNumber+= temp;
		      phonenumbers.add(phNumber);
		      System.out.println("phonenumbers is "+phonenumbers.get(i));
		      }
		    
	    }
	    catch(Exception e)
	    {
	    	
	    System.out.println("Exception happened **************************"+e.toString());
	    }
    }
	
}
