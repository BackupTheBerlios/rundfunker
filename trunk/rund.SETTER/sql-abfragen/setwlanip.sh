#!/bin/sh
# This shellscript sets the ip
# for a device set in $DEVICE
# $1 interface
# $2 ip address
# $3 netmask
# $4 gateway
# $5 nameserver
# $6 domain
# TODO: Check Parameters
sudo ifconfig $1 $2 netmask $3 down 2>&1
sudo route del default 2>&1
sudo route add default gw $4 dev $1 2>&1
sudo echo "nameserver $5\n" > /etc/resolv.conf
sudo ifconfig $1 up

