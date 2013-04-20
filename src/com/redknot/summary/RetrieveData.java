package com.redknot.summary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.redknot.summary.JSONParser;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.widget.TextView;

public class RetrieveData extends Activity {

	private ProgressDialog pDialog;

	// Creating JSON Parser object
	JSONParser jParser = new JSONParser();

	ArrayList<HashMap<String, String>> summary = new ArrayList<HashMap<String, String>>();
	ArrayList<HashMap<String, String>> singlecategory = new ArrayList<HashMap<String, String>>();

	// url to get all products list
	private static String url_summary = "http://redknot.ckreativity.com/android_connect/total_summary.php";

	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_SUMMARY = "summary";
	private static final String TAG_NAME = "name";
	private static final String TAG_PERCENTAGE = "catpercent";
	private static final String TAG_TOTAL_COUNT = "totalcount";
	private static final String TAG_TYPEDETAILS = "typedetails";
	private static final String TAG_TYPE_TYPE = "type";
	private static final String TAG_TYPE_COUNT = "count";
	private static final String TAG_TYPE_PERCENTAGE = "percentage";
	private static final String TAG_CATEGORY = "category";

	// products JSONArray
	JSONArray preferences = null;
	JSONArray typedetails = null;
	JSONObject json;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.data_load);

		// Hashmap for ListView
		
		

		// Loading products in Background Thread
		new LoadSummary().execute();

	}

	/**
	 * Background Async Task to Load all product by making HTTP Request
	 * */
	class LoadSummary extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(RetrieveData.this);
			pDialog.setMessage("Loading Data. Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		/**
		 * getting All products from url
		 * */
		protected String doInBackground(String... args) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();

			json = jParser.makeHttpRequest(url_summary, "GET", params);

			// Check your log cat for JSON response
			

			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog after getting all products
			pDialog.dismiss();

			runOnUiThread(new Runnable() {
				public void run() {

					try {

						int success = json.getInt(TAG_SUCCESS);

						if (success == 1) {

							preferences = json.getJSONArray(TAG_SUMMARY);

							for (int i = 0; i < preferences.length(); i++) {
								JSONObject c = preferences.getJSONObject(i);

								if (c.getInt(TAG_TOTAL_COUNT) > 0) {

									// Storing each json item in variable
									String prefName = c.getString(TAG_NAME);
									String prefPercentage = c
											.getString(TAG_PERCENTAGE);
									typedetails = c
											.getJSONArray(TAG_TYPEDETAILS);
									int count=c.getInt(TAG_TOTAL_COUNT);

									// creating new HashMap
									HashMap<String, String> map = new HashMap<String, String>();

									// adding each child node to HashMap key =>
									// value
									map.put(TAG_NAME, prefName);
									map.put(TAG_PERCENTAGE, prefPercentage);
									map.put(TAG_TOTAL_COUNT,String.valueOf(count));

									for (int j = 0; j < typedetails.length(); j++) {
										JSONObject t = typedetails.getJSONObject(j);

										HashMap<String, String> typed = new HashMap<String, String>();

										typed.put(TAG_CATEGORY, prefName);
										typed.put(TAG_TYPE_TYPE,t.getString(TAG_TYPE_TYPE).toUpperCase());
										typed.put(TAG_TYPE_COUNT,String.valueOf(t.getInt(TAG_TYPE_COUNT)));
										typed.put(TAG_TYPE_PERCENTAGE,String.valueOf(t.getDouble(TAG_TYPE_PERCENTAGE)));
										
										singlecategory.add(typed);

									}

									// adding HashList to ArrayList
									summary.add(map);

								}

							}
							
							Log.d("Single Category Details: ",singlecategory.toString());
							Log.d("Summary: ", summary.toString());
							// send the array list to the main activity
							Intent intent = new Intent(RetrieveData.this,
									SummaryActivity.class);
							
							Bundle b=new Bundle();
							b.putSerializable("summary", summary);
							b.putSerializable("singlecategory",singlecategory);
							
							//intent.putExtra("summary", summary);
							//intent.putExtra("singlecategory",singlecategory);
							intent.putExtras(b);
							//intent.putParcelableArrayListExtra("singlecategory", (ArrayList<? extends Parcelable>) singlecategory);
							
							startActivity(intent);
						} else {
							System.out.print("no records");
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			});

		}

	}

}
