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
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

public class FindPasswordActivity extends Activity implements OnClickListener {

	private FrameLayout retrieveLayout;
	private TextView securityQuestionText;
	private EditText answerText;
	private EditText emailAddressText;
	private EditText newPasswordText;
	private Button userNameConfirmBtn;
	private Button securityConfirmBtn;
	private Button changePasswordBtn;
	
	View answerQuestionView;
	View getUserNameView;
	View newPasswordView;
	String userName;
	String proceedToken;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setTitle("Retrieve Lost Password");
		setContentView(R.layout.activity_find_password);
		retrieveLayout =(FrameLayout) this.findViewById(R.id.retrieve_password_frame);
		
		LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		getUserNameView = inflater.inflate(R.layout.activity_find_pass_fragment1, null);
		answerQuestionView = inflater.inflate(R.layout.activity_find_pass_fragment2, null);
		newPasswordView = inflater.inflate(R.layout.fragment_changeforgotten_pass, null);
		emailAddressText = (EditText) getUserNameView.findViewById(R.id.find_pass_enter_email);
		answerText = (EditText) answerQuestionView.findViewById(R.id.find_pass_enter_answer);
		securityQuestionText = (TextView) answerQuestionView.findViewById(R.id.find_pass_security_display);
		securityConfirmBtn = (Button) answerQuestionView.findViewById(R.id.find_pass_confirm_question);
		securityConfirmBtn.setOnClickListener(this);
		userNameConfirmBtn = (Button) getUserNameView.findViewById(R.id.find_pass_confirm_email_btn);
		newPasswordText = (EditText) newPasswordView.findViewById(R.id.find_pass_enter_new_password_text);
		changePasswordBtn = (Button) newPasswordView.findViewById(R.id.find_pass_confirm_new_password);
		changePasswordBtn.setOnClickListener(this);
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
		
		if(v == securityConfirmBtn){
			String answer = answerText.getText().toString();
			
			if(answer != null && !answer.equals("")){

				this.checkSecurityQuestion(userName, answer);
			}else{
				answerText.setError("Invalid security answer");
			}
		}
		
		if(v == changePasswordBtn){
			String changedPassword = newPasswordText.getText().toString();
			if(changedPassword != null && changedPassword.length() >= 6){
				this.setNewPassword(changedPassword);
			}else{
				Toast.makeText(this, "Invalid New Password format", Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	private void retrieveSecurityQuestion(String userName){
		Toast.makeText(this, "Retrieving security question...", Toast.LENGTH_SHORT).show();
		new RetrieveQuestionTask().execute(userName);
	}
	
	private void checkSecurityQuestion(String userName, String answer){
		new VerifyQuestionTask().execute(userName, answer);
	}
	
	private void setNewPassword(String newPassword){
		new changePasswordTask().execute( userName,newPassword, proceedToken );
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
	
	private class changePasswordTask extends AsyncTask<String, Void, Boolean>{

		@Override
		protected Boolean doInBackground(String... params) {
			String userName = params[0];
			String password = params[1];
			String proceedToken = params[2];
			
			return APIHandler.changeForgottenPassword(userName, proceedToken, password);
		}
		
		@Override
		protected void onPostExecute(final Boolean success) {
			if(success){
				Toast.makeText(FindPasswordActivity.this, "Password Changed Successfully", Toast.LENGTH_SHORT).show();
				FindPasswordActivity.this.finish();
			}else{
				Toast.makeText(FindPasswordActivity.this, "Password Change failed", Toast.LENGTH_SHORT).show();
				FindPasswordActivity.this.finish();
			}
		}
	}
	
	private class VerifyQuestionTask extends AsyncTask<String, Void, Bundle>{

		@Override
		protected Bundle doInBackground(String... params) {
			// TODO Auto-generated method stub
			String userName = params[0];
			String answer = params[1];
			Bundle bundle = null;
			
			if(APIHandler.isNetworkAvaliable(FindPasswordActivity.this)){
				bundle = APIHandler.verifySecurityAnswer(userName, answer);
			}
			
			return bundle;
		}
		
		@Override
		protected void onPostExecute(final Bundle bundle) {
			if(bundle != null){
				proceedToken = bundle.getString(APIHandler.PROCEED_TOKEN);
				retrieveLayout.removeView(answerQuestionView);
				retrieveLayout.addView(newPasswordView);
			}else{
				Toast.makeText(FindPasswordActivity.this, "Answer verify failed", Toast.LENGTH_SHORT).show();
			}
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
			FindPasswordActivity.this.userName = userName;
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
