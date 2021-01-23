package hm.binkley.labs.cereal

import lombok.Generated
import java.math.BigInteger
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.util.Objects.hash
import java.util.UUID

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

internal enum class Crunchiness { MEDIUM }

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
    val uuid: UUID,
    val now: Instant,
    val `how long`: Duration,
    val `when`: LocalDateTime,
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
        crunch == other.crunch &&
        uuid == other.uuid &&
        now == other.now &&
        `how long` == other.`how long` &&
        `when` == other.`when`

    override fun hashCode() =
        hash(
            bint,
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
            crunch,
            uuid,
            now,
            `how long`,
            `when`,
        )

    override fun toString() =
        "Cereal(bint=$bint, required='$required', optional='$optional', byte=${byte.pretty()}, s=$s, ch=$ch, long=$long, bool=$bool, drool=$drool, d=$d, f=$f, z=$z, crunch=$crunch, uuid=$uuid, now=$now, how long=${`how long`}, when=${`when`}, super=${super.toString()})"
}
