#!/bin/bash
#QUINTA="s6 s9 s10 s11 s12 s13 s7 s8"
QUINTA="r1 s1"
#TOKILL="s6 s9 s10 s11 s12 s13 s7 s8"
SERVER="s1" 
#"s9"
CLIENT="r1"
 #"s6"
CONTROLLER="s7"
PREREPLICA="s1" #"s7 s8"
ATTACKER="s17"
IMGS="/media/miguel/MIGUEL/Papers/Author/Journals/2015-core-mis/IEEE_2/imgs/gnuplot/plots/"
PUB_KEY=miguelgarcia.pub

if [ "$1" = "client" ]; then
	sshpass -p quinta scp $CLIENT.quinta:/root/Core-MIS/client.latency $IMGS/client.latency
	cd $IMGS ; gnuplot latency.gnu		
elif [ "$1" = "server" ]; then
	sshpass -p quinta scp $SERVER.quinta:/root/Core-MIS/server.throughput $IMGS/server.throughput
	cd $IMGS ; gnuplot throughput.gnu		

elif [ "$1" = "config" ]; then
	for i in $QUINTA
	do
		echo $i ": cp configurations"
		scp -r config/* $i.quinta:~/Core-MIS/config/
		echo $i ": scp Core-MIS complete"
	done

elif [ "$1" = "setup" ]; then
	for i in $QUINTA
	do	
		sshpass -p quinta scp ~/.ssh/$PUB_KEY root@$i.quinta:~
		sshpass -p quinta	ssh root@$i.quinta "cat $PUB_KEY >>	.ssh/authorized_keys ; rm $PUB_KEY"
		echo $i ": mkdir Core-MIS"
		ssh $i.quinta mkdir /root/Core-MIS
		ssh $i.quinta chmod 777 -R Core-MIS/*
	done

elif [ "$1" = "clear" ]; then
	for i in $TOKILL
	do
		echo $i ": clear Core-MIS"
		ssh $i.quinta -C "killall java launch.sh"
		ssh $i.quinta rm /root/Core-MIS/
	done

else
	for i in $QUINTA 
	do
		ssh $i.quinta rm ~/Core-MIS/config/currentView
		scp -r * $i.quinta:~/Core-MIS
		echo $i ": scp Core-MIS complete"
	done
fi

