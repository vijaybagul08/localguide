package com.android.localguide;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.auth.AuthenticationException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.android.localguide.FaceBookClient.FaceBookPostMessageCallBack;
import com.android.localguide.LocalGuideApplication.favoriteItem;
import android.os.Handler;

public class FavoritesResults extends Activity implements OptionsAddressLayout.MovementIndicator,
FaceBookClient.FaceBookPostMessageCallBack,TwitterClient.TwitterPostMessageCallBack ,
GetDirectionsDialog.GetDirectionsDialogListener,FacebookDialog.FacebookDialogListener,TwitterDialog.TwitterDialogListener{
	
	Context mContext;
	LocalGuideApplication app;
	String result;
	String location;
	OptionsAddressLayout layout;
	private ArrayList<String> title;
	private ArrayList<String> streetaddress;
	private ArrayList<String> phonenumbers;
	private ArrayList<String> latitude;
	private ArrayList<String> longitude;
	TwitterClient mTwitterClient;
	FaceBookClient mFacebookClient;
	GetDirectionsDialog mGetDirectionsDialog;
	FacebookDialog mFacebookDialog;
	TwitterDialog mTwitterDialog;
	
	Button button1;
	Button button2;
	Button button3;
	Button button4;
	Button button5;
	Button button6;
	Button button7;
	
	private Handler mHandler = new Handler();
	private final int CALL_ID = 1;
	private final int MESSAGING_ID = 2;
	
	private Dialog dialog;
	int totalcount = 0;
	static int currentaddress =0;
    TableLayout layout1;
    TableLayout layout2;
    MyAnimation animation;
    ImageView nextArrow;
    ImageView previousArrow;
    ArrayList<favoriteItem> mFavList;
	public void onCreate(Bundle savedInstanceState) {
		
	super.onCreate(savedInstanceState);
    setContentView(R.layout.options);
    app = (LocalGuideApplication)this.getApplication();
    
    mContext = this;
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
    mGetDirectionsDialog = new GetDirectionsDialog(this,this);
    mFacebookDialog = new FacebookDialog(this,this);
    mTwitterDialog = new TwitterDialog(this,this);
    mFacebookClient  = new FaceBookClient(this,this);
    System.out.println("Accesskey for facbook is ************* "+app.getFacebookToken());
    mFacebookClient.setAccessToken(app.getFacebookToken());
    
    
    mTwitterClient = new TwitterClient(this,app.getTwitterAccessKey(),app.getTwitterAccessSecret());
    System.out.println("Accesskey for Twitter is ************* "+app.getTwitterAccessKey()+"::::"+app.getTwitterAccessSecret());
    layout.setParent(this);
    animation = new MyAnimation();
    
    /* get the results and position of list where user clicked from results activity */
    
    title = new ArrayList<String>();
    streetaddress = new ArrayList<String>();
    phonenumbers = new ArrayList<String>();
    latitude = new ArrayList<String>();
    longitude = new ArrayList<String>();
    
    Bundle bundle= getIntent().getExtras();
    
    if(this.getIntent().getAction().equals("com.mani.favorites") == true )
    {
    	System.out.println("faqvoites options screen ********* ");
    	app = (LocalGuideApplication)this.getApplication();
    	mFavList = app.getFavoritesList();
    	totalcount = mFavList.size();
    	layout.setTotalCount(totalcount);
    	for(int count=0;count<mFavList.size();count++)
    	{
    		System.out.println("title is "+mFavList.get(count).title);
    		System.out.println("title is "+mFavList.get(count).streetAddress);
    		System.out.println("title is "+mFavList.get(count).phoneNumber);
    		System.out.println("title is "+mFavList.get(count).latitude);
    		System.out.println("title is "+mFavList.get(count).longitude);
    		title.add(mFavList.get(count).title);
    		streetaddress.add(mFavList.get(count).streetAddress);
    		phonenumbers.add(mFavList.get(count).phoneNumber);
    		latitude.add(mFavList.get(count).latitude);
    		longitude.add(mFavList.get(count).longitude);
    		button7.setText("Delete favorites");
    		button7.setOnClickListener( new View.OnClickListener(){
    		    public void onClick(View v)
    		    {
    				   if( 	app.deleteFavorites(title.get(currentaddress)) == true )
    				   {
    					   Toast.makeText(mContext, "Successfully delted from favorites list", 4000).show();
    					   //moveRight();
    					   deleteAddress();
    				   }
    				   else
    				   {
    					   Toast.makeText(mContext, "Failed to delete favorites", 4000).show();
    				   }
    		    }
    	     });
    	}
    }
    currentaddress =  bundle.getInt("position");
    location = bundle.getString("location");
        
	String text;
	text = streetaddress.get(currentaddress);
	text+="\n";
	text += phonenumbers.get(currentaddress);
	System.out.println("Position is *************************************** "+text);
	layout.setTitle(title.get(currentaddress));
	layout.setAddress(text);
	layout.setCurrentPosition(currentaddress);
	layout1.startAnimation(animation);
    layout2.startAnimation(animation);
    
    /* Hide the previous arrow if the user selected first item in the result page */
    if(currentaddress == 0)
    	nextArrow.setVisibility(View.INVISIBLE);
    
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
	    	if( app.isTwitterAutheticated() == false)
	    	{
	    		new ErrorDialog(mContext,"Not authenticated","Please go to help page and authenticate with your twitter account",false).show();
	    	}
	    	else
	    	{ 
		    	if(checkInternetConnection() == true )
		    	{
		    		String text;
		    		text = title.get(currentaddress);
		    		text+= ","+streetaddress.get(currentaddress);
		    		mTwitterDialog.setMessage(text);
		    		mTwitterDialog.show();
		    	}
		    	else
		    	{
		    		showInternetErrorDialog();
		    	}
	    	}
	    }
     });
	
	button5.setOnClickListener( new View.OnClickListener(){
	    public void onClick(View v)
	    {
	    	if( app.isFacebookAuthenticated() == false)
	    	{
	    		new ErrorDialog(mContext,"Not authenticated","Please go to help page and authenticate with your facebook account",false).show();
	    	}
	    	else
	    	{
		    	if(checkInternetConnection() == true )
		    	{
				  	String FBtext;
		    		FBtext = title.get(currentaddress);
		    		FBtext+= ","+streetaddress.get(currentaddress);
		    		mFacebookDialog.setMessage(FBtext);
		    		mFacebookDialog.show();
		    	}
		    	else
		    	{
		    		showInternetErrorDialog();
		    	}
	    	}
	    }
     });
	
	
	button6.setOnClickListener( new View.OnClickListener(){
	    public void onClick(View v)
	    {
	    	if(checkInternetConnection() == true )
	    	{
	    		mGetDirectionsDialog.show();
	    	}
	    	else
	    	{
	    		showInternetErrorDialog();
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
    		layout.setCurrentPosition(currentaddress+1);
    		layout.setAddress(text);	
    		previousArrow.setVisibility(View.VISIBLE);
    		if(currentaddress < 1)
    			nextArrow.setVisibility(View.INVISIBLE);

    	}
 	}
	
	public void moveRight()
	{
    	if(currentaddress < totalcount-1)
    	{
    		
    		if(currentaddress == totalcount-2)
    			previousArrow.setVisibility(View.INVISIBLE);

    		currentaddress++;
    		String text;
    		text = streetaddress.get(currentaddress);
    		text+="\n";
    		text += phonenumbers.get(currentaddress);
    		layout.setTitle(title.get(currentaddress));
    		layout.setCurrentPosition(currentaddress+1);
    		layout.setAddress(text);	
    		nextArrow.setVisibility(View.VISIBLE);
    	}
 	}

	public void deleteAddress() {
		System.out.println("Delete address ****************** "+currentaddress+"::"+totalcount);
		// If currentaddress > totalcount bring the next element in.
		if(currentaddress <totalcount) {
    		currentaddress++;
    		totalcount = mFavList.size();
    		String text;
    		text = streetaddress.get(currentaddress);
    		text+="\n";
    		text += phonenumbers.get(currentaddress);
    		layout.setTitle(title.get(currentaddress));
    		layout.setAddress(text);	
    		previousArrow.setVisibility(View.VISIBLE);			
		} else {
			if(totalcount < 0) {
				currentaddress=totalcount;
				totalcount = mFavList.size();
	    		String text;
	    		text = streetaddress.get(currentaddress);
	    		text+="\n";
	    		text += phonenumbers.get(currentaddress);
	    		layout.setTitle(title.get(currentaddress));
	    		layout.setAddress(text);	
	    		previousArrow.setVisibility(View.VISIBLE);			
			} else {
				layout.setTitle("No elements to display ........ ");
			}
		}
		// Else show the previous element.
	}

	public void onFBButtonOkPressed(String msg) {
		mFacebookClient.PostWallMessage(msg);
	}
	
	public void onTwitterButtonOkPressed(String msg) {
    	try
    	{
    		mTwitterClient.postTweet("test message from sample android app");
    	}
    	catch (JSONException e) {
			e.printStackTrace();
		} catch (AuthenticationException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void onButtonOkPressed(boolean isCurrentLocation,String alocation)
	{
    	Intent intent = new Intent();
    	Bundle bun = new Bundle();
    	bun.putString("currentaddress",streetaddress.get(currentaddress));
    	  System.out.println("location is ********* "+location);
  		if(isCurrentLocation == true)
  			bun.putString("location",location);
  		else
  			bun.putString("location",alocation);
  		
    	intent.putExtras(bun);
    	//intent.setClass(mContext, MapsActivity.class);
    	intent.setClass(mContext, MapstabActivity.class);
    	startActivity(intent);
	}

	public void showInternetErrorDialog()
	{
		new ErrorDialog(this,"No Internet Connection","Please enable the internet connection",false).show();
	}
	
	private boolean checkInternetConnection() {

		ConnectivityManager conMgr = (ConnectivityManager) mContext.getSystemService (mContext.CONNECTIVITY_SERVICE);

		// ARE WE CONNECTED TO THE NET

		if (conMgr.getActiveNetworkInfo() != null
		&& conMgr.getActiveNetworkInfo().isAvailable()
		&& conMgr.getActiveNetworkInfo().isConnected()) {
			return true;
		} else {
			return false;
		}

	} 

	public void onFaceBookmessagePostCompleted(int response)
	{
		switch(response)
		{
		case FaceBookPostMessageCallBack.POST_SUCCESSFULL:
			 mHandler.post(new Runnable() {
				public void run() {
					mFacebookDialog.dismiss();
					 Toast.makeText(mContext, "Successfully posted to your wall", 4000).show();					
				}
			 });
			break;
		case FaceBookPostMessageCallBack.POST_FAILURE:
			 mHandler.post(new Runnable() {
					public void run() {
						mFacebookDialog.dismiss();
						 Toast.makeText(mContext, "Post to Facebook failure", 4000).show();					
					}
				 });
				break;
		}
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
	
	public void showCallOptions()
	{
		
		String delims = "[,]";
		String[] numbers = phonenumbers.get(currentaddress).split(delims);
		
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
		        new ErrorDialog(this,"Error","Call activity not FOUND FATAL error",false).show();		        
		    }
		}  
		else
		showDialog(CALL_ID);
	}
	
	public void showMessagingDialog()
	{
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
		 AlertDialog.Builder builder;
		 Context mContext = this; 
		 LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE); 
		 View layout;
		 TextView hintTitle;
		 EditText message;
	  switch(id)
	  {
	  case CALL_ID:
		  
			String delims = "[:]";
			String[] temp = phonenumbers.get(currentaddress).split(delims);
			System.out.println("Phonumbers are *** "+temp[0]+"::::"+temp[1]);
			delims = "[,]";
			final String[] numbers = temp[1].split(delims);
			   
			layout = inflater.inflate(R.layout.calldialog,(ViewGroup) findViewById(R.id.layout_call));   
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

			dialog = new CustomDialog(mContext);
			dialog.setContentView(layout);
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
