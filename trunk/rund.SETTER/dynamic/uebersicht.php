<?php
/*
 * Created on 06.10.2005
 *
 * To change the template for this generated file go to
 * Window - Preferences - PHPeclipse - PHP - Code Templates
 */
error_reporting(E_ALL);


//*************** Netzwerk ****************

	$address = $util->getAddress($cfg["eth"]);
	$netmask = $util->getNetmask($cfg["eth"]);
	$gateway = $util->getGateway();
	
	
//***************** Statistik *************
	
	$result1 = $mysql->query("SELECT COUNT(filename) as count FROM " . $cfg["table_song"]);
	$result2 = $mysql->query("SELECT COUNT(DISTINCT genre) as genrecount FROM " . $cfg["table_song"]);
	$result3 = $mysql->query("SELECT COUNT(DISTINCT artist) as artistcount FROM " . $cfg["table_song"]);
	$result4 = $mysql->query("SELECT COUNT(DISTINCT album) as albumcount FROM " . $cfg["table_song"]);
	$result6 = $mysql->query("SELECT SUM(songSeconds) as gesamtspielzeit FROM " . $cfg["table_song"]);
	$gesamtspielzeit = round((($result6[0]["gesamtspielzeit"])/3600)/24, 2);

?>

<br>

<table width="500" cellspacing="0">
	<tr>
		<th>Netzwerk Konfiguration</th>
	</tr>
</table>

<br>

<table width="500" cellspacing="0" border="0">
	<tr>
		<td width="40%">Adresse:</td>
		<td width="60%"><?=$address?></td>
	</tr>
	<tr>
		<td width="40%">Netmask:</td>
		<td width="60%"><?=$netmask?></td>
	</tr>
	<tr>
		<td width="40%">Gateway:</td>
		<td width="60%"><?=$gateway?></td>
	</tr>
</table>

<br><br>
 
<table width="500" cellspacing="0">
	<tr>
		<th>Statistik</th>
	</tr>
</table>

<br>

<table width="500" cellspacing="0" border="0">
	<tr>
		<td width="40%">Lieder insgesamt:</td>
		<td width="60%"><?=$result1[0]["count"]?></td>
	</tr>
	<tr>
		<td width="40%">Genres:</td>
		<td width="60%"><?=$result2[0]["genrecount"]?></td>
	</tr>
	<tr>
		<td width="40%">Artists:</td>
		<td width="60%"><?=$result3[0]["artistcount"]?></td>
	</tr>
	<tr>
		<td width="40%">Alben:</td>
		<td width="60%"><?=$result4[0]["albumcount"]?></td>
	</tr>
	<tr>
		<td width="40%">Gesamtspielzeit in Tagen:</td>
		<td width="60%"><?=$gesamtspielzeit?></td>
	</tr>
</table>

<br><br>