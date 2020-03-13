# Arrow library: How to generate and validate the documentation

Dokka is responsible for generating documentation based on source code annotations. Ank is in charge of compiling and validating your doc snippets and deploying the proper binaries for those.

In order to generate the documentation and validate it:

```bash
./gradlew buildArrowDoc
```

## Doc snippets policies

Whenever you are documenting a new type (type class, data type, whatever) you'll wonder how to add code snippets to it. Please,
use the following priority check list:

### 1. Snippets for public API docs

If the snippet is just docs for a public method of a type (as in arguments, return type, or how it should be used from call sites), that should be inlined in the Kdocs of that given method using Dokka. That's done under the actual type file. [Here you have a simple example for `Option` methods](https://github.com/arrow-kt/arrow/blob/11a65faa9eed23182994778fa0ce218b69bfc4ba/modules/core/arrow-core/src/main/kotlin/arrow/core/Option.kt#L14).

That will automatically inline the docs of each method into the docs of the given data type. This is intended to be used just for public APIs exposed to users of the library.

### 2. Snippets for broader samples

If your snippet is showing examples on how to use the public APIs in a broader scenario (like describing FP patterns or similar), then you'll add those snippets to the described docs Markdown file.

For the mentioned cases, you should double-check which `Ank` modifiers you want to use for the snippets (`silent`, `replace`, or `outFile(<file>)`). You'll find more details about each one of those in [Ank docs](https://github.com/arrow-kt/arrow-ank). See some real examples [on this docs PR](https://github.com/arrow-kt/arrow/pull/1134/files).

Also note that you can make your Ank snippets **editable and runnable in the actual browser**, which is quite handy. Just add this `{: data-executable='true'}` before your Ank Kotlin snippet. That **must be** used as a norm for all the snippets except for the ones that just represent infrastructure for following snippets (where there's not much value on making them runnable).

## "Type Class Hierarchy" sections

**Type Class Hierarchy** section can be included in a Type Class page via:

```
ank_macro_hierarchy(<class>)
```

When running Ank, that line will be replaced and a new `diagram.nomnol` will be created in the same directory.

Please, take into account that `permalink` directive in Jekyll header can change the location of the `index.html` file that will be generated for that page. If `permalink` includes a different path, the `diagram.nomnol` won't be found.
