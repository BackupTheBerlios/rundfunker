#!/bin/bash
# returns the device which
# has the name $PARTITIONNAME or $1
# (C) Jan Peuker 2004

DEVICES="/dev/sd?[0-9] /dev/hd?[0-9]"
PARTITIONNAME="usbpst"

if [ -n "$1" ]; then
	PARTITIONNAME=$1
fi

trymount() {
	if test -b $1; then
		# echo -n "1"
		return 0
	fi
	# echo -n "0"
	return 1
}

# taken from "fstype" bei Klaus Knopper

isext() {
	dd if="$1" count=1 bs=1k >/dev/null 2>&1 || return 1
	
	# echo -n "3"
	
	FILE="$(LANG=C LC_ALL=C LC_MESSAGES=C file -Lkbs "$1")"

	# I use "case" because it has pattern matching
	case "$FILE" in
	 *[Ee][Xx][Tt][23]*)
	 	# echo -n "5"
		return 0
		;;
	esac
	# echo -n "2"
	return 1
}


for i in $DEVICES
do
	# echo -n "trying $i: "
	if trymount $i && isext $i
	then
		LABEL="$(e2label "$i" 2>&1)"
		if [ $LABEL = $PARTITIONNAME ]; then
			echo -n $i
			exit 0
		# else
			# echo $LABEL
		fi
	fi
	# echo ""
done

exit 1
