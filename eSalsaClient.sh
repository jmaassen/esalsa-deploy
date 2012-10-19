#!/bin/sh
java -cp ":.:bin:external/*" \
nl.esciencecenter.esalsa.deploy.ui.swing.GUI $1
