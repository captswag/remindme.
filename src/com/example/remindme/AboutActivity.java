package com.example.remindme;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

public class AboutActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
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
	
	/**
	 * @param view
	 */
	public void shareIntent(View view) {
		Intent share = new Intent();
		share.setAction(Intent.ACTION_SEND);
		share.setType("text/plain");
		share.putExtra(Intent.EXTRA_SUBJECT, "RemindMe");
		share.putExtra(Intent.EXTRA_TEXT, "https://www.dropbox.com/s/7v3gqufek0zrmfr/RemindMe.apk?dl=0");
		startActivity(Intent.createChooser(share, getResources().getText(R.string.tell_a_friend )));
	}
	
}
