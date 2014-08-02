package com.mywork.ui;



import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mywork.onmaps.Calendar;
import com.mywork.onmaps.Profile;
import com.mywork.place.PlacesMapActivity;
import com.redknot.summary.RetrieveData;
import com.redknot.summary.SummaryActivity;

import com.redknot.resources.*;
import com.samtube405.authenticate.Authentication;
import com.samtube405.singleplace.InboxActivity;
import com.samtube405.singleplace.JSONParser;
import com.samtube405.timeline.FileCache;
import com.samtube405.timeline.ImageLoader;
import com.samtube405.timeline.MemoryCache;
import com.samtube405.timeline.TimelineActivity;
import com.samtube405.timeline.Utils;



import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
//import android.os.StrictMode;
import android.text.Html;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.QuickContactBadge;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;


@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class Login extends Activity implements OnItemClickListener{

	
	TextView tvUSername;
	QuickContactBadge imguser;
	
	
	User user=null;
	String username="USER DEFAULT";
	long userid=0;
	
	
	static String TWITTER_CONSUMER_KEY = "";
	static String TWITTER_CONSUMER_SECRET = "";
	
	static String PREFERENCE_NAME = "twitter_oauth";
	static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
	static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
	static final String PREF_KEY_TWITTER_LOGIN = "isTwitterLogedIn";
	
	static final String TWITTER_CALLBACK_URL = "oauth://t4jsample";
	
	static final String URL_TWITTER_AUTH = "auth_url";
	static final String URL_TWITTER_OAUTH_VERIFIER = "oauth_verifier";
	static final String URL_TWITTER_OAUTH_TOKEN = "oauth_token";
	
	// Progress dialog
	ProgressDialog pDialog;
	
	private static Twitter twitter;
	private static RequestToken requestToken;

	private static SharedPreferences mSharedPreferences;

	private ConnectionDetector cd;

	AlertDialogManager alert = new AlertDialogManager();
	

	Button btnLoginTwitter,btnLogoutTwitter;
	private AccessToken accessToken;
	
	private GestureDetector gd=null;
	LinearLayout layout;
	
	private int pics=0;
	
	CoverFlow coverFlow;
	
	Authentication au;
	
	JSONParser jsonParser_login = new JSONParser();
	
	JSONParser jsonParser_icons = new JSONParser();
	
	private static final String ADD_USER_URL = "http://redknot.ckreativity.com/android_connect/add_user.php";
	
	private static final String GET_ICONS_URL = "http://redknot.ckreativity.com/android_connect/get_icons.php";	
	
	private static final String IMG_MARKER_URL = "http://redknot.ckreativity.com/media/map_markers/";
	
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_PROFILE = "profile";	
	private static final String TAG_USER_NAME = "user_name";
	private static final String TAG_USER_ID = "user_id";
	
	private static final String TAG_CATEGORY = "category";	
	private static final String TAG_DISPLAY_TAG = "displaytag";
	private static final String TAG_TYPE = "type";
	private static final String TAG_ICON = "icon";
	
	
	//@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ui_main);
		
		layout=(LinearLayout) findViewById(R.id.rl1);
		
		tvUSername=(TextView)findViewById(R.id.tvUsername);
		
		imguser=(QuickContactBadge)findViewById(R.id.imguser);
		
		//Extra methods Twitter
		if (Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
			}
		
		cd = new ConnectionDetector(getApplicationContext());

		// Check if Internet present
		if (!cd.isConnectingToInternet()) {
			// Internet Connection is not present
			alert.showAlertDialog(Login.this, "Internet Connection Error",
					"Please connect to working Internet connection", false);
					//super.onBackPressed();
			//super.
			// stop executing code by return
			return;
		}
		
		// Check if twitter keys are set
		if(TWITTER_CONSUMER_KEY.trim().length() == 0 || TWITTER_CONSUMER_SECRET.trim().length() == 0){
			// Internet Connection is not present
			alert.showAlertDialog(Login.this, "Twitter oAuth tokens", "Please set your twitter oauth tokens first!", false);
			// stop executing code by return
			return;
		}

		// Shared Preferences
		mSharedPreferences = getApplicationContext().getSharedPreferences(
						"MyPref", 0);

		
		//////////////////////////////////////////////Animation Code
				
		
		coverFlow =  (CoverFlow) findViewById(R.id.coverflow);
		

		coverFlow.setAdapter(new ImageAdapter(this));

		ImageAdapter coverImageAdapter = new ImageAdapter(this);

		coverImageAdapter.createReflectedImages();

		coverFlow.setAdapter(coverImageAdapter);
		
		coverFlow.setOnItemClickListener(this);		

		coverFlow.setSpacing(-10);

		coverFlow.setSelection(2, true);

		coverFlow.setAnimationDuration(1000);
		///////////////////////////////////////////////////////////////
		
		
		
		btnLoginTwitter=(Button)findViewById(R.id.btnLoginTwitter);
		btnLogoutTwitter=(Button)findViewById(R.id.btnLogoutTwitter);
		
		
	
		/**
		 * Twitter login button click event will call loginToTwitter() function
		 * */
		btnLoginTwitter.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// Check if Internet present
				if (!cd.isConnectingToInternet()) {
					// Internet Connection is not present
					alert.showAlertDialog(Login.this, "Internet Connection Error",
							"Please connect to working Internet connection", false);							
					// stop executing code by return
					return;
				}
				// Call login twitter function
				loginToTwitter();
			}
		});
		
		/**
		 * Button click event for logout from twitter
		 * */
		btnLogoutTwitter.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// Call logout twitter function
				logoutFromTwitter();
				FileCache delf=new FileCache(getApplicationContext());
				delf.clear();
			}
		});	
		
		
		testRedirect();

		
	}
	
	public void testRedirect(){
		/** This if conditions is tested once is
		 * redirected from twitter page. Parse the uri to get oAuth
		 * Verifier
		 * */
		if (!isTwitterLoggedInAlready()) {			
			Uri uri=getIntent().getData();
			if (uri != null && uri.toString().startsWith(TWITTER_CALLBACK_URL)) {
				// oAuth verifier
				String verifier = uri
						.getQueryParameter(URL_TWITTER_OAUTH_VERIFIER);

				try {
					// Get the access token
					accessToken = twitter.getOAuthAccessToken(
							requestToken, verifier);

					// Shared Preferences
					Editor e = mSharedPreferences.edit();

					// After getting access token, access token secret
					// store them in application preferences
					e.putString(PREF_KEY_OAUTH_TOKEN, accessToken.getToken());
					e.putString(PREF_KEY_OAUTH_SECRET,
							accessToken.getTokenSecret());
					// Store login status - true
					e.putBoolean(PREF_KEY_TWITTER_LOGIN, true);
					e.commit(); // save changes

					Log.e("Twitter OAuth Token", "> " + accessToken.getToken());
					
					//setUserName(twitter,accessToken);

					setEnvironment();			

					new Initialize().execute();
					
					
					
					
				} catch (Exception e) {
					// Check log for login errors
					Log.e("Twitter Login Error", "> " + e.getMessage());
				}
			}
		}
	}
	
	public void setEnvironment(){
		btnLoginTwitter.setVisibility(View.GONE);
		
		coverFlow.setVisibility(View.VISIBLE);	
		tvUSername.setVisibility(View.VISIBLE);
		imguser.setVisibility(View.VISIBLE);
		
		au=new Authentication(this);
		
		username=au.getUserName();
		
		userid=au.getUserID();
		
		tvUSername.setText(username);
		
		imguser.setImageBitmap(au.getUserPic());
		
		
	}
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.ui_main, menu);
		return true;
	}
	
		
		/**
		 * Function to login twitter
		 * */
		private void loginToTwitter() {
			// Check if already logged in
			if (!isTwitterLoggedInAlready()) {
				ConfigurationBuilder builder = new ConfigurationBuilder();
				builder.setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
				builder.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET);
				Configuration configuration = builder.build();
				
				TwitterFactory factory = new TwitterFactory(configuration);
				twitter = factory.getInstance();

				if(!(Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO)) {
					try {
						requestToken = twitter.getOAuthRequestToken(TWITTER_CALLBACK_URL);
						this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(requestToken.getAuthenticationURL())));
						//startActivity(new Intent(Login.this,MainInterface.class));
					} catch (TwitterException e) {
						e.printStackTrace();
					}
				}else{
					new Thread(new Runnable() {
						public void run() {
						try {	
						requestToken = twitter.getOAuthRequestToken(TWITTER_CALLBACK_URL);
						Login.this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(requestToken.getAuthenticationURL())));
						//startActivity(new Intent(Login.this,MainInterface.class));
						} catch (TwitterException e) {
						e.printStackTrace();
						}
						}
						}).start();
					}	
				
				//userlabel.setText(""+getUserName());
				
			} else {						
				
				
				setEnvironment();	
				
				Toast.makeText(getApplicationContext(),
						"Hello You're Already Logged into twitter "+username, Toast.LENGTH_LONG).show();
				
				
				
				
				
				
				
			}
			
		}

		private void logoutFromTwitter() {
			// Clear the shared preferences
			Editor e = mSharedPreferences.edit();
			e.remove(PREF_KEY_OAUTH_TOKEN);
			e.remove(PREF_KEY_OAUTH_SECRET);
			e.remove(PREF_KEY_TWITTER_LOGIN);
			e.commit();			

			finish();
			//btnLoginTwitter.setVisibility(View.VISIBLE);
		}
		
		
		
		private boolean isTwitterLoggedInAlready() {
			// return twitter login status from Shared Preferences
			return mSharedPreferences.getBoolean(PREF_KEY_TWITTER_LOGIN, false);
		}
		
		@Override
		public void onItemClick(AdapterView<?> arg0, View v, int pos,long arg3) {
			// TODO Auto-generated method stub
			String clicked="";
			switch(pos){
			
			case 2:				
				//new NewTour().execute();
				Intent i0=new Intent(Login.this,PlacesMapActivity.class);
				i0.putExtra("username", username);
				i0.putExtra("userid", String.valueOf(userid));
				startActivity(i0);
				break;
				
			case 0:
				//btn="Intelligence Tour";
				//Intent i1=new Intent(Login.this, BarChart.class);
				//startActivity(i1);
				break;
				
			case 1:
				//btn="Favourits";
				Intent i2=new Intent(Login.this, RetrieveData.class);
				startActivity(i2);
				break;
				
			case 3:
				//btn="Community";
				Intent i3=new Intent(Login.this, TimelineActivity.class);
				startActivity(i3);
				break;
				
			case 4:
				Intent i4=new Intent(Login.this,Profile.class);
				i4.putExtra("userid", String.valueOf(userid));
				i4.putExtra("username", String.valueOf(username));
				startActivity(i4);

			}
			
						
		}

	
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}	
	
	
	public class ImageAdapter extends BaseAdapter{

		int mGalleryItemBackground;

		private Context mContext;

		private FileInputStream fis;

		private Integer[] mImageIds = {		
			
		  R.drawable.ui_recommendation,
		  R.drawable.ui_charts,
		  R.drawable.ui_newtour,
		  R.drawable.ui_timeline,
		  R.drawable.ui_cage_profile

		};

		private ImageView[] mImages;

		public ImageAdapter(Context c) {

			mContext = c;

			mImages = new ImageView[mImageIds.length];
			//Toast.makeText(CoverFlowExample.this,"WORKING",Toast.LENGTH_LONG ).show();
			
		}

		public boolean createReflectedImages() {
			//Toast.makeText(CoverFlowExample.this,"WORKING22222",Toast.LENGTH_LONG ).show();

			// The gap we want between the reflection and the original image

			final int reflectionGap = 4;

			int index = 0;

			for (int imageId : mImageIds) {

				Bitmap originalImage = BitmapFactory.decodeResource(
						getResources(),

						imageId);

				int width = originalImage.getWidth();

				int height = originalImage.getHeight();

				// This will not scale but will flip on the Y axis

				Matrix matrix = new Matrix();

				matrix.preScale(1, -1);

				// Create a Bitmap with the flip matrix applied to it.

				// We only want the bottom half of the image

				Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0,
						height / 2, width, height / 2, matrix, false);

				// Create a new bitmap with same width but taller to fit
				// reflection

				Bitmap bitmapWithReflection = Bitmap.createBitmap(width

				, (height + height / 2), Config.ARGB_8888);

				// Create a new Canvas with the bitmap that's big enough for

				// the image plus gap plus reflection

				Canvas canvas = new Canvas(bitmapWithReflection);

				// Draw in the original image

				canvas.drawBitmap(originalImage, 0, 0, null);

				// Draw in the gap

				Paint deafaultPaint = new Paint();

				canvas.drawRect(0, height, width, height + reflectionGap,
						deafaultPaint);

				// Draw in the reflection

				canvas.drawBitmap(reflectionImage, 0, height + reflectionGap,
						null);

				// Create a shader that is a linear gradient that covers the
				// reflection

				Paint paint = new Paint();

				LinearGradient shader = new LinearGradient(0,
						originalImage.getHeight(), 0,

						bitmapWithReflection.getHeight() + reflectionGap,
						0x70ffffff, 0x00ffffff,

						TileMode.CLAMP);

				// Set the paint to use this shader (linear gradient)

				paint.setShader(shader);

				// Set the Transfer mode to be porter duff and destination in

				paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));

				// Draw a rectangle using the paint with our linear gradient

				canvas.drawRect(0, height, width,

				bitmapWithReflection.getHeight() + reflectionGap, paint);

				ImageView imageView = new ImageView(mContext);

				imageView.setImageBitmap(bitmapWithReflection);
				
				//imageView.setId(index);
				
				//Log.d("INDEX SET : ",""+index);
				
				//imageView.setOnClickListener(this);
				

				imageView.setLayoutParams(new CoverFlow.LayoutParams(120, 180));

				imageView.setScaleType(ScaleType.MATRIX);

				mImages[index++] = imageView;

			}

			return true;

		}

		public int getCount() {

			return mImageIds.length;

		}

		public Object getItem(int position) {

			return position;

		}

		public long getItemId(int position) {

			return position;

		}

		public View getView(int position, View convertView, ViewGroup parent) {

			// Use this code if you want to load from resources

						ImageView i = new ImageView(mContext);

						i.setImageResource(mImageIds[position]);

						i.setLayoutParams(new CoverFlow.LayoutParams(250, 250));

						i.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
						
						i.setId(pics);
						
						pics++;

						// Make sure we set anti-aliasing otherwise we get jaggies

						BitmapDrawable drawable = (BitmapDrawable) i.getDrawable();

						drawable.setAntiAlias(true);

						return i;

		}

		/**
		 * Returns the size (0.0f to 1.0f) of the views 182. depending on the
		 * 'offset' to the center.
		 */

		public float getScale(boolean focused, int offset) {

			/* Formula: 1 / (2 ^ offset) */

			return Math.max(0, 1.0f / (float) Math.pow(2, Math.abs(offset)));

		}

		

	}
	

	/**
	 * Background Async Task to Load all INBOX messages by making HTTP Request
	 * */
	class Initialize extends AsyncTask<String, String, String> {
		

		private int success;
		
		


		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(Login.this);
			pDialog.setMessage("Initialize with RedKnot...");
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
			
			params.add(new BasicNameValuePair(TAG_USER_NAME, username));
			params.add(new BasicNameValuePair(TAG_USER_ID, String.valueOf(userid)));
			
			// getting JSON string from URL
			JSONObject json = jsonParser_login.makeHttpRequest(ADD_USER_URL, "POST",
					params);

			// Check your log cat for JSON reponse
			Log.d("User JSON: ", json.toString());
			
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
				Toast.makeText(getApplicationContext(),
						"Hello Welcome "+username+" to the RedKnot community ", Toast.LENGTH_LONG).show();				
			}else{
				Toast.makeText(getApplicationContext(),
						"You're already in the RedKnot community "+username, Toast.LENGTH_LONG).show();
			}
			
			new LoadIcons().execute();
				
			

		}
		
		

	}
	
	class LoadIcons extends AsyncTask<String, String, String> {
		
		private int success;
		
		

		private JSONArray icons;
		
		ArrayList<HashMap<String, String>> iconList = new ArrayList<HashMap<String, String>>();

		private int icon_success;

		private ProgressDialog pDialog2;


		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog2 = new ProgressDialog(Login.this);
			pDialog2.setMessage("Configuring Environment...");
			pDialog2.setIndeterminate(false);
			pDialog2.setCancelable(false);
			pDialog2.show();
		}

		/**
		 * getting Inbox JSON
		 * */
		protected String doInBackground(String... args) {
			
			MemoryCache memoryCache=new MemoryCache();
			
			ImageLoader imgLoader=new ImageLoader(getApplicationContext());
			
			// Building Parameters
			List<NameValuePair> no_params = new ArrayList<NameValuePair>();
			
			//no_params.add(new BasicNameValuePair(TAG_USER_NAME, username));
				
				// getting JSON string from URL
				JSONObject json_icons = jsonParser_icons.makeHttpRequest(GET_ICONS_URL, "GET",
						no_params);

				// Check your log cat for JSON reponse
				Log.d("Icons JSON: ", json_icons.toString());

				try {
					icons = json_icons.getJSONArray(TAG_CATEGORY);
					// looping through All messages
					for (int i = 0; i < icons.length(); i++) {
						JSONObject c = icons.getJSONObject(i);

						// Storing each json item in variable
						String displaytag = c.getString(TAG_DISPLAY_TAG);
						
						String type = c.getString(TAG_TYPE);
						
						String icon_url = IMG_MARKER_URL+c.getString(TAG_ICON);
						
						// creating new HashMap
						HashMap<String, String> map = new HashMap<String, String>();

						// adding each child node to HashMap key => value
						map.put(TAG_DISPLAY_TAG, displaytag);
						map.put(TAG_TYPE, type);
						map.put(TAG_ICON, icon_url);
						
						Bitmap bmp=imgLoader.getBitmap(icon_url);
						
			            memoryCache.put(icon_url, bmp);
						

						// adding HashList to ArrayList
						iconList.add(map);
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}

				
			
			
			try {
				icon_success=json_icons.getInt(TAG_SUCCESS);
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
			pDialog2.dismiss();			
			
			if(icon_success==1) Log.d("Icons Cached :","True");
				

		}
		
	}


	
	
	
}
