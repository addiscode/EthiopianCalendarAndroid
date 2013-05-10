package com.addiscode.android;


import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.List;

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
	private DBHandler dbHandler = new DBHandler(context);

	
	@Override
	public void onCreate() {
		super.onCreate();
		Thread notifThread = new Thread(new Runnable() {
			
			public void run() {
				// TODO implement 
				showNotification();
			}
			public void showNotification() {
				try {
					Calendar cal = Calendar.getInstance();
					int hour = cal.get(Calendar.HOUR_OF_DAY);
					int min = cal.get(Calendar.MINUTE);
					int sec = cal.get(Calendar.SECOND);
					int waitHour, waitMin, waitSec;
					waitHour = (0x17 - hour) + 6;
					waitMin = 0x3B - min; //59
					waitSec = 0x3C - sec;
					int waitMils = (waitHour*60*60*1000) + (waitMin*60*1000) + (waitSec*1000);
					Log.i(tag, "Showing notification");
					EthiopicCalendar etCal = new EthiopicCalendar(cal);
					int values[] = etCal.gregorianToEthiopic();
					List<Event> events = dbHandler.getEventsOn(values[0], values[1], values[2]);
					String eventNames = "";
					String icon = "globe";
					int c=0;
					for(Event ev: events) {
						Log.i(tag, ev.getTitle());
						String i = ev.getIcon().replaceAll("\\.{1}(png|PNG|jpg|JPG|jpeg|JPEG)", "");
						if(!i.equals(icon)) icon=i;
						c++;
						eventNames += ev.getTitle();
						if(c<events.size()) eventNames += ",";
					}
					if(events.size()>0) {
						NotificationManager notifManager = (NotificationManager) getSystemService(context.NOTIFICATION_SERVICE);
						Notification notif = new Notification(R.drawable.app_icon, "Ethiopian calendar event notification", 
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
					Log.i(tag, "waitTime: " + waitMils);
					Thread.sleep(waitMils);
					run();
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
			
		});
		
		notifThread.start();
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
