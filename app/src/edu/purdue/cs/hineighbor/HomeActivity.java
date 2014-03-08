package edu.purdue.cs.hineighbor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import edu.purdue.cs.hineighbor.SQLHandler;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class HomeActivity extends Activity {

	private long userId = 0;
	
	private boolean sidebarFlag = false;
	private SidebarFragment sidebar;
	
	SQLHandler sqlhandler = SQLHandler.getInstance(this);
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		userId = getIntent().getExtras().getLong(LoginActivity.USER_ID);
		//Set action bar
		setActionBar(this.getActionBar());
		
		ListView listView = (ListView) findViewById(R.id.listView_home); 
		//SimpleAdapter adapter = new SimpleAdapter(this, getData(), R.layout.activity_home_list_layout, new String[]{"Name","info","img"}, new int[] {R.id.list_username,R.id.list_detail_hobbies,R.id.list_avatar_image});
		//listView.setAdapter(adapter);
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
	
	/**
	 * Set a side bar in the main activity by fragment
	 */
	private void setSideBar(){
		FragmentManager fragmentMgr = this.getFragmentManager();
		FragmentTransaction transaction = fragmentMgr.beginTransaction();
		
		if(sidebarFlag == true){
			fragmentMgr.popBackStack();
			sidebarFlag = false;
			return;
		}
		
		sidebar = new SidebarFragment();

		Bundle bundle = new Bundle();
		bundle.putLong(LoginActivity.USER_ID, userId);
		
		sidebar.setArguments(bundle);
		
		transaction.replace(android.R.id.content, sidebar);
		transaction.setCustomAnimations(R.anim.slide_enter, R.anim.slide_exit);
		//transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		transaction.addToBackStack(null);
		transaction.commit();
		
		sidebarFlag = true;
		
	}
	
	private void setActionBar(ActionBar actionBar){
		actionBar.setTitle("Bonjour!");

		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME, ActionBar.DISPLAY_SHOW_HOME);
		//Set the background of action Bar
		ColorDrawable background = new ColorDrawable(Color.parseColor("#00A9FF"));
		background.setAlpha(150);
		actionBar.setBackgroundDrawable(background);

		actionBar.show();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	    	case android.R.id.home:
	    		setSideBar();
	    		return true;
	    		
	    	case R.id.action_logout:
	    		Intent login = new Intent(this,LoginActivity.class);
	    		startActivity(login);
	    		finish();
	    		return true;
	    		
	            
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

}
