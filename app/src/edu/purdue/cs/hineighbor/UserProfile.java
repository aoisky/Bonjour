package edu.purdue.cs.hineighbor;


import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class UserProfile extends Activity{

	//UI references
	TextView name = (TextView) findViewById(R.id.tvName);
	TextView birthday = (TextView) findViewById(R.id.tvBirthday);
	TextView gender = (TextView) findViewById(R.id.tvGender);
	TextView hobbies = (TextView) findViewById(R.id.tvHobbies);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_profile);
	}

}
