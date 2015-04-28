#!/bin/sh
clear

#ntpdate cuco.di.fc.ul.pt

BFTSMART="../../SMaRt"
#export PATH=$PATH:../../SMaRt/config
#echo "$PATH"
#cat config/system.config
if [ $2 = "replica" ]; then
	rm /root/Core-MIS/config/currentView
fi 
java -cp dist/SieveQ.jar:$BFTSMART/dist/BFT-SMaRt.jar:$BFTSMART/lib/slf4j-api-1.5.8.jar:$BFTSMART/lib/slf4j-jdk14-1.5.8.jar:$BFTSMART/lib/netty-3.1.1.GA.jar:$BFTSMART/lib/commons-codec-1.5.jar:config/ core.CoreMIS $@

