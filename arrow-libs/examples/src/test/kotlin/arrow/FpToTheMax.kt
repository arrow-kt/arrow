package arrow

import arrow.core.None
import arrow.core.Option
import arrow.core.Tuple2
import arrow.core.toT
import arrow.fx.IO
import arrow.fx.extensions.io.monadDefer.monadDefer
import arrow.fx.fix
import arrow.fx.typeclasses.MonadDefer
import arrow.mtl.State
import arrow.mtl.StateApi
import arrow.mtl.StatePartialOf
import arrow.mtl.fix
import arrow.mtl.run
import arrow.typeclasses.Monad
import java.util.Random

/**
 * This sample is a simple translation in Kotlin (using arrow, of course) of this talk: https://youtu.be/sxudIMiOo68
 */
object ORandom : Random()

interface Console<F> {
  fun putStrLn(s: String): Kind<F, Unit>
  fun getStrLn(): Kind<F, String>
}

class ConsoleImpl<F>(val delay: MonadDefer<F>) : Console<F> {
  override fun putStrLn(s: String): Kind<F, Unit> = delay.later { println(s) }
  override fun getStrLn(): Kind<F, String> = delay.later { readLine().orEmpty() }
}

interface FRandom<F> {
  fun nextInt(upper: Int): Kind<F, Int>
}

class FRandomImpl<F>(val delay: MonadDefer<F>) : FRandom<F> {
  override fun nextInt(upper: Int): Kind<F, Int> = delay.later { ORandom.nextInt(upper) }
}

class MonadAndConsoleRandom<F>(M: Monad<F>, C: Console<F>, R: FRandom<F>) : Monad<F> by M, Console<F> by C, FRandom<F> by R

data class TestData(val input: List<String>, val output: List<String>, val nums: List<Int>) {
  fun putStrLn(s: String): Tuple2<TestData, Unit> = copy(output = output.plus(s)) toT Unit
  fun getStrLn(): Tuple2<TestData, String> = copy(input = input.drop(1)) toT input[0]
  fun nextInt(@Suppress("UNUSED_PARAMETER") upper: Int): Tuple2<TestData, Int> = copy(nums = nums.drop(1)) toT nums[0]
}

typealias ForTestIO = StatePartialOf<TestData>

// Helper to make it clearer.
fun <A> TestIO(f: (TestData) -> Tuple2<TestData, A>) = State(f)

class TestIORandom : FRandom<ForTestIO> {
  override fun nextInt(upper: Int): Kind<ForTestIO, Int> = TestIO { it.nextInt(upper) }
}

class TestIOConsole : Console<ForTestIO> {
  override fun putStrLn(s: String): Kind<ForTestIO, Unit> = TestIO { it.putStrLn(s) }
  override fun getStrLn(): Kind<ForTestIO, String> = TestIO { it.getStrLn() }
}

object FpToTheMax {

  fun parseInt(s: String): Option<Int> = try {
    Option.just(s.toInt())
  } catch (e: Throwable) {
    None
  }

  fun <F> MonadAndConsoleRandom<F>.checkContinue(name: String): Kind<F, Boolean> = fx.monad {
    putStrLn("Do you want to continue, $name? [y/n]")
    val input = !getStrLn().map { it.toLowerCase() }
    when (input) {
        "y" -> just(true)
        "n" -> just(false)
      else -> checkContinue(name)
    }.bind()
  }

  fun <F> MonadAndConsoleRandom<F>.gameLoop(name: String): Kind<F, Unit> = fx.monad {
    val num = !nextInt(5).map { it + 1 }
    putStrLn("Dear $name, please guess a number from 1 to 5:").bind()
    val input = !getStrLn()
    parseInt(input).fold({ putStrLn("You did not enter a number") }) { guess ->
      if (guess == num) putStrLn("You guessed right, $name!")
      else putStrLn("You guessed wrong, $name! The number was: $num")
    }.bind()
    val cont = !checkContinue(name)
    (if (cont) gameLoop(name) else just(Unit)).bind()
  }

  fun <F> MonadAndConsoleRandom<F>.fMain(): Kind<F, Unit> = fx.monad {
    !putStrLn("What is your name?")
    val name = !getStrLn()
    !putStrLn("Hello $name, welcome to the game")
    !gameLoop(name)
  }

  @JvmStatic
  fun main(args: Array<String>) {
    val module = IO.monadDefer().run {
      MonadAndConsoleRandom(this, ConsoleImpl(this), FRandomImpl(this))
    }
    val r = module.fMain()
    r.fix().unsafeRunSync()
  }

  fun test(): List<String> = run {
    val testData = TestData(listOf("Plop", "4", "n"), listOf(), listOf(3))

    val module: MonadAndConsoleRandom<ForTestIO> = MonadAndConsoleRandom(StateApi.monad(), TestIOConsole(), TestIORandom())

    module.fMain().fix().run(testData).a.output
  }
}
