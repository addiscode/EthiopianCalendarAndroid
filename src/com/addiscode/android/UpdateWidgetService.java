package com.addiscode.android;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

public class UpdateWidgetService extends Service{
	
	@Override
	public void onCreate() {
		super.onCreate();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i("DEBUG", "Widget Update service run");
		RemoteViews remoteView = new RemoteViews(getPackageName(), R.layout.widget_layout);
		ComponentName thisWidget = new ComponentName(this,WidgetProvider.class);
		
		Context context = this.getApplicationContext();


		Calendar cal = Calendar.getInstance();
		int[] values = new EthiopicCalendar(cal.get(Calendar.YEAR),
				cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH))
				.gregorianToEthiopic();
		int weekDay = cal.get(Calendar.DAY_OF_WEEK);
		remoteView.setImageViewBitmap(R.id.widgetMonthLabel,
				buildBitmap(context, WidgetProvider.etMonths[values[1]-1],100,0,75,60, "#14130d"));
		remoteView.setTextViewText(R.id.widgetDateLabel,
				String.valueOf(values[2]));
		remoteView.setImageViewBitmap(R.id.widgetDayLabel,
				buildBitmap(context, WidgetProvider.etDays[weekDay-1],40,0,30,25, "#038CDC"));
		AppWidgetManager manager = AppWidgetManager.getInstance(context);
		Intent appIntent = new Intent(context, EthiopianCalendarActivity.class);
		PendingIntent appPendingIntent = PendingIntent.getActivity(context, 0, appIntent, 0);
		remoteView.setOnClickPendingIntent(R.id.widgetLayout, appPendingIntent);
		manager.updateAppWidget(thisWidget, remoteView);
		this.stopSelf();
		return Service.START_NOT_STICKY;
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public Bitmap buildBitmap(Context context, String text, int height, int left, int top, int fontSize, String fontColor) {
		int width;
		width = (int) (fontSize*0.7) * text.length();
		
		Bitmap bitMap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
		Canvas canvas = new Canvas(bitMap);
        Paint paint = new Paint(); 
        paint.setColor(0x0084c3ff); 
        paint.setStyle(Style.FILL); 
        Typeface typeFace = Typeface.createFromAsset(context.getAssets(),"DroidSansEthiopic-Regular.ttf");
        canvas.drawPaint(paint); 
        paint.setTextSize(fontSize);
        paint.setTextScaleX(1.f);
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        paint.setAntiAlias(true);
        paint.setColor(Color.parseColor(fontColor));
        paint.setTypeface(typeFace);
        canvas.drawText(text, left, top, paint);
        
        
        return bitMap;
	}
}
