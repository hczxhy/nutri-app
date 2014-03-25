package edu.sfsu.cs.orange.ocr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.xmlpull.v1.XmlPullParserException;
import net.simonvt.numberpicker.NumberPicker;
import android.app.Activity;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class RecommenderActivity extends Activity implements
		NumberPicker.OnValueChangeListener {

	public final static String NUTRITION_QUANT_KEY = "nutritionQuant";

	private float[] nutrition_values;
	private final static float[] conversion = { (float) 1, (float) 1,
			(float) 1000, (float) 1000, (float) 1 };
	private final static String[] queries = { "Calories", "Fat", "Cholesterol",
			"Sodium", "Carbohydrate" };
	private final static String[] units = { " Calories", "g", "mg", "mg", "g" };
	private Drawable[] allDrawables = new Drawable[5];
	private String[] allComparisonText = new String[5];
	private String[] allMeasurementText = new String[5];

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

	// view setup
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Resources res = getResources();
		setContentView(R.layout.recommender_fragment);

		// grab typeface from resource
		arialblack = Typeface.createFromAsset(getAssets(),
				"fonts/arialblack.ttf");
		arial = Typeface.createFromAsset(getAssets(), "fonts/arial.ttf");

		if (getIntent().getExtras() != null) {
			nutrition_values = (getIntent().getExtras())
					.getFloatArray(NUTRITION_QUANT_KEY);
		}
		// scroll wheel setup
		NumberPicker category_picker;
		Drawable divider = null;
		try {
			divider = Drawable.createFromXml(res,
					res.getXml(R.drawable.divider));
		} catch (NotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		category_picker = (NumberPicker) findViewById(R.id.comparison_category_picker);
		category_picker.setMinValue(0);
		category_picker.setMaxValue(4);
		category_picker.setValue(0);
		category_picker.setDividerDrawable(divider);
		category_picker.setFocusable(true);
		category_picker.setFocusableInTouchMode(true);
		category_picker.setDisplayedValues(queries);
		category_picker.setOnValueChangedListener(this);
		category_picker.setWrapSelectorWheel(false);
		// create database
		createDataList();
		
		// pre-load drawable and strings
		loadAllComparisonData();
		
		// initial default view
		updatePicture(0);
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
		fat[1] = new ComparisonData("Eight pretzel biscuits", 1,
				R.drawable.fat_001g);
		fat[2] = new ComparisonData("One granola bar", 2, R.drawable.fat_002g);
		fat[3] = new ComparisonData("A bag of baked potato crisps", 3,
				R.drawable.fat_003g);
		fat[4] = new ComparisonData("One icecream cone", 4, R.drawable.fat_004g);
		fat[5] = new ComparisonData("A handful of chips", 9,
				R.drawable.fat_009g);
		fat[6] = new ComparisonData("One bacon hamburger", 14,
				R.drawable.fat_014g);
		fat[7] = new ComparisonData("Small fries", 25, R.drawable.fat_025g);
		fat[8] = new ComparisonData("A large chunk of cheese", 33,
				R.drawable.fat_033g);
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
		ComparisonData[] carb = new ComparisonData[9];
		carb[0] = new ComparisonData("A cup of coffee", 0, R.drawable.carbohydrates_000g);
		carb[1] = new ComparisonData("A chunk of cheese", 1, R.drawable.carbohydrates_001g);
		carb[2] = new ComparisonData("Three celery sticks", 3, R.drawable.carbohydrates_003g);
		carb[3] = new ComparisonData("A glass of milk", 5, R.drawable.carbohydrates_005g);
		carb[4] = new ComparisonData("One orange", 12, R.drawable.carbohydrates_012g);
		carb[5] = new ComparisonData("A handful of peanuts", 16, R.drawable.carbohydrates_016g);
		carb[6] = new ComparisonData("A bowl of cereal", 20, R.drawable.carbohydrates_020g);
		carb[7] = new ComparisonData("A bowl of mac & cheese", 46, R.drawable.carbohydrates_046g);
		carb[8] = new ComparisonData("One cupcake", 48, R.drawable.carbohydrates_048g);
		dataList.add(carb);
	}

	// make sure database is closed
	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	public void loadAllComparisonData() {
		// do for all categories
		for (int category = 0; category < allDrawables.length; category++) {
			// compute closest food for this category
			ComparisonData[] thisCategory = dataList.get(category);
			float value = nutrition_values[category] * conversion[category];
			float minVal = Math.abs(thisCategory[0].value - value);
			int minIndex = 0;
			// find closest label for this category
			for (int i = 1; i < thisCategory.length; i++) {
				if (Math.abs(thisCategory[i].value - value) < minVal) {
					minVal = Math.abs(thisCategory[i].value - value);
					minIndex = i;
				}
			}
			String targetLabel = thisCategory[minIndex].label;
			String targetMeasurementText = "";
			if (category == 0) {
				targetMeasurementText = "" + value + units[category];
			} else {
				targetMeasurementText = "" + value + units[category] + " of "
						+ queries[category];
			}
			allDrawables[category] = getResources().getDrawable(
					thisCategory[minIndex].imageID);
			allComparisonText[category] = "Similar food in terms of "
					+ queries[category] + " is " + targetLabel;
			allMeasurementText[category] = targetMeasurementText;
		}

	}

	public void updatePicture(int category) {

		// set comparison text
		TextView comparison_text = (TextView) findViewById(R.id.comparison_result_text);
		comparison_text.setText(allComparisonText[category]);
		comparison_text.setTypeface(arial);

		// set measurement text
		TextView measurement_text = (TextView) findViewById(R.id.comparison_result_measurement);
		measurement_text.setText(allMeasurementText[category]);
		measurement_text.setTypeface(arial);

		// set image
		ImageView comparison_image = (ImageView) findViewById(R.id.comparison_result_image);
		comparison_image.setImageDrawable(allDrawables[category]);
	}

	@Override
	public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
		// TODO Auto-generated method stub
		updatePicture(newVal);
	}

}
