package hm.binkley.labs.cereal

import hm.binkley.labs.cereal.Crunchiness.MEDIUM
import sun.misc.Unsafe
import java.math.BigInteger
import java.nio.ByteBuffer

fun main() {
    val cereal = Cereal(
        bint = BigInteger.TWO.pow(Long.SIZE_BITS + 1),
        bool = true,
        byte = 3.toByte(),
        ch = 'A',
        crunch = Crunch(MEDIUM),
        d = null,
        f = 1.234f,
        s = 1024.toShort(),
        text = "BOB",
        z = 13,
        beans = 20,
    )
    val bytes = cereal.write()

    println("WRITE -> $cereal")
    println("BYTES -> ${bytes.pretty()}")
    println("READ -> ${bytes.read<Cereal>()}")
}
