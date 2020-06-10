#!/bin/bash
 
set -e

brew install hub
cd $BASEDIR/$ARROW_LIB

TAG_TIMESTAMP=$(git log $LATEST_PUBLISHED_VERSION --pretty="format:%ct" | head -1)
hub pr list --limit 500 --base master -s merged --format='%mt#%au#%t [%i](%U)%n' > PR-list-all.txt
while read line; do PR_TIMESTAMP=$(echo $line | cut -d# -f1); if [ $PR_TIMESTAMP -gt $TAG_TIMESTAMP ]; then echo $line; fi; done < PR-list-all.txt > PR-list.txt
cat PR-list.txt | cut -d# -f2 | sort | uniq > contributors.txt
while read contributor; do
    echo "### [@$contributor](https://github.com/$contributor)"
    grep "#${contributor}#" PR-list.txt | cut -d# -f3- | perl -pe 's/^/ - /'
done < contributors.txt > local-release-notes.txt

echo -e "Release $RELEASE_VERSION\n\n" > release-notes.txt
echo -e "$RELEASE_VERSION ($(date +%Y-%m-%d))\n---" >> release-notes.txt
cat local-release-notes.txt >> release-notes.txt

echo -e "\n--- RELEASE NOTES ---\n"
cat release-notes.txt
hub release create --copy -F release-notes.txt $RELEASE_VERSION
