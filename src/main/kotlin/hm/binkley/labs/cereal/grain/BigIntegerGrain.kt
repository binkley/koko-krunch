package hm.binkley.labs.cereal.grain

import hm.binkley.labs.cereal.Grain
import hm.binkley.labs.cereal.Prep
import hm.binkley.labs.cereal.buf
import java.math.BigInteger
import java.nio.ByteBuffer
import kotlin.reflect.KClass

class BigIntegerGrain : Grain<BigInteger> {
    override fun consent(type: KClass<*>) = BigInteger::class == type

    override fun absorb(it: BigInteger): Prep =
        with(it.toByteArray()) { size to { it.put(this) } }

    override fun extrude(buf: ByteBuffer, len: Int): BigInteger =
        buf.buf(len) { BigInteger(it) }
}
