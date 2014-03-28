package edu.purdue.cs.hineighbor;



import edu.purdue.cs.hineighbor.SQLHandler;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class HomeActivity extends Activity {

	//Save a userId
	private long userId = 0L;
	
	
	//private boolean sidebarFlag = false;
	//private SidebarFragment sidebar;
	
	private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private String[] titles;
    private ListView drawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence activityTitle;
    
	SQLHandler sqlhandler = SQLHandler.getInstance(this);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_navigation);
		userId = getIntent().getExtras().getLong(LoginActivity.USER_ID);
		//Set action bar
		setActionBar(this.getActionBar());
		activityTitle = this.getTitle();
		
		titles = getResources().getStringArray(R.array.home_list_title);
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawerList = (ListView) findViewById(R.id.left_drawer);
		drawerList.setOnItemClickListener(new DrawerItemClickListener());
		
		 mDrawerToggle = new ActionBarDrawerToggle(
	                this,                  
	                drawerLayout,         
	                R.drawable.ic_action_help, 
	                R.string.drawer_open,  
	                R.string.drawer_close 
	                ) {

	            /** Called when a drawer has settled in a completely closed state. */
	            public void onDrawerClosed(View view) {
	                super.onDrawerClosed(view);
	                getActionBar().setTitle(activityTitle);
	            }

	            /** Called when a drawer has settled in a completely open state. */
	            public void onDrawerOpened(View drawerView) {
	                super.onDrawerOpened(drawerView);
	                getActionBar().setTitle(activityTitle);
	            }
	        };
	        
		
		//Change to custom adapter If possible
		ArrayAdapter<String> drawerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, titles);
		drawerList.setAdapter(drawerAdapter);		
		
		drawerLayout.setDrawerListener(drawerToggle);
		
	}

	/**
	 * Set action bar
	 * @param actionBar
	 */
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
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
		 if (mDrawerToggle.onOptionsItemSelected(item)) {
	          return true;
	        }
		
	    switch (item.getItemId()) {
	    	case android.R.id.home:
	    		//setSideBar();
	    		return true;
	    		
	    	case R.id.action_logout:
	    		Intent login = new Intent(this,LoginActivity.class);
	    		startActivity(login);
	    		finish();
	    		return true;
	    		
	    	case R.id.action_settings:
	    		Intent settings = new Intent(this, SettingsActivity.class);
	    		startActivity(settings);
	            return true;
	            
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

	
	private class DrawerItemClickListener implements ListView.OnItemClickListener {
	    @Override
	    public void onItemClick(AdapterView parent, View view, int position, long id) {
	        selectItem(position);
	    }


	}

	/** Swaps fragments in the main content view */
	private void selectItem(int position) {
	    // Create a new fragment
		Fragment fragment = null;
		switch(position){
		//Home
		case 0:
			fragment = new HomeWelcomeFragment();
		break;
		//Profile
		case 1: 
		    fragment = new UserProfileFragment();
		break;
		//Matchings
		case 2:
		//About
		break;
		//Logout
		case 3:
			
		break;
		
		}
	    Bundle args = new Bundle();
	    args.putLong(APIHandler.USER_ID, userId);
	    fragment.setArguments(args);
	    // Insert the fragment by replacing any existing fragment
	    FragmentManager fragmentManager = getFragmentManager();
	    fragmentManager.beginTransaction()
	                   .replace(R.id.content_frame, fragment)
	                   .commit();

	    // Highlight the selected item and close the drawer
	    drawerList.setItemChecked(position, true);
	    drawerLayout.closeDrawer(drawerList);
	}


	
}
