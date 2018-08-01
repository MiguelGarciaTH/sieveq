#!/bin/sh
clear
BFTSMART="../BFT-SMaRt-PrimaryBackup"


#gnome-terminal --title "core mis" -x bash -c "sshpass -p quinta ssh -t s5 sh /root/Core-MIS/scripts/kill-prereplica.sh 7"


#gnome-terminal --title "core mis" -x bash -c "sshpass -p quinta ssh -t s4 'cd Core-MIS/ ; scripts/core-mis.sh 3 prereplica 8 config/prereplica.properties'"

kill $(ps aux | grep 'core.CoreMIS 3 prereplica' | awk '{print $2}')

#gnome-terminal -e "java -cp dist/Core-MIS.jar:$BFTSMART/dist/BFT-SMaRt.jar:$BFTSMART/lib/slf4j-api-1.5.8.jar:$BFTSMART/lib/slf4j-jdk14-1.5.8.jar:$BFTSMART/lib/netty-3.1.1.GA.jar:$BFTSMART/lib/commons-codec-1.5.jar:$BFTSMART/config/ core.CoreMIS 3 prereplica $1 config/prereplica$1.properties"

java -cp dist/SieveQ.jar:$BFTSMART/dist/BFT-SMaRt.jar:$BFTSMART/lib/slf4j-api-1.5.8.jar:$BFTSMART/lib/slf4j-jdk14-1.5.8.jar:$BFTSMART/lib/netty-3.1.1.GA.jar:$BFTSMART/lib/commons-codec-1.5.jar:$BFTSMART/config/ core.CoreMIS 3 prereplica $1 config/prereplica2.properties >> file.log 2>&1
