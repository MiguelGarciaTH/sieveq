#!/bin/sh

#sshpass -p quinta ssh $1.quinta 'kill $(ps aux | grep \"replica $REPLICA\"| awk '{print $2}')'

#cmd=kill $(ps aux | grep "replica 1"| awk '{print $2}')

sshpass -p quinta ssh $1.quinta killall java
