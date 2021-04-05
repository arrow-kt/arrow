#!/bin/bash
 
set -e

TAG_TIMESTAMP=$(git log $LATEST_PUBLISHED_VERSION --pretty="format:%ct" | head -1)
hub pr list --limit 500 --base main -s merged --format='%mt#%au#[%i](%U) %t%n' > PR-list-all.txt
while read line; do 
    pr_timestamp=$(echo $line | cut -d# -f1)
    if [ $pr_timestamp -gt $TAG_TIMESTAMP ]; then
        contribution=$(echo $line | cut -d# -f3- | perl -pe 's/^/ - /')
        contributor=$(echo $line | cut -d# -f2)
        echo "$contribution - [@$contributor](https://github.com/$contributor)"
    fi 
done < PR-list-all.txt > local-release-notes.txt

echo -e "Release $RELEASE_VERSION\n\n" > release-notes.txt
echo -e "$RELEASE_VERSION ($(date +%Y-%m-%d))\n---" >> release-notes.txt
cat local-release-notes.txt >> release-notes.txt

echo -e "\n--- RELEASE NOTES ---\n"
cat release-notes.txt
hub release create --copy -F release-notes.txt $RELEASE_VERSION
