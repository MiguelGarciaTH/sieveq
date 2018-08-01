#!/bin/sh
clear

BFTSMART="../../SMaRt"

if [ $2 = "replica" ]; then
 rm config/currentView
fi 
java -Xms10G -Xmx10G -cp dist/SieveQ.jar:lib/*:config/* core.CoreMIS $@


#java -cp dist/SieveQ.jar:lib/*:config/* core.CoreMIS $@
