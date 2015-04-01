#!/bin/sh
clear

BFTSMART="../../SMaRt"
#export PATH=$PATH:../../SMaRt/config
#echo "$PATH"

echo "kill -9 pgrep -f replica $1"
kill $(ps aux | grep 'replica 1' | awk '{print $2}')

export PATH=$PATH:/root/Core-MIS/config

cd /root/Core-MIS
SIEVE=.

echo $(pwd)
java -cp $SIEVE/dist/SieveQ.jar:$SIEVE/dist/BFT-SMaRt.jar:$SIEVE/lib/slf4j-api-1.5.8.jar:$SIEVE/lib/slf4j-jdk14-1.5.8.jar:$SIEVE/lib/netty-3.1.1.GA.jar:$SIEVE/lib/commons-codec-1.5.jar:config/ core.CoreMIS 3 replica 1 $SIEVE/config/replica.properties

#BFTSMART="/root/Core-MIS/dist"
#kill $(ps aux | grep 'replica 1' | awk '{print $2}')
#SIEVEQ="/root/Core-MIS"
#echo $(pwd)
#java -cp $SIEVEQ/dist/SieveQ.jar:$BFTSMART/BFT-SMaRt-PrimaryBackup.jar:$BFTSMART/lib/slf4j-api-1.5.8.jar:$BFTSMART/lib/slf4-jdk14-1.5.8.jar:$BFTSMART/lib/netty-3.1.1.GA.jar:$BFTSMART/lib/commons-codec-1.5.jar:$SIEVEQ/config/ core.CoreMIS 3 replica 1 $SIEVEQ/config/replica.properties
#>> file.log 2>&1 
