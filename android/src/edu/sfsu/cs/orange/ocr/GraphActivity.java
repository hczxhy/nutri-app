package edu.sfsu.cs.orange.ocr;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.simonvt.numberpicker.NumberPicker;

import org.xmlpull.v1.XmlPullParserException;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.todddavies.components.progressbar.ProgressWheel;

public class GraphActivity extends FragmentActivity implements NumberPicker.OnValueChangeListener{
	
	ExecutorService mExecServ;
	//Threshold values and other useful values
	public static final double GREEN_THRESH=0;
	public static final double YELLOW_THRESH=35;
	public static final double RED_THRESH=70;
	public static final int ANIM_SPEED=20;
	public static final float APPLE_WEIGHT=80;
	public static final String NUTRITION_LABEL_KEY = "nutritionValues";
	public static final String NUTRITION_QUANT_KEY = "nutritionQuant";
	public static final String LABEL_NAME="labelName";
	//Recommended daily nutrition values
	private float rec_fat=0;
	private float rec_car=0;
	private float rec_cal=0;
	private float rec_sod=0;
	private float rec_cho=0;
	//Percent and original values of fat, sodium, carbs, and cholesterol
	private float[] init_nums={0,0,0,0,0};			//In order: calories,fat,cholesterol,sodium,carbohydrates
	private float fat_num=0;
	private int fat_per=0;
	private float cho_num=0;
	private int cho_per=0;
	private float sod_num=0;
	private int sod_per=0;
	private float car_num=0;
	private int car_per=0;
	private float cal_num=0;
	//Drawables to be used in the progress bar
	private Drawable fat_green_bar=null;
	private Drawable fat_yellow_bar=null;
	private Drawable fat_red_bar=null;
	private Drawable car_green_bar=null;
	private Drawable car_yellow_bar=null;
	private Drawable car_red_bar=null;
	private Drawable sod_green_bar=null;
	private Drawable sod_yellow_bar=null;
	private Drawable sod_red_bar=null;
	private Drawable cho_green_bar=null;
	private Drawable cho_yellow_bar=null;
	private Drawable cho_red_bar=null;
	private Drawable divider=null;
	//Find views to be animated
	private TextAnimationView fat_text_anim;
	private TextAnimationView cho_text_anim;
	private TextAnimationView sod_text_anim;
	private TextAnimationView car_text_anim;
	private ProgressBar fat_pb;
	private ProgressBar cho_pb;
	private ProgressBar sod_pb;
	private ProgressBar car_pb;
	private ProgressWheel cal_pw;
	private NumberPicker serv_size_picker;
	//Get resources
	private Resources res;
	private Typeface arialblack;
	private Typeface arial;
	//Intermediate variables and states
	static long prev_scroll_time=0;
	static double f1=0;
	static double f2=0;
	static double f3=0;
	static double f4=0;
	static double f5=0;
	double cal_curr_color=GREEN_THRESH;
	double fat_curr_color=GREEN_THRESH;
	double car_curr_color=GREEN_THRESH;
	double sod_curr_color=GREEN_THRESH;
	double cho_curr_color=GREEN_THRESH;
	double cal_prev_color=GREEN_THRESH;
	double fat_prev_color=GREEN_THRESH;
	double car_prev_color=GREEN_THRESH;
	double sod_prev_color=GREEN_THRESH;
	double cho_prev_color=GREEN_THRESH;
	boolean cal_finished=false;
	boolean fat_finished=false;
	boolean car_finished=false;
	boolean cho_finished=false;
	boolean sod_finished=false;
	long prevScrollTime;
	int fat_id=111;
	int car_id=222;
	int sod_id=333;
	int cho_id=444;
	int cal_id=555;
	private boolean fat_is_mg=false;  
	private boolean car_is_mg=false;
	private boolean sod_is_mg=false;
	private boolean cho_is_mg=false;
	public static String labelname;
	
	private Handler handler;
	
