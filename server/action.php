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
require_once "include/class.Login.php";

$app = new Core();
if (!isset($_POST["action"])) $app->dieNoAction();

$database = new Database($db_params);

$action = $app->getPOST("action");
$access_token = $app->getPOST("access_token");

$login = new Login($app, $database, $access_token);

if ($action == "login"){
	if ($login->logIn($app->getPOST("username"), $app->getPOST("password"), "")) {
		$app->exitLoginSuccess($login->ret);
	} else $app->dieLoginError($login->error);
} else if ($action == "logout") {
	$login->logOut();
	$app->exitLogout();
} else if ($action == "register") {
	require_once "include/class.Registration.php";
	
	$reg = new Registration($app, $database);
	if ($reg->registerNewUser($app->getPOST("username"), $app->getPOST("password"), $app->getPOST("retype"), $app->getPOST("email")))
		
		$app->exitRegisterSuccess($reg->ret);
	else
		$app->dieRegisterError($reg->error);
} else if ($action == "update_profile") {
	if (!$login->isUser()) $app->dieUnauthorized();
	
	//TODO: finish the stub
	
} else {
	echo "Debug page<br>";
	echo "Logged in: ". $login->isUser() . "<br>";
}