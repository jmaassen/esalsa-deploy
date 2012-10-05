#!/bin/sh
java -cp ":.:bin:external/*" \
-Dgat.adaptor.path=/home/jason/Workspace/esalsa-deploy/external/adaptors \
nl.esciencecenter.esalsa.deploy.server.Server poprunner.properties
