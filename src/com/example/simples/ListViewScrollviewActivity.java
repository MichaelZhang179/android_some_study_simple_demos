package com.example.simples;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.simples.util.NoScrollListView;

public class ListViewScrollviewActivity extends Activity {
	private Context context = ListViewScrollviewActivity.this;
	private NoScrollListView lv;

	private String[] nameList = { "Miley Cyruc", "Alice Keys", "Jewel",
			"Dublin", "Kelly Clarkson", "Mariah Carey", "Sheen", "Adele",
			"Avril Lavigne", "Taylor Swift", "Michael Scolfield", "Kobe", "Abama", "NB" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listview_scrollview);

		lv = (NoScrollListView) findViewById(R.id.listview);
		ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, nameList);
		lv.setAdapter(adapter);

		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Toast.makeText(context, "Hey, " + nameList[arg2], Toast.LENGTH_SHORT).show();
			}
		});

	}

}
