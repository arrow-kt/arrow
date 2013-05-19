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

package org.funktionale.option

import org.funktionale.either.Either
import org.funktionale.either.Left
import org.funktionale.either.Right

/**
 * Created by IntelliJ IDEA.
 * @author Mario Arias
 * Date: 17/05/13
 * Time: 12:53
 */
public abstract class Option<out T> {
    public abstract fun isEmpty(): Boolean

    public fun nonEmpty(): Boolean = isDefined()

    public fun isDefined(): Boolean = !isEmpty()

    public abstract fun get(): T

    public fun getOrElse(default: () -> T): T {
        return if(isEmpty()){
            default()
        }else{
            get()
        }
    }

    public fun orNull(): T? {
        return if(isEmpty()){
            null
        }else{
            get()
        }
    }

    public fun<R> map(f: (T) -> R): Option<R> {
        return if(isEmpty()){
            none
        } else{
            Some(f(get()))
        }
    }

    public fun<R> fold(ifEmpty: () -> R, f: (T) -> R): R {
        return if(isEmpty()){
            ifEmpty()
        }else{
            f(get())
        }
    }

    public fun<R> flatMap(f: (T) -> Option<T>): Option<T> {
        return if(isEmpty()){
            none
        } else{
            f(get())
        }
    }

    public fun filter(predicate: (T) -> Boolean): Option<T> {
        return if(nonEmpty() && predicate(get())){
            this
        } else{
            none
        }
    }

    public fun filterNot(predicate: (T) -> Boolean): Option<T> {
        return if(nonEmpty() && !predicate(get())){
            this
        } else{
            none
        }
    }

    public fun exists(predicate: (T) -> Boolean): Boolean {
        return nonEmpty() && predicate(get())
    }

    public fun forEach(f: (T) -> Unit) {
        if(nonEmpty()) f(get())
    }

    public fun orElse(alternative: ()->Option<T>): Option<T> {
        return if(isEmpty()){
            alternative()
        } else{
            this
        }
    }

    public fun toList(): List<T> {
        return if(isEmpty()){
            listOf()
        }else{
            listOf(get())
        }
    }

    public fun<X> toRight(left: () -> X): Either<X, T> {
        return if(isEmpty()){
            Left(left())
        }else{
            Right(get())
        }
    }

    public fun<X> toLeft(right: () -> X): Either<T, X> {
        return if(isEmpty()){
            Right(right())
        }else{
            Left(get())
        }
    }


}

public fun<T> T?.toOption():Option<T>{
    return if(this != null){
        Some(this!!)
    } else{
        none
    }
}



