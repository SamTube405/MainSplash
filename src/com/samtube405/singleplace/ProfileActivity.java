package com.samtube405.singleplace;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import com.google.android.gcm.GCMRegistrar;
import com.mywork.ui.Login;
import com.mywork.ui.R;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class ProfileActivity extends Activity implements OnClickListener{
	// All xml labels
	TextView txtName;
	TextView txtContact;	
	TextView txtAddress;
	
	// Progress Dialog
	private ProgressDialog pDialog;

	// Creating JSON Parser object
	JSONParser jsonParser = new JSONParser();
	
	// Profile json object
	JSONArray profile;
	
	
	
	private static final String NOT_URL = "http://redknot.ckreativity.com/android_connect/set_notification.php";
	
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_NOT= "notification";	
	private static final String TAG_LAT = "lat";
	private static final String TAG_LONG = "longt";
	//private static final String TAG_USER_NAME = "user_name";
	private static final String TAG_USER_ID = "user_id";
	private static final String TAG_FLAG = "flag";
	
	
	JSONParser jsonParser_not= new JSONParser();
	
	String place_name;
	
	//GCM
	// label to display gcm messages
		TextView lblMessage;
		
		RatingBar rbPlace;
		
		public ToggleButton b;
		
		// Asyntask
		AsyncTask<Void, Void, Void> mRegisterTask;
		
		
		
		public static String username;
		public static String email;
		
		//palce Details
	    HashMap<String,String> place;
		
		public static String KEY_NAME = "name"; // name of the place	  	
	  	public static String KEY_LATITUDE = "lat"; // Place latitude
	  	public static String KEY_LONGITUDE = "long"; // Place longitude
	  	public static String KEY_ADDRESS = "address"; // Place area name
	  	public static String KEY_CONTACT = "contact"; // Place area name	  	
	  	public static String KEY_RATING = "rating";
	  	
	  	public static String KEY_USER = "username";
	  	public static String KEY_USER_ID = "userid";
	  	public static String KEY_NOT_FLAG = "notFlag";
	  	
	  	String flag="NO";
	  	
	  	String lat,longi;
	  	
	  	int rating=0;
	  	
	  	String userid="";
	

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.placeprofile);
		
		
        Intent i = getIntent();
        
        place=(HashMap<String,String>)i.getSerializableExtra("place");
        
        String name=place.get(KEY_NAME);
        String address=place.get(KEY_ADDRESS);
        String contact=place.get(KEY_CONTACT);
        lat=place.get(KEY_LATITUDE);
        longi=place.get(KEY_LONGITUDE);
        
        username=place.get(KEY_USER);        
        
        userid=place.get(KEY_USER_ID);        
        
        Log.d("USER ID PROFILE :",userid);
        
        rating=Integer.parseInt(place.get(KEY_RATING));
        
        flag=place.get(KEY_NOT_FLAG);
        
        
        
		
		txtName = (TextView) findViewById(R.id.tvPlace);
		txtContact = (TextView) findViewById(R.id.tvContact);		
		txtAddress = (TextView) findViewById(R.id.tvAddress);  
		
				
		txtName.setText(name);						
		txtAddress.setText(address);
		txtContact.setText(contact);
		
		rbPlace=(RatingBar)findViewById(R.id.rbPlace);
		
		rbPlace.setRating(rating);
        
  
		
		// get your ToggleButton
		b = (ToggleButton) findViewById(R.id.btnTog);
		
		Log.d("FFFFLAG :",flag);
		
		if(flag.equals("YES")) {
			Log.d("Check Me :",flag);
			b.setChecked(true);
		}
		else{
			b.setChecked(false);
			Log.d("Check Me Not :",flag);
		}
		
		

		// attach an OnClickListener
		b.setOnClickListener(new OnClickListener()
		{
		    @Override
		    public void onClick(View v)
		    {
		    	if(b.isChecked()) {
		        	flag="YES";
		        	Log.d("Checked True",flag);
		        }
		        else{
		        	flag="NO";
		        	Log.d("Checked False",flag);
		        }		        
		        new Notification().execute(flag);
		    }

			

			
		});
	}
	
	

	

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Background Async Task to Load all INBOX messages by making HTTP Request
	 * */
	class Notification extends AsyncTask<String, String, String> {
		

		private int success;


		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(ProfileActivity.this);
			pDialog.setMessage("Update Notification...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		/**
		 * getting Inbox JSON
		 * */
		protected String doInBackground(String... args) {
			
			
			
			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();			
			
			params.add(new BasicNameValuePair(TAG_LAT,lat));
			params.add(new BasicNameValuePair(TAG_LONG,longi));
			params.add(new BasicNameValuePair(TAG_USER_ID, userid));
			params.add(new BasicNameValuePair(TAG_FLAG,args[0]));
			
			Log.d("Pass Flag ",args[0]);
			// getting JSON string from URL
			JSONObject json = jsonParser_not.makeHttpRequest(NOT_URL, "POST",
					params);

			// Check your log cat for JSON reponse
			Log.d("Not JSON: ", json.toString());
			
			try {
				success=json.getInt(TAG_SUCCESS);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			

			

			return null;
		}		
		

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog after getting all products
			pDialog.dismiss();			
			
			if(success==1)
				Toast.makeText(getApplicationContext(),
						"Notification Updated", Toast.LENGTH_LONG).show();
			else{
				Toast.makeText(getApplicationContext(),
						"Notification update process failed", Toast.LENGTH_LONG).show();
			}
				

		}

	}

}
