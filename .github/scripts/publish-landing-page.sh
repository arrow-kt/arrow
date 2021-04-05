#!/bin/bash

set -e

MAIN_CONTENT=("CNAME"  "code"  "css"  "error.html"  "fonts"  "img"  "index.html"  "js"  "redirects.json")

cd _site/
for file in ${MAIN_CONTENT[*]}; do
    if [ -f "$file" ]; then
        echo "Copying $file ..."
        # TODO: --dryrun
        aws s3 cp $file s3://$S3_BUCKET/$file --dryrun >> $BASEDIR/logs/aws_sync.log
        continue
    fi
    echo "Sync $file ..."
    # TODO: --dryrun
    aws s3 sync $file s3://$S3_BUCKET/$file --dryrun --delete >> $BASEDIR/logs/aws_sync.log
done
