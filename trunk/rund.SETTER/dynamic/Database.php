<?php
/*
 * Created on 05.10.2005
 *
 * To change the template for this generated file go to
 * Window - Preferences - PHPeclipse - PHP - Code Templates
 */
 
abstract class Database
{
    // Die Factory Methode
    public static function databaseFactory($type)
    {
        if (include_once 'database/' . $type . '.php') {
            $classname = $type;
            return new $classname;
        } else {
            throw new Exception ('Datenbankklasse nicht gefunden');
        }
    }
    
    abstract protected function query($query);
    
}
 
 
?>
