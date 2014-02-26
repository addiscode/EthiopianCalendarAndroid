package com.addiscode.android;

import java.util.Calendar;
import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;
import kankan.wheel.widget.adapters.NumericWheelAdapter;
import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

public class ConverterActivity extends Activity implements OnClickListener{
	private final Context context = this;
	private TextView outLabel;
	private ToggleButton sourceBtn;
	private ImageButton convBtn;
	private OnWheelChangedListener listener,ethiopicListener;
	private final String[] daysArray = {"Sun","Mon","Tue","Wed","Thu","Fri","Sat"};
	private final String[] monthsArray = {"Meskerem", "Tikimit", "Hidar", "Tahisas", "Tir",
            "Yekatit", "Megabit", "Miyazia", "Ginbot", "Sene", "Hamile", "Nehase","Pagume"};
	private final String[] gregMonths = {"January", "February", "March", "April", "May",
            "June", "July", "August", "September", "October", "November", "December"};
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.converter_layout);
        
        outLabel = (TextView) findViewById(R.id.outLabel);
        sourceBtn = (ToggleButton) findViewById(R.id.sourceBtn);
        convBtn = (ImageButton) findViewById(R.id.convertBtn);

        final WheelView month = (WheelView) findViewById(R.id.month);
        final WheelView year = (WheelView) findViewById(R.id.year);
        final WheelView day = (WheelView) findViewById(R.id.day);
        
        listener = new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                updateDays(year, month, day,false);
            }
        };
        ethiopicListener = new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                updateDays(year, month, day,true);
            }
        };
        
        initWheel(year, month, day);
        
        sourceBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if(sourceBtn.isChecked()) {
			        Calendar calendar = Calendar.getInstance();
			        int curMonth = calendar.get(Calendar.MONTH);
			        String months[] = new String[] {"January", "February", "March", "April", "May",
			                "June", "July", "August", "September", "October", "November", "December"};
			        DateArrayAdapter arrayAdapter = new DateArrayAdapter(context, months, curMonth);
			        month.setViewAdapter(arrayAdapter);
			        month.setCurrentItem(curMonth);
			        month.addChangingListener(listener);
			    
			        // year
			        int curYear = calendar.get(Calendar.YEAR);
			        DateNumericAdapter dateAdapter = new DateNumericAdapter(context, 0, curYear+300, curYear);
			        year.setViewAdapter(dateAdapter);
			        year.setCurrentItem(curYear);
			        year.addChangingListener(listener);
			        
			        //day
			        updateDays(year, month, day,false);
			        day.setCurrentItem(calendar.get(Calendar.DAY_OF_MONTH) - 1);
				}
				else {
					initWheel(year, month, day);
				}
			}
			
		});
        
        
        convBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				int[] values;
				int weekDay;
				int y = year.getCurrentItem();
				int m = month.getCurrentItem()+1;
				int d = day.getCurrentItem()+1;
				Calendar cal = Calendar.getInstance();
				EthiopicCalendar etCal;
				String monthString;
				if(sourceBtn.isChecked()) {//we know is's from gregorian
					cal.set(y, m-1, d);
					weekDay = cal.get(Calendar.DAY_OF_WEEK);
					etCal = new EthiopicCalendar(y, m, d);
					values = etCal.gregorianToEthiopic();
					monthString = monthsArray[values[1]-1];
				}
				else {
					etCal = new EthiopicCalendar(y, m, d);
					values = etCal.ethiopicToGregorian();
					cal.set(values[0],values[1]-1,values[2]);
					weekDay = cal.get(Calendar.DAY_OF_WEEK);
					monthString = gregMonths[values[1]-1];
				}
				
				outLabel.setText(daysArray[weekDay-1] + ", " + values[2] + " " + monthString + " " + values[0]);
			}
		});
        
    }
    
    public void initWheel(WheelView year,WheelView month, WheelView day) {
    	Calendar calendar = Calendar.getInstance();
    	EthiopicCalendar etCal = new EthiopicCalendar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)+1,
    			calendar.get(Calendar.DAY_OF_MONTH));
    	int[] values = etCal.gregorianToEthiopic();
        int curMonth = values[1]-1;
        String months[] = new String[] {"Meskerem", "Tikimit", "Hidar", "Tahisas", "Tir",
                "Yekatit", "Megabit", "Miyazia", "Ginbot", "Sene", "Hamile", "Nehase","Pagume"};
        DateArrayAdapter arrayAdapter = new DateArrayAdapter(context, months, curMonth);
        month.setViewAdapter(arrayAdapter);
        month.setCurrentItem(curMonth);
        month.addChangingListener(ethiopicListener);
        month.setCyclic(true);
        // year
        int curYear = values[0];
        DateNumericAdapter dateAdapter = new DateNumericAdapter(context, 0, curYear+300, curYear);
        year.setViewAdapter(dateAdapter);
        year.setCurrentItem(curYear);
        year.addChangingListener(ethiopicListener);
        
        //day
        updateDays(year, month, day,true);
        day.setCurrentItem(values[2]-1);
        day.setCyclic(true);
    }
    /**
     * Updates day wheel. Sets max days according to selected month and year
     */
    void updateDays(WheelView year, WheelView month, WheelView day,boolean isEthiopic) {
    	if(isEthiopic) {
    		Calendar calendar = Calendar.getInstance();
    		EthiopicCalendar etCal = new EthiopicCalendar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)+1,
        			calendar.get(Calendar.DAY_OF_MONTH));
        	int[] values = etCal.gregorianToEthiopic();
        	
        	
        	int maxDays = 30;
        	if(month.getCurrentItem()==12) maxDays = (((year.getCurrentItem()+1) % 4) == 0)?6:5;
        	Log.i("Calendar_builder", month.getCurrentItem() + "/" + year.getCurrentItem());
	        day.setViewAdapter(new DateNumericAdapter(this, 1, maxDays, calendar.get(Calendar.DAY_OF_MONTH) - 1));
	        int curDay = Math.min(maxDays, values[2]);
	        day.setCurrentItem(curDay - 1, true);
    	}
    	else {
	        Calendar calendar = Calendar.getInstance();
	        calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + year.getCurrentItem());
	        calendar.set(Calendar.MONTH, month.getCurrentItem());
	        
	        int maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
	        day.setViewAdapter(new DateNumericAdapter(this, 1, maxDays, calendar.get(Calendar.DAY_OF_MONTH) - 1));
	        int curDay = Math.min(maxDays, day.getCurrentItem() + 1);
	        day.setCurrentItem(curDay - 1, true);
    	}
    }
    
    /**
     * Adapter for numeric wheels. Highlights the current value.
     */
    private class DateNumericAdapter extends NumericWheelAdapter {
        // Index of current item
        int currentItem;
        // Index of item to be highlighted
        int currentValue;
        
        /**
         * Constructor
         */
        public DateNumericAdapter(Context context, int minValue, int maxValue, int current) {
            super(context, minValue, maxValue);
            this.currentValue = current;
            setTextSize(16);
        }
        
        @Override
        protected void configureTextView(TextView view) {
            super.configureTextView(view);
            if (currentItem == currentValue) {
                view.setTextColor(0xFF0000F0);
            }
            view.setTypeface(Typeface.SANS_SERIF);
        }
        
        @Override
        public View getItem(int index, View cachedView, ViewGroup parent) {
            currentItem = index;
            return super.getItem(index, cachedView, parent);
        }
    }
    
    /**
     * Adapter for string based wheel. Highlights the current value.
     */
    private class DateArrayAdapter extends ArrayWheelAdapter<String> {
        // Index of current item
        int currentItem;
        // Index of item to be highlighted
        int currentValue;
        
        /**
         * Constructor
         */
        public DateArrayAdapter(Context context, String[] items, int current) {
            super(context, items);
            this.currentValue = current;
            setTextSize(16);
        }
        
        @Override
        protected void configureTextView(TextView view) {
            super.configureTextView(view);
            if (currentItem == currentValue) {
                view.setTextColor(0xFF0000F0);
            }
            view.setTypeface(Typeface.SANS_SERIF);
        }
        
        @Override
        public View getItem(int index, View cachedView, ViewGroup parent) {
            currentItem = index;
            return super.getItem(index, cachedView, parent);
        }
    }

	public void onClick(View v) {
		if(sourceBtn.isChecked()) {
			// month
		}
	}
}