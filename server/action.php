<?php
/**
 * action.php
 *
 * The hub for all actions of the app
 *
 * @author	Xiangyu Bu
 * @date	Feb 06, 2014
 */

require_once "config.inc.php";
require_once "include/class.Login.php";

$app = new Core();
$database = new Database($db_params);

$myFile = "testFile.txt";
$fh = fopen($myFile, 'a') or die("can't open file");
fwrite($fh, "POST:\n");
fwrite($fh, implode("\n", $_POST));
fclose($fh);

if (!isset($_POST["action"])) $app->dieNoAction();

$action = $app->getPOST("action");
$access_token = $app->getPOST("access_token");

$isLoggedInUser = false;

if ($access_token != ""){
	$login = new Login($app, $database);
	$isLoggedInUser = $login->verifyToken($app->getPOST("username"), $access_token);
	
	if ($isLoggedInUser) echo "Verified.";
}

if ($action == "login"){
	
	$login = new Login($app, $database);
	if ($login->logIn($app->getPOST("username"), $app->getPOST("password"), "")) {
		$app->exitLoginSuccess($login->ret);
	} else {
		$app->dieLoginError($login->error);
	}
} else if ($action == "logout") {
	require_once "include/class.Login.php";
	
	$login = new Login($app, $database);
	
} else if ($action == "register") {
	require_once "include/class.Registration.php";
	
	$reg = new Registration($app, $database);
	if ($reg->registerNewUser($app->getPOST("username"), $app->getPOST("password"), $app->getPOST("retype"), $app->getPOST("email")))
		
		$app->exitRegisterSuccess($reg->ret);
	else
		$app->dieRegisterError($reg->error);
}
?>