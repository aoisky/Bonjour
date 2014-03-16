package edu.purdue.cs.hineighbor;



import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;
/**
 * Signup fragment for user detail information
 * @author Yudong Yang
 *
 */
public class SignupDetailsFragment extends Fragment implements OnClickListener{
	
	private EditText nickNameEdit;
	private EditText ageEdit;
	private RadioGroup genderRadio;
	
	private Button registerBtn;
	
	private UserRegisterTask mAuthTask = null;
	String email;
	String password;
	String confirmPassword;
	boolean gender;
	int age;
	Bitmap userIconBitmap;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_signup_details,
				container, false);
		registerBtn = (Button)rootView.findViewById(R.id.signup_button_register);
		nickNameEdit = (EditText) rootView.findViewById(R.id.signup_nickname);
		ageEdit = (EditText) rootView.findViewById(R.id.signup_age);
		genderRadio = (RadioGroup) rootView.findViewById(R.id.signup_gender_radio_group);
		registerBtn.setOnClickListener(this);
		return rootView;
	}
	
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		ViewPager viewPager =  (ViewPager)getActivity().findViewById(R.id.pager);

		SignupBasicFragment basicFragment = (SignupBasicFragment)viewPager.getAdapter().instantiateItem(viewPager, 0);
	    SignupUploadFragment uploadFragment = (SignupUploadFragment)viewPager.getAdapter().instantiateItem(viewPager, 1);
		
		
			email = basicFragment.getEmail();
			password = basicFragment.getPassword();
			confirmPassword = basicFragment.getConfirmPassword();
			
			if(email != null && !email.contains("@")){
				viewPager.setCurrentItem(0);
				EditText emailEdit = (EditText)basicFragment.getView().findViewById(R.id.signup_email);
				emailEdit.setError("Invalid Email Address");
				emailEdit.requestFocus();
				return;
			}
			
			if(password != null && password.length() < 6){
				viewPager.setCurrentItem(0);
				EditText passwordEdit = (EditText)basicFragment.getView().findViewById(R.id.signup_password);
				passwordEdit.setError("Password too short");
				passwordEdit.requestFocus();
				return;
			}
			
			if(password != null && confirmPassword != null && !password.equals(confirmPassword)){
				viewPager.setCurrentItem(0);
				EditText passwordEdit = (EditText)basicFragment.getView().findViewById(R.id.signup_confirmPassword);
				passwordEdit.setError("Password not match");
				passwordEdit.requestFocus();
				return;
			}
		
		
		if(!uploadFragment.isIconSet()){
			viewPager.setCurrentItem(1);
			Toast.makeText(getActivity(), "You need to choose an user icon", Toast.LENGTH_SHORT).show();
			return;
		}
		
		ImageView userIcon = (ImageView) uploadFragment.getView().findViewById(R.id.signup_userIcon);
		
		userIconBitmap = ((BitmapDrawable)userIcon.getDrawable()).getBitmap();

		if(nickNameEdit.getText().toString().equals("")){
			nickNameEdit.setError("Invalid nickname");
			nickNameEdit.requestFocus();
			return;
		}
		
		if(ageEdit.getText().toString().equals("")){
			ageEdit.setError("Invalid Age");
			ageEdit.requestFocus();
			return;
		}
		
		if(genderRadio.getCheckedRadioButtonId() == -1){
			Toast.makeText(getActivity(), "You need to choose a gender", Toast.LENGTH_SHORT).show();
			genderRadio.requestFocus();
			return;
		}
		
		int genderId = genderRadio.getCheckedRadioButtonId();
		gender = false; // False: male True: female

		if(genderId == R.id.radioButtonFemale){
			gender = true;
		}
		
		age = Integer.parseInt(ageEdit.getText().toString());
		
		mAuthTask = new UserRegisterTask();
		mAuthTask.execute((Void) null);
		
	}
	
	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class UserRegisterTask extends AsyncTask<Void, Void, Long> {
		@Override
		protected Long doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.

			Long userId = APIHandler.register(SignupDetailsFragment.this.getActivity(), SignupDetailsFragment.this.email, password, confirmPassword, gender, age, userIconBitmap);
			return userId;
		}

		@Override
		protected void onPostExecute(final Long success) {
			mAuthTask = null;


			if (success != -1) {
				Intent homeIntent = new Intent(SignupDetailsFragment.this.getActivity(), HomeActivity.class);
				
				homeIntent.putExtra(LoginActivity.USER_ID, success);
				startActivity(homeIntent);
				SignupDetailsFragment.this.getActivity().finish();
			} else{
				Toast.makeText(getActivity(), "Register failed", Toast.LENGTH_SHORT).show();
				SignupDetailsFragment.this.getActivity().finish();
			}
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			Toast.makeText(getActivity(), "Register failed", Toast.LENGTH_SHORT).show();
			SignupDetailsFragment.this.getActivity().finish();
		}
	}
}
