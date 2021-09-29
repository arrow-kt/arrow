// This file allows to open just core libraries

rootProject.name = "arrow-core-libs"

include("arrow-meta:arrow-meta-test-models")
include("arrow-meta")
include("arrow-annotations")
include("arrow-core")
include("arrow-core-test")
include("arrow-continuations")
include("arrow-core-retrofit")

include("jekyll")
project(":jekyll").projectDir = file("dokka/jekyll")
