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

/**
 * Display all visualizations from this activity
 *
 * @author Jacob Li
 * 
 * Use set_recommended_values(float fat, float carbohydrates...) to set the
 * recommended daily nutrition values
 * 
 */

public class Visualization_Activity extends Activity {

	ExecutorService mExecServ;
	//Recommended daily nutrition values
	private float rec_fat=0;
	private float rec_car=0;
	private float rec_cal=0;
	private float rec_sod=0;
	private float rec_cho=0;
	private float ser_size=0;
	//Percent and original values of fat, sodium, carbs, and cholesterol
	private float fat_num=0;
	private int fat_per=-1;
	private float cho_num=0;
	private int cho_per=-1;
	private float sod_num=0;
	private int sod_per=-1;
	private float car_num=0;
	private int car_per=-1;
	private float cal_num=0;
	//Drawables to be used in the progress bar
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
	//Intermediate variable
	static double f1=0;
	static double f2=0;
	static double f3=0;
	static double f4=0;
	static double f5=0;
	//Threshold values and other useful values
	public static final double GREEN_THRESH=0;
	public static final double YELLOW_THRESH=35;
	public static final double RED_THRESH=70;
	public static final int ANIM_SPEED=20;
	public static final float APPLE_WEIGHT=80;
	public static final String NUTRITION_LABEL_KEY = "nutritionValues";
	public static final String NUTRITION_QUANT_KEY = "nutritionQuant";
	private Handler handler=new Handler();
	
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
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		res=getResources();
		//this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		//this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.graphs_page);
		mExecServ=Executors.newSingleThreadExecutor();
		setTypefaces();
		
