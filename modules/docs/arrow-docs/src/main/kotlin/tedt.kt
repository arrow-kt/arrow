import arrow.core.Either
import arrow.core.None
import arrow.core.Right
import arrow.core.right
import arrow.effects.extensions.fx.async.shift
import arrow.effects.extensions.fx.monad.followedBy
import arrow.effects.suspended.fx.Fx
import io.kotlintest.shouldBe
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.newSingleThreadContext

val subProgram = Dispatchers.IO.shift()
  .followedBy(Fx.asyncF<Int> { _, cbb ->
    Fx { delay(500) }.map { cbb(Right(1)) }
  })

val program = Fx.asyncF { _, cb: (Either<Throwable, Int>) -> Unit ->
  val cancel = Fx.unsafeRunNonBlockingCancellable(subProgram) { cb(it) }
  Dispatchers.IO.shift().followedBy(Fx { cancel() })
}

fun main() {
  Fx.unsafeRunBlocking(program) shouldBe None
}
