# Arrow library: How to build it

## Requirements

- JDK 8

## Steps

```bash
./gradlew build
```

## Other Gradle tasks

One of the drawbacks of the multi-repo is the ability to check changes that could impact on other libraries.

In order to overcome that situation, new Gradle tasks are provided for every repository:

* Build with local Arrow dependencies (tests execution included):
```bash
./gradlew buildWithLocalDeps
```
* Build libs and examples with local Arrow dependencies (tests execution included)
```bash
./gradlew buildAllWithLocalDeps
```

Those tasks will download the rest of the repositories automatically:

```
workspace/
├── arrow
├── arrow-core
├── ...
└── arrow-optics
```

In order to keep those repositories updated:

* Run git-pull for all the repositories:
```bash
./gradlew gitPullAll
```
* Run git-pull for the rest of the repositories:
```bash
./gradlew gitPullOthers
```
