#!/bin/sh

# Creates translation .mo files (binary) from the .po files (text).
# The game reads .mo files, so this needs to be run before building.

for i in *.po ; do 
	msgfmt "$i" -o $( basename $i .po ).mo 
done
