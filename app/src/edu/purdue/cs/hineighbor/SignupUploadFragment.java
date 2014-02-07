package edu.purdue.cs.hineighbor;


import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
/**
 * Signup activity for upload fragment
 * @author Yudong Yang
 *
 */
public class SignupUploadFragment extends Fragment implements OnClickListener{
	
	private static final int PICK_IMAGE = 1;
	
	private boolean iconSetFlag = false;
	private Button pickImageButton;
	private ImageView userIconView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_signup_upload,
				container, false);
		pickImageButton = (Button) rootView.findViewById(R.id.signup_upload_button);
		pickImageButton.setOnClickListener(this);
		
		userIconView = (ImageView) rootView.findViewById(R.id.signup_userIcon);
		return rootView;
	}
	@Override
	public void onClick(View v) {
		
		Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK);
		pickPhotoIntent.setType("image/*");
		startActivityForResult(pickPhotoIntent, PICK_IMAGE);    
	}
	
	public boolean isIconSet(){
		return iconSetFlag;
	}
	
	public void onActivityResult(int requestCode, int resultCode, 
		       Intent imageReturnedIntent) {
		    super.onActivityResult(requestCode, resultCode, imageReturnedIntent); 

		    switch(requestCode) { 
		    case PICK_IMAGE:
		        if(resultCode == Activity.RESULT_OK){  
		            Uri selectedImage = imageReturnedIntent.getData();
		            String[] filePathColumn = {MediaStore.Images.Media.DATA};

		            Cursor cursor = getActivity().getContentResolver().query(
		                               selectedImage, filePathColumn, null, null, null);
		            cursor.moveToFirst();

		            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
		            String filePath = cursor.getString(columnIndex);
		            cursor.close();

		            
		            Bitmap selectedImageBitmap = BitmapFactory.decodeFile(filePath);
		            userIconView.setImageBitmap(selectedImageBitmap);
		            iconSetFlag = true;
		        }
		    }
		}
	
}
