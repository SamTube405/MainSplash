package com.redknot.summary;

import java.util.ArrayList;
import java.util.HashMap;

import com.mywork.ui.R;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class SummaryActivity extends Activity {
    /** Called when the activity is first created. */
	
	static final String KEY_NAME = "name"; 
	static final String KEY_PERCENTAGE = "percentage";
	
    @SuppressWarnings("unchecked")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
       ListView list = (ListView)findViewById(R.id.list);
        
        ArrayList<HashMap<String, String>> prefList = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> singlecategory= new ArrayList<HashMap<String, String>>();
        
		
     // Getting intent data
		Intent i = getIntent();
		
		Bundle b=i.getExtras();
		
		//prefList = (ArrayList<HashMap<String, String>>) i.getSerializableExtra("summary");
		prefList=(ArrayList<HashMap<String, String>>) b.getSerializable("summary");
		
		//singlecategory=(ArrayList<HashMap<String, String>>) i.getSerializableExtra("siglecategory");
		singlecategory=(ArrayList<HashMap<String, String>>) b.getSerializable("singlecategory");
		
		final ArrayList<HashMap<String, String>> singlecat=singlecategory;
		
		//Log.d("Single Category Details: ",singlecategory.toString());
		
        LazyAdapter adapter=new LazyAdapter(this, prefList);        
        list.setAdapter(adapter);
        
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				String catName=((TextView)arg1.findViewById(R.id.prefName)).getText().toString();
				
				int percentage=((ProgressBar)arg1.findViewById(R.id.pb)).getProgress();
				
				
				
				Intent i=new Intent(SummaryActivity.this,PieChart.class);
				i.putExtra("catName", catName);
				i.putExtra("percentage", percentage);
				i.putExtra("singlecategory", singlecat);
				
				startActivity(i);
			}
		});
        
        
    }
}