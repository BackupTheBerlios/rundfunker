<?php

/*
 * Created on 05.10.2005
 *
 * To change the template for this generated file go to
 * Window - Preferences - PHPeclipse - PHP - Code Templates
 */

class MySQL extends Database {

	private $link;
	private $db;

	function __construct() {
		global $cfg;
		$this->link = mysql_connect($cfg["db_host"], $cfg["db_user"], $cfg["db_password"]) or die("Keine Verbindung möglich: ".mysql_error());
		$this->db = $cfg["db_database"];
		mysql_select_db($this->db) or die("Auswahl der Datenbank fehlgeschlagen");
	}

	public function query($query) {
		$temparray = array();
		$result = mysql_query ($query)
		or die ("\nSQL-Fehler");
		while($row = mysql_fetch_assoc ($result))
		{
			array_push($temparray,$row);
		}
		return $temparray;
	}
}
?>

