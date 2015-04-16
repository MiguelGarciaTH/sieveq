#Core-mis firewall

This is an intrusion-tolerant replicated firewall.

# How to run the scripts
scripts/core-mis.sh MODE Type IDs

MODE: three modes of organize the components

Type: replica BFT-SMART, Server, First

IDs: in client type the client should provide the server ID to connect. 

## Mode 1 - simple, one client TOM to replicas and Server ####
First, launch the replicas:

scripts/core-mis.sh 1 replica 0 config/replica.properties

scripts/core-mis.sh 1 replica 1 config/replica.properties

scripts/core-mis.sh 1 replica 2 config/replica.properties

scripts/core-mis.sh 1 replica 3 config/replica.properties

Second, launch the server:

scripts/core-mis.sh 1 server 6 config/server.properties

Third, launch the client:

scripts/core-mis.sh 1 client 5 config/client.properties 6

scripts/core-mis.sh 3 attacker 666 config/attacker.properties 

## Mode 2 - primary backup, send to leader, leader send to the other replicas.
First, launch the replicas:

scripts/core-mis.sh 2 replica 0 config/replica.properties

scripts/core-mis.sh 2 replica 1 config/replica.properties

scripts/core-mis.sh 2 replica 2 config/replica.properties

scripts/core-mis.sh 2 replica 3 config/replica.properties

Second, launch the server:

scripts/core-mis.sh 2 server 6 config/server.properties


Third, launch the client:

scripts/core-mis.sh 2 client 5 config/client.properties 6


## Mode 3 - Demo version, one client, one pre-replica (pre-filter) TOM to replicas and Server ####
First, launch the replicas:

scripts/core-mis.sh 3 replica 0 config/replica.properties

scripts/core-mis.sh 3 replica 1 config/replica.properties

scripts/core-mis.sh 3 replica 2 config/replica.properties

scripts/core-mis.sh 3 replica 3 config/replica.properties


Second, launch the server: 
scripts/core-mis.sh 3 server 6 config/server.properties



Third, launch the controller

scripts/core-mis.sh 3 controller 9 config/controller.properties


Third, launch the prereplica:

scripts/core-mis.sh 3 prereplica 7 config/prereplica.properties


Fourth, launch the client:

scripts/core-mis.sh 3 client 7 config/client.properties 6


Fifth, launch the attacker

scripts/core-mis.sh 2 attacker 666 config/attacker.properties 

