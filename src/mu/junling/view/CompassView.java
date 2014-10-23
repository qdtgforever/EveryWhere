package mu.junling.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;



public class CompassView extends ImageView{
	
	private int mBorderThickness = 0;  
    private Context mContext;  
	
	//默认的颜色
    private int defaultColor = 0xFFFFFFFF;  
	
    // 如果只有其中一个有值，则只画一个圆形边框  
	//外框和内框的颜色
    private int mBorderOutsideColor = 0;  
    private int mBorderInsideColor = 0;  
	
    // 控件默认长、宽  
    private int defaultWidth = 0;  
    private int defaultHeight = 0;  
  
    public CompassView(Context context) {  
        super(context);  
        mContext = context;  
    }  
  
    public CompassView(Context context, AttributeSet attrs) {  
        super(context, attrs);  
        mContext = context;  
        setCustomAttributes(attrs);  
    }  
  
    public CompassView(Context context, AttributeSet attrs, int defStyle) {  
        super(context, attrs, defStyle);  
        mContext = context;  
        setCustomAttributes(attrs);  
    }  
  
    private void setCustomAttributes(AttributeSet attrs) {  
        /*TypedArray a = mContext.obtainStyledAttributes(attrs,  
                R.styleable.roundedimageview); */ 
        mBorderThickness =1;  
        mBorderOutsideColor = Color.YELLOW;  
        mBorderInsideColor = Color.YELLOW;  
    }  
    
    @Override  
    protected void onDraw(Canvas canvas) { 
	//获取自身的图片	
        Drawable drawable = getDrawable();
        if (drawable == null) {  
            return;  
        }  
     
        if (getWidth() == 0 || getHeight() == 0) {  
            return;  
        }  
        this.measure(0, 0);  
        
		//获取图片的高和宽
        int w = getWidth();
        int h = getHeight();
        
	// 取 drawable 的颜色格式  
                Bitmap.Config config = 
                        drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                                : Bitmap.Config.RGB_565;
				//根据取得的长和宽以及颜色格式生成一个Bitmap
                Bitmap b = Bitmap.createBitmap(w,h,config);
				
				//根据Bitmap生成一个Canvas
                Canvas mCanvas = new Canvas(b);   
                drawable.setBounds(0, 0, w, h);  
				
				
					//把图像放入到Canvas
                drawable.draw(mCanvas);

    
        if(b==null) {
        	Log.v("", "b==null!");
        }
        Bitmap bitmap = b.copy(Bitmap.Config.ARGB_8888, true);  
        if (defaultWidth == 0) {  
            defaultWidth = getWidth();  
  
        }  
        if (defaultHeight == 0) {  
            defaultHeight = getHeight();  
        }  
    
		
		
        int radius = 0;  
        if (mBorderInsideColor != defaultColor  
                && mBorderOutsideColor != defaultColor) {// 定义画两个边框，分别为外圆边框和内圆边框  
            radius = (defaultWidth < defaultHeight ? defaultWidth  
                    : defaultHeight) / 2 - 2 * mBorderThickness;  
            // 画内圆  
            drawCircleBorder(canvas, radius + mBorderThickness / 2,  
                    mBorderInsideColor);  
            // 画外圆  外圆是内圆的宽度的三倍
           drawCircleBorder(canvas, radius + mBorderThickness  
                    + mBorderThickness / 2, mBorderOutsideColor);  
					
        } else if (mBorderInsideColor != defaultColor  
                && mBorderOutsideColor == defaultColor) {// 定义画一个边框  
            radius = (defaultWidth < defaultHeight ? defaultWidth  
                    : defaultHeight) / 2 - mBorderThickness;  
            drawCircleBorder(canvas, radius + mBorderThickness / 2,  
                    mBorderInsideColor);  
        } else if (mBorderInsideColor == defaultColor  
                && mBorderOutsideColor != defaultColor) {// 定义画一个边框  
            radius = (defaultWidth < defaultHeight ? defaultWidth  
                    : defaultHeight) / 2 - mBorderThickness;  
            drawCircleBorder(canvas, radius + mBorderThickness / 2,  
                    mBorderOutsideColor);  
        } else {// 没有边框  
            radius = (defaultWidth < defaultHeight ? defaultWidth  
                    : defaultHeight) / 2;  
        }  
		
		
        Bitmap roundBitmap = getCroppedRoundBitmap(bitmap, radius);  
        canvas.drawBitmap(roundBitmap, defaultWidth / 2 - radius, defaultHeight  
                / 2 - radius, null);  
    }  
  
