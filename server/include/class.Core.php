<?php
/**
 * class.Core.php
 *
 * The system core
 * 
 * Handles Json input and output
 * 
 * @author	Xiangyu Bu
 * @date	Feb 20, 2014
 */
 
class Core {
	
	// username has at most 20 chars
	public $username_pattern = "/^[-*_a-z0-9A-Z.]{4,20}$/";
	
	public $security_questions = array(
		"sec_q01" => "What was your childhood nickname?",
		"sec_q02" => "In what city or town was your first job?",
		"sec_q03" => "Where were you when you had your first kiss?",
		"sec_q04" => "What is the last name of your first boss?",
		"sec_q05" => "How much was the first salary you received?"
	);
	
	protected $tokenHash = "ThisBonjOURInstanceNeedsSalt";
	
	public function __construct() {
	}
	
	public function __destruct() {
		$this->json = null;
	}
	
	public function jsonDump($data){
		#$s = json_encode($data, JSON_PRETTY_PRINT);
		$s = json_encode($data);
		header("Content-Type: application/json");
		header("Cache-Control: no-cache, must-revalidate");
		header("Content-Length: " . strlen($s));
		die($s);
	}
	
	public function returnJsonError($msg) {
		$this->jsonDump(array("code" => 400, "errno" => "001", "message" => $msg));
	}
	
	public function dieNoAction() {
		$this->jsonDump(array("code" => 400, "errno" => "002", "message" => "action not specified."));
	}
	
	public function dieRegisterError($error) {
		$this->jsonDump(array("code" => 400, "errno" => "100", "message" => $error));
	}
	
	public function dieLoginError($error) {
		$this->jsonDump(array("code" => 400, "errno" => "200", "message" => $error));
	}
	
	public function dieUserException(Exception $e, $extra = null){
		$a = array(
			"code" => $e->getCode(),
			"errno" => $e->getErrno(),
			"message" => $e->getMessage()
		);
		if ($extra != null)
			$a = $a + $extra;
		$this->jsonDump($a);
	}
	
	public function dieUnauthorized() {
		$this->jsonDump(array("code" => 400, "errno" => "201", "message" => "You are unauthorized for this operation."));
	}
	
	public function exitLoginSuccess($param, $extra = null) {
		$a = array("code" => 200, "access_token" => $param);
		if ($extra != null)
			$a = $a + $extra;
		$this->jsonDump($a);
	}
	
	public function exitRegisterSuccess($token) {
		$this->jsonDump(array("code" => 200, "user" => $token));
	}
	
	public function exitLogout(){
		$this->jsonDump(array("code" => 200, "access_token" => "", "message" => "successfully logged out."));
	}
	
	public function exitChangePassword(){
		$this->jsonDump(array("code" => 200, "message" => "Password successfully changed."));
	}
	
	public function exitChangeSecQA(){
		$this->jsonDump(array("code" => 200, "message" => "Security credentials successfully changed."));
	}
	
	public function exitWithArrayData($a){
		$this->jsonDump(array("code" => 200, "data" => $a));
	}
	
	/**
	 * deprecated.
	 */
	public function getSalt() {
		return base64_encode($this->tokenHash);
	}
	
	public function getOriginalToken($u, $d) {
		return base64_encode("token:" . $u . $d);
	}
	
	public function getAccessToken($u, $d) {
		$t = $this->getOriginalToken($u, $d);
		$p = password_hash($t, PASSWORD_DEFAULT);
		return $p;
	}
	
	public function getPOST($ind) {
		if (!isset($_POST[$ind])) return null;
		
		$str = urldecode($_POST[$ind]);
		return stripslashes($str);
	}
	
	// A user password is defined to be 
	// a string of length 6 to 32, allowing special characters.
	public function isValidPassword($str){
		$len = strlen($str);
		if ($len >= 6 and $len <= 33) return true;
		return false;
	}
	
	public function isValidUserName($str){
		$len = strlen($str);
		if ($len >= 4 and $len <= 20 and preg_match($this->username_pattern, $str)) return true;
		return false;
	}
	
	public function isValidEmail($str){
		return filter_var($str, FILTER_VALIDATE_EMAIL);
	}
	
	public function isValidDate($str){
		$f = explode("-", $str);	//YYYY-mm-dd
		return checkdate($f[1], $f[2], $f[0]); // !
	}
	
	public function sendEmail($address, $subject = "", $content = ""){
		// removed code
		
	}
	
	public function getRandomStr($len) {
		$chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		
		return substr(str_shuffle(substr(str_shuffle($chars), 0, $len / 2 + 1) . substr(str_shuffle($chars), 0, $len / 2 + 1)), 0, $len);
	}
	
	public function filterHtml($str){
		// removed htmlencode code
		
		return $str;
	}
}