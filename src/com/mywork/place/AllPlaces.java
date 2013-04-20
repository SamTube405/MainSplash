package com.mywork.place;


import com.mywork.ui.R;
import com.samtube405.singleplace.AndroidTabAndListView;
import com.samtube405.singleplace.InboxActivity;
import com.samtube405.singleplace.JSONParser;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.Html;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.view.View;

import com.redknot.resources.*;

public class AllPlaces extends Activity {

	// Alert Dialog Manager
	AlertDialogManager alert = new AlertDialogManager();

	// Places List
	PlaceList nearPlaces;

	// Progress dialog
	ProgressDialog pDialog;

	// Places Listview
	ListView lv;
	
	//places' details List
	//PlacesDetailsList detailsList;

	// ListItems data
	ArrayList<HashMap<String, String>> placesListItems = new ArrayList<HashMap<String, String>>();
	


	// KEY Strings
	public static String KEY_REFERENCE = "reference"; // id of the place
	public static String KEY_NAME = "name"; // name of the place
	public static String KEY_VICINITY = "vicinity"; // Place area name
	public static String KEY_LATITUDE = "lat"; // Place latitude
	public static String KEY_LONGITUDE = "long"; // Place longitude
	public static String KEY_ADDRESS = "address"; // Place area name
	public static String KEY_CONTACT = "contact"; // Place area name
	public static String KEY_RATING = "rating";
	public static String KEY_NOT_FLAG = "notFlag";
	private static final String KEY_USER_ID = "userid";
	
	private static final String GET_NOT_URL="http://redknot.ckreativity.com/android_connect/place_notification.php";
	
	JSONParser jsonParser_not = new JSONParser();
	
	private static final String TAG_NOT="notification";
	private static final String TAG_FLAG = "flag";
	private static final String TAG_SUCCESS = "success";
	
	String retFlag="NO";
	
