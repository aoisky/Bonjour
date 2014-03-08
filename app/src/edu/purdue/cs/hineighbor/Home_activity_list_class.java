package edu.purdue.cs.hineighbor;
/* The class is for storing the data that will be used in ListView in HomeActivity.
 * The data in the class should not be modified
 */

public class Home_activity_list_class {
	private String name;
	private String hobbies;
	int i = 0;
	
	public String getName(){
		return name;
	}
	
	public void setHobbies(String hobbies){
		this.hobbies = hobbies;
	}
	public Home_activity_list_class(String name, String hobbies){
		this.name = name;
		this.hobbies = hobbies;
	}
	
	public String getHobbies(){
		return hobbies;
	}
	
	
	
}
