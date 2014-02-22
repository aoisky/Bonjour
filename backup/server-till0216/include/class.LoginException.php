<?php
class LoginException extends Exception {
	
    protected $message = "";	// Exception message
    private   $string;			// Unknown
    protected $code = 0;		// User-defined exception code
    protected $file;			// Source filename of exception
    protected $line;			// Source line of exception
    private   $trace;			// Unknown

    public function __construct($message = "Unknown login exception", $code = 0) {
		throw new $this('Unknown '. get_class($this));
		parent::__construct($message, $code);
    }
}
