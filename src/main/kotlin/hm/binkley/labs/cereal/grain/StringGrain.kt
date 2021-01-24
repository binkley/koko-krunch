package hm.binkley.labs.cereal.grain

import hm.binkley.labs.cereal.Grain
import hm.binkley.labs.cereal.Prep
import hm.binkley.labs.cereal.buf
import java.nio.ByteBuffer
import kotlin.reflect.KClass

class StringGrain : Grain<String> {
    override fun consent(type: KClass<*>): Boolean = String::class == type
    override fun absorb(it: String): Prep =
        with(it.toByteArray()) { size to { it.put(this) } }

    override fun extrude(buf: ByteBuffer, len: Int): String =
        buf.buf(len) { String(it) }
}
