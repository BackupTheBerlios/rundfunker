#!/bin/sh
/sbin/ifconfig eth0 192.168.0.25 netmask 255.255.255.0 broadcast 192.168.0.255 upecho "done by $USER"