# Arrow Site

[![Arrow logo](https://raw.githubusercontent.com/arrow-kt/arrow-site/master/docs/img/home/arrow-brand-error.svg?sanitize=true)](https://arrow-kt.io)

This repository **just** includes the [Arrow website](https://arrow-kt.io/) main files:

* Landing page
* CSS
* JS
* sidebar
* etc.

and it could include common documentation for all the Arrow libraries.

The **rest** of the website content comes from:

* [Every Arrow library generates its own API Doc](https://github.com/arrow-kt/arrow/blob/master/docs/libraries/how-to-generate-and-validate-documentation.md) (pages with `/apidocs/` in the URL). Every pull request on an Arrow library will publish API Doc for the next version with this URL pattern: `https://arrow-kt.io/docs/next/apidocs/<arrow-module>/`.
* Some Arrow libraries include static documentation (pages without `/apidocs/` in the URL). Every pull request on an Arrow library will publish it with this URL pattern: `https://arrow-kt.io/docs/next/<optional-directory>/<page>`. The source code for that documentation can be found in these directories:
  * [Arrow Core](https://github.com/arrow-kt/arrow-core/tree/master/arrow-docs/docs)
  * [Arrow Fx](https://github.com/arrow-kt/arrow-fx/tree/master/arrow-docs/docs)
  * [Arrow Incubator](https://github.com/arrow-kt/arrow-incubator/tree/master/arrow-docs/docs)
  * [Arrow Optics](https://github.com/arrow-kt/arrow-optics/tree/master/arrow-docs/docs)
* [Arrow Media](https://media.arrow-kt.io): the showcase of tutorials, conference presentations and other Arrow-related content. If you have given a talk, written a post, or spread the word about Arrow in any other way, please don't hesitate to include it in the [MEDIA section of the Arrow website](https://media.arrow-kt.io/). To do so, take a look at the [Arrow Media](https://github.com/arrow-kt/arrow-media) repository.

## Table of contents

* [Release flow](#Release-flow)
* [How to add a menu entry](#How-to-add-a-menu-entry)
* [How to deploy the site to a local server](#How-to-deploy-the-site-to-a-local-server)
* [How to test links](#How-to-test-links)

### Release flow

This repository has its own release flow.

Every change that is pushed on `master` branch will be shown directly at the website:

* Main files: landing page, JS, CSS, etc.
* Next version will be re-generated with the styles and sidebars in this repository. For instance, for Arrow Core: https://arrow-kt.io/docs/next/core/.
* Previous versions that are included in [`update-other-versions.txt`](update-other-versions.txt) will be re-generated with the styles and sidebars in this repository. Those versions must match with the related tags. For instance, `0.10.4`. However, the version in the website (URL) will just consider `major.minor` version. Following the example, `0.10.4` in [`update-other-versions.txt`](update-other-versions.txt) file, it will re-generate the website under `docs/0.10/...`.

### How to add a menu entry

Go to `docs/_data/` and choose the correspondent `sidebar-x.yml` file. Then, add a menu entry in the proper section, as in:

```
- title: Applicative
  url: /typeclasses/applicative/
```

Check [this PR](https://github.com/arrow-kt/arrow/pull/1134/files) for a real example.

### How to deploy the site to a local server

We use Jekyll so you can deploy your docs to a local server to test your changes and see how those would look once released.

These steps **just** run the website with the main files: landing page, JS, CSS, etc. If you want to browse more content, find the required steps in [Arrow library: How to run the website in your local server](https://github.com/arrow-kt/arrow/blob/master/docs/libraries/how-to-run-the-website-in-your-local-server.md) for every Arrow library.

#### 1. Ank to compile and validate code snippets

Ank is in charge of compiling and validating your code snippets and deploying the proper binaries for those. Run the following command:

```bash
./gradlew runAnk
```

In this case, it will compile and validate the code snippets that are shown in the landing page.

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
