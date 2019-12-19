---
layout: docs-core
title: Linting
permalink: /docs/quickstart/linting/
---

## Arrow Linting

Some linters might complain about some code practices that are common when working with functional programming. This section explains some of them and how they can be disabled.

### Working with ArrowFx

When working with [Arrow Fx]({{ '/docs/fx' | relative_url }}), side effects are expressed as `suspend` functions.

However, IDEA will show *redundant suspend modifiers* warning if a function does not invoke other suspended functions. For ArrowFx, we want to be explicit about impure functions, so this warning is not useful.

You can disable this warning in Preferences > Editor > Inspections. Then search for the option Kotlin > Redundant constructs > Redundant suspend modifier.

![gif](/img/linting/linting_suspend_modifier.gif)

### Working with purity

In functional programming, being explicit about the return type of functions is important. Based on the return type of a function, we can try to identify whether it is a pure or an impure function. If a function does not return a value, it hints that a side effect is happening inside.

To make this explicit, these functions can return Unit. However, IDEA will show a *redundant 'Unit' type* warning because it is the default return type for such functions.

You can disable this warning in Preferences > Editor > Inspections. Then search for the option Kotlin > Redundant constructs > Redundant Unit return type.

![gif](/img/linting/linting_unit_return_type.gif)

If you are using [ktlint](https://ktlint.github.io/) as the linter of choice for your project, this scenario will be reported as an error, making builds fail.

You can disable these errors. For that, you must upgrade ktlint's version to `0.34.0` or later and add `disabled_rules=no-unit-return` to the `.editorconfig` file. You can read more about this topic [here](https://github.com/pinterest/ktlint#editorconfig).
