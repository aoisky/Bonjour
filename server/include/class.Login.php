<?php
/**
 * class.Login.php
 *
 * The model for user log in event
 *
 */

class Login{
	
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
		
		if ($t != ""){
			$this->logged_in = $this->verifyToken($this->core->getPOST("username"), $t);
		}
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
	
}
