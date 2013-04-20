package com.mywork.db;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import com.mywork.ui.Login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;


public class LoginAuthenticate{

	
	
	JSONParser jp= new JSONParser();
	
	private static final String url="http://redknot.ckreativity.com/android_connect/get_log_details.php";
	
	private static final String TAG_SUCCESS = "success";
    private static final String TAG_LOGIN = "log_d";
    private static final String TAG_USER = "user";
    private static final String TAG_PASS = "password";
    String U_Name,Password;
    static String result="mismatch";
   
    
    
    public LoginAuthenticate(String uname,String passwrd){
    	
    	this.U_Name=uname;
    	this.Password=passwrd;
    }
	
	
	public String init(){
		
		
		new GetLoginDetails().execute();
		return result;
	}

	
	class GetLoginDetails extends AsyncTask<String, String, String> {
		
        protected String doInBackground(String... params) {
 
            // updating UI from Background Thread
            
                    // Check for success tag
                    int success;
                    try {
                        // Building Parameters
                        List<NameValuePair> params1 = new ArrayList<NameValuePair>();
                        params1.add(new BasicNameValuePair("user", U_Name));
 
                        // getting product details by making HTTP request
                        // Note that product details url will use GET request
                        JSONObject json = jp.makeHttpRequest(
                                url,"GET", params1);
 
                        // check your log for json response
                        //System.out.println(json);
                        Log.d("Login Details", json.toString());
 
                        // json success tag
                       success = json.getInt(TAG_SUCCESS);
                        if (success == 1) {
                            // successfully received product details
                            JSONArray productObj = json
                                    .getJSONArray(TAG_LOGIN); // JSON Array
 
                            // get first product object from JSON Array
                            JSONObject product = productObj.getJSONObject(0);
                            
                            String got_uname=product.getString(TAG_USER);
                            String got_passwrd=product.getString(TAG_PASS);
                            
                            System.out.println("GOT USERNAME: "+got_uname);
                            System.out.println("GOT PASSWORD: "+got_passwrd);
                            
                            if(got_uname.equals(U_Name) && got_passwrd.equals(Password)){
                            	
                            	result="match";
                            	System.out.println("**************** MATCHED *****************");
                            	return result;
                            	
                            }
 
                 
                        }else{
                            // product with pid not found
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
          
 
            return null;
        }
 
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once got all details
           
        }
    }

}

