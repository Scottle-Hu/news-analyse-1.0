#!/bin/bash

source /etc/profile >& /dev/null

#参数不足，至少一个参数
if [ $# -lt 1 ];
then
    echo "USAGE: $0 <procedure> [<date>]"
    exit 1
fi

#程序参数
param=$1

#如果有日期参数
if [ $# -eq 2 ]; then
    param="$param $2"
fi

MAIN_CLASS_NAME="AnalyseApplication"

#检查是否已经启动
javaps=`ps -ef | grep 'news-analyse' | grep -v grep`

echo $javaps

if [ ! -z $javaps  ]; then
    echo "WARNING: application has already started!"
    exit 0
fi

#检查java环境变量
if [ -z $JAVA_HOME ];
then
    echo "ERROR: JAVA_HOME hasn't been set, please set JAVA_HOME."
    exit 1
fi

#判断是否package
if [ ! -f "target/news-analyse-1.0-1.0-SNAPSHOT.jar" ];
then
    echo "ERROR: Project hasn't been build, please use mvn package to build firstly."
    exit 1
fi

#jvm参数
JAVA_OPTS="-Xmx1G -Xms256M"

#后台启动
nohup $JAVA_HOME/bin/java $JAVA_OPTS -jar target/news-analyse-1.0-1.0-SNAPSHOT.jar $param 2>&1 &

echo "starting..."

#获取pid
javaps=`ps -ef | grep 'news-analyse' | grep -v grep`

if [ -z $javaps  ]; then
    echo "ERROR: failed!"
else
    pid=`echo $javaps | awk '{print $2}'`
    echo "[pid=$pid]"
    echo "STARTED!"
fi

exit 0
