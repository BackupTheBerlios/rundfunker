<html>
<head>
<title>Rundfunker display my IP, man!</title>
</head>
<body>
<pre>
<?php

	exec("sudo ifconfig eth1 2>&1", $array);
	$string = implode($array, "\n");
	// Obacht: Als Root ist ifconfig englisch!
	preg_match_all("/Addr:([0-9\.]+).*Mask:([0-9\.]+)/im", $string, $matches);
	echo "IP: ".$matches[1][0]."\n";
	echo "Maske: ".$matches[2][0]." \n";

	exec("sudo route 2>&1", $array);
	$string = implode($array, "\n");
	preg_match_all("/default\\s+([0-9\.]+)/im", $string, $matches);
	echo "Gateway: ".$matches[1][0]."\n";

	exec("sudo cat /etc/resolv.conf", $array);
	$string = implode($array, "\n");
	preg_match_all("/nameserver\\s+([^\\s]+)/im", $string, $matches);
	echo "Nameservers: ".implode($matches[1],",")."\n";

?>
</pre>
</body>
</html>
