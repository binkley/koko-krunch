package hm.binkley.labs.cereal

import hm.binkley.labs.cereal.Crunchiness.MEDIUM
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import java.math.BigInteger.TWO
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
    long = 9_876_543_210,
    optional = null,
    required = "BOB",
    s = 1024.toShort(),
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
            val i = withIndex().find {
                'b'.toByte() == it.value &&
                    'y'.toByte() == this[it.index + 1] &&
                    't'.toByte() == this[it.index + 2] &&
                    'e'.toByte() == this[it.index + 3]
            }!!.index
            this[i] = 'l'.toByte()
            this[i + 1] = 'o'.toByte()
            this[i + 2] = 'n'.toByte()
            this[i + 3] = 'g'.toByte()
        }

        shouldThrowExactly<AssertionError> {
            bytes.read<Cereal>()
        }
    }

    @Test
    fun `should complain about bad field type`() {
        val bytes = written.write().apply {
            val i = withIndex().find {
                'b'.toByte() == it.value &&
                    'y'.toByte() == this[it.index + 1] &&
                    't'.toByte() == this[it.index + 2] &&
                    'e'.toByte() == this[it.index + 3]
            }!!.index
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
