#!/bin/bash
#This script will be invoked before AlarmRoutingService rpm installation and create KeyStore with algorithm information.
#And it reads keyStore password information and updated in standalone-enm.xml file further use.

VAULT_PATH=/ericsson/3pp/jboss/bin/JBossVault                              #Path to store vault.keystore file
KEY_TOOL=/usr/java/default/bin/keytool										#Keytool Path
VAULT_SCRIPT=/ericsson/3pp/jboss/bin/vault.sh								#JBOSS vault Script

VAULT_INFO_FILE=/var/tmp/vault_info_fm.txt						#File to store content of vault that need to added in standalone
VERSION_FILE=/ericsson/3pp/jboss/version.txt
STANDALONE_PATH=/ericsson/3pp/jboss/standalone/configuration/standalone-enm.xml #Standalone file name
STANDALONE_CLI_FILE=/ericsson/3pp/jboss/bin/cli/services/setup_fm_vault.cli

KEY_STORE=vault.keystore               #File name of the vault store
KEY_PWD=6wkv/gSwMTY                    #vault key password
STORE_PWD=6wkv/gSwMTY				   #vault store password
STORE_ALIAS=jbossVault                      #vault store alias name
KEY_SIZE=128                           #Size of the key
KEY_ALGORITHM=AES					   #Key algorithm name
ITERATION_COUNT=8                      #Iteration count to store the password
SALT_PWD=saltpswd                      #Salt password to store the password
STORE_TYPE=jceks					   #Type of the key store


PWD_TO_BE_STORED=jbossKS              #Actual password to be stored
ATTRIBUTE_NAME=SecuredAttributeName    #Attribute name stored s reference
BLOCK_NAME=FM_VAULT                  #Block name of the password stored
GREP="/bin/grep"						#grep
SED="/bin/sed"						#sed
MKDIR="/bin/mkdir -p"					#mkdir
RMRF="/bin/rm -rf"					#rm -rf

PROPERTY_FM='\\t<property name="FM_ROUTING_KEYSTORE_PASSWORD_PROPERTY" value="${VAULT::FM_VAULT::RA_KEYSTORE_FM::1}"/>'

LOG_TAG="ERICAlarmRoutingService"

#//////////////////////////////////////////////////////////////
# This function will print an info message to /var/log/messages
# Arguments:
#       $1 - Message
# Return: 0
#/////////////////////////////////////////////////////////////
info()
{
    logger -t ${LOG_TAG} -p user.info "$1"
}

#//////////////////////////////////////////////////////////////
# This function will print an warning message to /var/log/messages
#/////////////////////////////////////////////////////////////
warn() {

    logger -s -t ${LOG_TAG} -p user.warn "WARNING: $@"
}

#Creates a vault keystore to store the passwords to be used in the applications 
create_keystore(){
if [ ! -d $VAULT_PATH ]; then
        $MKDIR $VAULT_PATH
        $KEY_TOOL -genseckey -alias $STORE_ALIAS -storetype $STORE_TYPE -keyalg $KEY_ALGORITHM -validity 10000 -keysize $KEY_SIZE -keypass $KEY_PWD -storepass $STORE_PWD  -keystore $VAULT_PATH/$KEY_STORE -dname "cn=JBOSS_VAULT, ou=ENM, o=Ericsson, c=SE"
else
        info "The Directory Already Exits : $VAULT_PATH"
fi
}

# open vault, give keystore to vault and save password into vault block
save_passwords(){
ATTRIBUTE_LIST=$1
for ATTRIBUTE_NAME in $ATTRIBUTE_LIST; do
JAVA_OPTS="$JAVA_OPTS -Djboss.modules.system.pkgs=com.sun.crypto.provider" sh $VAULT_SCRIPT -e $VAULT_PATH/  -k $VAULT_PATH/$KEY_STORE -p $STORE_PWD -s $SALT_PWD -i $ITERATION_COUNT -v $STORE_ALIAS -a $ATTRIBUTE_NAME -b $BLOCK_NAME -x $PWD_TO_BE_STORED > $VAULT_INFO_FILE #Store vault into a temporary file
done
}


update_standalone(){
info "update_standalone $STANDALONE_PATH"
if  $GREP -Fq "<vault>" $STANDALONE_PATH ; then
    logger "Vault configuration already exits in standalone-enm.xml"
else
	$SED -i -n '/<vault>/,/<\/vault>/p' $VAULT_INFO_FILE                      # Only vault configuration information is retained in the file
	$SED -i 's/<management> ...//;s/^/    /;1i\\' $VAULT_INFO_FILE            # To format vault info to fit in standalone file
	$SED -i "/<\/system-properties>/ r $VAULT_INFO_FILE" $STANDALONE_PATH     # To copy Vault information to stand alone standalone
fi

$SED -i "/<system-properties>/a $PROPERTY_FM" $STANDALONE_PATH     # To copy Property to system property of stand alone
    info "To copy Property to system property of stand alone"
$RMRF $VAULT_INFO_FILE
}

create_cli_update_script() {
  info "create_cli_update_script $STANDALONE_CLI_FILE"
  if [ -f  $VAULT_INFO_FILE ]; then
    $SED -n '/^\/core-service/p' $VAULT_INFO_FILE > $STANDALONE_CLI_FILE
    VAULT_SYSTEM_PROP_CLI="/system-property=FM_ROUTING_KEYSTORE_PASSWORD_PROPERTY:add(value=\$\${VAULT::FM_VAULT::RA_KEYSTORE_FM::1})"
    echo $VAULT_SYSTEM_PROP_CLI >> $STANDALONE_CLI_FILE
    $RMRF $VAULT_INFO_FILE
  else
    warn "create_cli_update_script failed $VAULT_INFO_FILE not found"
  fi
}

get_jboss_version() {
  VERSION=$(sed 's/.*Version //' $VERSION_FILE)
  firstchar=${VERSION:0:1}
  echo $firstchar
}

version=$(get_jboss_version)
if [ "$version" = "7" ]; then
  info "get_jboss_version EAP 7"
  STANDALONE_PATH=/ericsson/3pp/jboss/standalone/configuration/standalone-eap7-enm.xml #Standalone file name
else
  info "get_jboss_version EAP 6"
  STANDALONE_PATH=/ericsson/3pp/jboss/standalone/configuration/standalone-enm.xml #Standalone file name
fi

if  $GREP -Fq "FM_VAULT" $STANDALONE_PATH ; then
    info "FM vault related system properties already exits in standalone-enm.xml"
else
	create_keystore
	save_passwords "RA_KEYSTORE_FM"
	if [ "$version" = "6" ]; then
	  # EAP 6
	  update_standalone
	else
	  # EAP7
	  create_cli_update_script
	fi
fi

exit 0