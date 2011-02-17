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

public class GetDirectionsList {
	
	public interface SearchResultCallBack {
		public void OnSearchCompleted(int result);
		public static final int SUCCESS 	= 1;
		public static final int NETWORK_FAILURE		= 2;
	}
	
	public class DirectionItem
	{
		
		double latitude;
		double longitude;
		int duration;
		String distance;
		String instructions;
	}
    

	private ArrayList<DirectionItem> DirectionItemList;
	private String result;
	private String startLocation;
	private String destination;
	private SearchResultCallBack mCB;
	
	GetDirectionsList(String start, String dest,SearchResultCallBack aCB)
	{
		startLocation = start;
		destination = dest;
		mCB = aCB;
	}
	
	public void searchRoutes()
	{
		  	HttpClient client = new DefaultHttpClient();
			String query = "http://maps.googleapis.com/maps/api/directions/json?origin="+startLocation+"&destination="+destination+"&sensor=false";

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
			 
			 System.out.println("Routes are ****** "+result);
		   	 try
		        {
		         JSONObject json=new JSONObject(result);
		         JSONArray ja;
		         ja = json.getJSONArray("routes");
		         json = ja.getJSONObject(0);
		         ja=json.getJSONArray("legs");   
                 json = ja.getJSONObject(0);
                 ja = json.getJSONArray("steps");
		         	         
		         int resultCount = ja.length();
		         
		         for (int i = 0; i < resultCount; i++)
		           {
		           JSONObject resultObject = ja.getJSONObject(i);
		           DirectionItem item = new DirectionItem();
		           
		           // Get the distance
		           item.distance = resultObject.getString("distance");
		           
		           // Get the duration
		           
		           // Get the lat,long
		           
		           // Get the instruction
		           
		           DirectionItemList.add(item);
		           
		           //System.out.println("REsult steps is "+resultObject.toString());
		           
		           }
		        }
		         catch(Exception e)
		         {
		         	
		         System.out.println("Exception is "+e.toString());
		         }
		    }
}
