# Arrow library: How to add a new module

This guideline provides all the things to keep in mind when adding a new module:

- Configuration:
  - Add `<module>/gradle.properties`
  - Add `<module>/build.gradle`
  - Update `settings.xml` - This update can be avoided with the use of [Auto Module plugin](https://github.com/pablisco/auto-module) by [@pablisco](https://github.com/pablisco): [an example of use](https://github.com/arrow-kt/arrow-incubator/blob/master/settings.gradle)
- Website:
  - Update [libraries page](https://github.com/arrow-kt/arrow-core/tree/master/arrow-docs/docs/quickstart/libraries)
  - Update [sidebar files](https://github.com/arrow-kt/arrow-site/tree/master/docs/_data)
- Utilities:
  - Update BOM file: [build.gradle](https://github.com/arrow-kt/arrow/blob/how-to-add-a-new-module/BOM-file/build.gradle)

Last things can be automated though it's necessary to update them manually until it's ready.