	String username="",userid="";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.places_main);
		
		Intent i=getIntent();
		
		Bundle b=i.getExtras();
		
		this.nearPlaces=(PlaceList) b.getSerializable("places");
		
		Log.d("AllPlaces","Converted places details");
		
		Log.d("Places list",nearPlaces.results.toString());
		
		username=nearPlaces.cur_user;
		
		userid=nearPlaces.cur_userid;
		
		Log.d("USER ID ALLPLACES :",userid+" "+username);
		
		lv = (ListView) findViewById(R.id.placelist);
		
		new LoadPlaces().execute();		

		lv = (ListView) findViewById(R.id.placelist);

		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				

				//Log.d("CLICKED","*******  "+position+"  ********");
				// Starting new intent
				Intent in = new Intent(getApplicationContext(),
						AndroidTabAndListView.class);
				
							
				HashMap<String,String> place=placesListItems.get(position);
				
				place.put("username", username);		
				
				
				place.put("userid",userid );
				
				
				new GetNotification(in,place).execute(place.get(KEY_LATITUDE),place.get(KEY_LONGITUDE),userid);	
				
				
				
				
				
				

			}
		});
	}
	
	

	/**
	 * Background Async Task to Load Google places
	 * */
	class LoadPlaces extends AsyncTask<String, String, String> {

		String username;
		private int success;
		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(AllPlaces.this);
			pDialog.setMessage(Html
					.fromHtml("<b>Search</b><br/>Loading Places..."));
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		/**
		 * getting Places JSON
		 * */
		protected String doInBackground(String... args) {

			// Get json response status
			String status = nearPlaces.status;
			
			username=nearPlaces.cur_user;

			// Check for all possible status
			if (status.equals("OK")) {

				if (nearPlaces.results != null) {
					// loop through each place
					for (Place p : nearPlaces.results) {
						HashMap<String, String> map = new HashMap<String, String>();

						// Place reference won't display in listview - it will
						// be hidden
						// Place reference is used to get "place full details"
						map.put(KEY_REFERENCE, p.reference);

						// Place name
						map.put(KEY_NAME, p.name);
						
						double l=p.geometry.location.lat;
						
						map.put(KEY_LATITUDE,String.valueOf(l));
						
						double lo=p.geometry.location.lng;
						
						map.put(KEY_LONGITUDE,String.valueOf(lo));
						
						map.put(KEY_ADDRESS, p.formatted_address);
						
						map.put(KEY_CONTACT, p.formatted_phone_number);
						
						map.put(KEY_RATING,String.valueOf(p.rating));					
												
						
						// adding HashMap to ArrayList
						placesListItems.add(map);
					}
				}
			} else if (status.equals("ZERO_RESULTS")) {
				// Zero results found
				alert.showAlertDialog(
						AllPlaces.this,
						"Near Places",
						"Sorry no places found. Try to change the types of places",
						false);
			} else if (status.equals("UNKNOWN_ERROR")) {
				alert.showAlertDialog(AllPlaces.this, "Places Error",
						"Sorry unknown error occured.", false);
			} else if (status.equals("OVER_QUERY_LIMIT")) {
				alert.showAlertDialog(AllPlaces.this, "Places Error",
						"Sorry query limit to google places is reached", false);
			} else if (status.equals("REQUEST_DENIED")) {
				alert.showAlertDialog(AllPlaces.this, "Places Error",
						"Sorry error occured. Request is denied", false);
			} else if (status.equals("INVALID_REQUEST")) {
				alert.showAlertDialog(AllPlaces.this, "Places Error",
						"Sorry error occured. Invalid Request", false);
			} else {
				alert.showAlertDialog(AllPlaces.this, "Places Error",
						"Sorry error occured.", false);
			}

			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog and show
		 * the data in UI Always use runOnUiThread(new Runnable()) to update UI
		 * from background thread, otherwise you will get error
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog after getting all products
			pDialog.dismiss();
			// updating UI from Background Thread
			runOnUiThread(new Runnable() {
				public void run() {
					// list adapter
					ListAdapter adapter = new SimpleAdapter(AllPlaces.this,
							placesListItems, R.layout.place_item, new String[] {
									KEY_NAME }, new int[] {
									R.id.name});

					// Adding data into listview
					lv.setAdapter(adapter);

				}

			});

		}
		
		
		}
		
		/**
		 * Background Async Task to Load Google places
		 * */
		class GetNotification extends AsyncTask<String, String, String> {
			
			private int success;
			Intent in;
			HashMap<String, String> place;
			
			
			public GetNotification(Intent in, HashMap<String, String> place) {
				this.in=in;
				this.place=place;
			}

			/**
			 * getting Places JSON
			 * */
			protected String doInBackground(String... args) {
				// Building Parameters
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				
				//params.add(new BasicNameValuePair("place_name", "Kottawa Nursing Home"));
				
				params.add(new BasicNameValuePair("lat", args[0]));
				params.add(new BasicNameValuePair("longi", args[1]));
				params.add(new BasicNameValuePair("userid", args[2]));			
				
				
				// getting JSON string from URL
				JSONObject json = jsonParser_not.makeHttpRequest(GET_NOT_URL, "GET",
						params);

				// Check your log cat for JSON reponse
				Log.d("Notification JSON: ", json.toString());

				try {				
					success=json.getInt(TAG_SUCCESS);
					
					if(success==1) {
						retFlag=json.getJSONArray(TAG_NOT).getJSONObject(0).getString(TAG_FLAG);
						Log.d("Not Flag Just AAfter Query : ",retFlag);
					}
					else{
						retFlag="NO";
					}
					
					Log.d("Not Flag For All : ",retFlag);
						

				} catch (JSONException e) {
					e.printStackTrace();
				}
				return null;
				
			}
			
			protected void onPostExecute(String file_url) {
				place.put("notFlag",retFlag);
				
				Log.d("CLICKED","*******  "+place.toString()+"  ********");
				
				in.putExtra("place",place);
				
				startActivity(in);
			}
			
			

			
			
			
	}
	
	
	
	

}
