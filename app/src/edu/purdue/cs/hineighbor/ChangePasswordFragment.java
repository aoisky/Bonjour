package edu.purdue.cs.hineighbor;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ChangePasswordFragment extends Fragment implements OnClickListener{

	private Button confirmButton;
	private EditText oldPasswordText;
	private EditText newPasswordText;
	private EditText newPasswordTextRetype;
	private TextView userNameText;
	private long userId;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.fragment_change_password, container, false);
		userNameText = (TextView) rootView.findViewById(R.id.changepassword_username);
		confirmButton = (Button) rootView.findViewById(R.id.changepassword_confirm_btn);
		oldPasswordText = (EditText) rootView.findViewById(R.id.changepassword_old_password);
		newPasswordText = (EditText) rootView.findViewById(R.id.changepassword_new_password);
		newPasswordTextRetype = (EditText) rootView.findViewById(R.id.changepassword_new_password_retype);
		confirmButton.setOnClickListener(this);
		return rootView;
	}
	
	public void onActivityCreated(Bundle savedInstanceState){
		
		super.onActivityCreated(savedInstanceState);
		
		if(this.getArguments()!= null){
			userId = this.getArguments().getLong(APIHandler.USER_ID);
			userNameText.setText(APIHandler.getUserName(this.getActivity(), (int)userId));
		}
		
		
	}

	@Override
	public void onClick(View v) {
		String oldPassword;
		String newPassword;
		
		if(oldPasswordText.length() < 6){
			oldPasswordText.setError("Password Length should >= 6");
			return;
		}else{
			oldPassword = oldPasswordText.getText().toString();
		}
		
		if(newPasswordText.length() < 6){
			newPasswordText.setError("Password Length should >= 6");
			return;
		}else{
			newPassword = newPasswordText.getText().toString();
		}
		
		if(!newPasswordTextRetype.getText().toString().equals(newPasswordText.getText().toString())){
			newPasswordTextRetype.setError("Retyped password not match");
			return;
		}
		
		this.changePassword(oldPassword, newPassword);
		
	}
	
	private void changePassword(String oldPassword, String newPassword){
		new ChangePasswordTask().execute(oldPassword, newPassword);
	}
	
	private class ChangePasswordTask extends AsyncTask<String, Void, Boolean> {

		@Override
		protected Boolean doInBackground(String... params) {
			String oldPassword = params[0];
			String newPassword = params[1];
			
			boolean changePasswordFlag = APIHandler.changePassword(ChangePasswordFragment.this.getActivity(), (int)ChangePasswordFragment.this.userId, oldPassword, newPassword, newPassword);
			return changePasswordFlag;
		}
		
		@Override
		protected void onPostExecute(final Boolean success) {
			if(success == true){
				Toast.makeText(getActivity(), "Change password successfully", Toast.LENGTH_SHORT).show();
			}else{
				Toast.makeText(getActivity(), "Change password failed", Toast.LENGTH_SHORT).show();
			}
		}
	
	}
}
