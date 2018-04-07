package arrow.optics

import arrow.optionals

@optionals
data class Point2D(val x: Int, val y: Int, val color: Int?)
