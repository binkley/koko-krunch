package hm.binkley.labs.cereal

import hm.binkley.labs.cereal.Crunchiness.MEDIUM
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import java.math.BigInteger.TWO
import kotlin.reflect.full.memberProperties

private val written = Cereal(
    bint = TWO.pow(Long.SIZE_BITS + 1),
    bool = true,
    byte = 3.toByte(),
    ch = 'A',
    crunch = Crunch(MEDIUM),
    d = null,
    f = 1.234f,
    s = 1024.toShort(),
    text = "BOB",
    z = 13,
    beans = 20,
)

internal class KokoKrunchTest {
    @Test
    fun `should round trip`() {
        val bytes = written.write()

        val read = bytes.read<Cereal>()

        read shouldBe written
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
    fun `should complain about wrong field count for class`() {
        val count = Cereal::class.memberProperties.size
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
            // Last field, "z" is an Int
            val truncated = copyOf(size - Int.SIZE_BYTES - Int.SIZE_BYTES)
            truncated[truncated.size - 1] = 0.toByte()
            truncated
        }

        shouldThrowExactly<AssertionError> {
            bytes.read<Cereal>()
        }
    }

    @Test
    fun `should complain about wrong field type`() {
        val bytes = written.write().apply {
            this[indexOf('y'.toByte())] = 'z'.toByte()
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
