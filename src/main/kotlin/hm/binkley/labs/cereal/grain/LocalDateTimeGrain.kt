package hm.binkley.labs.cereal.grain

import hm.binkley.labs.cereal.Grain
import hm.binkley.labs.cereal.Prep
import java.nio.ByteBuffer
import java.time.LocalDateTime
import kotlin.reflect.KClass

private val dateGrain = LocalDateGrain()
private val timeGrain = LocalTimeGrain()

class LocalDateTimeGrain : Grain<LocalDateTime> {
    override fun consent(type: KClass<*>): Boolean =
        LocalDateTime::class == type

    override fun absorb(it: LocalDateTime): Prep {
        val date = dateGrain.absorb(it.toLocalDate())
        val time = timeGrain.absorb(it.toLocalTime())

        return (date.first + time.first) to { buf ->
            date.second(buf)
            time.second(buf)
        }
    }

    override fun extrude(buf: ByteBuffer, len: Int): LocalDateTime =
        LocalDateTime.of(
            dateGrain.extrude(buf, len),
            timeGrain.extrude(buf, len)
        )
}
