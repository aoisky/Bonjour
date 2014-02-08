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

if ($action == "login"){
	if ($user->logIn($app->getPOST("username"), $app->getPOST("password"), "")) {
		$app->exitLoginSuccess($user->ret);
	} else $app->dieLoginError($user->error);
} else if ($action == "logout") {
	$user->logOut();
	$app->exitLogout();
} else if ($action == "register") {
	if ($user->isUser()) $app->dieLoginError("You are already logged in.");
	
	if ($user->registerNewUser($app->getPOST("username"), $app->getPOST("password"), $app->getPOST("retype"), $app->getPOST("email")))		
		$app->exitRegisterSuccess($user->ret);
	else
		$app->dieRegisterError($user->error);
} else if ($action == "update_profile") {
	if (!$user->isUser()) $app->dieUnauthorized();
	
	//TODO: finish the stub
	
} else {
	echo "Debug page<br>";
	echo "Logged in: ". $user->isUser() . "<br>";
}