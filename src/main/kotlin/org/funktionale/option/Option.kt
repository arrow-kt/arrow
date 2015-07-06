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

import org.funktionale.collections.prependTo
import org.funktionale.either.Either
import org.funktionale.either.Left
import org.funktionale.either.Right
import org.funktionale.utils.GetterOperation
import org.funktionale.utils.GetterOperationImpl

/**
 * Created by IntelliJ IDEA.
 * @author Mario Arias
 * Date: 17/05/13
 * Time: 12:53
 */
@suppress("BASE_WITH_NULLABLE_UPPER_BOUND")
public abstract class Option<out T> {
    public abstract fun isEmpty(): Boolean

    public fun nonEmpty(): Boolean = isDefined()

    public fun isDefined(): Boolean = !isEmpty()

    public abstract fun get(): T

    public fun orNull(): T? {
        return if (isEmpty()) {
            null
        } else {
            get()
        }
    }

    public fun<R> map(f: (T) -> R): Option<R> {
        return flatMap { Some(f(it)) }
    }

    public fun<P1, R> map(p1: Option<P1>, f: (T, P1) -> R): Option<R> {
        return flatMap { t -> p1.map { pp1 -> f(t, pp1) } }
    }

    public fun<R> fold(ifEmpty: () -> R, f: (T) -> R): R {
        return if (isEmpty()) {
            ifEmpty()
        } else {
            f(get())
        }
    }

    public fun<R> flatMap(f: (T) -> Option<R>): Option<R> {
        return if (isEmpty()) {
            None
        } else {
            f(get())
        }
    }

    public fun filter(predicate: (T) -> Boolean): Option<T> {
        return if (nonEmpty() && predicate(get())) {
            this
        } else {
            None
        }
    }

    public fun filterNot(predicate: (T) -> Boolean): Option<T> {
        return if (nonEmpty() && !predicate(get())) {
            this
        } else {
            None
        }
    }

    public fun exists(predicate: (T) -> Boolean): Boolean {
        return nonEmpty() && predicate(get())
    }

    public fun forEach(f: (T) -> Unit) {
        if (nonEmpty()) f(get())
    }


    public fun toList(): List<T> {
        return if (isEmpty()) {
            listOf()
        } else {
            listOf(get())
        }
    }

    public fun<X> toRight(left: () -> X): Either<X, T> {
        return if (isEmpty()) {
            Left(left())
        } else {
            Right(get())
        }
    }

    public fun<X> toLeft(right: () -> X): Either<T, X> {
        return if (isEmpty()) {
            Right(right())
        } else {
            Left(get())
        }
    }

}

public fun<T> Option<T>.getOrElse(default: () -> T): T {
    return if (isEmpty()) {
        default()
    } else {
        get()
    }
}

public fun<T> Option<T>.orElse(alternative: () -> Option<T>): Option<T> {
    return if (isEmpty()) {
        alternative()
    } else {
        this
    }
}

@suppress("BASE_WITH_NULLABLE_UPPER_BOUND")
public fun<T> T?.toOption(): Option<T> {
    return if (this != null) {
        Some(this)
    } else {
        None
    }
}

public fun<T> option(body: () -> T): Option<T> {
    return try {
        Some(body())
    } catch(e: Exception) {
        None
    }
}

public val<K, V> Map<K, V>.option: GetterOperation<K, Option<V>>
    get () {
        return GetterOperationImpl { k -> this[k].toOption() }
    }


public fun<T> Array<out T>.firstOption(): Option<T> {
    return firstOrNull().toOption()
}

public fun BooleanArray.firstOption(): Option<Boolean> {
    return firstOrNull().toOption()
}

public fun ByteArray.firstOption(): Option<Byte> {
    return firstOrNull().toOption()
}

public fun CharArray.firstOption(): Option<Char> {
    return firstOrNull().toOption()
}

