package com.android.localguide;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts.People;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class OptionsScreen extends Activity implements OptionsAddressLayout.MovementIndicator{
	
	Context mContext;
	LocalGuideApplication app;
	String result;
	OptionsAddressLayout layout;
	private ArrayList<String> title;
	private ArrayList<String> streetaddress;
	private ArrayList<String> phonenumbers;
	private ArrayList<String> latitude;
	private ArrayList<String> longitude;
	Button button1;
	Button button2;
	Button button3;
	Button button4;
	Button button5;
	Button button6;
	Button button7;
	
	private final int CALL_ID = 1;
	private final int MESSAGING_ID = 2;
	private final int TWITTER_ID = 3;
	private final int FACEBOOK_ID = 4;
	
	private Dialog dialog;
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
    app = (LocalGuideApplication)this.getApplication();
    
    mContext = this.getApplicationContext();
    layout1 = (TableLayout)findViewById(R.id.myTableLayout1);
    layout2 = (TableLayout)findViewById(R.id.myTableLayout);
    nextArrow = (ImageView)findViewById(R.id.next);
    previousArrow = (ImageView)findViewById(R.id.previous);
    layout = (OptionsAddressLayout)findViewById(R.id.addressLayout);
    button1 = (Button)findViewById(R.id.button1);
    button2 = (Button)findViewById(R.id.button2);
    button3 = (Button)findViewById(R.id.button3);
    button4 = (Button)findViewById(R.id.button4);
    button5 = (Button)findViewById(R.id.button5);
    button6 = (Button)findViewById(R.id.button6);
    button7 = (Button)findViewById(R.id.button7);
    
    layout.setParent(this);
    animation = new MyAnimation();
    
    /* get the results and position of list where user clicked from results activity */
    
    Bundle bundle= getIntent().getExtras();
    result = bundle.getString("resultString");
    currentaddress =  bundle.getInt("position");
    
    title = new ArrayList<String>();
    streetaddress = new ArrayList<String>();
    phonenumbers = new ArrayList<String>();
    latitude = new ArrayList<String>();
    longitude = new ArrayList<String>();
    
    updateAllDetails();
    
	String text;
	text = streetaddress.get(currentaddress);
	text+="\n";
	text += phonenumbers.get(currentaddress);
	
	layout.setTitle(title.get(currentaddress));
	layout.setAddress(text);
	layout1.startAnimation(animation);
    layout2.startAnimation(animation);
    
    /* Hide the previous arrow if the user selected first item in the result page */
    if(currentaddress == 0)
    	previousArrow.setVisibility(View.INVISIBLE);
    
    if(currentaddress == totalcount-1)
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
    
	button1.setOnClickListener( new View.OnClickListener(){
	    public void onClick(View v)
	    {
	    	showCallOptions();
	    }
     });
	
	button2.setOnClickListener( new View.OnClickListener(){
	    public void onClick(View v)
	    {
	    	showMessagingDialog();
	    }
     });
	
	button3.setOnClickListener( new View.OnClickListener(){
	    public void onClick(View v)
	    {
	    	saveToPhoneBook();
	    }
     });
	
	button4.setOnClickListener( new View.OnClickListener(){
	    public void onClick(View v)
	    {
	    	Toast.makeText(mContext, "button4", 4000).show();
	    }
     });
	
	button5.setOnClickListener( new View.OnClickListener(){
	    public void onClick(View v)
	    {
	    	Toast.makeText(mContext, "button5", 4000).show();
	    }
     });
	
	button6.setOnClickListener( new View.OnClickListener(){
	    public void onClick(View v)
	    {
	    	Intent intent = new Intent();
	    	intent.setClass(mContext, MapsActivity.class);
	    	startActivity(intent);
	    }
     });
	
	button7.setOnClickListener( new View.OnClickListener(){
	    public void onClick(View v)
	    {
			   if( 	app.addToFavorites(title.get(currentaddress),
			    				streetaddress.get(currentaddress),
			    				phonenumbers.get(currentaddress),
			    				latitude.get(currentaddress), 
			    				longitude.get(currentaddress)) == false )
			   {
				   Toast.makeText(mContext, "Already present in favorites list", 4000).show();
			   }
			   else
			   {
				   Toast.makeText(mContext, "Succesfully added to favorites", 4000).show();
			   }
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
//		    System.out.println("Totol count is ::::::::::::: "+totalcount+":::"+ja.toString());
		    for (int i = 0; i < resultCount; i++)
		      {
	
		      JSONObject resultObject = ja.getJSONObject(i);
		      
		      /* Add the title */
		      title.add(resultObject.get("titleNoFormatting").toString());
		      
		      /* Add Address */
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

		      /* Parse the phone numbers JSONobject */
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
	
				    	  if(numbers.length() ==1)
				    	  {
				    		  phone = numbers.getJSONObject(0);
				    		  temp=phone.get("number").toString();
				    	  }
				    	  else
				    	  {
				    		  for(int k=0;k<numbers.length();k++)
				    		  {
				    			  phone = numbers.getJSONObject(k);
				    			  temp+=phone.get("number").toString();
				    			  if(k != numbers.length()-1)
				    				  temp+=",";
				    		  }
				    		  
				    	  }
			    	 }
	  		      
			       }
		      }

		      phNumber+= temp;
		      phonenumbers.add(phNumber);
		      
		      /* Add latitude and longitude */
		      if(resultObject.has("lat"))
		    	  latitude.add(resultObject.get("lat").toString());
		      
		      if(resultObject.has("lng"))
		    	  longitude.add(resultObject.get("lng").toString());
		      
		      }
		    
	    }
	    catch(Exception e)
	    {
	    	System.out.println("Exception happened **************************"+e.toString());
	    }
    }
	
	public void showCallOptions()
	{
		
		String delims = "[,]";
		String[] numbers = phonenumbers.get(currentaddress).split(delims);
		for(int i =0;i< numbers.length ;i++)
			System.out.println("The numbers are *********** "+numbers[i]);
		
		if(numbers.length == 1)
		{
		 try {
		        Intent callIntent = new Intent(Intent.ACTION_CALL);
		        String numberString ="tel:";
		        numberString+=numbers[0];
		        System.out.println("The number is ******************* "+numberString);
		        callIntent.setData(Uri.parse(numberString));
		        startActivity(callIntent);
		    } catch (ActivityNotFoundException e) {
		        System.out.println("Call activity not FOUND FATAL error");
		    }
		}  
		else
		showDialog(CALL_ID);
	}
	
	public void showMessagingDialog()
	{
		//showDialog(MESSAGING_ID);
		/* Opening the default android messaging activity to send message. User can type in the extra information
		 * they like to share it to their friends
		 */
		
		Intent sendIntent = new Intent(Intent.ACTION_VIEW);
		String smsBody=title.get(currentaddress);
		smsBody+=", "+streetaddress.get(currentaddress);
		smsBody+=", "+phonenumbers.get(currentaddress);
		sendIntent.putExtra("sms_body", smsBody); 
		sendIntent.setType("vnd.android-dir/mms-sms");
		startActivity(sendIntent);
	}
	
	public void saveToPhoneBook()
	{
			ContentValues contact = new ContentValues();
			contact.put(People.NAME, title.get(currentaddress));
			Uri insertUri = getContentResolver().insert(People.CONTENT_URI, contact);
			Log.d(getClass().getSimpleName(),insertUri.toString());
			Uri phoneUri = Uri.withAppendedPath(insertUri, People.Phones.CONTENT_DIRECTORY);
			contact.clear();
			contact.put(People.Phones.TYPE, People.TYPE_MOBILE);

			String delims = "[:]";
			String[] temp = phonenumbers.get(currentaddress).split(delims);
			delims = "[,]";
			final String[] numbers = temp[1].split(delims);
			contact.put(People.Phones.NUMBER, numbers[0]);
			
			getContentResolver().insert(phoneUri, contact);
			 
			Toast.makeText(mContext, "Created a new contact: " + title.get(currentaddress) + " " + phonenumbers.get(currentaddress), Toast.LENGTH_SHORT).show();
	      
	}
	 protected Dialog onCreateDialog(int id) {  
		 
	  switch(id)
	  {
	  
	  case CALL_ID:
		  
			String delims = "[:]";
			String[] temp = phonenumbers.get(currentaddress).split(delims);
			System.out.println("Phonumbers are *** "+temp[0]+"::::"+temp[1]);
			delims = "[,]";
			final String[] numbers = temp[1].split(delims);
			
			AlertDialog.Builder builder;   
			Context mContext = this;   
			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);   
			View layout = inflater.inflate(R.layout.calldialog,(ViewGroup) findViewById(R.id.layout_call));   
			ListView listview = (ListView)layout.findViewById(R.id.call_list);   
			listview.setAdapter(new CallListAdapter(this,numbers));   
			 
			listview.setOnItemClickListener(new OnItemClickListener()   
					{
			    public void onItemClick(AdapterView<?> parent, View v,int position, long id) {   
					 try {
					        Intent callIntent = new Intent(Intent.ACTION_CALL);
					        String numberString ="tel:";
					        numberString+=numbers[0];
					        System.out.println("The number is ******************* "+numberString);
					        callIntent.setData(Uri.parse(numberString));
					      //  startActivity(callIntent);
					    } catch (ActivityNotFoundException e) {
					        System.out.println("Call activity not FOUND FATAL error");
					    }
			     	}   
			});
			 
			ImageView close = (ImageView) layout.findViewById(R.id.close);
			close.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v){
					 dialog.dismiss();
				}
			});
			 
			builder = new AlertDialog.Builder(mContext);   
			builder.setView(layout);   
			dialog = builder.create();   
			return dialog;
	  }
	  return null;
	 }
	 
	 /* Call List adapter to display the call icon and phone number along with it in the call list dialog */
	 
	   private  class CallListAdapter extends BaseAdapter {
	        private LayoutInflater mInflater;
	        private int phNumbersCount;
	        private String[] phoneNumbers;
	        public CallListAdapter(Context context,String[] number) {
	        	
	        	phoneNumbers = new String[number.length];
	        	phoneNumbers = number;
	        	phNumbersCount = phoneNumbers.length;
	            // Cache the LayoutInflate to avoid asking for a new one each time.
	            mInflater = LayoutInflater.from(context);
	        }

	        public int getCount() {
	            return phNumbersCount;
	        }
	        public Object getItem(int position) {
	            return position;
	        }

	        public long getItemId(int position) {
	            return position;
	        }

	        public View getView(int position, View convertView, ViewGroup parent) {
	            ViewHolder holder;
	            if (convertView == null) {
	                convertView = mInflater.inflate(R.layout.callview, null);
	                AbsListView.LayoutParams params = new AbsListView.LayoutParams(AbsListView.LayoutParams.FILL_PARENT, 50);
	                convertView.setLayoutParams(params);
	                holder = new ViewHolder();
	                holder.title = (TextView) convertView.findViewById(R.id.phonenumber);
	                convertView.setTag(holder);
	            } else {
	                holder = (ViewHolder) convertView.getTag();
	            }
	            holder.title.setText(phoneNumbers[position]);
	            holder.title.setTextColor(Color.rgb(0xff, 0xff, 0xff));
	            holder.title.setTextSize(22);                          	            
	            return convertView;
	        }
	         class ViewHolder {
	            TextView title;
	            }
	    }
	 
}
