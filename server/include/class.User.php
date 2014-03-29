<?php
/**
 * class.User.php
 *
 * The model for user identity module
 * @author	Xiangyu Bu
 * @date	Feb 20, 2014
 */

class User{
	private $core = null;
	private $db = null;
	public $user_info = array();
	public $logged_in_user = "";
	private $logged_in = false;
	private $DEBUG_MODE = false;
	
	public static $DEFAULT_USER_PROFILE = array(
		"gender" => "male",
		"birthday" => "2014-02-12",
		"age" => 13,
		"hobby" => "cs",
		"desiredRange" => 10,
		"phone" => "",
		"avatar" => ""
	);
	
	public function __construct(Core $c, Database $d, $t) {
		$this->core = $c;
		$this->db = $d;
		$this->db->connect();
		
		if ($t != ""){
			$u = $this->core->getPOST("username");
			$this->logged_in = $this->verifyToken($u, $t);
			if ($this->isUser()) $this->logged_in_user = $u;
		}
	}
	
	public function isUser() {
		return $this->logged_in;
	}
	
	public function logIn($username = "", $password = "", $token = "") {
		
		if ((!$this->core->isValidUserName($username) and !$this->core->isValidEmail($username)) or !$this->core->isValidPassword($password))
			throw new LoginException("Username or password not provided or of invalid format.");
		
		$username = $this->db->escapeStr($username);
			
		$sql = "SELECT user_id, user_name, user_email, user_password, user_lastActiveTime, user_profile FROM users ". 
				"WHERE user_name = '" . $username . "' OR user_email = '" . $username . "';";
		
		$query = $this->db->selectQuery($sql);
		$rows = $this->db->getNumOfRecords();
			
		if ($rows == 1) {
			if (password_verify($password, $query[0]["user_password"])) {
				$this->username = $query[0]["user_name"];
				$this->user_info = $query[0];
				$this->logged_in = true;
				return $this->core->getAccessToken($username, $query[0]["user_lastActiveTime"]);
			} else 
				throw new LoginException("Wrong password. Try again.");
		} else
				throw new LoginException("This user does not exist.");
		
		throw new LoginException("Unknown error.");
	}
	
	public function verifyToken($username = "", $token = "") {
		if ($username == "" or $token == "") return false;
		if (!$this->core->isValidUserName($username) and ! $this->core->isValidEmail($username)) return false;
		
		$this->db->connect();
		$username = $this->db->escapeStr($username);
		
		$sql = "SELECT user_name, user_email, user_password, user_lastActiveTime FROM users
				WHERE user_name = '" . $username . "' OR user_email = '" . $username . "';";
		
		$query = $this->db->selectQuery($sql);
		$rows = $this->db->getNumOfRecords();
		
		if ($rows == 1) {
			if (password_verify($this->core->getOriginalToken($username, $query[0]["user_lastActiveTime"]), $token)) {
				$this->logged_in_user = $username;
				$this->user_info = $query[0];
				$this->logged_in = true;
				return true;
			}
		}
		
		return false;
	}

	public function logOut() {
		if (!$this->logged_in) return;
		
		$sql = "UPDATE users SET user_lastActiveTime=CURRENT_TIMESTAMP WHERE user_name='" . $this->logged_in_user . "';";
		//echo $sql;
		$this->db->updateQuery($sql);
	}
	
	public function createUser($username = "", $password = "", $repeatpass = "", $useremail = "", $user_profile = array()) {
	
		if (empty($username)) {
			throw new RegisterException("Empty Username.");
		} elseif (empty($password) || empty($repeatpass)) {
			throw new RegisterException("Empty Password.");
		} elseif ($password !== $repeatpass) {
			throw new RegisterException("Password and password repeat are not the same.");
		} elseif (strlen($password) < 6) {
			throw new RegisterException("Password has a minimum length of 6 characters.");
		} elseif (strlen($username) > 20 || strlen($username) < 4) {
			throw new RegisterException("Username cannot be shorter than 4 or longer than 20 characters.");
		} elseif (!$this->core->isValidUserName($username)) {
			throw new RegisterException("Username does not fit the name pattern.");
		} elseif (empty($useremail) or strlen($useremail) < 3) {
			throw new RegisterException("Email cannot be shorter than 3 characters.");
		} elseif (strlen($useremail) > 255) {
			throw new RegisterException("Email cannot be longer than 255 characters.");
		} elseif (!$this->core->isValidEmail($useremail)) {
			throw new RegisterException("Your email address is not in a valid email format.");
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
				throw new RegisterException("Sorry, that username or email address is already taken.");
			} else {
				// write new user's data into database
				if (count($user_profile) == 0) $user_profile = self::$DEFAULT_USER_PROFILE;
				
				$user_profile_json = $this->db->escapeStr(json_encode($user_profile));
				
				$sql = "INSERT INTO users (user_name, user_password, user_email, user_profile)
						VALUES('" . $username . "', '" . $user_password_hash . "', '" . $useremail . "', '" . $user_profile_json . "');";
				
				if ($this->db->insertQuery($sql)) {
					
					$sql = "SELECT user_id, user_lastActiveTime FROM users WHERE user_name = '" . $username . "';";
					
					$query = $this->db->selectQuery($sql);

					if (sizeof($query) == 1) {
						return array("token" => $this->core->getAccessToken($username, $query[0]["user_lastActiveTime"]), "profile" => $user_profile);
					}
				
				} else 
					throw new RegisterException("Sorry, your registration failed. Please go back and try again.");
			}
		}
		
