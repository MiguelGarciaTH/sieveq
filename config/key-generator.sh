#!/bin/bash


KEY_PATH=./keys
# generate a 2048-bit RSA private key
openssl genrsa -out $KEY_PATH/$1.pem 1024
# convert private Key to PKCS#8 format (so Java can read it)
openssl pkcs8 -topk8 -inform PEM -outform DER -in $KEY_PATH/$1.pem -out $KEY_PATH/$1.priv -nocrypt
openssl rsa -in $KEY_PATH/$1.pem -pubout -outform DER -out $KEY_PATH/$1.pub

# openssl pkcs8 -topk8 -inform PEM -outform DER -in private_key_file  -nocrypt > pkcs8_key






