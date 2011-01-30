package com.android.localguide;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;

public class MyAnimation extends Animation{

    float centerX ;
    float centerY;
	@Override
	public void initialize(int width, int height, int parentWidth,
	int parentHeight)
	{
		
	super.initialize(width, height, parentWidth, parentHeight);
	centerX = width /2 ;
	centerY = height /2;
	setDuration(1200);
	this.setRepeatMode(INFINITE);
	setInterpolator(new LinearInterpolator());
	}

	 @Override
	    protected void applyTransformation(float interpolatedTime, Transformation t) {
	        float degrees = 40;
	        float degrees1 = 40;
	        final Camera camera = new Camera();
	        final Matrix matrix = t.getMatrix();

	        camera.save();
         //   camera.translate(0.0f, 0.0f, 1.0f);
            
	       //  camera.rotateX(degrees);
	         //camera.rotateY(degrees);
	       // camera.rotateY(degrees1);
	        //camera.rotateX(degrees);
	        //camera.getMatrix(matrix);
	        //camera.restore();
		 	matrix.setRotate(degrees * interpolatedTime, centerX, centerY);
			matrix.postScale(interpolatedTime, interpolatedTime, centerX,centerY);
			matrix.preRotate(-degrees,centerX,centerY);
	        //matrix.preTranslate(-centerX, -centerY);
	        //matrix.postTranslate(centerX, centerY);
	    }
	
}
