package edu.sfsu.cs.orange.ocr;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

public class ResultsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_results);
		
		String[] nutritionValues = savedInstanceState.getStringArray("nutritionValues");
		float[] nutritionQuant = savedInstanceState.getFloatArray("nutritionQuant");
		String update;
		
		TextView tv;
		int[] ids = {R.id.textView1,R.id.textView2,R.id.textView3,R.id.textView4,R.id.textView5,R.id.textView6};		
		for(int i=0;i<ids.length;i++){
			tv = (TextView) findViewById(ids[i]);
			update = nutritionValues[i] + " " + Float.toString(nutritionQuant[i]);
			tv.setText(update);
		}		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.results, menu);
		return true;
	}

}
