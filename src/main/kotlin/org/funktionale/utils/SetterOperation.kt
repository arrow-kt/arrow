/*
 * Copyright 2013 Mario Arias
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.funktionale.utils

/**
 * Created by IntelliJ IDEA.
 * @author Mario Arias
 * Date: 15/05/13
 * Time: 13:06
 */
interface SetterOperation<K, V> {
    val setter: (K, V) -> Unit

    public fun set(key: K, value: V) {
        setter(key, value)
    }
}

class SetterOperationImpl<K, V>(override val setter: (K, V) -> Unit) : SetterOperation<K, V>