    /** 
     * 获取裁剪后的圆形图片 
     *  
     * @param radius 
     *            半径 
     */  
    public Bitmap getCroppedRoundBitmap(Bitmap bmp, int radius) {  
        Bitmap scaledSrcBmp;  
		
		//直径
        int diameter = radius * 2;  
  
        // 为了防止宽高不相等，造成圆形图片变形，因此截取长方形中处于中间位置最大的正方形图片  
        int bmpWidth = bmp.getWidth();  
        int bmpHeight = bmp.getHeight();  
        int squareWidth = 0, squareHeight = 0;  
        int x = 0, y = 0;  
        Bitmap squareBitmap;  
		
		
		  // 截取正方形图片 
        if (bmpHeight > bmpWidth) {// 高大于宽  
            squareWidth = squareHeight = bmpWidth;  
            x = 0;  
            y = (bmpHeight - bmpWidth) / 2;  
           
            squareBitmap = Bitmap.createBitmap(bmp, x, y, squareWidth,  
                   squareHeight);  
        } else if (bmpHeight < bmpWidth) {// 宽大于高  
            squareWidth = squareHeight = bmpHeight;  
            x = (bmpWidth - bmpHeight) / 2;  
            y = 0;  
            squareBitmap = Bitmap.createBitmap(bmp, x, y, squareWidth,  
                    squareHeight);  
       } else {  
            squareBitmap = bmp;  
        }  
  
  
  
  //如果长和宽不相等，那么就按照直径进行缩放
        if (squareBitmap.getWidth() != diameter  
                || squareBitmap.getHeight() != diameter) {  
            scaledSrcBmp = Bitmap.createScaledBitmap(squareBitmap, diameter,  
                    diameter, true);  
 
        } else {  
            scaledSrcBmp = squareBitmap;  
        }  
		
		//根据参数创造新位图
        Bitmap output = Bitmap.createBitmap(scaledSrcBmp.getWidth(),  
                scaledSrcBmp.getHeight(), Config.ARGB_8888);  
		//根据新位图构造桌布	
        Canvas canvas = new Canvas(output);  
		
		//构造图片大小的画笔
        Paint paint = new Paint();  
        Rect rect = new Rect(0, 0, scaledSrcBmp.getWidth(),  
                scaledSrcBmp.getHeight());  
        paint.setAntiAlias(true); //无锯齿 
        paint.setFilterBitmap(true);  //对位图进行滤波处理
        paint.setDither(true);  //设定是否使用图像抖动处理，会使绘制出来的图片颜色更加平滑和饱满，图像更加清晰 
		
        canvas.drawARGB(0, 0, 0, 0); //透明度设置，完全不透明
		
		//画出了圆形图案
        canvas.drawCircle(scaledSrcBmp.getWidth() / 2,  
                scaledSrcBmp.getHeight() / 2, scaledSrcBmp.getWidth() / 2,  
                paint);  
				
		//遮罩效果设置
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));  
        canvas.drawBitmap(scaledSrcBmp, rect, rect, paint);  
     
        bmp = null;  
        squareBitmap = null;  
        scaledSrcBmp = null;  
        return output;  
   }  
  

    private void drawCircleBorder(Canvas canvas, int radius, int color) {  
        Paint paint = new Paint();  
        /* 去锯齿 */  
        paint.setAntiAlias(true);  
       paint.setFilterBitmap(true);  
        paint.setDither(true);  
        paint.setColor(color);  
        /* 设置paint的　style　为STROKE：空心 */  
       paint.setStyle(Paint.Style.STROKE);  
       /* 设置paint的外框宽度 */  
       paint.setStrokeWidth(mBorderThickness);  
        canvas.drawCircle(defaultWidth / 2, defaultHeight / 2, radius, paint);  
    }  

	
	
	
}