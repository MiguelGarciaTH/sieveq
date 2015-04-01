#!/bin/bash


KEY_PATH=../keys/
# generate a 2048-bit RSA private key
openssl genrsa -out $KEY_PATH/$1_priv.pem

# convert private Key to PKCS#8 format (so Java can read it)
openssl pkcs8 -topk8 -inform PEM -outform DER -in $KEY_PATH/$1_priv.pem -out $KEY_PATH/$1_priv.der -nocrypt
openssl rsa -in $KEY_PATH/$1_priv.pem -pubout -outform DER -out $KEY_PATH/$1_pub.der
