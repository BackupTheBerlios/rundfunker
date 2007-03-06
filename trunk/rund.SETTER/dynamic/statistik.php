<?php
/*
 * Created on 06.10.2005
 *
 * To change the template for this generated file go to
 * Window - Preferences - PHPeclipse - PHP - Code Templates
 */

$time1 = time();

$result1 = $mysql->query("SELECT COUNT(filename) as count FROM " . $cfg["table_song"]);
$result2 = $mysql->query("SELECT COUNT(DISTINCT genre) as genrecount FROM " . $cfg["table_song"]);
$result3 = $mysql->query("SELECT COUNT(DISTINCT artist) as artistcount FROM " . $cfg["table_song"]);
$result4 = $mysql->query("SELECT COUNT(DISTINCT album) as albumcount FROM " . $cfg["table_song"]);
$result5 = $mysql->query("SELECT COUNT(genre) as nogenre FROM " . $cfg["table_song"] . " WHERE ISNULL(genre) OR genre='' OR genre='<undefined>';");
$result6 = $mysql->query("SELECT SUM(songSeconds) as gesamtspielzeit FROM " . $cfg["table_song"]);
$result7 = $mysql->query("SELECT AVG(songSeconds) as durchschnittslaenge FROM rf_song " . $cfg["table_song"]);
$result8 = $mysql->query("SELECT MAX(readDurationMillis) AS maxRead, AVG(readDurationMillis) AS avgRead, MIN(readDurationMillis) AS minRead FROM " . $cfg["table_song"]);
$result9 = $mysql->query("SELECT artist, title, round((pow((secondsPlayed / songSeconds / timesPlayed),2) * timesPlayed),2) AS lieblingsliedfaktor FROM " . $cfg["table_song"] . " HAVING lieblingsliedfaktor > 0 ORDER BY lieblingsliedfaktor DESC LIMIT 10;");
//$result10 = $mysql->query("SELECT readDurationMillis, artist, title, filename FROM " . $cfg["table_song"] . " ORDER BY readDurationMillis DESC LIMIT 10;");
//$result11 = $mysql->query("SELECT COUNT(DISTINCT album) as albumcount FROM " . $cfg["table_song"]);
//$result12 = $mysql->query("SELECT COUNT(DISTINCT album) as albumcount FROM " . $cfg["table_song"]);
//$result13 = $mysql->query("SELECT COUNT(DISTINCT album) as albumcount FROM " . $cfg["table_song"]);
$result14 = $mysql->query("SELECT filename FROM " . $cfg["table_song"] . " WHERE errors like '%watcher%'");


$gesamtspielzeit = round((($result6[0]["gesamtspielzeit"])/3600)/24, 2);


?>

<br> 

<table width="500" cellspacing="0">
	<tr>
		<th>Allgemeines</th>
	</tr>
</table> 

<br>

<table width="500" border="0" cellspacing="0">
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
		<td width="40%">Kein Genre:</td>
		<td width="60%"><?=$result5[0]["nogenre"]?></td>
	</tr>
	<tr>
		<td width="40%">Gesamtspielzeit in Tagen:</td>
		<td width="60%"><?=$gesamtspielzeit?></td>
	</tr>
	<tr>
		<td width="40%">Durchschnittl&auml;nge:</td>
		<td width="60%"><?=$result7[0]["durchschnittslaenge"] . " sec"?></td>
	</tr>
</table>

<br><br>

<table width="500" cellspacing="0">
	<tr>
	<th>Top 10 - Lieder</th>
	</tr>
</table> 

<br>

<table width="500" cellspacing="0" border="0">
	<tr>
	<?
		$i = 1;
		foreach ($result9 as $topsong) {	
	?>
		<tr>
			<td width="5%"><b><?=$i++?></b></td>
			<td width="35%"><?=htmlentities($topsong["artist"])?></td>
			<td width="50%"><?=htmlentities($topsong["title"])?></td>
			<td width="10%"><?=$topsong["lieblingsliedfaktor"]?></td>
		</tr>
	<?}?>
</table> 

<br><br>

<table width="500" cellspacing="0">
	<tr>
		<th>Zugriffe</th>
	</tr>
</table> 

<br>

<table width="500" cellspacing="0" border="0">
	<tr>
		<td width="40%">MaxRead:</td>
		<td width="60%"><?=$result8[0]["maxRead"]?></td>
	</tr>
	<tr>
		<td width="40%">AvgRead:</td>
		<td width="60%"><?=$result8[0]["avgRead"]?></td>
	</tr>
	<tr>
		<td width="40%">MinRead:</td>
		<td width="60%"><?=$result8[0]["minRead"]?></td>
	</tr>
	<tr>
		<td width="40%">Timeout:</td>
		<td width="60%"><?=$result14[0]["filename"]?></td>
	</tr>
</table> 

<br><br>

<?
$time2 = time();
echo "<br><font class=\"smalloutput\">Die Abfrage dauerte " . ($time2 - $time1) . " Sekunden</font>";
?>