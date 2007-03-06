<?php
/*
 * Created on 05.10.2005
 *
 * To change the template for this generated file go to
 * Window - Preferences - PHPeclipse - PHP - Code Templates
 */
 
//Import
require_once "admin/config.inc.php";
function __autoload($class_name) {
    require_once "dynamic/" . $class_name . '.php';
}

// Lade den MySQL Treiber
$mysql = Database::databaseFactory('MySQL');
print_r($mysql->query("select * from suchpfade"));
 
?>
