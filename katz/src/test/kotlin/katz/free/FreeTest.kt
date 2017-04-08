/*
 * Copyright (C) 2017 The Katz Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package katz

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldBe
import org.junit.runner.RunWith

sealed class Ops<A> : HK<Ops.F, A>{

    class F private constructor()

    data class Add(val a : Int, val y: Int) : Ops<Int>()
    data class Subtract(val a : Int, val y: Int) : Ops<Int>()

    companion object : FreeMonad<Ops.F> {
        fun add(n: Int, y : Int): Free<Ops.F, Int> = Free.liftF(Ops.Add(n, y))
        fun subtract(n: Int, y : Int): Free<Ops.F, Int> = Free.liftF(Ops.Subtract(n, y))
    }
}

fun <A> HK<Ops.F, A>.ev(): Ops<A> = this as Ops<A>

val optionInterpreter : FunctionK<Ops.F, Option.F> = object : FunctionK<Ops.F, Option.F> {
    override fun <A> invoke(fa: HK<Ops.F, A>): Option<A> {
        val op = fa.ev()
        return when (op) {
            is Ops.Add -> Option.Some(op.a + op.y)
            is Ops.Subtract -> Option.Some(op.a - op.y)
        } as Option<A>
    }
}

@RunWith(KTestJUnitRunner::class)
class FreeTest : UnitSpec() {

    val program = Ops.binding {
        val added = ! Ops.add(10, 10)
        val substracted = ! Ops.subtract(added, 50)
        yields(substracted)
    }.ev()

    init {

        "Can interpret an ADT as Free operations" {
            program.foldMap(Option, optionInterpreter).ev() shouldBe Option.Some(0)
        }

    }
}
