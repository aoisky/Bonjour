package edu.purdue.cs.hineighbor;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class UserProfileFragment extends Fragment {

	private SQLHandler sqlHandler;
	private String userName;
	private ImageView userProfileAvatar;
	private TextView genderText;
	private TextView birthdayText;
	private TextView ageText;
	private TextView phoneText;
	private long userId = 0L;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.fragment_user_profile, container, false);
		
		
		userProfileAvatar = (ImageView) rootView.findViewById(R.id.profile_image);
		genderText = (TextView) rootView.findViewById(R.id.profile_user_gender);
		birthdayText = (TextView) rootView.findViewById(R.id.profile_user_birthday);
		ageText = (TextView) rootView.findViewById(R.id.profile_user_age);
		phoneText = (TextView) rootView.findViewById(R.id.profile_user_phone_num);
		
		return rootView;
		
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		
		if(this.getArguments()!= null){
			userId = this.getArguments().getLong(APIHandler.USER_ID);
		}
		
		if(userId != 0L){
			userName = APIHandler.getUserName(this.getActivity(), (int)userId);
			if(userName != null){
				UserProfileTask userProfileTask = new UserProfileTask();
				userProfileTask.execute(userName);
			}

		}

		
		
	}
	
	public class UserProfileTask extends AsyncTask<String, Void, Bundle> {

		@Override
		protected Bundle doInBackground(String... userName) {
			// TODO Auto-generated method stub
			Bundle userProfileBundle = null;
			
				if(APIHandler.isNetworkAvaliable(UserProfileFragment.this.getActivity())){
					userProfileBundle = APIHandler.getUserProfile(userName[0]);
					
					return userProfileBundle;
				}
				
				return null;
			}
		
		protected void onPostExecute(final Bundle bundle){
			if(bundle != null){
				String gender =  bundle.getString(APIHandler.GENDER);
				if(gender != null){
					UserProfileFragment.this.genderText.setText(gender);
				}
				
				String birthday = bundle.getString(APIHandler.BIRTHDAY);
				if(birthday != null){
					UserProfileFragment.this.birthdayText.setText(birthday);
				}
				
				String phoneNum = bundle.getString(APIHandler.PHONE);
				if(phoneNum != null){
					UserProfileFragment.this.phoneText.setText(phoneNum);
				}
			}
		}
	}
}
