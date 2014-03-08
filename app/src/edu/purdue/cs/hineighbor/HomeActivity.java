package edu.purdue.cs.hineighbor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import edu.purdue.cs.hineighbor.SQLHandler;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class HomeActivity extends Activity {

	SQLHandler sqlhandler = SQLHandler.getInstance(this);
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		ListView listView = (ListView) findViewById(R.id.listView_home); 
		SimpleAdapter adapter = new SimpleAdapter(this, getData(), R.layout.activity_home_list_layout, new String[]{"Name","info","img"}, new int[] {R.id.list_username,R.id.list_detail_hobbies,R.id.list_avatar_image});
		listView.setAdapter(adapter);
	}

	private List<Map<String, Object>> getData() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		
		
	/*	map.put("Name", "user1");
		map.put("info", "hobbies 1");
		map.put("img", R.drawable.bonjour_icon);
		list.add(map);

		map = new HashMap<String, Object>();
		map.put("Name", "uesr2");
		map.put("info", "hobbies 2");
		map.put("img", R.drawable.bonjour_icon);
		list.add(map);

		map = new HashMap<String, Object>();
		map.put("Name", "user3");
		map.put("info", "hobbies 3");
		map.put("img", R.drawable.bonjour_icon);
		list.add(map);*/
		
		
		
		return list;
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home, menu);
		return true;
	}

}
