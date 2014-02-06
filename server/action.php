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

$app = new Core();
$database = new Database($db_params);

if (!isset($_POST["action"])) $app->dieNoAction();

if ($_POST["action"] == "login"){
	require_once "include/class.Login.php";
	
	$login = new Login($app, $database);
	if ($login->logIn($_POST["username"], $_POST["password"], "")) {
		$app->exitLoginSuccess($login->ret);
	} else {
		$app->dieLoginError($login->error);
	}
} else if ($_POST["action"] == "logout") {
	require_once "include/class.Login.php";
	
	$login = new Login($app, $database);
	
} else if ($_POST["action"] == "register") {
	require_once "include/class.Registration.php";
	
	$reg = new Registration($app, $database);
	if ($reg->registerNewUser($_POST["username"], $_POST["password"], $_POST["retype"], $_POST["email"]))
		
		$app->exitRegisterSuccess($reg->ret);
	else
		$app->dieRegisterError($reg->error);
}
?>