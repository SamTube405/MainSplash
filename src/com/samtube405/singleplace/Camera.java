package com.samtube405.singleplace;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import com.mywork.place.PlacesMapActivity;
import com.mywork.ui.R;
import com.sam.addplace.Addplace;
import com.samtube405.gallery.FullImageActivity;
import com.samtube405.gallery.ImageAdapter;
import com.samtube405.track.ParcelableGeoPoint;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class Camera extends Activity implements View.OnClickListener{
	ImageButton ib;
	//Button b;
	
	Intent i;
	final static int cameraResults=0;
	Bitmap bmp;
	
	

	// Creating JSON Parser object
	JSONParser jsonParser = new JSONParser();
	
	JSONParser jsonParser_gallery = new JSONParser();
	
	// Add photo JSON url
	private static final String ADD_PHOTO_URL = "http://redknot.ckreativity.com/android_connect/add_photo.php";
	
	private static final String GET_GALLERY_URL = "http://redknot.ckreativity.com/android_connect/get_gallery.php";
	
	private static final String MEDIA_URL = "http://redknot.ckreativity.com/media/";
	
	// JSON Node names
	private static final String TAG_SUCCESS = "success";
		
	String photo="";
	
	//palce Details
    HashMap<String,String> place;
    
	private String username="",userid="";
	
	public static String KEY_NAME = "name"; // name of the place	  	
  	public static String KEY_LATITUDE = "lat"; // Place latitude
  	public static String KEY_LONGITUDE = "long"; // Place longitude
  	public static String KEY_ADDRESS = "address"; // Place area name
  	public static String KEY_CONTACT = "contact"; // Place area name
  	public static String KEY_USER = "username";
  	public static String KEY_USER_ID = "userid";
  	
  	public static final String TAG_GALLERY = "gallery";
	public static final String TAG_IMAGE_URL = "image";
	public static final String TAG_UP_DATE = "uploaded_time";
	public static final String TAG_USER_NAME = "user_name";
	
	
	// Hashmap for ListView
	ArrayList<HashMap<String, String>> ggalleryList = new ArrayList<HashMap<String, String>>();
	
	
  	String placename="";
  	
  	String lat,longi;
	public JSONArray gallery;
	
	GridView gridView ;
  	
	Bitmap thumbs[];
  	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photo);
		
		//Extra methods Twitter
				if (Build.VERSION.SDK_INT > 9) {
					StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
					StrictMode.setThreadPolicy(policy);
					}
		
		Intent i = getIntent();
        
        place=(HashMap<String,String>)i.getSerializableExtra("place");
        
        placename=place.get(KEY_NAME);
        String address=place.get(KEY_ADDRESS);
        String contact=place.get(KEY_CONTACT);
        lat=place.get(KEY_LATITUDE);
        longi=place.get(KEY_LONGITUDE);
        
        username=place.get(KEY_USER);
        
        userid=place.get(KEY_USER_ID);
        
		initialize();
		
		new LoadGallery().execute();		
		
		

		/**
		 * On Click event for Single Gridview Item
		 * */
		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				
				// Sending image id to FullScreenActivity
				Intent i = new Intent(getApplicationContext(), FullImageActivity.class);
				// passing array index
				//i.putExtra("id", position);
				
				
				i.putExtra("thumb", thumbs[position]);
				
				i.putExtra("image", ggalleryList.get(position));
				
				startActivity(i);
			}
		});
	}

	private void initialize() {
		gridView = (GridView) findViewById(R.id.grid_view);
		ib=(ImageButton)findViewById(R.id.ibTakepic);		
		//b=(Button)findViewById(R.id.bSetWall);
		//b.setOnClickListener(this);
		ib.setOnClickListener(this);
	}
	

	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		
		case R.id.ibTakepic:i=new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
							startActivityForResult(i, cameraResults);break;
		}
		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode==RESULT_OK){
			Bundle extras=data.getExtras();
			
			bmp=(Bitmap)extras.get("data");
			//iv.setImageBitmap(bmp);
			
			photo=getEncodedPhotoString(bmp);
			
			AlertDialog alert = new AlertDialog.Builder(
					Camera.this).create();
			alert.setTitle("SHARE IT!");
			alert.setMessage("Do you want to share with RedKnot Community?!");
			alert.setButton("YES",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog,
								int which) {
							new AddPhoto(photo).execute();
						}
					});
			alert.setButton2("NO",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog,
								int which) {
							return;
						}
					});
			alert.show();
			
		}
	}
	
	public String getEncodedPhotoString(Bitmap bitmapOrg){		
		
		ByteArrayOutputStream bao = new ByteArrayOutputStream();

		bitmapOrg.compress(Bitmap.CompressFormat.JPEG, 100, bao);		

		byte [] ba = bao.toByteArray();

		String enstr=Base64.encodeBytes(ba);
		
		Log.d("Photo ", enstr);
		
		return enstr;
	}
	
	/**
	 * Background Async Task to Load all INBOX messages by making HTTP Request
	 * */
	class AddPhoto extends AsyncTask<String, String, String> {
		// Progress Dialog
		private ProgressDialog pDialog;
		
		int success=0;
		String photo="";
		public AddPhoto (String photo) {
			this.photo=photo;
		}

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(Camera.this);
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
			
			params.add(new BasicNameValuePair("lat", lat ));
			params.add(new BasicNameValuePair("longt", longi ));
			params.add(new BasicNameValuePair("place_name", placename));
			params.add(new BasicNameValuePair("user_id", userid));			
			params.add(new BasicNameValuePair("src", photo));

			
			// getting JSON string from URL
			JSONObject json = jsonParser.makeHttpRequest(ADD_PHOTO_URL, "POST",
					params);

			// Check your log cat for JSON reponse
			Log.d("Photo JSON: ", json.toString());		
			
			
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
			if(success==1){
				Toast.makeText(getApplicationContext(), "Your Photo Added!!", Toast.LENGTH_SHORT).show();
				new LoadGallery().execute();
			}
			else{
				Toast.makeText(getApplicationContext(), "Photo Adding Failed..Try Again!", Toast.LENGTH_SHORT).show();
			}
			//etmsg.setText("");
			//iv.setImageBitmap(null);

		}

	}
	
	/**
	 * Background Async Task to Load all INBOX messages by making HTTP Request
	 * */
	class LoadGallery extends AsyncTask<String, String, String> {
		// Progress Dialog
		private ProgressDialog pDialog;		
		
		// Hashmap for ListView
		ArrayList<HashMap<String, String>> lgalleryList = new ArrayList<HashMap<String, String>>();

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			pDialog = new ProgressDialog(Camera.this);
			pDialog.setMessage("Loading Gallery...");
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
			JSONObject json = jsonParser_gallery.makeHttpRequest(GET_GALLERY_URL, "GET",
					params);

			// Check your log cat for JSON reponse
			Log.d("Gallery JSON: ", json.toString());
			
			

			try {
				if(json.getInt(TAG_SUCCESS)==1){
				
				gallery = json.getJSONArray(TAG_GALLERY);
				
				thumbs=new Bitmap[gallery.length()];
				
				Log.d("Length : ",String.valueOf(gallery.length()));
				// looping through All messages
				for (int i = 0; i < gallery.length(); i++) {
					JSONObject c = gallery.getJSONObject(i);

					// Storing each json item in variable
					String image_url = c.getString(TAG_IMAGE_URL);
					
					String up_date = c.getString(TAG_UP_DATE);
					
					String user_name = c.getString(TAG_USER_NAME);
					

					// creating new HashMap
					HashMap<String, String> photo = new HashMap<String, String>();

					// adding each child node to HashMap key => value
					photo.put(TAG_IMAGE_URL, image_url);
					photo.put(TAG_UP_DATE, up_date);
					photo.put(TAG_USER_NAME, user_name);					
					

					// adding HashList to ArrayList					
					lgalleryList.add(photo);				
					
				}
				
				ggalleryList=lgalleryList;
				
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
			int i=0;
			URL url;
			if(lgalleryList!=null){
			
			for(HashMap<String, String> p:lgalleryList){
				String iurl=MEDIA_URL+p.get(TAG_IMAGE_URL).replaceAll(" ", "%20");
				try {
					url=new URL(iurl);			
					
					Log.d("URLs ",iurl);
				
					thumbs[i]=BitmapFactory.decodeStream(url.openConnection().getInputStream());
					
					i++;
					
				} catch (MalformedURLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			}			
			
			ImageAdapter ia=new ImageAdapter(Camera.this,thumbs);
			// Instance of ImageAdapter Class
			gridView.setAdapter(ia);
			
			Log.d("Adapetr count :",String.valueOf(ia.getCount()));
						
			pDialog.dismiss();
			
			
		}
	
	
	
}
	
}
