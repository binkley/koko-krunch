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
internal class KokoKrunch(
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
    fun copy(
        `how long`: Duration = this.`how long`,
        `when`: LocalDateTime = this.`when`,
        beans: Int = this.beans,
        bint: BigInteger = this.bint,
        bool: Boolean = this.bool,
        byte: Byte = this.byte,
        ch: Char = this.ch,
        crunch: Crunch = this.crunch,
        d: Double = this.d,
        drool: Boolean = this.drool,
        f: Float = this.f,
        long: Long = this.long,
        now: Instant = this.now,
        optional: String? = this.optional,
        required: String = this.required,
        s: Short = this.s,
        uuid: UUID = this.uuid,
        z: Int = this.z,
    ) = KokoKrunch(
        `how long` = `how long`,
        `when` = `when`,
        beans = beans,
        bint = bint,
        bool = bool,
        byte = byte,
        ch = ch,
        crunch = crunch,
        d = d,
        drool = drool,
        f = f,
        long = long,
        now = now,
        optional = optional,
        required = required,
        s = s,
        uuid = uuid,
        z = z,
    )

    override fun equals(other: Any?) = this === other ||
        other is KokoKrunch &&
        super.equals(other) &&
        `how long` == other.`how long` &&
        `when` == other.`when` &&
        bint == other.bint &&
        bool == other.bool &&
        byte == other.byte &&
        ch == other.ch &&
        crunch == other.crunch &&
        d == other.d &&
        drool == other.drool &&
        f == other.f &&
        long == other.long &&
        now == other.now &&
        optional == other.optional &&
        required == other.required &&
        s == other.s &&
        uuid == other.uuid &&
        z == other.z

    override fun hashCode() =
        31 * super.hashCode() + hash(
            `how long`,
            `when`,
            bint,
            bool,
            byte,
            ch,
            crunch,
            d,
            drool,
            f,
            long,
            now,
            optional,
            required,
            s,
            uuid,
            z,
        )

    override fun toString() =
        "Cereal(bint=$bint, required='$required', optional='$optional', byte=${byte.pretty()}, s=$s, ch=$ch, long=$long, bool=$bool, drool=$drool, d=$d, f=$f, z=$z, crunch=$crunch, uuid=$uuid, now=$now, how long=${`how long`}, when=${`when`}, super=${super.toString()})"
}
