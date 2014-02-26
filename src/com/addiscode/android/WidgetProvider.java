package com.addiscode.android;

import java.util.Calendar;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Typeface;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.TextView;

public class WidgetProvider extends AppWidgetProvider {
	private static PendingIntent service = null;
	public static String[] etMonths = {
		"መስከረም"," ጥቅምት","ህዳር","ታህሳስ","ጥር","የካቲት","መጋቢት","ሚያዚያ","ግንቦት","ሰኔ","ኅምሌ","ነኅሴ","ጳጉሜ"
	};
	public static String[] etDays = {
		"እሁድ","ሰኞ","ማክሰኞ","ረቡእ","ሃሙስ","አርብ","ቅዳሜ"
	};
	public void onUpdate(Context context, AppWidgetManager widgetManager,
			int[] allWidgetIds) {
		Log.i("DEBUG", "Ethiopian Calendar Widget initialized");
		final AlarmManager m = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		final Calendar TIME = Calendar.getInstance();
		TIME.set(Calendar.HOUR_OF_DAY, 0);
		TIME.set(Calendar.MINUTE, 1);
		TIME.set(Calendar.SECOND, 1);

		final Intent i = new Intent(context, UpdateWidgetService.class);
		if (WidgetProvider.service == null) {
			WidgetProvider.service = PendingIntent.getService(context, 0, i,
					PendingIntent.FLAG_CANCEL_CURRENT);
		}
		
		m.set(AlarmManager.RTC_WAKEUP, TIME.getTimeInMillis(),
				WidgetProvider.service);
	}

	public void onDisable(Context context) {
		Log.i("DEBUG", "Widget destroyed: " + WidgetProvider.service.toString());
		final AlarmManager m = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		m.cancel(WidgetProvider.service);
	}

	public void onDeleted(Context context, int[] widgets) {
		onDisable(context);
	}

}
