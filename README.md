<a href="https://arrow-kt.io" title="Arrow website"><img src="img/logo/arrow-dark.svg" width="200" alt=""></a>

[![Maven Central](https://img.shields.io/maven-central/v/io.arrow-kt/arrow-core?color=4caf50&label=latest%20release)](https://maven-badges.herokuapp.com/maven-central/io.arrow-kt/arrow-core)
[![Latest snapshot](https://img.shields.io/badge/dynamic/xml?color=orange&label=latest%20snapshot&prefix=v&query=%2F%2Fmetadata%2Fversioning%2Flatest&url=https%3A%2F%2Foss.sonatype.org%2Fservice%2Flocal%2Frepositories%2Fsnapshots%2Fcontent%2Fio%2Farrow-kt%2Farrow-core%2Fmaven-metadata.xml)](https://oss.sonatype.org/service/local/repositories/snapshots/content/io/arrow-kt/)
[![Kotlin version](https://img.shields.io/badge/Kotlin-1.4-blue)](https://kotlinlang.org/docs/reference/whatsnew14.html)

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![StackOverflow](https://img.shields.io/badge/arrow--kt-grey.svg?logo=stackoverflow)](https://stackoverflow.com/questions/tagged/arrow-kt)
[![Twitter](https://img.shields.io/twitter/follow/arrow_kt?color=blue&style=flat)](https://twitter.com/arrow_kt)

Λrrow is a library for Typed Functional Programming in Kotlin.

Arrow aims to provide a [*lingua franca*](https://en.wikipedia.org/wiki/Lingua_franca) of interfaces and abstractions across Kotlin libraries.
For this, it includes the most popular data types such as `Option`, `Either`, `Validated` etc and functional operators such as `traverse` and computation blocks to empower users to write pure FP apps and libraries built atop higher order abstractions.

Use the list below to learn more about Λrrow's main features.

- [Documentation](http://arrow-kt.io)
- [Type classes](http://arrow-kt.io/docs/typeclasses/intro/): defining behaviors for data
- [Effects](http://arrow-kt.io/docs/effects/io/): interfacing with external systems
- [Optics](http://arrow-kt.io/docs/optics/iso/): inspecting and modifying data structures

#### Curated external links

- [Media: blogs, presentations, etc.](https://media.arrow-kt.io)

If you have a blog post, talk, or upcoming event on Arrow, please considering opening an issue or PR to add to the collection over at the [Arrow Media](https://github.com/arrow-kt/arrow-media) repo. 

## Join Us

Arrow is an inclusive community powered by awesome individuals like you. As an actively growing ecosystem, Arrow and its associated libraries and toolsets are in need of new contributors! We have issues suited for all levels, from entry to advanced, and our maintainers are happy to provide 1:1 mentoring. All are welcome in Arrow.

If you’re looking to contribute, have questions, or want to keep up-to-date about what’s happening, please follow us here and say hello!

- [Arrow on Twitter](https://twitter.com/arrow_kt)
- [#arrow on Kotlin Slack](https://kotlinlang.slack.com/messages/C5UPMM0A0)
- [#arrow-contributors on Kotlin Slack](https://kotlinlang.slack.com/archives/C8UK6RTHU)
- [Arrow on Gitter](https://gitter.im/arrow-kt/Lobby)

Find more details in [CONTRIBUTING](CONTRIBUTING.md).

## Setup

### JDK

Make sure to have the latest version of JDK 1.8 installed.

### Android

Arrow supports Android starting on API 21 and up.

### Gradle

#### Basic Setup

In your project's root `build.gradle`, append this repository to your list:

```groovy
allprojects {
    repositories {
        mavenCentral()
    }
}
```

Add the dependencies into the project's `build.gradle`:

##### Λrrow Core

```groovy
def arrow_version = "0.13.2"
dependencies {
    implementation "io.arrow-kt:arrow-core:$arrow_version"
}
```

##### Λrrow Core + Λrrow Optics

```groovy
apply plugin: 'kotlin-kapt'

def arrow_version = "0.13.2"
dependencies {
    implementation "io.arrow-kt:arrow-optics:$arrow_version"
    kapt    "io.arrow-kt:arrow-meta:$arrow_version"
}
```

##### Λrrow Core + Λrrow Fx 

```groovy
def arrow_version = "0.13.2"
dependencies {
    implementation "io.arrow-kt:arrow-fx-coroutines:$arrow_version"
}
```

#### BOM file

To avoid specifying the Arrow version for every dependency, a BOM file is available:

```
    implementation platform("io.arrow-kt:arrow-stack:$arrow_version")

    implementation "io.arrow-kt:arrow-core"
    implementation "io.arrow-kt:arrow-fx-coroutines"
    ...
```

### Maven

#### Basic Setup

Make sure to have at least the latest version of JDK 1.8 installed.
Add to your `pom.xml` file the following properties:
```xml
<properties>
    <kotlin.version>1.4.0</kotlin.version>
    <arrow.version>0.13.2</arrow.version>
</properties>
```

Add the dependencies that you want to use:
```xml
<dependency>
    <groupId>io.arrow-kt</groupId>
    <artifactId>arrow-core</artifactId>
    <version>${arrow.version}</version>
</dependency>
```

#### Enabling kapt for the Optics DSL

For the Optics DSL, enable annotation processing using Kotlin plugin:
```xml
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

#### BOM file

To avoid specifying the Arrow version for every dependency, a BOM file is available:

```
  <dependencyManagement>
    <dependencies>
      <dependency>
        <groupId>io.arrow-kt</groupId>
        <artifactId>arrow-stack</artifactId>
        <version>${arrow.version}</version>
        <type>pom</type>
        <scope>import</scope>
      </dependency>
    </dependencies>
  </dependencyManagement>
  <dependencies>
    ...
  </dependencies>
```

## Next development version

If you want to try the latest features, replace `0.13.2` with `1.0.0-SNAPSHOT` and add this repository:

```groovy
allprojects {
    repositories {
        ...
        maven { url "https://oss.sonatype.org/content/repositories/snapshots/" }
    }
}
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
