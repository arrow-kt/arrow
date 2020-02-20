# Repositories organization

From:

* **arrow** repository

to:

* **arrow** repository: it will be the orchestrator about common configurations, commands for GitHub Actions integration tests of all the libraries, release flow, etc.

* **arrow-core** repository - NEW!
  - arrow-core 
  - arrow-core-data
  - arrow-meta (it includes arrow-meta-test-models)
  - arrow-annotations
  - arrow-syntax 

* **arrow-fx** repository - NEW!
  - arrow-fx
  - arrow-fx-reactor 
  - arrow-fx-rx2 
  - arrow-benchmarks-fx (it includes arrow-kio-benchmarks and arrow-scala-benchmarks)
  - arrow-streams
  - arrow-fx-kotlinx-coroutines

* **arrow-ank** repository - NEW!
  - arrow-ank
  - arrow-ank-gradle

* **arrow-incubator** repository - NEW!
  - arrow-aql
  - arrow-mtl 
  - arrow-mtl-data
  - arrow-fx-mtl (desire: rename to arrow-mtl-fx)
  - arrow-recursion
  - arrow-recursion-data 
  - arrow-generic
  - arrow-free
  - arrow-free-data 
  - arrow-reflect
  - arrow-kindedj
  - arrow-validation

* **arrow-docs** repository - NEW!
  - arrow-docs (previous arrow-docs module minus the part of the site and it's no longer the reponsible of generating the documentation)
  - arrow-examples

* **arrow-integrations** repository - NEW!
  - arrow-integrations-jackson-module
  - arrow-integrations-retrofit-adapter

* **arrow-ui** repository - NEW!
  - arrow-ui
  - arrow-ui-data 

* **arrow-optics** repository - NEW!
  - arrow-optics 
  - arrow-optics-mtl

* **arrow-test** repository - NEW!
  - arrow-test

* **arrow-site** repository - NEW!
