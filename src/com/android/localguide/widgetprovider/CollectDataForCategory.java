package com.android.localguide.widgetprovider;

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



public class CollectDataForCategory {
	String result;
	int count;
	String searchString;
	int resultCount;
	int currentResultCount;
	private ArrayList<DataItem> itemsList;
	public ArrayList<String> resultsArray;
	public boolean isResultsFound = false;
	boolean isStarted;
	boolean isNetworkIssue = false;
	public boolean isLocation_scanning = true;
	class DataItem {
		String title;
		String address;
		String phonenumbers;
	}
	CollectDataForCategory()
	{
		isStarted = true;  // Assuming true	
		count = 0;
		currentResultCount = 0;
	    itemsList = new ArrayList<DataItem>();
	    resultsArray = new ArrayList<String>();
	}
	
	public void setSearchString(String search)
	{
		searchString = search;
	}
	
	public void setStartedSearch(boolean started)
	{
		isStarted = started;
	}
	public void setLocationScanning(boolean scanning) {
		isLocation_scanning = scanning;
	}
	
	int getResultCount()
	{
		return resultCount;
	}
	
	public int val;
	
	public int getCurrentPosition() {
		return val;
	}
	
	public int incrementCount()
	{
		val = count;
		
		if(count == (resultCount-1)) {
			count = 0;
		} else if(count < (resultCount -1)) {
			count++;
		}

		return val;
	}
	
	public void updateMoreResults()
	{
		currentResultCount+=8;
		isStarted = true;
		count=0;
		itemsList.clear();
		resultsArray.clear();
		sendSearchRequest();
		
	}
	
	public String getTitle()
	{
		String value="";
		value = itemsList.get(val).title;
		return value;
		
	}
	public String getPhoneNumbers()
	{
		String value="";
		value += itemsList.get(val).phonenumbers;			
		return value;
		
	}
	
	public String getAddress()
	{
		String value="";
		value += itemsList.get(val).address;
		return value;
	}
	
	public  void sendSearchRequest()
	   {
		
		HttpClient client = new DefaultHttpClient();
		String query = "http://ajax.googleapis.com/ajax/services/search/local?hl=en&v=1.0&rsz=8&q="+searchString+"&start="+currentResultCount;

		try {
			URL url = new URL(query);
			URI uri = new URI(url.getProtocol(), url.getHost(), url.getPath(), url.getQuery(), null);
			HttpGet request = new HttpGet(uri);
			HttpParams httpParameters = new BasicHttpParams();
			// Set the timeout in milliseconds until a connection is established.
			int timeoutConnection = 3000;
			HttpConnectionParams.setConnectionTimeout(client.getParams(), timeoutConnection);
			// Set the default socket timeout (SO_TIMEOUT) 
			// in milliseconds which is the timeout for waiting for data.
			int timeoutSocket = 5000;
			HttpConnectionParams.setSoTimeout(client.getParams(), timeoutSocket);
			
			HttpResponse response = client.execute(request);
			Userrequest(response);
		}catch (URISyntaxException e){
			System.out.println("URI syntax error 1 ************8 ");
		}
		catch(Exception ex){
			System.out.println("Neetworj error 1 ************8 ");
			ex.printStackTrace();
			isNetworkIssue = true;
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
	    	        resultsArray.add(result);
	    	        updateData(result);
	    	   }catch(Exception ex){
	    		   System.out.println("Neetworj error 2 ************8 ");
	    		   isNetworkIssue = true;
	    	   }
	    	    
	    	}
	 public  void updateData(String result)
	    {
		 
		// System.out.println("update result is ********** "+result);
	   	 try
	        {
	         JSONObject json=new JSONObject(result);
	         JSONArray ja;
	         json = json.getJSONObject("responseData");
	         
	         ja = json.getJSONArray("results");
	         resultCount = ja.length();
	         if(resultCount == 0 ) {
    	         isResultsFound = false;
    	         isStarted = false;
	             return ;
	         } else
	             isResultsFound = true;
	         
	         for (int i = 0; i < resultCount; i++)
	           {
	        	 DataItem item = new DataItem();
		        
		           JSONObject resultObject = ja.getJSONObject(i);
		           item.title = resultObject.get("titleNoFormatting").toString();
		           System.out.println("Result is **************** "+item.title);
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
		           item.address = addrr;
		           
			       JSONObject phone;
			       JSONArray numbers;
			       String phNumber="Ph : ";
			       String temp="";
			      
		 	      if(resultObject.has("phoneNumbers") == true )
			      {
		 	    	 numbers = resultObject.getJSONArray("phoneNumbers");
				      if(numbers !=null )
				      {
				    	  if(numbers.length() > 0)
				    	  {
				        		  phone = numbers.getJSONObject(0);
					    		  temp=phone.get("number").toString();
				      	  }
		  		      
				       }
			         }
			       phNumber+= temp;
			       item.phonenumbers = phNumber;
			       itemsList.add(item);
		        }

	         //Update isStarted boolean
	         isStarted = false;
	         }
	         catch(Exception e)
	         {
	        	 System.out.println("JSON parsing exception"+e.toString());
	         }
	    }
}
