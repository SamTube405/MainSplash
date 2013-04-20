package com.samtube405.singleplace;

import java.util.HashMap;

import com.mywork.ui.R;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class AndroidTabAndListView extends TabActivity {
	// TabSpec Names
	private static final String INBOX_SPEC = "Reviews";
	private static final String OUTBOX_SPEC = "Capture It!";
	private static final String PROFILE_SPEC = "Place";
	
	
	//public static String KEY_NAME = "name"; // name of the place
	
	//palce Details
    HashMap<String,String> place;
    
 // KEY Strings
  	public static String KEY_REFERENCE = "reference"; // id of the place
  	public static String KEY_NAME = "name"; // name of the place
  	public static String KEY_VICINITY = "vicinity"; // Place area name
  	public static String KEY_LATITUDE = "lat"; // Place latitude
  	public static String KEY_LONGITUDE = "long"; // Place longitude
  	public static String KEY_ADDRESS = "address"; // Place area name
  	public static String KEY_CONTACT = "contact"; // Place area name
  	public static String KEY_RATING = "rating";
  	
  	public static String KEY_USER = "username";
  	public static String KEY_USER_ID = "userid";
  	public static String KEY_NOT_FLAG = "notFlag";
  	
  	String username="",notFlag="";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.singleplacemain);
        
          
        Intent i = getIntent();
        
        place=(HashMap<String,String>)i.getSerializableExtra("place");
        
        Log.d("USER ID TABHOST :",place.get(KEY_USER_ID));
        
        Log.d("Rating: ",place.get(KEY_RATING));
        
        
        
        TabHost tabHost = getTabHost();
        
        // Inbox Tab
        TabSpec inboxSpec = tabHost.newTabSpec(INBOX_SPEC);
        // Tab Icon
        inboxSpec.setIndicator(INBOX_SPEC, getResources().getDrawable(R.drawable.tabreview));
        Intent inboxIntent = new Intent(this, InboxActivity.class);
        inboxIntent.putExtra("place",place);
        // Tab Content
        inboxSpec.setContent(inboxIntent);
        
        // Outbox Tab
        TabSpec outboxSpec = tabHost.newTabSpec(OUTBOX_SPEC);
        outboxSpec.setIndicator(OUTBOX_SPEC, getResources().getDrawable(R.drawable.tabcamera));
        Intent cameraIntent = new Intent(this, Camera.class);
        cameraIntent.putExtra("place",place);
        outboxSpec.setContent(cameraIntent);
        
        // Profile Tab
        TabSpec profileSpec = tabHost.newTabSpec(PROFILE_SPEC);
        profileSpec.setIndicator(PROFILE_SPEC, getResources().getDrawable(R.drawable.tabplace));
        Intent profileIntent = new Intent(this, ProfileActivity.class); 
        profileIntent.putExtra("place",place);			
        profileSpec.setContent(profileIntent);
        
        // Adding all TabSpec to TabHost
        tabHost.addTab(profileSpec); // Adding Profile tab
        tabHost.addTab(inboxSpec); // Adding Inbox tab
        tabHost.addTab(outboxSpec); // Adding Outbox tab
        
    }
}