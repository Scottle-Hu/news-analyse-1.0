#!/bin/bash

MAIN_CLASS_NAME="AnalyseApplication"

javaps=`ps -ef | grep 'news-analyse' | grep -v grep`

if [  -z $javaps ]; then
    echo "WARNING: application is not running!"
    exit 0
fi

echo "stopping..."

pid=`echo $javaps | awk '{print $2}'`

kill -9 $pid

javaps=`ps -ef | grep 'news-analyse' | grep -v grep`

if [  -z $javaps ]; then
    echo "STOPPED!"
else
    echo "ERROR: can not stop application!"
    exit 1
fi

exit 0
