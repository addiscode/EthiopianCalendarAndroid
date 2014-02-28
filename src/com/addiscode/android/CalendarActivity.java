package com.addiscode.android;

import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class CalendarActivity extends Activity implements View.OnClickListener, View.OnTouchListener {
	public final Context context = this;
	public final String tag = "CALENDAR_ACTIVITY";
	private TableLayout calendarTable;
	private List<Event> eventsThisMonth;
	private DBHandler dbHandler;
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
		calendarTable = (TableLayout) findViewById(R.id.calendarTable);
		Calendar cal = Calendar.getInstance();
		int[] values = new EthiopicCalendar(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH)+1,
				cal.get(Calendar.DAY_OF_MONTH)).gregorianToEthiopic();
		weekDay = cal.get(Calendar.DAY_OF_WEEK);
		currentDay = values[2];
		currentMonth = values[1];
		currentYear = values[0];
		todayLabel.setText("Today is " + values[2] + ", " + ethMonths[values[1]-1] + " " + values[0]);
		
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
		
		dbHandler = new DBHandler(context);
		eventsThisMonth = dbHandler.getEventsInMonth(selectedYear, selectedMonth);
		//put weekdays in the first row
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
		
		LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	    llp.setMargins(50, 0, 0, 0);
		
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
					
					//create a new text view
					tv.setText(String.valueOf(day));
					tv.setTextColor(Color.rgb(0, 253, 255));
					tv.setTextSize(26);
					tv.setGravity(Gravity.CENTER);
//					tv.setBackgroundColor(Color.rgb(243, 237, 243));
					
					
					if(selectedYear>currentYear || (selectedYear==currentYear && selectedMonth>currentMonth) 
						|| (selectedYear==currentYear && selectedMonth==currentMonth && day >= currentDay))
						{registerForContextMenu(tv);}
					
					//change color for the current date
					if(currentDay == day) {
						tv.setTextColor(Color.rgb(255, 255, 0));
					}
					
					//found an event on date? put a ring on it :)
					int eventFound = checkEvent(day);
					if(eventFound > 0) {
						if(eventFound == 2)
							tv.setBackgroundResource(R.drawable.date_circle);
						else
							tv.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
					} 
				}
				tv.setPadding(5, 5, 5, 5);
				tr.addView(tv);
			}
			calendarTable.addView(tr,new TableLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
		}
		
		
	}
	
	private int checkEvent(int selectedDay) {
		int eventFound = Event.NO_EVENT;
		for(Event ev: eventsThisMonth) {
			if(ev.getDay() == selectedDay) {
				eventFound = Event.IS_INTERNATIONAL;
				if(ev.isEthiopian()) return Event.IS_ETHIOPIAN;
			}
		}
		return eventFound;
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
		// TODO Auto-generated method stub
		dumpEvent(event);
		return false;
	}
	
	private void dumpEvent(MotionEvent event) {
			final String TAG = "TOUCH";
		   String names[] = { "DOWN" , "UP" , "MOVE" , "CANCEL" , "OUTSIDE" ,
		      "POINTER_DOWN" , "POINTER_UP" , "7?" , "8?" , "9?" };
		   StringBuilder sb = new StringBuilder();
		   int action = event.getAction();
		   int actionCode = action & MotionEvent.ACTION_MASK;
		   sb.append("event ACTION_" ).append(names[actionCode]);
		   if (actionCode == MotionEvent.ACTION_POINTER_DOWN
		         || actionCode == MotionEvent.ACTION_POINTER_UP) {
		      sb.append("(pid " ).append(
		      action >> MotionEvent.ACTION_POINTER_ID_SHIFT);
		      sb.append(")" );
		   }
		   sb.append("[" );
		   for (int i = 0; i < event.getPointerCount(); i++) {
		      sb.append("#" ).append(i);
		      sb.append("(pid " ).append(event.getPointerId(i));
		      sb.append(")=" ).append((int) event.getX(i));
		      sb.append("," ).append((int) event.getY(i));
		      if (i + 1 < event.getPointerCount())
		         sb.append(";" );
		   }
		   sb.append("]" );
		   Log.d(TAG, sb.toString());
		}

}
