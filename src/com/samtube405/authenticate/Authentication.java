package com.samtube405.authenticate;

import java.io.IOException;
import java.net.URL;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.samtube405.authenticate.TwitterUtilities;

public class Authentication {	
	
	public static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
	
	public static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";

	private static SharedPreferences mSharedPreferences;
	
	AccessToken accessToken;
	
	Twitter twitter;
	
	TwitterUtilities tu;
	
	ConfigurationBuilder builder;
	
	Context c;
	
	public String status;
	
	public Authentication(Context context){
		
		this.c=context;
		
		tu=new TwitterUtilities();
		
		// Shared Preferences
		mSharedPreferences = c.getSharedPreferences("MyPref", 0);
		
		builder = new ConfigurationBuilder();
		
		builder.setOAuthConsumerKey(tu.getTwitterConsumerKey());
		
		builder.setOAuthConsumerSecret(tu.getTwitterConsumerSecret());
		
		// Access Token 
		String access_token = mSharedPreferences.getString(PREF_KEY_OAUTH_TOKEN, "");
		// Access Token Secret
		String access_token_secret = mSharedPreferences.getString(PREF_KEY_OAUTH_SECRET, "");
		
		accessToken = new AccessToken(access_token, access_token_secret);
		
		twitter = new TwitterFactory(builder.build()).getInstance(accessToken);
	}
	
	
	
	public String getUserName(){
		try {
			return twitter.showUser(accessToken.getUserId()).getName();
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	
	public long getUserID(){
		try {
			return twitter.showUser(accessToken.getUserId()).getId();
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	
	public Bitmap getUserPic(){
		Bitmap bmp=null;
		URL url;
		
		try {
			url=twitter.showUser(accessToken.getUserId()).getProfileImageURL();
			
			bmp=BitmapFactory.decodeStream(url.openConnection() .getInputStream());
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return bmp;
	}
	
	
	/**
	 * Function to update status
	 * */
	public class updateTwitterStatus extends AsyncTask<String, String, String> {		
	 
	    /**
	     * getting Places JSON
	     * */
	    protected String doInBackground(String... args) {
	       
	    	try{
	            // Update status
	            twitter4j.Status response = twitter.updateStatus(args[0]);
	 
	            Log.d("Status", "> " + response.getText());
	        } catch (TwitterException e) {
	            // Error in updating status
	            Log.d("Twitter Update Error", e.getMessage());
	        }
	        return null;
	    }
	 
	    
	 
	}
	
	
	public void updateTweet(String status){
		new updateTwitterStatus().execute(status);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
