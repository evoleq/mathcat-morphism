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


/**
 * Function composition for suspend functions
 */
@MathSpeakDsl
suspend infix fun <R, S, T> (suspend (S)->T).o(other: suspend(R)->S): suspend (R)->T = {
    r -> this@o(other(r))
}

@MathCatDsl
fun <S, T> by(suspended: Suspended<S, T>): suspend (S)->T = suspended.morphism

/**
 * Pipe
 */
@MathCatDsl
suspend infix fun <S, T> (suspend (S)->T).pipe(next: S): T = this(next)


/**
 * Evaluation
 */
@MathCatDsl
suspend fun <S, T> Pair<suspend (S)->T,S>.evaluate(): T = first(second)