package edu.purdue.cs.hineighbor;


import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class UserProfile extends Activity{

	private long userId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_profile);
		
		userId = getIntent().getExtras().getLong(LoginActivity.USER_ID);
		SQLHandler sqlHandler = SQLHandler.getInstance(this);
		
		//UI references
		TextView name = (TextView) findViewById(R.id.nameOfUser);
		TextView birthday = (TextView) findViewById(R.id.birthday);
		TextView gender = (TextView) findViewById(R.id.gender);
		TextView hobbies = (TextView) findViewById(R.id.age);
		
		String userName = sqlHandler.getUserNameByUserId((int)userId);
		name.setText(userName);
		
		String genderStr = sqlHandler.getGenderByUserId((int)userId);
		
		gender.setText(genderStr);
	}

}
