# Release flow

Prepare a pull request for `arrow` repository with these changes:

0. Update `arrow-stack/build.gradle` when required
1. Update versions in `arrow-libs/gradle.properties`. For instance, the release version will be `0.10.5` and the next SNAPSHOT version will be `0.11.0-SNAPSHOT`:
```
VERSION_NAME=0.11.0-SNAPSHOT
LATEST_VERSION=0.10.5
```
2. Update versions in `README.md`
3. Update versions in [the QuickStart section of the website](arrow-docs/docs/quickstart/setup/README.md).
4. Update versions in [the sidebar](arrow-site/docs/_data/doc-versions.yml).

When merging that pull request:

* New RELEASE version will be published into Bintray for all the Arrow libraries.
* A tag will be created with the RELEASE version.
* Release notes will be created and associated to that tag.
* The website will be updated with a new RELEASE version (`doc` and `doc/major.minor` directories)

Then, it will be necessary to sync Bintray with Maven.
