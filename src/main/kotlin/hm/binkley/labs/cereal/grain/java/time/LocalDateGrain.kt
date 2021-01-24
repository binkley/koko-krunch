package hm.binkley.labs.cereal.grain.java.time

import hm.binkley.labs.cereal.Grain
import hm.binkley.labs.cereal.Prep
import java.nio.ByteBuffer
import java.time.LocalDate
import kotlin.reflect.KClass

class LocalDateGrain : Grain<LocalDate> {
    override fun consent(type: KClass<*>): Boolean = LocalDate::class == type
    override fun absorb(it: LocalDate): Prep =
        3 * Int.SIZE_BYTES to { buf ->
            buf.putInt(it.year)
            buf.putInt(it.monthValue)
            buf.putInt(it.dayOfMonth)
        }

    override fun extrude(buf: ByteBuffer, len: Int): LocalDate =
        LocalDate.of(buf.int, buf.int, buf.int)
}
