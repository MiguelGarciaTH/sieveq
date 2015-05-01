#!/bin/bash

APP="SieveQ"

LOCAL=0
if [ $LOCAL = 1 ]; then
	QUINTA="r1 s1"
	TOKILL=$QUINTA
	SERVER="s1" 
	PREREPLICA="s1" 
	CLIENT="r1"
else 
	QUINTA="s6 s9 s10 s11 s12 s13 s7 s8"
	TOKILL=$QUINTA
	SERVER="s9"
	CLIENT="s6"
	CONTROLLER="s7"
	PREREPLICA="s7 s8"
fi

#ATTACKER="s17"
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
		echo $i ": cp $APP configurations"
		scp -r config/* $i.quinta:~/Core-MIS/config/
		echo $i ": scp $APP complete"
	done

elif [ "$1" = "rename" ]; then
	for i in $QUINTA
	do
		echo $i ": renaming node to mhenriques "
		ssh mhenriques@quinta.navigators.di.fc.ul.pt quinta update -u mhenriques $i
		echo $i ":  renaming node complete "
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
		echo $i ": clear $APP"
		ssh $i.quinta -C "killall java"
		ssh $i.quinta rm -R /root/Core-MIS/*
	done

else
	for i in $QUINTA 
	do
		rsync -aL -d --delete * $i.quinta:/root/Core-MIS
		echo $i ": $APP sync completed .."
	done
fi

