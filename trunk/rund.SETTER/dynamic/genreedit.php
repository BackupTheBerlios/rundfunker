<?PHP
$time1 = time();

$httpQuery = $_SERVER['PHP_SELF'] . "?modul=genreedit&genre=" . $_GET["genre"];

include_once("dynamic/genreedit_handle.php");

$eintraege = 10;
$page = $_GET["page"];
if(!isset($page))
{
$page = 1;
}


$user_genre = $mysql->query("SELECT genre FROM " . $cfg["table_genres"] . " WHERE id = " . $_GET["genre"]);

$query = "SELECT trim(t1.genre) as genre, t2.genre as usergenreid FROM "
	. $cfg["table_song"] . " as t1 LEFT JOIN " . $cfg["table_genres_sgenres"]
	. " as t2 ON t1.genre = t2.sgenre AND t2.genre = "
	. $_GET["genre"] . " OR t1.genre is null AND t2.sgenre = '[null]' AND t2.genre = "
	. $_GET["genre"] . " GROUP BY trim(t1.genre) order by trim(t1.genre) limit " . (($page - 1) * $eintraege) . "," . $eintraege;

//echo $query;

$genres = $mysql->query($query);
$genrecount = $mysql->query("SELECT count(distinct genre) as count from " . $cfg["table_song"]);

?>
<br>

<table width="500" cellspacing="0">
<tr>
<th>Genre bearbeiten - <?=$user_genre[0][genre]?></th>
</tr>
</table>

<br>

<table width="500" cellspacing="0" border="0">
  <tr>
    <th align="left">Genre</th>
    <th width="70">Optionen</th>
  </tr>
  <? foreach($genres as $genre) { ?>
  <tr bgcolor="#<?=(($i++)%2 == 0)?"FFFFFF":"EEEEEE"?>">
    <td height="15">
    	<?
    	$temp_genre = ($genre[genre] == "")?"unknown":$genre[genre];
    	if($genre[usergenreid] != $_GET["genre"])
    	{
    		echo "<font color=\"#797d80\">" . $temp_genre . "</font>";
    	} else {
    		echo "<font color=\"#000000\">" . $temp_genre . "</font>";
    	}
    	?>
    </th>
	<td width="70" align="center">
	<?
	if($genre[usergenreid] != $_GET["genre"])
	{
		echo "<a href=\"" . $httpQuery
		. "&page=" . $page . "&register=" . urlencode(($genre[genre] == "")?"[null]":$genre[genre])
		. "\">anmelden</a>";
	} else {
		echo "<a href=\"" . $httpQuery
		. "&page=" . $page . "&clear=" . urlencode(($genre[genre] == "")?"[null]":$genre[genre])
		. "\"><img src=\"shared/images/bt_del.png\" border=\"0\"></a>";
	}
	?>
	</td>
  </tr>
  <? } ?>
</table>

<br>

<table width="500" cellspacing="0" border="0">
  <tr>
  	<td align="center"><? $util->pager($httpQuery, $eintraege, $genrecount[0][count] + 1, $page); ?></td>
  </tr>
</table>

<?
 $time2 = time();
 
 echo "<br><font class=\"smalloutput\">Die Abfrage dauerte " . ($time2 - $time1) . " Sekunden</font>";
?>