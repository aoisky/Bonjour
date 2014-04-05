package edu.purdue.cs.hineighbor;


import android.app.Activity;
import android.app.ActionBar;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;

public class EditProfileActivity extends Activity {

	private long userId;
	
	private FrameLayout editProfileLayout;
	
	private EditText ageEdit;
	private EditText hobbyText;
	private EditText securityAnswerText;
	private Spinner securitySpinner;
	private RadioGroup genderRadio;
	private String userName;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_profile);
		userId = getIntent().getExtras().getLong(LoginActivity.USER_ID);
		editProfileLayout = (FrameLayout) findViewById(R.id.edit_profile_container);

		LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View editProfileView = inflater.inflate(R.layout.fragment_edit_profile, null);
		ageEdit = (EditText) editProfileView.findViewById(R.id.edit_text_age);
		hobbyText = (EditText) editProfileView.findViewById(R.id.edit_text_hobby);
		securitySpinner = (Spinner) editProfileView.findViewById(R.id.edit_security_question_spinner);
		securityAnswerText = (EditText) editProfileView.findViewById(R.id.edit_text_security_answer);
		genderRadio = (RadioGroup) editProfileView.findViewById(R.id.edit_profile_signup_gender_radio_group);
		editProfileLayout.addView(editProfileView);

		setActionBar(this.getActionBar());
		
		if(userId != 0L){
			userName = APIHandler.getUserName(this, (int)userId);
			
			
			if(userName != null){
				RetrieveProfileTask retrieveProfileTask = new RetrieveProfileTask();
				retrieveProfileTask.execute(userName);
			}
		}
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit_profile, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * Set action bar
	 * @param actionBar
	 */
	private void setActionBar(ActionBar actionBar){

		//Set the background of action Bar
		ColorDrawable background = new ColorDrawable(Color.parseColor("#00A9FF"));
		background.setAlpha(150);
		actionBar.setBackgroundDrawable(background);

		actionBar.show();
	}

	private class RetrieveProfileTask extends AsyncTask<String, Void, Bundle>{
		
		@Override
		protected Bundle doInBackground(String... params) {
			// TODO Auto-generated method stub
			Bundle userProfileBundle = null; 
			
			String userName = params[0];
			
			userProfileBundle = APIHandler.getUserProfile(userName);
			
			return userProfileBundle;
			
		}
		protected void onPostExecute(final Bundle bundle){
		if(bundle != null){
			String gender =  bundle.getString(APIHandler.GENDER);
			if(gender != null){
				//EditProfileActivity.this.genderText.setText(gender);
			}
			
			String birthday = bundle.getString(APIHandler.BIRTHDAY);
			if(birthday != null){
				//EditProfileActivity.this.birthdayText.setText(birthday);
			}
			
			String desiredRange = bundle.getString(APIHandler.DESIRED_RANGE);
			
			
			String hobby = bundle.getString(APIHandler.HOBBY);
			if(hobby != null){
				EditProfileActivity.this.hobbyText.setText(hobby);
			}
			
			
			}
		}
		
	}


}
