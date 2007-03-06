<?
//exec('env LC_ALL=C /sbin/ifconfig ' . $cfg["eth"], $array, $i);
//$fl_array = preg_grep("/[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}/", $array);
//$ipzeile = current($fl_array);

//$ipzeile = "inet addr:192.168.0.8 Bcast:192.168.0.255 Mask:255.255.255.0";
//$ipzeile = "15. April 2003";

//$address = preg_replace('/.*addr:([0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}).*/', '${1}', $ipzeile);
$address = $util->getAddress($cfg["eth"]);
?>

<br>

<APPLET archive="RundPlayerApplet.jar" WIDTH="500" HEIGHT="292" ALIGN="MIDDLE" CODE="rundplayerapplet.RundfunkerApplet.class">
<param name="IP" value="<?=$address?>">
</applet>

<br>