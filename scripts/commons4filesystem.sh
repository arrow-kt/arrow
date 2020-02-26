#!/bin/bash

export OUTPUT_FILE=$BASEDIR/output.log
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
    echo ""
    printFileHeaderSeparator
    echo $1
    printFileHeaderSeparator
    echo ""
    cat $2
}

function showFiles()
{
    showFile "OUTPUT" $OUTPUT_FILE
    showFile "ERROR LOG" $ERRORLOG_FILE
    showFile "RESULT" $RESULT_FILE 
}

function saveResult()
{
    if [[ $1 -eq 0 ]]; then
        printf "%-20s\t%-20s\t%-20s\n" "$3" "$2" "OK" >> $RESULT_FILE
    else
        EXIT_CODE=$1
        printf "%-20s\t%-20s\t%-20s\n" "$3" "$2" "KO !!" >> $RESULT_FILE
    fi
}

function printHeader()
{
    echo ""
    printHeaderSeparator
    echo "$1 > $2"
    printHeaderSeparator
    echo ""
}

function addHeaders()
{
    printHeader "$1" "$2" >> $OUTPUT_FILE
    printHeader "$1" "$2" >> $ERRORLOG_FILE
}

function exitForResult()
{
    exit $EXIT_CODE
}

function runAndSaveResult()
{
    addHeaders "$1" "$2"
    $3 >> $OUTPUT_FILE 2>> $ERRORLOG_FILE
    saveResult $? "$1" "$2"
}
