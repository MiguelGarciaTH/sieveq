#!/bin/sh

pid=$(ps aux | grep 'replica 1' | awk '{print $2}')
echo $pid

kill -9 $pid
