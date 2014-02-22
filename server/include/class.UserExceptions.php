<?php
/**
 * class.UserExceptions.php
 *
 * The exception models for User class.
 * @author	Xiangyu Bu
 * @date	Feb 20, 2014
 */

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