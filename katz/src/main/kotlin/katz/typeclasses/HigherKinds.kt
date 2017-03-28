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

interface HK<out F, out A>

typealias HK2<F, A, B> = HK<HK<F, A>, B>

typealias HK3<F, A, B, C> = HK<HK2<F, A, B>, C>

typealias HK4<F, A, B, C, D> = HK<HK3<F, A, B, C>, D>

typealias HK5<F, A, B, C, D, E> = HK<HK4<F, A, B, C, D>, E>
