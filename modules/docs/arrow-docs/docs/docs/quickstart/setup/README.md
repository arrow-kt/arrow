---
layout: docs
title: Setup
permalink: /docs/quickstart/setup/
---

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
    kapt    'io.arrow-kt:arrow-meta:$arrow_version' //optional
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

### Linting

Some linters might complaint about some code practices that are common when working with functional programming. You can read more about how to solve this problem in the [Linting]({{ '/docs/quickstart/linting/' | relative_url }}) section.
