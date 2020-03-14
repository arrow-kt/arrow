#!/bin/bash

echo -e "## Details\n"
echo -e "* **Commit hash**: \`$(git log -1 --pretty=format:%h)\`\n"
echo -e "* **Author**: $(git log -1 --pretty=format:%an)\n"
echo -e "* **More info**: [error log](https://github.com/arrow-kt/arrow-optics/commit/$GITHUB_SHA/checks)\n"
