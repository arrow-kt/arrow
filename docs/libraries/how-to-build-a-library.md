# Arrow library: How to build it

## Requirements

- JDK 8

For [Arrow Fx](https://github.com/arrow-kt/arrow-fx):

- Android SDK
- `ANDROID_SDK_ROOT` environment variable
- If using Intellij IDEA 2020.x, disable bundled Android Plugin to avoid this error when loading the Gradle project:
> Cannot convert string value 'JETPACK_COMPOSE' to an enum value of type 'com.android.builder.model.AndroidGradlePluginProjectFlags$BooleanFlag' (valid case insensitive values: APPLICATION_R_CLASS_CONSTANT_IDS, TEST_R_CLASS_CONSTANT_IDS, TRANSITIVE_R_CLASS)

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
