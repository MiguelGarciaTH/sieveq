#!/bin/bash
APP="SieveQ"
#QUINTA="v2 s5 s6 s7 s8 s9 s11"
QUINTA="s4 s15 s5 s6 s7 s8 s9 s11 s10"


#s2
#QUINTA="v2"
TOKILL=$QUINTA
SERVER="s6"
#CLIENT="s2"
CLIENT="s15"
CONTROLLER="s5"
PREREPLICA="s5"
#CLIENTS="r5,7 r3,8 r9,11 r4,10 r10,12 r1,13 r7,15 r2,14"
CLIENTS="s15,7"
#v2,8 v2,11 v2,10 v2,12 v2,13 v2,15 v2,14 v2,16 v2,17 v2,v18 v2,19 v2,20 v2,21 v2,22 v2,23 v2,24 v2,25 v2,26 v2,27 v2,28 v2,29 v2,30 v2,31 v2,32 v2,33 v2,34 v2,35 v2,36 v2,37"

#ATTACKER="s17"
#IMGS="/media/miguel/MIGUEL/Papers/Author/Journals/2015-core-mis/IEEE_2/imgs/gnuplot/plots/"
IMGS="/media/miguel/MIGUEL/Papers/Author/Journals/2015-SieveQ/imgs/gnuplot/plots/"
PUB_KEY=miguelgarcia.pub


if [ "$1" = "client" ]; then
	sshpass -p quinta scp $CLIENT.quinta:/root/Core-MIS/client.latency $IMGS/client.latency
	cd $IMGS ; gnuplot latency.gnu		
elif [ "$1" = "server" ]; then
	sshpass -p quinta scp $SERVER.quinta:/root/Core-MIS/server.throughput $IMGS/server.throughput
	cd $IMGS ; gnuplot throughput.gnu		

elif [ "$1" = "on" ]; then
	ssh -t quinta "quinta on $QUINTA"

elif [ "$1" = "run" ]; then
	k=1
	t=0
	for i in $CLIENTS; do IFS=","; set $i;  
		gnome-terminal --title "$1" -x bash -c "ssh -t $1.quinta 'cd Core-MIS/; ./scripts/core-mis.sh 3 client $2 config/client$k.properties 6;'"
#		$t++
		echo $i ":  running ... $t "
#		if [ "$k" == 1 ]; then		
#			k=2
#		else
#			k=1;
#		fi 
		if [ "$2" == 7 ]; then		
			sleep 2m
		fi
		sleep 1m
	done

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

elif [ "$1" = "java" ]; then
	for i in $QUINTA
	do	
		sshpass -p quinta scp ~/Scripts/install-java-7.sh root@$i.quinta:/root/Core-MIS/
		sshpass -p quinta ssh root@$i.quinta "sh /root/Core-MIS/install-java-7.sh"
	done

elif [ "$1" = "setup" ]; then
	for i in $QUINTA
	do	
		sshpass -p quinta scp ~/.ssh/$PUB_KEY root@$i.quinta:~
		sshpass -p quinta ssh root@$i.quinta "cat $PUB_KEY >>	.ssh/authorized_keys ; rm $PUB_KEY"
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

