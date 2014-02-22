<?php
/**
 * class.User.php
 *
 * The model for user identity module
 * @author	Xiangyu Bu
 * @date	Feb 07, 2014
 */

require_once "class.LoginException.php";
require_once "class.RegisterException.php";

class User{
	
	private $DEBUG_MODE = false;
	
	private $core = null;
	private $db = null;
	public $error = "";
	public $ret = "";
	
	public $user_profile = null;
	private $logged_in = false;

	public function __construct(Core $c, Database $d, $t) {
		$this->core = $c;
		$this->db = $d;
		
		if ($t != "")
			$this->logged_in = $this->verifyToken($this->core->getPOST("username"), $t);
	}
	
	public function isUser() {
		return $this->logged_in;
	}

	public function logIn($username = "", $password = "", $token = "") {
		// check login form contents
		if (!preg_match($this->core->username_pattern, $username) or $password == "") {
		
			$this->error = "Username or password not provided or of invalid format.";
			
		} else {
			
			$this->db->connect();
			
			$username = $this->db->escapeStr($username);
			
			$sql = "SELECT user_name, user_email, user_password, user_lastActiveTime FROM users ". 
					"WHERE user_name = '" . $username . "' OR user_email = '" . $username . "';";
			
			$query = $this->db->selectQuery($sql);
			$rows = $this->db->getNumOfRecords();
			if ($rows == 1) {

				if (password_verify($password, $query[0]["user_password"])) {
					$this->username = $username;
					$this->user_profile = $query[0];
					$this->logged_in = true;
					$this->ret = $this->core->getAccessToken($username, $query[0]["user_lastActiveTime"]);
					
					if ($this->DEBUG_MODE) echo $this->ret;
					
					return true;
				} else $this->error = "Wrong password. Try again.";
			} else $this->error = "This user does not exist.";
		}
		
		return false;
	}
	
	public function verifyToken($username = "", $token = "") {
		if ($username == "" or $token == "") return false;
		if (!preg_match($this->core->username_pattern, $username)) return false;
		
		$this->db->connect();
		$username = $this->db->escapeStr($username);
		
		$sql = "SELECT user_name, user_email, user_password, user_lastActiveTime FROM users
				WHERE user_name = '" . $username . "' OR user_email = '" . $username . "';";
		
		$query = $this->db->selectQuery($sql);
		$rows = $this->db->getNumOfRecords();
		
		if ($rows == 1) {
			if (password_verify($this->core->getOriginalToken($username, $query[0]["user_lastActiveTime"]), $token)) {
				$this->username = $username;
				$this->user_profile = $query[0];
				$this->logged_in = true;
				return true;
			}
		}
		
		return false;
	}

	public function logOut() {
		if (!$this->logged_in) return;
		
		$sql = "UPDATE users SET user_lastActiveTime=CURRENT_TIMESTAMP WHERE user_name='" . $this->user_profile["user_name"] . "';";
		//echo $sql;
		$this->db->updateQuery($sql);
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
