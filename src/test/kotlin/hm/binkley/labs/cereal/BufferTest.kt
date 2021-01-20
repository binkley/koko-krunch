package hm.binkley.labs.cereal

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

internal class BufferTest {
    @Test
    fun `should not find something`() {
        "abc".toByteArray().indexOf("def".toByteArray()) shouldBe -1
    }
}
