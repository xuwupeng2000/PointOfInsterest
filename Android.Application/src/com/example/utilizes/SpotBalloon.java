package com.example.utilizes;

import android.graphics.Canvas; 
import android.graphics.ColorFilter; 
import android.graphics.Paint; 
import android.graphics.drawable.Drawable; 
 
 
public class SpotBalloon extends Drawable { 
       
 
        private Paint spotBalloonPaint = new Paint(); 
        private static final Paint blackTextPaint = new Paint(); 
        private static final int HEIGHT = 20; 
        private static final int WIDTH = 100;
 
        private String name;       
 
        public SpotBalloon(int alpha, int r, int g, int b) {
 
                spotBalloonPaint.setARGB(alpha, r, g, b);
 
                spotBalloonPaint.setAntiAlias(true);
 
                blackTextPaint.setARGB(255, 0, 0, 0);
 
                blackTextPaint.setAntiAlias(true); 
        }
 
 
 
        @Override
 
        public void draw(Canvas canvas) {               
 
                canvas.setViewport(WIDTH, HEIGHT);
 
                canvas.drawCircle(0, 0, 8, blackTextPaint);
 
                canvas.drawCircle(0, 0, 7, spotBalloonPaint);
 
                // TODO Auto-generated method stub 
        }
 
 
 
        @Override
 
        public int getOpacity() {
        	// TODO Auto-generated method stub
                return 0;
        }
 
 
 
        @Override
 
        public void setAlpha(int alpha) { 
                // TODO Auto-generated method stub
        }
 
 
 
        @Override
 
        public void setColorFilter(ColorFilter cf) { 
                // TODO Auto-generated method stub
 
        }
 
       
 
        public void setName(String name){ 
                this.name = name; 
        }
}
 
