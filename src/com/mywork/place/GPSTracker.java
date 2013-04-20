package com.mywork.place;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.text.Html;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

public class GPSTracker extends Service implements LocationListener {

	ProgressDialog pd;
	Context context;
	boolean isGPSEnabled=false;
	boolean isNetworkEnabled=false;
	boolean canGetLocation=false;
	Location currentBestLocation,location;
	double latitude,longitude;
	final static long MIN_DISTANCE_CHANGE_FOR_UPDATES=10;
	final static long MIN_TIME_BW_UPDATES=1000*2;
	final static long MAX_TIME_LIM=1000*60*2;
	LocationManager locationmanager;
	//Time got,now=new Time();
	Long old_place_time;
	public GPSTracker(Context c){
		this.context=c;
		getLocation();
		
	}
	
	public void getLocation(){
		
		try{
			//set location
			locationmanager=(LocationManager) context.getSystemService(LOCATION_SERVICE);
			
			//getting GPS status
			isGPSEnabled=locationmanager.isProviderEnabled(LocationManager.GPS_PROVIDER);
			
			//getting Network status
			isNetworkEnabled=locationmanager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
			
			if(!isGPSEnabled&&!isNetworkEnabled){
				//no GPS or Network -> no coverage
				
			}
			else{//at least one coverage
				
				this.canGetLocation=true;
				
				//get location by Network
				if(isNetworkEnabled){
					
					locationmanager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
					Log.d("Network","Network");
					
					if(locationmanager!=null){
						//get location from locationmanager
						currentBestLocation=locationmanager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
					
						/*(while(location==null){
							
						}*/
					
						/*if(isBetterLocation(location, currentBestLocation)){
							
							latitude=location.getLatitude();
							longitude=location.getLongitude();
							
							
						}
						
						else{
							latitude=currentBestLocation.getLatitude();
							longitude=currentBestLocation.getLongitude();
							
						}*/
						if(currentBestLocation!=null){
							//get lat and long if a location is there
							latitude=currentBestLocation.getLatitude();
							longitude=currentBestLocation.getLongitude();
							
							//Long got_time=location.getTime();
							//old_place_time=currentBestLocation.getTime();
							
						
							//got=new Time();
							//got.set(got_time);
							//now=new Time();
							//now.setToNow();
						}
						
					}
				}
				//get lat/long using GPS if available
				if(isGPSEnabled){
					if(location==null){
						locationmanager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
						Log.d("GPS Enabled","GPS Enabled");
					
						if(locationmanager!=null){
						//get location
							currentBestLocation=locationmanager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
							
							/*while(location==null){
								
							}*/
							
						
							
							
							/*if(isBetterLocation(location, currentBestLocation)){
								
								latitude=location.getLatitude();
								longitude=location.getLongitude();
								
								
							}
							
							else{
								latitude=currentBestLocation.getLatitude();
								longitude=currentBestLocation.getLongitude();
								
							}*/
							
							//old_place_time=currentBestLocation.getTime();
							//got=new Time();
							//got.set(got_time);
							//now=new Time();
							//now.setToNow();
							if(currentBestLocation!=null){
								
								latitude=currentBestLocation.getLatitude();
								longitude=currentBestLocation.getLongitude();
							
							}
							
						}
						
						
					}
					
				}
				
				
			}
			
			
		}catch(Exception e){
			e.printStackTrace();
			
		}
		
		
	}
	
	public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
 
        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");
 
        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
 
        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                context.startActivity(intent);
            }
        });
 
        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
            }
        });
 
        // Showing Alert Message
        alertDialog.show();
        
    }

	@Override
	public void onLocationChanged(Location l) {
		
		
		// TODO Auto-generated method stub
		location=l;
		if(isBetterLocation(location, currentBestLocation)){
			
			latitude=location.getLatitude();
			longitude=location.getLongitude();
			
			
		}
		
		else{
			latitude=currentBestLocation.getLatitude();
			longitude=currentBestLocation.getLongitude();
			
		}
		
		//Toast.makeText(context, "Your Current Location is, Lat: "+latitude+" - Long: "+longitude, Toast.LENGTH_LONG).show();
	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public boolean canGetLocation() {
        return this.canGetLocation;
    }
	
	public double getLatitude(){
		
		return this.latitude;
	}
	
	public double getLongitude(){
		
		return this.longitude;
	}
	public long locationGotTime(){
		
		return this.old_place_time;
	}
	/*public Time nowTime(){
	
	return this.now;
	}*/
	
	public boolean isBetterLocation(Location loc,Location currentbest){
		
		if(currentbest==null){
			return true;
		}
		
		long timedelta=loc.getTime()-currentbest.getTime();
		boolean isSignificantlyNewer=timedelta>MAX_TIME_LIM;
		boolean isSignificantlyOlder=timedelta< -MAX_TIME_LIM;
		boolean isnewer=timedelta>0;
		
		if(isSignificantlyNewer){
			
			return true;
		}else if(isSignificantlyOlder){
			
			return false;
		}
		
		int accuracyDelta = (int) (loc.getAccuracy() - currentbest.getAccuracy());
	    boolean isLessAccurate = accuracyDelta > 0;
	    boolean isMoreAccurate = accuracyDelta < 0;
	    boolean isSignificantlyLessAccurate = accuracyDelta > 50;
	    
	    boolean isFromSameProvider = isSameProvider(loc.getProvider(),
	            currentbest.getProvider());

	    if (isMoreAccurate) {
	        return true;
	    } else if (isnewer && !isLessAccurate) {
	        return true;
	    } else if (isnewer && !isSignificantlyLessAccurate && isFromSameProvider) {
	        return true;
	    }
	    return false;
		
	}
	
	private boolean isSameProvider(String provider1, String provider2) {
	    if (provider1 == null) {
	      return provider2 == null;
	    }
	    return provider1.equals(provider2);
	}
    
}
