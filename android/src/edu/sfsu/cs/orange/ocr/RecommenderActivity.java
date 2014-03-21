package edu.sfsu.cs.orange.ocr;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

public class RecommenderActivity extends FragmentActivity {

	SectionsPagerAdapter mSectionsPagerAdapter;
	ViewPager mViewPager;

	public final static String NUTRITION_QUANT_KEY = "nutritionQuant";

	// private MyDatabaseHelper db = null;
	private float[] nutrition_values;
	private final static float[] conversion = { (float) 89, (float) 14,
			(float) 0.373, (float) 2.325, (float) 1.0 };
	private final static String[] comparison_target = { "Banana",
			"Cheeseburger", "Egg", "Teaspoon of Salt", "blah blah CARB" };
	private final static String[] queries = { "Calories", "Fat", "Cholesterol",
			"Sodium", "Carbohydrate" };
	
	// resources
	private static Typeface arialblack;
	private static Typeface arial;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recommender_activity);

		
		// grab typeface from resource
		arialblack=Typeface.createFromAsset(getAssets(), "fonts/arialblack.ttf");
		arial=Typeface.createFromAsset(getAssets(), "fonts/arial.ttf");


		if (getIntent().getExtras() != null) {
			nutrition_values = (getIntent().getExtras())
					.getFloatArray(NUTRITION_QUANT_KEY);
		}

		// Set up the ViewPager with the sections adapter.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		
		// set title font 
		PagerTitleStrip _Title = (PagerTitleStrip) findViewById(R.id.pager_title_strip);
		for (int counter = 0; counter < _Title.getChildCount(); counter++) {

			if (_Title.getChildAt(counter) instanceof TextView) {
				((TextView) _Title.getChildAt(counter)).setTypeface(arialblack);
				((TextView) _Title.getChildAt(counter)).setTextSize(25);
			}

		}

	}

	// make sure database is closed
	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	// swipe tab inner-class
	public class SectionsPagerAdapter extends FragmentPagerAdapter {
		private final int COUNT = 5;

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {

			Fragment frag;
			// int ind = Math.min(position,conversion.length-1);
			// frag =
			// RecommenderViewFragment.newInstance(position,nutrition_values[position]/conversion[position]);
			frag = RecommenderViewFragment
					.newInstance(
							position,
							(float) (nutrition_values[position] / conversion[position]));

			return frag;
		}

		@Override
		public int getCount() {

			return this.COUNT;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return queries[position];
		}
	}

	public void done(View view) {
		finish();
	}

	// my static fragment
	public static class RecommenderViewFragment extends Fragment {

		public static final String ARG_QUERY_LABEL = "category";
		public static final String ARG_QUERY_VALUE = "nutrition_value";

		public RecommenderViewFragment() {
		}

		// new instantiation of fragment
		public static RecommenderViewFragment newInstance(int query, float value) {
			RecommenderViewFragment frag = new RecommenderViewFragment();

			// argument passing to fragment (String query)
			Bundle bundle = new Bundle();
			bundle.putInt(ARG_QUERY_LABEL, query);
			bundle.putFloat(ARG_QUERY_VALUE, value);

			frag.setArguments(bundle);

			return frag;
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {

			int category = getArguments().getInt(ARG_QUERY_LABEL);
			float value = getArguments().getFloat(ARG_QUERY_VALUE);

			View rootView = inflater.inflate(R.layout.recommender_fragment,
					container, false);
			TextView comparison_text = (TextView) rootView
					.findViewById(R.id.comparison_result_text);

			comparison_text.setText("This food is equivalent to "
					+ String.format("%.2f", value) + " "
					+ comparison_target[category] + " in terms of "
					+ queries[category]);

			comparison_text.setTypeface(arial);
			
			return rootView;
		}

		@Override
		public void onPause() {
			super.onPause();
		}

	}

}
