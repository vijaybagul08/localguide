package com.android.localguide;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import oauth.signpost.OAuth;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;

import org.apache.http.auth.AuthenticationException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.ParseException;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class TwitterActivity extends Activity{

    private static final String CONSUMER_KEY = "9rGstuoFXWINNjk10wVzQ";
    private static final String CONSUMER_SECRET = "NhoXr4cHOgMj7VY36FMJQiqKme2EzRAmCPcBZU18";
    private static final String CALLBACK_URL = "TwitterAuth://twitt";
    
    private static final String REQUEST_URL = "http://twitter.com/oauth/request_token";
    private static final String ACCESS_URL = "http://twitter.com/oauth/access_token";
    private static final String AUTHORIZE_URL = "http://twitter.com/oauth/authorize";
    public static final String FETCH_CREDENTIALS_URI = "http://api.twitter.com/1/account/verify_credentials.json";
    public static final String FETCH_UPDATESTATUS_URI= "http://api.twitter.com/1/statuses/update.json";
    
    private String mConsumerKey;
    private String mConsumerSecret;
    private String mCallbackUrl;
    
    private String mRequestTokenUrl;
    private String mAccessTokenUrl;
    private String mAuthorizeUrl;
    
    private String mAccessKey;
    private String mAccessSecret;
    
    private static CommonsHttpOAuthConsumer mConsumer;
    
    CommonsHttpOAuthProvider mProvider;
    private static HttpClient mClient;
    
    private WebView mWebView;
    Context mContext;
    private static String result;
    
	public void onCreate(Bundle savedInstance)
	{
		super.onCreate(savedInstance);
		mContext = this.getApplicationContext();
	    mConsumerKey = CONSUMER_KEY;
	    mConsumerSecret = CONSUMER_SECRET;
	    mCallbackUrl = CALLBACK_URL;
	    mRequestTokenUrl = REQUEST_URL;
	    mAccessTokenUrl = ACCESS_URL;
	    mAuthorizeUrl = AUTHORIZE_URL;
	    
	    if(checkInternetConnection() == false)
	    {
    		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
    		alertDialog.setTitle("No Internect Connection");
    		alertDialog.setMessage("Please Enable the internet connection to authenticate");
    		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
    		      public void onClick(DialogInterface dialog, int which) {
    		 
    		       //here you can add functions
    		 
    		    } });
    		alertDialog.setIcon(R.drawable.icon);
    		alertDialog.show();
    		finish();
	    }
	    else
	    {
	    	mConsumer = new CommonsHttpOAuthConsumer(mConsumerKey, mConsumerSecret);
	    	mProvider = new CommonsHttpOAuthProvider(mRequestTokenUrl, mAccessTokenUrl,mAuthorizeUrl);
	    	mClient = new DefaultHttpClient();  
	    	mWebView = new WebView(this);
	    	setContentView (mWebView);
	    	authenticate();
	    }
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
	
	public void showError(String error)
	{
   		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle("error");
		alertDialog.setMessage("Please Try again");
		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
		      public void onClick(DialogInterface dialog, int which) {
		 
		       //here you can add functions
		    	  finish();
		 
		    } });
		alertDialog.setIcon(R.drawable.icon);
		alertDialog.show();
		finish();
	}
	public void authenticate()
	{
    	try {
    		String authURL = mProvider.retrieveRequestToken(mConsumer, mCallbackUrl);
	    	 
			 mWebView.getSettings().setJavaScriptEnabled(true);
			 mWebView.loadUrl(authURL);
			 mWebView.setWebViewClient(new RestWebViewClient());
	    } catch (OAuthMessageSignerException e) {
	    	e.printStackTrace();
	    	
	    } catch (OAuthNotAuthorizedException e) {
	    	e.printStackTrace();
	    	
	    } catch (OAuthExpectationFailedException e) {
	    	e.printStackTrace();
	    	
	    } catch (OAuthCommunicationException e) {
	    	e.printStackTrace();
	    	
	    }

	}
    private void getToken (Uri uri) {
    	
		String verifier = uri.getQueryParameter(OAuth.OAUTH_VERIFIER);
		try {

			mProvider.retrieveAccessToken(mConsumer, verifier);
			mAccessKey = mConsumer.getToken();
			mAccessSecret = mConsumer.getTokenSecret();
			
			System.out.println("Accesskey = " + mAccessKey);
			System.out.println( "AccessSecret = " + mAccessSecret);
			postTweet("sent to post");
			fetchUserCredentials();
		} catch (OAuthMessageSignerException e) {
			showError("OauthSigning Exception");
		} catch (OAuthNotAuthorizedException e) {
			showError("OauthNot authorized Exception");
		} catch (OAuthExpectationFailedException e) {
			showError("Oauth failed Exception");
		} catch (OAuthCommunicationException e) {
			showError("OauthCommunication Exception");
		} catch(Exception e)
		{
			showError("OauthSigning Exception");
		}
    }
	public void fetchUserCredentials() throws JSONException,
    ParseException, IOException, AuthenticationException {
			try {
				
				// create a request that requires authentication
				HttpGet post = new HttpGet(FETCH_CREDENTIALS_URI);
			    final List<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
			    nvps.add(new BasicNameValuePair("include_entities", "true"));
			    mConsumer.setTokenWithSecret(mAccessKey, mAccessSecret); // Working properly key, secret is the order.
			    
			    // set this to avoid 417 error (Expectation Failed)
			    post.getParams().setBooleanParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, false);
			    // sign the request
			    mConsumer.sign(post);
			    // send the request
				org.apache.http.HttpResponse response = mClient.execute(post);
			
				int statusCode = response.getStatusLine().getStatusCode();
			    final String reason = response.getStatusLine().getReasonPhrase();
			
			    System.out.println( "Posting message, statuscode = " + statusCode);
			    if (statusCode != 200) {
			        System.out.println("Reason is :::"+ reason);
			    
			        if(statusCode == 401)
			        {
			        	throw new AuthenticationException();
			        }
			    }
			    else
			    {	
			    	// response status should be 200 OK
			    	parseResponse(response);
					// release connection
			        response.getEntity().consumeContent();	
			    	try
			    	{
			    	 JSONObject json=new JSONObject(result);
			         JSONObject status;
			         status = json.getJSONObject("status");
			         // Update the callback with secret and key and username
			        // mCB.onTwitterAuthenticateCompleted(TwitterAuthenticationCallBack.AUTHENTICAION_SUCCESSFULL, mAccessKey, mAccessSecret, json.get("name").toString());
			         System.out.println("Name is "+json.get("name").toString());
						Intent data = new Intent();
						Bundle bun = new Bundle();
						bun.putString("AccessKey", mAccessKey);
						bun.putString("AccessSecret", mAccessSecret);
						bun.putString("UserName", json.getString("name").toString());
						data.putExtras(bun);
						
						this.setResult(RESULT_OK, data);
						finish();

			         }
			         catch(Exception e)
			         {
			        	 showError("JSON parsing Exception");
			         }
			    }
			} catch (OAuthMessageSignerException e) {
				showError("OauthMessage signing Exception");
			} catch (OAuthExpectationFailedException e) {
				showError("OauthFailed Exception");
			} catch (OAuthCommunicationException e) {
				showError("OauthCommunication Exception");
			} catch (UnsupportedEncodingException e) {
				showError("Unspported Encoding Exception");
			} catch (IOException e) {
				showError("Input Ouput Exception");
			}
			}
	public void postTweet(String tweetMessage) throws JSONException,
    ParseException, IOException, AuthenticationException {
		try {
			
			List<BasicNameValuePair> qparams = new ArrayList<BasicNameValuePair>();
			qparams.add(new BasicNameValuePair("status","@vinothg123 Visint next week bangkok"));
			qparams.add(new BasicNameValuePair("in_reply_to_status_id", "9100592710418432"));
		
		    URI uri = URIUtils.createURI(null,FETCH_UPDATESTATUS_URI, -1, null, URLEncodedUtils.format(qparams, "UTF-8"), null);
		    
			// create a request that requires authentication
		    HttpPost post = new HttpPost(FETCH_UPDATESTATUS_URI);
		    post.setEntity(new UrlEncodedFormEntity(qparams, HTTP.UTF_8)); 
		    post.getParams().setBooleanParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, false);
		    mConsumer.setTokenWithSecret(mAccessKey, mAccessSecret); // Working properly key, secret is the order.
		    
		    // sign the request
		    mConsumer.sign(post);
		    // send the request
			org.apache.http.HttpResponse response = mClient.execute(post);
		
			int statusCode = response.getStatusLine().getStatusCode();
		    final String reason = response.getStatusLine().getReasonPhrase();
		
		    System.out.println( "Posting message, statuscode = " + statusCode);
		    if (statusCode != 200) {
		        System.out.println("Reason is :::"+ reason);
		    
		        if(statusCode == 401)
		        {
		        	throw new AuthenticationException();
		        }
		    }
		    else
		    {	
		    	// response status should be 200 OK
			//	parseResponse(response);
				// release connection
		        response.getEntity().consumeContent();
		        System.out.println("Response is ******* "+response.toString());
		    }
		} catch(URISyntaxException e)
		{
		
		}catch (OAuthMessageSignerException e) {
		
			e.printStackTrace();
		} catch (OAuthExpectationFailedException e) {
			e.printStackTrace();
		} catch (OAuthCommunicationException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
}

	 public  static String parseResponse(org.apache.http.HttpResponse response){
	  	    
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
	    
	    	   }catch(Exception ex){
	    	        result = "Error";
	    	   }
	    	    return result;
	    	}

	 private class RestWebViewClient extends WebViewClient {
   	 @Override
   	 public boolean shouldOverrideUrlLoading(WebView view, String url) {
   		 
   		 if (url.toString().startsWith(mCallbackUrl)) {
   			getToken (Uri.parse(url));
   		 }
   		 //else
   			 //view.loadUrl(url);
   		 return true;
   	 }
   	 
      @Override
      public void onReceivedError(WebView view, int errorCode,
              String description, String failingUrl) {
          super.onReceivedError(view, errorCode, description, failingUrl);

      }
 }
}

