---
layout: quickstart
title: Quick Start
permalink: /quickstart/
---

<div class="quickstart-doc" markdown="1">

<!--- Module Libraries
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
-->
<div class="modular-libraries" markdown="1">
<div class="modular-libraries-header" markdown="1">
# Λrrow is a library for Typed Functional Programming in Kotlin.
Arrow is composed of 4 main modular libraries
</div>
<div class="libraries-list" markdown="1">
<!--- Module library Core
-------------------------------------
-->
<div href="#" class="library-item" markdown="1">
<div class="library-item-content" markdown="1">
### Core
Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.

<a href="#" class="library-cta core">Read more</a>
</div>
<div class="library-item-brand" markdown="1">
![Core](/img/quickstart/modular-libraries-core.svg "Arrow Core")
</div>
</div>
<!--- Module library Fx
-------------------------------------
-->
<div href="#" class="library-item" markdown="1">
<div class="library-item-content" markdown="1">
### Fx
Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.

<a href="#" class="library-cta fx">Read more</a>
</div>
<div class="library-item-brand" markdown="1">
![Core](/img/quickstart/modular-libraries-fx.svg "Arrow Core")
</div>
</div>
<!--- Module library Optics
-------------------------------------
-->
<div href="#" class="library-item" markdown="1">
<div class="library-item-content" markdown="1">
### Optics
Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.

<a href="#" class="library-cta optics">Read more</a>
</div>
<div class="library-item-brand" markdown="1">
![Core](/img/quickstart/modular-libraries-optics.svg "Arrow Core")
</div>
</div>
<!--- Module library Meta start
-------------------------------------
-->
<div href="#" class="library-item" markdown="1">
<div class="library-item-content" markdown="1">
### Meta
Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.

<a href="#" class="library-cta meta">Read more</a>
</div>
<div class="library-item-brand" markdown="1">
![Core](/img/quickstart/modular-libraries-meta.svg "Arrow Core")
</div>
</div>

</div>
</div>

<!--- Setup
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
-->

<div class="setup" markdown="1">
## Setup

{: .setup-subtitle}
Configure Arrow for your project
<div class="setup-jdk-android" markdown="1">
<div class="jdk-item" markdown="1">
![Jdk](/img/quickstart/jdk-logo.svg "jdk")

Make sure to have the latest version of JDK 1.8 installed.
</div>
<div class="android-item" markdown="1">
![Android](/img/quickstart/android-logo.svg "android")

<!--- Module Libraries -->
Arrow supports Android starting on API 21 and up.
</div>
</div>

<div class="setup-graddle-maven" markdown="1">
<!-- Tab links -->
<div class="tab" markdown="1">
  <button class="tablinks" onclick="openCity(event, 'London')" id="Maven" markdown="1">Maven</button>
  <button class="tablinks" onclick="openCity(event, 'Paris')" markdown="1">Graddle</button>
</div>

<!-- Tab content -->
<div id="London" class="tabcontent" markdown="1">

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

##### Arrow Core

```groovy
def arrow_version = "0.13.2"
dependencies {
    implementation "io.arrow-kt:arrow-core:$arrow_version"
}
```

##### Arrow Core + Arrow Optics

```groovy
apply plugin: 'kotlin-kapt'

def arrow_version = "0.13.2"
dependencies {
    implementation "io.arrow-kt:arrow-optics:$arrow_version"
    kapt    "io.arrow-kt:arrow-meta:$arrow_version"
}
```

##### Arrow Core + Arrow Fx

```groovy
def arrow_version = "0.13.2"
dependencies {
    implementation "io.arrow-kt:arrow-fx-coroutines:$arrow_version"
}
```

#### BOM file

```groovy
def arrow_version = "0.13.2"
dependencies {
    implementation "io.arrow-kt:arrow-fx-coroutines:$arrow_version"
}
```

</div>

<div id="Paris" class="tabcontent" markdown="1">

#### Basic Setup

Make sure to have at least the latest version of JDK 1.8 installed.
Add to your pom.xml file the following properties:

</div>
</div>

</div>


</div>
