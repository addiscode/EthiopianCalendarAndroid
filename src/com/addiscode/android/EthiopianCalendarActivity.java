package com.addiscode.android;

import java.util.Calendar;
import java.util.Date;

import android.app.Dialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TabHost;

public class EthiopianCalendarActivity extends TabActivity implements View.OnTouchListener {
    /** Called when the activity is first created. */
	private final Context context = this;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Resources res = getResources();
        TabHost tabHost = getTabHost();
        TabHost.TabSpec tabSpec;
        Intent intent;
        
        intent = new Intent().setClass(this, CalendarActivity.class);
        tabSpec = tabHost.newTabSpec("calendar").setIndicator("Calendar",res.getDrawable(R.drawable.calendar_tab)).setContent(intent);
        tabHost.addTab(tabSpec);
        
        intent = new Intent().setClass(this, ConverterActivity.class);
        tabSpec = tabHost.newTabSpec("converter").setIndicator("Converter",res.getDrawable(R.drawable.converter_tab)).setContent(intent);
        tabHost.addTab(tabSpec);
        
        tabHost.setCurrentTab(2);
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(1,1,1, "Addiscode").setIcon(R.drawable.addiscode_logo);
		menu.add(1, 2, 2, "About").setIcon(R.drawable.information);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		final Dialog dialog;
		Button okBtn;
		switch (item.getItemId()) {
		case 1:
			dialog = new Dialog(context);
			dialog.setContentView(R.layout.addiscode);
			dialog.setTitle("About addiscode");
			okBtn = (Button) dialog.findViewById(R.id.exitCompanyBtn);
			okBtn.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
			dialog.show();
			break;
		case 2:
			dialog = new Dialog(context);
			dialog.setContentView(R.layout.about);
			dialog.setTitle("About");
			okBtn = (Button) dialog.findViewById(R.id.exitAboutBtn);
			okBtn.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
			dialog.show();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		Log.i("DEBUG", "action: "+ event.getAction());
		return false;
	}
    

    
}