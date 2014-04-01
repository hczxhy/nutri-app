package edu.sfsu.cs.orange.ocr;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class LabelListCustomAdapter extends ArrayAdapter<String> {
	private final Context mContext;
	private final String[] mNames;
	private final String[] mTypes;
	private final String[] mCosts;

	public LabelListCustomAdapter(Context context, String[] names, String[] types, String[] costs) {
		super(context, R.layout.list_item, names);
		this.mContext=context;
		this.mNames=names;
		this.mTypes=types;
		this.mCosts=costs;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Typeface arial=Typeface.createFromAsset(getContext().getAssets(), "fonts/arial.ttf");
		Typeface arialblack=Typeface.createFromAsset(getContext().getAssets(), "fonts/arialblack.ttf");
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.list_item, parent, false);
		TextView food_name = (TextView) rowView.findViewById(R.id.food_item);
		TextView food_type = (TextView) rowView.findViewById(R.id.food_label);
		TextView food_cost = (TextView) rowView.findViewById(R.id.food_cost);
		
		food_name.setTypeface(arialblack);
		food_type.setTypeface(arial);
		food_cost.setTypeface(arial);
		
		food_name.setText(mNames[position]);
		System.out.println("*"+mTypes[position]+"*");
		System.out.println("*"+mCosts[position]+"*");
		if(mTypes[position].length()==0){
			food_type.setText("No Description");
		}else{
			food_type.setText("Description: "+mTypes[position]);
		}
		if(mCosts[position].length()==0){
			food_cost.setText("No Cost");
		}else{
			food_cost.setText("Cost: $" + mCosts[position]);
		}

		return rowView;
	}
}