	public void setTypefaces(){
		arialblack=Typeface.createFromAsset(getAssets(), "fonts/arialblack.ttf");
		arial=Typeface.createFromAsset(getAssets(), "fonts/arial.ttf");

		TextView title=(TextView) findViewById(R.id.title);
		title.setTypeface(arialblack);
		((TextView) findViewById(R.id.cal_text)).setTypeface(arial);
		((TextView) findViewById(R.id.ser_text)).setTypeface(arial);
		((TextView) findViewById(R.id.fat_text)).setTypeface(arial);
		((TextView) findViewById(R.id.car_text)).setTypeface(arial);
		((TextView) findViewById(R.id.cho_text)).setTypeface(arial);
		((TextView) findViewById(R.id.sod_text)).setTypeface(arial);
		((Button) findViewById(R.id.save_button)).setTypeface(arial);
		((Button) findViewById(R.id.comparison_button)).setTypeface(arial);
		cal_pw.setTypefaces(arial);
		serv_size_picker.setTypefaces(arial);
	}
	
	public void set_fat(int value){
		fat_per=value;
	}
	public void set_carb(int value){
		car_per=value;
	}
	public void set_chol(int value){
		cho_per=value;
	}
	public void set_sod(int value){
		sod_per=value;
	}
	
	public void set_recommended_values(float calories, float fat, float carbohydrates, float cholesterol, float sodium){
		rec_cal=calories;
		rec_fat=fat;
		rec_car=carbohydrates;
		rec_cho=cholesterol;
		rec_sod=sodium;
	}
	
