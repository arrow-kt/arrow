Katz
====
[![Build Status](https://travis-ci.org/FineCinnamon/Katz.svg?branch=master)](https://travis-ci.org/FineCinnamon/Katz/)
[![Kotlin version badge](https://img.shields.io/badge/kotlin-1.1.3.dev.1450-blue.svg)](http://kotlinlang.org/)
[![Hex.pm](https://img.shields.io/hexpm/l/plug.svg)](http://www.apache.org/licenses/LICENSE-2.0)
[![codecov](https://codecov.io/gh/FineCinnamon/Katz/branch/master/graph/badge.svg)](https://codecov.io/gh/FineCinnamon/Katz)
[![version](https://jitpack.io/v/FineCinnamon/Katz.svg)](https://jitpack.io/#FineCinnamon/Katz/)

Functional Datatypes and abstractions for Kotlin inspired by [Cats][cats]

# How to contribute

If you are thinking about contributing to the project, you should read the following lines about the basics of the contribution philosophy we have:

* All the FP types use similar combinators. Some of them are capable of supporting just a subset of those operations, and other ones support a different subset. But in the end we have no more than a set of N given combinators that we can find in almost all the FP types.
* The intention of **Katz** this library is to create a straightforward and simple API.
* For the types we add, we should think about the operations we can find on `Semigroup`, `Monoid`, `Foldable`, `Traversable`, `Functor`, `Applicative`, `Monad`..., and provide them in the types that can support them.
* For those operations, you should always follow the same naming and form to kind off keep the standards.

This approach is going to give us the possibility to Learn that all the computation aspects can be resolved by identifying the abstractions and always using the same combinators, independently of the data type they are expressed on. In example:

* We will find `Functor.map` in `Option`, `List`, `Future`... and so on.
* We will find `Applicative.pure` on those types also.
* ... and the same way with all the combinators we are going to implement.

Since `Kotlin` does not support `HKTs` we are going to code all the combinators in each one of the types, but at the same time, that's a good practice to learn how the same operations can be applied to different types in functional programing. So you normally have:

* A data structure with an ADT representing it's concrete states.
* The structure is recursive, so you can iterate over it and use pattern matching over it's subtypes (ADT types) representing it's different concrete state implementations.
* This recursion can be expressed with a `fold`.
* That `fold` is the base for the rest of the combinators.

If you follow this pattern, you will see by yourself how functional programing libraries are implemented, and you are going to know how to do pure functional programing using immutable data structures.

## The need to not overoptimize using Kotlin

Once we have the pure functional base, we can start adding `Kotlin` optimizations over it using the language's syntatic sugar. But **we need to put attention on creating correct definitions over performance improvements or any type of early micro-optimization.**

Ideally, we would want to have property based tests where we would apply a series of norms over the base abstractions in each one of the types implemented.

# Add it to your project

Use it at your how risk, the actual state is not production ready.

Add it in your root `build.gradle` at the end of repositories.

```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
        maven { url 'https://kotlin.bintray.com/kotlinx' }
        maven { url "http://dl.bintray.com/kotlin/kotlin-dev" }
    }
}
```

Add the dependency into project `build.gradle`

```
dependencies {
    compile 'com.github.FineCinnamon:Katz:v0.2'
}
```

# License

    Copyright (C) 2017 The Katz Authors

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

