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

package katz.data

/**
 * State[S, A] is basically a function S => (S, A),
 * where S is the type that represents your state
 * and A is the result the function produces. In addition
 * to returning the result of type A, the function
 * returns a new S value, which is the updated state.
 */
class State<S, out A>(val runF: (S) -> Pair<S, A>) {

    fun run(s: S): Pair<S, A> {
        return runF(s)
    }

    fun runA(s: S): A = run(s).second

    fun runS(s: S): S = run(s).first

    fun <B> map(f: (A) -> B): State<S, B> {
        return State { s1 ->
            run(s1).let { (s2, a2) -> Pair(s2, f(a2)) }
        }
    }

    fun <B> flatMap(f: (A) -> State<S, B>): State<S, B> {
        return State { s1 ->
            run(s1).let { (s2, a2) -> f(a2).run(s2) }
        }
    }

}
