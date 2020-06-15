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

interface Morphism<S, T> : ReadOnlyProperty<Any?, (S)->T> {
    val morphism: (S)->T
    
    override fun getValue(thisRef: Any?, property: KProperty<*>): (S) -> T = { s ->morphism(s)}
    
    
    operator fun<T1> times(other: Morphism<T, T1>): Morphism<S, T1> = Morphism {
        s -> other.morphism(  morphism(s) ) }
    
    @MathSpeakDsl
    infix fun <R> o(other: Morphism<R, S>): Morphism<R, T> = other * this
}

@MathCatDsl
@Suppress("FunctionName")
fun <S, T> Morphism(f: (S)->T): Morphism<S, T> = object : Morphism<S, T> {
    override val morphism: (S) -> T= f
}