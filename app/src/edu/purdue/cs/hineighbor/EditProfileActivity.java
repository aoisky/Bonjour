package edu.purdue.cs.hineighbor;


import java.util.ArrayList;
import java.util.HashMap;

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
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * Edit user's profile
 * @author Yudong Yang
 *
 */
public class EditProfileActivity extends Activity implements OnItemSelectedListener, OnClickListener{

	private long userId;
	
	private FrameLayout editProfileLayout;
	
	private EditText ageEdit;
	private EditText hobbyText;
	private EditText birthdayText;
	private EditText securityAnswerText;
	private Spinner securitySpinner;
	private String userName;
	private Button confirmBtn;
	
	HashMap<String, String> securityHashMap;
	ArrayAdapter<String> dataAdapter;
	ArrayList<String> questionList;
	String selectedQuestion = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_profile);
		userId = getIntent().getExtras().getLong(LoginActivity.USER_ID);
		editProfileLayout = (FrameLayout) findViewById(R.id.edit_profile_container);

		LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View editProfileView = inflater.inflate(R.layout.fragment_edit_profile, null);
		ageEdit = (EditText) editProfileView.findViewById(R.id.edit_text_age);
		birthdayText = (EditText) editProfileView.findViewById(R.id.edit_text_birthday);
		hobbyText = (EditText) editProfileView.findViewById(R.id.edit_text_hobby);
		securitySpinner = (Spinner) editProfileView.findViewById(R.id.edit_security_question_spinner);
		securityAnswerText = (EditText) editProfileView.findViewById(R.id.edit_text_security_answer);
		confirmBtn = (Button) editProfileView.findViewById(R.id.edit_profile_confirm_btn);
		editProfileLayout.addView(editProfileView);
		securitySpinner.setOnItemSelectedListener(this);
		setActionBar(this.getActionBar());
		confirmBtn.setOnClickListener(this);
		confirmBtn.setEnabled(false);
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

	
	private class UpdateProfileTask extends AsyncTask<Void, Void, Boolean>{

		@Override
		protected Boolean doInBackground(Void... params) {
			String selectedNo = null;
			Bundle bundle = new Bundle();
			bundle.putLong(APIHandler.USER_ID, userId);
			bundle.putString(APIHandler.ACCESS_TOKEN, APIHandler.getAccessTokenByUserId(EditProfileActivity.this, (int)userId));
			bundle.putString(APIHandler.BIRTHDAY, birthdayText.getText().toString());
			bundle.putString("age", ageEdit.getText().toString());
			bundle.putString(APIHandler.HOBBY, hobbyText.getText().toString());
			
			for(String questionNo : EditProfileActivity.this.securityHashMap.keySet()){
				String question = EditProfileActivity.this.securityHashMap.get(questionNo);
				if(question.equals(EditProfileActivity.this.selectedQuestion)){
					selectedNo = questionNo;
					break;
				}
				
			}
			boolean updateSecuritySuccess = APIHandler.setUserSecurityQuestion(EditProfileActivity.this, userId, selectedNo, securityAnswerText.getText().toString());
			if(!updateSecuritySuccess){
				Toast.makeText(EditProfileActivity.this, "Server returns error when updating the security anwser", Toast.LENGTH_SHORT).show();
			}
			
			return APIHandler.updateUserProfile(EditProfileActivity.this, bundle);
		}
		
		@Override
		protected void onPostExecute(final Boolean success){
			if(success){
				confirmBtn.setEnabled(true);
				Toast.makeText(EditProfileActivity.this, "User Profile updated", Toast.LENGTH_SHORT).show();
			}else{
				Toast.makeText(EditProfileActivity.this, "Failed to update User Profile ", Toast.LENGTH_SHORT).show();
			}
		}
		
	}
	
	
	private class RetrieveQuestionSetTask extends AsyncTask<Void, Void, HashMap<String, String>>{

		@Override
		protected HashMap<String,String> doInBackground(Void... params) {

			HashMap<String,String> securityHashMap = APIHandler.getSecurityQuestions();
			
			return securityHashMap;
		}
		
		@Override
		protected void onPostExecute(final HashMap<String,String> securityHashMap){
			EditProfileActivity.this.securityHashMap = new HashMap<String, String>(securityHashMap);
			EditProfileActivity.this.questionList = new ArrayList<String>();
			questionList.addAll(securityHashMap.values());
			
			EditProfileActivity.this.dataAdapter = new ArrayAdapter<String>(EditProfileActivity.this,
					android.R.layout.simple_spinner_item, questionList);
				dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				securitySpinner.setAdapter(dataAdapter);
				confirmBtn.setEnabled(true);
		}
		
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
				
				String age = bundle.getString("age");
				if(age != null){
					EditProfileActivity.this.ageEdit.setText(age);
				}
				
				//Black box bug #12
				/*
				String hobby = bundle.getString(APIHandler.HOBBY);
				if(hobby != null){
					EditProfileActivity.this.hobbyText.setText(hobby);
				}
				*/
				
				String birthday = bundle.getString(APIHandler.BIRTHDAY);
				if(birthday != null){
					EditProfileActivity.this.birthdayText.setText(birthday);
				}
				
				
			}else{
				Toast.makeText(EditProfileActivity.this,"Retrieve current profile failed", Toast.LENGTH_SHORT).show();
			}
			
			//Black box bug #6 !!!!!
			new RetrieveQuestionSetTask().execute();
		}
		
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		selectedQuestion = parent.getItemAtPosition(position).toString();
	}


	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v == confirmBtn){
			confirmBtn.setEnabled(false);
			new UpdateProfileTask().execute();
		}
	}


}
