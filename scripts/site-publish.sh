#!/bin/bash

set -e
echo "Publish in S3 ..."

if [ -d $BASEDIR/site/build/_site/apidocs ]; then
    for module in $BASEDIR/site/build/_site/apidocs/*; do
        echo "Sync with docs/next/apidocs/$(basename $module)"
        aws s3 sync $module s3://$S3_BUCKET/docs/next/apidocs/$(basename $module) > aws_sync_jekyll.log
    done
fi

#    for file in $BASEDIR/site/build/_site/*; do
#        echo "Sync with docs/next/$(basename $file)"
#        if [ -f "$file" ]; then
#            echo "Copying $file ..."
#            aws s3 cp $file s3://$S3_BUCKET/docs/next/$(basename $file) >> aws_sync_jekyll.log
#        else
#            echo "Sync $file ..."
#            aws s3 sync $file s3://$S3_BUCKET/docs/next/$(basename $file) >> aws_sync_jekyll.log
#        fi
#    done
