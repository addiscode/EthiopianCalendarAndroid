package com.addiscode.android;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MyReceiver extends BroadcastReceiver {
	private static PendingIntent notificationService = null;
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i("DEBUG", "Boot Completion event recieved");
		final AlarmManager m = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		final Calendar TIME = Calendar.getInstance();
		TIME.set(Calendar.HOUR_OF_DAY, 0);
		TIME.set(Calendar.MINUTE, 1);
		TIME.set(Calendar.SECOND, 1);
		final Intent notificationIntent = new Intent(context, NotificationService.class);
		
		if(MyReceiver.notificationService == null) {
			MyReceiver.notificationService = PendingIntent.getService(context, 0, notificationIntent, 0);
		}
		m.set(AlarmManager.RTC_WAKEUP, TIME.getTimeInMillis(),
				MyReceiver.notificationService);
		
	}

}
