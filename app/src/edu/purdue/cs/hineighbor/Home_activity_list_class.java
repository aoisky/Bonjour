package edu.purdue.cs.hineighbor;
/*
 * 
 */

public class Home_activity_list_class {
	private String name;
	private String hobbies;
	
	public String getName(){
		return name;
	}
	
	public Home_activity_list_class(String name, String hobbies){
		this.name = name;
		this.hobbies = hobbies;
	}
	
	public void setHobbies(String hobbies){
		this.hobbies = hobbies;
	}
	
	
}
