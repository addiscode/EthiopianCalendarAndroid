/**
/ * @author hafiz
 *
 * Feb 28, 2014
 */

package com.addiscode.android;

import android.app.Activity;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;

public class SimpleGestureFilter extends SimpleOnGestureListener {

	public final static int SWIPE_UP = 1;
	public final static int SWIPE_DOWN = 2;
	public final static int SWIPE_LEFT = 3;
	public final static int SWIPE_RIGHT = 4;

	public final static int MODE_TRANSPARENT = 0;
	public final static int MODE_SOLID = 1;
	public final static int MODE_DYNAMIC = 2;

	private final static int ACTION_FAKE = -13; // just an unlikely number
	private int SWIPE_MIN_DISTANCE = 100;
	private int SWIPE_MAX_DISTANCE = 550;
	private int SWIPE_MIN_VELOCITY = 100;

	private int mode = MODE_DYNAMIC;
	private boolean running = true;
	private boolean tapIndicator = false;

	private Activity context;
	private GestureDetector detector;
	private SimpleGestureListener listener;

	public SimpleGestureFilter(Activity context, SimpleGestureListener sgl) {

		this.context = context;
		this.detector = new GestureDetector(context, this);
		this.listener = sgl;
	}

	public void onTouchEvent(MotionEvent event) {

		if (!this.running)
			return;

		boolean result = this.detector.onTouchEvent(event);

		if (this.mode == MODE_SOLID)
			event.setAction(MotionEvent.ACTION_CANCEL);
		else if (this.mode == MODE_DYNAMIC) {

			if (event.getAction() == ACTION_FAKE)
				event.setAction(MotionEvent.ACTION_UP);
			else if (result)
				event.setAction(MotionEvent.ACTION_CANCEL);
			else if (this.tapIndicator) {
				event.setAction(MotionEvent.ACTION_DOWN);
				this.tapIndicator = false;
			}

		}
		// else just do nothing, it's Transparent
	}

	public void setMode(int m) {
		this.mode = m;
	}

	public int getMode() {
		return this.mode;
	}

	public void setEnabled(boolean status) {
		this.running = status;
	}

	public void setSwipeMaxDistance(int distance) {
		this.SWIPE_MAX_DISTANCE = distance;
	}

	public void setSwipeMinDistance(int distance) {
		this.SWIPE_MIN_DISTANCE = distance;
	}

	public void setSwipeMinVelocity(int distance) {
		this.SWIPE_MIN_VELOCITY = distance;
	}

	public int getSwipeMaxDistance() {
		return this.SWIPE_MAX_DISTANCE;
	}

	public int getSwipeMinDistance() {
		return this.SWIPE_MIN_DISTANCE;
	}

	public int getSwipeMinVelocity() {
		return this.SWIPE_MIN_VELOCITY;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {

		final float xDistance = Math.abs(e1.getX() - e2.getX());
		final float yDistance = Math.abs(e1.getY() - e2.getY());

		if (xDistance > this.SWIPE_MAX_DISTANCE
				|| yDistance > this.SWIPE_MAX_DISTANCE)
			return false;

		velocityX = Math.abs(velocityX);
		velocityY = Math.abs(velocityY);
		boolean result = false;

		if (velocityX > this.SWIPE_MIN_VELOCITY
				&& xDistance > this.SWIPE_MIN_DISTANCE) {
			if (e1.getX() > e2.getX()) // right to left
				this.listener.onSwipe(SWIPE_LEFT);
			else
				this.listener.onSwipe(SWIPE_RIGHT);

			result = true;
		} else if (velocityY > this.SWIPE_MIN_VELOCITY
				&& yDistance > this.SWIPE_MIN_DISTANCE) {
			if (e1.getY() > e2.getY()) // bottom to up
				this.listener.onSwipe(SWIPE_UP);
			else
				this.listener.onSwipe(SWIPE_DOWN);

			result = true;
		}

		return result;
	}

	 /*@Override
     public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
         try {
             if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_DISTANCE
            		 || Math.abs(e1.getX() - e2.getX()) > SWIPE_MAX_DISTANCE)
                 return false;
             if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_MIN_VELOCITY) {
                 this.listener.onSwipe(SWIPE_LEFT);
             }  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_MIN_VELOCITY) {
                 this.listener.onSwipe(SWIPE_RIGHT);
             }else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_MIN_VELOCITY) {
                 this.listener.onSwipe(SWIPE_DOWN);
             }else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_MIN_VELOCITY) {
                 this.listener.onSwipe(SWIPE_UP);
             }
         } catch (Exception e) {
             // nothing
         }
         return false;
     }*/
	
	
	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		this.tapIndicator = true;
		return false;
	}

	@Override
	public boolean onDoubleTap(MotionEvent arg) {
		this.listener.onDoubleTap();
		;
		return true;
	}

	@Override
	public boolean onDoubleTapEvent(MotionEvent arg) {
		return true;
	}

	@Override
	public boolean onSingleTapConfirmed(MotionEvent arg) {

		if (this.mode == MODE_DYNAMIC) { // we owe an ACTION_UP, so we fake an
			arg.setAction(ACTION_FAKE); // action which will be converted to an
										// ACTION_UP later.
			this.context.dispatchTouchEvent(arg);
		}

		return false;
	}

	static interface SimpleGestureListener {
		void onSwipe(int direction);

		void onDoubleTap();
	}

}