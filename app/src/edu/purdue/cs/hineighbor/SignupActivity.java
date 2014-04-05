package edu.purdue.cs.hineighbor;

import java.util.Locale;


import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Signup activity
 * @author Yudong Yang
 *
 */
public class SignupActivity extends Activity {

	
	private Fragment fragmentTagBasic;
	private Fragment fragmentTagUpload;
	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signup);
		
		//Set actionBar background
		ColorDrawable background = new ColorDrawable(Color.parseColor("#00A9FF"));
		background.setAlpha(150);
		getActionBar().setBackgroundDrawable(background);
		
		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getFragmentManager());

		this.setTitle("Create a new account");
		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.signup, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	
	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			switch(position){
			
			case 0:
				Fragment signupBasicFragment = new SignupBasicFragment();
				fragmentTagBasic = signupBasicFragment;
				return signupBasicFragment;
			
			case 1:
				Fragment signupUploadFragment = new SignupUploadFragment();
				fragmentTagUpload = signupUploadFragment;
				return new SignupUploadFragment();
				
			case 2:
				return new SignupDetailsFragment();
		}
		
		return null;
		}

		public Fragment getBasicFragment(){
			return fragmentTagBasic;
		}
		
		public Fragment getUploadFragment(){
			return fragmentTagUpload;
		}
		
		@Override
		public int getCount() {
			// Show 3 total pages.
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.signup_basic_info).toUpperCase(l);
			case 1:
				return getString(R.string.signup_upload_icon).toUpperCase(l);
			case 2:
				return getString(R.string.signup_detail_info).toUpperCase(l);
			}
			return null;
		}
	}

}
