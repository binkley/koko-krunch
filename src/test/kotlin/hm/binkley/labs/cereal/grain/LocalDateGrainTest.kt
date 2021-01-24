package hm.binkley.labs.cereal.grain

import hm.binkley.labs.cereal.Grain
import io.kotest.matchers.collections.shouldHaveSize
import org.junit.jupiter.api.Test
import java.util.ServiceLoader.load

internal class LocalDateGrainTest {
    @Test
    fun `should load`() {
        load(Grain::class.java).filterIsInstance<LocalDateGrain>() shouldHaveSize 1
    }
}
