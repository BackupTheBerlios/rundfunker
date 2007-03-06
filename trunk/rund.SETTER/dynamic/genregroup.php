<?php
/*
 * Created on 06.10.2005
 *
 * To change the template for this generated file go to
 * Window - Preferences - PHPeclipse - PHP - Code Templates
 */

include_once("dynamic/genregroup_handle.php");

//$genres = $mysql->query("SELECT trim(genre) as genre FROM " . $cfg["table_song"] . " GROUP BY trim(genre) order by trim(genre)");
$owngenres = $mysql->query("SELECT genre, id FROM " . $cfg["table_genres"] . " order by genre");

?>

<br>

<table width="500" cellspacing="0">
	<tr>
	<th>Genre bearbeiten</th>
	</tr>
</table> 

<br><br>

<table width="500" border="0" cellspacing="0">
  <tr>
  	<th width="20" align="center">&nbsp;</th>
  	<th align="left">Genre</th>
  	<th colspan="2" width="70">Optionen</th>
  </tr>
    <? foreach($owngenres as $owngenre) { ?>
    <tr bgcolor="#<?=(($i++)%2 == 0)?"FFFFFF":"EEEEEE"?>">
		 <td width="20" align="center">&nbsp;</td>
         <td><?=$owngenre[genre]?></td>
         <td width="35" align="center"><a href="<?=$_SERVER["PHP_SELF"]?>?modul=genreedit&genre=<?=$owngenre["id"]?>"><img src="shared/images/bt_edt.png" border="0"></a></td>
         <td width="35" align="center"><a href="javascript:confirm_genre_delete('<?=$_SERVER["PHP_SELF"]?>?modul=genregroup&del=<?=$owngenre["id"]?>','<?=$owngenre[genre]?>')"><img src="shared/images/bt_del.png" border="0"></a></td>          
       </tr>
  	<? } ?>
</table>

<br><br><br>

<table width="500" cellspacing="0">
	<tr>
	<th>Genre erstellen</th>
	</tr>
</table> 

<br>

<form name="genresanlegen" action="<?=$_SERVER["PHP_SELF"]?>?modul=genregroup" method="POST">
	
	<table width="500" border="0" cellspacing="0">
		<tr>
			<td>
				<input type="text" name="tf_genrename" style="width: 344px;">
				<input type="submit" name="bt_add" value="anlegen" style="width: 150px;"/>
			</td>
		</tr>
	</table>

</form>

<br><br><br>