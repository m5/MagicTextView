package com.qwerjk.better_text;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.TextView;

public class MagicTextView extends TextView {
	private ArrayList<Shadow> outerShadows;
	private ArrayList<Shadow> innerShadows;
	
	private Canvas tempCanvas;
	private Bitmap tempBitmap;
	
	private int[] lockedCompoundPadding;
	private boolean compoundPaddingLocked;
	private Drawable foregroundDrawable;
	
	private float strokeWidth;
	private Integer strokeColor;
	private Join strokeJoin;
	private float strokeMiter;

	public MagicTextView(Context context) {
		super(context);
		init(null);
	}
	public MagicTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs);
	}
	public MagicTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(attrs);
	}
	
	public void init(AttributeSet attrs){
		outerShadows = new ArrayList<Shadow>();
		innerShadows = new ArrayList<Shadow>();
	
		if(attrs != null){
			TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.MagicTextView);
			
            String typefaceName = a.getString( R.styleable.MagicTextView_typeface);
            if(typefaceName != null) {
                Typeface tf = Typeface.createFromAsset(getContext().getAssets(), String.format("fonts/%s.ttf", typefaceName));
                setTypeface(tf);
            }
            
			if(a.hasValue(R.styleable.MagicTextView_foreground)){
				Drawable foreground = a.getDrawable(R.styleable.MagicTextView_foreground);	
				if(foreground != null){
					this.setForegroundDrawable(foreground);
				}else{
					this.setTextColor(a.getColor(R.styleable.MagicTextView_foreground, 0xff000000));
				}
			}
		
			if(a.hasValue(R.styleable.MagicTextView_background)){
				Drawable background = a.getDrawable(R.styleable.MagicTextView_background);
				if(background != null){
					this.setBackgroundDrawable(background);
				}else{
					this.setBackgroundColor(a.getColor(R.styleable.MagicTextView_background, 0xff000000));
				}
			}
			
			if(a.hasValue(R.styleable.MagicTextView_innerShadowColor)){
				this.addInnerShadow(a.getFloat(R.styleable.MagicTextView_innerShadowRadius, 0), 
									a.getFloat(R.styleable.MagicTextView_innerShadowDx, 0), 
									a.getFloat(R.styleable.MagicTextView_innerShadowDy, 0),
									a.getColor(R.styleable.MagicTextView_innerShadowColor, 0xff000000));
			}
			
			if(a.hasValue(R.styleable.MagicTextView_outerShadowColor)){
				this.addOuterShadow(a.getFloat(R.styleable.MagicTextView_outerShadowRadius, 0), 
									a.getFloat(R.styleable.MagicTextView_outerShadowDx, 0), 
									a.getFloat(R.styleable.MagicTextView_outerShadowDy, 0),
									a.getColor(R.styleable.MagicTextView_outerShadowColor, 0xff000000));
			}
			
			if(a.hasValue(R.styleable.MagicTextView_strokeColor)){
				float strokeWidth = a.getFloat(R.styleable.MagicTextView_strokeWidth, 1);
				int strokeColor = a.getColor(R.styleable.MagicTextView_strokeColor, 0xff000000);
				float strokeMiter = a.getFloat(R.styleable.MagicTextView_strokeMiter, 10);
				Join strokeJoin = null;
				switch(a.getInt(R.styleable.MagicTextView_strokeJoinStyle, 0)){
				case(0): strokeJoin = Join.MITER; break;
				case(1): strokeJoin = Join.BEVEL; break;
				case(2): strokeJoin = Join.ROUND; break;
				}
				this.setStroke(strokeWidth, strokeColor, strokeJoin, strokeMiter);
			}
		}
	}
	
	public void setStroke(float width, int color, Join join, float miter){
		strokeWidth = width;
		strokeColor = color;
		strokeJoin = join;
		strokeMiter = miter;
	}
	
	public void setStroke(float width, int color){
		setStroke(width, color, Join.MITER, 10);
	}
	
	public void addOuterShadow(float r, float dx, float dy, int color){
		if(r == 0){ r = 0.0001f; }
		outerShadows.add(new Shadow(r,dx,dy,color));
	}
	
	public void addInnerShadow(float r, float dx, float dy, int color){
		if(r == 0){ r = 0.0001f; }
		innerShadows.add(new Shadow(r,dx,dy,color));
	}
	
	public void clearInnerShadows(){
		innerShadows.clear();
	}
	
	public void clearOuterShadows(){
		outerShadows.clear();
	}
	
	public void setForegroundDrawable(Drawable d){
		this.foregroundDrawable = d;
	}
	
	public Drawable getForeground(){
		return this.foregroundDrawable == null ? this.foregroundDrawable : new ColorDrawable(this.getCurrentTextColor());
	}
	
	@Override
	public void onDraw(Canvas canvas){
		super.onDraw(canvas);
		
		Drawable restoreBackground = this.getBackground();
		Drawable[] restoreDrawables = this.getCompoundDrawables();
		int restoreColor = this.getCurrentTextColor();

		this.lockCompoundPadding();
		this.setCompoundDrawables(null,  null, null, null);
		

		
		this.setTextColor(0x00000000);
		for(Shadow shadow : outerShadows){
			this.setShadowLayer(shadow.r, shadow.dx, shadow.dy, shadow.color);
			super.onDraw(canvas);
		}
		this.setShadowLayer(0,0,0,0);
		this.setTextColor(restoreColor);
		
		if(this.foregroundDrawable != null && this.foregroundDrawable instanceof BitmapDrawable){
			generateTempCanvas(canvas);
			super.onDraw(tempCanvas);
			Paint paint = ((BitmapDrawable) this.foregroundDrawable).getPaint();
			paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
			this.foregroundDrawable.setBounds(canvas.getClipBounds());
			this.foregroundDrawable.draw(tempCanvas);
			canvas.drawBitmap(tempBitmap, 0, 0, null);
			tempCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
		}

		if(strokeColor != null){
			TextPaint paint = this.getPaint();
			paint.setStyle(Style.STROKE);
			paint.setStrokeJoin(strokeJoin);
			paint.setStrokeMiter(strokeMiter);
			this.setTextColor(strokeColor);
			paint.setStrokeWidth(strokeWidth);
			super.onDraw(canvas);
			paint.setStyle(Style.FILL);
			this.setTextColor(restoreColor);
		}
		if(innerShadows.size() > 0){
			generateTempCanvas(canvas);
			TextPaint paint = this.getPaint();
			for(Shadow shadow : innerShadows){
				this.setTextColor(shadow.color);
				super.onDraw(tempCanvas);
				this.setTextColor(0x00000000);
				this.setShadowLayer(shadow.r, shadow.dx, shadow.dy, shadow.color);
				paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
				super.onDraw(tempCanvas);
				canvas.drawBitmap(tempBitmap, 0, 0, null);
				tempCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
				paint.setXfermode(null);
				this.setTextColor(restoreColor);
				this.setShadowLayer(0,0,0,0);
			}
		}
		
		
		if(restoreDrawables != null){
			this.setCompoundDrawablesWithIntrinsicBounds(restoreDrawables[0], restoreDrawables[1], restoreDrawables[2], restoreDrawables[3]);
		}
		this.setBackgroundDrawable(restoreBackground);
		this.setTextColor(restoreColor);
	}
	
	public void generateTempCanvas(Canvas canvas){
		if(tempCanvas == null
        || tempCanvas.getWidth() != canvas.getWidth()
        || tempCanvas.getHeight() != canvas.getHeight()
        ){
			tempCanvas = new Canvas();
			tempBitmap = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_8888);
			tempCanvas.setBitmap(tempBitmap);
		}
	}
	
	
	public void lockCompoundPadding(){
		lockedCompoundPadding = new int[]{
				getCompoundPaddingLeft(),
				getCompoundPaddingRight(),
				getCompoundPaddingTop(),
				getCompoundPaddingBottom()
		};
		compoundPaddingLocked = true;
	}
	
	public void unlockCompoundPadding(){
		compoundPaddingLocked = false;
	}
	
	@Override
	public int getCompoundPaddingLeft(){
		return !compoundPaddingLocked ? super.getCompoundPaddingLeft() : lockedCompoundPadding[0];
	}
	
	@Override
	public int getCompoundPaddingRight(){
		return !compoundPaddingLocked ? super.getCompoundPaddingRight() : lockedCompoundPadding[1];
	}
	
	@Override
	public int getCompoundPaddingTop(){
		return !compoundPaddingLocked ? super.getCompoundPaddingTop() : lockedCompoundPadding[2];
	}
	
	@Override
	public int getCompoundPaddingBottom(){
		return !compoundPaddingLocked ? super.getCompoundPaddingBottom() : lockedCompoundPadding[3];
	}
	
	public static class Shadow{
		float r;
		float dx;
		float dy;
		int color;
		public Shadow(float r, float dx, float dy, int color){
			this.r = r;
			this.dx = dx;
			this.dy = dy;
			this.color = color;
		}
	}
}
