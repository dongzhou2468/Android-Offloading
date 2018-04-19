package com.example.xposedhookmethod;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class MethodListActivity extends ListActivity {

	private List<Map<String, String>> methodList;
	
	public static Map<String, Integer> methodDetail;
	public static SparseArray<List<String>> methodTimes;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		getData();
		SimpleAdapter adapter = new SimpleAdapter(this, methodList, R.layout.list_methods,
				new String[]{"modifier", "word_native", "returnType", "methodName", "param", "time"},
				new int[]{R.id.modifier, R.id.word_native, R.id.returnType, R.id.methodName, R.id.param, R.id.time});
		setListAdapter(adapter);
	}
	
	private void getData() {
		
		methodList 		= new ArrayList<Map<String, String>>();
		
		methodDetail 	= new HashMap<String, Integer>();
		methodTimes 	= new SparseArray<List<String>>();
		
		try {
			Log.i("jw", "reading from log file...");
		    File file = new File("/data/system/"
					+ HookAppAllMethod.FILTER_PKGNAME +"_LoadedClass.txt");
		    BufferedReader br = new BufferedReader(new FileReader(file));
		    String readline = "";
		    while ((readline = br.readLine()) != null) {
		        if (readline.equals("")) {
					continue;
				} else {
					Log.i("jw", "readline:" + readline);
					setData(readline);
				}
		    }
		    br.close();
		} catch (Exception e) {
		    e.printStackTrace();
		}
	}

	private void setData(String line) {

		// 1717---public org.json.JSONObject com.facepp.http.HttpRequests.request(
		//					java.lang.String,java.lang.String,com.facepp.http.PostParameters)
		String time 		= line.split("---")[0];
		String[] methodInfo = line.split("---")[1].split(" ");
		
		if (!methodDetail.containsKey(line.split("---")[1])) {
			methodDetail.put(line.split("---")[1], methodDetail.size());
			List<String> list = new ArrayList<String>();
			list.add(time);
			methodTimes.put(methodDetail.size() - 1, list);
		} else {
			int index = methodDetail.get(line.split("---")[1]);
			methodTimes.get(index).add(time);
			//update average time
			int sum = 0, i;
			for (i = 0; i < methodTimes.get(index).size(); i++) {
				sum += Integer.parseInt(methodTimes.get(index).get(i));
			}
			methodList.get(index).put("time", (sum / i) + " ms");
			return;
		}
		
		int i = 0;
		Map<String, String> map = new HashMap<String, String>();
		if (line.contains("static")) {
			map.put("modifier", methodInfo[i++] + " " + methodInfo[i++] + " ");
		} else {
			map.put("modifier", methodInfo[i++] + " ");
		}
		if (line.contains("native")) {
			map.put("word_native", methodInfo[i++] + " ");
		}
		String[] returnTypeArr = methodInfo[i++].split("\\.");
		map.put("returnType", returnTypeArr[returnTypeArr.length - 1] + " ");
		String[] nameAndParam = methodInfo[i].split("\\(");
		String[] nameArr = nameAndParam[0].split("\\.");
		map.put("methodName", nameArr[nameArr.length - 1]);
		if (nameAndParam[1].contains(")") && nameAndParam[1].length() > 1) {
			map.put("param", "(...)");
		} else {
			map.put("param", "()");
		}
		map.put("time", time + " ms");
//		if (Integer.parseInt(time) > 2000) {
//			((TextView) findViewById(R.id.time)).setTextColor(Color.parseColor("#FF0000"));
//		}
		Log.i("jw", map.toString());
		
		methodList.add(map);
		
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Log.i("jw",  "position: " + position);
		Intent intent = new Intent();
		intent.setClass(MethodListActivity.this, MethodDetailActivity.class);
		intent.putExtra("methodPosi", position);
		startActivity(intent);
	}

}
