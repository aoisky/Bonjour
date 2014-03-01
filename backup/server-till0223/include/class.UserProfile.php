<?php
/**
 * class.UserProfile.php
 *
 * The model for user profile
 * @author	Xiangyu Bu
 * @date	Feb 13, 2014
 */

class UserProfile {
	private $data = array();
	
	public function __construct(){
	}
	
	public function __construct($str){
		if ($str != "") {
			$this->data = json_decode($str);
		}
	}
	
	public function __destruct(){
	}
	
	public function hasField($f){
		return array_key_exists($f, $this->data);
	}
	
	public function addField($f, $v){
		$this->data = $this->data + array($f => $v);
	}
	
	public function updateField($f, $v){
		if ($this->hasField($f)) $this->data[$f] = $v;
		else $this->addField($f, $v);
	}
	
	public function removeField($f){
		if ($this->hasField($f))
			unset($this->data[$f]);
	}
	
	public function toJsonStr(){
		return json_encode($this->data);
	}
}