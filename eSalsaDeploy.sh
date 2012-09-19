#!/bin/sh
java -cp ":.:bin:external/*" \
-Dgat.adaptor.path=/home/jason/Workspace/esalsa-deploy/external/adaptors \
nl.esciencecenter.esalsa.deploy.POPRunner \
--config ensemble1/config.1degree.x1 \
--worker ensemble1/worker.DAS4VU.1degree.cartesian.32cores \
--worker ensemble1/worker.DAS4LU.1degree.cartesian.32cores \
--inputs ensemble1/input.1degree.x1 \
--outputs ensemble1/output.1degree.x1 \
--experiment ensemble1/experiment.1degree.DAS4VU \
--experiment ensemble1/experiment.1degree.DAS4LU
