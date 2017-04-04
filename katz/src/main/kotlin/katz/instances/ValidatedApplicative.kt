/*
 * Copyright (C) 2017 The Katz Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package katz

class ValidatedApplicative<E> : Applicative<ValidatedF<E>> {

    override fun <A> pure(a: A): Validated<E, A> = Validated.Valid(a)

    override fun <A, B> ap(fa: ValidatedKind<E, A>, ff: HK<ValidatedF<E>, (A) -> B>): Validated<E, B> =
            flatMap(fa, { a -> map(ff, { f -> f(a) }) }).ev()

    fun <A, B> flatMap(fa: ValidatedKind<E, A>, f: (A) -> ValidatedKind<E, B>): ValidatedKind<E, B> =
            fa.ev().fold({ Validated.Invalid(it) }, { f(it).ev() })
}

fun <E, A> ValidatedKind<E, A>.ev(): Validated<E, A> = this as Validated<E, A>
