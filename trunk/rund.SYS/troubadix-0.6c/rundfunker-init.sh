#!/bin/bash
# /pst/troubadix/rundfunker-init.sh -> /etc/init.d/rundfunker
# Rundfunker main startup Script
# (c) Jan Peuker 2005

set -e
set -u
${DEBIAN_SCRIPT_DEBUG:+ set -v -x}

SELF=$(cd $(dirname $0); pwd -P)/$(basename $0)

#
# main()
#

case "${1:-''}" in
  'start')
	echo "Rundfunker is booting"
	;;
  'stop')
  	echo "Rundfunker is shutting down"
	;;
  'restart')
  	set +e; $SELF stop; set -e
	$SELF start
	;;
  'status')
  	echo "Status"
  	;;
  *)
  	echo "Usage $SELF start|stop|restart|status"
	exit 1
	;;
esac
  
