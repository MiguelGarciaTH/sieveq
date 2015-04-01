#!/bin/sh
echo "kill -9 pgrep -f prereplica $1"
kill -9 `pgrep -f "prereplica $1"`
