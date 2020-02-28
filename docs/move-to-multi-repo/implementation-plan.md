# Implementation plan

:heavy_check_mark: Repositories creation

:heavy_check_mark: Repositories > `Settings` > `Options`

:heavy_check_mark: Repositories > `Settings` > `Manage access`

:heavy_check_mark: Repositories > `Settings` > `Secrets`: [How to create secrets](how-to-create-secrets.md)

* :heavy_check_mark: `AWS_ACCESS_KEY_ID`
* :heavy_check_mark: `AWS_CLOUDFRONT_ID`
* :heavy_check_mark: `AWS_SECRET_ACCESS_KEY`
* :heavy_check_mark: `S3_BUCKET`
* :heavy_check_mark: `BINTRAY_API_KEY`
* :heavy_check_mark: `BINTRAY_USER`
* :heavy_check_mark: `BOT_GITHUB_TOKEN`

:heavy_check_mark: Adapt paths to the new S3 organization (pending of receiving the new organization).

:heavy_check_mark: Check if there are new artifacts to consider.

:heavy_check_mark: **Block the contributions!!**

:heavy_check_mark: **arrow** repository > `Settings` > `Branches` > Update branch protection rules

:heavy_check_mark: **arrow** repository: [PR to add new content + remove current checks](https://github.com/arrow-kt/arrow/pull/2066)

:heavy_check_mark: **arrow** repository: [PR to update the documentation](https://github.com/arrow-kt/arrow/pull/2079)

:heavy_check_mark: New repositories > Extract and push content on `master` branch: [How to extract the content](how-to-extract-content.md); use [`extract-content.sh`](scripts/extract-content.sh)

:heavy_check_mark: New repositories > Add the additional configuration and checks: [`copy-conf.sh`](scripts/copy-conf.sh) will add 2 branches in every repository to create the first pull requests. First pull request with `new-conf` branch. Then, a second pull request with `global-checks` branch (except for `arrow-site`).

:heavy_check_mark: New repositories > `Settings` > `Branches` > Create branch protection rules

:heavy_check_mark: **Unblock the contributions!!**

:white_large_square: **arrow** repository: move issues to the correspondent repositories

:heavy_check_mark: New repositories > Add issues about KTLint (xxenabled=false):
* `arrow-fx/arrow-fx-kotlinx-coroutines/build.gradle`
* `arrow-fx/build.gradle`
* `arrow-ank/build.gradle`

:white_large_square: New repositories: add badges.

:heavy_check_mark: New repositories > `Settings` > `Webhooks` - Analyze!

:white_large_square: Change documentation to the new S3

:white_large_square: **arrow** repository > `Settings` > `Secrets`: update secrets about S3 bucket (it will be used for release flow)

:white_large_square: Check that everything is working OK

:heavy_check_mark: **arrow** repository: PR to remove `modules` directory
