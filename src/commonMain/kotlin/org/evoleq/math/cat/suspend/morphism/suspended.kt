/**
 * Copyright (c) 2020 Dr. Florian Schmidt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.evoleq.math.cat.suspend.morphism

import org.evoleq.math.cat.marker.MathCatDsl
import org.evoleq.math.cat.marker.MathSpeakDsl
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * You might wish to inherit from suspend functions directly: If<S, T>  : suspend (S)->T.
 * This is not possible! An elegant way to inherit from suspend functions is given by using delegation:
 */
interface Suspended<in S, out T> : ReadOnlyProperty<Any?, suspend (S)->T> {
    val morphism: suspend (S)->T

    override fun getValue(thisRef: Any?, property: KProperty<*>): suspend (S) -> T = morphism
}

/**
 * Constructor function for [Suspended]
 */
@MathCatDsl
@Suppress("FunctionName")
fun <S, T> Suspended(function: suspend (S)->T): Suspended<S, T> = object : Suspended<S, T> {
    override val morphism: suspend (S) -> T
        get() = function
}
/**********************************************************************************************************************
 *
 * Composition
 *
 **********************************************************************************************************************/

/**
 * Compose [Suspended]s in the mathematical sense
 */
@MathSpeakDsl
suspend infix fun<R, S, T> Suspended<S, T>.o(other: Suspended<R, S>): Suspended<R, T> = other * this

/**
 * Compose [Suspended]s in the mathematical negative sense (f*g = g o f)
 */
suspend operator fun<S, T, U> Suspended<S, T>.times(other: Suspended<T, U>): Suspended<S, U> = Suspended { s -> other.morphism(morphism(s))}

/**********************************************************************************************************************
 *
 * Functorial structure
 *
 **********************************************************************************************************************/
/**
 * Map [Suspended]
 */
@MathCatDsl
suspend infix fun <S, T, U> Suspended<S, T>.map(f: suspend (T)->U): Suspended<S, U> = Suspended{
    s -> f(by(this)(s))
}

/**
 * Contra-map [Suspended]
 */
@MathCatDsl
suspend infix fun <R, S, T> Suspended<S, T>.coMap(f: suspend (R)->S): Suspended<R, T> = Suspended{
    r -> by(this)(f(r))
}

/**********************************************************************************************************************
 *
 * Applicative
 *
 **********************************************************************************************************************/

/**
 * Apply method of the applicative [Suspended]
 */
@MathCatDsl
fun <R, S, T> Suspended<R, (S) -> T>.apply(): (Suspended<R, S>)-> Suspended<R, T> = {
    mS -> Suspended{r ->
    val f = (by(this@apply))(r)
    val s = (by(mS))(r)
    f(s)
}
}
/**
 * Apply method of the applicative [Suspended]
 */
@MathCatDsl
infix fun <R, S, T> Suspended<R, (S) -> T>.apply(other: Suspended<R, S>): Suspended<R, T> = apply()(other)

/**********************************************************************************************************************
 *
 * Monadic structure
 *
 **********************************************************************************************************************/

/**
 * Return the [Suspended] monad
 */
@MathCatDsl
@Suppress("FunctionName")
fun <S, T> ReturnSuspended(t: T): Suspended<S, T> = Suspended { t }

/**
 * Multiplication of th [Suspended] monad
 */
@MathCatDsl
suspend fun <S, T> Suspended<S, Suspended<S, T>>.multiply(): Suspended<S, T> = Suspended{s -> (by(by(this)(s)))(s)}

/**
 * Bind function of [Suspended]
 */
@MathCatDsl
suspend infix fun <S, T, U> Suspended<S, T>.bind(f: suspend (T)-> Suspended<S, U>): Suspended<S, U> = (this map f).multiply()


/**
 * Kleisli [Suspended]
 */
interface KlSuspended<B, S, T> : Suspended<S, Suspended<B, T>>

/**
 * Constructor function for [KlSuspended] 
 */
@MathCatDsl
@Suppress("FunctionName")
fun <B, S, T> KlSuspended(arrow: suspend (S)-> Suspended<B, T>): KlSuspended<B, S, T> = object : KlSuspended<B, S, T> {
    override val morphism: suspend (S) -> Suspended<B, T> = arrow
}

/**
 * Identity element of the [KlSuspended]
 */
@MathCatDsl
@Suppress("FunctionName")
fun <S, T> KlReturnSuspended(t: T): KlSuspended<S, T, T> = KlSuspended { ReturnSuspended(t) }

/**
 * Multiplication of [KlSuspended]s
 */
operator fun <B, R, S, T> KlSuspended<B, R, S>.times(other: KlSuspended<B, S, T>): KlSuspended<B, R, T> = KlSuspended{
    r: R -> ((by(this@times))(r) map by(other)).multiply()
}




