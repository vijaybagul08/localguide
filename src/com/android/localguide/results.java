package com.android.localguide;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class results extends ListActivity implements SpinnerButton.SpinnerButtonCallback {

	static String result="";
	private ArrayList<String> title;
	private ArrayList<String> address;
	String searchString;
    LinearLayout mScreenLayout;
    MyAnimation animation;
    String location;
	Handler mHandler = new Handler();
	private int mCurrentResultCount = 0;
	SpinnerButton moreButton;
    Context mContext;
    Dialog mDialog;
	private Runnable mDelayedTask = new Runnable() {
        public void run() {
        	sendSearchRequest(mCurrentResultCount);
        	mCurrentResultCount+= 8;
        }
    };
	public void onCreate(Bundle savedInstanceState) {
      
		super.onCreate(savedInstanceState);
        setContentView(R.layout.results);
        mContext = this;
        Bundle bundle= getIntent().getExtras();
        mDialog = new ErrorDialog(this,"","Searching...",true);
        searchString = bundle.getString("categoryString");
        searchString += " ";
        searchString += bundle.getString("locationString");
        location = bundle.getString("locationString");
        title = new ArrayList<String>();
        address = new ArrayList<String>();
        mHandler.postDelayed(mDelayedTask, 2000);
        
        mScreenLayout = (LinearLayout) findViewById(R.id.resultsLayout);

        mDialog.show();
        animation = new MyAnimation();
        mScreenLayout.startAnimation(animation);
        ListView list =  getListView();
        AnimationSet set = new AnimationSet(true);
        Animation animation1 = AnimationUtils.loadAnimation(this, R.anim.slide_in_left);
        animation1.setDuration(300);
        set.addAnimation(animation1);
        LayoutAnimationController controller = new LayoutAnimationController(set,1.0f);
        
        list.setLayoutAnimation(controller);

        list.setOnItemClickListener(new OnItemClickListener() { 
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
        		  Intent intent = new Intent();
        		  intent.setClass(results.this, OptionsScreen.class);
        		  intent.setAction("com.mani.results");
        		  Bundle bun = new Bundle();
                  bun.putString("resultString", result);
                  bun.putString("location", location);
                  bun.putInt("position", position); 
                  intent.putExtras(bun);
                  startActivity(intent);
        	}
        	
        }); 
	  }
	
	public void onButtonPress()
	{
		mHandler.postDelayed(mDelayedTask, 2000);
	}
	public void startMoreResults()
	{
		mHandler.postDelayed(mDelayedTask, 2000);
	}
	public  void sendSearchRequest(int count)
	   {
	  	HttpClient client = new DefaultHttpClient();
		String query = "http://ajax.googleapis.com/ajax/services/search/local?hl=en&v=1.0&rsz=8&q="+searchString+"&start=";
		query+=count;
		System.out.println("Query is ******************* "+query);
		try {
			URL url = new URL(query);
			URI uri = new URI(url.getProtocol(), url.getHost(), url.getPath(), url.getQuery(), null);
			HttpGet request = new HttpGet(uri);
			HttpParams httpParameters = new BasicHttpParams();
			// Set the timeout in milliseconds until a connection is established.
			int timeoutConnection = 3000;
			HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
			// Set the default socket timeout (SO_TIMEOUT) 
			// in milliseconds which is the timeout for waiting for data.
			int timeoutSocket = 5000;
			HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
			
			HttpResponse response = client.execute(request);
			Userrequest(response);
		}catch (URISyntaxException e){
			
		}
		catch(Exception ex){
			System.out.println("Please try again... "+ex.toString()+":::");
			mDialog.dismiss();
			this.finish();
		}
		
	   }
	 public  void Userrequest(HttpResponse response){
	    System.out.println("response is *** "+response);
	    	try{
	    	        InputStream in = response.getEntity().getContent();
	    	        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
	    	        StringBuilder str = new StringBuilder();
	    	        String line = null;
	    	        while((line = reader.readLine()) != null){
	    	            str.append(line + "\n");
	    	        }
	    	        in.close();
	    	        result = str.toString();
	    	        mDialog.dismiss();
	    	        updateData(result);
	    	   }catch(Exception ex){
	    		   System.out.println("Neetworj error + ************8 "+ex.toString()+":::");
	    	        result = "Error";
	    	   }
	    	    
	    	}
	 public  void updateData(String result)
	    {
	   	 try
	        {
	         JSONObject json=new JSONObject(result);
	         JSONArray ja;
	         json = json.getJSONObject("responseData");
	         ja = json.getJSONArray("results");
	         
	         int resultCount = ja.length();
	         for (int i = 0; i < resultCount; i++)
	           {
	           JSONObject resultObject = ja.getJSONObject(i);
	           title.add(resultObject.get("titleNoFormatting").toString());
	           JSONArray addr;
	           addr = resultObject.getJSONArray("addressLines");
	           int count = addr.length();
	           String addrr="";
	           for(int j=0;j<count;j++)
	           {
	               addrr+=addr.getString(j);
	               if(j==0)
	             	  addrr+=',';
	           }
	           address.add(addrr);
	           }
	         setListAdapter(new EfficientAdapter(this));
	         }
	         catch(Exception e)
	         {
	         	
	         
	         }
	    }
	 
    private  class EfficientAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        public EfficientAdapter(Context context) {
            // Cache the LayoutInflate to avoid asking for a new one each time.
            mInflater = LayoutInflater.from(context);
        }

        public int getCount() {
            return title.size();
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
                convertView = mInflater.inflate(R.layout.listview, null);
                holder = new ViewHolder();
                holder.title = (TextView) convertView.findViewById(R.id.title);
                holder.address = (TextView) convertView.findViewById(R.id.address);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.title.setText(title.get(position));
            holder.title.setTextColor(Color.rgb(0xff, 0xff, 0xff));
            
            holder.address.setText(address.get(position));
            holder.address.setTextColor(Color.rgb(0xff, 0xff, 0xff));
            return convertView;
        }
         class ViewHolder {
            TextView title;
            TextView address;
        }
    }
}

