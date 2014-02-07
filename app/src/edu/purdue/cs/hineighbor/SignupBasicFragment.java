package edu.purdue.cs.hineighbor;



import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
/**
 * Signup Fragment for basic information
 * @author Yudong Yang
 *
 */
public class SignupBasicFragment extends Fragment{

	private String email = null;
	private String password = null;
	private String confirmPassword = null;
	
	private EditText emailAddress;
	private EditText passwordEdit;
	private EditText passwordConfirm;

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_signup_basic,
				container, false);

		if(getArguments() != null){
			email = getArguments().getString("email");
			password = getArguments().getString("password");
		}
		emailAddress = (EditText) rootView.findViewById(R.id.signup_email);
		passwordEdit = (EditText) rootView.findViewById(R.id.signup_password);
		passwordConfirm = (EditText) rootView.findViewById(R.id.signup_confirmPassword);
		
		if(email != null){
			emailAddress.setText(email);
		}
		
		if(password != null){
			passwordEdit.setText(password);
		}

		emailAddress.addTextChangedListener(new TextWatcher(){

			@Override
			public void afterTextChanged(Editable edit) {
				// TODO Auto-generated method stub
				email = edit.toString();
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {}
		});
		
		passwordEdit.addTextChangedListener(new TextWatcher(){

			@Override
			public void afterTextChanged(Editable edit) {
				// TODO Auto-generated method stub
				password = edit.toString();
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {}
		});
		
		passwordConfirm.addTextChangedListener(new TextWatcher(){

			@Override
			public void afterTextChanged(Editable edit) {
				// TODO Auto-generated method stub
				confirmPassword = edit.toString();
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {}
		});
		return rootView;
	}
	
	public String getEmail(){
		return email;
	}
	
	public String getPassword(){
		return password;
	}
	
	public String getConfirmPassword(){
		return confirmPassword;
	}
}
