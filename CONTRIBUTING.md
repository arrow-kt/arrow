# Contributing guidelines

* Adding a new datatype
* Adding a new typeclass
* Running tests
* [Building documentation](#building-documentation)

## Building documentation

You can find documentation in `modules/docs/arrow-docs/docs/docs`

Documentation is being built by [ΛNK](https://github.com/arrow-kt/ank) which gives an ability to verify and interpret code snippets in the documentation. For example:


```kotlin:ank
1 + 1
```

results in

```
1 + 1
// 2
```

To build the documentation, in the root of the project, run

```
$ ./gradlew clean dokka :arrow-docs:runAnk
```

The artifact will reside in `modules/docs/arrow-docs/build/site`. The artifact ΛNK produces could be served as a website by [jekyll](https://jekyllrb.com/), a static site generator.

In order to setup a local version of the documentation in your machine, you will have to install [gem](https://rubygems.org/pages/download) as Jekyll is written in ruby. Afterwards, install [bundler](https://bundler.io/) to manage the project dependencies.

```
$ gem install bundler
$ cd modules/docs/arrow-docs/
$ bundle install --path vendor/bundle
```

After the dependencies are installed, you can preview the documentation by serving the artifact produced by ΛNK.

```
$ bundle exec jekyll serve --source build/site/
```

By default, the documentation will now be available at [http://127.0.0.1:4000](http://127.0.0.1:4000).
