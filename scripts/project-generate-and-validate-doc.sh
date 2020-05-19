#!/bin/bash

set -e
$BASEDIR/arrow/scripts/project-assemble.sh $1
$BASEDIR/arrow/scripts/project-run-dokka.sh $1
$BASEDIR/arrow/scripts/project-run-ank.sh $1
$BASEDIR/arrow/scripts/project-locate-doc.sh $1
