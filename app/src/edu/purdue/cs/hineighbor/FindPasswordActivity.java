package edu.purdue.cs.hineighbor;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

public class FindPasswordActivity extends Activity implements OnClickListener {

	private FrameLayout retrieveLayout;
	private TextView securityQuestionText;
	private EditText emailAddressText;
	private Button userNameConfirmBtn;
	private Button securityConfirmBtn;
	private Button changePasswordBtn;
	
	View answerQuestionView;
	View getUserNameView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setTitle("Retrieve Lost Password");
		setContentView(R.layout.activity_find_password);
		retrieveLayout =(FrameLayout) this.findViewById(R.id.retrieve_password_frame);
		
		LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		getUserNameView = inflater.inflate(R.layout.activity_find_pass_fragment1, null);
		answerQuestionView = inflater.inflate(R.layout.activity_find_pass_fragment2, null);
		emailAddressText = (EditText) getUserNameView.findViewById(R.id.find_pass_enter_email);
		securityQuestionText = (TextView) answerQuestionView.findViewById(R.id.find_pass_security_display);
		securityConfirmBtn = (Button) answerQuestionView.findViewById(R.id.find_pass_confirm_question);
		securityConfirmBtn.setOnClickListener(this);
		userNameConfirmBtn = (Button) getUserNameView.findViewById(R.id.find_pass_confirm_email_btn);
		
		userNameConfirmBtn.setOnClickListener(this);
		retrieveLayout.addView(getUserNameView);
		
		setActionBar(this.getActionBar());
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.find_password, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v == userNameConfirmBtn){
			String emailAddress = emailAddressText.getText().toString();
			if(emailAddress != null && emailAddress.contains("@")){
				retrieveSecurityQuestion(emailAddress);
			}else{
				emailAddressText.setError("Invalid email address");
			}
		}
	}
	
	private void retrieveSecurityQuestion(String userName){
		Toast.makeText(this, "Retrieving security question...", Toast.LENGTH_SHORT).show();
		new RetrieveQuestionTask().execute(userName);
	}
	
	private String checkSecurityQuestion(String answer){
		return null;
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
	
	private class VerifyQuestionTask extends AsyncTask<String, Void, String>{

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}

	private class RetrieveQuestionTask extends AsyncTask<String, Void, String>{

		@Override
		protected String doInBackground(String... params) {
			String userName = params[0];
			String securityQuestion = null;
			if(APIHandler.isNetworkAvaliable(FindPasswordActivity.this)){
				securityQuestion = APIHandler.getUserSecurityQuestion(userName);
			} else{
				Toast.makeText(FindPasswordActivity.this, "Network is not available", Toast.LENGTH_SHORT).show();
				this.cancel(true);
			}
			return securityQuestion;
		}
		
		@Override
		protected void onPostExecute(final String question) {
			if(question != null){
				retrieveLayout.removeView(getUserNameView);
				retrieveLayout.addView(answerQuestionView);
				FindPasswordActivity.this.securityQuestionText.setText(question);
			}else{
				Toast.makeText(FindPasswordActivity.this, "Security question is not set", Toast.LENGTH_SHORT).show();
			}
		}
		
		@Override
		protected void onCancelled(){

		}
		
	}
}
