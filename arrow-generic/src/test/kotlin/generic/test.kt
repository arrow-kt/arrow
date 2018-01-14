package generic

import arrow.core.*
import arrow.generic.*
import arrow.syntax.monoid.empty

object test {

    fun Person.toJson(): StringBuilder =
            StringBuilder()
                    .append('{')
                    .append(""""name"""", ':', """"$name"""", ',')
                    .append(""""age"""", ':', age)
                    .append('}')

    @JvmStatic fun main(args: Array<String>): Unit {
        val p = Person("raul", 37)
        println(p.tupled())
        println(p.foldLabelled { (np, name), (ap, age) ->
            "$np : $name, $ap : $age"
        })
        println(p.tupledLabelled())
        println(p.combine(p))
        println(p.empty())
        println(p.toJson())


    }

}