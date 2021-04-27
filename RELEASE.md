# Release flow

Prepare a pull request for `arrow` repository with these changes:

0. Previous checks (until they are automated):
  * No broken links in the website
  * Dependencies in `arrow-stack/build.gradle`
1. Update versions in `arrow-libs/gradle.properties`. For instance, the release version will be `0.10.5` and the next SNAPSHOT version will be `0.11.0-SNAPSHOT`:
```
VERSION_NAME=0.11.0-SNAPSHOT
LATEST_VERSION=0.10.5
```
2. Update versions in `README.md`
3. Update versions in [the QuickStart section of the website](arrow-site/docs/docs/quickstart/README.md).
4. Update versions in [the sidebar](arrow-site/docs/_data/doc-versions.yml).

When merging that pull request:

* New RELEASE version will be published for all the Arrow libraries into Sonatype staging repository.
* A tag will be created with the RELEASE version.
* Release notes will be created and associated to that tag.
* The website will be updated with a new RELEASE version (`doc` and `doc/major.minor` directories)

Then, it will be necessary to close and release the Sonatype repository to sync with Maven Central:

1. Login to https://oss.sonatype.org/ > `Staging repositories`
3. Check the content and then: **Close** (it will check if the content meet the requirements)
4. **Release** to sync with Maven Central (**Drop** and repeat if there are issues).

NOTE: [This plugin](https://github.com/gradle-nexus/publish-plugin) provides tasks for closing and releasing the staging repositories. However, that plugin must be applied to the root project and it would be necessary to discard modules for publication. Let's keep this note here to give it a try later on.

## Fixes from previous versions

When fixing `major.minor.patch` version:

1. Create `release/major.minor.(patch + 1)` branch from tag `major.minor.patch`.
2. Apply the fix into the new branch:
   * Via pull request for new changes.
   * Directly for existing changes (cherry-pick).
3. Check that new `major.minor.(patch + 1)-SNAPSHOT` artifacts are deployed into [Sonatype OSSRH](https://oss.sonatype.org/service/local/repositories/snapshots/content/io/arrow-kt/) with the fixes.
4. Try the new `major.minor.(patch + 1)-SNAPSHOT` version.
5. Create a pull request into `main` branch if the new changes must be applied to the new versions as well.
6. Create a pull request into `release/major.minor.(patch + 1)` branch:
    * Change `LATEST_VERSION` in `arrow-libs/gradle.properties`.
    * Update the version in `README.md`.
    * Update the version in [the QuickStart section of the website](arrow-site/docs/docs/quickstart/README.md).
    * Update [the sidebar](arrow-site/docs/_data/doc-versions.yml).

What will happen when merging the last pull request?

* New Arrow libraries will be published with `major.minor.(patch + 1)` version into Sonatype staging repository.
* `major.minor.(patch + 1)` tag will be created.
* `doc/major.minor` directory in the website will be updated.

TODO: Release notes and GitHub release must be created manually.

Then, it will be necessary to close and release the Sonatype repository to sync with Maven Central in the same way as other versions.

Last step:

* Update [the sidebar](arrow-site/docs/_data/doc-versions.yml) in the `main` branch to show the new latest version for `major.minor`.
