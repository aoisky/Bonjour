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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
import android.os.Bundle;
import android.util.Log;

/**
 * Bonjour API Handler
 * @author Yudong Yang
 *
 */
public class APIHandler {
	
	public static final String LOG_TAG = "APIHandler";
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
	
	/**
	 * Logout the user
	 * @param context
	 * @param userId
	 * @return logout result
	 */
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
    
    //Not FINISHED!!!
    public static Bundle getUserProfile(String userName){
    	Log.d(LOG_TAG, "Start getting user profile");
    	if(userName == null){
    		Log.d(LOG_TAG, "getUserProfile: username is null");
    		return null;
    	}
    	
    	String format = "action=getProfile&targetUser=%s";
    	
		String responseStr = apiConnection(String.format(format, userName));
		
		if(responseStr != null){
			if(getResponseCode(responseStr) == 200)
				
				
				return null;
			else
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
     * Get user security answer
     * @param userName
     * @return user security answer
     */
    public static String getSecurityQuestion(String userName){
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
			
			StringBuilder sb = new StringBuilder(2 * encodedByteStr.length);
			
			//Change byte array to string
			for(byte b : encodedByteStr){
				sb.append(String.format("%02x", b&0xff));
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
     * Get String from JSON
     * @param responseStr
     * @param jsonIndex
     * @return
     */
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