	public void change_title(String title){
		if(findViewById(R.id.title)!=null){
			((TextView) findViewById(R.id.title)).setText(title);
		}
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		res=getResources();
		setContentView(R.layout.graphs_page);
		mExecServ=Executors.newSingleThreadExecutor();
		
		try {
			//Use custom progress bar colors
			divider=Drawable.createFromXml(res, res.getXml(R.drawable.divider));
			//Give each progress bar its own set of color drawables
			fat_green_bar=Drawable.createFromXml(res, res.getXml(R.drawable.greenprogressbar));
			car_green_bar=Drawable.createFromXml(res, res.getXml(R.drawable.greenprogressbar));
			sod_green_bar=Drawable.createFromXml(res, res.getXml(R.drawable.greenprogressbar));
			cho_green_bar=Drawable.createFromXml(res, res.getXml(R.drawable.greenprogressbar));
			fat_yellow_bar=Drawable.createFromXml(res, res.getXml(R.drawable.yellowprogressbar));
			car_yellow_bar=Drawable.createFromXml(res, res.getXml(R.drawable.yellowprogressbar));
			sod_yellow_bar=Drawable.createFromXml(res, res.getXml(R.drawable.yellowprogressbar));
			cho_yellow_bar=Drawable.createFromXml(res, res.getXml(R.drawable.yellowprogressbar));
			fat_red_bar=Drawable.createFromXml(res, res.getXml(R.drawable.redprogressbar));
			car_red_bar=Drawable.createFromXml(res, res.getXml(R.drawable.redprogressbar));
			sod_red_bar=Drawable.createFromXml(res, res.getXml(R.drawable.redprogressbar));
			cho_red_bar=Drawable.createFromXml(res, res.getXml(R.drawable.redprogressbar));
		} catch (NotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (XmlPullParserException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		//Find fields on the layout for editing purposes
		car_pb= (ProgressBar) findViewById(R.id.car_pb);
		fat_pb= (ProgressBar) findViewById(R.id.fat_pb);
		cho_pb= (ProgressBar) findViewById(R.id.chol_pb);
		sod_pb= (ProgressBar) findViewById(R.id.sod_pb);
		fat_text_anim=(TextAnimationView) findViewById(R.id.fat_num);
		car_text_anim=(TextAnimationView) findViewById(R.id.car_num);
		cho_text_anim=(TextAnimationView) findViewById(R.id.chol_num);
		sod_text_anim=(TextAnimationView) findViewById(R.id.sod_num);
		cal_pw=(ProgressWheel) findViewById(R.id.cal_spinner);
		serv_size_picker=(NumberPicker) findViewById(R.id.serving_size_picker);
		serv_size_picker.setMinValue(1);
		serv_size_picker.setMaxValue(20);
		serv_size_picker.setDividerDrawable(divider);
		serv_size_picker.setFocusable(true);
		serv_size_picker.setFocusableInTouchMode(true);
		serv_size_picker.setOnValueChangedListener(this);
		serv_size_picker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		setTypefaces();
	}
	
	@Override
	public void onStart(){
		super.onStart();
		handler=new Handler();
		//Convert values to percentages ****Needs to be connected with the settings page*****
		float[] values={0,0,0,0,0,0,0};
		if(getIntent().getExtras()!=null){
			values=(getIntent().getExtras()).getFloatArray(NUTRITION_QUANT_KEY);
			labelname=(getIntent().getExtras()).getString(LABEL_NAME);
		}
		SharedPreferences sharedPref = this.getSharedPreferences("OCRSettingsPreferences", MODE_PRIVATE);
		float calval = sharedPref.getFloat("calories", 2014f); 
		float fat = calval*0.03f;
		float carb = calval*0.15f;
		float chol = 0.3f;
		float sodium = 2.4f;
		set_recommended_values(calval, fat, carb, chol, sodium);
		fat_num=values[1];
		car_num=values[4];
		cho_num=values[2];
		sod_num=values[3]; 
		cal_num=values[0];
		init_nums=values.clone();
		if(fat_num<1){
			fat_is_mg=true;
			fat_num=1000*fat_num;
			rec_fat=1000*rec_fat;
			init_nums[1]=1000*init_nums[1];
		}
		if(car_num<1){
			car_is_mg=true;
			car_num=1000*car_num;
			rec_car=1000*rec_car;
			init_nums[4]=1000*init_nums[4];
		}
		if(sod_num<1){
			sod_is_mg=true;
			sod_num=1000*sod_num;
			rec_sod=1000*rec_sod;
			init_nums[3]=1000*init_nums[3];
		}
		if(cho_num<1){
			cho_is_mg=true;
			cho_num=1000*cho_num;
			rec_cho=1000*rec_cho;
			init_nums[2]=1000*init_nums[2];
		}
		set_percentages();
		if(labelname!=null){
			((TextView) findViewById(R.id.title)).setText(labelname);
		}

		cal_pw.set_recommended_calories(rec_cal);

		run_visualization();
		
		//Comparison button
		Button comparisonButton = (Button)findViewById(R.id.comparison_button);
		comparisonButton.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v){
				Intent intent = new Intent(GraphActivity.this, RecommenderActivity.class);
				Bundle b = new Bundle();
				b.putFloatArray(RecommenderActivity.NUTRITION_QUANT_KEY, get_curr_vals());
				intent.putExtras(b);
				startActivity(intent);
			}
		});
		
