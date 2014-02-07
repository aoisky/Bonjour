<?php
/**
 * class.Core.php
 *
 * The system core
 * 
 * Handles Json input and output
 * 
 * @author	Xiangyu Bu
 * @date	Feb 05, 2014
 */

class Core {
	
	public $username_pattern = "/^[-*_a-z0-9A-Z.]{4,20}$/";
	
	protected $tokenHash = "ThisBonjOURInstanceNeedsSalt";
	
	public function __construct() {
	}
	
	public function __destruct() {
		$this->json = null;
	}
	
	public function jsonDump($data){
		$s = json_encode($data, JSON_PRETTY_PRINT);
		header("Content-Type: application/json");
		header("Cache-Control: no-cache, must-revalidate");
		header("Content-Length: " . strlen($s));
		die($s);
	}
	
	public function returnJsonError($msg) {
		$this->jsonDump(array("code" => 400, "errno" => "001", "message" => $msg));
	}
	
	public function dieLoginError($error) {
		$this->jsonDump(array("code" => 400, "errno" => "200", "message" => $error));
	}
	
	public function dieRegisterError($error) {
		$this->jsonDump(array("code" => 400, "errno" => "100", "message" => $error));
	}
	
	public function dieNoAction() {
		$this->jsonDump(array("code" => 400, "errno" => "002", "message" => "action not specified."));
	}
	
	public function exitLoginSuccess($param) {
		$this->jsonDump(array("code" => 200, "access_token" => $param));
	}
	
	public function exitRegisterSuccess($token) {
		$this->jsonDump(array("code" => 200, "access_token" => '"' + $token + '"'));
	}
	
	public function getSalt() {
		return base64_encode($this->tokenHash);
	}
	
	public function getAccessToken($u, $d) {
		$p = password_hash("token:" + $u + $d, PASSWORD_BCRYPT, array("cost" => 7, "salt" => $this->getSalt()));
		return $p;
	}
}