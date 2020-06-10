#!/bin/bash
 
set -e

brew install hub
echo -e "Release RELEASE_VERSION\n\n" > $BASEDIR/arrow/release-notes-all.txt
echo -e "RELEASE_VERSION ($(date +%Y-%m-%d))\n---\n" >> $BASEDIR/arrow/release-notes-all.txt
for lib in $(cat $BASEDIR/arrow/lists/libs.txt); do
    cd $BASEDIR/$lib
    TAG_TIMESTAMP=$(git log $LATEST_PUBLISHED_VERSION --pretty="format:%ct" | head -1)
    hub pr list --limit 500 --base master -s merged --format='%mt#%au#%t [%i](%U)%n' > PR-list-all.txt
    while read line; do PR_TIMESTAMP=$(echo $line | cut -d# -f1); if [ $PR_TIMESTAMP -gt $TAG_TIMESTAMP ]; then echo $line; fi; done < PR-list-all.txt > PR-list.txt
    cat PR-list.txt | cut -d# -f2 | sort | uniq > contributors.txt
    while read contributor; do 
        echo "### [@$contributor](https://github.com/$contributor)"
        grep "#${contributor}#" PR-list.txt | cut -d# -f3- | perl -pe 's/^/ - /'
    done < contributors.txt > local-release-notes.txt
    echo -e "\n## $lib\n" >> $BASEDIR/arrow/release-notes-all.txt
    cat local-release-notes.txt >> $BASEDIR/arrow/release-notes-all.txt
done

cd $BASEDIR/arrow
TAG_TIMESTAMP=$(git log $LATEST_PUBLISHED_VERSION --pretty="format:%ct" | head -1)
hub pr list --limit 500 --base master -s merged --format='%mt#%au#%t [%i](%U)%n' > PR-list-all.txt
while read line; do PR_TIMESTAMP=$(echo $line | cut -d# -f1); if [ $PR_TIMESTAMP -gt $TAG_TIMESTAMP ]; then echo $line; fi; done < PR-list-all.txt > PR-list.txt
cat PR-list.txt | cut -d# -f2 | sort | uniq > contributors.txt
while read contributor; do
    echo "### [@$contributor](https://github.com/$contributor)"
    grep "#${contributor}#" PR-list.txt | cut -d# -f3- | perl -pe 's/^/ - /'
done < contributors.txt > local-release-notes.txt

echo -e "\n## arrow\n" >> $BASEDIR/arrow/release-notes-all.txt
cat local-release-notes.txt >> $BASEDIR/arrow/release-notes-all.txt
echo -e "\n--- RELEASE NOTES ALL ---\n"
cat $BASEDIR/arrow/release-notes-all.txt
hub release create --copy -F release-notes-all.txt RELEASE_VERSION
