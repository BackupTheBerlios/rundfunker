<?php
/*
 * Created on 06.10.2005
 *
 * To change the template for this generated file go to
 * Window - Preferences - PHPeclipse - PHP - Code Templates
 */
 
 $menue = $mysql->query("select * from " . $cfg["table_menue"] . " order by menu_order");
 ?>
 <table width="100%" cellpadding="0" cellspacing="0">
 <?
 foreach($menue AS $menup)
 {
 	?>
 	<tr>
 	  <td><a href="?modul=<?=$menup["modul"]?>">
 	    <?=$menup["menuepunkt"]?>
 	  </a></td>
	<td width="3" height="24"><img src="shared/images/pfeil.gif" width="3" height="5"></td>
	</tr>
 	<?
 }
 
?>
</table>
