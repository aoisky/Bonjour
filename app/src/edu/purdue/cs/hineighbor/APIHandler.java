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
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import android.content.Context;
import android.graphics.Bitmap;
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
	
	public static long login(Context context, String userName, String password){
		try {
			userName = 	URLEncoder.encode(userName, "UTF-8");
			password = URLEncoder.encode(password, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String loginStr = String.format("action=login&username=%s&password=%s", userName, password);
		String responseStr = apiConnection(loginStr);
		Log.d(LOG_TAG, "loginResponseStr: " + responseStr);
		
		if(APIHandler.getResponseCode(responseStr) == 200){
			
			String accessToken = APIHandler.getStringFromJSON(responseStr, "access_token");
			Log.d(LOG_TAG, "login access token: " + accessToken);
			SQLHandler sqlHandler = SQLHandler.getInstance(context);
			
			//Decode the userName and password to insert the database
			try {
				userName = URLDecoder.decode(userName,"UTF-8");
				password = URLDecoder.decode(password,"UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(sqlHandler.setUserAccessToken(userName, accessToken) == false){
				Log.d(LOG_TAG, "Login: No exists user entry in the database");
				
				//Create Default user profile
				UserInfo userInfo = new UserInfo(context, userName, accessToken, password);
				return sqlHandler.addUser(userInfo);
			}
			
		}
		Log.d(LOG_TAG, "Login Error");
		return -1L;
	}
	
	public static long register(Context context, String userName, String password, String retype, boolean gender, int age, Bitmap userIcon ){
		
		String regStr = String.format("action=register&username=%s&email=%s&password=%s&retype=%s", userName, userName, password, retype);
		if(password.equals(retype))
			return -1;
		
		String responseStr = apiConnection(regStr);
		
		int codeInt = APIHandler.getResponseCode(responseStr);
		
		if(codeInt == 200){
			
			String accessToken = APIHandler.getStringFromJSON(responseStr, "access_token");
			
			
			SQLHandler sqlHandler = SQLHandler.getInstance(context);
			
			UserInfo userInfo = new UserInfo(userName, accessToken, 0, userIcon, age, userName, password, gender);
			
			long userId = sqlHandler.addUser(userInfo);
			
			return userId;
		}
		
		return -1;
		
	}
	
	public static boolean logout(Context context, int userId){
		String accessToken = getUserAccessToken(context, userId);
		String userName = getUserName(context, userId);
		
		String logoutStr = String.format("username=%s&access_token=%saction=logout", userName, accessToken);
		String responseStr = apiConnection(logoutStr);
		
		if(responseStr != null){
			if(getResponseCode(responseStr) == 200)
				return true;
			else
				return false;
		}else{
			return false;
		}

	}
	
	public static boolean changePassword(Context context, int userId, String oldPassword, String newPassword, String retype){
		String accessToken = getUserAccessToken(context, userId);
		String userName = getUserName(context, userId);
		
		String changePasswordStr = String.format("username=%s&access_token=%s&oldpassword=%s&newpassword=%s&retype=%s&action=change_password", userName, accessToken, oldPassword, newPassword, retype);
		String responseStr = apiConnection(changePasswordStr);
		
		if(apiConnection(changePasswordStr) != null){
			if(getResponseCode(responseStr) == 200)
				return true;
			else
				return false;
		}else{
			return false;
		}

	}
	
    private static String getUserName(Context context, int userId){
		SQLHandler sql = SQLHandler.getInstance(context);
		String userName = sql.getUserNameByUserId(userId);
		return userName;
    }
    
    
    private static String getStringFromJSON(String responseStr, String jsonIndex){
        JSONParser parser = new JSONParser();
        if( responseStr == null || jsonIndex == null)
        	return null;
        
        ContainerFactory containerFactory = new ContainerFactory(){
            public List creatArrayContainer() {
              return new LinkedList();
            }

            public Map createObjectContainer() {
              return new LinkedHashMap();
            }
        };
        
        String jsonEntry = null;
        
        try {
        	Map authJSON = (Map)parser.parse(responseStr, containerFactory);
        	if(jsonIndex.equals("code")){
        		long responseCode = (Long) authJSON.get(jsonIndex);
        		jsonEntry = String.valueOf(responseCode);
        	}else{
        		jsonEntry = (String)authJSON.get(jsonIndex);
        	}
	        Log.d(LOG_TAG, "Get String From JSON: " + jsonEntry);
	        
	        return jsonEntry;
	        
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        return null;
    }
    
    private static int getResponseCode(String responseStr){
        String responseCodeStr = getStringFromJSON(responseStr, "code");
        if(responseCodeStr == null) {
   	        Log.d(LOG_TAG, "Failed to get response code, responseCodeStr is null");
        	return -1;
        }
        
        int responseCode;
        
        try{
        	responseCode = Integer.parseInt(responseCodeStr);
        }catch(NumberFormatException e){
        	
        	responseCode = -1;
        }
        
        return responseCode;
    }
	
	private static String getUserAccessToken(Context context, int userId){
		SQLHandler sql = SQLHandler.getInstance(context);
		String accessToken = sql.getUserAccessToken(userId);
		
		return accessToken;
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
