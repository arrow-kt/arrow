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

/**
 * The dual of monads, used to extract values from F
 */
interface Comonad<F> {

    fun <A, B> coflatMap(fa: HK<F, A>, f: (HK<F, A>) -> B): HK<F, B>

    fun <A> extract(fa: HK<F, A>) : A

}
