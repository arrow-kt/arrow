funKTionale
===========

Functional constructs and patterns for [Kotlin](http://kotlin-lang.org)

## Modules

| Module | Description | Internal Dependencies | Artifact | Size(KB) |
|---|---|---|---|---| 
|All| GOTY edition. Every other module content is included but not Experimental module|N/A|`funktionale-all`|1328|
|Collections|Collections related extensions such as `tail`, `prependTo` and others|N/A|`funkationale-collections`|4|
|Complement|Extension functions for predicates to generate complement functions|N/A|`funktionale-complement`|36|
|Composition| Extensions `andThen` (`forwardCompose`) and `compose` for functions|N/A|`funktionale-composition`|8|
|Currying|Extension `curried` and `uncurried` for functions|N/A|`funktionale-currying`|348|
|Either|Either and Disjuntion (right-biased Either) types|Option|`funktionale-either`|44|
|Experimental|Playground and examples. **Not to be used on production**|All|`funktionale-experimental`|148|
|Memoization|Memoization for functions|N/A|`funktionale-memoization`|112|
|Option|Option type|Collections and Utils|`funktionale-option`|20|
|Pairing|Transformations for functions with arity 2 or 3 to one parameter of type `Pair` or `Triple` respectively |N/A|`funktionale-pairing`|8|
|Partials|Partial applied functions|N/A|`funktionale-partials`|688|
|Reverse|Extension `reverse` for functions|N/A|`funktionale-reverse`|32|
|Try|Try computation type|Either|`funktionale-try`|12|
|Utils|`identity` and `constant` functions and Partial Functions |N/A|`funktionale-complement`|20|
|Validation|Validation types and functions with Disjunctions|Either|`funktionale-validation`|12|

## Documentation

Read the [Wiki](https://github.com/MarioAriasC/funKTionale/wiki)

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
    <version>${funktionale.version}</version>
</dependency>
```

## How to contribute?

Rise your PR against Experimental module (`funktionale-experimental`). Once it gets approved I'll move it to a proper module 


