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

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout.LayoutParams;

public class results extends ListActivity implements SpinnerButton.SpinnerButtonCallback {

	static String result="";
	private ArrayList<String> title;
	private ArrayList<String> address;
	String searchString;
    LinearLayout mScreenLayout;
    MyAnimation animation;
	Handler mHandler = new Handler();
	private int mCurrentResultCount = 0;
	SpinnerButton moreButton;
    Context mContext;
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
        searchString = bundle.getString("categoryString");
        searchString += " ";
        searchString += bundle.getString("locationString");

        title = new ArrayList<String>();
        address = new ArrayList<String>();
        mHandler.postDelayed(mDelayedTask, 2000);
        
        mScreenLayout = (LinearLayout) findViewById(R.id.resultsLayout);
        moreButton = (SpinnerButton)findViewById(R.id.morebutton);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, 50);
        moreButton.setLayoutParams(params);
        moreButton.setTextSize(30);
        moreButton.setParent(this);
        moreButton.start();
        animation = new MyAnimation();
        mScreenLayout.startAnimation(animation);
        ListView list =  getListView();
        
        list.setOnItemClickListener(new OnItemClickListener() { 
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
        		Intent intent = new Intent();
        		intent.setClass(results.this, OptionsScreen.class);
        		intent.setAction("com.mani.results");
        		  Bundle bun = new Bundle();
                  bun.putString("resultString", result);
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
			System.out.println("Neetworj error 1 ************8 ");
	          //txtResult.setText("Failed!");
		}
		
	   }
	 public  void Userrequest(HttpResponse response){
	    
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
	    	        moreButton.stop();
	    	        updateData(result);
	    	   }catch(Exception ex){
	    		   System.out.println("Neetworj error 2 ************8 ");
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
                AbsListView.LayoutParams params = new AbsListView.LayoutParams(AbsListView.LayoutParams.FILL_PARENT, 120);
                //LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                convertView.setLayoutParams(params);
                holder = new ViewHolder();
                holder.title = (TextView) convertView.findViewById(R.id.title);
                holder.address = (TextView) convertView.findViewById(R.id.address);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.title.setText(title.get(position));
            holder.title.setTextColor(Color.rgb(0xff, 0xff, 0xff));
            holder.title.setTextSize(22);
            
            holder.address.setText(address.get(position));
            holder.address.setTextColor(Color.rgb(0xff, 0xff, 0xff));
            holder.address.setTextSize(18);
            return convertView;
        }
         class ViewHolder {
            TextView title;
            TextView address;
        }
    }
}

