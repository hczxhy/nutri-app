package edu.sfsu.cs.orange.ocr;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.todddavies.components.progressbar.ProgressWheel;

public class GraphActivity extends Activity {
	
	ExecutorService mExecServ;
	//Threshold values and other useful values
	public static final double GREEN_THRESH=0;
	public static final double YELLOW_THRESH=35;
	public static final double RED_THRESH=70;
	public static final int ANIM_SPEED=20;
	public static final float APPLE_WEIGHT=80;
	public static final String NUTRITION_LABEL_KEY = "nutritionValues";
	public static final String NUTRITION_QUANT_KEY = "nutritionQuant";
	//Recommended daily nutrition values
	private float rec_fat=0;
	private float rec_car=0;
	private float rec_cal=0;
	private float rec_sod=0;
	private float rec_cho=0;
	private float ser_size=0;
	//Percent and original values of fat, sodium, carbs, and cholesterol
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
	private Drawable green_bar=null;
	private Drawable yellow_bar=null;
	private Drawable red_bar=null;
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
	private ImageView serving_size;
	//Get resources
	private Resources res;
	private Typeface arialblack;
	private Typeface arial;
	//Intermediate variables and states
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
	}
	
	public void set_ser_size(float value){
		ser_size=value;
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
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		res=getResources();
		setContentView(R.layout.graphs_page);
		mExecServ=Executors.newCachedThreadPool();
		setTypefaces();
		
		try {
			//Use custom progress bar colors
			green_bar=Drawable.createFromXml(res, res.getXml(R.drawable.greenprogressbar));
			yellow_bar=Drawable.createFromXml(res, res.getXml(R.drawable.yellowprogressbar));
			red_bar=Drawable.createFromXml(res, res.getXml(R.drawable.redprogressbar));
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

	}
	
	@Override
	public void onStart(){
		super.onStart();
		handler=new Handler();
		//Convert values to percentages ****Needs to be connected with the settings page*****
		float[] values={0,0,0,0,0,0,0};
		if(getIntent().getExtras()!=null){
			values=(getIntent().getExtras()).getFloatArray(NUTRITION_QUANT_KEY);
		}
		
		set_recommended_values((float)1200, (float)60, (float)300, (float) 0.2, (float)2);
		fat_num=values[1];
		set_fat((int) Math.round(fat_num/rec_fat*100));
		car_num=values[4];
		set_carb((int) Math.round(car_num/rec_car*100));
		cho_num=values[2];
		set_chol((int) Math.round(cho_num/rec_cho*100));
		sod_num=values[3]; 
		set_sod((int) Math.round(sod_num/rec_sod*100));
		cal_num=values[0];
		ser_size=values[6];
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
		cal_pw.set_recommended_calories(rec_cal);
		serving_size=(ImageView) findViewById(R.id.serving_size_vis);
		
		run_visualization();
		
		// button listener
		final float[] _val = values;
		Button comparisonButton = (Button)findViewById(R.id.comparison_button);
		comparisonButton.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v){
				Intent intent = new Intent(GraphActivity.this, RecommenderActivity.class);
				Bundle b = new Bundle();
				b.putFloatArray(RecommenderActivity.NUTRITION_QUANT_KEY, _val);
				intent.putExtras(b);
				startActivity(intent);
			}
		});
	}
	
	
	public double interpolate_one_frame(double init, int end, boolean reached_end_goal){
		if((int)Math.round(init)<end){
			reached_end_goal=false;
			init=init+(Math.sqrt((double) end-init))/4;
		}else if ((int)Math.round(init)>end){
			reached_end_goal=false;
			init=init-(Math.sqrt(init-(double) end))/4;
		}else{
			if(reached_end_goal==fat_finished){
				fat_finished=true;
			}else if(reached_end_goal==car_finished){
				car_finished=true;
			}else if(reached_end_goal==sod_finished){
				sod_finished=true;
			}else if (reached_end_goal==cho_finished){
				cho_finished=true;
			}else if(reached_end_goal==cal_finished){
				cal_finished=true;
			}
		}
		return init;
	}
	
	public void check_color(double curr_frame, double curr_color){
		if(curr_frame<YELLOW_THRESH){
			curr_color=GREEN_THRESH;
		}else if(curr_frame>YELLOW_THRESH && curr_frame< RED_THRESH){
			curr_color=YELLOW_THRESH;
		}else{
			curr_color=RED_THRESH;
		}
	}
	public Drawable getBarColorDrawable(double curr_color){
		if(curr_color==GREEN_THRESH){
			return green_bar;
		}else if(curr_color==YELLOW_THRESH){
			return yellow_bar;
		}else{
			return red_bar;
		}
	}
	
	public void run_visualization(){
		//Create a thread to animate the progress bar
		mExecServ.execute(new Runnable() {
			@Override
			public void run() {
				
				while (!cal_finished || !fat_finished || !car_finished || !cho_finished || !sod_finished) {
					//Interpolate frames based on a parabolic acceleration curve for animation
					f1=interpolate_one_frame(f1,fat_per,fat_finished);
					f2=interpolate_one_frame(f2,car_per,car_finished);
					f3=interpolate_one_frame(f3,sod_per,sod_finished);
					f4=interpolate_one_frame(f4,cho_per,cho_finished);
					f5=interpolate_one_frame(f5,(int) Math.round(cal_num),cal_finished);
					//Change any colors as necessary
					check_color(f1, fat_curr_color);
					check_color(f2, car_curr_color);
					check_color(f3, sod_curr_color);
					check_color(f4, cho_curr_color);
					check_color(f5, cal_curr_color);
					if(fat_curr_color!=fat_prev_color){
						runOnUiThread(new Runnable() {
			        	     @Override
			        	     public void run() {
			        	    	 fat_pb.setProgressDrawable(getBarColorDrawable(fat_curr_color));
			        	    }
			        	});
						fat_prev_color=fat_curr_color;
					}
					if(car_curr_color!=car_prev_color){
						runOnUiThread(new Runnable() {
			        	     @Override
			        	     public void run() {
			        	    	 car_pb.setProgressDrawable(getBarColorDrawable(car_curr_color));
			        	    }
			        	});
						car_prev_color=car_curr_color;
					}
					if(sod_curr_color!=sod_prev_color){
						runOnUiThread(new Runnable() {
			        	     @Override
			        	     public void run() {
			        	    	 sod_pb.setProgressDrawable(getBarColorDrawable(sod_curr_color));
			        	    }
			        	});
						sod_prev_color=sod_curr_color;
					}
					if(cho_curr_color!=cho_prev_color){
						runOnUiThread(new Runnable() {
			        	     @Override
			        	     public void run() {
			        	    	 cho_pb.setProgressDrawable(getBarColorDrawable(cho_curr_color));
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
							fat_text_anim.invalidate();
							car_text_anim.invalidate();
							sod_text_anim.invalidate();
							cho_text_anim.invalidate();
							cal_pw.invalidate();
							fat_text_anim.draw_value(fat_num, (int) Math.round(f1));
							fat_pb.setProgress(Math.min((int) Math.round(f1),100));
							car_text_anim.draw_value(car_num, (int) Math.round(f2));
							car_pb.setProgress(Math.min((int) Math.round(f2),100));
							sod_text_anim.draw_value(sod_num, (int) Math.round(f3));
							sod_pb.setProgress(Math.min((int) Math.round(f3),100));
							cho_text_anim.draw_value(cho_num, (int) Math.round(f4));
							cho_pb.setProgress(Math.min((int) Math.round(f4),100));
							cal_pw.setProgress((int)Math.round(f5));
						}
			        });
			     }
				System.out.println("Thread Finished");
			}
		});
		
	}
	
}








