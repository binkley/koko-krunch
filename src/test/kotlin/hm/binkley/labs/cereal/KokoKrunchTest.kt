package hm.binkley.labs.cereal

import hm.binkley.labs.cereal.Crunchiness.MEDIUM
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import java.math.BigInteger.TWO
import java.time.Duration
import java.time.Instant.EPOCH
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Month.AUGUST
import java.time.temporal.ChronoUnit.DAYS
import java.util.UUID
import kotlin.reflect.full.memberProperties

private val written = Cereal(
    beans = 20,
    bint = TWO.pow(Long.SIZE_BITS + 1),
    bool = true,
    byte = 3.toByte(),
    ch = 'A',
    crunch = Crunch(MEDIUM),
    d = 3.14159,
    drool = false,
    f = 1.234f,
    `how long` = Duration.ofDays(1L).plusNanos(1L),
    long = 9_876_543_210,
    now = EPOCH.plus(20L * 365, DAYS).plusNanos(42L),
    optional = null,
    required = "BOB",
    s = 1024.toShort(),
    // Type 3 with ns:URL for https://github.com/binkley/koko-krun ch
    uuid = UUID.fromString("d24a38a6-ee97-3c3a-bac3-520497f431ef"),
    `when` = LocalDateTime.of(
        LocalDate.of(1968, AUGUST, 1),
        LocalTime.of(11, 12, 13, 14)
    ),
    z = 13,
)

internal class KokoKrunchTest {
    @Test
    fun `should round trip`() {
        val bytes = written.write()

        val read = bytes.read<Cereal>()

        read shouldBe written
    }

    @Test
    fun `should complain about too little data`() {
        shouldThrowExactly<AssertionError> {
            ByteArray(0).read<Cereal>()
        }
    }

    @Test
    fun `should complain about bad magic`() {
        val bytes = written.write().apply {
            replaceAt(0, MAGIC.replace(MAGIC[0], MAGIC[0] - 1).toByteArray())
        }

        shouldThrowExactly<AssertionError> {
            bytes.read<Cereal>()
        }
    }

    @Test
    fun `should complain about bad version`() {
        val bytes = written.write().apply {
            this[4] = (VERSION + 1).toByte()
        }

        shouldThrowExactly<AssertionError> {
            bytes.read<Cereal>()
        }
    }

    @Test
    fun `should complain about bad sentinel`() {
        val bytes = written.write().apply {
            this[size - 1] = 1.toByte()
        }

        shouldThrowExactly<AssertionError> {
            bytes.read<Cereal>()
        }
    }

    @Test
    fun `should complain about wrong class type`() {
        val bytes = written.write().apply {
            this[indexOf('C'.toByte())] = 'D'.toByte()
        }

        shouldThrowExactly<AssertionError> {
            bytes.read<Cereal>()
        }
    }

    @Test
    fun `should complain about wrong int size`() {
        val bytes = written.write().apply {
            this[indexOf(4.toByte())] = 3.toByte()
        }

        shouldThrowExactly<AssertionError> {
            bytes.read<Cereal>()
        }
    }

    @Test
    fun `should complain about wrong field count`() {
        // Subtract 1 to ignore the transient field
        val count = Cereal::class.memberProperties.size - 1
        val bytes = written.write().apply {
            this[indexOf(count.toByte())] = (count - 1).toByte()
        }

        shouldThrowExactly<AssertionError> {
            bytes.read<Cereal>()
        }
    }

    @Test
    fun `should complain about too few fields`() {
        val bytes = with(written.write()) {
            // Last field, "z" is an Int -- remove it
            val truncated = copyOf(size - Int.SIZE_BYTES - Int.SIZE_BYTES)
            truncated[truncated.size - 1] = 0.toByte()
            truncated
        }

        shouldThrowExactly<AssertionError> {
            bytes.read<Cereal>()
        }
    }

    @Test
    fun `should complain about wrong field name`() {
        val bytes = written.write().apply {
            val i = indexOf("crunch".toByteArray())
            replaceAt(i, "church".toByteArray())
        }

        shouldThrowExactly<AssertionError> {
            bytes.read<Cereal>()
        }
    }

    @Test
    fun `should complain about wrong field type`() {
        val bytes = written.write().apply {
            val i = indexOf("byte".toByteArray())
            replaceAt(i, "long".toByteArray())
        }

        shouldThrowExactly<AssertionError> {
            bytes.read<Cereal>()
        }
    }

    @Test
    fun `should complain about bad field type`() {
        val bytes = written.write().apply {
            val i = indexOf("byte".toByteArray())
            this[i + 1] = 'z'.toByte()
        }

        shouldThrowExactly<AssertionError> {
            bytes.read<Cereal>()
        }
    }

    @Test
    fun `should complain about extra bytes`() {
        val bytes = with(written.write()) {
            copyOf(size + 1)
        }

        shouldThrowExactly<AssertionError> {
            bytes.read<Cereal>()
        }
    }
}
