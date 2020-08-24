# How it works

**arrow** repository orchestrates all the Λrrow libraries.

## Configuration

| File | Description | Comment |
| ---- | ----------- | ------- |
| [`gradle.properties`](/gradle.properties) | Global properties | Every library loads these properties when starting a Gradle execution. |
| [`gradle/main.gradle`](/gradle/main.gradle) | Global build configuration | Every library loads this configuration when starting a Gradle execution. **Note**: it shouldn't include particular configuration for a library. For instance, `arrow-benchmarks-fx` adds JitPack.io repository in its `build.gradle`. |
| [`gradle/subproject.gradle`](/gradle/subproject.gradle) | Global sub-project build configuration | Every library loads this configuration when starting a Gradle sub-project execution. |
| [`gradle/apidoc-creation.gradle`](/gradle/apidoc-creation.gradle) | Configuration to build the API documentation | This file is loaded for those libraries that generate documentation. |
| [`gradle/doc-validation.gradle`](/gradle/doc-validation.gradle) | Configuration to check the documentation | This file is loaded by the `arrow-docs` modules. |
| [`gradle/publication.gradle`](/gradle/publication.gradle) | Configuration to publish a library | This file is loaded for those libraries that must be published in artifact repositories. |

If these files are changed, a full check for all the libraries will be executed to approve the pull request.

## Build order

It's necessary to keep a [build order](/lists/libs.txt) according to the dependencies among libraries.

That order is used when doing full checks for all the Λrrow libraries. In those checks, external Λrrow dependencies for a library come from the **local** repository.

If this file is changed, a full check for all the libraries will be executed to approve the pull request.

## Checks

Every library has several checks that just call the commands included in the [`scripts`](/scripts) directory.

If these files are changed, a full check for all the libraries will be executed to approve the pull request.

Find details about these checks in [this guideline to propose improvements](/docs/libraries/how-to-propose-an-improvement.md).

## Release

Take a look at [RELEASE guideline](/docs/how-to-release-arrow.md). 
