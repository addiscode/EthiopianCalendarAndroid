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
				buildBitmap(context, WidgetProvider.etMonths[values[1]-1],180,80,30,50,45));
		remoteView.setTextViewText(R.id.widgetDateLabel,
				String.valueOf(values[2]));
		remoteView.setImageViewBitmap(R.id.widgetDayLabel,
				buildBitmap(context, WidgetProvider.etDays[weekDay-1],180,40,30,25,30));
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

	
	public Bitmap buildBitmap(Context context, String text, int width, int height, int left, int top, int fontSize) {
		Bitmap bitMap = Bitmap.createBitmap(width, height, Bitmap.Config.ALPHA_8);
		Canvas c = new Canvas(bitMap);
		Paint paint = new Paint();
		Typeface typeFace = Typeface.createFromAsset(context.getAssets(),"nyala.ttf");
//		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
        paint.setTextSize(fontSize);
        paint.setTextScaleX(1.f);
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        paint.setAntiAlias(true);
        paint.setColor(Color.YELLOW);
        paint.setTypeface(typeFace);
        c.drawText(text, left, top, paint);
		return bitMap;
	}
}
