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

`arrow` repository is just an orchestrator for all the **Λrrow** libraries (configuration, global integration checks, etc).

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

### Gradle tasks

One of the drawbacks of multi-repo is the ability to check changes that could impact on other repositories.

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
buildWithLocalDeps - Build with local Arrow dependencies (tests execution included)
buildAllWithLocalDeps - Build libs and examples with local Arrow dependencies (tests execution included)
installAllWithLocalDeps - Install all the artifacts using local Arrow dependencies (no tests execution)

Arrow (Git) tasks
-----------------
gitPullAll - Run git-pull for all the repositories
gitPullOthers - Run git-pull for the rest of the repositories
```

### Other tools

Find some scripts to download all the new repositories in [`utils`](utils/) directory.

## Contributing with documentation

-- TODO

* [Documentation](https://github.com/arrow-kt/arrow-site/)
