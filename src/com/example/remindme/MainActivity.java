package com.example.remindme;

//adb -d forward tcp:5601 tcp:5601 Connect Android Wear to phone using emulator
//adb shell dumpsys alarm > dump.txt To see all AlarmManager in the system

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;


@SuppressLint("InflateParams") public class MainActivity extends Activity {
	
	int pHour, pMinute, pYear = -1, pMonth, pDay;
	Calendar c;
	PendingIntent pendingIntent;
	AlarmManager alarmManager;
	SharedPreferences shapre;
	Intent intentOpen;
	int REQUEST_OK = 1;
	String action = "TASK_GOT";
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        intentOpen = new Intent(this, AlarmReceiver.class);
        intentOpen.setAction(action);
    }
    
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
        shapre = getSharedPreferences("logincredentials", 0);
        if (shapre.getString("username", "0000").equals("0000"))
        	startActivity(new Intent(this, LoginActivity.class));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.menu, menu);
    	return true;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch(item.getItemId()) {
		case R.id.about: startActivity(new Intent(this, AboutActivity.class));
			return true;
		case R.id.add: showDialog(0);
			return true;
		case R.id.login: startActivity(new Intent(this, LoginActivity.class));
			return true;
		case R.id.mic: speechToText();
		default: return super.onOptionsItemSelected(item);
		}
	}
	public void startToday(View view) {
		startActivity(new Intent(this, Today.class));
	}
	public void startNext(View view) {
		startActivity(new Intent(this, Next.class));
	}
	public void startThisMonth(View view) {
		startActivity(new Intent(this, ThisMonth.class));
	}
	public void startAll(View view) {
		startActivity(new Intent(this, All.class));
	}
	@SuppressWarnings("deprecation")
	public void showTimePickerDialog(View view) {
	    showDialog(1);
	}
	@SuppressWarnings("deprecation")
	public void showDatePickerDialog(View view) {
		showDialog(2);
	}
	@Override
	@Deprecated
	protected Dialog onCreateDialog(int id) {
		// TODO Auto-generated method stub
		switch(id) {
		case 0: AlertDialog.Builder builder = new AlertDialog.Builder(this);
				LayoutInflater inflate = this.getLayoutInflater();
				final View layout = inflate.inflate(R.layout.dialog_layout, null);
				final EditText reminderText = (EditText)layout.findViewById(R.id.reminder);
				
				builder.setView(layout);
				
				builder.setPositiveButton("Set", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						if (reminderText.getText().toString().equals(""))
							Toast.makeText(getBaseContext(), "Please enter a to-do", Toast.LENGTH_SHORT).show();
						else
						if (pYear == -1)
							Toast.makeText(getBaseContext(), "Reminder not set, Didn't set Time & Date", Toast.LENGTH_SHORT).show();
						else {
							int id = 0;
							String sql = reminderText.getText().toString();
							SQLiteDatabase myDatabase = openOrCreateDatabase("todo.db", MODE_PRIVATE, null);
							myDatabase.execSQL("CREATE TABLE IF NOT EXISTS todo(task VARCHAR, hour INTEGER, minute INTEGER, day INTEGER, month INTEGER, year INTEGER, id INTEGER PRIMARY KEY);");
							
							Cursor resultSet = myDatabase.rawQuery("SELECT * FROM todo", null);
							try {
								resultSet.moveToLast();
								id = Integer.parseInt(resultSet.getString(6));
								id++;
							}
							catch(Exception e) {
								e.printStackTrace();
							}
							String query = "INSERT INTO todo VALUES('"+sql+"', "+pHour+", "+pMinute+", "+pDay+", "+pMonth+", "+pYear+", "+id+");";
							Calendar c = Calendar.getInstance();
							c.set(pYear, pMonth, pDay);
							if (c.before(Calendar.getInstance())) {
								Toast.makeText(getBaseContext(), "The entered date is already over. Reminder not set", Toast.LENGTH_SHORT).show();
							}
							else {								
								myDatabase.execSQL(query);
								intentOpen = new Intent(getBaseContext(), AlarmReceiver.class);
								intentOpen.setAction(action);
								intentOpen.putExtra("task", sql);
								intentOpen.putExtra("id", id);
								intentOpen.putExtra("year", pYear);
								intentOpen.putExtra("month", pMonth);
								intentOpen.putExtra("day", pDay);
								intentOpen.putExtra("hour", pHour);
								intentOpen.putExtra("minute", pMinute);
								pendingIntent = PendingIntent.getBroadcast(getBaseContext(), id, intentOpen, Intent.FILL_IN_DATA);
								c.set(pYear, pMonth, pDay, pHour, pMinute);
								
								alarmManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
								//
								
/*								intentOpen = new Intent(getBaseContext(), AlarmReceiver.class);
								intentOpen.setAction(action);

								pendingIntent = PendingIntent.getBroadcast(getBaseContext(), id, intentOpen, Intent.FILL_IN_DATA);
								
								pendingIntent.cancel();
								alarmManager.cancel(pendingIntent);*/

								//
								Log.d("PendingIntent", id+"");
								Log.d("Year", pYear+"");
								Log.d("Month", pMonth+"");
								Log.d("Day", pDay+"");
								Log.d("Hour", pHour+"");
								Log.d("Minute", pMinute+"");
								//
								
							}
							myDatabase.close();	
							pYear = -1;
							reminderText.setText("");														
						}
						}
				});
				builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						pYear = -1;
						reminderText.setText("");
					}
				});
				builder.setOnDismissListener(new OnDismissListener() {
					
					@Override
					public void onDismiss(DialogInterface dialog) {
						// TODO Auto-generated method stub
						pYear = -1;
						reminderText.setText("");
					}
				});
				return builder.create();
		case 1: c = Calendar.getInstance();
        		pHour = c.get(Calendar.HOUR_OF_DAY);
        		pMinute = c.get(Calendar.MINUTE);
				return new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
	    		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
	        	pHour = hourOfDay;
	        	pMinute = minute;
	        	Toast.makeText(getBaseContext(), "Time set for "+pHour+" : "+pMinute, Toast.LENGTH_SHORT).show();
	    		}
	        }, pHour, pMinute, false);
		case 2: c = Calendar.getInstance();
				pYear = c.get(Calendar.YEAR);
				pMonth = c.get(Calendar.MONTH);
				pDay = c.get(Calendar.DAY_OF_MONTH);
				return new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
			 
				public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                pYear = year;
                pMonth = monthOfYear;
                pDay = dayOfMonth;
	        	Toast.makeText(getBaseContext(), "Date set for "+pDay+"/"+pMonth+"/"+pYear, Toast.LENGTH_SHORT).show();
				}
			}, pYear, pMonth, pDay);
		}
		return super.onCreateDialog(id);
	}
	
	public void speechToText() {
		Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");
		try {
			startActivityForResult(i, REQUEST_OK);
		} catch (Exception e) {
			Toast.makeText(this, "Error translating audio", Toast.LENGTH_LONG).show();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode==REQUEST_OK  && resultCode==RESULT_OK) {
			ArrayList<String> thingsYouSaid = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			try {
				String timeArray[] = new String[2];
				int timeOnly[] = new int[2];
				int dateArray[] = new int[3];
				int i = 0;
				String parsedText = thingsYouSaid.get(0);
				Toast.makeText(this, parsedText, Toast.LENGTH_SHORT).show();
				int at = parsedText.lastIndexOf("at");
				int on = parsedText.lastIndexOf("On");
				String task = parsedText.substring(0, at - 1);
				char c = Character.toUpperCase(parsedText.charAt(0));
				task = new StringBuilder(task).deleteCharAt(0).toString();
				task = c + task;
				String time = parsedText.substring(at+3, on-1);
				for (String obj : time.split(" ")) {
					timeArray[i] = obj;
					i++;
				}
				i = 0;
				for (String obj : timeArray[0].split(":| ")) {
					timeOnly[i] = Integer.parseInt(obj);
					i++;
				}
				i = 0;
				String date = parsedText.substring(on + 3);
				for (String obj : date.split(" ")) {
					dateArray[i] = Integer.parseInt(obj);
					i++;
				}
				i = 0;
				String realDate = parsedText.substring(parsedText.lastIndexOf("On")+3);
				for (String obj : realDate.split(" ")) {
					dateArray[i] = Integer.parseInt(obj);
					i++;
				}
				if (timeArray[1].equals("p.m.")) {
					if (timeOnly[0]!=12) 
						timeOnly[0]+=12;
				}
				pHour = timeOnly[0];
				pMinute = timeOnly[1];
				pDay = dateArray[0];
				pMonth = dateArray[1];
				pYear = dateArray[2];
				Log.d("task", task);
				Log.d("pHour", pHour+"");
				Log.d("pMinute", pMinute+"");
				Log.d("pDay", pDay+"");
				Log.d("pMonth", pMonth+"");
				Log.d("pYear", pYear+"");
				int id = 0;
				SQLiteDatabase myDatabase = openOrCreateDatabase("todo.db", MODE_PRIVATE, null);
				myDatabase.execSQL("CREATE TABLE IF NOT EXISTS todo(task VARCHAR, hour INTEGER, minute INTEGER, day INTEGER, month INTEGER, year INTEGER, id INTEGER PRIMARY KEY);");
				
				Cursor resultSet = myDatabase.rawQuery("SELECT * FROM todo", null);
				try {
					resultSet.moveToLast();
					id = Integer.parseInt(resultSet.getString(6));
					id++;
				}
				catch(Exception e) {
					e.printStackTrace();
				}
				String query = "INSERT INTO todo VALUES('"+task+"', "+pHour+", "+pMinute+", "+pDay+", "+(pMonth-1)+", "+pYear+", "+id+");";
				if (isValidDate(pYear+"-"+pMonth+"-"+pDay)) {
					Calendar cal = Calendar.getInstance();
					cal.set(pYear, pMonth, pDay);
					if (cal.before(Calendar.getInstance())) {
						Toast.makeText(this, "The entered date is already over. Reminder not set", Toast.LENGTH_SHORT).show();
					}
					else {							
						myDatabase.execSQL(query);
						//
						
						intentOpen = new Intent(getBaseContext(), AlarmReceiver.class);
						intentOpen.setAction(action);
						intentOpen.putExtra("task", task);
						intentOpen.putExtra("id", id);
						intentOpen.putExtra("year", pYear);
						intentOpen.putExtra("month", pMonth-1);
						intentOpen.putExtra("day", pDay);
						intentOpen.putExtra("hour", pHour);
						intentOpen.putExtra("minute", pMinute);
						pendingIntent = PendingIntent.getBroadcast(getBaseContext(), id, intentOpen, Intent.FILL_IN_DATA);
						cal.set(pYear, pMonth, pDay, pHour, pMinute);
						
						alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
						//
					}
				}
				else 
					Toast.makeText(this, "Unable to parse Audio", Toast.LENGTH_SHORT).show();
				myDatabase.close();
				pYear = -1;
			}
			catch(Exception e) {
				Log.d("Exception", e.toString());
				Toast.makeText(this, "Eg: Buy milk at 10:30 pm on 7 11 2014", Toast.LENGTH_SHORT).show();
			}
		}
	}
	  @SuppressLint("SimpleDateFormat") public boolean isValidDate(String inDate) {
		    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		    dateFormat.setLenient(false);
		    try {
		      dateFormat.parse(inDate.trim());
		    } catch (ParseException pe) {
		      return false;
		    }
		    return true;
		  }
	
	
	   
}