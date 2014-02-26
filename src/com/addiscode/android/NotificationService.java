package com.addiscode.android;


import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.List;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

public class NotificationService extends Service {
	private final IBinder myBinder = new MyBinder();
	private final Context context = this;
	private static final String tag = "NOTIFICATION_SERVICE";
	private static PendingIntent service = null;
	private DBHandler dbHandler = new DBHandler(context);

	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onCreate();
		
		final AlarmManager m = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		final Calendar TIME = Calendar.getInstance();
		TIME.set(Calendar.HOUR_OF_DAY, 0);
		TIME.set(Calendar.MINUTE, 1);
		TIME.set(Calendar.SECOND, 1);
		
		
		final Intent i = new Intent(context, UpdateWidgetService.class);
		if (NotificationService.service == null) {
			NotificationService.service = PendingIntent.getService(context, 0, i,
					PendingIntent.FLAG_CANCEL_CURRENT);
		}
		
		
		Calendar cal = Calendar.getInstance();
		
		Log.i(tag, "Showing notification: " + String.valueOf(System.currentTimeMillis()));
		
		EthiopicCalendar etCal = new EthiopicCalendar(cal);
		int values[] = etCal.gregorianToEthiopic();
		List<Event> events = dbHandler.getEventsOn(values[0], values[1], values[2]);
		String eventNames = "";
		String icon = "globe";
		int c=0;
		for(Event ev: events) {
			Log.i(tag, ev.getTitle());
			String ic = ev.getIcon().replaceAll("\\.{1}(png|PNG|jpg|JPG|jpeg|JPEG)", "");
			if(!ic.equals(icon)) icon=ic;
			c++;
			eventNames += ev.getTitle();
			if(c<events.size()) eventNames += ",";
		}
		if(events.size()>0) {
			NotificationManager notifManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			Notification notif = new Notification(R.drawable.app_icon, "Ethiopian calendar", 
					System.currentTimeMillis());
			RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.custom_notification);
	    	contentView.setImageViewResource(R.id.notifIcon, getDrawableId(icon));
	    	contentView.setTextViewText(R.id.notifTitle, "Events for today");
	    	contentView.setTextViewText(R.id.notifText, eventNames);
	    	notif.contentView = contentView;
	    	notif.flags |= Notification.FLAG_AUTO_CANCEL;
	    	Intent notifIntent = new Intent(getApplicationContext(), NotificationReceiver.class);
	    	PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notifIntent, 0);
	    	notif.contentIntent = contentIntent;
	    	notifManager.notify(0, notif);
		}
		Log.i(tag, "--END SHOWING NOTIFICATIONS --" + String.valueOf(System.currentTimeMillis()));
		m.set(AlarmManager.RTC_WAKEUP, TIME.getTimeInMillis(),
				NotificationService.service);
		return Service.START_NOT_STICKY;
	}
	private int getDrawableId(String name) {
		int drawableId = 0;
		try {
		    Class res = R.drawable.class;
		    Field field = res.getField(name);
		    drawableId = field.getInt(null);
		}
		catch (Exception e) {
		    Log.e("MyTag", "Failure to get drawable id.", e);
		}
		return drawableId;
	}
	@Override
	public IBinder onBind(Intent intent) {
		return myBinder;
	}
	
	public class MyBinder extends Binder{
		NotificationService getService() {
			return NotificationService.this;
		}
	}

}
