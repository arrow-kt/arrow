<a href="https://arrow-kt.io" title="Arrow website"><img src="https://arrow-kt.io/img/arrow-brand.svg" width="200" alt=""></a>

[![Maven Central](https://img.shields.io/maven-central/v/io.arrow-kt/arrow-core?color=4caf50&label=latest%20release)](https://maven-badges.herokuapp.com/maven-central/io.arrow-kt/arrow-core)
[![Kotlin version](https://img.shields.io/badge/Kotlin-1.8.10-blue)](https://kotlinlang.org/docs/whatsnew18.html)

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![StackOverflow](https://img.shields.io/badge/arrow--kt-grey.svg?logo=stackoverflow)](https://stackoverflow.com/questions/tagged/arrow-kt)
[![Twitter](https://img.shields.io/twitter/follow/arrow_kt?color=blue&style=flat)](https://twitter.com/arrow_kt)

Λrrow is a library for Typed Functional Programming in Kotlin.

Arrow aims to provide a [*lingua franca*](https://en.wikipedia.org/wiki/Lingua_franca) of interfaces
and abstractions across Kotlin libraries. For this, it includes the most popular data types such
as `Option`, and `Either`, functional operators such as `zipOrAccumulate`, and computation
blocks to empower users to write pure FP apps and libraries built atop higher order abstractions.

## [Documentation](http://arrow-kt.io)

- [Quickstart and setup](https://arrow-kt.io/learn/quickstart/)
- [Typed errors](https://arrow-kt.io/learn/typed-errors/)
- [Coroutines and resources](https://arrow-kt.io/learn/coroutines/)
- [Resilience](https://arrow-kt.io/learn/resilience/)
- [Immutable data](https://arrow-kt.io/learn/immutable-data/)
- [Collections and functions](https://arrow-kt.io/learn/collections-functions/)

The documentation is hosted in a [separate repository](https://github.com/arrow-kt/arrow-website).

## Arrow 2.0

⚠️ _**Every new API has been backported to Arrow 1.2**. We strongly discourage from using Arrow 2.0
at this point, except for testing purposes, and use Arrow 1.2 instead._

The next version of Arrow is [in active development](https://github.com/arrow-kt/arrow/pull/2778).
If you want to try it, you need to add the following repository in your build file:

```
maven("https://oss.sonatype.org/content/repositories/snapshots")
```

and depend on the `2.0.0-SNAPSHOT` version of the desired library.

## Join Us

Arrow is an inclusive community powered by awesome individuals like you. As an actively growing
ecosystem, Arrow and its associated libraries and toolsets are in need of new contributors! We have
issues suited for all levels, from entry to advanced, and our maintainers are happy to provide 1:1
mentoring. All are welcome in Arrow.

If you’re looking to contribute, have questions, or want to keep up-to-date about what’s happening,
please follow us here and say hello!

- [Arrow on Twitter](https://twitter.com/arrow_kt)
- [#arrow on Kotlin Slack](https://kotlinlang.slack.com/messages/C5UPMM0A0)
- [#arrow-contributors on Kotlin Slack](https://kotlinlang.slack.com/archives/C8UK6RTHU)
- [Arrow on Gitter](https://gitter.im/arrow-kt/Lobby)

Find more details in [CONTRIBUTING](CONTRIBUTING.md).

## License

    Copyright (C) 2017 The Λrrow Authors

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
