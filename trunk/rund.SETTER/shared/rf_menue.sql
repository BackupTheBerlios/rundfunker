# MySQL-Front 3.2  (Build 7.35)

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

# Host: localhost    Database: rundfunker
# ------------------------------------------------------
# Server version 4.1.15-nt

#
# Table structure for table rf_menue
#

DROP TABLE IF EXISTS `rf_menue`;
CREATE TABLE `rf_menue` (
  `menuepunkt` varchar(40) NOT NULL default '',
  `modul` varchar(100) NOT NULL default '',
  `menu_order` int(11) NOT NULL default '0',
  PRIMARY KEY  (`modul`(1))
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

#
# Dumping data for table rf_menue
#

/*!40101 SET NAMES latin1 */;

INSERT INTO `rf_menue` VALUES ('Einstellungen','einstellungen.php',2);
INSERT INTO `rf_menue` VALUES ('Genres','genregroup.php',4);
INSERT INTO `rf_menue` VALUES ('Pfadkonfiguration','pfadkonf.php',3);
INSERT INTO `rf_menue` VALUES ('Statisik','statistik.php',5);
INSERT INTO `rf_menue` VALUES ('&Uuml;bersicht','uebersicht.php',1);

/*!40101 SET NAMES utf8 */;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
