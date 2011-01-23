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
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class results extends ListActivity  {

	static String result="";
	private ArrayList<String> title;
	private ArrayList<String> address;
	String searchString;
	private final int DIALOG = 1;
	ProgressDialog dialog;
	Handler mHandler = new Handler();
	private int mCurrentResultCount = 0;
	
    private Runnable mDelayedTask = new Runnable() {
        public void run() {
        	sendSearchRequest(mCurrentResultCount);
        	mCurrentResultCount+= 8;
        }
    };
	public void onCreate(Bundle savedInstanceState) {
      
		super.onCreate(savedInstanceState);
        setContentView(R.layout.results);
        Bundle bundle= getIntent().getExtras();
        searchString = bundle.getString("categoryString");
        searchString += " ";
        searchString += bundle.getString("locationString");
    
        title = new ArrayList<String>();
        address = new ArrayList<String>();
        //Show the searching dialog
        showDialog(DIALOG);
        mHandler.postDelayed(mDelayedTask, 2000);
        
        Button moreButton = (Button)findViewById(R.id.morebutton);
        
        moreButton.setOnClickListener(new OnClickListener()
        {
        	public void onClick(View v)
        	{
        		showDialog(DIALOG);
        		mHandler.postDelayed(mDelayedTask, 2000);
        	}
        });
        
        ListView list =  getListView();
        list.setOnItemClickListener(new OnItemClickListener() { 
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
        		Intent intent = new Intent();
        		intent.setClass(results.this, information.class);
        		  Bundle bun = new Bundle();
                  bun.putString("resultString", result); 
                  intent.putExtras(bun);
        		startActivity(intent);
        	}
        	
        }); 
	  }
	@Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG: {
                dialog = new ProgressDialog(this);
               // dialog.setTitle(false);
                dialog.setMessage("Searching...");
                dialog.setIndeterminate(true);
                dialog.setCancelable(true);
                return dialog;
            }
        }
        return null;
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
	         dialog.dismiss();
	         setListAdapter(new EfficientAdapter(this));
	         }
	         catch(Exception e)
	         {
	         	
	         
	         }
	    }
	 
	 public void onItemClick(AdapterView<?> adaptview, View clickedview, int position,
	                 long id) {
	             //TODO: ACTIONS
	             clickedview.setSelected(true);
	 }
    private  class EfficientAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        private Bitmap mIcon1;
        private Bitmap mIcon2;

        public EfficientAdapter(Context context) {
            // Cache the LayoutInflate to avoid asking for a new one each time.
            mInflater = LayoutInflater.from(context);

            // Icons bound to the rows.
            mIcon1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.next_arrow);
            mIcon2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.next_arrow);
        }

        public int getCount() {
            return title.size();
        	//return DATA.length;
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
                AbsListView.LayoutParams params = new AbsListView.LayoutParams(AbsListView.LayoutParams.FILL_PARENT, 100);
                convertView.setLayoutParams(params);
                holder = new ViewHolder();
                holder.title = (TextView) convertView.findViewById(R.id.title);
                holder.address = (TextView) convertView.findViewById(R.id.address);
                holder.icon = (ImageView) convertView.findViewById(R.id.icon);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.title.setText(title.get(position));
            holder.title.setTextColor(Color.rgb(0xbf, 0x6e, 0x46));
            holder.address.setText(address.get(position));
            holder.address.setTextSize(20);
            holder.address.setTextColor(Color.rgb(0xbf, 0x6e, 0x46));
            holder.address.setTextSize(18);
            holder.icon.setImageBitmap((position & 1) == 1 ? mIcon1 : mIcon2);
            
            return convertView;
        }
         class ViewHolder {
            TextView title;
            TextView address;
            ImageView icon;
        }
    }
}

