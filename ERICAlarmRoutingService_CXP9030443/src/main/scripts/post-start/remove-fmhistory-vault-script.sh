#!/bin/bash
#This script will be invoked post installation of AlarmRoutingService rpm to remove create-fmhistory-vault.sh file.
rm -rf /ericsson/3pp/jboss/bin/pre-start-with-exit/create-fmhistory-vault.sh