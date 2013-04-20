package com.sam.addplace;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.maps.GeoPoint;
import com.samtube405.track.ParcelableGeoPoint;
import com.mywork.ui.R;





import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class Addplace extends Activity implements OnClickListener, OnItemSelectedListener{

	Button save;
	ImageButton ibBack;
	EditText inputlat,inputlon,inputname,inputaddress,inputcontact;
	Spinner spinner,subspinner;
	RatingBar ratingBar;
	String map,type="NO TYPE";
	String userrating="0";
	
	private ProgressDialog pDialog;
	private static String url_add_name ="http://redknot.ckreativity.com/android_connect/add_name.php";
    private static final String TAG_SUCCESS1 = "success";
	
    JSONParser jsonParser1 = new JSONParser();
    JSONParser jParser = new JSONParser();
    JSONArray prefArray = null;
	
	ArrayList<String> preferencelist = new ArrayList<String>();
	
	
	
	HashMap<String, String[]> cats = new HashMap<String, String[]>();
	
	
	private static String url_all_prefs = "http://redknot.ckreativity.com/android_connect/get_prefs.php";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_CATEGORY = "category";
    private static final String TAG_DISPLAY = "displaytag";
    private static final String TAG_TYPE = "type";
    
    GeoPoint gp=null;
  
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.newplace);
		
		ParcelableGeoPoint pgp=getIntent().getParcelableExtra("geoPoint");
		gp=pgp.getGeoPoint();
		
		save=(Button)findViewById(R.id.bSave);
		//ibBack=(ImageButton)findViewById(R.id.ibBack);
		//inputlat=(EditText)findViewById(R.id.etLat);
		//inputlon=(EditText)findViewById(R.id.etLong);
		inputname=(EditText)findViewById(R.id.etName);
		inputaddress=(EditText)findViewById(R.id.etAddress);
		inputcontact=(EditText)findViewById(R.id.etContact);
		spinner=(Spinner)findViewById(R.id.spinner1);
		subspinner=(Spinner)findViewById(R.id.spinner2);
		ratingBar=(RatingBar)findViewById(R.id.rbuserrating);
		
		
		new LoadAllPrefs().execute();
		
		spinner.setOnItemSelectedListener(this);
		subspinner.setOnItemSelectedListener(this);
		save.setOnClickListener(this);
		
		
		//if rating value is changed,
		//display the current rating value 
		ratingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
			

			public void onRatingChanged(RatingBar ratingBar, float rating,
				boolean fromUser) {
	 
				userrating=String.valueOf(rating);
	 
			}
		});
		
		
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
		
		switch (arg0.getId()) {
		case R.id.bSave:
			double lat=0,lon=0;
			if(gp!=null){
				// latitude
	               lat = gp.getLatitudeE6() / 1E6;
	               // longitude
	               lon = gp.getLongitudeE6() / 1E6;
			}
			
			new Addnewplace().execute(String.valueOf(lat),String.valueOf(lon),type,userrating);
			break;			
		

		
		}
		
	}
	
	
public	class Addnewplace extends AsyncTask<String, String, String> {
		
		
		private int success;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			
			
			pDialog = new ProgressDialog(Addplace.this);
            pDialog.setMessage("Adding Place..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
			
			
		}

		@Override
		protected String doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			
			
			
			String lat=arg0[0];
			String lon=arg0[1];
			String name=inputname.getText().toString();
			String address=inputaddress.getText().toString();
			String contact=inputcontact.getText().toString();
			String preference=arg0[2];
			String urating=arg0[3];
			
			
			
			
			List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("lat", lat));
            params.add(new BasicNameValuePair("longt", lon));
            params.add(new BasicNameValuePair("name", name));
            params.add(new BasicNameValuePair("address", address));
            params.add(new BasicNameValuePair("contact", contact));
            params.add(new BasicNameValuePair("preference", preference));
            params.add(new BasicNameValuePair("rating", urating));
			
            
            JSONObject json = jsonParser1.makeHttpRequest(url_add_name,"POST", params);
            
            Log.d("New Place : ", json.toString());
            
           
            try {
				success=json.getInt(TAG_SUCCESS1);
				 
					
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
            
            
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			//super.onPostExecute(result);
			
			pDialog.dismiss();
			/*Dialog d=new Dialog(Addplace.this);
			d.setTitle("Place added to database!");
			TextView tv=new TextView(Addplace.this);
			tv.setText("Success");
			d.setContentView(tv);	
			d.show();*/
			if(success==1){
				Toast.makeText(getApplicationContext(), "New Place Registered!!", Toast.LENGTH_SHORT).show();				
			}
			else{
				Toast.makeText(getApplicationContext(), "Adding New place Failed..Try Again!", Toast.LENGTH_SHORT).show();
			}
			
			//inputlat.setText("");
			//inputlon.setText("");
			inputname.setText("");
			inputaddress.setText("");
			inputcontact.setText("");
			
			
			
		}
		
		
		
		
	}
	
public class LoadAllPrefs extends AsyncTask<String, String, String>{

	
	
	@Override
	protected String doInBackground(String... arg0) {
		// TODO Auto-generated method stub
		
	
		
       List<NameValuePair> params = new ArrayList<NameValuePair>();
        
        // getting JSON string from URL
       JSONObject json = jParser.makeHttpRequest(url_all_prefs, "GET", params);

     
       Log.d("All Products: ", json.toString());
		
       
       
       try {
		int success = json.getInt(TAG_SUCCESS);
		
		if(success==1){
			
			
			prefArray = json.getJSONArray(TAG_CATEGORY);
			 
			String displaytag,type;
			
			String types[];
			
             for (int i = 0; i < prefArray.length(); i++) {
            	 
                 JSONObject c = prefArray.getJSONObject(i);
               
                 displaytag = c.getString(TAG_DISPLAY);
                 
                 type=c.getString(TAG_TYPE);
                 
                 types=type.split(",");
                 
                 cats.put(displaytag, types);

                 preferencelist.add(displaytag);
                 
                 
                 
                 
             }
             
            
             
		}else{
			
		}
		
		
	} catch (JSONException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
       
       
       
		return null;
	}

	@Override
	protected void onPostExecute(String result) {
		// TODO Auto-generated method stub
		//super.onPostExecute(result);
		
		 runOnUiThread(new Runnable() { public void run() {
			 
			 //System.out.println("inside thread..");
			ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(Addplace.this,android.R.layout.simple_spinner_item,preferencelist);

			// Drop down layout style - list view with radio button
			dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			 
			spinner.setAdapter(dataAdapter);
				
			  
	 } });
		
	}
	
}

@Override
public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {	 
	 switch (arg0.getId()) {
		case R.id.spinner1:
			map=arg0.getSelectedItem().toString();		
			Log.d("Category :",map);
			LoadSubTypes(map);
			break;
			
		case R.id.spinner2:
			type=arg0.getSelectedItem().toString().toLowerCase();	
			Log.d("Type 1 :",type);
			break;
		
		}
}

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
private void LoadSubTypes(String map) {
	
	ArrayList<String> subtypeslist = new ArrayList<String>();
	
	
	if(cats!=null && !(map.isEmpty())){		
		
		String types[]=cats.get(map);
		
		for(int i=0;i<types.length;i++) subtypeslist.add(types[i].toUpperCase());
		
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(Addplace.this,android.R.layout.simple_spinner_item,subtypeslist);

		// Drop down layout style - list view with radio button
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		 
		subspinner.setAdapter(dataAdapter);
	}
	
	
}

@Override
public void onNothingSelected(AdapterView<?> arg0) {
	// TODO Auto-generated method stub
	
}


	

}
