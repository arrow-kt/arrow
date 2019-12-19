<img height="100" src="https://avatars2.githubusercontent.com/u/29458023?v=4&amp;s=200" width="100">

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.arrow-kt/arrow-core-data/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.arrow-kt/arrow-core)
[![Latest snapshot](https://img.shields.io/maven-metadata/v?color=%230576b6&label=latest%20snapshot&metadataUrl=https%3A%2F%2Foss.jfrog.org%2Fartifactory%2Foss-snapshot-local%2Fio%2Farrow-kt%2Farrow-core%2Fmaven-metadata.xml)](https://oss.jfrog.org/artifactory/oss-snapshot-local/io/arrow-kt/arrow-core/)
[![Release Status](https://github.com/arrow-kt/arrow/workflows/Release/badge.svg)](https://github.com/arrow-kt/arrow/actions?query=workflow%3ARelease+branch%3Amaster)
[![Kotlin version badge](https://img.shields.io/badge/kotlin-1.3-blue.svg)](https://kotlinlang.org/docs/reference/whatsnew13.html)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0)
[![StackOverflow](https://img.shields.io/badge/arrow--kt-black.svg?logo=stackoverflow)]( http://stackoverflow.com/questions/tagged/arrow-kt )


Λrrow is a library for Typed Functional Programming in Kotlin.

Arrow aims to provide a [*lingua franca*](https://en.wikipedia.org/wiki/Lingua_franca) of interfaces and abstractions across Kotlin libraries.
For this, it includes the most popular data types, type classes and abstractions such as `Option`, `Try`, `Either`, `IO`, `Functor`, `Applicative`, `Monad` to empower users to write pure FP apps and libraries built atop higher order abstractions.

Use the list below to learn more about Λrrow's main features.

- [Documentation](http://arrow-kt.io)
- [Patterns](http://arrow-kt.io/docs/patterns/glossary/): tutorials and approaches to day-to-day challenges using FP
- [Libraries](http://arrow-kt.io/docs/quickstart/libraries/): all the libraries provided by Λrrow
- [Type classes](http://arrow-kt.io/docs/typeclasses/intro/): defining behaviors for data
- [Data types](http://arrow-kt.io/docs/datatypes/intro/): common abstractions
- [Effects](http://arrow-kt.io/docs/effects/io/): interfacing with external systems
- [Optics](http://arrow-kt.io/docs/optics/iso/): inspecting and modifying data structures

#### Curated external links

- [Projects and Examples](http://arrow-kt.io/docs/quickstart/projects/)
- [Blogs and Presentations](http://arrow-kt.io/docs/quickstart/blogs/)

## Join Us

Arrow is an inclusive community powered by awesome individuals like you. As an actively growing ecosystem, Arrow and its associated libraries and toolsets are in need of new contributors! We have issues suited for all levels, from entry to advanced, and our maintainers are happy to provide 1:1 mentoring. All are welcome in Arrow.

If you’re looking to contribute, have questions, or want to keep up-to-date about what’s happening, please follow us here and say hello!

- [Arrow on Twitter](https://twitter.com/arrow_kt)
- [#Arrow on Kotlin Slack](https://kotlinlang.slack.com/)
- [Arrow on Gitter](https://gitter.im/arrow-kt/Lobby)

## Setup

### Next development version

If you want to try the last features, replace `0.10.3` by `0.10.4-SNAPSHOT` in the following guideline.

### JDK

Make sure to have the latest version of JDK 1.8 installed.

### Android

Arrow supports Android out of the box starting on API 21 and up.

We'll be working on a Arrow-Android integration module that adds some helpers and integrations.

### Gradle

#### Basic Setup

In your project's root `build.gradle` append these repositories to your list:

```groovy
allprojects {
    repositories {
        mavenCentral()
        jcenter()
        maven { url "https://dl.bintray.com/arrow-kt/arrow-kt/" } 
        maven { url 'https://oss.jfrog.org/artifactory/oss-snapshot-local/' } // for SNAPSHOT builds
    }
}
```

Add the dependencies into the project's `build.gradle`:

##### Λrrow Core

```groovy
apply plugin: 'kotlin-kapt'

def arrow_version = "0.10.3"
dependencies {
    implementation "io.arrow-kt:arrow-core:$arrow_version"
    implementation "io.arrow-kt:arrow-syntax:$arrow_version"
    kapt    "io.arrow-kt:arrow-meta:$arrow_version"
}
```

##### Λrrow Core + Λrrow Optics

```groovy
apply plugin: 'kotlin-kapt'

def arrow_version = "0.10.3"
dependencies {
    implementation "io.arrow-kt:arrow-optics:$arrow_version"
    implementation "io.arrow-kt:arrow-syntax:$arrow_version"
    kapt    "io.arrow-kt:arrow-meta:$arrow_version"
}
```

##### Λrrow Core + Λrrow Fx 

```groovy
apply plugin: 'kotlin-kapt'

def arrow_version = "0.10.3"
dependencies {
    implementation "io.arrow-kt:arrow-fx:$arrow_version"
    implementation "io.arrow-kt:arrow-syntax:$arrow_version"
    kapt    "io.arrow-kt:arrow-meta:$arrow_version"
}
```

##### Λrrow Core + Λrrow Optics + Λrrow Fx

```groovy
apply plugin: 'kotlin-kapt'

def arrow_version = "0.10.3"
dependencies {
    implementation "io.arrow-kt:arrow-fx:$arrow_version"
    implementation "io.arrow-kt:arrow-optics:$arrow_version"
    implementation "io.arrow-kt:arrow-syntax:$arrow_version"
    kapt    "io.arrow-kt:arrow-meta:$arrow_version"
}
```

##### Other libraries

Here is the complete [library list]({{ '/docs/quickstart/libraries/' | relative_url }}) for a more granular dependency set-up.

#### Additional Setup

For projects that wish to use their own `@higherkind`, `@optics` and other meta programming facilities provided by Λrrow
the setup below is also required:

Add the dependencies into the project's `build.gradle`

```groovy
apply plugin: 'kotlin-kapt' //optional
apply from: rootProject.file('gradle/generated-kotlin-sources.gradle') //only for Android projects

def arrow_version = "0.10.3"
dependencies {
    ...
    kapt    "io.arrow-kt:arrow-meta:$arrow_version" //optional
    ...
}
```

`gradle/generated-kotlin-sources.gradle`
```groovy
apply plugin: 'idea'

idea {
    module {
        sourceDirs += files(
                'build/generated/source/kapt/main',
                'build/generated/source/kapt/debug',
                'build/generated/source/kapt/release',
                'build/generated/source/kaptKotlin/main',
                'build/generated/source/kaptKotlin/debug',
                'build/generated/source/kaptKotlin/release',
                'build/tmp/kapt/main/kotlinGenerated')
        generatedSourceDirs += files(
                'build/generated/source/kapt/main',
                'build/generated/source/kapt/debug',
                'build/generated/source/kapt/release',
                'build/generated/source/kaptKotlin/main',
                'build/generated/source/kaptKotlin/debug',
                'build/generated/source/kaptKotlin/release',
                'build/tmp/kapt/main/kotlinGenerated')
    }
}
```

### Maven
 
#### Basic Setup

Add to your pom.xml file the following properties:
```
<properties>
    <kotlin.version>1.3.0</kotlin.version>
     <arrow.version>0.10.3</arrow.version>
</properties>
```

Add the dependencies that you want to use
```
        <dependency>
            <groupId>io.arrow-kt</groupId>
            <artifactId>arrow-core</artifactId>
            <version>${arrow.version}</version>
        </dependency>
        <dependency>
            <groupId>io.arrow-kt</groupId>
            <artifactId>arrow-syntax</artifactId>
            <version>${arrow.version}</version>
        </dependency>

```

#### Enabling kapt

Enable annotaton processing using kotlin plugin 
```
<plugin>
    <groupId>org.jetbrains.kotlin</groupId>
    <artifactId>kotlin-maven-plugin</artifactId>
    <version>${kotlin.version}</version>
    <executions>
        <execution>
            <id>kapt</id>
            <goals>
                <goal>kapt</goal>
            </goals>
            <configuration>
                <sourceDirs>
                    <sourceDir>src/main/kotlin</sourceDir>
                </sourceDirs>
                <annotationProcessorPaths>
                    <annotationProcessorPath>
                        <groupId>io.arrow-kt</groupId>
                        <artifactId>arrow-meta</artifactId>
                        <version>${arrow.version}</version>
                    </annotationProcessorPath>
                </annotationProcessorPaths>
            </configuration>
        </execution>
        <execution>
            <id>compile</id>
            <phase>compile</phase>
            <goals>
                <goal>compile</goal>
            </goals>
            <configuration>
                <sourceDirs>
                    <sourceDir>src/main/kotlin</sourceDir>
                </sourceDirs>
            </configuration>
        </execution>
        <execution>
            <id>test-compile</id>
            <phase>test-compile</phase>
            <goals>
                <goal>test-compile</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```
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
