# Implementation plan

:heavy_check_mark: Repositories creation

:heavy_check_mark: Repositories > `Settings` > `Options`

:heavy_check_mark: Repositories > `Settings` > `Manage access`

:white_large_square: Repositories > `Settings` > `Secrets`: [How to create secrets](how-to-create-secrets.md)

:white_large_square: Check if there are new artifacts to consider.

:white_large_square: **Block the contributions!!**

:white_large_square: **arrow** repository > `Settings` > `Branches` > Update branch protection rules

:white_large_square: **arrow** repository: [PR to add new content + remove current checks](https://github.com/arrow-kt/arrow/pull/2066)

:white_large_square: New repositories > Extract and push content: [How to extract the content](how-to-extract-content.md); use [`extract-content.sh`](scripts/extract-content.sh)

:white_large_square: New repositories > Add the additional configuration and checks: [`copy-conf.sh`](scripts/copy-conf.sh) will add a branch in every repository to create the first pull request.

:white_large_square: New repositories > `Settings` > `Branches` > Create branch protection rules

:white_large_square: **Unblock the contributions!!**

:white_large_square: New repositories: add badges.

:white_large_square: New repositories > `Settings` > `Webhooks` - Analyze!

:white_large_square: **arrow** repository > `Settings` > `Secrets`: update secrets about S3 bucket

:white_large_square: **arrow** repository: move issues to the correspondent repositories

:white_large_square: New repositories > Add issues about KTLint and Animals Sniffer

:white_large_square: Check that everything is working OK

:white_large_square: **arrow** repository: PR to remove `modules` directory
