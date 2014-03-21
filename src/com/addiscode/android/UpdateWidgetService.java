package com.addiscode.android;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.List;

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
import android.graphics.Paint.Style;
import android.graphics.Typeface;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

public class UpdateWidgetService extends Service {

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		DBHandler dbHandler = new DBHandler(this);
		List<Event> todayEvents;
		
		Log.i("DEBUG", "Widget Update service run");
		RemoteViews remoteView = new RemoteViews(getPackageName(), R.layout.widget_layout);
		ComponentName thisWidget = new ComponentName(this,WidgetProvider.class);
		
		Context context = this.getApplicationContext();

		DecimalFormat decFormatter = new DecimalFormat("00");  
		Calendar cal = Calendar.getInstance();
		int[] values = new EthiopicCalendar(cal.get(Calendar.YEAR),
				cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH))
				.gregorianToEthiopic();
		
		todayEvents = dbHandler.getEventsOn(values[0], values[1], values[2]);
		
		String dateDec = decFormatter.format(values[2]);
		int weekDay = cal.get(Calendar.DAY_OF_WEEK);
		remoteView.setImageViewBitmap(R.id.widgetMonthLabel,
				buildBitmap(context, WidgetProvider.etMonths[values[1]-1],100,0,75,60, "#FFFF00"));
		remoteView.setTextViewText(R.id.widgetDateLabel, dateDec);
		remoteView.setImageViewBitmap(R.id.widgetDayLabel,
				buildBitmap(context, WidgetProvider.etDays[weekDay-1],40,0,30,35, "#530000"));
		
		
		//show some luv on my widget :|]
		
		String emblemIcon = "";
		if(todayEvents != null){
			for(Event ev: todayEvents) {
				if(!ev.getIcon().equals(emblemIcon))
					emblemIcon = ev.getIcon().replaceAll("\\.{1}(png|PNG|jpg|JPG|jpeg|JPEG)", "");break;
				
			}
			
			//set emblem
			if(!emblemIcon.isEmpty())
				remoteView.setImageViewResource(R.id.widgetEmblem, getDrawableId(emblemIcon));
		}
		
		
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

	public Bitmap buildBitmap(Context context, String text, int height,
			int left, int top, int fontSize, String fontColor) {
		int width;
		width = (int) (fontSize * 0.75) * text.length();

		Bitmap bitMap = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_4444);
		Canvas canvas = new Canvas(bitMap);
		Paint paint = new Paint();
		paint.setColor(0x0084c3ff);
		paint.setStyle(Style.FILL);
		Typeface typeFace = Typeface.createFromAsset(context.getAssets(),
				"DroidSansEthiopic-Regular.ttf");
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

	private int getDrawableId(String name) {
		int drawableId = 0;
		try {
			Class res = R.drawable.class;
			Field field = res.getField(name);
			drawableId = field.getInt(null);
		} catch (Exception e) {
			Log.e("MyTag", "Failure to get drawable id.", e);
		}
		return drawableId;
	}
}
