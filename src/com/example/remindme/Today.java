package com.example.remindme;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TimePicker;
import android.widget.Toast;

public class Today extends Activity{

	ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();
	HashMap<String,String> item = new HashMap<String, String>();
	Calendar calendar = Calendar.getInstance();
	Calendar today = Calendar.getInstance();
	String months[] = {"JAN", "FEB", "MAR", "APR", "MAY", "JUNE", "JUL", "AUG", "SEPT", "OCT", "NOV", "DEC"};
	String days[] = {"SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"};
	String dateString = "";
	String msgOnCreateContextMenu, msgOnly, timeOnly;
	int id, listId;
	int pHour, pMinute, pYear = -1, pMonth, pDay;
	ListView listView;
	Calendar c;
	ContentValues contentValues;	
	
	AlarmManager alarmManager;
	String action = "TASK_GOT"; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.all_layout);
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		try 
		{	
			SQLiteDatabase myDatabase = openOrCreateDatabase("todo.db", MODE_PRIVATE, null);
			Cursor resultSet = myDatabase.rawQuery("SELECT * FROM todo", null);
			
			if (resultSet.moveToFirst()){
				do{
					item = new HashMap<String, String>();
					calendar.set(Integer.parseInt(resultSet.getString(5)), Integer.parseInt(resultSet.getString(4)), Integer.parseInt(resultSet.getString(3)));
					if (today.equals(calendar))
					{
						dateString = months[Integer.parseInt(resultSet.getString(4))]+" "+String.format("%02d", Integer.parseInt(resultSet.getString(3)))
								+", "+days[calendar.get(Calendar.DAY_OF_WEEK)-1]+" "
								+String.format("%02d", Integer.parseInt(resultSet.getString(1)))+":"
								+String.format("%02d", Integer.parseInt(resultSet.getString(2)));
						
						item.put("appointment", dateString);
						item.put("task", resultSet.getString(0));
						item.put("id", resultSet.getString(6));
						list.add(item);
					}
				}while(resultSet.moveToNext());
			}
			
			resultSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
    	
    	SimpleAdapter adapter = new SimpleAdapter(this, list, R.layout.listview_layout_today, new String[] {"appointment", "task"}, new int[] {R.id.row1, R.id.row2});
    	listView = (ListView)findViewById(R.id.listview);
    	listView.setAdapter(adapter);
    	registerForContextMenu(listView);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		super.onCreateContextMenu(menu, v, menuInfo);
		if (v.getId()==R.id.listview) {
			
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;

			listId = info.position;
			msgOnCreateContextMenu = list.get(listId).get("task") + " on " +list.get(listId).get("appointment");
			msgOnly = list.get(listId).get("task");
			timeOnly = "on "+ list.get(listId).get("appointment");
			id = Integer.parseInt(list.get(listId).get("id"));
			
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.context_menu, menu);
		}
	}

	@SuppressWarnings("deprecation")
	public void showTimePickerDialog(View view) {
	    showDialog(1);
	}
	@SuppressWarnings("deprecation")
	public void showDatePickerDialog(View view) {
		showDialog(2);
	}

	@SuppressLint("InflateParams") @Override
	@Deprecated
	protected Dialog onCreateDialog(int ids, Bundle args) {
		// TODO Auto-generated method stub
		switch(ids) {
		case 0: AlertDialog.Builder builder = new AlertDialog.Builder(this);
		LayoutInflater inflate = this.getLayoutInflater();
		final View layout = inflate.inflate(R.layout.dialog_layout, null);
		final EditText reminderText = (EditText)layout.findViewById(R.id.reminder);
		reminderText.setText(msgOnly);
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
					String sql = reminderText.getText().toString();
					contentValues = new ContentValues();
					contentValues.put("task", sql);
					contentValues.put("hour", pHour);
					contentValues.put("minute", pMinute);
					contentValues.put("day", pDay);
					contentValues.put("month", pMonth);
					contentValues.put("year", pYear);
					
					//
					Intent intentOpen = new Intent(getBaseContext(), AlarmReceiver.class);
					intentOpen.setAction(action);

					PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), id, intentOpen, Intent.FILL_IN_DATA);
					
					pendingIntent.cancel();
					alarmManager.cancel(pendingIntent);
					
					intentOpen = new Intent(getBaseContext(), AlarmReceiver.class);
					intentOpen.setAction("TASK_GOT");
					intentOpen.putExtra("task", sql);
					intentOpen.putExtra("id", id);
					intentOpen.putExtra("year", pYear);
					intentOpen.putExtra("month", pMonth);
					intentOpen.putExtra("day", pDay);
					intentOpen.putExtra("hour", pHour);
					intentOpen.putExtra("minute", pMinute);
					pendingIntent = PendingIntent.getBroadcast(getBaseContext(), id, intentOpen, PendingIntent.FLAG_CANCEL_CURRENT);
					c.set(pYear, pMonth, pDay, pHour, pMinute);
					
					alarmManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
					
					//
					SQLiteDatabase myDatabase = openOrCreateDatabase("todo.db", MODE_PRIVATE, null);
					myDatabase.update("todo", contentValues, "id = "+id, null);
					list.remove(listId);
					
					item = new HashMap<String, String>();					
					calendar.set(pYear, pMonth, pDay);
					dateString = months[pMonth]+" "+String.format("%02d", pDay)+", "+days[calendar.get(Calendar.DAY_OF_WEEK)-1]+" "
							+String.format("%02d", pHour)+":"+String.format("%02d", pMinute);
					
					item.put("appointment", dateString);
					item.put("task", sql);
					item.put("id", id+"");
					list.add(item);
					listView.invalidateViews();
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
		default: return super.onCreateDialog(id, args);
		}
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		
		SQLiteDatabase myDatabase = openOrCreateDatabase("todo.db", MODE_PRIVATE, null);
		
		switch(item.getItemId()) {
		case R.id.edit: showDialog(0);
			return true;
		case R.id.delete: 
			Intent intentOpen = new Intent(getBaseContext(), AlarmReceiver.class);
			intentOpen.setAction(action);

			PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), id, intentOpen, Intent.FILL_IN_DATA);
			
			pendingIntent.cancel();
			alarmManager.cancel(pendingIntent);
			
			myDatabase.delete("todo", "id="+id, null);
			list.remove(listId);
			listView.invalidateViews();
			return true;
		case R.id.share: Intent share = new Intent();
			share.setAction(Intent.ACTION_SEND);
			share.setType("text/plain");
			share.putExtra(Intent.EXTRA_TEXT, msgOnCreateContextMenu);
			startActivity(Intent.createChooser(share, "Share RemindMe."));
			return true;
		case R.id.pushtowear: pushToWear();
			return true;
		default: return super.onContextItemSelected(item);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch(item.getItemId()) {
		case android.R.id.home: Intent i = new Intent(this, MainActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
			return true;
		default: return super.onOptionsItemSelected(item); 
		}
	}
	
	public void pushToWear() {
		int notificationId = id;
		String eventTitle = msgOnly, GROUP_KEY = "remindme", eventLocation = timeOnly;
		Intent notificationAllIntent = new Intent(this, All.class);
		notificationAllIntent.putExtra("Notification", id);
		PendingIntent pIntent = PendingIntent.getActivity(this, id, notificationAllIntent, 0);
		
		Intent shareIntent = new Intent();
		shareIntent.setAction(Intent.ACTION_SEND);
		shareIntent.setType("text/plain");
		shareIntent.putExtra(Intent.EXTRA_TEXT, msgOnCreateContextMenu);
		PendingIntent pShareIntent = PendingIntent.getActivity(this, id, shareIntent, PendingIntent.FLAG_CANCEL_CURRENT);
		
		Intent service = new Intent(this, DoneService.class);
		service.putExtra("id", id);
		PendingIntent pService = PendingIntent.getService(this, id, service, PendingIntent.FLAG_CANCEL_CURRENT);	
		
		Intent emailService = new Intent(this, EmailService.class);
		emailService.putExtra("mail", msgOnCreateContextMenu);
		PendingIntent eService = PendingIntent.getService(this, id, emailService, PendingIntent.FLAG_CANCEL_CURRENT);
		
		Notification notificationBuilder = new NotificationCompat.Builder(this)
		.setSmallIcon(R.drawable.ic_notification_icon)
        .setContentTitle(eventTitle)
        .setContentText(eventLocation)
        .setContentIntent(pIntent)
        .setGroup(GROUP_KEY)
        .setAutoCancel(true)
        .addAction(R.drawable.ic_action_done, "Done", pService)
        .addAction(R.drawable.ic_action_mail, "E-Mail", eService)
        .addAction(R.drawable.ic_action_share, "Share", pShareIntent)
        .build();
		
		NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
		
		notificationManager.notify(notificationId, notificationBuilder);
	}	
}
