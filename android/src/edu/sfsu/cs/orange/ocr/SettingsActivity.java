package edu.sfsu.cs.orange.ocr;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

public class SettingsActivity extends Activity {

	static int w;
	static int h;
	static float a;
	static int offset;
	static float af;
	static float calories;
	static boolean ttt;
	TextView caloriesValue;
	SeekBar seekBar,seekBar2,seekBar3,seekBar4;
	TextView seekBarValue,seekBarValue2,seekBarValue3;
	static SharedPreferences sharedPref;
	static SharedPreferences.Editor editor;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);							
		caloriesValue = (TextView)findViewById(R.id.Calories);					
		
		// set up preferences file
		sharedPref = this.getSharedPreferences("OCRSettingsPreferences",Context.MODE_PRIVATE);
		editor = sharedPref.edit();
		w = sharedPref.getInt("weight", 64);
		h = sharedPref.getInt("height", 170);
		a = sharedPref.getFloat("age", 30.0f);
		offset = sharedPref.getInt("offset", 5);
		af = sharedPref.getFloat("af", 1.375f);
		ttt = sharedPref.getBoolean("toggle", false);
		
		// initialize the settings page
		seekBar = (SeekBar)findViewById(R.id.heightBar);		
		seekBar.setProgress(h/3);
		seekBar2 = (SeekBar)findViewById(R.id.weightBar);
		seekBar2.setProgress(w/2);
		seekBar3 = (SeekBar)findViewById(R.id.ageBar);
		seekBar3.setProgress((int)(a/1.5f));
		seekBar4 = (SeekBar)findViewById(R.id.sedentaryBar);
		seekBar4.setProgress((int)(((af-1.2f)/0.7f)*100f));
		updateCalories();				
		
		// reset if default button is clicked
		Button button = (Button) findViewById(R.id.button_default);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setDefault();
                setDefault();
            }
        });
		
		// adjust male female
		ToggleButton toggle = (ToggleButton) findViewById(R.id.maleFemale);
		toggle.setChecked(ttt);
		toggle.setOnCheckedChangeListener(new OnCheckedChangeListener() {
		@Override
		    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		                if (isChecked){
		                	offset = -161; //checked is female
		                	ttt = isChecked;
		                	updateCalories();
		                }
		                else{
		                	offset = 5;
		                	ttt = isChecked;
		                	updateCalories();
		                }
		            }
        });
		
		// adjust height bar		 
        seekBarValue = (TextView)findViewById(R.id.textHeight);
        seekBarValue.setText(String.valueOf(h));
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){ 
		   @Override 
		   public void onProgressChanged(SeekBar seekBar, int progress, 
		     boolean fromUser) { 
		    // TODO Auto-generated method stub 
			int val = progress*3;
		    seekBarValue.setText(String.valueOf(val));
		    h = val;		    
		    updateCalories();
		   } 	
		   @Override 
		   public void onStartTrackingTouch(SeekBar seekBar) { 
		    // TODO Auto-generated method stub 
		   } 	
		   @Override 
		   public void onStopTrackingTouch(SeekBar seekBar) { 
		    // TODO Auto-generated method stub 
		   } 
       }); 
        
        // adjust weight bar 		
        seekBarValue2 = (TextView)findViewById(R.id.textWeight); 
        seekBarValue2.setText(String.valueOf(w));
        seekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){ 
     		   @Override 
     		   public void onProgressChanged(SeekBar seekBar, int progress, 
     		     boolean fromUser) { 
     		    // TODO Auto-generated method stub 
     			int val = progress*2;
     		    seekBarValue2.setText(String.valueOf(val)); 
     		    w = val;
     		    updateCalories();
     		   } 	
     		   @Override 
     		   public void onStartTrackingTouch(SeekBar seekBar) { 
     		    // TODO Auto-generated method stub 
     		   } 	
     		   @Override 
     		   public void onStopTrackingTouch(SeekBar seekBar) { 
     		    // TODO Auto-generated method stub 
     		   } 
        });
        
        // adjust age bar  		
        seekBarValue3 = (TextView)findViewById(R.id.textAge); 
        seekBarValue3.setText(String.valueOf((int)a));
        seekBar3.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){ 
      		   @Override 
      		   public void onProgressChanged(SeekBar seekBar, int progress, 
      		     boolean fromUser) { 
      		    // TODO Auto-generated method stub 
      			float val = progress*1.5f;
      		    seekBarValue3.setText(String.valueOf((int)val)); 
      		    a = val;
      		    updateCalories();
      		   } 	
      		   @Override 
      		   public void onStartTrackingTouch(SeekBar seekBar) { 
      		    // TODO Auto-generated method stub 
      		   } 	
      		   @Override 
      		   public void onStopTrackingTouch(SeekBar seekBar) { 
      		    // TODO Auto-generated method stub 
      		   } 
        });
        
        // adjust sedentary bar  		              
        seekBar4.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){ 
      		   @Override 
      		   public void onProgressChanged(SeekBar seekBar, int progress, 
      		     boolean fromUser) { 
      		    // TODO Auto-generated method stub
      			   af = 1.2f + (progress/100f)*0.7f;
      			   updateCalories();
      		   } 	
      		   @Override 
      		   public void onStartTrackingTouch(SeekBar seekBar) { 
      		    // TODO Auto-generated method stub 
      		   } 	
      		   @Override 
      		   public void onStopTrackingTouch(SeekBar seekBar) { 
      		    // TODO Auto-generated method stub 
      		   } 
        });
        
	   
		
	}
			
	public void updateCalories(){
		calories = af*(10*w + 6.25f*h - 5*a + offset);
		caloriesValue.setText(String.valueOf((int)calories));
		editor.putInt("weight", w);
		editor.putInt("height", h);
		editor.putFloat("age", a);
		editor.putFloat("af", af);
		editor.putInt("offset", offset);
		editor.putBoolean("toggle", ttt);
		editor.putFloat("calories", calories);
		editor.commit();
	}
	
	public void setDefault(){
		w = 64;
		h = 170;
		a = 30;
		offset = 5;
		af = 1.375f;
		ttt = false;
		ToggleButton toggle = (ToggleButton) findViewById(R.id.maleFemale);
		toggle.setChecked(ttt);
		seekBar = (SeekBar)findViewById(R.id.heightBar);		
		seekBar.setProgress(h/3);
		seekBar2 = (SeekBar)findViewById(R.id.weightBar);
		seekBar2.setProgress(w/2);
		seekBar3 = (SeekBar)findViewById(R.id.ageBar);
		seekBar3.setProgress((int)(a/1.5f));
		seekBar4 = (SeekBar)findViewById(R.id.sedentaryBar);
		seekBar4.setProgress((int)(((af-1.2f)/0.7f)*100f));
		
		seekBarValue = (TextView)findViewById(R.id.textHeight);
        seekBarValue.setText(String.valueOf(h));
        seekBarValue2 = (TextView)findViewById(R.id.textWeight); 
        seekBarValue2.setText(String.valueOf(w));
        seekBarValue3 = (TextView)findViewById(R.id.textAge); 
        seekBarValue3.setText(String.valueOf((int)a));
        
        updateCalories();
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.results, menu);
		return true;
	}

}
