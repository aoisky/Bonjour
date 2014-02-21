package edu.purdue.cs.hineighbor;


import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;

/**
 * Local database
 * @author Yudong Yang
 *
 */
public class SQLHandler extends SQLiteOpenHelper{

	private static final String LOG_TAG = "SQLHandler";
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "BonjourSQL.db";
	private static final String USER_TABLE_NAME = "users";
	private static final String FRIENDS_TABLE_NAME = "friends";
	private static final String TEXT_TYPE = " TEXT ";
	private static final String INTEGER_TYPE = " INTEGER ";
	private static final String NOT_NULL = " NOT NULL ";
	public static final String USER_ID = "_id";
	public static final String USER_NAME = "username";
	public static final String USER_EMAIL = "email";
	public static final String USER_AGE = "userage";
	public static final String USER_GENDER = "gender";
	public static final String USER_PASSWORD = "password";
	public static final String USER_AVATAR_PATH = "useravatarpath";
	public static final String USER_TIMESTAMP = "user_timestamp";
	
	public static final String FRIENDSLIST_USER_ID = "friendslist_user_id";
	public static final String FRIEND_PROFILE_ID = "profile_id";
	public static final String FRIEND_ID = "friend_key_id";
	public static final String FRIEND_NAME = "friend_name";
	public static final String FRIEND_HOBBY = "friend_hobby";
	public static final String FRIEND_LOCATION = "friend_loc";
	public static final String FRIEND_TYPE = "friend_type";
	public static final int FRIEND_TYPE_NONE = 0;
	public static final int FRIEND_TYPE_NORMAL = 1;
	public static final int FRIEND_TYPE_LIKE = 2;
	public static final String FRIEND_TIMESTAMP = "friend_timestamp";
	
	private static final String USER_SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + USER_TABLE_NAME;
	private static final String ARTICLE_SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + FRIENDS_TABLE_NAME;
	
	private static final String FRIEND_TABLE_CREATE = "CREATE TABLE " + FRIENDS_TABLE_NAME
			+ " (" 
			+ FRIENDSLIST_USER_ID + INTEGER_TYPE + NOT_NULL + "," 
			+ FRIEND_PROFILE_ID + INTEGER_TYPE + NOT_NULL + ","
			+ FRIEND_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ FRIEND_NAME + TEXT_TYPE + NOT_NULL + ","
			+ FRIEND_HOBBY + TEXT_TYPE + ","
			+ FRIEND_LOCATION + INTEGER_TYPE + ","
			+ FRIEND_TYPE + INTEGER_TYPE + ","
			+ FRIEND_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
			+ " )";
	

