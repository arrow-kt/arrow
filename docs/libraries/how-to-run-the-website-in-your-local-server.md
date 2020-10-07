# Arrow library: How to run the website in your local server

## Via Gradle task (recommended)

Arrow website can be run for any of the Arrow libraries via this Gradle task:

```sh
./gradlew runArrowSite
```

After generating and validating the documentation for the main libraries (Core, Fx, Optics) and the current Arrow library (if it's not any of those), the website will be launched in [127.0.0.1:4000](http://127.0.0.1:4000) so you can open it with a standard browser.

## Via manual steps

It can be useful to know an alternative in order to avoid running all the steps with the previous Gradle tasks and just the necessary ones when making changes.

### 1. Prepare the site

```sh
git clone https://github.com/arrow-kt/arrow-site.git
cd arrow-site
rm docs/index.md
```

### 2. Copy the documentation from Arrow library

There are 4 main Arrow libraries that will provide the main pages to browse all the documentation:

* [Arrow Core](https://github.com/arrow-kt/arrow-core)
* [Arrow Fx](https://github.com/arrow-kt/arrow-fx)
* [Arrow Optics](https://github.com/arrow-kt/arrow-optics)
* [Arrow Incubator](https://github.com/arrow-kt/arrow-incubator)

Steps:

```sh
git clone https://github.com/arrow-kt/<arrow-library>.git
cd <arrow-library>
./gradlew buildArrowDoc
```

Then copy the result to the `docs` directory:

```sh
cp -r <arrow-library>/arrow-docs/build/site/* arrow-site/docs
```

### 3. Run the website in your local server

```sh
cd arrow-site
bundle install --gemfile Gemfile --path vendor/bundle
bundle exec jekyll serve -s docs
```

This will install any needed dependencies locally, and will use it to launch the complete website in [127.0.0.1:4000/docs](http://127.0.0.1:4000/docs) so you can open it with a standard browser.

If you get an error while installing the Ruby gem _http_parser_, check if the path to your Arrow directory contains spaces. According to this [issue](https://github.com/tmm1/http_parser.rb/issues/47), the installation with spaces in the path is currently not working.

## How to test links

Test for broken links in documentation using

```sh
wget --spider -r -nd -nv -l 5 http://127.0.0.1:4000/docs/
```
