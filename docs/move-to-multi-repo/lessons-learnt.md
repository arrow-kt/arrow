# Lessons learnt

* It's not possible to have a `plugins` block in a common file: `Only Project and Settings build scripts can contain plugins {} blocks.`
* Build properties cannot be included in a common file (just versions and configuration values).
* Configuration changes follow the same rule as source code: don't mix refactor + new feature in the same step.
