package edu.purdue.cs.hineighbor;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MatchingsFragment extends Fragment {

	private long userId = 0L;
	private String desiredRange = "5";
	private MatchingUserAdapter matchingUserAdapter;
	ListView matchList;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.fragment_matchings, container, false);
		
		matchList = (ListView) rootView.findViewById(android.R.id.list);
		
		return rootView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		
		if(this.getArguments()!= null){
			userId = this.getArguments().getLong(APIHandler.USER_ID);
		}
		showInputDesiredRange();
	}
	
	private void getRecentPosition(){
		String locationProvider = LocationManager.GPS_PROVIDER;

		LocationManager locationManager = (LocationManager) this.getActivity().getSystemService(Context.LOCATION_SERVICE);
		LocationListener locationListener = new LocationListener() {
		    public void onLocationChanged(Location location) {
		    	Toast.makeText(MatchingsFragment.this.getActivity(), "GPS Information Received", Toast.LENGTH_SHORT).show();
		      // Called when a new location is found by the network location provider.
		    	new MatchingTask().execute(location);
		    	LocationManager locationManager = (LocationManager) MatchingsFragment.this.getActivity().getSystemService(Context.LOCATION_SERVICE);
		    	locationManager.removeUpdates(this);
		    }

		    public void onStatusChanged(String provider, int status, Bundle extras) {}

		    public void onProviderEnabled(String provider) {}

		    public void onProviderDisabled(String provider) {}
		  };
		  locationManager.requestLocationUpdates(locationProvider, 0, 0, locationListener);
		  
	}
	
	private void showInputDesiredRange(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
		builder.setTitle("Matching Desired Range (meters)");

		// Set up the input
		final EditText input = new EditText(this.getActivity());
		
		input.setInputType(InputType.TYPE_CLASS_NUMBER);
		builder.setView(input);

		// Set up the buttons
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() { 
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		        desiredRange = input.getText().toString();
		        if(desiredRange != null && !desiredRange.equals("") && Integer.parseInt(desiredRange) < 100000 ){
		        	getRecentPosition();
		        }else{
		        	Toast.makeText(getActivity(), "Invalid desired range", Toast.LENGTH_SHORT).show();
		        	showInputDesiredRange();
		        }
		    }
		});
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		        dialog.cancel();
		    }
		});

		builder.show();
	}
	
	
	private class MatchingUserAdapter extends ArrayAdapter<Bundle>{

	    private class ViewHolder {
	        ImageView userAvatar;
	        TextView userNameText;
	        TextView genderText;
	        TextView ageText;
	        TextView hobbyText;
	        TextView phoneText;
	    }
	    
		ViewHolder holder = null;
		Context context;
		public MatchingUserAdapter(Context context, int resource,
				 List<Bundle> objects) {
			super(context, resource, objects);
			this.context = context;
		}
		
		ImageView userAvatar = null;
		
	    public View getView(int position, View view, ViewGroup parent) {
			
			Bundle userInfo = getItem(position);
			
	        LayoutInflater mInflater = (LayoutInflater) context
	                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			
			if(view == null){
				view = mInflater.inflate(R.layout.matching_user_row, null);
				 holder = new ViewHolder();
		            holder.userAvatar = (ImageView) view.findViewById(R.id.avatar_image);
		            holder.userNameText = (TextView) view.findViewById(R.id.matching_user_name);
		            holder.genderText = (TextView) view.findViewById(R.id.matching_user_gender);
		            holder.ageText = (TextView) view.findViewById(R.id.matching_user_age);
		            holder.phoneText = (TextView) view.findViewById(R.id.matching_user_phone);
		            view.setTag(holder);
		        } else{
		            holder = (ViewHolder)view.getTag();
		        }
			
			holder.userNameText.setText(userInfo.getString("user_email"));
			holder.genderText.setText(userInfo.getString("gender"));
			holder.ageText.setText(userInfo.getString("age"));
			holder.hobbyText.setText(userInfo.getString("hobby"));
			holder.phoneText.setText(userInfo.getString("phone"));
			this.userAvatar = holder.userAvatar;
			new DownloadImageTask().execute(APIHandler.AUTH_SERVER_URL + userInfo.getString("avatar"));
	    	return view;
	    }
	    
	    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

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
		
	}
	
	private class MatchingTask extends AsyncTask<Location, Void, ArrayList<Bundle>>{

		@Override
		protected ArrayList<Bundle> doInBackground(Location... params) {
			// TODO Auto-generated method stub
			double altitude = params[0].getAltitude();
			double latitude = params[0].getLatitude();
			String mProvider = params[0].getProvider();
			double longitude = params[0].getLongitude();
			ArrayList<Bundle> matchingBundle = APIHandler.updateLocationMatching(getActivity(), userId, desiredRange, mProvider, String.valueOf(latitude), String.valueOf(longitude),String.valueOf(altitude));
			
			return matchingBundle;
		}
		
		@Override
		protected void onPostExecute(final ArrayList<Bundle> bundleList){
			if(bundleList == null){
				Toast.makeText(MatchingsFragment.this.getActivity(), "Matching results retrieve failed", Toast.LENGTH_SHORT).show();
			}else{
				matchingUserAdapter = new MatchingUserAdapter(getActivity(), android.R.id.list, bundleList );
				matchList.setAdapter(matchingUserAdapter);
				matchingUserAdapter.notifyDataSetChanged();
			}
		}
		
	}
}
