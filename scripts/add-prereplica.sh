#!/bin/sh
clear
BFTSMART="../BFT-SMaRt-PrimaryBackup"



#gnome-terminal -e "java -cp dist/Core-MIS.jar:$BFTSMART/dist/BFT-SMaRt.jar:$BFTSMART/lib/slf4j-api-1.5.8.jar:$BFTSMART/lib/slf4j-jdk14-1.5.8.jar:$BFTSMART/lib/netty-3.1.1.GA.jar:$BFTSMART/lib/commons-codec-1.5.jar:$BFTSMART/config/ core.CoreMIS 3 prereplica $1 config/prereplica$1.properties"

kill $(ps aux | grep 'core.CoreMIS 3 prereplica' | awk '{print $2}')

java -cp dist/Core-MIS.jar:$BFTSMART/dist/BFT-SMaRt.jar:$BFTSMART/lib/slf4j-api-1.5.8.jar:$BFTSMART/lib/slf4j-jdk14-1.5.8.jar:$BFTSMART/lib/netty-3.1.1.GA.jar:$BFTSMART/lib/commons-codec-1.5.jar:$BFTSMART/config/ core.CoreMIS 3 prereplica $1 config/prereplica$1.properties >> file.log 2>&1 &
