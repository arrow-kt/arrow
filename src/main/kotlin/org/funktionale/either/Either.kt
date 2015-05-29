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

package org.funktionale.either

/**
 * Created by IntelliJ IDEA.
 * @author Mario Arias
 * Date: 17/05/13
 * Time: 19:01
 */
@suppress("BASE_WITH_NULLABLE_UPPER_BOUND")
abstract public class Either<out L, out R> {

    public fun left(): LeftProjection<L, R> = LeftProjection(this)
    public fun right(): RightProjection<L, R> = RightProjection(this)

    public abstract fun component1(): L?
    public abstract fun component2(): R?

    public abstract fun isLeft(): Boolean
    public abstract fun isRight(): Boolean

    public fun<X> fold(fl: (L) -> X, fr: (R) -> X): X {
        return when (this) {
            is Left<L, R> -> fl(this.l)
            is Right<L, R> -> fr(this.r)
            else -> throw UnsupportedOperationException()
        }
    }

    public fun swap(): Either<R, L> {
        return when (this) {
            is Left<L, R> -> Right(this.l)
            is Right<L, R> -> Left(this.r)
            else -> throw UnsupportedOperationException()
        }
    }
}

public fun<T> Either<T, T>.merge(): T {
    return when (this) {
        is Left<T, T> -> this.l
        is Right<T, T> -> this.r
        else -> throw UnsupportedOperationException()
    }
}

public fun<L, R> Pair<L, R>.toLeft(): Left<L, R> {
    return Left(this.component1())
}

public fun<L, R> Pair<L, R>.toRight(): Right<L, R> {
    return Right(this.component2())
}

public fun<T> either(body: () -> T): Either<Exception, T> {
    return try {
        Right(body())
    } catch(e: Exception) {
        Left(e)
    }
}

