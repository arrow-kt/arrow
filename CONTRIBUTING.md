# Join Us

Arrow is an inclusive community powered by awesome individuals like you. As an actively growing ecosystem, Arrow and its associated libraries and toolsets are in need of new contributors! We have issues suited for all levels, from entry to advanced, and our maintainers are happy to provide 1:1 mentoring. All are welcome in Arrow.

If you’re looking to contribute, have questions, or want to keep up-to-date about what’s happening, please follow us here and say hello!

- [Arrow on Twitter](https://twitter.com/arrow_kt)
- [#arrow on Kotlin Slack](https://kotlinlang.slack.com/messages/C5UPMM0A0)
- [#arrow-contributors on Kotlin Slack](https://kotlinlang.slack.com/archives/C8UK6RTHU)
- [Arrow on Gitter](https://gitter.im/arrow-kt/Lobby)

# How-tos

In this page you'll find these guidelines for contributions:

- [How to build the libraries](#how-to-build-the-libraries)
- [How to generate and validate the documentation](#how-to-generate-and-validate-the-documentation)
- [How to run the website in your local workspace](#how-to-run-the-website-in-your-local-workspace)
- [How to propose an improvement](#how-to-propose-an-improvement)
- [Notes](#notes)
   - [How to upgrade Gradle](#how-to-upgrade-gradle)
   - [How to add a new module](#how-to-add-a-new-module)
   - [Gradle dependency configurations](#gradle-dependency-configurations)

Can't find what you're looking for? Please, contact us at [#arrow on Kotlin Slack](https://kotlinlang.slack.com/messages/C5UPMM0A0) or [create an issue](https://github.com/arrow-kt/arrow/issues/new/choose).

## How to build the libraries

### Requirements

- JDK 8

### Building the whole project

To build all the libraries (compilation + tests) and examples run in the project root (`arrow` directory):

```bash
./gradlew build
```

### Building a single library

To build just CORE library, FX library, OPTICS library etc.:

1. Find the Gradle subproject name by running

```bash
./gradlew projects
```

E.g. for the CORE subproject you will get
```
Root project 'arrow'
...
+--- Project ':arrow-core'
```

2.  Append `:build` after the subproject name and run this task. See also Gradle documentation [how to run subproject tasks.](https://docs.gradle.org/current/userguide/intro_multi_project_builds.html#sec:executing_tasks_by_fully_qualified_name)

```bash
./gradlew :arrow-core:build
```

## How to generate and validate the documentation

Dokka is responsible for generating documentation based on source code annotations. [Knit](https://github.com/Kotlin/kotlinx-knit) is in charge of compiling and validating your doc snippets and deploying the proper binaries for those.

The `build` task runs `knitCheck` to check if all Knit annotated code snippets in KDoc comments have been generated as examples. Knit code snippet annotations look like HTML comments inside KDoc:
```kotlin
/**
 * ```kotlin
 * // Code example goes here
 * ```
 * <!--- KNIT example-arrow-core-01.kt -->
 */
```

If you added/changed any Knit annotated code snippets in the docs you have to run the `knit` task to (re-)generate the examples, otherwise your build will fail:

```bash
./gradlew knit
```

In order to generate the documentation and validate it:

```bash
./gradlew buildDoc
```

### Doc snippets policies

Whenever you are documenting a new type (type class, data type, whatever) you'll wonder how to add code snippets to it. Please,
use the following priority check list:

#### 1. Snippets for public API docs

If the snippet is just docs for a public method of a type (as in arguments, return type, or how it should be used from call sites), that should be inlined in the Kdocs of that given method using Dokka and annotated with Knit (see above for more details). That's done under the actual type file. Here you have [a simple example for `Option` type](https://github.com/arrow-kt/arrow/blob/8659228f06bb44b2ea42d18b97d3dc0bdf424763/arrow-libs/core/arrow-core/src/commonMain/kotlin/arrow/core/Option.kt#L19).

That will automatically inline the docs of each method into the docs of the given data type. This is intended to be used just for public APIs exposed to users of the library.

All public classes, functions, and properties must include public docs in Arrow before merging into `main`.

Public docs in Arrow follow a particular structure that ensures users have a similar experience when browsing the arrow website and documentation.

Declarations including classes, functions, and others must include docs in the following structure:

All Kdocs should include a short header that describes what the data type or function is for and a triple backticks ``` fenced block demonstrating its use (ending with an appropriate Knit annotation)

for example

```kotlin
/**
 * (...)
 * 
 * `Option<A>` is a container for an optional value of type `A`. If the value of type `A` is present, the `Option<A>` is an instance of `Some<A>`, containing the present value of type `A`. If the value is absent, the `Option<A>` is the object `None`.
 *
 * ```kotlin
 * import arrow.core.Option
 * import arrow.core.Some
 * import arrow.core.none
 *
 * //sampleStart
 * val someValue: Option<String> = Some("I am wrapped in something")
 * val emptyValue: Option<String> = none()
 * //sampleEnd
 * fun main() {
 *  println("value = $someValue")
 *  println("emptyValue = $emptyValue")
 * }
 * ```
 * <!--- KNIT example-option-01.kt -->
 *  
 * (...)
 */ 
public sealed class Option<out A> {
```

#### 2. Snippets for broader samples

If your snippet is showing examples on how to use the public APIs in a broader scenario (like describing FP patterns or similar), then you'll add those snippets to the described docs Markdown file.

## How to propose an improvement 

If it's the first time you contribute with a GitHub repository, take a look at [Collaborating with issues and pull requests](https://docs.github.com/en/github/collaborating-with-issues-and-pull-requests).

### How to create an issue

Please, follow the link to [create an issue](https://github.com/arrow-kt/arrow/issues/new/choose).

### How to create a pull request

The easiest way to contribute to Arrow is to create a branch from a fork, and then create a PR on Github from your branch.

Arrow is a large project that uses several tools to verify that the code is formatted consistently, and that we don't break downstream projects that rely on Arrow's API across versions. 

For code formatting we use [Spotless](https://github.com/diffplug/spotless/tree/main/plugin-gradle) with [KtFmt](https://github.com/facebookincubator/ktfmt) and for API binary compatibility we use [Binary Compatibility Validator](https://github.com/Kotlin/binary-compatibility-validator). They need to run before you commit and push your code to Github.

If you've included those changes for binary compatibility and formatted the code correctly it's time to open your PR and get your contribution into Arrow. Thanks ahead of time for your effort and contributions 🙏

#### Requirements to change an existing feature

If you want to propose a fix, rename, move etc., please execute these required tasks and make sure they pass:

* If you changed/added [Knit](https://github.com/Kotlin/kotlinx-knit) annotated code snippets in KDocs:

```bash
./gradlew knit # (Re-)generate code examples from snippets in docs
```

* Required tasks:
```bash
./gradlew spotlessApply # Format code
./gradlew build
./gradlew buildDoc
```

Note: if, when running `build`, you see the following error:

```bash
> Task :arrow-core:apiCheck FAILED
```

This means you have changed (advertently or not) some public API. In this case read in the next point below how to resolve this.

* The approval by 2 maintainers of the Arrow Community.

#### Requirements to add a new feature

Please, ensure these points when adding a new feature:

* Include documentation via [Dokka](https://kotlinlang.org/docs/reference/kotlin-doc.html). Please, find examples in the existing code to follow the same pattern.
* Include tests that cover the proper cases

When creating the pull request, please execute these required tasks and make sure they pass:

* If you changed/added [Knit](https://github.com/Kotlin/kotlinx-knit) annotated code snippets in KDocs:

```bash
./gradlew knit # (Re-)generate code examples from snippets in docs
```

* Required tasks:
```bash
./gradlew spotlessApply # Format code
./gradlew build
./gradlew buildDoc
```

Note: as part of the `build` task `apiCheck` is run. If you have added/changed any public APIs, this task will fail with a message like this one:

```bash
> Task :arrow-core:apiCheck FAILED

FAILURE: Build failed with an exception.

* What went wrong:
Execution failed for task ':arrow-core:apiCheck'.
> API check failed for project arrow-core.
  --- /Users/john/projects/arrow/arrow-libs/core/arrow-core/api/arrow-core-retrofit.api
  +++ /Users/john/projects/arrow/arrow-libs/core/arrow-core/build/api/arrow-core-retrofit.api
 ```

To make the check pass you need to run:

```bash
./gradlew apiDump
```

This will generate updated `.api` files which you can then manually review (if the API changes are the ones you intended) and commit and push for the Arrow maintainers to review as well.

* The approval by 2 maintainers of the Arrow Community is required as well.

#### How to download the tests report

Both successful or failed build checks allow to download the tests report to review it:

![how-to-download-tests-report](img/doc/download-report.png)

#### What happens when merging a pull request

When merging the pull request, a new SNAPSHOT library will be published into [Sonatype OSSRH](https://oss.sonatype.org/service/local/repositories/snapshots/content/io/arrow-kt/).

On the other hand, the documentation for the next version (SNAPSHOT) will be updated:

* [Arrow Core](https://arrow-kt.io/docs/next/core/)
* [Arrow Fx](https://arrow-kt.io/docs/next/fx/)
* [Arrow Optics](https://arrow-kt.io/docs/next/optics/dsl/)

If any of these actions fails, an issue will be created to be solved as soon as possible.

## Notes

### How to upgrade Gradle

The use of Gradle appears in several subprojects: `arrow-core`, `arrow-stack`, etc.

However, links are being used so it's just necessary to upgrade Gradle in the project root directory:

```
./gradlew wrapper --gradle-version <new-version>
```

### How to add a new module

This short guideline provides all the things to keep in mind when adding a new module:

- Configuration:
  - Add `<module>/gradle.properties`
  - Add `<module>/build.gradle.kts`
  - Update `settings.gradle.kts`
- Utilities:
  - Update BOM file: [build.gradle](arrow-stack/build.gradle)
  
### Gradle dependency configurations

| Configuration | Use | Note |
| ------------- | --- | ---- |
| `api` | compilation | exported to consumers for compilation |
| `implementation` | compilation + runtime | exported to consumers for runtime | 
| `compileOnly` | just compilation | not exported to consumers | 
| `runtimeOnly` | just runtime | exported to consumers for runtime | 
| `testImplementation` | test compilation + test runtime |  | 
| `testCompileOnly` | test compilation |  | 
| `testRuntimeOnly` | test runtime |  |
