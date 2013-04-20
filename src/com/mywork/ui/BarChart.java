package com.mywork.ui;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.LinearLayout;

public class BarChart extends Activity{

	GraphicalView mChart;
	CategorySeries series=new CategorySeries("Demo Bar Graph");
	XYMultipleSeriesDataset mDataset=new XYMultipleSeriesDataset();
	XYMultipleSeriesRenderer mRenderer=new XYMultipleSeriesRenderer();
	XYSeries mCurrentSeries;
	XYSeriesRenderer mCurrentRenderer=new XYSeriesRenderer();
	LinearLayout layout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chart);
		layout=(LinearLayout) findViewById(R.id.charts);
		initChart();
		addSampleData();
		mChart=ChartFactory.getBarChartView(this, mDataset, mRenderer, Type.DEFAULT);
		layout.addView(mChart);
	}

public void initChart(){
		
		mCurrentSeries=new XYSeries("Sample Data");
		mDataset.addSeries(mCurrentSeries);  
		
		//mCurrentRenderer=new XYSeriesRenderer();
		
		mCurrentRenderer.setDisplayChartValues(true);
		mCurrentRenderer.setChartValuesSpacing((float)0.5);
		mCurrentRenderer.setColor(Color.CYAN);
		
		mRenderer.addSeriesRenderer(mCurrentRenderer);
		/*for line graph*/
		mRenderer.setChartTitle("Community Favourits");
		mRenderer.setApplyBackgroundColor(true);
		mRenderer.setMarginsColor(Color.BLACK);
		mRenderer.setBackgroundColor(Color.BLACK);
		mRenderer.setZoomEnabled(true);
		mRenderer.setZoomButtonsVisible(true);
		mRenderer.setBarSpacing(0.25);
		mRenderer.setXTitle("Bar Values");
		mRenderer.setYTitle("Value");
}

public void addSampleData() {
	
	int y[]={24,35,43,56,34,23,42,34,23,43,34,74};
	
	for(int i=0;i<y.length;i++){
		
		mCurrentSeries.add((i+1),y[i]);
		mRenderer.addXTextLabel(i+1, "Bar"+(i+1));
	}
}
	
}
