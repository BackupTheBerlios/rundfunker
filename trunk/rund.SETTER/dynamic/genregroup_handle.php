<?php
/*
 * Created on 01.12.2005
 *
 * To change the template for this generated file go to
 * Window - Preferences - PHPeclipse - PHP - Code Templates
 */
 
  if(isset($_POST["bt_add"]))
 {
 	if(isset($_POST["tf_genrename"]))
 	$mysql->query("insert into " . $cfg["table_genres"] . " (genre) values('" . trim($_POST["tf_genrename"]) . "')");
 }
 
  if(isset($_GET["del"]))
 {
 	$mysql->query("delete from " . $cfg["table_genres"] . " where id=" . $_GET["del"] . " limit 1");
 	$mysql->query("delete from " . $cfg["table_genres_sgenres"] . " where genre=" . $_GET["del"]);
 }
 
?>
