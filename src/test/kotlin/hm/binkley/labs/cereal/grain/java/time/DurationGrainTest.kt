package hm.binkley.labs.cereal.grain.java.time

import hm.binkley.labs.cereal.Grain
import io.kotest.matchers.collections.shouldHaveSize
import org.junit.jupiter.api.Test
import java.util.ServiceLoader.load

internal class DurationGrainTest {
    @Test
    fun `should load`() {
        load(Grain::class.java).filterIsInstance<DurationGrain>() shouldHaveSize 1
    }
}
