package com.example.remindme;

import java.util.Calendar;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver{

	String action = "TASK_GOT";
	String months[] = {"JAN", "FEB", "MAR", "APR", "MAY", "JUNE", "JUL", "AUG", "SEPT", "OCT", "NOV", "DEC"};
	String days[] = {"SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"};
	
	@Override
	public void onReceive(Context arg0, Intent arg1) {
		// TODO Auto-generated method stub
		if (action.equals(arg1.getAction())){
			Log.d(action, arg1.getStringExtra("task"));
			pushToWear(arg0, arg1);
		}
	}
	public void pushToWear(Context context, Intent arg1) {
		int id = arg1.getIntExtra("id", -1);
		Calendar c = Calendar.getInstance();
		c.set(arg1.getIntExtra("year", -1), arg1.getIntExtra("month", -1), arg1.getIntExtra("day", -1), arg1.getIntExtra("hour", -1), arg1.getIntExtra("minute", -1));
		String timeOnly = "on "+months[c.get(Calendar.MONTH)];
		timeOnly = timeOnly + " " + String.format("%02d", arg1.getIntExtra("day", -1))+", "+days[c.get(Calendar.DAY_OF_WEEK)-1];
		timeOnly = timeOnly + " " + String.format("%02d", arg1.getIntExtra("hour", -1))+":";
		timeOnly = timeOnly + String.format("%02d", arg1.getIntExtra("minute", -1));
		String msgOnly = arg1.getStringExtra("task");
		String msgOnCreateContextMenu = msgOnly + " " + timeOnly;
				
		int notificationId = id;
		String eventTitle = msgOnly, eventLocation = timeOnly;
		Intent notificationAllIntent = new Intent(context, All.class);
		notificationAllIntent.putExtra("Notification", id);
		PendingIntent pIntent = PendingIntent.getActivity(context, id, notificationAllIntent, 0);
		
		Intent shareIntent = new Intent();
		shareIntent.setAction(Intent.ACTION_SEND);
		shareIntent.setType("text/plain");
		shareIntent.putExtra(Intent.EXTRA_TEXT, msgOnCreateContextMenu);
		PendingIntent pShareIntent = PendingIntent.getActivity(context, id, shareIntent, PendingIntent.FLAG_CANCEL_CURRENT);
		
		Intent service = new Intent(context, DoneService.class);
		service.putExtra("id", id);
		PendingIntent pService = PendingIntent.getService(context, id, service, PendingIntent.FLAG_CANCEL_CURRENT);	
		
		Intent emailService = new Intent(context, EmailService.class);
		emailService.putExtra("mail", msgOnCreateContextMenu);
		PendingIntent eService = PendingIntent.getService(context, id, emailService, PendingIntent.FLAG_CANCEL_CURRENT);
		
		Notification notificationBuilder = new NotificationCompat.Builder(context)
		.setSmallIcon(R.drawable.ic_notification_icon)
        .setContentTitle(eventTitle)
        .setContentText(eventLocation)
        .setContentIntent(pIntent)
        .setAutoCancel(true)
        .addAction(R.drawable.ic_action_done, "Done", pService)
        .addAction(R.drawable.ic_action_mail, "E-Mail", eService)
        .addAction(R.drawable.ic_action_share, "Share", pShareIntent)
        .build();
		notificationBuilder.defaults |= Notification.DEFAULT_VIBRATE;
		notificationBuilder.defaults |= Notification.DEFAULT_SOUND; 
		NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
		
		notificationManager.notify(notificationId, notificationBuilder);
	}  

}
