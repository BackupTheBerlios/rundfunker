<?php

if(isset($_GET["register"]))
{
		$mysql->query("INSERT INTO " . $cfg["table_genres_sgenres"]
		. " VALUES(" . $_GET["genre"] . ",'" . urldecode($_GET["register"]) . "')");
//	echo "REGISTER: " . urldecode($_GET["register"]);
}

if(isset($_GET["clear"]))
{
	$mysql->query("DELETE FROM " . $cfg["table_genres_sgenres"]
	. " WHERE genre=" . $_GET["genre"] . " AND sgenre='"
	. urldecode($_GET["clear"]) . "' LIMIT 1");
//	echo "CLEAR: " . urldecode($_GET["clear"]);
}

?>
