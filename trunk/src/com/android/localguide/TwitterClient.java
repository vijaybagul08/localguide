package com.android.localguide;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;

import org.apache.http.auth.AuthenticationException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;

import android.accounts.Account;
import android.content.Context;
import android.net.ParseException;
import android.util.Log;
import android.webkit.WebView;

public class TwitterClient {
	

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
    public static final String UPDATE_STATUS_URI = "http://api.twitter.com/version/statuses/update.json";
    
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
    
    
        
    TwitterClient(Context context,String accessKey,String accessSecret)
    {
    mContext = context;
    mConsumerKey = CONSUMER_KEY;
    mConsumerSecret = CONSUMER_SECRET;
    mAccessKey = accessKey;
    mAccessSecret = accessSecret;
    
    mConsumer = new CommonsHttpOAuthConsumer(mConsumerKey, mConsumerSecret);
    mConsumer.setTokenWithSecret(accessKey,accessSecret);
    mClient = new DefaultHttpClient();  
    }
    
	public void postTweet(String tweetMessage) throws JSONException,
            ParseException, IOException, AuthenticationException {
    	try {
    		
    		List<BasicNameValuePair> qparams = new ArrayList<BasicNameValuePair>();
	    	qparams.add(new BasicNameValuePair("status","@vinothg123 Visint next week bangkok"));
    		qparams.add(new BasicNameValuePair("in_reply_to_status_id", "9100592710418432"));

	        URI uri = URIUtils.createURI(null,UPDATE_STATUS_URI, -1, null, URLEncodedUtils.format(qparams, "UTF-8"), null);
	        
    		// create a request that requires authentication
	        HttpPost post = new HttpPost(UPDATE_STATUS_URI);
	        post.setEntity(new UrlEncodedFormEntity(qparams, HTTP.UTF_8)); 
	        post.getParams().setBooleanParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, false);
	        mConsumer = new CommonsHttpOAuthConsumer(mConsumerKey, mConsumerSecret);
	        //52681808-hq1bD2QMMxmASKRqNUp4B6LW1rO4U7uvDVFh2DAy6
	        	//myywAFkhViAphog5wkzaYdiTWDdPdrCEzOjkZ4fVAzo
//	        mConsumer.setTokenWithSecret(mAccessKey, mAccessSecret); // Working properly key, secret is the order.
	        mConsumer.setTokenWithSecret("52681808-hq1bD2QMMxmASKRqNUp4B6LW1rO4U7uvDVFh2DAy6", "myywAFkhViAphog5wkzaYdiTWDdPdrCEzOjkZ4fVAzo");
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
	
    
 }
