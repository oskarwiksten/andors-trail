#!/bin/sh

for i in *.po
do
	echo "$i"
	msgmerge --no-fuzzy-matching "${i}" english.pot \
		| msgattrib --translated \
		| msguniq --no-wrap --sort-by-file >"${i}.tmp1"
	mv "${i}.tmp1" "$i"
done
