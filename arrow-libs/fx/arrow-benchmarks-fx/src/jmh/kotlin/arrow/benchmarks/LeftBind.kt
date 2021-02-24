package arrow.benchmarks

import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.CompilerControl
import org.openjdk.jmh.annotations.Fork
import org.openjdk.jmh.annotations.Measurement
import org.openjdk.jmh.annotations.Param
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State
import org.openjdk.jmh.annotations.Warmup
import java.util.concurrent.TimeUnit

@State(Scope.Thread)
@Fork(2)
@Warmup(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10)
@CompilerControl(CompilerControl.Mode.DONT_INLINE)
open class LeftBind {

  @Param("10000")
  var size: Int = 0

  @Param("100")
  var depth: Int = 0

  @Benchmark
  fun catsIO(): Int =
    arrow.benchmarks.effects.scala.cats.`LeftBind$`.`MODULE$`.unsafeIOLeftBindLoop(depth, size, 0)

  @Benchmark
  fun scalazZIO(): Int =
    arrow.benchmarks.effects.scala.zio.`LeftBind$`.`MODULE$`.unsafeIOLeftBindLoop(depth, size, 0)

  @Benchmark
  fun kio(): Int =
    arrow.benchmarks.effects.kio.LeftBind.leftBind(depth, size, 0)

}
