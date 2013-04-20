package com.redknot.summary;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.widget.EditText;
 
public class AlertDialogManager {
    /**
     * Function to display simple Alert Dialog
     * @param context - application context
     * @param title - alert dialog title
     * @param message - alert message
     * @param status - success/failure (used to set icon)
     *               - pass null if you don't want icon
     * */
	Context c;
	String t;
	String m;
   
    public void showAlertDialog(Context context, String title, String message,
            Boolean status) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
 
        // Setting Dialog Title
        alertDialog.setTitle(title);
 
        // Setting Dialog Message
        alertDialog.setMessage(message);
 
        //if(status != null)
            // Setting alert dialog icon
            //alertDialog.setIcon((status) ? R.drawable.success : R.drawable.fail);
 
        // Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
 
        // Showing Alert Message
        alertDialog.show();
    }
    
    public void showAlertDialogWithEditText(Context context, String title, String message) {
    	c=context;
    	t=title;
    	m=message;
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
 
        // Setting Dialog Title
        alertDialog.setTitle(title);
 
        // Setting Dialog Message
        alertDialog.setMessage(message);
        
        final EditText nm = new EditText(context);
		nm.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
		alertDialog.setView(nm);
 
        //if(status != null)
            // Setting alert dialog icon
            //alertDialog.setIcon((status) ? R.drawable.success : R.drawable.fail);
 
        // Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            	String tourName=nm.getText().toString();
            	if (tourName.length() == 0) {
            		showAlertDialogWithEditText(c, t, m);
				}
            }
        });
 
        // Showing Alert Message
        alertDialog.show();
    }
}
