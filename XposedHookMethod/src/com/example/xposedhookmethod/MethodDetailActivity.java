package com.example.xposedhookmethod;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MethodDetailActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_method_detail);
		
		Intent intent = getIntent();
		int methodPosi = intent.getExtras().getInt("methodPosi");
		String methodStr = null;
		for (String methodKey : MethodListActivity.methodDetail.keySet()) {
			if (MethodListActivity.methodDetail.get(methodKey) == methodPosi) {
				methodStr = methodKey;
			}
		}
		
		TextView methodNameView = (TextView) findViewById(R.id.methodNameDetail);
		TextView classPathView 	= (TextView) findViewById(R.id.classPath);
		TextView returnTypeView = (TextView) findViewById(R.id.returnTypeDetail);
		TextView timeRecordView = (TextView) findViewById(R.id.timeRecord);
		
		String[] 	methodDetail	= methodStr.split("\\(")[0].split(" ");
		String[] 	classPathArr 	= methodDetail[methodDetail.length - 1].split("\\.");
		String 		returnType 		= methodDetail[methodDetail.length - 2];
		String		paramStr 		= methodStr.split("\\(")[1].split("\\)")[0];
		
		methodNameView.setText(classPathArr[classPathArr.length - 1]);
		
		StringBuilder classPath = new StringBuilder();
		for (int i = 0; i < classPathArr.length - 1; i++) {
			classPath.append(classPathArr[i]);
			if (i != classPathArr.length - 2) {
				classPath.append(".");
			}
		}
		classPathView.setText(classPath);
		
		String wordNoParam 	= "No Parameter";
		String wordParam	= "Parameter:";
		LinearLayout paramParentView = (LinearLayout) findViewById(R.id.paramDetail);
		LinearLayout.LayoutParams wordLayoutParams = new LinearLayout.LayoutParams(
			    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		wordLayoutParams.setMargins(45, 45, 0, 0);
		LinearLayout.LayoutParams valueLayoutParams = new LinearLayout.LayoutParams(
			    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		valueLayoutParams.setMargins(100, 0, 0, 0);
		
		TextView paramView0 = new TextView(this);
		TextView paramView1;
		if (paramStr == null) {
			paramView0.setText(wordNoParam);
			paramView0.setLayoutParams(wordLayoutParams);
			paramParentView.addView(paramView0);
		} else {
			paramView0.setText(wordParam);
			paramView0.setLayoutParams(wordLayoutParams);
			paramView0.setTextColor(Color.parseColor("#D2B48C"));
			paramView0.setTextSize(16);
			paramParentView.addView(paramView0);
			String[] paramArr = paramStr.split(",");
			for (String param : paramArr) {
				paramView1 = new TextView(this);
				paramView1.setText(param);
				paramView1.setLayoutParams(valueLayoutParams);
				paramView1.setTextColor(Color.parseColor("#000000"));
				paramView1.setTextSize(18);
				paramParentView.addView(paramView1);
			}
		}
		
		returnTypeView.setText(returnType);
		
		StringBuilder timeRecord = new StringBuilder();
		for (String timeStr : MethodListActivity.methodTimes.get(methodPosi)) {
			timeRecord.append(timeStr);
			timeRecord.append(" ms  ");
		}
		timeRecordView.setText(timeRecord);
	}

}
