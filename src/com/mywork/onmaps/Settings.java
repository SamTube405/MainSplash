package com.mywork.onmaps;

import com.mywork.ui.R;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class Settings extends Activity{

	TextView tv;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		
		tv=(TextView) findViewById(R.id.settingsmaintv);
	}

}
