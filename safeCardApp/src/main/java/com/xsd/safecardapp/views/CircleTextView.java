package com.xsd.safecardapp.views;

import com.hysd.usiapp.R;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

public class CircleTextView extends TextView{
	
	private boolean isChecked = false;

	public CircleTextView(Context context) {
		super(context);
		init();
	}
	
	public CircleTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public CircleTextView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);	
		init();
	}

	public void init(){
		this.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				isChecked = (!isChecked);
				if(isChecked){
					setBackgroundResource(R.drawable.shape_circle);
				}else{
					setBackgroundResource(R.color.safe_transparent);
				}
			}
		});
	}
	
	public void setChecked(boolean b){
		this.isChecked = b;
		if(isChecked){
			setBackgroundResource(R.drawable.shape_circle);
		}else{
			setBackgroundResource(R.color.safe_transparent);
		}
	}
	
	
	public boolean isChecked(){
		return isChecked;
	}
	

}
