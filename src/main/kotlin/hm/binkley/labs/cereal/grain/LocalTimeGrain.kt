package hm.binkley.labs.cereal.grain

import hm.binkley.labs.cereal.Grain
import hm.binkley.labs.cereal.Prep
import java.nio.ByteBuffer
import java.time.LocalTime
import kotlin.reflect.KClass

class LocalTimeGrain : Grain<LocalTime> {
    override fun consent(type: KClass<*>): Boolean = LocalTime::class == type
    override fun absorb(it: LocalTime): Prep =
        4 * Int.SIZE_BYTES to { buf ->
            buf.putInt(it.hour)
            buf.putInt(it.minute)
            buf.putInt(it.second)
            buf.putInt(it.nano)
        }

    override fun extrude(buf: ByteBuffer, len: Int): LocalTime =
        LocalTime.of(buf.int, buf.int, buf.int, buf.int)
}
