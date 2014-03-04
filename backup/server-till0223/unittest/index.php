<?php
/**
 * index.php
 *
 * The index page of unittest utils.
 *
 * @author	Xiangyu Bu
 * @date	Fed 13, 2014
 */

$files = scandir (".");

print "<h1>Bonjour Unittest Suite</h1>\n";

print "<ul>\n";

foreach ($files as $f) {
	if ($f != "." and $f != ".." and $f != "index.php")
		print "<li><a href=\"" . $f . "\">" . $f . "</a></li>\n";
}

print "</ul>\n";