	private static final String USER_TABLE_CREATE = "CREATE TABLE " + USER_TABLE_NAME
			+ " (" 
			+ USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ USER_NAME + TEXT_TYPE + ","
			+ USER_EMAIL + TEXT_TYPE + ","
			+ USER_AGE + INTEGER_TYPE + ","
			+ USER_PASSWORD + TEXT_TYPE + ","
			+ USER_GENDER + INTEGER_TYPE + ","
			+ USER_AVATAR_PATH + TEXT_TYPE + ","
			+ USER_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
			+ " )";
	
	
	public SQLHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(USER_TABLE_CREATE);
		db.execSQL(FRIEND_TABLE_CREATE);
		Log.d(LOG_TAG, "Created user and article tables in the database");
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldversion, int newversion) {
		Log.d(LOG_TAG, "Upgrade from version " + oldversion + " to " + newversion + ", Previous database will be deleted.");
        db.execSQL(USER_SQL_DELETE_ENTRIES);
        db.execSQL(ARTICLE_SQL_DELETE_ENTRIES);
        
        onCreate(db);
	}
	
	
	public long addUser(UserInfo userInfo){
		if(userInfo == null) 
			return -1;
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(USER_NAME, userInfo.getUserName());
		values.put(USER_EMAIL, userInfo.getEmail());
		values.put(USER_AGE, userInfo.getAge());
		values.put(USER_PASSWORD, userInfo.getmd5Password());
		
		if(userInfo.getGender() == true){
			values.put(USER_GENDER, 1);
		} else{
			values.put(USER_GENDER, 0);
		}
		
	   // String dirName = Environment.getExternalStorageDirectory().toString();
		
	    //OutputStream os = null;
		
		long rowid = db.insert(USER_TABLE_NAME, null, values);
		Log.d(LOG_TAG, "A user has been added to the database: " + userInfo.getUserName());

		return rowid;
	}
	
    public int getFriendsCountByType(int userId, int friendType) {
        String countQuery = "SELECT * FROM " + FRIENDS_TABLE_NAME + " WHERE " + FRIEND_TYPE + " = " + friendType + " AND " + FRIENDSLIST_USER_ID + " = " + userId;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        Log.d(LOG_TAG, "Userid: " + userId + "Get friends count: " + count);

        return count;
    }
	
    public int getUsersCount() {
        String countQuery = "SELECT * FROM " + USER_TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        Log.d(LOG_TAG, "Get users count: " + count);

        return count;
    }
    
    public int getAllFriendsCountByUserId(long userId){
        String countQuery = "SELECT * FROM " + FRIENDS_TABLE_NAME + " WHERE " + FRIENDSLIST_USER_ID  + " = " + userId;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        Log.d(LOG_TAG, "Get all friends count: " + count);

        return count;
    }
    
    public List<Bundle> getAllFriendsByUserId(long userId){
		List<Bundle> articleList = new ArrayList<Bundle>();
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(FRIENDS_TABLE_NAME, new String[] { FRIENDSLIST_USER_ID, FRIEND_PROFILE_ID, FRIEND_ID, FRIEND_NAME, FRIEND_HOBBY, FRIEND_LOCATION, FRIEND_TYPE, FRIEND_TIMESTAMP}, FRIENDSLIST_USER_ID + " = ?", new String[]{String.valueOf(userId)}, null, null, null);
		
	       if (cursor.moveToFirst()) {
	            do {
	            	Bundle bundle = new Bundle();
	            	
	        		bundle.putLong(FRIENDSLIST_USER_ID, cursor.getLong(0));
	        		bundle.putLong(FRIEND_PROFILE_ID, cursor.getLong(1));
	        		bundle.putString(FRIEND_ID, cursor.getString(2));
	        		bundle.putString(FRIEND_NAME, cursor.getString(3));
	        		bundle.putString(FRIEND_HOBBY, cursor.getString(4));
	        		bundle.putString(FRIEND_LOCATION, cursor.getString(5));
	        		bundle.putString(FRIEND_TYPE, cursor.getString(6));
	        		bundle.putString(FRIEND_TIMESTAMP, cursor.getString(7));

	        		articleList.add(bundle);
	            } while (cursor.moveToNext());
	        }
	       
	       return articleList;
    }
    

	public long addFriend(Bundle bundle){
		if(bundle == null) 
			return -1;
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(FRIENDSLIST_USER_ID, bundle.getLong(FRIENDSLIST_USER_ID));
		values.put(FRIEND_PROFILE_ID, bundle.getString(FRIEND_PROFILE_ID));
		values.put(FRIEND_NAME, bundle.getString(FRIEND_NAME));
		values.put(FRIEND_HOBBY, bundle.getString(FRIEND_HOBBY));
		values.put(FRIEND_LOCATION, bundle.getString(FRIEND_LOCATION));
		values.put(FRIEND_TYPE, bundle.getInt(FRIEND_TYPE));
		long rowid = db.insert(FRIENDS_TABLE_NAME, null, values);
		Log.d(LOG_TAG, "userId: " + bundle.getLong(FRIENDSLIST_USER_ID) +" ,A friend added to the database: " + bundle.getString(FRIEND_NAME));

		return rowid;
	}
	


}