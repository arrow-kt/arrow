#!/bin/bash

set -e
MAIN_CONTENT=(CNAME  code  css  error.html  fonts  img  index.html  js  redirects.json)


echo "Publish in S3 ..."
cd $BASEDIR/site/build/_site

for file in ${MAIN_CONTENT[*]}; do
    rm -rf $file
done

for file in *; do
    if [ -f "$file" ]; then
        echo "Copying $file into docs/next/$file ..."
        aws s3 cp $file s3://$S3_BUCKET/docs/next/$file
    else
        if [ "$file" == "apidocs" ]; then
            for module in apidocs/*; do
                echo "Sync $module with docs/next/apidocs/$(basename $module) ..."
                aws s3 sync $module s3://$S3_BUCKET/docs/next/apidocs/$(basename $module)
            done
        else
            echo "Sync $file with docs/next/$file ..."
            aws s3 sync $file s3://$S3_BUCKET/docs/next/$file
        fi
    fi
done

aws cloudfront create-invalidation --distribution-id $AWS_CLOUDFRONT_ID --paths "/docs/next/*"
