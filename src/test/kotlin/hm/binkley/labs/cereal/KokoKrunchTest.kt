package hm.binkley.labs.cereal

import hm.binkley.labs.cereal.Crunchiness.MEDIUM
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
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

private val written = KokoKrunch(
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
    fun `should equal`() {
        written.copy() shouldBe written
        written.copy(beans = written.beans + 1) shouldNotBe written
    }

    @Test
    fun `should hash`() {
        written.copy().hashCode() shouldBe written.hashCode()
        written.copy(beans = written.beans + 1).hashCode() shouldNotBe
            written.hashCode()
    }
}
