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