funKTionale
===========

Functional constructs and patterns for [Kotlin](http://kotlin-lang.org)

## Modules

| Module | Description | Internal Dependencies | Size(KB) |
|---|---|---|---| 
|`funktionale-all`| GOTY edition. Every other module content is included but not Experimental module|N/A|1328|
|`funktionale-collections`|Collections related extensions such as `tail`, `prependTo` and others|N/A|4|
|`funktionale-complement`|Extension functions for predicates to generate complement functions|N/A|36|
|`funktionale-composition`| Extensions `andThen` (`forwardCompose`) and `compose` for functions|N/A|8|
|`funktionale-currying`|Extension `curried` and `uncurried` for functions|N/A|348|
|`funktionale-either`|Either and Disjuntion (right-biased Either) types|`funktionale-option`|44|
|`funktionale-experimental`|Playground and examples. **Not to be used on production**|`funktionale-all`|148|
|`funktionale-memoization`|Memoization for functions|N/A|112|
|`funktionale-option`|Option type|`funktionale-collections` and `funktionale-utils`|20|
|`funktionale-pairing`|Transformations for functions with arity 2 or 3 to one parameter of type `Pair` or `Triple` respectively |N/A|8|
|`funktionale-partials`|Partial applied functions|N/A|688|
|`funktionale-reverse`|Extension `reverse` for functions|N/A|32|
|`funktionale-try`|Try computation type|`funktionale-either`|12|
|`funktionale-utils`|`identity` and `constant` functions and Partial Functions |N/A|20|
|`funktionale-validation`|Validation types and functions with Disjunctions|`funktionale-either`|12|

## Documentation

Read the [Wiki](https://github.com/MarioAriasC/funKTionale/wiki)

## Conference and talks

Functional Programming in Kotlin with funKTionale ([video](https://www.youtube.com/watch?v=klakgWp1KWg), [presentation](https://speakerdeck.com/marioariasc/functional-programming-in-kotlin-with-funktionale-2))

## Maven (and Gradle)

You must configure your `pom.xml` file using JCenter repository

```xml
<repository>
    <id>central</id>
    <name>bintray</name>
    <url>http://jcenter.bintray.com</url>
</repository>
```

Then you can use any funKTionale module to your library

```xml
<dependency>
    <groupId>org.funktionale</groupId>
    <artifactId>funktionale-all</artifactId>
    <version>1.1</version>
</dependency>
```

## How to contribute?

Rise your PR against Experimental module (`funktionale-experimental`). Once it gets approved I'll move it to a proper module 


