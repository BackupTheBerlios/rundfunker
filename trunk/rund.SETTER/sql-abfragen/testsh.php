<html>
<body>
<pre>
<?php
	$skript = dirname($_SERVER['SCRIPT_FILENAME']).'/admin/echome.sh';
	echo $skript."\n";
	system('sudo '.$skript.' 2>&1',$return);
	echo $return.'.';
?>
</pre>
</body>
</html>
