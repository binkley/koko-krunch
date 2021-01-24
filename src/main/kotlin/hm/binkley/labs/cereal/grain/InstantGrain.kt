package hm.binkley.labs.cereal.grain

import hm.binkley.labs.cereal.Grain
import hm.binkley.labs.cereal.Prep
import java.nio.ByteBuffer
import java.time.Instant
import java.time.temporal.ChronoField.INSTANT_SECONDS
import java.time.temporal.ChronoField.NANO_OF_SECOND
import kotlin.reflect.KClass

class InstantGrain : Grain<Instant> {
    override fun consent(type: KClass<*>): Boolean = Instant::class == type

    override fun absorb(it: Instant): Prep =
        (Long.SIZE_BYTES + Int.SIZE_BYTES) to { buf ->
            buf.putLong(it.getLong(INSTANT_SECONDS))
            buf.putInt(it.get(NANO_OF_SECOND))
        }

    override fun extrude(buf: ByteBuffer, len: Int): Instant =
        Instant.ofEpochSecond(buf.long, buf.int.toLong())
}
