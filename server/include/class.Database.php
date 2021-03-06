<?php
/**
 * Database model
 *
 * @author	Xiangyu Bu
 * @date	Fed 20, 2014
 */

class Database {
	private $DEBUG_MODE = true;
	
	private $db = null;
	private $dbParams;
	private $num_of_rows = -1;
	
	public function __construct($p){
		$this->dbParams = $p;
	}
	
	public function __destruct(){
		if ($this->db) $this->db->close();
		$this->db = null;
	}
	
	public function connect(){
		if ($this->db) return;
		$this->db = new mysqli($this->dbParams["host"], $this->dbParams["username"], $this->dbParams["password"], $this->dbParams["database"]);
		if (!$this->db)
			//die("Database connection error.");
			die("Connect failed: %s\n" + mysqli_connect_error());
	}
	
	public function escapeStr($str){
		return $this->db->real_escape_string($str);
	}
	
	function selectQuery($sql){
		//echo $sql;
		$q = $this->db->query($sql);
		$a = null;
		if ($q){
			$this->num_of_rows = $q->num_rows;
			while ($row = $q->fetch_array(MYSQLI_BOTH)){
				$a[] = $row;
			}
			$q->free();
		} else if ($this->DEBUG_MODE) {
			die("Error message from Db: %s\n" . $this->db->error);
		} else {
			$this->core->dieDbError();
		}
		return $a;
	}
	
	function getNumOfRecords(){
		$t = $this->num_of_rows;
		$this->num_of_rows = -1;
		return $t;
	}
	
	function insertQuery($sql){
		$q = $this->db->query($sql);
		if ($q){
			//$q->free();
			return true;
		} else if ($DEBUG_MODE) {
			die("Error message from Db: %s\n" . $this->db->error);
		} else {
			$this->dieDbError();
		}
		return false;
	}
	
	function deleteQuery($sql){
		$this->insertQuery($sql);
	}
	
	function updateQuery($sql){
		$this->insertQuery($sql);
	}
	
	public function jsonDump($data){
		#$s = json_encode($data, JSON_PRETTY_PRINT);
		$s = json_encode($data);
		header("Content-Type: application/json");
		header("Cache-Control: no-cache, must-revalidate");
		header("Content-Length: " . strlen($s));
		die($s);
	}
	
	public function dieDbError() {
		$this->jsonDump(array("code" => 500, "errno" => "003", "message" => "database query error."));
	}
}
