package hm.binkley.labs.cereal

import lombok.Generated
import java.math.BigInteger
import java.util.Objects.hash

@Generated // Lie to JaCoCo
internal open class Chocolate(val beans: Int) {
    override fun equals(other: Any?) = this === other ||
            other is Chocolate &&
            javaClass == other.javaClass &&
            beans == other.beans

    override fun hashCode() = hash(beans)
    override fun toString() = "Chocolate(beans=$beans)"

    companion object {
        @Suppress("unused")
        const val FOO: Int = Int.MAX_VALUE
    }
}

internal enum class Crunchiness { LOW, MEDIUM, HIGH }

@Generated // Lie to JaCoCo
internal data class Crunch(val level: Crunchiness)

@Generated // Lie to JaCoCo
internal class Cereal(
    val bint: BigInteger,
    val required: String,
    val optional: String?,
    val byte: Byte,
    val s: Short,
    val ch: Char,
    val long: Long,
    val bool: Boolean,
    val drool: Boolean,
    val d: Double,
    val f: Float,
    val z: Int,
    val crunch: Crunch,
    @Suppress("unused")
    @Transient
    val transient: Long = 0,
    beans: Int,
) : Chocolate(beans) {
    override fun equals(other: Any?) = this === other ||
            other is Cereal &&
            javaClass == other.javaClass &&
            beans == other.beans &&
            bint == other.bint &&
            required == other.required &&
            optional == other.optional &&
            byte == other.byte &&
            s == other.s &&
            ch == other.ch &&
            long == other.long &&
            bool == other.bool &&
            drool == other.drool &&
            d == other.d &&
            f == other.f &&
            z == other.z &&
            crunch == other.crunch

    override fun hashCode() =
        hash(bint,
            required,
            optional,
            byte,
            s,
            ch,
            long,
            bool,
            drool,
            d,
            f,
            z,
            crunch)

    override fun toString() =
        "Cereal(bint=$bint, required='$required', optional='$optional', byte=${byte.pretty()}, s=$s, ch=$ch, long=$long, bool=$bool, drool=$drool, d=$d, f=$f, z=$z, crunch=$crunch, super=${super.toString()})"
}
