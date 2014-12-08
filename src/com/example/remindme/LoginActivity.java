package com.example.remindme;

import java.util.Properties;

import javax.mail.Session;
import javax.mail.Store;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {

	SharedPreferences shapre;
	EditText user, pass, friend;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_activity);
		
		ActionBar actionBar = getActionBar();
		actionBar.hide();
		shapre = getSharedPreferences("logincredentials", 0);
		user = (EditText)findViewById(R.id.username);
		pass = (EditText)findViewById(R.id.password);
		friend = (EditText)findViewById(R.id.friendemail);
		user.setHint(shapre.getString("username", "Email Address"));
		friend.setHint(shapre.getString("friendemail", "Friend's Mail"));
	}
	@SuppressLint("CommitPrefEdits") public void registerMail(View view) {
		String username, password, friendmail;
		username = user.getText().toString();
		password = pass.getText().toString();
		friendmail = friend.getText().toString();
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

		StrictMode.setThreadPolicy(policy); 
		
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");
		
		Session emailSession = Session.getDefaultInstance(props, null);
		
		Store store;
			try {
				store = emailSession.getStore("imaps");
				store.connect("imap.gmail.com", username, password);
				SharedPreferences.Editor editor = shapre.edit();
				editor.putString("username", username);
				editor.putString("password", password);
				editor.putString("friendemail", friendmail);
				editor.apply();
				finish();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show();
			}
	}

}
