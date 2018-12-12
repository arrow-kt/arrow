import arrow.effects.IO
import arrow.effects.Ref
import arrow.effects.instances.io.monad.flatMap
import arrow.effects.instances.io.monadDefer.monadDefer

fun main(args: Array<String>) {

  Ref.of(0, IO.monadDefer()).flatMap { ref ->
    IO.just(1).bracketCase(use = { IO.unit }, release = { i, _ ->
      ref.set(i + 1)
    }).flatMap { ref.get }
  }.unsafeRunSync()
    .let(::println)

}