		try {
			//Use custom progress bar colors
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
		System.out.println("Sodium is "+sod_per);
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
		
		run_serving_size_visualization();
		run_cal_visualization();
		run_fat_visualization();
		run_carb_visualization();
		run_chol_visualization();
		run_sod_visualization();
		
		
		// button listener
		final float[] _val = values;
		Button comparisonButton = (Button)findViewById(R.id.comparison_button);
		comparisonButton.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v){
				Intent intent = new Intent(Visualization_Activity.this, RecommenderActivity.class);
				Bundle b = new Bundle();
				b.putFloatArray(RecommenderActivity.NUTRITION_QUANT_KEY, _val);
				intent.putExtras(b);
				startActivity(intent);
			}
		});
	}
	
	public void run_cal_visualization(){
		//Create a thread to animate the progress bar
		mExecServ.execute(new Runnable() {
			@Override
			public void run() {
				while ((int)Math.round(f5)<(int) Math.round(cal_num) || cal_num==0) {
					//Interpolate frames based on a parabolic acceleration curve for animation
					f5=f5+(Math.sqrt((double)(cal_num-f5)))/2;
			        try {
			           Thread.sleep(ANIM_SPEED);
			        } catch (InterruptedException e) {
			           e.printStackTrace();
			        }
			        //Actually draw the visualization onto the screen here
			        if(f5<cal_num){
			        	handler.post(new Runnable() {
							public void run() {
								cal_pw.invalidate();
								cal_pw.setProgress((int)Math.round(f5));
							}
				        });
			        }
			     }
			}
		});
	}
	
	public void run_serving_size_visualization(){
		if(ser_size<APPLE_WEIGHT-20){
			serving_size.setImageDrawable(res.getDrawable(R.drawable.servingsize_less));
		}else if(ser_size>APPLE_WEIGHT-20 && ser_size<APPLE_WEIGHT+20){
			serving_size.setImageDrawable(res.getDrawable(R.drawable.servingsize_equal));
		}else{
			serving_size.setImageDrawable(res.getDrawable(R.drawable.servingsize_more));
		}
	}
	
	public void run_fat_visualization(){
		//Create a thread to animate the progress bar
		mExecServ.execute(new Runnable() {
			@Override
			public void run() {
				double curr_state=GREEN_THRESH;
				double prev_state=GREEN_THRESH;
				while ((int)Math.round(f1)<fat_per || fat_per==0) {
					//Interpolate frames based on a parabolic acceleration curve for animation
					f1=f1+(Math.sqrt((double) fat_per-f1))/4;
					fat_pb.setIndeterminate(false);
			        try {
			           Thread.sleep(ANIM_SPEED);
			        } catch (InterruptedException e) {
			           e.printStackTrace();
			        }
			        //Determine the color for the progressbar & text
			        if(f1>=GREEN_THRESH && f1<YELLOW_THRESH){
			        	curr_state=GREEN_THRESH;
			        }else if(f1>YELLOW_THRESH && f1<RED_THRESH){
			        	curr_state=YELLOW_THRESH;
			        }else{
			        	curr_state=RED_THRESH;
			        }
			        //Set the color of the progressbar & text
			        if(curr_state!=prev_state){
			        	if(curr_state==YELLOW_THRESH){
				        	runOnUiThread(new Runnable() {
				        	     @Override
				        	     public void run() {
				        	    	 fat_pb.setProgressDrawable(yellow_bar);
				        	    }
				        	});
			        	}else if(curr_state==RED_THRESH){
				        	runOnUiThread(new Runnable() {
				        	     @Override
				        	     public void run() {
				        	    	 fat_pb.setProgressDrawable(red_bar);
				        	    }
				        	});
			        	}
			        	prev_state=curr_state;
			        }
			        //Actually draw the visualization onto the screen here
			        if((int)Math.round(f1)>100){
			        	handler.post(new Runnable() {
							public void run() {
								fat_text_anim.invalidate();
								fat_text_anim.draw_value(fat_num, (int) Math.round(f1));
								fat_pb.setProgress(100);
							}
				        });
			        }else if(fat_per!=0){
				        handler.post(new Runnable() {
							public void run() {
								fat_text_anim.invalidate();
								fat_text_anim.draw_value(fat_num, (int) Math.round(f1));
								fat_pb.setProgress((int) Math.round(f1));
							}
				        });
			        }else{
			        	handler.post(new Runnable() {
							public void run() {
								fat_text_anim.draw_value(fat_num, 0);
								fat_pb.setProgress(0);
							}
				        });
			        	break;
			        }
			     }
			}
		});
	}
	
	public void run_carb_visualization(){
		mExecServ.execute(new Runnable() {
			@Override
			public void run() {
				double curr_state=GREEN_THRESH;
				double prev_state=GREEN_THRESH;
				while ((int)Math.round(f2)<car_per || car_per==0) {
					f2=f2+(Math.sqrt((double) car_per-f2))/4;
					car_pb.setIndeterminate(false);
			        try {
			           Thread.sleep(ANIM_SPEED);
			        } catch (InterruptedException e) {
			           e.printStackTrace();
			        }
			        if(f2>=GREEN_THRESH && f2<YELLOW_THRESH){
			        	curr_state=GREEN_THRESH;
			        }else if(f2>YELLOW_THRESH && f2<RED_THRESH){
			        	curr_state=YELLOW_THRESH;
			        }else{
			        	curr_state=RED_THRESH;
			        }
			        if(curr_state!=prev_state){
			        	if(curr_state==YELLOW_THRESH){
				        	runOnUiThread(new Runnable() {
				        	     @Override
				        	     public void run() {
				        	    	 car_pb.setProgressDrawable(yellow_bar);
				        	    }
				        	});
			        	}else if(curr_state==RED_THRESH){
				        	runOnUiThread(new Runnable() {
				        	     @Override
				        	     public void run() {
				        	    	 car_pb.setProgressDrawable(red_bar);
				        	    }
				        	});
			        	}
			        	prev_state=curr_state;
			        }
			        
			        if((int)Math.round(f2)>100){
			        	handler.post(new Runnable() {
							public void run() {
								car_text_anim.invalidate();
								car_text_anim.draw_value(car_num, (int) Math.round(f2));
								car_pb.setProgress(100);
							}
				        });
			        }else if(car_per!=0){
				        handler.post(new Runnable() {
							public void run() {
								car_text_anim.invalidate();
								car_text_anim.draw_value(car_num, (int) Math.round(f2));
								car_pb.setProgress((int) Math.round(f2));
							}
				        });
			        }else{
			        	handler.post(new Runnable() {
							public void run() {
								car_text_anim.draw_value(car_num, 0);
								car_pb.setProgress(0);
							}
				        });
			        	break;
			        }
			     }
			}
		});
	}
	
	public void run_sod_visualization(){
		mExecServ.execute(new Runnable() {
			@Override
			public void run() {
				double curr_state=GREEN_THRESH;
				double prev_state=GREEN_THRESH;
				while ((int)Math.round(f3)<sod_per|| sod_per==0) {
					f3=f3+(Math.sqrt((double) sod_per-f3))/4;
					sod_pb.setIndeterminate(false);
			        try {
			           Thread.sleep(ANIM_SPEED);
			        } catch (InterruptedException e) {
			           e.printStackTrace();
			        }
			        if(f3>=GREEN_THRESH && f3<YELLOW_THRESH){
			        	curr_state=GREEN_THRESH;
			        }else if(f3>YELLOW_THRESH && f3<RED_THRESH){
			        	curr_state=YELLOW_THRESH;
			        }else{
			        	curr_state=RED_THRESH;
			        }
			        if(curr_state!=prev_state){
			        	if(curr_state==YELLOW_THRESH){
				        	runOnUiThread(new Runnable() {
				        	     @Override
				        	     public void run() {
				        	    	 sod_pb.setProgressDrawable(yellow_bar);
				        	    }
				        	});
			        	}else if(curr_state==RED_THRESH){
				        	runOnUiThread(new Runnable() {
				        	     @Override
				        	     public void run() {
				        	    	 sod_pb.setProgressDrawable(red_bar);
				        	    }
				        	});
			        	}
			        	prev_state=curr_state;
			        }
			        
			        if((int)Math.round(f3)>100){
			        	handler.post(new Runnable() {
							public void run() {
								sod_text_anim.invalidate();
								sod_text_anim.draw_value(sod_num, (int) Math.round(f3));
								sod_pb.setProgress(100);
							}
				        });
			        }else if(sod_per!=0){
				        handler.post(new Runnable() {
							public void run() {
								sod_text_anim.invalidate();
								sod_text_anim.draw_value(sod_num, (int) Math.round(f3));
								sod_pb.setProgress((int) Math.round(f3));
							}
				        });
			        }else{
			        	handler.post(new Runnable() {
							public void run() {
								sod_text_anim.draw_value(sod_num, 0);
								sod_pb.setProgress(0);
							}
				        });
			        	break;
			        }
			     }
			}
		});
	}
	
	public void run_chol_visualization(){
		mExecServ.execute(new Runnable() {
			@Override
			public void run() {
				double curr_state=GREEN_THRESH;
				double prev_state=GREEN_THRESH;
				while ((int)Math.round(f4)<cho_per|| cho_per==0) {
					f4=f4+(Math.sqrt((double) cho_per-f4))/4;
					cho_pb.setIndeterminate(false);
			        try {
			           Thread.sleep(ANIM_SPEED);
			        } catch (InterruptedException e) {
			           e.printStackTrace();
			        }
			        if(f4>=GREEN_THRESH && f4<YELLOW_THRESH){
			        	curr_state=GREEN_THRESH;
			        }else if(f4>YELLOW_THRESH && f4<RED_THRESH){
			        	curr_state=YELLOW_THRESH;
			        }else{
			        	curr_state=RED_THRESH;
			        }
			        if(curr_state!=prev_state){
			        	if(curr_state==YELLOW_THRESH){
				        	runOnUiThread(new Runnable() {
				        	     @Override
				        	     public void run() {
				        	    	 cho_pb.setProgressDrawable(yellow_bar);
				        	    }
				        	});
			        	}else if(curr_state==RED_THRESH){
				        	runOnUiThread(new Runnable() {
				        	     @Override
				        	     public void run() {
				        	    	 cho_pb.setProgressDrawable(red_bar);
				        	    }
				        	});
			        	}
			        	prev_state=curr_state;
			        }
			        
			        if((int)Math.round(f4)>100){
			        	handler.post(new Runnable() {
							public void run() {
								cho_text_anim.invalidate();
								cho_text_anim.draw_value(cho_num, (int) Math.round(f4));
								cho_pb.setProgress(100);
							}
				        });
			        }else if(cho_per!=0){
				        handler.post(new Runnable() {
							public void run() {
								cho_text_anim.invalidate();
								cho_text_anim.draw_value(cho_num, (int) Math.round(f4));
								cho_pb.setProgress((int) Math.round(f4));
							}
				        });
			        }else{
			        	handler.post(new Runnable() {
							public void run() {
								cho_text_anim.draw_value(cho_num, 0);
								cho_pb.setProgress(0);
							}
				        });
			        	break;
			        }
			     }
			}
		});
	}
}
