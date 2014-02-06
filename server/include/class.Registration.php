<?php
/**
 * class.Registration.php
 *
 * The model for user registration event
 *
 */

class Registration{
	
	private $db = null;
	private $core = null;
	
	public $error = "";
	public $ret = "";
	
	public function __construct(Core $c, Database $db){
		$this->core = $c;
		$this->db = $db;
	}

	public function registerNewUser($username = "", $password = "", $repeatpass = "", $useremail = "") {
	
		if (empty($username)) {
			$this->error = "Empty Username.";
		} elseif (empty($password) || empty($repeatpass)) {
			$this->error = "Empty Password.";
		} elseif ($password !== $repeatpass) {
			$this->error = "Password and password repeat are not the same.";
		} elseif (strlen($password) < 6) {
			$this->error = "Password has a minimum length of 6 characters.";
		} elseif (strlen($username) > 20 || strlen($username) < 4) {
			$this->error = "Username cannot be shorter than 4 or longer than 20 characters.";
		} elseif (!preg_match($this->core->username_pattern, $username)) {
			$this->error = "Username does not fit the name pattern.";
		} elseif (empty($useremail) or strlen($useremail) < 5) {
			$this->error = "Email cannot be shorter than 50 characters.";
		} elseif (strlen($useremail) > 64) {
			$this->error = "Email cannot be longer than 64 characters.";
		} elseif (!filter_var($useremail, FILTER_VALIDATE_EMAIL)) {
			$this->error = "Your email address is not in a valid email format.";
		} else {
		
			$this->db->connect();

			// escaping, additionally removing everything that could be (html/javascript-) code
			$username = $this->db->escapeStr(strip_tags($username, ENT_QUOTES));
			$useremail = $this->db->escapeStr(strip_tags($useremail, ENT_QUOTES));
			
			$user_password = $password;
			$user_password_hash = password_hash($user_password, PASSWORD_DEFAULT);

			$sql = "SELECT * FROM users WHERE user_name = '" . $username . "' OR user_email = '" . $useremail . "';";
			$query_check_user_name = $this->db->selectQuery($sql);

			if (sizeof($query_check_user_name) > 0) {
				$this->error = "Sorry, that username or email address is already taken.";
			} else {
				// write new user's data into database
				$sql = "INSERT INTO users (user_name, user_password, user_email)
						VALUES('" . $username . "', '" . $user_password_hash . "', '" . $useremail . "');";
				
				if ($this->db->insertQuery($sql)) {
					
					$sql = "SELECT user_lastActiveTime FROM users WHERE user_name = '" . $username . "';";

					$query = $this->db->selectQuery($sql);

					if (sizeof($query) == 1) {
						$this->ret = $this->core->getAccessToken($username, $query[0]["user_lastActiveTime"]);
					}
				
					return true;
				} else 
					$this->error = "Sorry, your registration failed. Please go back and try again.";
			}
		}
		
		return false;
	}
}