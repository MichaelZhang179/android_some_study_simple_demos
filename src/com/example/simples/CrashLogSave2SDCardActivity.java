package com.example.simples;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

/**
 * 保存崩溃日志到sd卡， http://blog.csdn.net/kevinmeng_ini58/article/details/7440810
 * @author Michael
 */
public class CrashLogSave2SDCardActivity extends Activity {
	private Context context = CrashLogSave2SDCardActivity.this;

	private String s;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		System.out.println(s.equals("any string"));  //  故意空引用的log
	}

}
