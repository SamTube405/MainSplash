package com.mywork.db;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import twitter4j.api.NewTwitterMethods;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.mywork.db.PutToPlaces.LoadSinglePlaceDetails;
import com.mywork.place.GooglePlaces;
import com.mywork.place.Place;
import com.mywork.place.PlaceDetails;
import com.mywork.place.PlaceList;
import com.mywork.preference.SessionManager;

public class AddTourPlaces {

	JSONParser jsonParser = new JSONParser();
	private static final String url = "http://redknot.ckreativity.com/android_connect/add_to_tour.php";// change

	private static final String TAG_SUCCESS = "success";
	PlaceList nearPlaces;
	ProgressDialog pDialog;
	GooglePlaces googleplaces;
	PlaceDetails placeDetails;
	String name, address, contact, type, user_id, date, trip_id,single;
	double place_lat, place_longi, center_lat, center_longi, radius;
	// GeoPoint center;
	Context c;
	boolean db_upload_success = true;
	boolean is_task_finished = false;
	boolean should_ignore = false;

	PlaceList detailsList = new PlaceList();

	SessionManager sm;

	public AddTourPlaces(PlaceList p, Context con, String trip, String user,
			GeoPoint c, double r, boolean ignore) {

		// sm=new SessionManager(con);
		this.nearPlaces = p;
		this.c = con;
		this.center_lat = c.getLatitudeE6() / 1E6;
		this.center_longi = c.getLongitudeE6() / 1E6;
		this.radius = r;
		this.user_id = user;
		this.trip_id = trip;
		this.should_ignore = ignore;
	}

	public void init() {

		Date d = new Date();
		date = new SimpleDateFormat("yyyy-MM-dd").format(d);

		detailsList.results = new ArrayList<Place>();

		if (!should_ignore) {
			Vector<String> refs = new Vector<String>();
			if (nearPlaces.results != null) {
				for (Place p : nearPlaces.results) {
					refs.add(p.reference);
				}
				LoadSinglePlaceDetails l = new LoadSinglePlaceDetails();

				l.execute(refs.toArray(new String[0]));

			}

		} else {
			if (nearPlaces.results != null) {
				new UploadPlaceDetails().execute();
			}

		}
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
								String gotTypes[] = placeDetails.result.types;
								// Log.d("PLACE TYPES: ",gotTypes.toString());
								String t = "";
								for (int it = 0; it < gotTypes.length; it++) {

									if (it == gotTypes.length - 1) {

										t += gotTypes[it];

									} else {
										t += gotTypes[it] + ",";
									}
								}
								Log.d("PLACE TYPES: ", t);

								// type = placeDetails.result.types[0];
								type = t;
								name = placeDetails.result.name;
								address = placeDetails.result.formatted_address;
								contact = placeDetails.result.formatted_phone_number;
								place_lat = placeDetails.result.geometry.location.lat;
								place_longi = placeDetails.result.geometry.location.lng;

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
										.toString(place_lat)));

								params.add(new BasicNameValuePair("long",
										Double.toString(place_longi)));

								params.add(new BasicNameValuePair("clat",
										Double.toString(center_lat)));

								params.add(new BasicNameValuePair("clong",
										Double.toString(center_longi)));

								params.add(new BasicNameValuePair("radius",
										Double.toString(radius)));

								params.add(new BasicNameValuePair("type", type));

								params.add(new BasicNameValuePair("user_id",
										user_id));

								params.add(new BasicNameValuePair("date", date));

								params.add(new BasicNameValuePair("name", name));
								params.add(new BasicNameValuePair("address",
										address));
								params.add(new BasicNameValuePair("contact",
										contact));
								params.add(new BasicNameValuePair("trip_id",
										trip_id));
								params.add(new BasicNameValuePair("single", String.valueOf(should_ignore)));

								// getting JSON Object
								// Note that create product url accepts POST
								// method
								JSONObject json = jsonParser.makeHttpRequest(
										url, "POST", params);

								// check log cat fro response
								Log.d("Create Response", json.toString());

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

	class UploadPlaceDetails extends AsyncTask<String, String, String> {

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

		@Override
		protected String doInBackground(String... param) {
			// TODO Auto-generated method stub
			placeDetails=new PlaceDetails();
			for (Place p : nearPlaces.results) {

				if (p != null) {
					
					Log.d("Place Details: ",p.toString());
					placeDetails.result = p;
					placeDetails.status = "OK";
					detailsList.results.add(placeDetails.result);

					String gotTypes[] = p.types;
					// Log.d("PLACE TYPES: ",gotTypes.toString());
					String t = "";
					for (int it = 0; it < gotTypes.length; it++) {

						if (it == gotTypes.length - 1) {

							t += gotTypes[it];

						} else {
							t += gotTypes[it] + ",";
						}
					}

					type = t;
					name = p.name;
					address = p.formatted_address;
					contact = p.formatted_phone_number;
					place_lat = p.geometry.location.lat;
					place_longi = p.geometry.location.lng;

					List<NameValuePair> params = new ArrayList<NameValuePair>();

					params.add(new BasicNameValuePair("lat", Double
							.toString(place_lat)));

					params.add(new BasicNameValuePair("long", Double
							.toString(place_longi)));

					params.add(new BasicNameValuePair("clat", Double
							.toString(center_lat)));

					params.add(new BasicNameValuePair("clong", Double
							.toString(center_longi)));

					params.add(new BasicNameValuePair("radius", Double
							.toString(radius)));

					params.add(new BasicNameValuePair("type", type));

					params.add(new BasicNameValuePair("user_id", user_id));

					params.add(new BasicNameValuePair("date", date));

					params.add(new BasicNameValuePair("name", name));
					params.add(new BasicNameValuePair("address", address));
					params.add(new BasicNameValuePair("contact", contact));
					params.add(new BasicNameValuePair("trip_id", trip_id));
					params.add(new BasicNameValuePair("single", String.valueOf(should_ignore)));

					JSONObject json = jsonParser.makeHttpRequest(url, "POST",
							params);
					try {
						if (json.getInt(TAG_SUCCESS) != 1) {
							db_upload_success = false;
						}
					} catch (Exception e) {
						e.printStackTrace();

					}

				}

			}
			
			if (detailsList.results.size() > 0) {
				detailsList.status = "OK";
			} else {
				detailsList.status = "ZERO_RESULTS";

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