		throw new RegisterException("Unknown error.");
	}
	
	public function changePassword($userName = "", $oldPassword ="", $newPassword = "", $retypeNewPassword = "", $token = ""){
	
		if ($token != "" and !password_verify($userName . ":correct_ans", $token))
			throw new ResetPassException("Unauthorized operation.");
		
		if ($userName == null or ($token == "" and $oldPassword == null) or $newPassword == null or $retypeNewPassword == null)
			throw new ResetPassException("Username, old password, new password, and retype password must be filled in.");
		
		if ($token == "" and $oldPassword == $newPassword)
			throw new ResetPassException("New password and old password must be different.");
		
		if ($newPassword != $retypeNewPassword)
			throw new ResetPassException("New password and retype new password do not match.");
		
		if ((!$this->core->isValidUserName($userName) and ! $this->core->isValidEmail($userName)) or !$this->core->isValidPassword($newPassword) or ($token == "" and !$this->core->isValidPassword($oldPassword)))
			throw new ResetPassException("Username or password are not of valid format.");
		
		
		$sql = "SELECT user_name, user_email, user_password FROM users " .
				"WHERE user_name = '" . $userName . "' OR user_email = '" . $userName . "';";
		
		$query = $this->db->selectQuery($sql);
		$rows = $this->db->getNumOfRecords();
		
		if ($rows == 1) {
			if ($token != "" or password_verify($oldPassword, $query[0]["user_password"])) {
				$this->db->updateQuery("UPDATE users SET user_password=\"" . password_hash($newPassword, PASSWORD_DEFAULT) . "\" WHERE user_name = '" . $userName . "' OR user_email = '" . $userName . "';");
			} else 
				throw new ResetPassException("Username and password do not match, or user not registered.");
		} else 
			throw new ResetPassException("Username and password do not match, or user not registered.");
	}
	
	public function getSecurityQuestionOfUser($username){
		if ($username == null or $username == "" or (!$this->core->isValidUserName($username) and ! $this->core->isValidEmail($username)))
			throw new ResetPassException("Username is empty or of invalid format.");
		
		$sql = "SELECT user_securityQuestion FROM users " .
				"WHERE user_name = '" . $username . "' OR user_email = '" . $username . "';";
		
		$query = $this->db->selectQuery($sql);
		$rows = $this->db->getNumOfRecords();
		
		if ($rows == 1){
			$q_id = $query[0]["user_securityQuestion"];
			
			if ($q_id == "")
				throw new EmptyFieldException("The security question is unset.");
			
			if (!array_key_exists($q_id, $this->core->security_questions))
				throw new EmptyFieldException("The stored security question is invalid.");
			
			return $this->core->security_questions[$q_id];
			
		} else 
			throw new ResetPassException("The specified user does not exist.");
	}
	
	public function verifySecurityAnswer($username, $answer){
		if ($username == null or $username == "" or (!$this->core->isValidUserName($username) and ! $this->core->isValidEmail($username)))
			throw new ResetPassException("Username is empty or of invalid format.");
		
		$sql = "SELECT user_lastActiveTime, user_securityAnswerHash FROM users " .
				"WHERE user_name = '" . $username . "' OR user_email = '" . $username . "';";
		
		$query = $this->db->selectQuery($sql);
		$rows = $this->db->getNumOfRecords();
		
		if ($rows == 1){
			if (password_verify($answer, $query[0]["user_securityAnswerHash"])) {
				$this->username = $username;
				$this->logged_in = true;
				return $this->core->getAccessToken($username, $query[0]["user_lastActiveTime"]);
			} else throw new ResetPassException("Wrong security answer.");
		} else throw new ResetPassException("The specified user does not exist.");
	}
	
	public function generateNewPassword($username, $token){
		if ($username == null or $username == "" or (!$this->core->isValidUserName($username) and ! $this->core->isValidEmail($username)))
			throw new ResetPassException("Username is empty or of invalid format.");
		
		if (!password_verify($username . ":reset_pass", $token))
			throw new ResetPassException("Unauthorized operation.");
		
		$newpass = $this->core->getRandomStr(10);
		
		$this->changePassword($username, "", $newpass, $newpass, password_hash($username . ":correct_ans", PASSWORD_DEFAULT));
		
		return $newpass;
	}
	
	public function getProfile($username){
		if ($username == null or $username == "" or (!$this->core->isValidUserName($username) and ! $this->core->isValidEmail($username)))
			throw new UserProfileException("Username is empty or of invalid format.");
		
		$sql = "SELECT user_profile FROM users " .
				"WHERE user_name = '" . $username . "' OR user_email = '" . $username . "';";
		
		$query = $this->db->selectQuery($sql);
		$rows = $this->db->getNumOfRecords();
		
		if ($rows == 1){
			if (empty($query[0]["user_profile"])) return self::$DEFAULT_USER_PROFILE;
			else return json_decode($query[0]["user_profile"], true);
		} else 
			throw new UserProfileException("The specified user does not exist.");
	}
	
	public function setProfile($username, $prof){
		if ($username == null or $username == "" or (!$this->core->isValidUserName($username) and ! $this->core->isValidEmail($username)))
			throw new UserProfileException("Username is empty or of invalid format.");
		
		$sql = "UPDATE users SET user_profile=\"" . $this->db->escapeStr(json_encode($prof)) . "\" WHERE user_name='" . $username . "' OR user_email='" . $username . "' LIMIT 1;";
		$this->db->updateQuery($sql);
	}
	
	public function getMatchings($json){
		// $longitude = $json["mLongitude"];
		// $latitude = $json["mLatitude"];
		// $altitude = $json["mAltitude"];
		// will include the requester herself
		$sql = "SELECT user_id, user_email, user_profile, user_hobby FROM users " .
				//
				// ( 3959 * acos( cos( radians(43.493655) ) * cos( radians(" &  latitude & ") ) * cos( radians(" &  longitude & ") - radians(-1.474941) ) + sin( radians(43.493655) ) * sin( radians(" &  latitude & ") ) ) ) AS distance
				// WHERE should include a distance check
				// also add a LIMIT directive
				";";
		$query = $this->db->selectQuery($sql);
		return $query;
	}
	
	public function saveUserAvatar($img_str){
		//error_reporting(E_ERROR);
		$img = imagecreatefromstring($img_str);
		if (!$img) return "";
		$tmp_filename = $this->core->getRandomStr(24);
		if (imagepng($img, realpath(dirname(__FILE__) . "/../public/upload") . "/" . $tmp_filename . ".png", 9,  PNG_ALL_FILTERS))
			return "/public/upload/" . $tmp_filename . ".png";
		return "";
	}
	
	public function setSecurityCreds($userName, $q, $a){
		$this->db->updateQuery("UPDATE users SET user_securityQuestion=\"" . $q . "\", user_securityAnswerHash=\"" . password_hash($a, PASSWORD_DEFAULT) . "\" WHERE user_name = '" . $userName . "' OR user_email = '" . $userName . "' LIMIT 1;");
	}
}

class UserException extends Exception {
	protected $errno;
	
	public function __construct($message = "", $code = 0, $errno = "0"){
		$this->errno = $errno;
		parent::__construct($message, $code);
	}
	
	public function getErrno(){
		return $this->errno;
	}
}

class LoginException extends UserException {
	public function __construct($message = ""){
		parent::__construct($message, 400, "200");
	}
}

class RegisterException extends UserException {
	public function __construct($message = ""){
		parent::__construct($message, 400, "100");
	}
}

class ResetPassException extends UserException {
	public function __construct($message = ""){
		parent::__construct($message, 400, "300");
	}
}

class EmptyFieldException extends UserException {
	public function __construct($message = ""){
		parent::__construct($message, 400, "400");
	}
}

class UserProfileException extends UserException {
	public function __construct($message = ""){
		parent::__construct($message, 400, "500");
	}
}

class UserAvatarException extends UserException {
	public function __construct($message = ""){
		parent::__construct($message, 500, "600");
	}
}