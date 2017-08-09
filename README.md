KΛTEGORY
========
[![Build Status](https://travis-ci.org/kategory/kategory.svg?branch=master)](https://travis-ci.org/kategory/kategory/)
[![Kotlin version badge](https://img.shields.io/badge/kotlin-1.1.3.dev.1450-blue.svg)](http://kotlinlang.org/)
[![Hex.pm](https://img.shields.io/hexpm/l/plug.svg)](http://www.apache.org/licenses/LICENSE-2.0)
[![codecov](https://codecov.io/gh/kategory/kategory/branch/master/graph/badge.svg)](https://codecov.io/gh/kategory/kategory)
[![version](https://jitpack.io/v/kategory/kategory.svg)](https://jitpack.io/#kategory/kategory/)

Functional Datatypes and abstractions for Kotlin inspired by [Cats][cats]

<img height="100" src="https://avatars2.githubusercontent.com/u/29458023?v=4&amp;s=200" width="100">

# Add it to your project

Use it at your how risk, the actual state is not production ready.

Add it in your root `build.gradle` at the end of repositories.

```groovy
allprojects {
    repositories {
        jcenter()
        maven { url 'https://kotlin.bintray.com/kotlinx' }
        maven { url "http://dl.bintray.com/kotlin/kotlin-dev" }
    }
}
```

Add the dependency into project `build.gradle`

```groovy
dependencies {
    compile 'io.kategory:kategory:0.3.4'
}
```

# License

    Copyright (C) 2017 The Kategory Authors

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

[cats]: https://github.com/typelevel/cats

