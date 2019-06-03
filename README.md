Sample project demonstrating how to create a basic Kotlin compiler plugin.

Run this command to see it in action:

```text
$ ./gradlew :app:build
```
```text
:compiler-plugin:kaptKotlin
:compiler-plugin:compileKotlin
Using kotlin incremental compilation
:compiler-plugin:compileJava UP-TO-DATE
:compiler-plugin:copyMainKotlinClasses
:compiler-plugin:processResources UP-TO-DATE
:compiler-plugin:classes UP-TO-DATE
:compiler-plugin:jar
:app:compileKotlin
Using kotlin incremental compilation
w: *** IT'S ALIVE ***
:app:compileJava UP-TO-DATE
:app:copyMainKotlinClasses
:app:processResources UP-TO-DATE
:app:classes UP-TO-DATE
:app:jar
:app:assemble
:app:compileTestKotlin UP-TO-DATE
:app:compileTestJava UP-TO-DATE
:app:copyTestKotlinClasses
:app:processTestResources UP-TO-DATE
:app:testClasses UP-TO-DATE
:app:test UP-TO-DATE
:app:check UP-TO-DATE
:app:build

BUILD SUCCESSFUL
```
# arrow-meta-prototype
