package edu.sfsu.cs.orange.ocr;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Typeface;
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
	static float h;
	static float a;
	static int offset;
	static float af;
	static float calories;
	static boolean ttt;
	static int ht,wt; //stores type of units 0 - SI 1 - imperial
	TextView caloriesValue;
	SeekBar seekBar,seekBar2,seekBar3,seekBar4;
	TextView seekBarValue,seekBarValue2,seekBarValue3;
	TextView aunit,wunit,hunit; //seekbarvalueTypes
	static SharedPreferences sharedPref;
	static SharedPreferences.Editor editor;
	//Get resources
	private Resources res;
	private Typeface arialblack;
	private Typeface arial;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);							
		caloriesValue = (TextView)findViewById(R.id.Calories);					
		
		arialblack=Typeface.createFromAsset(getAssets(), "fonts/arialblack.ttf");
		arial=Typeface.createFromAsset(getAssets(), "fonts/arial.ttf");
				
		
		TextView sett = (TextView) findViewById(R.id.Settings);
		sett.setTypeface(arial);
		TextView calInfo = (TextView) findViewById(R.id.caloriesInfo);
		calInfo.setTypeface(arial);
		hunit = (TextView) findViewById(R.id.heightUnit);
		hunit.setTypeface(arial);
		wunit = (TextView) findViewById(R.id.weightUnit);
		wunit.setTypeface(arial);
		aunit = (TextView) findViewById(R.id.ageUnit);
		aunit.setTypeface(arial);
		
		// set up preferences file
		sharedPref = this.getSharedPreferences("OCRSettingsPreferences",Context.MODE_PRIVATE);
		editor = sharedPref.edit();
		w = sharedPref.getInt("weight", 64);
		h = sharedPref.getFloat("height", 170.0f);
		a = sharedPref.getFloat("age", 30.0f);
		offset = sharedPref.getInt("offset", 5);
		af = sharedPref.getFloat("af", 1.375f);
		ttt = sharedPref.getBoolean("toggle", false);
		ht = sharedPref.getInt("heightType", 0);		
		wt = sharedPref.getInt("weightType",0);
		
		// initialize the settings page
		seekBar = (SeekBar)findViewById(R.id.heightBar);		
		seekBar.setProgress((int)(h - 100f));
		seekBar2 = (SeekBar)findViewById(R.id.weightBar);
		seekBar2.setProgress(w/2);
		seekBar3 = (SeekBar)findViewById(R.id.ageBar);
		seekBar3.setProgress((int)(a));
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
        button.setTypeface(arial);
		
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
        seekBarValue.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ht = (ht+1)%2;                
                updateCalories(); 
                if (ht == 0){			
        	        seekBarValue.setText(String.valueOf((int)h));	        
        	        hunit.setText("cm");	        		
        		}
        		else{
        			seekBarValue.setText(String.valueOf((int)(h*0.393701)));
        			hunit.setText("in");
        		}
            }
        });
        if (ht == 0){			
	        seekBarValue.setText(String.valueOf((int)h));	        
	        hunit.setText("cm");	        		
		}
		else{
			seekBarValue.setText(String.valueOf((int)(h*0.393701)));
			hunit.setText("in");
		}        
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){ 
		   @Override 
		   public void onProgressChanged(SeekBar seekBar, int progress, 
		     boolean fromUser) { 
		    // TODO Auto-generated method stub 
			float val = progress + 100f;		    
		    h = val;
		    if (ht == 0){			
    	        seekBarValue.setText(String.valueOf((int)h));	        
    	        hunit.setText("cm");	        		
    		}
    		else{
    			seekBarValue.setText(String.valueOf((int)(h*0.393701)));
    			hunit.setText("in");
    		}
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
        seekBarValue2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                wt = (wt+1)%2;                
                updateCalories(); 
                if (wt == 0){			
        			seekBarValue2.setText(String.valueOf(w));
        	        wunit.setText("kg");	        		
        		}
        		else{
        			seekBarValue2.setText(String.valueOf((int)(w*2.2)));
        			wunit.setText("lb");
        		} 
            }
        }); 
        if (wt == 0){			
			seekBarValue2.setText(String.valueOf(w));
	        wunit.setText("kg");	        		
		}
		else{
			seekBarValue2.setText(String.valueOf((int)(w*2.2)));
			wunit.setText("lb");
		}
        seekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){ 
     		   @Override 
     		   public void onProgressChanged(SeekBar seekBar, int progress, 
     		     boolean fromUser) { 
     		    // TODO Auto-generated method stub 
     			int val = progress*2;     		     
     		    w = val;
     		    if (wt == 0){			
     				seekBarValue2.setText(String.valueOf(w));
     		        wunit.setText("kg");	        		
     			}
     			else{
     				seekBarValue2.setText(String.valueOf((int)(w*2.2)));
     				wunit.setText("lb");
     			} 
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
      			float val = progress;
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
        
        caloriesValue.setTypeface(arial);
		seekBarValue.setTypeface(arial);
		seekBarValue2.setTypeface(arial);
		seekBarValue3.setTypeface(arial);
		
	}
			
	public void updateCalories(){
		calories = af*(10*w + 6.25f*h - 5*a + offset);
		caloriesValue.setText(String.valueOf((int)calories));
		editor.putInt("weight", w);
		editor.putFloat("height", h);
		editor.putFloat("age", a);
		editor.putFloat("af", af);
		editor.putInt("offset", offset);
		editor.putBoolean("toggle", ttt);
		editor.putFloat("calories", calories);
		editor.putInt("heightType", ht);
		editor.putInt("weightType",wt);		
		editor.commit();
	}
	
	public void setDefault(){
		w = 64;
		h = 170f;
		a = 30;
		offset = 5;
		af = 1.375f;
		ttt = false;
		ToggleButton toggle = (ToggleButton) findViewById(R.id.maleFemale);
		toggle.setChecked(ttt);
		seekBar = (SeekBar)findViewById(R.id.heightBar);		
		seekBar.setProgress((int)(h - 100f));
		seekBar2 = (SeekBar)findViewById(R.id.weightBar);
		seekBar2.setProgress(w/2);
		seekBar3 = (SeekBar)findViewById(R.id.ageBar);
		seekBar3.setProgress((int)(a));
		seekBar4 = (SeekBar)findViewById(R.id.sedentaryBar);
		seekBar4.setProgress((int)(((af-1.2f)/0.7f)*100f));
		
		seekBarValue = (TextView)findViewById(R.id.textHeight);		
		if (ht == 0){			
	        seekBarValue.setText(String.valueOf((int)h));	        
	        hunit.setText("cm");	        		
		}
		else{
			seekBarValue.setText(String.valueOf((int)(h*0.393701)));
			hunit.setText("in");
		}         
		seekBarValue2 = (TextView)findViewById(R.id.textWeight);
		if (wt == 0){			
			seekBarValue2.setText(String.valueOf(w));
	        wunit.setText("kg");	        		
		}
		else{
			seekBarValue2.setText(String.valueOf((int)(w*2.2)));
			wunit.setText("lb");
		}        
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
