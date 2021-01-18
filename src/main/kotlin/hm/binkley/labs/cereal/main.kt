package hm.binkley.labs.cereal

import hm.binkley.labs.cereal.Crunchiness.MEDIUM
import java.math.BigInteger

fun main() {
    val cereal = Cereal(
        beans = 20,
        bint = BigInteger.TWO.pow(Long.SIZE_BITS + 1),
        bool = true,
        byte = 3.toByte(),
        ch = 'A',
        crunch = Crunch(MEDIUM),
        d = 3.14159,
        drool = false,
        f = 1.234f,
        long = 9_876_543_210,
        optional = null,
        required = "BOB",
        s = 1024.toShort(),
        z = 13,
    )
    val bytes = cereal.write()

    println("WRITE -> $cereal")
    println("BYTES -> ${bytes.pretty()}")
    println("READ -> ${bytes.read<Cereal>()}")
}
