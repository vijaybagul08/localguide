package com.android.localguide;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.Util;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.Facebook.DialogListener;

public class FaceBookClient {
	
	public interface FaceBookAuthenticationCallBack
	{
		public void onFaceBookAuthenticateCompleted(int response,String token);
		public static final int AUTHENTICAION_SUCCESSFULL = 1;
		public static final int AUTHENTICAION_FAILURE = 2;
	}

	public interface FaceBookPostMessageCallBack
	{
		public void onFaceBookmessagePostCompleted(int response);
		public static final int POST_SUCCESSFULL = 1;
		public static final int POST_FAILURE = 2;
	}
	Activity mActivity;
	FaceBookAuthenticationCallBack mCB;
	static FaceBookPostMessageCallBack mPostCB;
	String mToken;
    private Facebook mFacebook;
    private AsyncFacebookRunner mAsyncRunner;
    public static final String APP_ID = "175729095772478";
    public static final String PERMISSION_READ_STREAM		= "read_stream";
    public static final String PERMISSION_PUBLISH_STREAM	= "publish_stream";
    public static final String PERMISSION_READ_MAILBOX		= "read_mailbox";
    public static final String PERMISSION_OFFLINE_ACCESS	= "offline_access";
    private static final String GRAPH_FEED		= "me/feed";
    
    FaceBookClient(Activity context,FaceBookAuthenticationCallBack aCB) 
    {
    	mActivity = context;
    	mCB = aCB;
    
    }
    FaceBookClient(Activity context,FaceBookPostMessageCallBack aPostCB)
    {	
    	mActivity = context;
    	mPostCB = aPostCB;
    	mFacebook = new Facebook(APP_ID);
       	mAsyncRunner = new AsyncFacebookRunner(mFacebook);
     }
    
    public void setAccessToken(String token)
    {
    	mToken = token;
    }
    
    public void initialize () {
    	mFacebook = new Facebook(APP_ID);
       	mAsyncRunner = new AsyncFacebookRunner(mFacebook);
       	String[] permissions = {PERMISSION_READ_STREAM, PERMISSION_PUBLISH_STREAM, PERMISSION_READ_MAILBOX, PERMISSION_OFFLINE_ACCESS};
       	mFacebook.authorize(mActivity, permissions, new LoginDialogListener());
    }
    
    private final class LoginDialogListener implements DialogListener {
        public void onComplete(Bundle values) {
            onAuthenticationComplete(values);
        }

        public void onFacebookError(FacebookError error) {
        	mActivity.finish();
        }
        
        public void onError(DialogError error) {
        	mActivity.finish();
        }

        public void onCancel() {
        	mActivity.finish();
        }
    }
    
    public void onAuthenticationComplete (final Bundle bundle) {
    	mAsyncRunner.request("me", new MyRequestListener());
    }
    
    public class MyRequestListener implements AsyncFacebookRunner.RequestListener
    {
    	public void onComplete(final String response,Object o)
    	{
          	String accessKey = mFacebook.getAccessToken();
        	String userName = null;
        	mFacebook.setAccessExpires(0);
        	mCB.onFaceBookAuthenticateCompleted(FaceBookAuthenticationCallBack.AUTHENTICAION_SUCCESSFULL,accessKey);
        	
        	System.out.println("Access key facebook is "+accessKey);
        	PostWallMessage("Welcome to FB from my test application - Android");
            try {
                // process the response here: executed in background thread
            	JSONObject json = Util.parseJson(response);
                userName = json.getString("name");
                System.out.println("username is "+userName);
            } catch (JSONException e) {
                System.out.println( "JSON Error in response");
            } catch (FacebookError e) {
                System.out.println("Facebook Error: " + e.getMessage());
            }
  
    	}
    	
        public void onFacebookError(FacebookError e,Object o) {
            //Log.e(TAG, e.getMessage());
        	mCB.onFaceBookAuthenticateCompleted(FaceBookAuthenticationCallBack.AUTHENTICAION_FAILURE,null);
            e.printStackTrace();
        }

        public void onFileNotFoundException(FileNotFoundException e,Object o) {
        	mCB.onFaceBookAuthenticateCompleted(FaceBookAuthenticationCallBack.AUTHENTICAION_FAILURE,null);
           // Log.e(TAG, e.getMessage());
            e.printStackTrace();
        }

        public void onIOException(IOException e,Object o) {
        	mCB.onFaceBookAuthenticateCompleted(FaceBookAuthenticationCallBack.AUTHENTICAION_FAILURE,null);
           // Log.e(TAG, e.getMessage());
            e.printStackTrace();
        }

        public void onMalformedURLException(MalformedURLException e,Object o) {
        	mCB.onFaceBookAuthenticateCompleted(FaceBookAuthenticationCallBack.AUTHENTICAION_FAILURE,null);
           // Log.e(TAG, e.getMessage());
            e.printStackTrace();
        }
    }
    public void PostWallMessage(String message) {
		Bundle args = new Bundle();
    	args.putString("message", message);
    	mAsyncRunner.request(GRAPH_FEED, args, "POST", new WallMessagePostRequestListener(),null);
	}
	public static class WallMessagePostRequestListener extends BaseRequestListener {
        public void onComplete(final String response) {
        	 try {
                 // process the response here: executed in background thread
        		 Log.e("Mani", "Response: " + response.toString());
                 JSONObject json = Util.parseJson(response);
                 
                 String id = json.getString("id");
                 mPostCB.onFaceBookmessagePostCompleted(FaceBookPostMessageCallBack.POST_SUCCESSFULL);
             } catch (JSONException e) {
            	 System.out.println( "JSON Error in response");
             } catch (FacebookError e) {
                 System.out.println("Facebook Error: " + e.getMessage());
             }
        }
    }
	
	public static class BaseRequestListener implements RequestListener {
        public void onComplete(final String response,Object o) {
        	 
        }
        
        public void onFacebookError(FacebookError e,Object o) {
            
            e.printStackTrace();
        }

        public void onFileNotFoundException(FileNotFoundException e,Object o) {
            
            e.printStackTrace();
        }

        public void onIOException(IOException e,Object o) {
            
            e.printStackTrace();
        }

        public void onMalformedURLException(MalformedURLException e,Object o) {
            
            e.printStackTrace();
        }
    }
 }
