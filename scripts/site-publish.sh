#!/bin/bash

set -e
echo "Publish $1 in S3 ..."
for module in $BASEDIR/site/build/_site/apidocs/*; do
    echo "Sync with docs/next/docs/apidocs/$(basename $module)"
    aws s3 sync $module s3://$S3_BUCKET/docs/next/docs/apidocs/$(basename $module) > aws_sync_jekyll.log
done

if [[ "$1" == "arrow-docs" ]]; then
    for directory in $BASEDIR/site/build/_site/static/*; do
        for file in $directory/*; do
            echo "Sync with docs/next/docs/$(basename $file)"
            if [ -f "$file" ]; then
                echo "Copying $file ..."
                aws s3 cp $file s3://$S3_BUCKET/docs/next/docs/$(basename $file) >> aws_sync_jekyll.log
            else
                echo "Sync $file ..."
                aws s3 sync $file s3://$S3_BUCKET/docs/next/docs/$(basename $file) >> aws_sync_jekyll.log
            fi
        done
    done
fi
