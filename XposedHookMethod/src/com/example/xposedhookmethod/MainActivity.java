package com.example.xposedhookmethod;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	private TextView hintTxt;

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_demo);
		
		hintTxt = (TextView)findViewById(R.id.hinttxt);
		
		findViewById(R.id.listmethod).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent i = new Intent(MainActivity.this, MethodListActivity.class);
				startActivity(i);
			}
		});
		
		findViewById(R.id.savemethod).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				/*
				boolean isAll = HookAppAllMethod.dumpAllMethodInfo();
				boolean isCall = HookAppAllMethod.dumpCallMethodInfo();
				if(isAll && isCall){
					hintTxt.setText("�������...");
				}if(isCall && !isAll){
					hintTxt.setText("���÷����������...");
				}if(!isCall && isAll){
					hintTxt.setText("ȫ�������������...");
				}else{
					hintTxt.setText("����ʧ��...");
				}
				*/
				/*
				try{
					hintTxt.setText("д�ļ�...");
					FileWriter fw = new FileWriter(Environment.getExternalStorageDirectory() + "/"
							+ HookAppAllMethod.FILTER_PKGNAME + "_LoadedClass.txt", true);
					Log.i("jw", Environment.getExternalStorageDirectory() + "");
					fw.write("write test");
					hintTxt.setText("д��...");
					fw.close();
				}catch(Exception e){
					e.printStackTrace();
				}
				*/
				try {
					Log.i("jw", "reading from log file...");
				    File file = new File("/data/system/"
							+ HookAppAllMethod.FILTER_PKGNAME +"_LoadedClass.txt");
				    BufferedReader br = new BufferedReader(new FileReader(file));
				    String readline = "";
				    StringBuffer sb = new StringBuffer();  
				    while ((readline = br.readLine()) != null) {  
				        Log.i("jw", "readline:" + readline);  
				        sb.append(readline + "\n");  
				    }
				    br.close();
				    hintTxt.setText(sb);
				} catch (Exception e) {
				    e.printStackTrace();
				}
			}});
	}
	
}
