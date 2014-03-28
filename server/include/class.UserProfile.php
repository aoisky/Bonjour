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
		
	public function __construct($str){
		if ($str != "") {
			$this->data = json_decode($str, true);
		}
	}
	
	public function __destruct(){
	}
	
	public function hasField($f){
		return array_key_exists($f, $this->data);
	}
	
	public function addField($f, $v){
		$this->data[$f] = $v;
	}
	
	public function updateField($f, $v){
		$this->addField($f, $v);
	}
	
	public function removeField($f){
		if (!$this->hasField($f)) return;
		if (empty($this->data)) return;
		unset($this->data[array_search($f, $this->data)]);
	}
	
	public function toJsonStr(){
		return json_encode($this->data);
	}
	
	public function getArray(){
		return $this->data;
	}
}