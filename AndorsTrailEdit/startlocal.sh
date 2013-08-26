#!/bin/sh

cat <<END

============
Starting local content editor at http://localhost:8080/AndorsTrailEdit/editor.html
============

END
weborf --port 8080 --mime --basedir "$(dirname $0)/../"
