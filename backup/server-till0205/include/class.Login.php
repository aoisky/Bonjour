<?php
/**
 * class.Login.php
 *
 * The model for user log in event
 *
 */

class Login{

	private $core = null;
	private $db = null;
	
	public $error = "";
	public $ret = "";

	public function __construct(Core $c, Database $d) {
		// create/read session, absolutely necessary
		session_start();
		$this->core = $c;
		$this->db = $d;
	}

	public function logIn($username = "", $password = "", $token = "") {
		// check login form contents
		if (!preg_match($this->core->username_pattern, $username) or $password == "") {
		
			$this->error = "Username or password not provided or of invalid format.";
			
		} else {
			
			$this->db->connect();
			
			$username = $this->db->escapeStr($username);

			$sql = "SELECT user_name, user_email, user_password, user_lastActiveTime FROM users
					WHERE user_name = '" . $username . "' OR user_email = '" . $username . "';";

			$query = $this->db->selectQuery($sql);

			if (sizeof($query) == 1) {

				if (password_verify($password, $query[0]["user_password"])) {
					
					$this->ret = $this->core->getAccessToken($username, $query[0]["user_lastActiveTime"]);
					
					// write user data into PHP SESSION (a file on your server)
					$_SESSION['user_name'] = $query[0]["user_name"];
					$_SESSION['user_email'] = $query[0]["user_email"];
					$_SESSION['user_login_status'] = 1;
					return true;
					
				} else {
					$this->error = "Wrong password. Try again.";
					
				}
			} else {
				$this->error = "This user does not exist.";
			}
		}
		
		return false;
	}

	public function logOut() {
		
		$this->db->updateQuery("UPDATE user_lastActiveTime=CURRENT_TIMESTAMP WHERE user_name='" + $_SESSION['user_name'] + "';");
		
		// delete the session of the user
		$_SESSION = array();
		session_destroy();
		// return a little feeedback message
		
		return true;
	}
	
}
