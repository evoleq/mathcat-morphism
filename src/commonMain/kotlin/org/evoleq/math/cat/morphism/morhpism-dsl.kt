package org.evoleq.math.cat.morphism

import org.evoleq.math.cat.marker.MathSpeakDsl

@MathSpeakDsl
infix fun <R, S, T> ((S)->T).o(other: (R)->S): (R)->T = {r -> this(other(r))}