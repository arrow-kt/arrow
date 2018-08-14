package arrow

import arrow.core.Option
import arrow.core.Try
import arrow.effects.ForIO
import arrow.effects.IO
import arrow.effects.console
import arrow.effects.fRandom
import arrow.effects.fix
import arrow.effects.monad
import arrow.typeclasses.Monad
import arrow.typeclasses.binding
import java.util.Random

/**
 * This sample is a simple translation in Kotlin (using arrow, of course) of this talk: https://youtu.be/sxudIMiOo68
 */


object ORandom : Random()

interface Console<F> {
    fun putStrLn(s: String): Kind<F, Unit>
    fun getStrLn(): Kind<F, String>
}

@instance(IO::class)
interface IOConsoleInstance : Console<ForIO> {

    override fun putStrLn(s: String): Kind<ForIO, Unit> = IO { println(s) }

    override fun getStrLn(): Kind<ForIO, String> = IO { readLine().orEmpty() }

}

interface FRandom<F> {
    fun nextInt(upper: Int): Kind<F, Int>
}

@instance(IO::class)
interface FRandomInstance: FRandom<ForIO> {
    override fun nextInt(upper: Int): Kind<ForIO, Int> = IO { ORandom.nextInt(upper) }
}

class MonadAndConsoleRandom<F>(M: Monad<F>, C: Console<F>, R: FRandom<F>): Monad<F> by M, Console<F> by C, FRandom<F> by R {

}

object FpToTheMax {

    fun parseInt(s: String): Option<Int> = Try { s.toInt() }.toOption()


    fun <F> checkContinue(MC: MonadAndConsoleRandom<F>, name: String): Kind<F,Boolean> = MC.binding {
        MC.putStrLn("Do you want to continue, $name?").bind()
        val input = MC.getStrLn().map { it.toLowerCase() }.bind()
        when (input) {
            "y" ->  MC.just(true)
            "n" -> MC.just(false)
            else -> checkContinue(MC, name)
        }.bind()
    }

    fun <F> gameLoop(MC: MonadAndConsoleRandom<F>, name: String): Kind<F, Unit> = MC.binding {
        val num = MC.nextInt(5).map { it + 1 }.bind()
        MC.putStrLn("Dear $name, please guess a number from 1 to 5:").bind()
        val input = MC.getStrLn().bind()
        parseInt(input).fold({ MC.putStrLn("You did not enter a number")}){ guess ->
            if (guess == num) MC.putStrLn("You guessed right, $name!")
            else MC.putStrLn("You guessed wrong, $name! The number was: $num")
        }.bind()
        val cont = checkContinue(MC, name).bind()
        (if (cont) gameLoop(MC, name) else MC.just(Unit)).bind()
    }

    fun <F> fMain(MC: MonadAndConsoleRandom<F>): Kind<F, Unit> = MC.binding {
        MC.putStrLn("What is your name?").bind()
        val name = MC.getStrLn().bind()
        MC.putStrLn("Hello $name, welcome to the game").bind()
        gameLoop(MC, name).bind()
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val r = fMain(MonadAndConsoleRandom(IO.monad(), IO.console(), IO.fRandom()))
        r.fix().unsafeRunSync()
    }

}
