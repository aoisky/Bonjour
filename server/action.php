<?php
/**
 * action.php
 *
 * The hub for all actions of the app
 *
 * @author	Xiangyu Bu
 * @date	Feb 07, 2014
 */

require_once "config.inc.php";
require_once "include/class.User.php";

$app = new Core();
if (!isset($_POST["action"])) $app->dieNoAction();

$database = new Database($db_params);
$action = $app->getPOST("action");
$access_token = $app->getPOST("access_token");
$user = new User($app, $database, $access_token);

// file_put_contents("log.txt", var_export($_POST, true));

// the following actions allow guests to perform
if ($action == "register"){
	// register a user
	if ($user->isUser()) $app->dieLoginError("You are already logged in.");
	
	try {
		$myProfile = User::$DEFAULT_USER_PROFILE;
		
		foreach ($myProfile as $key => $val){
			if (isset($_POST[$key])){
				$v = $app->getPOST($key);
				if ($key == "avatar") {
					$v = $_POST["avatar"];
					$avatar_url = $user->saveUserAvatar(base64_decode($v));
					$myProfile["avatar"] = $avatar_url;
				} else if ($key == "birthday" and !$app->isValidDate($v)) {
 						throw new UserProfileException("The birthday format is not valid.");
				} else if ($key == "age" and !is_numeric($v)) {
						throw new UserProfileException("Age should be a number.");
				} else $myProfile[$key] = $app->filterHtml($v);
			}
		}
		
		$user_info = $user->createUser($app->getPOST("username"), $app->getPOST("password"), $app->getPOST("retype"), $app->getPOST("email"), $myProfile);
		
		$app->exitRegisterSuccess($user_info);
	} catch (Exception $e) {
		$app->dieUserException($e);
	}
	
} elseif ($action == "login"){
	// user log in operation
	try {
		$tok = $user->logIn($app->getPOST("username"), $app->getPOST("password"), "");
		$app->exitLoginSuccess($tok);
	} catch (LoginException $e) {
		$app->dieUserException($e);
	}
	
} elseif ($action == "logout") {
	// user log out operation
	// logout operation will ignore guest requests internally
	
	$user->logOut();
	$app->exitLogout();
} elseif ($action == "getSecurityQuestions") {
	
	$app->exitWithArrayData($app->security_questions);
} elseif ($action == "forgotPassword") {
	$username = $app->getPOST("username");
	try {
		$q_string = $user->getSecurityQuestionOfUser($username);
		$app->exitWithArrayData($q_string);
	} catch (EmptyFieldException $e) {
		//TODO: generate a new password when
		//		the user does not have security question
		//		and send it to the user's email address
		
		$app->dieUserException($e, array("proceed_token" => password_hash($username . ":reset_pass", PASSWORD_DEFAULT)));
	} catch (ResetPassException $e) {
		$app->dieUserException($e);
	}
} elseif ($action == "setSecurityAnswer") {
	$username = $app->getPOST("username");
	$question = $app->getPOST("question");
	$answer = $app->getPOST("answer");
	try {
		if (!array_key_exists($question, $app->security_questions))
			throw new Exception("Please choose one security question from the list");
		$user->setSecurityCreds($username, $question, $answer);
		$app->exitChangeSecQA();
	} catch (ResetPassException $e) {
		$app->dieUserException($e);
	}
} elseif ($action == "verifySecurityAnswer") {
	
	$username = $app->getPOST("username");
	$answer = $app->getPOST("answer");
	try {
		$tok = $user->verifySecurityAnswer($username, $answer);
		$app->exitLoginSuccess($tok, array("proceed_token" => password_hash($username . ":correct_ans", PASSWORD_DEFAULT)));
	} catch (ResetPassException $e) {
		$app->dieUserException($e);
	}
} elseif ($action == "changeForgottenPassword") {
	
	$username = $app->getPOST("username");
	$newpassword = $app->getPOST("newpassword");
	$renewpassword = $app->getPOST("retype");
	$proceed_token = $app->getPOST("proceed_token");
	try {
		$user->changePassword($username, "", $newpassword, $renewpassword, $proceed_token);
		$app->sendEmail("@", "subject", "content");
		$app->exitChangePassword();
	} catch (ResetPassException $e) {
		$app->dieUserException($e);
	}
} elseif ($action == "genNewPassword") {
	
	$username = $app->getPOST("username");
	$proceed_token = $app->getPOST("proceed_token");
	try {
		$newpass = $user->generateNewPassword($username, $proceed_token);
		$app->sendEmail("@", "subject", "content" . $newpass);
		$app->exitChangePassword();
	} catch (ResetPassException $e) {
		$app->dieUserException($e);
	}
} else if ($action == "getProfile") {
	//TODO: finish the stub
	try {
		$targetUser = $app->getPOST("targetUser");
		$profile = $user->getProfile($targetUser);
		$app->exitWithArrayData($profile);
	} catch (UserProfileException $e) {
		$app->dieUserException($e);
	}
}

