package arrow

import arrow.core.*
import arrow.data.*
import arrow.effects.*
import arrow.effects.extensions.io.monadDefer.monadDefer
import arrow.effects.typeclasses.MonadDefer
import arrow.core.extensions.id.monad.monad
import arrow.data.extensions.statet.monad.monad
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

class ConsoleInstance<F>(val delay: MonadDefer<F>) : Console<F> {
    override fun putStrLn(s: String): Kind<F, Unit> = delay.delay { println(s) }
    override fun getStrLn(): Kind<F, String> = delay.delay { readLine().orEmpty() }
}

interface FRandom<F> {
    fun nextInt(upper: Int): Kind<F, Int>
}

class FRandomInstance<F>(val delay: MonadDefer<F>) : FRandom<F> {
    override fun nextInt(upper: Int): Kind<F, Int> = delay.delay { ORandom.nextInt(upper) }
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

class TestIORandomInstance: FRandom<ForTestIO> {
    override fun nextInt(upper: Int): Kind<ForTestIO, Int> = TestIO { it.nextInt(upper) }
}

class TestIOConsoleInstance: Console<ForTestIO> {
    override fun putStrLn(s: String): Kind<ForTestIO, Unit> = TestIO { it.putStrLn(s)}
    override fun getStrLn(): Kind<ForTestIO, String> = TestIO { it.getStrLn() }
}



object FpToTheMax {

    fun parseInt(s: String): Option<Int> = Try { s.toInt() }.toOption()

    fun <F> MonadAndConsoleRandom<F>.checkContinue(name: String): Kind<F, Boolean> = binding {
        putStrLn("Do you want to continue, $name?").bind()
        val input = getStrLn().map { it.toLowerCase() }.bind()
        when (input) {
            "y" -> just(true)
            "n" -> just(false)
            else -> checkContinue(name)
        }.bind()
    }

    fun <F> MonadAndConsoleRandom<F>.gameLoop(name: String): Kind<F, Unit> = binding {
        val num = nextInt(5).map { it + 1 }.bind()
        putStrLn("Dear $name, please guess a number from 1 to 5:").bind()
        val input = getStrLn().bind()
        parseInt(input).fold({ putStrLn("You did not enter a number") }) { guess ->
            if (guess == num) putStrLn("You guessed right, $name!")
            else putStrLn("You guessed wrong, $name! The number was: $num")
        }.bind()
        val cont = checkContinue(name).bind()
        (if (cont) gameLoop(name) else just(Unit)).bind()
    }

    fun <F> MonadAndConsoleRandom<F>.fMain(): Kind<F, Unit> = binding {
        putStrLn("What is your name?").bind()
        val name = getStrLn().bind()
        putStrLn("Hello $name, welcome to the game").bind()
        gameLoop(name).bind()
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val module = IO.monadDefer().run {
            MonadAndConsoleRandom(this, ConsoleInstance(this), FRandomInstance(this))
        }
        val r = module.fMain()
        r.fix().unsafeRunSync()
    }



    fun test(): List<String> = run {
        val testData: TestData = TestData(listOf("Plop", "4", "n"), listOf(), listOf(3))

        val m: Monad<ForTestIO> = StateT.monad(Id.monad())
        val module: MonadAndConsoleRandom<ForTestIO> = MonadAndConsoleRandom(m, TestIOConsoleInstance(), TestIORandomInstance() )

        module.fMain().fix().run(testData).a.output
    }

}

