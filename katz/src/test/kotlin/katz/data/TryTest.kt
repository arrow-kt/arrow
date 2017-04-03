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

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldBe
import katz.Try.*
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class TryTest : UnitSpec() {
    init {
        "invoke of any should be success" {
            Try.invoke { 1 } shouldBe Success(1)
        }

        "invoke of exception should be failure" {
            val ex = Exception()
            Try.invoke { throw ex } shouldBe Failure<Any>(ex)
        }

        "flatMap should modify entity" {
            val failure: Try<Int> = Failure(Exception())

            Success(1).flatMap { failure } shouldBe failure
            Success(1).flatMap { Success(2) } shouldBe Success(2)
            failure.flatMap { Success(2) } shouldBe failure
        }

        "map should modify value" {
            val failure: Try<Int> = Failure(Exception())

            Success(1).map { 2 } shouldBe Success(2)
            failure.map { 2 } shouldBe failure
        }

        "filter evaluates predicate" {
            val failure: Try<Int> = Failure(Exception())

            Success(1).filter { true } shouldBe Success(1)
            Success(1).filter { false } shouldBe Failure<Int>(PredicateException("Predicate does not hold for 1"))
            failure.filter { true } shouldBe failure
            failure.filter { false } shouldBe failure
        }

        "failed tries to swap" {
            val ex = Exception()
            val failure: Try<Int> = Failure(ex)

            Success(1).failed() shouldBe Failure<Int>(UnsupportedOperationException("Success.failed"))
            failure.failed() shouldBe Success(ex)
        }

        "fold should call left function on Failure" {
            Failure<Int>(Exception()).fold({ 2 }, { 3 }) shouldBe 2
        }

        "fold should call right function on Success" {
            Success(1).fold({ 2 }, { 3 }) shouldBe 3
        }

        "fold should call left function on Success with exception" {
            Success(1).fold({ 2 }, { throw Exception() }) shouldBe 2
        }

        "failure is failure and not success" {
            val failure = Failure<Int>(Exception())

            failure.isFailure shouldBe true
            failure.isSuccess shouldBe false
        }

        "success is success and not failure" {
            val success = Success(1)

            success.isFailure shouldBe false
            success.isSuccess shouldBe true
        }

        "getOrElse returns default if Failure" {
            Success(1).getOrElse { 2 } shouldBe 1
            Failure<Int>(Exception()).getOrElse { 2 } shouldBe 2
        }

        "recoverWith should modify Failure entity" {
            Success(1).recoverWith { Failure<Int>(Exception()) } shouldBe Success(1)
            Success(1).recoverWith { Success(2) } shouldBe Success(1)
            Failure<Int>(Exception()).recoverWith { Success(2) } shouldBe Success(2)
        }

        "recover should modify Failure value" {
            Success(1).recover { 2 } shouldBe Success(1)
            Failure<Int>(Exception()).recover { 2 } shouldBe Success(2)
        }

        "transform applies left function for Success" {
            Success(1).transform({ Success(2) }, { Success(3) }) shouldBe Success(2)
        }

        "transform applies right function for Failure" {
            Failure<Int>(Exception()).transform({ Success(2) }, { Success(3) }) shouldBe Success(3)
        }
    }
}
