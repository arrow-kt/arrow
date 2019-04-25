package arrow.effects

import arrow.effects.suspended.fx.Fx
import arrow.test.generators.either
import arrow.test.generators.functionAToB
import arrow.test.generators.throwable
import io.kotlintest.properties.Gen

// Based on Arbitrary[IO[A]] instance in cats.effects
fun <A> Gen.Companion.fx(genA: Gen<A>): Gen<Fx<A>> = Gen.oneOf(
  genA.map(Fx.Companion::just), // Just
  genA.map { Fx { it } }, // Invoke
  Gen.throwable().map(Fx.Companion::raiseError), // RaiseError
  Gen.either(Gen.throwable(), genA).map { Fx.async<A> { _, cb -> cb(it) } }, // Fx.Async
  Gen.either(Gen.throwable(), genA).map { Fx.asyncF<A> { _, cb -> Fx { cb(it) } } }, // Fx.AsyncF
  fx(genA).map { fx -> // Single cancellable Fx
    Fx.ConnectionSwitch(fx, Fx.ConnectionSwitch.makeCancelable, Fx.ConnectionSwitch.revertToOld)
  },
  Gen.either(Gen.throwable(), fx(genA)).map { // NestedAsync
    Fx.async<Fx<A>> { _, cb -> cb(it) }.flatMap { x -> x }
  },
  genA.map { Fx { it }.flatMap { Fx.just(it) } }, // BindSuspend
  Gen.bind(fx(genA), Gen.functionAToB<A, Fx<A>>(fx(genA))) { fx, f -> fx.flatMap(f) }, // FlatMapOne
  Gen.bind(fx(genA), Gen.functionAToB<A, A>(genA)) { fx, f -> fx.map(f) }, // MapOne
  Gen.bind(fx(genA), Gen.functionAToB<A, A>(genA), Gen.functionAToB<A, A>(genA)) { fx, f, g -> fx.map(f).map(g) } // MapTwo
)
