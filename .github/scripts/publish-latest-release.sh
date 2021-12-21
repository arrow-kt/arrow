#!/bin/bash

# Reason: it's not possible to sync all the directories because of 'docs/0.10', 'docs/next', etc.
#         and latest release appears in the root directory

set -e

cd _site/
for file in *; do
    if [ "$file" != "index.html" ]; then
      if [ -f "$file" ]; then
          echo "Copying $file ..."
          aws s3 cp $file s3://$S3_BUCKET/docs/$file >> $BASEDIR/logs/aws_sync.log
          continue
      fi
      echo "Sync $file ..."
      aws s3 sync $file s3://$S3_BUCKET/docs/$file --delete >> $BASEDIR/logs/aws_sync.log
    fi
done