		// Save button 
		Button saveButton = (Button)findViewById(R.id.save_button);
		saveButton.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v){
				saveItem(get_curr_vals());
			}
		});
	}

	@Override
	public void onResume(){
		super.onResume();
		run_visualization();
		
	}
	//Scale the nutritional values according to the current serving size
	public void scale_values(int scale_factor){
		cal_num=scale_factor*init_nums[0];
		car_num=scale_factor*init_nums[4];
		sod_num=scale_factor*init_nums[3];
		cho_num=scale_factor*init_nums[2];
		fat_num=scale_factor*init_nums[1];
		set_percentages();
	}
	//Set percentages of daily intake values
	public void set_percentages(){
		set_fat((int) Math.round(fat_num/rec_fat*100));
		set_carb((int) Math.round(car_num/rec_car*100));
		set_chol((int) Math.round(cho_num/rec_cho*100));
		set_sod((int) Math.round(sod_num/rec_sod*100));
	}
	
	public void reset_flags(){
		cal_finished=false;
		fat_finished=false;
		sod_finished=false;
		car_finished=false;
		cho_finished=false;
	}
	//Get the current values on the screen
	public float[] get_curr_vals(){
		float[] curr_vals={cal_num, fat_num, cho_num, sod_num, car_num};
		return curr_vals;
	}
	//Draw the next frame for the progress bar
	public double interpolate_one_frame(double init, int end, int id){
		if(id==cal_id){
			if((int)Math.round(init)<end){
				init=init+2*(Math.sqrt((double) end-init))/3;
			}else if ((int)Math.round(init)>end){
				init=init-2*(Math.sqrt(init-(double) end))/3;
			}else{
				cal_finished=true;
			}
		}else{
			if((int)Math.round(init)<end){
				init=init+(Math.sqrt((double) end-init))/4;
			}else if ((int)Math.round(init)>end){
				init=init-(Math.sqrt(init-(double) end))/4;
			}else{
				if(id==fat_id){
					fat_finished=true;
				}else if(id==car_id){
					car_finished=true;
				}else if(id==sod_id){
					sod_finished=true;
				}else if (id==cho_id){
					cho_finished=true;
				}
			}
		}
		return init;
	}
	
	public double check_color(double curr_frame){
		double curr_color;
		if(curr_frame<YELLOW_THRESH){
			curr_color=GREEN_THRESH;
		}else if(curr_frame>YELLOW_THRESH && curr_frame< RED_THRESH){
			curr_color=YELLOW_THRESH;
		}else{
			curr_color=RED_THRESH;
		}
		return curr_color;
	}
	public Drawable getBarColorDrawable(double curr_color, int id){
		if(curr_color==GREEN_THRESH){
			if(id==fat_id)
				return fat_green_bar;
			if(id==car_id)
				return car_green_bar;
			if(id==sod_id)
				return sod_green_bar;
			if(id==cho_id)
				return cho_green_bar;
		}else if(curr_color==YELLOW_THRESH){
			if(id==fat_id)
				return fat_yellow_bar;
			if(id==car_id)
				return car_yellow_bar;
			if(id==sod_id)
				return sod_yellow_bar;
			if(id==cho_id)
				return cho_yellow_bar;
		}else{
			if(id==fat_id)
				return fat_red_bar;
			if(id==car_id)
				return car_red_bar;
			if(id==sod_id)
				return sod_red_bar;
			if(id==cho_id)
				return cho_red_bar;
		}
		return fat_green_bar;
	}
	
	public void run_visualization(){
		//Create a thread to animate the progress bar
		mExecServ.execute(new Runnable() {
			@Override
			public void run() {
				System.out.println("Sod_num initially is "+Float.toString(sod_num));

				//System.out.println("Sod_num now is "+Float.toString(sod_num));
				while (!cal_finished || !fat_finished || !car_finished || !cho_finished || !sod_finished) {
					//Interpolate frames based on a parabolic acceleration curve for animation
					f1=interpolate_one_frame(f1,fat_per,fat_id);
					f2=interpolate_one_frame(f2,car_per,car_id);
					f3=interpolate_one_frame(f3,sod_per,sod_id);
					f4=interpolate_one_frame(f4,cho_per,cho_id);
					f5=interpolate_one_frame(f5,(int) Math.round(cal_num),cal_id);
					//Change any colors as necessary
					fat_curr_color=check_color(f1);
					car_curr_color=check_color(f2);
					sod_curr_color=check_color(f3);
					cho_curr_color=check_color(f4);
					cal_curr_color=check_color(f5);
					if(fat_curr_color!=fat_prev_color){
						runOnUiThread(new Runnable() {
			        	     @Override
			        	     public void run() {
			        	    	 fat_pb.setProgressDrawable(getBarColorDrawable(fat_curr_color, fat_id));
			        	    }
			        	});
						fat_prev_color=fat_curr_color;
					}
					if(car_curr_color!=car_prev_color){
						runOnUiThread(new Runnable() {
			        	     @Override
			        	     public void run() {
			        	    	 car_pb.setProgressDrawable(getBarColorDrawable(car_curr_color, car_id));
			        	    }
			        	});
						car_prev_color=car_curr_color;
					}
					if(sod_curr_color!=sod_prev_color){
						runOnUiThread(new Runnable() {
			        	     @Override
			        	     public void run() {
			        	    	 sod_pb.setProgressDrawable(getBarColorDrawable(sod_curr_color, sod_id));
			        	    }
			        	});
						sod_prev_color=sod_curr_color;
					}
					if(cho_curr_color!=cho_prev_color){
						runOnUiThread(new Runnable() {
			        	     @Override
			        	     public void run() {
			        	    	 cho_pb.setProgressDrawable(getBarColorDrawable(cho_curr_color, cho_id));
			        	    }
			        	});
						cho_prev_color=cho_curr_color;
					}

					//Sleep a short time to simulate animation
					try {
			           Thread.sleep(ANIM_SPEED);
			        } catch (InterruptedException e) {
			           e.printStackTrace();
			        }
			        //Actually draw the visualization onto the screen here
		        	handler.post(new Runnable() {
						public void run() {
							if(!cal_finished){
								cal_pw.invalidate();
								cal_pw.setProgress((int)Math.round(f5));
							}
							if(!fat_finished){
								fat_text_anim.invalidate();
								fat_text_anim.draw_value((int)fat_num, (int) Math.round(f1), fat_is_mg);
								fat_pb.setMax(0);
								fat_pb.setProgress(0);
								fat_pb.setMax(100);
								fat_pb.setProgress(Math.min(100, (int) Math.round(f1)));
							}
							if(!car_finished){
								car_text_anim.invalidate();
								car_text_anim.draw_value((int)car_num, (int) Math.round(f2), car_is_mg);
								car_pb.setMax(0);
								car_pb.setProgress(0);
								car_pb.setMax(100);
								car_pb.setProgress(Math.min(100, (int) Math.round(f2)));
							}
							if(!sod_finished){
								sod_text_anim.invalidate();
								sod_text_anim.draw_value((int)sod_num, (int) Math.round(f3), sod_is_mg);
								sod_pb.setMax(0);
								sod_pb.setProgress(0);
								sod_pb.setMax(100);
								sod_pb.setProgress(Math.min(100, (int) Math.round(f3)));
							}
							if(!cho_finished){
								cho_text_anim.invalidate();
								cho_text_anim.draw_value((int)cho_num, (int) Math.round(f4), cho_is_mg);
								cho_pb.setMax(0);
								cho_pb.setProgress(0);
								cho_pb.setMax(100);
								cho_pb.setProgress(Math.min(100, (int) Math.round(f4)));
							}
						}
			        });
			     }
			}
		});
		
	}

	@Override
	public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
		scale_values(newVal);
    	reset_flags();
    	run_visualization();
	}
	
	public void saveItem(float[] _vals){
		SaveDialog sd=new SaveDialog();
		sd.set_vals(_vals);
		sd.show(getFragmentManager(), "dialog");
	}

	
	/*public void handleScroll(int value){
		mExecServ.execute(new Runnable() {
			@Override
			public void run() {
				try {
		           Thread.sleep(ANIM_SPEED);
		        } catch (InterruptedException e) {
		           e.printStackTrace();
		        }

	        	handler.post(new Runnable() {
					public void run() {

					}
		        });
		     }
		});
		if(curr_scrolling){
			long curr_time=System.currentTimeMillis();
			while(curr_time-prevScrollTime<100){
				curr_time=System.currentTimeMillis();
				curr_scrolling=false;
			}
		}else{
			scale_values(value);
	    	reset_flags();
	    	run_visualization();
		}
	}*/
	
}









