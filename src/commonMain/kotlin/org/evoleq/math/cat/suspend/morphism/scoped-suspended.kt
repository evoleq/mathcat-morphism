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

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import org.evoleq.math.cat.functor.Diagonal
import org.evoleq.math.cat.marker.MathCatDsl
import org.evoleq.math.cat.marker.MathSpeakDsl
import org.evoleq.math.cat.structure.x
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * You might wish to inherit from suspend extension functions directly: If<S, T> : suspend [CoroutineScope].(S)->T.
 * This is not possible! An elegant way to inherit from suspend functions is given by using delegation:
 */
interface ScopedSuspended<in S, out T> : ReadOnlyProperty<Any?, suspend CoroutineScope.(S) -> T> {
    val morphism: suspend CoroutineScope.(S)->T

    override fun getValue(thisRef: Any?, property: KProperty<*>): suspend CoroutineScope.(S) -> T = { s ->morphism(s)}
}

/**
 * Constructor function for [ScopedSuspended]
 */
@MathCatDsl
@Suppress("FunctionName")
fun <S, T> ScopedSuspended(function: suspend CoroutineScope.(S)->T): ScopedSuspended<S, T> = object : ScopedSuspended<S, T> {
    override val morphism: suspend CoroutineScope.(S) -> T = function
}

/**********************************************************************************************************************
 *
 * Composition
 *
 **********************************************************************************************************************/

/**
 * Function composition for [ScopedSuspended]
 */
@MathSpeakDsl
suspend infix fun <R, S, T> ScopedSuspended<S, T>.o(other: ScopedSuspended<R, S>): ScopedSuspended<R, T> = other * this
/**
 * Compose [ScopedSuspended]s in the mathematically negative sense (f*g = g o f)
 */
suspend operator fun<S, T, U> ScopedSuspended<S, T>.times(other: ScopedSuspended<T, U>): ScopedSuspended<S, U> = ScopedSuspended {
    s -> other.morphism( this, morphism(s) ) }

/**
 * Function composition for scoped suspended functions
 */
@MathSpeakDsl
suspend infix fun <R, S, T> (suspend CoroutineScope.(S)->T).o(other: suspend CoroutineScope.(R)->S): suspend CoroutineScope.(R)->T = {
    r -> this@o(other(r))
}
/**********************************************************************************************************************
 *
 * Functorial structure
 *
 **********************************************************************************************************************/
/**
 * Map [ScopedSuspended]
 */
@MathCatDsl
suspend infix fun <S, T, U> ScopedSuspended<S, T>.map(f: suspend CoroutineScope.(T)->U): ScopedSuspended<S, U> = ScopedSuspended {
    s -> f(by(this@map)(s))
}

/**
 * Contra map [ScopedSuspended]
 */
@MathCatDsl
suspend infix fun <R, S, T> ScopedSuspended<S, T>.coMap(f: suspend CoroutineScope.(R)->S): ScopedSuspended<R, T> = ScopedSuspended {
    s -> by(this@coMap)(f(s))
}

/**********************************************************************************************************************
 *
 * Applicative
 *
 **********************************************************************************************************************/

/**
 * Apply function of the applicative [ScopedSuspended]
 */
@MathCatDsl
suspend fun <R, S, T> (ScopedSuspended<R, suspend CoroutineScope.(S)->T>).apply(): suspend CoroutineScope.(ScopedSuspended<R, S>)->ScopedSuspended<R, T> = {
    sS -> ScopedSuspended{r ->   (by(this@apply) x by(sS)) (Diagonal(r)).evaluate()
} }

/**
 * Apply function of the applicative [ScopedSuspended]
 */
@MathCatDsl
suspend infix fun <R, S, T> (ScopedSuspended<R, suspend CoroutineScope.(S)->T>).apply(other: ScopedSuspended<R, S>): ScopedSuspended<R, T> = coroutineScope { apply()(other) }

/**********************************************************************************************************************
 *
 * Monadic structure
 *
 **********************************************************************************************************************/
/**
 * Return function of the [ScopedSuspended] monad
 */
@MathCatDsl
@Suppress("FunctionName")
fun <S, T> ReturnScopedSuspended(value: T): ScopedSuspended<S, T> = ScopedSuspended { value }

/**
 * Multiplication on the [ScopedSuspended] monad
 */
suspend fun <S, T> ScopedSuspended<S, ScopedSuspended<S, T>>.multiply(): ScopedSuspended<S, T> = ScopedSuspended{
    s -> by(by(this@multiply)(s))(s)
}

/**
 * Bind function on the [ScopedSuspended] monad
 */
suspend fun <S, T, U> ScopedSuspended<S, T>.bind(f: suspend CoroutineScope.(T)->ScopedSuspended<S, U>): ScopedSuspended<S, U> = (this map f).multiply()



/**
 * Kleisli [ScopedSuspended]
 */
interface KlScopedSuspended<B, S, T> : ScopedSuspended<S, ScopedSuspended<B, T>>

/**
 * Constructor function for [KlScopedSuspended]
 */
@MathCatDsl
@Suppress("FunctionName")
fun <B, S, T> KlScopedSuspended(arrow: suspend CoroutineScope.(S)-> ScopedSuspended<B, T>): KlScopedSuspended<B, S, T> = object : KlScopedSuspended<B, S, T> {
    override val morphism: suspend CoroutineScope.(S) -> ScopedSuspended<B, T> = arrow
}

/**
 * Identity element of the [KlScopedSuspended]
 */
@MathCatDsl
@Suppress("FunctionName")
fun <S, T> KlReturnScopedSuspended(t: T): KlScopedSuspended<S, T, T> = KlScopedSuspended { ReturnScopedSuspended(t) }

/**
 * Multiplication of [KlScopedSuspended]s
 */
operator fun <B, R, S, T> KlScopedSuspended<B, R, S>.times(other: KlScopedSuspended<B, S, T>): KlScopedSuspended<B, R, T> = KlScopedSuspended{
    r: R -> ((by(this@times))(r) map by(other)).multiply()
}
