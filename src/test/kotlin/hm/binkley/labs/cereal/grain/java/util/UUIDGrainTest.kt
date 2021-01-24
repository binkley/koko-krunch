package hm.binkley.labs.cereal.grain.java.util

import hm.binkley.labs.cereal.Grain
import io.kotest.matchers.collections.shouldHaveSize
import org.junit.jupiter.api.Test
import java.util.ServiceLoader.load

internal class UUIDGrainTest {
    @Test
    fun `should load`() {
        load(Grain::class.java).filterIsInstance<UUIDGrain>() shouldHaveSize 1
    }
}
