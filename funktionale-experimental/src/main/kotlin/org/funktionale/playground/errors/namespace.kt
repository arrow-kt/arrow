/*
 * Copyright 2013 - 2016 Mario Arias
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

package org.funktionale.playground.errors

sealed class Try<T>{
    class Success<T>(t:T):Try<T>()
    class Failure<T>(t:Throwable):Try<T>()

    companion object{
        operator fun <T> invoke(f:() -> T):Try<T> = try{
            Success(f())
        } catch (e:Throwable) {
            Failure<T>(e)
        }
    }
}

fun main(args: Array<String>) {
    //val d = Try{ 1 }
}

