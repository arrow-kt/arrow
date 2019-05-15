package arrow.effects

import arrow.test.generators.either
import arrow.test.generators.functionAToB
import arrow.test.generators.throwable
import io.kotlintest.properties.Gen

// Based on Arbitrary[IO[A]] instance in cats.effects
fun <A> Gen.Companion.fx(genA: Gen<A>): Gen<IO<A>> = Gen.oneOf(
  genA.map(IO.Companion::just), // Just
  genA.map { IO { it } }, // Invoke
  Gen.throwable().map(IO.Companion::raiseError), // RaiseError
  Gen.either(Gen.throwable(), genA).map { IO.async<A> { _, cb -> cb(it) } }, // IO.Async
  Gen.either(Gen.throwable(), genA).map { IO.asyncF<A> { _, cb -> IO { cb(it) } } }, // IO.AsyncF
  fx(genA).map { fx -> // Single cancellable IO
    IO.ConnectionSwitch(fx, IO.ConnectionSwitch.makeCancelable, IO.ConnectionSwitch.revertToOld)
  },
  Gen.either(Gen.throwable(), fx(genA)).map { // NestedAsync
    IO.async<IO<A>> { _, cb -> cb(it) }.flatMap { x -> x }
  },
  genA.map { IO { it }.flatMap { IO.just(it) } }, // BindSuspend
  Gen.bind(fx(genA), Gen.functionAToB<A, IO<A>>(fx(genA))) { fx, f -> fx.flatMap(f) }, // FlatMapOne
  Gen.bind(fx(genA), Gen.functionAToB<A, A>(genA)) { fx, f -> fx.map(f) }, // MapOne
  Gen.bind(fx(genA), Gen.functionAToB<A, A>(genA), Gen.functionAToB<A, A>(genA)) { fx, f, g -> fx.map(f).map(g) } // MapTwo
)
