package com.example.simples;

import android.app.Application;
import android.content.Context;

import com.example.simples.util.CrashHandler;

/**
 * @since 14-12-18
 * @author Michael
 */
public class application extends Application {
	private Context context = application.this;

	@Override
	public void onCreate() {
		super.onCreate();

		CrashHandler crashHandler = CrashHandler.getInstance();
		crashHandler.init(getApplicationContext());
	}
}
