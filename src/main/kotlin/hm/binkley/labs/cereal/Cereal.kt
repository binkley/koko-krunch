package hm.binkley.labs.cereal

import java.math.BigInteger
import java.util.Objects.hash

internal open class Chocolate(val beans: Int) {
    override fun equals(other: Any?): Boolean = this === other ||
            other is Chocolate &&
            javaClass == other.javaClass &&
            beans == other.beans

    override fun hashCode() = hash(beans)

    override fun toString(): String {
        return "Chocolate(beans=$beans)"
    }
}

internal enum class Crunchiness { LOW, MEDIUM, HIGH }
internal data class Crunch(val level: Crunchiness)

internal class Cereal(
    val bint: BigInteger,
    val text: String,
    val byte: Byte,
    val s: Short,
    val ch: Char,
    val bool: Boolean,
    val d: Double?,
    val f: Float,
    val z: Int,
    val crunch: Crunch,
    beans: Int,
) : Chocolate(beans) {
    override fun equals(other: Any?): Boolean = this === other ||
            other is Cereal &&
            javaClass == other.javaClass &&
            beans == other.beans &&
            bint == other.bint &&
            text == other.text &&
            byte == other.byte &&
            s == other.s &&
            ch == other.ch &&
            bool == other.bool &&
            d == other.d &&
            f == other.f &&
            z == other.z &&
            crunch == other.crunch

    override fun hashCode() =
        hash(bint, text, byte, s, ch, bool, d, f, z, crunch)

    override fun toString(): String {
        return "Cereal(bint=$bint, text='$text', byte=$byte, s=$s, ch=$ch, bool=$bool, d=$d, f=$f, z=$z, crunch=$crunch, beans=$beans)"
    }
}
