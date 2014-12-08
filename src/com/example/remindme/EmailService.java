package com.example.remindme;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

public class EmailService extends Service{
	SharedPreferences shapre;
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

		StrictMode.setThreadPolicy(policy); 
		final String username;
		final String password;
		String to;
		String subject = intent.getStringExtra("mail");
		shapre = getSharedPreferences("logincredentials", 0);
		username = shapre.getString("username", "Email Address");
		password = shapre.getString("password", "Password");
		to = shapre.getString("friendemail", "Friend's Mail");

		try {
            GMailSender sender = new GMailSender(username, password);
            sender.sendMail(subject, username + " wants you to " + subject,
                    username, to);
            Toast.makeText(this, "Mail Send Successfully.....", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.e("SendMail", e.getMessage(), e);
        }
	
		return START_NOT_STICKY;
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
