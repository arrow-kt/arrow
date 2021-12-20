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

In order to generate the documentation and validate it:

```bash
./gradlew buildDoc
```

### Doc snippets policies

Whenever you are documenting a new type (type class, data type, whatever) you'll wonder how to add code snippets to it. Please,
use the following priority check list:

#### 1. Snippets for public API docs

If the snippet is just docs for a public method of a type (as in arguments, return type, or how it should be used from call sites), that should be inlined in the Kdocs of that given method using Dokka. That's done under the actual type file. [Here you have a simple example for `Option` methods](https://github.com/arrow-kt/arrow/blob/11a65faa9eed23182994778fa0ce218b69bfc4ba/modules/core/arrow-core/src/main/kotlin/arrow/core/Option.kt#L14).

That will automatically inline the docs of each method into the docs of the given data type. This is intended to be used just for public APIs exposed to users of the library.

All public classes, functions, and properties must include public docs in Arrow before merging into `main`.

Public docs in Arrow follow a particular structure that ensures users have a similar experience when browsing the arrow website and documentation.

Declarations including classes, functions, and others must include docs in the following structure:

All Kdocs should include a short header that describes what the data type or function is for and a triple backticks ``` fenced block demonstrating its use

for example

```kotlin
/**
 * [Refined] is an Abstract class providing predicate validation in refined types companions.
 *
 * The example below shows a refined type `Positive` that ensures [Int] is > than 0.
 *
 * ```kotlin
 * import arrow.refinement.Refined
 * import arrow.refinement.ensure
 *
 * @JvmInline
 * value class Positive /* private constructor */ (val value: Int) {
 *  companion object : Refined<Int, Positive>(::Positive, {
 *    ensure((it > 0) to "$it should be > 0")
 *  })
 * }
 * ```
 */
abstract class Refined<A, out B>
```

#### 2. Snippets for broader samples

If your snippet is showing examples on how to use the public APIs in a broader scenario (like describing FP patterns or similar), then you'll add those snippets to the described docs Markdown file.

## How to run the website in your local workspace

```sh
./gradlew :arrow-site:buildSite
```

That Gradle task is equivalent to run Dokka and Jekyll build:

```bash
./gradlew dokkaGfm
cd arrow-site
bundle install --gemfile Gemfile --path vendor/bundle
bundle exec jekyll serve -s build/site
```

This will install any needed dependencies locally, and will use it to launch the complete website in [127.0.0.1:4000](http://127.0.0.1:4000) so you can open it with a standard browser.

If you get an error while installing the Ruby gem _http_parser_, check if the path to your Arrow directory contains spaces. According to this [issue](https://github.com/tmm1/http_parser.rb/issues/47), the installation with spaces in the path is currently not working.

### How to test links

Test for broken links in documentation using

```sh
wget --spider -r -nd -nv -l 5 http://127.0.0.1:4000
```

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

* Required tasks:
```bash
./gradlew spotlessApply # Format code
./gradlew apiDump # Generate .api files for binary compatibility review
./gradlew build
./gradlew buildDoc
```
* The approval by 2 maintainers of the Arrow Community.

#### Requirements to add a new feature

Please, ensure these points when adding a new feature:

* Include documentation via [Dokka](https://kotlinlang.org/docs/reference/kotlin-doc.html). Please, find examples in the existing code to follow the same pattern.
* Include tests that cover the proper cases

When creating the pull request, please execute these required tasks and make sure they pass:

* Required tasks:
```bash
./gradlew spotlessApply # Format code
./gradlew apiDump # Generate .api files for binary compatibility review
./gradlew build
./gradlew buildDoc
```
* The approval by 2 maintainers of the Arrow Community.

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
  - Add `<module>/build.gradle`
  - Update `settings.xml`
- Website:
  - Update [sidebar files](arrow-site/docs/_data)
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
