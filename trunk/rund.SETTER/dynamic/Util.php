<?php
class Util {

	public function pager($link, $eintraege, $count, $page = 1) {
		$maxpages = ceil($count / $eintraege);
		?>
		<table border="0">
			<tr>
				<td width="20" align="center">
					<?=($page > 1)?"<a href=\"" . $link . "&page=1" . "\">&lt;&lt;</a>":"&nbsp;&nbsp;";?>
				</td>
				<td width="20" align="center">
					<?=($page > 1)?"<a href=\"" . $link . "&page=" . ($page - 1) . "\">&lt;</a>":"&nbsp;";?>
				</td>
				<td width="20" align="center">
					Seite&nbsp;<?=$page?>&nbsp;von&nbsp;<?=$maxpages?>
				</td>
				<td width="20" align="center">
					<?=($page < $maxpages)?"<a href=\"" . $link . "&page=" . ($page + 1) . "\">&gt;</a>":"&nbsp;";?>
				</td>
				<td width="20" align="center">
					<?=($page < $maxpages)?"<a href=\"" . $link . "&page=" . $maxpages . "\">&gt;&gt;</a>":"&nbsp;&nbsp;";?>
				</td>
			</tr>
		</table>
		<?


	}

	public function sendMessage($ip, $port, $message) {

		error_reporting(E_ALL);
		$fp = fsockopen($ip, $port, $errno, $errstr, 30);
		if (!$fp) {
		    echo "Fehler: $errno - $errstr<br>\n";
		} else {
		    fwrite($fp, $message . "\n");
		    fclose($fp);
		}

	}

	public function writeNetworkConfig($filename, $eth, $address, $netmask, $network, $broadcast, $gateway) {

		error_reporting(E_ALL);

		// Sichergehen, dass die Datei existiert und beschreibbar ist
		if (is_writable($filename)) {

			$file = file($filename);

			if (!$handle = fopen($filename, "w")) {
				print "Kann die Datei $filename nicht öffnen";
				exit;
			}

			for($i = 0; $i < count($file); $i++)
			{
//				echo $file[$i] . "<br>";
				if(strpos($file[$i], $eth . " inet"))
				{
				$linepos = $i;
				break;
				}
			}
		
			$this->switchIP($file, $linepos, "address", $address);
			$this->switchIP($file, $linepos, "netmask", $netmask);
			$this->switchIP($file, $linepos, "network", $network);
			$this->switchIP($file, $linepos, "broadcast", $broadcast);
			$this->switchIP($file, $linepos, "gateway", $gateway);
			
			foreach($file as $line)
			{
				fwrite($handle, $line);
			}
			fclose($handle);

		} else {
			print "Die Datei $filename ist nicht schreibbar";
		}

	}

	private function switchIP(&$array, $line_pos, $type, $ip)
	{
			for($i = $line_pos; $i < count($array); $i++)
			{
				if($temppos = strpos($array[$i], $type))
				{
					$array[$i] = substr($array[$i],0,$temppos + strlen($type)) . " " . $ip . "\n";
					break;
				}
			}
	}
	
	public function setWlanConfig($eth, $address, $netmask, $gateway, $nameserver)
	{
		exec("/pst/troubadix/setwlanip.sh " . $eth . " " . $address . " " . $netmask . " " . $gateway . " " . $nameserver, $output, $checkint);
		$this->toLogfile("setWlanConfig: " . implode($output, "\n"));
		return $checkint == 1;
	}
	
	public function toLogfile($log)
	{
			$filename = "/tmp/log.txt";
			if (!$handle = fopen($filename, "a")) {
				print "Kann die Datei $filename nicht öffnen";
				exit;
			}
			fwrite($handle, $log . "\n");
			fclose($handle);
			return "wrote to Log";
	}

	public function getAddress($eth)
	{
		exec("sudo ifconfig " . $eth . " 2>&1", $array);
		$string = implode($array, "\n");
		// Obacht: Als Root ist ifconfig englisch!
		preg_match_all("/Addr:([0-9\.]+).*Mask:([0-9\.]+)/im", $string, $matches);
		return $matches[1][0];
	}

	public function getNetmask($eth)
	{
		exec("sudo ifconfig " . $eth . " 2>&1", $array);
		$string = implode($array, "\n");
		// Obacht: Als Root ist ifconfig englisch!
		preg_match_all("/Addr:([0-9\.]+).*Mask:([0-9\.]+)/im", $string, $matches);
		return $matches[2][0];
	}
	
	public function getGateway()
	{
		exec("sudo route 2>&1", $array);
		$string = implode($array, "\n");
		preg_match_all("/default\\s+([0-9\.]+)/im", $string, $matches);
		return $matches[1][0];
	}
	
	public function getNameserver($eth)
	{
		exec("sudo cat /etc/resolv.conf", $array);
		$string = implode($array, "\n");
		preg_match_all("/nameserver\\s+([^\\s]+)/im", $string, $matches);
		return implode($matches[1],",");	
	}

	public function searchPath($path)
	{
		echo preg_match("hallo", "hallo");
		//exec("sudo java -jar /home/troubadix/rundspieler/Rundsucher.jar " . $path, $output);
		//return $output;
	}

}
?>






