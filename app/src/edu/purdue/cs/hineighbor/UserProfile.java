package edu.purdue.cs.hineighbor;


import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class UserProfile extends Activity{


	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//UI references
		TextView name = (TextView) findViewById(R.id.nameOfUser);
		TextView birthday = (TextView) findViewById(R.id.birthday);
		TextView gender = (TextView) findViewById(R.id.gender);
		TextView hobbies = (TextView) findViewById(R.id.hobbies);
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_profile);
	}

}
