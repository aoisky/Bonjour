<?php
/**
 * unit.Database.php
 *
 * Unit test the Database class.
 * @author	Xiangyu Bu
 * @date	Feb 05, 2014
 */

require_once "unit.Core.php";
unit_title("Unit Test Database Class");

unit_success("Include classes...");
require_once realpath(__DIR__.'/..') . '/config.inc.php';
require_once realpath(__DIR__.'/..') . '/include/class.Database.php';

unit_success("Successful...");

unit_success("Initialize Database object...");
$db = new Database($db_params);

unit_success("Connecting to Database...");
$db->connect();

unit_success("Phase 1: Connection successful.");

$q = $db->selectQuery("SELECT * from users");
$n = $db->getNumOfRecords();

echo $n;
$db->insertQuery("");
$db->deleteQuery("");
$db->updateQuery("");
