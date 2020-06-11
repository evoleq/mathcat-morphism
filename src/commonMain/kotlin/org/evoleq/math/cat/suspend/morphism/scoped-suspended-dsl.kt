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
