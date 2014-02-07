package edu.purdue.cs.hineighbor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class APIHandler {
	
	public static final String LOG_TAG = "APIHandlder";
	public static final String AUTH_SERVER = "http://mc18.cs.purdue.edu:8088/cs240/demo/action.php";
	
	/**
	 * Utility function
	 * Check the network availability of this device
	 * @return network availability
	 */
	public static boolean isNetworkAvaliable(Context context){
		ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			return true;
		}
		return false;
	}
	
	public static String login(String userName, String password){
		try {
			userName = 	URLEncoder.encode(userName, "UTF-8");
			password = URLEncoder.encode(password, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String loginStr = String.format("action=login&username=%s&password=%s", userName, password);
		
		return apiConnection(loginStr);

	}
	
	public static String register(String userName, String email, String password, String retype){
		String regStr = String.format("action=register&username=%s&email=%s&password=%s&retype=%s", userName, email,password,retype);
		
		if(password.equals(retype))
			return null;
		
		return apiConnection(regStr);
		
	}
	
	/**
	 * Connect to the server
	 * @param requestStr
	 * @return JSON String
	 */
	private static String apiConnection(String requestStr){
		URL url;
		try {
			Log.d(LOG_TAG, "Start API connection");
			url = new URL(AUTH_SERVER);
		
			HttpURLConnection urlConnect = (HttpURLConnection)url.openConnection();
			urlConnect.setReadTimeout(10000 /* milliseconds */);
			urlConnect.setConnectTimeout(10000 /* milliseconds */);
			
			urlConnect.setRequestMethod("POST");
			urlConnect.setDoInput(true);
			urlConnect.setDoOutput(true);
		    urlConnect.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

	        OutputStream sendData = urlConnect.getOutputStream();
	        BufferedWriter dataWriter = new BufferedWriter(new OutputStreamWriter(sendData));
	        if(requestStr != null)
	        	Log.d(LOG_TAG,"Request String: " + requestStr); //Log Info for debug
	        	dataWriter.write(requestStr);
	        dataWriter.close();
	        
	        urlConnect.connect();
	        
	        InputStream userData = urlConnect.getInputStream();
	        
	        BufferedReader reader = new BufferedReader(new InputStreamReader(userData));
	        String receivedStr = reader.readLine();
	        reader.close();
	        urlConnect.disconnect();
	        
	        Log.d(LOG_TAG,"Returned JSON Info:" + receivedStr); //Log Info for debug
	        return receivedStr;
	        
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.d(LOG_TAG, "API Connection Exception");
			e.printStackTrace();
		}catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}

}
