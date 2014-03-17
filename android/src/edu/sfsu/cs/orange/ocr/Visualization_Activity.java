package edu.sfsu.cs.orange.ocr;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;

public class Visualization_Activity extends Activity {
	
	ExecutorService mExecServ;
	private ProgressBar fat_pb;
	private float fat_num=0;
	private int fat_per=-1;
	private ProgressBar cho_pb;
	private float cho_num=0;
	private int cho_per=-1;
	private ProgressBar sod_pb;
	private float sod_num=0;
	private int sod_per=-1;
	private ProgressBar car_pb;
	private float car_num=0;
	private int car_per=-1;
	private Drawable yellow_bar=null;
	private Drawable red_bar=null;
	private TextAnimationView fat_text_anim;
	private TextAnimationView cho_text_anim;
	private TextAnimationView sod_text_anim;
	private TextAnimationView car_text_anim;
	private Resources res;
	static double f1=0;
	static double f2=0;
	static double f3=0;
	static double f4=0;
	public static final double GREEN_THRESH=0;
	public static final double YELLOW_THRESH=35;
	public static final double RED_THRESH=70;
	public static final int ANIM_SPEED=20;
	public static final String NUTRITION_LABEL_KEY = "nutritionValues";
	public static final String NUTRITION_QUANT_KEY = "nutritionQuant";
	private Handler handler=new Handler();
	
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
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.visualization_page);
		res=getResources();
		mExecServ=Executors.newSingleThreadExecutor();
		
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
		float[] values=(getIntent().getExtras()).getFloatArray(NUTRITION_QUANT_KEY);
		fat_num=values[1];
		set_fat((int) Math.round(fat_num/60*100));
		car_num=values[4];
		set_carb((int) Math.round(car_num/300*100));
		cho_num=values[2];
		set_chol((int) Math.round(cho_num/0.2*100));
		sod_num=values[3];
		set_sod((int) Math.round(sod_num/20*100));
		//Find fields on the layout for editing purposes
		car_pb= (ProgressBar) findViewById(R.id.car_pb);
		fat_pb= (ProgressBar) findViewById(R.id.fat_pb);
		cho_pb= (ProgressBar) findViewById(R.id.chol_pb);
		sod_pb= (ProgressBar) findViewById(R.id.sod_pb);
		fat_text_anim=(TextAnimationView) findViewById(R.id.fat_num);
		car_text_anim=(TextAnimationView) findViewById(R.id.car_num);
		cho_text_anim=(TextAnimationView) findViewById(R.id.chol_num);
		sod_text_anim=(TextAnimationView) findViewById(R.id.sod_num);
		
		
		run_chol_visualization();
		run_sod_visualization();
		run_carb_visualization();
		run_fat_visualization();
	}
	
	
	public void run_fat_visualization(){
		//Create a thread to animate the progress bar
		mExecServ.submit(new Runnable() {
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
		mExecServ.submit(new Runnable() {
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
		mExecServ.submit(new Runnable() {
			@Override
			public void run() {
				double curr_state=GREEN_THRESH;
				double prev_state=GREEN_THRESH;
				while ((int)Math.round(f3)<sod_per) {
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
		mExecServ.submit(new Runnable() {
			@Override
			public void run() {
				double curr_state=GREEN_THRESH;
				double prev_state=GREEN_THRESH;
				while ((int)Math.round(f4)<cho_per) {
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
