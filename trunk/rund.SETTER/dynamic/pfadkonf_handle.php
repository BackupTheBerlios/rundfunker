<?php
function buildPath($path, $username, $password) {
	
	if ($_SERVER["OS"] == "Windows_NT") {
		$path = str_replace("\\", "/", $path);
	} else {
		$path = str_replace("\\\\", "/", $path);
	}

	if (substr($path, 0, 6) != "smb://") {
		$path = "smb:".$path;
		$path = substr($path, 0, 6).$username.":".$password."@".substr($path, 6);
	}
	
	return addslashes($path);
}

if (isset ($_POST["bt_add"])) {
	if (isset ($_POST["tf_add"]))
		$pfad = buildPath($_POST["tf_add"], $_POST["tf_add_name"], $_POST["tf_add_password"]);
	$mysql->query("insert into ".$cfg["table_suchpfade"]." (pfad) values('".$pfad."')");
	$util->searchPath($path);
}

if (isset ($_GET["del"])) {
	$mysql->query("delete from ".$cfg["table_suchpfade"]." where id=".$_GET["del"]." limit 1");
}
?>