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