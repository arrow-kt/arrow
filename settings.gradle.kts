/*
 * Copyright (C) 2017 The Arrow Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/** Core **/

include(":arrow-syntax")
project(":arrow-syntax").projectDir = file("modules/core/arrow-syntax")

include(":arrow-core")
project(":arrow-core").projectDir = file("modules/core/arrow-core")

include(":arrow-typeclasses")
project(":arrow-typeclasses").projectDir = file("modules/core/arrow-typeclasses")

include(":arrow-instances")
project(":arrow-instances").projectDir = file("modules/core/arrow-instances")

include(":arrow-data")
project(":arrow-data").projectDir = file("modules/core/arrow-data")

include(":arrow-free")
project(":arrow-free").projectDir = file("modules/core/arrow-free")

include(":arrow-mtl")
project(":arrow-mtl").projectDir = file("modules/core/arrow-mtl")

include(":arrow-annotations")
project(":arrow-annotations").projectDir = file("modules/core/arrow-annotations")

include(":arrow-annotations-processor")
project(":arrow-annotations-processor").projectDir = file("modules/core/arrow-annotations-processor")

include(":arrow-annotations-processor-test")
project(":arrow-annotations-processor-test").projectDir = file("modules/core/arrow-annotations-processor-test")

include(":arrow-kindedj")
project(":arrow-kindedj").projectDir = file("modules/core/arrow-kindedj")

include(":arrow-test")
project(":arrow-test").projectDir = file("modules/core/arrow-test")

include(":arrow-validation")
project(":arrow-validation").projectDir = file("modules/core/arrow-validation")

include(":arrow-examples")
project(":arrow-examples").projectDir = file("modules/core/arrow-examples")

/** Effects **/

include(":arrow-effects")
project(":arrow-effects").projectDir = file("modules/effects/arrow-effects")

include(":arrow-effects-rx2")
project(":arrow-effects-rx2").projectDir = file("modules/effects/arrow-effects-rx2")

include(":arrow-effects-kotlinx-coroutines")
project(":arrow-effects-kotlinx-coroutines").projectDir = file("modules/effects/arrow-effects-kotlinx-coroutines")

/** Recursion **/

include(":arrow-recursion")
project(":arrow-recursion").projectDir = file("modules/recursion-schemes/arrow-recursion")

/** Docs **/

include(":arrow-docs")
project(":arrow-docs").projectDir = file("modules/docs/arrow-docs")

/** Optics **/

include(":arrow-optics")
project(":arrow-optics").projectDir = file("modules/optics/arrow-optics")

/** Dagger **/

include(":arrow-dagger")
project(":arrow-dagger").projectDir = file("modules/dagger/arrow-dagger")

include(":arrow-dagger-effects")
project(":arrow-dagger-effects").projectDir = file("modules/dagger/arrow-dagger-effects")

include(":arrow-dagger-effects-rx2")
project(":arrow-dagger-effects-rx2").projectDir = file("modules/dagger/arrow-dagger-effects-rx2")

include(":arrow-dagger-effects-kotlinx-coroutines")
project(":arrow-dagger-effects-kotlinx-coroutines").projectDir = file("modules/dagger/arrow-dagger-effects-kotlinx-coroutines")


rootProject.name = "arrow-parent"
