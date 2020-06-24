#!/bin/bash
# Author: Andreas Baulig
#
# Execute this script on your development host PC. The purpose of this script is
# to provide the development Tomcat instance with a locally trusted certificate.
# This is needed if You want to use a web-browser to access the website via
# HTTPS.
#
# Inside the dev-container a script defined in .devcontainer/devcontainer.json
# will try to install whatever root CA it finds inside /workspace/docker/cert.
# This is done in order to allow integration tests to execute on HTTPS as well.

echo -n "This script will generate an SSL certificate to be used with the dev-containers
tomcat installation. To make certificate trusted by Your local system, we
generate a local root CA (Certificate Authority), which signs the server
certificate.

WARNING: Generateing a root CA and installing it as locally trusted may impose a
security risk on your system. You can use this script to generate the server
certificate only, and skip the installation of the root CA into Your local
trust-store(s).

Do you want to continue? (y/N): "
read answer
echo
if [ "$answer" != "${answer#[Yy]}" ]; then
    echo "Checking requirements..."
else
    echo "Aborting."
    exit 1;
fi

command -v 'mkcert' >/dev/null 2>&1 || (echo "mkcert not installed. Please visit https://github.com/FiloSottile/mkcert and follow the install instructions there."; exit 1)
command -v 'openssl' >/dev/null 2>&1 || (echo "openssl not installed. Please check with your distribution provider on how to install openssl."; exit 1)

echo $1
if [ -z "$1" ]; then
    while (( ${#keystore_password} < 8 ))
    do
        echo -n "Please define a password for the java keystore (>=8 characters): "
        read -s keystore_password
        echo
    done
else
    keystore_password="$1"
fi

mkdir -p cert \
&& cd cert \
&& mkcert localhost 127.0.0.1 >/dev/null 2>&1 \
&& cp "$(mkcert -CAROOT)/rootCA.pem" . \
&& rm -f sa1.jks \
&& cat 'rootCA.pem' 'localhost+1.pem' > chain.pem \
&& openssl pkcs12 -export -inkey localhost+1-key.pem -in chain.pem -name sa1 -out sa1.p12 -password "pass:$keystore_password"

echo -n "
A server certificate and key-pairs have been generated, using mkcert and your
current development CA. To make the certificate trusted, copy cert/rootCA.pem
into your system's trust-store(s).

Do you want mkcert to copy the development CA into your system's
trust-store(s)? (y/N): "
read answer
echo
if [ "$answer" != "${answer#[Yy]}" ]; then
    echo "Installing CA into system trust-store(s).
    "
    mkcert -install
else
    echo "Leaving system trust-store as is."
fi
