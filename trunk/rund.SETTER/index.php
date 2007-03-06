<html>
<head>
<title>Rundfunker</title>
<link href="shared/rundfunker.css" rel="stylesheet" type="text/css">
<script src="shared/rundfunker.js" type="text/javascript"></script>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
</head>

<body>
<?
//Import
require_once "admin/config.inc.php";
function __autoload($class_name) {
    require_once "dynamic/" . $class_name . '.php';
}



$mysql = Database::databaseFactory('MySQL');

if(!isset($_GET['modul']))
$_GET['modul'] = "uebersicht";

$util = new Util();

?>
<table width="100%">
<tr>
<td colspan="3" valign="top"><table width="100%"  border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td width="14" height="36"><img src="shared/images/header_left.gif" width="14" height="36"></td>
    <td background="shared/images/header_center.gif"><a href="index.php"><img src="shared/images/header_logo.gif" alt="Rundfunker" width="178" height="36" border="0"></a></td>
    <td width="14"><img src="shared/images/header_right.gif" width="14" height="36"></td>
  </tr>
</table>
</td>
</tr>
<tr>
  <td width="184" valign="top"><table width="100%" height="100%" border="0" cellpadding="0" cellspacing="0">
    <tr>
      <td width="14"><img src="shared/images/table_head_left.gif" width="14" height="25"></td>
      <td background="shared/images/table_head_center.gif"><img src="shared/images/headline_menu.gif" width="48" height="25"></td>
      <td width="14"><img src="shared/images/table_head_right.gif" width="14" height="25"></td>
    </tr>
    <tr>
      <td background="shared/images/table_middle_left.gif">&nbsp;</td>
      <td><? include_once "dynamic/menue.php" ?></td>
      <td background="shared/images/table_middle_right.gif">&nbsp;</td>
    </tr>
    <tr>
      <td height="15" valign="top"><img src="shared/images/table_bottom_left.gif" width="14" height="15"></td>
      <td height="15" background="shared/images/table_bottom_center.gif">&nbsp;</td>
      <td valign="top"><img src="shared/images/table_bottom_right.gif" width="14" height="15"></td>
    </tr>
  </table></td>
  <td width="4" valign="top">&nbsp;</td>
  <td valign="top"><table width="100%" height="100%"  border="0" cellpadding="0" cellspacing="0">
    <tr>
      <td width="14"><img src="shared/images/table_head_left.gif" width="14" height="25"></td>
      <td background="shared/images/table_head_center.gif"><img src="shared/images/headline_<?=$_GET['modul']?>.gif" height="25"></td>
      <td width="14"><img src="shared/images/table_head_right.gif" width="14" height="25"></td>
    </tr>
    <tr>
      <td background="shared/images/table_middle_left.gif">&nbsp;</td>
      <td><?
if(!@include_once "dynamic/" . $_GET['modul'] . ".php")
include_once "dynamic/404.php";
?></td>
      <td background="shared/images/table_middle_right.gif">&nbsp;</td>
    </tr>
    <tr>
      <td height="15" valign="top"><img src="shared/images/table_bottom_left.gif" width="14" height="15"></td>
      <td height="15" background="shared/images/table_bottom_center.gif">&nbsp;</td>
      <td valign="top"><img src="shared/images/table_bottom_right.gif" width="14" height="15"></td>
    </tr>
  </table></td>
  </tr>
</table>
</body>
</html>
