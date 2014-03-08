package edu.purdue.cs.hineighbor;





import android.graphics.Bitmap;


/**
 * A class to save user information
 * @author Yudong Yang
 *
 */
public class UserInfo{
	

	private int uid;
	private String userName;
	private Bitmap userAvatar;
	private int age;
	private String email;
	private String md5Password;
	private boolean gender;
	private String accessToken;
	
	public UserInfo(String userName, String accessToken, int uid, Bitmap userAvatar, int age, String email, String md5Password, boolean gender){
		this.uid = uid;
		this.userName = userName;
		this.accessToken = accessToken;
		this.userAvatar = userAvatar;
		this.age = age;
		this.email = email;
		this.md5Password = md5Password;
		this.gender = gender;
	}
	
	public int getuid(){
		return uid;
	}
	
	public String getUserAccessToken(){
		return accessToken;
	}
	
	public String getUserName(){
		return userName;
	}
	
	public Bitmap getUserAvatar(){
		return userAvatar;
	}
	
	public int getAge(){
		return age;
	}
	
	public String getEmail(){
		return email;
	}
	
	public String getmd5Password(){
		return md5Password;
	}

	public boolean getGender(){
		return gender;
	}

	public int getIntGender(){
		if(gender == true){	
			
			return 1;
			
		} else{
			
			return 0;	
			
		}

	}
		
}
