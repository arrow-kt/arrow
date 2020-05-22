#!/bin/bash

echo -e "## Data\n"
echo -e "* **Commit hash**: \`$(git log -1 --pretty=format:%h)\`\n"
echo -e "* **Author**: $(git log -1 --pretty=format:%an)\n"
echo -e "* **More info**: [error log](https://github.com/${GITHUB_REPOSITORY}/actions/runs/${GITHUB_RUN_ID})\n"

if [ -f $BASEDIR/error.log ]; then
    echo -e "\n## Error\n\n"
    cat $BASEDIR/error.log
fi

if [ -f $BASEDIR/result.log ]; then
    echo -e "\n## Result\n\n"
    cat $BASEDIR/result.log
fi
