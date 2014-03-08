package edu.purdue.cs.hineighbor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.app.Activity;
import android.app.ListActivity;
import android.view.Menu;
import android.widget.SimpleAdapter;

public class HomeListLayoutActivity extends ListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		SimpleAdapter adapter = new SimpleAdapter(this, getData(), R.layout.activity_home_list_layout, new String[]{"Name","info","img"}, new int[] {R.id.list_username,R.id.list_detail_hobbies,R.id.list_avatar_image});
		setListAdapter(adapter);
		}
	

	private List<Map<String, Object>> getData() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("Name", "G1");
		map.put("info", "google 1");
		map.put("img", R.drawable.nouser);
		list.add(map);

		map = new HashMap<String, Object>();
		map.put("Name", "G2");
		map.put("info", "google 2");
		map.put("img", R.drawable.nouser);
		list.add(map);

		map = new HashMap<String, Object>();
		map.put("Name", "G3");
		map.put("info", "google 3");
		map.put("img", R.drawable.nouser);
		list.add(map);
		
		return list;
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home_list_layout, menu);
		return true;
	}

}