if (!$user->isUser()) $app->dieUnauthorized();
	
if ($action == "updateProfile") {
	// update user profile
	
	try {
		$myProfile = $user->getProfile($user->logged_in_user);
		
		foreach ($myProfile as $key => $val){
			if (isset($_POST[$key])){
				$v = $app->getPOST($key);
				if ($key == "avatar") {
					$v = $_POST["avatar"];
					if ($v != $val) {
						$avatar_url = $user->saveUserAvatar(base64_decode($v));
						$myProfile["avatar"] = $avatar_url;
					}
				} else if ($key == "birthday" and !$app->isValidDate($v)) {
 						throw new UserProfileException("The birthday format is not valid.");
				} else if ($key == "age" and !is_numeric($v)) {
						throw new UserProfileException("Age should be a number.");
				} else $myProfile[$key] = $app->filterHtml($v);
			}
		}
		
		$user->setProfile($user->logged_in_user, $myProfile);
		$app->exitWithArrayData($myProfile);
	} catch (UserException $e) {
		$app->dieUserException($e);
	}
	
} else if ($action == "change_password") {
	
	try {
		
		$uname = $app->getPOST("username");
		$uoldpass = $app->getPOST("oldpassword");
		$unewpass = $app->getPOST("newpassword");
		$uretype = $app->getPOST("retype");
		$user->changePassword($uname, $uoldpass, $unewpass, $uretype);
		$app->sendEmail("@", "subject", "content");
		$app->exitChangePassword();
		
	} catch (ResetPassException $e) {
		$app->dieUserException($e);
	}

} else if ($action == "match") {
	try {
		
		$data = $app->getPOST("data");
		$data = base64_decode($data);
		
		$json = json_decode($data, true);
		$json_errno = json_last_error();
		
		if ($json_errno){
			$err_msg = "";
			switch ($json_errno){
				case JSON_ERROR_DEPTH:
					$err_msg = 'Maximum stack depth exceeded';
					break;
				case JSON_ERROR_STATE_MISMATCH:
					$err_msg = 'Underflow or the modes mismatch';
					break;
				case JSON_ERROR_CTRL_CHAR:
					$err_msg = 'Unexpected control character found';
					break;
				case JSON_ERROR_SYNTAX:
					$err_msg = 'Syntax error, malformed JSON';
					break;
				case JSON_ERROR_UTF8:
					$err_msg = 'Malformed UTF-8 characters, possibly incorrectly encoded';
					break;
				default:
					$err_msg = 'Unknown error';
					break;
			}
			$app->returnJsonError($err_msg);
		}
	    
		if ($json == NULL)
			$app->jsonDump(array("code" => 400, "errno" => "601", "message" => "Cannot parse object."));
		
		if (!array_key_exists("desiredRange", $json) or !array_key_exists("mLatitude", $json) or !array_key_exists("mLongitude", $json))
			$app->jsonDump(array("code" => 400, "errno" => "600", "message" => "Range or Longitude or Latitude not specified."));
		
		$match_list = $user->getMatchings($json);
		
		if (count($match_list) == 0) {
			$app->jsonDump(array("code" => 400, "errno" => "700", "message" => "No match found."));
		}
		
		$app->jsonDump(array("code" => 200, "matchings" => $match_list));
		
	} catch (Exception $e){
	}
} else {
	echo "Debug page<br>";
	echo "Logged in: ". $user->isUser() . "<br>";
}