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
import org.evoleq.math.cat.marker.MathCatDsl


/**
 * Force delegation by function
 */
@MathCatDsl
fun <S, T> by(arrow: ScopedSuspended<S, T>): suspend CoroutineScope.(S)->T = arrow.morphism

/**
 * Mathspeak: evolve(data) by f, where f is a [ScopedSuspended]
 * Use together with [by]
 */
@MathCatDsl
fun <S> CoroutineScope.evolve(data: S): Pair<CoroutineScope,S> = Pair(this,data)

/**
 * Mathspeak: evolve(data) by f, where f is a [ScopedSuspended]
 * Use together with [evolve], for example
 */
@MathCatDsl
suspend infix fun <S, T> Pair<CoroutineScope,S>.by(arrow: ScopedSuspended<S, T>): T = arrow.morphism(first,second)


@MathCatDsl
@Suppress("FunctionName")
fun <T> Id(): ScopedSuspended<T,T> = ScopedSuspended{
    t->t
}

/**
 * Transform [ScopedSuspended] to [Suspended] by injecting a [CoroutineScope]
 */
@MathCatDsl
fun <S, T> ScopedSuspended<S, T>.onScope(scope: CoroutineScope): Suspended<S, T> = Suspended{ s: S -> morphism(scope,s)}

fun <S, T> ScopedSuspended<S, T>.asKlSuspended(): KlSuspended<CoroutineScope,S, T> = KlSuspended {
    s -> Suspended{ scope -> by(this@asKlSuspended)(scope, s) }
}