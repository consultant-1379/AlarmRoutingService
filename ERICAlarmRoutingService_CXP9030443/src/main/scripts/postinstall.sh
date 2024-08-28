#!/bin/bash
#post install script will be invoked once AlarmRoutingService rpm after installation. 
#And scripts copies the /opt/ericsson/com.ericsson.nms.services.AlarmRoutingService/conf/FM_EMAIL_DIGITAL_SIGNATURE.xml file to  
#/ericsson/credm/data/xmlfiles/ to generate FM_EMAIL_DIGITAL_SIGNATURE.jks file for signing the email messages.

CERTS_PATH=/ericsson/fm/data/certs/
XML_FILES_PATH=/ericsson/credm/data/xmlfiles/
CONF_PATH=/ericsson/fault_management/alarmroutingservice/conf/
MKDIR="/bin/mkdir -p"
CP="/bin/cp"

logger "AlarmRoutingService RPM Postinstall"

#/opt/ericsson/com.ericsson.nms.services.AlarmRoutingService
stringRunLevel=`grep '^id:' /etc/inittab`
runLevel=`sed "s/[^0-9]//g;s/^$/-1/;" <<< $stringRunLevel`
if [ ! -e $CERTS_PATH ]; then
   $MKDIR -m 775 $CERTS_PATH
fi  
if [ ! -e $XML_FILES_PATH ]; then
   $MKDIR -m 775 $XML_FILES_PATH 
fi

$CP $CONF_PATH/FM_EMAIL_DIGITAL_SIGNATURE.xml $XML_FILES_PATH
exit 0
