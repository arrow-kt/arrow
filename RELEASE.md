## Release flow

Every **Λrrow** library publishes SNAPSHOT versions from its repository.

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

-- TODO: website
