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
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Contacts.People;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.android.localguide.FaceBookClient.FaceBookPostMessageCallBack;
import com.android.localguide.LocalGuideApplication.favoriteItem;

public class OptionsScreen extends Activity implements OptionsAddressLayout.MovementIndicator,
FaceBookClient.FaceBookPostMessageCallBack,GetDirectionsDialog.GetDirectionsDialogListener,
FacebookDialog.FacebookDialogListener,TwitterDialog.TwitterDialogListener{
	
	Context mContext;
	LocalGuideApplication app;
	String result;
	String location;
	OptionsAddressLayout layout;
	private ArrayList<String> resultArray;
	private ArrayList<String> title;
	private ArrayList<String> streetaddress;
	private ArrayList<String> phonenumbers;
	private ArrayList<String> latitude;
	private ArrayList<String> longitude;

	FaceBookClient mFacebookClient;
	GetDirectionsDialog mGetDirectionsDialog;
	FacebookDialog mFacebookDialog;
	TwitterDialog mTwitterDialog;
	
	ListView mListView;
	
	private final int CALL_ID = 1;
	private final int MESSAGING_ID = 2;
	final String FONT_TTF = "quicksand_bold.ttf";
	private Handler mHandler = new Handler();
	private Dialog dialog;
	int totalcount = 0;
	int currentaddress =0;
    TableLayout layout1;
    LinearLayout layout2;
    MyAnimation animation;
    ImageView nextArrow;
    ImageView previousArrow;
    TextView call;
    TextView message;
    TextView phonebook;
    TextView twitter_option;
    TextView facebook_option;
    TextView diection;
    TextView favorite;
    
    ArrayList<favoriteItem> mFavList;
    ArrayList<String> options;
    boolean isLaunchedFromAppWidget = false;
    static Typeface mFont;
    
	public void onCreate(Bundle savedInstanceState) {
		
	super.onCreate(savedInstanceState);
    setContentView(R.layout.options);
    app = (LocalGuideApplication)this.getApplication();
    
    mContext = this;
    layout1 = (TableLayout)findViewById(R.id.myTableLayout1);
    mListView = (ListView)findViewById(R.id.options_list);
    nextArrow = (ImageView)findViewById(R.id.next);
    previousArrow = (ImageView)findViewById(R.id.previous);
    layout = (OptionsAddressLayout)findViewById(R.id.addressLayout);
    mGetDirectionsDialog = new GetDirectionsDialog(this,this);
    mFacebookDialog = new FacebookDialog(this,this);
    mTwitterDialog = new TwitterDialog(this,this);
    
    mFacebookClient  = new FaceBookClient(this,this);
    mFacebookClient.setAccessToken(app.getFacebookToken());

    layout.setParent(this);
    totalcount = 0;
    animation = new MyAnimation();
    
    options = new ArrayList<String>();
    options.add("- "+this.getString(R.string.call));
    options.add("- "+this.getString(R.string.message));
    options.add("- "+this.getString(R.string.phonebook));
    options.add("- "+this.getString(R.string.twitter));
    options.add("- "+this.getString(R.string.facebook));
    options.add("- "+this.getString(R.string.direction));
    options.add("- "+this.getString(R.string.fav_description));
    /* get the results and position of list where user clicked from results activity */
    
    title = new ArrayList<String>();
    streetaddress = new ArrayList<String>();
    phonenumbers = new ArrayList<String>();
    latitude = new ArrayList<String>();
    longitude = new ArrayList<String>();
    
    Bundle bundle= getIntent().getExtras();
    
    if(this.getIntent().getAction().equals("com.mani.favorites") == true )
    {
    	app = (LocalGuideApplication)this.getApplication();
    	mFavList = app.getFavoritesList();
    	for(int count=0;count<mFavList.size();count++)
    	{
    		title.add(mFavList.get(count).title);
    		streetaddress.add(mFavList.get(count).streetAddress);
    		phonenumbers.add(mFavList.get(count).phoneNumber);
    		latitude.add(mFavList.get(count).latitude);
    		longitude.add(mFavList.get(count).longitude);
    	}
    }
    else if(this.getIntent().getAction().equals("com.mani.results") == true)
    {
    	    resultArray = bundle.getStringArrayList("resultString");
    	    updateAllDetails();
    	    currentaddress =  bundle.getInt("position");
    }
    else if(this.getIntent().getAction().equals("com.mani.widgetprodiver") == true)
    {
    		isLaunchedFromAppWidget = true;
    		currentaddress = this.getIntent().getIntExtra("position", 0);
    		resultArray = bundle.getStringArrayList("resultString");
    	    updateAllDetails();
    }

    
    location = bundle.getString("location");
    
	String text;
	text = streetaddress.get(currentaddress);
	text+="\n";
	text += phonenumbers.get(currentaddress);
	layout.setTitle(title.get(currentaddress));
	layout.setAddress(text);
	layout.setCurrentPosition(currentaddress+1);
	layout1.startAnimation(animation);
    
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

    mListView.setOnItemClickListener(new OnItemClickListener() { 
        public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
        	switch(position){
	        	case 0:
	        		showCallOptions();	        		
	        		break;
	        	case 2:
	        		saveToPhoneBook();
	        	    break;
	        	case 1:
	        		showMessagingDialog();
	        		break;
	        	case 3:
	        		showtwitterDialog();
	        		break;
	        	case 4:
	        		showfacebookDialog();
	        		break;
	        	case 5:
	    	    	if(checkInternetConnection() == true )
	    	    	{
	    	    		mGetDirectionsDialog.show();
	    	    	}
	    	    	else
	    	    	{
	    	    		showInternetErrorDialog();
	    	    	}

	        		break;
	        	case 6:
		        	{
		     		   LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
		    		   View layout;
		        		
	 				   if( 	app.addToFavorites(title.get(currentaddress),
			    				streetaddress.get(currentaddress),
			    				phonenumbers.get(currentaddress),
			    				latitude.get(currentaddress), 
			    				longitude.get(currentaddress)) == false )
					   {
					   	   layout = inflater.inflate(R.layout.custom_toast, null);
			               TextView message = (TextView)layout.findViewById(R.id.message);
				           message.setTypeface(getTypeface(mContext,FONT_TTF));
				           message.setText(mContext.getString(R.string.fav_already_present));
				           ImageView info = (ImageView)layout.findViewById(R.id.warning);
				           info.setImageResource(R.drawable.info_icon);
				           Toast toastView = new Toast(mContext);
				           toastView.setView(layout);
				           toastView.setDuration(Toast.LENGTH_LONG);
				           toastView.setGravity(Gravity.CENTER, 0,0);
				           toastView.show();
					   }
					   else
					   {
					   	   layout = inflater.inflate(R.layout.custom_toast, null);
			               TextView message = (TextView)layout.findViewById(R.id.message);
				           message.setTypeface(getTypeface(mContext,FONT_TTF));
				           message.setText(mContext.getString(R.string.fav_added));
				           ImageView info = (ImageView)layout.findViewById(R.id.warning);
				           info.setImageResource(R.drawable.info_icon);
				           Toast toastView = new Toast(mContext);
				           toastView.setView(layout);
				           toastView.setDuration(Toast.LENGTH_LONG);
				           toastView.setGravity(Gravity.CENTER, 0,0);
				           toastView.show();
					   }
		        	}
	        		break;
        	}
        }
    });
	
	 mHandler.post(new Runnable() {
			public void run() {
				mListView.setAdapter(new OptionsAdapter(OptionsScreen.this));
			}
	 });
	 
	}
	
	public static Typeface getTypeface(Context context, String typeface) {
	    if (mFont == null) {
	        mFont = Typeface.createFromAsset(context.getAssets(), typeface);
	    }
	    return mFont;
	}
	
	public void onBackPressed ()
	{
		if(isLaunchedFromAppWidget == true) {
			isLaunchedFromAppWidget = false;
			startActivity(new Intent("com.android.localguideTabScreen"));
		}
		finish();
	}
	public void showtwitterDialog() {
    	if( app.isTwitterAutheticated() == false)
    	{
    		new ErrorDialog(mContext,mContext.getString(R.string.not_authenticated),mContext.getString(R.string.twitter_auth_pls),false).show();
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
	
	public void showfacebookDialog() {
    	if( app.isFacebookAuthenticated() == false)
    	{
    		new ErrorDialog(mContext,mContext.getString(R.string.not_authenticated),mContext.getString(R.string.facebook_auth_pls),false).show();
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
	public void onFBButtonOkPressed(String msg) {
		mFacebookClient.PostWallMessage(msg);
	}
	
	public void onTwitterButtonOkPressed(String msg) {
		
    	if (TwitterUtils.isAuthenticated(app)) {
    		sendTweet(msg);
    	} 
	}
	
	public void sendTweet(String msg) {
		final String msgg = msg;
		Thread t = new Thread() {
	        public void run() {
	        	
	        	try {
	        		TwitterUtils.sendTweet(app,msgg);
	        		mHandler.post(new Runnable() {
						public void run() {
						   mTwitterDialog.dismiss();
						   LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);							 
						   View layout = inflater.inflate(R.layout.custom_toast, null);
						   TextView message = (TextView)layout.findViewById(R.id.message);
						   message.setTypeface(getTypeface(mContext,FONT_TTF));
						   message.setText(mContext.getString(R.string.twitter_success));
						   ImageView info = (ImageView)layout.findViewById(R.id.warning);
						   info.setImageResource(R.drawable.info_icon);
						   Toast toastView = new Toast(mContext);
						   toastView.setView(layout);
						   toastView.setDuration(Toast.LENGTH_LONG);
						   toastView.setGravity(Gravity.CENTER, 0,0);
						   toastView.show();
							 
							 //Toast.makeText(mContext, mContext.getString(R.string.twitter_success), 4000).show();					
						}
					 });

				} catch (Exception ex) {
					ex.printStackTrace();
				}
	        }
	    };
	    t.start();
	}

	
	public void onButtonOkPressed(boolean isCurrentLocation,String alocation)
	{
    	Intent intent = new Intent();
    	Bundle bun = new Bundle();
    	bun.putString("currentaddress",streetaddress.get(currentaddress));

  		if(isCurrentLocation == true)
  			bun.putString("location",location);
  		else
  			bun.putString("location",alocation);
  		
    	intent.putExtras(bun);
    	intent.setClass(mContext, MapstabActivity.class);
    	startActivity(intent);
	}

	public void showInternetErrorDialog()
	{
		new ErrorDialog(this,mContext.getString(R.string.no_internet),mContext.getString(R.string.enable_internet),false).show();
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
					   LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);							 
					   View layout = inflater.inflate(R.layout.custom_toast, null);
					   TextView message = (TextView)layout.findViewById(R.id.message);
					   message.setTypeface(getTypeface(mContext,FONT_TTF));
					   message.setText(mContext.getString(R.string.facebook_success));
					   ImageView info = (ImageView)layout.findViewById(R.id.warning);
					   info.setImageResource(R.drawable.info_icon);
					   Toast toastView = new Toast(mContext);
					   toastView.setView(layout);
					   toastView.setDuration(Toast.LENGTH_LONG);
					   toastView.setGravity(Gravity.CENTER, 0,0);
					   toastView.show();
				}
			 });
			break;
		case FaceBookPostMessageCallBack.POST_FAILURE:
			 mHandler.post(new Runnable() {
					public void run() {
						 mFacebookDialog.dismiss();
						   LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);							 
						   View layout = inflater.inflate(R.layout.custom_toast, null);
						   TextView message = (TextView)layout.findViewById(R.id.message);
						   message.setTypeface(getTypeface(mContext,FONT_TTF));
						   message.setText(mContext.getString(R.string.facebook_failure));
						   Toast toastView = new Toast(mContext);
						   toastView.setView(layout);
						   toastView.setDuration(Toast.LENGTH_LONG);
						   toastView.setGravity(Gravity.CENTER, 0,0);
						   toastView.show();						 
					}
				 });
			break;
		}
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

	
	public void onMovementDetected(boolean isLeftMovement)
	{
		layout.performHapticFeedback (android.view.HapticFeedbackConstants.VIRTUAL_KEY, android.view.HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
		
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
			for(int l =0;l<resultArray.size();l++ )
			{
		    JSONObject json=new JSONObject(resultArray.get(l));
		    JSONArray ja;
		    json = json.getJSONObject("responseData");
		    ja = json.getJSONArray("results");
		    
		    int resultCount = ja.length();
		    totalcount += ja.length();

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
		      System.out.println("Title , street is ******* "+title.get(i)+"::"+streetaddress.get(i));
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
			layout.setTotalCount(totalcount);
		}
	    catch(Exception e)
	    {
	    	//System.out.println("Exception happened **************************"+e.toString());
	    	throw new RuntimeException(e); 
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
		        callIntent.setData(Uri.parse(numberString));
		        startActivity(callIntent);
		    } catch (ActivityNotFoundException e) {
		    	Toast.makeText(mContext, "Call activity not FOUND FATAL error", 4000).show();
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
			String text = String.format(mContext.getString(R.string.create_contact), title.get(currentaddress) + " " + phonenumbers.get(currentaddress));
			   LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);							 
			   View layout = inflater.inflate(R.layout.custom_toast, null);
			   TextView message = (TextView)layout.findViewById(R.id.message);
			   message.setTypeface(getTypeface(mContext,FONT_TTF));
			   message.setText(text);
			   ImageView info = (ImageView)layout.findViewById(R.id.warning);
			   info.setImageResource(R.drawable.info_icon);
			   Toast toastView = new Toast(mContext);
			   toastView.setView(layout);
			   toastView.setDuration(Toast.LENGTH_LONG);
			   toastView.setGravity(Gravity.CENTER, 0,0);
			   toastView.show();			
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
	                AbsListView.LayoutParams params = new AbsListView.LayoutParams(AbsListView.LayoutParams.FILL_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
	                convertView.setLayoutParams(params);
	                holder = new ViewHolder();
	                holder.title = (TextView) convertView.findViewById(R.id.phonenumber);
	                holder.title.setTypeface(getTypeface(mContext,FONT_TTF));
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
	   
		int icons[]={ R.drawable.telephone,R.drawable.message,R.drawable.phonebook,R.drawable.twitter_icon,R.drawable.facebook,R.drawable.globe,R.drawable.favorite};
		//String options[] = { " - Call"," - Message"," - Save to Phonebook"," - Twitter"," - Facebook"," - Get directions"," - Save to Favorites"};

		private  class OptionsAdapter extends BaseAdapter {
	        private LayoutInflater mInflater;

	        public OptionsAdapter(Context context) {
	            // Cache the LayoutInflate to avoid asking for a new one each time.
	            mInflater = LayoutInflater.from(context);
	        }

	        public int getCount() {
	            return options.size();
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
	                convertView = mInflater.inflate(R.layout.options_item, null);
	                holder = new ViewHolder();
	                holder.icon = (ImageView) convertView.findViewById(R.id.icon);
	                holder.option = (TextView) convertView.findViewById(R.id.option);
	                holder.option.setTypeface(getTypeface(mContext,FONT_TTF));
	                convertView.setTag(holder);
	            } else {
	                holder = (ViewHolder) convertView.getTag();
	            }
	            holder.icon.setImageBitmap(BitmapFactory.decodeResource(mContext.getResources(), icons[position]));
	            holder.option.setText(options.get(position));
	            return convertView;
	        }
	         class ViewHolder {
	            ImageView icon;
	            TextView option;
	        }
	    }

}
