package com.addiscode.android;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MySheduleReceiver extends BroadcastReceiver {

	// Restart service every 30 seconds
	private  long REPEAT_TIME = 1000 * 30;
	private final String tag = "NOTIF_SHEDULER";
	@Override
	public void onReceive(Context context, Intent intent) {
		Calendar cal = Calendar.getInstance();
		int hour = cal.get(Calendar.HOUR);
		int min = cal.get(Calendar.MINUTE);
		int sec = cal.get(Calendar.SECOND);
		int waitHour, waitMin, waitSec;
		waitHour = (22 - hour) + 6;
		waitMin = 60 - min;
		waitSec = 60 - sec;
		
		Log.i(tag, " the time is " + hour + ":" + min);
		AlarmManager service = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Intent i = new Intent(context, MyReceiver.class);
		PendingIntent pending = PendingIntent.getBroadcast(context, 0, i,
				PendingIntent.FLAG_CANCEL_CURRENT);
		Intent notif_service = new Intent(context, NotificationService.class);
		context.startService(notif_service);
		// Start 30 seconds after boot completed
		cal.add(Calendar.SECOND, 30);
		//
		// Fetch every 30 seconds
		// InexactRepeating allows Android to optimize the energy consumption
		service.setInexactRepeating(AlarmManager.RTC_WAKEUP,
				cal.getTimeInMillis(), REPEAT_TIME, pending);

		// service.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
		// REPEAT_TIME, pending);

	}
}