package com.example.remindme;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;

public class DoneService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		int value = intent.getIntExtra("id", -1);
		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		notificationManager.cancel(value);
		SQLiteDatabase myDatabase = openOrCreateDatabase("todo.db", MODE_PRIVATE, null);
		myDatabase.delete("todo", "id="+value, null);
		myDatabase.close();
		return START_NOT_STICKY;
	}
	

}
