package com.redknot.summary;

import java.util.ArrayList;
import java.util.HashMap;

import com.mywork.ui.R;



import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

public class LazyAdapter extends BaseAdapter {
    
    
    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater=null;
    private Activity activity;
    
    private static final String TAG_NAME = "name";
	private static final String TAG_PERCENTAGE = "catpercent";
	private static final String TAG_TOTAL_COUNT = "totalcount";
    
    
    public LazyAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
        activity = a;
        data=d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
    }

    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }
    
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.list_item, null);

        TextView prefCategory = (TextView)vi.findViewById(R.id.prefName); // preference category
        TextView percentage = (TextView)vi.findViewById(R.id.tpercentage); // percentage preference
        ProgressBar pb = (ProgressBar)vi.findViewById(R.id.pb); // duration
       
        HashMap<String, String> pref = new HashMap<String, String>();
        pref = data.get(position);
        
        int prefp=0;
        
        try{
            prefp = (int)Double.parseDouble(pref.get(TAG_PERCENTAGE));
            
            }catch (Exception e){
            	percentage.setText("not working");
            }
        
        // Setting all values in listview
        prefCategory.setText(pref.get(TAG_NAME));
        percentage.setText(""+prefp+"%");
        pb.setProgress(prefp);
        return vi;
    }
}