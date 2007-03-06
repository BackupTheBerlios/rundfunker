<?php

//include_once("Util.php");
//$util = new Util();

function ipArray2IpString($array) {
	return $array[0].".".$array[1].".".$array[2].".".$array[3];
}

if (isset ($_POST["submit"])) {

	$mysql->query("update ".$cfg["table_ipconfig"]." set ip='".ipArray2IpString($_POST["address"])."' where typ='address'");
	$mysql->query("update ".$cfg["table_ipconfig"]." set ip='".ipArray2IpString($_POST["netmask"])."' where typ='netmask'");
	$mysql->query("update ".$cfg["table_ipconfig"]." set ip='".ipArray2IpString($_POST["gateway"])."' where typ='gateway'");
	$mysql->query("update ".$cfg["table_ipconfig"]." set ip='".$_POST["dhcp"]."' where typ='dhcp'");

	$util->setWlanConfig($cfg["eth"], ipArray2IpString($_POST["address"]), ipArray2IpString($_POST["netmask"]), ipArray2IpString($_POST["gateway"]), ipArray2IpString($_POST["gateway"]));
}

//header("Location: http://".$_SERVER['HTTP_HOST']
//                      .dirname($_SERVER['PHP_SELF'])
//                      ."/../?modul=einstellungen");


?>






