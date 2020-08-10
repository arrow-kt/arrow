# Release flow

## Artifacts

Every **Λrrow** library publishes SNAPSHOT versions from its repository into [OSS](https://oss.jfrog.org/artifactory/oss-snapshot-local/io/arrow-kt/)

However, RELEASE versions must be published at the same time. So in order to publish a RELEASE version, it's necessary to prepare a pull request for `arrow` repository with these changes:

* Update versions in `gradle.properties`. For instance, the release version will be `0.10.5` and the next SNAPSHOT version will be `0.11.0-SNAPSHOT`:
```
VERSION_NAME=0.11.0-SNAPSHOT
LATEST_VERSION=0.10.5
```
* Update versions in `README.md`

When merging that pull request:

* New RELEASE version will be published into Bintray for all the Arrow libraries.
* A first SNAPSHOT version will be published into OSS for all the Arrow libraries (because they depend on SNAPSHOT versions for other Arrow libraries by default).
* A tag will be created for all the repositories.
* Release notes will be created and associated to the last tag.
* Versions in the QuickStart section of the website will be updated as well.

Then, it will be necessary to sync Bintray with Maven (pending task: automating it).

## Documentation

Every **Λrrow** library publishes the API Doc and some static documentation from its repository into the next version of the website:

* API Doc:
    * `https://arrow-kt.io/docs/next/apidocs/<arrow-module>/`
* Static documentation:
    * https://arrow-kt.io/docs/next/core/
    * https://arrow-kt.io/docs/next/fx/
    * https://arrow-kt.io/docs/next/optics/dsl/
    * https://arrow-kt.io/docs/next/aql/intro/

TODO: In order to publish a RELEASE version, ...

### How to fix the documentation for the latest release

1. Clone the repository to be fixed. For instance, `arrow-core`.
2. Create a branch from the latest release version:
```
git checkout -b <branch-name> <latest-release-version>
```
For instance:
```
git checkout -b fix-documentation 0.10.5
```
3. Update the documentation.
4. Check that everything is working as expected:
```
-/gradlew buildArrowDoc
```
5. Commit the changes.
6. Create a new annotated tag with the format `<latest-release-version>.<fix>`. For instance:
```
git tag -a 0.10.5.1 -m "Fix ..."
```
7. Push the tag
```
git push origin <tag>
```
8. TODO: waiting for https://github.com/arrow-kt/arrow-site/pull/60
9. Create the pull request to apply the fix on `master` branch as well.
