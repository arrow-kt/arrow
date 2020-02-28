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
    if [ -f $2 ]; then
        echo ""
        printFileHeaderSeparator
        echo $1
        printFileHeaderSeparator
        echo ""
        cat $2
    fi
}

function showFiles()
{
    showFile "OUTPUT" $OUTPUT_FILE
    showFile "ERROR LOG" $ERRORLOG_FILE
    showFile "RESULT" $RESULT_FILE 
}

function saveTmpFile()
{
    printHeader "$1" "$2" >> $3
    cat $3.tmp >> $3
}

function saveResult()
{
    saveTmpFile "$2" "$3" $OUTPUT_FILE
    if [[ $1 -eq 0 ]]; then
        printf "%-30s\t%-20s\t%-20s\n" "$3" "$2" "OK" >> $RESULT_FILE
    else
        EXIT_CODE=$1
        saveTmpFile "$2" "$3" $ERRORLOG_FILE
        printf "%-30s\t%-20s\t%-20s\n" "$3" "$2" "KO !!" >> $RESULT_FILE
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

function exitForResult()
{
    exit $EXIT_CODE
}

function runAndSaveResult()
{
    $3 > $OUTPUT_FILE.tmp 2> $ERRORLOG_FILE.tmp
    saveResult $? "$1" "$2"
}
