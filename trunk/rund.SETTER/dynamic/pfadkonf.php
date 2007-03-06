<?php
/*
 * Created on 06.10.2005
 *
 * To change the template for this generated file go to
 * Window - Preferences - PHPeclipse - PHP - Code Templates
 */
 
 include_once("dynamic/pfadkonf_handle.php");
 
 $result = $mysql->query("select id,pfad from " . $cfg["table_suchpfade"] . " order by id");
 $count = 1;
 
?>

<br>

<table width="500" cellspacing="0">
	<tr>
		<th>Aktuelle Konfiguration</th>
	</tr>
</table> 

<br><br>

<table width="500" border="0" cellspacing="0">
	<tr>
		<th width="20" align="center">#</th>
		<th align="left">Pfad</th>
		<th>Optionen</th>
	</tr>
	<? foreach($result AS $entry) { ?>
	<tr bgcolor="#<?=(($i++)%2 == 0)?"FFFFFF":"EEEEEE"?>">
		<td width="20" align="center"><?=$count++;?></td>
		<td><?=$entry["pfad"]?></td>
		<td width="40" align="center"><a href="javascript:confirm_pfad_delete('<?=$_SERVER["PHP_SELF"]?>?modul=pfadkonf&del=<?=$entry["id"]?>')"><img src="shared/images/bt_del.png" border="0"></a></td>
	</tr>
	<? } ?>
</table>

<br><br><br>

<table width="500" cellspacing="0">
	<tr>
	<th>Pfad hinzuf&uuml;gen</th>
	</tr>
</table> 

<br>

<form method="post" action="<?=$_SERVER["PHP_SELF"]?>?modul=pfadkonf" enctype="text/plain">
	<table width="500" cellspacing="0" border="0">
		<tr>
			<td>Name:</td>
			<td><input type="text" name="tf_add_name" style="width: 135px;"></td>
			<td align="right">Passwort:</td>
			<td align="right"><input type="text" name="tf_add_password" style="width: 135px;"></td>
			<td><input type="submit" name="bt_add" value="hinzuf&uuml;gen" style="width: 131px;"></td>
		</tr>
		<tr>
			<td>Pfad:</td>
			<td colspan="4"><input type="text" name="tf_add" style="width: 460px;"></td>
		</tr>
	</table>
</form>

<br><br><br>