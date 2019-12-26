# Arrow docs

This is the Arrow documentation deployed to the website (arrow-kt.io).

* [Documentation contribution](#documentation-contribution)
* [Blog section contribution](#blog-section-contribution)

## Documentation contribution

We prefer Kdocs only for documenting data types or type classes. Only use tutorial style markdown docs for patterns or tutorials. In case you need to add a new docs section, you should go like this:

### 1. Add a menu entry to your docs

Go to `modules/docs/arrow-docs/docs/_data/sidebar.yml` and add a menu entry in the proper section, as in:

```
- title: Applicative
  url: /docs/typeclasses/applicative/
```

Check [this PR](https://github.com/arrow-kt/arrow/pull/1134/files) for a real example.

### 2. Add your docs file

Add your docs as a Markdown file inside the corresponding directory. You have directories for all the sections
available in docs under `modules/docs/arrow-docs/docs/docs/`.

Let's say you want to add docs for a Type class, for extension `Applicative`. You'd need to add a README like [this one](https://github.com/arrow-kt/arrow/blob/master/modules/docs/arrow-docs/docs/docs/arrow/typeclasses/applicative/README.md).

You'll find all the sections available in the [docs side menu](https://arrow-kt.io/docs/). As you can see, there's:
* **Quick start:** Basically how to start using Arrow, including links to libraries, posts, talks, and sample projects.
* **API docs:** This is the public API reference.
* **Patterns:** This section is much more like blogposts. Each one describes real world problems and how to solve them using Arrow. Pretty instructive.
* **Data Types:** Docs for all the data types included in Arrow.
* **Type Classes:** Docs for all the type classes included in Arrow.
* **Effects:** Docs for all the effects module constructs, including type classes and data types.
* **Optics:** Docs for the fancy optics DSL and all the related types.
* **Arrow Query Language:** A human readable DSL used to build up arrow programs without the need for more specific knowledge. Usable over any query system.
* **Generic:** We have some stuff generated at compile time under our `generic` module. Here you'll find the docs for it.
* **Integration:** Docs for Arrow integration with third party libraries.
* **Free:** Docs for Arrow Free Monads constructs.
* **Recursion schemes:** Docs on how arrow achieves recursion schemes.
* **Legal:** Credits and Licenses.

### 3. Add a permalink to your file

Add the proper permalink to your docs on top position of your README file, as in:

```
---
layout: docs
title: Applicative
permalink: /docs/arrow/typeclasses/applicative/
redirect_from:
  - /docs/typeclasses/applicative/
---
```

Again, take a look at [this PR](https://github.com/arrow-kt/arrow/pull/1134/files) for a real example.

### 4. Link your file from intro pages

There are docs intro pages for both [data types](https://arrow-kt.io/docs/datatypes/intro/) and [type classes](https://arrow-kt.io/docs/typeclasses/intro/) which contain a list of the data types and typeclasses available in Arrow. Each item comes along with a brief and simple description.

In case you're adding docs for a data type or a type class, be sure to add a new entry from the corresponding intro page to your docs, so people can scan them easily in a single page.

### 5. Format your docs properly

If you're adding a data type, go to other already existing data type docs to follow the same approach.
Do the same for type class, and any other doc sections.
  * Use proper Ank snippet configurations (more details in policies section below).

## Doc snippets policies

Whenever you are documenting a new type (type class, data type, whatever) you'll wonder how to add code snippets to it. Please,
use the follow priority check list:

### 1. Snippets for public API docs

If the snippet is just docs for a public method of a type (as in arguments, return type, or how it should be used from call sites), that should be inlined in the Kdocs of that given method using Dokka. That's done under the actual type file. [Here you have a simple example for `Option` methods](https://github.com/arrow-kt/arrow/blob/11a65faa9eed23182994778fa0ce218b69bfc4ba/modules/core/arrow-core/src/main/kotlin/arrow/core/Option.kt#L14).

That will automatically inline the docs of each method into the docs of the given data type. This is intended to be used just for public APIs exposed to users of the library.

### 2. Snippets for broader samples

If your snippet is showing examples on how to use the public API's in a broader scenario (like describing FP patterns or similar) then you'll add those snippets to the described docs Markdown file.

For the mentioned cases, you should double check which `Ank` modifiers you wanna use for the snippets (`silent`, `replace` or `outFile(<file>)`). You have more details about each one of those in [Ank docs](../../ank/README.md). You have some real examples [on this docs PR](https://github.com/arrow-kt/arrow/pull/1134/files).

Also note that you can make your Ank snippets **editable and runnable in the actual browser**, which is quite handy. Just add this `{: data-executable='true'}` before your Ank Kotlin snippet. That **must be** used as a norm for all the snippets except for the ones that just represent infrastructure for following snippets (where there's not much value on making then runnable).

## How to deploy docs to a local server

We use Jekyll so you can deploy your docs to a local server to test your changes and see how those would look once released.

So, after making you doc changes as mentioned before, you would:

### 1. Run Dokka and Ank to get your docs deployed locally

Dokka is responsible of generating documentation based on source code annotations, while Ank is in charge of compiling and validating your doc snippets and deploying the proper binaries for those. Run the following commands in this exact order in Arrow **root dir**:

```bash
./gradlew clean dokka
```

You can run Dokka for a **single module**. That will save you a lot of time. For example, if you've added some docs to the
`arrow-typeclasses` module, just run:

```bash
./gradlew clean :arrow-typeclasses:dokka
```

After running Dokka, you can run Ank. That will deploy all the Dokka generated binaries along with the rest of the docs.

```bash
./gradlew :arrow-docs:runAnk
```

### 2. Run the docs in your local server

Once docs are generated locally, do this (**also from the root directory**):

```bash
bundle install --gemfile modules/docs/arrow-docs/Gemfile --path vendor/bundle
BUNDLE_GEMFILE=modules/docs/arrow-docs/Gemfile bundle exec jekyll serve -s modules/docs/arrow-docs/build/site/
```

This will install any needed dependencies locally, and will use it to launch the complete website in [127.0.0.1:4000](http://127.0.0.1:4000) so you can open it with a standard browser.

If you should get an error while installing the Ruby gem _http_parser_, please check if the path to your Arrow directory contains spaces. According to this [issue](https://github.com/tmm1/http_parser.rb/issues/47), the installation with spaces in the path is currently not working.

## Blog section contribution

If you had given a talk, written a post, or spread the word on Arrow in any other way, please don't hesitate to include it in the [blog section of the Arrow website](//arrow-kt.io/blog).

To do so, please add a Markdown file inside the `_posts` directory and include the relevant info to your contribution. As standard for Jekyll posts, please name it as follows:

`yyyy-mm-dd-title-in-kebab-case`

The Front Matter values available to these files are:

* `title`: A short title for your contribution.
* `header-image`: You can link an external relevant image to be set as a header. Take into account that it will be mostly dynamically trimmed into a ~16:9 ratio. If you have no image you can always set one ready for it, i.e. `/img/blog-image-header.png`.
* `icon`: An icon to be shown at the top-right of the header. In the same way as the image, you can select one of `/img/icon-podcast.svg`, `/img/icon-news.svg`, or `/img/icon-video.svg`.
* `version`: Arrow version relevant to this contribution, e.g. `version 0.9.0`. You can leave it empty if it's something generic.
* `category`: `news` or `media` mainly. It will affect the tab section where your contribution will appear in.
* `link`: An external link pointing to where more info about your contribution can be found.
* `event`: Optional field, add it if the contribution happened at some event or conference, useful for talks or workshops.

Then in the content section you can write a small description following Markdown format as usual, including links. Please refrain of setting a long chunk of text here, as it probably won't fit well between the other ones.

To check how it will actually look there, please refer to the [run the docs in your local server](#2-run-the-docs-in-your-local-server) section.


## How to test links

Test for broken links in documentation using

```sh
wget --spider -r -nd -nv -l 5 http://127.0.0.1:4000/docs/
```
