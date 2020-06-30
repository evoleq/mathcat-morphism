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
package org.evoleq.math.cat.morphism

import org.evoleq.math.cat.marker.MathCatDsl
import org.evoleq.math.cat.marker.MathSpeakDsl
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * Represent morphisms and hom-sets of the underlying type system (part of SET)
 */
interface Morphism<S, T> :  ReadOnlyProperty<Any?, (S)->T> {
    val morphism: (S)->T
    
    override fun getValue(thisRef: Any?, property: KProperty<*>): (S) -> T = { s ->morphism(s)}
}

/**
 * Constructor function of [Morphism]
 */
@MathCatDsl
@Suppress("FunctionName")
fun <S, T> Morphism(f: (S)->T): Morphism<S, T> = object : Morphism<S, T> {
    override val morphism: (S) -> T= f
}

/**********************************************************************************************************************
 *
 * Composition
 *
 **********************************************************************************************************************/


/**
 * Compose [Morphism]s
 */
@MathSpeakDsl
infix fun <R, S, T> Morphism<S, T>.o(other: Morphism<R, S>): Morphism<R, T> = other * this

/**
 * Compose [Morphism]s in the mathematically negative sense (f*g = g o f)
 */
operator fun<S, T, U> Morphism<S, T>.times(other: Morphism<T, U>): Morphism<S, U> = Morphism {
    s -> other.morphism(  morphism(s) )
}

/**********************************************************************************************************************
 *
 * Functorial structure
 *
 **********************************************************************************************************************/
/**
 * Map
 */
@MathCatDsl
infix fun <S,T,U> Morphism<S, T>.map(f:(T)->U): Morphism<S, U> = Morphism ( f o by(this) )

/**
 * Contra-map
 */
@MathCatDsl
infix fun <R, S, T> Morphism<S, T>.coMap(f:(R)->S): Morphism<R, T> = Morphism ( by(this) o f)

/**********************************************************************************************************************
 *
 * Applicative structure
 *
 **********************************************************************************************************************/
/**
 * Apply method of the applicative [Morphism]
 */
@MathCatDsl
fun <R, S, T> Morphism<R, (S)->T>.apply(): (Morphism<R, S>)->Morphism<R, T> = {
    mS -> this@apply bind { f -> mS map f }
}
/**
 * Apply method of the applicative [Morphism]
 */
@MathCatDsl
infix fun <R, S, T> Morphism<R, (S)->T>.apply(other: Morphism<R, S>): Morphism<R, T> = apply()(other)

/**********************************************************************************************************************
 *
 * Monadic structure
 *
 **********************************************************************************************************************/
/**
 * Return the [Morphism] monad
 */
@MathCatDsl
@Suppress("FunctionName")
fun <S, T> ReturnMorphism(t: T): Morphism<S, T> = Morphism { t }

/**
 * Multiplication of th [Morphism] monad
 */
@MathCatDsl
fun <S, T> Morphism<S, Morphism<S, T>>.multiply(): Morphism<S, T> = Morphism{s -> by(by(this)(s))(s)}

/**
 * Bind function of [Morphism]
 */
@MathCatDsl
infix fun <S, T, U> Morphism<S, T>.bind(f: (T)->Morphism<S, U>): Morphism<S, U> = (this map f).multiply()


/**
 * Kleisli [Morphism]
 */
interface KlMorphism<B, S, T> : Morphism<S, Morphism<B, T>>

/**
 * Constructor function for [KlMorphism]
 */
@MathCatDsl
@Suppress("FunctionName")
fun <B, S, T> KlMorphism(arrow: (S)->Morphism<B, T>): KlMorphism<B, S, T> = object : KlMorphism<B, S, T> {
    override val morphism: (S) -> Morphism<B, T> = arrow
}

/**
 * Multiplication of [KlMorphism]s
 */
operator fun <B, R, S, T> KlMorphism<B, R, S>.times(other: KlMorphism<B, S, T>): KlMorphism<B, R, T> = KlMorphism{
    r: R -> (by(this@times)(r) map by(other)).multiply()
}

