#!/bin/sh

cat <<END

============
Starting local content editor at http://localhost:8080/AndorsTrailEdit/editor.html
============

END
webfsd -F -p 8080 -r "$(dirname $0)/../"
