<?php
/**
 * Database Operations
 *
 * @author	Xiangyu Bu
 * @date	Fed 05, 2014
 */

class Database {
	private $db = null;
	private $dbParams;
	
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
		if (!$this->db) die("Connect failed: %s\n" + mysqli_connect_error());
	}
	
	public function escapeStr($str){
		return $this->db->real_escape_string($str);
	}
	
	function selectQuery($sql){
		$q = $this->db->query($sql);
		$a = null;
		if ($q){
			while ($row = $q->fetch_array(MYSQLI_BOTH)){
				$a[] = $row;
			}
			$q->free();
		}	
		return $a;
	}
	
	function insertQuery($sql){
		$q = $this->db->query($sql);
		if ($q){
			$q->free();
			return true;
		}
		return false;
	}
	
	function deleteQuery($sql){
		insertQuery($sql);
	}
	
	function updateQuery($sql){
		insertQuery($sql);
	}
	
}
