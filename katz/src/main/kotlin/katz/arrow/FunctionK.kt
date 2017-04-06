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

interface FunctionK<in F, out G> {

    /**
     * Applies this functor transformation from `F` to `G`
     */
    operator fun <A> invoke(fa: HK<F, A>): HK<G, A>

}

fun <F, G, H> FunctionK<F, G>.or(h: FunctionK<H, G>): FunctionK<CoproductFG<F, H>, G> =
        object : FunctionK<CoproductFG<F, H>, G> {
            override fun <A> invoke(fa: CoproductKind<F, H, A>): HK<G, A> {
                return fa.ev().fold(this@or, h)
            }
        }