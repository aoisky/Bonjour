package edu.purdue.cs.hineighbor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;


import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;

/**
 * Bonjour API Handler
 * @author Yudong Yang
 *
 */
public class APIHandler {
	
	public static final String LOG_TAG = "APIHandler";
	public static final String AUTH_SERVER_URL = "http://mc18.cs.purdue.edu:8088/cs240/demo";
	public static final String AUTH_SERVER = "http://mc18.cs.purdue.edu:8088/cs240/demo/action.php";
	
	//Some string constants for bundle keys
	public static final String USER_ID = "userId";
	public static final String USERNAME = "username";
	public static final String GENDER = "gender";
	public static final String ACCESS_TOKEN = "access_token";
	public static final String PROCEED_TOKEN = "proceed_token";
	public static final String BIRTHDAY = "birthday";
	public static final String DESIRED_RANGE = "desiredRange";
	public static final String PHONE = "phone";
	public static final String DATA = "data";
	public static final String MATCHINGS = "matchings";
	public static final String HOBBY = "hobby";
	
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
	
	/**
	 * Log into a user account
	 * @param context
	 * @param userName
	 * @param password
	 * @return userId in the database
	 */
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
			}else{
				Log.d(LOG_TAG, "Login: set access token succefully");
				return sqlHandler.getUserIdByUserName(userName);
			}
			
		}
		Log.d(LOG_TAG, "Login Error");
		return -1L;
	}
	/**
	 * Register a user
	 * @param context
	 * @param userName
	 * @param password
	 * @param retype
	 * @param gender
	 * @param age
	 * @param userIcon
	 * @return userId in the database
	 */
	public static long register(Context context, String userName, String password, String retype, boolean gender, int age, Bitmap userIcon ){
		Log.d(LOG_TAG, "Start register");
		
		String email = userName;
		
		userName = userName.substring(0,userName.indexOf("@"));
		
		String regStr = String.format("action=register&username=%s&email=%s&password=%s&retype=%s", userName, email, password, password);
		regStr = regStr.concat("&age=" + age);
		
		if(userIcon != null){
			
			ByteArrayOutputStream blob = new ByteArrayOutputStream();
			userIcon.compress(Bitmap.CompressFormat.PNG, 90, blob);
		
			byte[] bitmapdata = blob.toByteArray();
			String avatarBase64 = Base64.encodeBytes(bitmapdata);
			
			try {
				regStr = regStr.concat("&avatar=" + URLEncoder.encode(avatarBase64,"UTF-8"));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		String responseStr = apiConnection(regStr);
		
		int codeInt = APIHandler.getResponseCode(responseStr);

		if(codeInt == 200){
			
			String accessToken = APIHandler.getStringFromJSON(responseStr, "token");
			//String avatarPath = APIHandler.getStringFromJSON(responseStr, "avatar");
			
			SQLHandler sqlHandler = SQLHandler.getInstance(context);
			
			UserInfo userInfo = new UserInfo(userName, accessToken, age, userName, password, gender);
			
			long userId = sqlHandler.addUser(userInfo);
			
			return userId;
		}
		
		return -1;
		
	}
	
	/**
	 * Logout the user
	 * @param context
	 * @param userId
	 * @return logout result
	 */
	public static boolean logout(Context context, int userId){
		String accessToken = getUserAccessToken(context, userId);
		String userName = getUserName(context, userId);
		
		String logoutStr = String.format("username=%s&access_token=%s&action=logout", userName, accessToken);
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
	
	/**
	 * Change user's password
	 * @param context
	 * @param userId
	 * @param oldPassword
	 * @param newPassword
	 * @param retype
	 * @return change password result
	 */
	public static boolean changePassword(Context context, int userId, String oldPassword, String newPassword, String retype){
		String accessToken = getUserAccessToken(context, userId);
		String userName = getUserName(context, userId);
		
		String changePasswordStr = String.format("username=%s&access_token=%s&oldpassword=%s&newpassword=%s&retype=%s&action=change_password", userName, accessToken, oldPassword, newPassword, retype);
		String responseStr = apiConnection(changePasswordStr);
		
		if(responseStr != null){
			if(getResponseCode(responseStr) == 200)
				return true;
			else
				return false;
		}else{
			return false;
		}

	}
	
	/**
	 * Get user name by userId
	 * @param context
	 * @param userId
	 * @return user name
	 */
    public static String getUserName(Context context, int userId){
		SQLHandler sql = SQLHandler.getInstance(context);
		String userName = sql.getUserNameByUserId(userId);
		return userName;
    }
    

    /**
     * Get user Id by userName
     * @param context
     * @param userName
     * @return userId
     */
    public static long getUserId(Context context, String userName){
    	SQLHandler sql = SQLHandler.getInstance(context);
    	long userId = sql.getUserIdByUserName(userName);
    	return userId;
    }
    
    /**
     * Get access token by user id in the database
     * @param context
     * @param userId
     * @return
     */
    public static String getAccessTokenByUserId(Context context, int userId){
    	SQLHandler sql = SQLHandler.getInstance(context);
    	String accessToken = sql.getUserAccessToken(userId);
    	
    	return accessToken;
    }
    
    /**
     * Get a user profile bundle by username 
     * @param userName
     * @return userProfile bundle
     */
    public static Bundle getUserProfile(String userName){
    	Log.d(LOG_TAG, "Start getting user profile");
    	if(userName == null){
    		Log.d(LOG_TAG, "getUserProfile: username is null");
    		return null;
    	}
    	
    	String format = "action=getProfile&targetUser=%s";
    	
		String responseStr = apiConnection(String.format(format, userName));
		
		if(responseStr != null){
			if(getResponseCode(responseStr) == 200){
				Bundle bundle = new Bundle();
				HashMap<String, String> hashMap = getHashMapFromJSON(responseStr, DATA);
				
				for(String key : hashMap.keySet()){
					String value = hashMap.get(key);
					Log.d(LOG_TAG, "Profile{ " + key +" }:" + value);
					bundle.putString(key, value);
				}
				return bundle;
				
			} else
				return null;
		}else{
			return null;
		}
    	
    }
    
    /**
     * Update user profile
     * @param context
     * @param bundle
     * @return update result
     */
    public static boolean updateUserProfile(Context context, Bundle bundle){
    	Log.d(LOG_TAG, "Start updating user profile");
    	long userId = 0L;
    	String userName;
    	String accessToken;
    	String gender;
    	String birthday;
    	String desiredRange;
    	String phone;
    	StringBuffer strBuf = new StringBuffer();
    	String format = "action=updateProfile&username=%s&access_token=%s";
    	String requestFormat = "&%s=%s";
    	
    	if(context != null && bundle != null){
    		if( ( (userName = bundle.getString(USERNAME)) == null && (userId = bundle.getLong(USER_ID)) == 0L) || (accessToken = bundle.getString(ACCESS_TOKEN) )== null ){
    			return false;
    		}else{
    			//Construct request string
    			if(userName == null){
    				userName = getUserName(context, (int)userId);
    			}
    			
    			strBuf.append(String.format(format, userName, accessToken));
    			
    			if((gender = bundle.getString(GENDER)) != null){
    				strBuf.append(String.format(requestFormat, GENDER, gender));
    			}
    			
    			if((birthday = bundle.getString(BIRTHDAY)) != null){
    				strBuf.append(String.format(requestFormat, BIRTHDAY, birthday));
    			}
    			
    			if((desiredRange = bundle.getString(DESIRED_RANGE)) != null){
    				strBuf.append(String.format(requestFormat, DESIRED_RANGE, desiredRange));
    			}
    			
    			if((phone = bundle.getString(PHONE)) != null){
    				strBuf.append(String.format(requestFormat, PHONE, phone));
    			}
    			
    			String responseStr = apiConnection(strBuf.toString());
    			
    			if(responseStr != null){
    				if(getResponseCode(responseStr) == 200){
    					Log.d(LOG_TAG, "Profile updated successful");
    					return true;
    				}else{
    					Log.d(LOG_TAG, "Profile updated failed");
    					return false;
    				}
    			}else{
    				Log.d(LOG_TAG, "Profile updated failed");
    				return false;
    			}
    			
    		}
    			
    	}
    	Log.d(LOG_TAG, "Profile updated failed");
    	return false;
    }
    
    /**
     * Get security questions
     * @return security questions hashMap
     */
    public static HashMap<String,String> getSecurityQuestions(){
    	String format = "action=getSecurityQuestions";
    	
    	String responseStr = apiConnection(format);
    	
    	if(responseStr != null){
			if(getResponseCode(responseStr) == 200){
				HashMap<String, String> hashMap = getHashMapFromJSON(responseStr, DATA);
				
				for(String key : hashMap.keySet()){
					Log.d(LOG_TAG, "Security Question{ " + key +" }:" + hashMap.get(key));
				}
				
				return hashMap;
			}else
				return null;
		}else{
			return null;
		}
    	
    }
    
    /**
     * 
     * @param userName
     * @return security question set result
     */
    public static boolean setUserSecurityQuestion(Context context, long userId, String questionNo, String answer){
    	String format = "action=setSecurityAnswer&question=%s&username=%s&access_token=%s&answer=%s";
    	String userName = APIHandler.getUserName(context, (int)userId);
    	SQLHandler sqlHandler = SQLHandler.getInstance(context);
    	String accessToken = sqlHandler.getUserAccessToken((int)userId);
    	
    	String responseStr = apiConnection(String.format(format, questionNo, userName, accessToken, getMD5(answer)));
    	
    	if(responseStr != null){
			if(getResponseCode(responseStr) == 200){
				return true;
			}else{
				return false;
			}
    	}
				
    	
    	return false;
    }
    
    /**
     * Get user security answer
     * @param userName
     * @return user security answer
     */
    public static String getUserSecurityQuestion(String userName){
    	String format = "action=forgotPassword&username=%s";
    	
    	String responseStr = apiConnection(String.format(format, userName));
    	
    	if(responseStr != null){
			if(getResponseCode(responseStr) == 200)
				return getStringFromJSON(responseStr, DATA);
			else
				return null;
		}else{
			return null;
		}
    	
    }
    
    
/**
 * Verify the security answer
 * @param userName
 * @param answer
 * @return access_token and proceed_token bundle, if error returns null
 */
    public static Bundle verifySecurityAnswer(String userName, String answer){
    	
    	String format = "action=verifySecurityAnswer&username=%s&answer=%s";
    	
    	String responseStr = apiConnection(String.format(format, userName, getMD5(answer)));
		
		if(responseStr != null){
			if(getResponseCode(responseStr) == 200){
				Bundle bundle = new Bundle();
				bundle.putString(ACCESS_TOKEN, getStringFromJSON(responseStr, ACCESS_TOKEN));
				bundle.putString(PROCEED_TOKEN, getStringFromJSON(responseStr, PROCEED_TOKEN));
				return bundle;
		}else
				return null;
		}else{
			return null;
		}
    	
    }
    
    /**
     * Generate new password
     * @param userName
     * @param proceedToken
     * @return success or fail
     */
    public static boolean generateNewPassword(String userName, String proceedToken){
    	String format = "action=genNewPassword&proceed_token=%s&username=%s";
    	
    	String responseStr = apiConnection(String.format(format, proceedToken, userName ));
		
		if(responseStr != null){
			if(getResponseCode(responseStr) == 200){
				return true;
		}else
				return false;
		}else{
			return false;
		}
    }
    
    /**
     * Change forgotten password
     * @param userName
     * @param proceedToken
     * @param newPassword
     * @return success or fail
     */
    public static boolean changeForgottenPassword(String userName, String proceedToken, String newPassword){
    	String format = "action=changeForgottenPassword&proceed_token=%s&username=%s&newpassword=%s&retype=%s";
    	
    	String responseStr = apiConnection(String.format(format, proceedToken, userName, newPassword, newPassword ));
		
		if(responseStr != null){
			if(getResponseCode(responseStr) == 200){
				return true;
		}else
				return false;
		}else{
			return false;
		}
    }
    
    /**
     * Update location and obtain bundle list that contains user information
     * @param desiredDistance
     * @param mProvider
     * @param mLatitude
     * @param mLongitude
     * @param mAltitude
     * @return
     */
    public static ArrayList<Bundle> updateLocationMatching(Context context, long userId, String desiredDistance,String mProvider, String mLatitude, String mLongitude, String mAltitude, ArrayList<Bundle> bundleList){
    	Log.d(LOG_TAG, "Start updating location and matching users");
    	String format = "action=match&username=%s&access_token=%s&data=%s";
    	
    	//Get user login information
    	String userName = getUserName(context, (int)userId);
    	String accessToken = getAccessTokenByUserId(context,(int) userId);
    	
    	JSONObject object = new JSONObject();

    		try {
    	    	if(desiredDistance != null){
    	    		object.put("desiredRange", desiredDistance);
    	    	}else{
    	    		object.put("desiredRange", "");
    	    	}
    	    	
    	    	if(mProvider != null){
    	    		object.put("mProvider", mProvider);
    	    	}else{
    	    		object.put("mProvider", "");
    	    	}
    	    	
    	    	if(mLatitude != null){
    	    		object.put("mLatitude", mLatitude);
    	    	}else{
    	    		object.put("mLatitude", "");
    	    	}
    	    	
    	    	if(mLongitude != null){
    	    		object.put("mLongitude", mLongitude);
    	    	}else{
    	    		object.put("mLongitude", "");
    	    	}
    	    	
    	    	if(mAltitude != null){
    	    		object.put("mAltitude", mAltitude);
    	    	}else{
    	    		object.put("mAltitude", "");
    	    	}
    	    	
    	    	String locationJSON = object.toString();
    	    	String base64LocationString = getBase64String(locationJSON);
    	    	
    	    	String responseStr = apiConnection(String.format(format, userName, accessToken, base64LocationString));
    	    	
    	    	if(responseStr != null){
    				if(getResponseCode(responseStr) == 200){
    					return getBundleListFromJSON(responseStr, MATCHINGS, bundleList);
    					
    				} else
    					return null;
    			}else{
    				return null;
    			}
    	    	
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
    	
    	
    }
    
    /**
     * Utility function to convert a string to base64
     * @param str
     * @return
     */
    private static String getBase64String(String str){
    	byte[] strBytes = str.getBytes();
    	
    	return Base64.encodeBytes(strBytes);
    }
    
    /**
     * Utility function to convert a string to md5 string
     * @param str
     * @return md5 string
     */
    private static String getMD5(String str){
    	
    	try {
    		byte[] byteStr;
			byteStr = str.getBytes("UTF-8");
			
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			byte[] encodedByteStr = md5.digest(byteStr);
			
						
		       StringBuffer sb = new StringBuffer();
		        for (int i = 0; i < encodedByteStr.length; ++i) {
		          sb.append(Integer.toHexString((encodedByteStr[i] & 0xFF) | 0x100).substring(1,3));
		       }
			
			return sb.toString();

		
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return null;
    	
    }
    
    /**
     * Get a bundle list that contains matching information
     * @param responseStr
     * @param jsonIndex
     * @return bundleList
     */
    private static ArrayList<Bundle> getBundleListFromJSON(String responseStr, String jsonIndex, ArrayList<Bundle> bundleList){
    	JSONTokener tokener = new JSONTokener(responseStr);
    	
    	
    	JSONObject object;
		try {
			object = (JSONObject) tokener.nextValue();
    		JSONArray arrayObject = (JSONArray) object.get(jsonIndex);
    		
    		int arrayLength = arrayObject.length();
    		for(int i = 0; i < arrayLength; i++){
    			JSONObject jsonObject = arrayObject.getJSONObject(i);
    			
    			int objectLength = jsonObject.length();
				JSONArray keyArray = jsonObject.names();
				Log.d(LOG_TAG,"getBundleListFromJSON: New bundle created");
				Bundle bundle = new Bundle();
    			for(int j = 0; j < objectLength; j++){

    	    		String jsonName = keyArray.getString(j);
    	    		String value = jsonObject.getString(jsonName);
    	    		Log.d(LOG_TAG, "Put value into bundle{ " + jsonName + "}: " + value);
    	    		bundle.putString(jsonName, value);
    				
    			}
    			bundleList.add(bundle);

    		}
    		
    		return bundleList;
    		
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
    	
    	
    }
    
    
    /**
     * Return a HashMap that contains information of the JSON index
     * @param responseStr
     * @param jsonIndex
     * @return jsondataHashMap
     */
    private static HashMap<String, String> getHashMapFromJSON(String responseStr, String jsonIndex){
    	JSONTokener tokener = new JSONTokener(responseStr);
    	HashMap<String, String> hashMap = new HashMap<String, String>();
    	
    	JSONObject object;
			try {
				object = (JSONObject) tokener.nextValue();
	    		JSONObject mapObject = (JSONObject) object.get(jsonIndex);
	    		
	    		JSONArray nameArray = mapObject.names();
	    		
	    		int length = nameArray.length();
	    		for(int i = 0; i < length; i++){
	    			String jsonName = nameArray.getString(i);
	    			String value = mapObject.getString(jsonName);
	    			hashMap.put(jsonName, value);
	    		}
	    		
	    		return hashMap;
	    		
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}catch (Exception e){
				e.printStackTrace();
				Log.d(LOG_TAG, "JSON parse hashMap unknown error");
			}
			
			return null;
    	
    }
    
    
    /**
     * Get String from JSON
     * @param responseStr
     * @param jsonIndex
     * @return
     */
    private static String getStringFromJSON(String responseStr, String jsonIndex){
    	JSONTokener tokener = new JSONTokener(responseStr);
        
    	try {
			JSONObject object = (JSONObject) tokener.nextValue();
			String str = object.getString(jsonIndex);
			Log.d(LOG_TAG, "Obtained JSON string: " + str);
			return str;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			Log.d(LOG_TAG, "JSON parse expcetion");
			e.printStackTrace();
		}catch (Exception e){
			e.printStackTrace();
			Log.d(LOG_TAG, "JSON parse string unknown error");
		}
    	
    	return null;
    	
    }
    
    /**
     * Get response code from JSON string
     * @param responseStr
     * @return response code
     */
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
    
	/**
	 * Get user access token
	 * @param context
	 * @param userId
	 * @return userAccessToken
	 */
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
		Log.d(LOG_TAG, "Start API connection");
		URL url;
		try {
			
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
	        
	        String otherInfo;
	        
	        while((otherInfo = reader.readLine()) != null){
	        	Log.d(LOG_TAG, "Other received info: " + otherInfo);
	        }
	        
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
