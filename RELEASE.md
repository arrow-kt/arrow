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
