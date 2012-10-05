#!/bin/bash

SERVER=localhost
EXEC="java -cp "./:.:bin:./external/*" nl.esciencecenter.esalsa.deploy.ui.cli.Client"

# workers
$EXEC $SERVER add worker ensemble1/worker.DAS4VU.1degree.cartesian.32cores
$EXEC $SERVER add worker ensemble1/worker.DAS4LU.1degree.cartesian.32cores
$EXEC $SERVER add worker ensemble1/worker.localhost.1degree.cartesian.4cores

# inputs
$EXEC $SERVER add inputs ensemble1/input.1degree.x1

#config templates
$EXEC $SERVER add template ensemble1/config.1degree.x1

#experiments
$EXEC $SERVER add experiment ensemble1/experiment.1degree.localhost
$EXEC $SERVER add experiment ensemble1/experiment.1degree.DAS4VU
$EXEC $SERVER add experiment ensemble1/experiment.1degree.DAS4LU


