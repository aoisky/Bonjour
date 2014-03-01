<?php
/**
 * Unit.Core.php
 * 
 * The core file for unit test suites.
 */

error_reporting(E_ALL);
ini_set("display_errors", "1");

function unit_title($t) {
	echo "<h1>" . $t . "</h1>";
}

function unit_success($str) {
	echo $str . "<br />";
}

function unit_error($str) {
	echo "<b>" . $str . "</b><br />";
}
