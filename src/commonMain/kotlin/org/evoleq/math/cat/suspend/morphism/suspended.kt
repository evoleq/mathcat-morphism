/**
 * Copyright (c) 2018-2020 Dr. Florian Schmidt
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
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

interface Suspended<in S, out T> : ReadOnlyProperty<Any?, suspend (S)->T> {
    val morphism: suspend (S)->T

    override fun getValue(thisRef: Any?, property: KProperty<*>): suspend (S) -> T = morphism

    suspend fun<T1> times(other: Suspended<T, T1>): Suspended<S, T1> = Suspended { s -> other.morphism(morphism(s))}
}

@Suppress("FunctionName")
@MathCatDsl
fun <S, T> Suspended(function: suspend (S)->T): Suspended<S, T> = object : Suspended<S, T> {
    override val morphism: suspend (S) -> T
        get() = function
}


