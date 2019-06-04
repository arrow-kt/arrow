# arrow-meta-prototype

**Please document all findings in the code**

Sample project demonstrating how to create a basic Kotlin compiler plugin.

Run this command to see it in action:

```text
$ ./gradlew :app:build
```

# Resources

## Projects
 - Top to bottom from most simple to most complex


 - [Sample kotlin compiler plugin](https://github.com/Takhion/sample-kotlin-compiler-plugin) a hello world compiler plugin by @Takhion
 - [Redacted](https://github.com/ZacSweers/redacted-compiler-plugin) port of auto-value redacted for classes by @ZacSweers
 - [Spek compiler plugin](https://github.com/spekframework/spek/pull/657/files) compiler plugin that writes code using the IR backend.
 - [DebugLog](https://github.com/kevinmost/debuglog) bad gradle setup, check [Takhion's](https://github.com/Takhion/sample-kotlin-compiler-plugin) gradle setup. DebugLog uses `org.jetbrains.org.objectweb.asm.` bytecode DSL. (Java only) @kevinmost
 - [Parcelize](https://github.com/JetBrains/kotlin/tree/master/plugins/android-extensions/android-extensions-compiler/src/org/jetbrains/kotlin/android/parcel) plugin to add parcelable logic to data classes. Uses the `org.jetbrains.org.objectweb.asm.` bytecode DSL. (Java only)
 - [Kotlinx serialization](https://github.com/JetBrains/kotlin/tree/master/plugins/kotlin-serialization/kotlin-serialization-compiler/src/org/jetbrains/kotlinx/serialization/compiler) uses all backend-ends + Kotlin IR.
 - [Android synthetic extensions](https://github.com/JetBrains/kotlin/tree/master/plugins/android-extensions/android-extensions-compiler/src/org/jetbrains/kotlin/android/synthetic) generates synthetic methods on Activity/View and Fragment to access views directly by their xml id.
 
 - **currently broken** [purity](https://github.com/pardom/purity) a plugin that checks if functions and lambdas with @Pure are actually pure.


 ## Slack
  - [Progress on IR](https://kotlinlang.slack.com/archives/C7L3JB43G/p1551303086009100?thread_ts=1551303086.009100&cid=C7L3JB43G)
