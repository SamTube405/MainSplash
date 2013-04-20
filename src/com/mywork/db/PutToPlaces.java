package com.mywork.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import twitter4j.api.NewTwitterMethods;

import com.redknot.resources.*;

import com.mywork.place.AllPlaces;
import com.mywork.place.GooglePlaces;
import com.mywork.place.Place;
import com.mywork.place.PlaceDetails;
import com.mywork.place.PlaceList;
import com.mywork.place.PlacesDetailsList;
import com.mywork.place.PlacesMapActivity;
import com.mywork.ui.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;

public class PutToPlaces {

	JSONParser jsonParser = new JSONParser();
	private static final String url = "http://redknot.ckreativity.com/android_connect/update_place.php";

	private static final String TAG_SUCCESS = "success";
	PlaceList nearPlaces;
	ProgressDialog pDialog;
	GooglePlaces googleplaces;
	PlaceDetails placeDetails;
	String name, address, contact, type;
	double lat, longi;
	Context c;
	boolean db_upload_success = true;
	boolean is_task_finished = false;

	PlaceList detailsList = new PlaceList();

	public PutToPlaces(PlaceList p, Context con) {

		nearPlaces = p;
		this.c = con;
	}

	public void init() {
		detailsList.results = new ArrayList<Place>();
		Vector<String> refs = new Vector<String>();
		if (nearPlaces.results != null) {
			for (Place p : nearPlaces.results) {
				refs.add(p.reference);
			}
			LoadSinglePlaceDetails l = new LoadSinglePlaceDetails();

			l.execute(refs.toArray(new String[0]));

		}
		// return db_upload_success;
	}

	public boolean isSuccess() {

		while (!is_task_finished) {

		}

		return db_upload_success;
	}

	public PlaceList getDetailsList() {
		return this.detailsList;
	}

	class LoadSinglePlaceDetails extends AsyncTask<String[], String, String> {

		/**
		 * getting Profile JSON
		 * */

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(c);
			pDialog.setMessage(Html
					.fromHtml("<b>Search</b><br/>Uploading Places To Database..."));
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		protected String doInBackground(String[]... args) {
			String[] reference = args[0];

			// creating Places class object
			googleplaces = new GooglePlaces();

			// Check if used is connected to Internet
			try {

				for (int i = 0; i < reference.length; i++) {

					placeDetails = googleplaces.getPlaceDetails(reference[i]);

					if (placeDetails != null) {
						String status = placeDetails.status;

						// check place deatils status
						// Check for all possible status
						if (status.equals("OK")) {
							if (placeDetails.result != null) {

								// PlaceDetails temp=new PlaceDetails();
								// Place temp=new Place();

								// Log.d("Place Details: ",placeDetails.result.toString());

								type = placeDetails.result.types[0];
								name = placeDetails.result.name;
								address = placeDetails.result.formatted_address;
								contact = placeDetails.result.formatted_phone_number;
								lat = placeDetails.result.geometry.location.lat;
								longi = placeDetails.result.geometry.location.lng;

								name = name == null ? "Not present" : name; // if
																			// name
																			// is
																			// null
																			// display
																			// as
																			// "Not present"
								address = address == null ? "Not present"
										: address;
								placeDetails.result.formatted_address = address;
								contact = contact == null ? "Not present"
										: contact;
								placeDetails.result.formatted_phone_number = contact;

								detailsList.results.add(placeDetails.result);

								List<NameValuePair> params = new ArrayList<NameValuePair>();
								params.add(new BasicNameValuePair("lat", Double
										.toString(lat)));
								params.add(new BasicNameValuePair("long",
										Double.toString(longi)));
								params.add(new BasicNameValuePair("name", name));
								params.add(new BasicNameValuePair("address",
										address));
								params.add(new BasicNameValuePair("contact",
										contact));
								params.add(new BasicNameValuePair("type", type));

								// getting JSON Object
								// Note that create product url accepts POST
								// method
								JSONObject json = jsonParser.makeHttpRequest(
										url, "POST", params);

								// check log cat fro response
								// Log.d("Create Response", json.toString());

								if (json.getInt(TAG_SUCCESS) != 1) {
									db_upload_success = false;
								}
							}
						}
					}
				}

				if (detailsList.results.size() > 0) {
					detailsList.status = "OK";
				} else {
					detailsList.status = "ZERO_RESULTS";

				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String file_url) {
			// dismiss the dialog after getting all products
			pDialog.dismiss();

		}

	}

}
