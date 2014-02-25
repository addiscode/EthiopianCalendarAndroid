package com.addiscode.android;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHandler extends SQLiteOpenHelper{
	private static final String KEY_ID = "id";
	private static final String KEY_TITLE = "title";
	private static final String KEY_ICON = "icon";
	private static final String KEY_ETHIOPIAN = "isEthiopian";
	private static final String KEY_MONTH = "month";
	private static final String KEY_DAY = "day";
	private static final String KEY_MOVABLE = "isLeapYearMovable";
	private static final String DATABASE_NAME = "EthiopianCalendarDb";
	private static final String DATABASE_PATH = "/data/data/com.addiscode.android/databases/";
	private static final String TABLE_EVENTS = "events";
	private static final int DATABASE_VERSION = 1;
	private static final String tag = "DBHandler";
	private  final Context context;
	private SQLiteDatabase db;
	
	
	private static final String EventsQuery = "CREATE TABLE events (" +
					    "id" + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
					    "title" + " TEXT, " +
					    "icon"  + " TEXT, " + 
					    "isEthiopian" + " INTEGER, " + 
					    "month" + " INTEGER, " +
					    "day" + " INTEGER, " +
					    "isLeapYearMovable" + " INTEGER);";
	
	public DBHandler(Context context) {
		super(context, DATABASE_NAME, null, 1);
		this.context = context;
	}
	

	
	@Override
	public synchronized void onCreate(SQLiteDatabase db){
		createDataBase();
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldV, int newV){
		Log.i(tag, "Upgrading database from " + oldV + " to " + newV);
		db.execSQL("DROP TABLE IF EXISTS events");
		onCreate(db);
	}
		
	public DBHandler open() throws SQLException {
		db = getReadableDatabase();
		return this;
	}
	
	public List<Event> getAllEvents() {
		createDataBase();
		List<Event> events = new ArrayList<Event>();
		String selectQuery = "SELECT * FROM " + TABLE_EVENTS + " ORDER BY " +  KEY_MONTH + ", " + KEY_DAY + " ASC";
		Cursor cur = db.rawQuery(selectQuery, null);
		if(cur.moveToFirst()) {
			do {
				Event ev = new Event();
				ev.setId(cur.getInt(0));
				ev.setTitle(cur.getString(1));
				ev.setIcon(cur.getString(2));
				ev.setEthiopian((cur.getInt(3)==1)?true:false);
				ev.setMonth(cur.getInt(4));
				ev.setDay(cur.getInt(5));
				ev.setLeapYearMovable((cur.getInt(6)==1)?true:false);
				events.add(ev);
			}while(cur.moveToNext());
		}
		close();
		return events;
	}
	
	public List<Event> getEventsInMonth(int y, int m) {
		open();
		EthiopicCalendar etCal;
		int startDay, startMonth, startYear, endDay, endMonth, endYear;
		List<Event> events = new ArrayList<Event>();
		String selectQuery = "SELECT * FROM " + TABLE_EVENTS + " WHERE " + KEY_ETHIOPIAN + "= 1 and " + KEY_MONTH + "=" + m + " ORDER BY " 
							+ KEY_DAY + " ASC";
		Cursor cur = db.rawQuery(selectQuery, null);
		if(cur.moveToFirst()) {
			do{
				Event ev = new Event();
				ev.setId(cur.getInt(0));
				ev.setTitle(cur.getString(1));
				ev.setIcon(cur.getString(2));
				ev.setEthiopian((cur.getInt(3)==1)?true:false);
				ev.setMonth(cur.getInt(4));
				ev.setDay(cur.getInt(5));
				ev.setLeapYearMovable((cur.getInt(6)==1)?true:false);
				ev.setYear(y);
				events.add(ev);
			}while(cur.moveToNext());
		}
		etCal = new EthiopicCalendar(y, m, 1);
		int[] values = etCal.ethiopicToGregorian();
		startDay = values[2];
		startMonth = values[1];
		startYear = values[0];
		int maxDay = 30;
		if(m==13) maxDay = (y-1 % 4 == 0)?6:5;
		etCal.set(y, m, maxDay);
		etCal.ethiopicToGregorian();
		values = etCal.ethiopicToGregorian();
		endDay = values[2];
		endMonth = values[1];
		endYear = values[0];
		
		selectQuery = "SELECT * FROM " + TABLE_EVENTS + " WHERE (" + KEY_MONTH + "=" + startMonth    
					+ " OR " + KEY_MONTH + "=" + endMonth + ") and " + KEY_ETHIOPIAN + "=0" + " ORDER BY "
					+ KEY_DAY + ", " + KEY_MONTH + " ASC";
		Log.i(tag, selectQuery);
		
		etCal.set(y, 1, 1);
		int[] startValues = etCal.ethiopicToGregorian();
		int gregYear = startValues[0];
		etCal.set(gregYear+1, 1, 1);
		etCal.gregorianToEthiopic();
		int endOfMonth = startValues[1];
		int endOfDay = startValues[2];
		cur = db.rawQuery(selectQuery, null);
		if(cur.moveToFirst()) {
			do{
				int evMonth,evDay;
				evMonth = cur.getInt(4);
				evDay = cur.getInt(5);
				if((evMonth==startMonth && evDay>=startDay) ||
						(evMonth==endMonth && evDay<=endDay)) {
					if(evMonth>=endOfMonth && evDay>=endOfDay) gregYear+=1;
					etCal.set(gregYear, cur.getInt(4), cur.getInt(5));
					values = etCal.gregorianToEthiopic();
					Event ev = new Event();
					ev.setId(cur.getInt(0));
					ev.setTitle(cur.getString(1));
					ev.setIcon(cur.getString(2));
					ev.setEthiopian((cur.getInt(3)==1)?true:false);
					ev.setMonth(values[1]);
					ev.setDay(values[2]);
					ev.setLeapYearMovable((cur.getInt(6)==1)?true:false);
					ev.setYear(y);
					events.add(ev);
				}
			}while(cur.moveToNext());
		}
		close();
		return events;		
	}
	public List<Event> getEventsOn(int y, int m, int d) {
		open();
		List<Event> events = new ArrayList<Event>();
		String selectQuery = "SELECT * FROM " + TABLE_EVENTS + " WHERE " + KEY_MONTH +  "=" + m +
				" and " + KEY_DAY + "=" + d + " and " + KEY_ETHIOPIAN + "=" + 1;
		Cursor cur = db.rawQuery(selectQuery, null);
		Log.i(tag, selectQuery);
		if(cur.moveToFirst()) {
			do{
				Event ev = new Event();
				ev.setId(cur.getInt(0));
				ev.setTitle(cur.getString(1));
				ev.setIcon(cur.getString(2));
				ev.setEthiopian((cur.getInt(3)==1)?true:false);
				ev.setMonth(cur.getInt(4));
				ev.setDay(cur.getInt(5));
				ev.setLeapYearMovable((cur.getInt(6)==1)?true:false);
				ev.setYear(y);
				events.add(ev);
			}while(cur.moveToNext());
		}
		
		/*
		 * Fetch international events
		 */
		EthiopicCalendar etCal = new EthiopicCalendar(y, m, d);
		int[] values = etCal.ethiopicToGregorian();
		int gregMonth, gregDay;
		gregMonth = values[1];
		gregDay = values[2];
		selectQuery = "SELECT * FROM " + TABLE_EVENTS + " WHERE " + KEY_MONTH +  "=" + gregMonth +
				" and " + KEY_DAY + "=" + gregDay + " and " + KEY_ETHIOPIAN + "=" + 0;
		Log.i(tag, selectQuery);
		cur = db.rawQuery(selectQuery, null);
		if(cur.moveToFirst()) {
			do{
				Event ev = new Event();
				ev.setId(cur.getInt(0));
				ev.setTitle(cur.getString(1));
				ev.setIcon(cur.getString(2));
				ev.setEthiopian((cur.getInt(3)==1)?true:false);
				ev.setMonth(cur.getInt(4));
				ev.setDay(cur.getInt(5));
				ev.setLeapYearMovable((cur.getInt(6)==1)?true:false);
				ev.setYear(y);
				events.add(ev);
			}while(cur.moveToNext());
		}
		close();
		return events;
	}
	/*
	 * Initializing database
	 */
	
	 /**
     * Creates a empty database on the system and rewrites it with your own database.
     * */
    public synchronized void createDataBase(){
    	boolean dbExist = checkDataBase();
 
    	if(dbExist){
    		//do nothing - database already exist
    	}else{
 
    		//By calling this method and empty database will be created into the default system path
               //of your application so we are gonna be able to overwrite that database with our database.
        	try {
 
    			copyDataBase();
    			Log.i(tag, "Successfully copied");
 
    		} catch (Exception e) {
    			Log.i(tag, e.getMessage());
        		throw new Error("Error copying database");
        	}
    	}
    	
 
    }
    
    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     * @return true if it exists, false if it doesn't
     */
    private synchronized boolean checkDataBase(){
 
    	SQLiteDatabase checkDB = null;
 
    	try{
    		String myPath = DATABASE_PATH + DATABASE_NAME;
    		checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
 
    	}catch(Exception e){
    		e.printStackTrace();
    		//database does't exist yet.
    	}
 
    	if(checkDB != null){
 
    		checkDB.close();
 
    	}
 
    	return checkDB != null ? true : false;
    }
 
    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     * */
    private synchronized void copyDataBase() throws IOException{
 
    	//Open your local db as the input stream
    	 
    	InputStream myInput = context.getAssets().open(DATABASE_NAME);
 
    	// Path to the just created empty db
    	String outFileName = DATABASE_PATH + DATABASE_NAME;
 
    	//Open the empty db as the output stream
    	OutputStream myOutput = new FileOutputStream(outFileName);
 
    	//transfer bytes from the inputfile to the outputfile
    	byte[] buffer = new byte[1024];
    	int length=0;
    	while ((length = myInput.read(buffer))!=1){
    		Log.i(tag,"length: " + length);
    		myOutput.write(buffer, 0, length);
    	}
    	Log.i(tag, "Finished embedding database: " + length);
    	//Close the streams
    	myOutput.flush();
    	myOutput.close();
    	myInput.close();
 
    }
 
    public void openDataBase() throws SQLException{
    	//Open the database
        String myPath = DATABASE_PATH + DATABASE_NAME;
    	db = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
 
    }
 
    @Override
	public synchronized void close() {
 
    	    if(db != null)
    		    db.close();
 
    	    super.close();
 
	}

}
