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

/**
 * Function composition (f:S->T,g:R, S) -> f o g: R->T
 */
@MathSpeakDsl
infix fun <R, S, T> ((S)->T).o(other: (R)->S): (R)->T = {r -> this(other(r))}

/**
 * Mimic delegation by a function
 */
@MathCatDsl
fun <S, T> by(morphism: Morphism<S, T>): (S)->T = morphism.morphism

/**
 * Pipe
 */
@MathCatDsl
infix fun <S, T> ((S)->T).pipe(next: S): T = this(next)

/**
 * Evaluation
 */
@MathCatDsl
fun <S, T> Pair<(S)->T,S>.evaluate(): T = first(second)