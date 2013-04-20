package com.mywork.preference;

import java.util.HashMap;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SessionManager {

	// Shared Preferences
	SharedPreferences pref;

	// Editor for Shared preferences
	Editor editor;

	// Context
	Context context;

	// Shared pref mode
	int PRIVATE_MODE = 0;

	// Sharedpref file name
	private static final String PREF_NAME = "UserPref";

	private static final String KEY_PREF = "preferences";
	
	private static final String KEY_RAD = "radius";

	private static final String IS_PREF_SET = "IsPrefSet";
	
	private static final String IS_RADIUS_SET = "IsRadiusSet";

	public SessionManager(Context c) {

		this.context = c;
		pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
		editor = pref.edit();
		editor.putBoolean(IS_PREF_SET, false);
		editor.putBoolean(IS_RADIUS_SET, false);
		editor.commit();

	}

	public void addPreference(String pref) {
		editor.putBoolean(IS_PREF_SET, true);
		editor.putString(KEY_PREF, pref);
		editor.commit();

	}

	public void updatePreference(String pref) {
		if(pref.length()==0){
			editor.putBoolean(IS_PREF_SET, false);
		}
		editor.remove(KEY_PREF);
		editor.putString(KEY_PREF, pref);
		editor.commit();

	}
	
	public void addRadius(String rad) {
		editor.putBoolean(IS_RADIUS_SET, true);
		editor.putString(KEY_RAD, rad);
		editor.commit();

	}
	
	public void updateRadius(String rad) {
		if(rad.length()==0){
			editor.putBoolean(IS_RADIUS_SET, false);
		}
		editor.remove(KEY_RAD);
		editor.putString(KEY_RAD, rad);
		editor.commit();

	}
	
	public HashMap<String,String> getPreference(){
		HashMap<String,String> prefs=new HashMap<String,String>();
		prefs.put(KEY_PREF, pref.getString(KEY_PREF, null));
		prefs.put(KEY_RAD, pref.getString(KEY_RAD, null));
		return prefs;
		
	}

	public boolean isPreferenceSet() {

		return pref.getBoolean(IS_PREF_SET, false);
	}

	public boolean isRadiusSet() {

		return pref.getBoolean(IS_RADIUS_SET, false);
	}
}
