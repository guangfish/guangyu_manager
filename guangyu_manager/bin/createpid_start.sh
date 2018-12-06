#!/bin/bash

PRGDIR=`dirname $0`
SERVER_HOME=$(echo `readlink -f $PRGDIR` | sed 's/\/bin//')
. "$SERVER_HOME/bin/env.sh"

export DISPLAY=:1
export CLASSPATH=$CLASSPATH
nohup $JAVA_HOME/bin/java -server $JAVA_OPTS com.bt.om.CreateTaobaoPid ${COMMAND} > $SERVER_HOME/logs/catalina-pids.log 2>&1 &
