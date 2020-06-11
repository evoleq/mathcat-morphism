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

    suspend operator fun<T1> times(other: Suspended<T, T1>): Suspended<S, T1> = Suspended { s -> other.morphism(morphism(s))}
    
    @MathSpeakDsl
    suspend infix fun<R> o(other: Suspended<R, S>): Suspended<R, T> = other * this
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

/**
 * Function composition for suspend functions
 */
@MathSpeakDsl
suspend infix fun <R, S, T> (suspend (S)->T).o(other: suspend(R)->S): suspend (R)->T = {
    r -> this@o(other(r))
}


