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
import android.widget.ImageView;
import android.widget.TextView;

public class RecommenderActivity extends FragmentActivity {

	SectionsPagerAdapter mSectionsPagerAdapter;
	ViewPager mViewPager;

	public final static String NUTRITION_QUANT_KEY = "nutritionQuant";

	private float[] nutrition_values;
	private final static float[] conversion = { (float) 1, (float) 1,
			(float) 1000, (float) 1000, (float) 1000 };
	private final static String[] queries = { "Calories", "Fat", "Cholesterol",
			"Sodium", "Carbohydrate" };
	private final static String[] units = { " Calories", "g", "mg", "mg", "mg" };

	// database
	class ComparisonData {
		public String label;
		public float value;
		public int imageID;
		
		public ComparisonData(String label, float value, int imageID) {
			this.label = label;
			this.value = value;
			this.imageID = imageID;
		}
	}

	private static List<ComparisonData[]> dataList = new ArrayList<ComparisonData[]>();
	// resources
	private static Typeface arialblack;
	private static Typeface arial;
	private Fragment[] allFragments = new Fragment[5];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recommender_activity);

		// grab typeface from resource
		arialblack = Typeface.createFromAsset(getAssets(),
				"fonts/arialblack.ttf");
		arial = Typeface.createFromAsset(getAssets(), "fonts/arial.ttf");

		if (getIntent().getExtras() != null) {
			nutrition_values = (getIntent().getExtras())
					.getFloatArray(NUTRITION_QUANT_KEY);
		}
		// loag fragments
		loadFragments();

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

		// create database
		createDataList();

	}

	// create database
	public void createDataList() {
		ComparisonData[] cal = new ComparisonData[15];
		cal[0] = new ComparisonData("One blueberry", 1, R.drawable.calories_001);
		cal[1] = new ComparisonData("Three cherries", 10,
				R.drawable.calories_010);
		cal[2] = new ComparisonData("One carrot", 35, R.drawable.calories_035);
		cal[3] = new ComparisonData("One box of raisin", 45,
				R.drawable.calories_045);
		cal[4] = new ComparisonData("One cup of pistachios", 80,
				R.drawable.calories_080);
		cal[5] = new ComparisonData("One bowl of popcorn", 150,
				R.drawable.calories_150);
		cal[6] = new ComparisonData("Handful of fries", 225,
				R.drawable.calories_225);
		cal[7] = new ComparisonData("One small cup of sundae", 325,
				R.drawable.calories_325);
		cal[8] = new ComparisonData("Handful of onion rings", 340,
				R.drawable.calories_340);
		cal[9] = new ComparisonData("One cupcake", 460, R.drawable.calories_460);
		cal[10] = new ComparisonData("Two clices of pizza", 530,
				R.drawable.calories_530);
		cal[11] = new ComparisonData("Fried chicken burger", 632,
				R.drawable.calories_632);
		cal[12] = new ComparisonData("Seven pancakes", 750,
				R.drawable.calories_750);
		cal[13] = new ComparisonData("Triple cheeseburger", 1120,
				R.drawable.calories_1120);
		cal[14] = new ComparisonData("Mcdonald Big Breakfast", 1350,
				R.drawable.calories_1350);
		dataList.add(cal);
		ComparisonData[] fat = new ComparisonData[10];
		fat[0] = new ComparisonData("One apple", 0, R.drawable.fat_000g);
		fat[1] = new ComparisonData("Eight pretzel biscuits", 1, R.drawable.fat_001g);
		fat[2] = new ComparisonData("One granola bar", 2, R.drawable.fat_002g);
		fat[3] = new ComparisonData("A bag of baked potato crisps", 3, R.drawable.fat_003g);
		fat[4] = new ComparisonData("One icecream cone", 4, R.drawable.fat_004g);
		fat[5] = new ComparisonData("A handful of chips", 9, R.drawable.fat_009g);
		fat[6] = new ComparisonData("One bacon hamburger", 14, R.drawable.fat_014g);
		fat[7] = new ComparisonData("Small fries", 25, R.drawable.fat_025g);
		fat[8] = new ComparisonData("A large chunk of cheese", 33, R.drawable.fat_033g);
		fat[9] = new ComparisonData("A bar of butter", 81, R.drawable.fat_081g);
		dataList.add(fat);
		ComparisonData[] chole = new ComparisonData[11];
		chole[0] = new ComparisonData("One plate of fruits & veggies", 0,
				R.drawable.cholesterol_000mg);
		chole[1] = new ComparisonData("One oyster", 4,
				R.drawable.cholesterol_004mg);
		chole[2] = new ComparisonData("One box of milk", 21,
				R.drawable.cholesterol_021mg);
		chole[3] = new ComparisonData("One slice of cheese", 46,
				R.drawable.cholesterol_046mg);
		chole[4] = new ComparisonData("Porkchop", 65,
				R.drawable.cholesterol_065mg);
		chole[5] = new ComparisonData("BBQ ribs", 84,
				R.drawable.cholesterol_084mg);
		chole[6] = new ComparisonData("Double cheeseburger", 103,
				R.drawable.cholesterol_103mg);
		chole[7] = new ComparisonData("Four chicken fingers", 124,
				R.drawable.cholesterol_124mg);
		chole[8] = new ComparisonData("One egg", 216,
				R.drawable.cholesterol_216mg);
		chole[9] = new ComparisonData("One McMuffin", 234,
				R.drawable.cholesterol_234mg);
		chole[10] = new ComparisonData("Fried eggs", 367,
				R.drawable.cholesterol_367mg);
		dataList.add(chole);
		ComparisonData[] sodium = new ComparisonData[8];
		sodium[0] = new ComparisonData("One banana", 1, R.drawable.sodium_001mg);
		sodium[1] = new ComparisonData("Handful of Grapes", 5,
				R.drawable.sodium_005mg);
		sodium[2] = new ComparisonData("One Oreo cookie", 80,
				R.drawable.sodium_080mg);
		sodium[3] = new ComparisonData("A handful of almond", 119,
				R.drawable.sodium_119mg);
		sodium[4] = new ComparisonData("Ham", 150, R.drawable.sodium_150mg);
		sodium[5] = new ComparisonData("Two Toast", 360,
				R.drawable.sodium_360mg);
		sodium[6] = new ComparisonData("6-inch Sandwitch", 529,
				R.drawable.sodium_529mg);
		sodium[7] = new ComparisonData("Bacon", 1460, R.drawable.sodium_1460mg);
		dataList.add(sodium);
		ComparisonData[] carb = new ComparisonData[4];
		carb[0] = new ComparisonData("carbo 1", 100, R.drawable.active);
		carb[1] = new ComparisonData("carbo 2", 200, R.drawable.active);
		carb[2] = new ComparisonData("carbo 3", 300, R.drawable.active);
		carb[3] = new ComparisonData("carbo 4", 400, R.drawable.active);
		dataList.add(carb);
	}

	// pre-load all fragment to memory
	public void loadFragments() {
		for (int i = 0; i < allFragments.length; i++) {
			allFragments[i] = RecommenderViewFragment.newInstance(i,
					(float) (nutrition_values[i] * conversion[i]));
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
			return allFragments[position];
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

			/*
			 * comparison_text.setText("This food is equivalent to " +
			 * String.format("%.2f", value) + " " + comparison_target[category]
			 * + " in terms of " + queries[category]);
			 */

			// compute closest food for this category
			ComparisonData[] thisCategory = dataList.get(category);
			float minVal = Math.abs(thisCategory[0].value - value);
			int minIndex = 0;
			for (int i = 1; i < thisCategory.length; i++) {
				if (Math.abs(thisCategory[i].value - value) < minVal) {
					minVal = Math.abs(thisCategory[i].value - value);
					minIndex = i;
				}
			}
			String targetLabel = thisCategory[minIndex].label;
			int targetImageID = thisCategory[minIndex].imageID;

			// set comparison text
			TextView comparison_text = (TextView) rootView
					.findViewById(R.id.comparison_result_text);
			comparison_text.setText("Similar food in terms of "
					+ queries[category] + " is " + targetLabel);
			comparison_text.setTypeface(arial);

			// set measurement text
			TextView measurement_text = (TextView) rootView
					.findViewById(R.id.comparison_result_measurement);
			if (category == 0) {
				measurement_text.setText(value + units[category]);
			} else {
				measurement_text.setText(value + units[category] + " of "
						+ queries[category]);
			}

			measurement_text.setTypeface(arial);

			// set image
			ImageView comparison_image = (ImageView) rootView
					.findViewById(R.id.comparison_result_image);
			comparison_image.setImageDrawable(getResources().getDrawable(
					targetImageID));

			return rootView;
		}

		@Override
		public void onPause() {
			super.onPause();
		}

	}

}
