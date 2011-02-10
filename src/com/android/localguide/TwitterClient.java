package com.android.localguide;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
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
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.json.JSONException;
import org.json.JSONObject;

import android.accounts.Account;
import android.app.Activity;
import android.content.Context;
import android.net.ParseException;
import android.net.Uri;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class TwitterClient {
	
	public interface TwitterAuthenticationCallBack
	{
		public void onTwitterAuthenticateCompleted(int response,String accessKey,String accessSecret,String username);
		public static final int AUTHENTICAION_SUCCESSFULL = 1;
		public static final int AUTHENTICAION_FAILURE = 2;
	}

	public interface TwitterPostMessageCallBack
	{
		public void onFaceBookmessagePostCompleted(int response);
		public static final int POST_SUCCESSFULL = 1;
		public static final int POST_FAILURE = 2;
	}
	
//    private static final String CONSUMER_KEY = "GveIPloE2pt1lqOsdVHbcw";
//    private static final String CONSUMER_SECRET = "o8eRpfPqFzFxaW84PkFC2bGcDibZYKvYiVdJeek";
    private static final String CONSUMER_KEY = "9rGstuoFXWINNjk10wVzQ";
    private static final String CONSUMER_SECRET = "NhoXr4cHOgMj7VY36FMJQiqKme2EzRAmCPcBZU18";
    private static final String CALLBACK_URL = "TwitterAuth://twitt";
    
    private static final String REQUEST_URL = "http://twitter.com/oauth/request_token";
    private static final String ACCESS_URL = "http://twitter.com/oauth/access_token";
    private static final String AUTHORIZE_URL = "http://twitter.com/oauth/authorize";
    public static final String FETCH_CREDENTIALS_URI = "http://api.twitter.com/1/account/verify_credentials.json";
	
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
    TwitterAuthenticationCallBack mCB;
    
    TwitterClient(Context context,TwitterAuthenticationCallBack aCB)
    {
    mContext = context;
    mConsumerKey = CONSUMER_KEY;
    mConsumerSecret = CONSUMER_SECRET;
    mCallbackUrl = CALLBACK_URL;
    mRequestTokenUrl = REQUEST_URL;
    mAccessTokenUrl = ACCESS_URL;
    mAuthorizeUrl = AUTHORIZE_URL;
    mCB = aCB;
    }
    
    TwitterClient(Context context,String accessKey,String accessSecret)
    {
    mContext = context;
    mConsumerKey = CONSUMER_KEY;
    mConsumerSecret = CONSUMER_SECRET;
    mConsumer = new CommonsHttpOAuthConsumer(mConsumerKey, mConsumerSecret);
    mConsumer.setTokenWithSecret(accessKey,accessSecret);
    mClient = new DefaultHttpClient();  
    }
    
	public void initialize () {
    	mConsumer = new CommonsHttpOAuthConsumer(mConsumerKey, mConsumerSecret);
    	mProvider = new CommonsHttpOAuthProvider(mRequestTokenUrl, mAccessTokenUrl,mAuthorizeUrl);
    	mClient = new DefaultHttpClient();  
    	mWebView = new WebView(mContext);
    	((Activity)mContext).setContentView (mWebView);
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
			
			fetchUserCredentials();
		} catch (OAuthMessageSignerException e) {
			e.printStackTrace();
		} catch (OAuthNotAuthorizedException e) {
			e.printStackTrace();
		} catch (OAuthExpectationFailedException e) {
			e.printStackTrace();
		} catch (OAuthCommunicationException e) {
			e.printStackTrace();
		} catch(Exception e)
		{
			
		}
    }
	
    public void fetchUserCredentials() throws JSONException,
            ParseException, IOException, AuthenticationException {
    	try {
    		
    		// create a request that requires authentication
    		HttpGet post = new HttpGet(FETCH_CREDENTIALS_URI);
	        final List<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
	        nvps.add(new BasicNameValuePair("include_entities", "true"));
	     //   mConsumer.setTokenWithSecret(mAccessKey, mAccessSecret); // Working properly key, secret is the order.
	        
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
		         mCB.onTwitterAuthenticateCompleted(TwitterAuthenticationCallBack.AUTHENTICAION_SUCCESSFULL, mAccessKey, mAccessSecret, json.get("name").toString());
		         System.out.println("Name is "+json.get("name").toString());
		         }
		         catch(Exception e)
		         {
		         	e.printStackTrace();
		         }
	        }
	    } catch (OAuthMessageSignerException e) {
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
