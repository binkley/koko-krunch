package hm.binkley.labs.cereal

import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import kotlin.reflect.full.memberProperties

internal class CerealTest {
    @Test
    fun `should round trip`() {
        val bytes = fixture.write()

        val read = bytes.read<KokoKrunch>()

        read shouldBe fixture
    }

    @Test
    fun `should complain about too little data`() {
        shouldThrowExactly<CerealException> {
            ByteArray(0).read<KokoKrunch>()
        }
    }

    @Test
    fun `should complain about bad magic`() {
        val bytes = fixture.write().apply {
            replaceAt(0, MAGIC.replace(MAGIC[0], MAGIC[0] - 1).toByteArray())
        }

        shouldThrowExactly<CerealException> {
            bytes.read<KokoKrunch>()
        }
    }

    @Test
    fun `should complain about bad version`() {
        val bytes = fixture.write().apply {
            this[4] = (VERSION + 1).toByte()
        }

        shouldThrowExactly<CerealException> {
            bytes.read<KokoKrunch>()
        }
    }

    @Test
    fun `should complain about bad sentinel`() {
        val bytes = fixture.write().apply {
            this[size - 1] = 1.toByte()
        }

        shouldThrowExactly<CerealException> {
            bytes.read<KokoKrunch>()
        }
    }

    @Test
    fun `should complain about wrong class type`() {
        val bytes = fixture.write().apply {
            val i = indexOf("KokoKrunch".toByteArray())
            replaceAt(i, "JojoBrunch".toByteArray())
        }

        shouldThrowExactly<CerealException> {
            bytes.read<KokoKrunch>()
        }
    }

    @Test
    fun `should complain about wrong int size`() {
        val bytes = fixture.write().apply {
            this[indexOf(4.toByte())] = 3.toByte()
        }

        shouldThrowExactly<CerealException> {
            bytes.read<KokoKrunch>()
        }
    }

    @Test
    fun `should complain about wrong field count`() {
        // Subtract 1 to ignore the transient field
        val count = KokoKrunch::class.memberProperties.size - 1
        val bytes = fixture.write().apply {
            this[indexOf(count.toByte())] = (count - 1).toByte()
        }

        shouldThrowExactly<CerealException> {
            bytes.read<KokoKrunch>()
        }
    }

    @Test
    fun `should complain about too few fields`() {
        val bytes = with(fixture.write()) {
            // Last field, "z" is an Int -- remove it
            val truncated = copyOf(size - Int.SIZE_BYTES - Int.SIZE_BYTES)
            truncated[truncated.size - 1] = 0.toByte()
            truncated
        }

        shouldThrowExactly<CerealException> {
            bytes.read<KokoKrunch>()
        }
    }

    @Test
    fun `should complain about wrong field name`() {
        val bytes = fixture.write().apply {
            val i = indexOf("crunch".toByteArray())
            replaceAt(i, "church".toByteArray())
        }

        shouldThrowExactly<CerealException> {
            bytes.read<KokoKrunch>()
        }
    }

    @Test
    fun `should complain about wrong field type`() {
        val bytes = fixture.write().apply {
            val i = indexOf("byte".toByteArray())
            replaceAt(i, "long".toByteArray())
        }

        shouldThrowExactly<CerealException> {
            bytes.read<KokoKrunch>()
        }
    }

    @Test
    fun `should complain about bad field type`() {
        val bytes = fixture.write().apply {
            val i = indexOf("byte".toByteArray())
            this[i + 1] = 'z'.toByte()
        }

        shouldThrowExactly<CerealException> {
            bytes.read<KokoKrunch>()
        }
    }

    @Test
    fun `should complain about extra bytes`() {
        val bytes = with(fixture.write()) {
            copyOf(size + 1)
        }

        shouldThrowExactly<CerealException> {
            bytes.read<KokoKrunch>()
        }
    }
}
