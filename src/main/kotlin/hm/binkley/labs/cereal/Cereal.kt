package hm.binkley.labs.cereal

import java.math.BigInteger

internal data class Cereal(
    val bint: BigInteger,
    val text: String,
    val byte: Byte,
    val s: Short,
    val ch: Char,
    val bool: Boolean,
    val d: Double?,
    val f: Float,
    val z: Int,
)
