package edu.sfsu.cs.orange.ocr;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ImageView;

public class TextAnimationView extends ImageView {

	private Paint grey_paint=new Paint();
	private Paint large_green_paint=new Paint();
	private Paint large_yellow_paint=new Paint();
	private Paint large_red_paint=new Paint();
	private Paint small_green_paint=new Paint();
	private Paint small_yellow_paint=new Paint();
	private Paint small_red_paint=new Paint();
	private Paint large_curr_paint;
	private Paint small_curr_paint;
	private int curr_per=0;
	private float curr_val=0;
	private static final int LARGE_TEXT_SIZE=40;
	private static final int SMALL_TEXT_SIZE=30;
	private static final int SHADOW_SIZE=5;

	private void init(){
		grey_paint.setColor(Color.GRAY);
		grey_paint.setStyle(Paint.Style.STROKE);
		grey_paint.setStrokeWidth(4);
		
		large_green_paint.setColor(Color.GREEN);
		large_green_paint.setTextSize(LARGE_TEXT_SIZE);
		large_green_paint.setShadowLayer(SHADOW_SIZE, 0, 0, Color.GRAY);
		
		large_yellow_paint.setColor(Color.YELLOW);
		large_yellow_paint.setTextSize(LARGE_TEXT_SIZE);
		large_yellow_paint.setShadowLayer(SHADOW_SIZE, 0, 0, Color.GRAY);
		
		large_red_paint.setColor(Color.RED);
		large_red_paint.setTextSize(LARGE_TEXT_SIZE);
		large_red_paint.setShadowLayer(SHADOW_SIZE, 0, 0, Color.GRAY);
		
		small_green_paint.setColor(Color.GREEN);
		small_green_paint.setTextSize(SMALL_TEXT_SIZE);
		small_green_paint.setShadowLayer(SHADOW_SIZE, 0, 0, Color.GRAY);
		
		small_yellow_paint.setColor(Color.YELLOW);
		small_yellow_paint.setTextSize(SMALL_TEXT_SIZE);
		small_yellow_paint.setShadowLayer(SHADOW_SIZE, 0, 0, Color.GRAY);
		
		small_red_paint.setColor(Color.RED);
		small_red_paint.setTextSize(SMALL_TEXT_SIZE);
		small_red_paint.setShadowLayer(SHADOW_SIZE, 0, 0, Color.GRAY);
		
		large_curr_paint=large_green_paint;
		small_curr_paint=small_green_paint;
	}

	public TextAnimationView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setWillNotDraw(false);
		init();
	}

	 public void draw_value(float value, int percent){
		 if(value<Visualization_Activity.YELLOW_THRESH){
			 large_curr_paint=large_green_paint;
			 small_curr_paint=small_green_paint;
		 }else if(value>Visualization_Activity.YELLOW_THRESH && value<Visualization_Activity.RED_THRESH){
			 large_curr_paint=large_yellow_paint;
			 small_curr_paint=small_yellow_paint;
		 }else{
			 large_curr_paint=large_red_paint;
			 small_curr_paint=small_red_paint;
		 }
		 curr_per=percent;
		 curr_val=value;
	 }
	 
	 @Override
	 protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		//Draw on canvas
		//Draw divider line
		canvas.drawLine(190, 0, 190, 45, grey_paint);
		
		//Draw text
		canvas.drawText(Integer.toString(curr_per)+"%", 75, 35, large_curr_paint);
		canvas.drawText(Float.toString(curr_val)+"g", 255, 35, small_curr_paint);
	 }
	 @Override
	 protected void onSizeChanged(int w, int h, int oldw, int oldh){
		 super.onSizeChanged(w, h, oldw, oldh);
	 }


}
