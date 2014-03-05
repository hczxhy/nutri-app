package edu.sfsu.cs.orange.ocr;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

public class ResultsActivity extends Activity {

	public static final String NUTRITION_LABEL_KEY = "nutritionValues";
	public static final String NUTRITION_QUANT_KEY = "nutritionQuant";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_results);

		Bundle b = getIntent().getExtras();
		
		String[] nutritionValues = b.getStringArray(NUTRITION_LABEL_KEY);
		String[] nutritionMeasure = {"","g","mg","g","g","g"};
		float[] nutritionQuant = b.getFloatArray(NUTRITION_QUANT_KEY);
		String update;
		
		
		TextView tv;
		int[] ids = {R.id.textView1,R.id.textView2,R.id.textView3,R.id.textView4,R.id.textView5,R.id.textView6};		
		for(int i=0;i<ids.length;i++){
			tv = (TextView) findViewById(ids[i]);
			update = nutritionValues[i] + " " + Float.toString(nutritionQuant[i]) + nutritionMeasure[i];
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
