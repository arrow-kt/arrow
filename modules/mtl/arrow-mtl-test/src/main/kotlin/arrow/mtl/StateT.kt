package arrow.mtl

import arrow.Kind
import arrow.core.Tuple2
import arrow.core.extensions.tuple2.eq.eq
import arrow.mtl.StateT
import arrow.mtl.StateTFun
import arrow.mtl.StateTPartialOf
import arrow.mtl.fix
import arrow.mtl.runM
import arrow.test.generators.GenK
import arrow.test.generators.tuple2
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import arrow.typeclasses.Monad
import io.kotlintest.properties.Gen



