package org.evoleq.math.cat.morphism

import org.evoleq.math.cat.marker.MathSpeakDsl

/**
 * Function composition (f:S->T,g:R, S) -> f o g: R->T
 */
@MathSpeakDsl
infix fun <R, S, T> ((S)->T).o(other: (R)->S): (R)->T = {r -> this(other(r))}