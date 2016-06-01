package com.example.simples;

import com.example.simples.R;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

/**
 * @author Michael
 */
public class ClockActivity extends Activity {
	private Context context = ClockActivity.this;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.clock);
	}

}
