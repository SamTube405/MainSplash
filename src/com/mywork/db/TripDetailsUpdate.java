package com.mywork.db;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import com.redknot.resources.*;

import com.mywork.place.PlacesMapActivity;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class TripDetailsUpdate {

	JSONParser jsonParser = new JSONParser();
	private static final String url = "http://redknot.ckreativity.com/android_connect/trip_details.php";
	
	private static final String TAG_SUCCESS = "success";
	
	private static final String TAG_ID = "user";
	private static final String TAG_TRIP_NAME = "trip";
	private static final String TAG_DATE = "date";
	private static final String TAG_TRIP_ID = "trip_id";
	
	String id,name,date,trip_id="NULL";
	
	Activity c;
	
	AlertDialogManager alert=new AlertDialogManager();
	
	String message;
	
	JSONObject json;

	
	public TripDetailsUpdate(String user_id, String tname,Activity con){
		
		this.id=user_id;
		this.name=tname;
		this.c=con;
		
		Date d=new Date();
		date=new SimpleDateFormat("yyyy-MM-dd").format(d);
		
		new UpdateTrip().execute();
	}
	
	public String getTripId(){
		
		return this.trip_id;
	}
	
	class UpdateTrip extends AsyncTask<String, String, String>{

		@Override
		protected String doInBackground(String... para) {
			// TODO Auto-generated method stub
			
			List<NameValuePair> params=new ArrayList<NameValuePair>();
			
			params.add(new BasicNameValuePair(TAG_ID, id));
			params.add(new BasicNameValuePair(TAG_TRIP_NAME, name));
			params.add(new BasicNameValuePair(TAG_DATE, date));
			
			json=jsonParser.makeHttpRequest(url, "POST", params);
			
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			c.runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					
					try{
						
					message=json.getString("message");
					TripDetailsUpdate.this.trip_id=Integer.toString(json.getInt(TAG_TRIP_ID));
					//alert.showAlertDialog(c, "Trip Details Update",message +" And Your Tour Id is: "+trip_id, false);
					Log.d("Trip Update :","ID "+trip_id);
				
					}catch(Exception e){
						
						e.printStackTrace();
					}
				}
			});
		}
		
		
		
		
	}
	
}
