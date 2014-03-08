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

// the following actions allow guests to perform
if ($action == "register"){
	// register a user
	if ($user->isUser()) $app->dieLoginError("You are already logged in.");
	
	try {
		$tok = $user->registerNewUser($app->getPOST("username"), $app->getPOST("password"), $app->getPOST("retype"), $app->getPOST("email"));
		$app->exitRegisterSuccess($tok);
	} catch (RegisterException $e) {
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
	// dump the security questions to json
	
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
		$profile = $user->getProfile($targetUser)->getArray();
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
		$profile_fields_array = explode("|", $profile_fields);
		foreach ($profile_fields_array as $key){
			if (isset($_POST[$key])){
				$v = $app->getPOST($key);
				if ($key == "birthday"){
					$f = explode("-", $v);	//YYYY-mm-dd
					if (!checkdate($f[1], $f[2], $f[0]))
						throw new UserProfileException("The birthday format is not valid.");
				}
				$myProfile->updateField($key, $v);
			} else {
				$myProfile->removeField($key);
			}
		}
		$user->setProfile($user->logged_in_user, $myProfile);
		$app->exitWithArrayData($myProfile->getArray());
	} catch (UserProfileException $e) {
		$app->dieUserException($e);
	}
	
} else if ($action == "update_photo") {
	//TODO: finish the stub
	
} else if ($action == "upload_photo") {
	//TODO: finish the stub
	
	require "include/class.FileUploadHandler.php";
	$upload_handler = new UploadHandler();
	
} else if ($action == "delete_photo") {
	//TODO: finish the stub
	
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
	
} else {
	echo "Debug page<br>";
	echo "Logged in: ". $user->isUser() . "<br>";
}