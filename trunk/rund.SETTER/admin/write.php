<?php
// Dieses Skript schreibt eine Datei in tmp
if($handle = fopen ("/tmp/test.txt", "a+")){
	if(fwrite($handle, date('Y-m-d h:i:s')."\tZugriff\n")) {
		echo "Daten geschrieben";
	} else echo "Fehler beim Schreiben";
	fclose($handle);	
} else echo "Fehler beim Öffnen";
?>
