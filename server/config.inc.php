<?php
/**
 * The configuration file of the program
 */

define("DEBUG_MODE", true);

if (DEBUG_MODE) {
	error_reporting(E_ALL | E_STRICT);
	ini_set('display_errors', '1');
	ini_set('display_startup_errors', true);
	
	// checking for minimum PHP version
	// FIXME: remove this when the server is chosen
	if (version_compare(PHP_VERSION, '5.3.7', '<')) {
		die("PHP < 5.3.7 !");
	} else if (version_compare(PHP_VERSION, '5.5.0', '<')) {
		// For PHP < 5.5, include this library for password hashing functions
		require_once "include/lib.password.php";
	}
}

$APP_URL = "http://127.0.0.1/bonjour/";

$db_params = array(
	"host" => "localhost", 
	"username" => "bonjour_demo", 
	"password" => "demo", 
	"database" => "bonjour_demo",
);

require_once "include/class.Database.php";
require_once "include/class.Core.php";
