#!/bin/bash

export ERRORLOG_FILE=$BASEDIR/error.log
export RESULT_FILE=$BASEDIR/result.log
export EXIT_CODE=0

function printFileHeaderSeparator()
{
    printf "%0.s*" {1..80}
    echo ""
}

function printHeaderSeparator()
{
    printf "%0.s-" {1..80}
    echo ""
}

function showFile()
{
    TITLE=$1
    LOGFILE=$2

    if [ -f $LOGFILE ]; then
        echo ""
        printFileHeaderSeparator
        echo $TITLE
        printFileHeaderSeparator
        echo ""
        cat $LOGFILE
    fi
}

function showFiles()
{
    showFile "ERROR LOG" $ERRORLOG_FILE
    showFile "RESULT" $RESULT_FILE 
}

function saveTmpFile()
{
    REPOSITORY=$1
    ACTION=$2
    LOGFILE=$3

    printHeader "$REPOSITORY" "$ACTION" >> $LOGFILE
    cat $LOGFILE.tmp >> $LOGFILE
}

function saveResult()
{
    PARTIAL_EXIT_CODE=$1
    REPOSITORY=$2
    ACTION=$3
    
    if [[ $PARTIAL_EXIT_CODE -eq 0 ]]; then
        printf "%-30s\t%-20s\t%-20s\n" "$ACTION" "$REPOSITORY" "OK" >> $RESULT_FILE
    else
        EXIT_CODE=$PARTIAL_EXIT_CODE
        saveTmpFile "$REPOSITORY" "$ACTION" $ERRORLOG_FILE
        printf "%-30s\t%-20s\t%-20s\n" "$ACTION" "$REPOSITORY" "KO !!" >> $RESULT_FILE
    fi
}

function printHeader()
{
    REPOSITORY=$1
    ACTION=$2

    echo ""
    printHeaderSeparator
    echo "$REPOSITORY > $ACTION"
    printHeaderSeparator
    echo ""
}

function exitForResult()
{
    exit $EXIT_CODE
}

function runAndSaveResult()
{
    REPOSITORY=$1
    ACTION=$2
    COMMANDLINE=$3
    
    $COMMANDLINE 2> $ERRORLOG_FILE.tmp
    saveResult $? "$REPOSITORY" "$ACTION"
}
