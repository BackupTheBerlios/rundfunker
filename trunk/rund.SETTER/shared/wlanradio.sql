# MySQL-Front 3.2  (Build 7.35)

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

# Host: localhost    Database: wlanradio
# ------------------------------------------------------
# Server version 4.1.15-nt

/*!40101 SET NAMES latin1 */;

#
# Table structure for table song
#

DROP TABLE IF EXISTS `song`;
CREATE TABLE `song` (
  `artist` text,
  `title` text,
  `filepath` text NOT NULL,
  `filename` text NOT NULL,
  `IDgenre` int(11) NOT NULL default '0',
  `timesPlayed` int(11) unsigned NOT NULL default '0',
  `secondsPlayed` int(11) unsigned NOT NULL default '0',
  UNIQUE KEY `funique` (`filename`(125),`filepath`(125))
) ENGINE=MyISAM DEFAULT CHARSET=latin1 COMMENT='Gefundene Lieder';

#
# Dumping data for table song
#



#
# Table structure for table webi_ipconfig
#

DROP TABLE IF EXISTS `webi_ipconfig`;
CREATE TABLE `webi_ipconfig` (
  `typ` varchar(15) NOT NULL default '',
  `ip` varchar(15) default NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

#
# Dumping data for table webi_ipconfig
#

INSERT INTO `webi_ipconfig` VALUES ('ip','192.168.0.2');
INSERT INTO `webi_ipconfig` VALUES ('subnetmask','255.255.255.0');
INSERT INTO `webi_ipconfig` VALUES ('standardgateway','192.168.0.1');
INSERT INTO `webi_ipconfig` VALUES ('dhcp','0');


#
# Table structure for table webi_menue
#

DROP TABLE IF EXISTS `webi_menue`;
CREATE TABLE `webi_menue` (
  `menuepunkt` varchar(40) NOT NULL default '',
  `modul` varchar(100) NOT NULL default '',
  PRIMARY KEY  (`modul`(1))
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

#
# Dumping data for table webi_menue
#

INSERT INTO `webi_menue` VALUES ('Einstellungen','einstellungen.php');
INSERT INTO `webi_menue` VALUES ('Pfadkonfiguration','pfadkonf.php');
INSERT INTO `webi_menue` VALUES ('Statisik','statistik.php');
INSERT INTO `webi_menue` VALUES ('Übersicht','uebersicht.php');


#
# Table structure for table webi_suchpfade
#

DROP TABLE IF EXISTS `webi_suchpfade`;
CREATE TABLE `webi_suchpfade` (
  `Id` int(11) NOT NULL auto_increment,
  `pfad` text,
  PRIMARY KEY  (`Id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 COMMENT='MP3-Pfade im Netzwerk';

#
# Dumping data for table webi_suchpfade
#

INSERT INTO `webi_suchpfade` VALUES (13,'\\\\spielzeug\\Share\\MP3-Musik\\MP3-Musik-Mathi\\m\\Metallica');
INSERT INTO `webi_suchpfade` VALUES (14,'\\\\spielzeug\\Share\\MP3-Musik\\MP3-Musik-Mathi\\m\\Metallica');

/*!40101 SET NAMES utf8 */;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
