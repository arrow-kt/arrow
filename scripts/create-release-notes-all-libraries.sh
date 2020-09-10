#!/bin/bash
 
set -e

echo -e "Release $RELEASE_VERSION\n\n" > $BASEDIR/arrow/release-notes-all.txt
echo -e "$RELEASE_VERSION ($(date +%Y-%m-%d))\n---\n" >> $BASEDIR/arrow/release-notes-all.txt
for lib in $(cat $BASEDIR/arrow/lists/libs.txt); do
    cd $BASEDIR/$lib
    TAG_TIMESTAMP=$(git log $LATEST_PUBLISHED_VERSION --pretty="format:%ct" | head -1)
    hub pr list --limit 500 --base master -s merged --format='%mt#%au#[%i](%U) %t%n' > PR-list-all.txt
    while read line; do 
        pr_timestamp=$(echo $line | cut -d# -f1)
        if [ $pr_timestamp -gt $TAG_TIMESTAMP ]; then
            contribution=$(echo $line | cut -d# -f3- | perl -pe 's/^/ - /')
            contributor=$(echo $line | cut -d# -f2)
            echo "$contribution - [@$contributor](https://github.com/$contributor)"
        fi 
    done < PR-list-all.txt > local-release-notes.txt
    echo -e "\n## $lib\n" >> $BASEDIR/arrow/release-notes-all.txt
    cat local-release-notes.txt >> $BASEDIR/arrow/release-notes-all.txt
done

cd $BASEDIR/arrow
TAG_TIMESTAMP=$(git log $LATEST_PUBLISHED_VERSION --pretty="format:%ct" | head -1)
hub pr list --limit 500 --base master -s merged --format='%mt#%au#[%i](%U) %t%n' > PR-list-all.txt
while read line; do 
    pr_timestamp=$(echo $line | cut -d# -f1)
    if [ $pr_timestamp -gt $TAG_TIMESTAMP ]; then
        contribution=$(echo $line | cut -d# -f3- | perl -pe 's/^/ - /')
        contributor=$(echo $line | cut -d# -f2)
        echo "$contribution - [@$contributor](https://github.com/$contributor)"
    fi 
done < PR-list-all.txt > local-release-notes.txt

echo -e "\n## arrow\n" >> $BASEDIR/arrow/release-notes-all.txt
cat local-release-notes.txt >> $BASEDIR/arrow/release-notes-all.txt
echo -e "\n--- RELEASE NOTES ALL ---\n"
cat $BASEDIR/arrow/release-notes-all.txt
hub release create --copy -F release-notes-all.txt RELEASE_VERSION
