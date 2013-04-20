package com.samtube405.singleplace;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mywork.ui.R;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class InboxActivity extends ListActivity implements OnClickListener{
	// Progress Dialog
	private ProgressDialog pDialog;

	// Creating JSON Parser object
	JSONParser jsonParser_inbox = new JSONParser();
	
	JSONParser jsonParser_review = new JSONParser();

	//ArrayList<HashMap<String, String>> inboxList;

	// products JSONArray
	JSONArray inbox = null;

	// Inbox JSON url
	private static final String INBOX_URL = "http://redknot.ckreativity.com/android_connect/get_all_reviews.php";
	
	private static final String ADD_REVIEW_URL = "http://redknot.ckreativity.com/android_connect/add_review.php";
	
	// ALL JSON node names
	/*private static final String TAG_MESSAGES = "messages";
	private static final String TAG_ID = "id";
	private static final String TAG_FROM = "from";
	private static final String TAG_EMAIL = "email";
	private static final String TAG_SUBJECT = "subject";
	private static final String TAG_DATE = "date";*/
	
	// JSON Node names
		private static final String TAG_SUCCESS = "success";
		private static final String TAG_REVIEW = "review";
		private static final String TAG_PLACE = "place_name";
		private static final String TAG_USER_NAME = "user_name";
		private static final String TAG_DATE = "date";
		private static final String TAG_MESSAGE = "message";
		
		//palce Details
	    HashMap<String,String> place;
		
		public static String KEY_NAME = "name"; // name of the place	  	
	  	public static String KEY_LATITUDE = "lat"; // Place latitude
	  	public static String KEY_LONGITUDE = "long"; // Place longitude
	  	public static String KEY_ADDRESS = "address"; // Place area name
	  	public static String KEY_CONTACT = "contact"; // Place area name
	  	public static String KEY_USER = "username";
	  	public static String KEY_USER_ID = "userid";
	
		
		EditText etmsg;
		
		String user_review="DEFAULT";
		
		String username="",userid="";	
		
		String latitude="";
        String longitude="";
        
		
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.inbox_list);
		
		//Intent i = getIntent();        
		 
		Intent i = getIntent();
        
        place=(HashMap<String,String>)i.getSerializableExtra("place");
        
        String name=place.get(KEY_NAME);
        String address=place.get(KEY_ADDRESS);
        String phone=place.get(KEY_CONTACT);
        latitude=place.get(KEY_LATITUDE);
        longitude=place.get(KEY_LONGITUDE);
        
        username=place.get(KEY_USER);
        
        userid=place.get(KEY_USER_ID);
		
		// Getting listview from xml
		ListView lv = (ListView) findViewById(R.id.list);		 		
		
		
        
        etmsg=(EditText)findViewById(R.id.usermsg);
        Button bsubmit=(Button)findViewById(R.id.brsubmit);
        
        bsubmit.setOnClickListener(this);
 
        // Loading INBOX in Background Thread
        new LoadInbox(latitude,longitude).execute();
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.brsubmit:
			user_review=etmsg.getText().toString();
			Toast.makeText(getApplicationContext(), user_review, Toast.LENGTH_SHORT).show();
			if(!user_review.equalsIgnoreCase("DEFAULT")){				
				// Adding review from the Background Thread
		        new AddReview(latitude,longitude,user_review).execute();
			}
			else{
				Toast.makeText(getApplicationContext(), "Give your Review..Try Again!", Toast.LENGTH_SHORT).show();
			}
			
			
			
			
			
			
			break;
		
		}
		
	}
	/**
	 * Background Async Task to Load all INBOX messages by making HTTP Request
	 * */
	class LoadInbox extends AsyncTask<String, String, String> {
		// Hashmap for ListView
		ArrayList<HashMap<String, String>> inboxList = new ArrayList<HashMap<String, String>>();
					
		String lat="";
		String longi="";
		
		public LoadInbox(String lat, String longi) {
			this.lat=lat;
			this.longi=longi;
		}

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			pDialog = new ProgressDialog(InboxActivity.this);
			pDialog.setMessage("Loading Reviews...");
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
			
			//params.add(new BasicNameValuePair("place_name", "Kottawa Nursing Home"));
			
			params.add(new BasicNameValuePair("lat", lat));
			params.add(new BasicNameValuePair("longi", longi));

			
			// getting JSON string from URL
			JSONObject json = jsonParser_inbox.makeHttpRequest(INBOX_URL, "GET",
					params);

			// Check your log cat for JSON reponse
			Log.d("Inbox JSON: ", json.toString());

			try {
				inbox = json.getJSONArray(TAG_REVIEW);
				// looping through All messages
				for (int i = 0; i < inbox.length(); i++) {
					JSONObject c = inbox.getJSONObject(i);

					// Storing each json item in variable
					String user_name = c.getString(TAG_USER_NAME);
					//long date = c.getLong(TAG_DATE);
					String date = c.getString(TAG_DATE);
					//c.get
					String message = c.getString(TAG_MESSAGE);
					//String date = c.getString(TAG_ID);

					// creating new HashMap
					HashMap<String, String> map = new HashMap<String, String>();

					// adding each child node to HashMap key => value
					map.put(TAG_MESSAGE, message);
					map.put(TAG_USER_NAME, user_name);
					map.put(TAG_DATE, date);					
					//map.put(TAG_ID, date);

					// adding HashList to ArrayList
					inboxList.add(map);
				}

			} catch (JSONException e) {
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
			// updating UI from Background Thread
			runOnUiThread(new Runnable() {
				public void run() {
					/**
					 * Updating parsed JSON data into ListView
					 * */
					setListAdapter(null);
					
					ListAdapter adapter = new SimpleAdapter(
							InboxActivity.this, inboxList,
							R.layout.inbox_list_item, new String[] { TAG_MESSAGE, TAG_USER_NAME, TAG_DATE },
							new int[] { R.id.tvmsg, R.id.tvuser, R.id.tvdate });
					// updating listview
					setListAdapter(adapter);
					
					
					
					Log.d("POST EXECUTE ",TAG_MESSAGE);
					
				}
			});
			//finish();
		}

	}


	/**
	 * Background Async Task to Load all INBOX messages by making HTTP Request
	 * */
	class AddReview extends AsyncTask<String, String, String> {
		int success=0;
		String lat="";
		String longi="";
		String user_review="";
		
		
		public AddReview(String lat, String longi, String user_review) {
			this.lat=lat;
			this.longi=longi;
			this.user_review=user_review;
		}

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(InboxActivity.this);
			pDialog.setMessage("Submitting...");
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
			
			//params.add(new BasicNameValuePair("place_name", "Kottawa Nursing Home"));
			params.add(new BasicNameValuePair("lat", lat.toString()));
			params.add(new BasicNameValuePair("longi", longi.toString()));
			params.add(new BasicNameValuePair("user_id", userid));
			//params.add(new BasicNameValuePair("date", getCurrentDate()));
			params.add(new BasicNameValuePair("message", user_review));

			
			// getting JSON string from URL
			JSONObject json2 = jsonParser_review.makeHttpRequest(ADD_REVIEW_URL, "POST",
					params);

			// Check your log cat for JSON reponse
			Log.d("Review JSON: ", json2.toString());
			
			try {
				success=json2.getInt(TAG_SUCCESS);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			

			

			return null;
		}
		
		public String getCurrentDate(){
			Calendar c = Calendar.getInstance();
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	        String formattedDate = df.format(c.getTime());
	        return formattedDate;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog after getting all products
			pDialog.dismiss();
			if(success==1){
				Toast.makeText(getApplicationContext(), "Your Review Added!!", Toast.LENGTH_SHORT).show();
				// Loading INBOX in Background Thread
		        new LoadInbox(lat,longi).execute();
			}
			else{
				Toast.makeText(getApplicationContext(), "Review Adding Failed..Try Again!", Toast.LENGTH_SHORT).show();
			}
			etmsg.setText("");

		}

	}

}
