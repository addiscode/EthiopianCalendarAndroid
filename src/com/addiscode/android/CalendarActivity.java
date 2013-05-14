package com.addiscode.android;

import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint.FontMetrics;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class CalendarActivity extends Activity implements View.OnClickListener, View.OnTouchListener{
	public final Context context = this;
	public final String tag = "CALENDAR_ACTIVITY";
	private TableLayout calendarTable;
	private LinearLayout eventsList;
	private TextView todayLabel, selectedYearLabel, selectedMonthLabel;
	private int currentYear, currentMonth, currentDay, selectedYear, selectedMonth, selectedDay, weekDay;
	private String[] days = {"Sun","Mon","Tue","Wed","Thu","Fri","Sat"};
	private String[] ethMonths = {"Meskerem","Tikimit","Hidar","Tahisas","Tir","Yekatit","Megabit","Miyazia",
			"Ginbot","Sene","Hamile","Nehase","Pagume"};
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.calendar_layout);
		/*
		 * Now the GUI is loaded lets do some logic		 */
		todayLabel = (TextView) findViewById(R.id.todayLabel);
		eventsList = (LinearLayout) findViewById(R.id.eventsList);
		calendarTable = (TableLayout) findViewById(R.id.calendarTable);
		Calendar cal = Calendar.getInstance();
		int[] values = new EthiopicCalendar(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH)+1,
				cal.get(Calendar.DAY_OF_MONTH)).gregorianToEthiopic();
		weekDay = cal.get(Calendar.DAY_OF_WEEK);
		currentDay = values[2];
		currentMonth = values[1];
		currentYear = values[0];
		todayLabel.setText(values[2] + ", " + ethMonths[values[1]-1] + " " + values[0]);
		
		ImageView prevMonthBtn = (ImageView) findViewById(R.id.prevMonthBtn);
		ImageView nextMonthBtn = (ImageView) findViewById(R.id.nextMonthBtn);
		ImageView prevYearBtn = (ImageView) findViewById(R.id.prevYearBtn);
		ImageView nextYearbtn = (ImageView) findViewById(R.id.nextYearBtn);
		selectedMonthLabel = (TextView) findViewById(R.id.monthLabel);
		selectedYearLabel = (TextView) findViewById(R.id.yearLabel);
		prevMonthBtn.setOnClickListener(this);
		nextMonthBtn.setOnClickListener(this);
		prevYearBtn.setOnClickListener(this);
		nextYearbtn.setOnClickListener(this);
		
		drawCalendar(currentYear,currentMonth);
	}
	
	
	public void drawCalendar(int year, int month) {
		calendarTable.removeAllViews();
		selectedMonthLabel.setText(ethMonths[month-1]);
		selectedYearLabel.setText(String.valueOf(year));
		selectedYear = year;
		selectedMonth = month;
		TableRow tr;
		int i = 100;
		tr = new TableRow(context);
		tr.setBackgroundColor(Color.rgb(135, 186, 178));
		for(String day: days) {		
			TextView tv = new TextView(context);
			tv.setText(day);
			tv.setId(i);
			tv.setPadding(5, 5, 5, 5);
			tv.setTextColor(Color.rgb(75, 68, 26));
			tr.addView(tv);
			i++;
		}
		calendarTable.addView(tr,new TableLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
		/*
		 * Heading done
		 */
		int startDayOfWeek;
		
		EthiopicCalendar ethCal = new EthiopicCalendar(year,month,1);
		int[] values = ethCal.ethiopicToGregorian();
		Calendar cal = Calendar.getInstance();
		cal.set(values[0], values[1]-1, values[2]);
		startDayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
		int rows,day,maxDay;
		rows = (startDayOfWeek>6) ? 6:5;
		maxDay = 30;
		if(month==13) {
			maxDay = (((year+1)%4) == 0)?6:5;
			rows = (8-startDayOfWeek<maxDay)? 2: 1;
			}
		for(int r=0;r<rows;r++) {
			tr = new TableRow(context);
			for(int c=1;c<=7;c++) {
				day = (((7*r)+c)-startDayOfWeek)+1;
				TextView tv = new TextView(context);
				if(day>0 && day<=maxDay) {
					tv.setText(String.valueOf(day));
					tv.setTextColor(Color.rgb(28, 68, 62));
					tv.setTextSize(14);
					if(selectedYear>currentYear || (selectedYear==currentYear && selectedMonth>currentMonth) 
						|| (selectedYear==currentYear && selectedMonth==currentMonth && day >= currentDay))
						{registerForContextMenu(tv);}
					if(currentDay == day) {
						tv.setBackgroundColor(Color.rgb(243, 237, 243));
						tv.setTextColor(Color.rgb(75, 68, 26));
						tv.setTextSize(18);
					}	
				}
				tv.setPadding(5, 5, 5, 5);
				tr.addView(tv);
			}
			calendarTable.addView(tr,new TableLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
		}
		
		/*
		 * Fetch events
		 */
		DBHandler handler = new DBHandler(context);
		List<Event> events = handler.getEventsInMonth(selectedYear,selectedMonth);
		
		/*
		 * Display events
		 */
		eventsList.removeAllViews();
		LinearLayout listContainer;
		LinearLayout eventLayout;
		for(Event ev : events) {
			String name = ev.getIcon().replaceAll("\\.{1}(png|PNG|jpg|JPG|jpeg|JPEG)", "");
			listContainer = new LinearLayout(context);
			listContainer.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
			ImageView iv = new ImageView(context);
			iv.setImageResource(getDrawableId(name));
			iv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
			listContainer.addView(iv);
			eventLayout = new LinearLayout(context);
			eventLayout.setOrientation(LinearLayout.VERTICAL);
			eventLayout.setPadding(3, 0, 3, 0);
			eventLayout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
			TextView titleLabel = new TextView(context);
			titleLabel.setText(ev.getTitle());
			titleLabel.setTextColor(Color.rgb(249, 245, 141));
			TextView dateLabel = new TextView(context);
			dateLabel.setText(ev.getDay() + "/" + ev.getMonth() + "/" + ev.getYear());
			dateLabel.setTextSize(10);
			dateLabel.setTextColor(Color.WHITE);
			eventLayout.addView(titleLabel);
			eventLayout.addView(dateLabel);
			listContainer.addView(eventLayout);
			eventsList.addView(listContainer);
			
		}
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
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		final Dialog dialog = new Dialog(context);
		dialog.setContentView(R.layout.dobby);
		dialog.setTitle("Go to date");
		dialog.setCanceledOnTouchOutside(true);
		dialog.setCancelable(true);
		dialog.show();
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		Toast.makeText(context, "Item: " + item.getTitle(), Toast.LENGTH_SHORT).show();
		return super.onContextItemSelected(item);
	}


	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.prevMonthBtn:
			selectedMonth = (selectedMonth>1)?selectedMonth-1:13;
			drawCalendar(selectedYear, selectedMonth);
			break;
		case R.id.nextMonthBtn:
			selectedMonth = (selectedMonth==13)?1:selectedMonth+1;
			drawCalendar(selectedYear, selectedMonth);
			break;
		case R.id.prevYearBtn:
			selectedYear--;
			drawCalendar(selectedYear, selectedMonth);
			break;
		case R.id.nextYearBtn:
			selectedYear++;
			drawCalendar(selectedYear, selectedMonth);
			break;
		default:
			Toast.makeText(context, "view id: " + v.getId(), Toast.LENGTH_SHORT).show();
			break;
		}
	}


	public boolean onTouch(View v, MotionEvent event) {
		int action = event.getAction();
		Log.i("MULTI", "action: " + action);
		return true;
	}
}
