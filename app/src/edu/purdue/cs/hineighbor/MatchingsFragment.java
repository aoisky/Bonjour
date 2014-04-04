package edu.purdue.cs.hineighbor;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

public class MatchingsFragment extends Fragment {

	private long userId = 0L;
	private String desiredRange = "5";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.fragment_matchings, container, false);
		
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
		// Or use LocationManager.GPS_PROVIDER
		LocationManager locationManager = (LocationManager) this.getActivity().getSystemService(Context.LOCATION_SERVICE);
		Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
		if(lastKnownLocation == null){
			Toast.makeText(this.getActivity(), "GPS Information retrieved failed", Toast.LENGTH_SHORT).show();
			return;
		}
		new MatchingTask().execute(lastKnownLocation);
	}
	
	private void showInputDesiredRange(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
		builder.setTitle("Matching Desired Range");

		// Set up the input
		final EditText input = new EditText(this.getActivity());
		
		input.setInputType(InputType.TYPE_CLASS_NUMBER);
		builder.setView(input);

		// Set up the buttons
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() { 
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		        desiredRange = input.getText().toString();
		        getRecentPosition();
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
	
	private class MatchingTask extends AsyncTask<Location, Void, Void>{

		@Override
		protected Void doInBackground(Location... params) {
			// TODO Auto-generated method stub
			double altitude = params[0].getAltitude();
			double latitude = params[0].getLatitude();
			String mProvider = params[0].getProvider();
			double longitude = params[0].getLongitude();
			APIHandler.updateLocationMatching(getActivity(), userId, desiredRange, mProvider, String.valueOf(latitude), String.valueOf(longitude),String.valueOf(altitude));
			return null;
		}
		
	}
}
