# Join Us

Arrow is an inclusive community powered by awesome individuals like you. As an actively growing ecosystem, Arrow and its associated libraries and toolsets are in need of new contributors! We have issues suited for all levels, from entry to advanced, and our maintainers are happy to provide 1:1 mentoring. All are welcome in Arrow.

If you’re looking to contribute, have questions, or want to keep up-to-date about what’s happening, please follow us here and say hello!

- [Arrow on Twitter](https://twitter.com/arrow_kt)
- [#arrow on Kotlin Slack](https://kotlinlang.slack.com/messages/C5UPMM0A0)
- [#arrow-contributors on Kotlin Slack](https://kotlinlang.slack.com/archives/C8UK6RTHU)
- [Arrow on Gitter](https://gitter.im/arrow-kt/Lobby)

# Contributing guidelines

## Contributing with source code

### Repositories

`arrow` repository is just an orchestrator for all the **Λrrow** libraries (configuration, global integration checks, etc). For instance, it includes these configuration files:

| File | Description | Comment |
| ---- | ----------- | ------- |
| [`gradle.properties`](https://github.com/arrow-kt/arrow/blob/master/gradle.properties) | Global properties | Every library loads these properties when starting a Gradle execution. |
| [`generic-conf.gradle`](https://github.com/arrow-kt/arrow/blob/master/generic-conf.gradle) | Global build configuration | Every library loads this configuration when starting a Gradle execution. **Note**: it shouldn't include particular configuration for a library. For instance, `arrow-benchmarks-fx` adds JitPack.io repository in its `build.gradle`. |
| [`subproject-conf.gradle`](https://github.com/arrow-kt/arrow/blob/master/subproject-conf.gradle) | Global sub-project build configuration | Every library loads this configuration when starting a Gradle sub-project execution. |
| [`doc-conf.gradle`](https://github.com/arrow-kt/arrow/blob/master/doc-conf.gradle) | Configuration to build and check the documentation | This file is loaded for those libraries that generate documentation. |
| [`publish-conf.gradle`](https://github.com/arrow-kt/arrow/blob/master/publish-conf.gradle) | Configuration to publish a library | This file is loaded for those libraries that must be published in artifact repositories. |

If these files are changed, a full check for all the libraries will be executed to approve the pull request.

You'll find the **Λrrow** source code in these repositories:

|   | Library | SSH | HTTPS |
| - | ------- | -------------- | ---------------- |
| <img src="https://github.com/arrow-kt/arrow-site/blob/master/docs/img/core/arrow-core-brand-sidebar.svg" alt="" width="50px"> | [Λrrow Ank](https://github.com/arrow-kt/arrow-ank) | `git@github.com:arrow-kt/arrow-ank.git` | `https://github.com/arrow-kt/arrow-ank.git` |
| <img src="https://github.com/arrow-kt/arrow-site/blob/master/docs/img/core/arrow-core-brand-sidebar.svg" alt="" width="50px"> | [Λrrow Core](https://github.com/arrow-kt/arrow-core) | `git@github.com:arrow-kt/arrow-core.git` | `https://github.com/arrow-kt/arrow-core.git` |
| <img src="https://github.com/arrow-kt/arrow-site/blob/master/docs/img/fx/arrow-fx-brand-sidebar.svg" alt="" width="50px"> | [Λrrow Fx](https://github.com/arrow-kt/arrow-fx) | `git@github.com:arrow-kt/arrow-fx.git` | `https://github.com/arrow-kt/arrow-fx.git` |
| <img src="https://github.com/arrow-kt/arrow-site/blob/master/docs/img/incubator/arrow-incubator-brand-sidebar.svg" alt="" width="50px"> | [Λrrow Incubator](https://github.com/arrow-kt/arrow-incubator) | `git@github.com:arrow-kt/arrow-incubator.git` | `https://github.com/arrow-kt/arrow-incubator.git` |
| <img src="https://github.com/arrow-kt/arrow-site/blob/master/docs/img/core/arrow-core-brand-sidebar.svg" alt="" width="50px"> | [Λrrow Integrations](https://github.com/arrow-kt/arrow-integrations) | `git@github.com:arrow-kt/arrow-integrations.git` | `https://github.com/arrow-kt/arrow-integrations.git` |
| <img src="https://github.com/arrow-kt/arrow-site/blob/master/docs/img/optics/arrow-optics-brand-sidebar.svg" alt="" width="50px"> | [Λrrow Optics](https://github.com/arrow-kt/arrow-optics) | `git@github.com:arrow-kt/arrow-optics.git` | `https://github.com/arrow-kt/arrow-optics.git` |
| <img src="https://github.com/arrow-kt/arrow-site/blob/master/docs/img/core/arrow-core-brand-sidebar.svg" alt="" width="50px"> | [Λrrow UI](https://github.com/arrow-kt/arrow-ui) | `git@github.com:arrow-kt/arrow-ui.git` | `https://github.com/arrow-kt/arrow-ui.git` |

With every pull request on those repositories, a new SNAPSHOT library will be published into [OSS repository](https://oss.jfrog.org/artifactory/oss-snapshot-local/io/arrow-kt/).

### Gradle tasks

One of the drawbacks of the multi-repo is the ability to check changes that could impact on other repositories.

In order to overcome that situation, new Gradle tasks are provided for every repository:

```
$> cd <arrow-library-repository>
$> ./gradlew tasks

...

------------------------------------------------------------
Tasks runnable from root project
------------------------------------------------------------

Arrow tasks
-----------
buildAllWithLocalDeps - Build libs and examples with local Arrow dependencies (tests execution included)
buildArrowDoc - Generate API doc and run validation
buildWithLocalDeps - Build with local Arrow dependencies (tests execution included)
installAllWithLocalDeps - Install all the artifacts using local Arrow dependencies (no tests execution)

Arrow (Git) tasks
-----------------
gitPullAll - Run git-pull for all the repositories
gitPullOthers - Run git-pull for the rest of the repositories
```

## Contributing with documentation

Would you like to help with the website on https://arrow-kt.io ?

Take a look at [README file in `arrow-site` repository](https://github.com/arrow-kt/arrow-site/) where you'll find all the details about it.

# Can't find what you're looking for?

Please, contact us at [#arrow on Kotlin Slack](https://kotlinlang.slack.com/messages/C5UPMM0A0) or [create an issue](https://github.com/arrow-kt/arrow/issues/new/choose).
