package edu.purdue.cs.hineighbor;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

public class FindPasswordActivity extends Activity implements OnClickListener {

	private FrameLayout retrieveLayout;
	private TextView securityQuestionText;
	private EditText emailAddressText;
	private Button userNameConfirmBtn;
	private Button securityConfirmBtn;
	private Button changePasswordBtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_find_password);
		retrieveLayout =(FrameLayout) this.findViewById(R.id.retrieve_password_frame);
		
		LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View getUserNameView = inflater.inflate(R.layout.activity_find_pass_fragment1, null);
		View answerQuestionView = inflater.inflate(R.layout.activity_find_pass_fragment2, null);
		emailAddressText = (EditText) getUserNameView.findViewById(R.id.find_pass_enter_email);
		securityQuestionText = (TextView) answerQuestionView.findViewById(R.id.find_pass_security_display);
		securityConfirmBtn = (Button) answerQuestionView.findViewById(R.id.find_pass_confirm_question);
		securityConfirmBtn.setOnClickListener(this);
		
		
		retrieveLayout.addView(answerQuestionView);
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
		
	}
	
	private String retrieveSecurityQuestion(String userName){
		return null;
	}
	
	private String checkSecurityQuestion(String answer){
		
		return null;
	}

}
