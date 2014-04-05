package edu.purdue.cs.hineighbor;

import java.io.InputStream;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * User Profile Fragment
 * @author Yudong Yang
 *
 */
public class UserProfileFragment extends Fragment implements OnClickListener {


	private String userName;
	private ImageView userProfileAvatar;
	private TextView userNameText;
	private TextView genderText;
	private TextView birthdayText;
	private TextView hobbyText;
	private TextView desireRangeText;
	private TextView phoneText;
	private Button editProfileBtn;
	private long userId = 0L;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.fragment_user_profile, container, false);
		
		userNameText = (TextView) rootView.findViewById(R.id.profile_user_name);
		userProfileAvatar = (ImageView) rootView.findViewById(R.id.user_avatar_image);
		genderText = (TextView) rootView.findViewById(R.id.profile_user_gender);
		birthdayText = (TextView) rootView.findViewById(R.id.profile_user_birthday);
		hobbyText = (TextView) rootView.findViewById(R.id.profile_user_hobby);
		desireRangeText = (TextView) rootView.findViewById(R.id.profile_user_desired_range);
		phoneText = (TextView) rootView.findViewById(R.id.profile_user_phone_num);
		editProfileBtn = (Button) rootView.findViewById(R.id.profile_user_edit_profile_btn);
		editProfileBtn.setOnClickListener(this);
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
			userNameText.setText(userName);
			
			if(userName != null){
				UserProfileTask userProfileTask = new UserProfileTask();
				userProfileTask.execute(userName);
				
			}

		}
		
	}
	
	 private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
	    	ImageView userAvatar;
	    	
	    	public DownloadImageTask(ImageView userAvatar){
	    		this.userAvatar = userAvatar;
	    	}
	    	
	        protected Bitmap doInBackground(String... urls) {
	            String urldisplay = urls[0];
	            Bitmap mIcon11 = null;
	            try {
	                InputStream in = new java.net.URL(urldisplay).openStream();
	                mIcon11 = BitmapFactory.decodeStream(in);
	            } catch (Exception e) {
	                e.printStackTrace();
	            }
	            return mIcon11;
	        }

	        protected void onPostExecute(Bitmap result) {
	        	userAvatar.setImageBitmap(result);
	        }
	        
	    }
	
	private class UserProfileTask extends AsyncTask<String, Void, Bundle> {

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
				
				String age = bundle.getString("age");
				
				if(age != null){
					
					UserProfileFragment.this.desireRangeText.setText(age);
					
				}
				
				String hobby = bundle.getString(APIHandler.HOBBY);
				if(hobby != null){
					UserProfileFragment.this.hobbyText.setText(hobby);
				}
				
				String phoneNum = bundle.getString(APIHandler.PHONE);
				if(phoneNum != null){
					UserProfileFragment.this.phoneText.setText(phoneNum);
				}
				
				String avatarUrl = bundle.getString("avatar");
				if(avatarUrl != null){
					avatarUrl = APIHandler.AUTH_SERVER_URL + avatarUrl;
					new DownloadImageTask(userProfileAvatar).execute(avatarUrl);
				}
				
			}
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v == editProfileBtn){
			Intent editProfileIntent = new Intent(this.getActivity(), EditProfileActivity.class);
			editProfileIntent.putExtra(APIHandler.USER_ID, userId);
			this.getActivity().startActivity(editProfileIntent);
		}
	}
}
