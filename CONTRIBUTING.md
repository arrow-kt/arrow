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

### Steps

To build all the libraries (compilation + tests) and examples:

```bash
cd arrow-libs
./gradlew build
```

To build just CORE libraries, FX libraries, OPTICS libraries, etc. select the correspondent directory.

For instance, for CORE libraries:

```bash
cd arrow-libs/core
./gradlew build
```

## How to generate and validate the documentation

Dokka is responsible for generating documentation based on source code annotations. Ank is in charge of compiling and validating your doc snippets and deploying the proper binaries for those.

In order to generate the documentation and validate it:

```bash
cd arrow-libs
./gradlew dokka

cd ../arrow-site
./gradlew runAnk
```

### Doc snippets policies

Whenever you are documenting a new type (type class, data type, whatever) you'll wonder how to add code snippets to it. Please,
use the following priority check list:

#### 1. Snippets for public API docs

If the snippet is just docs for a public method of a type (as in arguments, return type, or how it should be used from call sites), that should be inlined in the Kdocs of that given method using Dokka. That's done under the actual type file. [Here you have a simple example for `Option` methods](https://github.com/arrow-kt/arrow/blob/11a65faa9eed23182994778fa0ce218b69bfc4ba/modules/core/arrow-core/src/main/kotlin/arrow/core/Option.kt#L14).

That will automatically inline the docs of each method into the docs of the given data type. This is intended to be used just for public APIs exposed to users of the library.

#### 2. Snippets for broader samples

If your snippet is showing examples on how to use the public APIs in a broader scenario (like describing FP patterns or similar), then you'll add those snippets to the described docs Markdown file.

For the mentioned cases, you should double-check which `Ank` modifiers you want to use for the snippets (`silent`, `replace`, or `outFile(<file>)`). You'll find more details about each one of those in [Ank docs](https://github.com/arrow-kt/arrow-ank). See some real examples [on this docs PR](https://github.com/arrow-kt/arrow/pull/1134/files).

Also note that you can make your Ank snippets **editable and runnable in the actual browser**, which is quite handy. Just add this `{: data-executable='true'}` before your Ank Kotlin snippet. That **must be** used as a norm for all the snippets except for the ones that just represent infrastructure for following snippets (where there's not much value on making them runnable).

## How to run the website in your local workspace

After generating and validating the documentation (previous step):

```sh
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

#### Requirements to change an existing feature

If you want to propose a fix, rename, move, etc. please, ensure that these checks pass:

* Required checks:
    * `arrow libraries: build`
    * `arrow libraries: build documentation`
* The approval by 2 maintainers of the Arrow Community.

#### Requirements to add a new feature

Please, ensure these points when adding a new feature:

* Include documentation via [Dokka](https://kotlinlang.org/docs/reference/kotlin-doc.html). Please, find examples in the existing code to follow the same pattern.
* [Use Ank to validate for code snippets](https://github.com/arrow-kt/arrow/blob/main/arrow-libs/ank/README.md)
* Include tests that cover the proper cases

When creating the pull request, ensure that these checks pass:

* Required automatic checks:
    * `arrow libraries: build`
    * `arrow libraries: build documentation`
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

The use of Gradle appears in several places: `arrow-libs`, `arrow-libs/core`, `arrow-stack`, etc.

However, links are being used so it's just necessary to upgrade Gradle in `arrow-libs` directory:

```
cd arrow-libs
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
