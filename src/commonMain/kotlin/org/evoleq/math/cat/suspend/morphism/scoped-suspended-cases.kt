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

suspend fun <S, T> cases(
    condition: S.()->Boolean,
    then: ScopedSuspended<S, T>,
    alternative: ScopedSuspended<S, T>
): ScopedSuspended<S, T> = ScopedSuspended {
    s -> when(condition(s)) {
        true -> by(then)(s)
        false ->by(alternative)(s)
    }
}