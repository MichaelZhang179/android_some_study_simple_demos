package com.example.simples;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class MainActivity extends Activity {
	private Context context = MainActivity.this;
	private TextView listView_scrollView;
	private TextView ddlistview;
	private TextView crashLogSave2SDCard;
	private TextView clock;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		setListeners();
	}
	
	private void setListeners() {
		listView_scrollView = (TextView) findViewById(R.id.listView_scrollView);
		listView_scrollView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(context, ListViewScrollviewActivity.class);
				startActivity(intent);
			}
		});
		
		ddlistview = (TextView) findViewById(R.id.ddlistview);
		ddlistview.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(context, DropDownListviewActivity.class);
				startActivity(intent);
			}
		});
		
		crashLogSave2SDCard = (TextView) findViewById(R.id.crashLogSave2SDCard);
		crashLogSave2SDCard.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(context, CrashLogSave2SDCardActivity.class);
				startActivity(intent);
			}
		});
		
		clock = (TextView) findViewById(R.id.clock);
		clock.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(context, ClockActivity.class);
				startActivity(intent);
			}
		});
	}

}
