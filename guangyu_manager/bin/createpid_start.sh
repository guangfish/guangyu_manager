#!/bin/bash

PRGDIR=`dirname $0`
SERVER_HOME=$(echo `readlink -f $PRGDIR` | sed 's/\/bin//')

CLASSPATH=`echo $JAVA_HOME/lib/*.jar | tr ' ' ':'`
CLASSPATH=$CLASSPATH:`echo $SERVER_HOME/target/lib/compile/*.jar | tr ' ' ':'`
CLASSPATH=$CLASSPATH:$SERVER_HOME/conf
if [ -d "$SERVER_HOME/target/src/classes" ] ; then
	CLASSPATH=$SERVER_HOME/target/src/classes:$CLASSPATH
fi

export DISPLAY=:1
export CLASSPATH=$CLASSPATH

nohup ${JAVA_HOME}/bin/java com.bt.om.CreateTaobaoPid >${SERVER_HOME}/logs/catalina-pids.log 2>&1 &
