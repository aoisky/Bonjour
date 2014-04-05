package edu.purdue.cs.hineighbor;



import edu.purdue.cs.hineighbor.SQLHandler;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
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
import android.widget.Toast;


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
		this.showWelcome();
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
				this.logoutAccount();
	    		return true;
	    		
	            
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

	
	private class DrawerItemClickListener implements ListView.OnItemClickListener {
	    @Override
	    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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
			changeFragment(fragment, position);
			getActionBar().setTitle("Welcome");
		break;
		//Profile
		case 1: 
		    fragment = new UserProfileFragment();
			changeFragment(fragment, position);
			getActionBar().setTitle("User Profile");
		break;
		
		//Matchings
		case 2:
			fragment = new MatchingsFragment();
			changeFragment(fragment, position);
			getActionBar().setTitle("Matchings");
		break;
		//Change password
		case 3:
			fragment = new ChangePasswordFragment();
			changeFragment(fragment,position);
			getActionBar().setTitle("Change Password");
		break;
		
		//About
		case 4:
		    drawerList.setItemChecked(position, true);
		    drawerLayout.closeDrawer(drawerList);
		    this.showAbout();
		break;
		
		//Logout
		case 5:
		    drawerList.setItemChecked(position, true);
		    drawerLayout.closeDrawer(drawerList);
			Toast.makeText(this, "Now Logout...", Toast.LENGTH_SHORT).show();
			this.logoutAccount();
		break;
		
		}

	}
	
	private void showWelcome(){
		Fragment fragment = new HomeWelcomeFragment();
	    Bundle args = new Bundle();
	    args.putLong(APIHandler.USER_ID, userId);
	    fragment.setArguments(args);
	    // Insert the fragment by replacing any existing fragment
	    FragmentManager fragmentManager = getFragmentManager();
	    fragmentManager.beginTransaction()
	                   .replace(R.id.content_frame, fragment)
	                   .commit();
	}
	
	private void changeFragment(Fragment fragment, int position){
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

	private void logoutAccount(){
		new HomeActivityTask().execute(0);
	}
	
	private void showAbout(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		  builder.setMessage("Bonjour Version 1.0 Beta\n")
		  .setTitle("About Us")
		  .setCancelable(false)
		  .setPositiveButton("OK", new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int id) { }
      });

		  AlertDialog alert = builder.create();
		  alert.show();
	}
	
	private class HomeActivityTask extends AsyncTask<Integer, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Integer... params) {
			boolean logoutFlag = false;
			
			if(params[0] == 0){
				if(APIHandler.isNetworkAvaliable(HomeActivity.this)){
					logoutFlag = APIHandler.logout(HomeActivity.this, (int)HomeActivity.this.userId);
				}
			}
			
			return logoutFlag;
		}
		
		@Override
		protected void onPostExecute(final Boolean success) {
			
			if(success == true){
				Toast.makeText(HomeActivity.this, "Logout successfully", Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
				HomeActivity.this.startActivity(intent);
				HomeActivity.this.finish();
			} else {
				Toast.makeText(HomeActivity.this, "Logout failed, please check network connection", Toast.LENGTH_SHORT).show();
			}
			
		}
		

	}
	
}
