package com.redknot.summary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.SeriesSelection;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import com.mywork.ui.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class PieChart extends Activity {

	GraphicalView mChart;
	CategorySeries series = new CategorySeries("Pie Graph");
	DefaultRenderer renderer = new DefaultRenderer();
	// XYMultipleSeriesDataset mDataset=new XYMultipleSeriesDataset();
	// XYMultipleSeriesRenderer mRenderer=new XYMultipleSeriesRenderer();
	// XYSeries mCurrentSeries;
	// XYSeriesRenderer mCurrentRenderer=new XYSeriesRenderer();
	LinearLayout layout;

	Vector<Double> typepercents = new Vector<Double>();
	Vector<Integer> typecounts = new Vector<Integer>();
	Vector<String> typenames = new Vector<String>();

	int[] colors;

	int no_of_types = 0;

	ArrayList<HashMap<String, String>> singleCategory = new ArrayList<HashMap<String, String>>();

	ArrayList<HashMap<String, String>> listdata = new ArrayList<HashMap<String, String>>();

	TextView catname, percent;
	ProgressBar pb;

	String catName;
	int percentage;

	ListView l;

	private static final String TAG_CATEGORY = "category";
	private static final String TAG_TYPE_TYPE = "type";
	private static final String TAG_TYPE_COUNT = "count";
	private static final String TAG_TYPE_PERCENTAGE = "percentage";

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chart);

		Intent i = getIntent();

		 l=(ListView)findViewById(R.id.typelist);

		catName = i.getStringExtra("catName");
		percentage = i.getIntExtra("percentage", 0);

		singleCategory = (ArrayList<HashMap<String, String>>) i
				.getSerializableExtra("singlecategory");

		Log.d("Single Category Details: ", singleCategory.toString());
		;

		catname = (TextView) findViewById(R.id.catName);
		catname.setText(catName);

		pb = (ProgressBar) findViewById(R.id.pb1);
		pb.setProgress(percentage);

		percent = (TextView) findViewById(R.id.percent);
		percent.setText("" + percentage + "%");

		layout = (LinearLayout) findViewById(R.id.charts);

		initChart();

		 setList();
		// mChart=ChartFactory.getBarChartView(this, mDataset, mRenderer,
		// Type.DEFAULT);
		mChart = ChartFactory.getPieChartView(this, series, renderer);

		/*
		 * mChart.setOnClickListener(new View.OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { // TODO Auto-generated method
		 * stub
		 * 
		 * SeriesSelection selection=mChart.getCurrentSeriesAndPoint();
		 * 
		 * if(selection!=null){
		 * 
		 * Toast.makeText( PieChart.this, "Chart element data point index " +
		 * selection.getPointIndex() + " was clicked" + " point value=" +
		 * selection.getValue(), Toast.LENGTH_SHORT).show(); }
		 * 
		 * } });
		 */

		/*mChart.setOnLongClickListener(new View.OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				SeriesSelection selection = mChart.getCurrentSeriesAndPoint();

				if (selection != null) {

					Toast.makeText(
							PieChart.this,
							"Chart element data point index "
									+ selection.getPointIndex()
									+ " was clicked" + " point value="
									+ selection.getValue(), Toast.LENGTH_SHORT)
							.show();
					
				}
				return false;
			}
		});*/

		layout.addView(mChart);

		
		  l.setOnItemClickListener(new AdapterView.OnItemClickListener() {
		  
		 
		@Override public void onItemClick(AdapterView<?> arg0, View arg1, int
		  arg2, long arg3) { // TODO Auto-generated method stub
			  
			  String type=((TextView)arg1.findViewById(R.id.type)).getText().toString();
			  
			  Intent i=new Intent(PieChart.this,BarChart.class);
			  i.putExtra("type", type);
			  startActivity(i);
		  
		  } });
		 
	}

	public void initChart() {

		/*
		 * mCurrentSeries=new XYSeries("Sample Data");
		 * mDataset.addSeries(mCurrentSeries);
		 * 
		 * //mCurrentRenderer=new XYSeriesRenderer();
		 * 
		 * mCurrentRenderer.setDisplayChartValues(true);
		 * mCurrentRenderer.setChartValuesSpacing((float)0.5);
		 * mCurrentRenderer.setColor(Color.CYAN);
		 * 
		 * mRenderer.addSeriesRenderer(mCurrentRenderer);
		 * 
		 * 
		 * mRenderer.setChartTitle("Bar Graph");
		 * mRenderer.setApplyBackgroundColor(true);
		 * mRenderer.setMarginsColor(Color.BLACK);
		 * mRenderer.setBackgroundColor(Color.BLACK);
		 * mRenderer.setZoomEnabled(true);
		 * mRenderer.setZoomButtonsVisible(true); mRenderer.setBarSpacing(0.25);
		 * mRenderer.setXTitle("Bar Values"); mRenderer.setYTitle("Value");
		 */

		for (int i = 0; i < singleCategory.size(); i++) {
			HashMap<String, String> onetype = new HashMap<String, String>();

			onetype = singleCategory.get(i);

			String category = onetype.get(TAG_CATEGORY);

			if (category.equals(catName)) {

				String type = onetype.get(TAG_TYPE_TYPE);
				double percent = Double.parseDouble(onetype
						.get(TAG_TYPE_PERCENTAGE));
				int count = Integer.parseInt(onetype.get(TAG_TYPE_COUNT));

				typepercents.add(no_of_types, percent);
				typecounts.add(no_of_types, count);
				typenames.add(no_of_types, type);

				series.add(type, percent);

				no_of_types++;

			}
		}

		colors = new int[no_of_types];
		
		List<Integer> colorCode=new ArrayList<Integer>();
		Random rand=new Random();
		for(int i = 0; i < no_of_types; i++){
			int x;
			do{
			
			x=(rand.nextInt(256)+1);
			
			}while(colorCode.contains(x));
			
			colorCode.add(x);
			
		}
		
		Collections.shuffle(colorCode,rand);

		Random rand1=new Random();
		Random rand2=new Random();
		for (int i = 0; i < no_of_types; i++) {

			SimpleSeriesRenderer s = new SimpleSeriesRenderer();

			colors[i] = Color.argb(255, (rand1.nextInt(256)+rand2.nextInt(100))/(i+1)^2, (rand1.nextInt(200)+rand2.nextInt(256))/(i+1)^3,
					(rand1.nextInt(256)+rand2.nextInt(200))/(i+1)^4);
			//colors[i]=Color.argb(255,colorCode.get(i)/(i+2),colorCode.get(i)/(i+4),colorCode.get(i)/(i+8));

			s.setColor(colors[i]);

			renderer.addSeriesRenderer(s);

		}
		renderer.setChartTitle(catName + " decomposition by types");
		renderer.setApplyBackgroundColor(true);
		renderer.setBackgroundColor(Color.BLACK);
		renderer.setZoomEnabled(true);
		renderer.setZoomButtonsVisible(true);
		renderer.setShowLabels(true);
		//renderer.setClickEnabled(true);
		//renderer.setSelectableBuffer(20);

	}

	public void setList() {

		for (int i = 0; i < no_of_types; i++) {
			HashMap<String, String> onetype = new HashMap<String, String>();

			onetype.put(TAG_TYPE_TYPE, typenames.get(i));
			onetype.put(TAG_TYPE_PERCENTAGE,
					String.valueOf(typepercents.get(i))+"%");

			listdata.add(onetype);
		}
		ListAdapter adapter = new SimpleAdapter(this, listdata,
				R.layout.type_list_item, new String[] { TAG_TYPE_TYPE,
						TAG_TYPE_PERCENTAGE }, new int[] { R.id.type,
						R.id.percentage });
		l.setAdapter(adapter);

	}

}
