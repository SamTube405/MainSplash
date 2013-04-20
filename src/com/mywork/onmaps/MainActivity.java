package com.mywork.onmaps;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends Activity {
	/** Called when the activity is first created. */
	private ProgressDialog pDialog;

	// Creating JSON Parser object
	JSONParser jParser = new JSONParser();

	ArrayList<HashMap<String, String>> toursList;

	// url to get all products list
	private static String url_all_tours = "http://redknot.ckreativity.com/android_connect/get_all_tours.php";

	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_TOURS = "tours";
	private static final String TAG_ID = "id";
	private static final String TAG_LATITUDE = "latitude";
	private static final String TAG_LONGITUDE = "longitude";

	// products JSONArray
	JSONArray tours = null;
	
	TextView tv;
	String s="Results are : ";

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.dbload);
		
		//Extra methods Twitter
				if (Build.VERSION.SDK_INT > 9) {
					StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
					StrictMode.setThreadPolicy(policy);
					}
		

		// Hashmap for ListView
		toursList = new ArrayList<HashMap<String, String>>();

		// Loading products in Background Thread
		new LoadAllTours().execute();
		
		
	}

	/**
	 * Background Async Task to Load all product by making HTTP Request
	 * */
	class LoadAllTours extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(MainActivity.this);
			pDialog.setMessage("Loading tours. Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		/**
		 * getting All products from url
		 * */
		protected String doInBackground(String... args) {

			runOnUiThread(new Runnable() {
				public void run() {

					List<NameValuePair> params = new ArrayList<NameValuePair>();

					params.add(new BasicNameValuePair("id", "1"));
					// getting JSON string from URL
					JSONObject json = jParser.makeHttpRequest(url_all_tours,
							"GET", params);

					// Check your log cat for JSON response
					Log.d("All Tours: ", json.toString());

					try {
						// Checking for SUCCESS TAG
						int success = json.getInt(TAG_SUCCESS);

						if (success == 1) {
							// products found
							// Getting Array of Products
							tours = json.getJSONArray(TAG_TOURS);

							// looping through All Products
							for (int i = 0; i < tours.length(); i++) {
								JSONObject c = tours.getJSONObject(i);

								// Storing each json item in variable
								String id = c.getString(TAG_ID);
								String latitude = c.getString(TAG_LATITUDE);
								String longitude = c.getString(TAG_LONGITUDE);

								// creating new HashMap
								HashMap<String, String> map = new HashMap<String, String>();

								// adding each child node to HashMap key =>
								// value
								map.put(TAG_ID, id);
								map.put(TAG_LATITUDE, latitude);
								map.put(TAG_LONGITUDE, longitude);

								// adding HashList to ArrayList
								toursList.add(map);
								//s.concat(map.toString()+"\n");
								//s=s+map.toString()+"\n";
							}
							//tv.setText(s);
							Intent intent = new Intent (MainActivity.this,MyTours.class);
							intent.putExtra("tour_list",toursList);
							startActivity(intent);
						} else {
							System.out.print("no records");
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			});
			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog after getting all products
			pDialog.dismiss();
			// updating UI from Background Thread
			/*runOnUiThread(new Runnable() {
				public void run() {
					for (int i = 0; i < toursList.size(); i++) {
						TextView tv = new TextView(MainActivity.this);
						tv.setText(toursList.get(i).get(TAG_LATITUDE));
					}

				}
			});*/

		}

	}

}