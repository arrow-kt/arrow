# Release flow

## 0. Previous checks

* [No broken links in the website](CONTRIBUTING.md#how-to-test-links)
* Dependencies in `arrow-stack/build.gradle`:
  * The new modules are considered
  * The old modules don't appear

## 1. Create a pull request

Prepare a pull request with these changes:

1. Update versions in `arrow-libs/gradle.properties`. For instance, the release version will be `0.10.5` and the next SNAPSHOT version will be `0.11.0-SNAPSHOT`:
```
projects.version=0.11.0-SNAPSHOT
projects.latestVersion=0.10.5
```
2. Update versions in `README.md`

When merging that pull request, these things will happen automatically:

* New RELEASE version will be published for all the Arrow libraries into Sonatype staging repository.
* A tag will be created with the RELEASE version.
* Release notes will be created and associated to that tag.
* The website will be updated with a new RELEASE version (`doc` and `doc/major.minor` directories)

## 2. Close and release

Then, close and release the Sonatype repository to sync with Maven Central:

1. Login to https://oss.sonatype.org/ > `Staging repositories`
2. Check the content of the new staging repository
3. Select the staging repository and **Close** (it will check if the content meet the requirements)
4. Select the staging repository and **Release** to sync with Maven Central
5. **Drop** and repeat if there are issues.

NOTE: [This plugin](https://github.com/gradle-nexus/publish-plugin) provides tasks for closing and releasing the staging repositories. However, that plugin must be applied to the root project, and it would be necessary to discard modules for publication. Let's keep this note here to give it a try later on.

### About signing artifacts with GPG/PGP

One of the requirements for artifacts available in Central Maven is being signed with GPG/PGP.

These secrets are involved to meet that requirement:

* `ORG_GRADLE_PROJECT_SIGNINGKEY`: private key from [Generating a Key Pair](https://central.sonatype.org/publish/requirements/gpg/#generating-a-key-pair)
* `ORG_GRADLE_PROJECT_SIGNINGPASSWORD`: passphrase from [Generating a Key Pair](https://central.sonatype.org/publish/requirements/gpg/#generating-a-key-pair)

To verify artifacts during **Close** task, the public key must be distributed to a key server: [Distributing Your Public Key](https://central.sonatype.org/publish/requirements/gpg/#distributing-your-public-key).

## How to fix a released version

Context:

* Latest release has a bug and `main` branch already has other additional features.
* A released version has a bug, and it's not the latest release.

How to fix a `<major.minor.patch>` version in some of those contexts:

1. Create `release/<major.minor.(patch + 1)>` branch from tag `<major.minor.patch>`.
2. Apply the fix into the new branch:
   * Via pull request for new changes.
   * Directly for existing changes (cherry-pick).
3. Check that new `<major.minor.(patch + 1)-SNAPSHOT>` artifacts are deployed into [Sonatype OSS](https://oss.sonatype.org/service/local/repositories/snapshots/content/io/arrow-kt/) with the fixes.
4. Try the new `<major.minor.(patch + 1)-SNAPSHOT>` version.
5. Create a pull request into `main` branch if the fix must be applied to the new versions as well.
6. Create a pull request into `release/<major.minor.(patch + 1)>` branch to release the fix:
    * Change just `projects.latestVersion` in `arrow-libs/gradle.properties`.
    * Update the version in `README.md`.

What will happen when merging the last pull request?

* New Arrow libraries will be published with `<major.minor.(patch + 1)>` version into Sonatype staging repository.
* `<major.minor.(patch + 1)>` tag will be created.
* `doc/<major.minor>` directory in the website will be updated.

TODO: Release notes and GitHub release must be created manually.

Then, close and release the Sonatype repository to sync with Maven Central in the same way as other versions.