public fun DoubleArray.firstOption(): Option<Double> {
    return firstOrNull().toOption()
}

public fun FloatArray.firstOption(): Option<Float> {
    return firstOrNull().toOption()
}


public fun IntArray.firstOption(): Option<Int> {
    return firstOrNull().toOption()
}


public fun LongArray.firstOption(): Option<Long> {
    return firstOrNull().toOption()
}


public fun ShortArray.firstOption(): Option<Short> {
    return firstOrNull().toOption()
}

public fun<T> Iterable<T>.firstOption(): Option<T> {
    return firstOrNull().toOption()
}

public fun<T> List<T>.firstOption(): Option<T> {
    return firstOrNull().toOption()
}

@deprecated("Migrate to using Sequence<T> and respective functions", ReplaceWith("Sequence<T>.firstOrNull().toOption()"))
public fun<T> Stream<T>.firstOption(): Option<T> {
    return firstOrNull().toOption()
}

public fun<T> Sequence<T>.firstOption(): Option<T> {
    return firstOrNull().toOption()
}


public fun String.firstOption(): Option<Char> {
    return firstOrNull().toOption()
}

public inline fun <T> Array<out T>.firstOption(predicate: (T) -> Boolean): Option<T> {
    return firstOrNull(predicate).toOption()
}

public inline fun BooleanArray.firstOption(predicate: (Boolean) -> Boolean): Option<Boolean> {
    return firstOrNull(predicate).toOption()
}

public inline fun ByteArray.firstOption(predicate: (Byte) -> Boolean): Option<Byte> {
    return firstOrNull(predicate).toOption()
}

public inline fun CharArray.firstOption(predicate: (Char) -> Boolean): Option<Char> {
    return firstOrNull(predicate).toOption()
}

public inline fun DoubleArray.firstOption(predicate: (Double) -> Boolean): Option<Double> {
    return firstOrNull(predicate).toOption()
}

public inline fun FloatArray.firstOption(predicate: (Float) -> Boolean): Option<Float> {
    return firstOrNull(predicate).toOption()
}

public inline fun IntArray.firstOption(predicate: (Int) -> Boolean): Option<Int> {
    return firstOrNull(predicate).toOption()
}

public inline fun LongArray.firstOption(predicate: (Long) -> Boolean): Option<Long> {
    return firstOrNull(predicate).toOption()
}

public inline fun ShortArray.firstOption(predicate: (Short) -> Boolean): Option<Short> {
    return firstOrNull(predicate).toOption()
}

public inline fun <T> Iterable<T>.firstOption(predicate: (T) -> Boolean): Option<T> {
    return firstOrNull(predicate).toOption()
}

@deprecated("Migrate to using Sequence<T> and respective functions", ReplaceWith("Sequence<T>.firstOrNull(predicate).toOption()"))
public inline fun <T> Stream<T>.firstOption(predicate: (T) -> Boolean): Option<T> {
    return firstOrNull(predicate).toOption()
}

public inline fun <T> Sequence<T>.firstOption(predicate: (T) -> Boolean): Option<T> {
    return firstOrNull(predicate).toOption()
}


public inline fun String.firstOption(predicate: (Char) -> Boolean): Option<Char> {
    return firstOrNull(predicate).toOption()
}

public fun<T, R> List<T>.traverse(f: (T) -> Option<R>): Option<List<R>> {
    return foldRight(Some(emptyList())) { i: T, accumulator: Option<List<R>> ->
        f(i).map(accumulator) { head: R, tail: List<R> ->
            head prependTo tail
        }
    }
}

public fun<T> List<Option<T>>.sequential(): Option<List<T>>{
    return traverse { it }
}

public fun<T> List<Option<T>>.flatten(): List<T> {
    return filter { it.isDefined() }.map { it.get() }
}

public fun<P1, R> Function1<P1, R>.optionLift(): (Option<P1>) -> Option<R> {
    return { it.map(this) }
}