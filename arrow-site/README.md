# Arrow Site

This repository includes the [Arrow website](https://arrow-kt.io/) main files:

* Landing page
* CSS
* JS
* sidebar
* etc.

The rest of the website content comes from:

* Every Arrow library generates its own `apidocs`. For instance, take a look at [Arrow Core](https://github.com/arrow-kt/arrow-core).
* [Arrow Docs](https://github.com/arrow-kt/arrow-docs) still includes some [static files](https://github.com/arrow-kt/d-arrow-module/tree/master/arrow-docs-repository/arrow-docs/docs/static).
* [Arrow Media](https://github.com/arrow-kt/arrow-media): the showcase of tutorials, conference presentations and other Arrow-related content. If you have given a talk, written a post, or spread the word about Arrow in any other way, please don't hesitate to include it in the [MEDIA section of the Arrow website](https://media.arrow-kt.io/). To do so, take a look at the [Arrow Media](https://github.com/arrow-kt/arrow-media) repository.


## Table of contents

* [Release flow](Release-flow)
* [How to add a menu entry](How-to-add-a-menu-entry)
* [How to deploy the site to a local server](How-to-deploy-the-site-to-a-local-server)
* [How to test links](How-to-test-links)

### Release flow

This repository has its own release flow. Every change that is pushed on `master` branch will be shown directly at the website.

### How to add a menu entry

Go to `docs/_data/` and choose the correspondent `sidebar-x.yml` file. Then, add a menu entry in the proper section, as in:

```
- title: Applicative
  url: /typeclasses/applicative/
```

Check [this PR](https://github.com/arrow-kt/arrow/pull/1134/files) for a real example.

### How to deploy the site to a local server

We use Jekyll so you can deploy your docs to a local server to test your changes and see how those would look once released.

#### 1. Ank to compile and validate code snippets

Ank is in charge of compiling and validating your code snippets and deploying the proper binaries for those. Run the following command:

```bash
./gradlew runAnk
```

#### 2. Run the docs in your local server

Once code snippets are validated, do this:

```bash
bundle install --gemfile Gemfile --path vendor/bundle
bundle exec jekyll serve -s build/site/
```

This will install any needed dependencies locally, and will use it to launch the complete website in [127.0.0.1:4000](http://127.0.0.1:4000) so you can open it with a standard browser.

If you should get an error while installing the Ruby gem _http_parser_, please check if the path to your Arrow directory contains spaces. According to this [issue](https://github.com/tmm1/http_parser.rb/issues/47), the installation with spaces in the path is currently not working.

### How to test links

Test for broken links in documentation using

```sh
wget --spider -r -nd -nv -l 5 http://127.0.0.1:4000/docs/
```
