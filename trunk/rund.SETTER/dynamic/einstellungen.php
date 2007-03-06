<?php
/*
 * Created on 06.10.2005
 *
 * To change the template for this generated file go to
 * Window - Preferences - PHPeclipse - PHP - Code Templates
 */

include_once("dynamic/einstellungen_handle.php");

$result = $mysql->query("select ip from " . $cfg["table_ipconfig"]);
//$address = explode(".",$result[0]["ip"]);
//$netmask = explode(".",$result[1]["ip"]);
//$gateway = explode(".",$result[4]["ip"]);
$address = explode(".",$util->getAddress($cfg["eth"]));
$netmask = explode(".",$util->getNetmask($cfg["eth"]));
$gateway = explode(".",$util->getGateway());

//dirname($_SERVER["PHP_SELF"])/dynamic/einstellungen_handle.php
?>

<br> 

<table width="500" cellspacing="0">
	<tr>
		<th>Netzwerk Konfiguration</th>
	</tr>
</table> 

<form method="post" action="<?=$_SERVER["PHP_SELF"]?>?modul=einstellungen" enctype="text/plain">
	<table width="500" border="0" cellspacing="0">
		<tr>
			<td colspan="2">&nbsp;</td>
		</tr>
		<tr>
			<td width="120">Adresse:</td>
			<td>
				<input name="address[0]" value="<?=$address[0]?>" type="text" size="3" maxlength="3" class="ip">
				&nbsp;.
				<input name="address[1]" value="<?=$address[1]?>" type="text" size="3" maxlength="3" class="ip">
				&nbsp;.
				<input name="address[2]" value="<?=$address[2]?>" type="text" size="3" maxlength="3" class="ip">
				&nbsp;.
				<input name="address[3]" value="<?=$address[3]?>" type="text" size="3" maxlength="3" class="ip">
			</td>
		</tr>
		<tr>
			<td width="120">Netmask:</td>
			<td>
				<input name="netmask[0]" value="<?=$netmask[0]?>" type="text" size="3" maxlength="3" class="ip">
				&nbsp;.
				<input name="netmask[1]" value="<?=$netmask[1]?>" type="text" size="3" maxlength="3" class="ip">
				&nbsp;.
				<input name="netmask[2]" value="<?=$netmask[2]?>" type="text" size="3" maxlength="3" class="ip">
				&nbsp;.
				<input name="netmask[3]" value="<?=$netmask[3]?>" type="text" size="3" maxlength="3" class="ip">
			</td>
		</tr>
		<tr>
			<td width="120">Gateway:</td>
			<td>
				<input name="gateway[0]" value="<?=$gateway[0]?>" type="text" size="3" maxlength="3" class="ip">
				&nbsp;.
				<input name="gateway[1]" value="<?=$gateway[1]?>" type="text" size="3" maxlength="3" class="ip">
				&nbsp;.
				<input name="gateway[2]" value="<?=$gateway[2]?>" type="text" size="3" maxlength="3" class="ip">
				&nbsp;.
				<input name="gateway[3]" value="<?=$gateway[3]?>" type="text" size="3" maxlength="3" class="ip">
			</td>
		</tr> 
		<tr height="30">
			<td width="120">DHCP:</td>
			<td> on&nbsp;
				<input type="radio" size="20" name="dhcp" value="1" class="radio" <? if($result[5]["ip"]=='1') echo "checked"; ?>>
				&nbsp; /&nbsp;off&nbsp;
				<input type="radio" size="20" name="dhcp" value="0" class="radio" <? if($result[5]["ip"]=='0') echo "checked"; ?>>
			</td>
		</tr>
		<tr height="30">
			<td width="120">&nbsp;</td>
			<td><input type="submit" value="&uuml;bernehmen" name="submit" class="button"></td>
		</tr>
	</table>
</form>

<br><br>